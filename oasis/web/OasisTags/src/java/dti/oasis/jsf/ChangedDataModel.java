package dti.oasis.jsf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * data model to store changed data
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   12/26/12
 *
 * @author jxgu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class ChangedDataModel<T> implements Serializable {

    /**
     * add a new entity
     *
     * @param entity
     */
    public void insertEntity(T entity) {
        if (isEntityInList(insertList, entity)) {
            updateEntityInList(insertList, entity);
        } else {
            insertList.add(entity);
        }
    }

    /**
     * update an entity
     *
     * @param entity
     */
    public void updateEntity(T entity) {
        if (isEntityInList(insertList, entity)) {
            updateEntityInList(insertList, entity);
        } else {
            if (isEntityInList(updateList, entity)) {
                updateEntityInList(updateList, entity);
            } else {
                updateList.add(entity);
            }
        }
    }

    /**
     * @param entity
     */
    public void deleteEntity(T entity) {
        if (isEntityInList(insertList, entity)) {
            insertList.remove(entity);
        } else {
            if (isEntityInList(updateList, entity)) {
                updateList.remove(entity);
            }
            if (isEntityInList(deleteList, entity)) {
                updateEntityInList(deleteList, entity);
            } else {
                deleteList.add(entity);
            }
        }
    }

    /**
     * clear all data
     */
    public void clear() {
        insertList.clear();
        updateList.clear();
        deleteList.clear();
    }

    /**
     * search the list to find an existing entity
     *
     * @param list
     * @param entity
     * @return
     */
    protected T searchEntityInList(List<T> list, T entity) {
        T targetEntity = null;
        for (T existingEntity : list) {
            if (existingEntity.equals(entity)) {
                targetEntity = existingEntity;
                break;
            }
        }
        return targetEntity;
    }

    /**
     * Check whether entity already exists in list
     *
     * @param list
     * @param entity
     * @return
     */
    protected boolean isEntityInList(List<T> list, T entity) {
        T targetEntity = searchEntityInList(list, entity);
        boolean exists = (targetEntity != null);
        return exists;
    }

    /**
     * Add entity to List.
     *
     * @param entityList
     * @param entity
     */
    protected void updateEntityInList(List<T> entityList, T entity) {
        T existingEntity = searchEntityInList(entityList, entity);
        if (existingEntity == null) {
            entityList.add(entity);
        } else {
            entityList.remove(existingEntity);
            entityList.add(entity);
        }
    }

    /**
     * check the changed status of the data model.
     */
    public boolean isChanged() {
        return insertList.size() > 0 || updateList.size() > 0 || deleteList.size() > 0;
    }

    public List<T> getInsertList() {
        return insertList;
    }

    public List<T> getUpdateList() {
        return updateList;
    }

    public List<T> getDeleteList() {
        return deleteList;
    }

    private List<T> insertList = new ArrayList<T>();

    private List<T> updateList = new ArrayList<T>();

    private List<T> deleteList = new ArrayList<T>();

}
