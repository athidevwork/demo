package dti.pm.transactionmgr.batchrenewalprocessmgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.transactionmgr.batchrenewalprocessmgr.BatchRenewalFields;

import java.util.logging.Logger;
import java.util.ArrayList;

/**
 * This class extends the default record load processor to enforce entitlements for Batch Renewal web page. This class works in
 * conjunction with pageEntitlements.xml configuration.
 * <p/>
 * <p>Any field that requires entitlement enforcement is determined by an indicator field that gets appended to the list
 * of fields in a record. This record is published as xml data island, which then gets intercepted by pageEntitlements
 * oasis tag to auto-generate javascript that enforces the entitlements.</p>
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 5, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/04/2007       fcb         postProcessRecordSet: logic added for 0 size recordSet.
 * 09/29/2011       dzhang      123437 - Added page entitlement logic for Delete WIP and ReRate option.
 * 11/01/2011       wfu         125309 - Modified page entitlement logic for Issue and Batch Print.
 * 03/12/2013       adeng       138243 - Added a reminder by comments.
 * 04/08/2014       awu         156019 - Added page entitlement for Release button.
 * ---------------------------------------------------
 */
public class BatchRenewalEntitlementRecordLoadProcessor implements RecordLoadProcessor {
    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     * @return true if this Record should be added to the RecordSet;
     *         false if this Record should be excluded from the RecordSet.
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecord",
            new Object[]{record, String.valueOf(rowIsOnCurrentPage)});

        //if any changes below logic will be done for these buttons availability in the future
        if (BatchRenewalFields.getProcessCode(record).equals(BatchRenewalFields.ProcessCodeValues.PRERENEWAL) &&
                BatchRenewalFields.getStatus(record).equals(BatchRenewalFields.StatusCodeValues.COMPLETE)) {
            record.setFieldValue(IS_ISSUE_AVAILABLE, YesNoFlag.Y);
            record.setFieldValue(IS_DELETE_WIP_AVAILABLE, YesNoFlag.Y);
            record.setFieldValue(IS_RERATE_AVAILABLE, YesNoFlag.Y);
            record.setFieldValue(IS_RELEASE_AVAILABLE, YesNoFlag.Y);
        }
        else {
            record.setFieldValue(IS_ISSUE_AVAILABLE, YesNoFlag.N);
            record.setFieldValue(IS_DELETE_WIP_AVAILABLE, YesNoFlag.N);
            record.setFieldValue(IS_RERATE_AVAILABLE, YesNoFlag.N);
            record.setFieldValue(IS_RELEASE_AVAILABLE, YesNoFlag.N);
        }

        if (!BatchRenewalFields.getProcessCode(record).equals(BatchRenewalFields.ProcessCodeValues.PRINT) &&
            !BatchRenewalFields.getProcessCode(record).equals(BatchRenewalFields.ProcessCodeValues.RELHOLDEOD) &&
                BatchRenewalFields.getStatus(record).equals(BatchRenewalFields.StatusCodeValues.COMPLETE)) {
            record.setFieldValue(IS_PRINT_AVAILABLE, YesNoFlag.Y);
        }
        else {
            record.setFieldValue(IS_PRINT_AVAILABLE, YesNoFlag.N);
        }

        // format "practiceState", "issueState", "agent", "underwriter" and "policyTypeDesc" fields
        if (record.hasStringValue(BatchRenewalFields.ISSUE_STATE_LIST_DESC)) {
            BatchRenewalFields.setIssueStateListDesc(
                record, BatchRenewalFields.getIssueStateListDesc(record).replace('\r', ','));
        }
        if (record.hasStringValue(BatchRenewalFields.POLICY_TYPE_CODE_DESC)) {
            BatchRenewalFields.setPolicyTypeCodeDesc(
                record, BatchRenewalFields.getPolicyTypeCodeDesc(record).replace('\r', ','));
        }
        if (record.hasStringValue(BatchRenewalFields.PRACTICE_STATE_LIST_DESC)) {
            BatchRenewalFields.setPracticeStateListDesc(
                record, BatchRenewalFields.getPracticeStateListDesc(record).replace('\r', ','));
        }
        if (record.hasStringValue(BatchRenewalFields.AGENT_LIST_DESC)) {
            BatchRenewalFields.setAgentListDesc(
                record, BatchRenewalFields.getAgentListDesc(record).replace('\r', ','));
        }
        if (record.hasStringValue(BatchRenewalFields.UNDERWRITER_LIST_DESC)) {
            BatchRenewalFields.setUnderwriterListDesc(
                record, BatchRenewalFields.getUnderwriterListDesc(record).replace('\r', ','));
        }

        l.exiting(getClass().getName(), "postProcessRecord");
        return true;
    }

    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        if (recordSet.getSize() == 0) {
            ArrayList pageEntitlementFields = new ArrayList();
            pageEntitlementFields.add(IS_ISSUE_AVAILABLE);
            pageEntitlementFields.add(IS_PRINT_AVAILABLE);
            pageEntitlementFields.add(IS_DELETE_WIP_AVAILABLE);
            pageEntitlementFields.add(IS_RERATE_AVAILABLE);
            recordSet.addFieldNameCollection(pageEntitlementFields);
        }
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
    }

    public BatchRenewalEntitlementRecordLoadProcessor() {
    }

    public static final String IS_DELETE_WIP_AVAILABLE = "isDeleteWipAvailable";
    public static final String IS_RERATE_AVAILABLE = "isReRateAvailable";
    public static final String IS_ISSUE_AVAILABLE = "isIssueAvailable";
    public static final String IS_PRINT_AVAILABLE = "isPrintAvailable";
    public static final String IS_RELEASE_AVAILABLE = "isReleaseAvailable";
}
