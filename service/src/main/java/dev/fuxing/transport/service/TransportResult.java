package dev.fuxing.transport.service;


import dev.fuxing.transport.TransportList;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by: Fuxing
 * Date: 4/6/18
 * Time: 1:31 PM
 */
public class TransportResult {
    private final int code;
    private Map<String, Object> map;

    protected TransportResult(int code, Map<String, Object> map) {
        this.code = code;
        this.map = map;
    }

    protected int getCode() {
        return code;
    }

    protected Map<String, Object> getMap() {
        return map;
    }

    /**
     * @param name   of field
     * @param object to put
     * @return TransportResult for chaining
     */
    public TransportResult put(String name, Object object) {
        map.put(name, object);
        return this;
    }

    public static TransportResult ok(TransportList list) {
        TransportResult result = of(200).put("data", list);
        if (list.hasCursorMap()) {
            result.put("cursor", list.getCursorMap());
        }
        return result;
    }

    /**
     * @param data json data to return
     * @return TransportResult with 200 status
     */
    public static TransportResult ok(Object data) {
        return of(200).put("data", data);
    }

    /**
     * @param code status code
     * @param data json data
     * @return TransportResult with custom status code & data
     */
    public static TransportResult of(int code, Object data) {
        return of(code).put("data", data);
    }

    /**
     * @param code status code
     * @return TransportResult with custom status code
     */
    public static TransportResult of(int code) {
        return new TransportResult(code, new HashMap<>());
    }

    /**
     * @return TransportResult with 404 status
     */
    public static TransportResult notFound() {
        return of(404);
    }

    /**
     * @return TransportResult with 200 status
     */
    public static TransportResult ok() {
        return of(200);
    }

    @Override
    public String toString() {
        return "TransportResult{" +
                "code=" + code +
                ", map=" + map +
                '}';
    }
}
