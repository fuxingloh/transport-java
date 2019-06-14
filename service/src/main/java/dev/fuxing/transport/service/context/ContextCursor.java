package dev.fuxing.transport.service.context;

import dev.fuxing.transport.TransportCursor;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * Created by: Fuxing
 * Date: 2019-06-14
 * Time: 06:07
 */
public interface ContextCursor extends Context {

    /**
     * @param key          to get in cursor
     * @param defaultValue default value
     * @return a single key in cursor or defaultValue if not found
     */
    @Nullable
    default String queryCursorString(String key, @Nullable String defaultValue) {
        return queryCursor().get(key, defaultValue);
    }

    /**
     * cursor itself will not determine the direction, it is stateless
     * other querystring will determine the direction
     *
     * @return Cursor called 'cursor'
     */
    @NotNull
    default TransportCursor queryCursor() {
        return queryCursor("cursor");
    }

    /**
     * @param name cursor name, e.g. next, prev
     * @return Cursor, or a empty cursor will be returned
     */
    @NotNull
    default TransportCursor queryCursor(String name) {
        return queryCursor(name, TransportCursor.EMPTY);
    }

    /**
     * @param name          cursor name, e.g. next, prev
     * @param defaultCursor the default cursor to use if don't exist
     * @return Cursor, defaultCursor if don't exist
     */
    @Nullable
    default TransportCursor queryCursor(String name, @Nullable TransportCursor defaultCursor) {
        String cursor = request().queryParams(name);
        if (StringUtils.isBlank(cursor)) return defaultCursor;

        return TransportCursor.fromBase64(cursor);
    }
}
