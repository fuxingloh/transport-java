package dev.fuxing.jpa;

import com.fasterxml.jackson.databind.JsonNode;
import dev.fuxing.err.NotFoundException;
import dev.fuxing.utils.JsonUtils;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by: Fuxing
 * Date: 2019-06-12
 * Time: 20:42
 */
public final class EntityPatch {

    public static <T> JsonBody<T> with(EntityManager entityManager, T entity, JsonNode body) {
        if (entity == null) {
            throw new NotFoundException();
        }

        return new JsonBody<>(entityManager, entity, body);
    }

    public static class JsonBody<T> extends Body<T> {
        private final JsonNode json;

        JsonBody(EntityManager entityManager, T entity, JsonNode json) {
            super(entityManager, entity);
            this.json = json;
        }

        public JsonBody<T> lock() {
            return lock(LockModeType.WRITE);
        }

        public JsonBody<T> lock(LockModeType type) {
            entityManager.lock(entity, type);
            return this;
        }

        public <E> JsonBody<T> patch(String name, Class<E> enumClass, EnumConsumer<T, E> consumer) {
            return patch(name, (NodeConsumer<T>) (content, node) -> {
                E type = JsonUtils.toObject(node, enumClass);
                consumer.accept(content, type);
            });
        }

        public JsonBody<T> patch(String name, StringConsumer<T> consumer) {
            return patch(name, (NodeConsumer<T>) (content, node) -> {
                consumer.accept(content, node.asText());
            });
        }

        public JsonBody<T> patch(String name, LongConsumer<T> consumer) {
            return patch(name, (NodeConsumer<T>) (content, node) -> {
                consumer.accept(content, node.asLong());
            });
        }

        public JsonBody<T> patch(String name, IntegerConsumer<T> consumer) {
            return patch(name, (NodeConsumer<T>) (content, node) -> {
                consumer.accept(content, node.asInt());
            });
        }

        public JsonBody<T> patch(String name, NodeConsumer<T> consumer) {
            return patch(name, (json) -> {
                consumer.accept(entity, json);
            });
        }

        private JsonBody<T> patch(String name, Consumer<JsonNode> consumer) {
            if (json.has(name)) {
                consumer.accept(json.path(name));
            }
            return this;
        }

        public <E> JsonBody<T> patch(String name, Function<T, E> function, Consumer<JsonBody<E>> consumer) {
            JsonNode deepJson = json.path(name);
            E deepEntity = function.apply(entity);
            consumer.accept(new JsonBody<>(entityManager, deepEntity, deepJson));
            return this;
        }

        /**
         * This method requires OneToMany relationship to be CASCADE.PERSIST and orphanRemoval = true
         * This method will delete, patch and create entity
         *
         * @param name       of collection
         * @param toEntities from entity to list of deep entities
         * @param keyEqual   key predicate function
         * @param patcher    to patch the deep entity
         * @param toEntity   from json node to entity
         * @param <E>        entity type
         * @return chaining of the current instance
         */
        public <E> JsonBody<T> patch(String name, Function<T, Collection<E>> toEntities, BiPredicate<E, JsonNode> keyEqual, Consumer<EntityPatch.JsonBody<E>> patcher, Function<JsonNode, E> toEntity) {
            Collection<E> deepEntities = toEntities.apply(entity);
            Set<JsonNode> deepBodies = new HashSet<>();
            json.path(name).forEach(deepBodies::add);

            deepEntities.removeIf(deepEntity -> {
                // Check whether existing json exist
                Optional<JsonNode> deepJson = deepBodies.stream()
                        .filter(jsonNode -> keyEqual.test(deepEntity, jsonNode))
                        .findFirst();

                // If deepJson exist, patch and remove from existing list
                if (deepJson.isPresent()) {
                    patcher.accept(new EntityPatch.JsonBody<>(entityManager, deepEntity, deepJson.get()));
                    deepBodies.remove(deepJson.get());
                    return false;
                }

                // If deepJson don't exist, it needs to be removed so that orphanRemoval can delete the entity
                return true;
            });

            // Add remaining bodies into deep entities
            deepBodies.forEach(jsonNode -> {
                deepEntities.add(toEntity.apply(jsonNode));
            });

            return this;
        }
    }

    public static abstract class Body<T> {
        protected final EntityManager entityManager;

        protected final T entity;

        protected Body(EntityManager entityManager, T entity) {
            this.entityManager = entityManager;
            this.entity = entity;
        }

        public T persist() {
            return persist(entityManager::persist);
        }

        public T persist(Consumer<T> consumer) {
            consumer.accept(entity);
            return entity;
        }
    }

    public interface NodeConsumer<T> {
        void accept(T t, JsonNode json);
    }

    public interface StringConsumer<T> {
        void accept(T t, String string);
    }

    public interface LongConsumer<T> {
        void accept(T t, Long aLong);
    }

    public interface IntegerConsumer<T> {
        void accept(T t, Integer integer);
    }

    public interface EnumConsumer<T, E> {
        void accept(T t, E aEnum);
    }
}
