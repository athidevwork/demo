package dti.pm.policymgr.renewalflagmgr.dao;

import dti.oasis.app.AppException;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.dao.BaseDAO;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   2/16/2016
 *
 * @author tzeng
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 02/24/2015       tzeng       Initial version.
 * ---------------------------------------------------
 */

public class RenewalFlagJdbcDAO extends BaseDAO implements RenewalFlagDAO{

    @Override
    public RecordSet loadAllRenewalFlag(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRenewalFlag", new Object[]{inputRecord, recordLoadProcessor});
        }

        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Renewal_Flag.Sel_Renewal_Flag");
            rs = spDao.execute(inputRecord, recordLoadProcessor);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load renewal flags", e);
            l.throwing(getClass().getName(), "loadAllRenewalFlag", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRenewalFlag", rs);
        }
        return rs;
    }

    @Override
    public int saveAllRenewalFlag(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllRenewalFlag", new Object[]{inputRecords});
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllRenewalFlag", new Object[]{inputRecords});
        }

        int processCount;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Renewal_Flag.Save_Renewal_Flag");
            processCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to insert renewal flags", e);
            l.throwing(getClass().getName(), "saveAllRenewalFlag", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllRenewalFlag", String.valueOf(processCount));
        }
        return processCount;
    }

    @Override
    public int deleteAllRenewalFlag(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "deleteAllRenewalFlag", new Object[]{inputRecords});
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "deleteAllRenewalFlag", new Object[]{inputRecords});
        }

        int processCount;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Renewal_Flag.Delete_Renewal_Flag");
            processCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to delete renewal flags", e);
            l.throwing(getClass().getName(), "deleteAllRenewalFlag", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "deleteAllRenewalFlag", String.valueOf(processCount));
        }
        return processCount;
    }

}
