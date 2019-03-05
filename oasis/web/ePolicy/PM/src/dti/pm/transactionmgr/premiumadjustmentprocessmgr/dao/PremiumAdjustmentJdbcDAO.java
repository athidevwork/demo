package dti.pm.transactionmgr.premiumadjustmentprocessmgr.dao;

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
 * This class implements the PremiumAdjustmentDAO interface.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   July 9, 2007
 *
 * @author rlli
 */


public class PremiumAdjustmentJdbcDAO extends BaseDAO implements PremiumAdjustmentDAO {


    /**
     * load all coverage
     *
     * @param inputRecord (policy_id, transaction_code)
     * @return coverage record set
     */
    public RecordSet loadAllCoverage(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllCoverage", new Object[]{inputRecord});
        RecordSet rs = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Premium_Adjustment.Sel_Coverage_Info");

        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load coverage information ", e);
            l.throwing(getClass().getName(), "loadAllCoverage", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllCoverage", rs);
        }
        return rs;
    }

    /**
     * load all premium adjustment
     *
     * @param inputRecord (transactionLogId)
     * @return premium adjustment recordSet
     */
    public RecordSet loadAllPremiumAdjustment(Record inputRecord, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllPremiumAdjustment", new Object[]{inputRecord});
        RecordSet rs = null;

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Premium_Adjustment.Sel_Premium_Adjustment_Info");
        try {
            rs = spDao.execute(inputRecord, loadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load premium adjustment information", e);
            l.throwing(getClass().getName(), "loadAllPremiumAdjustment", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllPremiumAdjustment", rs);
        }
        return rs;
    }


    /**
     * save all premium adjustment
     *
     * @param inputRecord
     * @return returnCode
     */
    public String saveAllPremiumAdjustment(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllSharedDetail", new Object[]{inputRecord});

        String returnValue;

        // Create a DataRecordMapping for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("transId", "transactionLogId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("covBaseId", "coverageBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("covCode", "productCoverageCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("compVal", "componentValue"));
        mapping.addFieldMapping(new DataRecordFieldMapping("compSign", "componentSign"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));

        // Version the records in batch mode with 'Pm_Endorse.Change_Coverage'
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Prem_Adjustment", mapping);
        try {
            returnValue = spDao.execute(inputRecord).getSummaryRecord().getStringValue("retCode");
        }

        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save shared detail.", e);
            l.throwing(getClass().getName(), "saveAllPremiumAdjustment", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "saveAllPremiumAdjustment", returnValue);
        return returnValue;
    }


    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public PremiumAdjustmentJdbcDAO() {
    }
}
