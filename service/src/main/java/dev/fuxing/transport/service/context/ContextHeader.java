package dev.fuxing.transport.service.context;

/**
 * Created by: Fuxing
 * Date: 2019-04-24
 * Time: 19:18
 */
public interface ContextHeader extends Context {
    /**
     * @param name name of header
     * @return nullable string header
     */
    default String getHeader(String name) {
        return request().headers(name);
    }
}
