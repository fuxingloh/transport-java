package dev.fuxing.jpa;


import dev.fuxing.err.ErrorURL;

/**
 * Created by: Fuxing
 * Date: 2019-04-16
 * Time: 18:31
 * Project: v22-transport
 */
public final class DatabaseException extends ErrorURL {

    public DatabaseException(Throwable cause) {
        super(500, "err.fuxing.dev", DatabaseException.class, "Unknown database error.", cause);
    }
}
