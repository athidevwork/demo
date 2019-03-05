package dti.pm.tailmgr;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.core.http.RequestIds;

/**
 * Helper constants and set/get methods to access Tail Fields.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 21, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/03/2008       sxm         Issue 81453 - added additional fields
 * 11/06/2008       yhyang      #87658 Add TERM_EFFECTIVE_FROM_DATE and TERM_EFFECTIVE_TO_DATE.
 * 02/25/2011       dzhang      Issue 113062 - Added minimumTermBaseRecordId
 * 04/27/2012       xnie        Issue 132999 - Added tailTermBaseRecordId and get/set.
 * 05/04/2012       xnie        Issue 132999 - Roll backed prior changes.
 * 04/10/2014       xnie        Issue 153450 - Added saveAndCloseB and get/set methods.
 * 05/27/2014       xnie        Issue 153450 - Roll backed prior changes.
 * 07/22/2016       eyin        Issue 176557 - Added tailExtRemLimitB and get/set methods.
 * ---------------------------------------------------
 */

public class TailFields {
    public static final String COVERAGE_LIMIT_CODE = "coverageLimitCode";
    public static final String TAIL_EXT_REM_LIMIT_B = "tailExtRemLimitB";
    public static final String TAIL_COV_BASE_RECORD_ID = "tailCovBaseRecordId";
    public static final String CURRENT_POLICY_MODE = "currentPolicyMode";
    public static final String TAIL_RECORD_MODE = "tailRecordMode";
    public static final String TAIL_SCREEN_MODE = "tailScreenMode";
    public static final String RATING_MODULE_CODE = "ratingModuleCode";
    public static final String TO_RATE_B = "toRateB";
    public static final String TAIL_CURR_POL_REL_STAT_TYPE_CD = "tailCurrPolRelStatTypeCd";
    public static final String EFFECTIVE_FROM_DATE = "effectiveFromDate";
    public static final String EFFECTIVE_TO_DATE = "effectiveToDate";
    public static final String TERM_BASE_RECORD_ID = "termBaseRecordId";
    public static final String PRODUCT_COVERAGE_CODE = "productCoverageCode";
    public static final String CVG_RELATION_TYPE_CODE = "cvgRelationTypeCode";
    public static final String SELECT_IND = RequestIds.SELECT_IND;
    public static final String SUB_COVERAGE_DESC = "subCoverageDesc";
    public static final String ANNUAL_BASE_RATE = "annualBaseRate";
    public static final String TAIL_GROSS_PREMIUM = "tailGrossPremium";
    public static final String PROCESS_CODE = "processCode";
    public static final String PROCESS_ACTION = "processAction"; // maintain same value of processCode
    public static final String PROCESS_ACTION_UPDATE = "UPDATE";
    public static final String COVERAGE_RELATION_ID = "coverageRelationId";
    public static final String TAIL_ACCOUNTING_FROM_DATE = "tailAccountingFromDate";
    public static final String RATE_PERCENT = "ratePercent";
    public static final String TAIL_RECORD_MODE_CODE = "tailRecordModeCode";
    public static final String TAIL_NET_PREMIUM = "tailNetPremium";
    public static final String PROD_COV_REL_TYPE_CODE = "productCovRelTypeCode";
    public static final String RATING_BASIS = "ratingBasis";
    public static final String MAIN_COVERAGE_ID = "mainCoverageId";
    public static final String RETROACTIVE_DATE = "retroactiveDate";
    public static final String SUB_COVERAGE_B = "subCoverageB";
    public static final String ADJ_INCIDENT_LIMIT = "adjIncidentLimit";
    public static final String ADJ_AGGREGATE_LIMIT = "adjAggregateLimit";
    public static final String TAIL_STATUS = "tailStatus";
    public static final String TERM_EFFECTIVE_FROM_DATE = "termEffectiveFromDate";
    public static final String TERM_EFFECTIVE_TO_DATE = "termEffectiveToDate";
    public static final String MINIMUM_TERM_BASE_RECORD_ID = "minimumTermBaseRecordId";
    public static final String POLICY_TERM_HISTORY_ID = "policyTermHistoryId";

    public static String getTailNetPremium(Record record) {
        return record.getStringValue(TAIL_NET_PREMIUM);
    }

    public static void setTailNetPremium(Record record, String tailNetPremium) {
        record.setFieldValue(TAIL_NET_PREMIUM, tailNetPremium);
    }

    public static String getTailRecordModeCode(Record record) {
        return record.getStringValue(TAIL_RECORD_MODE_CODE);
    }

    public static void setTailRecordModeCode(Record record, String tailRecordModeCode) {
        record.setFieldValue(TAIL_RECORD_MODE_CODE, tailRecordModeCode);
    }

    public static String getRatePercent(Record record) {
        return record.getStringValue(RATE_PERCENT);
    }

    public static void setRatePercent(Record record, String ratePercent) {
        record.setFieldValue(RATE_PERCENT, ratePercent);
    }

    public static boolean hasRatePercent(Record record) {
        return record.hasStringValue(RATE_PERCENT);
    }

    public static String getTailAccountingFromDate(Record record) {
        return record.getStringValue(TAIL_ACCOUNTING_FROM_DATE);
    }

    public static void setTailAccountingFromDate(Record record, String tailAccountingFromDate) {
        record.setFieldValue(TAIL_ACCOUNTING_FROM_DATE, tailAccountingFromDate);
    }

    public static String getCoverageRelationId(Record record) {
        return record.getStringValue(COVERAGE_RELATION_ID);
    }

    public static void setCoverageRelationId(Record record, String coverageRelationId) {
        record.setFieldValue(COVERAGE_RELATION_ID, coverageRelationId);
    }

    public static String getProcessCode(Record record) {
        return record.getStringValue(PROCESS_CODE);
    }

    public static void setProcessCode(Record record, String processCode) {
        record.setFieldValue(PROCESS_CODE, processCode);
    }

    public static String getProcessAction(Record record) {
        return record.getStringValue(PROCESS_ACTION);
    }

    public static String getTailGrossPremium(Record record) {
        return record.getStringValue(TAIL_GROSS_PREMIUM);
    }

    public static void setTailGrossPremium(Record record, String tailGrossPremium) {
        record.setFieldValue(TAIL_GROSS_PREMIUM, tailGrossPremium);
    }

    public static String getAnnualBaseRate(Record record) {
        return record.getStringValue(ANNUAL_BASE_RATE);
    }

    public static void setAnnualBaseRate(Record record, String annualBaseRate) {
        record.setFieldValue(ANNUAL_BASE_RATE, annualBaseRate);
    }

    public static String getSubCoverageDesc(Record record) {
        return record.getStringValue(SUB_COVERAGE_DESC);
    }

    public static void setSubCoverageDesc(Record record, String subCoverageDesc) {
        record.setFieldValue(SUB_COVERAGE_DESC, subCoverageDesc);
    }

    public static String getSelectInd(Record record) {
        return record.getStringValue(SELECT_IND);
    }

    public static void setSelectInd(Record record, String selectInd) {
        record.setFieldValue(SELECT_IND, selectInd);
    }

    public static String getCvgRelationTypeCode(Record record) {
        return record.getStringValue(CVG_RELATION_TYPE_CODE);
    }

    public static void setCvgRelationTypeCode(Record record, String cvgRelationTypeCode) {
        record.setFieldValue(CVG_RELATION_TYPE_CODE, cvgRelationTypeCode);
    }

    public static String getProductCoverageCode(Record record) {
        return record.getStringValue(PRODUCT_COVERAGE_CODE);
    }

    public static void setProductCoverageCode(Record record, String productCoverageCode) {
        record.setFieldValue(PRODUCT_COVERAGE_CODE, productCoverageCode);
    }

    public static String getTermBaseRecordId(Record record) {
        return record.getStringValue(TERM_BASE_RECORD_ID);
    }

    public static void setTermBaseRecordId(Record record, String termBaseRecordId) {
        record.setFieldValue(TERM_BASE_RECORD_ID, termBaseRecordId);
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

    public static YesNoFlag getToRateB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(TO_RATE_B));
    }

    public static void setToRateB(Record record, YesNoFlag toRateB) {
        record.setFieldValue(TO_RATE_B, toRateB);
    }

    public static String getTailCurrPolRelStatTypeCd(Record record) {
        return record.getStringValue(TAIL_CURR_POL_REL_STAT_TYPE_CD);
    }

    public static void setTailCurrPolRelStatTypeCd(Record record, String tailCurrPolRelStatTypeCd) {
        record.setFieldValue(TAIL_CURR_POL_REL_STAT_TYPE_CD, tailCurrPolRelStatTypeCd);
    }

    public static String getRatingModuleCode(Record record) {
        return record.getStringValue(RATING_MODULE_CODE);
    }

    public static void setRatingModuleCode(Record record, String ratingModuleCode) {
        record.setFieldValue(RATING_MODULE_CODE, ratingModuleCode);
    }

    public static String getTailRecordMode(Record record) {
        return record.getStringValue(TAIL_RECORD_MODE);
    }

    public static void setTailRecordMode(Record record, TailRecordMode tailRecordMode) {
        record.setFieldValue(TAIL_RECORD_MODE, tailRecordMode);
    }

    public static String getTailScreenMode(Record record) {
        return record.getStringValue(TAIL_SCREEN_MODE);
    }

    public static void setTailScreenMode(Record record, TailScreenMode tailScreenMode) {
        record.setFieldValue(TAIL_SCREEN_MODE, tailScreenMode);
    }

    public static String getCurrentPolicyMode(Record record) {
        return record.getStringValue(CURRENT_POLICY_MODE);
    }

    public static void setCurrentPolicyMode(Record record, ScreenModeCode currentPolicyMode) {
        record.setFieldValue(CURRENT_POLICY_MODE, currentPolicyMode);
    }

    public static String getTailCovBaseRecordId(Record record) {
        return record.getStringValue(TAIL_COV_BASE_RECORD_ID);
    }

    public static void setTailCovBaseRecordId(Record record, String tailCovBaseRecordId) {
        record.setFieldValue(TAIL_COV_BASE_RECORD_ID, tailCovBaseRecordId);
    }

    public static String getCoverageLimitCode(Record record) {
        return record.getStringValue(COVERAGE_LIMIT_CODE);
    }

    public static void setCoverageLimitCode(Record record, String coverageLimitCode) {
        record.setFieldValue(COVERAGE_LIMIT_CODE, coverageLimitCode);
    }

    public static String getTailExtRemLimitB(Record record) {
        return record.getStringValue(TAIL_EXT_REM_LIMIT_B);
    }

    public static void setTailExtRemLimitB(Record record, YesNoFlag tailExtRemLimitB) {
        record.setFieldValue(TAIL_EXT_REM_LIMIT_B, tailExtRemLimitB);
    }


    public static String getProdCovRelTypeCode(Record record) {
        return record.getStringValue(PROD_COV_REL_TYPE_CODE);
    }

    public static void setProdCovRelTypeCode(Record record, String prodCovRelTypeCode) {
        record.setFieldValue(PROD_COV_REL_TYPE_CODE, prodCovRelTypeCode);
    }

    public static String getRatingBasis(Record record) {
        return record.getStringValue(RATING_BASIS);
    }

    public static void setRatingBasis(Record record, String ratingBasis) {
        record.setFieldValue(RATING_BASIS, ratingBasis);
    }

    public static String getMainCoverageId(Record record) {
        return record.getStringValue(MAIN_COVERAGE_ID);
    }

    public static void setMainCoverageId(Record record, String mainCoverageId) {
        record.setFieldValue(MAIN_COVERAGE_ID, mainCoverageId);
    }

    public static String getRetroactiveDate(Record record) {
        return record.getStringValue(RETROACTIVE_DATE);
    }

    public static void setRetroactiveDate(Record record, String retroactiveDate) {
        record.setFieldValue(RETROACTIVE_DATE, retroactiveDate);
    }

    public static YesNoFlag getSubCoverageB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(SUB_COVERAGE_B));
    }

    public static void setSubCoverageB(Record record, YesNoFlag subCoverageB) {
        record.setFieldValue(SUB_COVERAGE_B, subCoverageB);
    }

    public static String getAdjIncidentLimit(Record record) {
        return record.getStringValue(ADJ_INCIDENT_LIMIT);
    }

    public static void setAdjIncidentLimit(Record record, String adjIncidentLimit) {
        record.setFieldValue(ADJ_INCIDENT_LIMIT, adjIncidentLimit);
    }

    public static String getAdjAggregateLimit(Record record) {
        return record.getStringValue(ADJ_AGGREGATE_LIMIT);
    }

    public static void setAdjAggregateLimit(Record record, String adjAggregateLimit) {
        record.setFieldValue(ADJ_AGGREGATE_LIMIT, adjAggregateLimit);
    }

    public static String getTailStatus(Record record) {
        return record.getStringValue(TAIL_STATUS);
    }

    public static void setTailStatus(Record record, String tailStatus) {
        record.setFieldValue(TAIL_STATUS, tailStatus);
    }

    public static String getTermEffectiveFromDate(Record record) {
        return record.getStringValue(TERM_EFFECTIVE_FROM_DATE);
    }

    public static void setTermEffectiveFromDate(Record record, String termEffectiveFromDate) {
        record.setFieldValue(TERM_EFFECTIVE_FROM_DATE, termEffectiveFromDate);
    }

    public static String getTermEffectiveToDate(Record record) {
        return record.getStringValue(TERM_EFFECTIVE_TO_DATE);
    }

    public static void setTermEffectiveToDate(Record record, String termEffectiveToDate) {
        record.setFieldValue(TERM_EFFECTIVE_TO_DATE, termEffectiveToDate);
    }

    public static String getMinimumTermBaseRecordId(Record record) {
        return record.getStringValue(MINIMUM_TERM_BASE_RECORD_ID);
    }

    public static void setMinimumTermBaseRecordId(Record record, String minimumTermBaseRecordId) {
        record.setFieldValue(MINIMUM_TERM_BASE_RECORD_ID, minimumTermBaseRecordId);
    }

    public static String getPolicyTermHistoryId(Record record) {
        return record.getStringValue(POLICY_TERM_HISTORY_ID);
    }

    public static void setPolicyTermHistoryId(Record record, String policyTermHistoryId) {
        record.setFieldValue(POLICY_TERM_HISTORY_ID, policyTermHistoryId);
    }
}
