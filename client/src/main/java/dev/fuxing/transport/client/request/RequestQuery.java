package dev.fuxing.transport.client.request;

import java.util.Collection;
import java.util.Map;

/**
 * Created by: Fuxing
 * Date: 2019-04-24
 * Time: 21:25
 */
public interface RequestQuery<T extends RequestQuery> {
    T query(String name, String value);

    @SuppressWarnings("unchecked")
    default T query(Map<String, Object> parameters) {
        parameters.forEach((s, o) -> query(s, o.toString()));
        return (T) this;
    }

    default T query(String name) {
        return query(name, "");
    }

    default T query(String name, Long value) {
        return query(name, String.valueOf(value));
    }

    default T query(String name, Integer value) {
        return query(name, String.valueOf(value));
    }

    @SuppressWarnings("unchecked")
    default T query(String name, Object... values) {
        for (Object value : values) {
            query(name, value.toString());
        }
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    default T query(String name, Collection<?> collection) {
        for (Object value : collection) {
            query(name, value.toString());
        }
        return (T) this;
    }

    default T queryCursor(String cursor) {
        return query("cursor", cursor);
    }
}
