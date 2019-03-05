package dti.pm.transactionmgr.premiumadjustmentprocessmgr;

import dti.oasis.recordset.Record;

/**
 * Constant field class for premium adjustment
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   July 9, 2008
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
public class PremiumAdjustmentFields {
    public static final String COMPONENT_TYPE_CODE = "componentTypeCode";
    public static final String SHORT_DESCRIPTION = "shortDescription";
    public static final String COMPONENT_VALUE = "componentValue";
    public static final String COMPONENT_SIGN = "componentSign";
    public static final String COVERAGE_BASE_RECORD_ID = "coverageBaseRecordId";

    public static final String RECORD_MODE_CODE = "recordModeCode";
    public static final String OFFICIAL_RECORD_ID = "officialRecordId";
    public static final String POLICY_COV_COMPONENT_ID = "policyCovComponentId";
    public static final String BASE_RECORD_B = "baseRecordB";
    public static final String PRODUCT_COVERAGE_CODE = "productCoverageCode";
    public static final String POL_COV_COMP_BASE_REC_ID = "polCovCompBaseRecId";
    public static final String TRANSACTION_LOG_ID = "transactionLogId";


    public static void setComponentTypeCode(Record record, String componentTypeCode) {
        record.setFieldValue(COMPONENT_TYPE_CODE, componentTypeCode);
    }

    public static String getComponentTypeCode(Record record) {
        return record.getStringValue(COMPONENT_TYPE_CODE);
    }

    public static void setShortDescription(Record record, String shortDescription) {
        record.setFieldValue(SHORT_DESCRIPTION, shortDescription);
    }

    public static String getShortDescription(Record record) {
        return record.getStringValue(SHORT_DESCRIPTION);
    }

    public static void setComponentValue(Record record, String componentValue) {
        record.setFieldValue(COMPONENT_VALUE, componentValue);
    }

    public static String getComponentValue(Record record) {
        return record.getStringValue(COMPONENT_VALUE);
    }

    public static void setComponentSign(Record record, String componentSign) {
        record.setFieldValue(COMPONENT_SIGN, componentSign);
    }

    public static String getComponentSign(Record record) {
        return record.getStringValue(COMPONENT_SIGN);
    }

    public static void setCoverageBaseRecordId(Record record, String coverageBaseRecordId) {
        record.setFieldValue(COVERAGE_BASE_RECORD_ID, coverageBaseRecordId);
    }

    public static String getCoverageBaseRecordId(Record record) {
        return record.getStringValue(COVERAGE_BASE_RECORD_ID);
    }

    public static void setProductCoverageCode(Record record, String productCoverageCode) {
        record.setFieldValue(PRODUCT_COVERAGE_CODE, productCoverageCode);
    }

    public static String getProductCoverageCode(Record record) {
        return record.getStringValue(PRODUCT_COVERAGE_CODE);
    }

    public static void setRecordModeCode(Record record, String recordModeCode) {
        record.setFieldValue(RECORD_MODE_CODE, recordModeCode);
    }

    public static String getRecordModeCode(Record record) {
        return record.getStringValue(RECORD_MODE_CODE);
    }

    public static void setOfficialRecordId(Record record, String officialRecordId) {
        record.setFieldValue(OFFICIAL_RECORD_ID, officialRecordId);
    }

    public static String getOfficialRecordId(Record record) {
        return record.getStringValue(OFFICIAL_RECORD_ID);
    }

    public static void setPolicyCovComponentId(Record record, String policyCovComponentId) {
        record.setFieldValue(POLICY_COV_COMPONENT_ID, policyCovComponentId);
    }

    public static String getPolicyCovComponentId(Record record) {
        return record.getStringValue(POLICY_COV_COMPONENT_ID);
    }

    public static void setBaseRecordB(Record record, String baseRecordB) {
        record.setFieldValue(BASE_RECORD_B, baseRecordB);
    }

    public static String getBaseRecordB(Record record) {
        return record.getStringValue(BASE_RECORD_B);
    }

    public static void setPolCovCompBaseRecId(Record record, String polCovCompBaseRecId) {
        record.setFieldValue(POL_COV_COMP_BASE_REC_ID, polCovCompBaseRecId);
    }

    public static String getPolCovCompBaseRecId(Record record) {
        return record.getStringValue(POL_COV_COMP_BASE_REC_ID);
    }

    public static void setTransactionLogId(Record record, String transactionLogId) {
        record.setFieldValue(TRANSACTION_LOG_ID, transactionLogId);
    }

    public static String getTransactionLogId(Record record) {
        return record.getStringValue(TRANSACTION_LOG_ID);
    }

}
