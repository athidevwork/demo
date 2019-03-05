package dti.pm.core.http;

/**
 * Common ePolicy Request parameter and attribute names.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 19, 2006
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/09/2007       sxm         Added RISK_ID, COVERAGE_ID, and COVERAGE_CLASS_ID
 * 08/23/2007       sxm         Added POLICY_LIST
 * 08/31/2007       Mark        Added TRANSACTION_ID,TERM_BASE_RECORD_ID,POLICY_ID
 * 01/19/2011       wfu         Added IS_FROM_COPY_NEW for issue 113566.
 * 02/22/2011       wfu         Added IS_TRIGGER_FORMS for issue 113063.
 * 04/06/2011       fcb         119324: added isNewPolicyCreated
 * 06/10/2011       wqfu        103799 - Added ENTITY_ID, IS_COPY_ACTS_STATS
 * 05/29/2013       jshen       141758 - Added POLICY_COV_COMPONENT_ID
 * 06/06/2013       awu         138241 - Added ENTITY_ROLE_ID
 * 03/13/2015       awu         161778 - Added isSamePolicyB and PreviousPolicyNo.
 * 08/21/2015       wdang       165535 - Added endQuoteId.
 * 09/22/2015       Elvin       Issue 160360: added IS_PREVIEW_B
 * 03/25/2016       eyin        Issue 170323: added FROM_FIND_POLICY_B/ORG_SORT_COLUMN/ORG_SORT_TYPE/ORG_SORT_ORDER
 *                                            /ORG_ROW_ID and BACK_TO_LIST_ORG_INFO.
 * 02/28/2017       mlm         183387 - changed IS_PREVIEW_B to IS_PREVIEW_REQUEST
 * ---------------------------------------------------
 */
public interface RequestIds extends dti.oasis.http.RequestIds {

    public final static String POLICY_NO = "policyNo";
    public final static String POLICY_HEADER = "policyHeader";
    public final static String POLICY_TERM_HISTORY_ID = "policyTermHistoryId";
    public final static String POLICY_VIEW_MODE = "policyViewMode";
    public final static String SELECTED_POLICY_VIEW_MODE = "selectedPolicyViewMode";
    public final static String IS_POLICY_VIEW_MODE_VISIBLE = "isPolicyViewModeVisible";
    public final static String POLICY_SEARCH_MAX_ROWS = "PolicySearchMaximumRows"; // max rows to return per sys config
    public final static String POLICY_SEARCH_TOTAL_ROWS ="PolicySeachTotalRows";  // how many returned
    public final static String SAVE_INPROGRESS ="ProceedToken";  // Indicator for save in-progress    
    public final static String INITIAL_VALUES = "initialValues";
    public static final String RISK_ID = "riskId";
    public static final String COVERAGE_ID = "coverageId";
    public static final String POLICY_COV_COMPONENT_ID = "policyCovComponentId";
    public static final String COVERAGE_CLASS_ID = "coverageClassId";
    public static final String TRANSACTION_ID = "transactionId";
    public static final String TERM_BASE_RECORD_ID = "termBaseRecordId";
    public static final String POLICY_ID = "policyId";
    public static final String WORKFLOW_FOR = "workflowFor";
    public static final String IS_FROM_COPY_NEW = "isFromCopyNew";  // to determine if invoke from copy new
    public static final String IS_TRIGGER_FORMS = "isTriggerForms";  // to determine if invoke forms trigger 
    public static final String IS_NEW_POLICY_CREATED = "isNewPolicyCreated";
    public static final String ENTITY_ID = "entityId";
    public static final String IS_COPY_ACTS_STATS = "isCopyActsStats"; // to determine if invoke from prior acts copy
    public static final String ENTITY_ROLE_ID = "EntityRoleId";
    public static final String IS_SAME_POLICY_B = "isSamePolicyB"; // Indicator for view Special Condition
    public static final String PREVIOUS_POLICY_NO = "PreviousPolicyNo";
    public static final String END_QUOTE_ID = "endQuoteId";
    public static final String IS_PREVIEW_REQUEST = "isPreviewRequest";
    public static final String FROM_FIND_POLICY_B = "fromFindPolicyB";
    public static final String ORG_SORT_COLUMN = "orgSortColumn";
    public static final String ORG_SORT_TYPE = "orgSortType";
    public static final String ORG_SORT_ORDER = "orgSortOrder";
    public static final String ORG_ROW_ID = "orgRowId";
    public static final String BACK_TO_LIST_ORG_INFO = "backToListOrgInfo";
    public static final String QQ_DEFAULTS_CACHE = "quickQuoteDefaultsCache";
}
