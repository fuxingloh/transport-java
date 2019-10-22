package dev.fuxing.err;

/**
 * Created by: Fuxing
 * Date: 2019-04-02
 * Time: 11:48
 */
public final class BadGatewayException extends TransportException {

    static {
        ExceptionParser.register(BadGatewayException.class, BadGatewayException::new);
    }

    BadGatewayException(TransportException e) {
        super(e);
    }

    /**
     * The server, while acting as a gateway or proxy, received an invalid response from the upstream server it accessed in attempting to fulfill the request.
     */
    public BadGatewayException() {
        super(502, BadGatewayException.class, "Bad gateway.");
    }

    /**
     * The server, while acting as a gateway or proxy, received an invalid response from the upstream server it accessed in attempting to fulfill the request.
     *
     * @param throwable cause of exception, (not recommended)
     */
    public BadGatewayException(Throwable throwable) {
        super(502, BadGatewayException.class, "Bad gateway.", throwable);
    }
}
