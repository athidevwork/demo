package dti.pm.coveragemgr;

import dti.oasis.recordset.Record;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.util.StringUtils;
import dti.pm.busobjs.PMStatusCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Constants for Coverage.
 * <p/>
 * <p>(C) 2007 Delphi Technology, inc. (dti)</p>
 * Date:   March 26, 2007
 *
 * @author Bhong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/21/2007       sxm         Added PRODUCT_COVERAGE_DESC and SUB_COVERAGE_AVAILABLE_B
 * 05/25/2009       yhyang      Added POLICY_NAV_LEVEL_CODE and POLICY_NAV_SOURCE_ID
 * 05/10/2011       wqfu        Added IS_MANUALLY_RATED 
 * 07/20/2011       syang       121208 - Added some fields for coverage grouping.
 * 01/17/2012       wfu         125059 - Added field SRC_COVERAGE_CODE.
 * 04/30/2012       xnie        132237 - Added field COVERAGE_DESCRIPTION.
 * 01/16/2013       tcheng      140034 - Added field COVERAGE_VERSION_EFFECTIVE_TO_DATE.
 * 04/24/2013       awu         141758 - Added setProductDefaultSharedLimitB, setShortTermB, getParentCovCode.
 * 08/09/2013       awu         146878 - Added OBJ_ID, RECORD_MODE, NOSE_B, RETRO_B, RETRO_CHANGE_B, DEL_FLAT_B,
 *                                       SAME_COVERAGE_B, STATE_B and CoverageCodeValues.
 * 08/22/2013       adeng       145619 - Reverted the fixing of issue 140034, removed field COVERAGE_VERSION_EFFECTIVE_TO_DATE.
 * 09/06/2013       adeng       147468 - Added coverage record mode code for ENDQUOTE.
 * 10/10/2013       adeng       148929 - 1)Added field PRODUCT_DEFAULT_SUBLIMIT_B with getter method.
 *                                       2)Added field SUBLIMIT_B with setter method.
 * 10/23/2013       xnie        148246 - Added updateExpB and get()/set().
 * 01/01/2014       Parker      148029 - Cache risk header, coverage header and policy navigation information to policy header.
 * 01/22/2014       Jyang       150639 - Removed COVERAGE_CONTINUOUS_EFF_DATE and COVERAGE_CONTINUOUS_EXP_DATE.
 * 05/16/2014       wdang       154193 - Revert issue 145619.
 * 12/26/2014       xnie        156995 - Added COVERAGE_TABLE_NAME/TABLE_NAME/COVERAGE_IDS/COVERAGE_FIELDS_LIST
 *                                       /COVERAGE_DB_FIELDS_LIST fields. Added a map COVERAGE_MANUAL_UPDATABLE_FIELDS.
 * 11/27/2015       wdang       166922 - modified setRiskManualUpdatableFields for newly-added fields.
 * 07/26/2016       lzhang      169751 - Add IBNRCovgEffectiveFromDate, IBNRCovgEffectiveToDate
 *                                       and IBNRCovgB get/set method
 * 06/15/2017       wrong       186163 - Add claimProcessCode fields and getter/setter method.
 * 06/28/2017       tzeng       186273 - Added field mainCoverageBaseRecordId with set/get/has methods.
 * ---------------------------------------------------
 */
public class CoverageFields {
    public static final String PRODUCT_COVERAGE_CODE = "productCoverageCode";
    public static final String COVERAGE_EFFECTIVE_FROM_DATE = "coverageEffectiveFromDate";
    public static final String COVERAGE_EFFECTIVE_TO_DATE = "coverageEffectiveToDate";
    public static final String ORIG_COVERAGE_EFFECTIVE_TO_DATE = "origCoverageEffectiveToDate";
    public static final String COVERAGE_BASE_EFFECTIVE_FROM_DATE = "coverageBaseEffectiveFromDate";
    public static final String COVERAGE_BASE_EFFECTIVE_TO_DATE = "coverageBaseEffectiveToDate";
    public static final String COVERAGE_BASE_RECORD_ID = "coverageBaseRecordId";
    public static final String OFFICIAL_RECORD_ID = "officialRecordId";
    public static final String RETRO_DATE = "retroDate";
    public static final String ORIG_RETRO_DATE = "origRetroDate";
    public static final String POLICY_FORM_CODE = "policyFormCode";
    public static final String COV_PART_CODE = "covPartCode";
    public static final String PARENT_COV_CODE = "parentCovCode";
    public static final String ANNUAL_BASE_RATE = "annualBaseRate";
    public static final String ORIG_ANNUAL_BASE_RATE = "origAnnualBaseRate";
    public static final String PRODUCT_DEFAULT_SHARED_LIMITB = "productDefaultSharedLimitB";
    public static final String PRODUCT_DEFAULT_SUBLIMIT_B = "productDefaultSublimitB";
    public static final String SUBLIMIT_B = "SubLimitB";
    public static final String COVERAGE_LIMIT_CODE = "coverageLimitCode";
    public static final String PRODUCT_DEFAULT_LIMIT_CODE = "productDefaultLimitCode";
    public static final String RATING_MODULE_CODE = "ratingModuleCode";
    public static final String CANCELLATION_METHOD_CODE = "cancellationMethodCode";
    public static final String DEFAULT_DEPENDENT_COV_B = "defaultDependentCovB";
    public static final String COVERAGE_ID = "coverageId";
    public static final String PARENT_COVERAGE_ID = "parentCoverageId";
    public static final String SHARED_LIMITS_B = "sharedLimitsB";
    public static final String PRODUCT_SHARED_LIMIT_B = "productSharedLimitB";
    public static final String AFTER_IMAGE_RECORD_B = "afterImageRecordB";
    public static final String PRIMARY_COVERAGE_B = "primaryCoverageB";
    public static final String PCF_STATE_CODE = "pcfStateCode";
    public static final String COVERAGE_CODE = "coverageCode";
    public static final String PRACTICE_STATE_CODE = "practiceStateCode";
    public static final String COVERAGE_STATUS = "coverageStatus";
    public static final String PRODUCT_COVERAGE_DESC = "productCoverageDesc";
    public static final String SUB_COVERAGE_AVAILABLE_B = "subCovgAvailableB";
    public static final String RATE_PAYOR_DEPEND_B = "ratePayorDependB";
    public static final String RATE_PAYOR_DEPEND_CODE = "ratePayorDependCode";
    public static final String ORIG_RATE_PAYOR_DEPEND_CODE = "origRatePayorDependCode";
    public static final String MAIN_COVERAGE_BASE_RECORD_ID = "mainCoverageBaseRecordId";

    public static final String NOSE_COVERAGE_B = "noseCoverageB";
    public static final String COVERAGE_SEGMENT_CODE = "coverageSegmentCode";
    public static final String COVERAGE_BASE_STATUS = "coverageBaseStatus";
    public static final String EXCESS_B = "excessB";
    public static final String POLICY_NAV_LEVEL_CODE = "policyNavLevelCode";
    public static final String POLICY_NAV_SOURCE_ID = "policyNavSourceId";
    public static final String RISK_NAV_SOURCE_ID = "riskNavSourceId";
    public static final String COVERAGE_NAV_SOURCE_ID = "coverageNavSourceId";
    public static final String SHORT_TERM_B = "shortTermB";
    public static final String IS_MANUALLY_RATED = "isManuallyRated";
    public static final String COVERAGE_GROUP = "coverageGroup";
    public static final String COVERAGE_GROUP_CODE = "coverageGroupCode";
    public static final String PRODUCT_COVERAGE_ID = "productCoverageId";
    public static final String SEQUENCE_NO = "sequenceNo";
    public static final String GROUP_EXPAND_COLLAPSE = "groupExpandCollapse";
    public static final String SRC_COVERAGE_CODE = "srcCoverageCode";
    public static final String COVG_PART_SHARED_LIMIT_B = "covgPartSharedLimitB";
    public static final String COVERAGE_DESCRIPTION = "coverageDescription";
    public static final String COVERAGE_VERSION_EFFECTIVE_TO_DATE = "coverageVersionEffectiveToDate";
    public static final String LATEST_COVERAGE_EFFECTIVE_TO_DATE = "latestCoverageEffectiveToDate";

    public static final String OBJ_ID = "objId";
    public static final String RECORD_MODE = "recMode";
    public static final String NOSE_B = "noseB";
    public static final String RETRO_B = "retroB";
    public static final String RETRO_CHANGE_B = "retroChkb";
    public static final String DEL_FLAT_B = "delFlatB";
    public static final String SAME_COVERAGE_B = "samecovgB";
    public static final String STATE_B = "stateB";
    public static final String UPDATE_EXP_B = "updateExpB";
    public static final String COVERAGE_TABLE_NAME = "COVERAGE";
    public static final String TABLE_NAME = "tableName";
    public static final String COVERAGE_IDS = "ids";
    public static final String COVERAGE_FIELDS_LIST = "coverageFieldsList";
    public static final String COVERAGE_DB_FIELDS_LIST = "coverageDbFieldsList";
    public static final String TO_COVG_BASE_RECORD_IDS = "toCovgBaseRecordIds";
    public static final String CLAIM_PROCESS_CODE = "claimProcessCode";
    public static final String IBNR_COVG_EFFECTIVE_FROM_DATE = "IBNRCovgEffectiveFromDate";
    public static final String IBNR_COVG_EFFECTIVE_TO_DATE = "IBNRCovgEffectiveToDate";
    public static final String IBNR_COVG_B = "IBNRCovgB";

    public static Map<String, String> COVERAGE_MANUAL_UPDATABLE_FIELDS = new HashMap<String, String>();
    
    public static YesNoFlag getExcessB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(EXCESS_B));
    }
    
    public static PMStatusCode getCoverageBaseStatus(Record record) {
        Object value = record.getFieldValue(COVERAGE_BASE_STATUS);
        PMStatusCode result = null;
        if (value == null || value instanceof PMStatusCode) {
            result = (PMStatusCode) value;
        }
        else {
            result = PMStatusCode.getInstance(value.toString());
        }
        return result;
    }

    public static void setCoverageBaseStatus(Record record, PMStatusCode coverageBaseStatus) {
        record.setFieldValue(COVERAGE_BASE_STATUS, coverageBaseStatus);
    }    

    public static String getCoverageSegmentCode(Record record) {
        return record.getStringValue(COVERAGE_SEGMENT_CODE);
    }

    public static void setCoverageSegmentCode(Record record, String coverageSegmentCode) {
        record.setFieldValue(COVERAGE_SEGMENT_CODE, coverageSegmentCode);
    }

    public static YesNoFlag getNoseCoverageB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(NOSE_COVERAGE_B));
    }

    public static void setNoseCoverageB(Record record, YesNoFlag noseCoverageB) {
        record.setFieldValue(NOSE_COVERAGE_B, noseCoverageB);
    }

    // Get methods
    public static String getProductCoverageCode(Record record) {
        return record.getStringValue(PRODUCT_COVERAGE_CODE);
    }

    public static String getProductDefaultSharedLimitB(Record record) {
        return record.getStringValue(PRODUCT_DEFAULT_SHARED_LIMITB);
    }

    public static String getCoverageEffectiveToDate(Record record) {
        return record.getStringValue(COVERAGE_EFFECTIVE_TO_DATE);
    }

    public static String getProductDefaultSubLimitB(Record record) {
        return record.getStringValue(PRODUCT_DEFAULT_SUBLIMIT_B);
    }

    public static void setSubLimitB(Record record, String subLimitB) {
        record.setFieldValue(SUBLIMIT_B, subLimitB);
    }

    public static String getOrigCoverageEffectiveToDate(Record record) {
        return record.getStringValue(ORIG_COVERAGE_EFFECTIVE_TO_DATE);
    }

    public static String getProductDefaultLimitCode(Record record) {
        return record.getStringValue(PRODUCT_DEFAULT_LIMIT_CODE);
    }

    public static String getPolicyFormCode(Record record) {
        return record.getStringValue(POLICY_FORM_CODE);
    }

    public static String getRatingModuleCode(Record record) {
        return record.getStringValue(RATING_MODULE_CODE);
    }

    public static String getCoverageBaseRecordId(Record record) {
        return record.getStringValue(COVERAGE_BASE_RECORD_ID);
    }

    public static String getOfficialRecordId(Record record) {
        return record.getStringValue(OFFICIAL_RECORD_ID);
    }

    public static String getRetroDate(Record record) {
        return record.getStringValue(RETRO_DATE);
    }

    public static String getOrigRetroDate(Record record) {
        return record.getStringValue(ORIG_RETRO_DATE);
    }

    public static String getCoverageId(Record record) {
        return record.getStringValue(COVERAGE_ID);
    }

    public static String getAnnualBaseRate(Record record) {
        return record.getStringValue(ANNUAL_BASE_RATE);
    }

    public static String getOrigAnnualBaseRate(Record record) {
        return record.getStringValue(ORIG_ANNUAL_BASE_RATE);
    }

    public static String getSharedLimitsB(Record record) {
        return record.getStringValue(SHARED_LIMITS_B);
    }

    public static String getProductSharedLimitB(Record record) {
        return record.getStringValue(PRODUCT_SHARED_LIMIT_B);
    }

    public static YesNoFlag getPrimaryCoverageB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(PRIMARY_COVERAGE_B));
    }

    public static String getPcfStateCode(Record record) {
        return record.getStringValue(PCF_STATE_CODE);
    }

    public static String getCoverageCode(Record record) {
        return record.getStringValue(COVERAGE_CODE);
    }

    public static String getPracticeStateCode(Record record) {
        return record.getStringValue(PRACTICE_STATE_CODE);
    }

    public static YesNoFlag getAfterImageRecordB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(AFTER_IMAGE_RECORD_B));
    }

    public static boolean hasCoverageStatus(Record record) {
        return record.hasStringValue(COVERAGE_STATUS);
    }

    public static PMStatusCode getCoverageStatus(Record record) {
        Object value = record.getFieldValue(COVERAGE_STATUS);
        PMStatusCode result = null;
        if (value == null || value instanceof PMStatusCode) {
            result = (PMStatusCode) value;
        }
        else {
            result = PMStatusCode.getInstance(value.toString());
        }
        return result;
    }

    public static void setCoverageStatus(Record record, String coverageStatus) {
        record.setFieldValue(COVERAGE_STATUS, coverageStatus);
    }

    public static String getProductCoverageDesc(Record record) {
        return record.getStringValue(PRODUCT_COVERAGE_DESC);
    }

    public static String getSubCovgAvailableB(Record record) {
        return record.getStringValue(SUB_COVERAGE_AVAILABLE_B);
    }

    public static YesNoFlag getRatePayorDependB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(RATE_PAYOR_DEPEND_B));
    }

    public static String getRatePayorDependCode(Record record) {
        return record.getStringValue(RATE_PAYOR_DEPEND_CODE);
    }

    public static String getOrigRatePayorDependCode(Record record) {
        return record.getStringValue(ORIG_RATE_PAYOR_DEPEND_CODE);
    }

    public static String getLatestCoverageEffectiveToDate(Record record) {
        return record.getStringValue(LATEST_COVERAGE_EFFECTIVE_TO_DATE);
    }


    
    // Set methods
    public static void setProductCoverageCode(Record record, String productCoverageCode) {
        record.setFieldValue(PRODUCT_COVERAGE_CODE, productCoverageCode);
    }

    public static void setLatestCoverageEffectiveToDate(Record record, String latestCoverageEffectiveToDate){
        record.setFieldValue(LATEST_COVERAGE_EFFECTIVE_TO_DATE, latestCoverageEffectiveToDate);
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

    public static String getCoverageBaseEffectiveFromDate(Record record) {
        return record.getStringValue(COVERAGE_BASE_EFFECTIVE_FROM_DATE);
    }

    public static void setCoverageBaseEffectiveFromDate(Record record, String coverageBaseEffectiveFromDate) {
        record.setFieldValue(COVERAGE_BASE_EFFECTIVE_FROM_DATE, coverageBaseEffectiveFromDate);
    }

    public static String getCoverageBaseEffectiveToDate(Record record) {
        return record.getStringValue(COVERAGE_BASE_EFFECTIVE_TO_DATE);
    }

    public static void setCoverageBaseEffectiveToDate(Record record, String coverageBaseEffectiveToDate) {
        record.setFieldValue(COVERAGE_BASE_EFFECTIVE_TO_DATE, coverageBaseEffectiveToDate);
    }

    public static String getCoverageLimitCode(Record record) {
        return record.getStringValue(COVERAGE_LIMIT_CODE);
    }

    public static void setCoverageLimitCode(Record record, String coverageLimitCode) {
        record.setFieldValue(COVERAGE_LIMIT_CODE, coverageLimitCode);
    }

    public static void setRetroDate(Record record, String retroDate) {
        record.setFieldValue(RETRO_DATE, retroDate);
    }

    public static void setAnnualBaseRate(Record record, String annualBaseRate) {
        record.setFieldValue(ANNUAL_BASE_RATE, annualBaseRate);
    }

    public static void setCancellationMethodCode(Record record, String cancellationMethodCode) {
        record.setFieldValue(CANCELLATION_METHOD_CODE, cancellationMethodCode);
    }

    public static void setCovPartCode(Record record, String covPartCode) {
        record.setFieldValue(COV_PART_CODE, covPartCode);
    }

    public static void setParentCovCode(Record record, String parentCovCode) {
        record.setFieldValue(PARENT_COV_CODE, parentCovCode);
    }

    public static void setCoverageBaseRecordId(Record record, String coverageBaseRecordId) {
        record.setFieldValue(COVERAGE_BASE_RECORD_ID, coverageBaseRecordId);
    }

    public static void setOfficialRecordId(Record record, String officialRecordId) {
        record.setFieldValue(OFFICIAL_RECORD_ID, officialRecordId);
    }

    public static void setCoverageId(Record record, String coverageId) {
        record.setFieldValue(COVERAGE_ID, coverageId);
    }

    public static void setSharedLimitsB(Record record, String sharedLimitsB) {
        record.setFieldValue(SHARED_LIMITS_B, sharedLimitsB);
    }

    public static void setProductSharedLimitB(Record record, String productSharedLimitB) {
        record.setFieldValue(PRODUCT_SHARED_LIMIT_B, productSharedLimitB);
    }

    public static void setAfterImageRecordB(Record record, YesNoFlag afterImageRecordB) {
        record.setFieldValue(AFTER_IMAGE_RECORD_B, afterImageRecordB);
    }

    public static void setPrimaryCoverageB(Record record, YesNoFlag primaryCoverageB) {
        record.setFieldValue(PRIMARY_COVERAGE_B, primaryCoverageB);
    }

    public static void setPcfStateCode(Record record, String pcfStateCode) {
        record.setFieldValue(PCF_STATE_CODE, pcfStateCode);
    }

    public static void setCoverageCode(Record record, String coverageCode) {
        record.setFieldValue(COVERAGE_CODE, coverageCode);
    }

    public static void setPolicyFormCode(Record record, String policyFormCode) {
        record.setFieldValue(POLICY_FORM_CODE, policyFormCode);
    }

    public static void setPracticeStateCode(Record record, String practiceStateCode) {
        record.setFieldValue(PRACTICE_STATE_CODE, practiceStateCode);
    }

    public static void setRatingModuleCode(Record record, String ratingModuleCode) {
        record.setFieldValue(RATING_MODULE_CODE, ratingModuleCode);
    }

    public static void setCoverageStatus(Record record, PMStatusCode coverageStatus) {
        record.setFieldValue(COVERAGE_STATUS, coverageStatus);
    }

    public static void setProductCoverageDesc(Record record, String productCoverageDesc) {
        record.setFieldValue(PRODUCT_COVERAGE_DESC, productCoverageDesc);
    }

    public static void setSubCovgAvailableB(Record record, String SubCovgAvailableB) {
        record.setFieldValue(SUB_COVERAGE_AVAILABLE_B, SubCovgAvailableB);
    }

    public static void setRatePayorDependCode(Record record, String ratePayorDependCode) {
        record.setFieldValue(RATE_PAYOR_DEPEND_CODE, ratePayorDependCode);
    }

    public static String getPolicyNavLevelCode(Record record) {
        return record.getStringValue(POLICY_NAV_LEVEL_CODE);
    }

    public static String getPolicyNavSourceId(Record record) {
        return record.getStringValue(POLICY_NAV_SOURCE_ID);
    }

    public static String getRiskNavSourceId(Record record) {
        return record.getStringValue(RISK_NAV_SOURCE_ID);
    }

    public static String getCoverageNavSourceId(Record record) {
        return record.getStringValue(COVERAGE_NAV_SOURCE_ID);
    }

    public static void setPolicyNavLevelCode(Record record, String policyNavLevelCode) {
        record.setFieldValue(POLICY_NAV_LEVEL_CODE, policyNavLevelCode);
    }

    public static void setPolicyNavSourceId(Record record, String policyNavSourceId) {
        record.setFieldValue(POLICY_NAV_SOURCE_ID, policyNavSourceId);
    }

    public static void setRiskNavSourceId(Record record, String riskNavSourceId) {
        record.setFieldValue(RISK_NAV_SOURCE_ID, riskNavSourceId);
    }

    public static void setCoverageNavSourceId(Record record, String coverageNavSourceId) {
        record.setFieldValue(COVERAGE_NAV_SOURCE_ID, coverageNavSourceId);
    }

    public static String getShortTermB(Record record) {
        return record.getStringValue(SHORT_TERM_B);
    }

    public static String getIsManuallyRated(Record record) {
        return record.getStringValue(IS_MANUALLY_RATED);
    }

    public static String getCoverageGroup(Record record) {
        return record.getStringValue(COVERAGE_GROUP);
    }

    public static void setCoverageGroup(Record record, String coverageGroup) {
        record.setFieldValue(COVERAGE_GROUP, coverageGroup);
    }

    public static String getCoverageGroupCode(Record record) {
        return record.getStringValue(COVERAGE_GROUP_CODE);
    }

    public static void setCoverageGroupCode(Record record, String coverageGroupCode) {
        record.setFieldValue(COVERAGE_GROUP_CODE, coverageGroupCode);
    }

    public static String getProductCoverageId(Record record) {
        return record.getStringValue(PRODUCT_COVERAGE_ID);
    }

    public static void setProductCoverageId(Record record, String productCoverageId) {
        record.setFieldValue(PRODUCT_COVERAGE_ID, productCoverageId);
    }

    public static String getSequenceNo(Record record) {
        return record.getStringValue(SEQUENCE_NO);
    }

    public static void setSequenceNo(Record record, String sequenceNo) {
        record.setFieldValue(SEQUENCE_NO, sequenceNo);
    }

    public static String getGroupExpandCollapse(Record record) {
        return record.getStringValue(GROUP_EXPAND_COLLAPSE);
    }

    public static void setGroupExpandCollapse(Record record, String groupExpandCollapse) {
        record.setFieldValue(GROUP_EXPAND_COLLAPSE, groupExpandCollapse);
    }

    public static String getSrcCoverageCode(Record record) {
        return record.getStringValue(SRC_COVERAGE_CODE);
    }
    
    public static void setSrcCoverageCode(Record record, String srcCoverageCode) {
        record.setFieldValue(SRC_COVERAGE_CODE, srcCoverageCode);
    }

    public static String getCovgPartSharedLimitB(Record record) {
        return record.getStringValue(COVG_PART_SHARED_LIMIT_B);
    }

    public static void setCovgPartSharedLimitB(Record record, String covgPartSharedLimitB) {
        record.setFieldValue(COVG_PART_SHARED_LIMIT_B, covgPartSharedLimitB);
    }

    public static String getCoverageDescription(Record record) {
        return record.getStringValue(COVERAGE_DESCRIPTION);
    }

    public static void setCoverageDescription(Record record, String coverageDescription) {
        record.setFieldValue(COVERAGE_DESCRIPTION, coverageDescription);
    }

    public static void setProductDefaultSharedLimitB(Record record, String productDefaultSharedLimitB) {
        record.setFieldValue(PRODUCT_DEFAULT_SHARED_LIMITB, productDefaultSharedLimitB);
    }

    public static void setShortTermB(Record record, String shortTermB) {
        record.setFieldValue(SHORT_TERM_B, shortTermB);
    }

    public static String getParentCovCode(Record record) {
        return record.getStringValue(PARENT_COV_CODE);
    }
    
    public static String getParentCoverageId(Record record) {
        return record.getStringValue(PARENT_COVERAGE_ID);
    }

    public static void setParentCoverageId(Record record, String parentCoverageId) {
        record.setFieldValue(PARENT_COVERAGE_ID, parentCoverageId);
    }

    public static void setObjId(Record record, String objId) {
        record.setFieldValue(OBJ_ID, objId);
    }

    public static String getObjId(Record record) {
        return record.getStringValue(OBJ_ID);
    }

    public static void setRecordMode(Record record, String recordMode) {
        record.setFieldValue(RECORD_MODE, recordMode);
    }

    public static String getRecordMode(Record record) {
        return record.getStringValue(RECORD_MODE);
    }

    public static void setNoseB(Record record, YesNoFlag noseB) {
        record.setFieldValue(NOSE_B, noseB);
    }

    public static YesNoFlag getNoseB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(NOSE_B));
    }

    public static void setRetroB(Record record, YesNoFlag retroB) {
        record.setFieldValue(RETRO_B, retroB);
    }

    public static YesNoFlag getRetroB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(RETRO_B));
    }

    public static void setRetroChangeB(Record record, YesNoFlag retroChangeB) {
        record.setFieldValue(RETRO_CHANGE_B, retroChangeB);
    }

    public static YesNoFlag getRetroChangeB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(RETRO_CHANGE_B));
    }

    public static void setDelFlatB(Record record, YesNoFlag delFlatB) {
        record.setFieldValue(DEL_FLAT_B, delFlatB);
    }

    public static YesNoFlag getDelFlatB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(DEL_FLAT_B));
    }

    public static void setSameCoverageB(Record record, YesNoFlag sameCoverageB) {
        record.setFieldValue(SAME_COVERAGE_B, sameCoverageB);
    }

    public static YesNoFlag getSameCoverageB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(SAME_COVERAGE_B));
    }

    public static void setStateB(Record record, YesNoFlag stateB) {
        record.setFieldValue(STATE_B, stateB);
    }

    public static YesNoFlag getStateB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(STATE_B));
    }

    public class CoverageRecordModeValues {
        public static final String OFFICIAL_MOD = "OFFICIAL";
        public static final String OFFICIAL_TEMP_MOD = "OFF_TEMP";
        public static final String OFFICIAL_ENDQ_MOD = "OFF_ENDQ";
    }

    public class RatePayorDependCodeValues {
        public static final String HOSPITALPY = "HOSPITALPY";
    }

    public static YesNoFlag getUpdateExpB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(UPDATE_EXP_B));
    }

    public static void setUpdateExpB(Record record, YesNoFlag updateExpB) {
        record.setFieldValue(UPDATE_EXP_B, updateExpB);
    }

    public static String getTableName(Record record) {
        return record.getStringValue(TABLE_NAME);
    }

    public static void setTableName(Record record, String tableName) {
        record.setFieldValue(TABLE_NAME, tableName);
    }

    public static String getCoverageIds(Record record) {
        return record.getStringValue(COVERAGE_IDS);
    }

    public static void setCoverageIds(Record record, String coverageIds) {
        record.setFieldValue(COVERAGE_IDS, coverageIds);
    }

    public static String getCoverageFieldsList(Record record) {
        return record.getStringValue(COVERAGE_FIELDS_LIST);
    }

    public static void setCoverageFieldsList(Record record, String coverageFieldsList) {
        record.setFieldValue(COVERAGE_FIELDS_LIST, coverageFieldsList);
    }

    public static String getCoverageDbFieldsList(Record record) {
        return record.getStringValue(COVERAGE_DB_FIELDS_LIST);
    }

    public static void setCoverageDbFieldsList(Record record, String coverageDbFieldsList) {
        record.setFieldValue(COVERAGE_DB_FIELDS_LIST, coverageDbFieldsList);
    }

    public static String getToCovgBaseRecordIds(Record record) {
        return record.getStringValue(TO_COVG_BASE_RECORD_IDS);
    }

    public static void setToCovgBaseRecordIds(Record record, String coverageIds) {
        record.setFieldValue(TO_COVG_BASE_RECORD_IDS, coverageIds);
    }

    public static String getClaimProcessCode(Record record) {
        return record.getStringValue(CLAIM_PROCESS_CODE);
    }

    public static void setClaimProcessCode(Record record, String claimProcessCode) {
        record.setFieldValue(CLAIM_PROCESS_CODE, claimProcessCode);
    }

    public static String getIBNRCovgEffectiveFromDate(Record record) {
        return record.getStringValue(IBNR_COVG_EFFECTIVE_FROM_DATE);
    }

    public static void setIBNRCovgEffectiveFromDate(Record record, String IBNRCovgEffectiveFromDate) {
        record.setFieldValue(IBNR_COVG_EFFECTIVE_FROM_DATE, IBNRCovgEffectiveFromDate);
    }

    public static String getIBNRCovgEffectiveToDate(Record record) {
        return record.getStringValue(IBNR_COVG_EFFECTIVE_TO_DATE);
    }

    public static void setIBNRCovgEffectiveToDate(Record record, String IBNRCovgEffectiveToDate) {
        record.setFieldValue(IBNR_COVG_EFFECTIVE_TO_DATE, IBNRCovgEffectiveToDate);
    }

    public static void setIBNRCovgB(Record record, YesNoFlag retroB) {
        record.setFieldValue(IBNR_COVG_B, retroB);
    }

    public static YesNoFlag getIBNRCovgB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(IBNR_COVG_B));
    }

    public static String getMainCoverageBaseRecordId(Record record) {
        return record.getStringValue(MAIN_COVERAGE_BASE_RECORD_ID);
    }

    public static boolean hasMainCoverageBaseRecordId(Record record) {
        return record.hasStringValue(MAIN_COVERAGE_BASE_RECORD_ID);
    }

    public static void setMainCoverageBaseRecordId(Record record, String mainCoverageBaseRecordId) {
        record.setFieldValue(MAIN_COVERAGE_BASE_RECORD_ID, mainCoverageBaseRecordId);
    }

    protected static void setCoverageManualUpdatableFields() {
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("Coverage_Limit_Code", "CoverageLimitCode");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("Shared_Limit_B", "sharedLimitsB");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("Retroactive_Date", "retroDate");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("Effective_To_Date", "coverageEffectiveToDate");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("rating_Basis", "ratingBasis");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("incident_limit", "incidentLimit");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("extended_aggregate_limit", "extendedAggregateLimit");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("rate_payor_depend_code", "ratePayorDependCode");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("annual_base_rate", "annualBaseRate");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("sublimit_b", "sublimitB");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("Prioracts_Retro_Date", "prioractsRetroDate");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("pcf_county_code", "pcfCountyCode");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("pcf_participation_date", "pcfParticipationDate");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("deductible_comp_fk", "deductibleComponentId");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("covg_part_shared_limit_b", "covgPartSharedLimitB");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("char1", "char1");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("char2", "char2");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("char3", "char3");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("num1", "num1");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("num2", "num2");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("num3", "num3");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("date1", "date1");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("date2", "date2");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("date3", "date3");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("default_amt_of_insurance", "defaultAmountOfInsurance");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("addtl_amt_of_insurance", "addtlAmountOfInsurance");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("loss_of_income_days", "lossOfIncomeDays");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("Split_Retro_Date", "splitRetroDate");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("claims_made_date", "claimsMadeDate");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("exposure_unit", "exposureUnit");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("Building_Rate", "buildingRate");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("Used_For_Forecast_B", "usedForForecastB");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("direct_primary_b", "directPrimaryB");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("symbol", "symbol");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("limit_erosion_code", "limitErosionCode");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("cm_conv_date", "cmConvDate");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("cm_conv_override_date", "cmConvOverrideDate");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("oc_conv_date", "ocConvDate");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("oc_conv_override_date", "ocConvOverrideDate");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("manual_incident_limit", "manualIncidentLimit");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("manual_aggregate_limit", "manualAggregateLimit");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("manual_ded_sir_code", "manualDedSirCode");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("manual_ded_sir_inc_value", "manualDedSirIncValue");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("manual_ded_sir_agg_value", "manualDedSirAggValue");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("indemnity_type", "indemnityType");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("per_day_limit", "perDayLimit");
        COVERAGE_MANUAL_UPDATABLE_FIELDS.put("claim_process_code", "claimProcessCode");
    }

    public static List getCoverageManualUpdatableFieldsList() {
        String coverageManualUpdatableFields;
        String coverageManualUpdatableDbFields;
        List coverageManualUpdatableFieldsList = new ArrayList();
        List coverageManualUpdatableDbFieldsList = new ArrayList();
        List list = new ArrayList();
        Set<String> set = COVERAGE_MANUAL_UPDATABLE_FIELDS.keySet();

        setCoverageManualUpdatableFields();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            String key = (String)it.next();
            coverageManualUpdatableDbFieldsList.add(key);
            coverageManualUpdatableFieldsList.add(COVERAGE_MANUAL_UPDATABLE_FIELDS.get(key));
        }

        coverageManualUpdatableFields = StringUtils.arrayToDelimited((String[])coverageManualUpdatableFieldsList.toArray(new String[coverageManualUpdatableFieldsList.size()]), ",");
        coverageManualUpdatableFields = coverageManualUpdatableFields.substring(1);
        coverageManualUpdatableFields = coverageManualUpdatableFields.substring(0, coverageManualUpdatableFields.length() - 1);
        list.add(coverageManualUpdatableFields);
        coverageManualUpdatableDbFields = StringUtils.arrayToDelimited((String[])coverageManualUpdatableDbFieldsList.toArray(new String[coverageManualUpdatableDbFieldsList.size()]), ",");
        coverageManualUpdatableDbFields = coverageManualUpdatableDbFields.substring(1);
        coverageManualUpdatableDbFields = coverageManualUpdatableDbFields.substring(0, coverageManualUpdatableDbFields.length() - 1);
        list.add(coverageManualUpdatableDbFields);

        return list;
    }
}
