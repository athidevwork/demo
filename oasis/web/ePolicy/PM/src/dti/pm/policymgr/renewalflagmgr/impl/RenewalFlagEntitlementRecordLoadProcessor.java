package dti.pm.policymgr.renewalflagmgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.entitlementmgr.EntitlementFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.renewalflagmgr.RenewalFlagFields;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   2/16/2016
 *
 * @author tzeng
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 02/24/2015       tzeng       167532 - Initial version.
 * ---------------------------------------------------
 */
public class RenewalFlagEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {

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
        EntitlementFields.setIsRowEligibleForDelete(record, YesNoFlag.Y);
        if(isRecordSetReadOnly() ||
           RenewalFlagFields.getFlagType(record).equals(AUTO_FLAG_TYPE) ||
           (record.hasStringValue(RenewalFlagFields.RECORD_MODE_CODE) &&
            !StringUtils.isSame(RenewalFlagFields.getRecordModeCode(record), RecordMode.TEMP.getName()))) {
            EntitlementFields.setIsRowEligibleForDelete(record, YesNoFlag.N);
        }

        setPropertyByFlagType(record);

        // set the closed record invisible
        if (record.hasStringValue(RenewalFlagFields.CLOSING_TRANS_LOG_ID) &&
            StringUtils.isSame(getPolicyHeader().getLastTransactionId(), record.getStringValue(RenewalFlagFields.CLOSING_TRANS_LOG_ID))) {
            record.setDisplayIndicator(YesNoFlag.N);
        }
        else {
            record.setDisplayIndicator(YesNoFlag.Y);
        }

        l.exiting(getClass().getName(), "postProcessRecord");
        return super.postProcessRecord(record, rowIsOnCurrentPage);
    }

    /**
     * Determine fields available on Renewal Flag page.
     * @param record AUTO or MANUAL flag type for record to determine fields available or not.
     */
    private void setPropertyByFlagType(Record record) {
        if (RenewalFlagFields.getFlagType(record).equals(MANUAL_FLAG_TYPE)) {
            record.setFieldValue("isFlagNameEditable", YesNoFlag.Y);
            record.setFieldValue("isRiskNameEditable", YesNoFlag.Y);
            record.setFieldValue("isFlagManualDescVisible", YesNoFlag.Y);
            record.setFieldValue("isRenewBVisible", YesNoFlag.Y);
        }
        else if (RenewalFlagFields.getFlagType(record).equals(AUTO_FLAG_TYPE)) {
            record.setFieldValue("isFlagNameEditable", YesNoFlag.N);
            record.setFieldValue("isRiskNameEditable", YesNoFlag.N);
            record.setFieldValue("isFlagManualDescVisible", YesNoFlag.N);
            record.setFieldValue("isRenewBVisible", YesNoFlag.N);
        }
    }

    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecordSet", new Object[]{recordSet});

        // Set readOnly attribute to summary record
        EntitlementFields.setReadOnly(recordSet.getSummaryRecord(), isRecordSetReadOnly());

        if(recordSet.getSize()==0){
            List fieldNameList = new ArrayList();
            fieldNameList.add(EntitlementFields.IS_ROW_ELIGIBLE_FOR_DELETE);
            recordSet.addFieldNameCollection(fieldNameList);
        }

        l.exiting(getClass().getName(), "postProcessRecordSet");
    }

    /**
     * Return a Record of initial entitlement values.
     */
    public synchronized static Record getInitialEntitlementValuesForRenewalFlag() {
        if (c_initialEntitlementValues == null) {
            c_initialEntitlementValues = new Record();
            EntitlementFields.setIsRowEligibleForDelete(c_initialEntitlementValues, YesNoFlag.Y);
            c_initialEntitlementValues.setFieldValue(RenewalFlagFields.IS_FLAG_NAME_EDITABLE, YesNoFlag.Y);
            c_initialEntitlementValues.setFieldValue(RenewalFlagFields.IS_RISK_NAME_EDITABLE, YesNoFlag.Y);
            c_initialEntitlementValues.setFieldValue(RenewalFlagFields.IS_FLAG_MANUAL_DESC_VISIBLE, YesNoFlag.Y);
            c_initialEntitlementValues.setFieldValue(RenewalFlagFields.IS_RENEW_B_VISIBLE, YesNoFlag.Y);
        }
        return c_initialEntitlementValues;
    }

    /**
     * Check if current recordSet is readOnly
     *
     * @return boolean
     */
    private boolean isRecordSetReadOnly() {
        boolean isReadOnly = true;
        ScreenModeCode screenMode = getPolicyHeader().getScreenModeCode();
        if (screenMode.isManualEntry() ||
            screenMode.isRenewWIP() ||
            screenMode.isOosWIP()||
            screenMode.isWIP()) {
            isReadOnly = false;
        }
        return isReadOnly;
    }

    public RenewalFlagEntitlementRecordLoadProcessor(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }

    public PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    public void setPolicyHeader(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }

    private PolicyHeader m_policyHeader;
    private static Record c_initialEntitlementValues;

    private static final String MANUAL_FLAG_TYPE = "MANUAL";
    private static final String AUTO_FLAG_TYPE = "AUTO";
}
