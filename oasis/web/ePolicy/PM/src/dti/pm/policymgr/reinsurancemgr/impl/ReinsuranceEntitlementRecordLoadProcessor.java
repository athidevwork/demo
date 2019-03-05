package dti.pm.policymgr.reinsurancemgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.busobjs.TransactionTypeCode;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.entitlementmgr.EntitlementFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.reinsurancemgr.ReinsuranceFields;

import java.util.logging.Logger;

/**
 * This class extends the default record load processor to enforce entitlements for the reinsurance web page. This
 * class works in conjunction with pageEntitlements.xml configuration.
 * <p/>
 * <p>Any field that requires entitlement enforcement is determined by an indicator field that gets appended to the list
 * of fields in a record. This record is published as xml data island, which then gets intercepted by pageEntitlements
 * oasis tag to auto-generate javascript that enforces the entitlements.</p>
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 12, 2007
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *  05/08/08        yhchen      #82394 Add logic to set Delete button available
 *  06/25/2012      sxm         134889 - Set screen to readonly in prior term during renewal WIP
 * 11/19/2015       eyin        167171 - Modified getInitialEntitlementValuesForReinsurance(), Add synchronized lock on this function.
 * ---------------------------------------------------
 */
public class ReinsuranceEntitlementRecordLoadProcessor implements RecordLoadProcessor {

    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     * @return true if this Record should be added to the RecordSet;
     *         false if this Record should be excluded from the RecordSet.
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecord", new Object[]{record, String.valueOf(rowIsOnCurrentPage)});

        EntitlementFields.setIsRowEligibleForDelete(record, YesNoFlag.N);
        if (("-1").equals( ReinsuranceFields.getReinsurerEntityId(record))) {
              ReinsuranceFields.setReinsurerEntityId(record,"");
        }

        //set delete option available
        ScreenModeCode screenMode = getPolicyHeader().getScreenModeCode();
        if(screenMode.isManualEntry() || screenMode.isOosWIP() || screenMode.isRenewWIP() || screenMode.isWIP()){
           EntitlementFields.setIsRowEligibleForDelete(record, YesNoFlag.Y);
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
            recordSet.addFieldNameCollection(getInitialEntitlementValuesForReinsurance().getFieldNameList());
        }

        // Set readOnly attribute to summary record
        EntitlementFields.setReadOnly(recordSet.getSummaryRecord(), isRecordSetReadOnly());
        l.exiting(getClass().getName(), "postProcessRecordSet");
    }

    /**
     * Check if current recordSet is readOnly
     *
     * @return boolean
     */
    private boolean isRecordSetReadOnly() {
        boolean isReadOnly = false;
        // All available fields are readonly, if a transaction is not in progress
        // or Cancel/Reinstate in progress
        boolean isInProcess = getPolicyHeader().getLastTransactionInfo().getTransactionStatusCode().isInProgress();
        TransactionTypeCode transTypeCode = getPolicyHeader().getLastTransactionInfo().getTransactionTypeCode();
        if (!isInProcess) {
            isReadOnly = true;
        }
        else if (transTypeCode.isCancel() || transTypeCode.isReinstate()) {
            isReadOnly = true;
        }

        // During OOSWIP or RENEWWIP, it is editable only in the transaction initiated term.
        ScreenModeCode screenMode = getPolicyHeader().getScreenModeCode();
        if ((screenMode.isOosWIP() || screenMode.isRenewWIP()) && !getPolicyHeader().isInitTermB()) {
            isReadOnly = true;
        }
        return isReadOnly;
    }

    /**
     * Return a Record of initial entitlement values for a new Schedule record.
     */
    public synchronized static Record getInitialEntitlementValuesForReinsurance() {
        if (c_initialEntitlementValues == null) {
            c_initialEntitlementValues = new Record();
            EntitlementFields.setIsRowEligibleForDelete(c_initialEntitlementValues, YesNoFlag.Y);
        }
        return c_initialEntitlementValues;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public ReinsuranceEntitlementRecordLoadProcessor() {
    }

    public ReinsuranceEntitlementRecordLoadProcessor(PolicyHeader policyHeader) {
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
