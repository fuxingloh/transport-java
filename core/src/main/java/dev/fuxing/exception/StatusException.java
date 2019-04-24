package dev.fuxing.exception;

/**
 * Created By: Fuxing Loh
 * Date: 16/6/2017
 * Time: 7:04 PM
 */
public final class StatusException extends TransportException {
    static {
        ExceptionParser.register(StatusException.class, StatusException::new);
    }

    StatusException(TransportException e) {
        super(e);
    }

    /**
     * @param code to throw
     */
    public StatusException(int code) {
        super(code, StatusException.class, "Status code error.");
    }

    /**
     * @param code    to throw
     * @param message for user
     */
    public StatusException(int code, String message) {
        super(code, StatusException.class, message);
    }
}
