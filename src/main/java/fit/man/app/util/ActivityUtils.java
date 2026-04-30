package fit.man.app.util;

import fit.man.app.repository.entity.Record;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.sf.geographiclib.Geodesic;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ActivityUtils {
    public static final int MILLIS = 1000;
    public static final double DECIMAL_DEGREES = 180.0 / Math.pow(2, 31);
    public static final double KM_PER_HOUR = 3.6;
    public static final Short MARK_DISABLED = 0;
    public static final Short MARK_DEFAULT = 1;

    public static OffsetDateTime toOffsetDateTime(String dateTime) {
        return LocalDateTime.parse(dateTime)
                .atZone(ZoneId.systemDefault()).toOffsetDateTime();
    }

    public static String toLocalDateTimeString(OffsetDateTime odt) {
        var adjustedOdt = odt.withOffsetSameInstant(
                ZoneId.systemDefault().getRules().getOffset(odt.toInstant())
        );
        return adjustedOdt.toLocalDateTime().toString();
    }

    public static double calcDistance(double lat1, double lon1, double lat2, double lon2) {
        return Geodesic.WGS84.Inverse(lat1, lon1, lat2, lon2).s12;
    }

    public static double calcSpeed(Record rec1, Record rec2) {
        var lat1 = rec1.getPositionLat();
        var lon1 = rec1.getPositionLong();
        var time1 = rec1.getPositionTime();
        var lat2 = rec2.getPositionLat();
        var lon2 = rec2.getPositionLong();
        var time2 = rec2.getPositionTime();

        var dist = calcDistance(lat1, lon1, lat2, lon2);
        var time = Duration.between(time1, time2).toMillis() / (double) MILLIS;
        if (time == 0) {
            return 0;
        }
        return dist / time * KM_PER_HOUR;
    }

    public static boolean positionIsNull(Record rec) {
        var lat = rec.getPositionLat();
        var lon = rec.getPositionLong();
        var time = rec.getPositionTime();
        return lat == null || lon == null || time == null;
    }
}
