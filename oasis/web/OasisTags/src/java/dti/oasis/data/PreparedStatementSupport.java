package dti.oasis.data;

import dti.oasis.util.ByteArray;
import dti.oasis.util.DatabaseUtils;
import oracle.jdbc.OracleTypes;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * This class provides helper methods for setting input parameters on a PreparedStatement.
 * If the value is null, the setNull method on the PreparedStatement is invoked.
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
 * 09/26/2008      Larry       Issue 86826 DB connection leakage change
 * 09/21/2010      fcb         111824 - added support for Oracle XMLType
 * 07/23/2012      tcheng      135128 - added setClog to support for oracle CLOB
 * ---------------------------------------------------
 */
public class PreparedStatementSupport {

    public static PreparedStatementSupport getInstance(PreparedStatement ps) {
        return new PreparedStatementSupport(ps);
    }

    public void setStatement(PreparedStatement ps) {
        if (null == ps) {
            throw new NullPointerException("The PreparedStatement parameter is null.");
        }
        m_ps = ps;
    }

    public void setTinyInt(int parameterIndex, Short x) throws SQLException {
        if (null == x) {
            m_ps.setNull(parameterIndex, java.sql.Types.TINYINT);
        } else {
            m_ps.setInt(parameterIndex, x.intValue());
        }
    }

    public void setSmallInt(int parameterIndex, Short x) throws SQLException {
        if (null == x) {
            m_ps.setNull(parameterIndex, java.sql.Types.SMALLINT);
        } else {
            m_ps.setInt(parameterIndex, x.intValue());
        }
    }

    public void setInt(int parameterIndex, Integer x) throws SQLException {
        if (null == x) {
            m_ps.setNull(parameterIndex, java.sql.Types.INTEGER);
        } else {
            m_ps.setInt(parameterIndex, x.intValue());
        }
    }

    public void setBigInt(int parameterIndex, Integer x) throws SQLException {
        if (null == x) {
            m_ps.setNull(parameterIndex, java.sql.Types.BIGINT);
        } else {
            m_ps.setInt(parameterIndex, x.intValue());
        }
    }

    public void setLong(int parameterIndex, Long x) throws SQLException {
        if (null == x) {
            m_ps.setNull(parameterIndex, java.sql.Types.BIGINT);
        } else {
            m_ps.setLong(parameterIndex, x.longValue());
        }
    }

    public void setFloat(int parameterIndex, Float x) throws SQLException {
        if (null == x) {
            m_ps.setNull(parameterIndex, java.sql.Types.FLOAT);
        } else {
            m_ps.setFloat(parameterIndex, x.floatValue());
        }
    }

    public void setDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        if (null == x) {
            m_ps.setNull(parameterIndex, java.sql.Types.DECIMAL);
        } else {
            m_ps.setBigDecimal(parameterIndex, x);
        }
    }

    public void setInteger(int parameterIndex, Integer x) throws SQLException {
        if (null == x) {
            m_ps.setNull(parameterIndex, java.sql.Types.INTEGER);
        } else {
            m_ps.setInt(parameterIndex, x.intValue());
        }
    }

    public void setDouble(int parameterIndex, Double x) throws SQLException {
        if (null == x) {
            m_ps.setNull(parameterIndex, java.sql.Types.DOUBLE);
        } else {
            m_ps.setDouble(parameterIndex, x.doubleValue());
        }
    }

    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        if (null == x) {
            m_ps.setNull(parameterIndex, java.sql.Types.DECIMAL);
        } else {
            m_ps.setBigDecimal(parameterIndex, x);
        }
    }

    public void setBit(int parameterIndex, String x) throws SQLException {
        if (null == x) {
            m_ps.setNull(parameterIndex, java.sql.Types.BIT);
        } else {
            m_ps.setString(parameterIndex, x);
        }
    }

    public void setBoolean(int parameterIndex, Boolean x) throws SQLException {
        // http://www.oracle.com/technology/tech/java/sqlj_jdbc/htdocs/jdbc_faq.htm#34_05
        // It is not feasible for Oracle JDBC drivers to support calling arguments or return values of the PL/SQL types TABLE (now known as indexed-by tables), RESULT SET, RECORD, or BOOLEAN
        if (null == x) {
            m_ps.setNull(parameterIndex, java.sql.Types.BOOLEAN);
        } else {
            m_ps.setBoolean(parameterIndex, x.booleanValue());
        }
    }

    public void setBinary(int parameterIndex, String x) throws SQLException {
        if (null == x) {
            m_ps.setNull(parameterIndex, java.sql.Types.BINARY);
        } else {
            m_ps.setBinaryStream(parameterIndex, new BufferedInputStream(new ByteArrayInputStream(x.getBytes())), x.length());
        }
    }

    public void setBinary(int parameterIndex, ByteArray x) throws SQLException {
        if (null == x) {
            m_ps.setNull(parameterIndex, java.sql.Types.BINARY);
        } else {
            m_ps.setBinaryStream(parameterIndex, new BufferedInputStream(new ByteArrayInputStream(x.getBytes())), x.length());
        }
    }

    public void setVarBinary(int parameterIndex, String x) throws SQLException {
        if (null == x) {
            m_ps.setNull(parameterIndex, java.sql.Types.VARBINARY);
        } else {
            m_ps.setBinaryStream(parameterIndex, new BufferedInputStream(new ByteArrayInputStream(x.getBytes())), x.length());
        }
    }

    public void setVarBinary(int parameterIndex, ByteArray x) throws SQLException {
        if (null == x) {
            m_ps.setNull(parameterIndex, java.sql.Types.VARBINARY);
        } else {
            m_ps.setBinaryStream(parameterIndex, new BufferedInputStream(new ByteArrayInputStream(x.getBytes())), x.length());
        }
    }

    public void setLongVarBinary(int parameterIndex, String x) throws SQLException {
        if (null == x) {
            m_ps.setNull(parameterIndex, java.sql.Types.LONGVARBINARY);
        } else {
            m_ps.setBinaryStream(parameterIndex, new BufferedInputStream(new ByteArrayInputStream(x.getBytes())), x.length());
        }
    }

    public void setLongVarBinary(int parameterIndex, ByteArray x) throws SQLException {
        if (null == x) {
            m_ps.setNull(parameterIndex, java.sql.Types.LONGVARBINARY);
        } else {
            m_ps.setBinaryStream(parameterIndex, new BufferedInputStream(new ByteArrayInputStream(x.getBytes())), x.length());
        }
    }

    public void setBlob(int parameterIndex, String x) throws SQLException {
        if (null == x) {
            m_ps.setNull(parameterIndex, java.sql.Types.BLOB);
        } else {
            m_ps.setBinaryStream(parameterIndex, new BufferedInputStream(new ByteArrayInputStream(x.getBytes())), x.length());
        }
    }

    public void setBlob(int parameterIndex, InputStream x) throws SQLException {
        if (null == x) {
            m_ps.setNull(parameterIndex, java.sql.Types.BLOB);
        } else {
            try {
                m_ps.setBinaryStream(parameterIndex, x, x.available());
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    public void setBlob(int parameterIndex, ByteArray x) throws SQLException {
        if (null == x) {
            m_ps.setNull(parameterIndex, java.sql.Types.BLOB);
        } else {
            m_ps.setBinaryStream(parameterIndex, new BufferedInputStream(new ByteArrayInputStream(x.getBytes())), x.length());
        }
    }

    public void setClob(int parameterIndex, String x) throws SQLException {
        if (null == x) {
            m_ps.setNull(parameterIndex, java.sql.Types.CLOB);
        } else {
            m_ps.setAsciiStream(parameterIndex, new BufferedInputStream(new ByteArrayInputStream(x.getBytes())), x.length());
        }
    }

    public void setClob(int parameterIndex, Reader x) throws SQLException {
        if (null == x) {
            m_ps.setNull(parameterIndex, java.sql.Types.CLOB);
        } else {
            m_ps.setClob(parameterIndex, x);
        }
    }

    public void setClob(int parameterIndex, ByteArray x) throws SQLException {
        if (null == x) {
            m_ps.setNull(parameterIndex, java.sql.Types.CLOB);
        } else {
            m_ps.setAsciiStream(parameterIndex, new BufferedInputStream(new ByteArrayInputStream(x.getBytes())), x.length());
        }
    }

    public void setInputStream(int parameterIndex, InputStream x, int length) throws SQLException {
        if (null == x) {
            m_ps.setNull(parameterIndex, java.sql.Types.LONGVARBINARY);
        } else {
            m_ps.setBinaryStream(parameterIndex, x, length);
        }
    }

    public void setChar(int parameterIndex, String x) throws SQLException {
        if (null == x) {
            m_ps.setNull(parameterIndex, java.sql.Types.CHAR);
        } else {
            m_ps.setString(parameterIndex, x);
        }
    }

    public void setVarChar(int parameterIndex, String x) throws SQLException {
        if (null == x) {
            m_ps.setNull(parameterIndex, java.sql.Types.VARCHAR);
        } else {
            m_ps.setString(parameterIndex, x);
        }
    }

    public void setLongVarChar(int parameterIndex, String x) throws SQLException {
        if (null == x) {
            m_ps.setNull(parameterIndex, java.sql.Types.LONGVARCHAR);
        } else {
            m_ps.setString(parameterIndex, x);
        }
    }

    public void setString(int parameterIndex, String x) throws SQLException {
        if (null == x) {
            m_ps.setNull(parameterIndex, java.sql.Types.VARCHAR);
        } else {
            m_ps.setString(parameterIndex, x);
        }
    }

    public void setDate(int parameterIndex, java.sql.Date x) throws SQLException {
        if (null == x) {
            m_ps.setNull(parameterIndex, java.sql.Types.DATE);
        } else {
            m_ps.setDate(parameterIndex, x);
        }
    }

    public void setDate(int parameterIndex, java.util.Date x) throws SQLException {
        if (null == x) {
            m_ps.setNull(parameterIndex, java.sql.Types.DATE);
        } else {
            m_ps.setDate(parameterIndex, new java.sql.Date(x.getTime()));
        }
    }

    public void setTimestamp(int parameterIndex, java.sql.Timestamp x) throws SQLException {
        if (null == x) {
            m_ps.setNull(parameterIndex, java.sql.Types.DATE);
        } else {
            m_ps.setTimestamp(parameterIndex, x);
        }
    }

    public void setTimestamp(int parameterIndex, java.util.Date x) throws SQLException {
        if (null == x) {
            m_ps.setNull(parameterIndex, java.sql.Types.DATE);
        } else {
            m_ps.setTimestamp(parameterIndex, new java.sql.Timestamp(x.getTime()));
        }
    }

    public void setXml(int parameterIndex, Object x) throws SQLException {
        if (null == x) {
            m_ps.setNull(parameterIndex, OracleTypes.OPAQUE);
        } else {
            m_ps.setObject(parameterIndex, x);
        }
    }

    public void setNull(int parameterIndex) throws SQLException {
        m_ps.setNull(parameterIndex, java.sql.Types.NULL);
    }

    public void setObject(int parameterIndex, Object x) throws SQLException {
        m_ps.setObject(parameterIndex, x);
    }

    public void setObject(int parameterIndex, Object x, int targetSqlType)
        throws SQLException {
        m_ps.setObject(parameterIndex, x, targetSqlType);
    }

    public boolean execute() throws SQLException {
        return m_ps.execute();
    }

    public void close() throws SQLException {
        DatabaseUtils.close(m_ps);
    }

    protected PreparedStatementSupport() {
    }

    protected PreparedStatementSupport(PreparedStatement ps) {
        setStatement(ps);
    }

    private PreparedStatement m_ps;
}
