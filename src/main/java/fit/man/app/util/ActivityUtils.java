package fit.man.app.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ActivityUtils {
    public static final int MILLIS = 1000;
    public static final double DECIMAL_DEGREES = 180.0 / Math.pow(2, 31);
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
}
