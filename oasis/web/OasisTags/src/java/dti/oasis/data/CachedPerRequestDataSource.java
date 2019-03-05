package dti.oasis.data;

import dti.oasis.app.ConfigurationException;
import dti.oasis.request.RequestLifecycleListener;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.util.DatabaseUtils;
import dti.oasis.util.LogUtils;

import javax.sql.DataSource;
import java.io.PrintWriter;
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
import java.sql.SQLFeatureNotSupportedException;
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
 * This DataSource caches the connection in the RequestStorageManager for the lifetime of the request.
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
 * 8/5/2015         jxgu        Issue#164269 set AutoCommit to false for Weblogic 12.1.3
 * ---------------------------------------------------
 */
public class CachedPerRequestDataSource implements DataSource, RequestLifecycleListener {
    public static final String CONNECTION_KEY = "dti.oasis.data.CachedPerRequestDataSource.connection";

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        l.entering(getClass().getName(), "getParentLogger");

        return l;
    }

    /**
     * Delegate to the contained DataSource.
     */
    public int getLoginTimeout() throws SQLException {
        return getTargetDataSource().getLoginTimeout();
    }

    /**
     * Delegate to the contained DataSource.
     */
    public void setLoginTimeout(int seconds) throws SQLException {
        getTargetDataSource().setLoginTimeout(seconds);
    }

    /**
     * Delegate to the contained DataSource.
     */
    public PrintWriter getLogWriter() throws SQLException {
        return getTargetDataSource().getLogWriter();
    }

    /**
     * Delegate to the contained DataSource.
     */
    public void setLogWriter(PrintWriter out) throws SQLException {
        getTargetDataSource().setLogWriter(out);
    }

    /**
     * Return a connection, caching the connection for this request if the RequestStorageManager is setup for this request.
     * If the RequestStorageManager is not setup for this request, a new connection is returned.
     */
    public Connection getConnection() throws SQLException {
        l.entering(getClass().getName(), "getConnection");

        Connection conn = null;

        // Use the RequestStorageManager to cache the connection for this request
        RequestStorageManager rsm = getRequestStorageManager();
        if (rsm.isSetupForRequest()) {
            if (rsm.has(CONNECTION_KEY)) {
                conn = (Connection) rsm.get(CONNECTION_KEY);
                l.logp(Level.FINE, getClass().getName(), "getConnection", "Retrieved the cached connection from the RequestStorageManager.");
            } else {
                conn = new CachedConnection(getTargetDataSource().getConnection());
                conn.setAutoCommit(false);
                rsm.set(CONNECTION_KEY, conn, true);
                l.logp(Level.FINE, getClass().getName(), "getConnection", "Cached the connection in the RequestStorageManager.");
            }
        } else {
            // The RequestStorageManager is not setup for this request; return a new connection
            l.logp(Level.WARNING, getClass().getName(), "getConnection", "The RequestStorageManager is not setup for this request. Retrieved a new Connection from the target DataSource");
            conn = getTargetDataSource().getConnection();
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getConnection", conn);
        }
        return conn;
    }

    /**
     * Return a connection, caching the connection for this request if the RequestStorageManager is setup for this request.
     * If the RequestStorageManager is not setup for this request, a new connection is returned.
     */
    public Connection getConnection(String username, String password) throws SQLException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getConnection", new Object[]{username, password});
        }

        Connection conn = null;

        // Use the RequestStorageManager to cache the connection for this request
        RequestStorageManager rsm = getRequestStorageManager();
        if (rsm.isSetupForRequest()) {
            if (rsm.has(CONNECTION_KEY)) {
                conn = (Connection) rsm.get(CONNECTION_KEY);
                l.logp(Level.FINE, getClass().getName(), "getConnection", "Retrieved the cached connection from the RequestStorageManager.");
            } else {
                conn = new CachedConnection(getTargetDataSource().getConnection(username, password));
                conn.setAutoCommit(false);
                rsm.set(CONNECTION_KEY, conn);
                l.logp(Level.FINE, getClass().getName(), "getConnection", "Cached the connection in the RequestStorageManager.");
            }
        } else {
            // The RequestStorageManager is not setup for this request; return a new connection
            l.logp(Level.WARNING, getClass().getName(), "getConnection", "The RequestStorageManager is not setup for this request. Retrieved a new Connection from the target DataSource");
            conn = getTargetDataSource().getConnection(username, password);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getConnection", conn);
        }
        return conn;
    }

    /**
     * Initialize for the new request.
     */
    public void initialize() {
        // Do nothing, allowing the Connection to be lazy cached, only if used.
    }

    /**
     * Cleanup the cached connection.
     */
    public void terminate() {
        l.entering(getClass().getName(), "terminate");

        RequestStorageManager rsm = getRequestStorageManager();
        if (rsm.isSetupForRequest() && rsm.has(CONNECTION_KEY)) {
            Object connObj = rsm.get(CONNECTION_KEY);
            if (connObj instanceof CachedConnection) {
                CachedConnection conn = (CachedConnection) rsm.get(CONNECTION_KEY);
                if (isReadOnly()) {
                    try {
                        if (!conn.getAutoCommit()){
                            conn.rollback();
                        }
                    } catch (SQLException e) {
                        l.logp(Level.WARNING, getClass().getName(), "terminate", "Failed to roll back the readonly connection.", e);
                    }
                }
                DatabaseUtils.close(conn.getTargetConnection());
                rsm.remove(CONNECTION_KEY);
                l.logp(Level.FINE, getClass().getName(), "terminate", "Closed the cached connection and removed it from the RequestStorageManager.");
            }
        }
        l.exiting(getClass().getName(), "terminate");
    }

    /**
     * Cleanup the cached connection.
     */
    public boolean failure(Throwable e, boolean fixed) {
        l.entering(getClass().getName(), "failure");

        terminate();

        l.exiting(getClass().getName(), "failure");
        return false;  // Return false to signify that the failure was not fixed.
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public CachedPerRequestDataSource() {
    }

    public void verifyConfig() {
        if (getTargetDataSource() == null)
            throw new ConfigurationException("The required property 'targetDataSource' is missing.");
        if (getRequestStorageManager() == null)
            throw new ConfigurationException("The required property 'requestStorageManager' is missing.");
    }

    public DataSource getTargetDataSource() {
        return m_targetDataSource;
    }

    public void setTargetDataSource(DataSource targetDataSource) {
        m_targetDataSource = targetDataSource;
    }

    public RequestStorageManager getRequestStorageManager() {
        if (m_requestStorageManager == null) {
            // Allow this class to not be configured through Spring
            m_requestStorageManager = RequestStorageManager.getInstance();
        }
        return m_requestStorageManager;
    }

    public void setRequestStorageManager(RequestStorageManager requestStorageManager) {
        m_requestStorageManager = requestStorageManager;
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

    public boolean isReadOnly() {
        return m_readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.m_readOnly = readOnly;
    }

    private DataSource m_targetDataSource;
    private RequestStorageManager m_requestStorageManager;
    private boolean m_readOnly = false;
    private final Logger l = LogUtils.getLogger(getClass());
}

class CachedConnection implements Connection {

    public CachedConnection(Connection tarConnection) {
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
        // Do not close this connection.
        // Wait for the CachedPerRequestDataSource to get the target Connection and close it.
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
        if(class1.isInstance(this))
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
