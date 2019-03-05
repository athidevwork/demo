package dti.pm.transactionmgr.cancelprocessmgr.impl;

import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.DateUtils;
import dti.oasis.busobjs.YesNoFlag;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.transactionmgr.TransactionFields;
import dti.pm.transactionmgr.cancelprocessmgr.CancelProcessFields;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Date;

/**
 * This class extends the default record load processor to enforce entitlements for Multi Cancel web page. This class works in
 * conjunction with pageEntitlements.xml configuration.
 * <p/>
 * <p>Any field that requires entitlement enforcement is determined by an indicator field that gets appended to the list
 * of fields in a record. This record is published as xml data island, which then gets intercepted by pageEntitlements
 * oasis tag to auto-generate javascript that enforces the entitlements.</p>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 20, 2008
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/18/2011       syang       121201 - Add cancellation information for every record.
 * 06/21/2013       adeng       117011 - Modified postProcessRecordSet() to add "transactionComment2" for every record.
 * 07/15/2014       wdang       154953 - Modified postProcessRecordSet() to handle Multi Cancel Component during renewal WIP. 
 * ---------------------------------------------------
 */
public class MultiCancelEntitlementRecordLoadProcessor implements RecordLoadProcessor {


    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     * @return true if this Record should be added to the RecordSet;
     *         false if this Record should be excluded from the RecordSet.
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        return true;
    }

    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        Record outputRecord = recordSet.getSummaryRecord();
        //if is cancel COI
        if (getInputRecord().hasStringValue(CancelProcessFields.CANCELLATION_LEVEL)) {
            //set initial value to RISK
            CancelProcessFields.setCancellationLevel(outputRecord, CancelProcessFields.getCancellationLevel(getInputRecord()));
        }
        else if (getPolicyHeader().getScreenModeCode().isRenewWIP()) {
            // Set initial value to Component.
            CancelProcessFields.setCancellationLevel(outputRecord, CancelProcessManagerImpl.CANCEL_LEVEL_COMPONENT);
            // Pre-fill Cancellation Date as transaction effective date.  
            CancelProcessFields.setCancellationDate(outputRecord, 
                    getPolicyHeader().getLastTransactionInfo().getTransEffectiveFromDate());
        }
        else {
            //set initial value to RISK
            CancelProcessFields.setCancellationLevel(outputRecord, CancelProcessManagerImpl.CANCEL_LEVEL_RISK);
        }
        outputRecord.setFieldValue("accountingDate", DateUtils.formatDate(new Date()));
        outputRecord.setFieldValue("isProcessAvailable", YesNoFlag.Y);
        if(recordSet.getSize() == 0){
            outputRecord.setFieldValue("isProcessAvailable", YesNoFlag.N);
        }
        
        if (getPolicyHeader().getScreenModeCode().isRenewWIP()) {
            // Set following fields read-only
            outputRecord.setFieldValue("isCancellationLevelEnable", YesNoFlag.N);
            outputRecord.setFieldValue("isCancellationDateEnable", YesNoFlag.N);
            outputRecord.setFieldValue("isCancelDateEnable", YesNoFlag.N);
        }
        else {
            outputRecord.setFieldValue("isCancellationLevelEnable", YesNoFlag.Y);
            outputRecord.setFieldValue("isCancellationDateEnable", YesNoFlag.Y);
            outputRecord.setFieldValue("isCancelDateEnable", YesNoFlag.Y);
        }

        outputRecord.setFields(
            MultiCancelEntitlementRecordLoadProcessor.setInitialValueForMultiCancel(outputRecord), true);
        // Add cancellation information for every record.
        recordSet.setFieldValueOnAll(CancelProcessFields.CANCEL_DATE, "");
        recordSet.setFieldValueOnAll(CancelProcessFields.CANCEL_COMMENT, "");
        recordSet.setFieldValueOnAll(TransactionFields.TRANSACTION_COMMENT2, "");
        recordSet.setFieldValueOnAll(CancelProcessFields.CANCEL_REASON, "");
        recordSet.setFieldValueOnAll(CancelProcessFields.CANCEL_TYPE, "");
        recordSet.setFieldValueOnAll(CancelProcessFields.CANCEL_METHOD, "");
    }

    public static Record setInitialValueForMultiCancel(Record inputRecord) {
        Logger l = LogUtils.getLogger(MultiCancelEntitlementRecordLoadProcessor.class);
        if (l.isLoggable(Level.FINER)) {
            l.entering(MultiCancelEntitlementRecordLoadProcessor.class.getName(), "setInitialValueForMultiCancel", new Object[]{inputRecord});
        }
        Record outputRecord = new Record();
        outputRecord.setFieldValue("isCancelLevelAvailable", YesNoFlag.Y);
        outputRecord.setFieldValue("isCancelTypeAvailable", YesNoFlag.N);
        outputRecord.setFieldValue("isCancelReasonAvailable", YesNoFlag.N);
        outputRecord.setFieldValue("isCancelMethodAvailable", YesNoFlag.N);
        outputRecord.setFieldValue("isRiskTypeFilterAvailable", YesNoFlag.Y);
        outputRecord.setFieldValue("isCoverageFilterAvailable", YesNoFlag.N);
        outputRecord.setFieldValue("isComponentFilterAvailable", YesNoFlag.N);
        outputRecord.setFieldValue("isCoverageClassFilterAvailable", YesNoFlag.N);
        String cancelLevel = CancelProcessFields.getCancellationLevel(inputRecord);
        outputRecord.setFieldValue("isCoiHolderFilterAvailable", YesNoFlag.N);
        if (cancelLevel.equals(CancelProcessManagerImpl.CANCEL_LEVEL_RISK)
            || cancelLevel.equals(CancelProcessManagerImpl.CANCEL_LEVEL_COVERAGE)
            || cancelLevel.equals(CancelProcessManagerImpl.CANCEL_LEVEL_CLASS)) {
            outputRecord.setFieldValue("isCancelTypeAvailable", YesNoFlag.Y);
            outputRecord.setFieldValue("isCancelReasonAvailable", YesNoFlag.Y);
            outputRecord.setFieldValue("isCancelMethodAvailable", YesNoFlag.Y);
        }


        if (cancelLevel.equals(CancelProcessManagerImpl.CANCEL_LEVEL_COVERAGE)) {
            outputRecord.setFieldValue("isCoverageFilterAvailable", YesNoFlag.Y);
        }
        else if (cancelLevel.equals(CancelProcessManagerImpl.CANCEL_LEVEL_CLASS)) {
            outputRecord.setFieldValue("isCoverageFilterAvailable", YesNoFlag.Y);
            outputRecord.setFieldValue("isCoverageClassFilterAvailable", YesNoFlag.Y);
        }
        else if (cancelLevel.equals(CancelProcessManagerImpl.CANCEL_LEVEL_COMPONENT)) {
            outputRecord.setFieldValue("isCoverageFilterAvailable", YesNoFlag.Y);
            outputRecord.setFieldValue("isComponentFilterAvailable", YesNoFlag.Y);
        }

        if (cancelLevel.equals(CancelProcessManagerImpl.CANCEL_LEVEL_COI)) {
            outputRecord.setFieldValue("isRiskTypeFilterAvailable", YesNoFlag.N);
            outputRecord.setFieldValue("isCancelLevelAvailable", YesNoFlag.N);
            outputRecord.setFieldValue("isCoiHolderFilterAvailable", YesNoFlag.Y);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(MultiCancelEntitlementRecordLoadProcessor.class.getName(), "setInitialValueForMultiCancel", outputRecord);
        }
        return outputRecord;

    }

    public MultiCancelEntitlementRecordLoadProcessor(PolicyHeader policyHeader, Record inputRecord
    ) {        
        m_policyHeader = policyHeader;
        m_inputRecord = inputRecord;
    }
    
    private Record getInputRecord() {
        return m_inputRecord;
    }
    
    private PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    private Record m_inputRecord;
    private PolicyHeader m_policyHeader;
}
