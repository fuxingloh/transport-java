package dev.fuxing.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created by: Fuxing
 * Date: 2019-06-06
 * Time: 09:13
 */
class JsonUtilsTest {

    @Test
    void mergeObject() {
        TestObject object = new TestObject();
        object.a = "Filled A";

        ObjectNode patch;
        TestObject merged;

        patch = JsonUtils.createObjectNode(node -> {
            node.put("b", "Filled B");
        });
        merged = JsonUtils.merge(object, patch);
        Assertions.assertEquals(merged.a, "Filled A");
        Assertions.assertEquals(merged.b, "Filled B");

        patch = JsonUtils.createObjectNode(node -> {
            node.putNull("a");
            node.put("b", "Filled BB");
        });
        merged = JsonUtils.merge(object, patch);
        Assertions.assertNull(merged.a);
        Assertions.assertEquals(merged.b, "Filled BB");
    }

    @Test
    void mergeNode() {
        ObjectNode object = JsonUtils.createObjectNode(node -> {
            node.put("a", "a");
        });
        ObjectNode patch = JsonUtils.createObjectNode(node -> {
            node.put("a", "b");
            node.put("b", "b");
        });

        JsonNode merged = JsonUtils.merge(object, patch);
        Assertions.assertEquals(merged.path("a").asText(), "b");
        Assertions.assertEquals(merged.path("b").asText(), "b");
    }
}

class TestObject {
    public String a;
    public String b;

    @Override
    public String toString() {
        return JsonUtils.toString(this);
    }
}