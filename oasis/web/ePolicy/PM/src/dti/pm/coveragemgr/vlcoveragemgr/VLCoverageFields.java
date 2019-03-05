package dti.pm.coveragemgr.vlcoveragemgr;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.pm.busobjs.PMStatusCode;
import dti.pm.busobjs.ScreenModeCode;

/**
 * Constant values for VL Coverage
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 7, 2008
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class VLCoverageFields {
    public static final String VL_SCREEN_MODE_CODE = "vlScreenModeCode";
    public static final String COMPANY_INSURED_B = "companyInsuredB";
    public static final String OFFICIAL_RECORD_ID = "officialRecordId";
    public static final String EFFECTIVE_FROM_DATE = "effectiveFromDate";
    public static final String EFFECTIVE_TO_DATE = "effectiveToDate";
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";
    public static final String COV_RELATED_ENTITY_ID = "covRelatedEntityId";
    public static final String RATING_BASIS = "ratingBasis";
    public static final String ENTITY_ID = "entityId";
    public static final String PRACTICE_STATE_CODE = "practiceStateCode";
    public static final String COUNTY_CODE_USED_TO_RATE = "countyCodeUsedToRate";
    public static final String RISK_CLS_USED_TO_RATE = "riskClsUsedToRate";
    public static final String IN_UPDATE_MODE = "inUpdateMode";
    public static final String CLOSING_TRANS_LOG_ID = "closingTransLogId";
    public static final String RISK_BASE_ID = "riskBaseId";
    public static final String RISK_ID = "riskId";
    public static final String RISK_NAME = "riskName";
    public static final String RISK_TYPE_CODE = "riskTypeCode";
    public static final String VL_COVERAGE_STATUS = "vlCoverageStatus";
    public static final String STATUS = "status";
    public static final String RISK_SUB_CLS_USED_TO_RATE = "riskSubClsUsedToRate";
    public static final String AFTER_IMAGE_RECORD_B = "afterImageRecordB";
    public static final String RISK_PROCESS_CODE = "riskProcessCode";
    public static final String VL_POLICY_NO = "vlPolicyNo";

    public static String getVlPolicyNo(Record record) {
        return record.getStringValue(VL_POLICY_NO);
    }

    public static void setVlPolicyNo(Record record, String vlPolicyNo) {
        record.setFieldValue(VL_POLICY_NO, vlPolicyNo);
    }

    public static String getRiskProcessCode(Record record) {
        return record.getStringValue(RISK_PROCESS_CODE);
    }

    public static void setRiskProcessCode(Record record, String riskProcessCode) {
        record.setFieldValue(RISK_PROCESS_CODE, riskProcessCode);
    }

    public static YesNoFlag getAfterImageRecordB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(AFTER_IMAGE_RECORD_B));
    }

    public static void setAfterImageRecordB(Record record, YesNoFlag afterImageRecordB) {
        record.setFieldValue(AFTER_IMAGE_RECORD_B, afterImageRecordB);
    }

    public static String getRiskSubClsUsedToRate(Record record) {
        return record.getStringValue(RISK_SUB_CLS_USED_TO_RATE);
    }

    public static void setRiskSubClsUsedToRate(Record record, String riskSubClsUsedToRate) {
        record.setFieldValue(RISK_SUB_CLS_USED_TO_RATE, riskSubClsUsedToRate);
    }

    public static boolean hasVlCoverageStatus(Record record) {
        return record.hasStringValue(VL_COVERAGE_STATUS);
    }

    public static PMStatusCode getVlCoverageStatus(Record record) {
        Object value = record.getFieldValue(VL_COVERAGE_STATUS);
        PMStatusCode result = null;
        if (value == null || value instanceof PMStatusCode) {
            result = (PMStatusCode) value;
        }
        else {
            result = PMStatusCode.getInstance(value.toString());
        }
        return result;
    }

    public static void setVlCoverageStatus(Record record, PMStatusCode coverageStatus) {
        record.setFieldValue(VL_COVERAGE_STATUS, coverageStatus);
    }

    public static boolean hasStatus(Record record) {
        return record.hasStringValue(STATUS);
    }

    public static PMStatusCode getStatus(Record record) {
        Object value = record.getFieldValue(STATUS);
        PMStatusCode result = null;
        if (value == null || value instanceof PMStatusCode) {
            result = (PMStatusCode) value;
        }
        else {
            result = PMStatusCode.getInstance(value.toString());
        }
        return result;
    }

    public static void setStatus(Record record, PMStatusCode status) {
        record.setFieldValue(STATUS, status);
    }

    public static String getRiskTypeCode(Record record) {
        return record.getStringValue(RISK_TYPE_CODE);
    }

    public static void setRiskTypeCode(Record record, String riskTypeCode) {
        record.setFieldValue(RISK_TYPE_CODE, riskTypeCode);
    }

    public static String getRiskName(Record record) {
        return record.getStringValue(RISK_NAME);
    }

    public static void setRiskName(Record record, String riskName) {
        record.setFieldValue(RISK_NAME, riskName);
    }

    public static String getRiskId(Record record) {
        return record.getStringValue(RISK_ID);
    }

    public static void setRiskId(Record record, String riskId) {
        record.setFieldValue(RISK_ID, riskId);
    }


    public static String getRiskBaseId(Record record) {
        return record.getStringValue(RISK_BASE_ID);
    }

    public static void setRiskBaseId(Record record, String riskBaseId) {
        record.setFieldValue(RISK_BASE_ID, riskBaseId);
    }

    public static String getClosingTransLogId(Record record) {
        return record.getStringValue(CLOSING_TRANS_LOG_ID);
    }

    public static void setClosingTransLogId(Record record, String closingTransLogId) {
        record.setFieldValue(CLOSING_TRANS_LOG_ID, closingTransLogId);
    }

    public static YesNoFlag getInUpdateMode(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(IN_UPDATE_MODE));
    }

    public static void setInUpdateMode(Record record, YesNoFlag inUpdateMode) {
        record.setFieldValue(IN_UPDATE_MODE, inUpdateMode);
    }

    public static String getPracticeStateCode(Record record) {
        return record.getStringValue(PRACTICE_STATE_CODE);
    }

    public static void setPracticeStateCode(Record record, String practiceStateCode) {
        record.setFieldValue(PRACTICE_STATE_CODE, practiceStateCode);
    }

    public static String getCountyCodeUsedToRate(Record record) {
        return record.getStringValue(COUNTY_CODE_USED_TO_RATE);
    }

    public static void setCountyCodeUsedToRate(Record record, String countyCodeUsedToRate) {
        record.setFieldValue(COUNTY_CODE_USED_TO_RATE, countyCodeUsedToRate);
    }

    public static String getRiskClsUsedToRate(Record record) {
        return record.getStringValue(RISK_CLS_USED_TO_RATE);
    }

    public static void setRiskClsUsedToRate(Record record, String riskClsUsedToRate) {
        record.setFieldValue(RISK_CLS_USED_TO_RATE, riskClsUsedToRate);
    }

    public static String getEntityId(Record record) {
        return record.getStringValue(ENTITY_ID);
    }

    public static void setEntityId(Record record, String entityId) {
        record.setFieldValue(ENTITY_ID, entityId);
    }


    public static String getRatingBasis(Record record) {
        return record.getStringValue(RATING_BASIS);
    }

    public static void setRatingBasis(Record record, String ratingBasis) {
        record.setFieldValue(RATING_BASIS, ratingBasis);
    }

    public static String getCovRelatedEntityId(Record record) {
        return record.getStringValue(COV_RELATED_ENTITY_ID);
    }

    public static void setCovRelatedEntityId(Record record, String covRelatedEntityId) {
        record.setFieldValue(COV_RELATED_ENTITY_ID, covRelatedEntityId);
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

    public static String getStartDate(Record record) {
        return record.getStringValue(START_DATE);
    }

    public static void setStartDate(Record record, String startDate) {
        record.setFieldValue(START_DATE, startDate);
    }

    public static String getEndDate(Record record) {
        return record.getStringValue(END_DATE);
    }

    public static void setEndDate(Record record, String endDate) {
        record.setFieldValue(END_DATE, endDate);
    }


    public static String getOfficialRecordId(Record record) {
        return record.getStringValue(OFFICIAL_RECORD_ID);
    }

    public static void setOfficialRecordId(Record record, String officialRecordId) {
        record.setFieldValue(OFFICIAL_RECORD_ID, officialRecordId);
    }

    public static YesNoFlag getCompanyInsuredB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(COMPANY_INSURED_B));
    }

    public static void setCompanyInsuredB(Record record, YesNoFlag companyInsuredB) {
        record.setFieldValue(COMPANY_INSURED_B, companyInsuredB);
    }

    public static ScreenModeCode getVLScreenModeCode(Record record) {
        Object value = record.getFieldValue(VL_SCREEN_MODE_CODE);
        ScreenModeCode result = null;
        if (value == null || value instanceof ScreenModeCode) {
            result = (ScreenModeCode) value;
        }
        else {
            result = ScreenModeCode.getInstance(value.toString());
        }
        return result;
    }

    public static void setVLScreenModeCode(Record record, ScreenModeCode screenModeCode) {
        record.setFieldValue(VL_SCREEN_MODE_CODE, screenModeCode);
    }
}
