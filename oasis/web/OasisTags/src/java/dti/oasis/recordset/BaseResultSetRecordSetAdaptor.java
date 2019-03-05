package dti.oasis.recordset;

import dti.oasis.util.BaseResultSet;
import dti.oasis.util.LogUtils;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.busobjs.UpdateIndicator;

import java.util.Date;
import java.util.Iterator;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class adapts a RecordSet to behave as a BaseResultSet.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 17, 2006
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 2/1/2007         JMP         Handle null values for grid display.  Added additional method
 *                              to define "null" keyword translation to empty string.
 * 3/6/2007         MLM         Enhanced addEmptyRow to set the default value of "N"
 *                              for display_ind, update_ind and edit_ind columns.
 * 3/8/2007         JMP         Add sort method to invoke RecordSet comparator sorting
 * 12/4/2007        wer         Added hasNonDeletedRows to help OasisGrid to determine if an empty row should be added.
 * 03/22/2010       James       Issue#105489 Added isIgnoreGridSortOrder
 * 10/06/2014       awu         157694 - Added getString(String columnName, String defaultValue).
 * ---------------------------------------------------
 */
public class BaseResultSetRecordSetAdaptor implements BaseResultSet {

    /**
     * Get an instance of a BaseResultSet that is backed by the given RecordSet.
     */
    public static BaseResultSet getInstance(RecordSet recordSet) {
        return new BaseResultSetRecordSetAdaptor(recordSet);
    }

    /**
     * Return the number of rows in the Result Set.
     */
    public int getRowCount() {
        return m_recordSet.getSize();
    }


    /**
     * Return true if there are row contained that have the Update Indicator set to something other than deleted - 'D'.
     * Otherwise, return false.
     *
     * @return true if there are non-deleted rows; otherwise false.
     */
    public boolean hasNonDeletedRows() {
        l.entering(getClass().getName(), "hasNonDeletedRows");

        boolean hasNonDeletedRows = false;
        Iterator iter = m_recordSet.getRecords();
        while (iter.hasNext()) {
            Record record = (Record) iter.next();
            if (!record.getUpdateIndicator().equals(UpdateIndicator.DELETED)) {
                hasNonDeletedRows = true;
                break;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "hasNonDeletedRows", Boolean.valueOf(hasNonDeletedRows));
        }
        return hasNonDeletedRows;
    }

    /**
     * Return the number of columns in each row of the Result Set.
     */
    public int getColumnCount() {
        // store original count of columns
        int colCount = m_recordSet.getFieldCount();
        return colCount;
    }

    /**
     * Position the current row index to before the first row.
     */
    public void beforeFirst() {
        m_currentRowIndex = BEFORE_FIRST_ROW_INDEX;
    }

    /**
     * Position the current row index to before the first row.
     *
     * @return true if rows exist; otherwise, false.
     */
    public boolean first() {
        l.entering(getClass().getName(), "first");

        boolean hasRecords = m_recordSet.getSize() > 0;
        m_currentRowIndex = hasRecords ? FIRST_ROW_INDEX : BEFORE_FIRST_ROW_INDEX;

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "first", String.valueOf(hasRecords));
        }
        return hasRecords;
    }

    /**
     * Return true if on first row
     */
    public boolean isFirst() {
        return (m_currentRowIndex == 0);
    }

    /**
     * Return true if on last row
     */
    public boolean isLast() {
        return (m_currentRowIndex == m_recordSet.getSize() - 1);
    }

    /**
     * Move the current row index to the next row.
     *
     * @return true if there is a row at the current row index; otherwise, false;
     */
    public boolean next() {
        l.entering(getClass().getName(), "next");

        m_currentRowIndex++;
        boolean rowAtIndex = m_currentRowIndex < m_recordSet.getSize();

        if (!rowAtIndex) {
            // ensure we don't keep incrementing the current row index past the size of the records.
            m_currentRowIndex = m_recordSet.getSize();
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "next", String.valueOf(rowAtIndex));
        }
        return rowAtIndex;
    }

    /**
     * go to previous row
     *
     * @return true if on a real row
     */
    public boolean previous() {
        l.entering(getClass().getName(), "previous");
        m_currentRowIndex -= (m_currentRowIndex > 0) ? 1 : 0;
        boolean rc = (m_currentRowIndex > 0);
        l.exiting(getClass().getName(), "previous", new Boolean(rc));
        return rc;
    }

    /**
     * Data is from client
     * @return true if data is from client
     */
    public boolean isDataFromClient() {
        return m_recordSet.isDataFromClient();
    }

    /**
     * Add an empty row to the Result Set with empty values for each defined field.
     */
    public void addEmptyRow() {
        l.entering(getClass().getName(), "addEmptyRow");

        Record emptyRecord = new Record();
        Iterator iter = m_recordSet.getFieldNames();
        while (iter.hasNext()) {
            String fieldName = (String) iter.next();
            emptyRecord.setFieldValue(fieldName, null);
        }
        // For an empty placeholder row (id=-9999), default the display_ind and edit_ind to "N".
        emptyRecord.setDisplayIndicator(YesNoFlag.N);
        emptyRecord.setEditIndicator(YesNoFlag.N);
        emptyRecord.setUpdateIndicator(UpdateIndicator.NOT_CHANGED);
        m_recordSet.addRecord(emptyRecord);

        l.exiting(getClass().getName(), "addEmptyRow");
    }

    /**
     * Return true if the column exists; otherwise, false.
     */
    public boolean hasColumn(String columnName) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "hasColumn", new Object[]{columnName});
        }

        boolean hasColumn = false;
        if (m_recordSet.getSize() > 0) {
            Record firstRecord = m_recordSet.getFirstRecord();
            hasColumn = firstRecord.hasField(columnName);
        }
        else {
            Iterator iter = m_recordSet.getFieldNames();
            while (iter.hasNext()) {
                String fieldName = (String) iter.next();
                if (fieldName.equalsIgnoreCase(columnName)) {
                    hasColumn = true;
                    break;
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "hasColumn", String.valueOf(hasColumn));
        }
        return hasColumn;
    }

    /**
     * Return the name of the column at the specified column index.
     * The column indexes start at 1.
     *
     * @throws IndexOutOfBoundsException if index is out of range.
     */
    public String getColumnName(int columnIndex) {
        return (String) m_recordSet.getFieldNameList().get(columnIndex - 1);
    }

    /**
     * Return the value at the specified column index.
     * The column indexes start at 1.
     *
     * @throws IndexOutOfBoundsException if index is out of range.
     */
    public Object get(int columnIndex) {
        return m_recordSet.getRecord(m_currentRowIndex).getFieldValue(columnIndex - 1);
    }

    /**
     * Return the value mapped to the specified column name.
     *
     * @throws IllegalArgumentException if the desired column is not found.
     */
    public Object get(String columnName) {
        return m_recordSet.getRecord(m_currentRowIndex).getFieldValue(columnName);
    }

    /**
     * Return the String value at the specified column index.
     * The column indexes start at 1.
     *
     * @throws IndexOutOfBoundsException if index is out of range.
     */
    public String getString(int columnIndex) {
        return m_recordSet.getRecord(m_currentRowIndex).getStringValue(columnIndex - 1, "");
    }

    /**
     * Return the String value mapped to the specified column name.
     *
     * @throws IllegalArgumentException if the desired column is not found.
     */
    public String getString(String columnName) {
        return m_recordSet.getRecord(m_currentRowIndex).getStringValue(columnName, "");
    }

    /**
     * Return the String value of the specified column name.
     * If the value of the column name is null, or the column doesn't exist,
     * then return defaultValue.
     * @param columnName
     * @param defaultValue
     * @return
     */
    public String getString(String columnName, String defaultValue) {
        return m_recordSet.getRecord(m_currentRowIndex).getStringValue(columnName, defaultValue);
    }

    /**
     * Return the Date value at the specified column index.
     * The column indexes start at 1.
     *
     * @throws IndexOutOfBoundsException if index is out of range.
     */
    public Date getDate(int columnIndex) {
        return m_recordSet.getRecord(m_currentRowIndex).getDateValue(columnIndex - 1);
    }

    /**
     * Return the Date value mapped to the specified column name.
     *
     * @throws IllegalArgumentException if the desired column is not found.
     */
    public Date getDate(String columnName) {
        return m_recordSet.getRecord(m_currentRowIndex).getDateValue(columnName);
    }

    /**
     * Return the update indicator, specifying if this row has been inserted, deleted, updated or not changed.
     * If the update indicator is not found, UpdateIndicator.NOT_CHANGED is returned by default.
     */
    public char getUpdateInd() {
        return m_recordSet.getRecord(m_currentRowIndex).getUpdateIndicator().charAt(0);
    }

    /**
     * Return the display indicator, specifying if this row can be displayed.
     * If the display indicator is not found, 'Y' is returned by default.
     */
    public char getDisplayInd() {
        return m_recordSet.getRecord(m_currentRowIndex).getDisplayIndicator().charAt(0);
    }

    /**
     * Return the edit indicator, specifying if this row can be edited.
     * If the display indicator is not found, 'Y' is returned by default.
     */
    public char getEditInd() {
        return m_recordSet.getRecord(m_currentRowIndex).getEditIndicator().charAt(0);
    }

    /**
     * Return OBR enforcing Update Indicator
     */
    public String getOBREnforcingUpdateIndicator(){
        return m_recordSet.getOBREnforcingGridUpdateIndicator();
    }

    /**
     * Return OBR enforced field list
     */
    public String getOBREnforcingFieldList(){
        return m_recordSet.getOBREnforcingFieldList();
    }

    /**
     * Return OBR Consequence field list
     */
    public String getOBRConsequenceFieldList(){
        return m_recordSet.getOBRConsequenceFieldList();
    }

    /**
     * Return OBR AllAccessed field list
     */
    public String getOBRAllAccessedFieldList(){
        return m_recordSet.getOBRAllAccessedFieldList();
    }

    /**
     * Return OBR enforced result
     */
    public String getOBREnforcedResult(){
        return m_recordSet.getRecord(m_currentRowIndex).getOBREnforcedResult();
    }

    /**
     * Sort the RecordSet using the provided Comparator.
     */
    public void sort(Comparator comparator) {
        m_recordSet = m_recordSet.getSortedCopy(comparator);
    }

    public Iterator getRecords() {
        return m_recordSet.getRecords();
    }

    public RecordSet getRecordSet() {
        return m_recordSet;
    }

    protected BaseResultSetRecordSetAdaptor(RecordSet recordSet) {
        m_recordSet = recordSet;
    }

    private int m_currentRowIndex = BEFORE_FIRST_ROW_INDEX;
    private RecordSet m_recordSet;

    // The currentRowIndex is 0 based.
    private static final int BEFORE_FIRST_ROW_INDEX = -1;
    private static final int FIRST_ROW_INDEX = BEFORE_FIRST_ROW_INDEX + 1;
    private final Logger l = LogUtils.getLogger(getClass());
}
