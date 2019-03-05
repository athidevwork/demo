package dti.oasis.jpa;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   5/12/13
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class DefaultLoadByFilterProcessor implements LoadByFilterProcessor {
    @Override
    public Predicate postProcessFieldPredicate(Predicate predicate, Field field, Path root, CriteriaBuilder criteriaBuilder, EntityManager entityManager) {
        // Do nothing by default
        return predicate;
    }

    @Override
    public void postProcessPredicateList(List<Predicate> predicateList, Object filterEntity, Root root, CriteriaBuilder criteriaBuilder, EntityManager entityManager) {
        // Do nothing by default
    }

    @Override
    public void postProcessCriteriaQuery(Object filterEntity, Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder, EntityManager entityManager) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void postProcessTypedQuery(Object filterEntity, Root root, TypedQuery typedQuery, CriteriaBuilder criteriaBuilder, EntityManager entityManager) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map<String, Filter.Type> getFieldFilterTypeMap() {
        return m_fieldFilterTypeMap;
    }

    public void setFieldFilterTypeMap(String fieldName, Filter.Type type) {
        m_fieldFilterTypeMap.put(fieldName,type);
    }

    private Map<String, Filter.Type> m_fieldFilterTypeMap = new HashMap<String, Filter.Type>();
}
