package dev.fuxing.utils;

import java.time.Duration;
import java.util.Date;

/**
 * Created by: Fuxing
 * Date: 13/8/2017
 * Time: 11:44 PM
 */
public final class CompareUtils {
    private CompareUtils() { /**/ }

    /**
     * @param before  before date
     * @param elapsed elapsed duration
     * @return if time now is within before + elapsed
     * @see CompareUtils#within(long, Duration, long)
     */
    public static boolean within(Date before, Duration elapsed) {
        return within(before.getTime(), elapsed, System.currentTimeMillis());
    }

    /**
     * @param beforeMillis before millis
     * @param elapsed      elapsed duration
     * @return if time now is within before + elapsed
     * @see CompareUtils#within(long, Duration, long)
     */
    public static boolean within(long beforeMillis, Duration elapsed) {
        return within(beforeMillis, elapsed, System.currentTimeMillis());
    }

    /**
     * @param before  before date
     * @param elapsed elapsed duration
     * @param after   after date
     * @return if after is within before + elapsed
     * @see CompareUtils#within(long, Duration, long)
     */
    public static boolean within(Date before, Duration elapsed, Date after) {
        return within(before.getTime(), elapsed, after.getTime());
    }

    /**
     * Check that after is within before and elapsed
     * Before = 100m, elapsed = 20m, is after within 100 - 120
     *
     * @param beforeMillis before millis
     * @param elapsed      elapsed duration
     * @param afterMillis  after millis
     * @return if after is within before + elapsed
     */
    public static boolean within(long beforeMillis, Duration elapsed, long afterMillis) {
        return beforeMillis + elapsed.toMillis() > afterMillis;
    }

    /**
     * @param before  before date
     * @param elapsed elapsed duration
     * @param after   after date
     * @return if elapsed time has passed since before millis
     * @see CompareUtils#after(long, Duration, long)
     */
    public static boolean after(Date before, Duration elapsed, Date after) {
        return after(before.getTime(), elapsed, after.getTime());
    }

    /**
     * @param before  before date
     * @param elapsed elapsed duration
     * @return if elapsed time has passed since before millis
     * @see CompareUtils#after(long, Duration, long)
     */
    public static boolean after(Date before, Duration elapsed) {
        return after(before.getTime(), elapsed, System.currentTimeMillis());
    }

    /**
     * @param beforeMillis before millis
     * @param elapsed      elapsed duration
     * @return if elapsed time has passed since before millis
     * @see CompareUtils#after(long, Duration, long)
     */
    public static boolean after(long beforeMillis, Duration elapsed) {
        return after(beforeMillis, elapsed, System.currentTimeMillis());
    }

    /**
     * Check if the elapsed time has passed afterMillis since the before millis
     * beforeMillis + elapsed &#x3C; afterMillis
     * Basically will return true if elapsed time has passed since the before millis
     *
     * @param beforeMillis before millis
     * @param elapsed      elapsed duration
     * @param afterMillis  after millis
     * @return if elapsed time has passed since before millis
     */
    public static boolean after(long beforeMillis, Duration elapsed, long afterMillis) {
        return beforeMillis + elapsed.toMillis() < afterMillis;
    }
}
