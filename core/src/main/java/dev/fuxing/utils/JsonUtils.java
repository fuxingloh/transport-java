package dev.fuxing.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import dev.fuxing.err.JsonException;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * JsonUtils is basically a singleton ObjectMapper that wraps all JSON Error into {@code JsonException}.
 * There are also a bunch of helper method for parsing collections.
 * <p>
 * Created By: Fuxing Loh
 * Date: 16/6/2017
 * Time: 3:48 PM
 */
public final class JsonUtils {
    private JsonUtils() { /**/ }

    /**
     * Singleton ObjectMapper for all transport related libraries
     */
    public static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * It's the same as
     * <pre>
     *     JsonNode node;
     *     Entity entity;
     *
     *     // Raw
     *     if (node.has("path")) {
     *          entity.getCount().setTotal(node.path("path").path("total").asLong());
     *     }
     *
     *     // Using JsonUtils
     *     JsonUtils.hasPath(node, "path", pathNode -&#x3E; {
     *         entity.getCount().setTotal(pathNode.path("total").asLong());
     *     });
     * </pre>
     *
     * @param node     to check
     * @param name     path name
     * @param consumer to run if node path exist
     */
    public static void hasPath(JsonNode node, String name, Consumer<JsonNode> consumer) {
        if (node.has(name)) {
            consumer.accept(node.path(name));
        }
    }

    /**
     * Merge node changes into Object
     *
     * @param object original object
     * @param patch  node to merge into object
     * @param <T>    Type of Object
     * @return Merged Object
     */
    public static <T> T merge(T object, JsonNode patch) {
        try {
            return objectMapper.readerForUpdating(object).readValue(patch);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    /**
     * Merge node changes into Object
     *
     * @param object node for original object to be patched
     * @param patch  node to merge
     * @return Merged Node
     */
    public static JsonNode merge(JsonNode object, JsonNode patch) {
        try {
            return objectMapper.readerForUpdating(object).readValue(patch);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    /**
     * Exactly the same as
     * <pre>
     *     ObjectNode root = createObjectNode();
     *     root.set(name, node);
     *     return root;
     * </pre>
     *
     * @param name to name to node to be wrapped
     * @param node to wrap
     * @return wrapped node
     */
    public static ObjectNode wrap(String name, JsonNode node) {
        ObjectNode root = createObjectNode();
        root.set(name, node);
        return root;
    }

    /**
     * @param rootConsumer to manipulate the created ObjectNode
     * @return created ObjectNode
     */
    public static ObjectNode createObjectNode(Consumer<ObjectNode> rootConsumer) {
        ObjectNode objectNode = createObjectNode();
        rootConsumer.accept(objectNode);
        return objectNode;
    }

    /**
     * Exactly the same as
     * <pre>
     *     ObjectMapper mapper = new ObjectMapper();
     *     ObjectNode objectNode = mapper.createObjectNode();
     * </pre>
     *
     * @return newly created ObjectNode
     */
    public static ObjectNode createObjectNode() {
        return objectMapper.createObjectNode();
    }

    /**
     * Exactly the same as
     * <pre>
     *     ObjectMapper mapper = new ObjectMapper();
     *     ArrayNode arrayNode = mapper.createArrayNode();
     * </pre>
     *
     * @return newly created ArrayNode
     */
    public static ArrayNode createArrayNode() {
        return objectMapper.createArrayNode();
    }

    /**
     * Generic types type information is ignored when parsed as Java don't store them.
     * https://github.com/FasterXML/jackson-databind/issues/1816
     *
     * @param object JsonNode or POJO
     * @return JSON String
     */
    public static String toString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new JsonException(e);
        }
    }

    /**
     * Read json string to JsonNode
     *
     * @param json JSON String
     * @return JsonNode
     */
    public static JsonNode jsonToTree(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    /**
     * Read json string to JsonNode
     *
     * @param bytes JSON String
     * @return JsonNode
     */
    public static JsonNode bytesToTree(byte[] bytes) {
        try {
            return objectMapper.readTree(bytes);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    public static <T> T bytesToObject(byte[] bytes, Class<T> clazz) {
        try {
            return objectMapper.readValue(bytes, clazz);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    /**
     * @param object POJO into JsonNode
     * @param <T>    Node Type
     * @return JsonNode
     */
    public static <T extends JsonNode> T valueToTree(Object object) {
        return objectMapper.valueToTree(object);
    }

    /**
     * Basically this method convert JsonNode to Object then back to JsonNode
     *
     * @param node  to validate
     * @param clazz to validate against
     * @return Node/fields that are validated
     */
    public static JsonNode validate(JsonNode node, Class<?> clazz) {
        return valueToTree(toObject(node, clazz));
    }

    /**
     * @param object to copy
     * @param clazz  object class type
     * @param <T>    class type
     * @return Copied Object
     */
    public static <T> T deepCopy(T object, Class<T> clazz) {
        return toObject(valueToTree(object), clazz);
    }

    public static <T> T toObject(JsonNode node, Class<T> clazz) {
        if (node == null) return null;
        try {
            return objectMapper.treeToValue(node, clazz);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    @Nullable
    public static <T> T toEnum(String value, Class<T> clazz) {
        if (StringUtils.isBlank(value)) return null;
        JsonNode node = JsonUtils.valueToTree(value);
        return toObject(node, clazz);
    }

    @SuppressWarnings("Duplicates")
    public static <T> T toObject(String value, Class<T> clazz) {
        if (value == null) return null;
        try {
            return objectMapper.readValue(value, clazz);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    @SuppressWarnings("Duplicates")
    public static <T> T toObject(byte[] value, Class<T> clazz) {
        if (value == null) return null;
        try {
            return objectMapper.readValue(value, clazz);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    public static <T extends R, R> List<R> toList(JsonNode nodes, Class<T> clazz) {
        try {
            CollectionType type = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
            return objectMapper.convertValue(nodes, type);
        } catch (IllegalArgumentException e) {
            throw new JsonException(e);
        }
    }

    public static <T extends R, R> List<R> toList(String json, Class<T> clazz) {
        try {
            CollectionType type = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
            return objectMapper.readValue(json, type);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    public static <T extends R, R> List<R> toList(JsonNode nodes, Function<JsonNode, T> mapper) {
        List<R> list = new ArrayList<>();
        for (JsonNode node : nodes) {
            list.add(mapper.apply(node));
        }
        return list;
    }

    public static <T extends R, R> Set<R> toSet(JsonNode nodes, Class<T> clazz) {
        try {
            CollectionType type = objectMapper.getTypeFactory().constructCollectionType(Set.class, clazz);
            return objectMapper.convertValue(nodes, type);
        } catch (IllegalArgumentException e) {
            throw new JsonException(e);
        }
    }

    public static <T extends R, R> Set<R> toSet(String json, Class<T> clazz) {
        try {
            CollectionType type = objectMapper.getTypeFactory().constructCollectionType(Set.class, clazz);
            return objectMapper.readValue(json, type);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    public static <K, V> Map<K, V> toMap(JsonNode nodes, Class<K> keyClass, Class<V> valueClass) {
        try {
            MapType type = objectMapper.getTypeFactory().constructMapType(HashMap.class, keyClass, valueClass);
            return objectMapper.convertValue(nodes, type);
        } catch (IllegalArgumentException e) {
            throw new JsonException(e);
        }
    }

    public static <K, V> Map<K, V> toMap(String json, Class<K> keyClass, Class<V> valueClass) {
        try {
            MapType type = objectMapper.getTypeFactory().constructMapType(HashMap.class, keyClass, valueClass);
            return objectMapper.readValue(json, type);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }
}
