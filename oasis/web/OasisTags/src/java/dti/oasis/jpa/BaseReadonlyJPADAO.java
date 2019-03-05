package dti.oasis.jpa;

import dti.oasis.app.AppException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   12/23/2015
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
@Service("baseReadonlyJPADAO")
public abstract class BaseReadonlyJPADAO<T> extends BaseJPADAO<T> {
    @Override
    public void addEntity(List<T> entityList) {
        throw new AppException("Unsupported method.");
    }

    /**
     * Save an entity.
     * <P>This method is unsupported in the <code>DAO</code> classes.</P>
     *
     * @param entity
     * @return
     */
    @Override
    public T saveEntity(T entity) {
        throw new AppException("Unsupported method.");
    }

    /**
     * Save entities.
     * <P>This method is unsupported in the <code>DAO</code> classes.</P>
     *
     * @param entityList
     */
    @Override
    public List<NewEntityKeyMap> saveEntity(List<T> entityList) {
        throw new AppException("Unsupported method.");
    }

    /**
     * Validate an entity.
     * <P>This method is unsupported in the <code>DAO</code> classes.</P>
     *
     * @param entity
     */
    @Override
    public void validateEntity(T entity) {
        throw new AppException("Unsupported method.");
    }

    /**
     * Validate entities.
     * <P>This method is unsupported in the <code>DAO</code> classes.</P>
     *
     * @param entityList
     */
    @Override
    public void validateEntity(List<T> entityList) {
        throw new AppException("Unsupported method.");
    }

    @Override
    public T addEntity(T entity) {
        throw new AppException("Unsupported method.");
    }

    @Override
    public List<T> updateEntity(List<T> entityList) {
        throw new AppException("Unsupported method.");
    }

    @Override
    public T updateEntity(T entity) {
        throw new AppException("Unsupported method.");
    }

    @Override
    public void deleteEntity(List<T> entityList) {
        throw new AppException("Unsupported method.");
    }

    @Override
    public void deleteEntity(T entity) {
        throw new AppException("Unsupported method.");
    }

    /**
     * add entities
     *
     * @param entityManager
     * @param entityList
     */
    @Override
    public void addEntity(EntityManager entityManager, List<T> entityList) {
        throw new AppException("Unsupported method.");
    }

    /**
     * add entity
     *
     * @param entityManager
     * @param entity
     */
    @Override
    public T addEntity(EntityManager entityManager, T entity) {
        throw new AppException("Unsupported method.");
    }

    /**
     * update entities
     *
     * @param entityManager
     * @param entityList
     */
    @Override
    public List<T> updateEntity(EntityManager entityManager, List<T> entityList) {
        throw new AppException("Unsupported method.");
    }

    /**
     * update entity
     *
     * @param entityManager
     * @param entity
     */
    @Override
    public T updateEntity(EntityManager entityManager, T entity) {
        throw new AppException("Unsupported method.");
    }

    /**
     * delete entities
     *
     * @param entityManager
     * @param entityList
     */
    @Override
    public void deleteEntity(EntityManager entityManager, List<T> entityList) {
        throw new AppException("Unsupported method.");
    }

    /**
     * delete entity
     *
     * @param entityManager
     * @param entity
     */
    @Override
    public void deleteEntity(EntityManager entityManager, T entity) {
        throw new AppException("Unsupported method.");
    }
}
