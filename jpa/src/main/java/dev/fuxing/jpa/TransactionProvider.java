
package dev.fuxing.jpa;


import dev.fuxing.err.NotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Transaction provider to run lambda function in JPA style
 * <p>
 * Created by Fuxing
 * Date: 8/7/2015
 * Time: 4:07 PM
 * Project: v22-transport
 */
public class TransactionProvider {
    public static final String HINT_READ_ONLY = "org.hibernate.readOnly";

    protected final String unitName;
    protected final EntityManagerFactory factory;

    /**
     * @param unitName unit name of provider
     * @param factory  for provider to create entity manager
     */
    public TransactionProvider(String unitName, EntityManagerFactory factory) {
        this.unitName = unitName;
        this.factory = factory;
    }

    /**
     * @return provided EntityFactoryFactory
     */
    public EntityManagerFactory getFactory() {
        return factory;
    }

    /**
     * @return unit name of current provider
     */
    public String getUnitName() {
        return unitName;
    }

    /**
     * @return boolean indicating whether the provider is open
     */
    public boolean isOpen() {
        return getFactory().isOpen();
    }

    /**
     * Run JPA style transaction in lambda
     *
     * @param consumer transaction lambda
     */
    public void with(Consumer<EntityManager> consumer) {
        with(false, consumer);
    }

    /**
     * Run JPA style transaction in lambda
     *
     * @param readOnly whether this read-only, read only is faster as transaction is turned off
     * @param consumer transaction lambda
     */
    public void with(boolean readOnly, Consumer<EntityManager> consumer) {
        reduce(readOnly, em -> {
            consumer.accept(em);
            return null;
        });
    }

    /**
     * Run jpa style transaction in functional style with optional transaction
     * Optional Transaction are basically reduce transaction that will
     * catch NoResultException and convert it to Optional.empty()
     * Using the default transaction provider
     *
     * @param function reduce transaction to apply that with convert to optional
     * @param <T>      type of object
     * @return object
     */
    public <T> Optional<T> optional(Function<EntityManager, T> function) {
        return optional(false, function);
    }

    /**
     * Run jpa style transaction in functional style with optional transaction
     * Optional Transaction are basically reduce transaction that will
     * catch NoResultException and convert it to Optional.empty()
     * Using the default transaction provider
     *
     * @param readOnly whether this read-only, read only is faster as transaction is turned off
     * @param function reduce transaction to apply that with convert to optional
     * @param <T>      type of object
     * @return result
     */
    public <T> Optional<T> optional(boolean readOnly, Function<EntityManager, T> function) {
        T result = reduce(readOnly, function);
        return Optional.ofNullable(clean(result));
    }

    /**
     * Run JPA style transaction in functional style with reduce <br>
     * NoResultException is mapped to {@code null}
     *
     * @param function reduce transaction to apply
     * @param <T>      type of object
     * @return object
     */
    public <T> T reduce(Function<EntityManager, T> function) {
        return reduce(false, function);
    }

    /**
     * Run JPA style transaction in functional style with reduce. <br>
     * NoResultException is mapped to {@code null}
     *
     * @param readOnly whether this read-only, read only is faster as transaction is turned off
     * @param function reduce transaction to apply
     * @param <T>      type of object
     * @return object
     */
    public <T> T reduce(boolean readOnly, Function<EntityManager, T> function) {
        if (readOnly) {
            return reduceReadOnly(function);
        } else {
            return reduceTransactional(function);
        }
    }

    private <T> T reduceReadOnly(Function<EntityManager, T> function) {
        EntityManager entityManager = null;

        try {
            entityManager = factory.createEntityManager();
            entityManager.setProperty(HINT_READ_ONLY, true);

            T result = function.apply(entityManager);

            return clean(result);
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    private <T> T reduceTransactional(Function<EntityManager, T> function) {
        EntityManager entityManager = null;
        EntityTransaction transaction = null;

        try {
            entityManager = factory.createEntityManager();
            entityManager.setProperty(HINT_READ_ONLY, false);

            transaction = entityManager.getTransaction();
            transaction.begin();

            T result = function.apply(entityManager);

            if (transaction.getRollbackOnly()) {
                transaction.rollback();
            } else {
                transaction.commit();
            }

            return clean(result);
        } catch (Exception e) {
            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (Throwable ignored) {/**/}
            }

            if (e instanceof NoResultException) {
                throw new NotFoundException();
            }

            throw new DatabaseException(e);
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    private <T> T clean(T object) {
        if (object instanceof Collection) {
            for (Object o : (Collection) object) {
                HibernateUtils.clean(o);
            }
        }
        return HibernateUtils.clean(object);
    }
}
