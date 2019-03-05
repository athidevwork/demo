package dti.pm.policymgr.processacfmgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.pm.busobjs.PolicyViewMode;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.entitlementmgr.EntitlementFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.processacfmgr.ProcessAcfFields;

import java.util.ArrayList;

/**
 * This class extends the default record load processor to enforce entitlements for policy web page. This class works in
 * conjunction with pageEntitlements.xml configuration.
 * <p/>
 * <p>Any field that requires entitlement enforcement is determined by an indicator field that gets appended to the list
 * of fields in a record. This record is published to request as request attributes, which then gets intercepted by
 * pageEntitlements oasis tag to auto-generate java script that enforces the entitlements.</p>
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 31, 2011
 *
 * @author syang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/25/2012       sxm         134889 - Set screen to readonly in prior term during renewal WIP
 *
 * ---------------------------------------------------
 */
public class ProcessAcfOverrideEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {
    
    /**
     * Process the given record after it's been loaded to enforce page entitlements.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        record.setFieldValue(IS_OVERRIDE_DEL_AVAILABLE, YesNoFlag.N);
        // Set Delete option availability, enabled when the record displayed is added during the current transaction.
        if(ProcessAcfFields.getOverrideTransId(record).endsWith(getPolicyHeader().getLastTransactionId())){
            record.setFieldValue(IS_OVERRIDE_DEL_AVAILABLE, YesNoFlag.Y);
        }
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
            pageEntitlementFields.add(IS_OVERRIDE_DEL_AVAILABLE);
            recordSet.addFieldNameCollection(pageEntitlementFields);
        }
        recordSet.getSummaryRecord().setFieldValue(IS_ADD_AVAILABLE, YesNoFlag.N);
        // Set Add option availability, enabled when the current transaction is in WIP.
        if(getPolicyHeader().getLastTransactionInfo().getTransactionStatusCode().isInProgress()){
            recordSet.getSummaryRecord().setFieldValue(IS_ADD_AVAILABLE, YesNoFlag.Y);
        }

        // Set readOnly attribute to summary record
        if (isRecordSetReadOnly()) {
            recordSet.setFieldValueOnAll(IS_OVERRIDE_DEL_AVAILABLE, YesNoFlag.N);
            recordSet.getSummaryRecord().setFieldValue(IS_SAVE_AVAILABLE, YesNoFlag.N);
            EntitlementFields.setReadOnly(recordSet.getSummaryRecord(), true);
        }
    }

    /**
     * Check if current recordSet is readOnly
     *
     * @return
     */
    private boolean isRecordSetReadOnly() {
        boolean isReadOnly = false;
        // All available fields are readonly in following modes.
        PolicyViewMode viewMode = getPolicyHeader().getPolicyIdentifier().getPolicyViewMode();
        ScreenModeCode screenMode = getPolicyHeader().getScreenModeCode();
        if (viewMode.isOfficial() || viewMode.isEndquote() ||
            screenMode.isCancelWIP() || screenMode.isResinstateWIP() ||
           (screenMode.isOosWIP() || screenMode.isRenewWIP()) && !getPolicyHeader().isInitTermB()) {
            isReadOnly = true;
        }
        return isReadOnly;
    }

    public ProcessAcfOverrideEntitlementRecordLoadProcessor(PolicyHeader policyHeader) {
          setPolicyHeader(policyHeader);
    }

    public PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    public void setPolicyHeader(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }

    private PolicyHeader m_policyHeader;
    public static final String IS_ADD_AVAILABLE = "isAddAvailable";
    public static final String IS_SAVE_AVAILABLE = "isSaveAvailable";
    public static final String IS_OVERRIDE_DEL_AVAILABLE = "isOverrideDelAvailable";
}
