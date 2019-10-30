package dev.fuxing.jpa;

import com.fasterxml.jackson.databind.JsonNode;
import dev.fuxing.transport.TransportCursor;
import dev.fuxing.transport.TransportList;
import dev.fuxing.utils.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.*;
import java.util.stream.Collectors;

/**
 * Created by: Fuxing
 * Date: 2019-08-12
 * Time: 16:11
 */
public class EntityStream<T> {
    protected List<T> list;
    protected Map<String, String> cursor;

    protected EntityStream(List<T> list, Map<String, String> cursor) {
        this.list = list;
        this.cursor = cursor;
    }

    public <R> EntityStream<R> map(Function<T, R> function) {
        List<R> returned = new ArrayList<>();
        for (T t : this.list) {
            returned.add(function.apply(t));
        }
        return new EntityStream<>(returned, cursor);
    }

    public EntityStream<T> peek(Consumer<T> consumer) {
        list.forEach(consumer);
        return this;
    }

    public EntityStream<T> removeIf(Predicate<T> predicate) {
        list.removeIf(predicate);
        return this;
    }

    public EntityStream<T> cursor(int size, BiConsumer<T, TransportCursor.Builder> consumer) {
        if (list.size() == size) {
            TransportCursor.Builder builder = new TransportCursor.Builder();
            consumer.accept(list.get(size - 1), builder);
            this.cursor = Map.of(
                    "next", builder.build().toBase64()
            );
        }
        return this;
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    public TransportList<JsonNode> asTransportList() {
        List<JsonNode> nodes = list.stream().map(t -> {
            HibernateUtils.clean(t);
            JsonNode node = JsonUtils.valueToTree(t);
            return node;
        }).collect(Collectors.toList());
        return new TransportList<>(nodes, cursor);
    }

    /**
     * @param consumer to accept entity list and its next cursor
     */
    public void consume(BiConsumer<List<T>, Map<String, String>> consumer) {
        consumer.accept(list, cursor);
    }

    public static <T> EntityStream<T> of(Supplier<List<T>> supplier) {
        return new EntityStream<>(supplier.get(), null);
    }

    public static <T> EntityStream<T> of(List<T> list) {
        return new EntityStream<>(list, null);
    }
}
