package dti.oasis.util;

import dti.oasis.app.AppException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.request.RequestStorageIds;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.session.UserSessionIds;
import dti.oasis.session.UserSessionManager;
import dti.oasis.struts.ActionHelper;
import dti.oasis.struts.IOasisAction;
import oracle.jdbc.OracleTypes;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * OASISDatabase Utility Methods
 *
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 20, 2004
 *
 * @author jbe
 */
/*
 *  Revision Date    Revised By  Description
 *  ---------------------------------------------------
 *  6/22/2004       jbe         Add createClob
 *                              and freeClob
 *  8/3/2004        jbe         Add evaluateSqlExpression
 *  4/4/2005        jbe         Add close methods
 *  4/5/2005        jbe         Add setProtectedFields
 *  6/1/2004        jbe         Add getNewPK
 *  9/1/2005        jbe         Add getLong, getInt, getDouble & getFloat
 *  1/10/2006       sjz         Add clobToString
 * 01/23/2007       wer         Changed use of InitialContext to using ApplicationContext;
 *                              Added getDefaultDBPoolId();
 *  04/09/2008      wer         Enhanced getDefaultDBPoolId() to throw an exception if role-base dbPoolId configuration is required.
 * 07/14/2008       mlm         Enhanced to add setWebSessionId()
 * 09/19/2008       wer         Added close method to accept Statement, ResultSet, and Connection
 * 09/08/2010       Michael     Added  method :ClobToString(Object in)
 * 09/14/2010       Michael     Delete method :ClobToString(Clob in)
 * 08/29/2014       parker      Issue 138227. Enhancement to add the ows logs.
 * 05/11/2016       wdang       176749 - Added setWebContext as one entrance to store session variables in database.
 * 06/27/2016       Parker      Issue#177786 Remove final String in local method
 * 02/22/2017       tzeng       168385 - Modified setWebContext() to set requested transaction time.
 * 10/24/2018       mlm         196111 - Refactored to handle unicode characters correctly for UTF-8 encoding
 *  ---------------------------------------------------
 */

public class DatabaseUtils {
    private static String clsName = DatabaseUtils.class.getName();

    /**
     * Calls CS_Web_Utility.Set_Web_Context
     *
     * @param conn            JDBC Connection
     * @param userId
     * @param webSessionId
     * @param protectedFields
     * @param sourceContext
     * @throws java.sql.SQLException
     */
    public static void setWebContext(Connection conn,
                                     String userId,
                                     String webSessionId,
                                     String protectedFields,
                                     String sourceContext,
                                     String notificationTransactionTime) throws SQLException {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(clsName, "setWebContext", new Object[]{conn, sourceContext, notificationTransactionTime});
        }
        CallableStatement stmt = null;
        try {
            stmt = conn.prepareCall("{call CS_Web_Utility.Set_Web_Context(?,?,?,?,?)}");
            stmt.setString(1, userId);
            stmt.setString(2, webSessionId);
            if (protectedFields == null) {
                stmt.setNull(3, Types.VARCHAR);
            } else {
                stmt.setString(3, protectedFields);
            }
            stmt.setString(4, sourceContext);
            stmt.setString(5,notificationTransactionTime);
            stmt.executeUpdate();
        } finally {
            close(stmt);
        }
        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(clsName, "setWebContext");
        }
    }

    /**
     * Return the first Database Pool Id listed in the dbPoolId configuration property.
     *
     * @throws ConfigurationException if none of the associated roles are configured for a dbPoolId and role-based dbPoolId is required.
     */
    public static String getDefaultDBPoolId() throws ConfigurationException {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(clsName, "getDefaultDBPoolId");
        }

        if (isRoleBasedDBPoolIdRequired()) {
            ConfigurationException ce = new ConfigurationException("Cannot return the default dbPoolId because role-base dbPoolId configuration is required");
            c_l.throwing(clsName, "getDefaultDBPoolId", ce);
            throw ce;
        }

        String dbPoolId = null;
        try {
            dbPoolId = ApplicationContext.getInstance().getProperty(IOasisAction.KEY_ENVDBPOOLID).split(",")[0];
        } catch (Exception e) {
            dti.oasis.app.ConfigurationException ce = new dti.oasis.app.ConfigurationException("Failed to determine the default connection pool.", e);
            c_l.throwing(clsName, "getDefaultDBPoolId", ce);
            throw ce;
        }
        if (StringUtils.isBlank(dbPoolId)) {
            dti.oasis.app.ConfigurationException ce = new dti.oasis.app.ConfigurationException("There are no DB Pools IDs configured.");
            c_l.throwing(clsName, "getDefaultDBPoolId", ce);
            throw ce;
        }

        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(clsName, "getDefaultDBPoolId", dbPoolId);
        }
        return dbPoolId;
    }

    /**
     * Returns true if role-base dbPoolId configuration is required. Otherwise, false.
     */
    public static boolean isRoleBasedDBPoolIdRequired() {
        return YesNoFlag.getInstance(ApplicationContext.getInstance().getProperty("require.role.based.dbPoolId")).booleanValue();
    }

    /**
     * Formats a string value to be put into a SQL statement, e.g.,
     * changes "O'Neil" to "O''Neil".  Returns a blank instead of null.
     *
     * @param value The data value to be formatted.
     * @return String - The formatted data value.
     */
    public static String encodeValue(String value) {
        return (value == null) ? "" : (StringUtils.isBlank(value)) ? value : value.replaceAll("'", "''");
    }

    /**
     * Free the clob we created before
     *
     * @param conn
     * @throws SQLException
     */
    public static void freeClob(Connection conn) throws SQLException {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(clsName, "freeClob", new Object[]{conn});
        }

        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate("DELETE web_clob_holder WHERE WEB_CLOB_HOLDER_PK = 1");
        } finally {
            close(stmt);
        }
        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(clsName, "freeClob");
        }
    }

    /**
     * Create a temporary clob from the database using the table web_clob_holder.
     * Store the clobValue parameter in the CLOB.  This wraps the J2EE Container
     * CLOB which wraps the Oracle clob.  For this to work you must set the
     * connection's autocommit property to false.  You must also rollback the transaction
     * after you are done with the clob.
     *
     * @param conn      Live JDBC Connection
     * @param clobValue The value to put in the clob.
     * @return a Clob
     * @throws Exception
     */
    public static Clob createClob(Connection conn, String clobValue) throws Exception {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(clsName, "createClob", new Object[]{conn, clobValue});
        }
        Statement stmt = null;
        Statement stmt2 = null;
        ResultSet rs = null;
        //boolean auto = conn.getAutoCommit();
        Clob clob = null;
        Writer wri = null;
        try {
            // Autocommit must be false for the clob to work!
            //conn.setAutoCommit(false);
            stmt = conn.createStatement();
            stmt2 = conn.createStatement();
            // create an empty clob
            stmt.executeUpdate("INSERT into web_clob_holder VALUES (1,EMPTY_CLOB())");
            // get the clob
            rs = stmt2.executeQuery("SELECT clob_value from web_clob_holder where WEB_CLOB_HOLDER_PK = 1 for update");
            if (rs.next())
                clob = rs.getClob(1);
            else
                throw new SQLException("No CLOB!");

            // Get a writer so that we can write into the clob.  We need
            // a wrapper for this
            try {
                wri = ClobWrapperSelector.getClobFactory().getInstance(clob).setCharacterStream(1);
            } catch (Exception e) {
                c_l.throwing(clsName, "createClob", e);
                throw e;
            }
            // Store the data in the clob
            wri.write(clobValue.toCharArray());
            // flush!
            wri.flush();
        } finally {
            //conn.rollback();
            // put autocommit back to where it was
            //conn.setAutoCommit(auto);
            // close everything
            if (wri != null) wri.close();
            close(stmt);
            close(stmt2, rs);
        }
        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(clsName, "createClob");
        }
        return clob;
    }

    /**
     * Evaluates a SQL Expression.  Executes the sql, and returns the
     * value in the column referenced by the col parameter in the first row.
     * Returns null if no rows.
     *
     * @param conn JDBC Connection
     * @param sql  SQL to execute
     * @param col  column # to get
     * @return col# column of 1st row
     * @throws java.sql.SQLException
     */
    public static String evaluateSqlExpression(Connection conn, String sql, int col) throws SQLException {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(clsName, "evaluateSqlExpression", new Object[]{conn, sql, col});
        }
        Statement stmt = null;
        ResultSet rs = null;
        String val = null;
        try {
            stmt = conn.createStatement();
            c_l.fine(new StringBuffer("Executing ").append(sql).toString());
            rs = stmt.executeQuery(sql);
            if (rs.next())
                val = rs.getString(col);
        } finally {
            close(stmt, rs);
        }
        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(clsName, "evaluateSqlExpression", val);
        }
        return val;
    }

    /**
     * Close a JDBC ResultSet, logging, but otherwise ignoring exceptions.
     *
     * @param rs
     */
    public static void close(ResultSet rs) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(clsName, "close", new Object[]{rs});
        }
        try {
            if (rs != null) rs.close();
        } catch (SQLException ignore) {
            c_l.logp(Level.WARNING, clsName, "close", "Failed to close the ResultSet", ignore);
        }
        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(clsName, "close");
        }
    }

    /**
     * Close a JDBC Statement, logging, but otherwise ignoring exceptions.
     *
     * @param stmt
     */
    public static void close(Statement stmt) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(clsName, "close", new Object[]{stmt});
        }
        try {
            if (stmt != null) stmt.close();
        } catch (SQLException ignore) {
            c_l.logp(Level.WARNING, clsName, "close", "Failed to close the Statement", ignore);
        }
        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(clsName, "close");
        }
    }

    /**
     * Close a JDBC Connection, logging, but otherwise ignoring exceptions.
     *
     * @param conn
     */
    public static void close(Connection conn) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(clsName, "close", new Object[]{conn});
        }
        try {
            if (conn != null) conn.close();
            c_l.exiting(clsName, "close");
        } catch (SQLException ignore) {
            c_l.logp(Level.WARNING, clsName, "close", "Failed to close the Connection", ignore);
        }
        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(clsName, "close");
        }
    }

    /**
     * Close a JDBC ResultSet and Statement, logging, but otherwise ignoring exceptions.
     *
     * @param stmt
     * @param rs
     */
    public static void close(Statement stmt, ResultSet rs) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(clsName, "close", new Object[]{stmt, rs});
        }
        close(rs);
        close(stmt);
        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(clsName, "close");
        }
    }

    /**
     * Close a JDBC Statement and Connection, logging, but otherwise ignoring exceptions.
     */
    public static void close(Statement stmt, Connection conn) {
        close(stmt);
        close(conn);
    }

    /**
     * Close a JDBC Statement, ResultSet and Connection, logging, but otherwise ignoring exceptions.
     */
    public static void close(Statement stmt, ResultSet rs, Connection conn) {
        close(stmt);
        close(rs);
        close(conn);
    }

    /**
     * Returns next sequence from OASIS_SEQUENCE
     *
     * @param conn Live JBDC Connection
     * @return OASIS_SEQUENCE.nextval
     * @throws Exception
     */
    public static long getNewPK(Connection conn) throws Exception {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(clsName, "getNewPK", new Object[]{conn});
        }
        Statement stmt = null;
        ResultSet rs = null;
        String sql = "select oasis_sequence.nextval from dual";
        long newPK;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                newPK = rs.getLong(1);
            } else {
                throw new Exception("Failed to get new PK!");
            }
        } finally {
            close(stmt, rs);
        }

        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(clsName, "getNewPK", new Long(newPK));
        }
        return newPK;
    }

    /**
     * Returns next sequence from OASIS_SEQUENCE
     *
     * @return OASIS_SEQUENCE.nextval
     * @throws Exception
     */
    public static long getNewPK() throws Exception {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(clsName, "getNewPK");
        }

        long newPk;
        String dbPoolId = (String) UserSessionManager.getInstance().getUserSession().get(UserSessionIds.DB_POOL_ID);
        if (dbPoolId == null) {
            AppException ae = new AppException("Failed to get new PK. The DB Pool is not set for user session.");
            c_l.throwing(clsName, "getNewPK", ae);
            throw ae;
        }

        Connection conn = null;
        try {
            conn = DBPool.getConnection(dbPoolId);
            newPk = getNewPK(conn);
            if (c_l.isLoggable(Level.FINER)) {
                c_l.exiting(clsName, "getNewPK", Long.valueOf(newPk));
            }
        } finally {
            close(conn);
        }
        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(clsName, "getNewPK", new Long(newPk));
        }
        return newPk;
    }

    /**
     * Returns next sequence from oasis_access_trail_seq
     *
     * @return oasis_access_trail_seq.nextval
     */
    public static String getNewPK(String sequenceId) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(clsName, "getNewPK");
        }

        dti.oasis.session.UserSession session = UserSessionManager.getInstance().getUserSession();
        String dbPoolId;
        String owsAccessTrailId = "";
        if (session.has(UserSessionIds.DB_POOL_ID)) {
            dbPoolId = (String) session.get(UserSessionIds.DB_POOL_ID);
        } else {
            RequestStorageManager rsm = RequestStorageManager.getInstance();
            HttpServletRequest request = (HttpServletRequest) rsm.get(RequestStorageIds.HTTP_SEVLET_REQUEST);
            dbPoolId = ActionHelper.getDbPoolId(request);
        }
        if (dbPoolId == null) {
            AppException ae = new AppException("Failed to get new PK. The DB Pool is not set for user session.");
            c_l.throwing(clsName, "getNewPK", ae);
            throw ae;
        }

        Statement stmt = null;
        ResultSet rs = null;
        Connection conn = null;
        String sql = "select " + sequenceId + " from dual";
        try {
            conn = DBPool.getConnection(dbPoolId);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                owsAccessTrailId = rs.getString(1);
            }
        } catch (Exception e) {
            c_l.throwing(clsName, "getNewOasisAccessTrailId", e);
        } finally {
            close(stmt, rs, conn);
        }

        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(clsName, "getNewOasisAccessTrailId");
        }
        return owsAccessTrailId;
    }

    /**
     * Retrieves the value of the designated column in the current row of this ResultSet object
     * as a Long object.
     *
     * @param rs  ResultSet
     * @param col The first column is 1, the second is 2, ...
     * @return The column value; if the value is SQL NULL, the value returned is NULL
     * @throws SQLException if a database access error occurs
     */
    public static Long getLong(ResultSet rs, int col) throws SQLException {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(clsName, "getLong", new Object[]{rs, col});
        }

        long val = rs.getLong(col);
        Long retVal = (rs.wasNull()) ? null : new Long(val);

        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(clsName, "getLong", retVal);
        }
        return retVal;
    }

    /**
     * Retrieves the value of the designated column in the current row of this ResultSet object
     * as a Long object.
     *
     * @param rs  ResultSet
     * @param col the SQL name of the column
     * @return The column value; if the value is SQL NULL, the value returned is NULL
     * @throws SQLException if a database access error occurs
     */
    public static Long getLong(ResultSet rs, String col) throws SQLException {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(clsName, "getLong", new Object[]{rs, col});
        }

        long val = rs.getLong(col);
        Long retVal = (rs.wasNull()) ? null : new Long(val);

        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(clsName, "getLong", retVal);
        }
        return retVal;
    }

    /**
     * Retrieves the value of the designated column in the current row of this ResultSet object
     * as a Integer object.
     *
     * @param rs  ResultSet
     * @param col The first column is 1, the second is 2, ...
     * @return The column value; if the value is SQL NULL, the value returned is NULL
     * @throws SQLException if a database access error occurs
     */
    public static Integer getInteger(ResultSet rs, int col) throws SQLException {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(clsName, "getInteger", new Object[]{rs, col});
        }

        int val = rs.getInt(col);
        Integer retVal = (rs.wasNull()) ? null : new Integer(val);

        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(clsName, "getInteger", retVal);
        }
        return retVal;
    }

    /**
     * Retrieves the value of the designated column in the current row of this ResultSet object
     * as a Integer object.
     *
     * @param rs  ResultSet
     * @param col the SQL name of the column
     * @return The column value; if the value is SQL NULL, the value returned is NULL
     * @throws SQLException if a database access error occurs
     */
    public static Integer getInteger(ResultSet rs, String col) throws SQLException {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(clsName, "getInteger", new Object[]{rs, col});
        }

        int val = rs.getInt(col);
        Integer retVal = (rs.wasNull()) ? null : new Integer(val);

        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(clsName, "getInteger", retVal);
        }
        return retVal;
    }

    /**
     * Retrieves the value of the designated column in the current row of this ResultSet object
     * as a Float object.
     *
     * @param rs  ResultSet
     * @param col The first column is 1, the second is 2, ...
     * @return The column value; if the value is SQL NULL, the value returned is NULL
     * @throws SQLException if a database access error occurs
     */
    public static Float getFloat(ResultSet rs, int col) throws SQLException {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(clsName, "getFloat", new Object[]{rs, col});
        }

        float val = rs.getFloat(col);
        Float retVal = (rs.wasNull()) ? null : new Float(val);

        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(clsName, "getFloat", retVal);
        }
        return retVal;
    }

    /**
     * Retrieves the value of the designated column in the current row of this ResultSet object
     * as a Float object.
     *
     * @param rs  ResultSet
     * @param col the SQL name of the column
     * @return The column value; if the value is SQL NULL, the value returned is NULL
     * @throws SQLException if a database access error occurs
     */
    public static Float getFloat(ResultSet rs, String col) throws SQLException {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(clsName, "getFloat", new Object[]{rs, col});
        }

        float val = rs.getFloat(col);
        Float retVal = (rs.wasNull()) ? null : new Float(val);

        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(clsName, "getFloat", retVal);
        }
        return retVal;
    }

    /**
     * Retrieves the value of the designated column in the current row of this ResultSet object
     * as a Double object.
     *
     * @param rs  ResultSet
     * @param col The first column is 1, the second is 2, ...
     * @return The column value; if the value is SQL NULL, the value returned is NULL
     * @throws SQLException if a database access error occurs
     */
    public static Double getDouble(ResultSet rs, int col) throws SQLException {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(clsName, "getDouble", new Object[]{rs, col});
        }

        double val = rs.getDouble(col);
        Double retVal = (rs.wasNull()) ? null : new Double(val);

        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(clsName, "getDouble", retVal);
        }
        return retVal;
    }

    /**
     * Retrieves the value of the designated column in the current row of this ResultSet object
     * as a Double object.
     *
     * @param rs  ResultSet
     * @param col the SQL name of the column
     * @return The column value; if the value is SQL NULL, the value returned is NULL
     * @throws SQLException if a database access error occurs
     */
    public static Double getDouble(ResultSet rs, String col) throws SQLException {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(clsName, "getDouble", new Object[]{rs, col});
        }

        double val = rs.getDouble(col);
        Double retVal = (rs.wasNull()) ? null : new Double(val);

        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(clsName, "getDouble", retVal);
        }
        return retVal;
    }

    /**
     * Convert a given SQL CLOB type of value to plain String
     *
     * @param in the clob value
     * @return The text in the clob value
     * @throws Exception
     */

    public static String ClobToString(Object in) throws Exception {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(clsName, "ClobToString", new Object[]{in});
        }
        String clobResult = "";
        if ("java.sql.Clob".equals(in.getClass().getName())) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            BufferedReader br = new BufferedReader(((java.sql.Clob) in).getCharacterStream());
            String line = null;

            while ((line = br.readLine()) != null) {
                pw.println(line);
            }

            pw.close();

            clobResult = String.valueOf(sw.toString());
        } else if ("oracle.sql.CLOB".equals(in.getClass().getName())) {
            String rtn = "";
            oracle.sql.CLOB clob = (oracle.sql.CLOB) in;
            InputStream input = clob.getAsciiStream();
            int len = (int) clob.length();
            byte[] by = new byte[len];
            int i;
            while (-1 != (i = input.read(by, 0, by.length))) {
                input.read(by, 0, i);
            }
            rtn = new String(by);
            rtn = clob.getSubString((long) 1, (int) clob.length());
            clobResult = String.valueOf(rtn);
        } else if ("weblogic.jdbc.wrapper.Clob_oracle_sql_CLOB".equals(in.getClass().getName())) {
            String rtn = "";
            Method method = in.getClass().getMethod("getVendorObj", new Class[]{});
            oracle.sql.CLOB clob = (oracle.sql.CLOB) method.invoke(in);
            // getCharacterStream() returns unicode encoded string.
            // Do not use getAsciiStream() while may throw exception if unicode characters were used in the clob field.
            Reader reader = clob.getCharacterStream();
            int len = (int) clob.length();
            char[] data = new char[len];
            int bytesRead;
            StringBuffer sb = new StringBuffer();
            while((bytesRead = reader.read(data, 0, len)) != -1) {
                sb.append(data);
            }
            clobResult = String.valueOf(sb);
        } else {
            clobResult = String.valueOf(in.toString());
        }

        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(clsName, "ClobToString", clobResult);
        }
        return clobResult;
    }

    /**
     * decrypt String
     *
     * @param stringToDecrypt
     * @return String
     */
    public static String getDecryptedString(String DBPoolId, String stringToDecrypt) throws SQLException {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(clsName, "getDecryptString", new Object[]{stringToDecrypt});
        }

        String decryptedString = null;
        ResultSet rs = null;

        StringBuffer sbToDecrypt = new StringBuffer("");
        for (int i = 0; i < stringToDecrypt.length(); i++) {
            if (i > 0) {
                sbToDecrypt.append("||");
            }
            sbToDecrypt.append("chr(").append((int) stringToDecrypt.charAt(i)).append(")");
        }

        Connection conn = null;
        CallableStatement cs = null;
        try {
            conn = DBPool.getConnection(DBPoolId);
            c_l.fine("About to execute [ufe_process.get_decrypt_string] with parameter [" + sbToDecrypt.toString() + "]");
            // Setup the call.
            cs = conn.prepareCall("{call ufe_process.get_decrypt_string (?, ?) }");
            cs.setString("i_string_to_decrypt", sbToDecrypt.toString());
            cs.registerOutParameter("o_ref_curs", OracleTypes.CURSOR);
            cs.executeUpdate();

            rs = (ResultSet) cs.getObject("o_ref_curs");
            while (rs.next()) {
                decryptedString = rs.getString(1);
                break;
            }
            rs.close();
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get decrypted string", e);
            c_l.throwing(clsName, "getDecryptedString", ae);
            throw ae;
        } catch (Exception e1) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get decrypted string", e1);
            c_l.throwing(clsName, "getDecryptedString", ae);
            throw ae;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (cs != null) {
                cs.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(clsName, "getDecryptString", decryptedString);
        }
        return decryptedString;
    }

    private static final Logger c_l = LogUtils.getLogger(DatabaseUtils.class);
}
