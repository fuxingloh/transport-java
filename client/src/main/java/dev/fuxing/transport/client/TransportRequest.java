package dev.fuxing.transport.client;


import com.fasterxml.jackson.databind.JsonNode;
import dev.fuxing.err.*;
import dev.fuxing.transport.TransportError;
import dev.fuxing.transport.client.request.RequestAs;
import dev.fuxing.transport.client.request.RequestQuery;
import dev.fuxing.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Created By: Fuxing Loh
 * Date: 18/3/2017
 * Time: 4:13 PM
 */
public class TransportRequest implements RequestQuery, RequestAs {
    protected Executor executor;

    protected String url;
    protected String body;

    protected List<Header> headers = new ArrayList<>();
    protected List<NameValuePair> queries = new ArrayList<>();

    protected Function<URI, Request> requestFunction;

    public TransportRequest(Function<URI, Request> requestFunction, String url) {
        this.url = url;
        this.requestFunction = requestFunction;
    }

    public String getUrl() {
        return url;
    }

    /**
     * @param name  in path put
     * @param value for replace in path
     * @return current TransportRequest instance for chaining
     */
    public TransportRequest path(String name, Object value) {
        url = url.replace("/:" + name, "/" + value.toString());
        return this;
    }

    @Override
    public RequestQuery query(String name, String value) {
        queries.add(new BasicNameValuePair(name, value));
        return this;
    }

    public TransportRequest header(String name, String value) {
        headers.add(new BasicHeader(name, value));
        return this;
    }

    /**
     * @param object object to convert to json
     * @return this
     */
    public TransportRequest body(Object object) {
        this.body = JsonUtils.toString(object);
        return this;
    }


    @Override
    public TransportResponse asResponse() {
        if (executor != null) return asResponse(executor);

        // Automatically default to SHARED_EXECUTOR if not set.
        return asResponse(TransportClient.SHARED_EXECUTOR);
    }

    public TransportResponse asResponse(Executor executor) {
        try {
            Request request = asRequest();
            Response response = executor.execute(request);
            return asResponse(response);
        } catch (Exception e) {
            throw new UnknownException("Unknown error.", e);
        }
    }

    protected Request asRequest() throws URISyntaxException {
        URIBuilder builder = new URIBuilder(url);
        builder.addParameters(queries);

        Request request = requestFunction.apply(builder.build());
        headers.forEach(request::addHeader);

        if (body != null) {
            request.bodyString(body, ContentType.APPLICATION_JSON);
        }
        return request;
    }

    protected TransportResponse asResponse(Response res) throws IOException {
        HttpResponse response = res.returnResponse();
        StatusLine status = response.getStatusLine();
        HttpEntity entity = response.getEntity();

        String json = EntityUtils.toString(entity, "UTF-8");
        try {
            // Empty Json body will be ignored.
            if (StringUtils.isBlank(json)) {
                JsonNode body = JsonUtils.jsonToTree(json);
                tryParseError(body);
                return new TransportResponse(response, body);
            }

            // Try Parse Status for 502, 503, 504
            tryParseStatus(status, null);
            return new TransportResponse(response, null);
        } catch (JsonException e) {
            tryParseStatus(status, e);
            throw e;
        } finally {
            EntityUtils.consumeQuietly(entity);
        }
    }

    private void tryParseError(JsonNode body) throws ErrorURL {
        if (!body.has("error")) return;

        TransportError error = JsonUtils.toObject(body.path("error"), TransportError.class);
        if (StringUtils.isAnyBlank(error.getUrl(), error.getMessage()) || error.getCode() == null) {
            throw new UnknownException("Failed to parsed error body.");
        }
        throw new ErrorURL(error.getCode(), error.getUrl(), error.getMessage());
    }

    private void tryParseStatus(StatusLine status, Exception e) throws ErrorURL {
        // 502, 503, 504: all gateway related status code
        switch (status.getStatusCode()) {
            case 502:
                throw new BadGatewayException();
            case 503:
                throw new UnavailableException();
            case 504:
                throw new TimeoutException(504, "Request from client to server has timeout.", e);
        }
    }
}
