package dti.oasis.data;

import dti.oasis.app.AppException;
import dti.oasis.util.DatabaseUtils;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;
import java.math.BigDecimal;

/**
 * This class provides helper methods for getting result set columns from a ResultSet.
 * If the value was null, null is returned.
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
 *  06/16/2008      wer         Fixed to use colNumber from ColumnDesc
 *  07/09/2008      yhchen      #83894 change getColumnValue method to call resultset.getObject to retrieve column value
 *                              The number type value will be evaluated to java BigDecimal values
 * 09/26/2008       Larry       Issue 86826 DB connection leakage change
 * 09/23/2009       Fred        Issue 96884. Extend Internationalization to Date / Time fields
 * ---------------------------------------------------
 */
public class ResultSetSupport {

    /**
     * Return an instance of the <tt>ResultSetSupport</tt> class for operating on the provided <tt>ResultSet</tt>.
     *
     * @param rs the <tt>ResultSet</tt>
     * @return An instance of <tt>ResultSetSuport</tt> object.
     */
    public static ResultSetSupport getInstance(ResultSet rs) {
        if (null == rs) {
            throw new NullPointerException("The ResultSet parameter is null.");
        }
        return new ResultSetSupport(rs);
    }

    /**
     * Set the <tt>ResultSet</tt>.
     *
     * @param rs the <tt>ResultSet</tt>.
     */
    public void setResultSet(ResultSet rs) {
        if (null == rs) {
            throw new NullPointerException("The ResultSet parameter is null.");
        }
        m_rs = rs;
    }

    /**
     * Moves the cursor down one row from its current position.
     * A ResultSet cursor is initially positioned before the first row; the
     * first call to next makes the first row the current row; the
     * second call makes the second row the current row, and so on.
     * <p/>
     * <P>If an input stream is open for the current row, a call
     * to the method <code>next</code> will
     * implicitly close it. The ResultSet's warning chain is cleared
     * when a new row is read.
     *
     * @return true if the new current row is valid; false if there
     *         are no more rows
     * @throws java.sql.SQLException if a database access error occurs
     */
    public boolean next() throws SQLException {
        return m_rs.next();
    }

    //======================================================================
    // Methods for accessing results by column index
    //======================================================================

    /**
     * Gets the value of a column in the current row as a Boolean.
     *
     * @param columnIndex the first column is 1, the second is 2, ...
     * @return the column value; if the value is SQL NULL, the result is false
     * @throws SQLException if a database access error occurs
     */
    public Boolean getBoolean(int columnIndex) throws SQLException {
        boolean value = m_rs.getBoolean(columnIndex);

        if (m_rs.wasNull())
            return null;

        return Boolean.valueOf(value);
    }

    /**
     * Gets the value of a column in the current row as a Byte.
     *
     * @param columnIndex the first column is 1, the second is 2, ...
     * @return the column value; if the value is SQL NULL, the result is 0
     * @throws SQLException if a database access error occurs
     */
    public Byte getByte(int columnIndex) throws SQLException {
        byte value = m_rs.getByte(columnIndex);

        if (m_rs.wasNull())
            return null;

        return new Byte(value);
    }

    /**
     * Gets the value of a column in the current row as a Short.
     *
     * @param columnIndex the first column is 1, the second is 2, ...
     * @return the column value; if the value is SQL NULL, the result is 0
     * @throws SQLException if a database access error occurs
     */
    public Short getShort(int columnIndex) throws SQLException {
        short value = m_rs.getShort(columnIndex);

        if (m_rs.wasNull())
            return null;

        return new Short(value);
    }

    /**
     * Gets the value of a column in the current row as an Integer.
     *
     * @param columnIndex the first column is 1, the second is 2, ...
     * @return the column value; if the value is SQL NULL, the result is 0
     * @throws SQLException if a database access error occurs
     */
    public Integer getInteger(int columnIndex) throws SQLException {
        int value = m_rs.getInt(columnIndex);

        if (m_rs.wasNull())
            return null;

        return new Integer(value);
    }

    /**
     * Gets the value of a column in the current row as a Long.
     *
     * @param columnIndex the first column is 1, the second is 2, ...
     * @return the column value; if the value is SQL NULL, the result is 0
     * @throws SQLException if a database access error occurs
     */
    public Long getLong(int columnIndex) throws SQLException {
        long value = m_rs.getLong(columnIndex);

        if (m_rs.wasNull())
            return null;

        return new Long(value);
    }

    /**
     * Gets the value of a column in the current row as a Float.
     *
     * @param columnIndex the first column is 1, the second is 2, ...
     * @return the column value; if the value is SQL NULL, the result is 0
     * @throws SQLException if a database access error occurs
     */
    public Float getFloat(int columnIndex) throws SQLException {
        float value = m_rs.getFloat(columnIndex);

        if (m_rs.wasNull())
            return null;

        return new Float(value);
    }

    /**
     * Gets the value of a column in the current row as a Double.
     *
     * @param columnIndex the first column is 1, the second is 2, ...
     * @return the column value; if the value is SQL NULL, the result is 0
     * @throws SQLException if a database access error occurs
     */
    public Double getDouble(int columnIndex) throws SQLException {
        double value = m_rs.getDouble(columnIndex);

        if (m_rs.wasNull())
            return null;

        return new Double(value);
    }

    /**
     * Gets the value of a column in the current row as a BigDecimal.
     *
     * @param columnIndex the first column is 1, the second is 2, ...
     * @return the column value; if the value is SQL NULL, the result is 0
     * @throws SQLException if a database access error occurs
     */
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        BigDecimal value = m_rs.getBigDecimal(columnIndex);

        if (m_rs.wasNull())
            return null;

        return value;
    }

    //======================================================================
    // Methods for accessing results by column name
    //======================================================================

    /**
     * Gets the value of a column in the current row as a Boolean.
     *
     * @param columnName the SQL name of the column
     * @return the column value; if the value is SQL NULL, the result is false
     * @throws SQLException if a database access error occurs
     */
    public Boolean getBoolean(String columnName) throws SQLException {
        boolean value = m_rs.getBoolean(columnName);

        if (m_rs.wasNull())
            return null;

        return Boolean.valueOf(value);
    }

    /**
     * Gets the value of a column in the current row as a Byte.
     *
     * @param columnName the SQL name of the column
     * @return the column value; if the value is SQL NULL, the result is 0
     * @throws SQLException if a database access error occurs
     */
    public Byte getByte(String columnName) throws SQLException {
        byte value = m_rs.getByte(columnName);

        if (m_rs.wasNull())
            return null;

        return new Byte(value);
    }

    /**
     * Gets the value of a column in the current row as a Short.
     *
     * @param columnName the SQL name of the column
     * @return the column value; if the value is SQL NULL, the result is 0
     * @throws SQLException if a database access error occurs
     */
    public Short getShort(String columnName) throws SQLException {
        short value = m_rs.getShort(columnName);

        if (m_rs.wasNull())
            return null;

        return new Short(value);
    }

    /**
     * Gets the value of a column in the current row as an Integer.
     *
     * @param columnName the SQL name of the column
     * @return the column value; if the value is SQL NULL, the result is 0
     * @throws SQLException if a database access error occurs
     */
    public Integer getInt(String columnName) throws SQLException {
        int value = m_rs.getInt(columnName);

        if (m_rs.wasNull())
            return null;

        return new Integer(value);
    }

    /**
     * Gets the value of a column in the current row as a Long.
     *
     * @param columnName the SQL name of the column
     * @return the column value; if the value is SQL NULL, the result is 0
     * @throws SQLException if a database access error occurs
     */
    public Long getLong(String columnName) throws SQLException {
        long value = m_rs.getLong(columnName);

        if (m_rs.wasNull())
            return null;

        return new Long(value);
    }

    /**
     * Gets the value of a column in the current row as a Float.
     *
     * @param columnName the SQL name of the column
     * @return the column value; if the value is SQL NULL, the result is 0
     * @throws SQLException if a database access error occurs
     */
    public Float getFloat(String columnName) throws SQLException {
        float value = m_rs.getFloat(columnName);

        if (m_rs.wasNull())
            return null;

        return new Float(value);
    }

    /**
     * Gets the value of a column in the current row as a Double.
     *
     * @param columnName the SQL name of the column
     * @return the column value; if the value is SQL NULL, the result is 0
     * @throws SQLException if a database access error occurs
     */
    public Double getDouble(String columnName) throws SQLException {
        double value = m_rs.getDouble(columnName);

        if (m_rs.wasNull())
            return null;

        return new Double(value);
    }

    public java.util.Date getDate(int columnIndex) throws SQLException {
        java.sql.Date date = m_rs.getDate(columnIndex);
        return date == null ? null : new java.util.Date(date.getTime());
    }

    public java.util.Date getTime(int columnIndex) throws SQLException {
        java.sql.Time time = m_rs.getTime(columnIndex);
        return time == null ? null : new java.util.Date(time.getTime());
    }

    public java.util.Date getTimestamp(int columnIndex) throws SQLException {
        java.sql.Timestamp ts = m_rs.getTimestamp(columnIndex);
        return ts == null ? null : new java.util.Date(ts.getTime());
    }

    public java.util.Date getDate(String columnName) throws SQLException {
        java.sql.Date date = m_rs.getDate(columnName);
        return date == null ? null : new java.util.Date(date.getTime());
    }

    public java.util.Date getTime(String columnName) throws SQLException {
        java.sql.Time time = m_rs.getTime(columnName);
        return time == null ? null : new java.util.Date(time.getTime());
    }

    public java.util.Date getTimestamp(String columnName) throws SQLException {
        java.sql.Timestamp ts = m_rs.getTimestamp(columnName);
        return ts == null ? null : new java.util.Date(ts.getTime());
    }

    public Object getColumnValue(ColumnDesc c) {
        Object outResult = null;
        try {
            outResult = getObject(c.colNumber);
            //Get the full date-time
            if (outResult != null && outResult.getClass().getName().equals("java.sql.Date")) {
                outResult = getTimestamp(c.colNumber);
            }
        }
        catch (SQLException e) {
            throw new AppException("Parameter Column Index<" + c.colNumber + "> is out of bounds. Assuming that this column was removed from the query, and the stored procedure parameter metadata does not reflect the change yet.", e);
        }
        return outResult;
    }

    public Object getObject(int columnIndex) throws SQLException {
        return m_rs.getObject(columnIndex);
    }

    public Object getObject(String columnName) throws SQLException {
        return m_rs.getObject(columnName);
    }

    public String getString(int columnIndex) throws SQLException {
        return m_rs.getString(columnIndex);
    }

    public String getString(String columnName) throws SQLException {
        return m_rs.getString(columnName);
    }

    public InputStream getInputStream(int columnIndex) throws SQLException {
        return m_rs.getBinaryStream(columnIndex);
    }

    public void close() throws SQLException {
        DatabaseUtils.close(m_rs);
    }

    /**
     * Construct an instance of the <tt>ResultSetSupport</tt> class for operating on the provided <tt>ResultSet</tt>.
     *
     * @param rs the <tt>ResultSet</tt>.
     */
    protected ResultSetSupport(ResultSet rs) {
        m_rs = rs;
    }

    private ResultSet m_rs;

    private static Map c_getByColumnIndexMethods = new Hashtable();
}
