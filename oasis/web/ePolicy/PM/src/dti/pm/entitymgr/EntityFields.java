package dti.pm.entitymgr;

import dti.oasis.recordset.Record;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 28, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/04/2012       wfu         127082 - Add fields ENTITY_ID_FIELD_NAME, ENTITY_NAME_FIELD_NAME.
 * ---------------------------------------------------
 */

public class EntityFields {
    public static String getEntityId(Record record) {
        return record.getStringValue(ENTITY_ID);
    }

    public static void setEntityId(Record record, String entityId) {
        record.setFieldValue(ENTITY_ID, entityId);
    }

    public static String getEntityType(Record record) {
        return record.getStringValue(ENTITY_TYPE);
    }

    public static void setEntityType(Record record, String entityType) {
        record.setFieldValue(ENTITY_TYPE, entityType);
    }

    public static String getFullName(Record record) {
        return record.getStringValue(FULL_NAME);
    }

    public static void setFullName(Record record, String fullName) {
        record.setFieldValue(FULL_NAME, fullName);
    }

    public static String getEntityIdFieldName(Record record) {
        return record.getStringValue(ENTITY_ID_FIELD_NAME);
    }

    public static void setEntityIdFieldName(Record record, String entityIdFieldName) {
        record.setFieldValue(ENTITY_ID_FIELD_NAME, entityIdFieldName);
    }

    public static String getEntityNameFieldName(Record record) {
        return record.getStringValue(ENTITY_NAME_FIELD_NAME);
    }

    public static void setEntityNameFieldName(Record record, String entityNameFieldName) {
        record.setFieldValue(ENTITY_NAME_FIELD_NAME, entityNameFieldName);
    }

    public static final String ENTITY_ID = "entityId";
    public static final String ENTITY_TYPE = "entityType";
    public static final String FULL_NAME = "fullName";
    public static final String ENTITY_ID_FIELD_NAME = "entityIdFieldName";
    public static final String ENTITY_NAME_FIELD_NAME = "entityNameFieldName";
}
