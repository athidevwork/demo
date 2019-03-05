package dti.pm.policymgr.validationmgr.dao;

import dti.oasis.app.AppException;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.dao.BaseDAO;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   12/7/2016
 *
 * @author tzeng
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/07/2016       tzeng       166929 - Initial version.
 * ---------------------------------------------------
 */
public class SoftValidationJdbcDAO extends BaseDAO implements SoftValidationDAO{
    private final Logger l = LogUtils.getLogger(getClass());

    @Override
    public RecordSet loadSoftValidation(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadSoftValidation", inputRecord);
        }

        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Web_Policy.Sel_Soft_Validation");
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load soft validation", e);
            l.throwing(getClass().getName(), "loadSoftValidation", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadSoftValidation", rs);
        }
        return rs;
    }

    @Override
    public int saveAllSoftValidation(RecordSet inputRecords) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllSoftValidation", inputRecords);
        }

        int processCount;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Notify.Save_Validation");
            processCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to insert soft validation", e);
            l.throwing(getClass().getName(), "saveAllSoftValidation", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllSoftValidation", String.valueOf(processCount));
        }
        return processCount;
    }

    @Override
    public int deleteAllSoftValidation(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "deleteAllSoftValidation", inputRecord);
        }

        int processCount;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Notify.Delete_Validation");
            RecordSet rs = spDao.execute(inputRecord);
            processCount = rs.getSize();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to delete soft validation", e);
            l.throwing(getClass().getName(), "deleteAllSoftValidation", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "deleteAllSoftValidation", String.valueOf(processCount));
        }
        return processCount;
    }

    @Override
    public long getLatestSoftValidationTransaction(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getLatestSoftValidationTransaction", inputRecord);
        }

        long returnValue;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Transaction.Get_Latest_Soft_Valid_Trans");
            returnValue = spDao.execute(inputRecord).getSummaryRecord().getLongValue(StoredProcedureDAO.RETURN_VALUE_FIELD).longValue();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get latest soft validation transaction", e);
            l.throwing(getClass().getName(), "getLatestSoftValidationTransaction", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getLatestSoftValidationTransaction", String.valueOf(returnValue));
        }
        return returnValue;
    }
}
