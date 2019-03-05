package dti.pm.riskmgr;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.PMStatusCode;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 2, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/24/2011       ryzhao      123576 - Change the type of toRateB from YesNoFlag to String. Customer can config the
 *                              values of toRateB dropdown list in lookup_code table with type "RISK_REL_RATE_CODE".
 *                              It might contain other options besides Yes/No options.
 * 03/27/2013       tcheng      142700 - Add field policyNoReadonly.
 * 11/25/2014       kxiang      158853 - Added fields RISK_NAME_GH and RISK_NAME_HREF to get href value in WebWB.
 * 09/11/2015       tzeng       164679 - Added fields RECORD_MODE_CODE.
 * 05.08/2017       xnie        180317 - Added ownerRiskTypeCode/relatedRiskTypeCode.
 * 07/17/2017       wrong       168374 - Added fields PCF_RISK_COUNTY_CODE, PCF_RISK_CLASS_CODE and ANNUAL_PCF_CHARGE.
 * ---------------------------------------------------
 */
public class RiskRelationFields {
    public static final String RISK_RELATION_ID = "riskRelationId";
    public static final String REVERSE = "reverse";
    public static final String RISK_RELATION_STATUS = "riskRelationStatus";
    public static final String CLOSING_TRANS_LOG_ID = "closingTransLogId";
    public static final String RISK_CLOSING_TRANS_LOG_ID = "riskClosingTransLogId";
    public static final String COVERAGE_CLOSING_TRANS_LOG_ID = "coverageClosingTransLogId";
    public static final String OFFICIAL_RECORD_ID = "officialRecordId";
    public static final String RISK_OFFICIAL_RECORD_ID = "riskOfficialRecordId";
    public static final String RISK_RECORD_MODE_CODE = "riskRecordModeCode";
    public static final String COVERAGE_OFFICIAL_RECORD_ID = "coverageOfficialRecordId";
    public static final String COVERAGE_RECORD_MODE_CODE = "coverageRecordModeCode";
    public static final String CURRENT_RISK_TYPE_CODE = "currentRiskTypeCode";
    public static final String COMPANY_INSURED = "companyInsured";
    public static final String COMPANY_INSURED_PI = "PI";
    public static final String COMPANY_INSURED_CI = "CI";
    public static final String COMPANY_INSURED_NI = "NI";
    public static final String TYPE_CODE = "typeCode";
    public static final String COUNT_OF_POLICY_INSURED = "countOfPolicyInsured";
    public static final String COUNT_OF_COMPANY_INSURED = "countOfCompanyInsured";
    public static final String COUNT_OF_NON_INSURED = "countOfNonInsured";
    public static final String COUNT_OF_ACT_POLICY_INSURED = "countOfActPolicyInsured";
    public static final String COUNT_OF_ACT_COMPANY_INSURED = "countOfActCompanyInsured";
    public static final String COUNT_OF_ACT_NON_INSURED = "countOfActNonInsured";
    public static final String COUNT_OF_CXL_POLICY_INSURED = "countOfCxlPolicyInsured";
    public static final String COUNT_OF_CXL_COMPANY_INSURED = "countOfCxlCompanyInsured";
    public static final String COUNT_OF_CXL_NON_INSURED = "countOfCxlNonInsured";
    public static final String ADD_NI_COVERAGE_B = "addNiCoverageB";
    public static final String RISK_PROCESS_CODE = "riskProcessCode";
    public static final String OVERRIDE_STATS_B = "overrideStatsB";
    public static final String RISK_REL_EFFECTIVE_FROM_DATE = "riskRelEffectiveFromDate";
    public static final String RISK_REL_EFFECTIVE_TO_DATE = "riskRelEffectiveToDate";
    public static final String PRACTICE_STATE_CODE = "practiceStateCode";
    public static final String OLD_PRACTICE_STATE_CODE = "oldPracticeStateCode";
    public static final String COUNTY_CODE_USED_TO_RATE = "countyCodeUsedToRate";
    public static final String OLD_COUNTY_CODE_USED_TO_RATE = "oldCountyCodeUsedToRate";
    public static final String RISK_CLASS_CODE = "riskClassCode"; // specialty
    public static final String OLD_RISK_CLASS_CODE = "oldRiskClassCode";
    public static final String RATING_BASIS = "ratingBasis"; // Annual Premium
    public static final String OLD_RATING_BASIS = "oldRatingBasis";
    public static final String RISK_RELATION_TYPE_CODE = "riskRelationTypeCode";
    public static final String RISK_PARENT_ID = "riskParentId";
    public static final String NI_RISK_TYPE_CODE = "niRiskTypeCode";
    public static final String NI_COVERAGE_LIMIT_CODE = "niCoverageLimitCode";
    public static final String NI_RETRO_DATE = "niRetroDate";
    public static final String NI_CURRENT_CARRIER_ID = "niCurrentCarrierId";
    public static final String OLD_NI_RISK_TYPE_CODE = "oldNiRiskTypeCode";
    public static final String OLD_NI_COVERAGE_LIMIT_CODE = "oldNiCoverageLimitCode";
    public static final String OLD_NI_RETRO_DATE = "oldNiRetroDate";
    public static final String OLD_NI_CURRENT_CARRIER_ID = "oldNiCurrentCarrierId";
    public static final String OLD_COVERAGE_ID = "oldCoverageId";
    public static final String NUMBER_OF_EMPLOYED_DOCTOR = "numberOfEmployedDoctor";
    public static final String TYPE = "type";
    public static final String ROW_STATUS = "rowStatus";
    public static final String OVERRIDE_RISK_BASE_ID = "overrideRiskBaseId";
    public static final String TO_RATE_B = "toRateB";
    public static final String RET_CAN_RISKS = "retCanRisks";
    public static final String CHILD_RISK_TYPE = "childRiskType";
    public static final String CHILD_POLICY_TYPE = "childPolicyType";

    public static final String RISK_BASE_ID = "riskBaseId";
    public static final String COUNTY = "county";
    public static final String RISK_CLASS = "riskClass";
    public static final String RISK_EFF = "riskEff";
    public static final String RISK_EXP = "riskExp";
    public static final String PRACTICE_STATE = "practiceState";
    public static final String SOURCE_RECORD_ID = "sourceRecordId";
    public static final String PROPERTY_RISK_ID = "propertyRiskId";
    public static final String RISK_COUNTY_CODE = "riskCountyCode";

    public static final String CHILD_POLICY_NO = "childPolicyNo";
    public static final String POLICY_NO_READONLY = "policyNoReadonly";

    public static final String RISK_NAME_GH = "riskName_GH";
    public static final String RISK_NAME_HREF = "riskNameHref";
    public static final String RECORD_MODE_CODE = "recordModeCode";
    public static final String OWNER_RISK_TYPE_CODE = "ownerRiskTypeCode";
    public static final String RELATED_RISK_TYPE_CODE = "relatedRiskTypeCode";
    public static final String PCF_RISK_COUNTY_CODE = "pcfRiskCountyCode";
    public static final String PCF_RISK_CLASS_CODE = "pcfRiskClassCode";
    public static final String ANNUAL_PCF_CHARGE = "annualPcfCharge";

    public static String getRiskRelationId(Record record) {
        return record.getStringValue(RISK_RELATION_ID);
    }

    public static void setRiskRelationId(Record record, String riskRelationId) {
        record.setFieldValue(RISK_RELATION_ID, riskRelationId);
    }

    public static String getReverse(Record record) {
        return record.getStringValue(REVERSE);
    }

    public static void setReverse(Record record, YesNoFlag reverse) {
        record.setFieldValue(REVERSE, reverse);
    }

    public static PMStatusCode getRiskRelationStatus(Record record) {
        Object value = record.getFieldValue(RISK_RELATION_STATUS);
        PMStatusCode result;
        if (value == null || value instanceof PMStatusCode) {
            result = (PMStatusCode) value;
        }
        else {
            result = PMStatusCode.getInstance(value.toString());
        }
        return result;
    }

    public static void setRiskRelationStatus(Record record, PMStatusCode riskRelStatus) {
        record.setFieldValue(RISK_RELATION_STATUS, riskRelStatus);
    }

    public static String getClosingTransLogId(Record record) {
        return record.getStringValue(CLOSING_TRANS_LOG_ID);
    }
    
    public static String getRiskClosingTransLogId(Record record) {
        return record.getStringValue(RISK_CLOSING_TRANS_LOG_ID);
    }

    public static String getCoverageClosingTransLogId(Record record) {
        return record.getStringValue(COVERAGE_CLOSING_TRANS_LOG_ID);
    }

    public static String getOfficialRecordId(Record record) {
        return record.getStringValue(OFFICIAL_RECORD_ID);
    }

    public static String getRiskOfficialRecordId(Record record) {
        return record.getStringValue(RISK_OFFICIAL_RECORD_ID);
    }

    public static RecordMode getRiskRecordModeCode(Record record) {
        Object value = record.getFieldValue(RISK_RECORD_MODE_CODE);
        RecordMode result;
        if (value == null || value instanceof RecordMode) {
            result = (RecordMode) value;
        }
        else {
            result = RecordMode.getInstance(value.toString());
        }
        return result;
    }

    public static String getCoverageOfficialRecordId(Record record) {
        return record.getStringValue(COVERAGE_OFFICIAL_RECORD_ID);
    }

    public static RecordMode getCoverageRecordModeCode(Record record) {
        Object value = record.getFieldValue(COVERAGE_RECORD_MODE_CODE);
        RecordMode result;
        if (value == null || value instanceof RecordMode) {
            result = (RecordMode) value;
        }
        else {
            result = RecordMode.getInstance(value.toString());
        }
        return result;
    }

    public static String getCompanyInsured(Record record) {
        return record.getStringValue(COMPANY_INSURED);
    }

    public static void setCompanyInsured(Record record, String companyInsured) {
        record.setFieldValue(COMPANY_INSURED, companyInsured);
    }

    public static void setTypeCode(Record record, String typeCode) {
        record.setFieldValue(TYPE_CODE, typeCode);
    }

    public static String getCurrentRiskTypeCode(Record record) {
        return record.getStringValue(CURRENT_RISK_TYPE_CODE);
    }

    public static void setCurrentRiskTypeCode(Record record, String currentRiskTypeCode) {
        record.setFieldValue(CURRENT_RISK_TYPE_CODE, currentRiskTypeCode);
    }

    public static void setCountOfPolicyInsured(Record record, String countOfPolicyInsured) {
        record.setFieldValue(COUNT_OF_POLICY_INSURED, countOfPolicyInsured);
    }

    public static void setCountOfCompanyInsured(Record record, String countOfCompanyInsured) {
        record.setFieldValue(COUNT_OF_COMPANY_INSURED, countOfCompanyInsured);
    }

    public static void setCountOfNonInsured(Record record, String countOfNonInsured) {
        record.setFieldValue(COUNT_OF_NON_INSURED, countOfNonInsured);
    }

    public static YesNoFlag getAddNiCoverageB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(ADD_NI_COVERAGE_B));
    }

    public static void setAddNiCoverageB(Record record, YesNoFlag addNiCoverageB) {
        record.setFieldValue(ADD_NI_COVERAGE_B, addNiCoverageB);
    }

    public static String getRiskProcessCode(Record record) {
        return record.getStringValue(RISK_PROCESS_CODE);
    }

    public static void setRiskProcessCode(Record record, String riskProcessCode) {
        record.setFieldValue(RISK_PROCESS_CODE, riskProcessCode);
    }

    public static YesNoFlag getOverrideStatsB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(OVERRIDE_STATS_B));
    }

    public static void setOverrideStateB(Record record, YesNoFlag overrideStateB) {
        record.setFieldValue(OVERRIDE_STATS_B, overrideStateB);
    }

    public static String getRiskRelEffectiveFromDate(Record record) {
        return record.getStringValue(RISK_REL_EFFECTIVE_FROM_DATE);
    }

    public static void setRiskRelEffectiveFromDate(Record record, String riskRelEffFromDate) {
        record.setFieldValue(RISK_REL_EFFECTIVE_FROM_DATE, riskRelEffFromDate);
    }

    public static String getRiskRelEffectiveToDate(Record record) {
        return record.getStringValue(RISK_REL_EFFECTIVE_TO_DATE);
    }

    public static void setRiskRelEffectiveToDate(Record record, String riskRelEffToDate) {
        record.setFieldValue(RISK_REL_EFFECTIVE_TO_DATE, riskRelEffToDate);
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

    public static void setCountyCodeUsedToRate(Record record, String countyCode) {
        record.setFieldValue(COUNTY_CODE_USED_TO_RATE, countyCode);
    }

    public static String getRiskClassCode(Record record) {
        return record.getStringValue(RISK_CLASS_CODE);
    }

    public static void setRiskClassCode (Record record, String riskClassCode) {
        record.setFieldValue(RISK_CLASS_CODE, riskClassCode);
    }

    public static void setOldPracticeStateCode(Record record, String oldPracticeStateCode) {
        record.setFieldValue(OLD_PRACTICE_STATE_CODE, oldPracticeStateCode);
    }

    public static void setOldCountyCodeUsedToRate(Record record, String oldCountyCodeUsedToRate) {
        record.setFieldValue(OLD_COUNTY_CODE_USED_TO_RATE, oldCountyCodeUsedToRate);
    }

    public static void setOldRiskClassCode(Record record, String oldRiskClassCode) {
        record.setFieldValue(OLD_RISK_CLASS_CODE, oldRiskClassCode);
    }

    public static String getRatingBasis(Record record) {
        return record.getStringValue(RATING_BASIS);
    }

    public static void setRatingBasis(Record record, String ratingBasis) {
        record.setFieldValue(RATING_BASIS, ratingBasis);
    }

    public static String getOldRatingBasis(Record record) {
        return record.getStringValue(OLD_RATING_BASIS);
    }

    public static boolean isPolicyInsured(Record record) {
        return getCompanyInsured(record).equals(COMPANY_INSURED_PI);
    }

    public static boolean isCompanyInsured(Record record) {
        return getCompanyInsured(record).equals(COMPANY_INSURED_CI);
    }

    public static boolean isNonInsured(Record record) {
        return getCompanyInsured(record).equals(COMPANY_INSURED_NI);
    }

    public static String getRiskRelationTypeCode(Record record) {
        return record.getStringValue(RISK_RELATION_TYPE_CODE);
    }

    public static void setRiskRelationTypeCode(Record record, String riskRelationTypeCode) {
        record.setFieldValue(RISK_RELATION_TYPE_CODE, riskRelationTypeCode);
    }

    public static String getRiskParentId(Record record) {
        return record.getStringValue(RISK_PARENT_ID);
    }

    public static void setRiskParentId(Record record, String riskParentId) {
        record.setFieldValue(RISK_PARENT_ID, riskParentId);
    }

    public static void setType(Record record, String type) {
        record.setFieldValue(TYPE, type);
    }

    public static void setRowStatus(Record record, String rowStatus) {
        record.setFieldValue(ROW_STATUS, rowStatus);
    }

    public static void setOverrideRiskBaseId(Record record, String overrideRiskBaseId) {
        record.setFieldValue(OVERRIDE_RISK_BASE_ID, overrideRiskBaseId);
    }

    public static String getToRateB(Record record) {
        return record.getStringValue(TO_RATE_B, "N");
    }

    public static void setToRateB(Record record, String toRateB) {
        record.setFieldValue(TO_RATE_B, toRateB);
    }

    public static void setRetCanRisks(Record record, String retCanRisks) {
        record.setFieldValue(RET_CAN_RISKS, retCanRisks);
    }

    public static void setChildRiskType(Record record, String childRiskType) {
        record.setFieldValue(CHILD_RISK_TYPE, childRiskType);
    }

    public static void setChildPolicyType(Record record, String childPolicyType) {
        record.setFieldValue(CHILD_POLICY_TYPE, childPolicyType);
    }

    public static String getRiskBaseId(Record record) {
        return record.getStringValue(RISK_BASE_ID);
    }

    public static String getCounty(Record record) {
        return record.getStringValue(COUNTY);
    }

    public static String getRiskClass(Record record) {
        return record.getStringValue(RISK_CLASS);
    }

    public static String getRiskEff(Record record) {
        return record.getStringValue(RISK_EFF);
    }

    public static String getRiskExp(Record record) {
        return record.getStringValue(RISK_EXP);
    }

    public static String getPracticeState(Record record) {
        return record.getStringValue(PRACTICE_STATE);
    }

    public static String getSourceRecordId(Record record) {
        return record.getStringValue(SOURCE_RECORD_ID);
    }

    public static void setPropertyRiskId(Record record, String propertyRiskId) {
        record.setFieldValue(PROPERTY_RISK_ID, propertyRiskId);
    }

    public static String getRiskCountyCode(Record record) {
        return record.getStringValue(RISK_COUNTY_CODE);
    }

    public static String getPcfRiskCountyCode(Record record) {
        return record.getStringValue(PCF_RISK_COUNTY_CODE);
    }

    public static String getPcfRiskClassCode(Record record) { return record.getStringValue(PCF_RISK_CLASS_CODE); }

    public static String getAnnualPcfCharge(Record record) { return record.getStringValue(ANNUAL_PCF_CHARGE); }

    public static void setRiskCountyCode(Record record, String riskCountyCode) {
        record.setFieldValue(RISK_COUNTY_CODE, riskCountyCode);
    }

    public static String getNiRiskTypeCode(Record record) {
        return record.getStringValue(NI_RISK_TYPE_CODE);
    }

    public static void setNiRiskTypeCode(Record record, String niRiskTypeCode) {
        record.setFieldValue(NI_RISK_TYPE_CODE, niRiskTypeCode);
    }

    public static String getNiRetroDate(Record record) {
        return record.getStringValue(NI_RETRO_DATE);
    }

    public static void setNiRetroDate(Record record, String niRetroDate) {
        record.setFieldValue(NI_RETRO_DATE, niRetroDate);
    }

    public static String getOldNiRiskTypeCode(Record record) {
        return record.getStringValue(OLD_NI_RISK_TYPE_CODE);
    }

    public static void setCountOfActPolicyInsured(Record record, String countOfActPolicyInsured) {
        record.setFieldValue(COUNT_OF_ACT_POLICY_INSURED, countOfActPolicyInsured);
    }

    public static void setCountOfActCompanyInsured(Record record, String countOfActCompanyInsured) {
        record.setFieldValue(COUNT_OF_ACT_COMPANY_INSURED, countOfActCompanyInsured);
    }

    public static void setCountOfActNonInsured(Record record, String countOfActNonInsured) {
        record.setFieldValue(COUNT_OF_ACT_NON_INSURED, countOfActNonInsured);
    }
    public static void setCountOfCxlPolicyInsured(Record record, String countOfCxlPolicyInsured) {
        record.setFieldValue(COUNT_OF_CXL_POLICY_INSURED, countOfCxlPolicyInsured);
    }

    public static void setCountOfCxlCompanyInsured(Record record, String countOfCxlCompanyInsured) {
        record.setFieldValue(COUNT_OF_CXL_COMPANY_INSURED, countOfCxlCompanyInsured);
    }

    public static void setCountOfCxlNonInsured(Record record, String countOfCxlNonInsured) {
        record.setFieldValue(COUNT_OF_CXL_NON_INSURED, countOfCxlNonInsured);
    }

    public static String getChildPolicyNo(Record record) {
        return record.getStringValue(CHILD_POLICY_NO);
    }

    public static void setChildPolicyNo(Record record, String childPolicyNo) {
        record.setFieldValue(CHILD_POLICY_NO, childPolicyNo);
    }

    public class RiskRelationTypeCodeValues {
        public static final String COMP_INSURED_RISK_TYPES = "COMP_INSURED_RISK_TYPES";
    }

    public static void setRiskNameHref(Record record, String riskNameHref) {
        record.setFieldValue(RISK_NAME_HREF, riskNameHref);
    }
    
    public static String getRiskNameHref(Record record) {
        return record.getStringValue(RISK_NAME_HREF);
    }

    public static String getOwnerRiskTypeCode(Record record) {
        return record.getStringValue(OWNER_RISK_TYPE_CODE);
    }

    public static void setOwnerRiskTypeCode(Record record, String ownerRiskTypeCode) {
        record.setFieldValue(OWNER_RISK_TYPE_CODE, ownerRiskTypeCode);
    }

    public static String getRelatedRiskTypeCode(Record record) {
        return record.getStringValue(RELATED_RISK_TYPE_CODE);
    }

    public static void setRelatedRiskTypeCode(Record record, String relatedRiskTypeCode) {
        record.setFieldValue(RELATED_RISK_TYPE_CODE, relatedRiskTypeCode);
    }

    public static void setPcfRiskCountyCode(Record record, String pcfRiskCountyCode) {
        record.setFieldValue(PCF_RISK_COUNTY_CODE, pcfRiskCountyCode);
    }

    public static void setPcfRiskClassCode(Record record, String pcfRiskClassCode) {
        record.setFieldValue(PCF_RISK_CLASS_CODE, pcfRiskClassCode);
    }

    public static void setAnnualPcfCharge(Record record, String annualPcfCharge) {
        record.setFieldValue(ANNUAL_PCF_CHARGE, annualPcfCharge);
    }
}
