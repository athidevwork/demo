package dti.oasis.util;

import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.NClob;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 13, 2007
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/26/2008       Larry       Issue 86826 DB connection leakage change
 * ---------------------------------------------------
 */
public class ConnectionTrace implements Connection {


    public ConnectionTrace(Connection delegate) {
        m_delegate = delegate;
        c_openConnectionCount++;
        System.out.println("ConnectionTrace(): after opening connection, c_openConnectionCount = " + c_openConnectionCount);
    }


    @Override
    public int getHoldability() throws SQLException {
        return m_delegate.getHoldability();
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return m_delegate.getTransactionIsolation();
    }

    @Override
    public void clearWarnings() throws SQLException {
        m_delegate.clearWarnings();
    }

    @Override
    public void close() throws SQLException {

        if (m_delegate != null) {
            DatabaseUtils.close(m_delegate);
        }
        c_openConnectionCount--;
        System.out.println("ConnectionTrace.close(): after closing connection, c_openConnectionCount = " + c_openConnectionCount);

    }

    @Override
    public void commit() throws SQLException {
        m_delegate.commit();
    }

    @Override
    public void rollback() throws SQLException {
        m_delegate.rollback();
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return m_delegate.getAutoCommit();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return m_delegate.isClosed();
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return m_delegate.isReadOnly();
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        m_delegate.setHoldability(holdability);
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        m_delegate.setTransactionIsolation(level);
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        m_delegate.setAutoCommit(autoCommit);
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        m_delegate.setReadOnly(readOnly);
    }

    @Override
    public String getCatalog() throws SQLException {
        return m_delegate.getCatalog();
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        m_delegate.setCatalog(catalog);
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return m_delegate.getMetaData();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return m_delegate.getWarnings();
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return m_delegate.setSavepoint();
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        m_delegate.releaseSavepoint(savepoint);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        m_delegate.rollback(savepoint);
    }

    @Override
    public Statement createStatement() throws SQLException {
        return m_delegate.createStatement();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return m_delegate.createStatement(resultSetType, resultSetConcurrency);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return m_delegate.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public Map getTypeMap() throws SQLException {
        return m_delegate.getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        m_delegate.setTypeMap(map);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return m_delegate.nativeSQL(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return m_delegate.prepareCall(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return m_delegate.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return m_delegate.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return m_delegate.prepareStatement(sql);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return m_delegate.prepareStatement(sql, autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return m_delegate.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return m_delegate.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return m_delegate.prepareStatement(sql, columnIndexes);
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return m_delegate.setSavepoint(name);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return m_delegate.prepareStatement(sql, columnNames);
    }

    @Override
    public Clob createClob() throws SQLException {
        return m_delegate.createClob();
    }

    @Override
    public Blob createBlob() throws SQLException {
        return m_delegate.createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        return m_delegate.createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return m_delegate.createSQLXML();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return m_delegate.isValid(timeout);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        m_delegate.setClientInfo(name, value);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        m_delegate.setClientInfo(properties);
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return m_delegate.getClientInfo(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return m_delegate.getClientInfo();
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return m_delegate.createArrayOf(typeName, elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return m_delegate.createStruct(typeName, attributes);
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        m_delegate.setSchema(schema);
    }

    @Override
    public String getSchema() throws SQLException {
        return m_delegate.getSchema();
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        m_delegate.abort(executor);
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        m_delegate.setNetworkTimeout(executor, milliseconds);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return m_delegate.getNetworkTimeout();
    }

    @Override
    public Object unwrap(Class class1) throws SQLException {
        if(class1.isInstance(this))
            return this;
        else
            throw new SQLException(this + " is not an instance of " + class1);
    }

    @Override
    public boolean isWrapperFor(Class class1) throws SQLException {
        return class1.isInstance(this);
    }

    private Connection m_delegate;

    public static int c_openConnectionCount = 0;
}
