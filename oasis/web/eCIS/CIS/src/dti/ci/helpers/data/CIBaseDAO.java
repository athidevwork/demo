package dti.ci.helpers.data;

import dti.ci.helpers.CIHelper;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.DatabaseUtils;
import dti.oasis.util.SysParmProvider;

import java.io.Serializable;
import java.sql.*;
import java.util.logging.Logger;

/**
 * <p>Base Data Access Object superclass.</p>
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author Gerald C. Carney
 *         Date:   Dec 4, 2003
 *         <p/>
 *         Revision Date    Revised By  Description
 *         ----------------------------------------------------------
 *         04/14/2005       HXY         Moved commit logic back to BO.
 *         09/22/2008       Larry       issue 86826
 *         <p/>
 *         ----------------------------------------------------------
 */

public abstract class CIBaseDAO implements ICIBaseDAO, Serializable {

    /**
     * Standard save method.
     *
     * @param conn JDBC Connection object.
     * @param data Data to save.
     * @throws java.sql.SQLException
     */

    public void save(Connection conn, String data) throws SQLException {
        String methodName = "save";
        String methodDesc = "Class " + this.getClass().getName() +
                ", method " + methodName;
        Logger lggr = LogUtils.enterLog(this.getClass(), methodName,
                new Object[]{conn, data});
        if (StringUtils.isBlank(data)) {
            lggr.fine(methodDesc + ":  Data argument is blank.");
            lggr.exiting(this.getClass().getName(), methodName);
            return;
        }
        lggr.fine(methodDesc + ":  data = " + data);
        CallableStatement stmt = null;
        String sqlStatement = getUpdateSql();
        lggr.fine(methodDesc + ":  SQL statement = " + sqlStatement);
        try {
            stmt = conn.prepareCall(sqlStatement);
            stmt.setString(1, data);
            stmt.executeUpdate();
            lggr.exiting(this.getClass().getName(), methodName);
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (Exception ignore) {
            }
            try {
                lggr.throwing(this.getClass().getName(), methodName, e);
                String sqlStmtMsg = "Class " + this.getClass().getName() + ",  " +
                        methodDesc + ":  " +
                        "SQL statement:  " +
                        sqlStatement;
                lggr.info(sqlStmtMsg);
            } catch (Throwable ignore) {
            }
            throw e;
        } finally {
            if (stmt != null)
                //stmt.close();
                close(stmt);
        }
    }

    /**
     * Closed a JDBC Connection if it is valid.
     *
     * @param conn JDBC Connection to close.
     */
    public void closeConnection(Connection conn) {
        if (conn != null) {
            DatabaseUtils.close(conn);
        }

    }

    /**
     * Checks exception for Oracle specific things & returns more friendly message.
     *
     * @param e Exception object.
     * @return String - More friendly error msg if an ORA-20xxx error.
     * @throws Exception
     */
    public String checkException(Exception e) throws Exception {
        if (e instanceof SQLException) {
            if (e.getMessage().indexOf("ORA-20") == 0) {
                int nextMsg = e.getMessage().indexOf("ORA-", 10);
                if (nextMsg == -1)
                    return e.getMessage().substring(10);
                else
                    return e.getMessage().substring(10, nextMsg);
            }
        }
        throw e;
    }

    /**
     * Checks a PK string value to make sure it represents a long.
     *
     * @param pk The PK string value to be checked.
     * @return String - The PK value if it is a long;  otherwise -1.
     */
    public String checkPK(String pk) {
        if (StringUtils.isBlank(pk)) {
            pk = "";
        }
        if (CIHelper.isLong(pk)) {
            return pk;
        } else {
            return "-1";
        }
    }

    /**
     * Retrieve the entity's date of birth.
     *
     * @param conn     JDBC connection object.
     * @param entityPK Entity PK.
     * @return String  date of birth
     * @throws Exception
     */
    public String getDateOfBirth(Connection conn, long entityPK)
            throws Exception {
        String methodName = "getDateOfBirth";
        Logger lggr = LogUtils.enterLog(this.getClass(),
                methodName, new Object[]{conn, new Long(entityPK)});
        // get sql
        String sql = getDateOfBirthSQL();
        String dateOfBirth = "";
        CallableStatement cStmt = null;
        try {
            cStmt = conn.prepareCall(sql);
            cStmt.setLong(1, entityPK);
            cStmt.registerOutParameter(2, Types.VARCHAR);
            cStmt.execute();
            dateOfBirth = cStmt.getString(2);
            if (StringUtils.isBlank(dateOfBirth))
                dateOfBirth = "-1";
            lggr.exiting(this.getClass().getName(), methodName, dateOfBirth);
            return dateOfBirth;
        } finally {
            if (cStmt != null) close(cStmt);
        }
    }

    /**
     * Get the SQL to retrieve date of birth.
     * This should have 2 bound parms:
     * entityPK IN NUMBER
     * dateOfBirth OUT VARCHAR2
     *
     * @return SQL
     */
    public String getDateOfBirthSQL() {
        return "{ call wb_ci_relationship.get_date_of_birth(?,?) } ";
    }

    /**
     * Close a Statement if it is not null. Log any exceptions but otherwise ignore them.
     *
     * @param stmt JDBC Statement
     */
    public void close(Statement stmt) {
        if (stmt != null)
            DatabaseUtils.close(stmt);
    }

    /**
     * Close a ResultSet if it is not null. Log any exceptions but otherwise ignore them.
     *
     * @param rs JDBC ResultSet
     */
    public void close(ResultSet rs) {
        if (rs != null)
            DatabaseUtils.close(rs);
    }

    /**
     * Close a Statement and ResultSet if they are not null.  Will log any exceptions
     * but otherwise ignore them.
     *
     * @param stmt JDBC Statement
     * @param rs   JDBC ResultSet
     */
    public void close(Statement stmt, ResultSet rs) {
        DatabaseUtils.close(stmt, rs);
    }


    /**
     * Check if Hub functionality is enabled. If it's enabled,
     * we need to select from hub tables for some entity data.
     *
     * @return
     */
    protected boolean isHubEnabled() {
        return "Y".equalsIgnoreCase(SysParmProvider.getInstance().getSysParm("CI_ENABLE_HUB", "N"));
    }
}
