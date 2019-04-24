package dev.fuxing.exception;

/**
 * Created by: Fuxing
 * Date: 17/7/18
 * Time: 12:39 AM
 */
public final class ForbiddenException extends TransportException {
    static {
        ExceptionParser.register(ForbiddenException.class, ForbiddenException::new);
    }

    ForbiddenException(TransportException e) {
        super(e);
    }

    /**
     * The server understood the request, but is refusing to fulfill it.
     * Authorization will not help and the request SHOULD NOT be repeated.
     */
    public ForbiddenException() {
        this("Forbidden");
    }

    /**
     * The server understood the request, but is refusing to fulfill it.
     * Authorization will not help and the request SHOULD NOT be repeated.
     *
     * @param message reasons, (not recommended, better to not let the user know why, especially for private content)
     */
    public ForbiddenException(String message) {
        super(403, ForbiddenException.class, message);
    }
}
