package dti.oasis.util;

/*
 *  For JAVA 2 EE and JDBC2.0 only
 */

import dti.oasis.app.ApplicationContext;
import dti.oasis.session.UserSessionIds;
import dti.oasis.session.UserSessionManager;
import weblogic.jdbc.common.internal.RmiDataSource;
import weblogic.management.WebLogicObjectName;
import weblogic.management.MBeanHome;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.AttributeNotFoundException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * get database connection from  connection pool refferenced by JNI
 * Compatiable with JAVA 2 EE and JDBC2.0
 *
 * @author     Sam Zhu
 *
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 02/25/2010       James       Issue#104230 updating the code to stop using the deprecated code
 *                              Added new method clearStatementCache
 *                              Added new method restartConnectionPools
 * 11/10/2010       James       Issue#113896 Fix refreshparms to work with a multi-datasource 
 *                              for use in ASP
 * 04/21/2016       huixu       Issue#169769 Fix WebLogicSecurity.getAuthenticators to work in WebLogic 12.2.1
 * ---------------------------------------------------
*/

public class DBPool {
    /**
     *  Gets DB Connection from the pool by given pool ID
     *  Sample code:
     *     Connection conn = DBPool.getConnection("ma_nt_conv1_cnv");
     * @param  DBPoolID       The Name of the DB Pool ID
     * @return                The Connection
     * @exception  SQLException  Description of Exception
     * @exception NamingException
     * @since
     *
     * Sample code:
     <PRE>
     try {
     Connection conn = DBPool.getConnection("ma_nt_conv1_cnv");
     Statement stmt = conn.createStatement();
     ResultSet rs = stmt.executeQuery("select * from cat");
     ResultSetMetaData rsMetaData = rs.getMetaData();
     int colCount = rsMetaData.getColumnCount();
     for (int i = 0; i < colCount; i++) {
     System.out.print(rsMetaData.getColumnLabel(i + 1));
     System.out.print("\t");
     }
     System.out.println();
     while (rs.next()) {
     for (int i = 0; i < colCount; i++) {
     System.out.print(rs.getObject(i + 1).toString() + "\t");
     }
     System.out.println();
     }
     rs.close();
     stmt.close();
     conn.close();
     }
     catch (Exception e) {
     e.printStackTrace(System.out);
     }
     </PRE>
     */
    public static Connection getConnection(String DBPoolID)
            throws SQLException, NamingException {
        Logger l = LogUtils.enterLog(DBPool.class, "getConnection", DBPoolID);

        DataSource source = getDataSource(DBPoolID);
        Connection conn = source.getConnection();
        l.exiting(DBPool.class.getName(), "getConnection", conn);
        return conn;
    }

    /**
     * Get the DataSource for the given db pool id.
     */
    public static DataSource getDataSource(String DBPoolID) throws NamingException {
        Logger l = LogUtils.enterLog(DBPool.class, "getDataSource", new Object[]{DBPoolID});

        if (!DBPoolID.startsWith("jdbc/"))
            DBPoolID = "jdbc/" + DBPoolID;

        // Cache the DataSources to save the time of using the InitialContext.
        DataSource source = null;
        if (m_dataSourceCache.containsKey(DBPoolID)) {
            source = (DataSource) m_dataSourceCache.get(DBPoolID);
        }
        else {
            InitialContext ctx = new InitialContext();
            try {
                // Find a resource-ref entry if one exists
                Context env = (Context) ctx.lookup("java:comp/env");
                source = (DataSource) env.lookup(DBPoolID);
            } catch (NamingException e) {
                // If no resource-ref entry exists, look for the DataSource with the specified name
                source = (DataSource) ctx.lookup(DBPoolID);
            }
            m_dataSourceCache.put(DBPoolID, source);
        }

        l.exiting(DBPool.class.getName(), "getDataSource", source);
        return source;
    }

    /**
     * Method that resets the connection pool for the current server that is processing the request.
     *
     * @return boolean true, if the connection pool is resetted successfully; otherwise, false.
     */
    public static boolean resetConnectionPools() {
        Logger l = LogUtils.enterLog(DBPool.class, "resetConnectionPools");
        boolean isConnectionPoolsResetted = false;
        if(UserSessionManager.isConfigured()) {
            String dbPoolId = (String) UserSessionManager.getInstance().getUserSession().get(UserSessionIds.DB_POOL_ID);
            isConnectionPoolsResetted = resetConnectionPools(dbPoolId);
        } else {
            l.logp(Level.FINE, DBPool.class.getName(), "resetConnectionPools", "UserSessionManager is not configured to use for the current request");
        }
        return isConnectionPoolsResetted;
    }

    /**
     * Method that resets the connection pool for the current server that is processing the request.
     *
     * JMX security is required to invoke these methods, except if invoked by a JSP running as Admin
     *
     * @param dbPoolId Database Pool Id
     * @return boolean true, if the connection pool is resetted successfully; otherwise, false.
     */
    public static boolean resetConnectionPools(String dbPoolId) {
        Logger l = LogUtils.enterLog(DBPool.class, "resetConnectionPools", new Object[]{dbPoolId});
        boolean isConnectionPoolsResetted = invokeMethodOnDataSource(dbPoolId, "reset");
        l.exiting(DBPool.class.getName(), "resetConnectionPools", String.valueOf(isConnectionPoolsResetted));
        return isConnectionPoolsResetted;
    }

    /**
     * Method that restarts the connection pool for the current server that is processing the request.
     *
     * JMX security is required to invoke these methods, except if invoked by a JSP running as Admin
     *
     * @param dbPoolId Database Pool Id
     * @return boolean true, if the connection pool is resetted successfully; otherwise, false.
     */
    public static boolean restartConnectionPools(String dbPoolId) {
        Logger l = LogUtils.enterLog(DBPool.class, "restartConnectionPools", new Object[]{dbPoolId});
        boolean success = invokeMethodOnDataSource(dbPoolId, "shutdown");
        if (success) {
            success = invokeMethodOnDataSource(dbPoolId, "start");
        }
        l.exiting(DBPool.class.getName(), "restartConnectionPools", String.valueOf(success));
        return success;
    }

    /**
     * Method that force restart the connection pool for the current server that is processing the request.
     *
     * JMX security is required to invoke these methods, except if invoked by a JSP running as Admin
     *
     * @param dbPoolId Database Pool Id
     * @return boolean true, if the connection pool is resetted successfully; otherwise, false.
     */
    public static boolean forceRestartConnectionPools(String dbPoolId) {
        Logger l = LogUtils.enterLog(DBPool.class, "forceRestartConnectionPools", new Object[]{dbPoolId});
        boolean success = invokeMethodOnDataSource(dbPoolId, "forceShutdown");
        if (success) {
            success = invokeMethodOnDataSource(dbPoolId, "start");
        }
        l.exiting(DBPool.class.getName(), "forceRestartConnectionPools", String.valueOf(success));
        return success;
    }

    /**
     * get the number of active connections in connection pool
     *
     * JMX security is required to invoke these methods, except if invoked by a JSP running as Admin
     *
     * @param dbPoolId Database Pool Id
     * @return int the number of active connections
     */
    public static int getActiveConnectionsCount(String dbPoolId) {
        Logger l = LogUtils.enterLog(DBPool.class, "getActiveConnectionsCount", new Object[]{dbPoolId});
        int activeConnections = -1;
        try {
            activeConnections = ((Integer) getConnectionPoolAttribute(dbPoolId, "ActiveConnectionsCurrentCount")).intValue();
        } catch (Exception e) {
            l.logp(Level.FINE, DBPool.class.getName(), "getActiveConnectionsCount", "***Error while getting active connections count for pool Id:"
                    + dbPoolId  + " Reason:" + e.getMessage());
        }
        l.exiting(DBPool.class.getName(), "getActiveConnectionsCount", new Integer(activeConnections));
        return activeConnections;
    }

    /**
     * get the state of the connection pool
     *
     * JMX security is required to invoke these methods, except if invoked by a JSP running as Admin
     *
     * @param dbPoolId Database Pool Id
     * @return boolean is connection pool enable
     */
    public static String getConnectionPoolState(String dbPoolId) {
        Logger l = LogUtils.enterLog(DBPool.class, "getConnectionPoolState", new Object[]{dbPoolId});
        String state = null;
        try {
            state = (String) getConnectionPoolAttribute(dbPoolId, "State");
        } catch (Exception e) {
            l.logp(Level.FINE, DBPool.class.getName(), "getConnectionPoolState", "***Error while getting State for pool Id:"
                    + dbPoolId + " Reason:" + e.getMessage());
        }
        l.exiting(DBPool.class.getName(), "getConnectionPoolState", state);
        return state;
    }

    /**
     * get attribute of database connection pool
     *
     * JMX security is required to invoke these methods, except if invoked by a JSP running as Admin
     *
     * @param dbPoolId
     * @param attributeName
     * @return
     * @throws NamingException
     * @throws MalformedObjectNameException
     * @throws ReflectionException
     * @throws InstanceNotFoundException
     * @throws MBeanException
     * @throws AttributeNotFoundException
     */
    private static Object getConnectionPoolAttribute(String dbPoolId, String attributeName)
            throws NamingException, MalformedObjectNameException,
            ReflectionException, InstanceNotFoundException,
            MBeanException, AttributeNotFoundException {
        Logger l = LogUtils.enterLog(DBPool.class, "getConnectionPoolAttribute", new Object[]{dbPoolId});

        String serverName = ApplicationContext.getInstance().getServerName();

        InitialContext ctx = new InitialContext();
        MBeanServer connector = (MBeanServer) ctx.lookup("java:comp/env/jmx/runtime");
        // Get connection pool's name by data source
        String sPoolName = ((RmiDataSource) DBPool.getDataSource(dbPoolId)).getPoolName();
        ObjectName objectName = new ObjectName("com.bea:Name=" + sPoolName + ",ServerRuntime=" + serverName + ",Type=JDBCDataSourceRuntime");
        Object attributeValue = connector.getAttribute(objectName, attributeName);

        l.exiting(DBPool.class.getName(), "getConnectionPoolAttribute", attributeValue);
        return attributeValue;
    }

    /**
     * Method that clear Statement Cache of the datasource for the current server that is processing the request.
     *
     * @return boolean true, if the statement cache is cleared successfully; otherwise, false.
     */
    public static boolean clearStatementCache() {
        Logger l = LogUtils.enterLog(DBPool.class, "clearStatementCache");
        boolean isCleared = false;
        if (UserSessionManager.isConfigured()) {
            String dbPoolId = (String) UserSessionManager.getInstance().getUserSession().get(UserSessionIds.DB_POOL_ID);
            isCleared = invokeMethodOnDataSource(dbPoolId, "clearStatementCache");
        } else {
            l.logp(Level.FINE, DBPool.class.getName(), "clearStatementCache", "UserSessionManager is not configured to use for the current request");
        }
        l.exiting(DBPool.class.getName(), "clearStatementCache", String.valueOf(isCleared));
        return isCleared;
    }

    /**
     * Invoke a method on datasource
     *
     * supported method: clearStatementCache, forceShutdown, forceSuspend, reset, resume, shrink, shutdown, start, suspend
     *
     * @param dbPoolId
     * @param method
     * @return boolean true, if method is invoked successfully; otherwise, false.
     */
    public static boolean invokeMethodOnDataSource(String dbPoolId, String method) {
        Logger l = LogUtils.enterLog(DBPool.class, "invokeMethodOnDataSource", new Object[]{dbPoolId, method});
        boolean success = false;
        String serverName = ApplicationContext.getInstance().getServerName();
        if (StringUtils.isBlank(dbPoolId)) {
            dbPoolId = DatabaseUtils.getDefaultDBPoolId();
        }
        try {
            InitialContext ctx = new InitialContext();
            MBeanServer connector = (MBeanServer) ctx.lookup("java:comp/env/jmx/runtime");
            // Get connection pool's name by data source
            String sPoolName = ((RmiDataSource) getDataSource(dbPoolId)).getPoolName();
            ObjectName objectName = new ObjectName("com.bea:Name=" + sPoolName + ",ServerRuntime=" + serverName + ",Type=JDBCDataSourceRuntime");
            connector.invoke(objectName, method, null, null);
            success = true;
            l.logp(Level.FINE, DBPool.class.getName(), "invokeMethodOnDataSource", "Invoking " + method
                    + " on DataSource successfully for pool Id:" + dbPoolId + " at server:" + serverName);
        } catch (javax.management.InstanceNotFoundException ee) {
            l.logp(Level.FINE, DBPool.class.getName(), "invokeMethodOnDataSource", "***InstanceNotFoundException error raised while invoking "
                    + method + " on DataSource for pool Id:" + dbPoolId + " at server:" + serverName + " Reason:" + ee.getMessage());
        } catch (Exception e) {
            l.logp(Level.FINE, DBPool.class.getName(), "invokeMethodOnDataSource", "***Error while invoking " + method
                    + " on DataSource for pool Id " + dbPoolId + " at server:" + serverName + " Reason:" + e.getMessage());
            e.printStackTrace();
        }
        l.exiting(DBPool.class.getName(), "invokeMethodOnDataSource", String.valueOf(success));
        return success;
    }

    /**
     * check whether the data source is a multiple data source.
     * @param dbPoolId
     * @return
     */
    public static boolean isMultipleDataSource(String dbPoolId) {
        Logger l = LogUtils.enterLog(DBPool.class, "isMultipleDataSource", new Object[]{dbPoolId});
        boolean isMultipleDataSource = false;
        if (ApplicationContext.getInstance().hasProperty(dbPoolId + ".multidatasource")) {
            String multipleDataSourceString = ApplicationContext.getInstance().getProperty(dbPoolId + ".multidatasource");
            isMultipleDataSource = !StringUtils.isBlank(multipleDataSourceString);
        }
        l.exiting(DBPool.class.getName(), "isMultipleDataSource", isMultipleDataSource);
        return isMultipleDataSource;
    }

    /**
     * get dbPoolId list
     *
     * @param dbPoolId
     * @return
     */
    public static String[] getDbPoolIdList(String dbPoolId) {
        Logger l = LogUtils.enterLog(DBPool.class, "getDbPoolIdList", new Object[]{dbPoolId});
        String[] dbPoolIdList = new String[0];
        if (isMultipleDataSource(dbPoolId)) {
            String multipleDataSourceString = ApplicationContext.getInstance().getProperty(dbPoolId + ".multidatasource");
            dbPoolIdList = multipleDataSourceString.split(",");
        } else {
            dbPoolIdList = new String[]{dbPoolId};
        }
        for (int i = 0; i < dbPoolIdList.length; i++) {
            dbPoolIdList[i] = dbPoolIdList[i].trim();
        }
        l.exiting(DBPool.class.getName(), "getDbPoolIdList", dbPoolIdList);
        return dbPoolIdList;
    }

    /**
     * check whether there is shutdown datasource
     * @param dbPoolIdList
     * @return
     */
    public static boolean hasShutDownDataSource(String[] dbPoolIdList) {
        Logger l = LogUtils.enterLog(DBPool.class, "hasShutDownDataSource", new Object[]{dbPoolIdList});
        boolean hasShutDownDataSource = false;
        for (int i = 0; i < dbPoolIdList.length; i++) {
            String dbState = DBPool.getConnectionPoolState(dbPoolIdList[i]);
            if (DBPool.DB_CONNECTION_STATE_SHUTDOWN.equalsIgnoreCase(dbState)) {
                hasShutDownDataSource = true;
                break;
            }
        }
        l.exiting(DBPool.class.getName(), "hasShutDownDataSource", hasShutDownDataSource);
        return hasShutDownDataSource;
    }

    private static Map m_dataSourceCache = new Hashtable();

    public static String DB_CONNECTION_STATE_RUNNING = "Running";
    public static String DB_CONNECTION_STATE_SUSPENDED = "Suspended";
    public static String DB_CONNECTION_STATE_SHUTDOWN = "Shutdown";
    public static String DB_CONNECTION_STATE_OVERLOADED = "Overloaded";
    public static String DB_CONNECTION_STATE_UNHEALTHY = "Unhealthy";
    public static String DB_CONNECTION_STATE_UNKNOWN = "Unknown";

}


