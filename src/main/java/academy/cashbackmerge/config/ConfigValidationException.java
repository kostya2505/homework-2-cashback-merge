package academy.cashbackmerge.config;

/** Custom exception class for configuration validation errors. */
public class ConfigValidationException extends RuntimeException {

    /**
     * Constructs a new ConfigValidationException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     */
    public ConfigValidationException(String message) {
        super(message);
    }

    /**
     * Constructs a new ConfigValidationException with the specified detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method). (A {@code null}
     *     value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public ConfigValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
