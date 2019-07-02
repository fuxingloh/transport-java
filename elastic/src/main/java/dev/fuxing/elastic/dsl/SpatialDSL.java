package dev.fuxing.elastic.dsl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by: Fuxing
 * Date: 2019-06-30
 * Time: 08:44
 */
public interface SpatialDSL extends AbstractDSL {

    /**
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-geo-polygon-query.html
     *
     * @param name      name of term
     * @param pointList list of points to form a polygon
     * @return JsonNode = { "geo_polygon": { "location.latLng": { "points": ["-1,2", "-5,33" ...]}}}
     */
    default JsonNode filterPolygon(String name, List<String> pointList) {
        ObjectNode filter = createObjectNode();
        ArrayNode points = filter.putObject("geo_polygon")
                .putObject(name)
                .putArray("points");
        pointList.forEach(points::add);
        return filter;
    }

    /**
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-geo-distance-query.html
     *
     * @param name   name of term
     * @param latLng latLng center
     * @param metres metres in distance
     * @return JsonNode = { "geo_distance": { "distance": "1km", "location.latLng": "-1,2"}}
     */
    default JsonNode filterDistance(String name, String latLng, double metres) {
        ObjectNode filter = createObjectNode();
        filter.putObject("geo_distance")
                .put("distance", metres + "m")
                .put(name, latLng);
        return filter;
    }

    /**
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-geo-shape-query.html
     *
     * @param name   name of term
     * @param latLng point within
     * @return {"geo_shape": { "location": {
     * "shape": { "type": "point", "coordinates" : [13.0, 53.0]},
     * "relation": "within"
     * }}}
     */
    default JsonNode filterIntersectsPoint(String name, String latLng) {
        ObjectNode filter = createObjectNode();
        String[] split = latLng.split(",");

        filter.putObject("geo_shape")
                .putObject(name)
                .put("relation", "intersects")
                .putObject("shape")
                .put("type", "point")
                .putArray("coordinates")
                .add(Double.parseDouble(split[1]))
                .add(Double.parseDouble(split[0]));
        return filter;
    }

    static String[] getBoundingBox(double lat, double lng, double latOffsetKm, double lngOffsetKm) {
        final double latOffset = toRad(latOffsetKm);
        final double lngOffset = toRad(lngOffsetKm);
        return new String[]{
                (lat + latOffset) + "," + (lng - lngOffset), // Top Lat, Lng
                (lat - latOffset) + "," + (lng + lngOffset), // Bot Lat, Lng
        };
    }

    static <T> double[] getCentroid(List<T> list, Function<T, String> mapper) {
        List<String> points = list.stream()
                .map(mapper)
                .collect(Collectors.toList());
        return getCentroid(points);
    }

    /**
     * @param points to find centroid
     * @return centroid point
     */
    static double[] getCentroid(List<String> points) {
        double centroidLat = 0, centroidLng = 0;

        for (String point : points) {
            double[] latLng = parse(point);
            centroidLat += latLng[0];
            centroidLng += latLng[1];
        }

        return new double[]{
                centroidLat / points.size(),
                centroidLng / points.size()
        };
    }

    static double toRad(double radiusInKm) {
        return (1 / 110.54) * radiusInKm;
    }

    static double[] parse(String latLng) {
        Objects.requireNonNull(latLng);

        try {
            String[] split = latLng.split(",");
            double lat = Double.parseDouble(split[0].trim());
            double lng = Double.parseDouble(split[1].trim());
            return new double[]{lat, lng};
        } catch (NullPointerException | IndexOutOfBoundsException | NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }
}
