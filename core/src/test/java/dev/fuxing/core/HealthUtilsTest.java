package dev.fuxing.core;

import dev.fuxing.health.HealthUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;

/**
 * Created by: Fuxing
 * Date: 2019-04-02
 * Time: 01:50
 */
class HealthUtilsTest {
    @Test
    void postgres() throws Exception {
        Assertions.assertThrows(RuntimeException.class, () -> {
            HealthUtils.host("jdbc:postgresql://localhost:5444/postgres".substring(5), Duration.ofSeconds(6));
        });
    }

    @Test
    void url() throws Exception {
        Assertions.assertThrows(RuntimeException.class, () -> {
            HealthUtils.host("http://random.ap-southeast-10.elb.amazonaws.com", Duration.ofSeconds(6));
        });
    }
}