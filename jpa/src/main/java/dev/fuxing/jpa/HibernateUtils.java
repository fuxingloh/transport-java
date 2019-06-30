package dev.fuxing.jpa;

import org.hibernate.Hibernate;
import org.hibernate.collection.internal.PersistentSet;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.pojo.javassist.SerializableProxy;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.*;

/**
 * Thread-Safe Singleton Hibernate Util
 * Only use this if there is no default implementation available on your platform.
 * Created by Fuxing
 * Date: 1/1/2015
 * Time: 5:49 PM
 * Project: v22-transport
 */
public final class HibernateUtils {
    public static final String DEFAULT_PERSISTENCE_UNIT = "defaultPersistenceUnit";

    private static Map<String, EntityManagerFactory> factories = new HashMap<>();

    private HibernateUtils() { /* private */ }

    /**
     * @param properties nullable properties for overriding
     * @return created TransactionProvider
     */
    public static TransactionProvider setupFactory(Map<String, String> properties) {
        return setupFactory(DEFAULT_PERSISTENCE_UNIT, properties);
    }

    /**
     * @param unitName   persistence unit name
     * @param properties nullable properties for overriding
     * @return created TransactionProvider
     */
    public static TransactionProvider setupFactory(String unitName, Map<String, String> properties) {
        if (!factories.containsKey(unitName)) {
            synchronized (HibernateUtils.class) {
                if (!factories.containsKey(unitName)) {
                    // Setup Factory & put to map
                    EntityManagerFactory factory = Persistence.createEntityManagerFactory(unitName, properties);
                    factories.put(unitName, factory);
                    return new TransactionProvider(unitName, factory);
                }
            }
        }
        throw new RuntimeException(new IllegalStateException("Factory already initialized."));
    }

    /**
     * Shutdown the default instance
     * Thread-safe
     */
    public static void shutdown() {
        shutdown(DEFAULT_PERSISTENCE_UNIT);
    }

    /**
     * Shutdown the default instance
     * Thread-safe
     *
     * @param unitName to shut down
     */
    public static void shutdown(String unitName) {
        if (factories.containsKey(unitName)) {
            synchronized (HibernateUtils.class) {
                if (factories.containsKey(unitName)) {
                    factories.remove(unitName).close();
                }
            }
        }
    }

    /**
     * Shutdown all factory
     */
    public static void shutdownAll() {
        for (String unitName : factories.keySet()) {
            shutdown(unitName);
        }
    }

    /**
     * @param unitName persistence unit name
     * @return TransactionProvider of unit, null if don't exist
     */
    public static TransactionProvider get(String unitName) {
        EntityManagerFactory factory = factories.get(unitName);
        if (factory == null) return null;
        return new TransactionProvider(unitName, factory);
    }

    /**
     * @return default TransactionProvider
     */
    public static TransactionProvider get() {
        return get(DEFAULT_PERSISTENCE_UNIT);
    }

    public static <T> boolean has(EntityManager entityManager, Class<T> entityClass, String idName, Object idValue) {
        CriteriaBuilder qb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = qb.createQuery(Long.class);

        Root<T> root = cq.from(entityClass);
        cq.select(qb.count(root));

        cq.where(qb.equal(root.get(idName), idValue));
        return entityManager.createQuery(cq).getSingleResult() > 0;
    }

    public static <T> T clean(T value) {
        T result = unproxyObject(value);
        cleanFromProxies(result, new ArrayList<Object>());
        return result;
    }

    public static void initialize(Object proxy) {
        Hibernate.initialize(proxy);
    }

    @SuppressWarnings("unchecked")
    public static <T> T unproxy(T proxy) {
        return (T) Hibernate.unproxy(proxy);
    }

    private static void cleanFromProxies(Object value, List<Object> handledObjects) {
        if ((value != null) && (!isProxy(value)) && !containsTotallyEqual(handledObjects, value)) {
            handledObjects.add(value);
            if (value instanceof Iterable) {
                for (Object item : (Iterable<?>) value) {
                    cleanFromProxies(item, handledObjects);
                }
            } else if (value.getClass().isArray()) {
                for (Object item : (Object[]) value) {
                    cleanFromProxies(item, handledObjects);
                }
            }
            BeanInfo beanInfo = null;
            try {
                beanInfo = Introspector.getBeanInfo(value.getClass());
            } catch (IntrospectionException e) {
                // LOGGER.warn(e.getMessage(), e);
            }
            if (beanInfo != null) {
                for (PropertyDescriptor property : beanInfo.getPropertyDescriptors()) {
                    try {
                        if ((property.getWriteMethod() != null) && (property.getReadMethod() != null)) {
                            Object fieldValue = property.getReadMethod().invoke(value);
                            if (isProxy(fieldValue)) {
                                fieldValue = unproxyObject(fieldValue);
                                property.getWriteMethod().invoke(value, fieldValue);
                            }
                            cleanFromProxies(fieldValue, handledObjects);
                        }
                    } catch (Exception e) {
                        // LOGGER.warn(e.getMessage(), e);
                    }
                }
            }
        }
    }

    private static boolean containsTotallyEqual(Collection<?> collection, Object value) {
        if (collection == null || collection.isEmpty()) {
            return false;
        }
        for (Object object : collection) {
            if (object == value) {
                return true;
            }
        }
        return false;
    }

    public static boolean isProxy(Object value) {
        if (value == null) {
            return false;
        }
        if ((value instanceof HibernateProxy) || (value instanceof PersistentCollection)) {
            return true;
        }
        return false;
    }

    private static Object unproxyHibernateProxy(HibernateProxy hibernateProxy) {
        Object result = hibernateProxy.writeReplace();
        if (!(result instanceof SerializableProxy)) {
            return result;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <T> T unproxyObject(T object) {
        if (isProxy(object)) {
            if (object instanceof PersistentCollection) {
                PersistentCollection persistentCollection = (PersistentCollection) object;
                return (T) unproxyPersistentCollection(persistentCollection);
            } else if (object instanceof HibernateProxy) {
                HibernateProxy hibernateProxy = (HibernateProxy) object;
                return (T) unproxyHibernateProxy(hibernateProxy);
            } else {
                return null;
            }
        }
        return object;
    }

    private static Object unproxyPersistentCollection(PersistentCollection persistentCollection) {
        if (persistentCollection instanceof PersistentSet) {
            return unproxyPersistentSet((Map<?, ?>) persistentCollection.getStoredSnapshot());
        }
        return persistentCollection.getStoredSnapshot();
    }

    private static <T> Set<T> unproxyPersistentSet(Map<T, ?> persistenceSet) {
        return new LinkedHashSet<T>(persistenceSet.keySet());
    }
}
