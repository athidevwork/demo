package dti.pm.riskmgr.addtlexposuremgr.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.busobjs.PMStatusCode;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.busobjs.SysParmIds;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.riskmgr.RiskFields;
import dti.pm.riskmgr.addtlexposuremgr.RiskAddtlExposureFields;
import dti.pm.riskmgr.addtlexposuremgr.RiskAddtlExposureManager;

/**
 * <p>(C) 2017 Delphi Technology, inc. (dti)</p>
 * Date:   May 23, 2017
 *
 * @author eyin
 */
/*
 *
 * Revision Date Revised By Description
 * ---------------------------------------------------
 * 09/21/2017    eyin       169483, Initial version.
 * 12/07/2017   lzhang      182769 - Invisible ADD and SAVE button when transaction date
 *                                   is not located in risk period
 *                                   and not editable field when transaction date
 *                                   is not located in addtl expo period
 * ---------------------------------------------------
 */
public class RiskAddtlExposureEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {

    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecord", new Object[]{record});

        // data are all readonly if in VIEW_POLICY, VIEW_ENDQUOTE, CANCEL_WIP  or REINSTATE_WIP mode
        ScreenModeCode screenMode = getPolicyHeader().getScreenModeCode();
        RecordMode recordMode = PMCommonFields.getRecordModeCode(record);
        record.setFieldValue("isDeleteAvailable", YesNoFlag.N);
        record.setFieldValue("isChgPracticeValueAvailable", YesNoFlag.N);
        record.setFieldValue("isChgPracticeDateAvailable", YesNoFlag.N);
        record.setFieldValue("isSelectAddressAvailable", YesNoFlag.N);

        disableRiskAddtlExposureData(record);

        if(record.hasStringValue(RiskAddtlExposureFields.RISK_ADDTL_EXP_BASE_RECORD_ID) &&
            Long.parseLong(RiskAddtlExposureFields.getRiskAddtlExpBaseRecordId(record)) > 0){
            record.setFieldValue("isPracticeStateCodeEditable", YesNoFlag.N);
            record.setFieldValue("isRiskCountyEditable", YesNoFlag.N);
            record.setFieldValue("isRiskClassEditable", YesNoFlag.N);
        }else{
            record.setFieldValue("isPracticeStateCodeEditable", YesNoFlag.Y);
            record.setFieldValue("isRiskCountyEditable", YesNoFlag.Y);
            record.setFieldValue("isRiskClassEditable", YesNoFlag.Y);
        }

        // for Risk Additional Exposure set Change option's available status
        boolean isChangeAvailable = isChangeAvailableForRiskAddtlExposure(record);
        record.setFieldValue("isChgPracticeValueAvailable", YesNoFlag.getInstance(isChangeAvailable));
        record.setFieldValue("isChgPracticeDateAvailable", YesNoFlag.getInstance(isChangeAvailable));

        boolean isUpdateMode = true;
        if (screenMode.isViewPolicy() || screenMode.isViewEndquote() ||
            screenMode.isCancelWIP() || screenMode.isResinstateWIP()) {
            isUpdateMode = false;
            record.setFieldValue("isDeleteAvailable", YesNoFlag.N);
        }
        else if (screenMode.isManualEntry()) {
            if (recordMode.isTemp()) {
                record.setFieldValue("isDeleteAvailable", YesNoFlag.Y);
                record.setFieldValue("isSelectAddressAvailable", YesNoFlag.Y);
                enableRiskAddtlExposureData(record);
                record.setFieldValue("isEffectiveToDateEditable", YesNoFlag.N);
            }
            else {
                record.setFieldValue("isDeleteAvailable", YesNoFlag.N);
            }
        }

        String transEffFromDateStr = getPolicyHeader().getLastTransactionInfo().getTransEffectiveFromDate();
        String addtlExpEffFromDateStr = RiskAddtlExposureFields.getEffectiveFromDate(record);
        String addtlExpEffToDateStr = RiskAddtlExposureFields.getEffectiveToDate(record);
        if (isUpdateMode) {
            // #1
            if (recordMode.isOfficial()) {
                record.setFieldValue("isDeleteAvailable", YesNoFlag.N);
            }
            else {
                record.setFieldValue("isDeleteAvailable", YesNoFlag.Y);
            }
            // #2
            Date transEffFromDate = DateUtils.parseDate(transEffFromDateStr);
            Date addtlExpEffFromDate = DateUtils.parseDate(addtlExpEffFromDateStr);
            Date addtlExpEffToDate = DateUtils.parseDate(addtlExpEffToDateStr);
            if (record.hasStringValue(RiskAddtlExposureFields.OFFICIAL_RECORD_ID) &&
                Long.parseLong(RiskAddtlExposureFields.getOfficialRecordId(record)) > 0 && recordMode.isTemp()) {
                if (addtlExpEffFromDateStr.equals(addtlExpEffToDateStr) && screenMode.isRenewWIP()) {
                    disableRiskAddtlExposureData(record);
                }else if (record.hasStringValue(RiskFields.AFTER_IMAGE_RECORD_B) &&
                    RiskFields.getAfterImageRecordB(record).booleanValue()) {
                    if (screenMode.isOosWIP() && !recordMode.isRequest()) {
                        disableRiskAddtlExposureData(record);
                    }else{
                        enableRiskAddtlExposureData(record, transEffFromDate, addtlExpEffFromDate, addtlExpEffToDate);
                    }
                }else if(record.hasStringValue(RiskFields.AFTER_IMAGE_RECORD_B) &&
                    !RiskFields.getAfterImageRecordB(record).booleanValue()){ // afterImageB is N
                    // disables the Additional Exposure data.
                    disableRiskAddtlExposureData(record);
                }
            }
            else { // officialRecordId <= 0 or is in OFFICIAL mode
                if ((screenMode.isManualEntry() || screenMode.isWIP() || screenMode.isRenewWIP()) && !transEffFromDate.before(addtlExpEffFromDate) &&
                    !transEffFromDate.after(addtlExpEffToDate)) {
                    enableRiskAddtlExposureData(record);
                }
                else {
                    // diables the Risk Additional Exposure Info data
                    disableRiskAddtlExposureData(record);
                }

                if (recordMode.isTemp()&& !transEffFromDate.before(addtlExpEffFromDate) && !transEffFromDate.after(addtlExpEffToDate)) { // new added record
                    record.setFieldValue("isDeleteAvailable", YesNoFlag.Y);
                    record.setFieldValue("isSelectAddressAvailable", YesNoFlag.Y);
                    enableRiskAddtlExposureData(record);
                    record.setFieldValue("isEffectiveToDateEditable", YesNoFlag.N);
                }
            }
        }
        else {
            record.setFieldValue("isDeleteAvailable", YesNoFlag.N);
        }

        // During OOSWIP or RENEWWIP, the buttons should be invisible if the current term is not the initial term.
        if ((screenMode.isOosWIP() || screenMode.isRenewWIP()) && !getPolicyHeader().isInitTermB()) {
            record.setFieldValue("isDeleteAvailable", YesNoFlag.N);
            disableRiskAddtlExposureData(record);
        }

        if(getInputRecord().hasStringValue("snapshotB") && "Y".equalsIgnoreCase(getInputRecord().getStringValue("snapshotB"))){
            record.setFieldValue("isDeleteAvailable", YesNoFlag.N);
            disableRiskAddtlExposureData(record);
        }

        //set premium effective date which will be used to get premium class codes
        String premClassEffDt = SysParmProvider.getInstance().getSysParm(SysParmIds.PM_PREM_CLASS_EFF_DT);
        if (StringUtils.isBlank(premClassEffDt) || premClassEffDt.equals("RISK_EFF_DT")) {
            record.setFieldValue("premClassEffectiveDate", RiskFields.getRiskEffectiveFromDate(record));
        }
        else if (premClassEffDt.equals("TERM_EFF_DT")) {
            record.setFieldValue("premClassEffectiveDate", getPolicyHeader().getTermEffectiveFromDate());
        }
        else if (premClassEffDt.equals("TRANS_EFF_DT")) {
            record.setFieldValue("premClassEffectiveDate",
                getPolicyHeader().getLastTransactionInfo().getTransEffectiveFromDate());
        }

        if (isTransDateNotInDatesPeriod(transEffFromDateStr, addtlExpEffFromDateStr, addtlExpEffToDateStr)) {
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
            pageEntitlementFields.add("isDeleteAvailable");
            pageEntitlementFields.add("isChgPracticeValueAvailable");
            pageEntitlementFields.add("isChgPracticeDateAvailable");
            pageEntitlementFields.add("isAddAvailable");
            pageEntitlementFields.add("isSaveAvailable");
            pageEntitlementFields.add("isSelectAddressAvailable");
            pageEntitlementFields.add("isEffectiveToDateEditable");
            pageEntitlementFields.add("isPracticeStateCodeEditable");
            pageEntitlementFields.add("isRiskCountyEditable");
            pageEntitlementFields.add("isRiskClassEditable");
            pageEntitlementFields.add("isPercentPracticeEditable");
            recordSet.addFieldNameCollection(pageEntitlementFields);
        }

        ScreenModeCode screenMode = getPolicyHeader().getScreenModeCode();
        Record summaryRec = recordSet.getSummaryRecord();

        // GDR 71.4 Option Availability A
        String transEffFromDateStr = getPolicyHeader().getLastTransactionInfo().getTransEffectiveFromDate();

        String contigRiskEffectiveDateStr = getPolicyHeader().getRiskHeader().getContigRiskEffectiveDate();
        String contigRiskExpireDateStr = getPolicyHeader().getRiskHeader().getContigRiskExpireDate();

        summaryRec.setFieldValue("isAddAvailable", YesNoFlag.Y);
        summaryRec.setFieldValue("isSaveAvailable", YesNoFlag.Y);

        if (screenMode.isViewPolicy()) {
            summaryRec.setFieldValue("isAddAvailable", YesNoFlag.N);
            summaryRec.setFieldValue("isSaveAvailable", YesNoFlag.N);
        }
        else if (screenMode.isViewEndquote() || screenMode.isCancelWIP() || screenMode.isResinstateWIP()) {
            summaryRec.setFieldValue("isAddAvailable", YesNoFlag.N);
            summaryRec.setFieldValue("isSaveAvailable", YesNoFlag.N);
        }
        else if (screenMode.isManualEntry()) {
            if (isTransDateNotInDatesPeriod(transEffFromDateStr, contigRiskEffectiveDateStr, contigRiskExpireDateStr)) {
                summaryRec.setFieldValue("isAddAvailable", YesNoFlag.N);
                summaryRec.setFieldValue("isSaveAvailable", YesNoFlag.N);
            }
        }
        else {
            PMStatusCode riskStatus = getPolicyHeader().getRiskHeader().getRiskStatusCode();
            if (riskStatus.isActive() || riskStatus.isPending()) {
                if (isTransDateNotInDatesPeriod(transEffFromDateStr, contigRiskEffectiveDateStr, contigRiskExpireDateStr)) {
                    summaryRec.setFieldValue("isAddAvailable", YesNoFlag.N);
                    summaryRec.setFieldValue("isSaveAvailable", YesNoFlag.N);
                }
            }
            else {
                // reset overall status to VIEW_POLICY
                getPolicyHeader().setScreenModeCode(ScreenModeCode.VIEW_POLICY);

                summaryRec.setFieldValue("isAddAvailable", YesNoFlag.N);
                summaryRec.setFieldValue("isSaveAvailable", YesNoFlag.N);
            }
        }

        // During OOSWIP or RENEWWIP, the buttons should be invisible if the current term is not the initial term.
        if (screenMode.isViewPolicy() ||
            (screenMode.isOosWIP() || screenMode.isRenewWIP()) && !getPolicyHeader().isInitTermB()) {
            summaryRec.setFieldValue("isAddAvailable", YesNoFlag.N);
            summaryRec.setFieldValue("isSaveAvailable", YesNoFlag.N);
        }

        l.exiting(getClass().getName(), "postProcessRecordSet");
    }

    /**
     * Check Change option status
     *
     * @param record
     * @return
     */
    private boolean isChangeAvailableForRiskAddtlExposure(Record record) {
        Logger l = LogUtils.enterLog(getClass(), "isChangeAvailableForRiskAddtlExposure", record);

        boolean isChangeAvailable = false;
        PolicyHeader policyHeader = getPolicyHeader();
        ScreenModeCode screenMode = policyHeader.getScreenModeCode();
        RecordMode recordMode = PMCommonFields.getRecordModeCode(record);

        if (screenMode.isOosWIP() && (recordMode.isOfficial() || (record.hasStringValue(RiskAddtlExposureFields.OFFICIAL_RECORD_ID) &&
            Long.parseLong(RiskAddtlExposureFields.getOfficialRecordId(record)) > 0)) && getPolicyHeader().isInitTermB()) {
            isChangeAvailable = true;
        }

        String transEffDateString = policyHeader.getLastTransactionInfo().getTransEffectiveFromDate();
        Date transEffDate = DateUtils.parseDate(transEffDateString);
        String effFromDateString = null;
        String effToDateString = null;
        if (record.hasStringValue(RiskAddtlExposureFields.EFFECTIVE_FROM_DATE)) {
            effFromDateString = RiskAddtlExposureFields.getEffectiveFromDate(record);
        }
        if (record.hasStringValue(RiskAddtlExposureFields.EFFECTIVE_TO_DATE)) {
            effToDateString = RiskAddtlExposureFields.getEffectiveToDate(record);
        }
        // If the transaction effective from date before the effective from date, Change option should be hidden.
        if (isChangeAvailable && !StringUtils.isBlank(effFromDateString)) {
            Date effFromDate = DateUtils.parseDate(effFromDateString);
            if (transEffDate.before(effFromDate)) {
                isChangeAvailable = false;
            }
        }
        // If the transaction effective from date after the effective to date, Change option should be hidden.
        if (isChangeAvailable && !StringUtils.isBlank(effToDateString)) {
            Date effToDate = DateUtils.parseDate(effToDateString);
            if (transEffDate.after(effToDate)) {
                isChangeAvailable = false;
            }
        }

        l.exiting(getClass().getName(), "isChangeAvailableForRiskAddtlExposure");
        return isChangeAvailable;
    }

    /**
     * Check if current recordSet is readOnly
     *
     * @return boolean
     */
    private boolean isRecordSetReadOnly() {
        boolean isReadOnly = false;

        // During OOSWIP or RENEWWIP, this page should be read only if the current time is not the initial term.
        ScreenModeCode screenMode = getPolicyHeader().getScreenModeCode();
        if (screenMode.isViewPolicy() ||
            (screenMode.isOosWIP() || screenMode.isRenewWIP()) && !getPolicyHeader().isInitTermB()) {
            isReadOnly = true;
        }

        return isReadOnly;
    }

    /**
     * @param record
     * @param transEffFromDate
     * @param addtlExpEffFromDate
     * @param addtlExpEffTpDate
     */
    private void enableRiskAddtlExposureData(Record record,
                                             Date transEffFromDate,
                                             Date addtlExpEffFromDate,
                                             Date addtlExpEffTpDate) {
        if (!transDateNotInAddtlExposureDates(transEffFromDate, addtlExpEffFromDate, addtlExpEffTpDate)) {
            enableRiskAddtlExposureData(record);
        }
        else {
            disableRiskAddtlExposureData(record);
        }
    }

    private boolean transDateNotInAddtlExposureDates(Date transEffFromDate,
                                                     Date addtlExpEffFromDate,
                                                     Date addtlExpEffTpDate) {
        if (transEffFromDate.before(addtlExpEffFromDate) ||
            transEffFromDate.after(addtlExpEffTpDate) || transEffFromDate.equals(addtlExpEffTpDate)) {
            return true;
        }
        return false;
    }


    private void disableRiskAddtlExposureData(Record record) {
        record.setEditIndicator(YesNoFlag.N);
        record.setFieldValue("isEffectiveToDateEditable", YesNoFlag.N);
        record.setFieldValue("isPercentPracticeEditable", YesNoFlag.N);
        record.setFieldValue("isCoverageLimitCodeEditable", YesNoFlag.N);
    }

    private void enableRiskAddtlExposureData(Record record) {
        record.setEditIndicator(YesNoFlag.Y);
        record.setFieldValue("isEffectiveToDateEditable", YesNoFlag.Y);
        record.setFieldValue("isPercentPracticeEditable", YesNoFlag.Y);
        record.setFieldValue("isCoverageLimitCodeEditable", YesNoFlag.Y);
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
     * Return a Record of initial entitlement values.
     */
    public synchronized static Record getInitialEntitlementValues() {
        if (c_initialEntitlementValues == null) {
            c_initialEntitlementValues = new Record();
            c_initialEntitlementValues.setFieldValue(RiskAddtlExposureFields.IS_DELETE_VISIBLE, YesNoFlag.Y);
        }
        return c_initialEntitlementValues;
    }
    
    public RiskAddtlExposureEntitlementRecordLoadProcessor(PolicyHeader policyHeader,
                                                           Record inputRecord,
                                                           RiskAddtlExposureManager riskAddtlExposureManager){

        setPolicyHeader(policyHeader);
        setInputRecord(inputRecord);
        setRiskAddtlExposureManager(riskAddtlExposureManager);
    }

    public PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    public void setPolicyHeader(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }

    public RiskAddtlExposureManager getRiskAddtlExposureManager() {
        return m_riskAddtlExposureManager;
    }

    public void setRiskAddtlExposureManager(RiskAddtlExposureManager riskAddtlExposureManager) {
        m_riskAddtlExposureManager = riskAddtlExposureManager;
    }

    public Record getInputRecord() {
        return m_inputRecord;
    }

    public void setInputRecord(Record inputRecord) {
        m_inputRecord = inputRecord;
    }

    private static Record c_initialEntitlementValues;
    private PolicyHeader m_policyHeader;
    private RiskAddtlExposureManager m_riskAddtlExposureManager;
    private Record m_inputRecord;
}
