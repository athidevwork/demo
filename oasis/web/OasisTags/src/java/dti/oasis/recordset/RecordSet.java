package dti.oasis.recordset;

import dti.oasis.busobjs.Info;
import dti.oasis.busobjs.PagingInfo;
import dti.oasis.converter.Converter;
import dti.oasis.converter.ConverterFactory;
import dti.oasis.filter.Filter;
import dti.oasis.util.ListUtils;
import dti.oasis.util.LogUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class contains a set of Records, a Summary Record,
 * and information about the page from the underlying data source that this RecordSet represents.
 * The Records are indexed starting with 0 and ending with <tt>getSize() - 1</tt>.
 * The RecordSet also offers methods for retrieving a filtered sub-set, and for sorting the Records.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 18, 2006
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/06/2007       MLM         Refactored to use addFieldNameCollection instead of setFieldNameCollection
 * 01/05/2010       James       Added reduceRecords method.
 * 03/22/2010       James       Issue#105489 Added ignoreGridSortOrder
 * 12/28/2011       James       Issue#128899 RecordSet.getSortedCopy doesn't copy obr properties in RecordSet
 * 12/01/2014       xnie        Issue 158391 Modified merge() to Reset record number after merge.
 * 11/12/2018       wreeder     196160 - Optimize iteration through Fields in a Record with getFields() / field.getStringValue() instead of getFieldNames() / record.hasFieldValue(fieldId) / record.getStringValue(fieldId)
 * ---------------------------------------------------
 */
public class RecordSet implements Info, Serializable {

    /**
     * The index of the first row, if the RecordSet contains any Records.
     */
    public static final int FIRST_ROW_INDEX = 0;

    /**
     * Returns the first record in the RecordSet.
     *
     * @throws IndexOutOfBoundsException if there are no records.
     */
    public Record getFirstRecord() {
        return (Record) m_records.get(FIRST_ROW_INDEX);
    }

    /**
     * Gets the specified record. Records are indexed starting with 0.
     *
     * @param index
     */
    public Record getRecord(int index) {
        return (Record) m_records.get(index);
    }

    /**
     * This logic should be moved into <code>RecordSet</code> and exposed by one method.
     *
     * @param records
     */
    public void addRecords(RecordSet records) {
        if (records == null || records.getSize() == 0) return;
        Iterator recIter = records.getRecords();
        while (recIter.hasNext()) {
            addRecord((Record) recIter.next());
        }
    }

    /**
     * Add the given Record to this RecordSet.
     * By default, the recordNumber of each record is set according to its position in this RecordSet.
     *
     * @param record the record to add
     */
    public void addRecord(Record record) {
        addRecord(record, false);
    }

    /**
     * Add the given Record to this RecordSet.
     *
     * @param record               the record to add
     * @param preserveRecordNumber indicates whether to preserve the recordNumber or to recreate it based on the position in the sub set.
     */
    public void addRecord(Record record, boolean preserveRecordNumber) {

        if (!preserveRecordNumber) {
            record.setRecordNumber(m_records.size());
        }

        m_records.add(record);

        if (record.getFieldCount() != getFieldCount()) {
            addFieldNameCollection(record.getFieldNames());
            addFieldTypeMap(record);
        }
        record.connectToRecordSet(this);
    }

    /**
     * remove the record from this RecordSet.
     * @param record
     * @param resetRecordNumber  indicate whether to reset record number
     */
    public void removeRecord(Record record, boolean resetRecordNumber) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "removeRecord", new Object[]{record});
        }
        m_records.remove(record);
        record.disconnectFromRecordSet();
        if (resetRecordNumber) {
            resetRecordNumber();
        }
        l.exiting(getClass().getName(), "removeRecord");
    }

    /**
     *  Re-number the records
     */
    public void resetRecordNumber() {
        Iterator iter = this.getRecords();
        int i = 0;
        while (iter.hasNext()) {
            Record recordObject = (Record) iter.next();
            recordObject.setRecordNumber(i++);
        }
        l.exiting(getClass().getName(), "resetRecordNumber");
    }

    /**
     * reduce the number of records
     * @param maxRows
     */
    public void reduceRecords(int maxRows) {
        List removeRecords = new ArrayList();
        for (int i = 0; i < m_records.size(); i++) {
            Record record = (Record) m_records.get(i);
            if (record.getRecordNumber() >= maxRows) {
                removeRecords.add(record);
                record.disconnectFromRecordSet();
            }
        }
        m_records.removeAll(removeRecords);

        // Re-number the records if requested.
        Iterator iter = this.getRecords();
        int i = 0;
        while (iter.hasNext()) {
            Record recordObject = (Record) iter.next();
            recordObject.setRecordNumber(i++);
        }
    }


    /**
     * Replace the record at the specified index with the given record.
     *
     * @param index  the index of the record to replace
     * @param record the new record to replace the existing record with.
     */
    public void replaceRecord(int index, Record record) {
        m_records.set(index, record);
    }

    /**
     * Set the given field value on all contained Records.
     * By default, any fields that already exist with the same name are overwritten.
     */
    public void setFieldValueOnAll(String name, Object value) {
        setFieldValueOnAll(name, value, true);
    }

    /**
     * Set the given field value on all contained Records.
     */
    public void setFieldValueOnAll(String name, Object value, boolean overwriteFieldIfExists) {
        Iterator iter = getRecords();
        while (iter.hasNext()) {
            Record record = (Record) iter.next();
            if (overwriteFieldIfExists || !record.hasFieldValue(name)) {
                record.setFieldValue(name, value);
            }
        }
        addFieldName(name);
    }

    /**
     * Set the given record of fields on all contained Records.
     * By default, any fields that already exist with the same name are overwritten.
     */
    public void setFieldsOnAll(Record newFields) {
        setFieldsOnAll(newFields, true);
    }

    /**
     * Set the given record of fields on all contained Records.
     */
    public void setFieldsOnAll(Record newFields, boolean overwriteFieldIfExists) {
        Iterator iter = getRecords();
        while (iter.hasNext()) {
            Record record = (Record) iter.next();
            record.setFields(newFields, overwriteFieldIfExists);
        }
        addFieldNameCollection(newFields.getFieldNames());
        addFieldTypeMap(newFields);

    }

    /**
     * Get an Iterator of all Records in this RecordSet. This excludes the Summary Record.
     */
    public Iterator<Record> getRecords() {
        return m_records.iterator();
    }

    /**
     * get record list
     * @return
     */
    public List<Record> getRecordList() {
        return m_records;
    }

    /**
     * Return the Summary Record.
     */
    public Record getSummaryRecord() {
        return m_summaryRecord;
    }

    /**
     * Set the Summary Record.
     */
    public void setSummaryRecord(Record summaryRecord) {
        m_summaryRecord = summaryRecord;
    }

    /**
     * Get the PagingInfo, describing the page that this RecordSet contains data for.
     */
    public PagingInfo getPagingInfo() {
        if (!m_pagingInfoSetExternally) {
            m_pagingInfo.setPageNum(new Integer(1));
            Integer totRows = new Integer(m_records.size());
            if (totRows.intValue() > 0) {
                m_pagingInfo.setRowsPerPage(totRows);
            }
            m_pagingInfo.setTotalRows(totRows);
        }
        return m_pagingInfo;
    }

    /**
     * Set the PagingInfo, describing the page that this RecordSet contains data for.
     */
    public void setPagingInfo(PagingInfo pagingInfo) {
        m_pagingInfo = pagingInfo;
        m_pagingInfoSetExternally = true;
    }

    /**
     * Return an Iterator of the Field Names in the order that they appear in the first record.
     * If there are no records, but the fields names were set upon construction (ex. when loaded from a database result set),
     * the field names are still available.
     */
    public Iterator getFieldNames() {
        return m_fieldNamesList.iterator();
    }

    /**
     * Return the number of fields, as they exist in the first record.
     * If there are no records, but the fields names were set upon construction (ex. when loaded from a database result set),
     * the field count represents the number of field names that were setup upon construction.
     */
    public int getFieldCount() {
        return m_fieldNamesList.size();
    }

    /**
     * Return the number of Records in this RecordsSet. This excludes the Summary Record.
     */
    public int getSize() {
        return m_records.size();
    }

    /**
     * Get a new RecordSet with the Records that match the given filter.
     * The Summary Record is copied as is without modification.
     * The resulting RecordSet has references to all matching Records in the source RecordSet.
     * That means that any changes to Records from one directly affect the Records in the other.
     * By default, the recordNumber of each record is preserved from the source RecordSet.
     */
    public RecordSet getSubSet(Filter recordFilter) {
        return getSubSet(recordFilter, true);
    }

    /**
     * Get a new RecordSet with the Records that match the given filter.
     * The Summary Record is copied as is without modification.
     * The resulting RecordSet has references to all matching Records in the source RecordSet.
     * That means that any changes to Records from one directly affect the Records in the other.
     *
     * @param recordFilter         the filter used to find matching Records.
     * @param preserveRecordNumber indicates whether to preserve the recordNumber or to recreate it based on the position in the sub set.
     */
    public RecordSet getSubSet(Filter recordFilter, boolean preserveRecordNumber) {
        RecordSet rs = new RecordSet();

        // Add the matching Records
        for (Iterator iter = getRecords(); iter.hasNext();) {
            Record record = (Record) iter.next();
            if (recordFilter.accept(record)) {
                rs.addRecord(record, preserveRecordNumber);
            }
        }

        postCopyRecordSet(rs);

        return rs;
    }

    /**
     * Get a new RecordSet with the Records sorted using the provided Comparator.
     * The Summary Record is copied as is without modification.
     * The resulting RecordSet has references to all matching Records in the source RecordSet.
     * That means that any changes to Records from one directly affect the Records in the other.
     * By default, the recordNumber of each record is preserved from the source RecordSet.
     *
     * @param comparator the Comparator used to sort the RecordSet copy
     */
    public RecordSet getSortedCopy(Comparator comparator) {
        return getSortedCopy(comparator, true);
    }

    /**
     * Get a new RecordSet with the Records sorted using the provided Comparator.
     * The Summary Record is copied as is without modification.
     * The resulting RecordSet has references to all matching Records in the source RecordSet.
     * That means that any changes to Records from one directly affect the Records in the other.
     *
     * @param comparator           the Comparator used to sort the RecordSet copy
     * @param preserveRecordNumber indicates whether to preserve the recordNumber or to recreate it based on the position in the sub set.
     */
    public RecordSet getSortedCopy(Comparator comparator, boolean preserveRecordNumber) {
        RecordSet rs = new RecordSet();

        // Add all the Records
        rs.m_records = (List) ((ArrayList) m_records).clone();

        // Sort the cloned copy of records.
        Collections.sort(rs.m_records, comparator);

        // Re-number the records if requested.
        if (!preserveRecordNumber) {
            Iterator iter = rs.getRecords();
            int i = 0;
            while (iter.hasNext()) {
                Record record = (Record) iter.next();
                record.setRecordNumber(i++);
            }
        }

        postCopyRecordSet(rs);

        return rs;
    }

    /**
     * Merge modifiedRecords into existedRecords accoriding by mergeField
     * Replace the matching old record(mergeFields match) with modified record.
     * If cann't matched old record cant be found, add it.
     * And also merge the summary record to this recordSet.
     * The data types of all modified Record fields are converted to the data types of the corresponding fields in this RecordSet.
     *
     * @param modifiedRecords
     * @param mergeField
     * @return existedRecords
     */
    public void merge(RecordSet modifiedRecords, String mergeField) {
        if (this.getSize() == 0) {
            this.addRecords(modifiedRecords);
            this.getSummaryRecord().setFields(modifiedRecords.getSummaryRecord());
        }
        else if (modifiedRecords.getSize() == 0) {
            this.getSummaryRecord().setFields(modifiedRecords.getSummaryRecord());
            return;
        }
        else {
            //construct a map with key is pk, and value is index of recordSet.
            HashMap map = new HashMap();
            for (int i = 0; i < this.getSize(); i++) {
                Record record = this.getRecord(i);
                map.put(record.getStringValue(mergeField), new Integer(i));
            }
            // replace and add new record to existedRecord
            syncMissingFieldTypes();
            modifiedRecords.convertDataTypes(this.getFieldTypesMap());
            for (Iterator itor = modifiedRecords.getRecords(); itor.hasNext();) {
                Record modifiedRecord = (Record) itor.next();
                String recordId = modifiedRecord.getStringValue(mergeField);
                //replace
                if (map.containsKey(recordId)) {
                    this.replaceRecord(((Integer) map.get(recordId)).intValue(), modifiedRecord);
                }
                //add
                else {
                    this.addRecord(modifiedRecord);
                }
            }
            this.getSummaryRecord().setFields(modifiedRecords.getSummaryRecord());
            this.resetRecordNumber();
        }
    }

    /**
     * convert field dataType in RecordSet from String to the same dateType as fieldTypesMap
     *
     * @param fieldTypesMap
     * @return
     */
    public void convertDataTypes(Map fieldTypesMap) {
        ConverterFactory converterFactory = ConverterFactory.getInstance();
        Iterator itor2 = this.getRecords();
        //loop the recoredSet
        while (itor2.hasNext()) {
            Record record = (Record) itor2.next();
            Iterator itor = fieldTypesMap.keySet().iterator();
            //loop all fieldName need to be converted
            while (itor.hasNext()) {
                String fieldName = (String) itor.next();
                if (record.hasFieldValue(fieldName)) {
                    Class inputClassType = (Class) fieldTypesMap.get(fieldName);
                    Object fieldValue = record.getFieldValue(fieldName);
                    if (!inputClassType.isInstance(fieldValue)) {
                        //there is no converter for YesNoFlag now
                            Converter converter = converterFactory.getConverter(inputClassType);
                            Object convertedFieldValue = converter.convert(inputClassType,fieldValue);
                            record.setFieldValue(fieldName, convertedFieldValue);
                    }
                }
            }
        }
    }

    public void removeAllRecords() {
        m_records.clear();
        m_summaryRecord.clear();
        m_pagingInfo.clear();
    }

    /**
     * Clear contents of this RecordSet.
     */
    public void clear() {
        m_records.clear();
        m_summaryRecord.clear();
        m_fieldNamesList.clear();
        m_fieldNamesMap.clear();
        m_pagingInfo.clear();
        m_fieldTypesMap.clear();
        m_pagingInfoSetExternally = false;
        m_uppercaseFieldNamesSet = null;
    }

    /**
     * Set the field names of this RecordSet with the provided Collection of field names.
     */
    public void addFieldNameCollection(List fieldNames) {
        addFieldNameCollection(fieldNames.iterator());
    }

    /**
     * Set the field names of this RecordSet with the provided Iterator of field names.
     */
    public void addFieldNameCollection(Iterator fieldNamesIter) {
        while (fieldNamesIter.hasNext()) {
            String fieldName = (String) fieldNamesIter.next();
            addFieldName(fieldName);
        }
    }

    /**
     * set the fieldTypesMap by provided map
     *
     * @param fieldTypes
     */
    public void addFieldTypeMap(Map fieldTypes) {
        Iterator itor = fieldTypes.keySet().iterator();
        while (itor.hasNext()) {
            String fieldName = (String) itor.next();
            addFieldType(fieldName.toUpperCase(), (Class) fieldTypes.get(fieldName));
        }
    }

    /**
     * add fieldTypesMap by new record
     *
     * @param record
     */
    public void addFieldTypeMap(Record record) {
        Iterator itor = record.getFields();
        while (itor.hasNext()) {
            Field field = (Field) itor.next();
            String fieldName = field.getName();
            Object fieldValue = field.getValue();
            if (fieldValue != null) {
                addFieldType(fieldName.toUpperCase(), fieldValue.getClass());
            }
        }
    }

    protected void addFieldType(String fieldName, Class fieldType) {
        m_fieldTypesMap.put(fieldName.toUpperCase(), fieldType);
        //m_fieldNamesMap.put(fieldName.toUpperCase(), fieldType);
    }

    protected void addFieldName(String fieldName) {
        if (!m_fieldNamesMap.containsKey(fieldName.toUpperCase())) {
            m_fieldNamesMap.put(fieldName.toUpperCase(), fieldName);
            m_fieldNamesList.add(fieldName);
            m_uppercaseFieldNamesSet = null;
        }
    }

    public void removeFieldName(String fieldName) {
        if (m_fieldNamesMap.containsKey(fieldName.toUpperCase())) {
            m_fieldNamesMap.remove(fieldName.toUpperCase());

            // Remove the field name from the fieldNames list in a case insensitive manner.
            ListUtils.removeCaseInsensitive(m_fieldNamesList, fieldName);
            m_uppercaseFieldNamesSet = null;
        }
    }

    private void postCopyRecordSet(RecordSet rs) {
        // Use the same Summary Record
        rs.setSummaryRecord(getSummaryRecord());

        // Setup the PagingInfo
        PagingInfo pagingInfo = new PagingInfo();
        pagingInfo.setTotalRows(new Integer(rs.getSize()));
        if (m_pagingInfoSetExternally) {
            pagingInfo.setRowsPerPage(getPagingInfo().getRowsPerPage());
        }
        else {
            pagingInfo.setRowsPerPage(pagingInfo.getTotalRows());
        }
        rs.setPagingInfo(getPagingInfo());
        rs.m_pagingInfoSetExternally = m_pagingInfoSetExternally;

        rs.m_fieldNamesList = (List) ((ArrayList) m_fieldNamesList).clone();
        rs.m_fieldNamesMap = (Map) ((HashMap) m_fieldNamesMap).clone();

        rs.setOBREnforcingFieldList(this.getOBREnforcingFieldList());
        rs.setOBRConsequenceFieldList(this.getOBRConsequenceFieldList());
        rs.setOBRAllAccessedFieldList(this.getOBRAllAccessedFieldList());
        rs.setOBREnforcingGridUpdateIndicator(this.getOBREnforcingGridUpdateIndicator());
    }

    /**
     * Return the list of field names.
     */
    public List<String> getFieldNameList() {
        return m_fieldNamesList;
    }

    public Map getFieldTypesMap() {
        return m_fieldTypesMap;
    }

    public String toString() {
        return toString("; ", ", ");
    }

    public String toString(String recordSeparator, String fieldSeparator) {
        final StringBuffer buf = new StringBuffer();
        buf.append("RecordSet { ").append("m_summaryRecord=").append(m_summaryRecord.toString(fieldSeparator));
        buf.append(recordSeparator).append("m_pagingInfo=").append(m_pagingInfo);
        buf.append(recordSeparator).append("m_pagingInfoSetExternally=").append(m_pagingInfoSetExternally);
        buf.append(recordSeparator).append("m_records=");

        String sep = "";
        Iterator iter = m_records.iterator();
        while (iter.hasNext()) {
            Record record = (Record) iter.next();
            buf.append(sep).append(record.toString(fieldSeparator));
            sep = recordSeparator;
        }
        buf.append('}');
        return buf.toString();
    }

    /**
     * SyncMissingFieldTypes to m_fieldTypesMap by per record
     */
    public void syncMissingFieldTypes () {
        if (this.getSize() > 0) {
            Iterator itor = this.getRecords();
            //construct the undefinedTypeFieldNames (HashSet)
            HashSet undefinedTypeFieldNames = new HashSet();
            Record firstRecord = this.getFirstRecord();
            Iterator itor2 = firstRecord.getFieldNames();
            while (itor2.hasNext()) {
                undefinedTypeFieldNames.add(((String) itor2.next()).toUpperCase());
            }
            undefinedTypeFieldNames.removeAll(m_fieldTypesMap.keySet());
            while ((undefinedTypeFieldNames.size() > 0) && (itor.hasNext())) {
                Record record = (Record) itor.next();
                Iterator itor3 = undefinedTypeFieldNames.iterator();
                HashSet newAddedfieldNames=new HashSet();
                while (itor3.hasNext()) {
                    String fieldName = (String) itor3.next();
                    if (record.hasFieldValue(fieldName)) {
                        m_fieldTypesMap.put(fieldName.toUpperCase(), record.getFieldValue(fieldName).getClass());
                        newAddedfieldNames.add(fieldName.toUpperCase());
                    }
                }
                undefinedTypeFieldNames.removeAll(newAddedfieldNames);
            }
        }
    }

    /**
     * This methos is used to set the field name to the specified index of the field name list.
     * If the field name doesn't exist in the field name list, system does nothing.
     * Zero is the first index of field name list.
     *
     * @param name  field name
     * @param index specified index of this field name
     * @throws  IndexOutOfBoundsException if index is out of range.
     */
    public void setFieldIndex(String name, int index) {
        if (!m_fieldNamesMap.containsKey(name.toUpperCase())) {
            return;
        }
        // Remove the field name from the fieldNames list in a case insensitive manner.
        ListUtils.removeCaseInsensitive(m_fieldNamesList, name);
        // Add the field name to the specified postion of field name list.
        m_fieldNamesList.add(index, name);
        // Set the index in all records.
        Iterator recordsItr = getRecords();
        while (recordsItr.hasNext()) {
            Record record = (Record) recordsItr.next();
            record.setFieldIndex(name, index);
        }
    }

    /**
     * get uppercase field name set
     * @param useCache use cache or not
     * @return
     */
    public Set getUpperCaseFieldNameSet(boolean useCache) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getUpperCaseFieldNameSet", new Object[]{useCache});
        }
        if (m_uppercaseFieldNamesSet == null || !useCache) {
            m_uppercaseFieldNamesSet = new HashSet();
            for (Object object : m_fieldNamesList) {
                String fieldName = (String) object;
                m_uppercaseFieldNamesSet.add(fieldName.toUpperCase());
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getUpperCaseFieldNameSet", m_uppercaseFieldNamesSet);
        }
        return m_uppercaseFieldNamesSet;
    }

    public boolean isDataFromClient() {
        return isDataFromClient;
    }

    public void setDataFromClient(boolean dataFromClient) {
        isDataFromClient = dataFromClient;
    }

    public String getOBREnforcingFieldList() {
        return m_OBREnforcingFieldList;
    }

    public void setOBREnforcingFieldList(String enforcingFieldList) {
        m_OBREnforcingFieldList = enforcingFieldList;
    }

    public String getOBRConsequenceFieldList() {
        return m_OBRConsequenceFieldList;
    }

    public void setOBRConsequenceFieldList(String OBRConsequenceFieldList) {
        m_OBRConsequenceFieldList = OBRConsequenceFieldList;
    }

    public String getOBRAllAccessedFieldList() {
        return m_OBRAllAccessedFieldList;
    }

    public void setOBRAllAccessedFieldList(String OBRAllAccessedFieldList) {
        m_OBRAllAccessedFieldList = OBRAllAccessedFieldList;
    }

    public String getOBREnforcingGridUpdateIndicator() { return m_OBREnforcingGridUpdateIndicator; }

    public void setOBREnforcingGridUpdateIndicator(String OBREnforcingGridUpdateIndicator) {
        m_OBREnforcingGridUpdateIndicator = OBREnforcingGridUpdateIndicator;
    }

    private String m_OBREnforcingGridUpdateIndicator = "";
    private String m_OBREnforcingFieldList = "";
    private String m_OBRConsequenceFieldList = "";
    private String m_OBRAllAccessedFieldList = "";
    private List<Record> m_records = new ArrayList();
    private Record m_summaryRecord = new Record();
    private List<String> m_fieldNamesList = new ArrayList();
    private Set m_uppercaseFieldNamesSet = null;
    private Map m_fieldNamesMap = new HashMap();
    private Map m_fieldTypesMap = new HashMap();
    private PagingInfo m_pagingInfo = new PagingInfo();
    private boolean m_pagingInfoSetExternally = false;
    private boolean isDataFromClient = false;

    private final Logger l = LogUtils.getLogger(getClass());
}
