package dev.fuxing.postgres;

import java.util.List;

/**
 * Created by: Fuxing
 * Date: 2019-08-08
 * Time: 16:55
 */
public abstract class PojoListUserType<T> extends PojoCollectionUserType<List, T> {

    public PojoListUserType(Class<T> clazz) {
        super(List.class, clazz);
    }
}
