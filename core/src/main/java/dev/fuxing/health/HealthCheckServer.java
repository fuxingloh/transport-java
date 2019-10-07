package dev.fuxing.health;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import dev.fuxing.utils.SleepUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.Duration;

/**
 * Health check server. Make it easy to run a runnable in a function interface.
 * When the {@code Runnable} were to throw any exception. It will cause the health check to return a status 404.
 * <p>
 * Created by: Fuxing
 * Date: 3/7/18
 * Time: 12:11 AM
 */
public final class HealthCheckServer {
    private static final Logger logger = LoggerFactory.getLogger(HealthCheckServer.class);

    public static final int PORT = 7755;

    /**
     * @param port port to start on
     * @return HttpServer instance
     * @throws RuntimeException if port if occupied
     */
    private static HttpServer startHealthCheck(int port) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/health/check", HealthCheckServer::handleExchange);
            server.setExecutor(null);
            server.start();
            return server;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param exchange to send "{}", as Transport protocol return this
     */
    private static void handleExchange(HttpExchange exchange) {
        try {
            String response = "{}";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param runnableIntervals list of runnable interval
     */
    public static void startBlocking(RunnableInterval... runnableIntervals) {
        startBlocking((Runnable[]) runnableIntervals);
    }

    public interface RunnableInterval extends Runnable {
        Duration delay();

        default void before() {
            logger.info("Interval Before: Started.");
        }

        void start();

        default void after() {
            logger.info("Interval After: Started.");
        }

        @Override
        default void run() {
            while (!Thread.currentThread().isInterrupted()) {
                before();
                start();
                after();
                SleepUtils.sleep(delay());
            }
        }
    }

    public static void startBlocking(Runnable... runnableArray) {
        startBlocking(PORT, runnableArray);
    }

    public static void startBlocking(int port, Runnable... runnableArray) {
        HttpServer server = startHealthCheck(port);
        logger.info("Started HealthCheckServer on port: {}", port);

        HealthCheck.runBlocking(runnableArray);
        server.stop(0);
    }

    public static void startBlocking(Runnable runnable) {
        startBlocking(PORT, runnable);
    }

    public static void startBlocking(int port, Runnable runnable) {
        HttpServer server = startHealthCheck(port);
        logger.info("Started HealthCheckServer on port: {}", port);

        try {
            logger.info("Starting blocking task.");
            runnable.run();
        } finally {
            logger.warn("Task exited.");
            server.stop(0);
        }
    }
}
