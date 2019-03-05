package dti.pm.policymgr.underwritermgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.entitlementmgr.EntitlementFields;
import dti.pm.busobjs.TransactionTypeCode;
import dti.pm.busobjs.ScreenModeCode;

import java.util.Date;
import java.util.logging.Logger;

/**
 * This class extends the default record load processor to enforce entitlements for the underwriter web page. This class works in
 * conjunction with pageEntitlements.xml configuration.
 *
 * <p>Any field that requires entitlement enforcement is determined by an indicator field that gets appended to the list
 * of fields in a record. This record is published as xml data island, which then gets intercepted by pageEntitlements
 * oasis tag to auto-generate javascript that enforces the entitlements.</p>
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 26, 2007
 *
 * @author jmpotosky
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/06/2007         MLM       Enhanced postProcessRecordSet method to add page Entitlements fields to existing
 *                              field collection, if no record is added to the recordset.
 * 06/29/2010       dzhang      107902 - If the screen mode is "MANUAL_ENTRY", "CANCELWIP", or "REINSTATEWIP", 
 *                              remove records with closing_trans_log_fk equals current WIP transaction log FK.
 * 08/23/2010       syang       Issue 108651 - Added isRenewalBAvailable() to handle Renew indicator.
 * 07/29/2013       awu         147031 - 1. Modified postProcessRecord to set then Type field to disable;
 *                                       2. Modified getInitialEntitlementValuesForUnderwriter to set the Type to enable.
 * 06/28/2014       awu         154778 - Modified postProcessRecord to include RENEWAL WIP.
 * 11/19/2015       eyin        167171 - Modified getInitialEntitlementValuesForUnderwriter(), Add synchronized lock on this function.
 * 08/26/2016       wdang       167534 - WIP screenMode will disable closed record when as well.
 * ---------------------------------------------------
 */

public class UnderwriterEntitlementRecordLoadProcessor implements RecordLoadProcessor {

    /**
     * Process the given record after it's been loaded to enforce page entitlements.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecord", new Object[]{record, String.valueOf(rowIsOnCurrentPage)});

        EntitlementFields.setIsRowEligibleForDelete(record, YesNoFlag.N);
        record.setFieldValue("isEffectiveFromDateEditable", YesNoFlag.N);
        record.setFieldValue("isTypeEditable", YesNoFlag.N);

        //If the screen mode is "MANUAL_ENTRY", "CANCELWIP", or "REINSTATEWIP",
        // remove records with closing_trans_log_fk equals current WIP transaction log FK.
        ScreenModeCode screenModeCode = getPolicyHeader().getScreenModeCode();
        String transactionLogId =  getPolicyHeader().getLastTransactionInfo().getTransactionLogId();
        if (screenModeCode.isManualEntry() || screenModeCode.isCancelWIP() || screenModeCode.isResinstateWIP()
            || screenModeCode.isRenewWIP() || screenModeCode.isWIP()) {
           String closingTransLogId = record.getStringValue("closingTransLogId");
           if((!StringUtils.isBlank(closingTransLogId)) && closingTransLogId.equals(transactionLogId)) {
               record.setDisplayIndicator("N");
           }
        }
        // Issue 108651 - handle Renew indicator.
        record.setFieldValue("isRenewalBAvailable", isRenewalBAvailable(record));
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
        if(recordSet.getSize() == 0) {
            recordSet.addFieldNameCollection(getInitialEntitlementValuesForUnderwriter().getFieldNameList());
        }
        // Set readOnly attribute to summary record
        EntitlementFields.setReadOnly(recordSet.getSummaryRecord(), isRecordSetReadOnly());
        l.exiting(getClass().getName(), "postProcessRecordSet");
    }

    /**
     * Return a Record of initial entitlement values for a new Schedule record.
     */
    public synchronized static Record getInitialEntitlementValuesForUnderwriter() {
        if (c_initialEntitlementValues == null) {
            c_initialEntitlementValues = new Record();
            EntitlementFields.setIsRowEligibleForDelete(c_initialEntitlementValues, YesNoFlag.Y);
            c_initialEntitlementValues.setFieldValue("isEffectiveFromDateEditable", YesNoFlag.Y);
            c_initialEntitlementValues.setFieldValue("isTypeEditable", YesNoFlag.Y);
            c_initialEntitlementValues.setFieldValue("isRenewalBAvailable", YesNoFlag.Y);
        }
        return c_initialEntitlementValues;
    }

    /**
     * Check if current recordSet is readOnly
     *
     * @return boolean
     */
    private boolean isRecordSetReadOnly() {
        boolean isReadOnly = false;
        TransactionTypeCode transactionTypeCode = getPolicyHeader().getLastTransactionInfo().getTransactionTypeCode();

        // All available fields are readonly in:
        // 1.Cancelled Policy
        // 2.Cancel or Reinstatement Transaction in Progress While Viewing WIP Mode
        if (getPolicyHeader().getLastTransactionInfo().getTransactionStatusCode().isInProgress()) {
            if (transactionTypeCode.isCancel() || transactionTypeCode.isReinstate()) {
                isReadOnly = true;
            }
        }
        else {
            // This part logic covers the following case:
            // 1)Policy has a status in Cancel WIP,view mode is official,all options are enabled.
            // 2)Policy has a status in Cancel Official,all options are disabled.
            // 3)Policy has a status in Reinstate WIP,view mode is official,all options are disabled.
            // 4)Policy has a status in Reinstate Official,all options are enabled.
            if (getPolicyHeader().getPolicyStatus().isCancelled()) {
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
    private YesNoFlag isRenewalBAvailable(Record record) {
        YesNoFlag isAvailable = YesNoFlag.Y;
        Date effectiveToDate = record.getDateValue("effectiveToDate");
        Date termExpirationDate = DateUtils.parseDate(getPolicyHeader().getTermEffectiveToDate());
        if (effectiveToDate.before(termExpirationDate)) {
            isAvailable = YesNoFlag.N;
        }
        return isAvailable;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
    }

    public UnderwriterEntitlementRecordLoadProcessor() {
    }

    public UnderwriterEntitlementRecordLoadProcessor(PolicyHeader policyHeader) {
        setPolicyHeader(policyHeader);
    }

    public PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    public void setPolicyHeader(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }

    private PolicyHeader m_policyHeader;

    private static Record c_initialEntitlementValues;
}

