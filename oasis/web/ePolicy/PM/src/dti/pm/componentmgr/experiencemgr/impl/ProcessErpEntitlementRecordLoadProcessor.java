package dti.pm.componentmgr.experiencemgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.busobjs.PMStatusCode;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.componentmgr.experiencemgr.ExperienceComponentFields;
import dti.pm.policymgr.PolicyHeader;

import java.util.logging.Logger;

/**
 * This class extends the default record load processor to enforce entitlements for the process ERP page.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 3, 2011
 *
 * @author ryzhao
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/11/11         ryzhao      120601 - Process ERP option will not be shown if it is not update mode.
 *                                       Added one more condition same as what Save option will be done.
 * 06/08/2016       cesar       - changed Integer.parseInt to Long.parseLong in setInitialPageEntitlementFieldValues()
 * ---------------------------------------------------
 */

public class ProcessErpEntitlementRecordLoadProcessor implements RecordLoadProcessor {

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

        ExperienceComponentFields.setIsUpdateMode(record, isUpdateMode(record));

        l.exiting(getClass().getName(), "postProcessRecord");
        return true;
    }

    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        setInitialPageEntitlementFieldValues();
    }

    /**
     * Set entitlement values for Process ERP page
     *
     * @param record
     */
    public static void setEntitlementValuesForErp(PolicyHeader policyHeader, Record record) {
        ProcessErpEntitlementRecordLoadProcessor lp = new ProcessErpEntitlementRecordLoadProcessor(policyHeader, record);
        lp.setInitialPageEntitlementFieldValues();
    }

    private boolean setInitialPageEntitlementFieldValues() {
        Logger l = LogUtils.enterLog(getClass(), "setInitialPageEntitlementFieldValues", new Object[]{});

        Record inputRecord = getInputRecord();
        // If this page is accessed from Main Menu.
        if (!inputRecord.hasStringValue(ExperienceComponentFields.POLICY_ID)) {
            ExperienceComponentFields.setIsDeleteBatchAvailable(inputRecord, YesNoFlag.Y);
            ExperienceComponentFields.setIsProcessErpAvailable(inputRecord, YesNoFlag.Y);
            ExperienceComponentFields.setIsCloseAvailable(inputRecord, YesNoFlag.N);
            ExperienceComponentFields.setIsSaveAvailable(inputRecord, YesNoFlag.Y);
        }
        else {
            ExperienceComponentFields.setIsDeleteBatchAvailable(inputRecord, YesNoFlag.N);
            ExperienceComponentFields.setIsProcessErpAvailable(inputRecord, YesNoFlag.N);
            ExperienceComponentFields.setIsCloseAvailable(inputRecord, YesNoFlag.Y);
            ExperienceComponentFields.setIsSaveAvailable(inputRecord, isSaveAvailable(inputRecord));
            // Process ERP Option ' If accessed from Policy/Risk page, the option is available only if the transaction ID passed as input to page > 0.
            if (inputRecord.hasStringValue(ExperienceComponentFields.TRANS_LOG_ID)
                && Long.parseLong(ExperienceComponentFields.getTransLogId(inputRecord)) > 0
                && isSaveAvailable(inputRecord).booleanValue()) {
                ExperienceComponentFields.setIsProcessErpAvailable(inputRecord, YesNoFlag.Y);
            }
        }

        l.exiting(getClass().getName(), "setInitialPageEntitlementFieldValues");
        return true;
    }

    /**
     * Check if Save option is available if it is accessed from policy/risk page.
     * Only when it is in Update Mode, Save option will be available
     *
     * @param record the current record
     * @return YesNoFlag indicate if Save option is available
     */
    private YesNoFlag isSaveAvailable(Record record) {
        YesNoFlag isAvailable = YesNoFlag.Y;

        // Access from policy/risk page
        if (record.hasStringValue(ExperienceComponentFields.POLICY_ID)) {
            // If the current screen mode is VIEW_POLICY, VIEW_ENDQUOTE, REINSTATEWIP, or CANCELWIP
            // and if the risk is in cancelled status,  the ERP information is considered to be not editable/updatable.
            ScreenModeCode screenMode = getPolicyHeader().getScreenModeCode();
            if (screenMode.isViewPolicy() || screenMode.isViewEndquote() ||
                screenMode.isResinstateWIP() || screenMode.isCancelWIP()) {
                isAvailable = YesNoFlag.N;
            }
            else {
                if (record.hasStringValue(ExperienceComponentFields.RISK_ID)) {
                    PolicyHeader policyHeader = getPolicyHeader();
                    if (policyHeader.hasRiskHeader()) {
                        PMStatusCode riskStatus = policyHeader.getRiskHeader().getRiskStatusCode();
                        if (riskStatus.isCancelled()) {
                            isAvailable = YesNoFlag.N;
                        }
                    }
                }
            }
        }

        return isAvailable;
    }

    /**
     * Check if current ERP record is editable.
     * Only when the page is in update mode and the current ERP record has been processed, it will be editable.
     * One way to determine if ERP was processed is to look at data in erp_credit_b /erp_debit_b/process_date
     * and if they are all null, that means ERP has not been processed.
     *
     * @param record
     * @return YesNoFlag indicate if it is in update mode
     */
    private YesNoFlag isUpdateMode(Record record) {
        YesNoFlag isAvailable = YesNoFlag.Y;

        if (!ExperienceComponentFields.getIsSaveAvailable(getInputRecord()).booleanValue() || (
            !record.hasStringValue(ExperienceComponentFields.ELIGIBLE_CR_B) &&
            !record.hasStringValue(ExperienceComponentFields.ELIGIBLE_DB_B) &&
            !record.hasStringValue(ExperienceComponentFields.PROCESS_DATE))) {
            isAvailable = YesNoFlag.N;
        }

        return isAvailable;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
    }

    public PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    public void setPolicyHeader(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }

    public Record getInputRecord() {
        return m_inputRecord;
    }

    public void setInputRecord(Record inputRecord) {
        m_inputRecord = inputRecord;
    }

    public ProcessErpEntitlementRecordLoadProcessor(PolicyHeader policyHeader, Record inputRecord) {
        setPolicyHeader(policyHeader);
        setInputRecord(inputRecord);
    }

    private PolicyHeader m_policyHeader;
    private Record m_inputRecord;
}
