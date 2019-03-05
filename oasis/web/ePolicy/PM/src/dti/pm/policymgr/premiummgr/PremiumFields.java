package dti.pm.policymgr.premiummgr;

import dti.oasis.recordset.Record;

/**
 * Constants for Premium
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   July 11, 2007
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/04/2011       ryzhao      118806 - Add non premium fields. 
 * 01/24/2013       awu         140288 - Add productCoverageCode and coverageComponentCode
 * 02/07/2013       tcheng      141447 - Add a field IS_IN_WORKFLOW for auto view premium.
 * 07/19/2013       adeng       146439 - Add field deltaB with setter and getter method.
 * ---------------------------------------------------
 */
public class PremiumFields {
    public static final String HAS_PREM_DATA_FOR_TRANSACTION = "hasPremDataForTransaction";
    public static final String HAS_RATING_LOG_DATA_FOR_TRANSACTION = "hasRatingLogDataForTransaction";
    public static final String HAS_MEMBER_PREM_CONTRIBUTION = "hasMemberPremContribution";
    public static final String HAS_LAYER_DETIAL = "hasLayerDetail";
    public static final String IS_ROW_ELIGIBLE_FOR_VIEW_LAYER = "isRowEligibleForViewLayer";
    public static final String IS_ROW_ELIGIBLE_FOR_VIEW_MEM_CONT = "isRowEligibleForViewMemCont";
    public static final String IS_IN_WORKFLOW = "isInWorkflow";

    // Non premium fields
    public static final String RECORD_ID = "recordId";
    public static final String RISK_ID = "riskId";
    public static final String COVERAGE_ID = "coverageId";
    public static final String TRANSACTION_ID = "transactionId";
    public static final String DETAIL_TYPE = "detailType";
    public static final String PARENT_COVG_ID = "parentCovgId";
    public static final String COMPONENT_CODE = "componentCode";
    public static final String WRITTEN_PREMIUM = "writtenPremium";
    public static final String DELTA_AMOUNT = "deltaAmount";
    public static final String COVERAGE_AMOUNT_TOTAL = "coverageAmountTotal";
    public static final String COVERAGE_DELTA_TOTAL = "coverageDeltaTotal";
    public static final String RISK_AMOUNT_TOTAL = "riskAmountTotal";
    public static final String RISK_DELTA_TOTAL = "riskDeltaTotal";
    public static final String TRANS_AMOUNT_TOTAL = "transAmountTotal";
    public static final String TRANS_DELTA_TOTAL = "transDeltaTotal";
    public static final String PRODUCT_COVERAGE_CODE = "productCoverageCode";
    public static final String COVERAGE_COMPONENT_CODE = "coverageComponentCode";
    public static final String DELTA_B = "deltaB";

    public static void setRecordId(Record record, String recordId) {
        record.setFieldValue(RECORD_ID, recordId);
    }

    public static String getRecordId(Record record) {
        return record.getStringValue(RECORD_ID);
    }

    public static void setRiskId(Record record, String riskId) {
        record.setFieldValue(RISK_ID, riskId);
    }

    public static String getRiskId(Record record) {
        return record.getStringValue(RISK_ID);
    }

    public static void setCoverageId(Record record, String coverageId) {
        record.setFieldValue(COVERAGE_ID, coverageId);
    }

    public static String getCoverageId(Record record) {
        return record.getStringValue(COVERAGE_ID);
    }

    public static void setTransactionId(Record record, String transactionId) {
        record.setFieldValue(TRANSACTION_ID, transactionId);
    }

    public static String getTransactionId(Record record) {
        return record.getStringValue(TRANSACTION_ID, "");
    }

    public static void setDetailType(Record record, String detailType) {
        record.setFieldValue(DETAIL_TYPE, detailType);
    }

    public static String getDetailType(Record record) {
        return record.getStringValue(DETAIL_TYPE, "");
    }

    public static void setParentCovgId(Record record, String parentCovgId) {
        record.setFieldValue(PARENT_COVG_ID, parentCovgId);
    }

    public static String getParentCovgId(Record record) {
        return record.getStringValue(PARENT_COVG_ID);
    }

    public static void setComponentCode(Record record, String componentCode) {
        record.setFieldValue(COMPONENT_CODE, componentCode);
    }

    public static String getComponentCode(Record record) {
        return record.getStringValue(COMPONENT_CODE);
    }

    public static void setWrittenPremium(Record record, String writtenPremium) {
        record.setFieldValue(WRITTEN_PREMIUM, writtenPremium);
    }

    public static String getWrittenPremium(Record record) {
        return record.getStringValue(WRITTEN_PREMIUM);
    }

    public static void setDeltaAmount(Record record, String deltaAmount) {
        record.setFieldValue(DELTA_AMOUNT, deltaAmount);
    }

    public static String getDeltaAmount(Record record) {
        return record.getStringValue(DELTA_AMOUNT);
    }

    public static void setCoverageAmountTotal(Record record, String coverageAmountTotal) {
        record.setFieldValue(COVERAGE_AMOUNT_TOTAL, coverageAmountTotal);
    }

    public static String getCoverageAmountTotal(Record record) {
        return record.getStringValue(COVERAGE_AMOUNT_TOTAL);
    }

    public static void setCoverageDeltaTotal(Record record, String coverageDeltaTotal) {
        record.setFieldValue(COVERAGE_DELTA_TOTAL, coverageDeltaTotal);
    }

    public static String getCoverageDeltaTotal(Record record) {
        return record.getStringValue(COVERAGE_DELTA_TOTAL);
    }

    public static void setRiskAmountTotal(Record record, String riskAmountTotal) {
        record.setFieldValue(RISK_AMOUNT_TOTAL, riskAmountTotal);
    }

    public static String getRiskAmountTotal(Record record) {
        return record.getStringValue(RISK_AMOUNT_TOTAL);
    }

    public static void setRiskDeltaTotal(Record record, String riskDeltaTotal) {
        record.setFieldValue(RISK_DELTA_TOTAL, riskDeltaTotal);
    }

    public static String getRiskDeltaTotal(Record record) {
        return record.getStringValue(RISK_DELTA_TOTAL);
    }

    public static void setTransAmountTotal(Record record, String transAmountTotal) {
        record.setFieldValue(TRANS_AMOUNT_TOTAL, transAmountTotal);
    }

    public static String getTransAmountTotal(Record record) {
        return record.getStringValue(TRANS_AMOUNT_TOTAL);
    }

    public static void setTransDeltaTotal(Record record, String transDeltaTotal) {
        record.setFieldValue(TRANS_DELTA_TOTAL, transDeltaTotal);
    }

    public static String getTransDeltaTotal(Record record) {
        return record.getStringValue(TRANS_DELTA_TOTAL);
    }

    public static void setProductCoverageCode(Record record, String productCoverageCode) {
        record.setFieldValue(PRODUCT_COVERAGE_CODE, productCoverageCode);
    }

    public static String getProductCoverageCode(Record record) {
        return record.getStringValue(PRODUCT_COVERAGE_CODE);
    }

    public static void setCoverageComponentCode(Record record, String coverageComponentCode) {
        record.setFieldValue(COVERAGE_COMPONENT_CODE, coverageComponentCode);
    }

    public static String getCoverageComponentCode(Record record) {
        return record.getStringValue(COVERAGE_COMPONENT_CODE);
    }
    public static void setDeltaB(Record record, String deltaB) {
        record.setFieldValue(DELTA_B, deltaB);
    }

    public static String getDeltaB(Record record) {
        return record.getStringValue(DELTA_B);
    }

    public class DetailTypeCodeValues {
        public static final String TAX = "TAX";
        public static final String FEE_SRCHG = "FEE/SRCHG";
        public static final String ALL = "ALL";
    }
}
