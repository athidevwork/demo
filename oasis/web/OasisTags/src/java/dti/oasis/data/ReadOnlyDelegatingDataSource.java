package dti.oasis.data;

import dti.oasis.util.LogUtils;
import org.springframework.jdbc.datasource.DelegatingDataSource;

import javax.sql.DataSource;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This DataSource will roll back the change before closing it.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 27, 2006
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class ReadOnlyDelegatingDataSource extends DelegatingDataSource {

    /**
     * constructor
     */
    public ReadOnlyDelegatingDataSource() {

    }

    /**
     * constructor with data source
     *
     * @param targetDataSource
     */
    public ReadOnlyDelegatingDataSource(DataSource targetDataSource) {
        super(targetDataSource);
    }


    @Override
    public Connection getConnection() throws SQLException {
        if (l.isLoggable(Level.FINE)) {
            l.entering(getClass().getName(), "getConnection");
        }
        Connection connection = super.getConnection();
        connection = new ReadonlyConnection(connection);
        connection.setAutoCommit(false);
        l.exiting(getClass().getName(), "getConnection", connection);
        return connection;
    }

    /**
     * This connection is read only, it will roll back before closing
     */
    class ReadonlyConnection implements Connection {

        public ReadonlyConnection(Connection tarConnection) {
            m_targetConnection = tarConnection;
        }

        @Override
        public int getHoldability() throws SQLException {
            return m_targetConnection.getHoldability();
        }

        @Override
        public int getTransactionIsolation() throws SQLException {
            return m_targetConnection.getTransactionIsolation();
        }

        @Override
        public void clearWarnings() throws SQLException {
            m_targetConnection.clearWarnings();
        }

        @Override
        public void close() throws SQLException {
            // rollback before closing
            if (!m_targetConnection.getAutoCommit()){
                m_targetConnection.rollback();
            }
            m_targetConnection.close();
        }

        @Override
        public void commit() throws SQLException {
            m_targetConnection.commit();
        }

        @Override
        public void rollback() throws SQLException {
            m_targetConnection.rollback();
        }

        @Override
        public boolean getAutoCommit() throws SQLException {
            return m_targetConnection.getAutoCommit();
        }

        @Override
        public boolean isClosed() throws SQLException {
            return m_targetConnection.isClosed();
        }

        @Override
        public boolean isReadOnly() throws SQLException {
            return m_targetConnection.isReadOnly();
        }

        @Override
        public void setHoldability(int holdability) throws SQLException {
            m_targetConnection.setHoldability(holdability);
        }

        @Override
        public void setTransactionIsolation(int level) throws SQLException {
            m_targetConnection.setTransactionIsolation(level);
        }

        @Override
        public void setAutoCommit(boolean autoCommit) throws SQLException {
            m_targetConnection.setAutoCommit(autoCommit);
        }

        @Override
        public void setReadOnly(boolean readOnly) throws SQLException {
            m_targetConnection.setReadOnly(readOnly);
        }

        @Override
        public String getCatalog() throws SQLException {
            return m_targetConnection.getCatalog();
        }

        @Override
        public void setCatalog(String catalog) throws SQLException {
            m_targetConnection.setCatalog(catalog);
        }

        @Override
        public DatabaseMetaData getMetaData() throws SQLException {
            return m_targetConnection.getMetaData();
        }

        @Override
        public SQLWarning getWarnings() throws SQLException {
            return m_targetConnection.getWarnings();
        }

        @Override
        public Savepoint setSavepoint() throws SQLException {
            return m_targetConnection.setSavepoint();
        }

        @Override
        public void releaseSavepoint(Savepoint savepoint) throws SQLException {
            m_targetConnection.releaseSavepoint(savepoint);
        }

        @Override
        public void rollback(Savepoint savepoint) throws SQLException {
            m_targetConnection.rollback(savepoint);
        }

        @Override
        public Statement createStatement() throws SQLException {
            return m_targetConnection.createStatement();
        }

        @Override
        public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
            return m_targetConnection.createStatement(resultSetType, resultSetConcurrency);
        }

        @Override
        public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return m_targetConnection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        @Override
        public Map getTypeMap() throws SQLException {
            return m_targetConnection.getTypeMap();
        }

        @Override
        public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
            m_targetConnection.setTypeMap(map);
        }

        @Override
        public String nativeSQL(String sql) throws SQLException {
            return m_targetConnection.nativeSQL(sql);
        }

        @Override
        public CallableStatement prepareCall(String sql) throws SQLException {
            return m_targetConnection.prepareCall(sql);
        }

        @Override
        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            return m_targetConnection.prepareCall(sql, resultSetType, resultSetConcurrency);
        }

        @Override
        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return m_targetConnection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        @Override
        public PreparedStatement prepareStatement(String sql) throws SQLException {
            return m_targetConnection.prepareStatement(sql);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
            return m_targetConnection.prepareStatement(sql, autoGeneratedKeys);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            return m_targetConnection.prepareStatement(sql, resultSetType, resultSetConcurrency);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return m_targetConnection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
            return m_targetConnection.prepareStatement(sql, columnIndexes);
        }

        @Override
        public Savepoint setSavepoint(String name) throws SQLException {
            return m_targetConnection.setSavepoint(name);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
            return m_targetConnection.prepareStatement(sql, columnNames);
        }

        @Override
        public Clob createClob() throws SQLException {
            return m_targetConnection.createClob();
        }

        @Override
        public Blob createBlob() throws SQLException {
            return m_targetConnection.createBlob();
        }

        @Override
        public NClob createNClob() throws SQLException {
            return m_targetConnection.createNClob();
        }

        @Override
        public SQLXML createSQLXML() throws SQLException {
            return m_targetConnection.createSQLXML();
        }

        @Override
        public boolean isValid(int timeout) throws SQLException {
            return m_targetConnection.isValid(timeout);
        }

        @Override
        public void setClientInfo(String name, String value) throws SQLClientInfoException {
            m_targetConnection.setClientInfo(name, value);
        }

        @Override
        public void setClientInfo(Properties properties) throws SQLClientInfoException {
            m_targetConnection.setClientInfo(properties);
        }

        @Override
        public String getClientInfo(String name) throws SQLException {
            return m_targetConnection.getClientInfo(name);
        }

        @Override
        public Properties getClientInfo() throws SQLException {
            return m_targetConnection.getClientInfo();
        }

        @Override
        public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
            return m_targetConnection.createArrayOf(typeName, elements);
        }

        @Override
        public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
            return m_targetConnection.createStruct(typeName, attributes);
        }

        @Override
        public void setSchema(String schema) throws SQLException {
            m_targetConnection.setSchema(schema);
        }

        @Override
        public String getSchema() throws SQLException {
            return m_targetConnection.getSchema();
        }

        @Override
        public void abort(Executor executor) throws SQLException {
            m_targetConnection.abort(executor);
        }

        @Override
        public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
            m_targetConnection.setNetworkTimeout(executor, milliseconds);
        }

        @Override
        public int getNetworkTimeout() throws SQLException {
            return m_targetConnection.getNetworkTimeout();
        }

        @Override
        public Object unwrap(Class class1) throws SQLException {
            if (class1.isInstance(this))
                return this;
            else
                throw new SQLException(this + " is not an instance of " + class1);
        }

        @Override
        public boolean isWrapperFor(Class class1) throws SQLException {
            return class1.isInstance(this);
        }

        public Connection getTargetConnection() {
            return m_targetConnection;
        }

        public void setTargetConnection(Connection targetConnection) {
            m_targetConnection = targetConnection;
        }

        private Connection m_targetConnection;
    }


    private final Logger l = LogUtils.getLogger(getClass());
}
