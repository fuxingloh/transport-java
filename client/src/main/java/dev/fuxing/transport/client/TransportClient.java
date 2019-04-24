package dev.fuxing.transport.client;


import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;

/**
 * Created By: Fuxing Loh
 * Date: 18/3/2017
 * Time: 3:42 PM
 */
public abstract class TransportClient {
    protected static final Executor SHARED_EXECUTOR = Executor.newInstance();

    protected final String url;

    /**
     * @param url must not end with /
     */
    public TransportClient(String url) {
        this.url = url;
    }

    /**
     * @param path path must begin with /
     * @return full path
     */
    private String path(String path) {
        return url + path;
    }

    /**
     * Do something before the request get sent
     * E.g.
     * - Parse Data
     * - Do something
     *
     * @param request to intercept
     * @return modified or same request
     */
    protected TransportRequest before(TransportRequest request) {
        return request;
    }

    protected TransportRequest internalBefore(TransportRequest request) {
        request.executor = SHARED_EXECUTOR;
        return before(request);
    }

    protected TransportRequest doGet(String path) {
        TransportRequest request = new TransportRequest(Request::Get, path(path));
        return internalBefore(request);
    }

    protected TransportRequest doHead(String path) {
        TransportRequest request = new TransportRequest(Request::Head, path(path));
        return internalBefore(request);
    }

    protected TransportRequest doOptions(String path) {
        TransportRequest request = new TransportRequest(Request::Options, path(path));
        return internalBefore(request);
    }

    protected TransportRequest doPost(String path) {
        TransportRequest request = new TransportRequest(Request::Post, path(path));
        return internalBefore(request);
    }

    protected TransportRequest doDelete(String path) {
        TransportRequest request = new TransportRequest(Request::Delete, path(path));
        return internalBefore(request);
    }

    protected TransportRequest doPatch(String path) {
        TransportRequest request = new TransportRequest(Request::Patch, path(path));
        return internalBefore(request);
    }

    protected TransportRequest doPut(String path) {
        TransportRequest request = new TransportRequest(Request::Put, path(path));
        return internalBefore(request);
    }
}
