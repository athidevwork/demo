package dti.oasis.codelookupmgr.dao;

import dti.oasis.codelookupmgr.impl.CodeLookupManagerImpl;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.QueryParm;
import dti.oasis.util.DisconnectedResultSet;
import dti.oasis.util.Querier;
import dti.oasis.util.DatabaseUtils;
import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.SQLException;

import oracle.jdbc.OracleTypes;
import org.apache.struts.util.LabelValueBean;

import javax.sql.DataSource;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 30, 2006
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 11/18/2011       Jerry       Issue#126056 Filter drop down options which both key-value are Null Objects in the LOV option list.
 * ---------------------------------------------------
 */
public class CodeLookupJdbcDAO implements CodeLookupDAO {

    /**
     * Execute the named stored procedure. The expected format is:
     * [&]EXEC [codeColumnIndex][labelColumnIndex][storedProcName('&fieldId1&', '&fieldId2&','&fieldIdN&', ?)]
     * where:
     * [&] - the custom delimiter
     * codeColumnIndex - the 1-based index of the result set column that contains the lookup code
     * labelColumnIndex - the 1-based index of the result set column that contains the lookup label
     * storedProcName - the name of the stored procedure
     * fieldId1, fieldId2, fieldIdN - any number of input parameters that use Oasis fieldId values as input.
     * ? - placeholder for the OUT REF CURSOR
     *
     * TODO: Remove the Connection as a parameter when all application use Spring Configuration to configure the CodeLookupJdbcDAO with a DataSource.
     *
     * @return a List of LabelValueBeans
     */
    public ArrayList executeLovStoredProcedure(String fieldId, Connection conn, String sql) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "executeLovStoredProcedure", new Object[]{sql});
        }

        long startTime = System.currentTimeMillis();

        ArrayList list = new ArrayList();
        String originalSql = sql;
        sql = sql.substring("EXEC".length()).trim();
        int codeIdx = 1;
        int labelIdx = 1;
        int idx = 0;
        if (sql.startsWith("[")) {
            String codeIdxStr = sql.substring(1);
            idx = codeIdxStr.indexOf("]");
            if (idx > 0) {
                idx++;
                sql = codeIdxStr.substring(idx);   // remove the codeIdxStr field name from sql
                codeIdxStr = codeIdxStr.substring(0, idx - 1);   // get the codeIdxStr field name
            }
            codeIdx = Integer.parseInt(codeIdxStr);
        }
        if (sql.startsWith("[")) {
            String labelIdxStr = sql.substring(1);
            idx = labelIdxStr.indexOf("]");
            if (idx > 0) {
                idx++;
                sql = labelIdxStr.substring(idx);   // remove the labelIdxStr field name from sql
                labelIdxStr = labelIdxStr.substring(0, idx - 1);   // get the labelIdxStr field name
            }
            labelIdx = Integer.parseInt(labelIdxStr);
        }

        sql = (sql.startsWith("[") ? sql.substring(1) : sql);
        sql = (sql.endsWith("]") ? sql.substring(0, sql.length() - 1) : sql).trim();

        // Validate the format of the sql string
        if (StringUtils.isBlank(sql)) {
            throw new IllegalArgumentException("Invalid List of Values format for executing a stored procedure. lov sql string: " + originalSql);
        }

        sql = "BEGIN " + (sql.endsWith(";") ? sql : sql + ";") + " END;";
        Object[] parms = new Object[]{
            new QueryParm(OracleTypes.CURSOR, null)
        };
        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "executeLovStoredProcedure", "***** FINAL SQL : " + sql + " codeIdx: " + codeIdx + " labelIdx: " + labelIdx);
        }

        DisconnectedResultSet rs = null;
        // TODO: Uncomment the lines to get and close the connection when all application use Spring Configuration to configure the CodeLookupJdbcDAO with a DataSource.
//        Connection conn = null;
        boolean passedConnection = conn != null;
        try {
            if (!passedConnection) {
                conn = getConnection();
            }
            // TODO: Replace with a call to the SQLStatementDAO when it is available.
            rs = Querier.doRefCursorQuery(fieldId, sql, conn, parms, false);
        } catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to invoke the stored procedure sql: " + sql, e);
            l.throwing(getClass().getName(), "executeLovStoredProcedure", ae);
            throw ae;
        } finally {
            if (!passedConnection) {
                DatabaseUtils.close(conn);
            }
        }

        // Iterate through the result set, loading LabeValueBeans for each row
        if (rs != null) {
            while (rs.next()) {
                String value = rs.getString(codeIdx);
                if (null == rs.getString(codeIdx)) value = "";
                list.add(new LabelValueBean(rs.getString(labelIdx), value));
            }
        }
        else {
            l.logp(Level.WARNING, getClass().getName(), "executeLovStoredProcedure", "There are no List of Values returned when executing sql: " + sql);
        }

        long endTime = System.currentTimeMillis();
        if (getWarningTime() > 0 && endTime - startTime > getWarningTimeInMillis() || l.isLoggable(Level.FINE)) {
            l.logp((endTime - startTime > getWarningTimeInMillis() ? Level.WARNING : Level.FINE), getClass().getName(), "executeLovStoredProcedure",
                "Executed the following lookup procedure for fieldId<"+fieldId+"> in " + ((endTime - startTime) / 1000.0) + " seconds: " + sql);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "executeLovStoredProcedure", list);
        }
        return list;

    }

    /**
     * Execute a SQL statement that will return a ref cursor with code and label columns.
     *
     * TODO: Remove the Connection as a parameter when all application use Spring Configuration to configure the CodeLookupJdbcDAO with a DataSource.
     *
     * @return a List of LabelValueBeans
     */
    public ArrayList executeLovSQL(String fieldId, Connection conn, String sql) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "executeLovSQL", new Object[]{sql});
        }

        long startTime = System.currentTimeMillis();

        // TODO: Uncomment the lines to get and close the connection when all application use Spring Configuration to configure the CodeLookupJdbcDAO with a DataSource.
//        Connection conn = null;
        boolean passedConnection = conn != null;
        ArrayList list = null;
        try {
            if (!passedConnection) {
                conn = getConnection();
            }
            list = Querier.doListQuery(fieldId, sql, conn, 2, 1, false);
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to invoke the sql: " + sql, e);
            l.throwing(getClass().getName(), "executeLovSQL", ae);
            throw ae;
        } finally {
            if (!passedConnection) {
                DatabaseUtils.close(conn);
            }
        }

        long endTime = System.currentTimeMillis();
        if (getWarningTime() > 0 && endTime - startTime > getWarningTimeInMillis() || l.isLoggable(Level.FINE)) {
            l.logp((endTime - startTime > getWarningTimeInMillis() ? Level.WARNING : Level.FINE), getClass().getName(), "executeLovStoredProcedure",
                "Executed the following lookup query for fieldId<"+fieldId+"> in " + ((endTime - startTime) / 1000.0) + " seconds: " + sql);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "executeLovSQL", list);
        }
        return list;
    }

    /**
     * Execute the query to load the List of Values from the LOOKUP_CODE table for the given lookup type code.
     * <p/>
     * TODO: Remove the Connection as a parameter when all application use Spring Configuration to configure the CodeLookupJdbcDAO with a DataSource.
     *
     * @return a List of LabelValueBeans
     */
    public ArrayList executeLovLookup(String fieldId, Connection conn, String lookupLovSql, String lookupTypeCode, boolean displayLongDesc) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "executeLovLookup", new Object[]{lookupTypeCode});
        }

        long startTime = System.currentTimeMillis();

        // Sort rows by short/long descriptions
        String sql = LOOKUP_CODE_SQL;
        if (displayLongDesc) {
            sql += " ORDER BY 3";
        }
        else {
            sql += " ORDER BY 2";
        }

        // TODO: Uncomment the lines to get and close the connection when all application use Spring Configuration to configure the CodeLookupJdbcDAO with a DataSource.
//        Connection conn = null;
        boolean passedConnection = conn != null;
        ArrayList list = null;
        try {
            if (!passedConnection) {
                conn = getConnection();
            }

            Object[] parms = new Object[]{
                new QueryParm(OracleTypes.VARCHAR, lookupTypeCode)
            };

            int codeIdx = 1;
            int labelIdx = displayLongDesc ? 3 : 2;
            list = Querier.doListQuery(fieldId, sql, conn, parms, labelIdx, codeIdx, false);
            
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to invoke the sql: " + sql, e);
            l.throwing(getClass().getName(), "executeLovLookup", ae);
            throw ae;
        } finally {
            if (!passedConnection) {
                DatabaseUtils.close(conn);
            }
        }

        long endTime = System.currentTimeMillis();
        if (getWarningTime() > 0 && endTime - startTime > getWarningTimeInMillis() || l.isLoggable(Level.FINE)) {
            l.logp((endTime - startTime > getWarningTimeInMillis() ? Level.WARNING : Level.FINE), getClass().getName(), "executeLovStoredProcedure",
                "Executed the following LOOKUP type query for fieldId<"+fieldId+"> in " + ((endTime - startTime) / 1000.0) + " seconds: lovsql<" + lookupLovSql + ">; sql: " + sql);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "executeLovLookup", list);
        }
        return list;
    }

    protected Connection getConnection() throws SQLException {
        return getReadOnlyDataSource().getConnection();
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public CodeLookupJdbcDAO() {
    }

    public void verifyConfig() {
        // TODO: Uncomment the check for a ReadOnlyDataSource when all application use Spring Configuration to configure the CodeLookupJdbcDAO with a DataSource.
//        if (getReadOnlyDataSource() == null)
//            throw new ConfigurationException("The required property 'readOnlyDataSource' is missing.");
    }

    public DataSource getReadOnlyDataSource() {
        return m_readOnlyDataSource;
    }

    public void setReadOnlyDataSource(DataSource readOnlyDataSource) {
        m_readOnlyDataSource = readOnlyDataSource;
    }

    public float getWarningTime() {
        return m_warningTime;
    }

    public long getWarningTimeInMillis() {
        return (long) (m_warningTime * 1000);
    }

    public void setWarningTime(float warningTime) {
        m_warningTime = warningTime;
    }

    private DataSource m_readOnlyDataSource;
    private float m_warningTime;
    private final Logger l = LogUtils.getLogger(getClass());

    private static final String LOOKUP_CODE_SQL = "SELECT code, short_description, long_description FROM lookup_code WHERE lookup_type_code = ? ";
}
