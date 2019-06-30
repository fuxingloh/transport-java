package dev.fuxing.err;

/**
 * Created By: Fuxing Loh
 * Date: 22/3/2017
 * Time: 5:19 PM
 */
public final class TimeoutException extends TransportException {

    static {
        ExceptionParser.register(TimeoutException.class, TimeoutException::new);
    }

    TimeoutException(TransportException e) {
        super(e);
    }

    /**
     * The client did not produce a request within the time that the server was prepared to wait.
     * The client MAY repeat the request without modifications at any later time.
     */
    public TimeoutException() {
        this(null);
    }

    /**
     * The client did not produce a request within the time that the server was prepared to wait.
     * The client MAY repeat the request without modifications at any later time.
     *
     * @param throwable cause of timeout
     */
    public TimeoutException(Throwable throwable) {
        this(408, throwable);
    }

    /**
     * The client did not produce a request within the time that the server was prepared to wait.
     * The client MAY repeat the request without modifications at any later time.
     *
     * @param status    408 or 504
     * @param throwable cause of timeout
     */
    public TimeoutException(int status, Throwable throwable) {
        super(status, TimeoutException.class, "Request from client to server has timeout.", throwable);
    }
}
