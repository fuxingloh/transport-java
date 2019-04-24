package dev.fuxing.pubsub;

/**
 * Created by: Fuxing
 * Date: 2019-04-24
 * Time: 18:37
 */
public interface TransportPublisher<Message extends TransportMessage, Request> {


    /**
     * @param message to publish
     */
    default void publish(Message message) {
        Request request = createRequest(message);
        send(message, request);
    }

    /**
     * Not all publisher requires creating of Request.
     * This is like a 'before' filter.
     *
     * @param message message to create request with
     * @return created request
     */
    default Request createRequest(Message message) {
        return null;
    }

    /**
     * @param message to publish
     * @param request created request that publisher uses
     */
    void send(Message message, Request request);
}
