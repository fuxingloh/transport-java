package dev.fuxing.transport.service;

import dev.fuxing.utils.JsonUtils;
import spark.ResponseTransformer;

import java.util.Map;

/**
 * Created by: Fuxing
 * Date: 16/6/2017
 * Time: 1:46 PM
 */
public class TransportTransformer implements ResponseTransformer {
    public static final String EMPTY = "{}";

    /**
     * @param result to convert to string
     * @return converted to string
     */
    public String render(TransportResult result) {
        if (result.getMap() == null || result.getMap().isEmpty()) {
            return EMPTY;
        }

        return toString(result.getMap());
    }

    /**
     * Override this method for custom serialization.
     * - e.g. for simplifying objects
     *
     * @param map to convert to string
     * @return JSON represented in String
     */
    protected String toString(Map<String, Object> map) {
        return JsonUtils.toString(map);
    }

    /**
     * @see TransportResult
     * @see TransportRoute
     */
    @Override
    public String render(Object model) throws Exception {
        // Force casted to TransportResult because technically it should only return TransportResult
        // See TransportRoute
        return render((TransportResult) model);
    }
}
