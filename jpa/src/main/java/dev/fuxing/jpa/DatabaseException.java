package dev.fuxing.jpa;


import dev.fuxing.err.ExceptionParser;
import dev.fuxing.err.TransportException;

/**
 * Created by: Fuxing
 * Date: 2019-04-16
 * Time: 18:31
 * Project: v22-transport
 */
public final class DatabaseException extends TransportException {

    static {
        ExceptionParser.register(DatabaseException.class, DatabaseException::new);
    }

    DatabaseException(TransportException e) {
        super(e);
    }

    public DatabaseException(Throwable cause) {
        super(500, DatabaseException.class, "Unknown database error.", cause);
    }
}
