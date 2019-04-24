package dev.fuxing.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import dev.fuxing.exception.TransportException;
import dev.fuxing.utils.JsonUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.List;

/**
 * Created by: Fuxing
 * Date: 2019-04-06
 * Time: 20:15
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public final class TransportError {
    private int code;
    private String type;
    private String message;

    private String stacktrace;
    private List<String> sources;

    public TransportError() {
    }

    private TransportError(Builder builder) {
        this.code = builder.code;
        this.type = builder.type;
        this.message = builder.message;

        this.stacktrace = builder.stacktrace;
        this.sources = builder.sources;
    }

    /**
     * @return code, following status code
     */
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    /**
     * @return error type, aka ClassType
     */
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return human readable message
     */
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return full stacktrace of error, debugging only
     */
    public String getStacktrace() {
        return stacktrace;
    }

    public void setStacktrace(String stacktrace) {
        this.stacktrace = stacktrace;
    }

    /**
     * Last in list is the original source
     *
     * @return url source of error, debugging only
     */
    public List<String> getSources() {
        return sources;
    }

    public void setSources(List<String> sources) {
        this.sources = sources;
    }

    /**
     * @return A chain-able fluent error builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for the TransportError
     * <p>
     * Created by: Fuxing
     * Date: 2019-04-06
     * Time: 20:17
     */
    public static class Builder {
        private int code;
        private String type;
        private String message;

        private String stacktrace;
        private List<String> sources;

        public Builder exception(TransportException exception) {
            return code(exception.getCode())
                    .type(exception.getType())
                    .message(exception.getMessage())
                    .stacktrace(exception)
                    .sources(exception.getSources());
        }

        public Builder code(int code) {
            this.code = code;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder stacktrace(Throwable stacktrace) {
            this.stacktrace = ExceptionUtils.getStackTrace(stacktrace);
            return this;
        }

        public Builder sources(List<String> sources) {
            this.sources = sources;
            return this;
        }

        public TransportError build() {
            return new TransportError(this);
        }

        public JsonNode asJson() {
            return JsonUtils.valueToTree(build());
        }
    }
}
