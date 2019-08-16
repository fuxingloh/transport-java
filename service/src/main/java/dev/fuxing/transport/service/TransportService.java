package dev.fuxing.transport.service;

/**
 * Created By: Fuxing Loh
 * Date: 8/2/2017
 * Time: 2:29 PM
 *
 * @see dev.fuxing.transport.TransportError for structure for error
 */
public interface TransportService extends TransportPath, TransportMethod {

    /**
     * Start the router
     * By wiring all the routes
     */
    default void start() {
        route();
    }

    /**
     * Wire all the routes
     */
    void route();

    /**
     * @param code status code
     * @return TransportResult
     */
    default TransportResult result(int code) {
        return TransportResult.of(code);
    }

    /**
     * @param code   status code
     * @param object data object
     * @return TransportResult
     */
    default TransportResult result(int code, Object object) {
        return TransportResult.of(code, object);
    }
}
