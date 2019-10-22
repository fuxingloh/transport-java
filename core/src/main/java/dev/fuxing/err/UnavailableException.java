package dev.fuxing.err;

/**
 * Created by: Fuxing
 * Date: 3/12/2017
 * Time: 8:13 AM
 */
public final class UnavailableException extends TransportException {

    static {
        ExceptionParser.register(UnavailableException.class, UnavailableException::new);
    }

    UnavailableException(TransportException e) {
        super(e);
    }

    /**
     * The server is currently unable to handle the request due to a temporary overloading or maintenance of the server.
     * The implication is that this is a temporary condition which will be alleviated after some delay.
     */
    public UnavailableException() {
        super(503, UnavailableException.class, "Service temporarily unavailable.");
    }

    /**
     * The server is currently unable to handle the request due to a temporary overloading or maintenance of the server.
     * The implication is that this is a temporary condition which will be alleviated after some delay.
     *
     * @param throwable cause of exception, (not recommended)
     */
    public UnavailableException(Throwable throwable) {
        super(503, UnavailableException.class, "Service temporarily unavailable.", throwable);
    }
}
