package dti.oasis.data;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.struts.IOasisAction;
import dti.oasis.util.DBPool;
import dti.oasis.util.DatabaseUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.session.UserSessionManager;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Hashtable;
import java.util.Map;

/**
 * This DataSource uses the selected dbPoolId for the current user to determine which DataSource to use.
 * For now, this class uses the DatabaseUtils class to get the connection.
 * TODO: Configure with multiple available DataSources to replace the env variables for dbpoolids. Will also need to change the DatabaseUtils to use this info to determine available dbpoolids.
 * 
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
public class DBPoolDataSourceLocator implements DataSource {
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        l.entering(getClass().getName(), "getParentLogger");
        return l;
    }

    public int getLoginTimeout() throws SQLException {
        return getDataSource().getLoginTimeout();
    }

    public void setLoginTimeout(int seconds) throws SQLException {
        getDataSource().setLoginTimeout(seconds);
    }

    public PrintWriter getLogWriter() throws SQLException {
        return getDataSource().getLogWriter();
    }

    public void setLogWriter(PrintWriter out) throws SQLException {
        getDataSource().setLogWriter(out);
    }

    public Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    public Connection getConnection(String username, String password) throws SQLException {
        return getDataSource().getConnection(username, password);
    }

    /**
     * Return the DataSource for the dbPoolId associated with this request.
     */
    protected javax.sql.DataSource getDataSource() {
        l.entering(getClass().getName(), "getDataSource");

        /* Get Database Pool Id from HttpSession */
        String dbPoolId = (String) getUserSessionManager().getUserSession().get(IOasisAction.KEY_DBPOOLID);

        /* If we don't have one in session, then pull the first one
           from the web.xml.  This would occur if the user is anonymous. */
        if (StringUtils.isBlank(dbPoolId)) {
            if(DatabaseUtils.isRoleBasedDBPoolIdRequired()){
                ConfigurationException ce = new ConfigurationException("Cannot return Default DBPoolId for user: ["+getUserSessionManager().getUserSession().getUserId()+ "] when RoleBased DBPoolId is Required.");
                l.throwing(this.getClass().getName(), "getDataSource", ce);
                throw ce;
            }
            dbPoolId = DatabaseUtils.getDefaultDBPoolId();
        }

        javax.sql.DataSource ds = null;
        if (m_dataSourceCache.containsKey(dbPoolId)) {
            ds = (DataSource) m_dataSourceCache.get(dbPoolId);
            l.logp(Level.FINE, getClass().getName(), "getDataSource", "Found the DataSource for dbPoolId'"+dbPoolId+"' in the local cache.");
        }
        else {
            try {
                ds = DBPool.getDataSource(dbPoolId);
                m_dataSourceCache.put(dbPoolId, ds);
                l.logp(Level.FINE, getClass().getName(), "getDataSource", "Loaded the DataSource for dbPoolId'"+dbPoolId+"' from JNDI.");
            } catch (Exception e) {
                AppException ae = new AppException("Failed to locate the Data Source named '"+dbPoolId+"'.", e);
                l.throwing(getClass().getName(), "getDataSource", ae);
                throw ae;
            }
        }
        l.exiting(getClass().getName(), "getDataSource", ds);
        return ds;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public DBPoolDataSourceLocator() {
    }

    public UserSessionManager getUserSessionManager() {
        return m_userSessionManager;
    }

    public void setUserSessionManager(UserSessionManager userSessionManager) {
        m_userSessionManager = userSessionManager;
    }

    public void verifyConfig() {
        if (getUserSessionManager() == null)
            throw new ConfigurationException("The required property 'userSessionManager' is missing.");
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

    private UserSessionManager m_userSessionManager;
    private Map m_dataSourceCache = new Hashtable();
    private final Logger l = LogUtils.getLogger(getClass());
}
