package dev.fuxing.transport.service;


import dev.fuxing.transport.TransportList;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

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
 * <p>
 * - extra: (Map only)
 * Send extra data that is associated with payload.
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

        public Builder with(String name, Object data) {
            map.put(name, data);
            return this;
        }

        public Builder data(Object data) {
            return with("data", data);
        }

        public Builder data(TransportList list) {
            return data((Object) list).cursor(list);
        }

        public Builder cursor(Object cursor) {
            return with("cursor", cursor);
        }

        public Builder cursor(@NotNull TransportList list) {
            if (list.hasCursorMap()) {
                cursor(list.getCursorMap());
            }
            return this;
        }

        public Builder extra(Object extra) {
            return with("extra", extra);
        }

        public TransportResult build() {
            return new TransportResult(code, map);
        }
    }

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
