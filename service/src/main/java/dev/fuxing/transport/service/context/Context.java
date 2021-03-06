package dev.fuxing.transport.service.context;

import dev.fuxing.err.BadRequestException;
import spark.Request;
import spark.Response;

/**
 * Created by: Fuxing
 * Date: 2019-04-24
 * Time: 19:17
 */
public interface Context {
    /**
     * @return Spark request
     */
    Request request();

    /**
     * @return Spark response
     */
    Response response();

    /**
     * This kinda act as a session data
     *
     * @param clazz of session data, name of class will be used for identifier
     * @param <T>   type
     * @return Api session data stored or <code>null</code>
     */
    default <T> T get(Class<T> clazz) {
        return request().attribute(clazz.getName());
    }

    /**
     * This kinda act as a session data
     *
     * @param data to put into session
     */
    default void put(Object data) {
        request().attribute(data.getClass().getName(), data);
    }

    @SuppressWarnings({"unchecked", "WrapperTypeMayBePrimitive"})
    default <T> T parseObject(String name, String value, Class<T> clazz) {
        try {
            if (clazz == Long.class) {
                Long i = Long.parseLong(value);
                return (T) i;
            }
            if (clazz == Integer.class) {
                Integer i = Integer.parseInt(value);
                return (T) i;
            }
        } catch (NumberFormatException e) {
            throw new BadRequestException("The request could not be understood by the server due to malformed " + name + ".");
        }

        if (clazz == Boolean.class) {
            Boolean b = Boolean.parseBoolean(value);
            return (T) b;
        }

        throw new IllegalStateException(clazz.getSimpleName() + " is not implemented for parseObject()");
    }
}
