package dev.fuxing.transport;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * This is a special class that will be automatically converted in TransportResult with data & next node info
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
     * TransportList without next
     *
     * @param c collection to copy over
     */
    public TransportList(Collection<? extends T> c) {
        super(c);
    }

    /**
     * TransportList without next
     *
     * @param i      iterable to copy and map over
     * @param mapper mapper to map data
     * @param <O>    Object to map from
     */
    public <O> TransportList(Iterable<O> i, Function<O, T> mapper) {
        super(StreamSupport.stream(i.spliterator(), false).map(mapper).collect(Collectors.toList()));
    }

    /**
     * TransportList without next
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
     * @param size            if == collection.size will next mapper
     * @param builderConsumer for next
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
     * @param size            if == collection.size will next mapper
     * @param builderConsumer for next
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
     * @param c      collection to copy over
     * @param key    of next
     * @param object of next, nullable
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
     * @param c          collection to copy over
     * @param size       if == collection.size will next mapper
     * @param nextMapper to map the last into next
     */
    public TransportList(Collection<? extends T> c, int size, Function<T, TransportCursor> nextMapper) {
        super(c);
        if (size() == size) {
            this.cursor = Map.of(
                    "next", nextMapper.apply(get(size - 1)).toBase64()
            );
        }
    }

    /**
     * @param c               collection to copy over
     * @param size            if == collection.size will next mapper
     * @param builderConsumer to inject next param
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
     * @param function using current TransportList with cursor to get the Next TransportList
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

}
