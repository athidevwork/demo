package dti.ci.entitymgr;

import dti.oasis.recordset.Record;

/**
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * User: cyzhao
 * Date: Oct 14, 2010
 */
/*
 *
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class EntityFields {
    public static final String FULL_NAME = "fullName";
    public static final String ENTITY_ID = "entityId";
    public static final String ENTITY_TYPE = "entityType";
    public static final String CLIENT_ID = "clientId";
    public static final String ENTITY_NEW_NAME = "entityNewName";
    public static final String ORGANIZATION_NAME = "organizationName";
    public static final String ENTITY_NAME_COMPUTED = "entityNameComputed";
    public static final String GENDER = "gender";
    public static final String SOCIAL_SECURITY_NUMBER = "socialSecurityNumber";
    public static final String LEGACY_DATA_ID = "legacyDataID";
    public static final String REFERENCE_NUMBER = "referenceNumber";
    public static final String ENTITY_TYPE_PERSON = "P";
    public static final String ENTITY_TYPE_ORG = "O";

    public static String getFullName(Record record) {
        return record.getStringValue(FULL_NAME);
    }

    public static void setFullName(Record record, String fullName) {
        record.setFieldValue(FULL_NAME, fullName);
    }

    public static String getEntityId(Record record) {
        return record.getStringValueDefaultEmpty(ENTITY_ID);
    }

    public static void setEntityId(Record record, String entityId) {
        record.setFieldValue(ENTITY_ID, entityId);
    }

    public static String getEntityType(Record record) {
        return record.getStringValueDefaultEmpty(ENTITY_TYPE);
    }

    public static void setEntityType(Record record, String entityType) {
        record.setFieldValue(ENTITY_TYPE, entityType);
    }

    public static String getClientId(Record record) {
        return record.getStringValueDefaultEmpty(CLIENT_ID);
    }

    public static void setClientId(Record record, String clientId) {
        record.setFieldValue(CLIENT_ID, clientId);
    }

    public static String getOrganizationName(Record record) {
        return record.getStringValueDefaultEmpty(ORGANIZATION_NAME);
    }

    public static void setOrganizationName(Record record, String organizationName) {
        record.setFieldValue(ORGANIZATION_NAME, organizationName);
    }

    public static String getEntityNameComputed(Record record) {
        return record.getStringValueDefaultEmpty(ENTITY_NAME_COMPUTED);
    }

    public static void setEntityNameComputed(Record record, String entityNameComputed) {
        record.setFieldValue(ENTITY_NAME_COMPUTED, entityNameComputed);
    }

    public static String getGender(Record record) {
        return record.getStringValueDefaultEmpty(GENDER);
    }

    public static void setGender(Record record, String gender) {
        record.setFieldValue(GENDER, gender);
    }

    public static String getSocialSecurityNumber(Record record) {
        return record.getStringValueDefaultEmpty(SOCIAL_SECURITY_NUMBER);
    }

    public static void setSocialSecurityNumber(Record record, String socialSecurityNumber) {
        record.setFieldValue(SOCIAL_SECURITY_NUMBER, socialSecurityNumber);
    }

    public static String getLegacyDataID(Record record) {
        return record.getStringValueDefaultEmpty(LEGACY_DATA_ID);
    }

    public static void setLegacyDataId(Record record, String legacyDataID) {
        record.setFieldValue(LEGACY_DATA_ID, legacyDataID);
    }

    public static String getReferenceNumber(Record record) {
        return record.getStringValueDefaultEmpty(REFERENCE_NUMBER);
    }

    public static void setReferenceNumber(Record record, String referenceNumber) {
        record.setFieldValue(REFERENCE_NUMBER, referenceNumber);
    }
}
