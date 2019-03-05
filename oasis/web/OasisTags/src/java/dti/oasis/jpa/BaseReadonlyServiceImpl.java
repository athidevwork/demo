package dti.oasis.jpa;

import dti.oasis.app.AppException;

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
public abstract class BaseReadonlyServiceImpl<T>  extends BaseServiceImpl<T> {
    @Override
    public void addEntity(List<T> entityList) {
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

    @Override
    public T saveEntity(T entity) {
        throw new AppException("Unsupported method.");
    }

    @Override
    public List<NewEntityKeyMap> saveEntity(List<T> entityList) {
        throw new AppException("Unsupported method.");
    }

    @Override
    public void validateEntity(T entity) {
        throw new AppException("Unsupported method.");
    }

    @Override
    public void validateEntity(List<T> entityList) {
        throw new AppException("Unsupported method.");
    }
}
