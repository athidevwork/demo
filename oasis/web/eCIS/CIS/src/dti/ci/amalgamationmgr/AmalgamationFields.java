package dti.ci.amalgamationmgr;

import dti.oasis.recordset.Record;

/**
 * Amalgamation fields.
 * 
 * <p>(C) 2009 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 19, 2009
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class AmalgamationFields {

    public static final String POLICY_AMALGAMATION_ID = "policyAmalgamationId";
    public static final String SOURCE_POLICY_NO = "sourcePolicyNo";
    public static final String SOURCE_RISK_BASE_RECORD_ID = "sourceRiskBaseRecordId";
    public static final String DEST_POLICY_NO = "destPolicyNo";
    public static final String DEST_RISK_BASE_RECORD_ID = "destRiskBaseRecordId";
    public static final String CLAIMS_ACCESS_INDICATOR = "claimsAccessIndicator";
    public static final String CODEAMALGAMATION_DATE = "amalgamationDate";
    public static final String AMALGAMATION_CODE = "amalgamationCode";
    public static final String AMALGAMATION_DATE = "amalgamationDate";

    public static final String MANUAL_B = "manualB";

    public static String getPolicyAmalgamationId(Record record) {
        return record.getStringValue(POLICY_AMALGAMATION_ID);
    }

    public static void setPolicyAmalgamationId(Record record, String policyAmalgamationId) {
        record.setFieldValue(POLICY_AMALGAMATION_ID, policyAmalgamationId);
    }

    public static String getSourcePolicyNo(Record record) {
        return record.getStringValue(SOURCE_POLICY_NO);
    }

    public static void setSourcePolicyNo(Record record, String sourcePolicyNo) {
        record.setFieldValue(SOURCE_POLICY_NO, sourcePolicyNo);
    }

    public static String getDestPolicyNo(Record record) {
        return record.getStringValue(DEST_POLICY_NO);
    }

    public static void setDestPolicyNo(Record record, String destPolicyNo) {
        record.setFieldValue(DEST_POLICY_NO, destPolicyNo);
    }

    public static String getSourceRiskBaseRecordId(Record record) {
        return record.getStringValue(SOURCE_RISK_BASE_RECORD_ID);
    }

    public static void setSourceRiskBaseRecordId(Record record, String sourceRiskBaseRecordId) {
        record.setFieldValue(SOURCE_RISK_BASE_RECORD_ID, sourceRiskBaseRecordId);
    }

    public static String getDestRiskBaseRecordId(Record record) {
        return record.getStringValue(DEST_RISK_BASE_RECORD_ID);
    }

    public static void setDestRiskBaseRecordId(Record record, String destRiskBaseRecordId) {
        record.setFieldValue(DEST_RISK_BASE_RECORD_ID, destRiskBaseRecordId);
    }

    public static String getAmalgamationDate(Record record) {
        return record.getStringValue(AMALGAMATION_DATE);
    }

    public static void setAmalgamationDate(Record record, String amalgamationDate) {
        record.setFieldValue(AMALGAMATION_DATE, amalgamationDate);
    }

    public static String getClaimsAccessIndicator(Record record) {
        return record.getStringValue(CLAIMS_ACCESS_INDICATOR);
    }

    public static void setClaimsAccessIndicator(Record record, String claimsAccessIndicator) {
        record.setFieldValue(CLAIMS_ACCESS_INDICATOR, claimsAccessIndicator);
    }

    public static String getAmalgamationCode(Record record) {
        return record.getStringValue(AMALGAMATION_CODE);
    }

    public static void setAmalgamationCode(Record record, String amalgamationCode) {
        record.setFieldValue(AMALGAMATION_CODE, amalgamationCode);
    }
}

