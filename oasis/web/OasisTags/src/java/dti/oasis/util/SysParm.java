package dti.oasis.util;

import dti.oasis.data.StoredProcedureDAO;
import oracle.jdbc.OracleTypes;

import javax.naming.NamingException;
import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sys Parms Container
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date Dec 3, 2003
 *
 * @author jbe
 */
 /*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 2/6/2004         jbe     Added Logging
 * 1/6/2005         JBE     Add getSubsystemInfo
 * 09/25/2008       Larry   Issue 86826 DB connection leakage change
 * 03/07/2009       Fred    Remove blank spaces at the end of sysparm value
 * 01/21/2016       Parker  Issue#168627 Optimize the system parameter logic.
 * ---------------------------------------------------
 */

public class SysParm implements IParm {

    private HashMap parms = new HashMap();
    private HashMap subsystems = new HashMap(4);
    private static final String sql = "SELECT Cs_Get_System_Parameter(?) FROM dual";
    private static final String SQL_INSTALLED = "begin oasis_installation.get_installed_used_b(?,?,?);end;";
    private String dbPoolId = "";

    public SysParm() {
    }

    public SysParm(String dbPoolId) {
        this.dbPoolId = dbPoolId;
    }

    /**
     * Gets information about an OASIS subsystem from database.
     *
     * @param subsystem e.g. CM, PM, FM, RM
     * @return A SubsystemInfo class
     * @throws SQLException
     */
    private SubsystemInfo loadSubSystemFromDAO(String subsystem, Connection conn) throws SQLException, NamingException {
        Logger l = LogUtils.enterLog(getClass(), "getSubsystemInfo", subsystem);
        SubsystemInfo info = null;
        CallableStatement stmt = null;
        info = new SubsystemInfo();
        info.setSubsystem(subsystem);
        try {
            stmt = prepareSubsystemCall(conn);
            stmt.setString(1, subsystem);
            stmt.registerOutParameter(2, Types.VARCHAR);
            stmt.registerOutParameter(3, Types.VARCHAR);
            long startTime = System.currentTimeMillis();
            stmt.execute();
            long endTime = System.currentTimeMillis();
            Double callTime = (endTime - startTime) / 1000.0;
            info.setInstalled(stmt.getString(2).equals("Y"));
            info.setUsed(stmt.getString(3).equals("Y"));
            String callMethod = "SysParm.loadSubSystemFromDAO";
            Object[] parms = new Object[]{
                    new QueryParm(OracleTypes.VARCHAR, subsystem)};
            logSysParmCall(callMethod, callTime, SQL_INSTALLED, parms);
        } finally {
            closeStatement(stmt);
        }
        l.exiting(getClass().getName(), "getSubsystemInfo", info);
        return info;
    }

    /**
     * Gets information about an OASIS subsystem.
     *
     * @param subsystem e.g. CM, PM, FM, RM
     * @return A SubsystemInfo class
     * @throws SQLException
     */
    public SubsystemInfo getSubSystemInfo(String subsystem) throws SQLException, NamingException {
        Logger l = LogUtils.enterLog(getClass(), "getSubsytemInfo", subsystem);
        SubsystemInfo info = null;
        /* Check if we've already go this parm */
        if (subsystems.containsKey(subsystem)) {
            info = (SubsystemInfo) subsystems.get(subsystem);
        } else {
            Connection conn = DBPool.getConnection(dbPoolId);
            try {
                info = loadSubSystemFromDAO(subsystem, conn);
                subsystems.put(subsystem, info);
            } finally {
                closeConnection(conn);
            }
        }
        l.exiting(getClass().getName(), "getSubsytemInfo", info);
        return info;
    }

    /**
     * Prepare the statement parameter for sub system.
     *
     * @return A CallableStatement class
     * @throws SQLException
     */
    private CallableStatement prepareSubsystemCall(Connection conn) throws SQLException {
        Logger l = LogUtils.enterLog(getClass(), "prepareSubsystemCall");
        CallableStatement stmt = conn.prepareCall(SQL_INSTALLED);
        l.exiting(getClass().getName(), "prepareSubsystemCall");
        return stmt;
    }

    /**
     * Prepare the statement parameter.
     *
     * @return A PreparedStatement class
     * @throws SQLException
     */
    private PreparedStatement prepareParmStatement(Connection conn) throws SQLException {
        Logger l = LogUtils.enterLog(getClass(), "prepareParmStatement");
        PreparedStatement pStmt = conn.prepareStatement(sql);
        l.exiting(getClass().getName(), "prepareParmStatement", pStmt);
        return pStmt;
    }


    /**
     * Look up a single system parameter. Returns null if the parm is not found.
     *
     * @param key parm name
     * @return parm value
     * @throws SQLException, NamingException
     */
    private String get(String key, Connection conn) throws SQLException, NamingException {
        Logger l = LogUtils.enterLog(getClass(), "get", key);
        String val = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            /* Get the parm from the db */
            stmt = prepareParmStatement(conn);

            stmt.setString(1, key);
            l.fine(new StringBuffer("Executing: ").append(sql).append(" with ").append(key).toString());
            long startTime = System.currentTimeMillis();
            rs = stmt.executeQuery();
            long endTime = System.currentTimeMillis();
            Double callTime = (endTime - startTime) / 1000.0;
            if (rs.next()) {
                val = StringUtils.trimTail(rs.getString(1));
                parms.put(key, val);
            }
            l.exiting(getClass().getName(), "get", val);
            String callMethod = "SysParm.get."+key;
            String sysparmSql = sql.replace("?", "'"+key+"'");
            logSysParmCall(callMethod, callTime, sysparmSql, null);
        } finally {
            if (rs != null) DatabaseUtils.close(rs);
            closeStatement(stmt);

        }
        l.exiting(getClass().getName(), "get", val);
        return val;
    }

    /**
     * Look up a single system parameter. Returns null if the parm is not found.
     *
     * @param key parm name
     * @return parm value
     * @throws SQLException DB Problem
     */
    public String get(String key) throws SQLException, NamingException {
        Logger l = LogUtils.enterLog(getClass(), "get", key);
        String val = null;
        /* Check if we've already go this parm */
        if (parms.containsKey(key))
            val = (String) parms.get(key);
        else {
            Connection conn = DBPool.getConnection(dbPoolId);
            try {
                val = get(key, conn);
            } finally {
                closeConnection(conn);
            }
        }
        l.exiting(getClass().getName(), "get", val);
        return val;
    }

    /**
     * Looks up a set of system parameters.  A HashMap is returned containing the
     * System Parm values for each System Parm code.
     * If one of the parameters is not found, the entry will be null.
     *
     * @param keys Array of System Parm codes
     * @return HashMap keys are sysparm codes, entries are values
     * @throws SQLException DB Problem
     */
    public HashMap get(String[] keys) throws SQLException, NamingException {
        Logger l = LogUtils.enterLog(getClass(), "get", keys);
        int sz = keys.length;
        HashMap map = new HashMap(sz);
        Connection conn = null;

        try {
            /* Loop through keys */
            for (int i = 0; i < sz; i++) {
                /* Check if we've already got the parm */
                if (parms.containsKey(keys[i]))
                    map.put(keys[i], parms.get(keys[i]));
                else {
                    if (conn == null) {
                        conn = DBPool.getConnection(dbPoolId);
                    }
                    map.put(keys[i], get(keys[i], conn));
                }
            }
        } finally {
            closeConnection(conn);
        }
        l.exiting(getClass().getName(), "get", map);
        return map;
    }

    /**
     * Close the connection
     *
     */
    private void closeStatement(Statement stmt) {
        Logger l = LogUtils.enterLog(getClass(), "closeStatement", stmt);
        if (stmt != null) {
            DatabaseUtils.close(stmt);
        }
        l.exiting(getClass().getName(), "closeStatement");
    }

    /**
     * Close the connection
     *
     */
    private void closeConnection(Connection conn) {
        Logger l = LogUtils.enterLog(getClass(), "closeConnection", conn);
        if (conn != null) {
            DatabaseUtils.close(conn);
        }
        l.exiting(getClass().getName(), "closeConnection");
    }

    /**
     * Refreshes the currently loaded list of system parameters and subsystem info objects
     *
     * @throws SQLException
     */
    public void refresh() throws SQLException, NamingException {
        Connection conn = null;
        try {
            conn = DBPool.getConnection(dbPoolId);
            /* Get the parm from the db */
            refreshParms(conn);
            refreshSubsystems(conn);
        } finally {
            closeConnection(conn);
        }
    }

    /**
     * Refresh the sub system parameters
     *
     * @throws SQLException, NamingException
     */
    private void refreshSubsystems(Connection conn) throws SQLException, NamingException {
        Logger l = LogUtils.enterLog(getClass(), "refreshSubsystems");
        /* No subsystems, leave */
        if (subsystems.size() == 0) {
            l.exiting(getClass().getName(), "refreshSubsystems");
            return;
        }
        HashMap tempSubMap = new HashMap(4);
        /* Iterate through the subsystems we have */
        Iterator it = subsystems.keySet().iterator();
        while (it.hasNext()) {
            String subsystem = (String) it.next();
            SubsystemInfo info = loadSubSystemFromDAO(subsystem, conn);
            tempSubMap.put(subsystem, info);
        }
        subsystems = tempSubMap;
        l.exiting(getClass().getName(), "refreshSubsystems");
    }

    /**
     * Refresh the system parameters
     *
     * @throws SQLException, NamingException
     */
    private void refreshParms(Connection conn) throws SQLException, NamingException {
        Logger l = LogUtils.enterLog(getClass(), "refreshParms");
        /* No parms, leave */
        if (parms.size() == 0) {
            l.exiting(getClass().getName(), "refreshParms");
            return;
        }

        /* New Map to hold the refreshed parms */
        HashMap map = new HashMap(parms.size());

        /* Iterate through the sysparms we have */
        Iterator it = parms.keySet().iterator();
        while (it.hasNext()) {
            /* get the sysparm code */
            String key = (String) it.next();
            /* Get the value from the db and stick it in a new map */
            String value = get(key, conn);
            map.put(key, value);
            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(), "refreshParms", "key = " + key + "; value = " + value);
            }
        }
        /* Update the Map */
        parms = map;
        l.exiting(getClass().getName(), "refreshParms");
    }

    /**
     * Generate the toString method
     *
     * @return String
     */
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("dti.oasis.util.SysParm");
        buf.append("{parms=").append(parms);
        buf.append(",subsystems=").append(subsystems);
        buf.append(",dbPoolId=").append(dbPoolId);
        buf.append('}');
        return buf.toString();
    }

    private static void logSysParmCall(String callMethod, Double callTime, String callSql , Object[] parms){
        StoredProcedureDAO spDAO = StoredProcedureDAO.getInstance("SysParm.Loq_SysParm");
        String source = LogUtils.getPage();
        String userName = LogUtils.getUserId();
        String arguments = "";
        if(parms != null && parms.length>0) {
            StringBuffer sb = new StringBuffer("");
            int sz = parms.length;
            for (int i = 0; i < sz; i++) {
                sb.append("=>");
                QueryParm parm = (QueryParm) parms[i];
                if (parm.sqlType == OracleTypes.CURSOR) {
                    sb.append("refCursor");
                }
                else {
                    sb.append(parm.value);
                }
                sb.append("^");
            }
            arguments = sb.toString();
        }
        if(spDAO.isLogStoredProcedure())
            spDAO.logStoredProcedure(source, userName, callMethod, callTime, arguments, callSql,"SYSPARM");
    }
}
