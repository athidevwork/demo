package dti.oasis.util;

import dti.oasis.util.XMLUtils;
import java.sql.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.*;

import oracle.jdbc.OracleTypes;


/**
 * Singleton class to manage Web Queries.
 *
 *
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 4, 2005
 *
 * @author jbe
 */
/* 
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/10/2005       jbe         Handle parm_description & user_enterable parm columns.
 * 01/06/2016       wdang       168069 - 1) Support SELECT statement starting with WITH.
 *                                       2) Added a method to get WebQeury by specifying queryId.
 * ---------------------------------------------------
*/

public class WebQuery {
    private static final WebQuery INSTANCE = new WebQuery();
    private static final String SQL_PREFIX_CALLABLE_STMT = "EXEC";
    private static final String[] SQL_PREFIX_PREPARED_STMT = {"WITH","SELECT"};

    private static final String SQL_GET_QUERY_BY_QUERY_ID = "SELECT web_query_pk, query_text FROM web_query WHERE query_id = ?";
    private static final String SQL_GETQUERY = "SELECT query_text FROM web_query WHERE web_query_pk = ?";
    private static final String SQL_GETQUERYPARMS = "SELECT DECODE(parm_type" +
            ",'BOOLEAN'," + Types.BOOLEAN +
            ",'STRING'," + Types.VARCHAR +
            ",'NUMBER'," + Types.NUMERIC +
            ",'DATE'," + Types.DATE +
            ',' + Types.OTHER + ")," +
            "parm_name, parm_description, nvl(user_enterable_b,'N') user_enterable_b " +
            "FROM web_query_parm " +
            "WHERE web_query_fk = ? " +
            "ORDER BY parm_no";

    /**
     * Call this method to get the single instance of this class
     *
     * @return
     */
    public static WebQuery getInstance() {
        return INSTANCE;
    }

    private WebQuery() {
    }

    private Object readResolve() {
        return INSTANCE;
    }

    /**
     * Gets a Web Query from a web query pk, then executes it given a set of parameters.  A
     * DisconnectedResultSet is returned.  Note that if a required parameter is missing from
     * the map (parms) then an IllegalArgumentException will be thrown.  If the parameter is
     * present, but is null, then the parm is set to null.
     *
     * @param conn Live JDBC Connection
     * @param queryPk web_query.web_query_pk
     * @param parms Map of parameter names & values to bind to the query.
     * @return DisconnectedResultSet
     * @throws SQLException
     * @throws IllegalArgumentException Missing querypk or required parm.
     */
    public DisconnectedResultSet getResultSet(Connection conn, long queryPk, Map parms) throws SQLException {
        Logger l = LogUtils.enterLog(getClass(), "getResultSet", new Object[]{conn, String.valueOf(queryPk), parms});
        DisconnectedResultSet drs = null;
        // Get Query
        WebQueryInfo info = getQuery(conn, queryPk);
        // Check if it is regular SQL statement or procedure call
        String upperSQL= info.getSql().trim().toUpperCase();
        if (isCallableStatement(upperSQL)) {
            // Call procedure to retrieve the result from reference cursor
            CallableStatement cstmt = null;
            ResultSet rs = null;
            try {
                cstmt = callableStatement(conn, info, parms);
                cstmt.execute();
                // The reference cursor should always be the last parameter in the procedure
                rs = (ResultSet) cstmt.getObject(info.getParms().size() + 1);
                drs = new DisconnectedResultSet(rs);
            }
            finally {
                DatabaseUtils.close(cstmt, rs);
            }
        }
        else if (isPreparedStatement(upperSQL)) {
            // Execute SQL statement
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            try {
                pstmt = prepareStatement(conn, queryPk, parms);
                rs = pstmt.executeQuery();
                drs = new DisconnectedResultSet(rs);
            }
            finally {
                DatabaseUtils.close(pstmt, rs);
            }
        }
        else {
            throw new IllegalArgumentException("Invalid query Sql string: " + info.getSql());
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getResultSet", drs);
        }

        return drs;
    }

    /**
     * Construct and return a new map based on an existing map where all the keys in the
     * new map will be upper case Strings.
     * @param map
     * @return
     */
    private HashMap upperKeys(Map map) {
        Logger l = LogUtils.enterLog(getClass(),"upperKeys", map);
        HashMap retMap = new HashMap(map.size());
        Iterator it = map.keySet().iterator();
        while(it.hasNext()) {
            String key = (String) it.next();
            retMap.put(key.toUpperCase(), map.get(key));
        }
        l.exiting(getClass().getName(),"upperKeys", retMap);
        return retMap;
    }

    /**
     * Gets a Web Query from a web query pk, then prepared a JDBC statement with the
     * appropriate parameters.  The caller MUST close the statement when finished. Note that if a required parameter is missing from
     * the map (parms) then an IllegalArgumentException will be thrown.  If the parameter is
     * present, but is null, then the parm is set to null.
     *
     * @param conn Live JDBC Connection
     * @param queryPk web_query.web_query_pk
     * @param parms Map of parameter names & values to bind to the query.
     * @return DisconnectedResultSet
     * @throws SQLException
     * @throws IllegalArgumentException if invalid queryPk or missing parms.
     */
    public PreparedStatement prepareStatement(Connection conn, long queryPk, Map parms) throws SQLException {
        Logger l = LogUtils.enterLog(getClass(),"prepareStatement", new Object[] {conn, String.valueOf(queryPk), parms});
        PreparedStatement stmt = null;
        // Get Query
        WebQueryInfo info = getQuery(conn, queryPk);
        // Prepare Statement with SQL
        stmt = conn.prepareStatement(info.getSql());
        // Deal with any parms
        int sz = info.getParms().size();
        HashMap upperParms = upperKeys(parms);
        for(int i=0;i<sz;i++) {
            WebQueryInfo.QueryParm parm = (WebQueryInfo.QueryParm) info.getParms().get(i);
            // check for parm
            if(!upperParms.containsKey(parm.getName())) {
                String msg = "Parameter " + parm.getName() + " not passed in parms map.";
                l.severe(msg+"\nQuery="+info);
                throw new IllegalArgumentException(msg);
            }
            // get the parameter value
            Object o = upperParms.get(parm.getName());
            // set into the stmt with appropriate SQL type
            l.fine("Binding "+(i+1) + " with " +o);
            stmt.setObject(i+1, o, parm.getParmType());
        }
        l.exiting(getClass().getName(),"prepareStatement",stmt);
        return stmt;
    }

    /**
     * Gets a Web Query from a web query pk, then executes it given a set of parameters.  An
     * XML view of the data is returned.  Note that if a required parameter is missing from
     * the map (parms) then an IllegalArgumentException will be thrown.  If the parameter is
     * present, but is null, then the parm is set to null.
     *
     * @param conn Live JDBC Connection
     * @param queryPk web_query.web_query_pk
     * @param parms Map of parameter names & values to bind to the query.
     * @return DisconnectedResultSet
     * @throws SQLException
     * @throws IllegalArgumentException if invalid queryPk or missing parms.
     * @see dti.oasis.util.XMLUtils#resultSetToXml
     */
    public String getXML(Connection conn, long queryPk, Map parms) throws SQLException {
        Logger l = LogUtils.enterLog(getClass(), "getXML", new Object[]{conn, String.valueOf(queryPk), parms});

        String xml = "";
        // Get Query
        WebQueryInfo info = getQuery(conn, queryPk);
        // Check if it is regular SQL statement or procedure call
        String upperSQL= info.getSql().trim().toUpperCase();
        if (isCallableStatement(upperSQL)) {
            // Call procedure to retrieve the result from reference cursor
            CallableStatement cstmt = null;
            ResultSet rs = null;
            try {
                cstmt = callableStatement(conn, info, parms);
                cstmt.execute();
                // The reference cursor should always be the last parameter in the procedure
                rs = (ResultSet) cstmt.getObject(info.getParms().size() + 1);
                xml = XMLUtils.resultSetToXml(rs);
            }
            finally {
                DatabaseUtils.close(cstmt, rs);
            }
        }
        else if (isPreparedStatement(upperSQL)) {
            // Regular SQL statement
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            try {
                pstmt = prepareStatement(conn, queryPk, parms);
                rs = pstmt.executeQuery();
                xml = XMLUtils.resultSetToXml(rs);
            }
            finally {
                DatabaseUtils.close(pstmt, rs);
            }
        }
        else {
            throw new IllegalArgumentException("Invalid query Sql string: " + info.getSql());
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getXML", xml);
        }

        return xml;
    }

    /**
     * Gets a Web Query from a web query pk.  Parameters are stored in uppercase.
     *
     * @param conn    Live JDBC Connection
     * @param queryPk web_query.query_pk
     * @return A WebQueryInfo object
     * @throws java.sql.SQLException
     * @throws IllegalArgumentException if query not found.
     */
    public WebQueryInfo getQuery(Connection conn, long queryPk) throws SQLException {
        Logger l = LogUtils.enterLog(getClass(), "getQuery", new Object[]{conn, String.valueOf(queryPk)});
        ResultSet rs = null;
        PreparedStatement stmt = null;
        WebQueryInfo info = null;
        try {
            // SQL to get Query itself
            stmt = conn.prepareStatement(SQL_GETQUERY);
            stmt.setLong(1, queryPk);
            l.fine(new StringBuffer("Executing: ").append(SQL_GETQUERY).append(" with ").
                    append(queryPk).toString());
            rs = stmt.executeQuery();
            // query found?
            if (rs.next()) {
                info = new WebQueryInfo(queryPk);
                info.setSql(rs.getString(1));
                // done with this statement and resultset
                DatabaseUtils.close(stmt, rs);
                // new statement and resultset
                stmt = conn.prepareStatement(SQL_GETQUERYPARMS);
                stmt.setLong(1, queryPk);
                l.fine(new StringBuffer("Executing: ").append(SQL_GETQUERYPARMS).append(" with ").
                        append(queryPk).toString());
                rs = stmt.executeQuery();
                while (rs.next()) {
                    String yOrN = rs.getString(4);
                    boolean userEnterable = (yOrN!=null && yOrN.equals("Y"));
                    info.addParm(rs.getInt(1), rs.getString(2), rs.getString(3), userEnterable);
                }
            }
            else {
                l.severe("web_query.web_query_pk="+queryPk + " not found.");
                throw new IllegalArgumentException("Query not found.");
            }
            l.exiting(getClass().getName(), "getQuery", info);
            return info;
        }
        finally {
            DatabaseUtils.close(stmt, rs);
        }
    }

    /**
     * Popluate callable statement for query that uses procedure.
     *
     * @param conn
     * @param info
     * @param parms
     * @return CallableStatement
     * @throws SQLException
     */
    public CallableStatement callableStatement(Connection conn, WebQueryInfo info, Map parms) throws SQLException {
        Logger l = LogUtils.enterLog(getClass(), "prepareStatement", new Object[]{conn, info, parms});
        CallableStatement cstmt;

        // Populate sql
        String sql = info.getSql().trim().substring("EXEC".length()).trim();
        // Add question marks
        int sz = info.getParms().size();
        sql += "(?";
        for (int i = 0; i < sz; i++) {
            sql += ",?";
        }
        sql += ");";
        sql = "BEGIN " + sql + " END;";
        cstmt = conn.prepareCall(sql);

        // Deal with parameters
        HashMap upperParms = upperKeys(parms);
        for (int i = 0; i < sz; i++) {
            WebQueryInfo.QueryParm parm = (WebQueryInfo.QueryParm) info.getParms().get(i);
            // check for parm
            if (!upperParms.containsKey(parm.getName())) {
                String msg = "Parameter " + parm.getName() + " not passed in parms map.";
                l.severe(msg + "\nQuery=" + info);
                throw new IllegalArgumentException(msg);
            }
            // get the parameter value
            Object o = upperParms.get(parm.getName());
            // set into the stmt with appropriate SQL type
            l.fine("Binding " + (i + 1) + " with " + o);
            cstmt.setObject(i + 1, o, parm.getParmType());
        }
        // Set output parameter, the reference should always be last parameter in the procedure
        cstmt.registerOutParameter(info.getParms().size() + 1, OracleTypes.CURSOR);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "callableStatement", cstmt);
        }

        return cstmt;
    }

    /**
     * Gets a Web Query from a query id. Parameters are stored in uppercase.
     * @param conn Live JDBC Connection
     * @param queryId web_query.query_id
     * @return DisconnectedResultSet
     * @throws SQLException
     * @throws IllegalArgumentException if invalid queryPk or missing parms.
     */
    public WebQueryInfo getQueryById(Connection conn, String queryId) throws SQLException {
        Logger l = LogUtils.enterLog(getClass(),"getQueryById", new Object[] {conn, queryId});
        ResultSet rs = null;
        PreparedStatement stmt = null;
        WebQueryInfo info = null;
        try {
            // SQL to get Query itself
            stmt = conn.prepareStatement(SQL_GET_QUERY_BY_QUERY_ID);
            stmt.setString(1, queryId);
            l.fine(new StringBuffer("Executing: ").append(SQL_GET_QUERY_BY_QUERY_ID).append(" with ").
                append(queryId).toString());
            rs = stmt.executeQuery();
            // query found?
            if (rs.next()) {
                info = new WebQueryInfo(rs.getLong(1));
                info.setSql(rs.getString(2));
            } else {
                l.severe("web_query.query_id="+queryId + " not found.");
                throw new IllegalArgumentException("Query not found.");
            }

        } finally {
            DatabaseUtils.close(stmt, rs);
        }
        if (info != null) {
            ResultSet rs2 = null;
            PreparedStatement stmt2 = null;
            try {
                stmt2 = conn.prepareStatement(SQL_GETQUERYPARMS);
                stmt2.setLong(1, info.getQueryPk());
                l.fine(new StringBuffer("Executing: ").append(SQL_GETQUERYPARMS).append(" with ").
                    append(info.getQueryPk()).toString());
                rs2 = stmt2.executeQuery();
                while (rs2.next()) {
                    String yOrN = rs2.getString(4);
                    boolean userEnterable = (yOrN != null && yOrN.equals("Y"));
                    info.addParm(rs2.getInt(1), rs2.getString(2), rs2.getString(3), userEnterable);
                }
            } finally {
                DatabaseUtils.close(stmt2, rs2);
            }
        }
        l.exiting(getClass().getName(), "getQueryById", info);
        return info;
    }

    /**
     * is callable statement
     * @param upperSQL
     * @return
     */
    private boolean isCallableStatement(String upperSQL) {
        Logger l = LogUtils.enterLog(getClass(), "isCallableStatement", new Object[]{upperSQL});
        boolean isCallableStatement = false;
        if (upperSQL.startsWith(SQL_PREFIX_CALLABLE_STMT)) {
            isCallableStatement = true;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isCallableStatement", isCallableStatement);
        }
        return isCallableStatement;
    }

    /**
     * is prepared statement
     * @param upperSQL
     * @return
     */
    private boolean isPreparedStatement(String upperSQL) {
        Logger l = LogUtils.enterLog(getClass(), "isPreparedStatement", new Object[]{upperSQL});
        boolean isPreparedStatement = false;
        for (String prefix : SQL_PREFIX_PREPARED_STMT) {
            if (upperSQL.startsWith(prefix)) {
                isPreparedStatement = true;
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isPreparedStatement", isPreparedStatement);
        }
        return isPreparedStatement;
    }

}
