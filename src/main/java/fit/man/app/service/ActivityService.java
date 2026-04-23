package fit.man.app.service;

import com.garmin.fit.ActivityMesg;
import com.garmin.fit.Decode;
import com.garmin.fit.DeviceInfoMesg;
import com.garmin.fit.MesgBroadcaster;
import com.garmin.fit.RecordMesg;
import com.garmin.fit.SessionMesg;
import com.garmin.fit.SportMesg;
import com.garmin.fit.UserProfileMesg;
import fit.man.app.advice.exception.ActivityNotFoundException;
import fit.man.app.advice.exception.FitFileException;
import fit.man.app.api.model.ActivityResponse;
import fit.man.app.api.model.TrackResponse;
import fit.man.app.mapper.ActivityMapper;
import fit.man.app.repository.ActivityRepository;
import fit.man.app.repository.entity.Activity;
import fit.man.app.repository.entity.Record;
import fit.man.app.util.ActivityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public ActivityResponse loadNewActivity(InputStream is) {
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

        try {
            broadcaster.run(is);
        } catch (RuntimeException e) {
            log.atWarn().log(e.getMessage());
            throw new FitFileException(e.getMessage(), e);
        }

        activity.setStartTime(startTime.get().atOffset(zoneOffset.get()));
        activity.setEndTime(endTime.get().atOffset(zoneOffset.get()));

        return activityMapper.toResponse(checkNotExistsAndSave(activity));
    }

    private Activity checkNotExistsAndSave(Activity activity) {
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

    public TrackResponse getTrackInRange(OffsetDateTime startTimeBegin , OffsetDateTime startTimeEnd) {
        try {
            var track = activityMapper.toTrackResponse(
                    activityRepository.findByStartTimeBetweenOrderByStartTime(
                            startTimeBegin, startTimeEnd
                    ).getFirst()
            );
            log.atInfo().log("Read track {}", track);
            return track;
        } catch (RuntimeException e) {
            log.atWarn().log("No activity with startTime from {} to {}", startTimeBegin, startTimeEnd);
            throw new ActivityNotFoundException("No activity with startTime specified", e);
        }
    }
}
