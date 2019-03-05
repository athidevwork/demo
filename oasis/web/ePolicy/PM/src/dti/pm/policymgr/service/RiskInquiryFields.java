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
 * 08/05/2016       fcb         177135: new field added.
 * 07/05/2017       wrong       168374: added new fields.
 * 09/21/2017       eyin        169483: Added new fields for risk additional exposure.
 * 11/09/2018       wrong       194062: Added new field isSetupDefaultRisk.
 * ---------------------------------------------------
 */

public class RiskInquiryFields {
    public static final String RISK_NUMBER_ID = "riskbaserecordid";
    public static final String ENTITY_ID = "entityid";
    public static final String ENTITY_TYPE = "entityType";
    public static final String RISK_ID = "riskid";
    public static final String PRIMARY_RISK_B = "primaryriskb";
    public static final String EFFECTIVE_FROM_DATE = "riskeffectivefromdate";
    public static final String EFFECTIVE_TO_DATE = "riskeffectivetodate";
    public static final String ORIG_RISK_EFFECTIVE_FROM_DATE = "origriskeffectivefromdate";
    public static final String ORIG_RISK_EFFECTIVE_TO_DATE = "origriskeffectivetodate";
    public static final String PRACTICE_STATE_CODE = "practicestatecode";
    public static final String RISK_COUNTY = "riskcounty";
    public static final String RISK_STATUS = "riskstatus";
    public static final String RISK_TYPE_CODE = "riskTypeCode";
    public static final String RISK_CLASS_CODE = "riskclass";
    public static final String RISK_SUB_CLASS_CODE = "risksubclasscode";
    public static final String ALTERNATE_SPECIALTY_CODE = "alternatespecialtycode";
    public static final String RISK_DEDUCTIBLE_ID = "riskdeductibleid";
    public static final String TEACHING_B = "teachingb";
    public static final String PROCEDURE_CODES = "procedureCodes";
    public static final String RATE_MATURE_B = "rateMatureB";
    public static final String MOONLIGHTING_B = "moonlightingB";
    public static final String CM_YEAR = "cmYear";
    public static final String IBNR_B = "rollingIbnrB";
    public static final String IBNR_STATUS = "rollingIbnrStatus";
    public static final String SCORECARD_B = "scorecardEligibleB";
    public static final String CITY_CODE = "city";
    public static final String NOTE = "note";
    public static final String FTE_EQUIVALENT = "fteequivalent";
    public static final String FTE_FULL_TIME = "ftefulltimehrs";
    public static final String FTE_PART_TIME = "fteparttimehrs";
    public static final String FTE_PER_DIEM = "fteperdiemhrs";
    public static final String EXPOSURE_UNIT = "exposureunit";
    public static final String EXPOSURE_BASIS = "exposurebasis";
    public static final String NUMBER_OF_EMPLOYED_DOCTOR = "numberemployeddoctor";
    public static final String SQUARE_FOOTAGE = "squarefootage";
    public static final String NUMBER_VAP = "numberVap";
    public static final String NUMBER_BED = "numberbed";
    public static final String NUMBER_EXT_BED = "numberextbed";
    public static final String NUMBER_SKILL_BED = "numberskillbed";
    public static final String AVERAGE_DAILY_CENSUS = "averagedailycensus";
    public static final String ANNUAL_PATIENT_VISIT = "annualoutpatientvisit";
    public static final String NUMBER_QB_DELIVERY = "numberqbdelivery";
    public static final String NUMBER_IMPATIENT_SURG = "numberinpatientsurg";
    public static final String ANNUAL_OUTPATIENT_VISIT = "annualoutpatientvisit";
    public static final String NUMBER_ER_VISIT = "numberervisit";
    public static final String BUILDING_CLASS = "buildingclass";
    public static final String BUILDING_VALUE = "buildingValue";
    public static final String BUILDING_TYPE = "buildingType";
    public static final String USE_TYPE = "useType";
    public static final String FRAME_TYPE = "frametype";
    public static final String PROTECTION_CLASS = "protectionclass";
    public static final String SPRINKLER_B = "sprinklerb";
    public static final String CONSTRUCTION_TYPE = "constructionType";
    public static final String ROOF_TYPE = "roofType";
    public static final String FLOOR_TYPE = "floorType";
    public static final String PROTECTION_TYPE = "protectionType";
    public static final String FIRE_SERVICE_TYPE = "fireServiceType";
    public static final String HYDRANTS_TYPE = "hydrantsType";
    public static final String SECURITY_TYPE = "securityType";
    public static final String LOCATION = "location";
    public static final String LOCATION_DESCRIPTION = "descOfObject";
    public static final String FLEET_B = "fleetB";
    public static final String MAKE_OF_VEHICLE = "makeOfVehicle";
    public static final String VEHICLE_SUBCLASS = "subclass";
    public static final String MODEL_OF_VEHICLE = "modelOfVehicle";
    public static final String YEAR_OF_VEHICLE = "yearOfVehicle";
    public static final String ORIGINAL_COST_NEW = "originalCostNew";
    public static final String VIN = "vin";
    public static final String DATE1 = "date1";
    public static final String DATE2 = "date2";
    public static final String DATE3 = "date3";
    public static final String DATE4 = "date4";
    public static final String NUM1 = "num1";
    public static final String NUM2 = "num2";
    public static final String NUM3 = "num3";
    public static final String NUM4 = "num4";
    public static final String CHAR1 = "char1";
    public static final String CHAR2 = "char2";
    public static final String CHAR3 = "char3";
    public static final String CHAR4 = "char4";
    public static final String RECORD_MODE_CODE = "recordModeCode";
    public static final String OFFICIAL_RECORD_ID = "officialRecordId";
    public static final String AFTER_IMAGE_RECORD_B = "afterImageRecordB";
    public static final String SOURCE_ID = "sourceId";
    public static final String SOURCE_TABLE  = "sourceTable";
    public static final String RISK_SOURCE_TABLE = "RISK";
    public static final String BASE_RECORD_B = "baseRecordB";
    public static final String FIRST_RISK_B = "firstRiskB";
    public static final String ADD_CODE = "addCode";
    public static final String SLOT_ID = "slotId";
    public static final String PERSON_REFERENCE = "personReference";
    public static final String ORGANIZATION_REFERENCE = "organizationReference";
    public static final String PROPERTY_REFERENCE = "propertyReference";
    public static final String SKIP_DEFAULT_INSURED_CREATION = "SkipDefaultInsuredCreation";
    public static final String CAN_RESET_PRIMARY_INDICATOR = "canResetPrimaryIndicator";
    public static final String ALTERNATIVE_RATING_METHOD = "alternativeRatingMethod";
    public static final String REVENUE_BAND = "revenueBand";
    public static final String RATING_TIER = "ratingTier";
    public static final String PCF_RISK_COUNTY = "pcfriskcounty";
    public static final String PCF_RISK_CLASS_CODE = "pcfriskclass";
    public static final String IS_SETUP_DEFAULT_RISK = "isSetupDefaultRisk";

    public static final String RISK_ADDTL_EXPOSURE_ID = "riskAddtlExposureId";
    public static final String RISK_ADDTL_EXP_BASE_RECORD_ID = "riskAddtlExpBaseRecordId";
    public static final String PRODUCT_COVERAGE_CODE = "productCoverageCode";
    public static final String COVERAGE_LIMIT_CODE = "coverageLimitCode";
    public static final String PERCENT_PRACTICE = "percentPractice";
    public static final String ADDRESS_Id = "addressId";
    public static final String ROW_STATUS = "rowStatus";
    public static final String ORIG_PERCENT_PRACTICE = "origPercentPractice";
    public static final String ORIG_COVERAGE_LIMIT_CODE = "origCoverageLimitCode";
    public static final String ORIG_EFFECTIVE_TO_DATE = "origEffectiveToDate";
}
