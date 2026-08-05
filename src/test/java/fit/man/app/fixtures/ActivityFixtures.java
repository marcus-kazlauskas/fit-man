package fit.man.app.fixtures;

import com.garmin.fit.EventType;
import com.garmin.fit.Sport;
import fit.man.app.repository.entity.Activity;
import fit.man.app.repository.entity.Analysis;
import fit.man.app.repository.entity.Event;
import fit.man.app.repository.entity.Record;
import fit.man.app.util.ActivityUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public final class ActivityFixtures {
    public static OffsetDateTime START_TIME = ActivityUtils.toOffsetDateTime("2026-04-23T13:12:00");
    public static LocalDateTime POSITION_TIME_1 = LocalDateTime.parse("2026-04-24T13:12:00");
    public static LocalDateTime POSITION_TIME_2 = POSITION_TIME_1.plusMinutes(13);
    public static LocalDateTime POSITION_TIME_3 = POSITION_TIME_2.plusMinutes(10);
    public static LocalDateTime POSITION_TIME_4 = POSITION_TIME_3.plusMinutes(10);

    public static Activity createNewActivity() {
        var activity = new Activity();
        activity.setEndTime(ActivityUtils.toOffsetDateTime("2026-04-24T13:12:00"));
        activity.setStartTime(START_TIME);
        activity.setSport(Sport.WALKING.name());
        activity.setTotalElapsedTime(Duration.parse("PT24H"));
        activity.setTotalTimerTime(Duration.parse("PT13H12M"));
        activity.setTotalDistance(13.12F);
        activity.setTotalCalories(1312);
        activity.setTotalAscent(666);
        activity.setEnhancedAvgSpeed(4F);
        activity.setEnhancedMaxSpeed(5F);

        var record = new Record();
        record.setPositionTime(POSITION_TIME_1);
        record.setPositionLat(55.7887);
        record.setPositionLong(49.1221);
        record.setDistance(12.13F);
        record.setEnhancedSpeed(4F);
        record.setEnhancedAltitude(1F);
        record.setMark(ActivityUtils.MARK_DEFAULT);
        activity.addRecord(record);

        var event = new Event();
        event.setEventTime(POSITION_TIME_1);
        event.setEventName("TIMER");
        event.setEventType(EventType.START.name());
        activity.addEvent(event);

        return activity;
    }

    public static Record createRecordWithNullLat() {
        var record = new Record();
        record.setPositionTime(POSITION_TIME_1);
        record.setPositionLat(null);
        record.setPositionLong(49.1221);
        record.setDistance(12.13F);
        record.setEnhancedSpeed(4F);
        record.setEnhancedAltitude(1F);
        record.setMark(ActivityUtils.MARK_DEFAULT);
        return record;
    }

    public static Record createRecordWithNullLong() {
        var record = new Record();
        record.setPositionTime(POSITION_TIME_1);
        record.setPositionLat(55.7887);
        record.setPositionLong(null);
        record.setDistance(12.13F);
        record.setEnhancedSpeed(4F);
        record.setEnhancedAltitude(1F);
        record.setMark(ActivityUtils.MARK_DEFAULT);
        return record;
    }

    public static Record createRecordWithMarkDisabled() {
        var record = new Record();
        record.setPositionTime(POSITION_TIME_1);
        record.setPositionLat(55.7887);
        record.setPositionLong(49.1221);
        record.setDistance(1213F);
        record.setEnhancedSpeed(1400F);
        record.setEnhancedAltitude(1F);
        record.setMark(ActivityUtils.MARK_DISABLED);
        return record;
    }

    public static Record createRecord1() {
        var record = new Record();
        record.setPositionTime(POSITION_TIME_1);
        record.setPositionLat(55.7887);
        record.setPositionLong(49.1221);
        record.setDistance(12.13F);
        record.setEnhancedSpeed(4F);
        record.setEnhancedAltitude(1F);
        record.setMark(ActivityUtils.MARK_DEFAULT);
        return record;
    }

    public static Record createRecordWithFarPos() {
        var record = new Record();
        record.setPositionTime(POSITION_TIME_1.plusMinutes(3));
        record.setPositionLat(55.8387);
        record.setPositionLong(49.1721);
        record.setDistance(6644F);
        record.setEnhancedSpeed(132.88F);
        record.setEnhancedAltitude(2F);
        record.setMark(ActivityUtils.MARK_DEFAULT);
        return record;
    }

    public static Record createRecord2() {
        var record = new Record();
        record.setPositionTime(POSITION_TIME_2);
        record.setPositionLat(55.8887);
        record.setPositionLong(49.2221);
        record.setDistance(6644F);
        record.setEnhancedSpeed(13.3F);
        record.setEnhancedAltitude(3F);
        record.setMark(ActivityUtils.MARK_DEFAULT);
        return record;
    }

    public static Record createRecord3() {
        var record = new Record();
        record.setPositionTime(POSITION_TIME_3);
        record.setPositionLat(55.9387);
        record.setPositionLong(49.2721);
        record.setDistance(6644F);
        record.setEnhancedSpeed(13.3F);
        record.setEnhancedAltitude(3F);
        record.setMark(ActivityUtils.MARK_DEFAULT);
        return record;
    }

    public static Record createRecord4() {
        var record = new Record();
        record.setPositionTime(POSITION_TIME_4);
        record.setPositionLat(55.9887);
        record.setPositionLong(49.3221);
        record.setDistance(6644F);
        record.setEnhancedSpeed(13.3F);
        record.setEnhancedAltitude(3F);
        record.setMark(ActivityUtils.MARK_DEFAULT);
        return record;
    }

    public static Event createEvent1() {
        var event = new Event();
        event.setEventTime(POSITION_TIME_1);
        event.setEventName("TIMER");
        event.setEventType(EventType.START.name());
        return event;
    }

    public static Event createDisabledEvent() {
        var event = new Event();
        event.setEventTime(POSITION_TIME_2.minusMinutes(1));
        event.setEventName("TIMER");
        event.setEventType(EventType.STOP.name());
        return event;
    }

    public static Event createEvent2() {
        var event = new Event();
        event.setEventTime(POSITION_TIME_2);
        event.setEventName("TIMER");
        event.setEventType(EventType.STOP.name());
        return event;
    }

    public static Event createEvent3() {
        var event = new Event();
        event.setEventTime(POSITION_TIME_3);
        event.setEventName("TIMER");
        event.setEventType(EventType.START.name());
        return event;
    }

    public static Event createEvent4() {
        var event = new Event();
        event.setEventTime(POSITION_TIME_4);
        event.setEventName("TIMER");
        event.setEventType(EventType.STOP_ALL.name());
        return event;
    }

    public static Analysis createAnalysis() {
        var analysis = new Analysis();
        analysis.setTotalDistance(13000F);
        analysis.setMovingTime(43200L);
        analysis.setAverageSpeed(1.08F);
        analysis.setSuccess(true);
        return analysis;
    }

    public static Analysis createUnsuccessfulAnalysis() {
        var analysis = new Analysis();
        analysis.setTotalDistance(13000F);
        analysis.setMovingTime(null);
        analysis.setAverageSpeed(null);
        analysis.setSuccess(false);
        return analysis;
    }
}
