package dti.pm.riskmgr.ibnrriskmgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.busobjs.TransactionCode;
import dti.pm.entitlementmgr.EntitlementFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.riskmgr.ibnrriskmgr.InactiveRiskFields;

import java.util.ArrayList;
import java.util.Date;
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
 * Date:   Mar 17, 2011
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
public class AssociatedRiskEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {
    /**
     * Process the given record after it's been loaded to enforce page entitlements.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecord",
            new Object[]{record, String.valueOf(rowIsOnCurrentPage)});

        record.setFieldValue(IS_DELETE_ASSO_AVAILABLE, isDeleteAssoAvailable(record));
        record.setFieldValue(IS_CHANGE_ASSO_AVAILABLE, isChangeAssoAvailable(record));
        record.setFieldValue(IS_ADD_INC_AVAILABLE, isAddIncAvailable(record));

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
            pageEntitlementFields.add(IS_ADD_ASSO_AVAILABLE);
            pageEntitlementFields.add(IS_ADD_INC_AVAILABLE);
            pageEntitlementFields.add(IS_DELETE_ASSO_AVAILABLE);
            pageEntitlementFields.add(IS_CHANGE_ASSO_AVAILABLE);
            pageEntitlementFields.add(IS_SAVE_AVAILABLE);
            recordSet.addFieldNameCollection(pageEntitlementFields);
        }
        Record summaryRecord = recordSet.getSummaryRecord();
        boolean editable = isScreenInEditableMode();
        if (editable) {
            summaryRecord.setFieldValue(IS_SAVE_AVAILABLE, YesNoFlag.Y);
            summaryRecord.setFieldValue(IS_ADD_ASSO_AVAILABLE, YesNoFlag.Y);
        }
        else {
            summaryRecord.setFieldValue(IS_SAVE_AVAILABLE, YesNoFlag.N);
            summaryRecord.setFieldValue(IS_ADD_ASSO_AVAILABLE, YesNoFlag.N);
        }
        EntitlementFields.setReadOnly(recordSet.getSummaryRecord(), !editable);
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

    /**
     * check if Change Option available for current selected row.
     *
     * @param record the current record
     * @return YesNoFlag
     */
    private YesNoFlag isChangeAssoAvailable(Record record) {
        YesNoFlag isAvailable = YesNoFlag.N;

        if (PMCommonFields.hasRecordModeCode(record) && isScreenInEditableMode() &&
            !MIX_RECORD_MODE_CODE.equals(record.getStringValue(PMCommonFields.RECORD_MODE_CODE))) {
            RecordMode recordModeCode = PMCommonFields.getRecordModeCode(record);
            if (recordModeCode.isOfficial()) {
                isAvailable = YesNoFlag.Y;
            }
        }

        return isAvailable;
    }

    /**
     * check if Delete Option available for current selected row.
     *
     * @param record the current record
     * @return YesNoFlag
     */
    private YesNoFlag isDeleteAssoAvailable(Record record) {
        YesNoFlag isAvailable = YesNoFlag.N;

        if (PMCommonFields.hasRecordModeCode(record) && isScreenInEditableMode() &&
            !MIX_RECORD_MODE_CODE.equals(record.getStringValue(PMCommonFields.RECORD_MODE_CODE))) {
            RecordMode recordModeCode = PMCommonFields.getRecordModeCode(record);
            if (!recordModeCode.isOfficial()) {
                isAvailable = YesNoFlag.Y;
            }
        }

        return isAvailable;
    }

    /**
     * check if Add Option (Inactive Risk Detail Section) available for current selected row in Associated Risk grid.
     *
     * @param record the current record
     * @return YesNoFlag
     */
    private YesNoFlag isAddIncAvailable(Record record) {
        YesNoFlag isAvailable = YesNoFlag.N;

        if (isScreenInEditableMode()) {
            String sTransDate = getPolicyHeader().getLastTransactionInfo().getTransEffectiveFromDate();
            Date transDate = DateUtils.parseDate(sTransDate);
            Date riskExpDate = record.getDateValue(InactiveRiskFields.RISK_EFFECTIVE_TO_DATE);
            if (transDate.after(riskExpDate)) {
                isAvailable = YesNoFlag.N;
            }
            else {
                isAvailable = YesNoFlag.Y;
            }
        }

        return isAvailable;
    }

    /**
     * Set initial entitlement Values for Associated Risk
     *
     * @param policyHeader
     * @param record       the current record
     */
    public static void setInitialEntitlementValuesForAssoRisk(PolicyHeader policyHeader, Record record) {
        AssociatedRiskEntitlementRecordLoadProcessor entitlementRLP = new AssociatedRiskEntitlementRecordLoadProcessor(
            policyHeader);
        entitlementRLP.postProcessRecord(record, true);

    }

    public AssociatedRiskEntitlementRecordLoadProcessor() {
    }

    public AssociatedRiskEntitlementRecordLoadProcessor(PolicyHeader policyHeader) {
        setPolicyHeader(policyHeader);
    }

    public PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    public void setPolicyHeader(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }

    private PolicyHeader m_policyHeader;
    private static String MIX_RECORD_MODE_CODE = "MIX";
    public static final String IS_ADD_ASSO_AVAILABLE = "isAddAssoAvailable";
    public static final String IS_ADD_INC_AVAILABLE = "isAddIncAvailable";
    public static final String IS_DELETE_ASSO_AVAILABLE = "isDeleteAssoAvailable";
    public static final String IS_CHANGE_ASSO_AVAILABLE = "isChangeAssoAvailable";
    public static final String IS_SAVE_AVAILABLE = "isSaveAvailable";
}