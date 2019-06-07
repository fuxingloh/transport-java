package dev.fuxing.exception;

/**
 * Created by: Fuxing
 * Date: 2019-06-06
 * Time: 15:37
 */
public final class NotFoundException extends TransportException {
    static {
        ExceptionParser.register(NotFoundException.class, NotFoundException::new);
    }

    NotFoundException(TransportException e) {
        super(e);
    }

    /**
     * This is a non silent version of 404 exception.
     * Scenario that this should be thrown is during object patching and the object couldn't be found.
     */
    public NotFoundException() {
        this("Not found.");
    }

    /**
     * @param message indicating which object cannot be found.
     */
    public NotFoundException(String message) {
        super(404, NotFoundException.class, message);
    }
}
