package dti.oasis.data;

import dti.oasis.error.ConfiguredDBException;
import dti.oasis.error.ErrorHandler;
import dti.oasis.request.RequestStorageIds;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.util.DBPool;
import dti.oasis.util.LogUtils;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the error handler in order to address any exception that are raised due to database related
 * problems.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 29, 2007
 *
 * @author mlmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 02/25/2010       James       Issue#104230 call clearStatementCache instead of resetConnectionPools
 * 04/21/2016       huixu       Issue#169769 Fix WebLogicSecurity.getAuthenticators to work in WebLogic 12.2.1
 * ---------------------------------------------------
 */
public class DBMechanic implements ErrorHandler {

    /**
     * Method that handles the reported exception and makes an attempt to fix it.
     *
     * @param error, an exception that needs to be fixed.
     * @return boolean true, if the exception is fixed successfully; otherwise, false.
     */
    public boolean handleError(Throwable error) {
        Logger l = LogUtils.enterLog(getClass(), "handleError", new Object[]{error});
        boolean isProblemFixed=false;

        if(error!=null) {
            if(error instanceof SQLException) {
                isProblemFixed = fixSQLProblem((SQLException) error);
            }
        }

        l.exiting(getClass().getName(), "handleError", String.valueOf(isProblemFixed));
        return isProblemFixed;
    }

    /**
     * Method that tries to fix the raised SQL Exception. Only exceptions that are configured to address are considered.
     */
    private boolean fixSQLProblem(SQLException sqlError) {
        Logger l = LogUtils.enterLog(getClass(), "fixSQLProblem", new Object[]{sqlError});
        boolean isProblemFixed=false;

        if (!isConnectionPoolAlreadyResetForRequest()) {
            isProblemFixed = fixProblemDueToInvalidConnectionPool(sqlError);
        }

        l.exiting(getClass().getName(), "fixSQLProblem", String.valueOf(isProblemFixed));
        return isProblemFixed;
    }

    /**
     * Method that resets the connection pool, if the reported error is configured via Spring to reset it.
     */
    private boolean fixProblemDueToInvalidConnectionPool(SQLException sqlError) {
        Logger l = LogUtils.enterLog(getClass(), "fixProblemDueToInvalidConnectionPool", new Object[]{sqlError});
        boolean isProblemFixed = false;

        boolean isErrorConfiguredToHandle=false;
        if(getErrorCodesForConnectionPoolReset()!=null) {
            for(int i=0;i<getErrorCodesForConnectionPoolReset().length && isErrorConfiguredToHandle==false;i++) {
                if(getErrorCodesForConnectionPoolReset()[i]==sqlError.getErrorCode()) {
                    isErrorConfiguredToHandle=true;
                }
            }
        }

        if(getErrorMessagePatternsForConnectionPoolReset()!=null && isErrorConfiguredToHandle==false) {
            for(int i=0;i<getErrorMessagePatternsForConnectionPoolReset().length && isErrorConfiguredToHandle==false;i++) {
                if(sqlError.getMessage().indexOf(getErrorMessagePatternsForConnectionPoolReset()[i])>=0) {
                    isErrorConfiguredToHandle=true;
                }
            }
        }

        if (isErrorConfiguredToHandle) {
            l.logp(Level.SEVERE, this.getClass().getName(), "fixProblemDueToInvalidConnectionPool", "Failed to handle the error config", sqlError);
            ConfiguredDBException ae = new ConfiguredDBException("Failed to handle the error configuration",sqlError);
            throw ae;
        }

        l.exiting(getClass().getName(), "fixProblemDueToInvalidConnectionPool", String.valueOf(isProblemFixed));
        return isProblemFixed;
    }

    public boolean isConnectionPoolAlreadyResetForRequest() {
        Logger l = LogUtils.enterLog(getClass(), "isConnectionPoolAlreadyResetForRequest");
        boolean isResetted=false;
        if(RequestStorageManager.getInstance().has(RequestStorageIds.IS_CONNECTION_POOL_RESETTED)) {
            isResetted = Boolean.getBoolean(String.valueOf(RequestStorageManager.getInstance().get(RequestStorageIds.IS_CONNECTION_POOL_RESETTED)));
        }
        l.exiting(getClass().getName(), "isConnectionPoolAlreadyResetForRequest", String.valueOf(isResetted));
        return isResetted;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public DBMechanic() {
        super();
    }

    public void verifyConfig() {
    }

    public int[] getErrorCodesForConnectionPoolReset() {
        return m_errorCodesForConnectionPoolReset;
    }

    public void setErrorCodesForConnectionPoolReset(int[] errorCodesForConnectionPoolReset) {
        m_errorCodesForConnectionPoolReset = errorCodesForConnectionPoolReset;
    }

    public String[] getErrorMessagePatternsForConnectionPoolReset() {
        return m_errorMessagePatternsForConnectionPoolReset;
    }

    public void setErrorMessagePatternsForConnectionPoolReset(String[] errorMessagePatternsForConnectionPoolReset) {
        m_errorMessagePatternsForConnectionPoolReset = errorMessagePatternsForConnectionPoolReset;
    }

    @Override
    public String toString() {
        return "DBMechanic{" +
                "m_lastFixTime=" + m_lastFixTime +
                ", m_errorMessagePatternsForConnectionPoolReset=" + Arrays.toString(m_errorMessagePatternsForConnectionPoolReset) +
                ", m_errorCodesForConnectionPoolReset=" + Arrays.toString(m_errorCodesForConnectionPoolReset) +
                '}';
    }

    private long m_lastFixTime;
    private String[] m_errorMessagePatternsForConnectionPoolReset;
    private int[] m_errorCodesForConnectionPoolReset;
}
