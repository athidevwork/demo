package dti.ci.addressmgr;

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
 * 08/09/2011       kshen       Changed for issue 123063.
 * 08/11/2011       kshen       Added fields for issue 99502.
 * 09/07/2018       dzhang      Add get and set function to address singe line
 * ---------------------------------------------------
 */
public class AddressFields {
    public static final String PK = "pk";
    public static final String ENTITY_TYPE = "entityType";
    public static final String ENTITY_NAME = "entityName";
    public static final String ENTITY_LOCK_FLAG = "entityLockFlag";
    public static final String ALLOW_ADD_LOCKED_POL = "allowAddLockedPol";
    public static final String IN_ADDRESS_PK = "inAddressPK";
    public static final String IN_SOURCE_RECORD_FK = "inSourceRecordFK";
    public static final String DUMMY_SOURCE_RECORD_FK = "dummySourceRecordFK";
    public static final String IN_SOURCE_TABLE_NAME = "inSourceTableName";
    public static final String CLAIMS_ONLY_ADDR_ON_TOP = "claimsOnlyAddrOnTop";
    public static final String READ_ONLY = "readOnly";
    public static final String ENTITY = "ENTITY";
    public static final String ALLOW_ATTACH_TO_CLIENT = "allowAttachToClient";
    public static final String ADDRESS_SEARCH_ADD_ADDRLIST_LAYER = "Address_Search_Add_AddrList_Layer";
    public static final String ORIG_IN_SOURCE_RECORD_FK = "origInSourceRecordFK";
    public static final String IN_ADDRESS_TYPE_CODE = "inAddressTypeCode";
    public static final String CONCATENATED_ADDRESS = "concatenatedAddress";
    public static final String ADDRESS_ID = "addressId";
    public static final String SOURCE_RECORD_ID = "sourceRecordId";
    public static final String EXPIRED_ADDRESS_F_K = "expiredAddressFK";
    public static final String SOURCE_TABLE_NAME = "sourceTableName";
    public static final String SOURCE_RECORD_F_K = "sourceRecordFK";
    public static final String SQL_OPERATION = "sqlOperation";
    public static final String CS_ALLOWADDLOCKEDPOL = "CS_ALLOWADDLOCKEDPOL";
    public static final String PRIMARY_ADDR_B_ID = "primaryAddressB";
    public static final String IS_NEW_VAL_PROPERTY = "isNewValue";
    public static final String CODE = "code";
    public static final String SHORT_DESC = "shortDesc";
    public static final String LONG_DESC = "longDesc";
    public static final String ADDRESS_TYPE_CODE = "addressTypeCode";
    public static final String ADDRESS_TYPE_FILTER = "addressTypeFilter";
    public static final String PRIMARY_ADDRESS_B = "primaryAddressB";
    public static final String EFFECTIVE_FROM_DATE = "effectiveFromDate";
    public static final String EFFECTIVE_TO_DATE = "effectiveToDate";
    public static final String EFFECTIVE_FROM_DATE_ORIGINAL = "effectiveFromDateOriginal";
    public static final String USA_ADDRESS_B = "usaAddressB";
    public static final String CITY = "city";
    public static final String STATE_CODE = "stateCode";
    public static final String COUNTY_CODE = "countyCode";
    public static final String ZIP_CODE = "zipcode";
    public static final String ZIP_CODE_FOREIGN = "zipCodeForeign";
    public static final String ZIP_PLUS_FOUR = "zipPlusFour";
    public static final String ADDR_TYPE_HIDE_POBOX_PARAM = "addrTypeHidePOBoxParam";
    public static final String ADDR_TYPE_HIDE_POBOX = "addrTypeHidePOBox";
    public static final String ADDRESS_EFFECTIVE_FROM_DATE = "address_effectiveFromDate";
    public static final String ADDRESS_EFFECTIVE_TO_DATE = "address_effectiveToDate";
    public static final String NEW_ADDRESS_ID = "newAddressId";
    public static final String PROVINCE = "province";
    public static final String OTHER_PROVINCE = "otherProvince";
    public static final String COUNTRY_CODE = "countryCode";
    public static final String EXPIRED_B_COMPUTED = "expiredBComputed";
    public static final String ADDRESS_PK = "addressPK";
    public static final String ENTITY_FK = "entityFK";
    public static final String A_ADDRESS_ID = "aAddressId";
    public static final String A_ENTITY_ID = "aEntityId";
    public static final String A_USE_FOR_CHANGE = "aUseForChange";
    public static final String A_USE_FOR_WARNING = "aUseForWarning";
    public static final String A_PRIMARY_ADDR_CHANGE = "aPrimaryAddrChange";
    public static final String ENTITY_ID = "entityId";
    public static final String USE_FOR_CHANGE = "useForChange";
    public static final String USE_FOR_WARNING = "useForWarning";
    public static final String PRIMARY_ADDR_CHANGE = "primaryAddrChange";
    public static final String TO_ADDRESS_DESC = "toAddressDesc";
    public static final String SYS_PARAM_CS_VALIDATE_ADDXREF = "CS_VALIDATE_ADDXREF";
    public static final String SYS_PARAM_ADDR_EFF_ATTER_TODAY = "ADDR_EFF_ATTER_TODAY";
    public static final String SYS_PARAM_COUNTRY_CODE_CONFIG = "COUNTRY_CODE_CONFIG";
    public static final String IS_TRANS_ROLES_TO_NEW_ADDR = "isTransRolesToNewAddr";
    public static final String PRIMARY_ADDRESS_ID = "primaryAddressId";
    public static final String TO_ADDRESS_ID = "toAddressId";
    public static final String NEW_PRIMARY_ADDRESS_ID = "newPrimaryAddressId";
    public static final String COPY_ADDRESS_ID = "copyAddressId";
    public static final String BULK_MODIFY_ADDRESS_ID = "bulkModifyAddressId";
    public static final String CI_ADDR_PCT_PRAC_TYP = "CI_ADDR_PCT_PRAC_TYP";
    public static final String CI_ADDR_PCT_PRAC_FLD = "CI_ADDR_PCT_PRAC_FLD";
    public static final String CI_ADDR_PCT_PRAC_MSG = "CI_ADDR_PCT_PRAC_MSG";
    public static final String CS_SHOWLOCKEDPOL = "CS_SHOWLOCKEDPOL";
    public static final String NEW_EFFECTIVE_TO_DATE = "newEffectiveToDate";
    public static final String CLIENT_B = "clientB";
    public static final String ADDRESS_SINGLE_LINE = "addressSingleLine";
    public static final String ADDRESS_NAME = "addressName";
    public static final String ADDRESS_LINE_1 = "addressLine1";
    public static final String ADDRESS_LINE_2 = "addressLine2";
    public static final String ADDRESS_LINE_3 = "addressLine3";
    public static final String COUNTRY_CODE_USA = "USA";
    public static final String RETURN_VALUE = "returnValue";
    public static final String ACTION_CLASS_NAME = "actionClassName";

    public static final String ORIG_ADDRESS_ID = "origAddressId";
    public static final String ORIG_SOURCE_RECORD_ID = "origSourceRecordId";
    public static final String ORIG_ADDRESS_TYPE_CODE = "origAddressTypeCode";
    public static final String ORIG_SOURCE_TABLE_NAME = "origSourceTableName";
    public static final String DUMMY_SOURCE_RECORD_ID = "dummySourceRecordId";
    public static final String ALLOW_OTHER_CLIENT = "allowOtherClient";

    public static final String BTN_CI_ADDR_CHG_ROLE = "CI_ADDR_CHG_ROLE";
    public static final String BTN_CI_ADDR_PRIMARY_ADDR = "CI_ADDR_PRIMARY_ADDR";
    public static final String BTN_CI_ADRLST_ADD = "CI_ADRLST_ADD";
    public static final String BTN_CI_ADDR_COPY = "CI_ADDR_COPY";
    public static final String BTN_CI_ADDR_ADD_COPY = "CI_ADDR_ADD_COPY";
    public static final String BTN_CI_ADDR_BULK_MODIFY = "CI_ADDR_BULK_MODIFY";

    public static final String ROLE_TYPE_CODE = "roleTypeCode";
    public static final String ROLE_GROUP = "roleGroup";
    public static final String ROLE_PARENT = "roleParent";
    public static final String ENTITY_ROLE_ID_LIST = "entityRoleIdList";
    public static final String ENTITY_ROLE_ID = "entityRoleId";

    public static final String SYS_PARAM_CS_SHOW_ZIPCD_LIST = "CS_SHOW_ZIPCD_LIST";
    public static final String KEY_CS_SHOW_ZIPCD_LIST = "showZipcdList";
    public static final String SYS_PARAM_ZIP_OVERRIDE_ADDR = "ZIP_OVERRIDE_ADDR";
    public static final String KEY_ZIP_OVERRIDE_ADDR = "zipOverrideAddr";
    public static final String SYS_PARAM_ZIP_CODE_ENABLE = "ZIP_CODE_ENABLE";
    public static final String KEY_ZIP_CODE_ENABLE = "zipCodeEnable";

    public static final String SYS_PARAM_COUNTRY_CODE_USA = "COUNTRY_CODE_USA";
    public static final String KEY_COUNTRY_CODE_USA = "configuredUSACountryCode";
    public static final String SYS_PARAM_COUNTRY_CODE_CAN = "COUNTRY_CODE_CAN";
    public static final String KEY_COUNTRY_CODE_CAN = "configuredCanadaCountryCode";

    public static String getToAddressId(Record record){
        return record.getStringValue(TO_ADDRESS_ID);
    }
    public static void setToAddressId(Record record, String toAddressId) {
        record.setFieldValue(TO_ADDRESS_ID,toAddressId);
    }
    public static String getPrimaryAddressId(Record record){
        return record.getStringValue(PRIMARY_ADDRESS_ID);
    }
    public static void setPrimaryAddressId(Record record, String primaryAddressId) {
        record.setFieldValue(PRIMARY_ADDRESS_ID,primaryAddressId);
    }
    public static String getIsTransRolesToNewAddr(Record record){
        return record.getStringValue(IS_TRANS_ROLES_TO_NEW_ADDR);
    }
    public static void setIsTransRolesToNewAddr(Record record, String isTransRolesToNewAddr) {
        record.setFieldValue(IS_TRANS_ROLES_TO_NEW_ADDR,isTransRolesToNewAddr);
    }
    public static String getAAddressId(Record record){
        return record.getStringValue(A_ADDRESS_ID);
    }
    public static void setAAddressId(Record record, String aAddressId) {
        record.setFieldValue(A_ADDRESS_ID,aAddressId);
    }
    public static String getAEntityId(Record record){
        return record.getStringValue(A_ENTITY_ID);
    }
    public static void setAEntityId(Record record, String aEntityId) {
        record.setFieldValue(A_ENTITY_ID,aEntityId);
    }
    public static String getAUseForChange(Record record){
        return record.getStringValue(A_USE_FOR_CHANGE);
    }
    public static void setAUseForChange(Record record, String aUseForChange) {
        record.setFieldValue(A_USE_FOR_CHANGE,aUseForChange);
    }
    public static String getAUseForWarning(Record record){
        return record.getStringValue(A_USE_FOR_WARNING);
    }
    public static void setAUseForWarning(Record record, String aUseForWarning) {
        record.setFieldValue(A_USE_FOR_WARNING,aUseForWarning);
    }
    public static String getAPrimaryAddrChange(Record record){
        return record.getStringValue(A_PRIMARY_ADDR_CHANGE);
    }
    public static void setAPrimaryAddrChange(Record record, String aPrimaryAddrChange) {
        record.setFieldValue(A_PRIMARY_ADDR_CHANGE,aPrimaryAddrChange);
    }
    public static String getEntityId(Record record){
        return record.getStringValue(ENTITY_ID);
    }
    public static void setEntityId(Record record, String entityId) {
        record.setFieldValue(ENTITY_ID,entityId);
    }
    public static String getUseForChange(Record record){
        return record.getStringValue(USE_FOR_CHANGE);
    }
    public static void setUseForChange(Record record, String useForChange) {
        record.setFieldValue(USE_FOR_CHANGE,useForChange);
    }
    public static String getUseForWarning(Record record){
        return record.getStringValue(USE_FOR_WARNING);
    }
    public static void setUseForWarning(Record record, String useForWarning) {
        record.setFieldValue(USE_FOR_WARNING,useForWarning);
    }
    public static String getPrimaryAddrChange(Record record){
        return record.getStringValue(PRIMARY_ADDR_CHANGE);
    }
    public static void setPrimaryAddrChange(Record record, String primaryAddrChange) {
        record.setFieldValue(PRIMARY_ADDR_CHANGE,primaryAddrChange);
    }
    public static String getEntityFK(Record record){
        return record.getStringValue(ENTITY_FK);
    }
    public static void setEntityFK(Record record, String entityFK) {
        record.setFieldValue(ENTITY_FK,entityFK);
    }
    public static String getAddressPK(Record record){
        return record.getStringValue(ADDRESS_PK);
    }
    public static void setAddressPK(Record record, String addressPK) {
        record.setFieldValue(ADDRESS_PK,addressPK);
    }
    public static String getExpiredBComputed(Record record){
        return record.getStringValue(EXPIRED_B_COMPUTED);
    }
    public static void setExpiredBComputed(Record record, String expiredBComputed) {
        record.setFieldValue(EXPIRED_B_COMPUTED,expiredBComputed);
    }

    public static String getCountryCode(Record record) {
        return record.getStringValue(COUNTRY_CODE, "");
    }

    public static void setCountryCode(Record record, String countryCode) {
        record.setFieldValue(COUNTRY_CODE, countryCode);
    }

    public static String getOtherProvince(Record record) {
        return record.getStringValue(OTHER_PROVINCE);
    }

    public static void setOtherProvince(Record record, String otherProvince) {
        record.setFieldValue(OTHER_PROVINCE, otherProvince);
    }

    public static String getProvince(Record record) {
        return record.getStringValue(PROVINCE, "");
    }

    public static void setProvince(Record record, String province) {
        record.setFieldValue(PROVINCE, province);
    }

    public static String getNewAddressId(Record record) {
        return record.getStringValue(NEW_ADDRESS_ID);
    }

    public static void setNewAddressId(Record record, String newAddressId) {
        record.setFieldValue(NEW_ADDRESS_ID, newAddressId);
    }

    public static String getAddressEffectiveFromDate(Record record) {
        return record.getStringValue(ADDRESS_EFFECTIVE_FROM_DATE, "");
    }

    public static void setAddressEffectiveFromDate(Record record, String address_effectiveFromDate) {
        record.setFieldValue(ADDRESS_EFFECTIVE_FROM_DATE, address_effectiveFromDate);
    }

    public static String getCountyCode(Record record) {
        return record.getStringValue(COUNTY_CODE);
    }

    public static void setCountyCode(Record record, String countyCode) {
        record.setFieldValue(COUNTY_CODE, countyCode);
    }

    public static String getCity(Record record) {
        return record.getStringValue(CITY);
    }

    public static void setCity(Record record, String city) {
        record.setFieldValue(CITY, city);
    }

    public static String getStateCode(Record record) {
        return record.getStringValue(STATE_CODE);
    }

    public static void setStateCode(Record record, String stateCode) {
        record.setFieldValue(STATE_CODE, stateCode);
    }

    public static String getZipCode(Record record) {
        return record.getStringValue(ZIP_CODE);
    }

    public static void setZipCode(Record record, String zipCode) {
        record.setFieldValue(ZIP_CODE, zipCode);
    }

    public static String getUsaAddressB(Record record) {
        return record.getStringValue(USA_ADDRESS_B);
    }

    public static void setUsaAddressB(Record record, String usaAddressB) {
        record.setFieldValue(USA_ADDRESS_B, usaAddressB);
    }

    public static String getEffectiveFromDate(Record record) {
        return record.getStringValue(EFFECTIVE_FROM_DATE, "");
    }

    public static void setEffectiveFromDate(Record record, String effectiveFromDate) {
        record.setFieldValue(EFFECTIVE_FROM_DATE, effectiveFromDate);
    }

    public static String getEffectiveFromDateOriginal(Record record) {
        return record.getStringValue(EFFECTIVE_FROM_DATE_ORIGINAL);
    }

    public static void setEffectiveFromDateOriginal(Record record, String effectiveFromDateOriginal) {
        record.setFieldValue(EFFECTIVE_FROM_DATE_ORIGINAL, effectiveFromDateOriginal);
    }

    public static String getPrimaryAddressB(Record record) {
        return record.getStringValue(PRIMARY_ADDRESS_B);
    }

    public static void setPrimaryAddressB(Record record, String primaryAddressB) {
        record.setFieldValue(PRIMARY_ADDRESS_B, primaryAddressB);
    }

    public static String getAddressTypeFilter(Record record) {
        return record.getStringValue(ADDRESS_TYPE_FILTER, "");
    }

    public static void setAddressTypeFilter(Record record, String addressTypeFilter) {
        record.setFieldValue(ADDRESS_TYPE_FILTER, addressTypeFilter);
    }

    public static String getAddressTypeCode(Record record) {
        return record.getStringValue(ADDRESS_TYPE_CODE);
    }

    public static void setAddressTypeCode(Record record, String addressTypeCode) {
        record.setFieldValue(ADDRESS_TYPE_CODE, addressTypeCode);
    }

    public static String getCode(Record record) {
        return record.getStringValue(CODE);
    }

    public static void setCode(Record record, String code) {
        record.setFieldValue(CODE, code);
    }

    public static String getShortDesc(Record record) {
        return record.getStringValue(SHORT_DESC);
    }

    public static void setShortDesc(Record record, String shortDesc) {
        record.setFieldValue(SHORT_DESC, shortDesc);
    }

    public static String getLongDesc(Record record) {
        return record.getStringValue(LONG_DESC);
    }

    public static void setLongDesc(Record record, String longDesc) {
        record.setFieldValue(LONG_DESC, longDesc);
    }

    public static String getExpiredAddressFK(Record record) {
        return record.getStringValue(EXPIRED_ADDRESS_F_K, "");
    }

    public static void setExpiredAddressFK(Record record, String expiredAddressFK) {
        record.setFieldValue(EXPIRED_ADDRESS_F_K, expiredAddressFK);
    }

    public static String getSourceTableName(Record record) {
        return record.getStringValue(SOURCE_TABLE_NAME, "");
    }

    public static void setSourceTableName(Record record, String sourceTableName) {
        record.setFieldValue(SOURCE_TABLE_NAME, sourceTableName);
    }

    public static String getSourceRecordFK(Record record) {
        return record.getStringValue(SOURCE_RECORD_F_K, "");
    }

    public static void setSourceRecordFK(Record record, String sourceRecordFK) {
        record.setFieldValue(SOURCE_RECORD_F_K, sourceRecordFK);
    }

    public static String getSqlOperation(Record record) {
        return record.getStringValue(SQL_OPERATION);
    }

    public static void setSqlOperation(Record record, String sqlOperation) {
        record.setFieldValue(SQL_OPERATION, sqlOperation);
    }

    public static String getInAddressTypeCode(Record record) {
        return record.getStringValue(IN_ADDRESS_TYPE_CODE, "");
    }

    public static void setInAddressTypeCode(Record record, String inAddressTypeCode) {
        record.setFieldValue(IN_ADDRESS_TYPE_CODE, inAddressTypeCode);
    }

    public static String getSourceRecordId(Record record) {
        return record.getStringValue(SOURCE_RECORD_ID);
    }

    public static void setSourceRecordId(Record record, String sourceRecordId) {
        record.setFieldValue(SOURCE_RECORD_ID, sourceRecordId);
    }

    public static String getDummySourceRecordId(Record record) {
        return record.getStringValue(DUMMY_SOURCE_RECORD_ID);
    }

    public static void setDummySourceRecordId(Record record, String dummySourceRecordId) {
        record.setFieldValue(DUMMY_SOURCE_RECORD_ID, dummySourceRecordId);
    }

    public static String getConcatenatedAddress(Record record) {
        return record.getStringValue(CONCATENATED_ADDRESS);
    }

    public static void setConcatenatedAddress(Record record, String concatenatedAddress) {
        record.setFieldValue(CONCATENATED_ADDRESS, concatenatedAddress);
    }

    public static String getAddressId(Record record) {
        return record.getStringValue(ADDRESS_ID);
    }

    public static void setAddressId(Record record, String addressId) {
        record.setFieldValue(ADDRESS_ID, addressId);
    }

    public static String getInAddressPK(Record record) {
        return record.getStringValue(IN_ADDRESS_PK, "");
    }

    public static void setInAddressPK(Record record, String inAddressPK) {
        record.setFieldValue(IN_ADDRESS_PK, inAddressPK);
    }

    public static String getInSourceRecordFK(Record record) {
        return record.getStringValue(IN_SOURCE_RECORD_FK);
    }

    public static void setInSourceRecordFK(Record record, String inSourceRecordFK) {
        record.setFieldValue(IN_SOURCE_RECORD_FK, inSourceRecordFK);
    }

    public static String getDummySourceRecordFK(Record record) {
        return record.getStringValue(DUMMY_SOURCE_RECORD_FK, "");
    }

    public static void setDummySourceRecordFK(Record record, String dummySourceRecordFK) {
        record.setFieldValue(DUMMY_SOURCE_RECORD_FK, dummySourceRecordFK);
    }

    public static String getInSourceTableName(Record record) {
        return record.getStringValue(IN_SOURCE_TABLE_NAME);
    }

    public static void setInSourceTableName(Record record, String inSourceTableName) {
        record.setFieldValue(IN_SOURCE_TABLE_NAME, inSourceTableName);
    }

    public static String getAllowOtherClient(Record record) {
        return record.getStringValue(ALLOW_OTHER_CLIENT);
    }

    public static void setAllowOtherClient(Record record, String allowOtherClient) {
        record.setFieldValue(ALLOW_OTHER_CLIENT, allowOtherClient);
    }

    public static String getClaimsOnlyAddrOnTop(Record record) {
        return record.getStringValue(CLAIMS_ONLY_ADDR_ON_TOP);
    }

    public static void setClaimsOnlyAddrOnTop(Record record, String claimsOnlyAddrOnTop) {
        record.setFieldValue(CLAIMS_ONLY_ADDR_ON_TOP, claimsOnlyAddrOnTop);
    }

    public static String getReadOnly(Record record) {
        return record.getStringValue(READ_ONLY);
    }

    public static void setReadOnly(Record record, String readOnly) {
        record.setFieldValue(READ_ONLY, readOnly);
    }

    public static String getAllowAddLockedPol(Record record) {
        return record.getStringValue(ALLOW_ADD_LOCKED_POL);
    }

    public static void setAllowAddLockedPol(Record record, String allowAddLockedPol) {
        record.setFieldValue(ALLOW_ADD_LOCKED_POL, allowAddLockedPol);
    }

    public static String getEntityLockFlag(Record record) {
        return record.getStringValue(ENTITY_LOCK_FLAG);
    }

    public static void setEntityLockFlag(Record record, String entityLockFlag) {
        record.setFieldValue(ENTITY_LOCK_FLAG, entityLockFlag);
    }

    public static String getPk(Record record) {
        return record.getStringValue(PK);
    }

    public static void setPk(Record record, String pk) {
        record.setFieldValue(PK, pk);
    }

    public static String getEntityType(Record record) {
        return record.getStringValue(ENTITY_TYPE);
    }

    public static void setEntityType(Record record, String entityType) {
        record.setFieldValue(ENTITY_TYPE, entityType);
    }

    public static String getEntityName(Record record) {
        return record.getStringValue(ENTITY_NAME, "");
    }

    public static void setEntityName(Record record, String entityName) {
        record.setFieldValue(ENTITY_NAME, entityName);
    }

    public static String getNewPrimaryAddressId(Record record) {
        return record.getStringValue(NEW_PRIMARY_ADDRESS_ID, "");
    }

    public static void setNewPrimaryAddressId(Record record, String newPrimaryAddressId) {
        record.setFieldValue(NEW_PRIMARY_ADDRESS_ID, newPrimaryAddressId);
    }
    
    public static String getCopyAddressId(Record record) {
        return record.getStringValue(COPY_ADDRESS_ID, "");
    }

    public static void setCopyAddressId(Record record, String copyAddressId) {
        record.setFieldValue(COPY_ADDRESS_ID, copyAddressId);
    }

    public static String getBulkModifyAddressId(Record record) {
        return record.getStringValue(BULK_MODIFY_ADDRESS_ID, "");
    }

    public static void setBulkModifyAddressId(Record record, String bulkModifyAddressId) {
        record.setFieldValue(BULK_MODIFY_ADDRESS_ID, bulkModifyAddressId);
    }

    public static String getNewEffectiveToDate(Record record) {
        return record.getStringValue(NEW_EFFECTIVE_TO_DATE, "");
    }

    public static void setNewEffectiveToDate(Record record, String newEffectiveToDate) {
        record.setFieldValue(NEW_EFFECTIVE_TO_DATE, newEffectiveToDate);
    }

    public static String getAddressSingleLine(Record record) {
        return record.getStringValue(ADDRESS_SINGLE_LINE);
    }

    public static void setAddressSingleLine(Record record, String concatenatedAddress) {
        record.setFieldValue(ADDRESS_SINGLE_LINE, concatenatedAddress);
    }

}
