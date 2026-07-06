package fit.man.app.service;

import com.garmin.fit.ActivityMesg;
import com.garmin.fit.Decode;
import com.garmin.fit.DeviceInfoMesg;
import com.garmin.fit.EventMesg;
import com.garmin.fit.MesgBroadcaster;
import com.garmin.fit.RecordMesg;
import com.garmin.fit.SessionMesg;
import com.garmin.fit.SportMesg;
import com.garmin.fit.UserProfileMesg;
import fit.man.app.advice.exception.ActivityNotFoundException;
import fit.man.app.advice.exception.FitFileException;
import fit.man.app.api.model.ActivityResponse;
import fit.man.app.api.model.TrackResponse;
import fit.man.app.config.GlobalProperties;
import fit.man.app.mapper.ActivityMapper;
import fit.man.app.repository.ActivityRepository;
import fit.man.app.repository.entity.Activity;
import fit.man.app.repository.entity.Event;
import fit.man.app.repository.entity.Record;
import fit.man.app.util.ActivityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final ActivityMapper activityMapper;
    private final GlobalProperties globalProperties;

    public Activity readFitFile(InputStream is) {
        final var activity = new Activity();

        var decode = new Decode();
        var broadcaster = new MesgBroadcaster(decode);

        final var zoneOffset = new AtomicReference<>(ZoneOffset.UTC);
        final var startTime = new AtomicReference<>(Instant.now());
        final var endTime = new AtomicReference<>(Instant.now());

        broadcaster.addListener((ActivityMesg mesg) -> {
            var local = mesg.getLocalTimestamp();
            var utc = mesg.getTimestamp().getTimestamp();
            if (local != null && utc != null) {
                int diff = (int) (local - utc);
                zoneOffset.set(ZoneOffset.ofTotalSeconds(diff));
            }

            var fitEndTime = mesg.getTimestamp();
            endTime.set(fitEndTime.getDate().toInstant());
        });

        broadcaster.addListener((SessionMesg mesg) -> {
            var fitStartTime = mesg.getStartTime();
            startTime.set(fitStartTime.getDate().toInstant());

            var totalElapsedTime = (long) (mesg.getTotalElapsedTime() * ActivityUtils.MILLIS);
            activity.setTotalElapsedTime(Duration.ofMillis(totalElapsedTime));
            var totalTimeTime = (long) (mesg.getTotalTimerTime() * ActivityUtils.MILLIS);
            activity.setTotalTimerTime(Duration.ofMillis(totalTimeTime));
            activity.setTotalDistance(mesg.getTotalDistance());
            activity.setTotalCalories(mesg.getTotalCalories());
            activity.setTotalAscent(mesg.getTotalAscent());
            activity.setEnhancedAvgSpeed(mesg.getEnhancedAvgSpeed());
            activity.setEnhancedMaxSpeed(mesg.getEnhancedMaxSpeed());
        });

        broadcaster.addListener((SportMesg mesg) -> {
            activity.setSport(mesg.getSport().name());
        });

        broadcaster.addListener((UserProfileMesg mesg) -> {
            activity.setUserName(mesg.getFriendlyName());
        });

        broadcaster.addListener((DeviceInfoMesg mesg) -> {
            activity.setDeviceName(mesg.getProductName());
        });

        broadcaster.addListener((RecordMesg mesg) -> {
            var record =  new Record();
            var positionTimeUtc = mesg.getTimestamp().getDate().toInstant()
                    .atOffset(ZoneOffset.UTC)
                    .toLocalDateTime();
            record.setPositionTime(positionTimeUtc);
            var positionLat = mesg.getPositionLat();
            if (positionLat != null) {
                record.setPositionLat(positionLat * ActivityUtils.DECIMAL_DEGREES);
            }
            var positionLong = mesg.getPositionLong();
            if (positionLong != null) {
                record.setPositionLong(positionLong * ActivityUtils.DECIMAL_DEGREES);
            }
            record.setDistance(mesg.getDistance());
            record.setEnhancedSpeed(mesg.getEnhancedSpeed());
            record.setEnhancedAltitude(mesg.getEnhancedAltitude());
            record.setMark(ActivityUtils.MARK_DEFAULT);
            activity.addRecord(record);
        });

        broadcaster.addListener((EventMesg mesg) -> {
            var event = new Event();
            var eventTimeUtc = mesg.getTimestamp().getDate().toInstant()
                    .atOffset(ZoneOffset.UTC)
                    .toLocalDateTime();
            event.setEventTime(eventTimeUtc);
            event.setEventName(mesg.getEvent().name());
            event.setEventType(mesg.getEventType().name());
            activity.addEvent(event);
        });

        try {
            broadcaster.run(is);
        } catch (RuntimeException e) {
            log.atWarn().log(e.getMessage());
            throw new FitFileException(e.getMessage(), e);
        }

        activity.setStartTime(startTime.get().atOffset(zoneOffset.get()));
        activity.setEndTime(endTime.get().atOffset(zoneOffset.get()));

        return activity;
    }

    public ActivityResponse loadNewActivity(InputStream is) {
        var activity = readFitFile(is);
        return activityMapper.toResponse(checkNotExistsAndSave(activity));
    }

    public Activity checkNotExistsAndSave(Activity activity) {
        if (activityRepository.existsByStartTime(activity.getStartTime())) {
            log.atWarn().log("This activity {} is already saved in DB", activity);
            throw new FitFileException("Activity with startTime specified is already saved in DB");
        } else {
            var savedActivity = activityRepository.save(activity);
            log.atInfo().log("Saved activity {}", savedActivity);
            return savedActivity;
        }
    }

    public TrackResponse getTrackInRange(String startTimeBegin , String startTimeEnd) {
        var start = ActivityUtils.toOffsetDateTime(startTimeBegin);
        var end = ActivityUtils.toOffsetDateTime(startTimeEnd);
        return getTrackInRange(start, end);
    }

    private TrackResponse getTrackInRange(OffsetDateTime startTimeBegin , OffsetDateTime startTimeEnd) {
        var track = activityRepository.findFirstByStartTimeBetweenOrderByStartTime(startTimeBegin, startTimeEnd);
        if (track.isPresent()) {
            log.atInfo().log("Read track {}", track);
            return activityMapper.toTrackResponse(track.get());
        } else {
            log.atWarn().log("No activity with startTime from {} to {}", startTimeBegin, startTimeEnd);
            throw new ActivityNotFoundException("No activity with startTime specified");
        }
    }

    public void markActivities() {
        var rq = PageRequest.of(
                0,
                globalProperties.activityScheduler().batchSize(),
                Sort.by("startTime")
        );
        var activities = activityRepository.findByMarkedFalse(rq);
        log.atInfo().log("{} activities selected for markup", activities.size());

        for (var activity : activities) {
            markAndSave(activity);
        }
    }

    private void markAndSave(Activity activity) {
        var records = activity.getRecords();
        var i = 0;
        var j = 1;
        while (j < records.size()) {
            var rec1 = records.get(i);
            var rec1isNull = ActivityUtils.positionIsNull(rec1);
            var rec2 = records.get(j);
            var rec2isNull = ActivityUtils.positionIsNull(rec2);

            if (rec1isNull) {
                rec1.setMark(ActivityUtils.MARK_DISABLED);
                i++;
            } else if (rec2isNull || speedIsTooHigh(rec1, rec2)) {
                rec2.setMark(ActivityUtils.MARK_DISABLED);
            } else {
                i = j;
            }
            j++;
        }
        activity.setMarked(true);
        activityRepository.save(activity);
        log.atInfo().log("Saved marked up activity {}", activity);
    }

    public boolean speedIsTooHigh(Record rec1, Record rec2) {
        return ActivityUtils.calcSpeed(rec1, rec2) > globalProperties.activityScheduler().maxSpeed();
    }
}
