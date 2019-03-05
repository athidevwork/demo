package dti.ci.helpers;

/**
 * Interface for address constants.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author Gerald C. Carney
 *         Date:   Jan 27, 2004
 *         <p/>
 *         Revision Date    Revised By  Description
 *         ---------------------------------------------------
 *         <p/>02/05/2007   kshen       Added constants COUNTY_DESC_ID, EFF_FROM_DATE_ORIGINAL_ID,
 *                                      EXPIRE_ADDRESS_SUCCESS_FLAG,EXPIRE_ADDRESS_TOKEN, EXPIRE_PROCESS_DESC
 *         <p/> 08/13/2007  FWCH        Added constant ORIGINAL_COUNTY_CODE
 *         ---------------------------------------------------
 */

public interface ICIAddressConstants {

    public static final String EXPIRED_ADDR_FK_PROPERTY = "expiredAddressFK";

    public static final String ADDR_PK_ID = "address_addressPK";
    public static final String ADDR_SRC_TBL_NAME_ID = "address_sourceTableName";
    public static final String ADDR_SRC_REC_FK_ID = "address_sourceRecordFK";
    public static final String ADDR_NAME_ID = "address_addressName";
    public static final String LINE_1_ID = "address_addressLine1";
    public static final String LINE_2_ID = "address_addressLine2";
    public static final String LINE_3_ID = "address_addressLine3";
    public static final String CITY_ID = "address_city";
    public static final String COUNTY_CODE_ID = "address_countyCode";
    public static final String COUNTY_DESC_ID = "address_countyDesc";
    public static final String STATE_ID = "address_stateCode";
    public static final String ZIP_CODE_ID = "address_zipCode";
    public static final String ZIP_CODE_FOREIGN_ID = "address_zipCodeForeign";
    public static final String ZIP_PLUS_FOUR_ID = "address_zipPlusFour";
    public static final String ADDR_TYPE_CODE_ID = "address_addressTypeCode";
    public static final String ADDR_TYPE_CODE_SHORT_DESC = "address_addressTypeCodeShortDesc";
    public static final String ADDR_TYPE_CODE_LONG_DESC = "address_addressTypeCodeLongDesc";
    public static final String PROVINCE_ID = "address_province";
    public static final String OTHER_PROVINCE_ID = "address_otherProvince";
    public static final String PROVINCE_COMPUTED = "address_province_computed";

    public static final String COUNTRY_CODE_ID = "address_countryCode";
    public static final String PRIMARY_ADDR_B_ID = "address_primaryAddressB";
    public static final String USA_ADDR_B_ID = "address_usaAddressB";
    public static final String POST_OFC_ADDR_B_ID = "address_postOfficeAddressB";
    public static final String EFF_FROM_DATE_ID = "address_effectiveFromDate";
    public static final String EFF_FROM_DATE_ORIGINAL_ID = "address_effectiveFromDateOriginal";
    public static final String EFF_TO_DATE_ID = "address_effectiveToDate";
    public static final String PHONE_NUM_COMPUTED_ID = "address_phoneNumberComputed";
    public static final String ADDR_LEGACY_DATA_ID_ID = "address_legacyDataID";
    public static final String EXPIRED_ADDR_FK_ID = "address_expiredAddressFK";
    public static final String COPIED_ADDR_FK_ID = "address_copiedAddressFK";
    public static final String REINS_CTRL_ADDR_ID = "address_reinsControlAddr";
    public static final String EXPIRED_B_COMPUTED_ID = "address_expiredBComputed";
    public static final String UNDELIVERABLE_B = "address_undeliverableB";

    public static final String ADDR_PK_RS_COL_NAME = "address_pk";
    public static final String ADDR_TYPE_CODE_DESC_RS_COL_NAME = "address_type_code_desc";
    public static final String ADDR_PRIM_ADDR_B_RS_COL_NAME = "primary_address_b";
    public static final String ADDR_EXPIRED_FLAG_RS_COL_NAME = "expired_flag";
    public static final String ADDR_SINGLE_LINE_RS_COL_NAME = "address_single_line";
    public static final String ADDR_EFF_FR_DT_RS_COL_NAME = "effective_from_date";
    public static final String ADDR_EFF_TO_DT_RS_COL_NAME = "effective_to_date";

    public static final String ENTITY_NAME = "entityName";
    public static final String ENTITY = "ENTITY";
    public static final String IN_ADDRESS_PK = "inAddressPK";
    public static final String IN_SOURCE_RECORD_FK = "inSourceRecordFK";
    public static final String DUMMY_SOURCE_RECORD_FK = "dummySourceRecordFK";
    public static final String ORIG_IN_SOURCE_RECORD_FK = "origInSourceRecordFK";
    public static final String IN_SOURCE_TABLE_NAME = "inSourceTableName";
    public static final String IN_ADDRESS_TYPE_CODE = "inAddressTypeCode";
    public static final String ALLOW_OTHER_CLIENT = "allowOtherClient";
    public static final String CLAIMS_ONLY_ADDR_ON_TOP = "claimsOnlyAddrOnTop";
    public static final String READ_ONLY = "readOnly";
    public static final String DATA_BEAN = "dataBean";
    public static final String GRID_HEADER_BEAN = "gridHeaderBean";
    public static final String CI_ADDRESS_SEARCH_ADD_GRID_XML_FILE = "CIAddressSearchAddGrid.xml";
    public static final String ALLOW_ATTACH_TO_CLIENT = "allowAttachToClient";
    public static final String FIND_CLIENT_BUTTON = "findClientBtn";
    public static final String ADD_BUTTON = "addBtn";
    public static final String DELETE_BUTTON = "deleteBtn";
    public static final String SAVE_BUTTON = "saveBtn";
    public static final String SELECT_BUTTON = "selectBtn";
    public static final String ADDRESS_SEARCH_ADD_ADDRLIST_LAYER = "Address_Search_Add_AddrList_Layer";
    public static final String FORM_COUNTY_CODE = "formCountyCode";
    public static final String FORM_COUNTY_DESC = "formCountyDesc";
    public static final String ADDR_TYPE_HIDE_POBOX_PARAM = "addrTypeHidePOBoxParam";
    public static final String ADDR_TYPE_HIDE_POBOX = "addrTypeHidePOBox";
    public static final String ALERT_MSG_ON_SAVE = "alertMsgOnSave";
    public static final String ADDRESS_SAVE_MSG_KEY = "addressSaveMsgKey";
    public static final String ADDRESS_ADD_MSG_KEY = "address.add.message";
    public static final String ADDRESS_UPDATE_MSG_KEY = "address.update.message";
    public static final String ADDRESS_TYPE_FILTER = "addressTypeFilter";
    public static final String ADDR_SEARCH_TOKEN = "dti.ci.struts.action.CIAddressSearchAdd.TOKEN";
    // expire success
    public static final String EXPIRE_ADDRESS_SUCCESS_FLAG = "expireAddressSuccess";
    // expire address token
    public static final String EXPIRE_ADDRESS_TOKEN = "dti.ci.struts.action.CIAddressExpire.TOKEN";
    // expire process desc
    public static final String EXPIRE_PROCESS_DESC = "expire";
    public static final String ORIGINAL_COUNTY_CODE = "original_countyCode";

    public static final String COUNTRY_CODE_USA = "USA";
}
