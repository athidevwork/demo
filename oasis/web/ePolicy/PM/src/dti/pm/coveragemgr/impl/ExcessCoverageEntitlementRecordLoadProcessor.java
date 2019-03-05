package dti.pm.coveragemgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.SysParmProvider;
import dti.pm.busobjs.PolicyStatus;
import dti.pm.coveragemgr.ExcessCoverageFields;
import dti.pm.entitlementmgr.EntitlementFields;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

/**
 * This class extends the default record load processor to enforce entitlements for excess coverage web page. This class works in
 * conjunction with pageEntitlements.xml configuration.
 * <p/>
 * <p>Any field that requires entitlement enforcement is determined by an indicator field that gets appended to the list
 * of fields in a record. This record is published as xml data island, which then gets intercepted by pageEntitlements
 * oasis tag to auto-generate javascript that enforces the entitlements.</p>
 * <p/>
 * <p>(C) 2009 Delphi Technology, inc. (dti)</p>
 * Date:   April 03, 2009
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
public class ExcessCoverageEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {

    /**
     * Process the given record after it's been loaded to enforce page entitlements.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecord", new Object[]{record, String.valueOf(rowIsOnCurrentPage)});
        // The Delete button should be disabled whatever the policy status is CANCEL or not.
        record.setFieldValue("isDeleteAvailable", "N");

        l.exiting(getClass().getName(), "postProcessRecord");
        return true;
    }

    /**
     * Process the RecordSet for enforcing page entitlements after all records have been loaded and processed.
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        // If no record is fetched, add the page entitlement fields to the recordset field collection
        if (recordSet.getSize() == 0) {
            ArrayList pageEntitlementFields = new ArrayList();
            pageEntitlementFields.add("isAddAvailable");
            pageEntitlementFields.add("isDeleteAvailable");
            pageEntitlementFields.add("isSaveAvailable");
            recordSet.addFieldNameCollection(pageEntitlementFields);
        }
        // If the policy status is CANCEL, Add/Delete/Save options are disabled.
        if (getPolicyStatus().isCancelled()) {
            recordSet.getSummaryRecord().setFieldValue("isAddAvailable", "N");
            recordSet.getSummaryRecord().setFieldValue("isDeleteAvailable", "N");
            recordSet.getSummaryRecord().setFieldValue("isSaveAvailable", "N");
        }
        else {
            recordSet.getSummaryRecord().setFieldValue("isAddAvailable", "Y");
            recordSet.getSummaryRecord().setFieldValue("isDeleteAvailable", "N");
            recordSet.getSummaryRecord().setFieldValue("isSaveAvailable", "Y");
        }
        if (isRecordSetReadOnly()) {
            EntitlementFields.setReadOnly(recordSet.getSummaryRecord(), true);
        }
    }

    /**
     * Check if current recordSet is readOnly
     *
     * @return
     */
    private boolean isRecordSetReadOnly() {
        boolean isReadOnly = false;
        if (getPolicyStatus().isCancelled()) {
            isReadOnly = true;
        }
        return isReadOnly;
    }

    /**
     * Pass the inputRecord to RecordLoadProcessor.
     *
     * @param inputRecord
     */
    public ExcessCoverageEntitlementRecordLoadProcessor(Record inputRecord) {
        setInputRecord(inputRecord);
        PolicyStatus policyStatus = (PolicyStatus) getInputRecord().getFieldValue("PolicyStatus");
        setPolicyStatus(policyStatus);
    }

    public Record getInputRecord() {
        return m_inputRecord;
    }

    public void setInputRecord(Record inputRecord) {
        m_inputRecord = inputRecord;
    }

    public PolicyStatus getPolicyStatus() {
        return m_policyStatus;
    }

    public void setPolicyStatus(PolicyStatus policyStatus) {
        m_policyStatus = policyStatus;
    }

    private Record m_inputRecord;
    private PolicyStatus m_policyStatus;
    private static final String PM_PRCR_INT_DTYRMNS = "PM_PRCR_INT_DTYRMNS1";
}
