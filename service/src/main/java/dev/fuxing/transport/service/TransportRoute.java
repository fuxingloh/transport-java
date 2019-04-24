package dev.fuxing.transport.service;

import dev.fuxing.transport.TransportList;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Lambda Route interface
 * <p>
 * Created by: Fuxing
 * Date: 7/3/2017
 * Time: 4:22 PM
 */
@FunctionalInterface
public interface TransportRoute extends Route {
    String APP_JSON = "application/json; charset=utf-8";

    /**
     * Invoked when a request is made on this route's corresponding path e.g. '/hello'
     *
     * @param context context object contains request and response object
     * @return The content to be set in the response
     * @throws Exception implementation can choose to throw exception
     */
    Object handle(TransportContext context) throws Exception;

    /**
     * @param request  spark request
     * @param response spark response
     * @return TransportResult
     * @throws dev.fuxing.exception.TransportException auto convert into {error: {}}
     * @throws Exception                               auto convert into {error: {type: "UnknownException", ...}}
     * @see Object auto convert into {data: ...}
     * @see TransportResult auto convert into {...}
     * @see TransportList auto convert into {data: [], cursor: {}}
     */
    @Override
    default TransportResult handle(Request request, Response response) throws Exception {
        return handle(request, response, this::handle);
    }

    /**
     * Static handler for implementing classes to override
     *
     * @param request  spark request
     * @param response spark response
     * @param handler  {@code Object handle(TransportContext context)}
     * @return TransportResult
     * @throws dev.fuxing.exception.TransportException auto convert into {error: {}}
     * @throws Exception                               auto convert into {error: {type: "UnknownException", ...}}
     * @see Object auto convert into {data: ...}
     * @see TransportResult auto convert into {...}
     * @see TransportList auto convert into {data: [], cursor: {}}
     */
    static TransportResult handle(Request request, Response response, Handler handler) throws Exception {
        Object result = handler.handle(new TransportContext(request, response));
        response.type(APP_JSON);
        response.status(200);

        if (result instanceof TransportResult) {
            response.status(((TransportResult) result).getCode());
            return (TransportResult) result;
        }

        if (result instanceof TransportList) {
            TransportResult transportResult = TransportResult.ok(result);
            TransportList transportList = ((TransportList) result);

            if (transportList.hasCursorMap()) {
                transportResult.put("cursor", transportList.getCursorMap());
            }
            return transportResult;
        }

        if (result == null) {
            response.status(404);
            return TransportResult.notFound();
        }

        return TransportResult.ok(result);
    }

    interface Handler {
        Object handle(TransportContext context) throws Exception;
    }
}