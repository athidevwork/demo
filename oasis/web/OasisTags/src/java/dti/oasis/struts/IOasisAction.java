package dti.oasis.struts;

/**
 * Interface defining Oasis Action constants
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p> 
 * @author jbe
 */
/*
 * Date:   Aug 11, 2003
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 6/7/2004     jbe         Add KEY_ENVENROLL_OASISPROF
 * 6/22/2004    jbe         Add KEY_ENVCLOBFACTORY
 * 7/16/2004    jbe         Add KEY_ENVVERBOSEERROR & KEY_ENVDISABLELOGINAUTOCOMPLETE
 * 9/7/20004    jbe         Add KEY_ENVCANREGISTERCIS & KEY_ENVREGISTERCISPERSONURL, KEY_ENVREGISTERCISORGURL
 * 4/20/2005    jbe         Add KEY_ENVSUBSCRIBEEVENTS
 * 6/23/2005    jbe         Add KEY_ENVDISALLOWPASSWORDCHANGE, KEY_ENVALLOWEDREFERRERS & KEY_ENVREQUIREREFERRER
 * 6/14/2006    sxm	        removed FIELD_SELECTEDTOPNAVMENU
 * 01/23/2007   wer         Added ERROR_ACTION_FWD
 * 01/29/2007   jmp         Added KEY_GRIDxxx for M1 gridDisplay.jsp
 * 02/09/2007   mlm         Added KEY_IS_DIV_POPUP_ENABLED for div popup support and
 *                          KEY_IS_MULTI_GRID_SUPPORT_ENABLED for multi-grid support
 * 03/14/2007   jmp         Added KEY_GRID_SORT for configuratbl sort parameters
 * 05/15/2007   mlm         Added KEY_TAB_MENU_IDS_TO_EXCLUDE for UI2
 * 07/11/2007   mlm         Added KEY_IS_GLOBAL_SEARCH_VISIBLE, KEY_IS_GLOBAL_SEARCH_DEF_HINT for UI2
 * 04/09/2008   wer         Added KEY_DBPOOLIDROLENAME
 * 05/13/2010   mxg         Added KEY_AD_USER
 * ---------------------------------------------------
 */

public interface IOasisAction {
    /**
     * Name of attribute to find associated OasisElements object for current
     * page and user.
     */
    public String KEY_ELEMENTS = "elementsMap";

    /**
     * Name of attribute to find associated OasisFields object for current
     * page and user.
     */
    public String KEY_FIELDS = "fieldsMap";
    /**
     * Name of attribute to find associated OasisFields object for include
     * page and user. like policyHeader, ClaimHeader and so on.
     */
    public String KEY_HEADER_PAGE_FIELDS = "headerPageFieldsMap";

    /**
     * Name of attribute for cached version
     * of fieldsMap
     */
    public String KEY_FIELDS_CACHED = "fieldsMapCached";

    /**
     * Key to OASIS User object in HttpSession
     */
    public String KEY_OASISUSER = "userBean";

    /**
     * Name of attribute to find userid in HttpSession.
     * @deprecated User KEY_OASISUSER to get OasisUser object and call getUserId() on it
     */
    public String KEY_USERID = "CS_sessionUserID";

    /**
     * Name of attribute to find anonymous userid in Web Context (web.xml)
     */
    public String KEY_ANONUSERID = "anonUserId";

    /**
     * Name of attribute to find db pool id in HttpSession
     */
    public String KEY_DBPOOLID = "CS_DBPoolID";

    /**
     * Name of attribute to find the Role Name configured for the db pool id in HttpSession
     */
    public String KEY_DBPOOLIDROLENAME = "DBPoolID_RoleName";

    /**
     * Name of attribute to find xdk Installation flag in HttpSession
     */
    public String KEY_XDK = "CS_xdkInstalled";

    /**
     * Name of attribute to find xdk Installation flag in Web Context (web.xml)
     */
    public String KEY_ENVXDK = "xdkInstalled";

    /**
     * Name of attribute to find db pool id in Web Context (web.xml)
     */
    public String KEY_ENVDBPOOLID = "dbPoolId";

    /**
     * Name of attribute to find PageBean in HttpServletRequest
     */
    public String KEY_PAGEBEAN = "pageBean";

    /**
     * Name of attribute to find exception in HttpServletRequest
     */
    public String KEY_ERROR = "exception";

    /**
     * Name of attribute to find validation exception in HttpServletRequest
     */
    public String KEY_VALIDATION_ERROR = "validationException";    

    /**
     * Represents MENUITEM record in pf_web_navigation_util;
     */
    public String TYPE_MENU = "MENUITEM";

    /**
     * Key to web app usage in request (scope)
     */
    public String KEY_AUTH = "authtype";

    /**
     * Key to # of days a password may be used before expiration.  Found
     * in web.xml
     */
    public String KEY_ENVPASSWORDEXPDAYS = "passwordExpDays";

    /**
     * Key to password change JSP in web.xml
     */
    public String KEY_ENVPASSWORDCHANEGJSP = "passwordChangeJSP";

    /**
     * Key to session entry for login
     */
    public String KEY_LOGINPROCESS = "loginprocess";

    /**
     * Parameter indicating a URL
     */
    public String PARM_URL = "parmUrl";

    /**
     * Parameter indicating login process
     */
    public String PARM_LOGIN = "parmLogin";

    /**
     * Key to J2EE Security Factory in web.xml
     */
    public String KEY_ENVJ2EESECFACTORY = "j2eeSecFactory";

    /**
     * Key to # of days of inactivity before a user id is locked
     */
    public String KEY_ENVINACTIVITYLOCKDAYS = "inactivityLockDays";

    /**
     * Key to whether a user can enroll in this application
     */
    public String KEY_ENVCANENROLL = "canEnroll";

    /**
     * Key to roles (Comma Separated) a user will be set up with
     */
    public String KEY_ENVENROLLROLE = "enroleRole";

    /**
     * Key to OASIS Profiles (Comma Separated) a user will be set up with
     */
    public String KEY_ENVENROLL_OASISPROF = "enrollOasisProfile";

    /**
     * Key to Clob Factory in web.xml
     */
    public String KEY_ENVCLOBFACTORY = "clobFactory";

    /**
     * Key to whether user can subscribe to events from user settings page
      */
    public String KEY_ENVSUBSCRIBEEVENTS = "events";
    
    /**
     * Key to whether verbose errors are displayed on error pages
     */
    public String KEY_ENVVERBOSEERROR = "verboseErrors";

    /**
     * Key to define if JavaScript Errors should be displayed in an alert dialog.
     */
    public String KEY_ENVALERTJSERRORS = "alertJSErrors";

	/**
	 * Key to whether AUTOCOMPLETE should be disabled on the login page    
	 */
    public String KEY_ENVDISABLELOGINAUTOCOMPLETE = "disableLoginAutoComplete";

    /**
     * Key to whether a user can register in CIS
     */
    public String KEY_ENVCANREGISTERCIS = "canRegisterCIS";

    /**
     * Key to URL where PERSON can register in CIS
     */
    public String KEY_ENVREGISTERCISPERSONURL = "registerCISPERSONURL";

    /**
     * Key to URL where ORG can register in CIS
     */
    public String KEY_ENVREGISTERCISORGURL = "registerCISORGURL";

	/**
	 * Key to whether password changes are allowed. If not allowed, the
	 * change password function & password reminder are disabled.
	 */
	public String KEY_ENVDISALLOWPASSWORDCHANGE = "disallowPasswordChange";
	
	/**
	 * Key to comma separated list of "permitted" referrers.  Web site URL's that
	 * have permission to request this application.
	 */
	public String KEY_ENVALLOWEDREFERRERS = "allowedReferrers";
	
	/**
	 * Key to whether a referrer must be present in the request header.
	 */
	public String KEY_ENVREQUIREREFERRER = "requireReferrer";

    /**
     * Global Struts Error Page Forward.
     */
    public String ERROR_ACTION_FWD = "error";

    /**
     * Key for web workbench field configuration to define the page size
     * for a grid.  Utilized by the gridDisplay JSP page.
     */
    public String KEY_GRID_PAGE_SIZE = "gridPageSize";

    /**
     * Key for web workbench field configuration to define the height
     * for a grid.  Utilized by the gridDisplay JSP page.
     */
    public String KEY_GRID_HEIGHT = "gridHeight";

    /**
     * Key for web workbench field configuration to define the DIV tag height
     * for a grid.  Utilized by the gridDisplay JSP page.
     */
    public String KEY_GRID_HOLDER_DIV_HEIGHT = "gridHolderDivHeight";

        /**
     * Key for web workbench field configuration to define the DIV tag width
     * for a grid.  Utilized by the gridDisplay JSP page.
     */
    public String KEY_GRID_HOLDER_DIV_WIDTH = "gridHolderDivWidth";

    /**
     * Key for web workbench field configuration to define the sort parameters
     * for a grid.
     */
    public String KEY_GRID_SORT = "gridSortOrder";


    /**
     * Key for overriding divpopup.enabled system property at request level
     */
    String KEY_IS_DIV_POPUP_ENABLED = "divpopup.enabled";

    /**
     * Key for overriding multigridsupport.enabled system property at request level
     */
    String KEY_IS_MULTI_GRID_SUPPORT_ENABLED = "multigridsupport.enabled";

    /**
     * Key for tab menu ids to exclude
     */
    String KEY_TAB_MENU_IDS_TO_EXCLUDE = "tabMenuIdsToExclude";

    /**
     * Key for providing the default visibility of global search section
     */
    String KEY_IS_GLOBAL_SEARCH_VISIBLE = "globalsearch.visible";

   /**
    * Key for providing the default hint text for global search
    */
    String KEY_IS_GLOBAL_SEARCH_DEF_HINT = "globalsearch.hint";

   /**
   * Key for providing the default visibility of global menu section
   */
   String KEY_IS_GLOBAL_MENU_VISIBLE = "globalmenu.visible";

  /**
   * Key for providing the default hint text for global search
   */
   String KEY_MENU_ID_FOR_ACTIVITY_HISTORY = "CS_WC_MY_ACTIVITY_HISTORY";

  /**
   * Key for providing the default hint text for global search
   */
   String KEY_MENU_ID_FOR_MY_DIARY = "CS_WC_MY_DIARY";

  /**
   * Key for providing the default hint text for global search
   */
   String KEY_MENU_ID_FOR_MY_WORKFLOW_QUEUE = "CS_WC_MY_OAW";

    /**
     *Key for providing the default global search forward page url 
     */
   String KEY_GLOBAL_SEARCH_FORWARD_PAGE_URL = "globalsearch.forward.page.url";

     /**
     *Key for providing the build number 
     */
   String KEY_BUILD_NUMBER = "build.number";

     /**
     *Key for Active Directory user
     */
   String KEY_AD_USER = "AD_USER";

     /**
     *Key for Prior Login DATE/TIME
     */
   String KEY_PRIOR_LOGIN_TS = "PRIOR_LOGIN_TS";


    /**
    *Key for db pool id used in ufe url request for non-interactive forms from ODS
    */
   String KEY_UFE_DBPOOL_ID = "db";

    /**
     *Key for user id used in ufe url request for non-interactive forms from ODS
    */
   String KEY_UFE_USER_Id = "userid";
}
