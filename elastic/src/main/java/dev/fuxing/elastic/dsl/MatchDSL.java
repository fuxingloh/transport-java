package dev.fuxing.elastic.dsl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Created by: Fuxing
 * Date: 2019-06-30
 * Time: 08:48
 */
public interface MatchDSL extends AbstractDSL {
    /**
     * Search with text on name
     *
     * @return JsonNode must filter
     */
    default JsonNode mustMatchAll() {
        ObjectNode root = createObjectNode();
        root.putObject("match_all");
        return root;
    }

    default JsonNode multiMatch(String query, String field, String... fields) {
        ObjectNode root = createObjectNode();
        ObjectNode match = root.putObject("multi_match");

        match.put("query", query);
        match.put("type", "phrase_prefix");

        ArrayNode fieldsNode = match.putArray("fields");
        fieldsNode.add(field);
        for (String each : fields) fieldsNode.add(each);

        return root;
    }

    /**
     * @param name  to match
     * @param value to match
     * @return JsonNode match filter
     */
    default JsonNode match(String name, String value) {
        ObjectNode root = createObjectNode();
        root.putObject("match").put(name, value);
        return root;
    }

    default JsonNode matchFuzzy(String name, String text) {
        ObjectNode filter = createObjectNode();
        filter.putObject("match")
                .putObject(name)
                .put("query", text)
                .put("fuzziness", "AUTO")
                .put("prefix_length", 2);
        return filter;
    }
}
