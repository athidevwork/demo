package dti.pm.coveragemgr.underlyingmgr;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;

/**
 * Constants for Underlying coverage
 * <p>(C) 2018 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 12, 2018
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *  08/24/2018       wrong      188391 - Initial version.
 * ---------------------------------------------------
 */
public class UnderlyingCoverageFields {
    public static final String EFFECTIVE_FROM_DATE = "effectiveFromDate";
    public static final String EFFECTIVE_TO_DATE = "effectiveToDate";
    public static final String POLICY_UNDER_POL_ID = "policyUnderPolId";
    public static final String POLICY_UNDERLYING_COVG_ID = "policyUnderlyingCovgId";
    public static final String COVERAGE_BASE_ID = "coverageBaseId";
    public static final String POLICY_UNDER_COVG_BASE_ID = "policyUnderCovgBaseId";

    public static final String POLICY_UNDER_POL_NO = "policyUnderPolNo";
    public static final String UNDER_POLICY_TYPE_CODE = "underPolicyTypeCode";

    public static final String RISK_NAME = "riskName";
    public static final String RISK_TYPE = "riskType";
    public static final String UNDER_RISK_NAME = "underRiskName";
    public static final String UNDER_RISK_TYPE = "underRiskType";
    public static final String UNDER_RISK_ENTITY_ID = "underRiskEntityId";

    public static final String PRODUCT_COVERAGE_CODE = "productCoverageCode";
    public static final String UNDER_COVERAGE_CODE = "underCoverageCode";
    public static final String POLICY_FORM_CODE = "policyFormCode";
    public static final String COVERAGE_LIMIT_CODE = "coverageLimitCode";
    public static final String UND_RETROACTIVE_DATE = "undRetroactiveDate";
    public static final String COMPANY_INSURED_B = "companyInsuredB";
    public static final String OUTPUT_B = "outputB";
    public static final String POLICY_NO_READ_ONLY_B = "policyNoReadOnlyB";
    public static final String UNDER_ISS_COMP_ENT_ID = "underIssCompEntId";
    public static final String UNDER_COVG_TYPE = "underCovgType";
    public static final String RENEW_B = "renewB";
    public static final String RECORD_MODE_CODE = "recordModeCode";
    public static final String TRANSACTION_LOG_ID = "transactionLogId";
    public static final String CLOSING_TRANS_LOG_ID = "closingTransLogId";
    public static final String OFFICIAL_RECORD_ID = "officialRecordId";
    public static final String AFTER_IMAGE_RECORD_B = "afterImageRecordB";
    public static final String ORIG_EFFECTIVE_TO_DATE = "origEffectiveToDate";
    public static final String IS_POLICY_LEVEL = "isPolicyLevel";
    public static final String EXPIRED_B = "expiredB";

    public static String getUnderIssCompEntId(Record record) {
        return record.getStringValue(UNDER_ISS_COMP_ENT_ID);
    }

    public static void setUnderIssCompEntId(Record record, String underIssCompEntId) {
        record.setFieldValue(UNDER_ISS_COMP_ENT_ID, underIssCompEntId);
    }

    public static void setPolicyUnderlyingCovgId(Record record, String policyUnderlyingCovgId) {
        record.setFieldValue(POLICY_UNDERLYING_COVG_ID, policyUnderlyingCovgId);
    }

    public static String getPolicyUnderPolId(Record record) {
        return record.getStringValue(POLICY_UNDER_POL_ID);
    }

    public static void setPolicyUnderPolId(Record record, String policyUnderPolId) {
        record.setFieldValue(POLICY_UNDER_POL_ID, policyUnderPolId);
    }

    public static String getPolicyUnderlyingCovgId(Record record) {
        return record.getStringValue(POLICY_UNDERLYING_COVG_ID);
    }

    public static String getPolicyUnderCovgBaseId(Record record) {
        return record.getStringValue(POLICY_UNDER_COVG_BASE_ID);
    }

    public static void setPolicyUnderCovgBaseId(Record record, String policyUnderCovgBaseId) {
        record.setFieldValue(POLICY_UNDER_COVG_BASE_ID, policyUnderCovgBaseId);
    }

    public static String getCoverageBaseId(Record record) {
        return record.getStringValue(COVERAGE_BASE_ID);
    }

    public static void setCoverageBaseId(Record record, String coverageBaseId) {
        record.setFieldValue(COVERAGE_BASE_ID, coverageBaseId);
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

    public static String getRiskName(Record record) {return record.getStringValue(RISK_NAME); }

    public static void setRiskName(Record record, String riskName) {
        record.setFieldValue(RISK_NAME, riskName);
    }

    public static String getRiskType(Record record) {return record.getStringValue(RISK_TYPE); }

    public static void setRiskType(Record record, String riskType) {
        record.setFieldValue(RISK_TYPE, riskType);
    }

    public static String getUnderRiskName(Record record) {return record.getStringValue(UNDER_RISK_NAME); }

    public static void setUnderRiskName(Record record, String underRiskName) {
        record.setFieldValue(UNDER_RISK_NAME, underRiskName);
    }

    public static String getUnderRiskType(Record record) {return record.getStringValue(UNDER_RISK_TYPE); }

    public static void setUnderRiskType(Record record, String underRiskType) {
        record.setFieldValue(UNDER_RISK_TYPE, underRiskType);
    }

    public static String getUnderRiskEntityId(Record record) {return record.getStringValue(UNDER_RISK_ENTITY_ID); }

    public static void setUnderRiskEntityId(Record record, String underriskEntityId) {
        record.setFieldValue(UNDER_RISK_ENTITY_ID, underriskEntityId);
    }

    public static String getProductCoverageCode(Record record) {
        return record.getStringValue(PRODUCT_COVERAGE_CODE);
    }

    public static void setProductCoverageCode(Record record, String productCoverageCode) {
        record.setFieldValue(PRODUCT_COVERAGE_CODE, productCoverageCode);
    }

    public static String getUnderCoverageCode(Record record) {
        return record.getStringValue(UNDER_COVERAGE_CODE);
    }

    public static void setUnderCoverageCode(Record record, String underCoverageCode) {
        record.setFieldValue(UNDER_COVERAGE_CODE, underCoverageCode);
    }

    public static String getUnderCovgType(Record record) { return record.getStringValue(UNDER_COVG_TYPE); }

    public static void setUnderCovgType(Record record, String underCovgType) {
        record.setFieldValue(UNDER_COVG_TYPE, underCovgType);
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

    public static String getUndRetroactiveDate(Record record) {
        return record.getStringValue(UND_RETROACTIVE_DATE);
    }

    public static void setUndRetroactiveDate(Record record, String undRetroactiveDate) {
        record.setFieldValue(UND_RETROACTIVE_DATE, undRetroactiveDate);
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
