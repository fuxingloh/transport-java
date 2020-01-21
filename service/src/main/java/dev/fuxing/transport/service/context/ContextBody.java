package dev.fuxing.transport.service.context;

import com.fasterxml.jackson.databind.JsonNode;
import dev.fuxing.err.BadRequestException;
import dev.fuxing.err.JsonException;
import dev.fuxing.utils.JsonUtils;

import java.util.List;
import java.util.function.Function;

/**
 * Created by: Fuxing
 * Date: 2019-04-24
 * Time: 19:21
 */
public interface ContextBody extends Context {
    /**
     * @return request body as JsonNode
     * @throws JsonException json exception
     */
    default JsonNode bodyAsJson() {
        return JsonUtils.bytesToTree(request().bodyAsBytes());
    }

    /**
     * @param nonNull validate if body is non null
     * @return request body as JsonNode
     * @throws JsonException json exception
     */
    default JsonNode bodyAsJson(boolean nonNull) {
        JsonNode json = bodyAsJson();
        if (nonNull && json == null) throw new BadRequestException();
        return json;
    }

    /**
     * This method allows functional chaining
     *
     * @param mapper to convert json context body into object
     * @param <T>    converted Type
     * @return converted Object
     */
    default <T> T bodyAsMapped(Function<ContextBody, T> mapper) {
        return mapper.apply(this);
    }

    /**
     * @param clazz of body
     * @param <T>   body class type
     * @return request body as json object
     */
    default <T> T bodyAsObject(Class<T> clazz) {
        return JsonUtils.bytesToObject(request().bodyAsBytes(), clazz);
    }

    /**
     * @param clazz clazz
     * @param <T>   Type
     * @return List as type
     */
    default <T> List<T> bodyAsList(Class<T> clazz) {
        return JsonUtils.toList(bodyAsJson(), clazz);
    }

    /**
     * @param mapper json mapper
     * @param <T>    Type
     * @return List as type
     */
    default <T> List<T> bodyAsList(Function<JsonNode, T> mapper) {
        return JsonUtils.toList(bodyAsJson(), mapper);
    }
}
