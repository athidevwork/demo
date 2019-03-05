package dti.pm.policymgr.regionalmgr;

import dti.oasis.recordset.Record;

/**
 * Process Rm component fields.
 * <p>(C) 2008 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 19, 2008
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class RmComponentFields {

    public static final String PM_RM_PROCESS_MSTR_ID = "pmRmProcessMstrId";
    public static final String ACCOUNTING_DATE = "accountingDate";
    public static final String PROCESS_STATUS = "processStatus";
    public static final String TRANSACTION_EFFECTIVE_DATE = "transactionEffectiveDate";
    public static final String EFFECTIVE_FROM_DATE = "effectiveFromDate";
    public static final String EFFECTIVE_TO_DATE = "effectiveToDate";

    public static String getPmRmProcessMstrId(Record record) {
        return record.getStringValue(PM_RM_PROCESS_MSTR_ID);
    }

    public static void setPmRmProcessMstrId(Record record, String pmRmProcessMstrId) {
        record.setFieldValue(PM_RM_PROCESS_MSTR_ID, pmRmProcessMstrId);
    }

    public static String getAccountingDate(Record record) {
        return record.getStringValue(ACCOUNTING_DATE);
    }

    public static void setAccountingDate(Record record, String accountingDate) {
        record.setFieldValue(ACCOUNTING_DATE, accountingDate);
    }

    public static String getProcessStatus(Record record) {
        return record.getStringValue(PROCESS_STATUS);
    }

    public static void setProcessStatus(Record record, String processStatus) {
        record.setFieldValue(PROCESS_STATUS, processStatus);
    }

    public static String getTransactionEffectiveDate(Record record) {
        return record.getStringValue(TRANSACTION_EFFECTIVE_DATE);
    }

    public static void setTransactionEffectiveDate(Record record, String transactionEffectiveDate) {
        record.setFieldValue(TRANSACTION_EFFECTIVE_DATE, transactionEffectiveDate);
    }

    public static String getEffectiveFromDate(Record record) {
        return record.getStringValue(EFFECTIVE_FROM_DATE);
    }

    public static void setEffectiveFromDate(Record record, String effectiveFromDate) {
        record.setFieldValue(EFFECTIVE_FROM_DATE, effectiveFromDate);
    }

    public static String getEffectiveToDate(Record record) {
        return record.getStringValue(EFFECTIVE_TO_DATE);
    }

    public static void setEffectiveToDate(Record record, String effectiveToDate) {
        record.setFieldValue(EFFECTIVE_TO_DATE, effectiveToDate);
    }

    public class ProcessStatusValues {
        public static final String INPROGRESS = "INPROGRESS";
    }
}