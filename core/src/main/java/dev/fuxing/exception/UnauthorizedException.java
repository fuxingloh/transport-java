package dev.fuxing.exception;

/**
 * Created by: Fuxing
 * Date: 5/1/2018
 * Time: 1:48 AM
 */
public final class UnauthorizedException extends TransportException {
    static {
        ExceptionParser.register(UnauthorizedException.class, UnauthorizedException::new);
    }

    UnauthorizedException(TransportException e) {
        super(e);
    }

    /**
     * Similar to 403 Forbidden, but specifically for use when authentication is possible but has failed or required and not yet been provided.
     */
    public UnauthorizedException() {
        super(401, UnauthorizedException.class, "Unauthorized");
    }

    /**
     * Similar to 403 Forbidden, but specifically for use when authentication is possible but has failed or required and not yet been provided.
     *
     * @param message to provide information, (not recommended, better to not let the user know why)
     */
    public UnauthorizedException(String message) {
        super(401, UnauthorizedException.class, message);
    }
}
