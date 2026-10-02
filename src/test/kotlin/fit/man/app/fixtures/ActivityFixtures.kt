package fit.man.app.fixtures

import com.garmin.fit.EventType
import com.garmin.fit.Sport
import fit.man.app.repository.entity.Activity
import fit.man.app.repository.entity.Analysis
import fit.man.app.repository.entity.Event
import fit.man.app.repository.entity.Record
import fit.man.app.util.ActivityUtils
import java.time.Duration
import java.time.LocalDateTime
import java.time.OffsetDateTime

object ActivityFixtures {
    val START_TIME: OffsetDateTime = ActivityUtils.toOffsetDateTime("2026-04-23T13:12:00")
    val POSITION_TIME_1: LocalDateTime = LocalDateTime.parse("2026-04-24T13:12:00")
    val POSITION_TIME_2: LocalDateTime = POSITION_TIME_1.plusMinutes(13)
    val POSITION_TIME_3: LocalDateTime = POSITION_TIME_2.plusMinutes(10)
    val POSITION_TIME_4: LocalDateTime = POSITION_TIME_3.plusMinutes(10)

    fun createNewActivity(): Activity {
        val activity = Activity()
        activity.endTime = ActivityUtils.toOffsetDateTime("2026-04-24T13:12:00")
        activity.startTime = START_TIME
        activity.sport = Sport.WALKING.name
        activity.totalElapsedTime = Duration.parse("PT24H")
        activity.totalTimerTime = Duration.parse("PT13H12M")
        activity.totalDistance = 13.12F
        activity.totalCalories = 1312
        activity.totalAscent = 666
        activity.enhancedAvgSpeed = 4F
        activity.enhancedMaxSpeed = 5F
        activity.userName = "Misha"
        activity.deviceName = "App"

        val record = Record()
        record.positionTime = POSITION_TIME_1
        record.positionLat = 55.7887
        record.positionLong = 49.1221
        record.distance = 12.13F
        record.enhancedSpeed = 4F
        record.enhancedAltitude = 1F
        record.mark = ActivityUtils.MARK_DEFAULT
        activity.addRecord(record)

        val event = Event()
        event.eventTime = POSITION_TIME_1
        event.eventName = "TIMER"
        event.eventType = EventType.START.name
        activity.addEvent(event)

        return activity
    }

    fun createRecordWithNullLat(): Record {
        val record = Record()
        record.positionTime = POSITION_TIME_1
        record.positionLat = null
        record.positionLong = 49.1221
        record.distance = 12.13F
        record.enhancedSpeed = 4F
        record.enhancedAltitude = 1F
        record.mark = ActivityUtils.MARK_DEFAULT
        return record
    }

    fun createRecordWithNullLong(): Record {
        val record = Record()
        record.positionTime = POSITION_TIME_1
        record.positionLat = 55.7887
        record.positionLong = null
        record.distance = 12.13F
        record.enhancedSpeed = 4F
        record.enhancedAltitude = 1F
        record.mark = ActivityUtils.MARK_DEFAULT
        return record
    }

    fun createRecordWithMarkDisabled(): Record {
        val record = Record()
        record.positionTime = POSITION_TIME_1
        record.positionLat = 55.7887
        record.positionLong = 49.1221
        record.distance = 1213F
        record.enhancedSpeed = 1400F
        record.enhancedAltitude = 1F
        record.mark = ActivityUtils.MARK_DISABLED
        return record
    }

    fun createRecord1(): Record {
        val record = Record()
        record.positionTime = POSITION_TIME_1
        record.positionLat = 55.7887
        record.positionLong = 49.1221
        record.distance = 12.13F
        record.enhancedSpeed = 4F
        record.enhancedAltitude = 1F
        record.mark = ActivityUtils.MARK_DEFAULT
        return record
    }

    fun createRecordWithFarPos(): Record {
        val record = Record()
        record.positionTime = POSITION_TIME_1.plusMinutes(3)
        record.positionLat = 55.8387
        record.positionLong = 49.1721
        record.distance = 6644F
        record.enhancedSpeed = 132.88F
        record.enhancedAltitude = 2F
        record.mark = ActivityUtils.MARK_DEFAULT
        return record
    }

    fun createRecord2(): Record {
        val record = Record()
        record.positionTime = POSITION_TIME_2
        record.positionLat = 55.8887
        record.positionLong = 49.2221
        record.distance = 6644F
        record.enhancedSpeed = 13.3F
        record.enhancedAltitude = 3F
        record.mark = ActivityUtils.MARK_DEFAULT
        return record
    }

    fun createRecord3(): Record {
        val record = Record()
        record.positionTime = POSITION_TIME_3
        record.positionLat = 55.9387
        record.positionLong = 49.2721
        record.distance = 6644F
        record.enhancedSpeed = 13.3F
        record.enhancedAltitude = 3F
        record.mark = ActivityUtils.MARK_DEFAULT
        return record
    }

    fun createRecord4(): Record {
        val record = Record()
        record.positionTime = POSITION_TIME_4
        record.positionLat = 55.9887
        record.positionLong = 49.3221
        record.distance = 6644F
        record.enhancedSpeed = 13.3F
        record.enhancedAltitude = 3F
        record.mark = ActivityUtils.MARK_DEFAULT
        return record
    }

    fun createEvent1(): Event {
        val event = Event()
        event.eventTime = POSITION_TIME_1
        event.eventName = "TIMER"
        event.eventType = EventType.START.name
        return event
    }

    fun createDisabledEvent(): Event {
        val event = Event()
        event.eventTime = POSITION_TIME_2.minusMinutes(1)
        event.eventName = "TIMER"
        event.eventType = EventType.STOP.name
        return event
    }

    fun createEvent2(): Event {
        val event = Event()
        event.eventTime = POSITION_TIME_2
        event.eventName = "TIMER"
        event.eventType = EventType.STOP.name
        return event
    }

    fun createEvent3(): Event {
        val event = Event()
        event.eventTime = POSITION_TIME_3
        event.eventName = "TIMER"
        event.eventType = EventType.START.name
        return event
    }

    fun createEvent4(): Event {
        val event = Event()
        event.eventTime = POSITION_TIME_4
        event.eventName = "TIMER"
        event.eventType = EventType.STOP_ALL.name
        return event
    }

    fun createAnalysis(): Analysis {
        val analysis = Analysis()
        analysis.totalDistance = 13000F
        analysis.movingTime = 43200L
        analysis.averageSpeed = 1.08F
        analysis.success = true
        return analysis
    }

    fun createUnsuccessfulAnalysis(): Analysis {
        val analysis = Analysis()
        analysis.totalDistance = 13000F
        analysis.movingTime = null
        analysis.averageSpeed = null
        analysis.success = false
        return analysis
    }
}
