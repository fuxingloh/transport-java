package dev.fuxing.exception;

import com.typesafe.config.ConfigFactory;
import dev.fuxing.transport.TransportError;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * It's named transport exception to illustrate the idea that exception can survive the
 * transport process when moving between service and client.
 * <p>
 * Created by: Fuxing
 * Date: 16/6/2017
 * Time: 12:42 PM
 */
public class TransportException extends RuntimeException {
    public static final String DOMAIN;

    static {
        String domain = ConfigFactory.load().getString("exception.domain");
        DOMAIN = domain.replaceAll("/+$", "") + "/";
    }

    private final int code;
    private final String type;
    private final String message;
    private final String stacktrace;

    private List<String> sources = new ArrayList<>();

    /**
     * @param e for extending structure exception to create the Exception
     */
    protected TransportException(TransportException e) {
        this(e.code, e.type, e.message, e.stacktrace);
        this.sources = e.sources == null ? Collections.emptyList() : e.sources;
    }

    /**
     * @param code    error code
     * @param clazz   class to register to
     * @param message error message in detail, safe for user
     */
    public TransportException(int code, Class<? extends TransportException> clazz, String message) {
        this(code, clazz, message, null);
    }

    /**
     * @param code      error code
     * @param clazz     class to register to
     * @param message   error message in detail, safe for user
     * @param throwable error stacktrace
     */
    public TransportException(int code, Class<? extends TransportException> clazz, String message, Throwable throwable) {
        super(message, throwable);
        this.code = code;
        this.type = getType(clazz);
        this.message = message;

        if (throwable != null) {
            this.stacktrace = ExceptionUtils.getStackTrace(throwable);
        } else {
            this.stacktrace = null;
        }
    }

    protected TransportException(int code, String type, String message, String stacktrace) {
        super(message);
        this.code = code;
        this.type = type;
        this.message = message;
        this.stacktrace = stacktrace;
    }

    /**
     * @return status code, following HTTP status description
     */
    public int getCode() {
        return code;
    }

    /**
     * @return uniquely identifiable exception type
     */
    public String getType() {
        return type;
    }

    /**
     * @return class name, after the namespace
     */
    public String getClassName() {
        return StringUtils.removeStart(type, DOMAIN);
    }

    /**
     * @return sources where the exception originated form
     */
    public List<String> getSources() {
        return sources;
    }

    /**
     * Convert Structured exception to transport error
     * To be safe, only use this method
     *
     * @return TransportError
     */
    public TransportError toError() {
        TransportError error = new TransportError();
        error.setCode(code);
        error.setType(type);
        error.setMessage(message);
        error.setStacktrace(stacktrace);
        error.setSources(sources.isEmpty() ? null : sources);
        return error;
    }

    /**
     * @param types types of errors to check if exist
     * @return if error is any of the given type
     */
    public boolean isAnyType(String... types) {
        for (String t : types) {
            if (t.equals(type)) return true;
        }
        return false;
    }

    /**
     * @param error  block
     * @param source add new source
     * @return Structured exception
     */
    @Nullable
    public static TransportException fromError(@Nullable TransportError error, @Nullable String source) {
        if (error == null) return null;

        TransportException exception = new TransportException(
                error.getCode(), error.getType(), error.getMessage(), error.getStacktrace()
        );

        // Pass the source down
        if (error.getSources() != null) {
            exception.sources.addAll(error.getSources());
        }
        if (source != null) {
            exception.sources.add(source);
        }
        return exception;
    }

    /**
     * @param clazz of the exception
     * @param <T>   exception type
     * @return type of the error with domain and it's an url destination
     */
    public static <T extends TransportException> String getType(Class<T> clazz) {
        if (clazz.getPackageName().equals("dev.fuxing.exception")) {
            return DOMAIN + clazz.getSimpleName();
        }

        return DOMAIN + clazz.getName();
    }
}
