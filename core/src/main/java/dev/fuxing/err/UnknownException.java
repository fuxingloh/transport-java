package dev.fuxing.err;

/**
 * Created by: Fuxing
 * Date: 16/6/2017
 * Time: 1:27 PM
 */
public final class UnknownException extends TransportException {

    static {
        ExceptionParser.register(UnknownException.class, UnknownException::new);
    }

    UnknownException(TransportException e) {
        super(e);
    }

    /**
     * The server encountered an unexpected condition which prevented it from fulfilling the request.
     * With a message indicating the reason why the message occurred.
     * <p>
     * Code: 500
     * ClassName: UnknownException
     * Message: Custom message for user.
     *
     * @param throwable exception thrown
     * @param message   message for user
     */
    public UnknownException(Throwable throwable, String message) {
        super(500, UnknownException.class, message, throwable);
    }

    /**
     * The server encountered an unexpected condition which prevented it from fulfilling the request.
     * <p>
     * This is useful to map any throwable to an Unknown Exception.
     * Only throwable are allowed because, if exception is known. It used be registered.
     * <p>
     * Code: 500
     * ClassName: UnknownException
     * Message: The message from the exception itself.
     *
     * @param throwable any unknown error
     */
    public UnknownException(Throwable throwable) {
        super(500, UnknownException.class, throwable.getMessage(), throwable);
    }

    private UnknownException(String message) {
        super(500, UnknownException.class, message);
    }
}
