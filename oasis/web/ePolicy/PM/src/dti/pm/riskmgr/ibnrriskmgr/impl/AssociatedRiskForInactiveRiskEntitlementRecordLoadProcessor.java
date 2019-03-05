package dti.pm.riskmgr.ibnrriskmgr.impl;

import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.busobjs.TransactionCode;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.riskmgr.ibnrriskmgr.InactiveRiskFields;

import java.util.logging.Logger;

/**
 * This class extends the default record load processor to enforce entitlements for Affiliation web page. This class works in
 * conjunction with pageEntitlements.xml configuration.
 * <p/>
 * <p>Any field that requires entitlement enforcement is determined by an indicator field that gets appended to the list
 * of fields in a record. This record is published as xml data island, which then gets intercepted by pageEntitlements
 * oasis tag to auto-generate javascript that enforces the entitlements.</p>
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 13, 2011
 *
 * @author Dzhang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class AssociatedRiskForInactiveRiskEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {
    /**
     * Process the given record after it's been loaded to enforce page entitlements.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecord",
            new Object[]{record, String.valueOf(rowIsOnCurrentPage)});

        if (isScreenInEditableMode()) {
            String closingTransLogId = InactiveRiskFields.getClosingTransLogId(record);
            if ((!StringUtils.isBlank(closingTransLogId)) && closingTransLogId.equals(getPolicyHeader().getLastTransactionId())) {
                record.setDisplayIndicator("N");
            }
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
        l.exiting(getClass().getName(), "postProcessRecordSet");
    }

    private boolean isScreenInEditableMode() {
        boolean isEditable = false;
        ScreenModeCode screenModeCode = getPolicyHeader().getScreenModeCode();
        // The screen is editable during New Business WIP, Endorsement WIP, Renewal WIP, Reissue WIP, and Cancel Risk WIP.
        if (screenModeCode.isWIP() || screenModeCode.isRenewWIP() || screenModeCode.isManualEntry() ||
            (screenModeCode.isCancelWIP() && TransactionCode.RISKCANCEL.equals(getPolicyHeader().getLastTransactionInfo().getTransactionCode()))) {
            isEditable = true;
        }

        return isEditable;
    }

    public AssociatedRiskForInactiveRiskEntitlementRecordLoadProcessor() {
    }

    public AssociatedRiskForInactiveRiskEntitlementRecordLoadProcessor(PolicyHeader policyHeader) {
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