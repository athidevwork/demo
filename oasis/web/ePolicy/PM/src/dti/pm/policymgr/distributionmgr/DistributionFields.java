package dti.pm.policymgr.distributionmgr;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;

/**
 * Constant field class for process distribution
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 10, 2011
 *
 * @author wfu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 11/15/2013       xnie        142674 1) Added fields riskTypeCode, productCoverageCode, CALC_TRANS, and get/set
 *                                        methods.
 *                                     2) Added a new inner class ActionValues() and move static final String variables
 *                                        ACTION_CALC/ACTION_CALCWIP/ACTION_CALCDONE/ACTION_CALC_TRANS to it.
 * ---------------------------------------------------
 */
public class DistributionFields {
    public static final String DIVIDEND_RULE_ID = "dividendRuleId";
    public static final String EFFECTIVE_FROM_DATE = "effectiveFromDate";
    public static final String EFFECTIVE_TO_DATE = "effectiveToDate";
    public static final String SEQ_NO = "seqNo";
    public static final String ISSUE_COMPANY_ENTITY_ID = "issueCompanyEntityId";
    public static final String ISSUE_STATE_CODE = "issueStateCode";
    public static final String POLICY_TYPE_CODE = "policyTypeCode";
    public static final String TRANSACTION_CODE = "transactionCode";
    public static final String ACTION = "action";
    public static final String PERCENTAGE = "percentage";
    public static final String YEAR = "year";
    public static final String IS_PROCESS_AVAILABLE = "isProcessAvailable";
    public static final String DISTRI_SEARCH_DATE = "distriSearchDate";
    public static final String IS_SAVED = "isSaved";
    public static final String RISK_TYPE_CODE = "riskTypeCode";
    public static final String PRODUCT_COVERAGE_CODE = "productCoverageCode";

    public static void setDividendRuleId(Record record, String dividendRuleId) {
        record.setFieldValue(DIVIDEND_RULE_ID, dividendRuleId);
    }

    public static void setEffectiveFromDate(Record record, String effectiveFromDate) {
        record.setFieldValue(EFFECTIVE_FROM_DATE, effectiveFromDate);
    }

    public static void setEffectiveToDate(Record record, String effectiveToDate) {
        record.setFieldValue(EFFECTIVE_TO_DATE, effectiveToDate);
    }

    public static void setSeqNo(Record record, String seqNo) {
        record.setFieldValue(SEQ_NO, seqNo);
    }

    public static void setIssueCompanyEntityId(Record record, String issueCompanyEntityId) {
        record.setFieldValue(ISSUE_COMPANY_ENTITY_ID, issueCompanyEntityId);
    }

    public static void setIssueStateCode(Record record, String issueStateCode) {
        record.setFieldValue(ISSUE_STATE_CODE, issueStateCode);
    }

    public static void setPolicyTypeCode(Record record, String policyTypeCode) {
        record.setFieldValue(POLICY_TYPE_CODE, policyTypeCode);
    }

    public static void setTransactionCode(Record record, String transactionCode) {
        record.setFieldValue(TRANSACTION_CODE, transactionCode);
    }

    public static void setAction(Record record, String action) {
        record.setFieldValue(ACTION, action);
    }

    public static void setPercentage(Record record, String percentage) {
        record.setFieldValue(PERCENTAGE, percentage);
    }

    public static void setYear(Record record, String year) {
        record.setFieldValue(YEAR, year);
    }

    public static void setIsProcessAvailable(Record record, YesNoFlag isProcessAvailable) {
        record.setFieldValue(IS_PROCESS_AVAILABLE, isProcessAvailable);
    }

    public static void setDistriSearchDate(Record record, String distriSearchDate) {
        record.setFieldValue(DISTRI_SEARCH_DATE, distriSearchDate);
    }

    public static void setIsSaved(Record record, String isSaved) {
        record.setFieldValue(IS_SAVED, isSaved);
    }

    public static String getRiskTypeCode(Record record) {
        return record.getStringValue(RISK_TYPE_CODE);
    }

    public static void setRiskTypeCode(Record record, String riskTypeCode) {
        record.setFieldValue(RISK_TYPE_CODE, riskTypeCode);
    }

    public static String getProductCoverageCode(Record record) {
        return record.getStringValue(PRODUCT_COVERAGE_CODE);
    }

    public static void setProductCoverageCode(Record record, String productCoverageCode) {
        record.setFieldValue(PRODUCT_COVERAGE_CODE, productCoverageCode);
    }

    public static String getDividendRuleId(Record record) {
        return record.getStringValue(DIVIDEND_RULE_ID);
    }

    public static String getEffectiveFromDate(Record record) {
        return record.getStringValue(EFFECTIVE_FROM_DATE);
    }

    public static String getEffectiveToDate(Record record) {
        return record.getStringValue(EFFECTIVE_TO_DATE);
    }

    public static String getSeqNo(Record record) {
        return record.getStringValue(SEQ_NO);
    }

    public static String getIssueCompanyEntityId(Record record) {
        return record.getStringValue(ISSUE_COMPANY_ENTITY_ID);
    }

    public static String getIssueStateCode(Record record) {
        return record.getStringValue(ISSUE_STATE_CODE);
    }

    public static String getPolicyTypeCode(Record record) {
        return record.getStringValue(POLICY_TYPE_CODE);
    }

    public static String getTransactionCode(Record record) {
        return record.getStringValue(TRANSACTION_CODE);
    }

    public static String getAction(Record record) {
        return record.getStringValue(ACTION);
    }

    public static String getPercentage(Record record) {
        return record.getStringValue(PERCENTAGE);
    }

    public static String getYear(Record record) {
        return record.getStringValue(YEAR);
    }

    public static YesNoFlag getIsProcessAvailable(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(IS_PROCESS_AVAILABLE));
    }

    public static String getDistriSearchDate(Record record) {
        return record.getStringValue(DISTRI_SEARCH_DATE);
    }

    public static String getIsSaved(Record record) {
        return record.getStringValue(IS_SAVED);
    }

    public static class ActionValues {
        public static final String ACTION_CALC = "CALC";
        public static final String ACTION_CALCWIP = "CALCWIP";
        public static final String ACTION_CALCDONE = "CALCDONE";
        public static final String ACTION_CALC_TRANS = "CALC_TRANS";
    }
}
