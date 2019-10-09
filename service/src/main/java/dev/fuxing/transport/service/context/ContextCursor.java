package dev.fuxing.transport.service.context;

import dev.fuxing.transport.TransportCursor;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by: Fuxing
 * Date: 2019-06-14
 * Time: 06:07
 */
public interface ContextCursor extends Context {

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
     * @param name of the cursor
     * @return Cursor, if not found will attempt to read from TransportContext
     */
    @NotNull
    default TransportCursor queryCursor(String name) {
        TransportCursor.Builder builder = TransportCursor.builder();

        String cursor = request().queryParams(name);
        if (StringUtils.isNotBlank(cursor)) {
            builder.base64(cursor);
        }

        builder.putAll(queryParamsMap(name));
        return builder.build();
    }

    /**
     * @param ignores key to ignore
     * @return Map parameters with ignores trimmed out
     */
    @NotNull
    private Map<String, String> queryParamsMap(String... ignores) {
        Map<String, String> map = new HashMap<>();

        request().queryParams().forEach(s -> {
            for (String ignore : ignores) {
                if (s.equals(ignore)) return;
            }
            map.put(s, request().queryParams(s));
        });
        return map;
    }

    /**
     * @param name          cursor name, e.g. next, prev
     * @param defaultCursor the default cursor to use if don't exist
     * @return Cursor, defaultCursor if don't exist
     */
    @Nullable
    default TransportCursor queryCursor(String name, @Nullable TransportCursor defaultCursor) {
        String cursor = request().queryParams(name);
        if (StringUtils.isNotBlank(cursor)) {
            return TransportCursor.fromBase64(cursor);
        }
        return defaultCursor;
    }
}
