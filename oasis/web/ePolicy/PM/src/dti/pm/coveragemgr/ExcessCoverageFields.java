package dti.pm.coveragemgr;

import dti.oasis.recordset.Record;

/**
 * Constants for Excess Coverage.
 * <p/>
 * <p>(C) 2009 Delphi Technology, inc. (dti)</p>
 * Date:   April 07, 2009
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class ExcessCoverageFields {

    public static final String PRIOR_CARRIER_HISTORY_ID = "priorCarrierHistoryId";
    public static final String ENTITY_ID = "entityId";
    public static final String EFFECTIVE_END_DATE = "effectiveEndDate";
    public static final String EFFECTIVE_START_DATE = "effectiveStartDate";
    public static final String LIMIT_INCIDENT = "limitIncident";
    public static final String ATTACHEMENT_POINT = "attachmentPoint";
    public static final String BROKEN_ENTITY_ID = "brokerEntityId";
    public static final String CARRIER_ENTITY_ID = "carrierEntityId";
    public static final String EFFECTIVE_FROM_DATE = "effectiveFromDate";
    public static final String EFFECTIVE_TO_DATE = "effectiveToDate";
    public static final String EXCESS_LIMIT = "excessLimit";
    public static final String EXCESS_RETRO_DATE = "excessRetroDate";
    public static final String EXCESS_COVERAGE_B = "excessCoverageB";
    public static final String TERM_EFFECTIVE_FROM_DATE = "termEffectiveFromDate";
    public static final String TERM_EFFECTIVE_TO_DATE = "termEffectiveToDate";
    public static final String NEW_CURRENT_CARRIER_B = "newCurrentCarrierB";
    public static final String HOSPITAL_MISC_INFO_ID = "hospitalMiscInfoId";
    public static final String vapPremiumTRANSACTION_ID = "transactionId";
    public static final String TRANSACTION_ID = "transactionId";
    public static final String POLICY_ID = "policyId";
    public static final String VAP_PREMIUM = "vapPremium";
    public static final String OCC_PREMIUM = "occPremium";


    public static String getPriorCarrierHistoryId(Record record) {
        return record.getStringValue(PRIOR_CARRIER_HISTORY_ID);
    }

    public static void setPriorCarrierHistoryId(Record record, String priorCarrierHistoryId) {
        record.setFieldValue(PRIOR_CARRIER_HISTORY_ID, priorCarrierHistoryId);
    }

    public static String getEntityId(Record record) {
        return record.getStringValue(ENTITY_ID);
    }

    public static void setEntityId(Record record, String entityId) {
        record.setFieldValue(ENTITY_ID, entityId);
    }

    public static String getEffectiveEndDate(Record record) {
        return record.getStringValue(EFFECTIVE_END_DATE);
    }

    public static void setEffectiveEndDate(Record record, String effectiveEndDate) {
        record.setFieldValue(EFFECTIVE_END_DATE, effectiveEndDate);
    }

    public static String getEffectiveStartDate(Record record) {
        return record.getStringValue(EFFECTIVE_START_DATE);
    }

    public static void setEffectiveStartDate(Record record, String effectiveStartDate) {
        record.setFieldValue(EFFECTIVE_START_DATE, effectiveStartDate);
    }

    public static String getLimitIncident(Record record) {
        return record.getStringValue(LIMIT_INCIDENT);
    }

    public static void setLimitIncident(Record record, String limitIncident) {
        record.setFieldValue(LIMIT_INCIDENT, limitIncident);
    }

    public static String getAttachmentPoint(Record record) {
        return record.getStringValue(ATTACHEMENT_POINT);
    }

    public static void setAttachmentPoint(Record record, String attachmentPoint) {
        record.setFieldValue(ATTACHEMENT_POINT, attachmentPoint);
    }

    public static String getBrokerEntityId(Record record) {
        return record.getStringValue(BROKEN_ENTITY_ID);
    }

    public static void setBrokerEntityId(Record record, String brokerEntityId) {
        record.setFieldValue(BROKEN_ENTITY_ID, brokerEntityId);
    }

    public static String getCarrierEntityId(Record record) {
        return record.getStringValue(CARRIER_ENTITY_ID);
    }

    public static void setCarrierEntityId(Record record, String carrierEntityId) {
        record.setFieldValue(CARRIER_ENTITY_ID, carrierEntityId);
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

    public static String getExcessLimit(Record record) {
        return record.getStringValue(EXCESS_LIMIT);
    }

    public static void setExcessLimit(Record record, String excessLimit) {
        record.setFieldValue(EXCESS_LIMIT, excessLimit);
    }

    public static String getExcessRetroDate(Record record) {
        return record.getStringValue(EXCESS_RETRO_DATE);
    }

    public static void setExcessRetroDate(Record record, String excessRetroDate) {
        record.setFieldValue(EXCESS_RETRO_DATE, excessRetroDate);
    }

    public static String getExcessCoverageB(Record record) {
        return record.getStringValue(EXCESS_COVERAGE_B);
    }

    public static void setExcessCoverageB(Record record, String excessCoverageB) {
        record.setFieldValue(EXCESS_COVERAGE_B, excessCoverageB);
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

    public static String getNewCurrentCarrierB(Record record) {
        return record.getStringValue(NEW_CURRENT_CARRIER_B);
    }

    public static void setNewCurrentCarrierB(Record record, String newCurrentCarrierB) {
        record.setFieldValue(NEW_CURRENT_CARRIER_B, newCurrentCarrierB);
    }

    public static String getHospitalMiscInfoId(Record record) {
        return record.getStringValue(HOSPITAL_MISC_INFO_ID);
    }

    public static void setHospitalMiscInfoId(Record record, String hospitalMiscInfoId) {
        record.setFieldValue(HOSPITAL_MISC_INFO_ID, hospitalMiscInfoId);
    }

    public static String getTransactionId(Record record) {
        return record.getStringValue(TRANSACTION_ID);
    }

    public static void setTransactionId(Record record, String transactionId) {
        record.setFieldValue(TRANSACTION_ID, transactionId);
    }

    public static String getPolicyId(Record record) {
        return record.getStringValue(POLICY_ID);
    }

    public static void setPolicyId(Record record, String policyId) {
        record.setFieldValue(POLICY_ID, policyId);
    }

    public static String getVapPremium(Record record) {
        return record.getStringValue(VAP_PREMIUM);
    }

    public static void setVapPremium(Record record, String vapPremium) {
        record.setFieldValue(VAP_PREMIUM, vapPremium);
    }

    public static String getOccPremium(Record record) {
        return record.getStringValue(OCC_PREMIUM);
    }

    public static void setOccPremium(Record record, String occPremium) {
        record.setFieldValue(OCC_PREMIUM, occPremium);
    }

}
