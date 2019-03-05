package dti.pm.policymgr.mailingmgr;

import dti.oasis.recordset.Record;

/**
 * Action class for setting product mailing data.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 25, 2013
 *
 * @author AWU
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * ---------------------------------------------------
 */
public class ProductMailingFields {
    public static final String PRODUCT_MAILING_ID = "productMailingId";

    public static final String ISSUE_COMPANY_ID = "issueCompanyId";

    public static final String ISSUE_STATE_CODE = "issueStateCode";

    public static final String POLICY_TYPE_CODE = "policyTypeCode";

    public static final String RISK_TYPE_CODE = "riskTypeCode";

    public static final String COVERAGE_CODE = "coverageCode";

    public static final String COMPONENT_CODE = "componentCode";

    public static final String DAYS_BEFORE = "daysBefore";

    public static final String RATE_CHANGE = "rateChange";

    public static final String TRANSACTION_CODE = "transactionCode";

    public static final String EMAIL_ADDRESS = "emailAddress";

    public static final String MAILING_TYPE_CODE = "mailingTypeCode";

    public static final String SHORT_DESCRIPTION = "shortDescription";

    public static final String LONG_DESCRIPTION = "longDescription";

    public static final String ENDORSEMENT_CODE = "endorsementCode";

    public static final String EFFECTIVE_DATE = "effectiveFromDate";

    public static final String EXPIRATION_DATE = "effectiveToDate";

    //Set methods
    public static void setProductMailingId(Record record, String productMailingId) {
        record.setFieldValue(PRODUCT_MAILING_ID, productMailingId);
    }

    public static void setIssueCompanyId(Record record, String issueCompanyId) {
        record.setFieldValue(ISSUE_COMPANY_ID, issueCompanyId);
    }

    public static void setIssueStateCode(Record record, String issueStateCode) {
        record.setFieldValue(ISSUE_STATE_CODE, issueStateCode);
    }

    public static void setPolicyTypeCode(Record record, String policyTypeCode) {
        record.setFieldValue(POLICY_TYPE_CODE, policyTypeCode);
    }

    public static void setRiskTypeCode(Record record, String riskTypeCode) {
        record.setFieldValue(RISK_TYPE_CODE, riskTypeCode);
    }

    public static void setCoverageCode(Record record, String coverageCode) {
        record.setFieldValue(COVERAGE_CODE, coverageCode);
    }

    public static void setComponentCode(Record record, String componentCode) {
        record.setFieldValue(COMPONENT_CODE, componentCode);
    }

    public static void setDaysBefore(Record record, String daysBefore) {
        record.setFieldValue(DAYS_BEFORE, daysBefore);
    }

    public static void setRateChange(Record record, String rateChange) {
        record.setFieldValue(RATE_CHANGE, rateChange);
    }

    public static void setTransactionCode(Record record, String transactionCode) {
        record.setFieldValue(TRANSACTION_CODE, transactionCode);
    }

    public static void setEmailAddress(Record record, String emailAddress) {
        record.setFieldValue(EMAIL_ADDRESS, emailAddress);
    }

    public static void setMailingTypeCode(Record record, String mailingTypeCode) {
        record.setFieldValue(MAILING_TYPE_CODE, mailingTypeCode);
    }

    public static void setShortDescription(Record record, String shortDesc) {
        record.setFieldValue(SHORT_DESCRIPTION, shortDesc);
    }

    public static void setLongDescription(Record record, String longDesc) {
        record.setFieldValue(LONG_DESCRIPTION, longDesc);
    }

    public static void setEndorsementCode(Record record, String endorseCode) {
        record.setFieldValue(ENDORSEMENT_CODE, endorseCode);
    }

    public static void setEffectiveDate(Record record, String effectiveDate) {
        record.setFieldValue(EFFECTIVE_DATE, effectiveDate);
    }

    public static void setExpirationDate(Record record, String expirationDate) {
        record.setFieldValue(EXPIRATION_DATE, expirationDate);
    }

    //Get methods
    public static String getProductMailingId(Record record) {
        return record.getStringValue(PRODUCT_MAILING_ID);
    }

    public static String getIssueCompanyId(Record record) {
        return record.getStringValue(ISSUE_COMPANY_ID);
    }

    public static String getIssueStateCode(Record record) {
        return record.getStringValue(ISSUE_STATE_CODE);
    }

    public static String getPolicyTypeCode(Record record) {
        return record.getStringValue(POLICY_TYPE_CODE);
    }

    public static String getRiskTypeCode(Record record) {
        return record.getStringValue(RISK_TYPE_CODE);
    }

    public static String getCoverageCode(Record record) {
        return record.getStringValue(COVERAGE_CODE);
    }

    public static String getComponentCode(Record record) {
        return record.getStringValue(COMPONENT_CODE);
    }

    public static String getDaysBefore(Record record) {
        return record.getStringValue(DAYS_BEFORE);
    }

    public static String getRateChange(Record record) {
        return record.getStringValue(RATE_CHANGE);
    }

    public static String getTransactionCode(Record record) {
        return record.getStringValue(TRANSACTION_CODE);
    }

    public static String getEmailAddress(Record record) {
        return record.getStringValue(EMAIL_ADDRESS);
    }

    public static String getMailingTypeCode(Record record) {
        return record.getStringValue(MAILING_TYPE_CODE);
    }

    public static String getShortDescription(Record record) {
        return record.getStringValue(SHORT_DESCRIPTION);
    }

    public static String getLongDescription(Record record) {
        return record.getStringValue(LONG_DESCRIPTION);
    }

    public static String getEndorsementCode(Record record) {
        return record.getStringValue(ENDORSEMENT_CODE);
    }

    public static String getEffectiveDate(Record record) {
        return record.getStringValue(EFFECTIVE_DATE);
    }

    public static String getExpirationDate(Record record) {
        return record.getStringValue(EXPIRATION_DATE);
    }

}
