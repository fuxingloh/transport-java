package dev.fuxing.health;

import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * Created by: Fuxing
 * Date: 2019-04-02
 * Time: 12:20
 */
class HealthCheckServerTest {

    @Test
    void blocking() throws IOException {
        HealthCheckServer.startBlocking(() -> {
            try {
                Thread.sleep(6_000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
}