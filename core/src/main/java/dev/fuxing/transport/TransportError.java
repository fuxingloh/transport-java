package dev.fuxing.transport;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import dev.fuxing.err.ErrorURL;

/**
 * Created by: Fuxing
 * Date: 2019-04-06
 * Time: 20:15
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public final class TransportError {
    private Integer code;
    private String url;
    private String message;

    public TransportError() {
    }

    private TransportError(Builder builder) {
        this.code = builder.code;
        this.url = builder.url;
        this.message = builder.message;
    }

    /**
     * @return code, following status code
     */
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
        private String url;
        private String message;

        public Builder exception(ErrorURL errorURL) {
            return code(errorURL.getCode())
                    .url(errorURL.getUrl())
                    .message(errorURL.getMessage());
        }

        public Builder code(int code) {
            this.code = code;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public TransportError build() {
            return new TransportError(this);
        }
    }
}
