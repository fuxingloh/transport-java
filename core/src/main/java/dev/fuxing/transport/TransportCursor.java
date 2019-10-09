package dev.fuxing.transport;

import dev.fuxing.err.ConflictException;
import dev.fuxing.err.ParamException;
import dev.fuxing.utils.JsonUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

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
     * @param defaultSize default size if not present
     * @param maxSize     max size if present
     * @return size value from query string
     */
    public int size(int defaultSize, int maxSize) {
        Integer size = getInt("size", defaultSize);
        assert size != null : "size will always be present";

        if (size <= 0) return defaultSize;
        if (size >= maxSize) return maxSize;
        return size;
    }

    /**
     * @param keys of value, to check
     * @return whether all the keys exist
     */
    public boolean has(String... keys) {
        if (keys.length == 0) return false;

        for (String key : keys) {
            if (!parameter.containsKey(key)) return false;
        }
        return true;
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

    public <E extends Enum<E>> E getEnum(String key, Class<E> clazz) {
        E num = EnumUtils.getEnum(clazz, get(key));
        if (num != null) return num;
        throw new ConflictException("enum not parsable.");
    }

    public <E extends Enum<E>> Set<E> getEnums(String key, Class<E> clazz) {
        String value = get(key);
        if (StringUtils.isBlank(value)) {
            return Set.of();
        }
        return Arrays.stream(value.split(", *"))
                .map(s -> JsonUtils.toEnum(s, clazz))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @Nullable
    public Date getDate(String key) {
        Long millis = getLong(key, null);
        if (millis == null) return null;

        return new Timestamp(millis);
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
    public static TransportCursor fromBase64(String base64) {
        return builder().base64(base64).build();
    }

    /**
     * @return TransportCursor builder
     */
    public static Builder builder() {
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

        /**
         * @param cursor to read and put all from
         * @return Builder chaining
         */
        public Builder putAll(TransportCursor cursor) {
            this.parameters.putAll(cursor.parameter);
            return this;
        }

        /**
         * @param parameters to put
         * @return Builder chaining
         */
        public Builder putAll(Map<String, String> parameters) {
            this.parameters.putAll(parameters);
            return this;
        }

        /**
         * @param base64 to convert from base 64 to parameters
         * @return Builder chaining
         */
        public Builder base64(String base64) {
            byte[] decoded = DECODER.decode(base64);
            parameters.putAll(JsonUtils.toMap(decoded, String.class, String.class));
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
