package dti.oasis.util;

import dti.oasis.data.StoredProcedureDAO;
import oracle.jdbc.OracleTypes;
import org.apache.struts.util.LabelValueBean;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import dti.oasis.data.DataLoadProcessor;
import dti.oasis.data.DefaultDataLoadProcessor;

/**
 * Utility class to query a database.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 3, 2003
 *
 * @author jbe
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 2/6/2004         jbe         Added Logging
 * 7/15/2004        jbe         Add new methods using preparedstatements and QueryParm
 * 11/3/2004        jbe         Add doRefCursorQuery methods
 * 3/11/2005        jbe         Change doRefCursorQuery to use handleTransaction.
 * 01/23/2007       wer         Added support for the DataLoadProcessor to post process rows and the result set;
 * 09/25/2008       Larry       Issue 86826 DB connection leakage change
 * 11/18/2011       Jerry       Issue#126056 Filter drop down options which both key-value are Null Objects in the LOV option list.
 * ---------------------------------------------------
 */
public class Querier {

    protected final static String clsName = Querier.class.getName();

    /**
     * Execute a query that will return a collection of label/value columns.
     * Returns an ArrayList of LabelValueBean objects
     *
     * @param sql        query
     * @param conn       JDBC Connection
     * @param displayCol Label column # in query (1 based)
     * @param valueCol   Value column # in query (1 based)
     * @param closeQuery If true, connection will be closed after query is executed.
     * @return ArrayList of LabelValueBean objects
     * @throws SQLException
     * @see org.apache.struts.util.LabelValueBean
     */
    public static ArrayList doListQuery(String sql, Connection conn, int displayCol,
                                        int valueCol, boolean closeQuery) throws SQLException {
        return doListQuery(null, sql, conn, displayCol, valueCol, closeQuery);
    }

    /**
     * Execute a query that will return a collection of label/value columns.
     * Returns an ArrayList of LabelValueBean objects
     *
     * @param fieldId    LOV FieldId
     * @param sql        query
     * @param conn       JDBC Connection
     * @param displayCol Label column # in query (1 based)
     * @param valueCol   Value column # in query (1 based)
     * @param closeQuery If true, connection will be closed after query is executed.
     * @return ArrayList of LabelValueBean objects
     * @throws SQLException
     * @see org.apache.struts.util.LabelValueBean
     */
    public static ArrayList doListQuery(String fieldId, String sql, Connection conn, int displayCol,
                                        int valueCol, boolean closeQuery) throws SQLException {
        Logger l = LogUtils.enterLog(Querier.class, "doListQuery", new Object[]
            {fieldId, sql, conn, new Integer(displayCol), new Integer(valueCol), new Boolean(closeQuery)});
        ResultSet rs = null;
        Statement stmt = null;

        ArrayList list = new ArrayList();
        try {
            stmt = conn.createStatement();
            long startTime = System.currentTimeMillis();
            rs = stmt.executeQuery(sql);
            long endTime = System.currentTimeMillis();
            Double callTime = (endTime - startTime) / 1000.0;
            while (rs.next()) {
                // Set value to "" in LableValueBean if the value is empty string.
                // This covers when value is null. If we create LabelValueBean with null,
                // it will run into unexpected crash in some page.
                String sValue = rs.getString(valueCol);
                if (null == sValue) {
                    sValue = "";
                }
                list.add(new LabelValueBean(rs.getString(displayCol), sValue));
            }
            String callMethod = "Querier.doListQuery";
            if(fieldId!=null)
                callMethod = callMethod+"."+fieldId+".LOV";
            logQuerierCall(callMethod, callTime, sql, null);
            l.exiting(clsName, "doListQuery", list);
            return list;
        }
        finally {
            if (rs != null) DatabaseUtils.close(rs);
            if (stmt != null) DatabaseUtils.close(stmt);
            if (conn != null && closeQuery) DatabaseUtils.close(conn);
        }
    }

    /**
     * Execute a query via a PreparedStatement that will return a collection of label/value columns.
     * Returns an ArrayList of LabelValueBean objects
     *
     * @param sql        query
     * @param conn       JDBC Connection
     * @param parms      Array of parameters (QueryParm objects)
     * @param displayCol Label column # in query (1 based)
     * @param valueCol   Value column # in query (1 based)
     * @param closeQuery If true, connection will be closed after query is executed.
     * @return ArrayList of LabelValueBean objects
     * @throws SQLException
     * @see org.apache.struts.util.LabelValueBean
     */
    public static ArrayList doListQuery(String sql, Connection conn, Object[] parms, int displayCol,
                                        int valueCol, boolean closeQuery) throws SQLException {
        return doListQuery(null, sql, conn, parms, displayCol, valueCol, closeQuery);
    }

    /**
     * Execute a query via a PreparedStatement that will return a collection of label/value columns.
     * Returns an ArrayList of LabelValueBean objects
     *
     * @param fieldId    LOV FieldId
     * @param sql        query
     * @param conn       JDBC Connection
     * @param parms      Array of parameters (QueryParm objects)
     * @param displayCol Label column # in query (1 based)
     * @param valueCol   Value column # in query (1 based)
     * @param closeQuery If true, connection will be closed after query is executed.
     * @return ArrayList of LabelValueBean objects
     * @throws SQLException
     * @see org.apache.struts.util.LabelValueBean
     */
    public static ArrayList doListQuery(String fieldId, String sql, Connection conn, Object[] parms, int displayCol,
                                        int valueCol, boolean closeQuery) throws SQLException {
        Logger l = LogUtils.enterLog(Querier.class, "doListQuery", new Object[]
            {fieldId, sql, conn, parms, new Integer(displayCol), new Integer(valueCol), new Boolean(closeQuery)});
        ResultSet rs = null;
        PreparedStatement stmt = null;

        ArrayList list = new ArrayList();
        try {
            stmt = conn.prepareStatement(sql);
            int sz = parms.length;
            for (int i = 0; i < sz; i++) {
                QueryParm parm = (QueryParm) parms[i];
                l.fine(new StringBuffer("parm ").append(i).append("=").append(parm).toString());

                if (parm.value == null)
                    stmt.setNull(i + 1, parm.sqlType);
                else
                    stmt.setObject(i + 1, parm.value, parm.sqlType);
            }
            long startTime = System.currentTimeMillis();
            rs = stmt.executeQuery();
            long endTime = System.currentTimeMillis();
            Double callTime = (endTime - startTime) / 1000.0;
            while (rs.next()){
                // Set value to "" in LableValueBean if the value is empty string.
                // This covers when value is null. If we create LabelValueBean with null,
                // it will run into unexpected crash in some page.
                String sValue = rs.getString(valueCol);
                if (null == sValue) {
                    sValue = "";
                }
                list.add(new LabelValueBean(rs.getString(displayCol), sValue));
            }
            String callMethod = "Querier.doListQuery7";
            if(fieldId!=null)
                callMethod = callMethod+"."+fieldId+".LOV";
            logQuerierCall(callMethod, callTime, sql, parms);
            l.exiting(clsName, "doListQuery", list);
            return list;
        }
        finally {
            if (rs != null) DatabaseUtils.close(rs);
            if (stmt != null) DatabaseUtils.close(stmt);
            if (conn != null && closeQuery) DatabaseUtils.close(conn);
        }
    }

    /**
     * Execute a query that will return a collection of label/value columns.
     * Returns an ArrayList of LabelValueBean objects. The connection will
     * be closed after the query is executed.
     *
     * @param sql        query
     * @param conn       JDBC Connection
     * @param displayCol Label column # in query (1 based)
     * @param valueCol   Value column # in query (1 based)
     * @return ArrayList of LabelValueBean objects
     * @throws SQLException
     * @see org.apache.struts.util.LabelValueBean
     */
    public static ArrayList doListQuery(String sql, Connection conn, int displayCol, int valueCol)
            throws SQLException {

        return doListQuery(sql, conn, displayCol, valueCol, true);
    }

    /**
     * Execute a query via a PreparedStatement that will return a collection of label/value columns.
     * Returns an ArrayList of LabelValueBean objects. The connection will
     * be closed after the query is executed.
     *
     * @param sql        query
     * @param conn       JDBC Connection
     * @param parms      Array of parameters (QueryParm objects)
     * @param displayCol Label column # in query (1 based)
     * @param valueCol   Value column # in query (1 based)
     * @return ArrayList of LabelValueBean objects
     * @throws SQLException
     * @see org.apache.struts.util.LabelValueBean
     */
    public static ArrayList doListQuery(String sql, Connection conn, Object[] parms, int displayCol, int valueCol)
            throws SQLException {
        return doListQuery(sql, conn, parms, displayCol, valueCol, true);
    }

    /**
     * Creates a DisconnectedResultSet given an ActiveResultSet
     *
     * @param rs      Active ResultSet
     * @param maxRows max # rows to load into DisconnectedResultSet
     * @return DisconnectedResultSet
     * @throws SQLException
     */
    private static DisconnectedResultSet getDisconnectedResultSet(ResultSet rs, int maxRows)
            throws SQLException {
        return getDisconnectedResultSet(rs, maxRows, DEFAULT_DATA_LOAD_PROCESSOR);
    }

    /**
     * Creates a DisconnectedResultSet given an ActiveResultSet
     *
     * @param rs      Active ResultSet
     * @param maxRows max # rows to load into DisconnectedResultSet
     * @return DisconnectedResultSet
     * @throws SQLException
     */
    private static DisconnectedResultSet getDisconnectedResultSet(ResultSet rs, int maxRows, DataLoadProcessor dataLoadProcessor)
            throws SQLException {
        Logger l = LogUtils.enterLog(Querier.class, "getDisconnectedResultSet", new Object[]
                {rs, new Integer(maxRows)});

        DisconnectedResultSet drs = (maxRows == 0) ? new DisconnectedResultSet(rs, dataLoadProcessor) : new DisconnectedResultSet(rs, maxRows, dataLoadProcessor);
        l.exiting(clsName, "getDisconnectedResultSet", drs);
        return drs;
    }

    /**
     * Executes a query and returns a DisconnectedResultSet loaded with a predefined
     * # of rows.
     *
     * @param sql        query
     * @param conn       JDBC Connection
     * @param closeQuery if true, the connection will be closed after the query is executed.
     * @param maxRows    max # rows to load into DisconnectedResultSet, use -1 for all.
     * @return DisconnectedResultSet
     * @throws Exception
     */
    public static DisconnectedResultSet doQuery(String sql, Connection conn, boolean closeQuery, int maxRows) throws Exception {
        Logger l = LogUtils.enterLog(Querier.class, "doQuery", new Object[]
                {sql, conn, new Boolean(closeQuery), new Integer(maxRows)});

        DisconnectedResultSet drs = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            long startTime = System.currentTimeMillis();
            rs = stmt.executeQuery(sql);
            long endTime = System.currentTimeMillis();
            Double callTime = (endTime - startTime) / 1000.0;
            drs = getDisconnectedResultSet(rs, maxRows);
            logQuerierCall("Querier.doQuery4", callTime, sql, null);
            l.exiting(clsName, "doQuery", drs);
            return drs;
        }
        finally {
            if (rs != null) DatabaseUtils.close(rs);
            if (stmt != null) DatabaseUtils.close(stmt);
            if (conn != null && closeQuery) DatabaseUtils.close(conn);

        }

    }

    /**
     * Executes a query via a PreparedStatement and returns a DisconnectedResultSet loaded with a predefined
     * # of rows.
     *
     * @param sql        query with bound variables
     * @param conn       JDBC Connection
     * @param parms      Array of parameters (QueryParm objects)
     * @param closeQuery if true, the connection will be closed after the query is executed.
     * @param maxRows    max # rows to load into DisconnectedResultSet, use -1 for all.
     * @return DisconnectedResultSet
     * @throws Exception
     */
    public static DisconnectedResultSet doQuery(String sql, Connection conn, Object[] parms, boolean closeQuery, int maxRows) throws Exception {
        Logger l = LogUtils.enterLog(Querier.class, "doQuery", new Object[]
                {sql, conn, parms, new Boolean(closeQuery), new Integer(maxRows)});

        DisconnectedResultSet drs = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sql);
            int sz = parms.length;
            for (int i = 0; i < sz; i++) {
                QueryParm parm = (QueryParm) parms[i];
                l.fine(new StringBuffer("parm ").append(i).append("=").append(parm).toString());
                if (parm.value == null)
                    stmt.setNull(i + 1, parm.sqlType);
                else
                    stmt.setObject(i + 1, parm.value, parm.sqlType);
            }
            long startTime = System.currentTimeMillis();
            rs = stmt.executeQuery();
            long endTime = System.currentTimeMillis();
            Double callTime = (endTime - startTime) / 1000.0;
            drs = getDisconnectedResultSet(rs, maxRows);
            logQuerierCall("Querier.doQuery5", callTime, sql, parms);
            l.exiting(clsName, "doQuery", drs);
            return drs;
        }
        finally {
            if (rs != null) DatabaseUtils.close(rs);
            if (stmt != null) DatabaseUtils.close(stmt);
            if (conn != null && closeQuery) DatabaseUtils.close(conn);

        }

    }

    /**
     * Executes a function or stored procedure via a CallableStatement, finds a refcursor
     * and returns a DisconnectedResultSet loaded with all rows. ***<b><i>Note
     * the handleTransaction parameter.  This method will not work if you pass false for this
     * parameter but you do not turn off autocommit.</i></b>
     *
     * @param sql               query with bound variables
     * @param conn              JDBC Connection
     * @param parms             Array of parameters (QueryParm objects)
     * @param handleTransaction if true, this method will turn off autocommit, then
     *                          rollback when the query is finished.  Autocommit will be restored to its original value.
     *                          If you do not pass true for this parameter, you will need to manage the transaction on
     *                          your own.  This means you MUST turn of autocommit on the connection you pass and you
     *                          MUST rollback or commit the transaction yourself.
     * @return DisconnectedResultSet
     * @throws Exception
     */
    public static DisconnectedResultSet doRefCursorQuery(String sql, Connection conn, Object[] parms,
                                                         boolean handleTransaction) throws Exception {
        return doRefCursorQuery(sql, conn, parms, handleTransaction, -1, DEFAULT_DATA_LOAD_PROCESSOR);
    }

    /**
     * Executes a function or stored procedure via a CallableStatement, finds a refcursor
     * and returns a DisconnectedResultSet loaded with all rows. ***<b><i>Note
     * the handleTransaction parameter.  This method will not work if you pass false for this
     * parameter but you do not turn off autocommit.</i></b>
     *
     * @param sql               query with bound variables
     * @param conn              JDBC Connection
     * @param parms             Array of parameters (QueryParm objects)
     * @param handleTransaction if true, this method will turn off autocommit, then
     *                          rollback when the query is finished.  Autocommit will be restored to its original value.
     *                          If you do not pass true for this parameter, you will need to manage the transaction on
     *                          your own.  This means you MUST turn of autocommit on the connection you pass and you
     *                          MUST rollback or commit the transaction yourself.
     * @return DisconnectedResultSet
     * @throws Exception
     */
    public static DisconnectedResultSet doRefCursorQuery(String fieldId, String sql, Connection conn, Object[] parms,
                                                         boolean handleTransaction) throws Exception {
        return doRefCursorQuery(fieldId, sql, conn, parms, handleTransaction, -1, DEFAULT_DATA_LOAD_PROCESSOR);
    }

    /**
     * Executes a function or stored procedure via a CallableStatement, finds a refcursor
     * and returns a DisconnectedResultSet loaded with all rows. ***<b><i>Note
     * the handleTransaction parameter.  This method will not work if you pass false for this
     * parameter but you do not turn off autocommit.</i></b>
     *
     * @param sql               query with bound variables
     * @param conn              JDBC Connection
     * @param parms             Array of parameters (QueryParm objects)
     * @param handleTransaction if true, this method will turn off autocommit, then
     *                          rollback when the query is finished.  Autocommit will be restored to its original value.
     *                          If you do not pass true for this parameter, you will need to manage the transaction on
     *                          your own.  This means you MUST turn of autocommit on the connection you pass and you
     *                          MUST rollback or commit the transaction yourself.
     * @return DisconnectedResultSet
     * @throws Exception
     */
    public static DisconnectedResultSet doRefCursorQuery(String sql, Connection conn, Object[] parms,
                                                         boolean handleTransaction, DataLoadProcessor dataLoadProcessor) throws Exception {
        return doRefCursorQuery(sql, conn, parms, handleTransaction, -1, dataLoadProcessor);
    }

    /**
     * Executes a function or stored procedure via a CallableStatement, finds a refcursor
     * and returns a DisconnectedResultSet loaded with all rows. ***<b><i>Note
     * the handleTransaction parameter.  This method will not work if you pass false for this
     * parameter but you do not turn off autocommit.</i></b>
     *
     * @param sql   query with bound variables
     * @param conn  JDBC Connection
     * @param parms Array of parameters (QueryParm objects)
     * @return DisconnectedResultSet
     * @throws Exception
     */
    public static DisconnectedResultSet doRefCursorQuery(String sql, Connection conn, Object[] parms) throws Exception {
        return doRefCursorQuery(sql, conn, parms, false, -1);
    }


    /**
     * Executes a function or stored procedure via a CallableStatement, finds a refcursor
     * and returns a DisconnectedResultSet loaded with a predefined # of rows.  ***<b><i>Note
     * the handleTransaction parameter.  This method will not work if you pass false for this
     * parameter but you do not turn off autocommit.</i></b>
     *
     * @param sql               query with bound variables
     * @param conn              JDBC Connection
     * @param parms             Array of parameters (QueryParm objects)
     * @param handleTransaction if true, this method will turn off autocommit, then
     *                          rollback when the query is finished.  Autocommit will be restored to its original value.
     *                          If you do not pass true for this parameter, you will need to manage the transaction on
     *                          your own.  This means you MUST turn of autocommit on the connection you pass and you
     *                          MUST rollback or commit the transaction yourself.
     * @param maxRows           max # rows to load into DisconnectedResultSet, use -1 for all.
     * @return DisconnectedResultSet
     * @throws Exception
     */
    public static DisconnectedResultSet doRefCursorQuery(String sql, Connection conn, Object[] parms,
                                                         boolean handleTransaction, int maxRows) throws Exception {
        return doRefCursorQuery(sql, conn, parms, handleTransaction, maxRows, DEFAULT_DATA_LOAD_PROCESSOR);
    }

    /**
     * Executes a function or stored procedure via a CallableStatement, finds a refcursor
     * and returns a DisconnectedResultSet loaded with a predefined # of rows.  ***<b><i>Note
     * the handleTransaction parameter.  This method will not work if you pass false for this
     * parameter but you do not turn off autocommit.</i></b>
     *
     * @param sql               query with bound variables
     * @param conn              JDBC Connection
     * @param parms             Array of parameters (QueryParm objects)
     * @param handleTransaction if true, this method will turn off autocommit, then
     *                          rollback when the query is finished.  Autocommit will be restored to its original value.
     *                          If you do not pass true for this parameter, you will need to manage the transaction on
     *                          your own.  This means you MUST turn of autocommit on the connection you pass and you
     *                          MUST rollback or commit the transaction yourself.
     * @param maxRows           max # rows to load into DisconnectedResultSet, use -1 for all.
     * @return DisconnectedResultSet
     * @throws Exception
     */
    public static DisconnectedResultSet doRefCursorQuery(String sql, Connection conn, Object[] parms,
                                                         boolean handleTransaction, int maxRows, DataLoadProcessor dataLoadProcessor) throws Exception {
        return doRefCursorQuery(null, sql, conn, parms, handleTransaction,maxRows, dataLoadProcessor);
    }

    /**
     * Executes a function or stored procedure via a CallableStatement, finds a refcursor
     * and returns a DisconnectedResultSet loaded with a predefined # of rows.  ***<b><i>Note
     * the handleTransaction parameter.  This method will not work if you pass false for this
     * parameter but you do not turn off autocommit.</i></b>
     *
     * @param fieldId           LOV FieldId
     * @param sql               query with bound variables
     * @param conn              JDBC Connection
     * @param parms             Array of parameters (QueryParm objects)
     * @param handleTransaction if true, this method will turn off autocommit, then
     *                          rollback when the query is finished.  Autocommit will be restored to its original value.
     *                          If you do not pass true for this parameter, you will need to manage the transaction on
     *                          your own.  This means you MUST turn of autocommit on the connection you pass and you
     *                          MUST rollback or commit the transaction yourself.
     * @param maxRows           max # rows to load into DisconnectedResultSet, use -1 for all.
     * @return DisconnectedResultSet
     * @throws Exception
     */
    public static DisconnectedResultSet doRefCursorQuery(String fieldId, String sql, Connection conn, Object[] parms,
                                                         boolean handleTransaction, int maxRows, DataLoadProcessor dataLoadProcessor) throws Exception {
        Logger l = LogUtils.enterLog(Querier.class, "doRefCursorQuery", new Object[]
            {sql, conn, parms, new Boolean(handleTransaction), new Integer(maxRows)});

        DisconnectedResultSet drs = null;
        CallableStatement stmt = null;
        ResultSet rs = null;
        int refCursor = 0;
        boolean auto = conn.getAutoCommit();
        try {
            if (handleTransaction)
                conn.setAutoCommit(false);
            stmt = conn.prepareCall(sql);
            int sz = parms.length;
            for (int i = 0; i < sz; i++) {
                QueryParm parm = (QueryParm) parms[i];
                l.fine(new StringBuffer("parm ").append(i).append("=").append(parm).toString());
                if (parm.sqlType == OracleTypes.CURSOR) {
                    refCursor = i + 1;
                    stmt.registerOutParameter(i + 1, parm.sqlType);
                } else {
                    if (parm.value == null)
                        stmt.setNull(i + 1, parm.sqlType);
                    else
                        stmt.setObject(i + 1, parm.value, parm.sqlType);
                }
            }
            if (refCursor == 0) {
                String msg = "No RefCursor parameter found for sql " + sql + " with parms " + Arrays.toString(parms);
                l.warning(msg);
                throw new IllegalArgumentException(msg);
            }
            long startTime = System.currentTimeMillis();
            stmt.execute();
            long endTime = System.currentTimeMillis();
            Double callTime = (endTime - startTime) / 1000.0;
            rs = (ResultSet) stmt.getObject(refCursor);
            drs = getDisconnectedResultSet(rs, maxRows, dataLoadProcessor);
            String callMethod = "Querier.doRefCursorQuery";
            if(fieldId!=null)
                callMethod = callMethod+"."+fieldId+".LOV";
            logQuerierCall(callMethod, callTime, sql, parms);
            l.exiting(clsName, "doRefCursorQuery", drs);
            return drs;
        }
        finally {
            if (rs != null) DatabaseUtils.close(rs);
            if (stmt != null) DatabaseUtils.close(stmt);
            if (handleTransaction) {
                conn.rollback();
                conn.setAutoCommit(auto);
            }

        }

    }

    /**
     * Executes a query via a PreparedStatement and returns a DisconnectedResultSet loaded with all rows
     *
     * @param sql        Query
     * @param conn       JDBC Connection
     * @param parms      Array of parameters (QueryParm objects)
     * @param closeQuery if true, connection is closed after query is executed
     * @return DisconnectedResultset
     * @throws Exception
     */
    public static DisconnectedResultSet doQuery(String sql, Connection conn, Object[] parms, boolean closeQuery) throws Exception {
        return doQuery(sql, conn, parms, closeQuery, -1);
    }

    /**
     * Executes a query via a PreparedStatement and returns a DisconnectedResultSet loaded with all rows.
     * The connection will be closed after the query is executed.
     *
     * @param sql   Query
     * @param conn  JDBC Connection
     * @param parms Array of parameters (QueryParm objects)
     * @return DisconnectedResultSet
     * @throws Exception
     */
    public static DisconnectedResultSet doQuery(String sql, Connection conn, Object parms[]) throws Exception {
        return doQuery(sql, conn, parms, true, -1);
    }

    /**
     * Executes a query and returns a DisconnectedResultSet loaded with all rows
     *
     * @param sql        Query
     * @param conn       JDBC Connection
     * @param closeQuery if true, connection is closed after query is executed
     * @return DisconnectedResultset
     * @throws Exception
     */
    public static DisconnectedResultSet doQuery(String sql, Connection conn, boolean closeQuery) throws Exception {
        return doQuery(sql, conn, closeQuery, -1);
    }

    /**
     * Executes a query and returns a DisconnectedResultSet loaded with all rows.
     * The connection will be closed after the query is executed.
     *
     * @param sql  Query
     * @param conn JDBC Connection
     * @return DisconnectedResultSet
     * @throws Exception
     */
    public static DisconnectedResultSet doQuery(String sql, Connection conn) throws Exception {
        return doQuery(sql, conn, true, -1);
    }
/*	public static RowSet doQueryRowset(String sql) throws Exception{
		OracleCachedRowSet crs = new OracleCachedRowSet();
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = null;
		DataSource ds=null;
		try {
			conn = DBPool.getConnection("devpm1");
            stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			crs.populate(rs);
			return crs;
		}
		finally {
			if(rs!=null) rs.close();
			if(stmt!=null) stmt.close();
			if(conn!=null) conn.close();

		}

	}
	*/

    private static void logQuerierCall(String callMethod, Double callTime, String callSql , Object[] parms){
        StoredProcedureDAO spDAO = StoredProcedureDAO.getInstance("Querier.Loq_Querier");
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
            spDAO.logStoredProcedure(source, userName, callMethod, callTime, arguments, callSql,"QUERIER");
    }

    private static final DefaultDataLoadProcessor DEFAULT_DATA_LOAD_PROCESSOR = new DefaultDataLoadProcessor();
}
