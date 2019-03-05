package dti.pm.riskmgr.coimgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import dti.pm.busobjs.PolicyCycleCode;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.busobjs.SysParmIds;
import dti.pm.entitlementmgr.EntitlementFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.riskmgr.coimgr.CoiFields;

import java.util.Date;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.ArrayList;

/**
 * This class extends the default record load processor to enforce entitlements for COI web page. This class works in
 * conjunction with pageEntitlements.xml configuration.
 * <p/>
 * <p>Any field that requires entitlement enforcement is determined by an indicator field that gets appended to the list
 * of fields in a record. This record is published as xml data island, which then gets intercepted by pageEntitlements
 * oasis tag to auto-generate javascript that enforces the entitlements.</p>
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p> 
 * Date:   Jul 16, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/24/2010       syang       Issue 108651 - Added isRenewBAvailable() to handle Renew indicator.
 * 11/14/2011       dzhang      Issue 120683 - Modified postProcessRecordSet.
 * 12/02/2011       wfu         Issue 127703 - Modified isRenewBAvailable to disable Renew if risk is cancelled.
 * 04/13/2012       xnie        Issue 120683 - a) Modified postProcessRecord() to make select address button available
 *                                                when current record is not a new one.
 *                                             b) Removed isSelectAddressAvailable(), setUserHasProfileForAddress(),
 *                                                isUserHasProfileForAddress, and m_userHasProfileForAddress.
 *                                             c) Modified CoiEntitlementRecordLoadProcessor() to remove changing
 *                                                address user profile.
 * 08/30/2012       xnie        Issue 120683 - Modified postProcessRecord() and postProcessRecordSet() to remove
 *                                             isSelectAddressAvailable.
 * 12/23/2013       adeng       146639 - Modified postProcessRecordSet() to filter out expired official records.
 * 08/08/2014       kxiang      156598 - Modified postProcessRecordSet(),according to closingTransLogId and last
 *                                       transactionLogId to filter out expired official records.
 * 03/10/2017       wrong       180675 - Added isGridGenerateAvailable to display generate button in COI iframe
 *                                       in UI tab style.
 * 08/18/2017       wli         187807 - Modified postProcessRecord() to set isGenerateAvailable as Y for official
 *                                       policy.
 * 12/07/2017       lzhang      182769 - Invisible ADD and SAVE button when transaction date
 *                                       is not located in risk period
 *                                       and not editable field when transaction date
 *                                       is not located in coi period
 * ---------------------------------------------------
 */
public class CoiEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {
    /**
     * Process the given record after it's been loaded to enforce page entitlements.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecord",
            new Object[]{record, String.valueOf(rowIsOnCurrentPage)});

        String status = null;
        String pmUIStyle = SysParmProvider.getInstance().getSysParm("PM_UI_STYLE", "T");
        if (pmUIStyle.equals("T")) {
            record.setFieldValue("isGridSelAddrAvailable", YesNoFlag.Y);
        }
        else {
            record.setFieldValue("isGridSelAddrAvailable", YesNoFlag.N);
        }
        if (record.hasStringValue(CoiFields.COI_STATUS)) {
            status = CoiFields.getCoiStatus(record);
        }
        if (status != null && status.equals("PENDING")) {
            record.setFieldValue("isEffectiveFromDateEditable", YesNoFlag.Y);
            record.setFieldValue("isDeleteAvailable", YesNoFlag.Y);
        }
        else {
            record.setFieldValue("isEffectiveFromDateEditable", YesNoFlag.N);
            record.setFieldValue("isDeleteAvailable", YesNoFlag.N);
        }

        // Generate option
        PolicyCycleCode policyCycle = getPolicyHeader().getPolicyCycleCode();
        if (policyCycle.isPolicy()) {
            ScreenModeCode screenModeCode = getPolicyHeader().getScreenModeCode();
            // If cancle or reinstate transaction in progress, set page readonly
            if (screenModeCode.isCancelWIP() || screenModeCode.isResinstateWIP()) {
                record.setFieldValue("isGenerateAvailable", YesNoFlag.N);
                record.setFieldValue("isGridGenerateAvailable", YesNoFlag.N);
            }

            // If no transaction in progress, enable Generate option
            if (!getPolicyHeader().getLastTransactionInfo().getTransactionStatusCode().isInProgress()) {
                if (pmUIStyle.equals("T")) {
                    record.setFieldValue("isGridGenerateAvailable", YesNoFlag.Y);
                }
                else {
                    record.setFieldValue("isGridGenerateAvailable", YesNoFlag.N);
                }
                record.setFieldValue("isGenerateAvailable", YesNoFlag.Y);
            }
            else {
                record.setFieldValue("isGenerateAvailable", YesNoFlag.N);
                record.setFieldValue("isGridGenerateAvailable", YesNoFlag.N);
            }
        }
        else if (policyCycle.isQuote()) {
            // Disable Generate option
            record.setFieldValue("isGenerateAvailable", YesNoFlag.N);
            record.setFieldValue("isGridGenerateAvailable", YesNoFlag.N);
        }
        // if record is newly added, hide Generate option.
        if (record.hasStringValue(CoiFields.COI_STATUS)) {
            if (CoiFields.getCoiStatus(record).equals("PENDING")) {
                record.setFieldValue("isGenerateAvailable", YesNoFlag.N);
                record.setFieldValue("isGridGenerateAvailable", YesNoFlag.N);
            }
        }
        // Issue 108651 - handle Renew indicator.
        record.setFieldValue("isRenewBAvailable", isRenewBAvailable(record));

        String transEffFromDateStr = getPolicyHeader().getLastTransactionInfo().getTransEffectiveFromDate();
        String coiEffectiveFromDate = CoiFields.getCoiEffectiveFromDate(record);
        String coiEffectiveToDate = CoiFields.getCoiEffectiveToDate(record);
        if (isWIPTransModeAndTransDateNotInDatesPeriod(transEffFromDateStr, coiEffectiveFromDate, coiEffectiveToDate)){
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
            pageEntitlementFields.add("isEffectiveFromDateEditable");
            pageEntitlementFields.add("isAddAvailable");
            pageEntitlementFields.add("isSaveAvailable");
            pageEntitlementFields.add("isGenerateAvailable");
            pageEntitlementFields.add("isGridGenerateAvailable");
            pageEntitlementFields.add("isGridSelAddrAvailable");
            pageEntitlementFields.add("isDeleteAvailable");
            pageEntitlementFields.add("isRenewBAvailable");
            recordSet.addFieldNameCollection(pageEntitlementFields);
        }

        Record summaryRecord = recordSet.getSummaryRecord();
        PolicyCycleCode policyCycle = getPolicyHeader().getPolicyCycleCode();
        if (policyCycle.isPolicy()) {
            ScreenModeCode screenModeCode = getPolicyHeader().getScreenModeCode();
            // If cancle or reinstate transaction in progress
            if (screenModeCode.isCancelWIP() || screenModeCode.isResinstateWIP()) {
                // Disable Save and Add options
                summaryRecord.setFieldValue("isSaveAvailable", YesNoFlag.N);
                summaryRecord.setFieldValue("isAddAvailable", YesNoFlag.N);
            }
            else {
                // Enable Save and Add options
                summaryRecord.setFieldValue("isSaveAvailable", YesNoFlag.Y);
                summaryRecord.setFieldValue("isAddAvailable", YesNoFlag.Y);
            }
        }
        else if (policyCycle.isQuote()) {
            // Enable Save and Add options
            summaryRecord.setFieldValue("isSaveAvailable", YesNoFlag.Y);
            summaryRecord.setFieldValue("isAddAvailable", YesNoFlag.Y);
        }
        String transDateStr = getPolicyHeader().getLastTransactionInfo().getTransEffectiveFromDate();
        String contigRiskEffectiveDateStr = getPolicyHeader().getRiskHeader().getContigRiskEffectiveDate();
        String contigRiskExpireDateStr = getPolicyHeader().getRiskHeader().getContigRiskExpireDate();
        if (isWIPTransModeAndTransDateNotInDatesPeriod(transDateStr, contigRiskEffectiveDateStr, contigRiskExpireDateStr)){
            summaryRecord.setFieldValue("isSaveAvailable", YesNoFlag.N);
            summaryRecord.setFieldValue("isAddAvailable", YesNoFlag.N);
        }

        // Filtering:Hide the expired official records.
        RecordSet offRecords = recordSet.getSubSet(new RecordFilter("recordModeCode", "OFFICIAL"));
        Iterator offIt = offRecords.getRecords();
        String transactionLogId = getPolicyHeader().getLastTransactionInfo().getTransactionLogId();
        while (offIt.hasNext()) {
            Record offRecord = (Record) offIt.next();
            if (offRecord.hasStringValue("closingTransLogId")&& transactionLogId.equals(offRecord.getStringValue("closingTransLogId"))) {
                offRecord.setDisplayIndicator("N");
            }
        }

        // Set readOnly attribute to summary record
        EntitlementFields.setReadOnly(recordSet.getSummaryRecord(), isRecordSetReadOnly());
        l.exiting(getClass().getName(), "postProcessRecordSet");
    }

    /**
     * Set initial engitlement Values for COI holder
     *
     * @param policyHeader
     * @param record
     * @return
     */
    public static void setInitialEntitlementValuesForCoi(PolicyHeader policyHeader,
                                                               Record record) {
        CoiEntitlementRecordLoadProcessor entitlementRLP = new CoiEntitlementRecordLoadProcessor(policyHeader);
        // Set page entitlement values
        entitlementRLP.postProcessRecord(record, true);
    }

    /**
     * Check if current recordSet is readOnly
     *
     * @return boolean
     */
    private boolean isRecordSetReadOnly() {
        boolean isReadOnly = false;

        // All available fields are readonly in:
        // CANCELWIP or REINSTATEWIP
        // These logics are from original codes and fail to find clear description in requirement document.
        PolicyCycleCode policyCycle = getPolicyHeader().getPolicyCycleCode();
        if (policyCycle.isPolicy()) {
            ScreenModeCode screenModeCode = getPolicyHeader().getScreenModeCode();
            // If cancle or reinstate transaction in progress, set page readonly
            if (screenModeCode.isCancelWIP() || screenModeCode.isResinstateWIP()) {
                isReadOnly = true;
            }
        }

        return isReadOnly;
    }

    /**
     * Check if the renew indicator is available.
     *
     * @param record
     * @return YesNoFlag
     */
    private YesNoFlag isRenewBAvailable(Record record) {
        YesNoFlag isAvailable = YesNoFlag.Y;
        Date effectiveToDate = record.getDateValue("effectiveToDate");
        // If the effective to date is before than risk effective to date, disable renew indicator.
        if (!StringUtils.isBlank(getRiskEffectiveToDate())) {
            Date termExpirationDate = DateUtils.parseDate(getRiskEffectiveToDate());
            if (effectiveToDate.before(termExpirationDate)) {
                isAvailable = YesNoFlag.N;
            }
        }

        // If the risk status is Cancel, disable renew indicator.
        if (getPolicyHeader().getRiskHeader().getRiskStatusCode().isCancelled()) {
            isAvailable = YesNoFlag.N;
        }

        return isAvailable;
    }

    /**
     * Check if the transaction date is located in risk period
     *
     * @return YesNoFlag
     */
    public boolean isWIPTransModeAndTransDateNotInDatesPeriod(String transEffFromDate, String effFromDate, String effToDate) {
        Logger l = LogUtils.getLogger(DateUtils.class);
        if (l.isLoggable(Level.FINER)) {
            l.entering(DateUtils.class.getName(), "isWIPTransModeAndTransDateNotInDatesPeriod");
        }

        boolean retval = false;
        String transModeSysPara = SysParmProvider.getInstance().getSysParm(SysParmIds.PM_COI_WIP_TRANS, "N");
        if (YesNoFlag.getInstance(transModeSysPara).booleanValue()){
            ScreenModeCode screenModeCode = getPolicyHeader().getScreenModeCode();
            if ((screenModeCode.isManualEntry() || screenModeCode.isWIP() || screenModeCode.isRenewWIP() || screenModeCode.isOosWIP())
                && DateUtils.isTargetDateNotInDatesPeriod(transEffFromDate, effFromDate, effToDate)){
                retval = true;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(DateUtils.class.getName(), "isWIPTransModeAndTransDateNotInDatesPeriod", retval);
        }
        return retval;
    }
    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
    }

    public CoiEntitlementRecordLoadProcessor() {
    }

    public CoiEntitlementRecordLoadProcessor(PolicyHeader policyHeader) {
        setPolicyHeader(policyHeader);
    }

    public CoiEntitlementRecordLoadProcessor(PolicyHeader policyHeader, String riskEffectiveToDate) {
        setPolicyHeader(policyHeader);
        setRiskEffectiveToDate(riskEffectiveToDate);
    }

    public PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    public void setPolicyHeader(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }

    public String getRiskEffectiveToDate() {
        return m_riskEffectiveToDate;
    }

    public void setRiskEffectiveToDate(String riskEffectiveToDate) {
        m_riskEffectiveToDate = riskEffectiveToDate;
    }

    private PolicyHeader m_policyHeader;
    private String m_riskEffectiveToDate;
}
