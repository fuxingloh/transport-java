package dev.fuxing.postgres;

import java.util.Set;

/**
 * Created by: Fuxing
 * Date: 2019-08-08
 * Time: 16:55
 */
public abstract class PojoSetUserType<T> extends PojoCollectionUserType<Set, T> {

    public PojoSetUserType(Class<T> clazz) {
        super(Set.class, clazz);
    }
}
