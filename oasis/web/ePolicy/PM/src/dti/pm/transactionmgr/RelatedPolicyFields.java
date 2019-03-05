package dti.pm.transactionmgr;

import dti.oasis.recordset.Record;

/**
 * Constants for Related Policy.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   April 06, 2011
 *
 * @author ryzhao
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class RelatedPolicyFields {

    public static final String USER_ID = "userId";
    public static final String EFFECTIVE_FROM_DATE = "effectiveFromDate";
    public static final String EFFECTIVE_TO_DATE = "effectiveToDate";
    public static final String CURRENT_POLICY_NO = "currentPolicyNo";
    public static final String POLICY_HOLDER_NAME = "policyHolderName";
    public static final String TEXT1 = "text1";
    public static final String TEXT2 = "text2";

    public static String getUserId(Record record) {
        return record.getStringValue(USER_ID);
    }

    public static void setUserId(Record record, String userId) {
        record.setFieldValue(USER_ID, userId);
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

    public static String getCurrentPolicyNo(Record record) {
        return record.getStringValue(CURRENT_POLICY_NO);
    }

    public static void setCurrentPolicyNo(Record record, String currentPolicyNo) {
        record.setFieldValue(CURRENT_POLICY_NO, currentPolicyNo);
    }

    public static String getPolicyHolderName(Record record) {
        return record.getStringValue(POLICY_HOLDER_NAME);
    }

    public static void setPolicyHolderName(Record record, String policyHolderName) {
        record.setFieldValue(POLICY_HOLDER_NAME, policyHolderName);
    }

    public static String getText1(Record record) {
        return record.getStringValue(TEXT1);
    }

    public static void setText1(Record record, String text1) {
        record.setFieldValue(TEXT1, text1);
    }

    public static String getText2(Record record) {
        return record.getStringValue(TEXT2);
    }

    public static void setText2(Record record, String text2) {
        record.setFieldValue(TEXT2, text2);
    }
}