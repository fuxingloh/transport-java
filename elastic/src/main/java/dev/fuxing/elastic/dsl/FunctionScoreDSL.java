package dev.fuxing.elastic.dsl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.annotation.Nullable;

/**
 * Created by: Fuxing
 * Date: 2019-06-30
 * Time: 08:53
 */
public interface FunctionScoreDSL extends AbstractDSL {
    /**
     * @param must   function base score
     * @param latLng optional distance decaying with default decay 2.5km scale
     * @return function_score in JsonNode
     */
    default JsonNode withFunctionScoreMust(JsonNode must, @Nullable String latLng) {
        return withFunctionScoreMust(must, latLng, "2.5km");
    }

    /**
     * @param must   function base score
     * @param latLng latLng for distance decaying
     * @param scale  scale of decay, depending on use cases
     * @return function_score in JsonNode
     */
    default JsonNode withFunctionScoreMust(JsonNode must, @Nullable String latLng, String scale) {
        ObjectNode root = createObjectNode();
        ObjectNode function = root.putObject("function_score");
        function.put("score_mode", "multiply");
        function.set("query", must);

        ArrayNode functions = function.putArray("functions");
        functions.addObject()
                .putObject("gauss")
                .putObject("taste.importance")
                .put("scale", "0.1")
                .put("origin", "1");

        if (latLng != null) {
            functions.addObject()
                    .putObject("gauss")
                    .putObject("location.latLng")
                    .put("scale", scale)
                    .put("origin", latLng);
        }
        return root;
    }
}
