package dev.fuxing.transport.client.request;

import com.fasterxml.jackson.databind.JsonNode;
import dev.fuxing.transport.TransportList;
import dev.fuxing.transport.client.TransportResponse;

import java.util.Map;

/**
 * Created by: Fuxing
 * Date: 2019-04-24
 * Time: 21:40
 */
public interface RequestAs {

    default JsonNode asNode() {
        return asResponse().getNode();
    }

    default JsonNode asDataNode() {
        return asResponse().getDataNode();
    }

    /**
     * @param clazz class of object
     * @param <T>   type of object
     * @return object if data node is present
     * return null if node is null or missing
     */
    default <T> T asDataObject(Class<T> clazz) {
        return asResponse().asDataObject(clazz);
    }

    /**
     * @param clazz for deserialize
     * @param <T>   Class to use for deserialize
     * @param <R>   Class to use for holding the list value, R must extend T
     * @return Transport List
     */
    default <T extends R, R> TransportList<R> asDataList(Class<T> clazz) {
        return asResponse().asDataList(clazz);
    }

    /**
     * @param keyClass   class for Key
     * @param valueClass class for Value
     * @param <K>        key class
     * @param <V>        value class
     * @return Map
     */
    default <K, V> Map<K, V> asDataMap(Class<K> keyClass, Class<V> valueClass) {
        return asResponse().asDataMap(keyClass, valueClass);
    }

    /**
     * Call service and convert request to response
     * At the same time OfflineException and TimeoutException is parsed
     * Other exception will be parsed to unknown exception
     *
     * @return TransportResponse
     */
    TransportResponse asResponse();
}
