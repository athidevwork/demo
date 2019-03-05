package dti.oasis.data;

import dti.oasis.app.AppException;
import dti.oasis.busobjs.PagingInfo;
import dti.oasis.busobjs.PagingSupport;
import dti.oasis.converter.Converter;
import dti.oasis.converter.ConverterFactory;
import dti.oasis.filter.Filter;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import oracle.jdbc.OracleTypes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 18, 2006
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/06/2007         MLM       Refactored to use addFieldNameCollection instead of setFieldNameCollection
 * 06/16/2008       wer         Refactored for use with the new XMLRecordSetBuilder
 * ---------------------------------------------------
 */
public class RecordSetBuilder {

    public static RecordSetBuilder getInstance(ResultSet resultSet, Vector rsColumnDescVector, Record summaryRecord) {
        return new RecordSetBuilder(resultSet, rsColumnDescVector, summaryRecord);
    }

    public DataRecordMapping getDataRecordMapping() {
        return m_dataRecordMapping;
    }

    public void setDataRecordMapping(DataRecordMapping dataRecordMapping) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setDataRecordMapping", new Object[]{dataRecordMapping});
        }

        m_dataRecordMapping = dataRecordMapping;
        if (dataRecordMapping != null) {
            // Add the DataRecordFieldMappings to the cached fieldMappings
            for (int i = 0; i < getRsColumnDescVector().size(); i++) {
                ColumnDesc c = (ColumnDesc) getRsColumnDescVector().elementAt(i);
                if (m_dataRecordMapping.containsMappingForDataField(c.javaColumnName)) {
                    DataRecordFieldMapping dataRecordFieldMapping = m_dataRecordMapping.getMappingForDataField(c.javaColumnName);

                    FieldMapper fieldMapper = (FieldMapper) getFieldMappers().get(i);
                    fieldMapper.setTargetFieldName(dataRecordFieldMapping.getRecordFieldName());

                    if (dataRecordFieldMapping.hasOutputFieldConverter()) {
                        fieldMapper.setTargetFieldType(dataRecordFieldMapping.getOutputTargetType());
                        fieldMapper.setConverter(dataRecordFieldMapping.getOutputFieldConverter());
                    }
                    else if (dataRecordFieldMapping.hasOutputTargetType()) {
                        fieldMapper.setTargetFieldType(dataRecordFieldMapping.getOutputTargetType());
                        fieldMapper.setConverter(ConverterFactory.getInstance().getConverter(dataRecordFieldMapping.getOutputTargetType()));
                    }
                    getFieldMappers().set(i, fieldMapper);
                    if (l.isLoggable(Level.FINE)) {
                        l.logp(Level.FINE, getClass().getName(), "setDataRecordMapping", "Added field mapper for javaColumnName'" + c.javaColumnName + "'; fieldMapper = " + fieldMapper);
                    }
                }
            }
        }

        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "setDataRecordMapping", "Using the following field mappings for the result set columns: " + getFieldMappers());
        }
        l.exiting(getClass().getName(), "setDataRecordMapping");
    }

    protected void setRsColumnDescVector(Vector rsColumnDescVector) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setRsColumnDescVector", new Object[]{rsColumnDescVector});
        }

        if (rsColumnDescVector != null) {
            m_rsColumnDescVector = rsColumnDescVector;
            m_rsColumnDescMap = new HashMap();

            // Initialize the FieldMappers
            m_fieldMappers = new Vector();
            m_fieldMappers.setSize(m_rsColumnDescVector.size());
            for (int i = 0; i < m_rsColumnDescVector.size(); i++) {
                ColumnDesc c = (ColumnDesc) m_rsColumnDescVector.elementAt(i);
                m_fieldMappers.set(i, new FieldMapper(c.javaColumnName));
                setColumnDescMapFor(c);
            }
        }

        l.exiting(getClass().getName(), "setRsColumnDescVector");
    }


    protected Vector getRsColumnDescVector() {
        return m_rsColumnDescVector;
    }

    protected boolean hasColumnDescFor(String columnName) {
        return m_rsColumnDescMap.containsKey(columnName.toUpperCase());
    }

    protected ColumnDesc getColumnDescFor(String columnName) {
        return (ColumnDesc) m_rsColumnDescMap.get(columnName.toUpperCase());
    }

    private void setColumnDescMapFor(ColumnDesc c) {
        m_rsColumnDescMap.put(c.javaColumnName.toUpperCase(), c);
    }

    protected Vector getFieldMappers() {
        return m_fieldMappers;
    }

    /**
     * Build a RecordSet containing all rows from the contained StoredProcedureCursor.
     *
     * @return the resulting RecordSet
     */
    public RecordSet build() throws SQLException {
        return build(c_defaultLoadProcessor);
    }

    /**
     * Build a RecordSet containing all rows from the contained StoredProcedureCursor.
     * All rows are processed by the given LoadProcessor following the loading of each individual row,
     * and the RecordSet is processed following the loading of the entire RecordSet.
     *
     * @param loadProc the LoadProcessor
     * @return the resulting RecordSet
     */
    public RecordSet build(RecordLoadProcessor loadProc) throws SQLException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "build", new Object[]{loadProc});
        }

        if (loadProc == null) loadProc = c_defaultLoadProcessor;

        RecordSet recordSet = new RecordSet();
        recordSet.setSummaryRecord(getSummaryRecord());
        setFieldNamesAndTypesFromResultSetColumns(recordSet);

        int cursorRowCount = 0;

        while (next()) {
            Record rec = getRecord();
            cursorRowCount++;
            if (loadProc.postProcessRecord(rec, true)) {
                recordSet.addRecord(rec);
            }
        }

        // Log the number of rows retrieved
        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "build", "Processed " + cursorRowCount + " records.");
        }

        recordSet.setPagingInfo(new PagingInfo(1, cursorRowCount, cursorRowCount));

        loadProc.postProcessRecordSet(recordSet);

        l.exiting(getClass().getName(), "build");
        return recordSet;
    }

    /**
     * Build a RecordSet containing the specified page of rows from the contained StoredProcedureCursor.
     * All rows are processed by the given LoadProcessor following the loading of each individual row,
     * and the RecordSet is processed following the loading of the entire RecordSet.
     *
     * @param loadProc    the LoadProcessor
     * @param pageNum     the page to retrieve
     * @param rowsPerPage the number of rows per page
     * @return the resulting RecordSet
     */
    public RecordSet build(RecordLoadProcessor loadProc, int pageNum, int rowsPerPage) throws SQLException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "build", new Object[]{loadProc, new Integer(pageNum), new Integer(rowsPerPage)});
        }


        RecordSet recordSet = new RecordSet();
        int cursorRowCount = 0, currentPageRowIdx = 0;
        int firstRowIdx = PagingSupport.getFirstRowIndex(rowsPerPage, pageNum); // firstRowIdx is 1-based
        boolean rowIsOnCurrentPage = false;

        recordSet.setSummaryRecord(getSummaryRecord());
        setFieldNamesAndTypesFromResultSetColumns(recordSet);

        while (next()) {
            rowIsOnCurrentPage = false;
            Record rec = getRecord();
            cursorRowCount++; // cursorRowCount is 1-based.

            // Add only the rows that appear on the page
            if (firstRowIdx <= cursorRowCount && currentPageRowIdx < rowsPerPage) {
                rowIsOnCurrentPage = true;
            }

            loadProc.postProcessRecord(rec, rowIsOnCurrentPage);

            if (rowIsOnCurrentPage) {
                currentPageRowIdx++;
                recordSet.addRecord(rec);
            }
        }

        // Log the number of rows retrieved
        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "build", "Processed " + cursorRowCount + " records.");
        }

        recordSet.setPagingInfo(new PagingInfo(pageNum, cursorRowCount, rowsPerPage));

        loadProc.postProcessRecordSet(recordSet);

        l.exiting(getClass().getName(), "build");
        return recordSet;
    }

    /**
     * Build a RecordSet containing the page of rows from the contained StoredProcedureCursor that contain the row matching
     * the given page filter.
     * All rows are processed by the given LoadProcessor following the loading of each individual row,
     * and the RecordSet is processed following the loading of the entire RecordSet.
     *
     * @param loadProc    the LoadProcessor
     * @param pageFilter  the filter used to
     * @param rowsPerPage the number of rows per page
     * @return the resulting RecordSet
     */
    public RecordSet build(RecordLoadProcessor loadProc, Filter pageFilter, int rowsPerPage) throws SQLException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "build", new Object[]{loadProc, pageFilter, new Integer(rowsPerPage)});
        }


        RecordSet recordSet = new RecordSet();
        int cursorRowCount = 0,
            currentPageRowIdx = 0; // currentPageRowIdx is 1-based
        int pageNum = 1;
        boolean foundMatchingRow = false, filledMatchingPage = false;

        recordSet.setSummaryRecord(getSummaryRecord());
        setFieldNamesAndTypesFromResultSetColumns(recordSet);

        while (next()) {
            Record rec = getRecord();
            cursorRowCount++;
            currentPageRowIdx++;

            // If we haven't found a match and this record matches.
            if (!foundMatchingRow && pageFilter.accept(rec)) {
                foundMatchingRow = true;
                // process the current record set, as this contains the current page
                processRecordSet(recordSet, loadProc, true);
            }

            // If we found a match
            if (foundMatchingRow) {
                boolean rowIsOnCurrentPage = false;

                // if it is on the target page, add it to the record set
                if (!filledMatchingPage) {
                    rowIsOnCurrentPage = true;
                }

                // process the record
                loadProc.postProcessRecord(rec, rowIsOnCurrentPage);

                if (rowIsOnCurrentPage) {
                    recordSet.addRecord(rec);
                }
            }
            // If we haven't found a match yet, add the record to the record set and don't process it yet
            else {
                recordSet.addRecord(rec);
            }

            if (currentPageRowIdx == rowsPerPage) {
                // We have hit the bottom of the page.
                currentPageRowIdx = 0;
                if (foundMatchingRow) {
                    filledMatchingPage = true;
                } else {
                    pageNum++;
                    processRecordSet(recordSet, loadProc, false);
                    recordSet.clear();
                }
            }
        }

        // Log the number of rows retrieved
        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "build", "Processed " + cursorRowCount + " records.");
        }

        // If we haven't found a mathing row, throw an exception
        if (!foundMatchingRow && pageFilter.hasDataToCompare()) {
            throw new AppException(pageFilter.toString());
        } else if (!foundMatchingRow) {
            throw new AppException("Failed to find the matching row.");
        }

        recordSet.setPagingInfo(new PagingInfo(pageNum, cursorRowCount, rowsPerPage));

        loadProc.postProcessRecordSet(recordSet);

        l.exiting(getClass().getName(), "build");
        return recordSet;
    }

    protected void setResultSet(ResultSet rs) {
        m_rs = rs;
        if (rs != null) {
            m_rss = ResultSetSupport.getInstance(rs);
        }
    }

    public boolean hasResultSet() {
        return m_rss != null;
    }

    /**
     * Fetch the next row, and return true if one exists or false if there are no more rows.
     *
     * @return true if one exists or false if there are no more rows.
     */
    protected boolean next() throws SQLException {
        return m_rs.next();
    }

    protected ResultSetSupport getResultSet() {
        return m_rss;
    }

    /**
     * Return the Record for the current row.
     *
     * @throws SQLException
     */
    protected Record getRecord() throws SQLException {
        l.entering(getClass().getName(), "getRecord");

        Record record = new Record();

        // Build record with Fields to represent each column in the current row of the result set
        for (int i = 0; i < getRsColumnDescVector().size(); i++) {
            ColumnDesc c = (ColumnDesc) getRsColumnDescVector().elementAt(i);
            if (c.dataType != OracleTypes.LONGVARBINARY) {
//                int rsColumnIndex = i + 1;
                Object columnValue = getResultSet().getColumnValue(c);

                FieldMapper fieldMapper = (FieldMapper) getFieldMappers().get(i);
                record.setFieldValue(fieldMapper.getTargetFieldName(), fieldMapper.convert(columnValue));
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getRecord", record);
        }
        return record;
    }

    /**
     * Set the Field Names
     */
    protected void setFieldNamesAndTypesFromResultSetColumns(RecordSet recordSet) {
        // Make sure all result set column field names are added to the record set field names.
        List rsFieldNames = new ArrayList();
        Map rsFieldTypes= new HashMap();
        
        for (int i = 0; i < getRsColumnDescVector().size(); i++) {
            FieldMapper fieldMapper = (FieldMapper) getFieldMappers().get(i);
            rsFieldNames.add(fieldMapper.getTargetFieldName());
            if (fieldMapper.getTargetFieldType() != null) {
                rsFieldTypes.put(fieldMapper.getTargetFieldName(),fieldMapper.getTargetFieldType());
            }
        }
        recordSet.addFieldNameCollection(rsFieldNames);
        recordSet.addFieldTypeMap(rsFieldTypes);
    }

    private void processRecordSet(RecordSet recordSet, RecordLoadProcessor loadProc, boolean rowIsOnCurrentPage) {
        Iterator iter = recordSet.getRecords();
        while (iter.hasNext()) {
            Record rec = (Record) iter.next();
            loadProc.postProcessRecord(rec, rowIsOnCurrentPage);
        }
    }


    protected Record getSummaryRecord() {
        return m_summaryRecord;
    }

    protected RecordSetBuilder() {
    }

    protected RecordSetBuilder(ResultSet resultSet, Vector rsColumnDescVector, Record summaryRecord) {
        m_summaryRecord = summaryRecord;
        setRsColumnDescVector(rsColumnDescVector);
        setResultSet(resultSet);
    }

    protected DataRecordMapping m_dataRecordMapping;
    protected Record m_summaryRecord;
    private Vector m_rsColumnDescVector;
    private Map m_rsColumnDescMap;
    private ResultSet m_rs;
    private ResultSetSupport m_rss;
    private Vector m_fieldMappers;
    private final Logger l = LogUtils.getLogger(getClass());

    private static RecordLoadProcessor c_defaultLoadProcessor = new DefaultRecordLoadProcessor();
}


class FieldMapper {
    public FieldMapper() {
    }

    public FieldMapper(String targetFieldName) {
        m_targetFieldName = targetFieldName;
    }

    public Object convert(Object value) {
        return m_converter == null ? value : m_converter.convert(m_targetFieldType, value);
    }

    public String getTargetFieldName() {
        return m_targetFieldName;
    }

    public void setTargetFieldName(String targetFieldName) {
        m_targetFieldName = targetFieldName;
    }

    public Class getTargetFieldType() {
        return m_targetFieldType;
    }

    public void setTargetFieldType(Class targetFieldType) {
        m_targetFieldType = targetFieldType;
    }

    public boolean hasConverter() {
        return m_converter != null;
    }

    public Converter getConverter() {
        return m_converter;
    }

    public void setConverter(Converter converter) {
        m_converter = converter;
    }

    private String m_targetFieldName;
    private Class m_targetFieldType;
    private Converter m_converter;
}