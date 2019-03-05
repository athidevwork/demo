package dti.oasis.util;

import java.util.Date;
import java.util.Comparator;

/**
 * This interface captures the common methods necessary to access data in a result set.
 * It useful to allow multiple implementations without dictating the particular implementation.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 17, 2006
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/08/2007       JMP         Add sort method.
 * 12/4/2007        wer         Added hasNonDeletedRows to help OasisGrid to determine if an empty row should be added.
 * 03/22/2010       James       Issue#105489 Added isIgnoreGridSortOrder
 * 10/06/2014       awu         157694 - Added getString(String columnName, String defaultValue).
 * ---------------------------------------------------
 */
public interface BaseResultSet {

    /**
     * Return the number of rows in the Result Set.
     */
    int getRowCount();

    /**
     * Return the number of columns in each row of the Result Set.
     */
    int getColumnCount();

    /**
     * Position the current row index to before the first row.
     */
    void beforeFirst();

    /**
     * Position the current row index to before the first row.
     *
     * @return true if rows exist; otherwise, false.
     */
    public boolean first();

    /**
     * Move the current row index to the next row.
     *
     * @return true if there is a row at the current row index; otherwise, false;
     */
    boolean next();

    /**
     * Add an empty row to the Result Set with empty values for each defined field.
     */
    void addEmptyRow();

    /**
     * Return true if the column exists; otherwise, false.
     */
    public boolean hasColumn(String columnName);

    /**
     * Return the name of the column at the specified column index.
     * The column indexes start at 1.
     *
     * @throws  IndexOutOfBoundsException if index is out of range.
     */
    String getColumnName(int columnIndex);

    /**
     * Return the value at the specified column index.
     * The column indexes start at 1.
     *
     * @throws  IndexOutOfBoundsException if index is out of range.
     */
    Object get(int columnIndex);

    /**
     * Return the value mapped to the specified column name.
     *
     * @throws  IllegalArgumentException if the desired column is not found.
     */
    Object get(String columnName);

    /**
     * Return the String value at the specified column index.
     * The column indexes start at 1.
     *
     * @throws  IndexOutOfBoundsException if index is out of range.
     */
    String getString(int columnIndex);

    /**
     * Return the String value mapped to the specified column name.
     *
     * @throws  IllegalArgumentException if the desired column is not found.
     */
    String getString(String columnName);

    /**
     * Return the String value of the specified column name.
     * If the value of the column name is null, or the column doesn't exist,
     * then return defaultValue.
     * @param columnName
     * @param defaultValue
     * @return
     */
    String getString(String columnName, String defaultValue);

    /**
     * Return the Date value at the specified column index.
     * The column indexes start at 1.
     *
     * @throws  IndexOutOfBoundsException if index is out of range.
     */
    Date getDate(int columnIndex);

    /**
     * Return the Date value mapped to the specified column name.
     *
     * @throws IllegalArgumentException if the desired column is not found.
     */
    Date getDate(String columnName);

    /**
     * Return a debug string of this Result Set.
     */
    String toString();

    /**
     * Sort the RecordSet using the provided Comparator.
     */
    void sort(Comparator comparator);

    /********************************************************************************/
    /* Convenience methods for grid processing.                                     */
    /********************************************************************************/
    /**
     * Return the update indicator, specifying if this row has been inserted, deleted, updated or not changed.
     * If the update indicator is not found, UpdateIndicator.NOT_CHANGED is returned by default.
     */
    char getUpdateInd();

    /**
     * Return the display indicator, specifying if this row can be displayed.
     * If the display indicator is not found, 'Y' is returned by default.
     */
    char getDisplayInd();

    /**
     * Return the edit indicator, specifying if this row can be edited.
     * If the display indicator is not found, 'Y' is returned by default.
     */
    char getEditInd();

    /**
     * Return OBR enforced update Indicator
     */
    String getOBREnforcingUpdateIndicator();

    /**
     * Return OBR enforced field list
     */
    String getOBREnforcingFieldList();

    /**
     * Return OBR Consequence field list
     */
    String getOBRConsequenceFieldList();

    /**
     * Return OBR All Accessed field list
     */
    String getOBRAllAccessedFieldList();

    /**
     * Return OBR enforced result
     */
    String getOBREnforcedResult();

    /**
     * Return true if there are row contained that have the Update Indicator set to something other than deleted - 'D'.
     * Otherwise, return false.
     * @return true if there are non-deleted rows; otherwise false.
     */
    boolean hasNonDeletedRows();

    /**
     * Return true if on first row
     */
    boolean isFirst();

    /**
     * Return true if on last row
     */
    public boolean isLast();

    /**
     * go to previous row
     *
     * @return true if on a real row
     */
    public boolean previous();

    /**
     * Data is from client
     * @return true if data is from client
     */
    public boolean isDataFromClient();
}
