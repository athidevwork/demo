package dti.pm.transactionmgr.renewalprocessmgr.dao;

import dti.oasis.app.AppException;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.oasis.busobjs.YesNoFlag;
import dti.pm.core.dao.BaseDAO;
import dti.pm.transactionmgr.renewalprocessmgr.RenewalProcessFields;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the RenewalProcessDAO interface. This is consumed by any business logic objects
 * that handles renewal operation.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 16, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/15/2016       tzeng       177134 - Modified performAutoRenewal() return value to record.
 * ---------------------------------------------------
 */

public class RenewalProcessJdbcDAO extends BaseDAO implements RenewalProcessDAO {

    /**
     * save renewal information.
     *
     * @param inputRecord intput record
     * @return the return record of execute result
     */
    public Record renewPolicy(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "renewPolicy");

        Record outputRecord;
        //set fields' value
        inputRecord.setFieldValue("transCode", "MANRENEW");
        inputRecord.setFieldValue("batchModeYN", "Y");
        inputRecord.setFieldValue("validateB", "Y");
        inputRecord.setFieldValue("saveOfficialB", "N");
        if (inputRecord.hasStringValue("renewalTermExpDate")) {
            String parms = new StringBuffer("TERM_EXP_DATE^").append(
                RenewalProcessFields.getRenewalTermExpDate(inputRecord)).append("^").toString();
            inputRecord.setFieldValue("parms", parms);
        }

        /* Create DataRecordMappig for this stored procedure */
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("acctDt", "newAccountingDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("comments", "newTransactionComment"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effDate", "termEffectiveToDate"));
        // call Pm_Process_Transaction.Process_Renewal procedure to renew policy
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Process_Transaction.Process_Renewal", mapping);
        try {
            outputRecord = spDao.execute(inputRecord).getSummaryRecord();          
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to renew policy.", e);
            l.throwing(getClass().getName(), "renewPolicy", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "renewPolicy", outputRecord);
        }
        return outputRecord;
    }

    /**
     * load Pending Renewal Transaction
     *
     * @param inputRecord
     * @return the return record of PRT
     */
    public Record getPendingRenewalTransaction(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "getPendingRenewalTransaction");

        Record outputRecord;
        /* Create DataRecordMappig for this stored procedure */
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("transDate", "termEffectiveToDate"));

        // call Pm_Pending_Renewal.get_trans procedure to get PRT of current policy
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Pending_Renewal.get_trans", mapping);
        try {
            outputRecord = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to load Pending Renewal Transaction.", e);
            l.throwing(getClass().getName(), "getPendingRenewalTransaction", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPendingRenewalTransaction", outputRecord);
        }
        return outputRecord;
    }

    /**
     * load policy type configured parameter
     *
     * @param inputRecord
     * @return policy type configured parameter
     */
    public YesNoFlag isPolicyTypeConfigured(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "getPolicyTypeConfigured");

        Record outputRecord;
        /* Create DataRecordMappig for this stored procedure */
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("polType", "policyTypeCode"));

        // call Pm_Pending_Renewal.get_trans procedure to get PRT of current policy
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Get_Pms_Parameter", mapping);
        try {
            outputRecord = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to load Policy Type Configured.", e);
            l.throwing(getClass().getName(), "getPolicyTypeConfigured", ae);
            throw ae;
        }

        String result = outputRecord.getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPolicyTypeConfigured", result);
        }
        return YesNoFlag.getInstance(result);
    }

    /**
     * Validate auto renewal
     *
     * @param inputRecord
     * @return Record
     */
    public Record validateAutoRenewal(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAutoRenewal", new Object[]{inputRecord,});
        }

        Record outputRecord;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("expDate", "termEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("processLocCode", "regionalOffice"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Renewal.Val_Auto_Renewal", mapping);
        try {
            outputRecord = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to validate auto renewal.", e);
            l.throwing(getClass().getName(), "validateAutoRenewal", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateAutoRenewal", outputRecord);
        }
        return outputRecord;
    }

    /**
     * Perform auto renewal
     *
     * @param inputRecord
     * @return Record
     */
    public Record performAutoRenewal(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performAutoRenewal", new Object[]{inputRecord,});
        }

        Record outputRecord;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("effDate", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("expDate", "termEffectiveToDate"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_POLICY_PROCESS_RENEW", mapping);
        try {
            outputRecord = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to perform auto renewal.", e);
            l.throwing(getClass().getName(), "performAutoRenewal", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performAutoRenewal");
        }

        return outputRecord;
    }
}
