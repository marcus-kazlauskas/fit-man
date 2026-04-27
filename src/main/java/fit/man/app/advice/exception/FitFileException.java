package fit.man.app.advice.exception;

import java.io.Serial;

public class FitFileException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -6235884954053644325L;

    private static final String FIT_FILE_EXCEPTION_TEMPLATE = "FIT_FILE_EXCEPTION: %s";

    public FitFileException(String message) {
        super(FIT_FILE_EXCEPTION_TEMPLATE.formatted(message));
    }

    public FitFileException(String message, Throwable cause) {
        super(FIT_FILE_EXCEPTION_TEMPLATE.formatted(message), cause);
    }
}
