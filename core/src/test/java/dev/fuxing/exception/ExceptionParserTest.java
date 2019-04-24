package dev.fuxing.exception;

import org.junit.jupiter.api.Test;

/**
 * Created by: Fuxing
 * Date: 2019-04-02
 * Time: 01:51
 */
class ExceptionParserTest {
    @Test
    void name() {
        ExceptionParser.parse(new NullPointerException());
    }
}