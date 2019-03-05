package dti.oasis.request;

/**
 * This interface provides with a list of request storage id constants that are expected to exists in storage manager
 * for a given request thread.
 * 
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 29, 2007
 *
 * @author mlmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/16/2009       fcb         98370: HTTP_SEVLET_REQUEST added.
 * 07/11/2018       cesar       193446 - added IS_STRUTS_LOAD_ACTION
 * ---------------------------------------------------
 */
public interface RequestStorageIds {
    public final static String IS_CONNECTION_POOL_RESETTED = "connectionPoolResetted";
    public final static String DATA_SECURITY_PAGE_DEF_LOAD_PROCESSOR = "DataSecurityPageDefLoadProcessor";
    public final static String HTTP_SEVLET_REQUEST = "HttpServletRequest";
    public static final String EXECUTING_IN_BACKGROUND_THREAD = "ExecutingInBackgroundThread";
    public final static String IS_PROCESS_EXCLUDED_FOR_OBR = "IS_PROCESS_EXCLUDED_FOR_OBR";
    public final static String STRUTS_ACTION_CLASS = "STRUTS_ACTION_CLASS";
    public static final String CURRENT_GRID_ID = "currentGridId";
    public static final String USE_LABEL_FOR_EMPTY_OPTION = "UseLabelForEmptyOption";
    public static final String IS_STRUTS_LOAD_ACTION = "isStrutsLoadAction";
    public static final String QUEUED_DATA_FOR_PAGE_VIEW_CACHE = "QueuedDataForPageViewCache";

}
