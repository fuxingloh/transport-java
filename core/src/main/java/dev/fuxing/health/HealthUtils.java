package dev.fuxing.health;

import dev.fuxing.utils.SleepUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.time.Duration;

/**
 * Health utils for coordinating multiple services and wait for them to be up.
 * <p>
 * Created By: Fuxing Loh
 * Date: 17/4/2017
 * Time: 10:55 PM
 */
public final class HealthUtils {
    private static final Logger logger = LoggerFactory.getLogger(HealthUtils.class);

    public static void statusOk(String url, Duration timeout) {
        logger.info("Waiting for {} with timeout duration of {}", url, timeout);
        try {
            code(url, 200, (int) timeout.toMillis());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Wait for host until given timeout
     *
     * @param host    hostname to wait for
     * @param port    hostname to wait for
     * @param timeout timeout in duration
     */
    public static void host(String host, int port, Duration timeout) {
        logger.info("Waiting for {}:{} with timeout duration of {}", host, port, timeout);
        try {
            if (!ping(host, port, (int) timeout.toMillis())) {
                throw new RuntimeException(host + ":" + port + " is unreachable.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Wait for host until given timeout
     * Port is required in the url
     *
     * @param url     to wait for
     * @param timeout timeout in duration
     */
    public static void host(String url, Duration timeout) {
        logger.info("Waiting for {} with timeout duration of {}", url, timeout);
        try {
            URI uri = new URI(url);
            if (!ping(uri.getHost(), resolvePort(uri), (int) timeout.toMillis())) {
                throw new RuntimeException(url + " is unreachable.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void noException(Runnable runnable, Duration timeout) {
        logger.info("Waiting Runnable with timeout duration of {}", timeout);

        long startMillis = System.currentTimeMillis();
        while (System.currentTimeMillis() < startMillis + timeout.toMillis()) {
            try {
                runnable.run();
                return;

            } catch (Exception ignored) {
                SleepUtils.sleep(1000);
            }
        }
        throw new RuntimeException("HealthUtils.noException failed");
    }

    private static int resolvePort(URI uri) {
        if (uri.getPort() != -1) return uri.getPort();
        if (uri.getScheme().startsWith("https")) return 443;
        if (uri.getScheme().startsWith("http")) return 80;
        return -1;
    }

    /**
     * Keep trying with allowed duration
     */
    private static boolean ping(String host, int port, int timeout) throws InterruptedException {
        long startMillis = System.currentTimeMillis();
        while (System.currentTimeMillis() < startMillis + timeout) {
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(host, port), timeout);
                return true;
            } catch (IOException ignored) {
            }
            Thread.sleep(1000);
        }
        return false;
    }

    private static boolean code(String urlSting, int code, int timeout) {
        long startMillis = System.currentTimeMillis();
        while (System.currentTimeMillis() < startMillis + timeout) {
            try {
                URL url = new URL(urlSting);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(timeout);
                connection.setConnectTimeout(timeout);
                connection.setRequestMethod("GET");
                connection.connect();

                if (connection.getResponseCode() == code) return true;
                Thread.sleep(1000);
            } catch (Exception ignored) {

            }
        }
        return false;
    }

    public interface Runnable {
        void run() throws Exception;
    }
}
