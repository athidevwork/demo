package dti.oasis.jpa;

import dti.oasis.app.AppException;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import org.springframework.stereotype.Service;
import javax.persistence.EmbeddedId;
import javax.persistence.EntityManager;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   12/20/12
 *
 * @author jxgu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/12/2015       kshen       Added base method saveEntity and validateEntity.
 * 12/24/2015       kshen       168409. Enhance Services to restrict usage to a single Entity type.
 * 02/03/2016       kshen       169213
 * ---------------------------------------------------
 */
@Service("baseService")
public abstract class BaseJPADAO<T> implements BaseDAO<T> {

    protected abstract EntityManager getEntityManager();
    protected abstract Class<T> getEntityClass();

    /* ------------------------------------------------------------------------ */
    /* BaseService Methods                                                      */
    /* ------------------------------------------------------------------------ */

    @Override
    public long countAll() {
        return countAll(getEntityManager(), this.getEntityClass());
    }

    @Override
    public T loadByPk(long pk) {
        return (T) loadByPk(getEntityManager(), getEntityClass(), pk);
    }

    @Override
    public List<T> loadAll() {
        return (List<T>) loadAll(getEntityManager(), getEntityClass());
    }

    @Override
    public List<T> loadAll(String orderByField, boolean isAscending) {
        return (List<T>) loadAll(getEntityManager(), getEntityClass(), orderByField, isAscending);
    }

    @Override
    public List<T> loadAll(List<HashMap>  orderList) {
        return (List<T>) loadAll(getEntityManager(), getEntityClass(), orderList);
    }

    @Override
    public void addEntity(List<T> entityList) {
        addEntity(getEntityManager(), entityList);
    }

    /**
     * Save an entity.
     * <P>This method is unsupported in the <code>DAO</code> classes.</P>
     *
     * @param entity
     * @return
     */
    public T saveEntity(T entity) {
        throw new AppException("Unsupported method.");
    }

    /**
     * Save entities.
     * <P>This method is unsupported in the <code>DAO</code> classes.</P>
     *
     * @param entityList
     */
    public List<NewEntityKeyMap> saveEntity(List<T> entityList) {
        throw new AppException("Unsupported method.");
    }

    /**
     * Validate an entity.
     * <P>This method is unsupported in the <code>DAO</code> classes.</P>
     *
     * @param entity
     */
    public void validateEntity(T entity) {
        throw new AppException("Unsupported method.");
    }

    /**
     * Validate entities.
     * <P>This method is unsupported in the <code>DAO</code> classes.</P>
     *
     * @param entityList
     */
    public void validateEntity(List<T> entityList) {
        throw new AppException("Unsupported method.");
    }

    @Override
    public T addEntity(T entity) {
        return addEntity(getEntityManager(), entity);
    }

    @Override
    public List<T> updateEntity(List<T> entityList) {
        return updateEntity(getEntityManager(), entityList);
    }

    @Override
    public T updateEntity(T entity) {
        return updateEntity(getEntityManager(), entity);
    }

    @Override
    public void deleteEntity(List<T> entityList) {
        deleteEntity(getEntityManager(), entityList);
    }

    @Override
    public void deleteEntity(T entity) {
        deleteEntity(getEntityManager(), entity);
    }

    /* ------------------------------------------------------------------------ */
    /* BaseDAO Methods                                                          */
    /* ------------------------------------------------------------------------ */
    @Override
    public long countAll(EntityManager entityManager, Class<T> entityClass) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "countAll", new Object[]{});
        }
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<T> root = criteriaQuery.from(entityClass);
        criteriaQuery.select(criteriaBuilder.count(root));

        TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);

        Long count = typedQuery.getSingleResult();

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "countAll", count);
        }
        return count.longValue();
    }

    @Override
    public T loadByPk(EntityManager entityManager, Class<T> entityClass, long pk) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadByPk", new Object[]{entityManager, entityClass, pk});
        }

        T entity = entityManager.find(entityClass, pk);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadByPk", entity);
        }
        return entity;
    }

    /**
     * load all
     * @param entityManager
     * @return
     */
    public List<T> loadAll(EntityManager entityManager, Class<T> entityClass) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadEntityByFilter", new Object[]{entityManager,entityClass});
        }

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);

        Root<T> root = criteriaQuery.from(entityClass);
        criteriaQuery.where();

        TypedQuery<T> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setHint("eclipselink.jdbc.fetch-size", getFetchSize());
        List<T> result = typedQuery.getResultList();

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadEntityByFilter", result);
        }
        return result;
    }

    /**
     * load all
     * @param entityManager
     * @return
     */
    public List<T> loadAll(EntityManager entityManager, Class<T> entityClass, String orderByField, boolean isAscending) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadEntityByFilter", new Object[]{entityManager,entityClass});
        }
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        Root<T> root = criteriaQuery.from(entityClass);
        criteriaQuery.where();

        Path path = getFieldPath(root, orderByField);

        Expression expression = (String.class.equals(path.getJavaType())) ? criteriaBuilder.lower(path) : path;
        Order order = isAscending ?  criteriaBuilder.asc(expression) : criteriaBuilder.desc(expression);

        List<Order> orderList = new ArrayList<Order>();
        orderList.add(order);
        criteriaQuery.orderBy(orderList);

        TypedQuery<T> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setHint("eclipselink.jdbc.fetch-size", getFetchSize());
        List<T> result = typedQuery.getResultList();

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadEntityByFilter", result);
        }
        return result;
    }
    /**
     * load all
     * @param entityManager
     * @return
     */
    public List<T> loadAll(EntityManager entityManager, Class<T> entityClass, List orderByList) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadEntityByFilter", new Object[]{entityManager,entityClass});
        }
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        Root<T> root = criteriaQuery.from(entityClass);
        criteriaQuery.where();

        List<Order> orderResultList = generateOrderBy(criteriaBuilder, root, orderByList);
        if (orderResultList.size() > 0) {
            criteriaQuery.orderBy(orderResultList);
        }

        TypedQuery<T> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setHint("eclipselink.jdbc.fetch-size", getFetchSize());
        List<T> result = typedQuery.getResultList();

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadEntityByFilter", result);
        }
        return result;
    }

    /**
     * add entities
     * @param entityManager
     * @param entityList
     */
    public void addEntity(EntityManager entityManager, List<T> entityList) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addEntity", new Object[]{entityManager, entityList});
        }

        for (T entity : entityList) {
            entityManager.persist(entity);
        }
        entityManager.flush();
        for (T entity : entityList) {
            entityManager.refresh(entity);
        }

        l.exiting(getClass().getName(), "addEntity");
    }

    /**
     * add entity
     * @param entityManager
     * @param entity
     */
    public T addEntity(EntityManager entityManager, T entity){
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addEntity", new Object[]{entityManager, entity});
        }

        entityManager.persist(entity);

        entityManager.flush();

        entityManager.refresh(entity);

        l.exiting(getClass().getName(), "addEntity");

        return entity;
    }

    /**
     * update entities
     * @param entityManager
     * @param entityList
     */
    public List<T> updateEntity(EntityManager entityManager, List<T> entityList) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "updateEntity", new Object[]{entityManager, entityList});
        }

        List<T> returnList = new ArrayList<T>();
        for (T entity : entityList) {
            T managedEntity = entityManager.merge(entity);
            returnList.add(managedEntity);
        }
        entityManager.flush();
        for (T entity : returnList) {
            entityManager.refresh(entity);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "updateEntity", returnList);
        }

        return returnList;
    }

    /**
     * update entity
     * @param entityManager
     * @param entity
     */
    public T updateEntity(EntityManager entityManager, T entity) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "updateEntity", new Object[]{entityManager, entity});
        }

        entityManager.merge(entity);

        entityManager.flush();

        entityManager.refresh(entityManager.merge(entity));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "updateEntity", entity);
        }

        return entity;
    }

    /**
     * delete entities
     * @param entityManager
     * @param entityList
     */
    public void deleteEntity(EntityManager entityManager, List<T> entityList) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "deleteEntity", new Object[]{entityManager, entityList});
        }

        for (T entity : entityList) {
            T managedEntity = entityManager.merge(entity);
            entityManager.remove(managedEntity);
        }
        entityManager.flush();

        l.exiting(getClass().getName(), "deleteEntity");
    }

    /**
     * delete entity
     * @param entityManager
     * @param entity
     */
    public void deleteEntity(EntityManager entityManager, T entity) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "deleteEntity", new Object[]{entityManager, entity});
        }

        entityManager.remove(entityManager.merge(entity));

        l.exiting(getClass().getName(), "deleteEntity");
    }

    /* ------------------------------------------------------------------------ */
    /* LoadByFilterService Methods                                              */
    /* ------------------------------------------------------------------------ */

    /**
     * Load all entities based on a filter.
     *
     * @param filterEntity
     * @return
     */
    public List loadOptionsByFilter(String[] resultColumns, T filterEntity) {
        return loadOptionsByFilter(resultColumns, filterEntity, null, c_defaultLoadByFilterProcessor);
    }

    /**
     * Load all entities based on a filter.
     *
     * @param filterEntity
     * @return
     */
    public List loadOptionsByFilter(String[] resultColumns, T filterEntity, List<HashMap> orderList) {
        return loadOptionsByFilter(resultColumns, filterEntity, orderList, c_defaultLoadByFilterProcessor);
    }

    /**
     * Load all entities based on a filter.
     *
     * @param filterEntity
     * @return
     */
    public List loadOptionsByFilter(String[] resultColumns, T filterEntity, LoadByFilterProcessor loadByFilterProcessor) {
        return loadOptionsByFilter(resultColumns, filterEntity, null, loadByFilterProcessor);
    }

    /**
     * Load all entities based on a filter.
     *
     * @param filterEntity
     * @return
     */
    public List loadOptionsByFilter(String[] resultColumns, T filterEntity, List<HashMap> orderList, LoadByFilterProcessor loadByFilterProcessor) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadOptionsByFilter", new Object[]{resultColumns,filterEntity,orderList,loadByFilterProcessor});
        }

        EntityManager entityManager = getEntityManager();
        Class<T> entityClass = (Class<T>) filterEntity.getClass();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery(entityClass).distinct(true);
        Root<T> root = criteriaQuery.from(entityClass);

        List<Order> orderResultList = generateOrderBy(criteriaBuilder, root, orderList);
        if (orderResultList.size() > 0) {
            criteriaQuery.orderBy(orderResultList);
        }
        if (resultColumns != null && resultColumns.length > 0) {
            Path[] p = generateSubReturnResult(root, resultColumns);
            criteriaQuery.select(criteriaBuilder.array(p));
        }

        processPredicate(filterEntity, root, criteriaBuilder, criteriaQuery, loadByFilterProcessor);

        TypedQuery<T> typedQuery = entityManager.createQuery(criteriaQuery);
        loadByFilterProcessor.postProcessTypedQuery(filterEntity, root, typedQuery, criteriaBuilder, entityManager);
        typedQuery.setHint("eclipselink.jdbc.fetch-size", getFetchSize());
        List result = typedQuery.getResultList();

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadOptionsByFilter", result);
        }
        return result;
    }

    /**
     * filter entity based on filter
     *
     * @param filterEntity
     * @return
     */
    public long countAllByFilter(T filterEntity) {
        return countAllByFilter(filterEntity, c_defaultLoadByFilterProcessor);
    }

    /**
     * Count the number of entities based on filter, applying the loadByFilterProcessor during the process.
     *
     * @param filterEntity
     * @param loadByFilterProcessor
     * @return
     */
    public long countAllByFilter(T filterEntity, LoadByFilterProcessor loadByFilterProcessor) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "countAllByFilter", new Object[]{filterEntity});
        }
        EntityManager entityManager = getEntityManager();

        Class<T> entityClass = (Class<T>) filterEntity.getClass();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<T> root = criteriaQuery.from(entityClass);
        criteriaQuery.select(criteriaBuilder.count(root));

        processPredicate(filterEntity, root, criteriaBuilder, criteriaQuery, loadByFilterProcessor);

        TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
        loadByFilterProcessor.postProcessTypedQuery(filterEntity, root, typedQuery, criteriaBuilder, entityManager);

        Long count = typedQuery.getSingleResult();

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "countAllByFilter", count);
        }
        return count.longValue();
    }

    /**
     * Load all entities based on a filter.
     *
     * @param filterEntity
     * @return
     */
    public List<T> loadAllByFilter(T filterEntity) {
        return loadAllByFilter(filterEntity, c_defaultLoadByFilterProcessor);
    }

    /**
     * Load all entities based on a filter, applying the loadByFilterProcessor during the process.
     *
     * @param filterEntity
     * @param loadByFilterProcessor
     * @return
     */
    public List<T> loadAllByFilter(T filterEntity, LoadByFilterProcessor loadByFilterProcessor) {
        return this.loadAllByFilter(filterEntity, loadByFilterProcessor, null);
    }

    /**
     * Load all entities based on a filter, applying the loadByFilterProcessor during the process.
     *
     * @param filterEntity
     * @param orderList
     * @return
     */
    public List<T> loadAllByFilter(T filterEntity, List<HashMap> orderList) {
        return this.loadAllByFilter(filterEntity, c_defaultLoadByFilterProcessor, orderList);
    }

    /**
     * Load all entities based on a filter, applying the loadByFilterProcessor during the process.
     *
     * @param filterEntity
     * @param loadByFilterProcessor
     * @return
     */
    public List<T> loadAllByFilter(T filterEntity, LoadByFilterProcessor loadByFilterProcessor, List<HashMap>  orderList) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllByFilter", new Object[]{filterEntity});
        }

        EntityManager entityManager = getEntityManager();
        Class<T> entityClass = (Class<T>) filterEntity.getClass();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass).distinct(true);

        Root<T> root = criteriaQuery.from(entityClass);
        List<Order> orderResultList = generateOrderBy(criteriaBuilder, root, orderList);
        if (orderResultList.size() > 0) {
            criteriaQuery.orderBy(orderResultList);
        }


        processPredicate(filterEntity, root, criteriaBuilder, criteriaQuery, loadByFilterProcessor);

        TypedQuery<T> typedQuery = entityManager.createQuery(criteriaQuery);
        loadByFilterProcessor.postProcessTypedQuery(filterEntity, root, typedQuery, criteriaBuilder, entityManager);

        typedQuery.setHint("eclipselink.jdbc.fetch-size", getFetchSize());
        List<T> result = typedQuery.getResultList();

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllByFilter", result);
        }
        return result;
    }

    /**
     * Prepare the order by logic when generate the sql.
     *
     * @param criteriaBuilder
     * @param root
     * @param orderList
     * @return
     */
    private List<Order> generateOrderBy(CriteriaBuilder criteriaBuilder, Root<T> root, List<HashMap> orderList) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "generateOrderBy", new Object[]{criteriaBuilder, root, orderList});
        }

        List<Order> orderResultList = new ArrayList<Order>();
        if (orderList != null && orderList.size() > 0) {
            for (HashMap orderByMap : orderList) {
                String orderByColumn = orderByMap.get("orderByColumn").toString();
                Path path = null;
                while (orderByColumn.indexOf(".") > 0) {
                    int index = orderByColumn.indexOf(".");
                    if (path == null) {
                        path = root.get(orderByColumn.substring(0, index));
                    } else {
                        path = path.get(orderByColumn.substring(0, index));
                    }
                    orderByColumn = orderByColumn.substring(index + 1);
                }
                if (path == null) {
                    path = root.get(orderByColumn);
                } else {
                    path = path.get(orderByColumn);
                }
                String orderByType = orderByMap.get("orderByType").toString();

                Expression expression = (String.class.equals(path.getJavaType())) ? criteriaBuilder.lower(path) : path;
                Order order = ("Desc".equalsIgnoreCase(orderByType)) ? criteriaBuilder.desc(expression) : criteriaBuilder.asc(expression);
                orderResultList.add(order);
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "generateOrderBy", orderResultList);
        }
        return orderResultList;
    }

    /**
     * Prepare the order by logic when generate the sql.
     *
     * @param root
     * @param resultColumns
     * @return
     */
    private Path[] generateSubReturnResult(Root<T> root, String[] resultColumns) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "generateSubReturnResult", new Object[]{root, resultColumns});
        }

        Path[] p = new Path[resultColumns.length];

        if (resultColumns != null && resultColumns.length > 0) {

            for (int i = 0; i < resultColumns.length; i++) {
                Path path = null;
                String returnColumn = resultColumns[i];
                while (returnColumn.indexOf(".") > 0) {
                    int index = returnColumn.indexOf(".");
                    if (path == null) {
                        path = root.get(returnColumn.substring(0, index));
                    } else {
                        path = path.get(returnColumn.substring(0, index));
                    }
                    returnColumn = returnColumn.substring(index + 1);
                }

                if (path == null) {
                    path = root.get(returnColumn);
                } else {
                    path = path.get(returnColumn);
                }
                p[i] = path;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "generateSubReturnResult", p);
        }
        return p;
    }

    /**
     * common logic for predicates
     * @param filterEntity
     * @param root
     * @param criteriaBuilder
     * @param criteriaQuery
     * @param loadByFilterProcessor
     */
    private void processPredicate(T filterEntity, Root<T> root, CriteriaBuilder criteriaBuilder,
                                  CriteriaQuery criteriaQuery, LoadByFilterProcessor loadByFilterProcessor) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processPredicate", new Object[]{filterEntity, root, criteriaBuilder,
                    criteriaQuery, loadByFilterProcessor});
        }

        Class<T> entityClass = (Class<T>) filterEntity.getClass();

        List<Predicate> predicateList = new ArrayList<Predicate>();

        for (Field field : entityClass.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())){
                continue;
            }
            boolean isTransient = false;
            Transient transientAnnotation = field.getAnnotation(Transient.class);
            if (transientAnnotation != null) {
                isTransient = true;
            }
            EmbeddedId embeddedId = field.getAnnotation(EmbeddedId.class);
            if (isTransient) {
                if (field.getName().equals("searchCriteria")) {
                    SearchCriteria searchCriteria = (SearchCriteria) getFieldValue(field, filterEntity);
                    if (searchCriteria != null && !searchCriteria.isEmpty()) {
                        List<Predicate> searchCriteriaPredicateList = new ArrayList<Predicate>();
                        for (Field searchField : searchCriteria.getClass().getDeclaredFields()) {
                            EmbeddedId searchFieldEmbeddedId = searchField.getAnnotation(EmbeddedId.class);
                            if (searchFieldEmbeddedId == null) {
                                searchCriteriaPredicateList = generateSearchCriteriaPredicate(searchField, searchCriteria, root, criteriaBuilder, criteriaQuery, loadByFilterProcessor);
                                if (searchCriteriaPredicateList.size() > 0) {
                                    predicateList.addAll(searchCriteriaPredicateList);
                                }
                            }
                        }
                    }
                } else {
                    Predicate predicate = generateSubQueryPredicate(field, filterEntity, root, criteriaBuilder, criteriaQuery, loadByFilterProcessor);
                    predicate = loadByFilterProcessor.postProcessFieldPredicate(predicate, field, root, criteriaBuilder, getEntityManager());
                    if (predicate != null) {
                        predicateList.add(predicate);
                    }
                }
            } else {
                if (embeddedId == null) {
                    Predicate predicate = generatePredicate(field, filterEntity, root, criteriaBuilder, loadByFilterProcessor);
                    if(predicate == null){
                        predicate = generateSubQueryPredicate(field, filterEntity, root, criteriaBuilder, criteriaQuery, loadByFilterProcessor);
                    }
                    predicate = loadByFilterProcessor.postProcessFieldPredicate(predicate, field, root, criteriaBuilder, getEntityManager());
                    if (predicate != null) {
                        predicateList.add(predicate);
                    }
                } else {
                    Path path = root.get(field.getName());
                    Object filterValue = getFieldValue(field, filterEntity);
                    List<Predicate> list = processEmbeddedId(field, filterValue, path, criteriaBuilder, loadByFilterProcessor);
                    predicateList.addAll(list);
                }
            }
        }
        loadByFilterProcessor.postProcessPredicateList(predicateList, filterEntity, root, criteriaBuilder, getEntityManager());

        if (predicateList.size() > 0) {
            Predicate combinePredicate = predicateList.get(0);
            for (int i = 1; i < predicateList.size(); i++) {
                combinePredicate = criteriaBuilder.and(combinePredicate, predicateList.get(i));
            }
            criteriaQuery.where(combinePredicate);
        } else {
            criteriaQuery.where();
        }
        loadByFilterProcessor.postProcessCriteriaQuery(filterEntity, root, criteriaQuery, criteriaBuilder, getEntityManager());
        l.exiting(getClass().getName(), "processPredicate");
    }


    /**
     * process EmbeddedId
     * @param field
     * @param filterObject
     * @param path
     * @param criteriaBuilder
     * @param loadByFilterProcessor
     * @return
     */
    private <V> List<Predicate> processEmbeddedId(Field field, V filterObject, Path path, CriteriaBuilder criteriaBuilder,
                                        LoadByFilterProcessor loadByFilterProcessor) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processEmbeddedId", new Object[]{field, filterObject, path, criteriaBuilder});
        }
        List<Predicate> predicateList = new ArrayList<Predicate>();
        if (filterObject != null) {
            Class embeddedIdClass = field.getType();
            for (Field embeddedObjectField : embeddedIdClass.getDeclaredFields()) {
                Predicate predicate = generatePredicate(embeddedObjectField, filterObject, path, criteriaBuilder, loadByFilterProcessor);
                predicate = loadByFilterProcessor.postProcessFieldPredicate(predicate, field, path, criteriaBuilder, getEntityManager());
                if (predicate != null) {
                    predicateList.add(predicate);
                }
            }
        }
        l.exiting(getClass().getName(), "processEmbeddedId", predicateList);
        return predicateList;
    }

    /**
     * generate predicate
     * @param field
     * @param filterObject
     * @param root
     * @param criteriaBuilder
     * @return
     */
    private Predicate generateSubQueryPredicate(Field field, T filterObject,
                                                Root root, CriteriaBuilder criteriaBuilder, CriteriaQuery criteriaQuery,
                                                LoadByFilterProcessor loadByFilterProcessor) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "generatePredicate", new Object[]{field, filterObject, root,
                    criteriaBuilder, loadByFilterProcessor});
        }

        Predicate predicate = null;
        Filter filter = field.getAnnotation(Filter.class);
        Object fieldValue = getFieldValue(field, filterObject);
        if (filter != null && fieldValue != null) {
            try {
                String sourceColumn = filter.sourceColumn()[0];
                String foreignColumn = filter.foreignColumn();
                String returnColumn = StringUtils.isBlank(filter.returnColumn()) ? sourceColumn : filter.returnColumn();
                Class[] foreignTable = filter.foreignTable();
                Filter.Type filterType = filter.subType() == null ? filter.type():filter.subType();
                if (Filter.Type.IN.equals(filterType) || Filter.Type.NOTIN.equals(filterType)) {
                        Subquery subquery = criteriaQuery.subquery(foreignTable[0]);
                        Root subRoot = subquery.from(foreignTable[0]);
                        subquery.select(subRoot.get(returnColumn));
                        Predicate subPredicate = generatePredicate(field, filterObject, subRoot, criteriaBuilder, loadByFilterProcessor, true);
                        subquery.where(subPredicate);
                        criteriaQuery.select(root);
                        predicate = criteriaBuilder.in(root.get(sourceColumn)).value(subquery);
                } else {
                    Join join = null;
                    if (fieldValue instanceof Collection) {
                        join = root.join(root.getModel().getList(field.getName()), JoinType.RIGHT);
                    } else {
                        join = root.join(root.getModel().getSingularAttribute(field.getName()), JoinType.LEFT);
                    }
                    predicate = generatePredicate(field, filterObject, join.get(foreignColumn).getParentPath(), criteriaBuilder, loadByFilterProcessor, true,foreignColumn);

                }
            } catch (Exception e) {
                predicate = null;
            }
        }
        return predicate;
    }  /**
     * generate predicate
     * @param field
     * @param searchCriteria
     * @param parentRoot
     * @param criteriaBuilder
     * @return
     */
    private List<Predicate> generateSearchCriteriaPredicate(Field field, SearchCriteria searchCriteria,
                                                            Root parentRoot, CriteriaBuilder criteriaBuilder, CriteriaQuery criteriaQuery,
                                                            LoadByFilterProcessor loadByFilterProcessor) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "generatePredicate", new Object[]{field, searchCriteria, parentRoot,
                    criteriaBuilder, loadByFilterProcessor});
        }

        List<Predicate> predicateList = new ArrayList<Predicate>();
        Predicate predicate = null;
        Filter filter = field.getAnnotation(Filter.class);
        Object fieldValue = getFieldValue(field, searchCriteria);
        if (filter != null && fieldValue != null) {
            try {
                Class[] foreignTableArray = filter.foreignTable();
                Filter.Type filterType = filter.subType() == null ? filter.type() : filter.subType();
                String[] sourceColumn = filter.sourceColumn();
                String foreignColumn = filter.foreignColumn();
                if (Filter.Type.IN.equals(filterType) || Filter.Type.NOTIN.equals(filterType)) {
                    for (int i = 0; i < foreignTableArray.length; i++) {
                        String returnColumn = StringUtils.isBlank(filter.returnColumn()) ? sourceColumn[i] : filter.returnColumn();
                        Class foreignTable = foreignTableArray[i];
                        Subquery subquery = criteriaQuery.subquery(foreignTable);
                        Root subRoot = subquery.from(foreignTable);
                        subquery.select(subRoot.get(returnColumn));
                        if (i == foreignTableArray.length - 1) {
                            Predicate subPredicate = generatePredicate(field, searchCriteria, subRoot, criteriaBuilder, loadByFilterProcessor, true);
                            subquery.where(subPredicate);
                        }
                        criteriaQuery.select(parentRoot);
                        Predicate result = criteriaBuilder.in(parentRoot.get(sourceColumn[i])).value(subquery);
                        if (predicate == null) {
                            predicate = result;
                        } else {
                            predicate.in(result.getExpressions());
                        }
                        parentRoot = subRoot;
                    }
                } else {
                    Join join;
                    if (fieldValue instanceof Collection) {
                        join = parentRoot.join(parentRoot.getModel().getList(sourceColumn[0]), JoinType.LEFT);
                    } else {
                        join = parentRoot.join(parentRoot.getModel().getSingularAttribute(sourceColumn[0]), JoinType.LEFT);
                    }
                    if (foreignTableArray.length >= 1 && foreignTableArray[0] != String.class) {
                        for (int i = 0; i < foreignTableArray.length; i++) {
                            Class foreignTable = foreignTableArray[i];
                            CriteriaQuery<T> joinQuery = criteriaBuilder.createQuery(foreignTable).distinct(true);
                            Root<T> joinRoot = criteriaQuery.from(foreignTable);
                            if (fieldValue instanceof Collection) {
                                join.join(joinRoot.getModel().getList(sourceColumn[i + 1]), JoinType.LEFT);
                            } else {
                                join.join(joinRoot.getModel().getSingularAttribute(sourceColumn[i + 1]), JoinType.LEFT);
                            }
                        }
                    }
                    predicate = generatePredicate(field, searchCriteria, join.get(field.getName()).getParentPath(), criteriaBuilder, loadByFilterProcessor, true, foreignColumn);
                }
            } catch (Exception e) {
                predicate = null;
                predicateList = new ArrayList<Predicate>();
            }
        }
        if (predicate != null) {
            predicateList.add(predicate);
        }
        return predicateList;
    }

    /**
     * generate predicate
     * @param field
     * @param filterObject
     * @param root
     * @param criteriaBuilder
     * @param <V>
     * @return
     */
    private <V> Predicate generatePredicate(Field field, V filterObject,
                                            Path root, CriteriaBuilder criteriaBuilder,
                                            LoadByFilterProcessor loadByFilterProcessor) {
        return generatePredicate(field, filterObject, root, criteriaBuilder, loadByFilterProcessor, false);
    }
    /**
     * generate predicate
     * @param field
     * @param filterObject
     * @param root
     * @param criteriaBuilder
     * @param <V>
     * @return
     */
    private <V> Predicate generatePredicate(Field field, V filterObject,
                                            Path root, CriteriaBuilder criteriaBuilder,
                                            LoadByFilterProcessor loadByFilterProcessor,boolean isSubQuery) {
        return generatePredicate(field, filterObject, root, criteriaBuilder, loadByFilterProcessor, false, "");
    }

    /**
     * generate predicate
     * @param field
     * @param filterObject
     * @param root
     * @param criteriaBuilder
     * @param <V>
     * @return
     */
    private <V> Predicate generatePredicate(Field field, V filterObject,
                                            Path root, CriteriaBuilder criteriaBuilder,
                                            LoadByFilterProcessor loadByFilterProcessor,boolean isSubQuery,String fieldName) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "generatePredicate", new Object[]{field, filterObject, root,
                    criteriaBuilder, loadByFilterProcessor});
        }

        Predicate predicate = null;
        Object fieldValue = getFieldValue(field, filterObject);
        if (l.isLoggable(Level.FINE))
            l.logp(Level.FINE, getClass().getName(), "generatePredicate", "Retrieved value for field<" + field.getName() +"> = " + fieldValue);
        if (!isFieldValueEmpty(fieldValue)) {
            boolean isBlank = false;
            // String
            if (fieldValue instanceof String && ((String) fieldValue).length() == 0) {
                isBlank = true;
            }
            if (!isBlank) {
                Filter.Type filterType = null;
                if (loadByFilterProcessor.getFieldFilterTypeMap() != null) {
                    if (loadByFilterProcessor.getFieldFilterTypeMap().containsKey(field.getName())) {
                        filterType = loadByFilterProcessor.getFieldFilterTypeMap().get(field.getName());
                    }
                }
                if (filterType == null) {
                    Filter filter = field.getAnnotation(Filter.class);
                    if (filter != null) {
                        if (isSubQuery) {
                            filterType = filter.type();
                        } else {
                            filterType = Filter.Type.EQUAL == filter.subType() ? filter.type() : filter.subType();
                        }
                    }
                }
                if (filterType == null){
                    filterType = Filter.Type.EQUAL;
                }
                if (filterType == Filter.Type.EQUAL) {
                    Path<T> path = root.get(field.getName());
                    predicate = criteriaBuilder.equal(path, fieldValue);
                } else if (filterType == Filter.Type.LIKE) {
                    // LIKE is only for string
                    if (fieldValue instanceof String) {
                        Path<String> path = root.get(field.getName());
                        predicate = criteriaBuilder.like(criteriaBuilder.upper(path), criteriaBuilder.upper(criteriaBuilder.literal("%" + (String) fieldValue + "%")));
                    }
                } else if (filterType == Filter.Type.STARTSWITH) {
                    // STARTSWITH is only for string
                    if (fieldValue instanceof String) {
                        Path<String> path = root.get(field.getName());
                        predicate = criteriaBuilder.like(criteriaBuilder.upper(path), criteriaBuilder.upper(criteriaBuilder.literal((String) fieldValue + "%")));
                    }
                } else if (filterType == Filter.Type.GREATER) {
                    if (fieldValue instanceof Comparable){
                        Path<Comparable> path = root.get(field.getName());
                        predicate = criteriaBuilder.greaterThan(path,(Comparable)fieldValue);
                    }
                } else if (filterType == Filter.Type.LESS) {
                    if (fieldValue instanceof Comparable){
                        Path<Comparable> path = root.get(field.getName());
                        predicate = criteriaBuilder.lessThan(path,(Comparable)fieldValue);
                    }
                } else if (filterType == Filter.Type.GREATEROREQUAL) {
                    if (fieldValue instanceof Comparable){
                        Path<Comparable> path = root.get(field.getName());
                        predicate = criteriaBuilder.greaterThanOrEqualTo(path,(Comparable)fieldValue);
                    }
                } else if (filterType == Filter.Type.LESSOREQUAL) {
                    if (fieldValue instanceof Comparable){
                        Path<Comparable> path = root.get(field.getName());
                        predicate = criteriaBuilder.lessThanOrEqualTo(path,(Comparable)fieldValue);
                    }
                } else if (filterType == Filter.Type.IN || filterType == Filter.Type.NOTIN) {
                    Path<Collection> path = root.get(StringUtils.isBlank(fieldName)?field.getName():fieldName);
                    CriteriaBuilder.In in = criteriaBuilder.in(path);
                    String[] collection = fieldValue.toString().split("\\|");
                    if (collection.length > 0) {
                        for (int i = 0; i < collection.length; i++) {
                            in.value(collection[i]);
                        }
                        if (filterType == Filter.Type.IN) {
                            predicate = in;
                        } else {
                            predicate = criteriaBuilder.not(in);
                        }
                    }
                }
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "generatePredicate", predicate);
        }
        return predicate;
    }

    private boolean isFieldValueEmpty(Object fieldValue) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isFieldValueEmpty", new Object[]{fieldValue});
        }
        boolean isEmpty = false;
        if (fieldValue == null) {
            isEmpty = true;
        } else if (fieldValue instanceof Collection) {
            if (((Collection) fieldValue).size() == 0) {
                isEmpty = true;
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isFieldValueEmpty", isEmpty);
        }
        return isEmpty;
    }

    /**
     * get getter method
     * @param entityClass
     * @param field
     * @return
     */
    private Method getMethod(Class entityClass, Field field) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getMethod", new Object[]{entityClass, field});
        }

        String fieldName = field.getName();
        String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        Method method = null;
        try {
            method = entityClass.getMethod(methodName);
        } catch (NoSuchMethodException e) {
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getMethod", method);
        }
        return method;
    }

    /**
     * get field value
     *
     * @param field
     * @param object
     * @return
     */
    private Object getFieldValue(Field field, Object object) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getFieldValue", new Object[]{field, object});
        }

        Object fieldValue = null;
        Method getterMethod = getMethod(object.getClass(), field);
        if (getterMethod != null) {
            try {
                fieldValue = getterMethod.invoke(object);
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
        }
        if (fieldValue != null) {
            if (fieldValue instanceof String && ((String) fieldValue).length() == 0) {
                fieldValue = null;
            }
        }
        if (fieldValue instanceof Collection) {
            Collection resultValue = (Collection) fieldValue;
            if(resultValue.size() == 0) {
                fieldValue = null;
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getFieldValue", fieldValue);
        }
        return fieldValue;
    }


    /**
     * get path.
     *
     * Support "." in name. Example:  pfprofPK.profile
     *
     * @param root
     * @param fieldName
     * @return
     */
    private Path getFieldPath(Root root, String fieldName) {
        Path path = null;
        int position = fieldName.indexOf(".");
        if (position > 0) {
            String first = fieldName.substring(0, position);
            String second = fieldName.substring(position + 1);
            path = root.get(first).get(second);
        } else {
            path = root.get(fieldName);
        }
        return path;
    }

    protected Integer getFetchSize(){
       return c_defaultFetchSize;
    }
    private final Logger l = LogUtils.getLogger(getClass());

    static final DefaultLoadByFilterProcessor c_defaultLoadByFilterProcessor = new DefaultLoadByFilterProcessor();
    protected static final Integer c_defaultFetchSize = new Integer(256);

}
