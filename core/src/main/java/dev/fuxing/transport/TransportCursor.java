package dev.fuxing.transport;

import com.fasterxml.jackson.databind.JsonNode;
import dev.fuxing.err.ParamException;
import dev.fuxing.utils.JsonUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Cursor contains the all the information for the next list of objects.
 * <p>
 * All parameters are converted into string. When required, it will be converted back into the primitive type.
 * <p>
 * Created by: Fuxing
 * Date: 3/5/18
 * Time: 3:04 PM
 */
public class TransportCursor {
    public static final TransportCursor EMPTY = new TransportCursor(Map.of());
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();

    private Map<String, String> parameter;

    /**
     * @param parameter parameter
     */
    protected TransportCursor(@NotNull Map<String, String> parameter) {
        this.parameter = parameter;
    }

    private TransportCursor(Builder builder) {
        this(builder.parameters);
    }

    /**
     * @param key of value
     * @return string or {@code null} if not found
     */
    public String get(String key) {
        return get(key, null);
    }

    /**
     * @param key          of value
     * @param defaultValue default value if don't exist
     * @return string or {@code null} if not found
     */
    public String get(String key, String defaultValue) {
        return parameter.getOrDefault(key, defaultValue);
    }

    public <E extends Enum<E>> E getEnum(String key, Class<E> clazz, E defaultValue) {
        E num = EnumUtils.getEnum(clazz, get(key));
        if (num != null) return num;
        return defaultValue;
    }

    /**
     * @param key of value
     * @return found or defaultValue
     * @throws ParamException if cannot be parsed
     */
    @Nullable
    public Long getLong(String key) {
        return getLong(key, null);
    }

    /**
     * @param key          of value
     * @param defaultValue to return, Nullable
     * @return found or defaultValue
     * @throws ParamException if cannot be parsed
     */
    @Nullable
    public Long getLong(String key, @Nullable Long defaultValue) {
        String value = get(key);
        if (value == null) return defaultValue;

        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new ParamException(key);
        }
    }

    /**
     * @param key of value
     * @return found or defaultValue
     * @throws ParamException if cannot be parsed
     */
    @Nullable
    public Integer getInt(String key) {
        return getInt(key, null);
    }

    /**
     * @param key          of value
     * @param defaultValue to return, Nullable
     * @return found or defaultValue
     * @throws ParamException if cannot be parsed
     */
    @Nullable
    public Integer getInt(String key, @Nullable Integer defaultValue) {
        String value = get(key);
        if (value == null) return defaultValue;

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ParamException(key);
        }
    }

    /**
     * @param key of value
     * @return found or defaultValue
     * @throws ParamException if cannot be parsed
     */
    @Nullable
    public Double getDouble(String key) {
        return getDouble(key, null);
    }

    /**
     * @param key          of value
     * @param defaultValue to return, Nullable
     * @return found or defaultValue
     * @throws ParamException if cannot be parsed
     */
    @Nullable
    public Double getDouble(String key, @Nullable Double defaultValue) {
        String value = get(key);
        if (value == null) return defaultValue;

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new ParamException(key);
        }
    }

    public String toBase64() {
        return toBase64(this);
    }

    @Nullable
    public static String toBase64(TransportCursor cursor) {
        if (cursor == null) return null;

        String string = JsonUtils.toString(cursor.parameter);
        return ENCODER.encodeToString(string.getBytes());
    }

    @Nullable
    public static TransportCursor fromBase64(String value) {
        if (StringUtils.isBlank(value)) return null;

        byte[] decode = DECODER.decode(value);
        JsonNode node = JsonUtils.bytesToTree(decode);
        Map<String, String> parameters = JsonUtils.toMap(node, String.class, String.class);
        return new TransportCursor(parameters);
    }

    /**
     * @return TransportCursor builder
     */
    public Builder builder() {
        return new Builder();
    }

    /**
     * Builder for TransportCursor.
     */
    public static class Builder {
        private Map<String, String> parameters = new HashMap<>();

        /**
         * @param key   name
         * @param value value, all converted to string
         * @return Builder chaining
         */
        public Builder put(String key, Object value) {
            Objects.requireNonNull(value, "Value required.");
            this.parameters.put(key, value.toString());
            return this;
        }

        public TransportCursor build() {
            return new TransportCursor(this);
        }

        public String toBase64() {
            return build().toBase64();
        }
    }

    @Override
    public String toString() {
        return JsonUtils.toString(parameter);
    }
}
