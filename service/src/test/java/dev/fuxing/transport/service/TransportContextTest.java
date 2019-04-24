package dev.fuxing.transport.service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;

/**
 * Created by: Fuxing
 * Date: 2019-04-14
 * Time: 16:07
 */
class TransportContextTest {
    private static final Logger logger = LoggerFactory.getLogger(TransportContextTest.class);
    private static final int PORT = 34313;

    @BeforeAll
    static void setUp() {
        TransportServer.start(PORT, "", new TransportService() {
            @Override
            public void route() {
                GET("/path", call -> {
                    logger.info("QS: {}", call.request().queryString());

                    logger.info("Blank: {}", call.queryPresent("blank"));
                    logger.info("Valued: {}", call.queryPresent("valued"));
                    logger.info("None: {}", call.queryPresent("none"));

                    return "abc";
                });
            }
        });
    }

    @AfterAll
    static void tearDown() {
        Spark.stop();
    }

    @Test
    void request() {
        // TODO(fuxing): testing
//        TransportRequest request = new TransportRequest(HttpMethod.GET, "http://localhost:" + PORT + "/path");
//        request.query("blank");
//        request.query("valued", "value");
//        request.asResponse();
    }
}