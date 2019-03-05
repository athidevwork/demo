package dti.pm.coveragemgr.prioractmgr;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;

/**
 * Constants for Prior Act.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 29, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/10/2011       wqfu        Added CF_POLICY_ID,CF_RISK_BASE_ID,IS_COPY_AVAILABLE.
 * 06/13/2014       adeng       Added OFFICIAL,RECORD_MODE_CODE ,CLOSING_TRANS_LOG_ID.
 * ---------------------------------------------------
 */
public class PriorActFields {
    public static final String COVERAGE_EFFECTIVE_FROM_DATE = "coverageEffectiveFromDate";
    public static final String COVERAGE_EFFECTIVE_TO_DATE = "coverageEffectiveToDate";
    public static final String PRACTICE_STATE_CODE = "practiceStateCode";
    public static final String RISK_EFFECTIVE_FROM_DATE = "riskEffectiveFromDate";
    public static final String RISK_EFFECTIVE_TO_DATE = "riskEffectiveToDate";
    public static final String COVERAGE_LIMIT_CODE = "coverageLimitCode";

    public static final String RISK_START_DATE = "riskStartDate";
    public static final String RISK_EXPIRATION_DATE = "riskExpirationDate";
    public static final String COVERAGE_START_DATE = "coverageStartDate";
    public static final String COVERAGE_EFFECTIVE_DATE = "coverageEffectiveDate";
    public static final String PRIOR_COVERAGE_EXPIRATION_DATE = "priorCoverageExpirationDate";
    public static final String COVERAGE_RETRO_DATE = "coverageRetroDate";
    public static final String COVERAGE_BASE_RECORD_ID = "coverageBaseRecordId";

    public static final String MINIMAL_NOSE_DATE = "minimalNoseDate";
    public static final String RISK_COUNTY_CODE = "riskCountyCode";
    public static final String COMM_PRODUCT_COVERAGE_CODE = "commProductCoverageCode";
    public static final String SPECIALTY = "specialty";
    public static final String EXPOSURE_BASIS_CODE = "exposureBasisCode";

    public static final String CF_POLICY_ID = "cfPolicyId";
    public static final String CF_RISK_BASE_ID = "cfRiskBaseId";
    public static final String IS_COPY_AVAILABLE = "isCopyAvailable";
    public static final String OFFICIAL = "OFFICIAL";
    public static final String RECORD_MODE_CODE = "recordModeCode";
    public static final String CLOSING_TRANS_LOG_ID = "closingTransLogId";

    public static String getSpecialty(Record record) {
        return record.getStringValue(SPECIALTY);
    }

    public static void setSpecialty(Record record, String specialty) {
        record.setFieldValue(SPECIALTY, specialty);
    }

    public static String getCommProductCoverageCode(Record record) {
        return record.getStringValue(COMM_PRODUCT_COVERAGE_CODE);
    }

    public static void setCommProductCoverageCode(Record record, String commProductCoverageCode) {
        record.setFieldValue(COMM_PRODUCT_COVERAGE_CODE, commProductCoverageCode);
    }

    public static String getRiskCountyCode(Record record) {
        return record.getStringValue(RISK_COUNTY_CODE);
    }

    public static void setRiskCountyCode(Record record, String riskCountyCode) {
        record.setFieldValue(RISK_COUNTY_CODE, riskCountyCode);
    }

    public static String getRiskExpirationDate(Record record) {
        return record.getStringValue(RISK_EXPIRATION_DATE);
    }

    public static void setRiskExpirationDate(Record record, String riskExpirationDate) {
        record.setFieldValue(RISK_EXPIRATION_DATE, riskExpirationDate);
    }

    public static String getCoverageStartDate(Record record) {
        return record.getStringValue(COVERAGE_START_DATE);
    }

    public static void setCoverageStartDate(Record record, String coverageStartDate) {
        record.setFieldValue(COVERAGE_START_DATE, coverageStartDate);
    }

    public static String getCoverageEffectiveDate(Record record) {
        return record.getStringValue(COVERAGE_EFFECTIVE_DATE);
    }

    public static void setCoverageEffectiveDate(Record record, String coverageEffectiveDate) {
        record.setFieldValue(COVERAGE_EFFECTIVE_DATE, coverageEffectiveDate);
    }


    public static String getCoverageLimitCode(Record record) {
        return record.getStringValue(COVERAGE_LIMIT_CODE);
    }

    public static void setCoverageLimitCode(Record record, String coverageLimitCode) {
        record.setFieldValue(COVERAGE_LIMIT_CODE, coverageLimitCode);
    }

    public static String getRiskEffectiveFromDate(Record record) {
        return record.getStringValue(RISK_EFFECTIVE_FROM_DATE);
    }

    public static void setRiskEffectiveFromDate(Record record, String riskEffectiveFromDate) {
        record.setFieldValue(RISK_EFFECTIVE_FROM_DATE, riskEffectiveFromDate);
    }

    public static String getRiskEffectiveToDate(Record record) {
        return record.getStringValue(RISK_EFFECTIVE_TO_DATE);
    }

    public static void setRiskEffectiveToDate(Record record, String riskEffectiveToDate) {
        record.setFieldValue(RISK_EFFECTIVE_TO_DATE, riskEffectiveToDate);
    }

    public static String getMinimalNoseDate(Record record) {
        return record.getStringValue(MINIMAL_NOSE_DATE);
    }

    public static void setMinimalNoseDate(Record record, String minimalNoseDate) {
        record.setFieldValue(MINIMAL_NOSE_DATE, minimalNoseDate);
    }

    public static String getRiskStartDate(Record record) {
        return record.getStringValue(RISK_START_DATE);
    }

    public static void setRiskStartDate(Record record, String riskStartDate) {
        record.setFieldValue(RISK_START_DATE, riskStartDate);
    }

    public static String getPracticeStateCode(Record record) {
        return record.getStringValue(PRACTICE_STATE_CODE);
    }

    public static void setPracticeStateCode(Record record, String practiceStateCode) {
        record.setFieldValue(PRACTICE_STATE_CODE, practiceStateCode);
    }

    public static String getCoverageEffectiveFromDate(Record record) {
        return record.getStringValue(COVERAGE_EFFECTIVE_FROM_DATE);
    }

    public static void setCoverageEffectiveFromDate(Record record, String coverageEffectiveFromDate) {
        record.setFieldValue(COVERAGE_EFFECTIVE_FROM_DATE, coverageEffectiveFromDate);
    }

    public static void setCoverageEffectiveToDate(Record record, String coverageEffectiveToDate) {
        record.setFieldValue(COVERAGE_EFFECTIVE_TO_DATE, coverageEffectiveToDate);
    }

    public static String getCoverageEffectiveToDate(Record record) {
        return record.getStringValue(COVERAGE_EFFECTIVE_TO_DATE);
    }

    public static String getCoverageRetroDate(Record record) {
        return record.getStringValue(COVERAGE_RETRO_DATE);
    }

    public static void setCoverageRetroDate(Record record, String coverageRetroDate) {
        record.setFieldValue(COVERAGE_RETRO_DATE, coverageRetroDate);
    }

    public static String getExposureBasisCode(Record record) {
        return record.getStringValue(EXPOSURE_BASIS_CODE);
    }

    public static void setExposureBasisCode(Record record, String exposureBasisCode) {
        record.setFieldValue(EXPOSURE_BASIS_CODE, exposureBasisCode);
    }

    public static void setCfPolicyId(Record record, String cfPolicyId) {
        record.setFieldValue(CF_POLICY_ID, cfPolicyId);
    }

    public static String getCfPolicyId(Record record) {
        return record.getStringValue(CF_POLICY_ID);
    }
    
    public static void setCfRiskBaseId(Record record, String cfRiskBaseId) {
        record.setFieldValue(CF_RISK_BASE_ID, cfRiskBaseId);
    }

    public static String getCfRiskBaseId(Record record) {
        return record.getStringValue(CF_RISK_BASE_ID);
    }

    public static void setIsCopyAvailable(Record record, YesNoFlag isCopyAvailable) {
        record.setFieldValue(IS_COPY_AVAILABLE, isCopyAvailable);
    }

    public static YesNoFlag getIsCopyAvailable(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(IS_COPY_AVAILABLE));
    }

    public class ExposureCodeValues {
        public static final String PRIOR_CARR = "PRIOR_CARR";
    }

    public static String getPriorCoverageExpirationDate(Record record) {
        return record.getStringValue(PRIOR_COVERAGE_EXPIRATION_DATE);
    }

    public static void setPriorCoverageExpirationDate(Record record, String priorCoverageExpirationDate) {
        record.setFieldValue(PRIOR_COVERAGE_EXPIRATION_DATE, priorCoverageExpirationDate);
    }

    public static String getCoverageBaseRecordId(Record record) {
        return record.getStringValue(COVERAGE_BASE_RECORD_ID);
    }

    public static void setCoverageBaseRecordId(Record record, String coverageBaseRecordId) {
        record.setFieldValue(COVERAGE_BASE_RECORD_ID, coverageBaseRecordId);
    }

    public static String getClosingTransLogId(Record record) {
        return record.getStringValue(CLOSING_TRANS_LOG_ID);
    }
}
