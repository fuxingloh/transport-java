package dev.fuxing.err;

import dev.fuxing.err.custom.CustomException;
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
        Assertions.assertEquals("err.fuxing.dev/RateLimitException", url);
    }

    @Test
    void custom() {
        String url = TransportException.getType(CustomException.class);
        Assertions.assertEquals("err.fuxing.dev/dev.fuxing.err.custom.CustomException", url);
    }
}
