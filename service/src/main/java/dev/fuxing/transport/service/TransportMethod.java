package dev.fuxing.transport.service;

import spark.Spark;

/**
 * Created by: Fuxing
 * Date: 2019-08-13
 * Time: 20:40
 */
public interface TransportMethod {

    TransportTransformer toJson = new TransportTransformer();

    /**
     * Override for custom transformer
     *
     * @return default toJson transformer for json service to use
     */
    default TransportTransformer toJson() {
        return toJson;
    }

    /**
     * Map route for HTTP Get
     *
     * @param path  the path
     * @param route transport routing
     */
    default void GET(String path, TransportRoute route) {
        Spark.get(path, route, toJson());
    }

    /**
     * Map route for HTTP Post
     *
     * @param path  the path
     * @param route transport routing
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
     * @param route transport routing
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
     * @param route transport routing
     */
    default void DELETE(String path, TransportRoute route) {
        Spark.delete(path, route, toJson());
    }

    /**
     * Map route for HTTP Head
     *
     * @param path  the path
     * @param route transport routing
     */
    default void HEAD(String path, TransportRoute route) {
        Spark.head(path, route, toJson());
    }

    /**
     * Map route for HTTP Patch
     *
     * @param path  the path
     * @param route transport routing
     */
    default void PATCH(String path, TransportRoute route) {
        Spark.patch(path, route, toJson());
    }

    /**
     * Map route for HTTP Options
     *
     * @param path  the path
     * @param route transport routing
     */
    default void OPTIONS(String path, TransportRoute route) {
        Spark.options(path, route, toJson());
    }
}
