package dti.pm.riskmgr;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.pm.busobjs.PMStatusCode;
import dti.oasis.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Helper constants and set/get methods to access Risk Fields.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 26, 2007
 *
 * @author Sharon Ma
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/18/2011       dzhang      116263 - Add hasRiskEffectiveFromDate & hasRiskEffectiveToDate.
 * 07/04/2011       ryzhao      121160 - Add hasEntityId.
 * 08/17/2011       ryzhao      121160 - Rollback the changes.
 * 08/06/2013       jshen       146027 - Add primaryCovgExists
 * 10/23/2013       xnie        148246 - Added updateExpB and get()/set().
 * 01/09/2014       xnie        148083 - Added fields and get()/set() for maintaining risk detail function.
 * 03/18/2014       xnie        152969 - Added newRiskB and get()/set().
 * 05/06/2014       xnie        154373 - Changed riskSumOrDtlB to riskSumB.
 * 07/17/2014       jyang       154149 - Added isCopyAddrPhoneAvailable.
 * 12/26/2014       xnie        156995 - Added RISK_TABLE_NAME/TABLE_NAME/EXPIRE_B/RISK_IDS/RISK_FIELDS_LIST
 *                                       /RISK_DB_FIELDS_LIST fields. Added a map RISK_MANUAL_UPDATABLE_FIELDS.
 * 09/11/2015       tzeng       164679 - Added RISK_PKS/RECORD_MODE_CODE.
 * 11/27/2015       wdang       166922 - modified setRiskManualUpdatableFields to add revenue_band.
 * 12/25/2015       tzeng       166924 - Modified setRiskManualUpdatableFields to add ALTERNATIVE_RATING_METHOD.
 * 08/05/2016       fcb         177135 - new field added.
 * 09/06/2016       lzhang      179346 - add baseCode.
 * 07/17/2017       wrong       168374 - Added PCF_RISK_COUNTY, PCF_RISK_CLASS and IS_FUND_STATE fields.
 * 07/05/2018       ryzhao      187070 - Added suppress indicator related fields.
 * ---------------------------------------------------
 */
public class RiskFields {

    public static final String RISK_ID = "riskId";
    public static final String RISK_BASE_RECORD_ID = "riskBaseRecordId";
    public static final String AFTER_IMAGE_RECORD_B = "afterImageRecordB";
    public static final String BASE_RECORD_B = "baseRecordB";
    public static final String COVERAGE_PART_BASE_RECORD_ID = "coveragePartBaseRecordId";
    public static final String ENTITY_ID = "entityId";
    public static final String ENTITY_TYPE = "entityType";
    public static final String LOCATION = "location";
    public static final String PRACTICE_STATE_CODE = "practiceStateCode";
    public static final String PRIMARY_RISK_B = "primaryRiskB";
    public static final String RISK_COUNTY = "riskCounty";
    public static final String RISK_EFFECTIVE_FROM_DATE = "riskEffectiveFromDate";
    public static final String RISK_EFFECTIVE_TO_DATE = "riskEffectiveToDate";
    public static final String ORIG_RISK_EFFECTIVE_FROM_DATE = "origRiskEffectiveFromDate";
    public static final String ORIG_RISK_EFFECTIVE_TO_DATE = "origRiskEffectiveToDate";
    public static final String RISK_BASE_EFFECTIVE_TO_DATE = "riskBaseEffectiveToDate";
    public static final String RISK_NAME = "riskName";
    public static final String RISK_NAME_GH = "riskName_GH";
    public static final String RISK_NAME_HREF = "riskNameHref";
    public static final String RISK_STATUS = "riskStatus";
    public static final String RISK_TYPE_CODE = "riskTypeCode";
    public static final String RISK_TYPE_DESC = "riskTypeDesc";
    public static final String SLOT_ID = "slotId";
    public static final String ADD_CODE = "addCode";
    public static final String BASE_CODE = "baseCode";
    public static final String SQUARE_FOOTAGE = "squareFootage";
    public static final String ROLLING_IBNR_B = "rollingIbnrB";
    public static final String ORIG_ROLLING_IBNR_B = "origRollingIbnrB";
    public static final String OFFICIAL_RECORD_ID = "officialRecordId";
    public static final String RISK_SOCIETY_ID = "riskSocietyId";
    public static final String RISK_FIRST_NAME = "riskFirstName";
    public static final String RISK_LAST_NAME = "riskLastName";
    public static final String FTE_EQUIVALENT = "fteEquivalent";
    public static final String FTE_FULL_TIME_HRS = "fteFullTimeHrs";
    public static final String FTE_PART_TIME_HRS = "ftePartTimeHrs";
    public static final String FTE_PER_DIEM_HRS = "ftePerDiemHrs";
    public static final String PRIMARY_COVG_EXISTS = "primaryCovgExists";
    public static final String UPDATE_EXP_B = "updateExpB";
    public static final String INITIAL_LOADING_B = "initialLoadingB";
    public static final String INITIAL_LOADING_POLICY_ID = "initialLoadingPolicyId";
    public static final String IS_COPY_ADDR_PHONE_AVAILABLE = "isCopyAddrPhoneAvailable";
    public static final String RISK_TABLE_NAME = "RISK";
    public static final String TABLE_NAME = "tableName";
    public static final String EXPIRE_B = "expireB";
    public static final String RISK_IDS = "ids";
    public static final String RISK_FIELDS_LIST = "riskFieldsList";
    public static final String RISK_DB_FIELDS_LIST = "riskDbFieldsList";
    public static final String RISK_PKS = "riskPks";
    public static final String RECORD_MODE_CODE = "recordModeCode";
    public static final String ALTERNATIVE_RATING_METHOD = "alternativeRatingMethod";
    public static final String PCF_RISK_COUNTY = "pcfRiskCounty";
    public static final String PCF_RISK_CLASS = "pcfRiskClass";
    public static final String IS_FUND_STATE = "isFundState";

    // for copy risk addresses and phone numbers
    public static final String TYPE = "type";
    public static final String ADDRESS_TYPE = "ADDRESS";
    public static final String PHONE_TYPE = "PHONE";
    public static final String FOREIGN_ZIP = "foreignZip";
    public static final String ZIP_CODE = "zipCode";
    public static final String ADDRESS_PHONE_IDS = "addressPhoneIds";
    public static final String RISK_ENTITY_IDS = "riskEntityIds";
    public static final String CHANGE_EFFECITVE_DATE = "changeEffectiveDate";

    // for maintaining risk detail function
    public static final String RISK_DETAIL_B = "riskDetailB";
    public static final String RISK_DETAIL_ID = "riskDetailId";
    public static final String UPDATE_IND = "UPDATE_IND";
    public static final String EDIT_IND = "EDIT_IND";
    public static final String SAVE_CLOSE_B = "saveCloseB";
    public static final String SAVED_B = "savedB";
    public static final String CACHED_RISK_RECORD = "cachedRiskRecord";
    public static final String SLOT_OCCUPANT_B = "slotOccupantB";
    public static final String RISK_SUM_B = "riskSumB";
    public static final String NEW_RISK_B = "newRiskB";

    public static final String IS_REINSTATE_IBNR_RISK_VALID = "isReinstateIbnrRiskValid";
    public static final String RISK_EFF_FROM_DATE = "riskEffFromDate";
    public static final String IS_GR1_COMP_VISIBLE = "isGr1CompVisible";
    public static final String IS_GR1_COMP_EDITABLE = "isGr1CompEditable";
    public static final String IS_GR2_COMP_EDITABLE = "isGr2CompEditable";
    public static final String EXCLUDE_COMP_GR1_B = "excludeCompGr1B";
    public static final String EXCLUDE_COMP_GR2_B = "excludeCompGr2B";
    public static final String ORIG_EXCLUDE_COMP_GR1_B = "origExcludeCompGr1B";
    public static final String ORIG_EXCLUDE_COMP_GR2_B = "origExcludeCompGr2B";

    public static Map<String, String> RISK_MANUAL_UPDATABLE_FIELDS = new HashMap<String, String>();

    public static String getRiskSocietyId(Record record) {
        return record.getStringValue(RISK_SOCIETY_ID);
    }

    public static void setRiskSocietyId(Record record, String riskSocietyId) {
        record.setFieldValue(RISK_SOCIETY_ID, riskSocietyId);
    }

    public static String getFteFullTimeHrs(Record record) {
        return record.getStringValue(FTE_FULL_TIME_HRS);
    }

    public static void setFteFullTimeHrs(Record record, String fteFullTimeHrs) {
        record.setFieldValue(FTE_FULL_TIME_HRS, fteFullTimeHrs);
    }

    public static String getFtePartTimeHrs(Record record) {
        return record.getStringValue(FTE_PART_TIME_HRS);
    }

    public static void setFtePartTimeHrs(Record record, String ftePartTimeHrs) {
        record.setFieldValue(FTE_PART_TIME_HRS, ftePartTimeHrs);
    }

    public static String getFtePerDiemHrs(Record record) {
        return record.getStringValue(FTE_PER_DIEM_HRS);
    }

    public static void setFtePerDiemHrs(Record record, String ftePerDiemHrs) {
        record.setFieldValue(FTE_PER_DIEM_HRS, ftePerDiemHrs);
    }

    public static String getFteEquivalent(Record record) {
        return record.getStringValue(FTE_EQUIVALENT);
    }

    public static void setFteEquivalent(Record record, String fteEquivalent) {
        record.setFieldValue(FTE_EQUIVALENT, fteEquivalent);
    }

    public static String getOfficialRecordId(Record record) {
        return record.getStringValue(OFFICIAL_RECORD_ID);
    }

    public static void setOfficialRecordId(Record record, String officialRecordId) {
        record.setFieldValue(OFFICIAL_RECORD_ID, officialRecordId);
    }

    public static String getRiskId(Record record) {
        return record.getStringValue(RISK_ID);
    }

    public static void setRiskId(Record record, String riskId) {
        record.setFieldValue(RISK_ID, riskId);
    }

    public static String getRiskBaseRecordId(Record record) {
        return record.getStringValue(RISK_BASE_RECORD_ID);
    }

    public static void setRiskBaseRecordId(Record record, String riskBaseRecordId) {
        record.setFieldValue(RISK_BASE_RECORD_ID, riskBaseRecordId);
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

    public static void setCoveragePartBaseRecordId(Record record, String coveragePartBaseRecordId) {
        record.setFieldValue(COVERAGE_PART_BASE_RECORD_ID, coveragePartBaseRecordId);
    }

    public static String getEntityId(Record record) {
        return record.getStringValue(ENTITY_ID);
    }

    public static void setEntityId(Record record, String entityId) {
        record.setFieldValue(ENTITY_ID, entityId);
    }

    public static void setEntityType(Record record, String entityType) {
        record.setFieldValue(ENTITY_TYPE, entityType);
    }

    public static String getLocation(Record record) {
        return record.getStringValue(LOCATION);
    }

    public static void setLocation(Record record, String location) {
        record.setFieldValue(LOCATION, location);
    }

    public static String getPracticeStateCode(Record record) {
        return record.getStringValue(PRACTICE_STATE_CODE);
    }

    public static void setPracticeStateCode(Record record, String practiceStateCode) {
        record.setFieldValue(PRACTICE_STATE_CODE, practiceStateCode);
    }

    public static YesNoFlag getPrimaryRiskB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(PRIMARY_RISK_B));
    }

    public static void setPrimaryRiskB(Record record, YesNoFlag primaryRiskB) {
        record.setFieldValue(PRIMARY_RISK_B, primaryRiskB);
    }

    public static String getRiskCounty(Record record) {
        return record.getStringValue(RISK_COUNTY);
    }

    public static void setRiskCounty(Record record, String riskCounty) {
        record.setFieldValue(RISK_COUNTY, riskCounty);
    }

    public static String getRiskEffectiveFromDate(Record record) {
        return record.getStringValue(RISK_EFFECTIVE_FROM_DATE);
    }

    public static void setRiskEffectiveFromDate(Record record, String riskEffectiveFromDate) {
        record.setFieldValue(RISK_EFFECTIVE_FROM_DATE, riskEffectiveFromDate);
    }

    public static boolean hasRiskEffectiveFromDate(Record record) {
        return record.hasStringValue(RISK_EFFECTIVE_FROM_DATE);
    }

    public static void setRiskEffectiveToDate(Record record, String riskEffectiveToDate) {
        record.setFieldValue(RISK_EFFECTIVE_TO_DATE, riskEffectiveToDate);
    }

    public static String getRiskEffectiveToDate(Record record) {
        return record.getStringValue(RISK_EFFECTIVE_TO_DATE);
    }

    public static boolean hasRiskEffectiveToDate(Record record) {
        return record.hasStringValue(RISK_EFFECTIVE_TO_DATE);
    }

    public static String getOrigRiskEffectiveToDate(Record record) {
        return record.getStringValue(ORIG_RISK_EFFECTIVE_TO_DATE);
    }

    public static void setOrigRiskEffectiveToDate(Record record, String origRiskEffToDate) {
        record.setFieldValue(ORIG_RISK_EFFECTIVE_TO_DATE, origRiskEffToDate);
    }

    public static boolean hasRiskName(Record record) {
        return record.hasStringValue(RISK_NAME);
    }

    public static String getRiskName(Record record) {
        return record.getStringValue(RISK_NAME);
    }

    public static void setRiskName(Record record, String riskName) {
        record.setFieldValue(RISK_NAME, riskName);
    }

    public static void setRiskNameHref(Record record, String riskNameHref) {
        record.setFieldValue(RISK_NAME_HREF, riskNameHref);
    }

    public static boolean hasRiskStatus(Record record) {
        return record.hasStringValue(RISK_STATUS);
    }

    public static PMStatusCode getRiskStatus(Record record) {
        Object value = record.getFieldValue(RISK_STATUS);
        PMStatusCode result = null;
        if (value == null || value instanceof PMStatusCode) {
            result = (PMStatusCode) value;
        }
        else {
            result = PMStatusCode.getInstance(value.toString());
        }
        return result;
    }

    public static void setRiskStatus(Record record, PMStatusCode riskStatus) {
        record.setFieldValue(RISK_STATUS, riskStatus);
    }

    public static boolean hasRiskTypeCode(Record record) {
        return record.hasStringValue(RISK_TYPE_CODE);
    }

    public static String getRiskTypeCode(Record record) {
        return record.getStringValue(RISK_TYPE_CODE);
    }

    public static void setRiskTypeCode(Record record, String riskTypeCode) {
        record.setFieldValue(RISK_TYPE_CODE, riskTypeCode);
    }

    public static String getRiskTypeDesc(Record record) {
        return record.getStringValue(RISK_TYPE_DESC);
    }

    public static void setRiskTypeDesc(Record record, String riskTypeDesc) {
        record.setFieldValue(RISK_TYPE_DESC, riskTypeDesc);
    }

    public static String getSlotId(Record record) {
        return record.getStringValue(SLOT_ID);
    }

    public static void setSlotId(Record record, String slotId) {
        record.setFieldValue(SLOT_ID, slotId);
    }

    public static String getAddCode(Record record) {
        return record.getStringValue(ADD_CODE);
    }

    public static void setAddCode(Record record, String addCode) {
        record.setFieldValue(ADD_CODE, addCode);
    }

    public static void setRollingIbnrB(Record record, String rollingIbnrB) {
        record.setFieldValue(ROLLING_IBNR_B, rollingIbnrB);
    }

    public static String getRollingIbnrB(Record record) {
        return record.getStringValue(ROLLING_IBNR_B);
    }

    public static void setOrigRollingIbnrB(Record record, String origRollingIbnrB) {
        record.setFieldValue(ORIG_ROLLING_IBNR_B, origRollingIbnrB);
    }

    public static String getOrigRollingIbnrB(Record record) {
        return record.getStringValue(ORIG_ROLLING_IBNR_B);
    }

    public static String getRiskBaseEffectiveToDate(Record record) {
        return record.getStringValue(RISK_BASE_EFFECTIVE_TO_DATE);
    }

    public static void setRiskFirstName(Record record, String riskFirstName) {
        record.setFieldValue(RISK_FIRST_NAME, riskFirstName);
    }

    public static String getRiskFirstName(Record record) {
        return record.getStringValue(RISK_FIRST_NAME);
    }

    public static void setRiskLastName(Record record, String lastName) {
        record.setFieldValue(RISK_LAST_NAME, lastName);
    }

    public static String getLastName(Record record) {
        return record.getStringValue(RISK_LAST_NAME);
    }

    public static String getEntityType(Record record) {
        return record.getStringValue(ENTITY_TYPE);
    }

    public static String getOrigRiskEffectiveFromDate(Record record) {
        return record.getStringValue(ORIG_RISK_EFFECTIVE_FROM_DATE);
    }

    public static void setOrigRiskEffectiveFromDate(Record record, String origRiskEffFromDate) {
        record.setFieldValue(ORIG_RISK_EFFECTIVE_FROM_DATE, origRiskEffFromDate);
    }

    public static String getType(Record record) {
        return record.getStringValue(TYPE);
    }

    public static String getZipCode(Record record) {
        return record.getStringValue(ZIP_CODE);
    }

    public static void setForeignZip(Record record, String foreignZip) {
        record.setFieldValue(FOREIGN_ZIP, foreignZip);
    }

    public static String getAddressPhoneIds(Record record) {
        return record.getStringValue(ADDRESS_PHONE_IDS);
    }

    public static void setAddressPhoneIds(Record record, String addressPhoneIds) {
        record.setFieldValue(ADDRESS_PHONE_IDS, addressPhoneIds);
    }

    public static String getRiskEntityIds(Record record) {
        return record.getStringValue(RISK_ENTITY_IDS);
    }

    public static String getChangeEffectiveDate(Record record) {
        return record.getStringValue(CHANGE_EFFECITVE_DATE);
    }

    public static YesNoFlag getIsReinstateIbnrRiskValid(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(IS_REINSTATE_IBNR_RISK_VALID));
    }

    public static YesNoFlag getPrimaryCovgExists(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(PRIMARY_COVG_EXISTS));
    }

    public static void setPrimaryCovgExists(Record record, YesNoFlag primaryCoverageExists) {
        record.setFieldValue(PRIMARY_COVG_EXISTS, primaryCoverageExists);
    }

    public static YesNoFlag getUpdateExpB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(UPDATE_EXP_B));
    }

    public static void setUpdateExpB(Record record, YesNoFlag updateExpB) {
        record.setFieldValue(UPDATE_EXP_B, updateExpB);
    }

    public static YesNoFlag getRiskDetailB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(RISK_DETAIL_B));
    }

    public static void setRiskDetailB(Record record, YesNoFlag riskDetailB) {
        record.setFieldValue(RISK_DETAIL_B, riskDetailB);
    }

    public static String getRiskDetailId(Record record) {
        return record.getStringValue(RISK_DETAIL_ID);
    }

    public static void setRiskDetailId(Record record, String riskDetailId) {
        record.setFieldValue(RISK_DETAIL_ID, riskDetailId);
    }

    public static String getUpdateInd(Record record) {
        return record.getStringValue(UPDATE_IND);
    }

    public static void setUpdateInd(Record record, String updateInd) {
        record.setFieldValue(UPDATE_IND, updateInd);
    }

    public static String getEditInd(Record record) {
        return record.getStringValue(EDIT_IND);
    }

    public static void setEditInd(Record record, String updateInd) {
        record.setFieldValue(EDIT_IND, updateInd);
    }

    public static String getSaveCloseB(Record record) {
        return record.getStringValue(SAVE_CLOSE_B);
    }

    public static void setSaveCloseB(Record record, String saveCloseB) {
        record.setFieldValue(SAVE_CLOSE_B, saveCloseB);
    }

    public static String getSavedB(Record record) {
        return record.getStringValue(SAVED_B);
    }

    public static void setSavedB(Record record, String savedB) {
        record.setFieldValue(SAVED_B, savedB);
    }

    public static String getSlotOccupantB(Record record) {
        return record.getStringValue(SLOT_OCCUPANT_B);
    }

    public static void setSlotOccupantB(Record record, String slotOccupantB) {
        record.setFieldValue(SLOT_OCCUPANT_B, slotOccupantB);
    }

    public static String getRiskSumB(Record record) {
        return record.getStringValue(RISK_SUM_B);
    }

    public static void setRiskSumB(Record record, String riskSumB) {
        record.setFieldValue(RISK_SUM_B, riskSumB);
    }

    public static String getNewRiskB(Record record) {
        return record.getStringValue(NEW_RISK_B);
    }

    public static void setNewRiskB(Record record, String newRiskB) {
        record.setFieldValue(NEW_RISK_B, newRiskB);
    }

    public static String getIsCopyAddrPhoneAvailable(Record record){
        return record.getStringValue(IS_COPY_ADDR_PHONE_AVAILABLE);
    }

    public static void setIsCopyAddrPhoneAvailable(Record record, String isCopyAddrPhoneAvailable){
        record.setFieldValue(IS_COPY_ADDR_PHONE_AVAILABLE, isCopyAddrPhoneAvailable);
    }

    public static String getTableName(Record record) {
        return record.getStringValue(TABLE_NAME);
    }

    public static void setTableName(Record record, String tableName) {
        record.setFieldValue(TABLE_NAME, tableName);
    }

    public static String getExpireB(Record record) {
        return record.getStringValue(EXPIRE_B);
    }

    public static void setExpireB(Record record, String expireB) {
        record.setFieldValue(EXPIRE_B, expireB);
    }

    public static String getRiskIds(Record record) {
        return record.getStringValue(RISK_IDS);
    }

    public static void setRiskIds(Record record, String riskIds) {
        record.setFieldValue(RISK_IDS, riskIds);
    }

    public static String getRiskFieldsList(Record record) {
        return record.getStringValue(RISK_FIELDS_LIST);
    }

    public static void setRiskFieldsList(Record record, String riskFieldsList) {
        record.setFieldValue(RISK_FIELDS_LIST, riskFieldsList);
    }

    public static String getRiskDbFieldsList(Record record) {
        return record.getStringValue(RISK_DB_FIELDS_LIST);
    }

    public static void setRiskDbFieldsList(Record record, String riskDbFieldsList) {
        record.setFieldValue(RISK_DB_FIELDS_LIST, riskDbFieldsList);
    }

    public static String getAlternativeRatingMethod(Record record) {
        return record.getStringValue(ALTERNATIVE_RATING_METHOD);
    }

    public static void setAlternativeRatingMethod(Record record, String alternativeRatingMethod) {
        record.setFieldValue(ALTERNATIVE_RATING_METHOD, alternativeRatingMethod);
    }

    public static String getPcfRiskCounty(Record record) {
        return record.getStringValue(PCF_RISK_COUNTY);
    }

    public static void setPcfRiskCounty(Record record, String pcfRiskCounty) {
        record.setFieldValue(PCF_RISK_COUNTY, pcfRiskCounty);
    }

    public static String getPcfRiskClass(Record record) {
        return record.getStringValue(PCF_RISK_CLASS);
    }

    public static void setPcfRiskClass(Record record, String pcfRiskClass) {
        record.setFieldValue(PCF_RISK_CLASS, pcfRiskClass);
    }

    public static String getBaseCode(Record record) {
        return record.getStringValue(BASE_CODE);
    }

    public static void setBaseCode(Record record, String baseCode) {
        record.setFieldValue(BASE_CODE, baseCode);
    }

    public static boolean hasEntityId(Record record) {
        return record.hasStringValue(ENTITY_ID);
    }

    public static void setRiskEffFromDate(Record record, String riskEffFromDate) {
        record.setFieldValue(RISK_EFF_FROM_DATE, riskEffFromDate);
    }

    public static void setIsGr1CompVisible(Record record, YesNoFlag isGr1CompVisible) {
        record.setFieldValue(IS_GR1_COMP_VISIBLE, isGr1CompVisible);
    }

    public static void setIsGr1CompEditable(Record record, YesNoFlag isGr1CompEditable) {
        record.setFieldValue(IS_GR1_COMP_EDITABLE, isGr1CompEditable);
    }

    public static void setIsGr2CompEditable(Record record, YesNoFlag isGr2CompEditable) {
        record.setFieldValue(IS_GR2_COMP_EDITABLE, isGr2CompEditable);
    }

    public static String getExcludeCompGr1B(Record record) {
        return record.getStringValue(EXCLUDE_COMP_GR1_B);
    }

    public static void setExcludeCompGr1B(Record record, String excludeCompGr1B) {
        record.setFieldValue(EXCLUDE_COMP_GR1_B, excludeCompGr1B);
    }

    public static String getExcludeCompGr2B(Record record) {
        return record.getStringValue(EXCLUDE_COMP_GR2_B);
    }

    public static void setExcludeCompGr2B(Record record, String excludeCompGr2B) {
        record.setFieldValue(EXCLUDE_COMP_GR2_B, excludeCompGr2B);
    }

    public static String getOrigExcludeCompGr1B(Record record) {
        return record.getStringValue(ORIG_EXCLUDE_COMP_GR1_B);
    }

    public static void setOrigExcludeCompGr1B(Record record, String origExcludeCompGr1B) {
        record.setFieldValue(ORIG_EXCLUDE_COMP_GR1_B, origExcludeCompGr1B);
    }

    public static String getOrigExcludeCompGr2B(Record record) {
        return record.getStringValue(ORIG_EXCLUDE_COMP_GR2_B);
    }

    public static void setOrigExcludeCompGr2B(Record record, String origExcludeCompGr2B) {
        record.setFieldValue(ORIG_EXCLUDE_COMP_GR2_B, origExcludeCompGr2B);
    }

    protected static void setRiskManualUpdatableFields() {
        RISK_MANUAL_UPDATABLE_FIELDS.put("Primary_Risk_B", "primaryRiskB");
        RISK_MANUAL_UPDATABLE_FIELDS.put("County_Code_Used_to_Rate", "riskCounty");
        RISK_MANUAL_UPDATABLE_FIELDS.put("Slot_id", "slotId");
        RISK_MANUAL_UPDATABLE_FIELDS.put("Effective_To_Date", "riskEffectiveToDate");
        RISK_MANUAL_UPDATABLE_FIELDS.put("Risk_Cls_Used_To_Rate", "riskClass");
        RISK_MANUAL_UPDATABLE_FIELDS.put("Practice_State_Code", "practiceStateCode");
        RISK_MANUAL_UPDATABLE_FIELDS.put("risk_society_fk", "riskSocietyId");
        RISK_MANUAL_UPDATABLE_FIELDS.put("note", "note");
        RISK_MANUAL_UPDATABLE_FIELDS.put("fte_equivalent", "fteEquivalent");
        RISK_MANUAL_UPDATABLE_FIELDS.put("fte_full_time_hrs", "fteFullTimeHrs");
        RISK_MANUAL_UPDATABLE_FIELDS.put("fte_part_time_hrs", "ftePartTimeHrs");
        RISK_MANUAL_UPDATABLE_FIELDS.put("fte_per_diem_hrs", "ftePerDiemHrs");
        RISK_MANUAL_UPDATABLE_FIELDS.put("rolling_ibnr_b", "rollingIbnrB");
        RISK_MANUAL_UPDATABLE_FIELDS.put("Property_Risk_Fk", "location");
        RISK_MANUAL_UPDATABLE_FIELDS.put("Exposure_Basis_Code", "exposureBasis");
        RISK_MANUAL_UPDATABLE_FIELDS.put("Exposure_Unit", "exposureUnit");
        RISK_MANUAL_UPDATABLE_FIELDS.put("Risk_Sub_Cls_Used_To_Rate", "riskSubClassCode");
        RISK_MANUAL_UPDATABLE_FIELDS.put("Square_Footage", "squareFootage");
        RISK_MANUAL_UPDATABLE_FIELDS.put("Number_Of_Employed_Doctor", "numberEmployedDoctor");
        RISK_MANUAL_UPDATABLE_FIELDS.put("Number_Of_Vap", "numberVap");
        RISK_MANUAL_UPDATABLE_FIELDS.put("Number_Of_Bed", "numberBed");
        RISK_MANUAL_UPDATABLE_FIELDS.put("Average_Daily_Census", "averageDailyCensus");
        RISK_MANUAL_UPDATABLE_FIELDS.put("Annual_Outpatient_visit", "annualOutpatientVisit");
        RISK_MANUAL_UPDATABLE_FIELDS.put("Number_Of_Qb_Delivery", "numberQbDelivery");
        RISK_MANUAL_UPDATABLE_FIELDS.put("Risk_Process_Code", "riskProcessCode");
        RISK_MANUAL_UPDATABLE_FIELDS.put("group_Start_Date", "groupStartDate");
        RISK_MANUAL_UPDATABLE_FIELDS.put("rating_basis", "ratingBasis");
        RISK_MANUAL_UPDATABLE_FIELDS.put("number_of_ext_bed", "numberExtBed");
        RISK_MANUAL_UPDATABLE_FIELDS.put("number_of_skill_bed", "numberSkillBed");
        RISK_MANUAL_UPDATABLE_FIELDS.put("number_of_inpatient_surg", "numberInpatientSurg");
        RISK_MANUAL_UPDATABLE_FIELDS.put("number_of_outpatient_surg", "numberOutpatientSurg");
        RISK_MANUAL_UPDATABLE_FIELDS.put("number_of_er_visit", "numberErVisit");
        RISK_MANUAL_UPDATABLE_FIELDS.put("char1", "char1");
        RISK_MANUAL_UPDATABLE_FIELDS.put("char2", "char2");
        RISK_MANUAL_UPDATABLE_FIELDS.put("char3", "char3");
        RISK_MANUAL_UPDATABLE_FIELDS.put("char4", "char4");
        RISK_MANUAL_UPDATABLE_FIELDS.put("num1", "num1");
        RISK_MANUAL_UPDATABLE_FIELDS.put("num2", "num2");
        RISK_MANUAL_UPDATABLE_FIELDS.put("num3", "num3");
        RISK_MANUAL_UPDATABLE_FIELDS.put("num4", "num4");
        RISK_MANUAL_UPDATABLE_FIELDS.put("date1", "date1");
        RISK_MANUAL_UPDATABLE_FIELDS.put("date2", "date2");
        RISK_MANUAL_UPDATABLE_FIELDS.put("date3", "date3");
        RISK_MANUAL_UPDATABLE_FIELDS.put("date4", "date4");
        RISK_MANUAL_UPDATABLE_FIELDS.put("teaching_b", "teachingB");
        RISK_MANUAL_UPDATABLE_FIELDS.put("risk_deductible_fk", "riskDeductibleId");
        RISK_MANUAL_UPDATABLE_FIELDS.put("frame_type_code", "frameType");
        RISK_MANUAL_UPDATABLE_FIELDS.put("protection_class_code", "protectionClass");
        RISK_MANUAL_UPDATABLE_FIELDS.put("building_class_code", "buildingClass");
        RISK_MANUAL_UPDATABLE_FIELDS.put("sprinkler_b", "sprinklerB");
        RISK_MANUAL_UPDATABLE_FIELDS.put("alternate_speciality_code", "alternateSpecialityCode");
        RISK_MANUAL_UPDATABLE_FIELDS.put("city_code", "city");
        RISK_MANUAL_UPDATABLE_FIELDS.put("Procedure_Codes", "procedureCodes");
        RISK_MANUAL_UPDATABLE_FIELDS.put("Rate_Mature_B", "rateMatureB");
        RISK_MANUAL_UPDATABLE_FIELDS.put("rolling_ibnr_status", "rollingIbnrStatus");
        RISK_MANUAL_UPDATABLE_FIELDS.put("CM_Year", "cmYear");
        RISK_MANUAL_UPDATABLE_FIELDS.put("Construction_Type", "constructionType");
        RISK_MANUAL_UPDATABLE_FIELDS.put("Roof_Type", "roofType");
        RISK_MANUAL_UPDATABLE_FIELDS.put("Floor_Type", "floorType");
        RISK_MANUAL_UPDATABLE_FIELDS.put("Building_Type", "buildingType");
        RISK_MANUAL_UPDATABLE_FIELDS.put("Building_Value", "buildingValue");
        RISK_MANUAL_UPDATABLE_FIELDS.put("Use_type", "useType");
        RISK_MANUAL_UPDATABLE_FIELDS.put("Protection_Type", "protectionType");
        RISK_MANUAL_UPDATABLE_FIELDS.put("Fire_Service_Type", "fireServiceType");
        RISK_MANUAL_UPDATABLE_FIELDS.put("Hydrants_Type", "hydrantsType");
        RISK_MANUAL_UPDATABLE_FIELDS.put("Security_Type", "securityType");
        RISK_MANUAL_UPDATABLE_FIELDS.put("Moonlighting_B", "moonlightingB");
        RISK_MANUAL_UPDATABLE_FIELDS.put("fleet_b", "fleetB");
        RISK_MANUAL_UPDATABLE_FIELDS.put("year_of_vehicle", "yearOfVehicle");
        RISK_MANUAL_UPDATABLE_FIELDS.put("original_cost_new", "originalCostNew");
        RISK_MANUAL_UPDATABLE_FIELDS.put("make_of_vehicle", "makeOfVehicle");
        RISK_MANUAL_UPDATABLE_FIELDS.put("model_of_vehicle", "modelOfVehicle");
        RISK_MANUAL_UPDATABLE_FIELDS.put("vin", "vin");
        RISK_MANUAL_UPDATABLE_FIELDS.put("subClass", "subClass");
        RISK_MANUAL_UPDATABLE_FIELDS.put("desc_of_object", "descOfObject");
        RISK_MANUAL_UPDATABLE_FIELDS.put("location", "premiseLocation");
        RISK_MANUAL_UPDATABLE_FIELDS.put("scorecard_eligible_b", "scorecardEligibleB");
        RISK_MANUAL_UPDATABLE_FIELDS.put("org_hier_lvl1_fk", "orgHierLvl1Fk");
        RISK_MANUAL_UPDATABLE_FIELDS.put("org_hier_lvl2_fk", "orgHierLvl2Fk");
        RISK_MANUAL_UPDATABLE_FIELDS.put("org_hier_lvl3_fk", "orgHierLvl3Fk");
        RISK_MANUAL_UPDATABLE_FIELDS.put("org_hier_lvl4_fk", "orgHierLvl4Fk");
        RISK_MANUAL_UPDATABLE_FIELDS.put("org_hier_lvl5_fk", "orgHierLvl5Fk");
        RISK_MANUAL_UPDATABLE_FIELDS.put("revenue_band", "revenueBand");
        RISK_MANUAL_UPDATABLE_FIELDS.put("rating_tier", "ratingTier");
        RISK_MANUAL_UPDATABLE_FIELDS.put("alternative_rating_method", "alternativeRatingMethod");
        RISK_MANUAL_UPDATABLE_FIELDS.put("pcf_county_code_used_to_rate", "pcfRiskCounty");
        RISK_MANUAL_UPDATABLE_FIELDS.put("pcf_risk_cls_used_to_rate", "pcfRiskClass");
        RISK_MANUAL_UPDATABLE_FIELDS.put("exclude_comp_gr1_b", "excludeCompGr1B");
        RISK_MANUAL_UPDATABLE_FIELDS.put("exclude_comp_gr2_b", "excludeCompGr2B");
    }

    public static List getRiskManualUpdatableFieldsList() {
        String riskManualUpdatableFields;
        String riskManualUpdatableDbFields;
        List riskManualUpdatableFieldsList = new ArrayList();
        List riskManualUpdatableDbFieldsList = new ArrayList();
        List list = new ArrayList();
        Set<String> set = RISK_MANUAL_UPDATABLE_FIELDS.keySet();

        setRiskManualUpdatableFields();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            String key = (String)it.next();
            riskManualUpdatableDbFieldsList.add(key);
            riskManualUpdatableFieldsList.add(RISK_MANUAL_UPDATABLE_FIELDS.get(key));
        }

        riskManualUpdatableFields = StringUtils.arrayToDelimited((String[])riskManualUpdatableFieldsList.toArray(new String[riskManualUpdatableFieldsList.size()]), ",");
        riskManualUpdatableFields = riskManualUpdatableFields.substring(1);
        riskManualUpdatableFields = riskManualUpdatableFields.substring(0, riskManualUpdatableFields.length() - 1);
        list.add(riskManualUpdatableFields);
        riskManualUpdatableDbFields = StringUtils.arrayToDelimited((String[])riskManualUpdatableDbFieldsList.toArray(new String[riskManualUpdatableDbFieldsList.size()]), ",");
        riskManualUpdatableDbFields = riskManualUpdatableDbFields.substring(1);
        riskManualUpdatableDbFields = riskManualUpdatableDbFields.substring(0, riskManualUpdatableDbFields.length() - 1);
        list.add(riskManualUpdatableDbFields);

        return list;
    }
}
