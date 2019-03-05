package dti.pm.riskmgr;

import dti.oasis.recordset.Record;
import dti.oasis.busobjs.YesNoFlag;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 18, 2008
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/20/2010       syang       107937 - Added SPECIALTY_B and NETWORK_B.
 * 07/11/2016       lzhang      177681 - Added TO_COVG_BASE_IDS.
 * 07/17/2017       wrong       168374 - Added PCF_COUNTY_B, PCF_SPECIALTY_B and IS_FUND_STATE.
 * ---------------------------------------------------
 */
public class RiskCopyFields {
    public static final String TO_RISK_ID = "toRiskId";
    public static final String STATUS = "status";
    public static final String TO_COVERAGE_BASE_RECORD_ID = "toCoverageBaseRecordId";
    public static final String VALIDATE_ERROR_FLAG = "validateErrorFlag";
    public static final String COPY_FAILURE_FLAG = "copyFailureFlag";
    public static final String DELETE_TARGET_FAILURE_FLAG = "deleteTargetFailureFlag";
    public static final String DELETE_SOURCE_FAILURE_FLAG = "deleteSourceFailureFlag";
    public static final String IS_DELETE_SOURCE = "isDeleteSource";
    public static final String IS_CONFIRMED = "isConfirmed";
    public static final String TO_RISK_BASE_RECORD_ID = "toRiskBaseRecordId";
    public static final String TERM_EFF = "termEff";
    public static final String TERM_EXP = "termExp";
    public static final String TARGET_VALIDATE_RESULT = "targetValidateResult";
    public static final String FROM_RISK_CLASS_COUNT = "fromRiskClassCount";
    public static final String IS_LAST_SELECTED_RISK = "isLastSelectedRisk";
    public static final String IS_FIRST_SELECTED_RISK = "isFirstSelectedRisk";
    public static final String IS_VALID_TO_COPY = "isValidToCopy";
    public static final String IS_VALID_TO_DELETE = "isValidToDelete";
    public static final String IS_AFFILIATION_VALID_TO_COPY = "isAffiliationValidToCopy";
    public static final String STATE_B = "stateB";
    public static final String COUNTY_B = "countyB";
    public static final String SPECIALTY_B  = "specialityB";
    public static final String NETWORK_B  = "networkB";
    public static final String COPY_LEVEL = "copyLevel";
    public static final String BS_FIELD_NAME_LIST = "bsFieldNameList";
    public static final String CALL_FROM = "callFr";
    public static final String TO_COVG_BASE_RECORD_IDS = "toCovgBaseRecordIds";
    public static final String NEW_CM_COVERAGEB = "newCmCoverageB";
    public static final String COVERAGE_PKS = "coveragePks";
    public static final String PCF_COUNTY_B = "pcfCountyB";
    public static final String PCF_SPECIALTY_B = "pcfSpecialityB";
    public static final String IS_FUND_STATE = "isFundState";
    
    public static YesNoFlag getIsAffiliationValidToCopy(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(IS_AFFILIATION_VALID_TO_COPY));
    }

    public static void setIsAffiliationValidToCopy(Record record, YesNoFlag isAffiliationValidToCopy) {
        record.setFieldValue(IS_AFFILIATION_VALID_TO_COPY, isAffiliationValidToCopy);
    }

    public static YesNoFlag getIsValidToDelete(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(IS_VALID_TO_DELETE));
    }

    public static void setIsValidToDelete(Record record, YesNoFlag isValidToDelete) {
        record.setFieldValue(IS_VALID_TO_DELETE, isValidToDelete);
    }

    public static YesNoFlag getIsValidToCopy(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(IS_VALID_TO_COPY));
    }

    public static void setIsValidToCopy(Record record, YesNoFlag isValidToCopy) {
        record.setFieldValue(IS_VALID_TO_COPY, isValidToCopy);
    }

    public static YesNoFlag getIsFirstSelectedRisk(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(IS_FIRST_SELECTED_RISK));
    }

    public static void setIsFirstSelectedRisk(Record record, YesNoFlag isFirstSelectedRisk) {
        record.setFieldValue(IS_FIRST_SELECTED_RISK, isFirstSelectedRisk);
    }

    public static YesNoFlag getIsLastSelectedRisk(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(IS_LAST_SELECTED_RISK));
    }

    public static void setIsLastSelectedRisk(Record record, YesNoFlag isLastSelectedRisk) {
        record.setFieldValue(IS_LAST_SELECTED_RISK, isLastSelectedRisk);
    }

    public static String getFromRiskClassCount(Record record) {
        return record.getStringValue(FROM_RISK_CLASS_COUNT);
    }

    public static void setFromRiskClassCount(Record record, String fromRiskClassCount) {
        record.setFieldValue(FROM_RISK_CLASS_COUNT, fromRiskClassCount);
    }

    public static String getTermExp(Record record) {
        return record.getStringValue(TERM_EXP);
    }

    public static void setTermExp(Record record, String termExp) {
        record.setFieldValue(TERM_EXP, termExp);
    }

    public static String getTermEff(Record record) {
        return record.getStringValue(TERM_EFF);
    }

    public static void setTermEff(Record record, String termEff) {
        record.setFieldValue(TERM_EFF, termEff);
    }

    public static String getToRiskBaseRecordId(Record record) {
        return record.getStringValue(TO_RISK_BASE_RECORD_ID);
    }

    public static void setToRiskBaseRecordId(Record record, String toRiskBaseRecordId) {
        record.setFieldValue(TO_RISK_BASE_RECORD_ID, toRiskBaseRecordId);
    }

    public static YesNoFlag getIsConfirmed(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(IS_CONFIRMED));
    }

    public static void setIsConfirmed(Record record, YesNoFlag isConfirmed) {
        record.setFieldValue(IS_CONFIRMED, isConfirmed);
    }

    public static YesNoFlag getIsDeleteSource(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(IS_DELETE_SOURCE));
    }

    public static void setIsDeleteSource(Record record, YesNoFlag isDeleteSource) {
        record.setFieldValue(IS_DELETE_SOURCE, isDeleteSource);
    }

    public static YesNoFlag getDeleteTargetFailureFlag(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(DELETE_TARGET_FAILURE_FLAG));
    }

    public static void setDeleteTargetFailureFlag(Record record, YesNoFlag deleteTargetFailureFlag) {
        record.setFieldValue(DELETE_TARGET_FAILURE_FLAG, deleteTargetFailureFlag);
    }

    public static YesNoFlag getDeleteSourceFailureFlag(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(DELETE_SOURCE_FAILURE_FLAG));
    }

    public static void setDeleteSourceFailureFlag(Record record, YesNoFlag deleteSourceFailureFlag) {
        record.setFieldValue(DELETE_SOURCE_FAILURE_FLAG, deleteSourceFailureFlag);
    }

    public static YesNoFlag getValidateErrorFlag(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(VALIDATE_ERROR_FLAG));
    }

    public static void setValidateErrorFlag(Record record, YesNoFlag validateErrorFlag) {
        record.setFieldValue(VALIDATE_ERROR_FLAG, validateErrorFlag);
    }

    public static YesNoFlag getCopyFailureFlag(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(COPY_FAILURE_FLAG));
    }

    public static void setCopyFailureFlag(Record record, YesNoFlag copyFailureFlag) {
        record.setFieldValue(COPY_FAILURE_FLAG, copyFailureFlag);
    }

    public static String getToCoverageBaseRecordId(Record record) {
        return record.getStringValue(TO_COVERAGE_BASE_RECORD_ID);
    }

    public static void setToCoverageBaseRecordId(Record record, String toCoverageBaseRecordId) {
        record.setFieldValue(TO_COVERAGE_BASE_RECORD_ID, toCoverageBaseRecordId);
    }

    public static YesNoFlag getStatus(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(STATUS));
    }

    public static void setStatus(Record record, YesNoFlag status) {
        record.setFieldValue(STATUS, status);
    }

    public static String getToRiskId(Record record) {
        return record.getStringValue(TO_RISK_ID);
    }

    public static void setToRiskId(Record record, String toRiskId) {
        record.setFieldValue(TO_RISK_ID, toRiskId);
    }

    public static String getStateB(Record record) {
        return record.getStringValue(STATE_B);
    }

    public static void setStateB(Record record, String stateB) {
        record.setFieldValue(STATE_B, stateB);
    }

    public static String getCountyB(Record record) {
        return record.getStringValue(COUNTY_B);
    }

    public static void setCountyB(Record record, String countyB) {
        record.setFieldValue(COUNTY_B, countyB);
    }

    public static String getSpecialtyB(Record record) {
        return record.getStringValue(SPECIALTY_B);
    }

    public static void setSpecialtyB(Record record, String specialtyB) {
        record.setFieldValue(SPECIALTY_B, specialtyB);
    }

    public static String getNetworkB(Record record) {
        return record.getStringValue(NETWORK_B);
    }

    public static void setNetworkB(Record record, String networkB) {
        record.setFieldValue(NETWORK_B, networkB);
    }

    public static String getCopyLevel(Record record) {
       return record.getStringValue(COPY_LEVEL); 
    }

    public static String getBsFieldNameList(Record record) {
       return record.getStringValue(BS_FIELD_NAME_LIST);
    }

    public static String getToCovgBaseRecordIds(Record record) {
        return record.getStringValue(TO_COVG_BASE_RECORD_IDS);
    }

    public static void setToCovgBaseRecordIds(Record record, String coverageIds) {
        record.setFieldValue(TO_COVG_BASE_RECORD_IDS, coverageIds);
    }

    public static String getNewCmCoverageB(Record record) {
        return record.getStringValue(NEW_CM_COVERAGEB);
    }

    public static void setNewCmCoverageB(Record record, String newCmCoverageB) {
        record.setFieldValue(NEW_CM_COVERAGEB, newCmCoverageB);
    }

    public static String getCoveragePks(Record record) {
        return record.getStringValue(COVERAGE_PKS);
    }

    public static void setCoveragePks(Record record, String coveragePks) {
        record.setFieldValue(COVERAGE_PKS, coveragePks);
    }

    public static String getPcfCountyB(Record record) {
        return record.getStringValue(PCF_COUNTY_B);
    }

    public static void setPcfCountyB(Record record, String pcfCountyB) {
        record.setFieldValue(PCF_COUNTY_B, pcfCountyB);
    }

    public static String getPcfSpecialtyB(Record record) {
        return record.getStringValue(PCF_SPECIALTY_B);
    }

    public static void setPcfSpecialtyB(Record record, String pcfSpecialtyB) {
        record.setFieldValue(PCF_SPECIALTY_B, pcfSpecialtyB);
    }

    public static String getIsFundState(Record record) {
        return record.getStringValue(IS_FUND_STATE);
    }

    public static void setIsFundState(Record record, String isFundState) {
        record.setFieldValue(IS_FUND_STATE, isFundState);
    }

    public class CopyLevelValues {
        public static final String RISK_LEVEL = "RISK";
        public static final String COVERAGE_LEVEL = "COVERAGE";
        public static final String COVERAGE_CLASS_LEVEL = "COVERAGE_CLASS";
        public static final String COMPONENT_LEVEL = "COMPONENT";
    }

    public class CallFromValues {
        public static final String BS = "BS";
    }
}
