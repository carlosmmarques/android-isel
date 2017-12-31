package pt.isel.pdm.li51n.g4.tmdbisel.data.provider.TMDb;


public class TMDbAPIConnectionException extends Exception {
    /**
     * Constructs a new {@code Exception} that includes the current stack trace.
     */
    public TMDbAPIConnectionException() {
    }

    /**
     * Constructs a new {@code Exception} with the current stack trace and the
     * specified detail message.
     *
     * @param detailMessage the detail message for this exception.
     */
    public TMDbAPIConnectionException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * Constructs a new {@code Exception} with the current stack trace, the
     * specified detail message and the specified cause.
     *
     * @param detailMessage the detail message for this exception.
     * @param throwable
     */
    public TMDbAPIConnectionException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    /**
     * Constructs a new {@code Exception} with the current stack trace and the
     * specified cause.
     *
     * @param throwable the cause of this exception.
     */
    public TMDbAPIConnectionException(Throwable throwable) {
        super(throwable);
    }
}
