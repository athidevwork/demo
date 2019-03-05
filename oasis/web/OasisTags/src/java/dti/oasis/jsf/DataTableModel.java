package dti.oasis.jsf;

import dti.oasis.app.AppException;
import dti.oasis.jpa.Filter;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;

import javax.persistence.EmbeddedId;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   1/3/13
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
public class DataTableModel<T> implements Serializable {

    /**
     * insert a new row
     *
     * @param entity
     */
    public void insertRow(T entity) {
        if (entity == null){
            throw new AppException("Can not insert null");
        }
        dataList.add(entity);
        displayList.add(entity);
        if(supportBulkUpdate)
            getChangedData().insertEntity(entity);
    }

    /**
     * update entity
     * 
     * @param entity
     */
    public void updateRow(T entity) {
        if (entity == null){
            throw new AppException("Can not update null");
        }
        updateInList(dataList, entity);
        updateInList(displayList, entity);
        if(supportBulkUpdate)
            getChangedData().updateEntity(entity);
    }

    /**
     * delete entity
     * 
     * @param entity
     */
    public void deleteRow(T entity) {
        if (entity == null){
            throw new AppException("Can not delete null");
        }
        dataList.remove(entity);
        displayList.remove(entity);
        if(supportBulkUpdate)
            getChangedData().deleteEntity(entity);
    }

    /**
     * filter data based on filter
     */
    public void filterData() {
        this.filterData(c_emptyDataTableModelFilterProcessor);
    }

    /**
     * filter data based on filter
     */
    public void filterData(DataTableModelFilterProcessor filterProcessor) {
        Logger l = LogUtils.enterLog(getClass(), "filterData", new Object[]{filterProcessor});
        displayList = new ArrayList<T>();
        if (filter == null) {
            // filter is not used
            displayList.addAll(dataList);
        } else {
            for (T record : dataList) {
                if (matchFilter(record, filterProcessor)) {
                    displayList.add(record);
                }
            }
        }
        l.exiting(getClass().getName(), "filterData");
    }

    /**
     * get row count
     * @return
     */
    public int getRowCount(){
        return displayList.size();
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * clear all data and filter
     */
    public void clear() {
        filter = null;
        dataList.clear();
        displayList.clear();
        if(supportBulkUpdate && changedData != null)
            changedData.clear();
    }

    /**
     * clear all data
     */
    public void clearData() {
        dataList.clear();
        displayList.clear();
        if(supportBulkUpdate && changedData != null)
            changedData.clear();
    }

    /**
     * generate a temp pk
     * @return
     */
    public Long generatePK() {
        return pkGenerator++;
    }

    /**
     * select first row
     * @return
     */
    public boolean selectFirstRow() {
        Logger l = LogUtils.enterLog(getClass(), "selectFirstRow");
        boolean success = false;
        if (getDisplayList().size() > 0) {
            success = true;
            setSelectedRow(getDisplayList().get(0));
        } else {
            setSelectedRow(null);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "selectFirstRow", success);
        }
        return success;
    }

    /**
     * select last row
     * @return
     */
    public boolean selectLastRow() {
        Logger l = LogUtils.enterLog(getClass(), "selectLastRow");
        boolean success = false;
        if (getDisplayList().size() > 0) {
            success = true;
            setSelectedRow(getDisplayList().get(getDisplayList().size()-1));
        } else {
            setSelectedRow(null);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "selectLastRow", success);
        }
        return success;
    }

    /**
     * check whether the record match the filter
     *
     * @param record
     * @return
     */
    protected boolean matchFilter(T record, DataTableModelFilterProcessor filterProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "matchFilter", new Object[]{record, filterProcessor});
        }
        boolean isMatch = true;
        for (Field field : record.getClass().getDeclaredFields()) {
            Object filterValue = getFieldValue(field, filter);
            Object recordValue = getFieldValue(field, record);
            EmbeddedId embeddedId = field.getAnnotation(EmbeddedId.class);
            if (embeddedId == null) {
                isMatch = matchField(field, filterValue, recordValue);
            } else {
                isMatch = matchEmbeddedId(field, filterValue, recordValue);
            }
            if (isMatch) {
                isMatch = filterProcessor.postProcessMatchField(field, filterValue, recordValue);
            }
            if (!isMatch){
                break;
            }
        }
        if (isMatch) {
            isMatch = filterProcessor.postProcessMatchRecord(record);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "matchFilter", isMatch);
        }
        return isMatch;
    }

    /**
     * match EmbeddedId field
     * @param field
     * @return
     */
    protected boolean matchEmbeddedId(Field field, Object filterObject, Object targetObject) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "matchEmbeddedId", new Object[]{field, filterObject, targetObject});
        }
        boolean isMatch = true;
        if (filterObject != null) {
            Class embeddedIdClass = field.getType();
            for (Field embeddedObjectField : embeddedIdClass.getDeclaredFields()) {
                Object filterValue = getFieldValue(embeddedObjectField, filterObject);
                Object targetValue = getFieldValue(embeddedObjectField, targetObject);
                isMatch = matchField(embeddedObjectField, filterValue, targetValue);
                if (!isMatch) {
                    break;
                }
            }
        }
        l.exiting(getClass().getName(), "matchEmbeddedId", isMatch);
        return isMatch;
    }

    /**
     * match field
     * @param field
     * @param filterValue
     * @param recordValue
     * @return
     */
    protected boolean matchField(Field field, Object filterValue, Object recordValue){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "matchField", new Object[]{field, filterValue, recordValue});
        }
        boolean isMatch = true;
        if (filterValue != null) {
            if (filterValue instanceof Comparable) {
                if (recordValue == null) {
                    isMatch = false;
                } else {
                    Filter.Type filterType = null;
                    if (fieldFilterTypeMap != null) {
                        if (fieldFilterTypeMap.containsKey(field.getName())) {
                            filterType = fieldFilterTypeMap.get(field.getName());
                        }
                    }
                    if (filterType == null){
                        Filter filterAnnotation = field.getAnnotation(Filter.class);
                        if (filterAnnotation != null) {
                            filterType = filterAnnotation.type();
                        }
                    }
                    if (filterType == null){
                        filterType = Filter.Type.EQUAL;
                    }
                    isMatch = match(recordValue, filterValue, filterType);
                }
            }
        }
        l.exiting(getClass().getName(), "matchField", isMatch);
        return isMatch;
    }

    /**
     * @param recordValue
     * @param filterValue
     * @param filterType
     * @return
     */
    protected boolean match(Object recordValue, Object filterValue, Filter.Type filterType) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "match", new Object[]{recordValue, filterValue, filterType});
        }
        boolean isMatch = false;
        if (filterType == Filter.Type.EQUAL) {
            if (recordValue instanceof Date && filterValue instanceof  Date) {
                if (DateUtils.formatDate((Date) recordValue).equals(DateUtils.formatDate((Date) filterValue))) {
                    isMatch = true;
                }
            } else {
                if (recordValue.equals(filterValue)) {
                    isMatch = true;
                }
            }
        } else if (filterType == Filter.Type.LIKE) {
            if (recordValue instanceof String && filterValue instanceof String) {
                String recordValueString = ((String) recordValue).toUpperCase();
                String filterValueString = ((String) filterValue).toUpperCase();
                if (recordValueString.indexOf(filterValueString) >= 0) {
                    isMatch = true;
                }
            }
        } else if (filterType == Filter.Type.IN) {
            if (recordValue instanceof String && filterValue instanceof String) {
                String recordValueString = ((String) recordValue).toUpperCase();
                String filterValueString = ((String) filterValue).toUpperCase();
                if (filterValueString.indexOf(recordValueString) >= 0) {
                    isMatch = true;
                }
            }
        } else if (filterType == Filter.Type.STARTSWITH) {
            if (recordValue instanceof String && filterValue instanceof String) {
                String recordValueString = ((String) recordValue).toUpperCase();
                String filterValueString = ((String) filterValue).toUpperCase();
                if (recordValueString.startsWith(filterValueString)) {
                    isMatch = true;
                }
            }
        } else if (filterType == Filter.Type.GREATER) {
            if (filterValue instanceof Comparable) {
                Comparable recordValueComparable = (Comparable) recordValue;
                Comparable filterValueComparable = (Comparable) filterValue;
                if (recordValueComparable.compareTo(filterValueComparable) > 0) {
                    isMatch = true;
                }
            }
        } else if (filterType == Filter.Type.LESS) {
            if (filterValue instanceof Comparable) {
                Comparable recordValueComparable = (Comparable) recordValue;
                Comparable filterValueComparable = (Comparable) filterValue;
                if (recordValueComparable.compareTo(filterValueComparable) < 0) {
                    isMatch = true;
                }
            }
        } else if (filterType == Filter.Type.GREATEROREQUAL) {
            if (filterValue instanceof Comparable) {
                Comparable recordValueComparable = (Comparable) recordValue;
                Comparable filterValueComparable = (Comparable) filterValue;
                if (recordValueComparable.compareTo(filterValueComparable) >= 0) {
                    isMatch = true;
                }
            }
        } else if (filterType == Filter.Type.LESSOREQUAL) {
            if (filterValue instanceof Comparable) {
                Comparable recordValueComparable = (Comparable) recordValue;
                Comparable filterValueComparable = (Comparable) filterValue;
                if (recordValueComparable.compareTo(filterValueComparable) <= 0) {
                    isMatch = true;
                }
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "match", isMatch);
        }
        return isMatch;
    }

    /**
     * get getter method
     *
     * @param entityClass
     * @param field
     * @return
     */
    protected Method getMethod(Class entityClass, Field field) {
        Logger l = LogUtils.getLogger(getClass());
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
    protected Object getFieldValue(Field field, Object object) {
        Logger l = LogUtils.getLogger(getClass());
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
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getFieldValue", fieldValue);
        }
        return fieldValue;
    }
    
    protected void updateInList(List<T> list, T element){
        int index = list.indexOf(element);
        if(index!=-1) {
            list.set(index, element);
        } else {
            list.add(element);
        }
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        Logger l = LogUtils.getLogger(getClass());
        l.entering(getClass().getName(), "setDataList", new Object[]{dataList});
        setDataList(dataList, true);
        l.exiting(getClass().getName(), "setDataList");
    }

    public void setDataList(List<T> dataList, boolean filterData) {
        Logger l = LogUtils.getLogger(getClass());
        l.entering(getClass().getName(), "setDataList", new Object[]{dataList, filterData});

        this.dataList = dataList;
        if (filterData) {
            filterData();
        } else {
            displayList = new ArrayList<T>();
            displayList.addAll(dataList);
        }

        l.exiting(getClass().getName(), "setDataList");
    }

    public void setDisplayList(List<T> displayList) {
        this.displayList = displayList;
    }

    public List<T> getDisplayList() {
        return displayList;
    }

    public T getFilter() {
        return filter;
    }

    public void setFilter(T filter) {
        this.filter = filter;
    }

    public T getSearchCriteria() {
        return searchCriteria;
    }

    public void setSearchCriteria(T searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    public ChangedDataModel<T> getChangedData() {
        return changedData;
    }

    public T getSelectedRow() {
        return selectedRow;
    }

    public void setSelectedRow(T selectedRow) {
        this.selectedRow = selectedRow;
    }

    public T[] getSelectedRows() {
        return selectedRows;
    }

    public void setSelectedRows(T[] selectedRows) {
        this.selectedRows = selectedRows;
    }

    public T getCurrentEditRecord() {
        return currentEditRecord;
    }

    public void setCurrentEditRecord(T currentEditRecord) {
        this.currentEditRecord = currentEditRecord;
    }

    public Boolean getCurrentEditRecordChanged() {
        return currentEditRecordChanged;
    }

    public void setCurrentEditRecordChanged(Boolean currentEditRecordChanged) {
        this.currentEditRecordChanged = currentEditRecordChanged;
    }

    public Boolean isShowGrid() {
        if (!showGrid) {
            if (displayList.size() > 0) {
                showGrid = true;
            } else {
                showGrid = false;
            }
        }
        return showGrid;
    }

    public Boolean getShowGrid() {
        if (!showGrid) {
            if (displayList.size() > 0) {
                showGrid = true;
            } else {
                showGrid = false;
            }
        }
        return showGrid;
    }

    public void setShowGrid(Boolean showGrid) {
        this.showGrid = showGrid;
    }

    public Map<String, Filter.Type> getFieldFilterTypeMap() {
        return fieldFilterTypeMap;
    }

    public void setFieldFilterTypeMap(Map<String, Filter.Type> fieldFilterTypeMap) {
        this.fieldFilterTypeMap = fieldFilterTypeMap;
    }

    public DataTableModel(Boolean supportBulkUpdate) {
        this.supportBulkUpdate = supportBulkUpdate;
        if(supportBulkUpdate)
            changedData = new ChangedDataModel<T>();
    }

    public DataTableModel() {
        supportBulkUpdate = Boolean.TRUE;
        changedData = new ChangedDataModel<T>();
    }

    private List<T> dataList = new ArrayList<T>();

    private List<T> displayList = new ArrayList<T>();

    private Map<String, Filter.Type> fieldFilterTypeMap;

    private T filter;

    private T searchCriteria;

    private ChangedDataModel<T> changedData;
    
    private T currentEditRecord;

    private Boolean currentEditRecordChanged = Boolean.FALSE;

    private T selectedRow;

    private T[] selectedRows;
    
    private Long pkGenerator= new Long(-3000);

    private static final long serialVersionUID = 1L;

    private int pageSize;
    
    private Boolean supportBulkUpdate;

    private Boolean showGrid = false;

    private static DefaultDataTableModelFilterProcessor c_emptyDataTableModelFilterProcessor = new DefaultDataTableModelFilterProcessor();
}
