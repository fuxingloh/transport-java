package dev.fuxing.elastic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.fuxing.elastic.dsl.ElasticDSL;
import dev.fuxing.utils.JsonUtils;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by: Fuxing
 * Date: 2019-04-15
 * Time: 20:51
 * Project: v22-transport
 */
public class ElasticQuery {
    private static final ElasticDSL DSL = new ElasticDSL();

    private final int from;
    private final int size;
    private final JsonNode query;
    private final JsonNode sort;

    protected ElasticQuery(int from, int size, JsonNode query, JsonNode sort) {
        this.from = from;
        this.size = size;
        this.query = query;
        this.sort = sort;
    }

    public int getFrom() {
        return from;
    }

    public int getSize() {
        return size;
    }

    public JsonNode getQuery() {
        return query;
    }

    public JsonNode getSort() {
        return sort;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int from = 0;
        private int size = 20;
        private JsonNode query;
        private JsonNode sort;

        public Builder from(int from) {
            this.from = from;
            return this;
        }

        public Builder size(int size) {
            this.size = size;
            return this;
        }

        public Builder query(Consumer<Query> consumer) {
            Query query = new Query();
            consumer.accept(query);
            this.query = query.query;
            return this;
        }

        public Builder sort(Consumer<Sort> consumer) {
            Sort sort = new Sort();
            consumer.accept(sort);
            this.sort = sort.array;
            return this;
        }

        public ElasticQuery asPojo() {
            return new ElasticQuery(from, size, query, sort);
        }

        public JsonNode asJsonNode() {
            ObjectNode root = createObjectNode();
            root.put("from", from);
            root.put("size", size);

            if (query != null) {
                root.set("query", query);
            }

            if (sort != null) {
                root.set("sort", sort);
            }
            return root;
        }
    }

    public static class Query implements Match<Query> {
        private JsonNode query;

        public Query bool(Consumer<Bool> consumer) {
            Bool builder = new Bool();
            consumer.accept(builder);
            query = builder.asJsonNode();
            return this;
        }

        @Override
        public Query add(JsonNode node) {
            query = node;
            return this;
        }

        public static class Bool {
            private ObjectNode bool = createObjectNode();

            public Bool must(Consumer<Must> consumer) {
                Must builder = new Must();
                consumer.accept(builder);
                return set("must", builder.array);
            }

            public Bool filter(Consumer<Filter> consumer) {
                Filter filter = new Filter();
                consumer.accept(filter);
                return set("filter", filter.array);

            }

            public Bool should(Consumer<Should> consumer) {
                Should should = new Should();
                consumer.accept(should);
                return set("should", should.array);
            }

            public Bool set(String name, JsonNode node) {
                bool.set(name, node);
                return this;
            }

            public JsonNode asJsonNode() {
                ObjectNode root = createObjectNode();
                root.set("bool", bool);
                return root;
            }

            public static class Must extends Array<Must> implements Match<Must> {
            }

            public static class Filter extends Array<Filter> {

                public Filter polygon(String name, List<String> pointList) {
                    return add(DSL.filterPolygon(name, pointList));
                }

                /**
                 * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-geo-distance-query.html
                 *
                 * @param name   name of term
                 * @param latLng latLng center
                 * @param metres metres in distance
                 * @return Filter builder for chaining
                 */
                public Filter distance(String name, String latLng, double metres) {
                    return add(DSL.filterDistance(name, latLng, metres));
                }

                /**
                 * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-geo-shape-query.html
                 *
                 * @param name   name of term
                 * @param latLng point within
                 * @return Filter builder for chaining
                 */
                public Filter intersectsPoint(String name, String latLng) {
                    return add(DSL.filterIntersectsPoint(name, latLng));
                }

                /**
                 * @param name name of term
                 * @param text text of term
                 * @return Filter builder for chaining
                 */
                public Filter term(String name, String text) {
                    return add(DSL.filterTerm(name, text));
                }

                public Filter term(String name, boolean value) {
                    return add(DSL.filterTerm(name, value));
                }

                /**
                 * @param name  name of terms
                 * @param texts texts of terms
                 * @return Filter builder for chaining
                 */
                public Filter terms(String name, Collection<String> texts) {
                    return add(DSL.filterTerms(name, texts));
                }

                /**
                 * E.g. createdDate > 1000 is "createdDate", "gt", 1000
                 *
                 * @param name     name of field to filter
                 * @param operator operator in english form, e.g. gte, lt
                 * @param value    value to compare again
                 * @return Filter builder for chaining
                 */
                public Filter range(String name, String operator, long value) {
                    return add(DSL.filterRange(name, operator, value));
                }

                /**
                 * E.g. createdDate > 1000 is "createdDate", "gt", 1000
                 *
                 * @param name     name of field to filter
                 * @param operator operator in english form, e.g. gte, lt
                 * @param value    value to compare again
                 * @return Filter builder for chaining
                 */
                public Filter range(String name, String operator, double value) {
                    return add(DSL.filterRange(name, operator, value));
                }
            }

            public static class Should extends Array<Should> implements Match<Should> {
            }
        }
    }

    public static class Sort extends Array<Sort> {
        public Sort distance(String field, String latLng) {
            return add(DSL.sortDistance(field, latLng));
        }

        public Sort order(String field, String by) {
            return add(DSL.sortField(field, by));
        }

        public Sort asc(String field) {
            return order(field, "asc");
        }

        public Sort desc(String field) {
            return order(field, "desc");
        }
    }

    public interface Match<T extends Match<T>> {
        default T matchAll() {
            return add(DSL.mustMatchAll());
        }

        default T match(String name, String text) {
            return add(DSL.match(name, text));
        }

        default T matchFuzzy(String name, String text) {
            return add(DSL.matchFuzzy(name, text));
        }

        default T multiMatch(String query, String field, String... fields) {
            return add(DSL.multiMatch(query, field, fields));
        }

        T add(JsonNode node);
    }

    @SuppressWarnings("unchecked")
    public static abstract class Array<T extends Array<T>> {
        protected ArrayNode array = JsonUtils.createArrayNode();

        public T add(JsonNode node) {
            array.add(node);
            return (T) this;
        }
    }

    private static ObjectNode createObjectNode() {
        return JsonUtils.createObjectNode();
    }
}