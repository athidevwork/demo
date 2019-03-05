package dti.pm.policymgr.additionalinsuredmgr.impl;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.pm.busobjs.PolicyCycleCode;
import dti.pm.entitlementmgr.EntitlementFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.policymgr.additionalinsuredmgr.AdditionalInsuredFields;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import java.util.ArrayList;

/**
 * This class extends the default record load processor to enforce entitlements for the underwriter web page. This class works in
 * conjunction with pageEntitlements.xml configuration.
 * 
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 16, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/23/2010       syang       Issue 108651 - Added isRenewalBAvailable() to handle Renew indicator.
 * 02/28/2013       xnie        Issue 138026 - 1) Modified postProcessRecord() to show/hide Generate button.
 *                                             2) Modified postProcessRecordSet() to add field isGenerateAvailable when
 *                                                there is no any Additional Insured.
 * 11/19/2015       eyin        167171 - Modified getInitialEntitlementValuesForAddtionalInsured(), Add synchronized lock on this function.
 * ---------------------------------------------------
 */
public class AdditionalInsuredEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {

    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     * @return true if this Record should be added to the RecordSet;
     *         false if this Record should be excluded from the RecordSet.
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "postProcessRecord", new Object[]{record, String.valueOf(rowIsOnCurrentPage)});
        }

        EntitlementFields.setIsRowEligibleForDelete(record, YesNoFlag.N);
        // Set value of indicator field for "PM_ADDIINS_LOCATION"
        if (isRecordSetReadOnly()) {
            EntitlementFields.setReadOnly(record, true);
        }
        else {
            EntitlementFields.setReadOnly(record, false);
        }

        // Generate option
        PolicyCycleCode policyCycle = getPolicyHeader().getPolicyCycleCode();
        if (policyCycle.isPolicy()) {
            ScreenModeCode screenModeCode = getPolicyHeader().getScreenModeCode();
            // If cancle or reinstate transaction in progress, set page readonly
            if (screenModeCode.isCancelWIP() || screenModeCode.isResinstateWIP()) {
                record.setFieldValue("isGenerateAvailable", YesNoFlag.N);
            }

            // If no transaction in progress, enable Generate option
            if (!getPolicyHeader().getLastTransactionInfo().getTransactionStatusCode().isInProgress()) {
                record.setFieldValue("isGenerateAvailable", YesNoFlag.Y);
            }
            else {
                record.setFieldValue("isGenerateAvailable", YesNoFlag.N);
            }
        }
        else if (policyCycle.isQuote()) {
            // Disable Generate option
            record.setFieldValue("isGenerateAvailable", YesNoFlag.N);
        }
        // if record is newly added, hide Generate option.
        if (record.hasStringValue(AdditionalInsuredFields.ADDINS_STATUS)) {
            if (AdditionalInsuredFields.getAddInsStatus(record).equals("PENDING")) {
                record.setFieldValue("isGenerateAvailable", YesNoFlag.N);
            }
        }

        // Issue 108651 - handle Renew indicator.
        record.setFieldValue("isRenewalBAvailable", isRenewalBAvailable(record));
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "postProcessRecord", String.valueOf(true));
        }
        return true;
    }

    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "postProcessRecordSet", new Object[]{recordSet});
        }
        Record summeryRecord = recordSet.getSummaryRecord();
        EntitlementFields.setReadOnly(summeryRecord, YesNoFlag.N);

        ScreenModeCode screenMode = getPolicyHeader().getScreenModeCode();
        if (screenMode.isCancelWIP() || screenMode.isResinstateWIP() || getPolicyHeader().getPolicyStatus().isCancelled()) {
            EntitlementFields.setReadOnly(summeryRecord, YesNoFlag.Y);
        }

        if(recordSet.getSize()==0){
            List fieldNames = new ArrayList();
            fieldNames.add(EntitlementFields.IS_ROW_ELIGIBLE_FOR_DELETE);
            fieldNames.add(EntitlementFields.READ_ONLY);
            fieldNames.add("isRenewalBAvailable");
            fieldNames.add("isGenerateAvailable");
            recordSet.addFieldNameCollection(fieldNames);
        }
        // Set readOnly attribute to summary record
        EntitlementFields.setReadOnly(recordSet.getSummaryRecord(), isRecordSetReadOnly());
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "postProcessRecordSet");
        }

    }

    /**
     * Check if current recordSet is readOnly
     *
     * @return boolean
     */
    private boolean isRecordSetReadOnly() {
        boolean isReadOnly = false;
        ScreenModeCode screenMode = getPolicyHeader().getScreenModeCode();
        // All available fields are readonly in following scenarios:
        // If the current policy mode is CANCELWIP, REINSTATEWIP,
        // or the overall policy status (POLICY.POL_CURR_STATUS_CODE) = 'CANCEL'
        if (screenMode.isCancelWIP() || screenMode.isResinstateWIP() ||
            getPolicyHeader().getPolicyStatus().isCancelled()) {
            isReadOnly = true;
        }

        // 105611, the page should be readonly if it is opened from view cancellation detail page.
        if(getInputRecord().hasStringValue("snapshotB") && "Y".equalsIgnoreCase(getInputRecord().getStringValue("snapshotB"))){
            isReadOnly = true;
        }

        return isReadOnly;
    }

    /**
     * Check if the renew indicator is available.
     *
     * @param record
     * @return YesNoFlag
     */
    private YesNoFlag isRenewalBAvailable(Record record) {
        YesNoFlag isAvailable = YesNoFlag.Y;
        Date endDate = record.getDateValue("endDate");
        Date policyExpirationDate = DateUtils.parseDate(getPolicyHeader().getPolicyExpirationDate());
        if (endDate.before(policyExpirationDate)) {
            isAvailable = YesNoFlag.N;
        }
        return isAvailable;
    }

    /**
     * Return a Record of initial entitlement values for a new Additional Insured.
     */
    public synchronized static Record getInitialEntitlementValuesForAddtionalInsured() {
        if (c_initialEntitlementValues == null) {
            c_initialEntitlementValues = new Record();
            EntitlementFields.setIsRowEligibleForDelete(c_initialEntitlementValues, YesNoFlag.Y);
            EntitlementFields.setReadOnly(c_initialEntitlementValues, false);
            c_initialEntitlementValues.setFieldValue("isRenewalBAvailable", YesNoFlag.Y);
        }
        return c_initialEntitlementValues;
    }
    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public AdditionalInsuredEntitlementRecordLoadProcessor() {
    }

    public AdditionalInsuredEntitlementRecordLoadProcessor(PolicyHeader policyHeader, Record inputRecord) {
        setPolicyHeader(policyHeader);
        setInputRecord(inputRecord);
    }

    private PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    private void setPolicyHeader(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }

    public Record getInputRecord() {
        return  m_inputRecord;
    }

    public void setInputRecord(Record inputRecord) {
        m_inputRecord = inputRecord;
    }

    private PolicyHeader m_policyHeader;

    private static Record c_initialEntitlementValues;
    private Record m_inputRecord;

}
