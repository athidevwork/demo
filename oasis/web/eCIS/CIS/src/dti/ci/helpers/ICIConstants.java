package dti.ci.helpers;

import java.text.SimpleDateFormat;

/**
 * Interface for constants for all of CIS.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author Gerald C. Carney
 *         Date:   Dec 4, 2003
 *         <p/>
 *         Revision Date    Revised By  Description
 *         -----------------------------------------------------------------------
 *         04/12/2005       HXY         Added contants for controlling grid size.
 *         05/15/2007       mlm         Added KEY_CIS_TAB_MENU_GROUP_ID,
 *                                      KEY_TAB_MENUIDS_EXCLUDELIST_FOR_ORGANIZATION_TYPE for UI2
 *         07/28/2009       Leo         Issue 95771
 *         10/14/2009       Jacky       Add 'Jurisdiction' logic for issue #97673
 *         03/03/2011       Michael Li  Add CIS_PAGE_INACT_FLR for issue #116335
 *         <p/>
 *         -----------------------------------------------------------------------
 */

public interface ICIConstants {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
    public static final String ORACLE_DATE_FORMAT = "mm/dd/yyyy";

    public static final String UNEXPIRED_EFF_TO_DATE = "01/01/3000";
    public static final String UNEXPIRED_EFF_FROM_DATE = "01/01/1900";

    public static final String ENTITY_TYPE_PERSON_STRING = "P";
    public static final String ENTITY_TYPE_ORG_STRING = "O";
    public static final char ENTITY_TYPE_PERSON_CHAR = 'P';
    public static final char ENTITY_TYPE_ORG_CHAR = 'O';

    public static final String PK_PROPERTY = "pk";
    public static final String ENTITY_TYPE_PROPERTY = "entityType";
    public static final String ENTITY_NAME_PROPERTY = "entityName";
    public static final String ENTITY_FK_PROPERTY = "entityFK";
    public static final String FORM_ACTION_PROPERTY = "formAction";
    public static final String CHECKBOX_SPAN_PROPERTY = "CheckBoxSpan";
    public static final String TXT_XML_PROPERTY = "txtXML";
    public static final String PROCESS_PROPERTY = "process";
    public static final String MSG_PROPERTY = "msg";
    public static final String MSG_PARM_PROPERTY = "msgParm";
    public static final String IS_NEW_VAL_PROPERTY = "isNewValue";
    public static final String SQL_OPERATION_PROPERTY = "sqlOperation";
    public static final String SOURCE_TBL_NAME_PROPERTY = "sourceTableName";
    public static final String CLAIM_SOURCE_TBL_NAME = "CLAIM";
    public static final String SOURCE_REC_FK_PROPERTY = "sourceRecordFK";
    public static final String LIST_DISPLAYED_PROPERTY = "listDisplayed";

    public static final String INSERT_CODE = "INSERT";
    public static final String UPDATE_CODE = "UPDATE";

    public static final String SAVE_PROCESS_DESC = "save";
    public static final String ADD_PROCESS_DESC = "add";
    public static final String MODIFY_PROCESS_DESC = "modify";
    public static final String CLOSE_PROCESS_DESC = "close";
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";

    String SYSPARM_RES_CI_CONTACT_SET_ADDR = "CI_CONTACT_SET_ADDR";
    String ADDRESS_FK = "ADDRESSFK";
    String PRIMARY_ADDRESS_B_DESC = " 2";
    
    public static final String EVENT_NAME_PROPERTY = "eventName";

    public static final String GRID_HEIGHT_PROPERTY = "gridHeight";
    public static final String GRID_HOLDER_DIV_HEIGHT_PROPERTY = "gridHolderDivHeight";
    public static final String GRID_HOLDER_DIV_WIDTH_PROPERTY = "gridHolderDivWidth";
    public static final String GRID_PAGE_SIZE_PROPERTY = "gridPageSize";

    public static final String GRID_DATA_BEAN = "gridDataBean";
    public static final String GRID_HEADER_BEAN = "gridHeaderBean";

    public static final String LABEL_FOR_YES = "Yes";
    public static final String LABEL_FOR_NO = "No";

    public static final String VALUE_FOR_YES = "Y";
    public static final String VALUE_FOR_NO = "N";

    public static final String EMPTY_STRING = "";

    public static final String KEY_SYSPARMS = "sysParms";

    public String KEY_POPMSG = "popmsg";
    public String KEY_POPMSGPARM = "popmsgparm";

    public String GRID_BASE_XML_DATA = "<RS></RS>";
    //Added by Fred on 1/4/2007
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

    /**
     * Key for locating tab menu group id
     */
    public static final String KEY_CIS_TAB_MENU_GROUP_ID = "cis.tabmenugroupid";

    /**
     * Key for locating tab menu group id
     */
    public static final String KEY_TAB_MENUIDS_EXCLUDELIST_FOR_ORGANIZATION_TYPE = "cis.organization.tab.excludelist";

    public static final String PRINT_TYPE = "printType";

    public static final String INFO_POLICY_NO = "info_policy_number";

    final String SYS_PARA_CASE_CLAIM_JURIS_OPT = "CASE_CLAIM_JURIS_OPT";
    final String CASE_CLAIM_JURIS_VALUE_A = "a";
    final String CASE_CLAIM_JURIS_VALUE_B = "b";
    /**
     * Comma separated list of CIS pages to display filter for active-inactive records
     */
    public static final String CIS_PAGE_INACT_FLR = "CIS_PAGE_INACT_FLR";
    public static final String CIS_REFRESH_HEADER_FIELDS = "__REFRESH_CIS_HEADER_FIELDS";
    
    public static final String CS_CLIENT_ID_FORMAT_VALUE = "000000000#";
    public static final String CS_CLIENT_ID_FORMAT_PREFIX = "0000000000";

    public static final String SYS_PARA_COUNTRY_CODE_CONFIG = "COUNTRY_CODE_CONFIG";
    public static final String SYS_PARA_COUNTRY_CODE_USA = "COUNTRY_CODE_USA";

    public static final String VELOCITY_SOURCE_NAME = "VELOCITY";

    public static final String ENTITY_ID = "entityId";
}
