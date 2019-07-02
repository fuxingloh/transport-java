package dev.fuxing.elastic.dsl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Collection;

/**
 * Created by: Fuxing
 * Date: 2019-06-30
 * Time: 08:50
 */
public interface TermDSL extends AbstractDSL {
    /**
     * @param name name of term
     * @param text text of term
     * @return JsonNode =  { "term" : { "name" : "text" } }
     */
    default JsonNode filterTerm(String name, String text) {
        ObjectNode filter = createObjectNode();
        filter.putObject("term").put(name, text);
        return filter;
    }

    default JsonNode filterTerm(String name, boolean value) {
        ObjectNode filter = createObjectNode();
        filter.putObject("term").put(name, value);
        return filter;
    }

    /**
     * @param name  name of terms
     * @param texts texts of terms
     * @return JsonNode =  { "terms" : { "name" : "text" } }
     */
    default JsonNode filterTerms(String name, Collection<String> texts) {
        ObjectNode filter = createObjectNode();
        ArrayNode terms = filter.putObject("terms").putArray(name);
        for (String text : texts) {
            terms.add(text);
        }
        return filter;
    }

    /**
     * E.g. createdDate &gt; 1000 is "createdDate", "gt", 1000
     *
     * @param name     name of field to filter
     * @param operator operator in english form, e.g. gte, lt
     * @param value    value to compare again
     * @return filter range json
     */
    default JsonNode filterRange(String name, String operator, long value) {
        ObjectNode filter = createObjectNode();
        filter.putObject("range")
                .putObject(name)
                .put(operator, value);
        return filter;
    }

    /**
     * E.g. createdDate &gt; 1000 is "createdDate", "gt", 1000
     *
     * @param name     name of field to filter
     * @param operator operator in english form, e.g. gte, lt
     * @param value    value to compare again
     * @return filter range json
     */
    default JsonNode filterRange(String name, String operator, double value) {
        ObjectNode filter = createObjectNode();
        filter.putObject("range")
                .putObject(name)
                .put(operator, value);
        return filter;
    }
}
