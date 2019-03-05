package dti.oasis.jpa;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.lang.reflect.Field;
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
public interface LoadByFilterProcessor {
    Predicate postProcessFieldPredicate(Predicate predicate, Field field, Path root, CriteriaBuilder criteriaBuilder, EntityManager entityManager);

    void postProcessPredicateList(List<Predicate> predicateList, Object filterEntity, Root root, CriteriaBuilder criteriaBuilder, EntityManager entityManager);

    void postProcessCriteriaQuery(Object filterEntity, Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder, EntityManager entityManager);

    void postProcessTypedQuery(Object filterEntity, Root root, TypedQuery typedQuery, CriteriaBuilder criteriaBuilder, EntityManager entityManager);

    Map<String, Filter.Type> getFieldFilterTypeMap();
}
