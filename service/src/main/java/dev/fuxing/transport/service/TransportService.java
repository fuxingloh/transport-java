package dev.fuxing.transport.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.fuxing.utils.JsonUtils;
import spark.RouteGroup;
import spark.Spark;

/**
 * Created By: Fuxing Loh
 * Date: 8/2/2017
 * Time: 2:29 PM
 *
 * @see dev.fuxing.transport.TransportError for structure for error
 */
public interface TransportService {
    ObjectMapper objectMapper = JsonUtils.objectMapper;

    TransportTransformer toJson = new TransportTransformer();

    /**
     * Start the router
     * By wiring all the routes
     */
    default void start() {
        route();
    }

    /**
     * Wire all the routes
     */
    void route();

    /**
     * Override for custom transformer
     *
     * @return default toJson transformer for json service to use
     */
    default TransportTransformer toJson() {
        return toJson;
    }

    /**
     * @param path   path for before filter, accepts wildcards
     * @param filter json filter
     */
    default void BEFORE(String path, TransportFilter filter) {
        Spark.before(path, filter);
    }

    /**
     * @param path       path to add prefix to route
     * @param routeGroup route
     */
    default void PATH(String path, RouteGroup routeGroup) {
        Spark.path(path, routeGroup);
    }

    /**
     * Map route for HTTP Get
     *
     * @param path  the path
     * @param route json route
     */
    default void GET(String path, TransportRoute route) {
        Spark.get(path, route, toJson());
    }

    /**
     * Map route for HTTP Post
     *
     * @param path  the path
     * @param route json route
     */
    default void POST(String path, TransportRoute route) {
        Spark.post(path, route, toJson());
    }

    /**
     * Map route for HTTP Put
     *
     * @param path       the path
     * @param acceptType the request accept type
     * @param route      json node route
     */
    default void POST(String path, String acceptType, TransportRoute route) {
        Spark.post(path, acceptType, route, toJson);
    }

    /**
     * Map route for HTTP Put
     *
     * @param path  the path
     * @param route json route
     */
    default void PUT(String path, TransportRoute route) {
        Spark.put(path, route, toJson());
    }

    /**
     * Map route for HTTP Put
     *
     * @param path       the path
     * @param acceptType the request accept type
     * @param route      json node route
     */
    default void PUT(String path, String acceptType, TransportRoute route) {
        Spark.put(path, acceptType, route, toJson);
    }

    /**
     * Map route for HTTP Delete
     *
     * @param path  the path
     * @param route json route
     */
    default void DELETE(String path, TransportRoute route) {
        Spark.delete(path, route, toJson());
    }

    /**
     * Map route for HTTP Head
     *
     * @param path  the path
     * @param route json route
     */
    default void HEAD(String path, TransportRoute route) {
        Spark.head(path, route, toJson());
    }

    /**
     * Map route for HTTP Patch
     *
     * @param path  the path
     * @param route json route
     */
    default void PATCH(String path, TransportRoute route) {
        Spark.patch(path, route, toJson());
    }

    /**
     * @param code status code
     * @return TransportResult
     */
    default TransportResult result(int code) {
        return TransportResult.of(code);
    }

    /**
     * @param code   status code
     * @param object data object
     * @return TransportResult
     */
    default TransportResult result(int code, Object object) {
        return TransportResult.of(code, object);
    }
}
