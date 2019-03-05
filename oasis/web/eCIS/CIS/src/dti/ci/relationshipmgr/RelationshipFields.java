package dti.ci.relationshipmgr;

import dti.ci.helpers.ICIConstants;

import java.text.SimpleDateFormat;

/**
 * Interface for CIS relationship constants.
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 10, 2005
 *
 * @author HXY
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 02/12/2007       kshen       Added constant SAVE_SUCCESS_DESC for issue 63166
 * 03/24/2009       Jacky       Added constant for issue #89413
 *                              Address & Phone number fields
 * 03/25/2010       kshen       Added constants for issue 101585.
 * 02/15/2011       Michael     Changed for 112658.
 * 04/19/2011       kshen       Added the constant REVERSE_RELATION_INDICATOR_IMG.
 * 12/22/2011       Michael     Changed for 127479 refact this page.
 * ---------------------------------------------------
*/

public interface RelationshipFields extends ICIConstants {
    public static final String RELATIONSHIP_LIST_FILTER = "relationshipListFilter";
    public static final String RELATIONSHIP_LIST_FILTER_PREF = "relationshipListFilterPref";
    public static final String DATE_OF_BIRTH = "dateOfBirth";
    public static final String ENTITY_HAS_POLICY = "entityHasPolicy";

    public static final String PREV_RELATION_TYPE_CODE = "prevRelationTypeCode";
    public static final String PREV_EFFECTIVE_FROM_DATE = "prevEffectiveFromDate";
    public static final String REVERSE_RELATION_INDICATOR = "reverseRelationIndicator";
    public static final String REVERSE_RELATION_INDICATOR_IMG = "reverseRelationIndicatorImg";
    public static final String RELATION_TYPE_CODE = "relationTypeCode";
    public static final String EFFECTIVE_FROM_DATE = "effectiveFromDate";
    public static final String EFFECTIVE_TO_DATE = "effectiveToDate";
    public static final String DOING_BUSINESS_AS = "doingBusinessAs";
    public static final String ENTITY_CHILD_FK = "entityChildFK";
    public static final String ENTITY_PARENT_FK = "entityParentFK";
    public static final String ENTITY_RELATION_PK = "entityRelationPK";
    public static final String NAME_COMPUTED = "nameComputed";
    public static final String ADDL_INFO1 = "addlInfo1";
    public static final String ADDL_INFO2 = "addlInfo2";
    public static final String DIFF_ENTITY_FK = "diffEntityFK";
    public static final String ADDL_DATA1 = "addlData1";
    public static final String ACCOUNTING_TO_DATE = "accountingToDate";
    public static final String ADDL_RELATION_TYPE_CODE = "addlRelationTypeCode";
    public static final String PRIMARY_AFFILIATION_B = "primaryAffiliationB";
    public static final String NON_EDITABLE_CODE_COMPUTED = "nonEditableCodeComputed";
    public static final String PERCENT_PRACTICE = "percentPractice";
    public static final String PAY_EXCESS_B = "payExcessB";
    public static final String VAP_B = "vapB";
    public static final String NOTES_IND = "notesInd";

    public static final String PHONE_NUMBER_PK = "phoneNumberPK";
    public static final String AREA_CODE= "areaCode";
    public static final String PHONE_NUMBER_TYPE_CODE = "phoneNumberTypeCode";
    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String PHONE_EXTENSION = "phoneExtension";
    public static final String ADDRESS_PK= "addressPK";
    public static final String ADDRESS_TYPE_CODE = "addressTypeCode";
    public static final String ADDRESS_LINE1 = "addressLine1";
    public static final String ADDRESS_LINE2 = "addressLine2";
    public static final String CITY = "city";
    public static final String COUNTY_CODE = "countyCode";
    public static final String PROVINCE = "province";
    public static final String FOREIGNP_PROVINCE = "foreignProvince";
    public static final String STATE_CODE = "stateCode";
    public static final String ZIPCODE = "zipcode";
    public static final String ZIPCODE_FOREIGN = "zipCodeForeign";
    public static final String EMAIL_ADDRESS= "emailAddress";
    public static final String JOB_TITLE= "jobTitle";
    public static final String POLICY_NUMBER= "policyNumber";
    public static final String FAX_NUMBER= "faxNumber";
    public static final String COUNTRY_CODE= "address_countryCode";

    public static final String RELATION_TYPE_CODE_VALUE = "relationTypeCodeValue";
    public static final String EFFECTIVE_FROM_DATE_VALUE = "effectiveFromDateValue";
    public static final String EFFECTIVE_TO_DATE_VALUE = "effectiveToDateValue";

    public static final String REFRESH = "refresh";
    public static final String ADD = "add";
    public static final String EDIT = "edit";
    public static final String MODE = "mode";

    public static final String RELATIONSHIP_LIST_FILTER_ALL = "ALL";
    public static final String RELATIONSHIP_LIST_FILTER_ACTIVE = "ACTIVE";
    public static final String RELATIONSHIP_LIST_FILTER_EXPIRED = "EXPIRED";
    public static final String RELATIONSHIP_LIST_FILTER_DEFAULT = "DEFAULT";
    public static final String USER_ID = "userId";
    public static final String PREFERENCE_CODE = "preferenceCode";
    public static final String PREFERENCE_CS_REL_TAB_FLT_DISP= "CS_REL_TAB_FLT_DISP";

    public static final String SYSPARM_PM_CIS_WIP_CHG = "PM_CIS_WIP_CHG";
    public static final String SYSPARM_PM_CIS_WIP_CHG_RTYPE = "PM_CIS_WIP_CHG_RTYPE";
    public static final String SYSPARM_PM_CIS_WIP_CHG_RTYPE_DEFAULT = "xYz";
    public static final String SYSPARM_CM_CLIENTREL_ADDL1 = "CM_CLIENTREL_ADDL1";
    public static final String SYSPARM_CM_CLIENTREL_ADDL2 = "CM_CLIENTREL_ADDL2";
    public static final String SYSPARM_CS_INCL_SAMEFFDT_REL = "CS_INCL_SAMEFFDT_REL";
    public static final String SYSPARM_CS_EXCL_ENDDT_REL = "CS_EXCL_ENDDT_REL";
    public static final String SYSPARM_CS_VAL_PRIM_EMP = "CS_VAL_PRIM_EMP";

    public static final String SAVE_SUCCESS_DESC = "saveSuccess";
    public static final String SAVE_FAILED_DESC = "saveFailed";
    public static final String SAVE_RESULT = "saveResult";
    public static final String EXPIRE_RELATIONSHIP_PROCESS_DESC = "expireRelationships";
    public static final String SELECTE_RECORD_IDS = "selectedRecordIds";
    public static final String EXP_DATE = "expDate";

    public static final String ENTITY_CHILD_TYPE="entityChildType";
    public static final String POLICY_STATUS="policyStatus";

    public static final String USA_COUNTRY_CODE = "USA";
    public static final String RELATIONSHIP_DESC = "relationshipDesc";
}
