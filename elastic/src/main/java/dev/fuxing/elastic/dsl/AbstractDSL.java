package dev.fuxing.elastic.dsl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.fuxing.utils.JsonUtils;

/**
 * Created by: Fuxing
 * Date: 2019-06-30
 * Time: 08:41
 */
public interface AbstractDSL {
    default ObjectNode createObjectNode() {
        return JsonUtils.createObjectNode();
    }
}
