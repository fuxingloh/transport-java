package dev.fuxing.transport.client;


import dev.fuxing.exception.StatusException;
import org.apache.http.client.fluent.Request;

/**
 * Created by: Fuxing
 * Date: 2019-04-15
 * Time: 18:13
 */
public class HealthClient {

    private final String url;

    /**
     * @param host endpoint only
     */
    public HealthClient(String host) {
        this(host, "/health/check");
    }

    /**
     * @param host endpoint
     * @param path path
     */
    public HealthClient(String host, String path) {
        this.url = host + path;
    }

    /**
     * @throws StatusException                         if not 200
     * @throws dev.fuxing.exception.TransportException if is any of the transport error
     */
    public void check() {
        new TransportRequest(Request::Get, url)
                .hasCode(200);
    }
}
