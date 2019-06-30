package dev.fuxing.jpa;

import com.fasterxml.jackson.databind.JsonNode;
import dev.fuxing.utils.JsonUtils;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.util.function.Consumer;

/**
 * Created by: Fuxing
 * Date: 2019-06-12
 * Time: 20:42
 */
public final class EntityPatcher {

    public static <T> JsonBody<T> with(EntityManager entityManager, T entity, JsonNode body) {
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

        public JsonBody<T> patch(String name, Consumer<JsonNode> consumer) {
            if (json.has(name)) {
                consumer.accept(json.path(name));
            }
            return this;
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

    public static abstract class Body<T> {
        protected final EntityManager entityManager;

        protected final T entity;

        protected Body(EntityManager entityManager, T entity) {
            this.entityManager = entityManager;
            this.entity = entity;
        }

        public T persist() {
            entityManager.persist(entity);
            return entity;
        }
    }
}
