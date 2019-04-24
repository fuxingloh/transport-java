package dev.fuxing.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A health check util to run multiple {@code Runnable} into one. When one fail, all will fail.
 * This is an util class for HealthCheckServer for cases whether multiple {@code Runnable} is required.
 * <p>
 * Created by: Fuxing
 * Date: 2019-04-02
 * Time: 12:09
 */
public class HealthCheck {
    private static final Logger logger = LoggerFactory.getLogger(HealthCheck.class);

    /**
     * @param runnableArray to run, all will be interrupted if any one of them failed
     */
    public static void runBlocking(Runnable... runnableArray) {
        runAsync(runnableArray).join();
    }

    public static CompletableFuture runAsync(Runnable... runnableArray) {
        CompletableFuture[] futures = new CompletableFuture[runnableArray.length];
        for (int i = 0; i < runnableArray.length; i++) {
            futures[i] = CompletableFuture.runAsync(runnableArray[i]);
        }

        AtomicBoolean alive = new AtomicBoolean(true);

        return CompletableFuture.anyOf(futures)
                .exceptionally(throwable -> {
                    logger.error("Error on one of the CompletableFuture", throwable);
                    alive.set(false);

                    if (!alive.get()) return null;

                    for (CompletableFuture future : futures) {
                        future.completeExceptionally(new InterruptedException("Interrupted due to other service."));
                    }
                    return null;
                });
    }
}
