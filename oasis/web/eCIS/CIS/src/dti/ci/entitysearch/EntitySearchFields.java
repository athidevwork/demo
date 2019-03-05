package dti.ci.entitysearch;

import dti.oasis.recordset.Record;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   3/8/2018
 *
 * @author dpang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/14/2018       dpang       Issue 109216. Refactor "Entity Select Search" popup.
 * ---------------------------------------------------
 */
public class EntitySearchFields {
    public static final String SEARCH_CRITERIA_ADDL_FIELD = "searchCriteria_addlField";
    public static final String SEARCH_CRITERIA_CITY = "searchCriteria_city";
    public static final String SEARCH_CRITERIA_CLIENT_ID = "searchCriteria_clientId";
    public static final String SEARCH_CRITERIA_COUNTRY_CODE = "searchCriteria_countryCode";
    public static final String SEARCH_CRITERIA_DATE_OF_BIRTH = "searchCriteria_dateOfBirth";
    public static final String SEARCH_CRITERIA_LAST_OR_ORG_NAME = "searchCriteria_lastOrOrgName";
    public static final String SEARCH_CRITERIA_PHONE_NUMBER = "searchCriteria_phoneNumber";
    public static final String SEARCH_CRITERIA_ROLE_EXTERNAL_ID = "searchCriteria_roleExternalId";
    public static final String SEARCH_CRITERIA_ROLE_TYPE_CODE = "searchCriteria_roleTypeCode";
    public static final String SEARCH_CRITERIA_ENTITY_TYPE_CODE = "searchCriteria_entityTypeCode";
    public static final String SEARCH_CRITERIA_ENTITY_CLASS_CODE = "searchCriteria_entityClassCode";
    public static final String SEARCH_CRITERIA_SOURCE_FIELD = "searchCriteria_sourceField";
    public static final String SEARCH_CRITERIA_FOR_SESSION_PREFIX = "EntitySearch";
    public static final String SEARCH_CRITERIA_PREFIX = "searchCriteria_";

    public static final String INCLUDED_POLNO_FOR_SEARCH = "includedPolnoForSearch";
    public static final String IS_GLOBAL_SEARCH = "isGlobalSearch";
    public static final String ADDL_FIELD_TO_RETURN = "addlFieldToReturn";
    public static final String ADDITIONAL_SEARCH_SQL = "additionalSearchSql";
    public static final String ENTITY_TYPE_CODE = "entityTypeCode";
    public static final String ENTITY_CLASS_CODE = "entityClassCode";
    public static final String IS_FIND_INVOICER = "isFindInvoicer";
    public static final String MAX_ROW = "maxRow";

    public static final String POLICY_NO = "policyNo";
    public static final String INCLUDED_ADDL_DATA = "includedAddlData";
    public static final String INCLUDED_POLICY_NO = "includedPolicyNo";
    public static final String GLOBAL_SEARCH_PROCESS = "globalSearch";

    public static final String ENT_PK_FLD_NAME_PROPERTY = "entityPKFieldName";
    public static final String ENT_FULL_NAME_FLD_NAME_PROPERTY = "entityFullNameFieldName";
    public static final String ENT_LAST_NAME_FLD_NAME_PROPERTY = "entityLastNameFieldName";
    public static final String ENT_FIRST_NAME_FLD_NAME_PROPERTY = "entityFirstNameFieldName";
    public static final String ENT_MIDDLE_NAME_FLD_NAME_PROPERTY = "entityMiddleNameFieldName";
    public static final String ENT_ORG_NAME_FLD_NAME_PROPERTY = "entityOrgNameFieldName";
    public static final String ENT_CLIENT_ID_FLD_NAME_PROPERTY = "entityClientIdFieldName";
    public static final String ENT_ADDR_COUNTRY_FLD_NAME_PROPERTY = "entAddrCountryFldName";
    public static final String ENT_ADDR_STATE_FLD_NAME_PROPERTY = "entAddrStateFldName";
    public static final String ENT_ADDR_CITY_FLD_NAME_PROPERTY = "entAddrCityFldName";

    // added by Jacky 09-05-2008
    public static final String ENT_ACCOUNT_NO_FLD_NAME_PROPERTY = "entityAccountNoFieldName";
    public static final String ENT_ID_FOR_ADDL_SQL_PROPERTY = "idForAddlSql";
    // added by Jacky 12-10-2008
    public static final String ENT_ID_FOR_POLICY_NO_PROPERTY = "idForPolicyNo";
    public static final String ENT_ID_FOR_FROM_FM_PROPERTY = "fromFM";

    public static final String ROLE_TYPE_CODE_ARG = "roleTypeCodeArg";
    public static final String ROLE_TYPE_CODE_ARG_READ_ONLY = "roleTypeCodeArgReadOnly";
    public static final String ENTITY_CLASS_CODE_ARG = "entityClassCodeArg";
    public static final String ENTITY_CLASS_CODE_ARG_READ_ONLY = "entityClassCodeArgReadOnly";
    public static final int SINGLE = 1;
    public static final int MULTIPLE = 2;


    public static final String FLD_ENT_REL_ENT_PK = "entRelEntPK";
    // For issue 102270, added by Stephen 12-28-2009
    public static final String FROM_DOC_PROCESS = "fromDocProcess";
    public static final String ENT_ROLE_TYPE_CODE = "entityRoleTypeCodeFldName";
    public static final String EXTERNAL_NUMBER = "externalNumberFldName";
    public static final String DEFAULT_ROLE_TYPE_CODE = "defaultRoleTypeCode";

    //For issue 106849
    public static final String ENTITY_TYPE = "entityTypeCode";

    public static final String CLAIM_PK = "claimPK";

    public static final String DEFAULT_ENTITY_TYPE = "defaultEntityType";

    public static final String ENTITY_PK_VALUE = "entityPkValue";

    public static void setEntityPkValue(Record record, String entityPkValue) {
        record.setFieldValue(ENTITY_PK_VALUE, entityPkValue);
    }

    public static String getEntityPkValue(Record record) {
       return record.getStringValueDefaultEmpty(ENTITY_PK_VALUE);
    }

    public static void setSearchCriteriaSourceField(Record record, String searchCriteriaSourceField) {
        record.setFieldValue(SEARCH_CRITERIA_SOURCE_FIELD, searchCriteriaSourceField);
    }

    public static void setEntityClassCode(Record record, String entityClassCode) {
        record.setFieldValue(ENTITY_CLASS_CODE, entityClassCode);
    }

    public static void setIsFindInvoicer(Record record, String isFindInvoicer) {
        record.setFieldValue(IS_FIND_INVOICER, isFindInvoicer);
    }

    public static String getIncludedPolnoForSearch(Record record) {
        return record.getStringValueDefaultEmpty(INCLUDED_POLNO_FOR_SEARCH);
    }

    public static void setIncludedPolnoForSearch(Record record, String includedPolnoForSearch) {
        record.setFieldValue(INCLUDED_POLNO_FOR_SEARCH, includedPolnoForSearch);
    }

    public static String getIsGlobalSearch(Record record) {
        return record.getStringValueDefaultEmpty(IS_GLOBAL_SEARCH);
    }

    public static void setIsGlobalSearch(Record record, String isGlobalSearch) {
        record.setFieldValue(IS_GLOBAL_SEARCH, isGlobalSearch);
    }

    public static String getAddlFieldToReturn(Record record) {
        return record.getStringValueDefaultEmpty(ADDL_FIELD_TO_RETURN);
    }

    public static void setAddlFieldToReturn(Record record, String addlFieldToReturn) {
        record.setFieldValue(ADDL_FIELD_TO_RETURN, addlFieldToReturn);
    }

    public static String getAdditionalSearchSql(Record record) {
        return record.getStringValueDefaultEmpty(ADDITIONAL_SEARCH_SQL);
    }

    public static void setAdditionalSearchSql(Record record, String additionalSearchSql) {
        record.setFieldValue(ADDITIONAL_SEARCH_SQL, additionalSearchSql);
    }

    public static String getEntityTypeCode(Record record) {
        return record.getStringValueDefaultEmpty(ENTITY_TYPE_CODE);
    }

    public static void setEntityTypeCode(Record record, String entityTypeCode) {
        record.setFieldValue(ENTITY_TYPE_CODE, entityTypeCode);
    }

    public static int getMaxRow(Record record) {
        return record.getIntegerValue(MAX_ROW);
    }

    public static void setMaxRow(Record record, int maxRow) {
        record.setFieldValue(MAX_ROW, maxRow);
    }

    public static void setPolicyNo(Record record, String policyNo) {
        record.setFieldValue(POLICY_NO, policyNo);
    }

    public static String getIncludedAddlData(Record record) {
        return record.getStringValueDefaultEmpty(INCLUDED_ADDL_DATA);
    }

    public static void setIncludedAddlData(Record record, String includedAddlData) {
        record.setFieldValue(INCLUDED_ADDL_DATA, includedAddlData);
    }

    public static boolean getIncludedPolicyNo(Record record) {
        return record.getBooleanValue(INCLUDED_POLICY_NO, false);
    }

    public static void setIncludedPolicyNo(Record record, boolean includedPolicyNo) {
        record.setFieldValue(INCLUDED_POLICY_NO, includedPolicyNo);
    }

    public static String removeSearchCriteriaPrefix(String recordFieldName) {
        return recordFieldName.substring(SEARCH_CRITERIA_PREFIX.length());
    }
}
