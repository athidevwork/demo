package dti.pm.policymgr.renewalflagmgr;

import dti.oasis.recordset.Record;

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
 * 03/02/2016       tzeng       167532 - Initial version.
 * ---------------------------------------------------
 */

public class RenewalFlagFields {

    // fields for version
    public static final String RECORD_MODE_CODE = "recordModeCode";
    public static final String EFFECTIVE_FROM_DATE = "effectiveFromDate";
    public static final String EFFECTIVE_TO_DATE = "effectiveToDate";
    public static final String RISK_BASE_RECORD_ID = "riskBaseRecordId";
    public static final String CLOSING_TRANS_LOG_ID = "closingTransLogId";

    // fields for business
    public static final String POLICY_RENEWAL_FLAG_ID = "policyRenewalFlagId";
    public static final String FLAG_TYPE = "flagType";

    // fields for entitlement
    public static final String IS_FLAG_NAME_EDITABLE = "isFlagNameEditable";
    public static final String IS_RISK_NAME_EDITABLE = "isRiskNameEditable";
    public static final String IS_FLAG_MANUAL_DESC_VISIBLE = "isFlagManualDescVisible";
    public static final String IS_RENEW_B_VISIBLE = "isRenewBVisible";

    public static String getRecordModeCode(Record record) {
        return record.getStringValue(RECORD_MODE_CODE);
    }

    public static void setRecordModeCode(Record record, String recordModeCode) {
        record.setFieldValue(RECORD_MODE_CODE, recordModeCode);
    }

    public static String getEffectiveFromDate(Record record) {
        return record.getStringValue(EFFECTIVE_FROM_DATE);
    }

    public static void setEffectiveFromDate(Record record, String effectiveFromDate) {
        record.setFieldValue(EFFECTIVE_FROM_DATE, effectiveFromDate);
    }

    public static String getEffectiveToDate(Record record) {
        return record.getStringValue(EFFECTIVE_TO_DATE);
    }

    public static void setEffectiveToDate(Record record, String effectiveToDate) {
        record.setFieldValue(EFFECTIVE_TO_DATE, effectiveToDate);
    }

    public static String getRiskBaseRecordId(Record record) {
        return record.getStringValue(RISK_BASE_RECORD_ID);
    }

    public static void setRiskBaseRecordId(Record record, String riskBaseRecordId) {
        record.setFieldValue(RISK_BASE_RECORD_ID, riskBaseRecordId);
    }

    public static String getClosingTransLogId(Record record) {
        return record.getStringValue(CLOSING_TRANS_LOG_ID);
    }

    public static void setClosingTransLogId(Record record, String closingTransLogId) {
        record.setFieldValue(CLOSING_TRANS_LOG_ID, closingTransLogId);
    }

    public static String getPolicyRenewalFlagId(Record record) {
        return record.getStringValue(POLICY_RENEWAL_FLAG_ID);
    }

    public static void setPolicyRenewalFlagId(Record record, String policyRenewalFlagId) {
        record.setFieldValue(POLICY_RENEWAL_FLAG_ID, policyRenewalFlagId);
    }

    public static String getFlagType(Record record) {
        return record.getStringValue(FLAG_TYPE);
    }

    public static void setFlagType(Record record, String flagType) {
        record.setFieldValue(FLAG_TYPE, flagType);
    }

}
