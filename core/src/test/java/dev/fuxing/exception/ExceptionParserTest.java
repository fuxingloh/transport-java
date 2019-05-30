package dev.fuxing.exception;

import dev.fuxing.exception.custom.CustomException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created by: Fuxing
 * Date: 2019-04-02
 * Time: 01:51
 */
class ExceptionParserTest {
    @Test
    void parse() {
        ExceptionParser.parse(new NullPointerException());
    }

    @Test
    void root() {
        String url = TransportException.getType(RateLimitException.class);
        Assertions.assertEquals(url, "err.fuxing.dev/RateLimitException");
    }

    @Test
    void custom() {
        String url = TransportException.getType(CustomException.class);
        Assertions.assertEquals(url, "err.fuxing.dev/dev.fuxing.exception.custom.CustomException");
    }
}