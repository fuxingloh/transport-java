package dev.fuxing.transport.service;

import dev.fuxing.transport.service.context.*;
import spark.Request;
import spark.Response;

/**
 * Created by: Fuxing
 * Date: 17/3/2017
 * Time: 1:23 AM
 */
public class TransportContext implements ContextBody, ContextHeader, ContextPath, ContextQuery, ContextCursor {
    private final Request request;
    private final Response response;

    /**
     * @param request  spark request
     * @param response spark response
     */
    public TransportContext(Request request, Response response) {
        this.request = request;
        this.response = response;
    }

    /**
     * @return Spark request
     */
    public Request request() {
        return request;
    }

    /**
     * @return Spark response
     */
    public Response response() {
        return response;
    }
}
