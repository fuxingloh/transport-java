package dev.fuxing.transport.service.context;

import dev.fuxing.err.BadRequestException;
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
     * @throws BadRequestException path param not found
     */
    default long pathLong(String name) throws BadRequestException {
        try {
            String value = pathString(name);
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new BadRequestException("The request could not be understood by the server due to malformed path.");
        }
    }

    /**
     * @param name         of path param
     * @param defaultValue if not found
     * @return Long value
     * @throws BadRequestException if param not Long
     */
    default Long pathLong(String name, Long defaultValue) throws BadRequestException {
        try {
            String value = pathString(name);
            if (value == null) return defaultValue;

            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new BadRequestException("The request could not be understood by the server due to malformed path.");
        }
    }

    /**
     * @param name name of path param
     * @return Int value
     * @throws BadRequestException path param not found
     */
    default int pathInt(String name) throws BadRequestException {
        try {
            String value = pathString(name);
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new BadRequestException("The request could not be understood by the server due to malformed path.");
        }
    }

    /**
     * @param name         of path param
     * @param defaultValue if not found
     * @return Integer value
     * @throws BadRequestException if param not Long
     */
    default Integer pathInt(String name, Integer defaultValue) throws BadRequestException {
        try {
            String value = pathString(name);
            if (value == null) return defaultValue;

            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new BadRequestException("The request could not be understood by the server due to malformed path.");
        }
    }

    /**
     * @param name name of path param
     * @return Double value
     * @throws BadRequestException path param not found
     */
    default double pathDouble(String name) throws BadRequestException {
        try {
            String value = pathString(name);
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new BadRequestException("The request could not be understood by the server due to malformed path.");
        }
    }

    /**
     * @param name         of path param
     * @param defaultValue if not found
     * @return Double value
     * @throws BadRequestException if param not long
     */
    default Double pathDouble(String name, Double defaultValue) throws BadRequestException {
        try {
            String value = pathString(name);
            if (value == null) return defaultValue;

            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new BadRequestException("The request could not be understood by the server due to malformed path.");
        }
    }

    /**
     * @param name name of path param
     * @return String value
     * @throws BadRequestException path param not found
     */
    default String pathString(String name) throws BadRequestException {
        String value = request().params(name);
        if (StringUtils.isNotBlank(value)) {
            return value;
        }

        throw new BadRequestException("The request could not be understood by the server due to malformed path.");
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
