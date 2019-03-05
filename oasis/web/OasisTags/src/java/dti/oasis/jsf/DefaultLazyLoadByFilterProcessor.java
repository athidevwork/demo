package dti.oasis.jsf;

import dti.oasis.jpa.DefaultLoadByFilterProcessor;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import org.eclipse.persistence.jpa.JpaQuery;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   5/29/13
 *
 * This class takes care of adding the following predicates to a JPA query:
 * <ul>
 *  <li>
 *      <b>Order By</b> the specified Sort Field when provided.
 *          If the Sort Order is not provided, the provided Sort Field is ordered ascending.  
 *          The Row Key Name is added as the last ordered column with the defined Sort Order 
 *          to ensure a consistent order of records in case the provided Sort Field is not unique.
 *          When no Sort Field is provided, the query is ordered by the Row Key, using the Row Key Default Sort Order
 *          if defined, or descending by default.
 *  </li>
 *  <li>
 *      <b>Pagination</b> attributes, setting the First Row and Max Results to define which page of data to load 
 *          when these attributes are provided.      
 *  </li>
 * </ul>
 * When the For Counting attribute is set to true, the predicates are not added to optimize the query for retrieving the count of records. 
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class DefaultLazyLoadByFilterProcessor extends DefaultLoadByFilterProcessor {

    public DefaultLazyLoadByFilterProcessor(boolean forCounting) {
        m_forCounting = forCounting;
    }

    public DefaultLazyLoadByFilterProcessor(String rowKeyName) {
        this(rowKeyName, null);
    }

    public DefaultLazyLoadByFilterProcessor(String rowKeyName, String rowKeyDefaultSortOrder) {
        m_rowKeyName = rowKeyName;
        m_rowKeyDefaultSortOrder = rowKeyDefaultSortOrder;
    }

    public DefaultLazyLoadByFilterProcessor(String rowKeyName, int firstRow, int pageSize) {
        this(rowKeyName, null, firstRow, pageSize);
    }

    public DefaultLazyLoadByFilterProcessor(String rowKeyName, String rowKeyDefaultSortOrder, int firstRow, int pageSize) {
        m_rowKeyName = rowKeyName;
        m_rowKeyDefaultSortOrder = rowKeyDefaultSortOrder;
        m_firstRow = firstRow;
        m_maxResults = pageSize;
    }

    @Override
    public void postProcessCriteriaQuery(Object filterEntity, Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder, EntityManager entityManager) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "postProcessCriteriaQuery", new Object[]{filterEntity, root,
                    criteriaQuery, criteriaBuilder, entityManager});
        }

        if (!m_forCounting) {
            // Apply the Order By predicate
            List<Order> orderList = new ArrayList<Order>();
            if (!StringUtils.isBlank(m_sortField)) {
                if (isDescending(m_sortOrder)) {
                    orderList.add(criteriaBuilder.desc(criteriaBuilder.lower(getFieldPath(root, m_sortField))));

                    if (!m_sortField.equalsIgnoreCase(getRowKeyName()))
                        orderList.add(criteriaBuilder.desc(criteriaBuilder.lower(getFieldPath(root, getRowKeyName()))));
                } else {
                    orderList.add(criteriaBuilder.asc(criteriaBuilder.lower(getFieldPath(root, m_sortField))));

                    if (!m_sortField.equalsIgnoreCase(getRowKeyName()))
                        orderList.add(criteriaBuilder.asc(criteriaBuilder.lower(getFieldPath(root, getRowKeyName()))));
                }
            }
            else {
                if (isAscending(m_rowKeyDefaultSortOrder)) {
                    orderList.add(criteriaBuilder.asc(criteriaBuilder.lower(getFieldPath(root, getRowKeyName()))));
                } else {
                    orderList.add(criteriaBuilder.desc(criteriaBuilder.lower(getFieldPath(root, getRowKeyName()))));
                }
            }
            criteriaQuery.orderBy(orderList);
        }
        l.exiting(getClass().getName(), "postProcessCriteriaQuery");
    }

    @Override
    public void postProcessTypedQuery(Object filterEntity, Root root, TypedQuery typedQuery, CriteriaBuilder criteriaBuilder, EntityManager entityManager) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "postProcessTypedQuery", new Object[]{filterEntity, root,
                    criteriaBuilder, entityManager});
        }
        if (!m_forCounting) {
            // Apply the pagination predicates
            if (m_firstRow >= 0)
                typedQuery.setFirstResult(m_firstRow);
            if (m_maxResults >= 0) {
                typedQuery.setMaxResults(m_maxResults);
                typedQuery.unwrap(JpaQuery.class).getDatabaseQuery().setHintString("/*+ FIRST_ROWS(" + m_maxResults + ") */");
            }
        }
        l.exiting(getClass().getName(), "postProcessTypedQuery");
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
    protected Path getFieldPath(Root root, String fieldName) {
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

    public int getFirstRow() {
        return m_firstRow;
    }

    public void setFirstRow(int firstRow) {
        m_firstRow = firstRow;
    }

    public int getMaxResults() {
        return m_maxResults;
    }

    public void setMaxResults(int maxResults) {
        m_maxResults = maxResults;
    }

    public String getSortField() {
        return m_sortField;
    }

    public void setSortField(String sortField) {
        this.m_sortField = sortField;
    }

    public String getSortOrder() {
        return m_sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.m_sortOrder = sortOrder;
    }

    public boolean isForCounting() {
        return m_forCounting;
    }

    public void setForCounting(boolean forCounting) {
        m_forCounting = forCounting;
    }

    public String getRowKeyName() {
        return m_rowKeyName;
    }

    public void setRowKeyName(String rowKeyName) {
        m_rowKeyName = rowKeyName;
    }

    public String getRowKeyDefaultSortOrder() {
        return m_rowKeyDefaultSortOrder;
    }

    public void setRowKeyDefaultSortOrder(String rowKeyDefaultSortOrder) {
        m_rowKeyDefaultSortOrder = rowKeyDefaultSortOrder;
    }
    
    private boolean isAscending(String sortOrder) {
        return (!StringUtils.isBlank(sortOrder) &&
                ("ascending".equalsIgnoreCase(sortOrder) || "asc".equalsIgnoreCase(sortOrder)));
    }

    private boolean isDescending(String sortOrder) {
        return (!StringUtils.isBlank(sortOrder) &&
                ("descending".equalsIgnoreCase(sortOrder) || "desc".equalsIgnoreCase(sortOrder)));
    }

    private int m_firstRow = -1;
    private int m_maxResults = -1;
    private String m_sortField;
    private String m_sortOrder;
    private boolean m_forCounting = false;
    private String m_rowKeyName;
    private String m_rowKeyDefaultSortOrder;
}
