package dev.fuxing.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * This is a counter logging tool created to track count that are named.
 * <pre>
 * {@code
 * CounterLogger logger = new CounterLogger(this);
 * logger.increment("Key 1");
 * logger.increment("Key 2");
 * logger.print();
 * }
 * </pre>
 * <p>
 * Created by: Fuxing
 * Date: 21/8/18
 * Time: 11:11 PM
 */
public final class CounterLogger {
    private final Logger logger;
    private final Map<String, Long> counters = new HashMap<>();

    private long every;

    /**
     * @param object to get the class for logging
     */
    public CounterLogger(Object object) {
        this(LoggerFactory.getLogger(object.getClass()));
    }

    /**
     * @param object to get the class for logging
     * @param every  auto log every amount, 0 to disable
     */
    public CounterLogger(Object object, long every) {
        this(LoggerFactory.getLogger(object.getClass()), every);
    }

    /**
     * @param clazz to get the class for logging
     */
    public CounterLogger(Class clazz) {
        this(LoggerFactory.getLogger(clazz));
    }

    /**
     * @param clazz to get the class for logging
     * @param every auto log every amount, 0 to disable
     */
    public CounterLogger(Class clazz, long every) {
        this(LoggerFactory.getLogger(clazz), every);
    }

    /**
     * @param logger for logging counter
     */
    public CounterLogger(Logger logger) {
        this(logger, 1000);
    }

    /**
     * @param logger for logging counter
     * @param every  auto log every amount, 0 to disable
     */
    public CounterLogger(Logger logger, long every) {
        this.logger = logger;
        this.every = every;
    }

    /**
     * @param every auto log every amount
     */
    public void setEvery(long every) {
        this.every = every;
    }

    /**
     * @param name to increment by 1
     * @return incremented value
     */
    public long increment(String name) {
        return increment(name, 1);
    }

    /**
     * @param name to increment
     * @param by   amount
     * @return incremented value
     */
    public long increment(String name, long by) {
        if (by == 0) return get(name);

        return counters.compute(name, (s, count) -> {
            if (count == null) return by;
            count = count + by;
            if (count % every == 0) {
                logger.info("Counter: {}: {}", name, count);
            }
            return count;
        });
    }

    /**
     * Print all values in counter
     */
    public void print() {
        counters.forEach((name, count) -> {
            logger.info("Counter: {}: {}", name, count);
        });
    }

    /**
     * @param name to get
     * @return value or 0
     */
    public long get(String name) {
        return counters.getOrDefault(name, 0L);
    }

    /**
     * @param consumer to consume each key, value
     */
    public void forEach(BiConsumer<String, Long> consumer) {
        counters.forEach(consumer);
    }

    /**
     * Reset counter
     */
    public void reset() {
        counters.clear();
    }
}