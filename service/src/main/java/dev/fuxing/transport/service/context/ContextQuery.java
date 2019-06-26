package dev.fuxing.transport.service.context;

import dev.fuxing.exception.BadRequestException;
import dev.fuxing.exception.ParamException;
import dev.fuxing.transport.TransportSort;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;

/**
 * Created by: Fuxing
 * Date: 2019-04-24
 * Time: 19:18
 */
public interface ContextQuery extends Context {
    /**
     * @param name name of query string
     * @return long value from query string
     * @throws ParamException query param not found
     */
    default long queryLong(String name) throws ParamException {
        try {
            String value = queryString(name);
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new ParamException(name);
        }
    }

    /**
     * @param name         name of query string
     * @param defaultValue default long value if not found
     * @return long value from query string
     * @throws ParamException query param not found
     */
    default long queryLong(String name, long defaultValue) throws ParamException {
        try {
            String value = request().queryParams(name);
            if (StringUtils.isBlank(value)) return defaultValue;
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new ParamException(name);
        }
    }

    /**
     * @param name name of query string
     * @return integer value from query string
     * @throws ParamException query param not found
     */
    default int queryInt(String name) throws ParamException {
        try {
            String value = queryString(name);
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ParamException(name);
        }
    }

    /**
     * @param name         name of query string
     * @param defaultValue default int value if not found
     * @return int value from query string
     * @throws ParamException query param not found
     */
    default int queryInt(String name, int defaultValue) throws ParamException {
        try {
            String value = request().queryParams(name);
            if (StringUtils.isBlank(value)) return defaultValue;
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ParamException(name);
        }
    }

    /**
     * @param defaultSize default size if not present
     * @param maxSize     max size if present
     * @return size value from query string
     */
    default int querySize(int defaultSize, int maxSize) {
        int size = queryInt("size", defaultSize);
        if (size <= 0) return defaultSize;
        if (size >= maxSize) return maxSize;
        return size;
    }

    /**
     * @param name name of query string
     * @return double value from query string
     * @throws ParamException query param not found
     */
    default double queryDouble(String name) throws ParamException {
        try {
            String value = queryString(name);
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new ParamException(name);
        }
    }

    /**
     * @param name         name of query string
     * @param defaultValue default double value if not found
     * @return double value from query string
     * @throws ParamException query param not found
     */
    default double queryDouble(String name, double defaultValue) throws ParamException {
        try {
            String value = request().queryParams(name);
            if (StringUtils.isBlank(value)) return defaultValue;
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new ParamException(name);
        }
    }

    /**
     * Boolean query string by checking string.equal("true")
     *
     * @param name name of query string
     * @return boolean value from query string
     * @throws ParamException query param not found
     */
    default boolean queryBool(String name) throws ParamException {
        return Boolean.parseBoolean(queryString(name));
    }

    /**
     * Boolean query string by checking string.equal("true")
     *
     * @param name         name of query string
     * @param defaultValue default boolean value if not found
     * @return boolean value from query string
     * @throws ParamException query param not found
     */
    default boolean queryBool(String name, boolean defaultValue) throws ParamException {
        String value = request().queryParams(name);
        if (StringUtils.isBlank(value)) return defaultValue;
        return Boolean.parseBoolean(value);
    }

    /**
     * E.g.
     * /path?name=&#x26;other=, queryPresent("name") = true
     * /path?name=abc&#x26;other=, queryPresent("name") = true
     * /path?name=abc&#x26;other=, queryPresent("none") = false
     * /path, queryPresent("name") = false
     *
     * @param name name of query string
     * @return whether the name exist whether it's blank or not
     */
    default boolean queryPresent(String name) {
        return request().queryParams(name) != null;
    }

    /**
     * @param name name of query string
     * @return String value
     * @throws ParamException query param not found
     */
    default String queryString(String name) throws ParamException {
        String value = request().queryParams(name);
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        throw new ParamException(name);
    }

    /**
     * @param name         name of query string
     * @param defaultValue default String value
     * @return String value
     */
    default String queryString(String name, String defaultValue) throws ParamException {
        String value = request().queryParams(name);
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return defaultValue;
    }

    /**
     * @param name         of query string
     * @param defaultValue to return if not found
     * @param clazz        class to bound Object to
     * @param <T>          T
     * @return Object value
     */
    @SuppressWarnings("unchecked")
    default <T> T queryObject(String name, T defaultValue, Class<T> clazz) {
        String value = request().queryParams(name);
        if (StringUtils.isBlank(value)) return defaultValue;
        if (clazz == String.class) return (T) value;

        return parseObject(name, value, clazz);
    }

    /**
     * See {@link dev.fuxing.transport.TransportSort} for more info.
     *
     * @param defaultSort default sort direction, (recommended to use desc for most operations)
     * @return sort direction, used for list operation
     */
    default TransportSort querySort(TransportSort defaultSort) {
        return queryEnum("sort", TransportSort.class, defaultSort);
    }

    /**
     * Will return default if not present or enum match not found.
     *
     * @param name         of enum
     * @param clazz        to bound Object to
     * @param defaultValue default enum value
     * @param <E>          Enum class
     * @return enum
     */
    @Nullable
    default <E extends Enum<E>> E queryEnum(String name, Class<E> clazz, @Nullable E defaultValue) {
        E num = EnumUtils.getEnum(clazz, queryString(name, null));
        if (num != null) return num;
        return defaultValue;
    }

    /**
     * @param name  of enum
     * @param clazz to bound Object to
     * @param <E>   Enum class
     * @return enum
     */
    default <E extends Enum<E>> E queryEnum(String name, Class<E> clazz) {
        E num = EnumUtils.getEnum(clazz, queryString(name));
        if (num != null) return num;


        throw new BadRequestException("Enum " + name + " is invalid.");
    }
}
