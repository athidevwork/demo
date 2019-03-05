package dti.oasis.jpa;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.List;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   10/3/2014
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/12/2015       kshen       Added method saveEntity and validateEntity.
 * 12/24/2015       kshen       168409. Enhance Services to restrict usage to a single Entity type.
 * ---------------------------------------------------
 */
public interface BaseService<T> extends LoadByFilterService<T> {

    public long countAll();

    public T loadByPk(long pk);

    public List<T> loadAll();
    public List<T> loadAll(String orderByField, boolean isAscending);
    public List<T> loadAll(List<HashMap> orderList);

    public void addEntity(List<T> entityList);
    public T addEntity(T entity);

    public List<T> updateEntity(List<T> entityList);
    public T updateEntity(T entity);

    public void deleteEntity(List<T> entityList);
    public void deleteEntity(T entity);

    /**
     * Save an entity.
     * <P>This method is unsupported in the <code>DAO</code> classes.</P>
     *
     * @param entity
     * @return
     */
    public T saveEntity(T entity);

    /**
     * Save entities.
     * <P>This method is unsupported in the <code>DAO</code> classes.</P>
     *
     * @param entityList
     */
    public List<NewEntityKeyMap> saveEntity(List<T> entityList);

    /**
     * Validate an entity.
     * <P>This method is unsupported in the <code>DAO</code> classes.</P>
     *
     * @param entity
     */
    public void validateEntity(T entity);

    /**
     * Validate entities.
     * <P>This method is unsupported in the <code>DAO</code> classes.</P>
     *
     * @param entityList
     */
    public void validateEntity(List<T> entityList);
}
