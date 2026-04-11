package fit.man.app.util;

import org.springframework.util.StringUtils;

import java.util.Arrays;

public final class LoggerUtils {
    public static int TRUNCATE_THRESHOLD = 500;

    public static String truncate(Object... a) {
        return StringUtils.truncate(Arrays.deepToString(a), TRUNCATE_THRESHOLD);
    }
}
