package dti.pm.policymgr.underlyingpolicymgr;

import dti.oasis.recordset.Record;
import dti.oasis.busobjs.YesNoFlag;

/**
 * Constants for Underlying policy
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 3, 2008
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/1/2015        tzeng       165794 - Add renewB, groupNo, subGroupCode, limitValue1, limitValue1Code, limitValue2,
 *                                       limitValue2Code, limitValue3, limitValue3Code, recordModeCode, transactionLogId
 *                                       , closingTransLogId, officialRecordId, afterImageRecordB
 * 08/25/2016       ssheng      178365 - Add covPartCoverageCodeReadOnlyB and effectiveFromDateReadOnlyB.
 * 09/09/2016       xnie        178813 - Added ORIG_EFFECTIVE_TO_DATE and get/set methods.
 * ---------------------------------------------------
 */
public class UnderlyingPolicyFields {
    public static final String EFFECTIVE_FROM_DATE = "effectiveFromDate";
    public static final String EFFECTIVE_TO_DATE = "effectiveToDate";
    public static final String POLICY_UNDER_POL_ID = "policyUnderPolId";
    public static final String POLICY_UNDER_POL_NO = "policyUnderPolNo";
    public static final String UNDER_POLICY_TYPE_CODE = "underPolicyTypeCode";

    public static final String COV_PART_COVERAGE_CODE = "covPartCoverageCode";
    public static final String POLICY_FORM_CODE = "policyFormCode";
    public static final String COVERAGE_LIMIT_CODE = "coverageLimitCode";
    public static final String RETROACTIVE_DATE = "retroactiveDate";
    public static final String COMPANY_INSURED_B = "companyInsuredB";
    public static final String OUTPUT_B = "outputB";
    public static final String POLICY_NO_READ_ONLY_B = "policyNoReadOnlyB";
    public static final String COV_PART_COVERAGE_CODE_READ_ONLY_B = "covPartCoverageCodeReadOnlyB";
    public static final String EFFECTIVE_FROM_DATE_READ_ONLY_B = "effectiveFromDateReadOnlyB";
    public static final String POLICY_UNDERLYING_INFO_ID = "policyUnderlyingInfoId";
    public static final String UNDER_ISS_COMP_ENT_ID = "underIssCompEntId";
    public static final String RENEW_B = "renewB";
    public static final String GROUP_NO = "groupNo";
    public static final String SUB_GROUP_CODE = "subGroupCode";
    public static final String LIMIT_VALUE1 = "limitValue1";
    public static final String LIMIT_VALUE1_CODE = "limitValue1Code";
    public static final String LIMIT_VALUE2 = "limitValue2";
    public static final String LIMIT_VALUE2_CODE = "limitValue2Code";
    public static final String LIMIT_VALUE3 = "limitValue3";
    public static final String LIMIT_VALUE3_CODE = "limitValue3Code";
    public static final String RECORD_MODE_CODE = "recordModeCode";
    public static final String TRANSACTION_LOG_ID = "transactionLogId";
    public static final String CLOSING_TRANS_LOG_ID = "closingTransLogId";
    public static final String OFFICIAL_RECORD_ID = "officialRecordId";
    public static final String AFTER_IMAGE_RECORD_B = "afterImageRecordB";
    public static final String ORIG_EFFECTIVE_TO_DATE = "origEffectiveToDate";

    public static String getUnderIssCompEntId(Record record) {
        return record.getStringValue(UNDER_ISS_COMP_ENT_ID);
    }

    public static void setUnderIssCompEntId(Record record, String underIssCompEntId) {
        record.setFieldValue(UNDER_ISS_COMP_ENT_ID, underIssCompEntId);
    }

    public static String getPolicyUnderlyingInfoId(Record record) {
        return record.getStringValue(POLICY_UNDERLYING_INFO_ID);
    }

    public static void setPolicyUnderlyingInfoId(Record record, String policyUnderlyingInfoId) {
        record.setFieldValue(POLICY_UNDERLYING_INFO_ID, policyUnderlyingInfoId);
    }

    public static String getPolicyUnderPolId(Record record) {
        return record.getStringValue(POLICY_UNDER_POL_ID);
    }

    public static void setPolicyUnderPolId(Record record, String policyUnderPolId) {
        record.setFieldValue(POLICY_UNDER_POL_ID, policyUnderPolId);
    }

    public static String getPolicyUnderPolNo(Record record) {
        return record.getStringValue(POLICY_UNDER_POL_NO);
    }

    public static void setPolicyUnderPolNo(Record record, String policyUnderPolNo) {
        record.setFieldValue(POLICY_UNDER_POL_NO, policyUnderPolNo);
    }

    public static String getUnderPolicyTypeCode(Record record) {
        return record.getStringValue(UNDER_POLICY_TYPE_CODE);
    }

    public static void setUnderPolicyTypeCode(Record record, String policyTypeCode) {
        record.setFieldValue(UNDER_POLICY_TYPE_CODE, policyTypeCode);
    }


    public static String getCovPartCoverageCode(Record record) {
        return record.getStringValue(COV_PART_COVERAGE_CODE);
    }

    public static void setCovPartCoverageCode(Record record, String covPartCoverageCode) {
        record.setFieldValue(COV_PART_COVERAGE_CODE, covPartCoverageCode);
    }

    public static String getPolicyFormCode(Record record) {
        return record.getStringValue(POLICY_FORM_CODE);
    }

    public static void setPolicyFormCode(Record record, String policyFormCode) {
        record.setFieldValue(POLICY_FORM_CODE, policyFormCode);
    }

    public static String getCoverageLimitCode(Record record) {
        return record.getStringValue(COVERAGE_LIMIT_CODE);
    }

    public static void setCoverageLimitCode(Record record, String coverageLimitCode) {
        record.setFieldValue(COVERAGE_LIMIT_CODE, coverageLimitCode);
    }

    public static String getRetroactiveDate(Record record) {
        return record.getStringValue(RETROACTIVE_DATE);
    }

    public static void setRetroactiveDate(Record record, String retroactiveDate) {
        record.setFieldValue(RETROACTIVE_DATE, retroactiveDate);
    }

    public static YesNoFlag getCompanyInsuredB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(COMPANY_INSURED_B));
    }

    public static void setCompanyInsuredB(Record record, YesNoFlag companyInsuredB) {
        record.setFieldValue(COMPANY_INSURED_B, companyInsuredB);
    }

    public static YesNoFlag getOutputB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(OUTPUT_B));
    }

    public static void setOutputB(Record record, YesNoFlag outputB) {
        record.setFieldValue(OUTPUT_B, outputB);
    }

    public static YesNoFlag getPolicyNoReadOnlyB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(POLICY_NO_READ_ONLY_B));
    }

    public static void setPolicyNoReadOnlyB(Record record, YesNoFlag outputB) {
        record.setFieldValue(POLICY_NO_READ_ONLY_B, outputB);
    }

    public static YesNoFlag getEffectiveFromDateReadOnlyB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(EFFECTIVE_FROM_DATE_READ_ONLY_B));
    }

    public static void setEffectiveFromDateReadOnlyB(Record record, YesNoFlag outputB) {
        record.setFieldValue(EFFECTIVE_FROM_DATE_READ_ONLY_B, outputB);
    }

    public static YesNoFlag getCovPartCoverageCodeReadOnlyB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(COV_PART_COVERAGE_CODE_READ_ONLY_B));
    }

    public static void setCovPartCoverageCodeReadOnlyB(Record record, YesNoFlag outputB) {
        record.setFieldValue(COV_PART_COVERAGE_CODE_READ_ONLY_B, outputB);
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

    public static YesNoFlag getRenewB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(RENEW_B));
    }

    public static void setRenewB(Record record, YesNoFlag renewB) {
        record.setFieldValue(RENEW_B, renewB);
    }

    public static String getGroupNo(Record record) {
        return record.getStringValue(GROUP_NO);
    }

    public static void setGroupNo(Record record, String groupNo) {
        record.setFieldValue(GROUP_NO, groupNo);
    }

    public static String getSubGroupCode(Record record) {
        return record.getStringValue(SUB_GROUP_CODE);
    }

    public static void setSubGroupCode(Record record, String subGroupCode) {
        record.setFieldValue(SUB_GROUP_CODE, subGroupCode);
    }

    public static String getLimitValue1(Record record) {
        return record.getStringValue(LIMIT_VALUE1);
    }

    public static void setLimitValue1(Record record, String limitValue1) {
        record.setFieldValue(LIMIT_VALUE1, limitValue1);
    }

    public static String getLimitValue1Code(Record record) {
        return record.getStringValue(LIMIT_VALUE1_CODE);
    }

    public static void setLimitValue1Code(Record record, String limitValue1Code) {
        record.setFieldValue(LIMIT_VALUE1_CODE, limitValue1Code);
    }

    public static String getLimitValue2(Record record) {
        return record.getStringValue(LIMIT_VALUE2);
    }

    public static void setLimitValue2(Record record, String limitValue2) {
        record.setFieldValue(LIMIT_VALUE2, limitValue2);
    }

    public static String getLimitValue2Code(Record record) {
        return record.getStringValue(LIMIT_VALUE2_CODE);
    }

    public static void setLimitValue2Code(Record record, String limitValue2Code) {
        record.setFieldValue(LIMIT_VALUE2_CODE, limitValue2Code);
    }

    public static String getLimitValue3(Record record) {
        return record.getStringValue(LIMIT_VALUE3);
    }

    public static void setLimitValue3(Record record, String limitValue3) {
        record.setFieldValue(LIMIT_VALUE3, limitValue3);
    }

    public static String getLimitValue3Code(Record record) {
        return record.getStringValue(LIMIT_VALUE3_CODE);
    }

    public static void setLimitValue3Code(Record record, String limitValue3Code) {
        record.setFieldValue(LIMIT_VALUE3_CODE, limitValue3Code);
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

    public static String getOfficialRecordId(Record record) {
        return record.getStringValue(OFFICIAL_RECORD_ID);
    }

    public static void setOfficialRecordId(Record record, String officialRecordId) {
        record.setFieldValue(OFFICIAL_RECORD_ID, officialRecordId);
    }

    public static YesNoFlag getAfterImageRecordB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(AFTER_IMAGE_RECORD_B));
    }

    public static void setAfterImageRecordB(Record record, YesNoFlag afterImageRecordB) {
        record.setFieldValue(AFTER_IMAGE_RECORD_B, afterImageRecordB);
    }

    public static String getOrigEffectiveToDate(Record record) {
        return record.getStringValue(ORIG_EFFECTIVE_TO_DATE);
    }

    public static void setOrigEffectiveToDate(Record record, String origEffToDate) {
        record.setFieldValue(ORIG_EFFECTIVE_TO_DATE, origEffToDate);
    }
}
