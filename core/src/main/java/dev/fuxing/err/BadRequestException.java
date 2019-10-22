package dev.fuxing.err;

/**
 * Created by: Fuxing
 * Date: 27/9/18
 * Time: 3:15 PM
 */
public final class BadRequestException extends TransportException {
    static {
        ExceptionParser.register(BadRequestException.class, BadRequestException::new);
    }

    BadRequestException(TransportException e) {
        super(e);
    }

    /**
     * The request could not be understood by the server due to malformed syntax.
     * The client SHOULD NOT repeat the request without modifications.
     */
    public BadRequestException() {
        this("Bad request.");
    }

    /**
     * The request could not be understood by the server due to malformed syntax.
     * The client SHOULD NOT repeat the request without modifications.
     *
     * @param message bad request messagef or user the see
     */
    public BadRequestException(String message) {
        super(400, BadRequestException.class, message);
    }
}
