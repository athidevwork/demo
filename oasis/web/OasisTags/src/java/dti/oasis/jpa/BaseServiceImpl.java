package dti.oasis.jpa;

import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   5/29/13
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/12/2015       kshen       Added method saveEntity and validateEntity.
 * 12/24/2015       kshen       168409. Enhance Services to restrict usage to a single Entity type.
 *                              Return NewEntityKeyMap for saving entity list.
 * ---------------------------------------------------
 */
public abstract class BaseServiceImpl<T> implements BaseService<T> {

    protected abstract BaseService<T> getBaseServiceDAOImpl();
    protected LoadByFilterService getLoadByFilterServiceImpl() { return getBaseServiceDAOImpl(); }

    /* ------------------------------------------------------------------------ */
    /* BaseService Methods                                                      */
    /* ------------------------------------------------------------------------ */

    @Override
    @Transactional(readOnly = true)
    public long countAll() {
        l.entering(getClass().getName(), "countAll");

        long count = getBaseServiceDAOImpl().countAll();

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "countAll", count);
        }
        return count;
    }

    @Override
    @Transactional(readOnly = true)
    public T loadByPk(long pk) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadByPk", new Object[]{pk});
        }

        T result = getBaseServiceDAOImpl().loadByPk(pk);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadByPk", result);
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> loadAll() {
        l.entering(getClass().getName(), "loadAll");

        List<T> result = getBaseServiceDAOImpl().loadAll();

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAll", result);
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> loadAll(String orderByField, boolean isAscending) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAll", new Object[]{orderByField, isAscending});
        }

        List<T> result =  getBaseServiceDAOImpl().loadAll(orderByField, isAscending);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAll", result);
        }
        return result;
    }
    @Override
    @Transactional(readOnly = true)
    public List<T> loadAll(List<HashMap> orderList) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAll", new Object[]{orderList});
        }

        List<T> result =  getBaseServiceDAOImpl().loadAll(orderList);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAll", result);
        }
        return result;
    }

    @Override
    @Transactional
    public void addEntity(List<T> entityList) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addEntity", new Object[]{entityList});
        }

        getBaseServiceDAOImpl().addEntity(entityList);

        l.exiting(getClass().getName(), "addEntity");
    }

    @Override
    @Transactional
    public T addEntity(T entity) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addEntity", new Object[]{entity});
        }

        T result = getBaseServiceDAOImpl().addEntity(entity);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addEntity", result);
        }
        return result;
    }

    @Override
    @Transactional
    public List<T> updateEntity(List<T> entityList) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "updateEntity", new Object[]{entityList});
        }

        List<T> result = getBaseServiceDAOImpl().updateEntity(entityList);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "updateEntity", result);
        }
        return result;
    }

    @Override
    @Transactional
    public T updateEntity(T entity) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "updateEntity", new Object[]{entity});
        }

        T result = getBaseServiceDAOImpl().updateEntity(entity);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "updateEntity", result);
        }
        return result;
    }

    @Override
    @Transactional
    public void deleteEntity(List<T> entityList) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "deleteEntity", new Object[]{entityList});
        }

        getBaseServiceDAOImpl().deleteEntity(entityList);

        l.exiting(getClass().getName(), "deleteEntity");
    }

    @Override
    @Transactional
    public void deleteEntity(T entity) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "deleteEntity", new Object[]{entity});
        }

        getBaseServiceDAOImpl().deleteEntity(entity);

        l.exiting(getClass().getName(), "deleteEntity");
    }

    /* ------------------------------------------------------------------------ */
    /* LoadByFilterService Methods                                              */
    /* ------------------------------------------------------------------------ */

    @Override
    @Transactional(readOnly = true)
    public long countAllByFilter(T filterEntity) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "countAllByFilter", new Object[]{filterEntity});
        }

        long result = countAllByFilter(filterEntity, c_defaultLoadByFilterProcessor);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "countAllByFilter", result);
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public long countAllByFilter(T filter, LoadByFilterProcessor loadByFilterProcessor) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "countAllByFilter", new Object[]{filter, loadByFilterProcessor});
        }

        long count = getLoadByFilterServiceImpl().countAllByFilter(filter, loadByFilterProcessor);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "countAllByFilter", count);
        }
        return count;
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> loadAllByFilter(T filterEntity) {
        return loadAllByFilter(filterEntity, c_defaultLoadByFilterProcessor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> loadAllByFilter(T filterEntity, LoadByFilterProcessor loadByFilterProcessor) {
        return loadAllByFilter(filterEntity, loadByFilterProcessor, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> loadAllByFilter(T filterEntity, List<HashMap> orderList) {
        return loadAllByFilter(filterEntity, c_defaultLoadByFilterProcessor, orderList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> loadAllByFilter(T filterEntity, LoadByFilterProcessor loadByFilterProcessor, List<HashMap> orderList) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllByFilter", new Object[]{filterEntity});
        }

        List<T> result = getLoadByFilterServiceImpl().loadAllByFilter(filterEntity, loadByFilterProcessor, orderList);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllByFilter", result);
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List loadOptionsByFilter(String[] resultColumns, T filterEntity) {
        return loadOptionsByFilter(resultColumns, filterEntity, null, c_defaultLoadByFilterProcessor);
    }

    @Override
    @Transactional(readOnly = true)
    public List loadOptionsByFilter(String[] resultColumns, T filterEntity, List<HashMap> orderList) {
        return loadOptionsByFilter(resultColumns, filterEntity, orderList, c_defaultLoadByFilterProcessor);
    }

    @Override
    @Transactional(readOnly = true)
    public List loadOptionsByFilter(String[] resultColumns, T filterEntity, LoadByFilterProcessor loadByFilterProcessor) {
        return loadOptionsByFilter(resultColumns, filterEntity, null, loadByFilterProcessor);
    }

    @Override
    @Transactional(readOnly = true)
    public List loadOptionsByFilter(String[] resultColumns, T filterEntity, List<HashMap> orderList, LoadByFilterProcessor loadByFilterProcessor) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadOptionsByFilter", new Object[]{resultColumns, filterEntity, orderList, loadByFilterProcessor});
        }

        List<String[]> result = getLoadByFilterServiceImpl().loadOptionsByFilter(resultColumns, filterEntity, orderList, loadByFilterProcessor);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadOptionsByFilter", result);
        }
        return result;
    }

    @Transactional
    public T saveEntity(T entity) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveEntity", new Object[]{entity});
        }

        T result = null;

        BaseEntity baseEntity = (BaseEntity) entity;

        if (baseEntity.isNew() || baseEntity.isModified()) {
            validateEntity(entity);
        }

        if (baseEntity.isNew()) {
            result = addEntity(entity);
        } else if (baseEntity.isModified()) {
            result = updateEntity(entity);
        } else {
            deleteEntity(entity);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveEntity", result);
        }
        return result;
    }

    @Transactional
    public List<NewEntityKeyMap> saveEntity(List<T> entityList) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveEntity", new Object[]{entityList});
        }

        List<T> newOrModifiedEntityList = new ArrayList<T>();
        List<T> newEntityList = new ArrayList<T>();
        List<T> modifiedEntityList = new ArrayList<T>();
        List<T> deletedEntityList = new ArrayList<T>();

        List<NewEntityKeyMap> newEntityKeyMapList = new ArrayList<NewEntityKeyMap>();

        for (T entity : entityList) {
            BaseEntity baseEntity = (BaseEntity) entity;

            if (baseEntity.isNew()) {
                newOrModifiedEntityList.add(entity);
                newEntityList.add(entity);
            } else if (baseEntity.isModified()) {
                newOrModifiedEntityList.add(entity);
                modifiedEntityList.add(entity);
            } else if (baseEntity.isDeleted()) {
                deletedEntityList.add(entity);
            }
        }

        if (deletedEntityList.size() > 0) {
            deleteEntity(deletedEntityList);
        }

        validateEntity(newOrModifiedEntityList);

        if (newEntityList.size() > 0) {
            // Initialize new entity key map.
            initNewEntityKeyMap(newEntityKeyMapList, newEntityList);

            // Save entity.
            addEntity(newEntityList);

            // Set the new entity key to the new entity key map.
            processNewEntityKey(newEntityKeyMapList);
        }

        if (modifiedEntityList.size() > 0) {
            updateEntity(modifiedEntityList);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveEntity", newEntityKeyMapList);
        }
        return newEntityKeyMapList;
    }

    @Transactional
    public void validateEntity(T entity) {
        // Do nothing so that a sub-class can implement only if required.
    }

    @Transactional
    public void validateEntity(List<T> entityList) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateEntity", new Object[]{entityList});
        }

        for (T entity : entityList) {
            validateEntity(entity);
        }

        l.exiting(getClass().getName(), "validateEntity");
    }

    /**
     * Initialize the new entity key map.
     * @param newEntityKeyMapList
     * @param entityList
     */
    private void initNewEntityKeyMap(List<NewEntityKeyMap> newEntityKeyMapList, List<T> entityList) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "initNewEntityKeyMap",
                    new Object[]{newEntityKeyMapList, entityList});
        }

        if (entityList != null && entityList.size() > 0) {
            Class entityClazz = entityList.get(0).getClass();

            if (hasGeneratedValueKeyField(entityClazz)) {
                Method keyGetter = getGeneratedValueKeyGetterMethod(entityClazz);
                Method keySetter = getGeneratedValueKeySetterMethod(entityClazz);

                for (T entity : entityList) {
                    // Initial the entity key map.
                    NewEntityKeyMap<T> newEntityKeyMap = new NewEntityKeyMap<T>();
                    newEntityKeyMap.setEntity(entity);

                    try {
                        // Set old entity key to entity map.
                        Long entityKey = (Long) keyGetter.invoke(entity);
                        newEntityKeyMap.setEntityKey(entityKey);

                        // Set the entity key to empty so the system will generate a new PK.
                        keySetter.invoke(entity, (Long) null);
                    } catch (Exception e) {
                        AppException ae = ExceptionHelper.getInstance().handleException(
                                "Unable to get/set entity key for the field: " + getGeneratedValueKeyField(entityClazz).getName() + " of the entity class: " + entityClazz.getName(), e);
                        l.throwing(getClass().getName(), "initNewEntityKeyMap", ae);
                        throw ae;
                    }

                    // Add to new entity key map list.
                    newEntityKeyMapList.add(newEntityKeyMap);
                }
            }
        }

        l.exiting(getClass().getName(), "initNewEntityKeyMap");
    }

    /**
     * Process new entity
     * @param newEntityKeyMapList
     */
    private void processNewEntityKey(List<NewEntityKeyMap> newEntityKeyMapList) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processNewEntityKey", new Object[]{newEntityKeyMapList});
        }

        if (newEntityKeyMapList != null && newEntityKeyMapList.size() > 0) {
            // Get the key field
            Class entityClazz = newEntityKeyMapList.get(0).getEntity().getClass();

            if (hasGeneratedValueKeyField(entityClazz)) {
                Method keyGetter = getGeneratedValueKeyGetterMethod(entityClazz);

                for (NewEntityKeyMap newEntityKeyMap: newEntityKeyMapList) {
                    try {
                        newEntityKeyMap.setNewEntityKey((Long) keyGetter.invoke(newEntityKeyMap.getEntity()));
                    } catch (Exception e) {
                        AppException ae = ExceptionHelper.getInstance().handleException("Unable to initial new entity pk map.", e);
                        l.throwing(getClass().getName(), "initNewEntityKeyMap", ae);
                        throw ae;
                    }
                }
            }
        }

        l.exiting(getClass().getName(), "processNewEntityKey");
    }

    /**
     * Get generated-value key field by entity class.
     * @param clazz
     * @return
     */
    private Field getGeneratedValueKeyField(Class<T> clazz) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getGeneratedValueKeyField", new Object[]{clazz});
        }

        if (entityGeneratedValueKeyField == null) {
            synchronized (this) {
                if (entityGeneratedValueKeyField == null) {
                    // Get the generated-value key field if it is not cached.
                    for (Field field : clazz.getDeclaredFields()) {
                        Id id = field.getAnnotation(Id.class);
                        GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);

                        if (id != null && generatedValue != null) {
                            entityGeneratedValueKeyField = field;
                            break;
                        }
                    }

                    // If the generated-value key field is not found, set the cached field to be dummy field.
                    // so we will not try to find the generated-value key field in the next time.
                    if (entityGeneratedValueKeyField == null) {
                        entityGeneratedValueKeyField = DummyEntity.getDummyField();
                    }
                }
            }
        }

        // Get the generated-value key field from cache.
        Field keyField = (entityGeneratedValueKeyField.equals(DummyEntity.getDummyField())) ? null : entityGeneratedValueKeyField;

        // Add log if key field is not found.
        if (keyField == null) {
            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(), "getGeneratedValueKeyField",
                        "Entity: " + clazz.getName() + " doesn't have a generated value ID field.");
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getGeneratedValueKeyField", keyField);
        }

        return keyField;
    }

    /**
     * Check if an entity class has generated-value key field.
     * @param clazz
     * @return
     */
    private boolean hasGeneratedValueKeyField(Class<T> clazz) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "hasGeneratedValueKeyField", new Object[]{clazz});
        }

        boolean hasKeyField = (getGeneratedValueKeyField(clazz) != null);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "hasGeneratedValueKeyField", hasKeyField);
        }
        return hasKeyField;
    }

    /**
     * Get the getter method for the generated-value key field of an entity class
     * @param clazz
     * @return
     */
    private Method getGeneratedValueKeyGetterMethod(Class<T> clazz) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getGeneratedValueKeyGetterMethod", new Object[]{clazz});
        }

        // Get the key field for entity class.
        Field keyField = getGeneratedValueKeyField(clazz);

        if (keyField != null) {
            if (entityGeneratedValueKeyFieldGetter == null) {
                synchronized (this) {
                    if (entityGeneratedValueKeyFieldGetter == null) {
                        // Get the generated-value getter method if it is not cached.
                        try {
                            entityGeneratedValueKeyFieldGetter = new PropertyDescriptor(keyField.getName(), clazz).getReadMethod();
                        } catch (IntrospectionException e) {
                            AppException ae = ExceptionHelper.getInstance().handleException(
                                    "Unable to get the key getter method for the key field: " + keyField.getName() + " of the entity class: " + clazz.getName() + ".", e);
                            l.throwing(getClass().getName(), "getGeneratedValueKeyGetterMethod", ae);
                            throw ae;
                        }

                        // If the generated-value key getter method is not found, set the cached field to be dummy method.
                        // So we will not try to find the generated-value key getter method in the next time.
                        if (entityGeneratedValueKeyFieldGetter == null) {
                            entityGeneratedValueKeyFieldGetter = DummyEntity.getDummyMethod();
                        }
                    }
                }
            }
        }

        // Get the generated-value key getter from cache.
        Method getterMethod = (entityGeneratedValueKeyFieldGetter.equals(DummyEntity.getDummyMethod())) ? null : entityGeneratedValueKeyFieldGetter;

        // Throw error in the getter method is not found.
        if (getterMethod == null) {
            AppException ae = new AppException(AppException.UNEXPECTED_ERROR,
                    "Unable to get the key getter method for the key field: " + keyField.getName() + " of the entity class: " + clazz.getName() + ".");

            l.throwing(getClass().getName(), "getGeneratedValueKeyGetterMethod", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getGeneratedValueKeyGetterMethod", getterMethod);
        }

        return getterMethod;
    }

    /**
     * Get the setter method for the generated-value field of entity class.
     * @param clazz
     * @return
     */
    private Method getGeneratedValueKeySetterMethod(Class<T> clazz) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getGeneratedValueKeySetterMethod", new Object[]{clazz});
        }

        // Get the key field for entity class.
        Field keyField = getGeneratedValueKeyField(clazz);

        if (keyField != null) {
            if (entityGeneratedValueKeyFieldSetter == null) {
                synchronized (this) {
                    if (entityGeneratedValueKeyFieldSetter == null) {
                        // Get the generated-value getter method if it is not cached.
                        try {
                            entityGeneratedValueKeyFieldSetter = new PropertyDescriptor(keyField.getName(), clazz).getWriteMethod();
                        } catch (IntrospectionException e) {
                            AppException ae = ExceptionHelper.getInstance().handleException(
                                    "Unable to get the key setter method for the key field: " + keyField.getName() + " of the entity class: " + clazz.getName() + ".", e);
                            l.throwing(getClass().getName(), "getGeneratedValueKeySetterMethod", ae);
                            throw ae;
                        }

                        // If the generated-value key setter method is not found, set the cached field to be dummy method.
                        // So we will not try to find the generated-value key setter method in the next time.
                        if (entityGeneratedValueKeyFieldSetter == null) {
                            entityGeneratedValueKeyFieldSetter = DummyEntity.getDummyMethod();
                        }
                    }
                }
            }
        }

        // Get the generated-value key getter from cache.
        Method setterMethod = (entityGeneratedValueKeyFieldSetter.equals(DummyEntity.getDummyMethod())) ? null : entityGeneratedValueKeyFieldSetter;

        // Throw error in the setter method is not found.
        if (setterMethod == null) {
            AppException ae = new AppException(AppException.UNEXPECTED_ERROR,
                    "Unable to get the key setter method for the key field: " + keyField.getName() + " of the entity class: " + clazz.getName() + ".");

            l.throwing(getClass().getName(), "getGeneratedValueKeySetterMethod", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getGeneratedValueKeySetterMethod", setterMethod);
        }
        return setterMethod;
    }

    private static class DummyEntity {
        private static Field DUMMY_FIELD_VALUE;
        private static Method DUMMY_METHOD_VALUE;

        private Object dummyField;

        private DummyEntity() {
        }

        public void dummyMethod() {
        }

        public static Field getDummyField() {
            c_l.entering(BaseServiceImpl.class.getName(), "getDummyField");

            if (DUMMY_FIELD_VALUE == null) {
                synchronized (DummyEntity.class) {
                    if (DUMMY_FIELD_VALUE == null) {
                        try {
                            DUMMY_FIELD_VALUE = DummyEntity.class.getDeclaredField("dummyField");
                        } catch (NoSuchFieldException e) {
                            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get dummy field.", e);
                            c_l.throwing(DummyEntity.class.getName(), "getDummyField", ae);
                            throw ae;
                        }
                    }
                }
            }

            if (c_l.isLoggable(Level.FINER)) {
                c_l.exiting(DummyEntity.class.getName(), "getDummyField", DUMMY_FIELD_VALUE);
            }

            return DUMMY_FIELD_VALUE;
        }

        public static Method getDummyMethod() {
            c_l.entering(BaseServiceImpl.class.getName(), "getDummyMethod");

            if (DUMMY_METHOD_VALUE == null) {
                synchronized (DummyEntity.class) {
                    if (DUMMY_METHOD_VALUE == null) {
                        try {
                            DUMMY_METHOD_VALUE = DummyEntity.class.getMethod("dummyMethod");
                        } catch (NoSuchMethodException e) {
                            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get dummy method.", e);
                            c_l.throwing(DummyEntity.class.getName(), "getDummyMethod", ae);
                            throw ae;
                        }
                    }
                }
            }

            if (c_l.isLoggable(Level.FINER)) {
                c_l.exiting(DummyEntity.class.getName(), "getDummyMethod", DUMMY_METHOD_VALUE);
            }

            return DUMMY_METHOD_VALUE;
        }
    }

    private Field entityGeneratedValueKeyField;
    private Method entityGeneratedValueKeyFieldGetter;
    private Method entityGeneratedValueKeyFieldSetter;
    private final Logger l = LogUtils.getLogger(getClass());

    static final DefaultLoadByFilterProcessor c_defaultLoadByFilterProcessor = new DefaultLoadByFilterProcessor();
    private static final Logger c_l = LogUtils.getLogger(Record.class);
}
