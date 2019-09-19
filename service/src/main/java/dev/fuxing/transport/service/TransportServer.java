package dev.fuxing.transport.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.config.ConfigFactory;
import dev.fuxing.err.*;
import dev.fuxing.transport.TransportError;
import dev.fuxing.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Response;
import spark.Spark;

import java.net.SocketTimeoutException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Created by: Fuxing
 * Date: 9/12/2016
 * Time: 6:47 PM
 */
public class TransportServer implements TransportPath {
    protected static final Logger logger = LoggerFactory.getLogger(TransportServer.class);
    protected static final String DEFAULT_HEALTH_PATH = "/health/check";

    protected final TransportService[] services;
    private boolean started = false;

    protected boolean debug = true;

    /**
     * @param services array of routes for spark server to route with
     */
    public TransportServer(TransportService... services) {
        this.services = services;
    }

    /**
     * Support for guice injections
     *
     * @param services set of routes for spark server to route with
     */
    public TransportServer(Collection<TransportService> services) {
        this(services.toArray(new TransportService[0]));
    }

    /**
     * Start transport server with default port in the config = http.port
     * Port number can also be injected in the env as: HTTP_PORT
     *
     * @see TransportServer#start(int)
     */
    public void start() {
        start(ConfigFactory.load().getInt("http.port"));
    }

    /**
     * Start Spark Json Server with given services
     * Expected status code spark server should return is
     * 200: ok, no error in request
     * 400: transport error, constructed error from developer
     * 500: unknown error, all exception
     * 404: not found, endpoint not found
     * <p>
     * body: always json
     * <pre>
     * {
     *     data: {name: "Explicit data body"},
     *     "other": {name: "Other implicit data body"}
     * }
     * </pre>
     *
     * @param port port to run server with
     */
    public void start(int port) {
        // Setup port
        Spark.port(port);

        // Logging Setup
        logger.info("Path logging is registered to trace.");
        // Because it is trace, to activate logging

        // set dev.fuxing.transport.service.TransportServer to trace
        Spark.before((request, response) -> {
            if (!request.pathInfo().equals(DEFAULT_HEALTH_PATH)) {
                logger.trace("{}: {}", request.requestMethod(), request.pathInfo());
            }
        });

        // Setup all services
        setupRouters();

        // Default handler for not found
        Spark.notFound((req, res) -> {
            res.header("content-type", TransportRoute.APP_JSON);

            ObjectNode node = JsonUtils.wrap("error", TransportError.builder()
                    .code(404)
                    .type("EndpointNotFound")
                    .message("Requested " + req.pathInfo() + " endpoint is not registered.")
                    .asJson()
            );
            return JsonUtils.toString(node);
        });
        logger.info("Registered http 404 not found json response.");

        // Handle all expected exceptions
        handleException();
        logger.info("Started Transport Server on port: {}", port);
        this.started = true;
    }

    /**
     * Setup all the services by starting them
     */
    protected void setupRouters() {
        for (TransportService service : services) {
            service.start();
            logger.info("Started Service: {}", service.getClass().getSimpleName());
        }
    }

    /**
     * @see TransportException to understand how custom exception is created
     * @see UnknownException to understand how unknown exception is mapped
     * @see TransportError how error are formatted in response body
     */
    protected void handleException() {
        logger.info("Adding exception handling for StatusException.");
        Spark.exception(StatusException.class, (exception, request, response) -> {
            response.body(TransportTransformer.EMPTY);
            response.type(TransportRoute.APP_JSON);
            response.status(exception.getCode());
        });

        logger.info("Adding exception handling for ValidationException.");
        Spark.exception(ValidationException.class, (exception, request, response) -> {
            List<String> sources = exception.getSources();
            logger.debug("Validation exception thrown from sources: {}", sources, exception);
            try {
                logger.debug("Validated object:\n{}", JsonUtils.toString(exception.getObject()));
            } catch (JsonException e) {
                logger.debug("Attempt to parse validated object into JSON failed.");
            }
            handleException(new TransportContext(request, response), exception);
        });

        logger.info("Adding exception handling for TransportException.");
        Spark.exception(TransportException.class, (exception, request, response) -> {
            List<String> sources = exception.getSources();
            logger.warn("Transport exception thrown from sources: {}", sources, exception);
            handleException(new TransportContext(request, response), exception);
        });

        logger.info("Adding exception handling for TimeoutException.");
        Spark.exception(SocketTimeoutException.class, (exception, request, response) -> {
            handleException(new TransportContext(request, response), new TimeoutException(exception));
        });

        logger.info("Adding exception handling for all Exception.");
        Spark.exception(Exception.class, (exception, request, response) -> {
            try {
                if (mapException(exception)) return;
                // Unknown exception
                logger.warn("Unknown exception thrown", exception);
                handleException(new TransportContext(request, response), new UnknownException(exception));
            } catch (TransportException te) {
                // Mapped exception
                logger.warn("Transport exception thrown", exception);
                handleException(new TransportContext(request, response), te);
            }
        });
    }

    /**
     * If mapped, you can throw it
     *
     * @param exception additional exception to map
     * @return whether it is handled, meaning not to logg
     * @throws TransportException mapped exceptions
     */
    protected boolean mapException(Exception exception) throws TransportException {
        return false;
    }

    /**
     * @param context   with request and response
     * @param exception exception to write
     */
    protected void handleException(TransportContext context, TransportException exception) {
        TransportError error = exception.toError();

        if (!debug && error != null) {
            // If debug mode is disabled, stacktrace and source will be removed
            // Exception will still be logged
            error.setStacktrace(null);
            error.setSources(null);
        }

        ObjectNode body = JsonUtils.wrap("error", JsonUtils.valueToTree(error));

        Response response = context.response();
        response.body(JsonUtils.toString(body));
        response.type(TransportRoute.APP_JSON);
        response.status(exception.getCode());
    }

    /**
     * @return true if transport server has started
     */
    public boolean isStarted() {
        return started;
    }

    /**
     * @return whether it is debug mode
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * If debug mode is true, stacktrace and sources will be removed for TransportError
     * Otherwise,
     *
     * @param debug debug mode
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * @return port
     * @throws IllegalStateException when the server is not started
     */
    public int getPort() {
        return Spark.port();
    }

    /**
     * Using default /health/check as path
     *
     * @return TransportServer
     */
    public TransportServer withHealth() {
        return withHealth(DEFAULT_HEALTH_PATH);
    }

    /**
     * @param check function to reply with
     * @return TransportServer
     */
    public TransportServer withHealth(Function<TransportContext, String> check) {
        return withHealth(DEFAULT_HEALTH_PATH, check);
    }

    /**
     * @param runnable run a task, if task completes or fail, health will fail too
     * @return TransportServer
     */
    public TransportServer withHealth(Runnable runnable) {
        CompletableFuture<Void> future = CompletableFuture.runAsync(runnable);
        future.exceptionally(throwable -> {
            logger.error("HealthCheck Future Exception:", throwable);
            return null;
        });

        return withHealth(DEFAULT_HEALTH_PATH, cxt -> {
            if (future.isDone()) {
                throw new StatusException(400);
            }
            return "";
        });
    }

    /**
     * @param path for the health check
     * @return TransportServer
     */
    public TransportServer withHealth(String path) {
        return withHealth(path, cxt -> "");
    }

    /**
     * @param path  for the health check
     * @param check function to reply with
     * @return TransportServer
     */
    public TransportServer withHealth(String path, Function<TransportContext, String> check) {
        logger.info("Registered withHealth at path: {}", path);
        Spark.get(path, (req, res) -> check.apply(new TransportContext(req, res)));
        return this;
    }

    /**
     * Easy way to start a service in a server with the default port
     * <p>
     * Start transport server with default port in the config = http.port
     * Port number can also be injected in the env as: HTTP_PORT
     *
     * @param services to start
     * @return started TransportServer
     */
    public static TransportServer start(TransportService... services) {
        return start("", services);
    }

    /**
     * Easy way to start a service in a server with the prefix path and default port
     * <p>
     * Start transport server with default port in the config = http.port
     * Port number can also be injected in the env as: HTTP_PORT
     *
     * @param prefixPath path prefix, e.g. version number
     * @param services   to start
     * @return started TransportServer
     */
    public static TransportServer start(String prefixPath, TransportService... services) {
        TransportServer server = new TransportServer(services) {
            @Override
            protected void setupRouters() {
                Spark.path(prefixPath, super::setupRouters);
            }
        };
        server.start();
        return server;
    }


    /**
     * Easy way to start a service in a server with the prefix path and default port
     * <p>
     * Start transport server with default port in the config = http.port
     * Port number can also be injected in the env as: HTTP_PORT
     *
     * @param port       to start service in
     * @param prefixPath path prefix, e.g. version number
     * @param services   to start
     * @return started TransportServer
     */
    public static TransportServer start(int port, String prefixPath, TransportService... services) {
        TransportServer server = new TransportServer(services) {
            @Override
            protected void setupRouters() {
                Spark.path(prefixPath, super::setupRouters);
            }
        };
        server.start(port);
        return server;
    }
}
