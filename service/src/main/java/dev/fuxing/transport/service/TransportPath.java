package dev.fuxing.transport.service;

import spark.RouteGroup;
import spark.Spark;

/**
 * Created by: Fuxing
 * Date: 2019-08-13
 * Time: 20:41
 */
public interface TransportPath {
    /**
     * @param path   path for before filter, accepts wildcards
     * @param filter json filter
     */
    default void BEFORE(String path, TransportFilter filter) {
        Spark.before(path, filter);
    }

    default void AFTER(String path, TransportFilter filter) {
        Spark.after(path, filter);
    }

    default void AFTER(TransportFilter filter) {
        Spark.after(filter);
    }

    default void AFTER_AFTER(String path, TransportFilter filter) {
        Spark.afterAfter(path, filter);
    }

    default void AFTER_AFTER(TransportFilter filter) {
        Spark.afterAfter(filter);
    }

    /**
     * @param path       path to add prefix to route
     * @param routeGroup route
     */
    default void PATH(String path, RouteGroup routeGroup) {
        Spark.path(path, routeGroup);
    }
}
