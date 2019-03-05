package dti.ci.core.dao;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.util.DatabaseUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import oracle.jdbc.OracleTypes;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides DAO Layer with basic most commonly used DAO methods.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 9, 2006
 *
 * @author mlmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 27/08/2008       Larry   "throw null" in method checkException is not right for jdk1.4 compiling, issue 86102
 * 05/30/2016       dpang    Issue 149588
 * ---------------------------------------------------
 */
public class BaseDAO {

    private final Logger l = LogUtils.getLogger(getClass());

    protected Connection getConnection() {
        l.entering(getClass().getName(), "getConnection");

        Connection conn = null;
        try {
            conn = getAppDataSource().getConnection();
        } catch (SQLException e) {
            AppException ae = new AppException("Failed to get connection", e);
            l.throwing(getClass().getName(), "getConnection", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "getConnection", conn);
        return conn;
    }

    protected void closeConnection(Connection conn) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "closeConnection", new Object[]{conn});
        }

        DatabaseUtils.close(conn);

        l.exiting(getClass().getName(), "closeConnection");
    }

    /**
     * Returns the appropriate SQL Type for a cursor
     *
     * @return OracleTypes.CURSOR
     */
    protected int getCursorType() {
        return OracleTypes.CURSOR;
    }


        /**
     * Checks exception for Oracle specific things & returns more friendly message.
     *
     * @param e Exception object.
     * @return String - More friendly error msg if an ORA-20xxx error.
     * @param e Exception object.
     * @throws Exception
     */
    public String checkException(Exception e) {
        return this.checkException(e, true);
    }

    /**
     * Checks exception for Oracle specific things & returns more friendly message.
     *
     * @param e Exception object.
     * @param logMsg   Write the message to the log?
     * @return String - More friendly error msg if an ORA-20xxx error.
     * @throws Exception
     */
    public String checkException(Exception e, boolean logMsg) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "checkException", new Object[]{e, logMsg});
        }

        if (e instanceof SQLException) {
            int pos = e.getMessage().lastIndexOf("ORA-20");
            if(pos > -1) {
                int pos1 = e.getMessage().indexOf("ORA-", pos + 1);
                String msg = (pos1 < 0) ? e.getMessage().substring(pos + 11) :
                        e.getMessage().substring(pos + 11, pos1 - 1);
                if (pos1 > -1) {
                    if (logMsg) {
                        l.warning(new StringBuffer("***Reporting error to user:\n").
                            append(msg).append("\nFull Exception Details:\n").
                            append(e.getMessage()).toString());
                    }
                }

                l.exiting(getClass().getName(), "checkException", msg);
                return msg.replaceAll(",","&#44;");
            }
        }
        return  null;
    }

    /**
     * handle SQLException in DAOs
     *
     * @param se
     * @param messageKey
     * @param callingClassName
     * @param callingMethodName
     */
    protected void handleSQLException(SQLException se, String messageKey, String callingClassName, String callingMethodName) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "handleSQLException", new Object[]{se, messageKey, callingClassName, callingMethodName});
        }

        String errorMessage = checkException(se, false);
        MessageManager.getInstance().addErrorMessage(messageKey, new String[]{StringUtils.isBlank(errorMessage) ? se.getCause().getMessage() : errorMessage});

        AppException appException = new AppException(errorMessage);
        l.throwing(callingClassName, callingMethodName, appException);
        throw appException;
    }

    protected AppException handleSQLException(SQLException se, String messageKey) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "handleSQLException", new Object[]{se, messageKey});
        }

        String errorMessage = checkException(se, false);
        MessageManager.getInstance().addErrorMessage(messageKey, new String[]{StringUtils.isBlank(errorMessage) ? se.getCause().getMessage() : errorMessage});

        AppException appException = new AppException(errorMessage);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "handleSQLException", appException);
        }
        return appException;
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

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getAppDataSource() == null)
            throw new ConfigurationException("The required property 'appDataSource' is missing.");
    }

    public DataSource getAppDataSource() {
        return m_appDataSource;
    }

    public void setAppDataSource(DataSource appDataSource) {
        m_appDataSource = appDataSource;
    }

    private DataSource m_appDataSource;
}
