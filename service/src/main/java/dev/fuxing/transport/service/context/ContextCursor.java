package dev.fuxing.transport.service.context;

import dev.fuxing.err.ParamException;
import dev.fuxing.transport.TransportCursor;
import org.apache.commons.lang3.EnumUtils;
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

    /**
     * Cursor &#x3E; Query &#x3E; Default
     *
     * @param name         of enum
     * @param clazz        to bound Object to
     * @param cursor       to read from first
     * @param defaultValue default enum value
     * @param <E>          Enum class
     * @return enum
     */
    @Nullable
    default <E extends Enum<E>> E queryEnum(String name, Class<E> clazz, TransportCursor cursor, @Nullable E defaultValue) {
        E num = cursor.getEnum(name, clazz, null);
        if (num != null) return num;

        num = EnumUtils.getEnum(clazz, request().queryParams(name));
        if (num != null) return num;
        return defaultValue;
    }

    /**
     * Cursor &#x3E; Query &#x3E; Default
     *
     * @param name         name of query string
     * @param cursor       to read from first
     * @param defaultValue default String value
     * @return String value
     */
    default String queryString(String name, TransportCursor cursor, String defaultValue) throws ParamException {
        String value = cursor.get(name, null);
        if (value != null) return value;

        value = request().queryParams(name);
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return defaultValue;
    }

    /**
     * Cursor &#x3E; Query &#x3E; Default
     *
     * @param name         name of query string
     * @param cursor       to read from first
     * @param defaultValue default int value if not found
     * @return int value from query string
     * @throws ParamException query param not found
     */
    default Integer queryInt(String name, TransportCursor cursor, @Nullable Integer defaultValue) throws ParamException {
        Integer integer = cursor.getInt(name, null);
        if (integer != null) return integer;

        try {
            String value = request().queryParams(name);
            if (StringUtils.isBlank(value)) return defaultValue;
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ParamException(name);
        }
    }

    /**
     * Cursor &#x3E; Query &#x3E; Default
     *
     * @param name         name of query string
     * @param cursor       to read from first
     * @param defaultValue default long value if not found
     * @return long value from query string
     * @throws ParamException query param not found
     */
    default Long queryLong(String name, TransportCursor cursor, @Nullable Long defaultValue) throws ParamException {
        Long aLong = cursor.getLong(name, null);
        if (aLong != null) return aLong;

        try {
            String value = request().queryParams(name);
            if (StringUtils.isBlank(value)) return defaultValue;
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new ParamException(name);
        }
    }

    /**
     * Cursor &#x3E; Query &#x3E; Default
     *
     * @param name         name of query string
     * @param cursor       to read from first
     * @param defaultValue default double value if not found
     * @return double value from query string
     * @throws ParamException query param not found
     */
    default Double queryDouble(String name, TransportCursor cursor, @Nullable Double defaultValue) throws ParamException {
        Double aDouble = cursor.getDouble(name, null);
        if (aDouble != null) return aDouble;

        try {
            String value = request().queryParams(name);
            if (StringUtils.isBlank(value)) return defaultValue;
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new ParamException(name);
        }
    }
}
