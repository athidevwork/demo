package dti.pm.componentmgr;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 7, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/18/2007       fcb         getCoverageBaseRecordId and setCoverageBaseRecordId added.
 * 03/26/2008       fcb         several original values added.
 * 04/15/2011       ryzhao      116160 - Added field noteLink.
 * 05/04/2011       syang       120017 - Rollback the changes of issue 116160.
 * 05/05/2011       dzhang      117614 - Added field changeType.
 * 06/22/2011       wqfu        103810 - Added longDescription, compLongDescription.
 * 12/19/2012       awu         138624 - Added inner class ComponentCodeValues to define constant value.
 * 01/23/2013       adeng       141183 - Added field note and noteB with setter and getter method.
 * 01/25/2013       adeng       141183 - Roll backed last change.
 * 09/26/2013       xnie        146797 - Added field officialRecordId and get/set methods.
 * 10/08/2014       kxiang      157857 - Added field operation,copyAll and get/set methods for operation .
 * 06/28/2017       tzeng       186273 - Added field mainCoverageBaseRecordId with set/get methods.
 * ---------------------------------------------------
 */
public class ComponentFields {
    public static final String EFFECTIVE_FROM_DATE = "componentEffectiveFromDate";
    public static final String EFFECTIVE_TO_DATE = "componentEffectiveToDate";
    public static final String ORIG_EFFECTIVE_TO_DATE = "origComponentEffectiveToDate";
    public static final String COMPONENT_VALUE = "componentValue";
    public static final String ORIG_COMPONENT_VALUE = "origComponentValue";
    public static final String COMPONENT_TYPE_CODE = "componentTypeCode";
    public static final String ADVANCE_CM_YEAR_B = "advanceCmYearB";
    public static final String APPLY_RISK_SUSPEND_B = "applyRiskSuspendB";
    public static final String ISSUE_COMPANY_ENTITY_ID = "issueCompanyEntityId";
    public static final String PERCENT_VALUE_B = "percentValueB";
    public static final String COVERAGE_COMPONENT_CODE = "code"; //"coverageComponentCode";
    public static final String PARENT_COVERAGE_COMPONENT_CODE = "componentParent"; //"parentCoverageComponentCode"
    public static final String POLICY_COV_COMPONENT_ID = "policyCovComponentId";
    public static final String PRODUCT_COV_COMPONENT_ID = "productCovComponentId";
    public static final String COMPONENT_CYCLE_DATE = "componentCycleDate";
    public static final String ORIG_COMPONENT_CYCLE_DATE = "origComponentCycleDate";
    public static final String SHORT_DESCRIPTION = "shortDescription";
    public static final String LONG_DESCRIPTION = "longDescription";
    public static final String COMP_LONG_DESCRIPTION = "compLongDescription";
    public static final String HIGH_VALUE = "highValue";
    public static final String LOW_VALUE = "lowValue";
    public static final String RENEWAL_B = "renewalB";
    public static final String ORIG_RENEWAL_B = "origRenewalB";
    public static final String CANCELWIPMODE = "cancelWipMode";
    public static final String CYCLE_YEARS = "cycleYears";
    public static final String CHECK_DT = "checkDt";
    public static final String COMPONENT_SIGN = "componentSign";
    public static final String COMPONENT_PARENT = "componentParent";
    public static final String COMPONENT_GROUP = "componentGroup";
    public static final String POL_COV_COMP_BASE_REC_ID = "polCovCompBaseRecId";
    public static final String EXPIRY_DATE_B = "expiryDateB";
    public static final String TERM_EFFECTIVE_TO_DATE = "termEffectiveTo";
    public static final String TERM_EFFECTIVE_FROM_DATE = "termEffectiveFrom";
    public static final String CYCLED_B = "cycledB";
    public static final String PART_TIME_B = "partTimeB";
    public static final String DEFAULT_DEPENDENT_COMPONENT_B = "defaultDependentComponentB";
    public static final String SCHEDULE_B = "scheduledB";
    public static final String COMPONENT_OWNER = "componentOwner";
    public static final String COMP_POLICY_TYPE_CODE = "compPolicyTypeCode";
    public static final String COMP_RISK_TYPE_CODE = "compRiskTypeCode";
    public static final String COMP_PRODUCT_COVERAGE_CODE = "compProductCoverageCode";
    public static final String COVERAGE_BASE_RECORD_ID = "coverageBaseRecordId";
    public static final String COMP_NOTE = "note";
    public static final String ORIGINAL_COMP_NOTE = "origNote";
    public static final String COMP_CHAR1 = "compChar1";
    public static final String ORIGINAL_COMP_CHAR1 = "origCompChar1";
    public static final String COMP_CHAR2 = "compChar2";
    public static final String ORIGINAL_COMP_CHAR2 = "origCompChar2";
    public static final String COMP_CHAR3 = "compChar3";
    public static final String ORIGINAL_COMP_CHAR3 = "origCompChar3";
    public static final String COMP_NUM1 = "compNum1";
    public static final String ORIGINAL_COMP_NUM1 = "origCompNum1";
    public static final String COMP_NUM2 = "compNum2";
    public static final String ORIGINAL_COMP_NUM2 = "origCompNum2";
    public static final String COMP_NUM3 = "compNum3";
    public static final String ORIGINAL_COMP_NUM3 = "origCompNum3";
    public static final String COMP_DATE1 = "compDate1";
    public static final String ORIGINAL_COMP_DATE1 = "origCompDate1";
    public static final String COMP_DATE2 = "compDate2";
    public static final String ORIGINAL_COMP_DATE2 = "origCompDate2";
    public static final String COMP_DATE3 = "compDate3";
    public static final String ORIGINAL_COMP_DATE3 = "origCompDate3";
    public static final String INC_VALUE = "incValue";
    public static final String ORIGINAL_INC_VALUE = "origIncValue";
    public static final String AGG_VALUE = "aggValue";
    public static final String ORIGINAL_AGG_VALUE = "origAggValue";
    public static final String CLASSIFICATION_CODE = "classificationCode";
    public static final String ORIGINAL_CLASSIFICATION_CODE = "origClassificationCode";
    public static final String COMPONENT_SEQUENCE_NO = "componentSequenceNo";
    public static final String SEQUENCE_NO = "sequenceNo";
    public static final String COMPONENT_ACTION = "componentAction";
    public static final String ORIGINAL_LOW_VALUE = "origLowValue";
    public static final String ORIGINAL_HIGH_VALUE = "origHighValue";
    public static final String CHANGE_TYPE = "changeType";
    public static final String CHG_COMP_DATE = "chgCompDate";
    public static final String COMPONENT_EFF_FROM_DATE = "componentEffFromDate";
    public static final String COMPONENT_EFF_TO_DATE = "componentEffToDate";
    public static final String OFFICIAL_RECORD_ID = "officialRecordId";
    public static final String OPERATION = "operation";
    public static final String COPY_ALL = "copyAll";
    public static final String MAIN_COVERAGE_BASE_RECORD_ID = "mainCoverageBaseRecordId";

    public static String getSequenceNo(Record record) {
        return record.getStringValue(SEQUENCE_NO);
    }

    public static void setSequenceNo(Record record, String sequenceNo) {
        record.setFieldValue(SEQUENCE_NO, sequenceNo);
    }

    public static String getComponentSequenceNo(Record record) {
        return record.getStringValue(COMPONENT_SEQUENCE_NO);
    }

    public static void setComponentSequenceNo(Record record, String componentSequenceNo) {
        record.setFieldValue(COMPONENT_SEQUENCE_NO, componentSequenceNo);
    }

    public static String getCompChar1(Record record) {
        return record.getStringValue(COMP_CHAR1);
    }

    public static void setCompChar1(Record record, String compChar1) {
        record.setFieldValue(COMP_CHAR1, compChar1);
    }

    public static String getOriginalCompChar1(Record record) {
        return record.getStringValue(ORIGINAL_COMP_CHAR1);
    }

    public static void setOriginalCompChar1(Record record, String originalCompChar1) {
        record.setFieldValue(ORIGINAL_COMP_CHAR1, originalCompChar1);
    }

    public static String getCompChar2(Record record) {
        return record.getStringValue(COMP_CHAR2);
    }

    public static void setCompChar2(Record record, String compChar2) {
        record.setFieldValue(COMP_CHAR2, compChar2);
    }

    public static String getOriginalCompChar2(Record record) {
        return record.getStringValue(ORIGINAL_COMP_CHAR2);
    }

    public static void setOriginalCompChar2(Record record, String originalCompChar2) {
        record.setFieldValue(ORIGINAL_COMP_CHAR2, originalCompChar2);
    }

    public static String getCompChar3(Record record) {
        return record.getStringValue(COMP_CHAR3);
    }

    public static void setCompChar3(Record record, String compChar3) {
        record.setFieldValue(COMP_CHAR3, compChar3);
    }

    public static String getOriginalCompChar3(Record record) {
        return record.getStringValue(ORIGINAL_COMP_CHAR3);
    }

    public static void setOriginalCompChar3(Record record, String originalCompChar3) {
        record.setFieldValue(ORIGINAL_COMP_CHAR3, originalCompChar3);
    }


    public static String getCompNum1(Record record) {
        return record.getStringValue(COMP_NUM1);
    }

    public static void setCompNum1(Record record, String compNum1) {
        record.setFieldValue(COMP_NUM1, compNum1);
    }

    public static String getOriginalCompNum1(Record record) {
        return record.getStringValue(ORIGINAL_COMP_NUM1);
    }

    public static void setOriginalCompNum1(Record record, String originalCompNum1) {
        record.setFieldValue(ORIGINAL_COMP_NUM1, originalCompNum1);
    }

    public static String getCompNum2(Record record) {
        return record.getStringValue(COMP_NUM2);
    }

    public static void setCompNum2(Record record, String compNum2) {
        record.setFieldValue(COMP_NUM2, compNum2);
    }

    public static String getOriginalCompNum2(Record record) {
        return record.getStringValue(ORIGINAL_COMP_NUM2);
    }

    public static void setOriginalCompNum2(Record record, String originalCompNum2) {
        record.setFieldValue(ORIGINAL_COMP_NUM2, originalCompNum2);
    }

    public static String getCompNum3(Record record) {
        return record.getStringValue(COMP_NUM3);
    }

    public static void setCompNum3(Record record, String compNum3) {
        record.setFieldValue(COMP_NUM3, compNum3);
    }

    public static String getOriginalCompNum3(Record record) {
        return record.getStringValue(ORIGINAL_COMP_NUM3);
    }

    public static void setOriginalCompNum3(Record record, String originalCompNum3) {
        record.setFieldValue(ORIGINAL_COMP_NUM3, originalCompNum3);
    }

    public static String getCompDate1(Record record) {
        return record.getStringValue(COMP_DATE1);
    }

    public static void setCompDate1(Record record, String cCompDate1) {
        record.setFieldValue(COMP_DATE1, cCompDate1);
    }

    public static String getOriginalCompDate1(Record record) {
        return record.getStringValue(ORIGINAL_COMP_DATE1);
    }

    public static void setOriginalCompDate1(Record record, String originalCompDate1) {
        record.setFieldValue(ORIGINAL_COMP_DATE1, originalCompDate1);
    }

    public static String getCompDate2(Record record) {
        return record.getStringValue(COMP_DATE2);
    }

    public static void setCompDate2(Record record, String compDate2) {
        record.setFieldValue(COMP_DATE2, compDate2);
    }

    public static String getOriginalCompDate2(Record record) {
        return record.getStringValue(ORIGINAL_COMP_DATE2);
    }

    public static void setOriginalCompDate2(Record record, String originalCompDate2) {
        record.setFieldValue(ORIGINAL_COMP_DATE2, originalCompDate2);
    }

    public static String getCompDate3(Record record) {
        return record.getStringValue(COMP_DATE3);
    }

    public static void setCompDate3(Record record, String compDate3) {
        record.setFieldValue(COMP_DATE3, compDate3);
    }

    public static String getOriginalCompDate3(Record record) {
        return record.getStringValue(ORIGINAL_COMP_DATE3);
    }

    public static void setOriginalCompDate3(Record record, String originalCompDate3) {
        record.setFieldValue(ORIGINAL_COMP_DATE3, originalCompDate3);
    }

    public static String getCompNote(Record record) {
        return record.getStringValue(COMP_NOTE);
    }

    public static void setCompNote(Record record, String compNote) {
        record.setFieldValue(COMP_NOTE, compNote);
    }

    public static String getOriginalCompNote(Record record) {
        return record.getStringValue(ORIGINAL_COMP_NOTE);
    }

    public static void setOriginalCompNote(Record record, String originalCompNote) {
        record.setFieldValue(ORIGINAL_COMP_NOTE, originalCompNote);
    }

    public static String getIncValue(Record record) {
        return record.getStringValue(INC_VALUE);
    }

    public static void setIncValue(Record record, String incValue) {
        record.setFieldValue(INC_VALUE, incValue);
    }

    public static String getOriginalIncValue(Record record) {
        return record.getStringValue(ORIGINAL_INC_VALUE);
    }

    public static void setOriginalIncValue(Record record, String originalIncValue) {
        record.setFieldValue(ORIGINAL_INC_VALUE, originalIncValue);
    }

    public static String getAggValue(Record record) {
        return record.getStringValue(AGG_VALUE);
    }

    public static void setAggValue(Record record, String aggValue) {
        record.setFieldValue(AGG_VALUE, aggValue);
    }

    public static String getOriginalAggValue(Record record) {
        return record.getStringValue(ORIGINAL_AGG_VALUE);
    }

    public static void setOriginalAggValue(Record record, String originalAggValue) {
        record.setFieldValue(ORIGINAL_AGG_VALUE, originalAggValue);
    }

    public static String getClassificationCode(Record record) {
        return record.getStringValue(CLASSIFICATION_CODE);
    }

    public static void setClassificationCode(Record record, String classificationCode) {
        record.setFieldValue(CLASSIFICATION_CODE, classificationCode);
    }

    public static String getOriginalClassificationCode(Record record) {
        return record.getStringValue(ORIGINAL_CLASSIFICATION_CODE);
    }

    public static void setOriginalClassificationCode(Record record, String originalClassificationCode) {
        record.setFieldValue(ORIGINAL_CLASSIFICATION_CODE, originalClassificationCode);
    }

    public static String getComponentOwner(Record record) {
        return record.getStringValue(COMPONENT_OWNER);
    }

    public static void setComponentOwner(Record record, String componentOwner) {
        record.setFieldValue(COMPONENT_OWNER, componentOwner);
    }

    public static String getParentCoverageComponentCode(Record record) {
        return record.getStringValue(PARENT_COVERAGE_COMPONENT_CODE);
    }

    public static void setParentCoverageComponentCode(Record record, String parentCovgComponentCode) {
        record.setFieldValue(PARENT_COVERAGE_COMPONENT_CODE, parentCovgComponentCode);
    }

    public static YesNoFlag getPartTimeB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(PART_TIME_B));
    }

    public static YesNoFlag getCycledB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(CYCLED_B));
    }

    public static void setCycledB(Record record, YesNoFlag cycledB) {
        record.setFieldValue(CYCLED_B, cycledB);
    }

    public static void setTermEffectiveToDate(Record record, String termEffectiveToDate) {
        record.setFieldValue(TERM_EFFECTIVE_TO_DATE, termEffectiveToDate);
    }

    public static void setTermEffectiveFromDate(Record record, String termEffectiveFromDate) {
        record.setFieldValue(TERM_EFFECTIVE_FROM_DATE, termEffectiveFromDate);
    }

    public static YesNoFlag getExpiryDateB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(EXPIRY_DATE_B));
    }

    public static void setExpiryDateB(Record record, YesNoFlag expiryDateB) {
        record.setFieldValue(EXPIRY_DATE_B, expiryDateB);
    }

    public static String getPolCovCompBaseRecId(Record record) {
        return record.getStringValue(POL_COV_COMP_BASE_REC_ID);
    }

    public static void setPolCovCompBaseRecId(Record record, String polCovCompBaseRecId) {
        record.setFieldValue(POL_COV_COMP_BASE_REC_ID, polCovCompBaseRecId);
    }

    public static String getProductCovComponentId(Record record) {
        return record.getStringValue(PRODUCT_COV_COMPONENT_ID);
    }

    public static void setProductCovComponentId(Record record, String productCovComponentId) {
        record.setFieldValue(PRODUCT_COV_COMPONENT_ID, productCovComponentId);
    }

    public static void setComponentGroup(Record record, String componentGroup) {
        record.setFieldValue(COMPONENT_GROUP, componentGroup);
    }

    public static String getComponentParent(Record record) {
        return record.getStringValue(COMPONENT_PARENT);
    }

    public static String getComponentSigh(Record record) {
        return record.getStringValue(COMPONENT_SIGN);
    }

    public static void setComponentSigh(Record record, String componentSign) {
        record.setFieldValue(COMPONENT_SIGN, componentSign);
    }

    public static String getCheckDt(Record record) {
        return record.getStringValue(CHECK_DT);
    }

    public static void setCheckDt(Record record, String checkDt) {
        record.setFieldValue(CHECK_DT, checkDt);
    }

    public static String getComponentCycleDateOrg(Record record) {
        return record.getStringValue(ORIG_COMPONENT_CYCLE_DATE);
    }

    public static String getCycleYears(Record record) {
        return record.getStringValue(CYCLE_YEARS);
    }

    public static void setCycleYears(Record record, String cycleYears) {
        record.setFieldValue(CYCLE_YEARS, cycleYears);
    }

    public static String getComponentTypeCode(Record record) {
        return record.getStringValue(COMPONENT_TYPE_CODE);
    }

    public static void setComponentTypeCode(Record record, String componentTypeCode) {
        record.setFieldValue(COMPONENT_TYPE_CODE, componentTypeCode);
    }

    public static String getCancelWipMode(Record record) {
        return record.getStringValue(CANCELWIPMODE);
    }

    public static void setCancelWipMode(Record record, String cancelWipMode) {
        record.setFieldValue(CANCELWIPMODE, cancelWipMode);
    }

    public static String getOrigRenewalB(Record record) {
        return record.getStringValue(ORIG_RENEWAL_B);
    }

    public static String getRenewalB(Record record) {
        return record.getStringValue(RENEWAL_B);
    }

    public static void setRenewalB(Record record, String renewalB) {
        record.setFieldValue(RENEWAL_B, renewalB);
    }

    public static String getHighValue(Record record) {
        return record.getStringValue(HIGH_VALUE);
    }

    public static void setHighValue(Record record, String highValue) {
        record.setFieldValue(HIGH_VALUE, highValue);
    }

    public static String getLowValue(Record record) {
        return record.getStringValue(LOW_VALUE);
    }

    public static void setLowValue(Record record, String lowValue) {
        record.setFieldValue(LOW_VALUE, lowValue);
    }

    public static String getShortDescription(Record record) {
        return record.getStringValue(SHORT_DESCRIPTION);
    }

    public static void setShortDescription(Record record, String shortDesc) {
        record.setFieldValue(SHORT_DESCRIPTION, shortDesc);
    }

    public static String getComponentCycleDate(Record record) {
        return record.getStringValue(COMPONENT_CYCLE_DATE);
    }

    public static void setComponentCycleDate(Record record, String componentCycleDate) {
        record.setFieldValue(COMPONENT_CYCLE_DATE, componentCycleDate);
    }

    public static String getPolicyCovComponentId(Record record) {
        return record.getStringValue(POLICY_COV_COMPONENT_ID);
    }

    public static void setPolicyCovComponentId(Record record, String policyCovComponentId) {
        record.setFieldValue(POLICY_COV_COMPONENT_ID, policyCovComponentId);
    }

    public static String getCoverageComponentCode(Record record) {
        return record.getStringValue(COVERAGE_COMPONENT_CODE);
    }

    public static void setCoverageComponentCode(Record record, String coverageComponentCode) {
        record.setFieldValue(COVERAGE_COMPONENT_CODE, coverageComponentCode);
    }

    public static YesNoFlag getPercentValueB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(PERCENT_VALUE_B));
    }

    public static void setPercentValueB(Record record, YesNoFlag percentValueB) {
        record.setFieldValue(PERCENT_VALUE_B, percentValueB);
    }

    public static String getIssureCompanyEntityId(Record record) {
        return record.getStringValue(ISSUE_COMPANY_ENTITY_ID);
    }

    public static void setIssureCompanyEntityId(Record record, String issureCompanyEntityId) {
        record.setFieldValue(ISSUE_COMPANY_ENTITY_ID, issureCompanyEntityId);
    }

    public static YesNoFlag getApplyRiskSuspendB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(APPLY_RISK_SUSPEND_B));
    }

    public static YesNoFlag getAdvanceCmYearB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(ADVANCE_CM_YEAR_B));
    }

    public static String getEffectiveFromDate(Record record) {
        return record.getStringValue(EFFECTIVE_FROM_DATE);
    }

    public static void setEffectiveFromDate(Record record, String effectiveFromDate) {
        record.setFieldValue(EFFECTIVE_FROM_DATE, effectiveFromDate);
    }

    public static String getOrigComponentValue(Record record) {
        return record.getStringValue(ORIG_COMPONENT_VALUE);
    }

    public static void setOrigComponentValue(Record record, String origCompValue) {
        record.setFieldValue(ORIG_COMPONENT_VALUE, origCompValue);
    }

    public static String getOrigEffectiveToDate(Record record) {
        return record.getStringValue(ORIG_EFFECTIVE_TO_DATE);
    }

    public static void setOrigEffectiveToDate(Record record, String origEffToDate) {
        record.setFieldValue(ORIG_EFFECTIVE_TO_DATE, origEffToDate);
    }

    public static String getEffectiveToDate(Record record) {
        return record.getStringValue(EFFECTIVE_TO_DATE);
    }

    public static void setEffectiveToDate(Record record, String effectiveToDate) {
        record.setFieldValue(EFFECTIVE_TO_DATE, effectiveToDate);
    }

    public static String getComponentValue(Record record) {
        return record.getStringValue(COMPONENT_VALUE);
    }

    public static void setComponentValue(Record record, String componentValue) {
        record.setFieldValue(COMPONENT_VALUE, componentValue);
    }

    public static String getScheduledB(Record record) {
        return record.getStringValue(SCHEDULE_B);
    }

    public static void setScheduledB(Record record, String scheduledB) {
        record.setFieldValue(SCHEDULE_B, scheduledB);
    }

    public static String getCompPolicyTypeCode(Record record) {
        return record.getStringValue(COMP_POLICY_TYPE_CODE);
    }

    public static void setCompPolicyTypeCode(Record record, String compPolicyTypeCode) {
        record.setFieldValue(COMP_POLICY_TYPE_CODE, compPolicyTypeCode);
    }

    public static String getCompRiskTypeCode(Record record) {
        return record.getStringValue(COMP_RISK_TYPE_CODE);
    }

    public static void setCompRiskTypeCode(Record record, String compRiskTypeCode) {
        record.setFieldValue(COMP_RISK_TYPE_CODE, compRiskTypeCode);
    }

    public static String getCompProductCoverageCode(Record record) {
        return record.getStringValue(COMP_PRODUCT_COVERAGE_CODE);
    }

    public static void setCompProductCoverageCode(Record record, String compProductCoverageCode) {
        record.setFieldValue(COMP_PRODUCT_COVERAGE_CODE, compProductCoverageCode);
    }

    public static String getCoverageBaseRecordId(Record record) {
        return record.getStringValue(COVERAGE_BASE_RECORD_ID);
    }

    public static void setCoverageBaseRecordId(Record record, String coverageBaseRecordId) {
        record.setFieldValue(COVERAGE_BASE_RECORD_ID, coverageBaseRecordId);
    }

    public static String getComponentAction(Record record) {
        return record.getStringValue(COMPONENT_ACTION);
    }

    public static void setComponentAction(Record record, String componentAction) {
        record.setFieldValue(COMPONENT_ACTION, componentAction);
    }

    public static String getOrigLowValue(Record record) {
        return record.getStringValue(ORIGINAL_LOW_VALUE);
    }

    public static void setOrigLowValue(Record record, String origLowValue) {
        record.setFieldValue(ORIGINAL_LOW_VALUE, origLowValue);
    }

    public static String getOrigHighValue(Record record) {
        return record.getStringValue(ORIGINAL_HIGH_VALUE);
    }

    public static void setOrigHighValue(Record record, String origHighValue) {
        record.setFieldValue(ORIGINAL_HIGH_VALUE, origHighValue);
    }

    public static String getChangeType(Record record) {
        return record.getStringValue(CHANGE_TYPE);
    }

    public static void setChangeType(Record record, String changeType) {
        record.setFieldValue(CHANGE_TYPE, changeType);
    }

    public static String getComponentEffFromDate(Record record) {
        return record.getStringValue(COMPONENT_EFF_FROM_DATE);
    }

    public static String getComponentEffToDate(Record record) {
        return record.getStringValue(COMPONENT_EFF_TO_DATE);
    }

    public static String getLongDescription(Record record) {
        return record.getStringValue(LONG_DESCRIPTION);
    }

    public static void setLongDescription(Record record, String longDesc) {
        record.setFieldValue(LONG_DESCRIPTION, longDesc);
    }

    public static String getCompLongDescription(Record record) {
        return record.getStringValue(COMP_LONG_DESCRIPTION);
    }

    public static void setCompLongDescription(Record record, String compLongDesc) {
        record.setFieldValue(COMP_LONG_DESCRIPTION, compLongDesc);
    }

    public class ComponentCodeValues {
        public static final String NEWDOCTOR = "NEWDOCTOR";
    }

    public static String getOfficialRecordId(Record record) {
        return record.getStringValue(OFFICIAL_RECORD_ID);
    }

    public static void setOfficialRecordId(Record record, String officialRecordId) {
        record.setFieldValue(OFFICIAL_RECORD_ID, officialRecordId);
    }
    
    public static String getOperation(Record record){
        return record.getStringValue(OPERATION);
    }
    
    public static void setOperation(Record record, String operation){
        record.setFieldValue(OPERATION, operation);
    }

    public static String getMainCoverageBaseRecordId(Record record) {
        return record.getStringValue(MAIN_COVERAGE_BASE_RECORD_ID);
    }

    public static void setMainCoverageBaseRecordId(Record record, String mainCoverageBaseRecordId) {
        record.setFieldValue(MAIN_COVERAGE_BASE_RECORD_ID, mainCoverageBaseRecordId);
    }
}
