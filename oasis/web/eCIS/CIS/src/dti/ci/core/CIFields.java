package dti.ci.core;

import dti.oasis.recordset.Record;

/**
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * User: cyzhao
 * Date: Dec 7, 2010
 */
/*
 *
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class CIFields {
    public static final String PK = "pk";
    public static final String ENTITY_TYPE = "entityType";
    public static final String ENTITY_NAME = "entityName";
    public static final String ENTITY_SELECT_RESULTS = "entitySelectedResults";
    public static final String CURRENT_ENTITY_PK = "currentPrimaryKey";
    public static final String ENTITY_SPLIT_SIGN = "!~";
    public static final String URL_ELEMENT_SIGN = "&";
    public static final String EQUAL_SIGN = "=";
    public static final String ENTITY_PK_COLUMN = "entity_pk";
    public static final String ENTITY_TYPE_COLUMN = "client_name";
    public static final String CLIENT_NAME_COLUMN = "entity_type";
    public static final String INCLUDE_MULTI_ENTITY = "includeMultiEntity";
    public static final String NAVIGATION_PAGE_CODE = "CI_ENTITY_NAVIGATION";
    public static final String STATUS_ACTIVE = "A";
    public static final String VALUE_FOR_YES = "Y";
    public static final String VALUE_FOR_NO = "N";
    public static final String PK_PROPERTY = "pk";
    public static final String ENTITY_TYPE_PROPERTY = "entityType";
    public static final String ENTITY_NAME_PROPERTY = "entityName";
    public static final char ENTITY_TYPE_ORG_CHAR = 'O';
    public static final char ENTITY_TYPE_PERSON_CHAR = 'P';
    public static final String INVALID_ENTITY_PK_FORWARD = "invalidEntityPK";
    public static final String RETURN_VALUE = "returnValue";
    public static final String ID = "id";
    public static final String KEY_FIELDS = "fieldsMap";
    public static final String CHECKBOX_SPAN_PROPERTY = "CheckBoxSpan";
    public static final String ENTITY_ID = "entityId";
    public static final String ENTITY_TAX_ID = "federal_tax_id";
    public static final String ENTITY_SSN_ID = "social_security_number";
    public static final String ENTITY_HAS_TAX_ID_EXISTS = "hasTaxIdExists";

    public static final String CIS_HEADER_CLIENT_ID = "cisHeaderClientId";
    public static final String CIS_HEADER_LEGACY_DATA_ID = "cisHeaderLegacyDataId";
    public static final String CIS_HEADER_REFERENCE_NUMBER = "cisHeaderReferencenumber";
    public static final String CIS_HEADER_NOTE_IND = "noteExistB";

    public static String getEntityId(Record record){
        return record.getStringValue(ENTITY_ID);
    }
    public static void setEntityId(Record record, String entityId) {
        record.setFieldValue(ENTITY_ID,entityId);
    }
    public static String getPk(Record record) {
        return record.getStringValue(PK, "");
    }

    public static void setPk(Record record, String pk) {
        record.setFieldValue(PK, pk);
    }

    public static String getId(Record record) {
        return record.getStringValue(ID);
    }

    public static void setId(Record record, String id) {
        record.setFieldValue(ID, id);
    }

    public static String getReturnValue(Record record) {
        return record.getStringValue(RETURN_VALUE);
    }

    public static void setReturnValue(Record record, String returnValue) {
        record.setFieldValue(RETURN_VALUE, returnValue);
    }
}
