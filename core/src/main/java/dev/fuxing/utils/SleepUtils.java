package dev.fuxing.utils;

import java.time.Duration;

/**
 * Created by: Fuxing
 * Date: 20/8/18
 * Time: 6:43 PM
 */
public final class SleepUtils {
    private SleepUtils() { /**/ }

    /**
     * @param duration duration to sleep
     */
    public static void sleep(Duration duration) {
        sleep(duration.toMillis());
    }

    /**
     * @param millis to sleep
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
