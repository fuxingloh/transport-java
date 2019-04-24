package dev.fuxing.transport.service.context;

import dev.fuxing.exception.ParamException;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by: Fuxing
 * Date: 2019-04-24
 * Time: 19:18
 */
public interface ContextPath extends Context {
    /**
     * @return path of the call
     */
    default String pathInfo() {
        return request().pathInfo();
    }

    /**
     * @param name         of query string
     * @param defaultValue to return if not found
     * @param clazz        class to bound Object to
     * @param <T>          T
     * @return Object value
     */
    @SuppressWarnings("unchecked")
    default <T> T pathObject(String name, T defaultValue, Class<T> clazz) {
        String value = request().params(name);
        if (StringUtils.isBlank(value)) return defaultValue;
        if (clazz == String.class) return (T) value;

        return parseObject(name, value, clazz);
    }

    /**
     * @param name of path param
     * @return Long value
     * @throws ParamException path param not found
     */
    default long pathLong(String name) throws ParamException {
        try {
            String value = pathString(name);
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new ParamException(name);
        }
    }

    /**
     * @param name         of path param
     * @param defaultValue if not found
     * @return Long value
     * @throws ParamException if param not Long
     */
    default Long pathLong(String name, Long defaultValue) throws ParamException {
        try {
            String value = pathString(name);
            if (value == null) return defaultValue;

            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new ParamException(name);
        }
    }

    /**
     * @param name name of path param
     * @return Int value
     * @throws ParamException path param not found
     */
    default int pathInt(String name) throws ParamException {
        try {
            String value = pathString(name);
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ParamException(name);
        }
    }

    /**
     * @param name         of path param
     * @param defaultValue if not found
     * @return Integer value
     * @throws ParamException if param not Long
     */
    default Integer pathInt(String name, Integer defaultValue) throws ParamException {
        try {
            String value = pathString(name);
            if (value == null) return defaultValue;

            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ParamException(name);
        }
    }

    /**
     * @param name name of path param
     * @return Double value
     * @throws ParamException path param not found
     */
    default double pathDouble(String name) throws ParamException {
        try {
            String value = pathString(name);
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new ParamException(name);
        }
    }

    /**
     * @param name         of path param
     * @param defaultValue if not found
     * @return Double value
     * @throws ParamException if param not long
     */
    default Double pathDouble(String name, Double defaultValue) throws ParamException {
        try {
            String value = pathString(name);
            if (value == null) return defaultValue;

            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new ParamException(name);
        }
    }

    /**
     * @param name name of path param
     * @return String value
     * @throws ParamException path param not found
     */
    default String pathString(String name) throws ParamException {
        String value = request().params(name);
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        throw new ParamException(name);
    }

    /**
     * @param name         of path param
     * @param defaultValue if not found
     * @return String value, or default
     */
    default String pathString(String name, String defaultValue) {
        String value = request().params(name);
        if (value != null) return value;
        return defaultValue;
    }
}
