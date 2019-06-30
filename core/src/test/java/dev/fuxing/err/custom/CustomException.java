package dev.fuxing.err.custom;

import dev.fuxing.err.ExceptionParser;
import dev.fuxing.err.TransportException;

/**
 * Created by: Fuxing
 * Date: 2019-05-30
 * Time: 16:33
 */
public class CustomException extends TransportException {

    static {
        ExceptionParser.register(CustomException.class, CustomException::new);
    }

    protected CustomException(TransportException e) {
        super(e);
    }

    public CustomException() {
        super(400, CustomException.class, "Custom message");
    }
}