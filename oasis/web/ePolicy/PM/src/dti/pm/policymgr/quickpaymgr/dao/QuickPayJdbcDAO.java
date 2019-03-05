package dti.pm.policymgr.quickpaymgr.dao;

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
 * this class is an implements class for QuickPayDAO
 * <p/>
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * Date:   July 22, 2010
 *
 * @author Dzhang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/19/2010       dzhang      Update per Bill's comments.
 * 09/01/2010       dzhang      103800 - Modified the MessageSource key.
 * ---------------------------------------------------
 */

public class QuickPayJdbcDAO extends BaseDAO implements QuickPayDAO {

    /**
     * To load all quick pay transaction data.
     * <p/>
     *
     * @param inputRecord input record
     * @return quick pay transaction recordset
     */
    public RecordSet loadAllQuickPayTransaction(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllQuickPayTransaction", new Object[]{inputRecord});
        }

        RecordSet rs;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Quick_Pay.Sel_Quick_Pay_Transaction");
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load quick pay transaction data.", e);
            l.throwing(getClass().getName(), "loadAllQuickPayTransaction", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllQuickPayTransaction", new Object[]{rs});
        }
        return rs;
    }

    /**
     * To load all quick pay transaction history data.
     * <p/>
     *
     * @param inputRecord input record
     * @return quick pay transaction history recordset
     */
    public RecordSet loadAllTransactionHistory(Record inputRecord, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllTransactionHistory", new Object[]{inputRecord, loadProcessor});
        }

        RecordSet rs;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Quick_Pay.Sel_Transaction_History");
        try {
            rs = spDao.execute(inputRecord, loadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load quick pay transaction history data.", e);
            l.throwing(getClass().getName(), "loadAllTransactionHistory", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllTransactionHistory", new Object[]{rs});
        }
        return rs;
    }

    /**
     * To load all quick pay risks/coverages data.
     * <p/>
     *
     * @param inputRecord input record
     * @return quick pay risks/coverages recordset
     */
    public RecordSet loadAllRiskCoverage(Record inputRecord, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRiskCoverage", new Object[]{inputRecord, loadProcessor});
        }

        RecordSet rs;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Quick_Pay.Sel_Risk_Coverage");
        try {
            rs = spDao.execute(inputRecord, loadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load quick pay risks/coverages data.", e);
            l.throwing(getClass().getName(), "loadAllRiskCoverage", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRiskCoverage", new Object[]{rs});
        }
        return rs;
    }

    /**
     * To load quick pay summary data.
     * <p/>
     *
     * @param inputRecord input record
     * @return record contains quick pay summary data.
     */
    public Record loadQuickPaySummary(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadQuickPaySummary", new Object[]{inputRecord});
        }

        Record record = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Quick_Pay.Sel_Summary");

        try {
            RecordSet rs = spDao.execute(inputRecord);
            if (rs.getSize() > 0) {
                record = rs.getFirstRecord();
            }
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load quick pay summary data.", e);
            l.throwing(getClass().getName(), "loadQuickPaySummary", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadQuickPaySummary", new Object[]{record});
        }

        return record;
    }

    /**
     * Remove quick pay discount.
     * <p/>
     *
     * @param inputRecord input record
     * @return quick pay recordset
     */
    public RecordSet removeQuickPayDiscount(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "removeQuickPayDiscount", new Object[]{inputRecord});
        }

        RecordSet recordSet;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("termBaseId", "termBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("origTransLogId", "transactionLogId"));

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Manual_Quickpay.PM_Remove_QuickPay", mapping);
            recordSet = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to remove quick pay discount data.", e);
            l.throwing(getClass().getName(), "removeQuickPayDiscount", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "removeQuickPayDiscount", new Object[]{recordSet});
        }
        return recordSet;
    }

    /**
     * Give quick pay discount.
     * <p/>
     *
     * @param inputRecord input record
     * @return quick pay recordset
     */
    public RecordSet addQuickPayDiscount(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addQuickPayDiscount", new Object[]{inputRecord});
        }

        RecordSet recordSet;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("termBaseId", "termBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("origTransLogId", "transactionLogId"));

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Manual_Quickpay.PM_Give_QP_Percent", mapping);
            recordSet = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to give quick pay discount data.", e);
            l.throwing(getClass().getName(), "addQuickPayDiscount", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addQuickPayDiscount", new Object[]{recordSet});
        }
        return recordSet;
    }

    /**
     * To save all quick pay information.
     *
     * @param inputRecord a set of quick pay Record for saving.
     * @return the number of rows saved.
     */
    public RecordSet saveQuickPay(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveQuickPay", new Object[]{inputRecord});
        }

        RecordSet rs;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("pm_manual_quick_pay");
        try {
            rs = spDao.execute(inputRecord);
            Record record = rs.getSummaryRecord();
            if (!"0".equals(record.getStringValue("retcode"))) {
                throw new AppException("pm.quickPayTransactionDetail.save.error",
                    "Failed to save the quick pay data.");
            }
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save quick pay.", e);
            l.throwing(getClass().getName(), "saveQuickPay", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveQuickPay", new Object[]{rs});
        }
        return rs;
    }

    /**
     * To complete the quick pay transaction.
     *
     * @param inputRecord a set of quick pay Record for saving.
     * @return recordset.
     */
    public RecordSet completeQuickPayTransaction(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "completeQuickPayTransaction", new Object[]{inputRecord});
        }

        RecordSet rs;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("pm_fm_qp_trans_complete");
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to complete quick pay transaction.", e);
            l.throwing(getClass().getName(), "completeQuickPayTransaction", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "completeQuickPayTransaction", new Object[]{rs});
        }
        return rs;
    }

    /**
     * To get last quick pay transaction log id
     * <p/>
     *
     * @param inputRecord input record.
     * @return last quick pay transaction log id
     */
    public String getLastQuickPayTransactionLogId(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getLastQuickPayTransactionLogId", new Object[]{inputRecord});
        }

        // get the return value
        String returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Manual_Quickpay.PM_Get_Last_QP_Trans_Fk");
        try {
            returnValue = spDao.execute(inputRecord).getSummaryRecord().getStringValue(spDao.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get last quick pay transaction log id. ", e);
            l.throwing(getClass().getName(), "getLastQuickPayTransactionLogId", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getLastQuickPayTransactionLogId", returnValue);
        }
        return returnValue;
    }

    /**
     * To check if quick pay discount can be given.
     *
     * @param inputRecord input record.
     * @return the count of Non Insured Premium.
     */
    public String isAddQuickPayAllowed(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "isAddQuickPayAllowed", new Object[]{inputRecord});

        String returnValue;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("origTransLogId", "transactionLogId"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Manual_Quickpay.PM_Check_QP_Short_Term", mapping);
        try {
            Record record = spDao.execute(inputRecord).getSummaryRecord();
            returnValue = record.getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to check if add quick pay allowed.", e);
            l.throwing(getClass().getName(), "isAddQuickPayAllowed", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "isAddQuickPayAllowed", String.valueOf(returnValue));
        return returnValue;
    }

    /**
     * To delete the WIP data
     * <p/>
     *
     * @param inputRecord input record.
     * @return RecordSet record set.
     */
    public RecordSet deleteQuickPayWip(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "deleteQuickPayWip", new Object[]{inputRecord});

        RecordSet rs;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Manual_Quickpay.PM_Delete_QP_WIP");
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to delete WIP data.", e);
            l.throwing(getClass().getName(), "deleteQuickPayWip", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "deleteQuickPayWip", new Object[]{rs});
        return rs;
    }

    /**
     * To load all Original Transaction data.
     * <p/>
     *
     * @param inputRecord input record with the passed request values.
     * @return a Record loaded with original transaction data.
     */
    public Record loadOriginalTransaction(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadOriginalTransaction", new Object[]{inputRecord});
        }

        Record record = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Quick_Pay.Sel_Original_transaction");

        try {
            RecordSet rs = spDao.execute(inputRecord);
            if (rs.getSize() > 0) {
                record = rs.getFirstRecord();
            }
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load original transaction data.", e);
            l.throwing(getClass().getName(), "loadOriginalTransaction", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadOriginalTransaction", new Object[]{record});
        }
        return record;
    }

    /**
     * To load all risks/coverages information for process quick pay.
     * <p/>
     *
     * @param inputRecord input record
     * @return quick pay risks/coverages recordset
     */
    public RecordSet loadAllRiskCoverageForOriginalTransaction(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRiskCoverageForOriginalTransaction", new Object[]{inputRecord});
        }

        RecordSet rs;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Quick_Pay.Sel_Risk_Coverage_From_Trans");
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load process quick pay risks/coverages data.", e);
            l.throwing(getClass().getName(), "loadAllRiskCoverageForOriginalTransaction", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRiskCoverageForOriginalTransaction", new Object[]{rs});
        }
        return rs;
    }

    /**
     * To check if the coverage payor is a hospital.
     *
     * @param inputRecord input record.
     * @return 'Y' or 'N'.
     */
    public String isHospitalCoveragePayor(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "isHospitalCoveragePayor", new Object[]{inputRecord});

        String returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Manual_Quickpay.PM_Check_QP_Covg_Payor");
        try {
            Record record = spDao.execute(inputRecord).getSummaryRecord();
            returnValue = record.getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to check if the coverage payor is a hospital.", e);
            l.throwing(getClass().getName(), "isHospitalCoveragePayor", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "isHospitalCoveragePayor", String.valueOf(returnValue));
        return returnValue;
    }

    /**
     * To load quick pay transaction summary data.
     * <p/>
     *
     * @param inputRecord input record
     * @return record contains quick pay transaction summary data.
     */
    public Record loadTransactionSummary(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadTransactionSummary", new Object[]{inputRecord});
        }

        Record record = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Quick_Pay.Sel_Transaction_Summary");

        try {
            RecordSet rs = spDao.execute(inputRecord);
            if (rs.getSize() > 0) {
                record = rs.getFirstRecord();
            }
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load quick pay transaction summary data.", e);
            l.throwing(getClass().getName(), "loadTransactionSummary", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadTransactionSummary", new Object[]{record});
        }

        return record;
    }

    /**
     * To get last wip quick pay transaction log id
     * <p/>
     *
     * @param inputRecord input record.
     * @return last wip quick pay transaction log id
     */
    public String getLastWipQuickPayTransactionLogId(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getLastWipQuickPayTransactionLogId", new Object[]{inputRecord});
        }

        // get the return value
        String returnValue;                                                           
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Quick_Pay.PM_Get_Last_Wip_QP_Trans_Fk");
        try {
            returnValue = spDao.execute(inputRecord).getSummaryRecord().getStringValue(spDao.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get last wip quick pay transaction log id. ", e);
            l.throwing(getClass().getName(), "getLastWipQuickPayTransactionLogId", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getLastWipQuickPayTransactionLogId", returnValue);
        }
        return returnValue;
    }

}

