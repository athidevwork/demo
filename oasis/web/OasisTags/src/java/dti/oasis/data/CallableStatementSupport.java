package dti.oasis.data;

import dti.oasis.app.AppException;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Blob;
import java.sql.Clob;
import java.util.Hashtable;
import java.util.Map;

/**
 * This class provides helper methods for getting ouput parameters on a CallableStatement.
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
 * 02/29/2008       wer         Added support for getAsciiInputStream and getBinaryInputStream
 * 07/09/2008       yhchen      #83894 change getColumnValue method to call resultset.getObject to retrieve column value
 *                              The number type value will be evaluated to java BigDecimal values
 * ---------------------------------------------------
 */
public class CallableStatementSupport extends PreparedStatementSupport {

    public static CallableStatementSupport getInstance(CallableStatement cs) {
        return new CallableStatementSupport(cs);
    }

    public String getString(int parameterIndex) throws SQLException {
        return m_cs.getString(parameterIndex);
    }

    public Boolean getBoolean(int parameterIndex) throws SQLException {
        boolean value = m_cs.getBoolean(parameterIndex);

        if (m_cs.wasNull())
            return null;

        return Boolean.valueOf(value);
    }

    public Byte getByte(int parameterIndex) throws SQLException {
        byte value = m_cs.getByte(parameterIndex);

        if (m_cs.wasNull())
            return null;

        return new Byte(value);
    }

    public Short getShort(int parameterIndex) throws SQLException {
        short value = m_cs.getShort(parameterIndex);

        if (m_cs.wasNull())
            return null;

        return new Short(value);
    }

    public Integer getInteger(int parameterIndex) throws SQLException {
        int value = m_cs.getInt(parameterIndex);

        if (m_cs.wasNull())
            return null;

        return new Integer(value);
    }

    public Long getLong(int parameterIndex) throws SQLException {
        long value = m_cs.getLong(parameterIndex);

        if (m_cs.wasNull())
            return null;

        return new Long(value);
    }

    public Float getFloat(int parameterIndex) throws SQLException {
        float value = m_cs.getFloat(parameterIndex);

        if (m_cs.wasNull())
            return null;

        return new Float(value);
    }

    public Double getDouble(int parameterIndex) throws SQLException {
        double value = m_cs.getDouble(parameterIndex);

        if (m_cs.wasNull())
            return null;

        return new Double(value);
    }

    public java.util.Date getDate(int parameterIndex) throws SQLException {
        java.sql.Date date = m_cs.getDate(parameterIndex);
        return date == null ? null : new java.util.Date(date.getTime());
    }

    public java.util.Date getTime(int parameterIndex) throws SQLException {
        java.sql.Time time = m_cs.getTime(parameterIndex);
        return time == null ? null : new java.util.Date(time.getTime());
    }

    public java.util.Date getTimestamp(int parameterIndex) throws SQLException {
        java.sql.Timestamp timestamp = m_cs.getTimestamp(parameterIndex);
        return timestamp == null ? null : new java.util.Date(timestamp.getTime());
    }

    public Object getObject(int parameterIndex) throws SQLException {
        return m_cs.getObject(parameterIndex);
    }

    public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
        return m_cs.getBigDecimal(parameterIndex);
    }

    public InputStream getAsciiInputStream(int parameterIndex) throws SQLException {
        Clob clob = m_cs.getClob(parameterIndex);
        return clob == null ? null : clob.getAsciiStream();
    }

    public InputStream getBinaryInputStream(int parameterIndex) throws SQLException {
        Blob blob = m_cs.getBlob(parameterIndex);
        return blob == null ? null : blob.getBinaryStream();
    }

    public Object getColumnValue(ColumnDesc c){
        Object outResult = null;
        try{
            outResult = getObject(c.colNumber);
        }
        catch (SQLException e) {
            throw new AppException("Parameter Column Index<" + c.colNumber + "> is out of bounds. Assuming that this column was removed from the query, and the stored procedure parameter metadata does not reflect the change yet.", e);
        }
        return outResult;
    }   

    public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
        m_cs.registerOutParameter(parameterIndex, sqlType);
    }

    public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
        m_cs.registerOutParameter(parameterIndex, sqlType, scale);
    }

    public void registerOutParameter(int paramIndex, int sqlType, String typeName) throws SQLException {
        m_cs.registerOutParameter(paramIndex, sqlType, typeName);
    }

    public void setCallableStatement(CallableStatement cs) {
        setStatement(cs);
        m_cs = cs;
    }

    public CallableStatement getCallableStatement() {
        return m_cs;
    }

    public Connection getConnection() throws SQLException {
        return m_cs.getConnection();
    }

    public CallableStatementSupport() {
        super();
    }

    protected CallableStatementSupport(CallableStatement cs) {
        super(cs);
        m_cs = cs;
    }

    private CallableStatement m_cs;

    private static Map m_getByColumnIndexMethods = new Hashtable();
}
