package dti.oasis.jpa;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * The base interface for the DAO classes.
 *
 * <p>The following methods are not supported in the DAO classes:
 * <ul>
 *     <li>{@link BaseService#saveEntity(Object)}</li>
 *     <li>{@link BaseService#saveEntity(java.util.List)}</li>
 *     <li>{@link BaseService#validateEntity(Object)}</li>
 *     <li>{@link BaseService#validateEntity(java.util.List)}</li>
 * </ul>
 * </p>
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:
 *
 * @author
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/24/2015       kshen       168409. Enhance Services to restrict usage to a single Entity type.
 * ---------------------------------------------------
 */
public interface BaseDAO<T> extends BaseService<T> {

    public long countAll(EntityManager entityManager, Class<T> entityClass);

    public T loadByPk(EntityManager entityManager, Class<T> entityClass, long pk);

    public List<T> loadAll(EntityManager entityManager, Class<T> entityClass);
    public List<T> loadAll(EntityManager entityManager, Class<T> entityClass, String orderByField, boolean isAscending);

    public void addEntity(EntityManager entityManager, List<T> entityList);
    public T addEntity(EntityManager entityManager, T entity);

    public List<T> updateEntity(EntityManager entityManager, List<T> entityList);
    public T updateEntity(EntityManager entityManager, T entity);

    public void deleteEntity(EntityManager entityManager, List<T> entityList);
    public void deleteEntity(EntityManager entityManager, T entity);

}
