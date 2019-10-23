package dev.fuxing.transport.service;

import java.util.function.Consumer;

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
     * @param consumer for building
     * @return TransportResult created in the consumer
     */
    default TransportResult result(Consumer<TransportResult.Builder> consumer) {
        TransportResult.Builder builder = TransportResult.builder();
        consumer.accept(builder);
        return builder.build();
    }
}
