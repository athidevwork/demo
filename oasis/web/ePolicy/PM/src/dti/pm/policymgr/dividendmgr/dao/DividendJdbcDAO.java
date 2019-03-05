package dti.pm.policymgr.dividendmgr.dao;

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
 * This class implements the DividendDAO interface.
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 30, 2011
 *
 * @author wfu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/13/2012       wfu         128705 - Added related methods to handle new dividend process.
 * 12/26/2013       awu         148187 - Added loadAllTransferRisk, transferDividend, loadDividendAudit.
 * ---------------------------------------------------
 */

public class DividendJdbcDAO extends BaseDAO implements DividendDAO {

    /**
     * Load all dividend rules
     *
     * @param inputRecord
     * @param loadProcessor
     * @return RecordSet
     */
    public RecordSet loadAllDividendRule(Record inputRecord, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllDividendRule", new Object[]{inputRecord, loadProcessor});
        }

        RecordSet rs = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Dividend.Sel_Dividend_Declare");
        try {
            rs = spDao.execute(inputRecord, loadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load dividend rules", e);
            l.throwing(getClass().getName(), "loadAllDividendRule", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllDividendRule", rs);
        }

        return rs;
    }

    /**
     * Load all prior dividend
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllPriorDividend(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllPriorDividend", new Object[]{inputRecord});
        }

        RecordSet rs = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Dividend.Sel_Prior_Dividend");
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load prior dividend information", e);
            l.throwing(getClass().getName(), "loadAllPriorDividend", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllPriorDividend", rs);
        }

        return rs;
    }

    /**
     * Load all calculated dividend
     *
     * @param inputRecord
     * @param loadProcessor
     * @return RecordSet
     */
    public RecordSet loadAllCalculatedDividend(Record inputRecord, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllCalculatedDividend", new Object[]{inputRecord, loadProcessor});
        }

        RecordSet rs = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Dividend.Sel_Calculated_Dividend");
        try {
            rs = spDao.execute(inputRecord, loadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load calculated dividend", e);
            l.throwing(getClass().getName(), "loadAllCalculatedDividend", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllCalculatedDividend", rs);
        }

        return rs;
    }

    /**
     * load all dividend report summary
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllDividendReportSummary(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllDividendReportSummary", new Object[]{inputRecord});
        }

        RecordSet rs = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Dividend.Sel_Dividend_Report_Summary");
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load dividend report summary", e);
            l.throwing(getClass().getName(), "loadAllDividendReportSummary", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllDividendReportSummary", rs);
        }

        return rs;
    }

    /**
     * Load all dividend report detail
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllDividendReportDetail(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllDividendReportDetail", new Object[]{inputRecord});
        }

        RecordSet rs = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Dividend.Sel_All_Dividend_Report_Detail");
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load dividend report detail", e);
            l.throwing(getClass().getName(), "loadAllDividendReportDetail", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllDividendReportDetail", rs);
        }

        return rs;
    }

    /**
     * Save all changes of dividend rule
     *
     * @param inputRecords
     * @return
     */
    public void saveAllDividendRule(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllDividendRule", new Object[]{inputRecords});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Dividend.Save_Dividend_Rule");
        try {
            spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save dividend rule.", e);
            l.throwing(getClass().getName(), "saveAllDividendRule", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllDividendRule");
        }
    }

    /**
     * Calculate given dividend
     *
     * @param inputRecord
     * @return record with return code and return message
     */
    public Record calculateDividend(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "calculateDividend", new Object[]{inputRecord});
        }

        Record record = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Dividend.Calculate_Dividend");
        try {
            record = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to calculate dividend.", e);
            l.throwing(getClass().getName(), "calculateDividend", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "calculateDividend");
        }

        return record;
    }

    /**
     * Post the selected dividends
     *
     * @param inputRecord
     * @return record with return code and return message
     */
    public Record postDividend(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "postDividend", new Object[]{inputRecord});
        }

        Record record = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Dividend.Post_Dividend");
        try {
            record = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to post dividend.", e);
            l.throwing(getClass().getName(), "postDividend", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "postDividend");
        }

        return record;
    }

    /**
     * Load all dividend declaration rules
     *
     * @param inputRecord
     * @param loadProcessor
     * @return RecordSet
     */
    public RecordSet loadAllDividendDeclare(Record inputRecord, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllDividendDeclare", new Object[]{inputRecord, loadProcessor});
        }

        RecordSet rs = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Dividend_Manual.Sel_Dividend_Rule");
        try {
            rs = spDao.execute(inputRecord, loadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load dividend declaration", e);
            l.throwing(getClass().getName(), "loadAllDividendDeclare", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllDividendDeclare", rs);
        }

        return rs;
    }

    /**
     * Save all changes of dividend declaration
     *
     * @param inputRecords
     * @return
     */
    public void saveAllDividendDeclare(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllDividendDeclare", new Object[]{inputRecords});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Dividend_Manual.Save_Dividend_Declare");
        try {
            spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save dividend declaration.", e);
            l.throwing(getClass().getName(), "saveAllDividendDeclare", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllDividendDeclare");
        }
    }

    /**
     * Load all dividend records
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllDividendForPreview(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllDividendForPreview", new Object[]{inputRecord});
        }

        RecordSet rs = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Dividend_Manual.Preview_Dividend");
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load dividend records", e);
            l.throwing(getClass().getName(), "loadAllDividendForPreview", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllDividendForPreview", rs);
        }

        return rs;
    }

    /**
     * Process the selected dividends
     *
     * @param inputRecord
     * @return
     */
    public void performProcessDividend(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performProcessDividend", new Object[]{inputRecord});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Dividend_Manual.Process_Dividend");
        try {
            spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to process dividend.", e);
            l.throwing(getClass().getName(), "performProcessDividend", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performProcessDividend");
        }
    }

    /**
     * Load all processed dividend records
     *
     * @param inputRecord
     * @param loadProcessor
     * @return RecordSet
     */
    public RecordSet loadAllProcessedDividend(Record inputRecord, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllProcessedDividend", new Object[]{inputRecord, loadProcessor});
        }

        RecordSet rs = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Dividend_Manual.Sel_Processed_Dividend");
        try {
            rs = spDao.execute(inputRecord, loadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load processed dividend records", e);
            l.throwing(getClass().getName(), "loadAllProcessedDividend", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllProcessedDividend", rs);
        }

        return rs;
    }

    /**
     * Post the selected dividends
     *
     * @param inputRecord
     * @return record with return code and return message
     */
    public Record performPostDividend(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performPostDividend", new Object[]{inputRecord});
        }

        Record record = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Dividend_Manual.Post_Dividend");
        try {
            record = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to post dividend.", e);
            l.throwing(getClass().getName(), "performPostDividend", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performPostDividend");
        }

        return record;
    }

    /**
     * Load out all the available risks which can do dividend transfer.
     *
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllTransferRisk(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllTransferRisk", new Object[]{inputRecord});
        }

        RecordSet rs = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Dividend_Manual.Sel_Transfer_Info");
        try {
            rs = spDao.execute(inputRecord, recordLoadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load the available risk to do transfer", e);
            l.throwing(getClass().getName(), "loadAllTransferRisk", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllTransferRisk", rs);
        }

        return rs;
    }

    /**
     * Process dividend transfer.
     *
     * @param inputRecord
     * @return
     */
    public Record transferDividend(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "transferDividend", new Object[]{inputRecord});
        }

        Record summaryRec = null;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("auditFkList", "transferAuditDividendList"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Dividend_Manual.Transfer_Dividend", mapping);
        try {
            RecordSet rs = spDao.execute(inputRecord);
            summaryRec = rs.getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to do dividend transfer", e);
            l.throwing(getClass().getName(), "transferDividend", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "transferDividend");
        }
        return summaryRec;
    }

    /**
     * Load out all the dividend audit data.
     *
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllDividendAudit(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllDividendAudit", new Object[]{inputRecord});
        }

        RecordSet rs = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Dividend_Manual.Sel_Policy_Dividend_Audit");
        try {
            rs = spDao.execute(inputRecord, recordLoadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load dividend audit data.", e);
            l.throwing(getClass().getName(), "loadAllDividendAudit", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllDividendAudit", rs);
        }
        return rs;
    }
}
