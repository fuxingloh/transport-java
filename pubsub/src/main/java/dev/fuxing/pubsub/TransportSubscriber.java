package dev.fuxing.pubsub;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by: Fuxing
 * Date: 2019-04-24
 * Time: 18:37
 */
public interface TransportSubscriber<Message extends TransportMessage, Response> {

    /**
     * Single thread receiver
     *
     * @param receiver for receiving of messages
     * @return whether any messages has been processed
     */
    default boolean subscribe(Receiver<Message> receiver) {
        return subscribe(1, receiver);
    }

    /**
     * @param threads  to process messages
     * @param receiver for receiving of messages
     * @return whether any messages has been processed
     */
    default boolean subscribe(int threads, Receiver<Message> receiver) {
        List<Response> responses = getResponses();
        if (responses.isEmpty()) return false;

        ExecutorService service = Executors.newFixedThreadPool(threads);
        List<Response> completedList = new ArrayList<>();

        try {
            CompletableFuture[] futures = responses.stream()
                    .map(response -> mapFuture(service, response, receiver, completedList))
                    .toArray(CompletableFuture[]::new);

            CompletableFuture.allOf(futures).join();
            return true;
        } finally {
            service.shutdown();

            // Delete completed responses
            if (!completedList.isEmpty()) {
                delete(responses);
            }
        }
    }

    private CompletableFuture mapFuture(ExecutorService service, Response response, Receiver<Message> receiver, List<Response> completedList) {
        Runnable runnable = () -> {
            Message message = mapMessage(response);
            receiver.receive(message);
            completedList.add(response);
        };

        return CompletableFuture.runAsync(runnable, service);
    }

    /**
     * @return responses from subscriber
     */
    List<Response> getResponses();

    /**
     * @param response to map into message
     * @return mapped {@code Message}
     */
    Message mapMessage(Response response);

    /**
     * @param responses to delete when they are completed
     */
    void delete(List<Response> responses);

    interface Receiver<Message> {
        void receive(Message message);
    }
}
