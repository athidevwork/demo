package dti.pm.core.dao;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.util.DatabaseUtils;
import dti.oasis.util.LogUtils;
import oracle.jdbc.OracleTypes;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
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
 *
 * ---------------------------------------------------
 */
public class BaseDAO {

    protected Connection getConnection() {
        Logger l = LogUtils.enterLog(getClass(), "getConnection", new Object[]{});
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
        Logger l = LogUtils.enterLog(getClass(), "closeConnection", new Object[]{conn});

        DatabaseUtils.close(conn);

        l.exiting(getClass().getName(), "closeConnection");
        return;
    }

    /**
     * Returns the appropriate SQL Type for a cursor
     *
     * @return OracleTypes.CURSOR
     */
    protected int getCursorType() {
        return OracleTypes.CURSOR;
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
