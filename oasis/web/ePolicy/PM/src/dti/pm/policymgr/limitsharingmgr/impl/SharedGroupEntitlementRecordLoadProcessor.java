package dti.pm.policymgr.limitsharingmgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.SysParmProvider;
import dti.oasis.util.StringUtils;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.busobjs.SysParmIds;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.limitsharingmgr.SharedGroupFields;
import dti.pm.entitlementmgr.EntitlementFields;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This class extends the default record load processor to enforce entitlements for the share group web page.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 23, 2007
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/07/2012       tcheng      134180 - Modified postProcessRecordSet() to add two field for sharedGroupListGrid.
 * 06/25/2012       sxm         134889 - Set screen to readonly in prior term during renewal WIP
 * 08/24/2012       adeng       135972 - Added one more constructor to address no risk scenario.Modified postProcessRecord to make sure if has no risk the add button of detail
 *                              section will not be displayed.
 * 08/31/2012       adeng       135972 - Roll backed changes,using new solution of hiding Limit Sharing button.
 * ---------------------------------------------------
 */

public class SharedGroupEntitlementRecordLoadProcessor implements RecordLoadProcessor {

    /**
     * Process the given record after it's been loaded to enforce page entitlements.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecord", new Object[]{record, String.valueOf(rowIsOnCurrentPage)});
        ScreenModeCode screenMode = getPolicyHeader().getScreenModeCode();
        if (screenMode.isViewPolicy() || screenMode.isViewEndquote() ||
            screenMode.isCancelWIP() || screenMode.isResinstateWIP() ||
            (screenMode.isOosWIP() || screenMode.isRenewWIP()) && !getPolicyHeader().isInitTermB()) {
            record.setFieldValue("isDelShareGroupAvailable", YesNoFlag.N);
            record.setFieldValue("isShareGroupDataEditable", YesNoFlag.N);
            record.setFieldValue("isAddShareDetailAvailable", YesNoFlag.N);
            record.setFieldValue("isRenewalBEditable", YesNoFlag.N);
        }
        else {
            String recordModeCode = record.getStringValue("shareGroupRecordModeCode");
            YesNoFlag isEditable = isDataEditable(record);

            record.setFieldValue("isShareGroupDataEditable", isEditable);
            record.setFieldValue("isRenewalBEditable", isEditable);
            if (isEditable.booleanValue()) {
                if (DateUtils.parseDate(record.getFieldValue("shareGroupEffToDate").toString()).before (DateUtils.parseDate(getPolicyHeader().getTermEffectiveToDate()))){
                    record.setFieldValue("isRenewalBEditable", YesNoFlag.N);
                }
                else {
                    record.setFieldValue("isRenewalBEditable", YesNoFlag.Y);
                }
            }
            record.setFieldValue("isAddShareDetailAvailable", YesNoFlag.Y);
            if (recordModeCode.equals("TEMP")) {
                record.setFieldValue("isDelShareGroupAvailable", YesNoFlag.Y);
            }
            else {
                record.setFieldValue("isDelShareGroupAvailable", YesNoFlag.N);
            }
        }
        record.setFieldValue("origShareGroupEffToDate", SharedGroupFields.getShareGroupEffToDate(record));

        l.exiting(getClass().getName(), "postProcessRecord");
        return true;
    }

    /**
     * Process the RecordSet for enforcing page entitlements after all records have been loaded and processed.
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        // If no record is fetched, add the page entitlement fields to the recordset field collection
        if (recordSet.getSize() == 0) {
            ArrayList pageEntitlementFields = new ArrayList();
            pageEntitlementFields.add("isDelShareGroupAvailable");
            pageEntitlementFields.add("isShareGroupDataEditable");
            pageEntitlementFields.add("isAddShareDetailAvailable");
            // Issue 134180, handle js error pops up after change Expiration Date of Shared Group on Limit Sharing page;.
            pageEntitlementFields.add("isRenewalBEditable");
            pageEntitlementFields.add("origShareGroupEffToDate");

            recordSet.addFieldNameCollection(pageEntitlementFields);
        }
        // Set readOnly attribute to summary record
        EntitlementFields.setReadOnly(recordSet.getSummaryRecord(),isRecordSetReadOnly());
    }

    private YesNoFlag isDataEditable(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isDataEditable", new Object[]{record,});
        }
        YesNoFlag isEditable = YesNoFlag.N;
        Date transactionEffFromDate = DateUtils.parseDate(getPolicyHeader().getLastTransactionInfo().getTransEffectiveFromDate());
        Date currentEffFromDate = DateUtils.parseDate(record.getStringValue("shareGroupEffFromDate"));
        Date currentEffToDate = DateUtils.parseDate(record.getStringValue("shareGroupEffToDate"));
        if ((!(transactionEffFromDate.before(currentEffFromDate))) && (transactionEffFromDate.before(currentEffToDate))) {
            isEditable = YesNoFlag.Y;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isDataEditable", isEditable);
        }
        return isEditable;
    }

    /**
     * Check if current recordSet is readOnly
     *
     * @return boolean
     */
    private boolean isRecordSetReadOnly() {
        boolean isReadOnly = false;
        ScreenModeCode screenMode = getPolicyHeader().getScreenModeCode();
        // All available fields are readonly in following screen modes:
        // VIEW_POLICY, VIEW_ENDQUOTE, CANCELWIP, or REINSTATEWIP
        if (screenMode.isViewPolicy() || screenMode.isViewEndquote() ||
            screenMode.isCancelWIP() || screenMode.isResinstateWIP() ||
            (screenMode.isOosWIP() || screenMode.isRenewWIP()) && !getPolicyHeader().isInitTermB()) {
            isReadOnly = true;
        }

        return isReadOnly;
    }

    /**
     * Set initial engitlement Values for shared group
     *
     * @param record
     */
    public void setInitialEntitlementValuesForSharedGroup(Record record) {
        postProcessRecord(record, true);
    }

    public PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    public void setPolicyHeader(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }

    public SharedGroupEntitlementRecordLoadProcessor() {
    }

    public SharedGroupEntitlementRecordLoadProcessor(PolicyHeader policyHeader) {
        this.m_policyHeader = policyHeader;
    }

    private PolicyHeader m_policyHeader;
}
