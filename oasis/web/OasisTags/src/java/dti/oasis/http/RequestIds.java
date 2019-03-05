package dti.oasis.http;

/**
 * Common Oasis Request parameter and attribute names.
 * 
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 6, 2006
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/14/2008       mlm         Added WEB_SESSION_ID.
 * 02/16/2011       Blake       Modified for issue 112690:Make Identifier Prominent .
 * 10/15/2013       hxk         Issue 148423
 *                              1)  Added GLOBAL_SEARCH_ENTITY_REFERENCE_NUMBER.
 * 02/28/2017       mlm         183387 - Added INVOKE_ODS
 * 05/17/2017       cesar       182477 - Added CACHE_MASKED_FIELDS to be use for encode/decode masked fields.
 * 02/21/2018       mlm         191625 - Refactored to add support for IE_X_UA_Compatible_Value
 * 05/14/2018       cesar       192983 - Added OASIS_XSS_OVERRIDES_FIELDS, DO_NOT_USE_XSS_FILTER, OASIS_MASKED_FIELDS.
 * 07/11/2018       cesar       193446 - Added CSRF_PROTECTION
 * ---------------------------------------------------
 */
public interface RequestIds {
    String PROCESS = "process";
    String IS_NEW_VALUE = "isNewValue";
    String TEXT_XML = "txtXML";
    String DATA_BEAN = "dataBean";
    String REQUEST_URI = "requestURI";
    String GRID_RECORD_SET = "gridRecordSet";
    String SELECT_IND = "SELECT_IND";
    String ACTION_ITEM_IDS_TO_REMOVE = "actionItemIdsToRemove";
    String GLOBAL_SEARCH_CLAIM_NO = "cmUserView_claimNo";      
    String GLOBAL_SEARCH_CASE_NO = "occurrence_occurrenceNo";
    String GLOBAL_SEARCH_ENTITY_FIRSTNAME = "searchCriteria_firstName";
    String GLOBAL_SEARCH_ENTITY_LAST_OR_ORG_NAME = "searchCriteria_lastOrOrgName";
    String GLOBAL_SEARCH_ENTITY_CLIENT_ID = "searchCriteria_clientId";
    String GLOBAL_SEARCH_ENTITY_REFERENCE_NUMBER = "searchCriteria_referenceNumber";
    String GLOBAL_SEARCH_FIELD_NAME = "globalSearchFieldName";
    String GLOBAL_SEARCH_FIELD_VALUE = "globalSearchFieldValue";
    String WEB_SESSION_ID = "webSessionId";
    String INITIAL_VALUES = "initialValues";
    String SAVE_INPROGRESS ="ProceedToken";  // Indicator for save in-progress
    String IS_SAME_HEADER_ID_B = "isSameHeaderIdB"; // Indicator for view Special Condition
    String APPLICATION_ID = "applicationId"; // Override for the applicationId
    String APPLICATION_TITLE = "applicationTitle"; // Override for the applicationTitle
    String BASE_HELP_URI = "baseHelpUri"; // Override for the baseHelpUri
    String PRODUCT_LOGO_2 = "productLogo2";
    String SELECT_FIELDS_WITH_EXPIRED_OPTION ="SELECT_FIELDS_WITH_EXPIRED_OPTION";
    String EXPIRED_OPTION_SUFFIX =  "EXPIRED_OPTION_SUFFIX";

    String NOTES_IMAGE = "notesImage";
    String NO_NOTES_IMAGE = "noNotesImage";

    String HEADER_FIELDS_RECORD = "HEADER_FIELDS_RECORD";
    String NON_GRID_FIELDS_RECORD = "NON_GRID_FIELDS_RECORD";
    String RECORDSET_MAP = "RECORDSET_MAP";
    String GRID_FACT_DATA_LIST = "GRID_FACT_DATA_LIST";
    String CACHE_ID_FOR_PAGE_VIEW = "__UWID";
    String CACHE_ID_FOR_PAGE_VIEW_DATA = "__UWID_DATA";
    String FORWARD_PARAMETERS = "forwardParamaters";

    String INVOKE_ODS = "invokeODS";
    String IE_X_UA_COMPATIBLE_VALUE = "IE_X_UA_Compatible_Value";

    String CACHE_MASKED_FIELDS = "CACHE_MASKED_FIELDS";

    String USE_JQX_GRID = "useJqxGrid";

    String OASIS_XSS_OVERRIDES_FIELDS = "OASIS_XSS_OVERRIDES_FIELDS";
    String DO_NOT_USE_XSS_FILTER = "N";
    String OASIS_MASKED_FIELDS = "OASIS_MASKED_FIELDS";

    String CSRF_PROTECTION = "CSRF_PROTECTION";
}
