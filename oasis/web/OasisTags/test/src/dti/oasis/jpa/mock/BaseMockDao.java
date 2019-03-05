package dti.oasis.jpa.mock;

import dti.oasis.jpa.BaseJPADAO;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   12/10/2015
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
public abstract class BaseMockDao<T> extends BaseJPADAO<T> {
    @Override
    protected EntityManager getEntityManager() {
        return null;
    }

    @Override
    public  void deleteEntity(List<T> entityList) {
    }

    @Override
    public void addEntity(List<T> entityList) {
    }

    @Override
    public List<T> updateEntity(List<T> entityList) {
        return entityList;
    }
}
