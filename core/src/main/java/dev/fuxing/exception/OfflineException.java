package dev.fuxing.exception;

/**
 * Created By: Fuxing Loh
 * Date: 18/3/2017
 * Time: 3:42 PM
 */
public final class OfflineException extends TransportException {

    static {
        ExceptionParser.register(OfflineException.class, OfflineException::new);
    }

    OfflineException(TransportException e) {
        super(e);
    }

    /**
     * The server, while acting as a gateway or proxy, received an invalid response from the upstream server it accessed in attempting to fulfill the request.
     */
    public OfflineException() {
        this(null);
    }

    /**
     * The server, while acting as a gateway or proxy, received an invalid response from the upstream server it accessed in attempting to fulfill the request.
     *
     * @param throwable cause of invalid response
     */
    public OfflineException(Throwable throwable) {
        super(502, OfflineException.class, "Request from client to server is not reachable.", throwable);
    }
}
