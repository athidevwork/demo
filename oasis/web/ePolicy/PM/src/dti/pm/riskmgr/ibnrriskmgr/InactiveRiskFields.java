package dti.pm.riskmgr.ibnrriskmgr;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;

/**
 * Helper constants and set/get methods to access Inactive Risk Fields.
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 16, 2011
 *
 * @author Dzhang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * ---------------------------------------------------
 */
public class InactiveRiskFields {
    public static final String IBNR_INACTIVE_ID = "ibnrInactiveId";
    public static final String ASSOCIATED_RISK_ID = "associatedRiskId";
    public static final String RISK_BASE_RECORD_ID = "riskBaseRecordId";
    public static final String ASSOCIATED_ENTITY_ID = "associatedEntityId";
    public static final String ASSOCIATED_RISK_NAME = "associatedRiskName";
    public static final String IBNR_EFF_FROM_DATE = "ibnrEffFromDate";
    public static final String IBNR_EFF_TO_DATE = "ibnrEffToDate";
    public static final String INACTIVE_ENTITY_ID = "inactiveEntityId";
    public static final String INACTIVE_RISK_NAME = "inactiveRiskName";
    public static final String RISK_EFFECTIVE_FROM_DATE = "riskEffectiveFromDate";
    public static final String RISK_EFFECTIVE_TO_DATE = "riskEffectiveToDate";
    public static final String PRODUCT_COVERAGE_CODE = "productCoverageCode";
    public static final String PRIMARY_PROD_COVG_CODE = "primaryProdCovgCode";
    public static final String SECOND_GRID_ROW_STYLE = "secondGridRowStyle";
    public static final String OFFICIAL_RECORD_ID = "officialRecordId";
    public static final String AFTER_IMAGE_RECORD_B = "afterImageRecordB";
    public static final String ENTITY_ID = "entityId";
    public static final String ENTITY_TYPE = "entityType";
    public static final String PRACTICE_STATE_CODE = "practiceStateCode";
    public static final String IBNRNOTIFY_CONFIRMED = "ibnrNotify.confirmed";
    public static final String CLOSEING_TRANS_LOG_ID = "closingTransLogId";
    public static final String ASSOCIOATED_RISK_COUNT = "associatedRiskCount";
    public static final String TO_ASSOCIATED_RISK_ID = "toAssociatedRiskId";
    public static final String CANX_IBNR_RISK_PK_LIST = "canxIbnrRiskPkList";
    public static final String IS_IN_WORKFLOW = "isInWorkflow";
    public static final String COUNTY_CODE = "countyCode";
    public static final String COVERAGE_LIMIT_CODE = "coverageLimitCode";
    public static final String RISK_CLASS = "riskClass";
    public static final String ORG_ASSOCIATED_RISK_ID = "orgAssociatedRiskId";
    public static final String NOT_INVOKER_WORK_FLOW = "notInvokeWorkFlow";

    public static String getIbnrInactiveId(Record record) {
        return record.getStringValue(IBNR_INACTIVE_ID);
    }

    public static void setIbnrInactiveId(Record record, String ibnrInactiveId) {
        record.setFieldValue(IBNR_INACTIVE_ID, ibnrInactiveId);
    }

    public static String getAssociatedRiskId(Record record) {
        return record.getStringValue(ASSOCIATED_RISK_ID);
    }

    public static void setAssociatedRiskId(Record record, String associatedRiskId) {
        record.setFieldValue(ASSOCIATED_RISK_ID, associatedRiskId);
    }

    public static String getRiskBaseRecordId(Record record) {
        return record.getStringValue(RISK_BASE_RECORD_ID);
    }

    public static void setRiskBaseRecordId(Record record, String riskBaseRecordId) {
        record.setFieldValue(RISK_BASE_RECORD_ID, riskBaseRecordId);
    }

    public static void setAssociatedEntityId(Record record, String associatedEntityId) {
        record.setFieldValue(ASSOCIATED_ENTITY_ID, associatedEntityId);
    }

    public static String getAssociatedEntityId(Record record) {
        return record.getStringValue(ASSOCIATED_ENTITY_ID);
    }

    public static void setAssociatedRiskName(Record record, String associatedRiskName) {
        record.setFieldValue(ASSOCIATED_RISK_NAME, associatedRiskName);
    }

    public static String getAssociatedRiskName(Record record) {
        return record.getStringValue(ASSOCIATED_RISK_NAME);
    }

    public static YesNoFlag getAfterImageRecordB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(AFTER_IMAGE_RECORD_B));
    }

    public static void setAfterImageRecordB(Record record, YesNoFlag afterImageRecordB) {
        record.setFieldValue(AFTER_IMAGE_RECORD_B, afterImageRecordB);
    }

    public static String getIbnrEffFromDate(Record record) {
        return record.getStringValue(IBNR_EFF_FROM_DATE);
    }

    public static void setIbnrEffFromDate(Record record, String ibnrEffFromDate) {
        record.setFieldValue(IBNR_EFF_FROM_DATE, ibnrEffFromDate);
    }

    public static String getIbnrEffToDate(Record record) {
        return record.getStringValue(IBNR_EFF_TO_DATE);
    }

    public static void setIbnrEffToDate(Record record, String ibnrEffToDate) {
        record.setFieldValue(IBNR_EFF_TO_DATE, ibnrEffToDate);
    }

    public static void setInactiveEntityId(Record record, String inactiveEntityId) {
        record.setFieldValue(INACTIVE_ENTITY_ID, inactiveEntityId);
    }

    public static String getInactiveEntityId(Record record) {
        return record.getStringValue(INACTIVE_ENTITY_ID);
    }

    public static void setInactiveRiskName(Record record, String inactiveRiskName) {
        record.setFieldValue(INACTIVE_RISK_NAME, inactiveRiskName);
    }

    public static String getInactiveRiskName(Record record) {
        return record.getStringValue(INACTIVE_RISK_NAME);
    }

    public static String getOfficialRecordId(Record record) {
        return record.getStringValue(OFFICIAL_RECORD_ID);
    }

    public static void setOfficialRecordId(Record record, String officialRecordId) {
        record.setFieldValue(OFFICIAL_RECORD_ID, officialRecordId);
    }

    public static String getProductCoverageCode(Record record) {
        return record.getStringValue(PRODUCT_COVERAGE_CODE);
    }

    public static void setProductCoverageCode(Record record, String productCoverageCode) {
        record.setFieldValue(PRODUCT_COVERAGE_CODE, productCoverageCode);
    }

    public static String getPrimaryProdCovgCode(Record record) {
        return record.getStringValue(PRIMARY_PROD_COVG_CODE);
    }

    public static void setPrimaryProdCovgCode(Record record, String primaryProdCovgCode) {
        record.setFieldValue(PRIMARY_PROD_COVG_CODE, primaryProdCovgCode);
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

    public static String getPracticeStateCode(Record record) {
        return record.getStringValue(PRACTICE_STATE_CODE);
    }

    public static void setPracticeStateCode(Record record, String practiceStateCode) {
        record.setFieldValue(PRACTICE_STATE_CODE, practiceStateCode);
    }

    public static String getRiskEffectiveFromDate(Record record) {
        return record.getStringValue(RISK_EFFECTIVE_FROM_DATE);
    }

    public static void setRiskEffectiveFromDate(Record record, String riskEffectiveFromDate) {
        record.setFieldValue(RISK_EFFECTIVE_FROM_DATE, riskEffectiveFromDate);
    }

    public static void setRiskEffectiveToDate(Record record, String riskEffectiveToDate) {
        record.setFieldValue(RISK_EFFECTIVE_TO_DATE, riskEffectiveToDate);
    }

    public static String getRiskEffectiveToDate(Record record) {
        return record.getStringValue(RISK_EFFECTIVE_TO_DATE);
    }

    public static String getEntityType(Record record) {
        return record.getStringValue(ENTITY_TYPE);
    }

    public static String getClosingTransLogId(Record record) {
        return record.getStringValue(CLOSEING_TRANS_LOG_ID);
    }

    public static void setClosingTransLogId(Record record, String closingTransLogId) {
        record.setFieldValue(CLOSEING_TRANS_LOG_ID, closingTransLogId);
    }

    public static YesNoFlag getIbnrNotifyConfirmed(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(IBNRNOTIFY_CONFIRMED));
    }

    public static String getAssociatedRiskCount(Record record) {
        return record.getStringValue(ASSOCIOATED_RISK_COUNT);
    }

    public static void setCancelIbnrRiskPkList(Record record, String cancelIbnrRiskIdList) {
        record.setFieldValue(CANX_IBNR_RISK_PK_LIST, cancelIbnrRiskIdList);
    }

    public static String getCountyCode(Record record) {
        return record.getStringValue(COUNTY_CODE);
    }

    public static void setCountyCode(Record record, String countyCode) {
        record.setFieldValue(COUNTY_CODE, countyCode);
    }

    public static String getCoverageLimitCode(Record record) {
        return record.getStringValue(COVERAGE_LIMIT_CODE);
    }

    public static void setCoverageLimitCode(Record record, String coverageLimitCode) {
        record.setFieldValue(COVERAGE_LIMIT_CODE, coverageLimitCode);
    }

    public static String getRiskClass(Record record) {
        return record.getStringValue(RISK_CLASS);
    }

    public static void setRiskClass(Record record, String riskClass) {
        record.setFieldValue(RISK_CLASS, riskClass);
    }

    public static void setOrgAssociatedRiskId(Record record, String orgAssociatedRiskId) {
        record.setFieldValue(ORG_ASSOCIATED_RISK_ID, orgAssociatedRiskId);
    }

    public static String getOrgAssociatedRiskId(Record record) {
        return record.getStringValue(ORG_ASSOCIATED_RISK_ID);
    }
}
