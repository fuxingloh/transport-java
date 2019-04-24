package dev.fuxing.pubsub;

/**
 * TransportQueue is basically a single linear pubsub.
 * 1 message for 1 listener
 * <p>
 * Created by: Fuxing
 * Date: 2019-04-24
 * Time: 18:57
 */
public interface TransportQueue<Message extends TransportMessage, PubRequest, SubResponse> extends
        TransportPublisher<Message, PubRequest>,
        TransportSubscriber<Message, SubResponse> {
}
