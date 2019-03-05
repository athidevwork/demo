package dti.pm.riskmgr.ibnrriskmgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.busobjs.TransactionCode;
import dti.pm.entitlementmgr.EntitlementFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.riskmgr.ibnrriskmgr.InactiveRiskFields;

import java.util.ArrayList;
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
 * Date:   Mar 14, 2011
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
public class InactiveRiskEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {
    /**
     * Process the given record after it's been loaded to enforce page entitlements.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecord",
            new Object[]{record, String.valueOf(rowIsOnCurrentPage)});

        record.setFieldValue(IS_DELETE_INC_AVAILABLE, isDeleteIncAvailable(record));
        record.setFieldValue(IS_RECORD_EDITABLE, isRecordEditable(record));

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

        // If no record is fetched, add the page entitlement fields to the recordset field collection
        if (recordSet.getSize() == 0) {
            ArrayList pageEntitlementFields = new ArrayList();
            pageEntitlementFields.add(IS_DELETE_INC_AVAILABLE);
            recordSet.addFieldNameCollection(pageEntitlementFields);
        }
        Record summaryRecord = recordSet.getSummaryRecord();
        EntitlementFields.setReadOnly(summaryRecord, !isScreenInEditableMode());
        l.exiting(getClass().getName(), "postProcessRecordSet");
    }

    /**
     * check if Delete Option available for current selected row.
     *
     * @param record the current record
     * @return YesNoFlag
     */
    private YesNoFlag isRecordEditable(Record record) {
        YesNoFlag isEditable = YesNoFlag.N;

        if (PMCommonFields.hasRecordModeCode(record) && isScreenInEditableMode()) {
            RecordMode recordModeCode = PMCommonFields.getRecordModeCode(record);
            if (!recordModeCode.isOfficial()) {
                isEditable = YesNoFlag.Y;
            }
        }

        return isEditable;
    }

    /**
     * check if Record editable for current selected row.
     *
     * @param record the current record
     * @return YesNoFlag
     */
    private YesNoFlag isDeleteIncAvailable(Record record) {
        YesNoFlag isAvailable = YesNoFlag.N;

        if (PMCommonFields.hasRecordModeCode(record) && isScreenInEditableMode()) {
            RecordMode recordModeCode = PMCommonFields.getRecordModeCode(record);
            if (!recordModeCode.isOfficial()) {
                isAvailable = YesNoFlag.Y;
            }
        }

        return isAvailable;
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

    /**
     * Set initial entitlement Values for Inactive Risk
     *
     * @param policyHeader
     * @param record
     */
    public static void setInitialEntitlementValuesForInacRisk(PolicyHeader policyHeader, Record record) {
        InactiveRiskEntitlementRecordLoadProcessor entitlementRLP = new InactiveRiskEntitlementRecordLoadProcessor(
            policyHeader);
        entitlementRLP.postProcessRecord(record, true);
    }

    public InactiveRiskEntitlementRecordLoadProcessor() {
    }

    public InactiveRiskEntitlementRecordLoadProcessor(PolicyHeader policyHeader) {
        setPolicyHeader(policyHeader);
    }

    public PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    public void setPolicyHeader(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }

    private PolicyHeader m_policyHeader;
    public static final String IS_DELETE_INC_AVAILABLE = "isDeleteIncAvailable";
    public static final String IS_RECORD_EDITABLE = "isRecordEditable";
}