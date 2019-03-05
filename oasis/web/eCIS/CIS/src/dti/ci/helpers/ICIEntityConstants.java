package dti.ci.helpers;

/**
 * Interface for entity constants.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author Gerald C. Carney
 *         Date:   Dec 4, 2003
 *         <p/>
 *         Revision Date    Revised By  Description
 *         ---------------------------------------------------
 *         02/16/2007       kshen       Added constant DBA_DATA_BEAN, ENTITY_DBA_NAME_ID
 *         07/02/2007       FWCH        Added constant CHANGE_ENTITY_TYPE
 *         03/02/2009       Leo         Issue 87902.
 *         10/19/2009       hxk         Add SHOW_HIDE_EXP_WIT constant for issue 97591.
 *         07/04/2011       Michael     Issue 117347.
 *         10/26/2011       hxk         Issue 125683 - Add component field.
 *         04/03/2013       kshen       Issue 141547
 *         04/15/2013       bzhu        Issue 139501
 *         10/23/2014       bzhu        Issue 157844
 *         ---------------------------------------------------
 */

public interface ICIEntityConstants {

    public static final String PREFIX_NAME_LOOKUP_CODE = "CLIENT_PREFIX";
    public static final String SUFFIX_NAME_LOOKUP_CODE = "CLIENT_SUFFIX";

    public static final String OK_TO_SKIP_ENTITY_DUPS_PROPERTY = "okToSkipEntityDups";
    public static final String OK_TO_SKIP_TAX_ID_DUPS_PROPERTY = "okToSkipTaxIDDups";
    public static final String DUP_LIST_PROPERTY = "duplicateList";
    public static final String HIDE_TAX_ID_PROPERTY = "CI_WEB_HIDE_TAX_ID";
    public static final String SHOW_HIDE_EXP_WIT = "CI_SHOW_HIDE_EXP_WIT";

    public static final String DUP_REC_TAG = "duplicate";
    public static final String CLIENT_ID_TAG = "clientID";
    public static final String FULL_NAME_TAG = "fullName";
    public static final String TAX_ID_TAG = "taxID";
    public static final String EMAIL_TAG = "email";
    public static final String CITY_STATE_TAG = "cityState";
    public static final String CITY_ADDR1_TAG = "addr1";
    public static final String CITY_ADDR2_TAG = "addr2";
    public static final String CITY_ZIPCODE_TAG = "zipcode";
    public static final String CITY_LICENSE_TAG = "license";
    public static final String Data_Bean = "dataBean";
    public static final String Name_Data_Bean = "nameDataBean";
    public static final String Tax_Data_Bean = "taxDataBean";
    public static final String Loss_Data_Bean = "lossDataBean";
    public static final String DBA_DATA_BEAN = "dbaDataBean";
    public static final String ETD_DATA_BEAN = "etdDataBean";


    public static final String ENTITY_PK_ID = "entity_entityPK";
    public static final String CLIENT_ID_ID = "entity_" + CLIENT_ID_TAG;
    public static final String GENDER_ID = "entity_gender";
    public static final String ENTITY_TYPE_ID = "entity_entityType";
    public static final String ENTITY_TYPE_DESC_COMPUTED_ID = "entity_entityTypeDescComputed";
    public static final String ORG_NAME_ID = "entity_organizationName";
    public static final String FIRST_NAME_ID = "entity_firstName";
    public static final String MIDDLE_NAME_ID = "entity_middleName";
    public static final String LAST_NAME_ID = "entity_lastName";
    public static final String PREFIX_NAME_ID = "entity_prefixName";
    public static final String SUFFIX_NAME_ID = "entity_suffixName";
    public static final String PROFESSIONAL_DESIGNATION_ID = "entity_profDesignation";
    public static final String TITLE_ID = "entity_title";
    public static final String VERY_LONG_NAME_ID = "entity_veryLongName";
    public static final String SSN_ID = "entity_socialSecurityNumber";
    public static final String SSN_VERIFIED_B_ID = "entity_ssnVerifiedB";
    public static final String FED_TAX_ID_ID = "entity_federalTaxID";
    public static final String FED_TAX_ID_VERIFIED_B_ID = "entity_federalTaxIDVerifiedB";
    public static final String DEFAULT_TAX_ID_ID = "entity_defaultTaxID";
    public static final String TAX_INFO_EFF_DATE_ID = "entity_taxInfoEffectiveDate";
    public static final String MARITAL_STATUS_ID = "entity_maritalStatus";
    public static final String VIP_B_ID = "entity_veryImportantPersonB";
    public static final String DATE_OF_BIRTH_ID = "entity_dateOfBirth";
    public static final String DATE_OF_DEATH_ID = "entity_dateOfDeath";
    public static final String DECEASED_B_ID = "entity_deceasedB";
    public static final String MINOR_B_ID = "entity_minorB";
    public static final String DISCARDED_B_ID = "entity_discardedB";
    public static final String EMAIL_ADDRESS_1_ID = "entity_eMailAddress1";
    public static final String EMAIL_ADDRESS_2_ID = "entity_eMailAddress2";
    public static final String EMAIL_ADDRESS_3_ID = "entity_eMailAddress3";
    public static final String COUNTRY_CODE = "address_countryCode";
    public static final String LEGACY_DATA_ID_ID = "entity_legacyDataID";
    public static final String CHAR_1_ID = "entity_char1";
    public static final String CHAR_2_ID = "entity_char2";
    public static final String CHAR_3_ID = "entity_char3";
    public static final String CHAR_4_ID = "entity_char4";
    public static final String CHAR_5_ID = "entity_char5";
    public static final String NUM_1_ID = "entity_num1";
    public static final String NUM_2_ID = "entity_num2";
    public static final String NUM_3_ID = "entity_num3";
    public static final String DATE_1_ID = "entity_date1";
    public static final String DATE_2_ID = "entity_date2";
    public static final String DATE_3_ID = "entity_date3";
    public static final String SIC_CODE_ID = "entity_sicCode";
    public static final String LOSS_FREE_DATE_ID = "entity_lossFreeDate";
    public static final String CLAIMS_FREE_DATE_ID = "entity_claimsFreeDate";
    public static final String INSURED_SINCE_DATE_ID = "entity_insuredSinceDate";
    public static final String SPECIAL_HANDLING_ID = "entity_specialHandlingComputed";
    public static final String MINOR_B_COMPUTED_ID = "entity_minorBComputed";
    public static final String ENTITY_NAME_COMPUTED_ID = "entity_entityNameComputed";
    public static final String ENTITY_WEB_ADDRESS_1_ID = "entity_webAddress1";
    public static final String ENTITY_WEB_ADDRESS_2_ID = "entity_webAddress2";
    public static final String ENTITY_WEB_ADDRESS_3_ID = "entity_webAddress3";
    public static final String ENTITY_DBA_NAME_ID = "entity_dbaName";
    public static final String ENTITY_HICN_ID = "entity_hicn";
    public static final String ENTITY_LEGAL_NAME_ID = "entity_legalName";
    public static final String ENTITY_REFERENCE_NUMBER = "entity_referenceNumber";
    public static final String ENTITY_COMPONENT = "entity_component";
    public static final String ENTITY_ELECTRONIC_DISTRB_B = "entity_electronicDistrbB";
    public static final String CI_ADDL_INFO1 = "entity_ciAddlInfo1";
    public static final String CI_ADDL_INFO2 = "entity_ciAddlInfo2";
    public static final String CI_ADDL_INFO3 = "entity_ciAddlInfo3";
    public static final String ENTITY_LEGAL_NAME_EFF_DT_ID = "entity_legalNameEffectiveDate";

    public static final String DUP_TAX_ID_SYS_PARM_ERROR_VALUE = "ERROR";
    public static final String DUP_TAX_ID_SYS_PARM_WARNING_VALUE = "WARNING";
    public static final String DUP_TAX_ID_SYS_PARM_PROFILE_VALUE = "PROFILE";

    public static final String VENDOR_VERIFY_SYS_PARAM = "CM_CHK_VENDOR_VERIFY";

    public static final String CLIENT_DISCARDED_MSG = "clientDiscardedMsg";
    public static final String DISCARD_CLIENT_SYS_PARAM_ERROR = "ERROR";
    public static final String DISCARD_CLIENT_SYS_PARAM_IGNORE = "IGNORE";
    public static final String DISCARD_CLIENT_SYS_PARAM_WARNING = "WARNING";
    public static final String DISCARD_CLIENT_SYS_PARAM = "CS_DISCARD_CLIENT";
    public static final String ENTITY_LEGACY_DATAID = "entity_legacyDataID";
    public static final String ENTITY_EXTERNAL_DATAID = "entity_externalDataId";
    public static final String CHANGE_ENTITY_TYPE = "changetype";
    public static final String ENTITY_LICENSE = "entity_license";
    public static final String ENTITY_LICENSE_STATE = "entity_licenseState";

    public static final String MOREOB_START_DATE = "moreobStartDate";
    public static final String MOREOB_OFF_DATE = "moreobOffDate";
    public static final String MOREOB_MAINTENANCE_START = "maintenanceStart";
    public static final String MOREOB_GROUP_CODE = "moreobGroupCode";

    public static final String CI_ENTITY_CONTINUE_ADD = "CI_ENTY_CONTINUE_ADD";
    
    public static final String CI_REUSE_ADDRESS_CLEAR = "CI_REUSE_ADDR_CLEAR";
    public static final String CI_REUSE_PHONE_CLEAR = "CI_REUSE_PHONE_CLEAR";
    public static final String CI_REUSE_CLASSIFICATION_CLEAR = "CI_REUSE_CLASS_CLEAR";
                                                                //address_countryCode,address_primaryAddressB
    public static final String CI_ENTY_ADD_REUSE_FIELDS_ADDRESS="address_addressTypeCode,address_addressLine1,address_addressLine2,"
                                                        +"address_addressLine3,address_city,address_stateCode,address_countyCode,address_zipCode,address_zipPlusFour,"
                                                        +"address_effectiveFromDate,address_postOfficeAddressB,address_province,address_zipCodeForeign,address_countryCode,"
                                                        +"address_usaAddressB,";
    public static final String CI_ENTY_ADD_REUSE_FIELDS_PHONE="phoneNumber_phoneNumberTypeCode,phoneNumber_areaCode,phoneNumber_phoneNumber,phoneNumber_phoneExtension,"
                                                        +"phoneNumber_notRelatedToAddressBComputed,phoneNumber_primaryNumberB,";
    public static final String CI_ENTY_ADD_REUSE_FIELDS_CLASSIFICATION="entityClass_entityClassCode,entityClass_effectiveFromDate,entityClass_effectiveToDate,"
                                                        +"entityClass_entitySubClassCode,entityClass_entitySubTypeCode,";

}
