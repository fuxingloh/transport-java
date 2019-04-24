package dev.fuxing.transport.service;

import spark.Filter;
import spark.Request;
import spark.Response;

/**
 * Lambda Route interface
 * <p>
 * Created by: Fuxing
 * Date: 7/3/2017
 * Time: 4:22 PM
 */
@FunctionalInterface
public interface TransportFilter extends Filter {

    /**
     * Invoked when a request is made on this route's corresponding path e.g. '/hello'
     *
     * @param context Context object contains request and response object
     * @throws Exception implementation can choose to throw exception
     */
    void handle(TransportContext context) throws Exception;

    @Override
    default void handle(Request request, Response response) throws Exception {
        handle(new TransportContext(request, response));
    }
}