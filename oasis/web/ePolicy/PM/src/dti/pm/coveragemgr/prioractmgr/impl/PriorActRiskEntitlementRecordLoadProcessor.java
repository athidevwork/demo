package dti.pm.coveragemgr.prioractmgr.impl;

import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.util.LogUtils;
import dti.oasis.util.DateUtils;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.util.SysParmProvider;
import dti.pm.busobjs.PMStatusCode;
import dti.pm.busobjs.PolicyCycleCode;
import dti.pm.coveragemgr.CoverageFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.coveragemgr.prioractmgr.PriorActManager;
import dti.pm.coveragemgr.prioractmgr.PriorActFields;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.busobjs.PolicyStatus;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.entitlementmgr.EntitlementFields;

import java.util.Iterator;
import java.util.logging.Logger;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 11, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 02/15/2011       dzhang      Issue 117095 - Modified logic for isAddAvailable & isSaveAvailable & isRecordSetReadOnly.
 * 06/10/2011       wqfu        103799 - Modified postProcessRecordSet for isCopyStatsAvailable.
 * 08/29/2013       adeng       146449 - Modified isRecordSetReadOnly() to follow right requirement.
 * 03/03/2014       adeng       148692 - 1) Modified isRecordSetReadOnly() to use coverage status to set fields/buttons
 *                                       to read only.
 *                                       2) Modified postProcessRecordSet() to hide the buttons when record set is read
 *                                       only.
 * 06/10/2014       adeng       154011 - Hide the closed official record.
 * 11/24/2014       kxiang      159323 - 1) Modified postProcessRecordSet() to change set 'isDelRiskAvailable' logical.
 *                                       2) Modified isRecordSetReadOnly() to change set read Only logical.

 * 08/26/2016       wdang       167534 - Disable page for Renewal quote if isEditableForRenewalQuote returns false.
 * 03/10/2017       eyin        180675 - Added isRiskAIGCopyStatAvailable to display Copy Stats button in Prior Acts
 *                                       iframe for UI tab style.
 * 07/31/2018       xnie        187493 - Modified isRecordSetReadOnly() to introduce a new system parameter
 *                                       PM_EDIT_PA_ALL_TERMS.
 * ---------------------------------------------------
 */
public class PriorActRiskEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {

    /**
     * Returns a synchronized static instance of Prior Act Risk Entitlement Record Load Processor that has the
     * implementation information.
     *
     * @param inputRecord, policyHeader PriorActManager instance that provides basic information about selected coverage
     * @return an instance of PriorActRiskEntitlementRecordLoadProcessor class
     */
    public synchronized static PriorActRiskEntitlementRecordLoadProcessor getInstance(
        Record inputRecord, PolicyHeader policyHeader, PriorActManager priorActManager) {
        Logger l = LogUtils.enterLog(PriorActRiskEntitlementRecordLoadProcessor.class,
            "getInstance", new Object[]{inputRecord});

        PriorActRiskEntitlementRecordLoadProcessor instance;
        instance = new PriorActRiskEntitlementRecordLoadProcessor();
        instance.setInputRecord(inputRecord);
        instance.setPolicyHeader(policyHeader);
        instance.setPriorActManager(priorActManager);

        l.exiting(PriorActRiskEntitlementRecordLoadProcessor.class.getName(), "getInstance", instance);
        return instance;
    }

    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {

        record.setFieldValue("isDelRiskAvailable", YesNoFlag.N);

        // check if riskEffectiveFromDate/riskEffectiveToDate/practiceStateCode/riskCountyCode/specialty are available or not
        if (record.hasStringValue(PriorActFields.EXPOSURE_BASIS_CODE) &&
            PriorActFields.getExposureBasisCode(record).equals(PriorActFields.ExposureCodeValues.PRIOR_CARR)) {
            record.setFieldValue("isPriorCarrEditable", YesNoFlag.Y);
            if (PMCommonFields.getRecordModeCode(record).equals(RecordMode.TEMP)) {
                record.setFieldValue("isPracticeStateEditable", YesNoFlag.Y);
            }
            else {
                record.setFieldValue("isPracticeStateEditable", YesNoFlag.N);
            }
        }
        else {
            record.setFieldValue("isPriorCarrEditable", YesNoFlag.N);
            record.setFieldValue("isPracticeStateEditable", YesNoFlag.N);
        }

        return true;
    }

    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        Record summeryRecord = recordSet.getSummaryRecord();
        summeryRecord.setFieldValue("isAddAvailable", YesNoFlag.Y);
        summeryRecord.setFieldValue("isSaveAvailable", YesNoFlag.Y);
        summeryRecord.setFieldValue("isCopyStatAvailable", YesNoFlag.N);
        summeryRecord.setFieldValue("isRiskAIGCopyStatAvailable", YesNoFlag.N);

        Date coverageEffectiveDate = DateUtils.parseDate(PriorActFields.getCoverageEffectiveDate(getInputRecord()));
        Date coverageRetroDate = DateUtils.parseDate(PriorActFields.getCoverageRetroDate(getInputRecord()));
        Date termEff = DateUtils.parseDate(getPolicyHeader().getTermEffectiveFromDate());
        Date termExp = DateUtils.parseDate(getPolicyHeader().getTermEffectiveToDate());
        ScreenModeCode screenMode = getPolicyHeader().getScreenModeCode();
        PolicyStatus policyStatus = getPolicyHeader().getPolicyStatus();

        //set Add availability
        if (coverageEffectiveDate.after(termExp) || coverageEffectiveDate.before(termEff)
            || coverageRetroDate.equals(termEff)) {
            summeryRecord.setFieldValue("isAddAvailable", YesNoFlag.N);
        }else if(screenMode.isViewPolicy()||screenMode.isViewEndquote()||screenMode.isResinstateWIP()){
            summeryRecord.setFieldValue("isAddAvailable", YesNoFlag.N);
        }else if((!(screenMode.isViewPolicy()||screenMode.isViewEndquote()||screenMode.isResinstateWIP()
            ||screenMode.isManualEntry()||screenMode.isCancelWIP()||screenMode.isOosWIP()))&&
            (!(policyStatus.isActive()||policyStatus.isPending()))){
            summeryRecord.setFieldValue("isAddAvailable", YesNoFlag.N);
        }

        //set save availability
        if(screenMode.isViewPolicy()||screenMode.isViewEndquote()||screenMode.isResinstateWIP()){
            summeryRecord.setFieldValue("isSaveAvailable", YesNoFlag.N);
        }else if((!(screenMode.isViewPolicy()||screenMode.isViewEndquote()||screenMode.isResinstateWIP()
            ||screenMode.isManualEntry()||screenMode.isCancelWIP()||screenMode.isOosWIP()))&&
            (!(policyStatus.isActive()||policyStatus.isPending()))){
            summeryRecord.setFieldValue("isSaveAvailable", YesNoFlag.N);
        }


        //set copy stat availability
        if (!(screenMode.isViewPolicy() || screenMode.isViewEndquote() || screenMode.isResinstateWIP()) &&
            (coverageEffectiveDate.before(termExp) && !coverageEffectiveDate.before(termEff)) &&
            (recordSet.getSize() == 0) && coverageRetroDate.before(termEff)) {

            String pmUIStyle = SysParmProvider.getInstance().getSysParm("PM_UI_STYLE", "T");
            if (pmUIStyle.equals("T")) {
                summeryRecord.setFieldValue("isCopyStatAvailable", YesNoFlag.N);
                summeryRecord.setFieldValue("isRiskAIGCopyStatAvailable", YesNoFlag.Y);
            }
            else {
                summeryRecord.setFieldValue("isCopyStatAvailable", YesNoFlag.Y);
                summeryRecord.setFieldValue("isRiskAIGCopyStatAvailable", YesNoFlag.N);
            }

        }

        if (recordSet.getSize() > 0) {
            if (!(screenMode.isViewPolicy() || screenMode.isViewEndquote() || screenMode.isCancelWIP()
                || screenMode.isResinstateWIP()) && (policyStatus.isActive() || policyStatus.isPending())) {
                int covgCount = getPriorActManager().getPriorActCoverageCount(getPolicyHeader(), getInputRecord());
                Date minRetro = DateUtils.parseDate(getPriorActManager().getMinRetroDate(getPolicyHeader(), getInputRecord()));

                if (covgCount == 0 || !minRetro.before(coverageRetroDate)) {
                    recordSet.getSubSet(new RecordFilter(PMCommonFields.RECORD_MODE_CODE,RecordMode.TEMP))
                        .setFieldValueOnAll("isDelRiskAvailable", YesNoFlag.Y);
                }
            }

        }
        else if (recordSet.getSize() == 0) {
            List fieldNames = new ArrayList();

            fieldNames.add("isDelRiskAvailable");

            recordSet.addFieldNameCollection(fieldNames);
        }

        boolean isRecordSetReadOnly = isRecordSetReadOnly();
        if(isRecordSetReadOnly) {
            summeryRecord.setFieldValue("isAddAvailable", YesNoFlag.N);
            summeryRecord.setFieldValue("isCopyStatAvailable", YesNoFlag.N);
            summeryRecord.setFieldValue("isSaveAvailable", YesNoFlag.N);
            summeryRecord.setFieldValue("isRiskAIGCopyStatAvailable", YesNoFlag.N);
        }

        // Set readOnly attribute to summary record
        EntitlementFields.setReadOnly(recordSet.getSummaryRecord(), isRecordSetReadOnly);

        // Hide the closed official record.
        RecordSet offRecords = recordSet.getSubSet(new RecordFilter(PriorActFields.RECORD_MODE_CODE, PriorActFields.OFFICIAL));
        Iterator offIt = offRecords.getRecords();
        String transactionLogId = getPolicyHeader().getLastTransactionInfo().getTransactionLogId();
        while (offIt.hasNext()) {
            Record offRecord = (Record) offIt.next();
            if (offRecord.hasStringValue(PriorActFields.CLOSING_TRANS_LOG_ID) && transactionLogId.equals(PriorActFields.getClosingTransLogId(offRecord))) {
                offRecord.setDisplayIndicator("N");
            }
        }
    }

    /**
     * Check if current recordSet is readOnly
     *
     * @return boolean
     */
    private boolean isRecordSetReadOnly() {
        boolean isReadOnly = false;
        Date coverageEffectiveDate = DateUtils.parseDate(PriorActFields.getCoverageEffectiveDate(getInputRecord()));
        Date termEff = DateUtils.parseDate(getPolicyHeader().getTermEffectiveFromDate());
        Date termExp = DateUtils.parseDate(getPolicyHeader().getTermEffectiveToDate());
        ScreenModeCode screenMode = getPolicyHeader().getScreenModeCode();
        //get owner coverage's status
        PMStatusCode coverageStatus = PMStatusCode.getInstance(getPolicyHeader().getCoverageHeader().getCurrentCoverageStatus());
        String pmEditPaAllTerms = SysParmProvider.getInstance().getSysParm("PM_EDIT_PA_ALL_TERMS", "N");
        // All available fields are readonly in following scenarios:
        // 1. Owner coverage is not in current term and system parameter PM_EDIT_PA_ALL_TERMS is set to 'N'.
        // 2. Screen mode is "VIEW_POLICY", "VIEW_ENDQUOTE", or "REINSTATEWIP"
        // 3. Screen mode is NOT "VIEW_POLICY", "VIEW_ENDQUOTE", or
        // "REINSTATEWIP", "MANUAL_ENTRY", "CANCELWIP" or "OOSWIP", and coverage status is NOT "ACTIVE" or "PENDING".
        // 4. Check if the prior acts page is readonly for RN quote.
        if ((coverageEffectiveDate.after(termExp) || coverageEffectiveDate.before(termEff)) && (pmEditPaAllTerms.equals("N"))) {
            isReadOnly = true;
        }
        else if(screenMode.isViewPolicy()||screenMode.isViewEndquote()||screenMode.isResinstateWIP()
            ||screenMode.isCancelWIP()) {
            isReadOnly = true;
        }
        else if ((!(screenMode.isViewPolicy()||screenMode.isViewEndquote()||screenMode.isResinstateWIP()
            ||screenMode.isManualEntry()||screenMode.isCancelWIP()||screenMode.isOosWIP()))&&
            (!(coverageStatus.isActive()||coverageStatus.isPending()))) {
            isReadOnly = true;
        }
        else if (getPolicyHeader().getPolicyCycleCode().isQuote()
            && getPolicyHeader().getQuoteCycleCode().isRNQuote()
            && !getPriorActManager().isEditableForRenewalQuote(getPolicyHeader())) {
            isReadOnly = true;
        }

        return isReadOnly;
    }

    public Record getInputRecord() {
        return inputRecord;
    }

    public void setInputRecord(Record inputRecord) {
        this.inputRecord = inputRecord;
    }

    public PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    public void setPolicyHeader(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }


    public PriorActManager getPriorActManager() {
        return m_priorActManager;
    }

    public void setPriorActManager(PriorActManager priorActManager) {
        m_priorActManager = priorActManager;
    }
    private Record inputRecord;
    private PolicyHeader m_policyHeader;
    private PriorActManager m_priorActManager;
}
