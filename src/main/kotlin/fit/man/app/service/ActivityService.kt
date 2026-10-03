package fit.man.app.service

import com.garmin.fit.ActivityMesg
import com.garmin.fit.Decode
import com.garmin.fit.DeviceInfoMesg
import com.garmin.fit.EventMesg
import com.garmin.fit.MesgBroadcaster
import com.garmin.fit.RecordMesg
import com.garmin.fit.SessionMesg
import com.garmin.fit.SportMesg
import com.garmin.fit.UserProfileMesg
import fit.man.app.advice.exception.ActivityNotFoundException
import fit.man.app.advice.exception.FitFileException
import fit.man.app.api.model.ActivityResponse
import fit.man.app.api.model.TrackResponse
import fit.man.app.config.AppProperties
import fit.man.app.mapper.toResponse
import fit.man.app.mapper.toTrackResponse
import fit.man.app.repository.ActivityRepository
import fit.man.app.repository.entity.Activity
import fit.man.app.repository.entity.Event
import fit.man.app.repository.entity.Record
import fit.man.app.util.ActivityUtils
import org.hibernate.Hibernate
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.InputStream
import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.concurrent.atomic.AtomicReference

@Service
@Transactional
class ActivityService(
    private val activityRepository: ActivityRepository,
    private val appProperties: AppProperties,
) {
    fun readFitFile(inputStream: InputStream): Activity {
        val activity = Activity()

        val decode = Decode()
        val broadcaster = MesgBroadcaster(decode)

        val zoneOffset = AtomicReference(ZoneOffset.UTC)
        val startTime = AtomicReference(Instant.now())
        val endTime = AtomicReference(Instant.now())

        broadcaster.addListener { mesg: ActivityMesg ->
            val local = mesg.localTimestamp
            val utc = mesg.timestamp.timestamp
            if (local != null && utc != null) {
                val diff = (local - utc).toInt()
                zoneOffset.set(ZoneOffset.ofTotalSeconds(diff))
            }

            val fitEndTime = mesg.timestamp
            endTime.set(fitEndTime.date.toInstant())
        }

        broadcaster.addListener { mesg: SessionMesg ->
            val fitStartTime = mesg.startTime
            startTime.set(fitStartTime.date.toInstant())

            val totalElapsedTime = (mesg.totalElapsedTime * ActivityUtils.MILLIS).toLong()
            activity.totalElapsedTime = Duration.ofMillis(totalElapsedTime)
            val totalTimeTime = (mesg.totalTimerTime * ActivityUtils.MILLIS).toLong()
            activity.totalTimerTime = Duration.ofMillis(totalTimeTime)
            activity.totalDistance = mesg.totalDistance
            activity.totalCalories = mesg.totalCalories
            activity.totalAscent = mesg.totalAscent
            activity.enhancedAvgSpeed = mesg.enhancedAvgSpeed
            activity.enhancedMaxSpeed = mesg.enhancedMaxSpeed
        }

        broadcaster.addListener { mesg: SportMesg ->
            activity.sport = mesg.sport.name
        }

        broadcaster.addListener { mesg: UserProfileMesg ->
            activity.userName = mesg.friendlyName
        }

        broadcaster.addListener { mesg: DeviceInfoMesg ->
            activity.deviceName = mesg.productName
        }

        broadcaster.addListener { mesg: RecordMesg ->
            val record = Record()
            val positionTimeUtc =
                mesg.timestamp.date
                    .toInstant()
                    .atOffset(ZoneOffset.UTC)
                    .toLocalDateTime()
            record.positionTime = positionTimeUtc
            val positionLat = mesg.positionLat
            if (positionLat != null) {
                record.positionLat = positionLat * ActivityUtils.DECIMAL_DEGREES
            }
            val positionLong = mesg.positionLong
            if (positionLong != null) {
                record.positionLong = positionLong * ActivityUtils.DECIMAL_DEGREES
            }
            record.distance = mesg.distance
            record.enhancedSpeed = mesg.enhancedSpeed
            record.enhancedAltitude = mesg.enhancedAltitude
            record.mark = ActivityUtils.MARK_DEFAULT
            activity.addRecord(record)
        }

        broadcaster.addListener { mesg: EventMesg ->
            val event = Event()
            val eventTimeUtc =
                mesg.timestamp.date
                    .toInstant()
                    .atOffset(ZoneOffset.UTC)
                    .toLocalDateTime()
            event.eventTime = eventTimeUtc
            event.eventName = mesg.event.name
            event.eventType = mesg.eventType.name
            activity.addEvent(event)
        }

        try {
            broadcaster.run(inputStream)
        } catch (e: RuntimeException) {
            log.atWarn().log(e.message)
            throw FitFileException(e.message.orEmpty(), e)
        }

        activity.startTime = startTime.get().atOffset(zoneOffset.get())
        activity.endTime = endTime.get().atOffset(zoneOffset.get())

        return activity
    }

    fun loadNewActivity(inputStream: InputStream): ActivityResponse {
        val activity = readFitFile(inputStream)
        return checkNotExistsAndSave(activity).toResponse()
    }

    fun checkNotExistsAndSave(activity: Activity): Activity {
        if (activityRepository.existsByStartTime(activity.startTime!!)) {
            log.atWarn().log("This activity {} is already saved in DB", activity)
            throw FitFileException("Activity with startTime specified is already saved in DB")
        }
        val savedActivity = activityRepository.save(activity)
        log.atInfo().log("Saved activity {}", savedActivity)
        return savedActivity
    }

    fun getTrackInRange(
        startTimeBegin: String,
        startTimeEnd: String,
    ): TrackResponse {
        val start = ActivityUtils.toOffsetDateTime(startTimeBegin)
        val end = ActivityUtils.toOffsetDateTime(startTimeEnd)
        return getTrackInRange(start, end)
    }

    private fun getTrackInRange(
        startTimeBegin: OffsetDateTime,
        startTimeEnd: OffsetDateTime,
    ): TrackResponse {
        val track = activityRepository.findFirstByStartTimeBetweenOrderByStartTime(startTimeBegin, startTimeEnd)
        if (track.isPresent) {
            log.atInfo().log("Read track {}", track)
            return track.get().toTrackResponse()
        }
        log.atWarn().log("No activity with startTime from {} to {}", startTimeBegin, startTimeEnd)
        throw ActivityNotFoundException("No activity with startTime specified")
    }

    fun findActivitiesForMarkup(): List<Activity> {
        val activities = activityRepository.findByMarkedFalse(activityRequest)
        for (activity in activities) {
            Hibernate.initialize(activity.records)
        }
        log.atInfo().log("{} activities selected for markup", activities.size)
        return activities
    }

    fun findActivitiesForAnalysis(): List<Activity> {
        val activities = activityRepository.findByMarkedTrueAndAnalysisIsNull(activityRequest)
        for (activity in activities) {
            Hibernate.initialize(activity.records)
            Hibernate.initialize(activity.events)
        }
        log.atInfo().log("{} activities selected for analysis", activities.size)
        return activities
    }

    private val activityRequest: Pageable
        get() = PageRequest.of(0, appProperties.activityScheduler.batchSize, Sort.by("startTime"))

    companion object {
        private val log = LoggerFactory.getLogger(ActivityService::class.java)
    }
}
