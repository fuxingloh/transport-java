package dev.fuxing.exception;

/**
 * Created by: Fuxing
 * Date: 10/12/2016
 * Time: 11:26 AM
 */
public final class JsonException extends TransportException {

    static {
        ExceptionParser.register(JsonException.class, JsonException::new);
    }

    JsonException(TransportException e) {
        super(e);
    }

    /**
     * @param cause throwable for actual cause of json exception
     */
    public JsonException(Throwable cause) {
        super(400, JsonException.class, "Json malformed.", cause);
    }
}
