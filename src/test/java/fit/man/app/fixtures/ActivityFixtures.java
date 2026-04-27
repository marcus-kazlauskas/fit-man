package fit.man.app.fixtures;

import com.garmin.fit.Sport;
import fit.man.app.repository.entity.Activity;
import fit.man.app.repository.entity.Record;
import fit.man.app.util.ActivityUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public final class ActivityFixtures {
    public static OffsetDateTime START_TIME = ActivityUtils.toOffsetDateTime("2026-04-23T13:12:00");

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
        record.setPositionTime(LocalDateTime.parse("2026-04-24T13:12:00"));
        record.setPositionLat(55.7887);
        record.setPositionLong(49.1221);
        record.setDistance(12.13F);
        record.setEnhancedSpeed(4F);
        record.setEnhancedAltitude(1F);
        record.setMark((short) 1);
        activity.addRecord(record);

        return activity;
    }

    public static Record createRecordWithNullLat() {
        var record = new Record();
        record.setPositionTime(LocalDateTime.parse("2026-04-24T13:12:00"));
        record.setPositionLat(null);
        record.setPositionLong(49.1221);
        record.setDistance(12.13F);
        record.setEnhancedSpeed(4F);
        record.setEnhancedAltitude(1F);
        record.setMark((short) 1);
        return record;
    }

    public static Record createRecordWithNullLong() {
        var record = new Record();
        record.setPositionTime(LocalDateTime.parse("2026-04-24T13:12:00"));
        record.setPositionLat(55.7887);
        record.setPositionLong(null);
        record.setDistance(12.13F);
        record.setEnhancedSpeed(4F);
        record.setEnhancedAltitude(1F);
        record.setMark((short) 1);
        return record;
    }
}
