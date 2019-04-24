package dev.fuxing.transport.client;


import com.fasterxml.jackson.databind.JsonNode;
import dev.fuxing.exception.StatusException;
import dev.fuxing.transport.TransportList;
import dev.fuxing.utils.JsonUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Function;

/**
 * Created By: Fuxing Loh
 * Date: 18/3/2017
 * Time: 4:19 PM
 */
public class TransportResponse {
    private final JsonNode body;
    private final HttpResponse response;

    /**
     * For error parser, as long there is error node it is consider a error and will be converted to Structured Error
     * <pre>
     * {
     *      "data": ...,
     *      "next": ...,
     *      "error": ...
     * }
     * </pre>
     *
     * @param response http response
     */
    TransportResponse(HttpResponse response, @Nullable JsonNode body) {
        this.response = response;
        this.body = body;
    }

    public int getStatus() {
        StatusLine status = response.getStatusLine();
        return status.getStatusCode();
    }

    /**
     * @return Response Headers (map) with <b>same case</b> as server response.
     * For instance use <code>getHeaders().getFirst("Location")</code> and not <code>getHeaders().getFirst("location")</code> to get first header "Location"
     */
    public Header[] getHeaders() {
        return response.getAllHeaders();
    }

    /**
     * @param key of the header
     * @return get first header
     */
    public String getHeader(String key) {
        Header header = response.getFirstHeader(key);
        if (header == null) return null;
        return header.getValue();
    }

    /**
     * @return json node
     */
    public JsonNode getNode() {
        return body;
    }

    /**
     * @return json node
     */
    public JsonNode getDataNode() {
        return getNode().path("data");
    }

    /**
     * @param clazz class of object
     * @param <T>   type of object
     * @return object if data node is present
     * return null if node is null or missing
     */
    public <T> T asDataObject(Class<T> clazz) {
        JsonNode data = getDataNode();
        if (data.isNull() || data.isMissingNode()) return null;
        return JsonUtils.toObject(data, clazz);
    }

    /**
     * @param clazz for deserialize
     * @param <T>   Class to use for deserialize
     * @param <R>   Class to use for holding the list value, R must extend T
     * @return Transport List
     */
    public <T extends R, R> TransportList<R> asDataList(Class<T> clazz) {
        List<T> list = JsonUtils.toList(getDataNode(), clazz);
        JsonNode cursor = getNode().path("cursor");
        if (cursor.isObject()) {
            return new TransportList<>(list, JsonUtils.toMap(cursor, String.class, String.class));
        }
        return new TransportList<>(list);
    }

    /**
     * @param keyClass   class for Key
     * @param valueClass class for Value
     * @param <K>        key class
     * @param <V>        value class
     * @return Map
     */
    public <K, V> Map<K, V> asDataMap(Class<K> keyClass, Class<V> valueClass) {
        return JsonUtils.toMap(getDataNode(), keyClass, valueClass);
    }

    /**
     * @param mapper map from root node to any result
     * @param <T>    Type to return
     * @return Type
     */
    public <T> T as(Function<JsonNode, T> mapper) {
        return mapper.apply(getNode());
    }

    /**
     * Validate status code of response
     *
     * @param codes status codes to validate
     * @return TransportResponse for fluent chaining
     */
    public TransportResponse hasCode(int... codes) {
        int code = getStatus();

        for (int i : codes) {
            if (i == code) return this;
        }

        StringJoiner joiner = new StringJoiner(", ");
        for (int i : codes) {
            joiner.add(String.valueOf(i));
        }
        throw new StatusException(code, "Explicit validation on code(" + joiner.toString() + ") failed.");
    }
}