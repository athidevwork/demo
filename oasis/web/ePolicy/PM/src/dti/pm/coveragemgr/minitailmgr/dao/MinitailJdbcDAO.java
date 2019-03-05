package dti.pm.coveragemgr.minitailmgr.dao;

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
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * this class is the implementation of MinitailDAO
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 20, 2007
 *
 * @author zlzhu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/20/2007       zlzhu     Created
 * 11/25/2010       bhong     114074 - Removed useless data mapping in loadAllMinitail method
 * 01/08/2013       fcb       137981 - changes related to Pm_Dates modifications.
 * 01/13/2017       eyin      180675 - Added field 'coverageBaseId' to the 'mapping' for UI change.
 * ---------------------------------------------------
 */

public class MinitailJdbcDAO extends BaseDAO implements MinitailDAO {

    /**
     * load the risk coverage data
     * <p/>
     *
     * @param inputRecord input record
     * @param recordLoadProcessor the load processor
     * @return the result which met the condition
     */
    public RecordSet loadAllMinitailRiskCoverage(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllMinitailRiskCoverage", new Object[]{inputRecord, recordLoadProcessor});
        RecordSet rs = null;

        // Create a DataRecordMapping for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("termEffDate", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExpDate", "termEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("endQuoteId", "endorsementQuoteId"));
		mapping.addFieldMapping(new DataRecordFieldMapping("coverageBaseId", "coverageBaseRecordId"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Sel_Minitail_Parent", mapping);
        try {
            rs = spDao.execute(inputRecord, recordLoadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load mini tail risk coverage.", e);
            l.throwing(getClass().getName(), "loadAllMinitailRiskCoverage", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "loadAllMinitailRiskCoverage", rs);
        return rs;
    }

    /**
     * load the risk mini tail
     * <p/>
     *
     * @param inputRecord input record
     * @param recordLoadProcessor load processor
     * @return the result which met the condition
     */
    public RecordSet loadAllMinitail(Record inputRecord,RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllMinitail", new Object[]{inputRecord});
        RecordSet rs = null;

        // Create a DataRecordMapping for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("termEffDate", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExpDate", "termEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("endQuoteId", "endorsementQuoteId"));
		mapping.addFieldMapping(new DataRecordFieldMapping("coverageBaseId", "coverageBaseRecordId"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Sel_Minitail_Child", mapping);

        try {
            rs = spDao.execute(inputRecord,recordLoadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load mini tail", e);
            l.throwing(getClass().getName(), "loadAllMinitail", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "loadAllMinitail", rs);
        return rs;
    }

    /**
     * save/update the mini tail data
     * <p/>
     *
     * @param inputRecords mini tail record set
     * @return number of the updated rows
     */
    public int saveAllMinitail(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllMinitail", new Object[]{inputRecords});

        int updateCount;

        // Create a DataRecordMapping for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("newId", "miniTailId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("toRateB", "toRateB"));
        mapping.addFieldMapping(new DataRecordFieldMapping("ratingBasis", "minitailRatingBasis"));
        mapping.addFieldMapping(new DataRecordFieldMapping("recordMode", "recordModeCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Save_Mini_Tail", mapping);
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save mini tail.", e);
            l.throwing(getClass().getName(), "saveAllMinitail", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllMinitail", new Integer(updateCount));
        }

        return updateCount;
    }

    /**
     * get the start date of current risk
     * <p/>
     *
     * @param inputRecords input record
     * @return the start date of current risk
     */
    public Date getRiskStartDate(Record inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "getRiskStartDate", new Object[]{inputRecords});
        RecordSet recordSet = null;
        // Create a DataRecordMapping for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("riskBaseId", "riskBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("findDate", "effectiveFromDate"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Dates.NB_Risk_StartDt", mapping);
        try {
            recordSet = spDao.execute(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save mini tail.", e);
            l.throwing(getClass().getName(), "getRiskStartDate", ae);
            throw ae;
        }
        Date retDate = recordSet.getSummaryRecord().getDateValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getRiskStartDate", retDate);
        }
        return retDate;
    }

    /**
     * get the mini tail effective date
     * <p/>
     *
     * @param inputRecords input record
     * @return the mini tail effective date
     */
    public Date getMiniRiskEffectiveDate(Record inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "getMiniRiskEffectiveDate", new Object[]{inputRecords});
        RecordSet recordSet = null;
        // Create a DataRecordMapping for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("coverageBaseId", "coverageBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("riskBaseId", "riskBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_GET_MIN_RISK_EFF_DATE", mapping);
        try {
            recordSet = spDao.execute(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save mini tail.", e);
            l.throwing(getClass().getName(), "getMiniRiskEffectiveDate", ae);
            throw ae;
        }
        Date retDate = recordSet.getSummaryRecord().getDateValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getMiniRiskEffectiveDate", retDate);
        }
        return retDate;
    }

    /**
     * Load All free mini tail
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllFreeMiniTail(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllFreeMiniTail", new Object[]{inputRecord});
        }
        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Coverage.Sel_Free_Mini_tail");
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load free mini tail", e);
            l.throwing(getClass().getName(), "loadAllFreeMiniTail", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllFreeMiniTail", rs);
        }
        return rs;
    }

    /**
     * Check if free mini tail exist
     *
     * @param inputRecord
     * @return int
     */
    public int checkFreeMiniTail(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "checkFreeMiniTail", new Object[]{inputRecord});
        }
        int rc;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Mini_Tail.PM_Check_Free_MiniTail", mapping);
            rc = spDao.execute(inputRecord).getSummaryRecord().getIntegerValue("returnValue").intValue();
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load free mini tail", e);
            l.throwing(getClass().getName(), "loadAllFreeMiniTail", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "checkFreeMiniTail", Integer.valueOf(rc));
        }

        return rc;
    }
}
