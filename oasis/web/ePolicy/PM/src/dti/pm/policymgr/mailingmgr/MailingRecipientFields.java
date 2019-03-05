package dti.pm.policymgr.mailingmgr;

import dti.oasis.recordset.Record;

/**
 * Constant field class for mailing recipient
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 20, 2007
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class MailingRecipientFields {

    public static final String POLICY_MAILING_DTL_ID = "policyMailingDtlId";
    public static final String POLICY_MAILING_ID = "policyMailingId";
    public static final String POLICY_ID = "policyId";
    public static final String RISK_BASE_RECORD_ID = "riskBaseRecordId";
    public static final String GENERATE_DATE = "generateDate";
    public static final String RECEIVED_DATE = "receivedDate";
    public static final String RECEIVED_B = "receivedB";


    public static void setPolicyMailingDtlId(Record record, String policyMailingDtlId) {
        record.setFieldValue(POLICY_MAILING_DTL_ID, policyMailingDtlId);
    }

    public static String getPolicyMailingDtlId(Record record) {
        return record.getStringValue(POLICY_MAILING_DTL_ID);
    }

    public static void setPolicyMailingId(Record record, String policyMailingId) {
        record.setFieldValue(POLICY_MAILING_ID, policyMailingId);
    }

    public static String getPolicyMailingId(Record record) {
        return record.getStringValue(POLICY_MAILING_ID);
    }

    public static void setPolicyId(Record record, String policyId) {
        record.setFieldValue(POLICY_ID, policyId);
    }

    public static String getPolicyId(Record record) {
        return record.getStringValue(POLICY_ID);
    }


    public static void setRiskBaseRecordId(Record record, String riskBaseRecordId) {
        record.setFieldValue(RISK_BASE_RECORD_ID, riskBaseRecordId);
    }

    public static String getRiskBaseRecordId(Record record) {
        return record.getStringValue(RISK_BASE_RECORD_ID);
    }

    public static void setGenerateDate(Record record, String generateDate) {
        record.setFieldValue(GENERATE_DATE, generateDate);
    }

    public static String getGenerateDate(Record record) {
        return record.getStringValue(GENERATE_DATE);
    }

    public static void setReceivedDate(Record record, String receivedDate) {
        record.setFieldValue(RECEIVED_DATE, receivedDate);
    }

    public static String getReceivedDate(Record record) {
        return record.getStringValue(RECEIVED_DATE);
    }

    public static void setReceivedB(Record record, String receivedB) {
        record.setFieldValue(RECEIVED_B, receivedB);
    }

    public static String getReceivedB(Record record) {
        return record.getStringValue(RECEIVED_B);
    }

}
