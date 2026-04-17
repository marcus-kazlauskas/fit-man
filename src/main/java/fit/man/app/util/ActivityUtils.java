package fit.man.app.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ActivityUtils {
    public static final int MILLIS = 1000;
    public static final double DECIMAL_DEGREES = 180.0 / Math.pow(2, 31);
    public static final Short MARK_DEFAULT = 1;
}
