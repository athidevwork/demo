package dti.oasis.accesstrailmgr.dao;

import dti.oasis.app.AppException;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements methods to record user access activities;.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 12, 2010
 *
 * @author James
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class AccessTrailJdbcDAO implements AccessTrailDAO{

   /**
   * This method accepts the user activity information and saves it into database.
   *
   * @param inputRecord
   */
    public void addAccessTrail(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addAccessTrail", new Object[]{inputRecord});
        }

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(PROC_RECORD_ACITIVITY_HISTORY);
            spDao.executeUpdate(inputRecord);

        } catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save access trail into " + PROC_RECORD_ACITIVITY_HISTORY, e);
            l.throwing(getClass().getName(), "addAccessTrail", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "addAccessTrail");
    }

   /**
   * This method accepts the user activity information and saves it into database.
   *
   * @param inputRecord
   */
    public void addSessionInfo(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addSessionInfo", new Object[]{inputRecord});
        }

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(PROC_RECORD_SESSION_INFO);
            spDao.executeUpdate(inputRecord);

        } catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save session info into " + PROC_RECORD_SESSION_INFO, e);
            l.throwing(getClass().getName(), "addSessionInfo", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "addSessionInfo");
    }

     /**
     * This method get last login date/time.
     *
     * @param inputRecord Record contains input values
     * @return Date last login
     */
    public String getPriorLoginTimestamp(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPriorLoginTimestamp", new Object[]{inputRecord});
        }
        // get the return value
        String dateStr;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(FUNC_GET_PRIOR_LOGIN_TS);
        try {
            RecordSet outputRecordSet = spDao.execute(inputRecord);
            dateStr = outputRecordSet.getSummaryRecord().getStringValue(spDao.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to execute "+FUNC_GET_PRIOR_LOGIN_TS+".", e);
            l.throwing(getClass().getName(), "getPriorLoginTimestamp", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "getPriorLoginTimestamp", dateStr);
        return dateStr;
    }

    /**
     * load all web applications
     *
     * @param record
     * @param recordLoadProcessor
     * @return
     */
    public RecordSet loadAllActiveUsers(Record record, RecordLoadProcessor recordLoadProcessor) {
        String methodName = "loadAllActiveUsers";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{record, recordLoadProcessor});
        }
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(PROC_GET_SESSION_INFO);
            RecordSet recordSet = spDao.execute(record, recordLoadProcessor);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), methodName, recordSet);
            }
            return recordSet;

        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load active users information", e);
            l.throwing(getClass().getName(), methodName, ae);
            throw ae;
        }

    }

    private final Logger l = LogUtils.getLogger(getClass());

    private static String PROC_RECORD_ACITIVITY_HISTORY = "OASIS_ACCESS.Add_Access_Trail";
    private static String PROC_RECORD_SESSION_INFO = "OASIS_ACCESS.Add_Session_Info";
    private static String PROC_GET_SESSION_INFO = "OASIS_ACCESS.Select_Session_Info";
    private static String FUNC_GET_PRIOR_LOGIN_TS = "OASIS_ACCESS.Get_Prior_Login_Timestamp";
}
