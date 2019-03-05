package dti.oasis.util;

import dti.oasis.converter.Converter;
import dti.oasis.converter.ConverterFactory;
import dti.oasis.data.DataLoadProcessor;
import dti.oasis.data.DefaultDataLoadProcessor;
import dti.oasis.recordset.SortOrder;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Emulates a disconnected JDBC ResultSet
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 3, 2003
 *
 * @author jbe
 */
/*
* Revision Date    Revised By  Description
* ---------------------------------------------------
* 1/12/2004    jbe     Added setXXXX methods
* 2/6/2004     jbe     Added Logging
* 8/9/2004     jbe     Added setCurrent and getCurrent
* 10/14/2004   jbe     Added DataRow inner class and ROW_xxx constants
* 1/28/2005    jbe     Added Col Name/# map and getXXX methods given name
* 6/1/2005     jbe     Added copyRows methods, isNull methods and check
*                      for null in getXXXX primitive methods and set to 0 when SQL null.
* 01/23/2007   wer     Refactord to implement the BaseResultSet interface;
*                      Made DataRow and DateContainer public;
*                      Added support for displayInd and editInd;
*                      Added support for the DataLoadProcessor to post process rows and the result set;
* 01/29/2007   wer     Fixed setString and setDate to use the proper column index.
* 03/15/2007   jmp     Add sort method, current not implemented.
* 12/4/2007    wer     Added hasNonDeletedRows to help OasisGrid to determine if an empty row should be added.
* 01/18/2008   FWCH    Modified method getDate(int col) to eliminate ClassCastException.
* 03/22/2010   James   Issue#105489 Added isIgnoreGridSortOrder
* 11/02/2010   Michael   Issue#113530  If there are no records found that meet search criteria, there is still a message,
*                        '1 record, Page 1 of 1' displayed above the List of Notes section.
* 12/29/2012   kshen   Issue 127160. Implement method sort.
* 10/06/2014   awu     157694 - Added getString(String colName, String defaultValue).
* ---------------------------------------------------
* @see dti.oasis.util.DisconnectedColumnMetaData
*/
public class DisconnectedResultSet implements java.io.Serializable, BaseResultSet {

    private ArrayList rows = new ArrayList();
    private ArrayList cols = new ArrayList();
    private HashMap colNameNumberMap = new HashMap();
    private int current = 0;
    private boolean allRowsLoaded;
    private String m_OBREnforcingFieldList = "";
    private String m_OBRConsequenceFieldList = "";
    private String m_OBRAllAccessedFieldList = "";
    private String m_OBREnforcingUpdateIndicator = "";

    public static final char ROW_UPDATED = 'Y';
    public static final char ROW_INSERTED = 'I';
    public static final char ROW_DELETED = 'D';
    public static final char ROW_UNTOUCHED = 'N';

    /**
     * Inner Class holding row data and status
     */
    public class DataRow implements Serializable {
        ArrayList rowData;
        char updateInd;
        char displayInd;
        char editInd;
        String obrEnforcedResult = "";

        public DataRow(ArrayList rowData, char updateInd) {
            this.rowData = rowData;
            this.updateInd = updateInd;
            this.displayInd = 'Y';
            this.editInd = 'Y';
        }

        public DataRow(ArrayList rowData, char updateInd, char displayInd) {
            this.rowData = rowData;
            this.updateInd = updateInd;
            this.displayInd = displayInd;
            this.editInd = 'Y';
        }
        public DataRow(ArrayList rowData, char updateInd, char displayInd, char editInd) {
            this.rowData = rowData;
            this.updateInd = updateInd;
            this.displayInd = displayInd;
            this.editInd = editInd;
        }

        public DataRow() {
        }

        /**
         * Get the field with the given 1-based field index.
         */
        public Object get(int index) {
            return rowData.get(index - 1);
        }

        public Object get(String colName) {
            return get(getColNum(colName));
        }

        /**
         * Set the field with the given 1-based field index.
         */
        public void set(int index, Object o) {
            rowData.set(index - 1, o);
        }

        public void setRowData(ArrayList rowData) {
            this.rowData = rowData;
        }

        public void setUpdateInd(char updateInd) {
            this.updateInd = updateInd;
        }

        public ArrayList getRowData() {
            return rowData;
        }

        public char getUpdateInd() {
            return updateInd;
        }

        public char getDisplayInd() {
            return displayInd;
        }

        public void setDisplayInd(char displayInd) {
            this.displayInd = displayInd;
        }

        public char getEditInd() {
            return editInd;
        }

        public void setEditInd(char editInd) {
            this.editInd = editInd;
        }

        public String getObrEnforcedResult() {
            return obrEnforcedResult;
        }

        public void setObrEnforcedResult(String obrEnforcedResult) {
            this.obrEnforcedResult = obrEnforcedResult;
        }

        public void hide() {
            setDisplayInd('N');
        }

        public void protectDataRow() {
            setEditInd('N');
        }

        public String toString() {
            final StringBuffer buf = new StringBuffer();
            buf.append("dti.oasis.util.DisconnectedResultSet.DataRow");
            buf.append("{rowData=").append(rowData);
            buf.append(",updateInd=").append(updateInd);
            buf.append(",displayInd=").append(displayInd);
            buf.append(",editInd=").append(editInd);
            buf.append(",obrEnforcedResult=").append(obrEnforcedResult);
            buf.append('}');
            return buf.toString();
        }
    }

    public static class DataRowComparator implements Comparator {
        public DataRowComparator(int dataColumnIndex) {
            this(dataColumnIndex, true, SortOrder.ASC, null);
        }

        public DataRowComparator(int dataColumnIndex, SortOrder sortOrder) {
            this(dataColumnIndex, true, sortOrder, null);
        }

        public DataRowComparator(int dataColumnIndex, Converter converter) {
            this(dataColumnIndex, true, SortOrder.ASC, converter);
        }

        public DataRowComparator(int dataColumnIndex, SortOrder sortOrder, Converter converter) {
            this(dataColumnIndex, true, sortOrder, converter);
        }

        public DataRowComparator(int dataColumnIndex, boolean nullsAreGreater, SortOrder sortOrder, Converter converter) {
            m_dataRowColumnComparators = new ArrayList<DataRowColumnComparator>();
            m_dataRowColumnComparators.add(new DataRowColumnComparator(dataColumnIndex, nullsAreGreater, sortOrder, converter));
        }

        public void addDataRowColumnComparator(int dataColumnIndex) {
            addDataRowColumnComparator(dataColumnIndex, true, SortOrder.ASC, null);
        }

        public void addDataRowColumnComparator(int dataColumnIndex, SortOrder sortOrder) {
            addDataRowColumnComparator(dataColumnIndex, true, sortOrder, null);
        }
        public void addDataRowColumnComparator(int dataColumnIndex, Converter converter) {
            addDataRowColumnComparator(dataColumnIndex, true, SortOrder.ASC, converter);
        }
        public void addDataRowColumnComparator(int dataColumnIndex, SortOrder sortOrder, Converter converter) {
            addDataRowColumnComparator(dataColumnIndex, true, sortOrder, converter);
        }

        public void addDataRowColumnComparator(int dataColumnIndex, boolean nullsAreGreater, SortOrder sortOrder, Converter converter) {
            m_dataRowColumnComparators.add(new DataRowColumnComparator(dataColumnIndex, nullsAreGreater, sortOrder, converter));
        }

        public int compare(Object object1, Object object2) {
            int compareResult = 0;

            DataRow dataRowA = (DataRow) object1;
            DataRow dataRowB = (DataRow) object2;

            for (DataRowColumnComparator dataRowColumnComparator : m_dataRowColumnComparators) {
                compareResult = dataRowColumnComparator.compare(
                        dataRowA.get(dataRowColumnComparator.getDataColumnIndex()),
                        dataRowB.get(dataRowColumnComparator.getDataColumnIndex()));
                if (compareResult != 0) {
                    break;
                }
            }

            return compareResult;
        }

        private List<DataRowColumnComparator> m_dataRowColumnComparators;
    }

    public static class DataRowColumnComparator implements Comparator {
        public DataRowColumnComparator(int dataColumnIndex) {
            this(dataColumnIndex, true, SortOrder.ASC, null);
        }

        public DataRowColumnComparator(int dataColumnIndex, SortOrder sortOrder) {
            this(dataColumnIndex, true, sortOrder, null);
        }

        public DataRowColumnComparator(int dataColumnIndex, Converter converter) {
            this(dataColumnIndex, true, SortOrder.ASC, converter);
        }

        public DataRowColumnComparator(int dataColumnIndex, SortOrder sortOrder, Converter converter) {
            this(dataColumnIndex, true, sortOrder, converter);
        }

        public DataRowColumnComparator(int dataColumnIndex, boolean nullsAreGreater, SortOrder sortOrder, Converter converter) {
            m_dataColumnIndex = dataColumnIndex;
            m_nullsAreGreater = nullsAreGreater;
            m_sortOrder = sortOrder;
            m_converter = converter;
        }

        public boolean hasConverter() {
            return m_converter != null;
        }

        public int getDataColumnIndex() {
            return m_dataColumnIndex;
        }

        public int compare(Object object1, Object object2) {
            int compareResult = 0;
            if (object1 == object2) {
                return 0;
            }

            if (object1 == null) {
                return (m_nullsAreGreater ? 1 : -1) * m_sortOrder.intValue();
            }
            if (object2 == null) {
                return (m_nullsAreGreater ? -1 : 1) * m_sortOrder.intValue();
            }

            Comparable valA, valB;
            if (hasConverter()) {
                valA = (Comparable) m_converter.convert(object1);
                valB = (Comparable) m_converter.convert(object2);
            } else {
                valA = (Comparable) object1;
                valB = (Comparable) object2;
            }

            if (valA == valB) {
                return 0;
            }
            if (null == valA) {
                compareResult = m_nullsAreGreater ? 1 : -1;
            } else if (null == valB) {
                compareResult = m_nullsAreGreater ? -1 : 1;
            } else {
                compareResult = valA.compareTo(valB);
            }

            return compareResult * m_sortOrder.intValue();
        }

        private int m_dataColumnIndex;
        private boolean m_nullsAreGreater = false;
        private SortOrder m_sortOrder;
        private Converter m_converter = null;
    }



    /**
     * Returns current row
     */
    public int getCurrent() {
        return current;
    }

    /**
     * Sets current row.
     *
     * @param newCurrent
     * @throws IllegalArgumentException Thrown if newCurrent is less than 1 or greater
     *                                  than the total number of rows in the set.
     */
    public void setCurrent(int newCurrent) throws IllegalArgumentException {
        if (newCurrent < 1)
            throw new IllegalArgumentException("Current row " + newCurrent + " is less than 1.  Current " +
                    "row remains unchanged.");
        if (newCurrent > rows.size())
            throw new IllegalArgumentException("Current row " + newCurrent + " is greater than " +
                    "total number of rows in set [" + rows.size() + "].  Current row remains unchanged.");
        current = newCurrent;
    }

    public static class DateContainer implements Serializable {
        String strValue;
        java.util.Date value;

        DateContainer(java.util.Date d, String s) {
            strValue = s;
            value = d;
        }

        public java.util.Date getDate(){
            return value;
        }

        public String getDateAsString(){
            return strValue;
        }

        public String toString() {
            final StringBuffer buf = new StringBuffer();
            buf.append("dti.oasis.util.DisconnectedResultSet.DateContainer");
            buf.append("{strValue=").append(strValue);
            buf.append(",value=").append(value);
            buf.append('}');
            return buf.toString();
        }
    }

    /**
     * Empty default constructor
     */
    public DisconnectedResultSet() {
        Logger l = LogUtils.enterLog(getClass(), "constructor");
        l.exiting(getClass().getName(), "constructor");
    }

    /**
     * True if all rows in ResultSet were loaded into this object
     */
    public boolean isAllRowsLoaded() {
        return allRowsLoaded;
    }

    /**
     * Constructor, builds DisconnectedResultSet
     * loads maxRows # rows
     *
     * @param rs      JDBC ResultSet
     * @param maxRows Max # rows to load
     * @throws SQLException
     */
    public DisconnectedResultSet(ResultSet rs, int maxRows) throws SQLException {
        this(rs, maxRows, DEFAULT_DATA_LOAD_PROCESSOR);
    }

    /**
     * Constructor, builds DisconnectedResultSet
     * loads maxRows # rows
     *
     * @param rs      JDBC ResultSet
     * @param maxRows Max # rows to load
     * @throws SQLException
     */
    public DisconnectedResultSet(ResultSet rs, int maxRows, DataLoadProcessor dataLoadProcessor) throws SQLException {
        Logger l = LogUtils.enterLog(getClass(), "constructor", new Object[]{rs, new Integer(maxRows)});
        int count = 0;
        setMetaData(rs);
        allRowsLoaded = true;
        while (rs.next()) {
            if (count == maxRows) {
                allRowsLoaded = false;
                break;
            }
            count++;
            addRow(rs, dataLoadProcessor);
        }
        dataLoadProcessor.postProcessDisconnectedResultSet(this);

        l.exiting(getClass().getName(), "constructor", this);

    }

    /**
     * Constructor, builds DisconnectedResultSet, loads all rows
     *
     * @param rs JDBC ResultSet
     * @throws SQLException
     */
    public DisconnectedResultSet(ResultSet rs) throws SQLException {
        this(rs, DEFAULT_DATA_LOAD_PROCESSOR);
    }

    /**
     * Constructor, builds DisconnectedResultSet, loads all rows
     *
     * @param rs JDBC ResultSet
     * @throws SQLException
     */
    public DisconnectedResultSet(ResultSet rs, DataLoadProcessor dataLoadProcessor) throws SQLException {
        Logger l = LogUtils.enterLog(getClass(), "constructor", rs);
        setMetaData(rs);
        while (rs.next())
            addRow(rs, dataLoadProcessor);
        dataLoadProcessor.postProcessDisconnectedResultSet(this);
        allRowsLoaded = true;
        l.exiting(getClass().getName(), "constructor", this);
    }

    private void setMetaData(ResultSet rs) throws SQLException {
        Logger l = LogUtils.enterLog(getClass(), "setMetaData", rs);
        ResultSetMetaData rsmd = rs.getMetaData();
        int colCount = rsmd.getColumnCount();
        for (int i = 1; i <= colCount; i++) {
            cols.add(new DisconnectedColumnMetaData(rsmd, i));
            String nm = rsmd.getColumnName(i).toUpperCase();
            // check for dup column name.  If we have a dup, append a number
            // to the column name.  Keep looping until we find a valid number.
            // Stop at 200 for sanity sake.
            if (colNameNumberMap.containsKey(nm)) {
                for (int j = 1; j < 201; j++) {
                    String nm1 = new StringBuffer(nm).append("_____").append(j).toString();
                    if (!colNameNumberMap.containsKey(nm1)) {
                        nm = nm1;
                        break;
                    }
                }
            }
            colNameNumberMap.put(nm, new Integer(i));
        }
        l.exiting(getClass().getName(), "setMetaData");
    }

    /**
     * Add an empty row to object
     */
    public void addEmptyRow() {
        Logger l = LogUtils.enterLog(getClass(), "addEmptyRow");
        int sz = cols.size();
        ArrayList row = new ArrayList();
        if (sz > 0)
            row.add("-9999");
        for (int i = 1; i < sz; i++) {
            switch (((DisconnectedColumnMetaData) cols.get(i)).getColumnType()) {
                case Types.TIMESTAMP:
                case Types.TIME:
                case Types.DATE:
                    java.util.Date dt1 = null;
                    row.add(new DateContainer(dt1, null));
                    break;
                case Types.CHAR:
                case Types.VARCHAR:
                    row.add("");
                    break;
                case Types.DOUBLE:
                case Types.FLOAT:
                case Types.INTEGER:
                    row.add("0");
                    break;
                default:
                    row.add(null);
                    break;

            }
        }
        rows.add(new DataRow(row, 'N','N','N'));
        l.exiting(getClass().getName(), "addEmptyRow");
    }

    /**
     * Add current row of JDBC ResultSet
     *
     * @param rs JDBC ResultSet
     * @param dataLoadProcessor
     * @throws SQLException
     */
    public void addRow(ResultSet rs, DataLoadProcessor dataLoadProcessor) throws SQLException {
        Logger l = LogUtils.enterLog(getClass(), "addRow", rs);
        ArrayList row = new ArrayList();
        int colCount = cols.size();
        for (int i = 1; i <= colCount; i++) {
            int type = getColumn(i).getColumnType();
            switch (type) {
                case Types.DATE:
                case Types.TIME:
                case Types.TIMESTAMP:
                    row.add(new DateContainer(rs.getTimestamp(i), rs.getString(i)));
                    break;
                default:
                    row.add(rs.getString(i));
            }
        }
        DataRow dataRow = new DataRow(row, 'N');
        if (dataLoadProcessor.postProcessDataRow(dataRow)) {
            rows.add(dataRow);
        }
        l.exiting(getClass().getName(), "addRow");
    }

    /**
     * go to next row
     *
     * @return true if we're on a row
     */
    public boolean next() {
        Logger l = LogUtils.enterLog(getClass(), "next");
        current++;
        boolean rc = false;
        if (rows.size() >= current)
            rc = true;
        else
            afterLast();
        l.exiting(getClass().getName(), "next", new Boolean(rc));
        return rc;
    }

    /**
     * go to last row
     *
     * @return true if rows exist
     */
    public boolean last() {
        Logger l = LogUtils.enterLog(getClass(), "last");
        current = rows.size();
        boolean rc = (current > 0);
        l.exiting(getClass().getName(), "last", new Boolean(rc));
        return rc;
    }

    /**
     * go to first row
     *
     * @return true if rows exist
     */
    public boolean first() {
        Logger l = LogUtils.enterLog(getClass(), "first");
        current = (rows.size() > 0) ? 1 : 0;
        boolean rc = (current > 0);
        l.exiting(getClass().getName(), "first", new Boolean(rc));
        return rc;
    }

    /**
     * go to previous row
     *
     * @return true if on a real row
     */
    public boolean previous() {
        Logger l = LogUtils.enterLog(getClass(), "previous");
        current -= (current > 0) ? 1 : 0;
        boolean rc = (current > 0);
        l.exiting(getClass().getName(), "previous", new Boolean(rc));
        return rc;
    }

    /**
     * Data is from client
     *
     * @return true if data is from client
     */
    public boolean isDataFromClient() {
        return false;
    }

    /**
     * Return true if on first row
     */
    public boolean isFirst() {
        return (current == 1);
    }

    /**
     * Return true if on last row
     */
    public boolean isLast() {
        return (current == rows.size());
    }

    /**
     * Position before 1st row
     */
    public void beforeFirst() {
        current = 0;
    }

    /**
     * Position after last row
     */
    public void afterLast() {
        current = rows.size() + 1;
    }

    /**
     * Return String value of column
     *
     * @param colName column name
     * @return String value
     */
    public String getString(String colName) {
        LogUtils.enterLog(getClass(), "getString", colName);
        return getString(getColNum(colName));
    }

    /**
     * Return the String value of to the specified column name.
     * If the value of the column name is null or the column name doesn't exist,
     * Then return defaultValue.
     * @param colName
     * @param defaultValue
     * @return
     */
    public String getString(String colName, String defaultValue) {
        String value = null;
        Integer colNum;
        Object o = colNameNumberMap.get(colName.toUpperCase());
        if (o != null && o instanceof Integer) {
            colNum = ((Integer) o).intValue();
            value = getString(colNum);
        }
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }

    /**
     * Return String value of column
     *
     * @param col column #
     * @return String value
     */
    public String getString(int col) {
        Logger l = LogUtils.enterLog(getClass(), "getString", new Integer(col));
        Object o = ((DataRow) rows.get(current - 1)).get(col);
        String rc = null;
        if (o instanceof DateContainer)
            rc = ((DateContainer) o).strValue;
        else
            rc = (String) o;
        l.exiting(getClass().getName(), "getString", rc);
        return rc;
    }

    /**
     * Return double value of column
     *
     * @param colName column name
     * @return double primitive
     * @throws NumberFormatException
     */
    public double getDouble(String colName) throws NumberFormatException {
        LogUtils.enterLog(getClass(), "getDouble", colName);
        return getDouble(getColNum(colName));

    }

    /**
     * Return couble value of column
     *
     * @param col column #
     * @return double primitive, 0 if SQL null
     * @throws NumberFormatException
     */
    public double getDouble(int col) throws NumberFormatException {
        Logger l = LogUtils.enterLog(getClass(), "getDouble", new Integer(col));
        String val = getString(col);
        double rc = StringUtils.isBlank(val) ? 0 : Double.parseDouble(val);
        l.exiting(getClass().getName(), "getDouble", new Double(rc));
        return rc;
    }

    /**
     * Return int value of column
     *
     * @param colName column name
     * @return int primitive
     * @throws NumberFormatException
     */
    public int getInt(String colName) throws NumberFormatException {
        LogUtils.enterLog(getClass(), "getInt", colName);
        return getInt(getColNum(colName));
    }

    /**
     * Return int value of column
     *
     * @param col column #
     * @return int value, 0 if SQL null
     * @throws NumberFormatException
     */
    public int getInt(int col) throws NumberFormatException {
        Logger l = LogUtils.enterLog(getClass(), "getInt", new Integer(col));
        String val = getString(col);
        int rc = StringUtils.isBlank(val) ? 0 : Integer.parseInt(val);
        l.exiting(getClass().getName(), "getInt", new Integer(rc));
        return rc;
    }

    /**
     * Return long value of column
     *
     * @param colName column name
     * @return long primitive
     * @throws NumberFormatException
     */
    public long getLong(String colName) throws NumberFormatException {
        LogUtils.enterLog(getClass(), "getLong", colName);
        return getLong(getColNum(colName));

    }

    /**
     * Return long value of column
     *
     * @param col column #
     * @return long value, 0 if SQL null
     * @throws NumberFormatException
     */
    public long getLong(int col) throws NumberFormatException {
        Logger l = LogUtils.enterLog(getClass(), "getLong", new Integer(col));
        String val = getString(col);
        long rc = StringUtils.isBlank(val) ? 0 : Long.parseLong(val);
        l.exiting(getClass().getName(), "getLong", new Long(rc));
        return rc;
    }

    /**
     * Return float value of column
     *
     * @param colName column name
     * @return float primitive
     * @throws NumberFormatException
     */
    public float getFloat(String colName) throws NumberFormatException {
        LogUtils.enterLog(getClass(), "getFloat", colName);
        return getFloat(getColNum(colName));

    }

    /**
     * Return double value of column
     *
     * @param col column #
     * @return float value, 0 if SQL null
     * @throws NumberFormatException
     */
    public float getFloat(int col) throws NumberFormatException {
        Logger l = LogUtils.enterLog(getClass(), "getFloat", new Integer(col));
        String val = getString(col);
        float rc = StringUtils.isBlank(val) ? 0 : Float.parseFloat(val);
        l.exiting(getClass().getName(), "getFloat", new Float(rc));
        return rc;
    }

    /**
     * Return Date value of column
     *
     * @param colName column name
     * @return java.util.Date object
     */
    public java.util.Date getDate(String colName) {
        LogUtils.enterLog(getClass(), "getDate", colName);
        return getDate(getColNum(colName));
    }

    /**
     * Return the name of the column at the specified column index.
     * The column indexes start at 1.
     *
     * @throws IndexOutOfBoundsException if index is out of range.
     */
    public String getColumnName(int columnIndex) {
        return getColumn(columnIndex).getColumnName();
    }

    /**
     * Return Date value of column
     *
     * @param col column #
     * @return java.util.Date object
     */
    public java.util.Date getDate(int col) {
        Logger l = LogUtils.enterLog(getClass(), "getDate", new Integer(col));
        Object o = ((DataRow) rows.get(current - 1)).get(col);
        DateContainer rc = null;
        try {
            if (o == null) {
                return null;
            } else if (o instanceof DateContainer) {
                rc = (DateContainer) o;
            }
            else {
                return (Date) ConverterFactory.getInstance().getConverter(Date.class).convert(Date.class, o);
            }
        }
        catch (ClassCastException ce) {
            l.warning("Invalidate date: value=" + o);
            throw ce;
        }
        l.exiting(getClass().getName(), "getDate", rc.value);
        return rc.value;
    }

    /**
     * Return true if the column name exists; otherwise, false.
     */
    public boolean hasColumn(String colName) {
        return colNameNumberMap.get(colName.toUpperCase()) != null;
    }

    /**
     * Return column # given column name
     *
     * @param colName column name
     * @return 1 based column number
     */
    public int getColNum(String colName) {
        Object o = colNameNumberMap.get(colName.toUpperCase());
        if (o != null && o instanceof Integer) {
            return ((Integer) o).intValue();
        }
        else {
            throw new IllegalArgumentException("Column Name " + colName + " not found.");
        }
    }

    /**
     * Return object value of column
     *
     * @param colName column name
     * @return Object
     */
    public Object get(String colName) {
        LogUtils.enterLog(getClass(), "get", colName);
        return get(getColNum(colName));
    }

    /**
     * Return object value of column
     *
     * @param col column #
     * @return Object
     */
    public Object get(int col) {
        Logger l = LogUtils.enterLog(getClass(), "get", new Integer(col));
        Object o = ((DataRow) rows.get(current - 1)).get(col);
        l.exiting(getClass().getName(), "get", o);
        if (o instanceof DateContainer)
            return ((DateContainer) o).value;

        else
            return (String) o;
    }

    /**
     * Return # of columns
     */
    public int getColumnCount() {
        Logger l = LogUtils.enterLog(getClass(), "getColumnCount");
        l.exiting(getClass().getName(), "getColumnCount", new Integer(cols.size()));
        return cols.size();
    }

    /**
     * Return # of rows loaded
     */
    public int getRowCount() {
        Logger l = LogUtils.enterLog(getClass(), "getRowCount");
        l.exiting(getClass().getName(), "getRowCount", new Integer(rows.size()));
        return rows.size();
    }

    /**
     * Return true if there are row contained that have the Update Indicator set to something other than deleted - 'D'.
     * Otherwise, return false.
     *
     * @return true if there are non-deleted rows; otherwise false.
     */
    public boolean hasNonDeletedRows() {
        Logger l = LogUtils.enterLog(getClass(), "hasNonDeletedRows");

        boolean hasNonDeletedRows = false;
        int size = rows.size();
        for (int i = 0; i < size; i++) {
            char updateIndicator = ((DataRow) rows.get(i)).getUpdateInd();
            if (updateIndicator != 'D') {
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
     * Return Column MetaData
     *
     * @param col
     * @return DisconnectedColumnMetaData
     */
    public DisconnectedColumnMetaData getColumn(int col) {
        Logger l = LogUtils.enterLog(getClass(), "getColumn", new Integer(col));

        DisconnectedColumnMetaData rc = (DisconnectedColumnMetaData) cols.get(col - 1);
        l.exiting(getClass().getName(), "getColumn", rc);
        return rc;

    }

    /**
     * Set the String value into the column in the current row
     *
     * @param col column #
     * @param val value
     */
    public void setString(int col, String val) {
        Logger l = LogUtils.enterLog(getClass(), "setString", new Object[]
        {new Integer(col), val});
        ((DataRow) rows.get(current - 1)).set(col, val);
        l.exiting(getClass().getName(), "setString");
    }

    /**
     * Set the float value into the column in the current row
     *
     * @param col column #
     * @param val value
     */
    public void setFloat(int col, float val) {
        Logger l = LogUtils.enterLog(getClass(), "setFloat", new Object[]
        {new Integer(col), new Float(val)});
        setString(col, String.valueOf(val));
        l.exiting(getClass().getName(), "setFloat");
    }

    /**
     * Set the double value into the column in the current row
     *
     * @param col column #
     * @param val value
     */
    public void setDouble(int col, double val) {
        Logger l = LogUtils.enterLog(getClass(), "setDouble", new Object[]
        {new Integer(col), new Double(val)});

        setString(col, String.valueOf(val));
        l.exiting(getClass().getName(), "setDouble");
    }

    /**
     * Set the int value into the column in the current row
     *
     * @param col column #
     * @param val value
     */
    public void setInt(int col, int val) {
        Logger l = LogUtils.enterLog(getClass(), "setInt", new Object[]
        {new Integer(col), new Integer(val)});

        setString(col, String.valueOf(val));
        l.exiting(getClass().getName(), "setInt");
    }

    /**
     * Set the long value into the column in the current row
     *
     * @param col column #
     * @param val value
     */
    public void setLong(int col, long val) {
        Logger l = LogUtils.enterLog(getClass(), "setLong", new Object[]
        {new Integer(col), new Long(val)});

        setString(col, String.valueOf(val));
        l.exiting(getClass().getName(), "setLong");
    }

    /**
     * Set the Date value into the column in the current row
     *
     * @param col column #
     * @param val value
     */
    public void setDate(int col, java.util.Date val) {
        Logger l = LogUtils.enterLog(getClass(), "setDate", new Object[]
        {new Integer(col), val});

        SimpleDateFormat df = (SimpleDateFormat) SimpleDateFormat.getInstance();
        df.applyPattern("mm/dd/yyyy");
        ((DataRow) rows.get(current - 1)).set(col, new DateContainer(val, df.format(val)));
        l.exiting(getClass().getName(), "setDate");
    }

    /**
     * Deletes the current row.  If the current row was the last row, it
     * positions the current row at the new last row.
     */
    public void deleteRow() {
        Logger l = LogUtils.enterLog(getClass(), "deleteRow");
        if (current > 0) {
            rows.remove(current - 1);
            if (current > rows.size())
                last();
        }
        l.exiting(getClass().getName(), "deleteRow");

    }

    /**
     * Finds the first row where the value in the column "findCol"
     * matches the value "findValue".  It starts looking in the first row.
     * If a match is found, that row becomes the current row and this method
     * returns true.
     *
     * @param findCol   Column # in which to find the matching value
     * @param findValue Value to compare against.
     * @return true if a row is found, false if not
     */
    public boolean findRow(int findCol, String findValue) {
        return findRow(findCol, findValue, 1);
    }

    /**
     * Finds the first row where the value in the column "findCol"
     * matches the value "findValue".  It starts looking in row "startRow".
     * If a match is found, that row becomes the current row and this method
     * returns true.
     *
     * @param findCol   Column # in which to find the matching value
     * @param findValue Value to compare against.
     * @return true if a row is found, false if not
     */
    public boolean findRow(int findCol, String findValue, int startRow) {
        Logger l = LogUtils.enterLog(getClass(), "findRow",
                new Object[]{String.valueOf(findCol), findValue, String.valueOf(startRow)});

        if (startRow < 1)
            throw new IllegalArgumentException("Invalid startRow (<1)");
        if (findCol < 1 || findCol > cols.size())
            throw new IllegalArgumentException("Invalid column index");
        boolean rc = false;
        int sz = rows.size();
        for (int r = startRow; r <= sz; r++) {
            setCurrent(r);
            if (getString(findCol).equals(findValue)) {
                rc = true;
                break;
            }
        }
        l.exiting(getClass().getName(), "findRow", String.valueOf(rc));
        return rc;
    }

    /**
     * Set the UPDATE IND flag for the current row
     *
     * @param updateInd Y for updated, I for inserted, D for deleted, N for untouched.
     */
    public void setUpdateInd(char updateInd) {
        ((DataRow) rows.get(current - 1)).setUpdateInd(updateInd);
    }

    /**
     * Return the UPDATE_IND flag for the current row
     *
     * @return Y for updated, I for inserted, D for deleted, N for untouched.
     */
    public char getUpdateInd() {
        return ((DataRow) rows.get(current - 1)).getUpdateInd();
    }

    /**
     * Set the DISPLAY_IND flag for the current row
     *
     * @param displayInd Y for yes, N for no.
     */
    public void setDisplayInd(char displayInd) {
        ((DataRow) rows.get(current - 1)).setDisplayInd(displayInd);
    }

    /**
     * Return the DISPLAY_IND flag for the current row
     *
     * @return Y for yes, N for no.
     */
    public char getDisplayInd() {
        return ((DataRow) rows.get(current - 1)).getDisplayInd();
    }

    /**
     * Set the EDIT_IND flag for the current row
     *
     * @param editInd Y for yes, N for no.
     */
    public void setEditInd(char editInd) {
        ((DataRow) rows.get(current - 1)).setEditInd(editInd);
    }

    /**
     * Return the EDIT_IND flag for the current row
     *
     * @return Y for yes, N for no.
     */
    public char getEditInd() {
        return ((DataRow) rows.get(current - 1)).getEditInd();
    }

    /**
     * Set the OBR_ENFORCED_RESULT flag for the current row
     *
     * @param obrEnforcedResult
     */
    public void setOBREnforcedResult(String obrEnforcedResult) {
        ((DataRow) rows.get(current - 1)).setObrEnforcedResult(obrEnforcedResult);
    }

    /**
     * Return the OBR_ENFORCED_RESULT flag for the current row
     */
    public String getOBREnforcedResult() {
        return ((DataRow) rows.get(current - 1)).getObrEnforcedResult();
    }

    public String getOBREnforcingUpdateIndicator() {
        return m_OBREnforcingUpdateIndicator;
    }

    public void setOBREnforcingUpdateIndicator(String OBRUpdateIndicator) {
        m_OBREnforcingUpdateIndicator = OBRUpdateIndicator;
    }

    public String getOBREnforcingFieldList() {
        return m_OBREnforcingFieldList;
    }

    public void setOBREnforcingFieldList(String OBREnforcingFieldList) {
        m_OBREnforcingFieldList = OBREnforcingFieldList;
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

    /**
     * Sort the RecordSet using the provided Comparator.
     */
    public void sort(Comparator comparator) {
        Collections.sort(rows, comparator);
    }

    /**
     * Copies 1 or more rows from one DisconnectedResultSet to this DisconnectedResultSet.
     * The column metadata must match in terms of datatype, precision and scale.  Column name
     * is not considered.  All rows starting from the startRow parameter will be copied.
     * @param sourceRS Source DisconnectedResultSet to copy row(s) from
     * @param startRow Starting row in source DisconnectedResultSet
     */
    public void copyRows(DisconnectedResultSet sourceRS, int startRow) {
        copyRows(sourceRS, startRow,0);
    }

    /**
     * Copies 1 or more rows from one DisconnectedResultSet to this DisconnectedResultSet.
     * The column metadata must match in terms of datatype, precision and scale.  Column name
     * is not considered.
     * @param sourceRS Source DisconnectedResultSet to copy row(s) from
     * @param startRow Starting row in source DisconnectedResultSet
     * @param endRow Ending row (inclusive) in source DisconnectedResultSet.  If you pass 0
     * or a number larger than the total # of rows in the source DisconnectedResultSet, it will
     * be set to the rowcount.
     */
    public void copyRows(DisconnectedResultSet sourceRS, int startRow, int endRow) {
        Logger l = LogUtils.enterLog(getClass(), "copyRows",
                new Object[]{sourceRS, new Integer(startRow), new Integer(endRow)});
        if (sourceRS == null)
            throw new IllegalArgumentException("Invalid sourceRS");
        if (startRow < 1)
            throw new IllegalArgumentException("Invalid startRow=" + startRow);
        if (endRow!=0 && endRow < startRow)
            throw new IllegalArgumentException("Invalid startRow/endRow combination (" + startRow + '/' + endRow + ')');

        // if we are starting after the last row, just return
        if(startRow>sourceRS.getRowCount())
            return;

        // default end row to last row
        if (endRow > sourceRS.getRowCount() || endRow==0)
            endRow = sourceRS.getRowCount();
        // validate column counts match
        int sourceCount = sourceRS.getColumnCount();
        int destCount = getColumnCount();
        if (sourceCount != destCount)
            throw new IllegalArgumentException("Source ResultSet and Destination ResultSet have different column counts.");
        // validate column metadata matches
        for (int i = 1; i <= sourceCount; i++) {
            DisconnectedColumnMetaData sourceCol = sourceRS.getColumn(i);
            DisconnectedColumnMetaData destCol = getColumn(i);
            if (sourceCol.getColumnType() != destCol.getColumnType() ||
                    sourceCol.getPrecision() != destCol.getPrecision() ||
                    sourceCol.getScale() != destCol.getScale())
                throw new IllegalArgumentException("Source ResultSet and Destination ResultSet have different column metadata.");
        }
        // copy the rows
        for (int i = startRow; i <= endRow; i++) {
            rows.add((DataRow) sourceRS.rows.get(i - 1));
        }
        l.exiting(getClass().getName(), "copyRows");
    }

    /**
     * Returns whether a column is SQL null
     * @param col column number
     * @return true if SQL Null
     */
    public boolean isNull(int col) {
        return StringUtils.isBlank(getString(col));
    }

    /**
     * Returns whether a column is SQL null
     * @param colName column name
     * @return true if SQL Null
     */
    public boolean isNull(String colName) {
        return StringUtils.isBlank(getString(getColNum(colName)));
    }

    /**
     * Return String representation
     */
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("dti.oasis.util.DisconnectedResultSet");
        buf.append("{# of rows=").append(rows.size());
        buf.append(", # of cols=").append(cols.size());
        buf.append(", rows=").append(rows);
        buf.append(", cols=").append(cols);
        buf.append(", current=").append(current);
        buf.append(", allRowsLoaded=").append(allRowsLoaded);
        buf.append('}');
        return buf.toString();
    }

    private static final DefaultDataLoadProcessor DEFAULT_DATA_LOAD_PROCESSOR = new DefaultDataLoadProcessor();
}
