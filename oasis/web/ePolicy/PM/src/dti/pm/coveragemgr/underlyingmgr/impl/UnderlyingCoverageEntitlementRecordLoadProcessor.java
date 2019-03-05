package dti.pm.coveragemgr.underlyingmgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.busobjs.PolicyStatus;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.coveragemgr.underlyingmgr.UnderlyingCoverageFields;
import dti.pm.policymgr.PolicyHeader;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class extends the default record load processor to enforce entitlements for the underlying coverage web page.
 * This class works in conjunction with pageEntitlements.xml configuration.
 * <p/>
 * <p>Any field that requires entitlement enforcement is determined by an indicator field that gets appended to the list
 * of fields in a record. This record is published as xml data island, which then gets intercepted by pageEntitlements
 * oasis tag to auto-generate javascript that enforces the entitlements.</p>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 12, 2018
 *
 * @author wrong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *  08/24/2018       wrong      188391 - Initial version.
 *  11/15/2018       wrong      197079 - 1) Modified postProcessRecord() to add new conidtion of isTransDateNotInDatesPeriod.
 *                                       2) Modified enableUnderlyingCovgData/enableUnderlyingNICovgData to change
 *                                          field readonly/editable entitlement for CI and NI record.
 * ---------------------------------------------------
 */
public class UnderlyingCoverageEntitlementRecordLoadProcessor implements RecordLoadProcessor {
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

        disableAllUnderlyingCovgData(record);

        // data are all readonly if in VIEW_POLICY, VIEW_ENDQUOTE, CANCEL_WIP  or REINSTATE_WIP mode
        ScreenModeCode screenMode = getPolicyHeader().getScreenModeCode();
        RecordMode recordMode = PMCommonFields.getRecordModeCode(record);
        YesNoFlag companyInsuredB = UnderlyingCoverageFields.getCompanyInsuredB(record);
        String transEffFromDateStr = getPolicyHeader().getLastTransactionInfo().getTransEffectiveFromDate();
        String underlyingCoverageEffDate = UnderlyingCoverageFields.getEffectiveFromDate(record);
        String underlyingCoverageExpDate = UnderlyingCoverageFields.getEffectiveToDate(record);
        boolean isUpdateMode = true;
        if (screenMode.isViewPolicy() || screenMode.isViewEndquote() ||
            screenMode.isCancelWIP() || screenMode.isResinstateWIP()) {
            isUpdateMode = false;
            record.setEditIndicator(YesNoFlag.N);
            record.setFieldValue("isDeleteAvailable", YesNoFlag.N);
            record.setFieldValue("isCopyAvailable", YesNoFlag.N);
        }
        else if (screenMode.isManualEntry()) {
            if (recordMode.isTemp()) {
                record.setFieldValue("isDeleteAvailable", YesNoFlag.Y);
                record.setFieldValue("isCopyAvailable", YesNoFlag.Y);
            }
            else {
                record.setFieldValue("isDeleteAvailable", YesNoFlag.N);
                record.setFieldValue("isCopyAvailable", YesNoFlag.N);
            }
            record.setEditIndicator(YesNoFlag.Y);
        }

        if (isUpdateMode) {
            if (recordMode.isOfficial()) {
                record.setFieldValue("isDeleteAvailable", YesNoFlag.N);
                record.setFieldValue("isCopyAvailable", YesNoFlag.N);
            }
            else {
                record.setFieldValue("isDeleteAvailable", YesNoFlag.Y);
                record.setFieldValue("isCopyAvailable", YesNoFlag.Y);
            }
            Date transEffFromDate = DateUtils.parseDate(transEffFromDateStr);
            Date underlyingCovgEffDate = DateUtils.parseDate(underlyingCoverageEffDate);
            Date underlyingCovgExpDate = DateUtils.parseDate(underlyingCoverageExpDate);
            if (record.hasStringValue(UnderlyingCoverageFields.OFFICIAL_RECORD_ID) &&
                Long.parseLong(UnderlyingCoverageFields.getOfficialRecordId(record)) > 0 && recordMode.isTemp()) {
                if (record.hasStringValue(UnderlyingCoverageFields.AFTER_IMAGE_RECORD_B) &&
                    UnderlyingCoverageFields.getAfterImageRecordB(record).booleanValue()) {
                    if (!transDateNotInUnderlyingCovgDates(transEffFromDate, underlyingCovgEffDate, underlyingCovgExpDate)) {
                        if (companyInsuredB.booleanValue()) {
                            enableUnderlyingCovgData(record);
                        }
                        else {
                            enableUnderlyingNICovgData(record);
                        }
                    }
                } else {
                    disableAllUnderlyingCovgData(record);
                }
            }
            else {
                // officialRecordId <= 0 or is in OFFICIAL mode
                // new added record
                if ((screenMode.isManualEntry() || screenMode.isWIP() || screenMode.isRenewWIP() || screenMode.isOosWIP()) &&
                    !transEffFromDate.before(underlyingCovgEffDate) && !transEffFromDate.after(underlyingCovgExpDate)) {
                    record.setFieldValue("isCopyAvailable", YesNoFlag.Y);
                    if (!companyInsuredB.booleanValue()) {
                        if (recordMode.isTemp()) {
                            enableAllUnderlyingCovgData(record);
                        }
                        else {
                            enableUnderlyingNICovgData(record);
                        }
                    }
                    else {
                        if (recordMode.isTemp()) {
                            record.setEditIndicator(YesNoFlag.Y);
                            record.setFieldValue("isOutputBEditable", YesNoFlag.Y);
                            record.setFieldValue("isRenewBEditable", YesNoFlag.Y);
                            record.setFieldValue("isEffectiveToDateEditable", YesNoFlag.Y);
                        }
                        else {
                            enableUnderlyingCovgData(record);
                        }
                    }
                }
            }
        }

        // During OOSWIP or RENEWWIP, the buttons should be invisible if the current term is not the initial term.
        if (screenMode.isViewPolicy() ||
            (screenMode.isOosWIP() || screenMode.isRenewWIP()) && !getPolicyHeader().isInitTermB()) {
            record.setFieldValue("isDeleteAvailable", YesNoFlag.N);
            record.setFieldValue("isCopyAvailable", YesNoFlag.N);
        }

        if (isTransDateNotInDatesPeriod(transEffFromDateStr, underlyingCoverageEffDate, underlyingCoverageExpDate)) {
            record.setEditIndicator(YesNoFlag.N);
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
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecordSet", new Object[]{recordSet});

        // If no record is fetched, add the page entitlement fields to the recordset field collection
        if (recordSet.getSize() == 0) {
            ArrayList pageEntitlementFields = new ArrayList();
            pageEntitlementFields.add("isAddCompanyInsuredAvailable");
            pageEntitlementFields.add("isAddNonInsuredAvailable");
            pageEntitlementFields.add("isCopyAvailable");
            pageEntitlementFields.add("isDeleteAvailable");
            pageEntitlementFields.add("isUnderIssCompEditable");
            pageEntitlementFields.add("isUnderCoverageCodeEditable");
            pageEntitlementFields.add("isPracticeStateCodeEditable");
            pageEntitlementFields.add("isUnderPolicyNoEditable");
            pageEntitlementFields.add("isEffectiveFromDateEditable");
            pageEntitlementFields.add("isEffectiveToDateEditable");
            pageEntitlementFields.add("isPolicyFormCodeEditable");
            pageEntitlementFields.add("isUnderPolicyTypeCodeEditable");
            pageEntitlementFields.add("isUnderRiskTypeEditable");
            pageEntitlementFields.add("isCoverageLimitCodeEditable");
            pageEntitlementFields.add("isRetroDateEditable");
            recordSet.addFieldNameCollection(pageEntitlementFields);
        }

        ScreenModeCode screenMode = getPolicyHeader().getScreenModeCode();
        Record summaryRec = recordSet.getSummaryRecord();

        String transEffFromDateStr = getPolicyHeader().getLastTransactionInfo().getTransEffectiveFromDate();

        String contigCoverageEffectiveDateStr = getPolicyHeader().getCoverageHeader().getContigCoverageEffectiveDate();
        String contigCoverageExpireDateStr = getPolicyHeader().getCoverageHeader().getContigCoverageExpireDate();
        summaryRec.setFieldValue("isAddCompanyInsuredAvailable", YesNoFlag.Y);
        summaryRec.setFieldValue("isAddNonInsuredAvailable", YesNoFlag.Y);
        summaryRec.setFieldValue("isSaveAvailable", YesNoFlag.Y);

        // Filtering:
        // If a record with record mode code = 'TEMP' exists with a non-zero official record id,
        // if a record whose manuscript endorsement primary key equals the official record id is found,
        //  that record is not displayed.
        RecordSet tempRecords = recordSet.getSubSet(new RecordFilter("recordModeCode", "TEMP"));
        RecordSet offRecords = recordSet.getSubSet(new RecordFilter("recordModeCode", "OFFICIAL"));
        Iterator tempIt = tempRecords.getRecords();
        // Loop through records to set official record display_ind to "N", which hides rows in grid
        while (tempIt.hasNext()) {
            Record tempRec = (Record) tempIt.next();
            String sOfficialRecordId = tempRec.getStringValue("officialRecordId");
            Iterator offIt = offRecords.getRecords();
            while (offIt.hasNext()) {
                Record offRecord = (Record) offIt.next();
                String policyUnderlyingCovgId = offRecord.getStringValue("policyUnderlyingCovgId");
                if (policyUnderlyingCovgId.equals(sOfficialRecordId)) {
                    offRecord.setDisplayIndicator("N");
                }
            }
        }
        // Hide the expired official record.
        Iterator offIt = offRecords.getRecords();
        String transactionLogId = getPolicyHeader().getLastTransactionInfo().getTransactionLogId();
        while (offIt.hasNext()) {
            Record offRecord = (Record) offIt.next();
            if (offRecord.hasStringValue("closingTransLogId") && transactionLogId.equals(offRecord.getStringValue("closingTransLogId"))) {
                offRecord.setDisplayIndicator("N");
            }
        }

        if (screenMode.isViewPolicy()) {
            summaryRec.setFieldValue("isAddCompanyInsuredAvailable", YesNoFlag.N);
            summaryRec.setFieldValue("isAddNonInsuredAvailable", YesNoFlag.N);
            summaryRec.setFieldValue("isSaveAvailable", YesNoFlag.N);
        }
        else if (screenMode.isViewEndquote() || screenMode.isCancelWIP() || screenMode.isResinstateWIP()) {
            summaryRec.setFieldValue("isAddCompanyInsuredAvailable", YesNoFlag.N);
            summaryRec.setFieldValue("isAddNonInsuredAvailable", YesNoFlag.N);
            summaryRec.setFieldValue("isSaveAvailable", YesNoFlag.N);
        }
        else if (screenMode.isManualEntry()) {
            if (isTransDateNotInDatesPeriod(transEffFromDateStr, contigCoverageEffectiveDateStr, contigCoverageExpireDateStr)) {
                summaryRec.setFieldValue("isAddCompanyInsuredAvailable", YesNoFlag.N);
                summaryRec.setFieldValue("isAddNonInsuredAvailable", YesNoFlag.N);
                summaryRec.setFieldValue("isSaveAvailable", YesNoFlag.N);
            }
        }
        else {
            PolicyStatus policyStatus = getPolicyHeader().getPolicyStatus();
            if (policyStatus.isActive() || policyStatus.isPending()) {
                if (isTransDateNotInDatesPeriod(transEffFromDateStr, contigCoverageEffectiveDateStr, contigCoverageExpireDateStr)) {
                    summaryRec.setFieldValue("isAddCompanyInsuredAvailable", YesNoFlag.N);
                    summaryRec.setFieldValue("isAddNonInsuredAvailable", YesNoFlag.N);
                    summaryRec.setFieldValue("isSaveAvailable", YesNoFlag.N);
                }
            }
            else {
                // reset overall status to VIEW_POLICY
                getPolicyHeader().setScreenModeCode(ScreenModeCode.VIEW_POLICY);

                summaryRec.setFieldValue("isAddCompanyInsuredAvailable", YesNoFlag.N);
                summaryRec.setFieldValue("isAddNonInsuredAvailable", YesNoFlag.N);
                summaryRec.setFieldValue("isSaveAvailable", YesNoFlag.N);
            }
        }

        // During OOSWIP or RENEWWIP, the buttons should be invisible if the current term is not the initial term.
        if (screenMode.isViewPolicy() ||
            (screenMode.isOosWIP() || screenMode.isRenewWIP()) && !getPolicyHeader().isInitTermB()) {
            summaryRec.setFieldValue("isAddCompanyInsuredAvailable", YesNoFlag.N);
            summaryRec.setFieldValue("isAddNonInsuredAvailable", YesNoFlag.N);
            summaryRec.setFieldValue("isSaveAvailable", YesNoFlag.N);
        }

        l.exiting(getClass().getName(), "postProcessRecordSet");
    }

    private void disableAllUnderlyingCovgData(Record record) {
        record.setEditIndicator(YesNoFlag.N);
        record.setFieldValue("isUnderIssCompEditable", YesNoFlag.N);
        record.setFieldValue("isUnderPolicyNoEditable", YesNoFlag.N);
        record.setFieldValue("isUnderPolicyTypeCodeEditable", YesNoFlag.N);
        record.setFieldValue("isEffectiveFromDateEditable", YesNoFlag.N);
        record.setFieldValue("isEffectiveToDateEditable", YesNoFlag.N);
        record.setFieldValue("isPracticeStateCodeEditable", YesNoFlag.N);
        record.setFieldValue("isOutputBEditable", YesNoFlag.N);
        record.setFieldValue("isUnderCoverageCodeEditable", YesNoFlag.N);
        record.setFieldValue("isPolicyFormCodeEditable", YesNoFlag.N);
        record.setFieldValue("isRetroDateEditable", YesNoFlag.N);
        record.setFieldValue("isCoverageLimitCodeEditable", YesNoFlag.N);
        record.setFieldValue("isUnderRiskTypeEditable", YesNoFlag.N);
        record.setFieldValue("isRenewBEditable", YesNoFlag.N);
    }

    private void enableAllUnderlyingCovgData(Record record) {
        record.setEditIndicator(YesNoFlag.Y);
        record.setFieldValue("isUnderIssCompEditable", YesNoFlag.Y);
        record.setFieldValue("isUnderPolicyNoEditable", YesNoFlag.Y);
        record.setFieldValue("isUnderPolicyTypeCodeEditable", YesNoFlag.Y);
        record.setFieldValue("isEffectiveToDateEditable", YesNoFlag.Y);
        record.setFieldValue("isOutputBEditable", YesNoFlag.Y);
        record.setFieldValue("isPracticeStateCodeEditable", YesNoFlag.Y);
        record.setFieldValue("isUnderCoverageCodeEditable", YesNoFlag.Y);
        record.setFieldValue("isPolicyFormCodeEditable", YesNoFlag.Y);
        record.setFieldValue("isCoverageLimitCodeEditable", YesNoFlag.Y);
        record.setFieldValue("isUnderRiskTypeEditable", YesNoFlag.Y);
        record.setFieldValue("isRenewBEditable", YesNoFlag.Y);
        record.setFieldValue("isRetroDateEditable", isRetroActiveEditable(record));
    }

    private void enableUnderlyingCovgData(Record record) {
        record.setEditIndicator(YesNoFlag.Y);
        record.setFieldValue("isOutputBEditable", YesNoFlag.Y);
        record.setFieldValue("isRenewBEditable", YesNoFlag.Y);
        record.setFieldValue("isEffectiveToDateEditable", YesNoFlag.Y);
    }

    private void enableUnderlyingNICovgData(Record record) {
        record.setEditIndicator(YesNoFlag.Y);
        record.setFieldValue("isUnderIssCompEditable", YesNoFlag.Y);
        record.setFieldValue("isUnderPolicyNoEditable", YesNoFlag.Y);
        record.setFieldValue("isPracticeStateCodeEditable", YesNoFlag.Y);
        record.setFieldValue("isUnderPolicyTypeCodeEditable", YesNoFlag.Y);
        record.setFieldValue("isUnderCoverageCodeEditable", YesNoFlag.Y);
        record.setFieldValue("isPolicyFormCodeEditable", YesNoFlag.Y);
        record.setFieldValue("isOutputBEditable", YesNoFlag.Y);
        record.setFieldValue("isCoverageLimitCodeEditable", YesNoFlag.Y);
        record.setFieldValue("isUnderRiskTypeEditable", YesNoFlag.Y);
        record.setFieldValue("isRenewBEditable", YesNoFlag.Y);
        record.setFieldValue("isEffectiveToDateEditable", YesNoFlag.Y);
        record.setFieldValue("isRetroDateEditable", isRetroActiveEditable(record));
    }

    private boolean isRetroActiveEditable(Record record) {
        YesNoFlag isRetroDateEditable = YesNoFlag.N;
        // Check editable of the retroactive date
        if (record.hasStringValue(UnderlyingCoverageFields.POLICY_FORM_CODE) && UnderlyingCoverageFields.getPolicyFormCode(record).equals("CM")) {
            isRetroDateEditable = YesNoFlag.Y;
        }
        return isRetroDateEditable.booleanValue();
    }

    private boolean transDateNotInUnderlyingCovgDates(Date transEffFromDate,
                                                      Date underlyingCoverageEffDate,
                                                      Date underlyingCoverageExpDate) {
        if (transEffFromDate.before(underlyingCoverageEffDate) ||
            transEffFromDate.after(underlyingCoverageExpDate) || transEffFromDate.equals(underlyingCoverageExpDate)) {
            return true;
        }
        return false;
    }

    /**
     * Check whether the transaction date is located in date period
     *
     * @return YesNoFlag
     */
    public boolean isTransDateNotInDatesPeriod(String transEffFromDate, String effFromDate, String effToDate) {
        Logger l = LogUtils.getLogger(DateUtils.class);
        if (l.isLoggable(Level.FINER)) {
            l.entering(DateUtils.class.getName(), "isTransDateNotInDatesPeriod");
        }

        boolean retval = false;
        ScreenModeCode screenModeCode = getPolicyHeader().getScreenModeCode();
        if ((screenModeCode.isManualEntry() || screenModeCode.isWIP() || screenModeCode.isRenewWIP() || screenModeCode.isOosWIP())
            && DateUtils.isTargetDateNotInDatesPeriod(transEffFromDate, effFromDate, effToDate)){
            retval = true;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(DateUtils.class.getName(), "isTransDateNotInDatesPeriod", retval);
        }
        return retval;
    }

    /**
     * Set initialEntitlementValues for UnderLyingCoverage
     *
     * @param policyHeader
     * @param record
     */
    public static void setInitialEntitlementValuesForUnderlyingCoverage(PolicyHeader policyHeader,
                                                                        Record record) {
        UnderlyingCoverageEntitlementRecordLoadProcessor entitlementRLP = new UnderlyingCoverageEntitlementRecordLoadProcessor(policyHeader);
        //set page entitlement values
        entitlementRLP.postProcessRecord(record, true);
    }

    public UnderlyingCoverageEntitlementRecordLoadProcessor(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }

    public PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    public void setPolicyHeader(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }

    private PolicyHeader m_policyHeader;
}
