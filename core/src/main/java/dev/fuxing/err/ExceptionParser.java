package dev.fuxing.err;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * For future improvement of auto registration of Exception, Reflect APIs should be used to auto detect and construct the object.
 * <p>
 * Created by: Fuxing
 * Date: 22/8/2017
 * Time: 11:39 AM
 */
public final class ExceptionParser {
    private static final Map<String, Consumer<TransportException>> FOUND_CONSUMERS = new HashMap<>();
    private static final Set<String> NOT_FOUND_LIST = new HashSet<>();

    // Root Level Exception is all Automatically Registered
    static {
        try {
            Class.forName(BadGatewayException.class.getName());
            Class.forName(BadRequestException.class.getName());
            Class.forName(StatusException.class.getName());
            Class.forName(ConflictException.class.getName());
            Class.forName(ForbiddenException.class.getName());
            Class.forName(JsonException.class.getName());
            Class.forName(NotFoundException.class.getName());
            Class.forName(RateLimitException.class.getName());
            Class.forName(OfflineException.class.getName());
            Class.forName(ParamException.class.getName());
            Class.forName(TimeoutException.class.getName());
            Class.forName(UnauthorizedException.class.getName());
            Class.forName(UnavailableException.class.getName());
            Class.forName(UnknownException.class.getName());
            Class.forName(ValidationException.class.getName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Register a new class type of resolve
     * Exception type must be same as class name for this to work
     *
     * @param errorClass exception class to register
     * @param function   to cast Exception type
     * @param <T>        Exception Class Type
     */
    public static <T extends TransportException> void register(Class<T> errorClass, Function<TransportException, T> function) {
        String type = TransportException.getType(errorClass);
        FOUND_CONSUMERS.put(type, e -> {
            throw function.apply(e);
        });
    }

    public static void parse(Exception e) {
        if (e.getClass() == TransportException.class) {
            parseTransport((TransportException) e);
        }

        for (Throwable throwable : ExceptionUtils.getThrowables(e)) {
            parseEach(e, throwable);
        }
    }

    /**
     * @param e transport exception to parse
     * @throws TransportException thrown
     */
    private static void parseTransport(TransportException e) throws TransportException {
        Consumer<TransportException> consumer = FOUND_CONSUMERS.get(e.getType());
        if (consumer == null && !NOT_FOUND_LIST.contains(e.getType())) {
            try {
                Class.forName(e.getClassName());
                consumer = FOUND_CONSUMERS.get(e.getType());
            } catch (ClassNotFoundException e1) {
                NOT_FOUND_LIST.add(e.getType());
            }
        }

        // throws the actual registered exception
        if (consumer != null) {
            consumer.accept(e);
        }
        throw e;
    }

    private static void parseEach(Exception e, Throwable throwable) {
        String name = throwable.getClass().getSimpleName();
        if (name.equals("HttpHostConnectException")) throw new OfflineException(e);
        if (name.equals("NoHttpResponseException")) throw new OfflineException(e);

        if (throwable instanceof SocketTimeoutException) {
            throw new TimeoutException(e);
        }
    }
}
