package dti.oasis.data;

import dti.oasis.request.RequestStorageIds;
import dti.oasis.util.LogUtils;
import dti.oasis.util.DatabaseUtils;
import dti.oasis.tags.OasisFields;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.app.ConfigurationException;
import dti.oasis.app.AppException;
import dti.oasis.struts.IOasisAction;
import dti.oasis.session.UserSessionManager;
import dti.oasis.http.RequestIds;
import dti.oasis.util.OasisUser;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * This class uses user and protected field information in the RequestStorageManager
 * to setup the user and protected fields for all new Connections.
 * This enables use of PL/SQL global session variables.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 25, 2006
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/14/2008       mlm         Enhanced to publish web session id to DB.
 * 05/11/2016       wdang       176749 - Added setWebContext.
 * 02/22/2017       tzeng       168385 - Modified setWebContext() to set requested transaction time.
 * ---------------------------------------------------
 */
public class SetupUserConnectionDataSourceProxy implements DataSource {

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        Logger l = LogUtils.enterLog(getClass(), "getParentLogger");
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
     * Return a new Connection that has the user and protected fields setup.
     *
     * @return a new Connection with the user and protected fields setup.
     * @throws SQLException if there is an error while retrieving a new Connection from the contained DataSource.
     */
    public Connection getConnection() throws SQLException {
        Logger l = LogUtils.enterLog(getClass(), "getConnection");

        Connection conn = null;
        conn = getTargetDataSource().getConnection();
        setWebContext(conn);
        l.logp(Level.FINE, getClass().getName(), "getConnection", "Setup the UserId and Protected Fields on the new connection");
              
        l.exiting(getClass().getName(), "getConnection", conn);

        return conn;
    }

    /**
     * Return the Connection associated with the current request scope.
     * If there is no Connection assiciated with the current request scope, a new Connection is retrieved
     * from the contained DataSource, using the specified username and password,
     * and is associated with the current request scope.
     *
     * @return the Connection associated with the current request scope.
     * @throws SQLException if there is an error while retrieving a new Connection from the contained DataSource.
     */
    public Connection getConnection(String username, String password) throws SQLException {
        Logger l = LogUtils.enterLog(getClass(), "getConnection", new Object[]{username, password});

        Connection conn = null;
        conn = getTargetDataSource().getConnection(username, password);
        setWebContext(conn);
        l.logp(Level.FINE, getClass().getName(), "getConnection", "Setup the UserId and Protected Fields on the new connection");

        l.exiting(getClass().getName(), "getConnection", conn);

        return conn;
    }

    /**
     * Set web context in database session.
     * @param conn
     */
    protected void setWebContext(Connection conn) {
        Logger l = LogUtils.enterLog(getClass(), "setWebContext", new Object[]{conn});

        String protFields = null;
        // Find OasisFields collection.
        if (getRequestStorageManager().has(IOasisAction.KEY_FIELDS)) {
            protFields = ((OasisFields) getRequestStorageManager().get(IOasisAction.KEY_FIELDS)).getProtectedFields();
        }
        OasisUser oasisUser = getUserSessionManager().getUserSession().getOasisUser();
        try {
            DatabaseUtils.setWebContext(conn,
                oasisUser.getUserId(),
                getRequestStorageManager().get(RequestIds.WEB_SESSION_ID).toString(),
                protFields,
                oasisUser.getSourceContext().name(),
                oasisUser.getRequestedTransactionTime());
        } catch (SQLException e) {
            DatabaseUtils.close(conn);
            AppException ae = new AppException("Failed to setup web context.", e);
            l.throwing(getClass().getName(), "setWebContext", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "setWebContext");
    }


    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public SetupUserConnectionDataSourceProxy() {
    }

    public DataSource getTargetDataSource() {
        return m_targetDataSource;
    }

    public void setTargetDataSource(DataSource targetDataSource) {
        m_targetDataSource = targetDataSource;
    }

    public UserSessionManager getUserSessionManager() {
        return m_userSessionManager;
    }

    public void setUserSessionManager(UserSessionManager userSessionManager) {
        m_userSessionManager = userSessionManager;
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

    public void verifyConfig() {
        if (getTargetDataSource() == null)
            throw new ConfigurationException("The required property 'targetDataSource' is missing.");
        if (getUserSessionManager() == null)
            throw new ConfigurationException("The required property 'userSessionManager' is missing.");
        if (getRequestStorageManager() == null)
            throw new ConfigurationException("The required property 'requestStorageManager' is missing.");
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

    private DataSource m_targetDataSource;
    private UserSessionManager m_userSessionManager;
    private RequestStorageManager m_requestStorageManager;
}