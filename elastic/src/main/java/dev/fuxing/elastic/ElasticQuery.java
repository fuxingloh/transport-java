package dev.fuxing.elastic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.fuxing.elastic.dsl.ElasticDSL;
import dev.fuxing.elastic.dsl.SpatialDSL;
import dev.fuxing.utils.JsonUtils;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Created by: Fuxing
 * Date: 2019-04-15
 * Time: 20:51
 * Project: v22-transport
 *
 * @deprecated use Elasticsearch official high level rest API instead, available since v6.0.0.beta
 */
@Deprecated
public class ElasticQuery {
    private static final ElasticDSL DSL = new ElasticDSL();

    private final Integer from;
    private final Integer size;
    private final Set<String> sources;

    private final JsonNode query;
    private final JsonNode sort;
    private final JsonNode suggest;

    protected ElasticQuery(Integer from, Integer size, Set<String> sources, JsonNode query, JsonNode sort, JsonNode suggest) {
        this.from = from;
        this.size = size;
        this.sources = sources;
        this.query = query;
        this.sort = sort;

        this.suggest = suggest;
    }

    public Integer getFrom() {
        return from;
    }

    public Integer getSize() {
        return size;
    }

    public Set<String> getSources() {
        return sources;
    }

    public JsonNode getQuery() {
        return query;
    }

    public JsonNode getSort() {
        return sort;
    }

    public JsonNode getSuggest() {
        return suggest;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer from = null;
        private Integer size = null;
        private Set<String> sources;

        private JsonNode query;
        private JsonNode sort;
        private JsonNode suggest;

        public Builder from(int from) {
            this.from = from;
            return this;
        }

        public Builder size(int size) {
            this.size = size;
            return this;
        }

        public Builder sources(String... sources) {
            this.sources = Set.of(sources);
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

        public Builder suggest(Consumer<Suggest> consumer) {
            Suggest suggest = new Suggest();
            consumer.accept(suggest);
            this.suggest = suggest.suggest;
            return this;
        }

        public ElasticQuery asPojo() {
            return new ElasticQuery(from, size, sources, query, sort, suggest);
        }

        public JsonNode asJsonNode() {
            ObjectNode root = createObjectNode();
            if (from != null) {
                root.put("from", from);
            }

            if (size != null) {
                root.put("size", size);
            }

            if (sources != null) {
                root.set("_sources", JsonUtils.valueToTree(sources));
            }

            if (query != null) {
                root.set("query", query);
            }

            if (sort != null) {
                root.set("sort", sort);
            }

            if (suggest != null) {
                root.set("suggest", suggest);
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
                 * E.g. createdDate &gt; 1000 is "createdDate", "gt", 1000
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
                 * E.g. createdDate &gt; 1000 is "createdDate", "gt", 1000
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

    public static class Suggest {
        private ObjectNode suggest = createObjectNode();

        public Suggest prefix(String name, String prefixText, Consumer<Completion> consumer) {
            Completion completion = new Completion();
            consumer.accept(completion);

            suggest.putObject(name)
                    .put("prefix", prefixText)
                    .set("completion", completion.completion);
            return this;
        }

        public static class Completion {
            ObjectNode completion = createObjectNode();

            public Completion field(String field) {
                completion.put("field", field);
                return this;
            }

            public Completion fuzzy(boolean fuzzy) {
                completion.put("fuzzy", true);
                return this;
            }

            public Completion size(int size) {
                completion.put("size", size);
                return this;
            }

            public Completion contexts(Consumer<Contexts> consumer) {
                Contexts contexts = new Contexts();
                consumer.accept(contexts);
                completion.set("contexts", contexts.contexts);
                return this;
            }

            public static class Contexts {
                ObjectNode contexts = createObjectNode();

                public Contexts category(String name, String... categories) {
                    ArrayNode arrayNode = createArrayNode();
                    for (String cat : categories) {
                        arrayNode.add(cat);
                    }

                    contexts.set(name, arrayNode);
                    return this;
                }

                public Contexts geo(String name, String latLng, int precision, double boost) {
                    double[] ll = SpatialDSL.parse(latLng);

                    ArrayNode latLngArray = createArrayNode();
                    latLngArray.addObject()
                            .put("precision", precision)
                            .put("boost", boost)
                            .putObject("context")
                            .put("lat", ll[0])
                            .put("lon", ll[1]);
                    contexts.set(name, latLngArray);
                    return this;
                }

                public Contexts raw(String name, JsonNode context) {
                    contexts.set(name, context);
                    return this;
                }
            }
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
        protected ArrayNode array = createArrayNode();

        public T add(JsonNode node) {
            array.add(node);
            return (T) this;
        }
    }

    private static ObjectNode createObjectNode() {
        return JsonUtils.createObjectNode();
    }

    private static ArrayNode createArrayNode() {
        return JsonUtils.createArrayNode();
    }
}
