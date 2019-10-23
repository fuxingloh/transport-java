package dev.fuxing.transport.service;


import dev.fuxing.transport.TransportCursor;
import dev.fuxing.transport.TransportList;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * TransportResult.
 * Non-Error, error handling should throw TransportException instead.
 * <p>
 * Reversed for other purpose. (RESTRICTED)
 * - error
 * - meta (Compatibility with old version)
 * - status (Compatibility with old version)
 * - code (Compatibility with old version)
 * <p>
 * Reversed for specific purpose. (USE CASE)
 * - data (Map or List)
 * Main payload.
 * <p>
 * - cursor: (Map only)
 * Used together with TransportList pagination.
 * E.g.
 * "next": "ey..."
 * "next.articles": "ey..."
 * "articles.next": "ey..."
 * <p>
 * - extra: (Map only)
 * Send extra data that is associated with payload.
 * E.g.
 * "profile": {},
 * "profile.images": [{}, {}]
 *
 * @author Fuxing Loh
 * @since 2018-06-04 at 13:31
 */
public class TransportResult {
    private final int code;
    private final Map<String, Object> map;

    protected TransportResult(int code, Map<String, Object> map) {
        this.code = code;
        this.map = map;
    }

    public int getCode() {
        return code;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    /**
     * @return TransportResult with 404 status
     */
    public static TransportResult notFound() {
        return new TransportResult(404, Map.of());
    }

    /**
     * @return TransportResult with 200 status
     */
    public static TransportResult ok() {
        return new TransportResult(200, Map.of());
    }

    /**
     * TransportResult Builder
     */
    public static class Builder {
        private int code = 200;

        private Object data;
        private Map<String, String> cursor = new HashMap<>();
        private Map<String, Object> extra = new HashMap<>();

        private Map<String, Object> map = new HashMap<>();

        private Builder() {
        }

        public Builder code(int code) {
            this.code = code;
            return this;
        }

        public Builder notFound() {
            return code(404);
        }

        /**
         * @param name of root level node
         * @param data also known as the node
         * @return current Builder instance
         */
        public Builder with(String name, Object data) {
            Objects.requireNonNull(name);
            map.put(name, data);
            return this;
        }

        public Builder data(Object data) {
            this.data = data;
            return this;
        }

        public Builder data(TransportList list) {
            return data((Object) list).cursor(list);
        }

        public Builder cursor(Map<String, String> cursor) {
            this.cursor.putAll(cursor);
            return this;
        }

        public Builder cursor(@NotNull TransportList<?> list) {
            if (list.hasCursorMap()) {
                return cursor(list.getCursorMap());
            }
            return this;
        }

        public Builder cursor(String key, String base64) {
            Objects.requireNonNull(key);
            this.cursor.put(key, base64);
            return this;
        }

        public Builder cursor(String key, TransportCursor cursor) {
            Objects.requireNonNull(key);
            this.cursor.put(key, cursor.toBase64());
            return this;
        }

        public Builder extra(Map<String, Object> extra) {
            this.extra.putAll(extra);
            return this;
        }

        public Builder extra(String key, Object object) {
            Objects.requireNonNull(key);
            this.extra.put(key, object);
            return this;
        }

        public TransportResult build() {
            if (data != null) {
                map.put("data", data);
            }

            if (!cursor.isEmpty()) {
                map.put("cursor", cursor);
            }

            if (!extra.isEmpty()) {
                map.put("extra", extra);
            }
            return new TransportResult(code, map);
        }
    }

    /**
     * @return TransportResult.Builder for building TransportResult
     */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "TransportResult{" +
                "code=" + code +
                ", map=" + map +
                '}';
    }
}
