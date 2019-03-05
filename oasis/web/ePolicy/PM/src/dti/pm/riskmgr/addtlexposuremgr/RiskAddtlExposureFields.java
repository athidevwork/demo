package dti.pm.riskmgr.addtlexposuremgr;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;

/**
 * <p>(C) 2017 Delphi Technology, inc. (dti)</p>
 * Date:   May 23, 2017
 *
 * @author eyin
 */
/*
 *
 * Revision Date Revised By Description
 * ---------------------------------------------------
 * 09/21/2017    eyin       169483, Initial version.
 * ---------------------------------------------------
 */
public class RiskAddtlExposureFields {
    // fields for GH/href
    public static final String RISK_ADDTL_EXPOSURE_GH = "riskAddtlExposure_GH";
    public static final String RISK_ADDTL_EXPOSURE_NAME_HREF = "riskAddtlExposureHREF";

    // fields for grid/form
    public static final String RISK_BASE_RECORD_ID = "riskBaseRecordId";
    public static final String RISK_ENTITY_ID = "riskEntityId";
    public static final String RISK_STATUS = "riskStatus";
    public static final String RISK_ADDTL_EXPOSURE_ID = "riskAddtlExposureId";
    public static final String RISK_ADDTL_EXP_BASE_RECORD_ID = "riskAddtlExpBaseRecordId";
    public static final String EFFECTIVE_FROM_DATE = "effectiveFromDate";
    public static final String EFFECTIVE_TO_DATE = "effectiveToDate";
    public static final String ORIG_EFFECTIVE_TO_DATE = "origEffectiveToDate";
    public static final String PRACTICE_STATE_CODE = "practiceStateCode";
    public static final String RISK_COUNTY = "riskCounty";
    public static final String PERCENT_PRACTICE = "percentPractice";
    public static final String ORIG_PERCENT_PRACTICE = "OrigPercentPractice";
    public static final String COVERAGE_LIMIT_CODE = "coverageLimitCode";
    public static final String ORIG_COVERAGE_LIMIT_CODE = "OrigCoverageLimitCode";
    public static final String RISK_CLASS = "riskClass";
    public static final String BASE_RECORD_B = "baseRecordB";
    public static final String RISK_TYPE_CODE = "riskTypeCode";

    // fields for versions
    public static final String OFFICIAL_RECORD_ID = "officialRecordId";
    public static final String RECORD_MODE_CODE = "recordModeCode";
    public static final String CLOSING_TRANS_LOG_ID = "closingTransLogId";
    public static final String AFTER_IMAGE_RECORD_B = "afterImageRecordB";

    // fields for transaction
    public static final String TRANSACTION_CODE = "transactionCode";
    public static final String TRANS_EFFECTIVE_FROM_DATE = "transEffectiveFromDate";
    public static final String LATEST_TERM_EXP_DATE = "latestTermExpDate";

    // fields for page entitlement
    public static final String IS_DELETE_VISIBLE = "isDeleteVisible";

    // fields for validation
    public static final String STATUS_CODE = "statusCode";

    public static String getRiskAddtlExposureId(Record record) {
        return record.getStringValue(RISK_ADDTL_EXPOSURE_ID);
    }

    public static void setRiskAddtlExposureId(Record record, String riskAddtlExposureId) {
        record.setFieldValue(RISK_ADDTL_EXPOSURE_ID, riskAddtlExposureId);
    }

    public static String getRiskBaseRecordId(Record record) {
        return record.getStringValue(RISK_BASE_RECORD_ID);
    }

    public static void setRiskBaseRecordId(Record record, String riskBaseRecordId) {
        record.setFieldValue(RISK_BASE_RECORD_ID, riskBaseRecordId);
    }

    public static String getRiskEntityId(Record record) {
        return record.getStringValue(RISK_ENTITY_ID);
    }

    public static void setRiskEntityId(Record record, String riskEntityId) {
        record.setFieldValue(RISK_ENTITY_ID, riskEntityId);
    }

    public static String getRiskStatus(Record record) {
        return record.getStringValue(RISK_STATUS);
    }

    public static void setRiskStatus(Record record, String riskStatus) {
        record.setFieldValue(RISK_STATUS, riskStatus);
    }

    public static String getRiskAddtlExpBaseRecordId(Record record) {
        return record.getStringValue(RISK_ADDTL_EXP_BASE_RECORD_ID);
    }

    public static void setRiskAddtlExpBaseRecordId(Record record, String riskAddtlExposureBaseRecordId) {
        record.setFieldValue(RISK_ADDTL_EXP_BASE_RECORD_ID, riskAddtlExposureBaseRecordId);
    }

    public static String getEffectiveFromDate(Record record) {
        return record.getStringValue(EFFECTIVE_FROM_DATE);
    }

    public static void setEffectiveFromDate(Record record, String riskEffectiveFromDate) {
        record.setFieldValue(EFFECTIVE_FROM_DATE, riskEffectiveFromDate);
    }

    public static boolean hasEffectiveFromDate(Record record) {
        return record.hasStringValue(EFFECTIVE_FROM_DATE);
    }

    public static void setEffectiveToDate(Record record, String effectiveToDate) {
        record.setFieldValue(EFFECTIVE_TO_DATE, effectiveToDate);
    }

    public static String getEffectiveToDate(Record record) {
        return record.getStringValue(EFFECTIVE_TO_DATE);
    }

    public static boolean hasEffectiveToDate(Record record) {
        return record.hasStringValue(EFFECTIVE_TO_DATE);
    }

    public static void setOrigEffectiveToDate(Record record, String origEffectiveToDate) {
        record.setFieldValue(ORIG_EFFECTIVE_TO_DATE, origEffectiveToDate);
    }

    public static String getOrigEffectiveToDate(Record record) {
        return record.getStringValue(ORIG_EFFECTIVE_TO_DATE);
    }

    public static String getPracticeStateCode(Record record) {
        return record.getStringValue(PRACTICE_STATE_CODE);
    }

    public static void setPracticeStateCode(Record record, String practiceStateCode) {
        record.setFieldValue(PRACTICE_STATE_CODE, practiceStateCode);
    }

    public static String getRiskCounty(Record record) {
        return record.getStringValue(RISK_COUNTY);
    }

    public static void setRiskCounty(Record record, String riskCounty) {
        record.setFieldValue(RISK_COUNTY, riskCounty);
    }

    public static String getPercentPractice(Record record) {
        return record.getStringValue(PERCENT_PRACTICE);
    }

    public static void setPercentPractice(Record record, String percentPractice) {
        record.setFieldValue(PERCENT_PRACTICE, percentPractice);
    }

    public static String getOrigPercentPractice(Record record) {
        return record.getStringValue(ORIG_PERCENT_PRACTICE);
    }

    public static void setOrigPercentPractice(Record record, String OrigPercentPractice) {
        record.setFieldValue(ORIG_PERCENT_PRACTICE, OrigPercentPractice);
    }

    public static String getCoverageLimitCode(Record record) {
        return record.getStringValue(COVERAGE_LIMIT_CODE);
    }

    public static void setCoverageLimitCode(Record record, String coverageLimitCode) {
        record.setFieldValue(COVERAGE_LIMIT_CODE, coverageLimitCode);
    }

    public static String getOrigCoverageLimitCode(Record record) {
        return record.getStringValue(ORIG_COVERAGE_LIMIT_CODE);
    }

    public static void setOrigCoverageLimitCode(Record record, String OrigCoverageLimitCode) {
        record.setFieldValue(ORIG_COVERAGE_LIMIT_CODE, OrigCoverageLimitCode);
    }

    public static String getRiskClass(Record record) {
        return record.getStringValue(RISK_CLASS);
    }

    public static void setRiskClass(Record record, String riskClass) {
        record.setFieldValue(RISK_CLASS, riskClass);
    }

    public static String getOfficialRecordId(Record record) {
        return record.getStringValue(OFFICIAL_RECORD_ID);
    }

    public static void setOfficialRecordId(Record record, String officialRecordId) {
        record.setFieldValue(OFFICIAL_RECORD_ID, officialRecordId);
    }

    public static String getRecordModeCode(Record record) {
        return record.getStringValue(RECORD_MODE_CODE);
    }

    public static void setRecordModeCode(Record record, String recordModeCode) {
        record.setFieldValue(RECORD_MODE_CODE, recordModeCode);
    }

    public static String getClosingTransLogId(Record record) {
        return record.getStringValue(CLOSING_TRANS_LOG_ID);
    }

    public static void setClosingTransLogId(Record record, String closingTransLogId) {
        record.setFieldValue(CLOSING_TRANS_LOG_ID, closingTransLogId);
    }

    public static YesNoFlag getAfterImageRecordB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(AFTER_IMAGE_RECORD_B));
    }

    public static void setAfterImageRecordB(Record record, YesNoFlag afterImageRecordB) {
        record.setFieldValue(AFTER_IMAGE_RECORD_B, afterImageRecordB);
    }

    public static void setBaseRecordB(Record record, YesNoFlag baseRecordB) {
        record.setFieldValue(BASE_RECORD_B, baseRecordB);
    }

    public static void setRiskTypeCode(Record record, String riskTypeCode) {
        record.setFieldValue(RISK_TYPE_CODE, riskTypeCode);
    }

    public static void setTransactionCode(Record record, String transactionCode) {
        record.setFieldValue(TRANSACTION_CODE, transactionCode);
    }

    public static void setTransEffectiveFromDate(Record record, String transEffectiveFromDate) {
        record.setFieldValue(TRANS_EFFECTIVE_FROM_DATE, transEffectiveFromDate);
    }

    public static void setLatestTermExpDate(Record record, String latestTermExpDate) {
        record.setFieldValue(LATEST_TERM_EXP_DATE, latestTermExpDate);
    }
}
