package fit.man.app.advice.exception;

import java.io.Serial;

public class ActivityNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -2071804204765069876L;

    private static final String ACTIVITY_NOT_FOUND_EXCEPTION_TEMPLATE = "ACTIVITY_NOT_FOUND_EXCEPTION: %s";

    public ActivityNotFoundException(String message) {
        super(ACTIVITY_NOT_FOUND_EXCEPTION_TEMPLATE.formatted(message));
    }
}
