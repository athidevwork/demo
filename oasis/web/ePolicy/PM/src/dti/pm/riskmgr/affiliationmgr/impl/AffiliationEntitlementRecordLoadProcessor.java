package dti.pm.riskmgr.affiliationmgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.SysParmProvider;
import dti.pm.busobjs.PolicyCycleCode;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.busobjs.SysParmIds;
import dti.pm.entitlementmgr.EntitlementFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.riskmgr.affiliationmgr.AffiliationFields;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class extends the default record load processor to enforce entitlements for Affiliation web page. This class works in
 * conjunction with pageEntitlements.xml configuration.
 * <p/>
 * <p>Any field that requires entitlement enforcement is determined by an indicator field that gets appended to the list
 * of fields in a record. This record is published as xml data island, which then gets intercepted by pageEntitlements
 * oasis tag to auto-generate javascript that enforces the entitlements.</p>
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 13, 2008
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/18/12         fcb         137704 - isRecordSetReadOnly - logic to set readOnly for transactions
 *                              outside the term dates.
 * 12/07/2017       lzhang      182769 - Invisible ADD and SAVE button when transaction date
 *                                       is not located in risk period
 *                                       and not editable field when transaction date
 *                                       is not located in affiliation period
 * ---------------------------------------------------
 */
public class AffiliationEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {
    /**
     * Process the given record after it's been loaded to enforce page entitlements.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecord",
            new Object[]{record, String.valueOf(rowIsOnCurrentPage)});

        record.setFieldValue("origEffDate", AffiliationFields.getEffDate(record));
        record.setFieldValue("origExpDate", AffiliationFields.getExpDate(record));
        String relationTypeCode = AffiliationFields.getRelationTypeCode(record);
        if ("AW".equals(relationTypeCode)) {
            record.setFieldValue("isPercentPracticeEditable", YesNoFlag.Y);
        }
        else {
            record.setFieldValue("isPercentPracticeEditable", YesNoFlag.N);
        }
        record.setFieldValue("isDeleteAvailable", YesNoFlag.N);
        record.setFieldValue("isAddAvailable", YesNoFlag.Y);

        String transDateStr = getPolicyHeader().getLastTransactionInfo().getTransEffectiveFromDate();
        String affEffDateStr = AffiliationFields.getEffDate(record);
        String affExpDateStr = AffiliationFields.getExpDate(record);
        if (isTransDateNotInDatesPeriod(transDateStr, affEffDateStr, affExpDateStr)){
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
            pageEntitlementFields.add("isAddAvailable");
            pageEntitlementFields.add("isSaveAvailable");
            recordSet.addFieldNameCollection(pageEntitlementFields);
        }
        Record summaryRecord = recordSet.getSummaryRecord();
        boolean readOnly = isRecordSetReadOnly();
        if (readOnly) {
            summaryRecord.setFieldValue("isSaveAvailable", YesNoFlag.N);
            summaryRecord.setFieldValue("isAddAvailable", YesNoFlag.N);
        }
        else {
            summaryRecord.setFieldValue("isSaveAvailable", YesNoFlag.Y);
            summaryRecord.setFieldValue("isAddAvailable", YesNoFlag.Y);
        }
        EntitlementFields.setReadOnly(recordSet.getSummaryRecord(), readOnly);

        String transDateStr = getPolicyHeader().getLastTransactionInfo().getTransEffectiveFromDate();
        String contigRiskEffectiveDateStr = getPolicyHeader().getRiskHeader().getContigRiskEffectiveDate();
        String contigRiskExpireDateStr = getPolicyHeader().getRiskHeader().getContigRiskExpireDate();
        if (isTransDateNotInDatesPeriod(transDateStr, contigRiskEffectiveDateStr, contigRiskExpireDateStr)){
            summaryRecord.setFieldValue("isSaveAvailable", YesNoFlag.N);
            summaryRecord.setFieldValue("isAddAvailable", YesNoFlag.N);
        }
        l.exiting(getClass().getName(), "postProcessRecordSet");
    }

    /**
     * Check if current recordSet is readOnly
     *
     * @return boolean
     */
    private boolean isRecordSetReadOnly() {
        boolean isReadOnly = false;
        // CANCELWIP or REINSTATEWIP
        PolicyCycleCode policyCycle = getPolicyHeader().getPolicyCycleCode();
        if (policyCycle.isPolicy()) {
            ScreenModeCode screenModeCode = getPolicyHeader().getScreenModeCode();
            // If cancle or reinstate transaction in progress, set page readonly
            if (screenModeCode.isCancelWIP() || screenModeCode.isResinstateWIP()) {
                isReadOnly = true;
            }
        }
        //if this risk is cancelled
        if (getPolicyHeader().getRiskHeader().getRiskStatusCode().isCancelled()) {
            isReadOnly = true;
        }

        return isReadOnly;
    }

    /**
     * Check whether the transaction date is located in risk period
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
     * Set initial entitlement Values for affilaition         *
     *
     * @param record
     * @return
     */
    public static void setInitialEntitlementValuesForAffiliation(PolicyHeader policyHeader, Record record) {
        AffiliationEntitlementRecordLoadProcessor entitlementRLP = new AffiliationEntitlementRecordLoadProcessor(policyHeader);
        // Set page entitlement values
        entitlementRLP.postProcessRecord(record, true);
        record.setFieldValue("isDeleteAvailable", YesNoFlag.Y);
    }

    public AffiliationEntitlementRecordLoadProcessor() {
    }

    public AffiliationEntitlementRecordLoadProcessor(PolicyHeader policyHeader) {
        setPolicyHeader(policyHeader);
    }

    public PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    public void setPolicyHeader(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }


    private PolicyHeader m_policyHeader;


}
