package dev.fuxing.transport;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * This is a special class that will be automatically converted in TransportResult with data and next node info
 * <p>
 * Created by: Fuxing
 * Date: 3/5/18
 * Time: 3:04 PM
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransportList<T> extends ArrayList<T> {
    private Map<String, String> cursor;

    /**
     * TransportList without cursor
     *
     * @param c collection to copy over
     */
    public TransportList(Collection<? extends T> c) {
        super(c);
    }

    /**
     * TransportList without cursor
     *
     * @param i      iterable to copy and map over
     * @param mapper mapper to map data
     * @param <O>    Object to map from
     */
    public <O> TransportList(Iterable<O> i, Function<O, T> mapper) {
        super(StreamSupport.stream(i.spliterator(), false).map(mapper).collect(Collectors.toList()));
    }

    /**
     * TransportList without cursor
     *
     * @param c      collection to copy and map over
     * @param mapper mapper to map data
     * @param <O>    Object to map from
     */
    public <O> TransportList(Collection<O> c, Function<O, T> mapper) {
        super(c.stream().map(mapper).collect(Collectors.toList()));
    }

    /**
     * @param c               collection to copy and map over
     * @param mapper          to map object into T
     * @param size            if == collection.size next cursor will be created
     * @param builderConsumer for building next cursor
     * @param <O>             Object to map from
     */
    public <O> TransportList(Collection<O> c, Function<O, T> mapper, int size, BiConsumer<T, TransportCursor.Builder> builderConsumer) {
        super(c.stream().map(mapper).collect(Collectors.toList()));
        if (size() == size) {
            TransportCursor.Builder builder = new TransportCursor.Builder();
            builderConsumer.accept(get(size - 1), builder);
            this.cursor = Map.of(
                    "next", builder.build().toBase64()
            );
        }
    }


    /**
     * @param i               iterable to copy and map over
     * @param mapper          to map object into T
     * @param size            if == collection.size next cursor will be created
     * @param builderConsumer for building next cursor
     * @param <O>             Object to map from
     */
    public <O> TransportList(Iterable<O> i, Function<O, T> mapper, int size, BiConsumer<T, TransportCursor.Builder> builderConsumer) {
        super(StreamSupport.stream(i.spliterator(), false).map(mapper).collect(Collectors.toList()));
        if (size() == size) {
            TransportCursor.Builder builder = new TransportCursor.Builder();
            builderConsumer.accept(get(size - 1), builder);
            this.cursor = Map.of(
                    "next", builder.build().toBase64()
            );
        }
    }


    /**
     * @param c      collection to copy over
     * @param cursor map
     */
    public TransportList(Collection<? extends T> c, Map<String, String> cursor) {
        super(c);
        this.cursor = cursor;
    }

    /**
     * Single key-value cursor
     *
     * @param c      collection to copy over
     * @param key    for single key-value cursor
     * @param object for single key-value cursor, nullable
     */
    public TransportList(Collection<? extends T> c, String key, @Nullable Object object) {
        super(c);
        if (object != null) {
            TransportCursor.Builder builder = new TransportCursor.Builder();
            builder.put(key, object);
            this.cursor = Map.of(
                    "next", builder.build().toBase64()
            );
        }
    }

    /**
     * @param c            collection to copy over
     * @param size         if == collection.size, next cursor will be created
     * @param cursorMapper to map the last into a cursor
     */
    public TransportList(Collection<? extends T> c, int size, Function<T, TransportCursor> cursorMapper) {
        super(c);
        if (size() == size) {
            this.cursor = Map.of(
                    "next", cursorMapper.apply(get(size - 1)).toBase64()
            );
        }
    }

    /**
     * @param c               collection to copy over
     * @param size            if == collection.size, next cursor will be created
     * @param builderConsumer for building next cursor
     */
    public TransportList(Collection<? extends T> c, int size, BiConsumer<T, TransportCursor.Builder> builderConsumer) {
        super(c);
        if (size() == size) {
            TransportCursor.Builder builder = new TransportCursor.Builder();
            builderConsumer.accept(get(size - 1), builder);
            this.cursor = Map.of(
                    "next", builder.build().toBase64()
            );
        }
    }

    public boolean hasCursorMap() {
        return cursor != null;
    }

    public Map<String, String> getCursorMap() {
        return cursor;
    }

    @Nullable
    public String getCursorNext() {
        if (cursor == null) return null;
        return cursor.get("next");
    }

    @Nullable
    public String getCursorPrev() {
        if (cursor == null) return null;
        return cursor.get("prev");
    }

    /**
     * @param function using current TransportList with cursor to get the next TransportList
     * @return Iterator of TransportList Chaining
     */
    public Iterator<T> toIterator(Function<String, TransportList<T>> function) {
        TransportList<T> initial = this;
        return new Iterator<>() {
            TransportList<T> current = initial;
            Iterator<T> iterator = initial.iterator();

            @Override
            public boolean hasNext() {
                if (iterator.hasNext()) return true;

                String next = current.getCursorNext();
                if (next == null) return false;

                current = function.apply(next);
                if (current == null) return false;
                if (current.isEmpty()) return false;

                iterator = current.iterator();
                return iterator.hasNext();
            }

            @Override
            public T next() {
                return iterator.next();
            }
        };
    }

    /**
     * @param mapper function mapper
     * @param <R>    Mapped type
     * @return Mapped TransportList
     */
    public <R> TransportList<R> map(Function<T, R> mapper) {
        List<R> collected = this.stream()
                .map(mapper)
                .collect(Collectors.toList());

        return new TransportList<>(collected, cursor);
    }

    /**
     * @param <T> Item type
     * @return TransportList Builder
     */
    public static <T> TransportList.Builder<T> builder() {
        return new Builder<>();
    }

    public static final class Builder<T> {
        private List<T> list = new ArrayList<>();
        private Map<String, String> cursor = new HashMap<>();

        private Builder() {
        }

        public Builder<T> add(T item) {
            list.add(item);
            return this;
        }

        public Builder<T> addAll(Collection<T> items) {
            list.addAll(items);
            return this;
        }

        public Builder<T> cursor(String name, Consumer<TransportCursor.Builder> consumer) {
            TransportCursor.Builder builder = TransportCursor.builder();
            consumer.accept(builder);
            this.cursor.put(name, builder.toBase64());
            return this;
        }

        public Builder<T> next(int size, BiConsumer<T, TransportCursor.Builder> consumer) {
            if (list.size() == size) {
                this.cursor("next", builder -> {
                    consumer.accept(list.get(size - 1), builder);
                });
            }
            return this;
        }

        public <R> Builder<R> map(Function<T, R> mapper) {
            Builder<R> builder = new Builder<>();
            builder.list = list.stream()
                    .map(mapper)
                    .collect(Collectors.toList());
            builder.cursor = cursor;
            return builder;
        }

        public TransportList<T> build() {
            return new TransportList<>(list, cursor);
        }
    }
}
