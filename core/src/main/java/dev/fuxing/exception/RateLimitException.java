package dev.fuxing.exception;

/**
 * Created by: Fuxing
 * Date: 29/6/18
 * Time: 2:52 PM
 */
public final class RateLimitException extends TransportException {
    static {
        ExceptionParser.register(RateLimitException.class, RateLimitException::new);
    }

    RateLimitException(TransportException e) {
        super(e);
    }

    /**
     * The 429 status code indicates that the user has sent too many requests in a given amount of time ("rate limiting").
     *
     * @param message limitation exception
     */
    public RateLimitException(String message) {
        super(429, RateLimitException.class, message);
    }
}
