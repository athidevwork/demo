package dti.pm.policymgr.service;

/**
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * User: fcbibire
 * Date: Feb 22, 2012
 */
/*
 *
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/04/2016       wdang       169005: added new fields.
 * 11/07/2016       jyang2      181092: Added POLICY_FORM_CODE.
 * 06/15/2017       wrong       186163: Added CLAIM_PROCESS_CODE.
 * ---------------------------------------------------
 */
public class CoverageInquiryFields {
    public static final String COVERAGE_NUMBER_ID = "coveragebaserecordid";
    public static final String COVERAGE_CLASS_NUMBER_ID = "coverageClassBaseRecordId";
    public static final String PARENT_COVERAGE_NUMBER_ID = "parentCoverageBaseRecordId";
    public static final String COVERAGE_CODE = "coverageCode";
    public static final String PRODUCT_COVERAGE_CODE = "productCoverageCode";
    public static final String PRODUCT_COVERAGE_CLASS_CODE = "productCoverageClassCode";
    public static final String RISK_BASE_RECORD_ID = "riskBaseRecordId";
    public static final String COVERAGE_ID = "coverageId";
    public static final String COVERAGE_CLASS_ID = "coverageClassId";
    public static final String COVERAGE_CLASS_BASE_ID = "coverageClassBaseRecordId";
    public static final String PRIMARY_COVERAGE_INDICATOR = "primaryCoverageB";
    public static final String RECORD_MODE_CODE = "recordModeCode";
    public static final String COVERAGE_STATUS = "coverageStatus";
    public static final String COVERAGE_CLASS_STATUS = "coverageClassStatus";
    public static final String COVERAGE_BASE_EFFECTIVE_FROM_DATE = "coverageBaseEffectiveFromDate";
    public static final String COVERAGE_BASE_EFFECTIVE_TO_DATE = "coverageBaseEffectiveToDate";
    public static final String EFFECTIVE_FROM_DATE = "coverageEffectiveFromDate";
    public static final String EFFECTIVE_TO_DATE = "coverageEffectiveToDate";
    public static final String CLASS_EFFECTIVE_FROM_DATE = "coverageClassEffectiveFromDate";
    public static final String CLASS_EFFECTIVE_TO_DATE = "coverageClassEffectiveToDate";
    public static final String ORIG_EFFECTIVE_TO_DATE = "origCoverageEffectiveToDate";
    public static final String COVERAGE_LIMIT_CODE = "coverageLimitCode";
    public static final String SHARED_LIMIT_B = "sharedlimitsb";
    public static final String INCIDENT_LIMIT = "incidentLimit";
    public static final String AGGREGATE_LIMIT = "extendedAggregateLimit";
    public static final String PRODUCT_SUBLIMIT_B = "productSublimitB";
    public static final String LIMIT_EROSION_CODE = "limitErosionCode";
    public static final String RETROACTIVE_DATE = "retrodate";
    public static final String ORIG_RETROACTIVE_DATE = "origretrodate";
    public static final String SPLIT_RETROACTIVE_DATE = "splitRetroDate";
    public static final String CLAIMS_MADE_DATE = "claimsMadeDate";
    public static final String RATE_PAYOR_DEPEND_CODE = "ratePayorDependCode";
    public static final String ORIG_RATE_PAYOR_DEPEND_CODE = "origRatePayorDependCode";
    public static final String CANCELLATION_METHOD_CODE = "cancellationMethodCode";
    public static final String ANNUAL_BASE_RATE = "annualBaseRate";
    public static final String ORIG_ANNUAL_BASE_RATE = "origAnnualBaseRate";
    public static final String DEFAULT_AMOUNT_OF_INSURANCE = "defaultAmountOfInsurance";
    public static final String ADDL_AMOUNT_OF_INSURANCE = "addtlAmountOfInsurance";
    public static final String LOSS_OF_INCOME_DAYS = "lossOfIncomeDays";
    public static final String EXPOSURE_UNIT = "exposureUnit";
    public static final String BUILDING_RATE = "buildingRate";
    public static final String USED_FOR_FORECAST_B = "usedForForecastB";
    public static final String DIRECT_PRIMARY_B = "directPrimaryB";
    public static final String SYMBOL = "symbol";
    public static final String CM_CONV_DATE = "cmConvDate";
    public static final String CM_CONV_OVERRIDE_DATE = "cmConvOverrideDate";
    public static final String OC_CONV_DATE = "ocConvDate";
    public static final String OC_CONV_OVERRIDE_DATE = "ocConvOverrideDate";
    public static final String PCF_COUNTY_CODE = "pcfCountyCode";
    public static final String PCF_PARTICIPATION_DATE = "pcfParticipationDate";
    public static final String DEDUCTIBLE_COMPONENT_ID = "deductibleComponentId";
    public static final String DATE1 = "date1";
    public static final String DATE2 = "date2";
    public static final String DATE3 = "date3";
    public static final String NUM1 = "num1";
    public static final String NUM2 = "num2";
    public static final String NUM3 = "num3";
    public static final String CHAR1 = "char1";
    public static final String CHAR2 = "char2";
    public static final String CHAR3 = "char3";
    public static final String ORIG_COVERAGE_EFFECTIVE_TO_DATE = "origCoverageEffectiveToDate";
    public static final String IS_MANUALLY_RATED = "isManuallyRated";
    public static final String SKIP_DEFAULT_COVERAGE_CREATION = "SkipDefaultMedicalMalpracticeCoverageCreation";
    public static final String SKIP_DEFAULT_SUB_COVERAGE_CREATION = "SkipDefaultMedicalMalpracticeSubCoverageCreation";
    public static final String MANUAL_INCIDENT_LIMIT  = "manualIncidentLimit";
    public static final String MANUAL_AGGREGATE_LIMIT  = "manualAggregateLimit";
    public static final String MANUAL_DED_SIR_CODE  = "manualDedSirCode";
    public static final String MANUAL_DED_SIR_INC_VALUE  = "manualDedSirIncValue";
    public static final String MANUAL_DED_SIR_AGG_VALUE  = "manualDedSirAggValue";
    public static final String INDEMNITY_TYPE  = "indemnityType";
    public static final String PER_DAY_LIMIT = "perDayLimit";
    public static final String POLICY_FORM_CODE = "policyFormCode";
    public static final String CLAIM_PROCESS_CODE = "claimProcessCode";
}
