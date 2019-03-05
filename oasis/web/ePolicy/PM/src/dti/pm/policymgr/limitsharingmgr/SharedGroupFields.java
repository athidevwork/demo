package dti.pm.policymgr.limitsharingmgr;

import dti.oasis.recordset.Record;

/**
 * Constant field class for reinsurance
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Dev 29, 2007
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 1        11/11/09 GCHITTA    Issue 100140 - Modified to add renewal indicator
 * 11/10/2011       xnie        125517 - Added shrgrpMasterId field and set method.
 * 08/24/2012       adeng       135972 - Added one more field "hasRisk" in order it can be set
 *                                       it into inputRecord to do further processing.
 * 08/31/2012       adeng       135972 - Roll backed changes,using new solution of hiding Limit Sharing button.
 * ---------------------------------------------------
 */
public class SharedGroupFields {
    public static final String POLICY_SHARED_GROUP_MASTER_ID = "policySharedGroupMasterId";
    public static final String POLICY_ID = "policyId";
    public static final String SHARE_GROUP_EFF_FROM_DATE = "shareGroupEffFromDate";
    public static final String SHARE_GROUP_EFF_TO_DATE = "shareGroupEffToDate";
    public static final String SHARE_GROUP_TRANS_LOG_ID = "shareGroupTransLogId";
    public static final String SHARE_GROUP_ACCT_FROM_DATE = "shareGroupAcctFromDate";
    public static final String SHARE_GROUP_NO = "shareGroupNo";
    public static final String SHARE_GROUP_POLICY_TYPE_CODE = "shareGroupPolicyTypeCode";
    public static final String SHARE_GROUP_RECORD_MODE_CODE = "shareGroupRecordModeCode";
    public static final String SHARE_LIMIT_B = "shareLimitB";
    public static final String SHARE_DEDUCT_B = "shareDeductB";
    public static final String SHARE_SIR_B = "shareSirB";
    public static final String SHARE_GROUP_DESC = "shareGroupDesc";
    public static final String RENEWAL_B = "renewalB";
    public static final String SHARE_MASTER_ID = "shrgrpMasterId";

    public static void setPolicySharedGroupMasterId(Record record, String policySharedGroupMasterId) {
        record.setFieldValue(POLICY_SHARED_GROUP_MASTER_ID, policySharedGroupMasterId);
    }

    public static String getPolicySharedGroupMasterId(Record record) {
        return record.getStringValue(POLICY_SHARED_GROUP_MASTER_ID);
    }

    public static void setPolicyId(Record record, String policyId) {
        record.setFieldValue(POLICY_ID, policyId);
    }

    public static String getPolicyId(Record record) {
        return record.getStringValue(POLICY_ID);
    }

    public static void setShareGroupEffFromDate(Record record, String shareGroupEffFromDate) {
        record.setFieldValue(SHARE_GROUP_EFF_FROM_DATE, shareGroupEffFromDate);
    }

    public static String getShareGroupEffFromDate(Record record) {
        return record.getStringValue(SHARE_GROUP_EFF_FROM_DATE);
    }

    public static void setShareGroupEffToDate(Record record, String shareGroupEffToDate) {
        record.setFieldValue(SHARE_GROUP_EFF_TO_DATE, shareGroupEffToDate);
    }

    public static String getShareGroupEffToDate(Record record) {
        return record.getStringValue(SHARE_GROUP_EFF_TO_DATE);
    }

    public static void setShareGroupTransLogId(Record record, String shareGroupTransLogId) {
        record.setFieldValue(SHARE_GROUP_TRANS_LOG_ID, shareGroupTransLogId);
    }

    public static String getShareGroupTransLogId(Record record) {
        return record.getStringValue(SHARE_GROUP_TRANS_LOG_ID);
    }

    public static void setShareGroupAcctFromDate(Record record, String shareGroupAcctFromDate) {
        record.setFieldValue(SHARE_GROUP_ACCT_FROM_DATE, shareGroupAcctFromDate);
    }

    public static String getShareGroupAcctFromDate(Record record) {
        return record.getStringValue(SHARE_GROUP_ACCT_FROM_DATE);
    }

    public static void setShareGroupNo(Record record, String shareGroupNo) {
        record.setFieldValue(SHARE_GROUP_NO, shareGroupNo);
    }

    public static String getShareGroupNo(Record record) {
        return record.getStringValue(SHARE_GROUP_NO);
    }

    public static void setShareGroupPolicyTypeCode(Record record, String shareGroupPolicyTypeCode) {
        record.setFieldValue(SHARE_GROUP_POLICY_TYPE_CODE, shareGroupPolicyTypeCode);
    }

    public static String getShareGroupPolicyTypeCode(Record record) {
        return record.getStringValue(SHARE_GROUP_POLICY_TYPE_CODE);
    }

    public static void setShareGroupRecordModeCode(Record record, String shareGroupRecordModeCode) {
        record.setFieldValue(SHARE_GROUP_RECORD_MODE_CODE, shareGroupRecordModeCode);
    }

    public static String getShareGroupRecordModeCode(Record record) {
        return record.getStringValue(SHARE_GROUP_RECORD_MODE_CODE);
    }

    public static void setShareLimitB(Record record, String shareLimitB) {
        record.setFieldValue(SHARE_LIMIT_B,shareLimitB);
    }

    public static String getShareLimitB(Record record){
        return record.getStringValue(SHARE_LIMIT_B);
    }

    public static void setShareDeductB(Record record, String shareDeductB) {
        record.setFieldValue(SHARE_DEDUCT_B,shareDeductB);
    }

    public static String getShareDeductB(Record record){
        return record.getStringValue(SHARE_DEDUCT_B);
    }

    public static void setShareSirB(Record record, String shareSirB) {
        record.setFieldValue(SHARE_SIR_B,shareSirB);
    }

    public static String getShareSirB(Record record){
        return record.getStringValue(SHARE_SIR_B);
    }

    public static String getShareGroupDesc(Record record){
        return record.getStringValue(SHARE_GROUP_DESC);
    }

    public static void setShareGroupDesc(Record record, String shareGroupDesc) {
        record.setFieldValue(SHARE_GROUP_DESC,shareGroupDesc);
    }

    public static String getRenewalB(Record record){
        return record.getStringValue(RENEWAL_B);
    }

    public static void setRenewalB(Record record, String renewalB) {
        record.setFieldValue(RENEWAL_B,renewalB);
    }

    public static void setShareMasterId(Record record, String shareMasterId ) {
        record.setFieldValue(SHARE_MASTER_ID,shareMasterId);
    }
}
