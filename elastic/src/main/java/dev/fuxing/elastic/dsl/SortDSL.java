package dev.fuxing.elastic.dsl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Objects;

/**
 * Created by: Fuxing
 * Date: 2019-06-30
 * Time: 08:39
 */
public interface SortDSL extends AbstractDSL {

    /**
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-sort.html
     *
     * @param field  to sort by
     * @param latLng center
     * @return { "location.latLng" : "lat,lng", "order" : "asc", "unit" : "m", "mode" : "min", "distance_type" : "plane" }
     */
    default JsonNode sortDistance(String field, String latLng) {
        Objects.requireNonNull(latLng);

        ObjectNode geoDistance = createObjectNode()
                .put(field, latLng)
                .put("order", "asc")
                .put("unit", "m")
                .put("mode", "min")
                .put("distance_type", "plane");

        ObjectNode sort = createObjectNode();
        sort.set("_geo_distance", geoDistance);
        return sort;
    }

    /**
     * @param field field
     * @param by    direction
     * @return { "field": "by" }
     */
    default JsonNode sortField(String field, String by) {
        ObjectNode sort = createObjectNode();
        sort.put(field, by);
        return sort;
    }
}
