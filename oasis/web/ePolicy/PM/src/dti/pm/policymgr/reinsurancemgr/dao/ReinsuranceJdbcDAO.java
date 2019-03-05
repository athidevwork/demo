package dti.pm.policymgr.reinsurancemgr.dao;

import dti.oasis.app.AppException;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.util.LogUtils;
import dti.pm.core.dao.BaseDAO;
import dti.pm.policymgr.reinsurancemgr.impl.ReinsuranceEntitlementRecordLoadProcessor;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the ReinsuranceDAO interface.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 30, 2007
 *
 * @author rlli
 */

public class ReinsuranceJdbcDAO extends BaseDAO implements ReinsuranceDAO {

    /**
     * Retrieves all reinsurance information.
     *
     * @param record input record
     * @param recordLoadProcessor loadProcessor
     * @return recordSet
     */
    public RecordSet loadAllReinsurance(Record record, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllReinsurance", new Object[]{record});
        }
        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Reinsurance.Sel_Reinsurance_Info");
            rs = spDao.execute(record,recordLoadProcessor);
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAllReinsurance", rs);
            }
            return rs;
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load reinsurance information", e);
            l.throwing(getClass().getName(), "loadAllReinsurance", ae);
            throw ae;
        }
    }

    /**
     * Save all reinsurance information.
     *
     * @param inputRecords intput record
     * @return the number of row updateds
     */
    public int saveAllReinsurance(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllReinsurance");

        int updateCount = 0;
        // Insert the records in batch mode with 'Pm_Save_Screens.Save_Pol_Spec_Handling'
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Reinsurance.Save_Reinsurance_Info");
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save reinsurance.", e);
            l.throwing(getClass().getName(), "saveAllReinsurance", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllReinsurance", new Integer(updateCount));
        }
        return updateCount;
    }

    /**
     * Delete all reinsurance information.
     *
     * @param inputRecords intput record
     * @return the number of row updateds
     */
    public int deleteAllReinsurance(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "deleteAllReinsurance");
        int updateCount = 0;

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Reinsurance.Delete_Reinsurance_Info");
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to delete reinsurance.", e);
            l.throwing(getClass().getName(), "deleteAllReinsurance", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "deleteAllReinsurance", new Integer(updateCount));
        }
        return updateCount;
    }
}
