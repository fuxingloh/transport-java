package dev.fuxing.elastic.dsl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.fuxing.utils.JsonUtils;

import javax.annotation.Nullable;

/**
 * Created by: Fuxing
 * Date: 2019-06-30
 * Time: 08:43
 */
public interface SuggestDSL extends AbstractDSL{
    default JsonNode makeCompletion(String field, @Nullable ObjectNode contexts, int size) {
        ObjectNode completion = createObjectNode();
        completion.put("field", field);
        completion.put("fuzzy", true);
        completion.put("size", size);

        if (contexts != null) {
            completion.set("contexts", contexts);
        }

        return completion;
    }

    default JsonNode makeContextLatLng(String latLng, int precision, double boost) {
        double[] ll = SpatialDSL.parse(latLng);

        ArrayNode latLngArray = JsonUtils.createArrayNode();
        latLngArray.addObject()
                .put("precision", precision)
                .put("boost", boost)
                .putObject("context")
                .put("lat", ll[0])
                .put("lon", ll[1]);
        return latLngArray;
    }
}
