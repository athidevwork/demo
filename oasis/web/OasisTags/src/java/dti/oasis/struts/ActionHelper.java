package dti.oasis.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.http.RequestIds;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.navigationmgr.NavigationManager;
import dti.oasis.obr.RequestHelper;
import dti.oasis.pageentitlementmgr.PageEntitlementManager;
import dti.oasis.pageentitlementmgr.impl.PageEntitlementGroup;
import dti.oasis.recordset.Field;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.security.Authenticator;
import dti.oasis.security.FormFileWrapper;
import dti.oasis.tags.*;
import dti.oasis.util.*;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.upload.FormFile;
import org.apache.struts.util.RequestUtils;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class with static methods to support Struts Actions in OASIS
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 29, 2003
 *
 * @author jbe
 */

/*
 * Revision Date    Revised By  Description
 * ----------------------------------------------------------------------------
 * 12/4/2003		jbe			Change from protected getAnonUser to
 *                              public getCurrentUserId
 *                              Add setCurrentUser & call it from getConnection
 * 12/22/2003       jbe         Add new getCurrentUser method that returns
 * an OasisUser object.  Replace original getCurrentUser
 * method with getCurrentUserId method.
 * 1/8/2004         jbe         Added getRealPath
 * 1/13/2004        jbe         Added mapToBeans, mapToXML, formToXML
 * 2/5/2004         jbe         Added secureNav
 * 2/6/2004         jbe         Add Logging
 * 2/17/2004        jbe         Throw IllegalAccessException from securePage & secureNav
 * if user is invalid or should not see page.
 * Add isUserValidForPage
 * 2/20/2004        jbe         Refactor various methods out to other Util classes,
 * deprecating the ones in this class
 * 2/23/2004        JBE         Added getFormString
 * 6/10/2004        jbe         Add lovToXML and sendLovs
 * 11/11/2004       jbe         Use PreparedStatements
 *                              Add new overloaded securePage
 * 2/11/2005        jbe         Add getValidationMessages methods.
 *                              Add getFields & getElements methods.
 * 3/30/2005        jbe         Change security SQL
 * 4/5/2005         jbe         Add call to DatabaseUtils.setProtectedFields in getConnection.
 * 5/10/2005        jbe         Add new toExcel with DisconnecteResultSet parm & new writeExcelHeader
 * 9/1/2005			jbe			Change sendLovs to be ajax based
 * 9/2/2005         jbe         Support Struts 1.2 - Replace RequestUtils with TagUtils.getInstance()
 * 10/12/2005       jbe         In toExcel - Don't set Cache-Control and Pragma/nocache headers because IE chokes
 *                              on them over SSL. ALso use inline disposition.
 * 12/8/2005        weh         Add logic to securePage the stores the user
 *                              bean in the session. This was copied from the
 *                              logic in header.jsp and was put here to handle
 *                              cases when a user times out and is forwarded
 *                              back to the page after logging in again.  In
 *                              these cases, the action class will be invoked
 *                              before the jsp which could be a problem if the
 *                              action class assumes the bean will be present.
 * 04/04/2006       GCC         Added new method getBooleanFromYNField.
 * 04/05/2006       GCC         Added new method getDefaultValue.
 * 11/22/2006       SJZ         Added logic to get dbpool ID from request if not in session
 * 11/22/2006       SJZ         Move getConnection call a bit later inside of securePage method
 * 09/27/2006       MLM         1. Changed securePage method to add new PageDefLoadProcessor parameter.
 *                              2. Changed existing securePage method to pass an instance of DefaultPageDefLoadProcessor
 *                                 for PageDefLoadProcessor parameter - For backward compatibility
 *                              3. Added new overloaded for securePage method to accept PageDefLoadProcessor parameter.
 * 01/23/2007       wer         Changed use of InitialContext to using ApplicationContext;
 *                              Changed usage of new Boolean(x) in logging to String.valueOf(x);
 *                              Added isUserValidForPage() to validate a user has access to a page without loading OasisFields;
 *                              Added recordToBeans() to add DynaBeans for each Field in the given Record
 * 02/09/2007       wer         Support loading resources from Classpath if they cannot be found on disk.
 * 04/02/2007       sxm         Added securePage(HttpServletRequest request, String className, boolean includeLayerFields)
 * 05/03/2007       mlm         Code refactor to make use of NavigationManager for rendering global menu.
 * 09/27/2007       gjl         added getFormFile to support file upload
 * 04/09/2008       wer         Enhanced to support configuring a dbPoolId for a role associated with a user or it's group
 * 07/14/2008       mlm         Enhanced to publish web session id to DB.
 * 09/25/2008       Larry       Issue 86826 DB connection leakage change
 * 10/06/2009       fcb         Issue 96764: SQL_CHECKPAGE modified: enforced security for pages that are defined as
 *                              secured, but do not have any profile assigned.
 * 11/23/2009       qlxie       Modified initializeOasisUser() to set user with db parameter value passed from ODS.
 * 01/14/2010       James       Load the pageBean, even when loadFields is false
 * 08/31/2010       hxk         Added logic for multi-selects so that we will split up comma delimited String into
 * *                            String[].
 * 04/21/2011       James       Issue#119774 Publish the default values in ActionHelper.securePage
 * 09/27/2011       bhong       Refactor logics that initialize OasisUser
 * 10/03/2011       bhong       Made changes in initializeOasisUser and not create a new OasisUser and set it in the session unless db != null
 * 10/18/2011       Michael Li  issue 126170: The fieldId + "_IS_CHECKBOX" field only exists after the page is loaded,
 *                              so the default value is only used the very first time the page is loaded.
 * 08/10/2012      mlm          136362 - Setup default DBPoolId for non-interactive UFE form requests from ODS.
 * 10/21/2012      mxg          148032 - Large Policy Improvement Project: Cache the OasisFields in PolicyHeader for Policy Header and Policy/Risk/Coverage/Coverage Class pages fields.
 * 12/11/2103      bill/fcb     148037 - Added loadPageBean.
 * 05/11/2016      wdang        176749 - Modified getConnection to call setWebContext.
 *                                       Set source context to WEB when initializing OasisUser.
 * 02/22/2017       tzeng       168385 - Modified getConnection() to set requested transaction time.
 * 05/17/2017       cesar       182477 - Modified mapToBeans() and recordToBeans() to encode masked fields.
 * 09/07/2017       cesar       187894 - Modified decodeMaskedField() to check for null pointer.
 * 09/11/2017       kshen       Grid replacement. Added method isPageUseJqxGrid to check if a page uses jqxGrid.
 *                              Added method isJqxGridEnabled to check if the jqxGrid function is enabled in project level.
 * 10/02/2017       cesar       188804 - Added isBase64() to check if the string has been decoded.
 *                                       Added decodeField() to decode the base64 string
 *                                       Added isObjectApplicableForBase64() to check if the object instance is applicable for base64.
 *                                       Modified encodeMaskedField and decodeMaskedField to use new methods added.
 * 10/06/2017       cesar       185295 - Modified getFormFile to wrapper for FormFile.
 * 10/24/2017       cesar       186335 - Modified isBase64(), decodeField() and encodeMaskedField() to incldue UTF-8 when encoding/decoding
 * 11/03/2017       cesar       186335 - Modified encodeMaskedField() to properly log as warning when unable to encode objValue
 * 11/09/2017       cesar       186335 - Modified isBase64() to verify if object has been encoded or not.
 * 11/29/2017       cesar       190017 - Modified encodeMaskedField() to only encode when the following condition is matched:
 *                                           - field is masked in both form field and grid header field "_GH"
 *                                           - field is masked in form field and there is no associated grid header field "_GH"
 *                                           - field is masked in grid header and there is no associated form field
 * 01/17/2018       casar       190904 - Modified encodeMaskedField() to check for syatem_parmeter to whether encode/decode masked fields.
 * 05/14/2018       casar       192983 - Added getMaskedPageViewCacheMap(), getXssOverridesPageViewCacheMap(), loadPageViewCachedMap()
 *                                       getCurrentPageViewCacheMap(), getRequestHelper() to store/retrieve from PageViewCacheMap
 * 11/12/2018       wreeder     196160 - Optimize iteration through Fields in a Record with getFields() / field.getStringValue() instead of getFieldNames() / record.hasFieldValue(fieldId) / record.getStringValue(fieldId)
 * ---------------------------------------------------------------------------------------------------------------------
 */

public class ActionHelper {
    protected static final String clsName = ActionHelper.class.getName();
    protected static final String SQL_CHECKPAGE = "SELECT 1 \n" +
            "FROM pf_web_page p, pf_web_application a\n" +
            "WHERE a.pf_web_application_pk = p.pf_web_application_fk\n " +
            "AND p.struts_action = ?\n" +
            "AND nvl(p.status,'A') = 'A' \n" +
            "AND (nvl(p.security_b,'N') = 'N' \n" +
            "OR nvl(a.security_b,'Y') = 'N' \n" +
            "OR (nvl(a.security_b,'Y') = 'Y' AND nvl(p.security_b,'N') = 'Y'\n" +
            "AND ( \n" +
            "    EXISTS\n" +
            "    (SELECT x.pf_web_page_fk \n" +
            "    from pfuser_prof up, pfprof_web_page_xref x \n" +
            "    where x.application = up.application \n" +
            "    and x.profile = up.profile \n" +
            "    AND x.pf_web_page_fk = p.pf_web_page_pk\n" +
            "    and upper(up.userid) = upper(?) \n" +
            "    and x.status = 'A' and up.status = 'A' and (x.access_authority = 'R' or access_authority = 'RW'))\n" +
            "    ) ))";

    protected static final String VALUE_FOR_YES = "Y";
    protected static final String VALUE_FOR_NO = "N";

    /**
     * Gets a Logger and logs the entry of a method
     *
     * @param cls    Class
     * @param method Method
     * @param parms  Array of parameters
     * @return Logger
     * @deprecated Replaced by {@link dti.oasis.util.LogUtils#enterLog(java.lang.Class, java.lang.String, java.lang.Object[])}
     */
    public static Logger enterLog(Class cls, String method, Object[] parms) {
        return LogUtils.enterLog(cls, method, parms);
    }

    /**
     * Gets a Logger and logs the entry of a method
     *
     * @param cls    Class
     * @param method Method
     * @param parm   Single parameter
     * @return Logger
     * @deprecated Replaced by {@link dti.oasis.util.LogUtils#enterLog(java.lang.Class, java.lang.String, java.lang.Object)}
     */
    public static Logger enterLog(Class cls, String method, Object parm) {
        return LogUtils.enterLog(cls, method, parm);
    }

    /**
     * Gets a Logger and logs the entry of a method
     *
     * @param cls    Class
     * @param method Method
     * @return Logger
     * @deprecated Replaced by {@link dti.oasis.util.LogUtils#enterLog(java.lang.Class, java.lang.String)}
     */
    public static Logger enterLog(Class cls, String method) {
        return LogUtils.enterLog(cls, method);
    }

    /**
     * Gets OasisUser object from HttpSession.
     *
     * @param request HttpServletRequest
     * @return OasisUser object.
     */
    public static OasisUser getCurrentUser(HttpServletRequest request) {
        l.entering(ActionHelper.class.getName(), "getCurrentUser");

        HttpSession session = request.getSession();
        // Look in session
        OasisUser user = (OasisUser) session.getAttribute(IOasisAction.KEY_OASISUSER);

        l.exiting(clsName, "getCurrentUser", user);
        return user;
    }

    /**
     * Gets JSESSIONID from session cookie.
     *
     * @param request HttpServletRequest
     * @return id String
     */
    public static String getJSessionId(HttpServletRequest request){
        String id = null;
        javax.servlet.http.Cookie [] cookies = request.getCookies();
        if(cookies != null){
            for(int x =0; x<cookies.length;x++){
                javax.servlet.http.Cookie cookie = cookies[x];
                String name = cookie.getName();
                if(name.equals("JSESSIONID"))
                    id = cookie.getValue();
            }
        }
        return id;
    }

    /**
     * Gets userid from session/context
     *
     * @param request
     * @return userid
     * @see dti.oasis.struts.IOasisAction
     */
    public static String getCurrentUserId(HttpServletRequest request) {
        l.entering(ActionHelper.class.getName(), "getCurrentUserId");
        String userid = getCurrentUser(request).getUserId();
        l.exiting(clsName, "getCurrentUserId", userid);
        return userid;
    }

    /**
     * Loads and secures a web page's navigation.  The navigation
     * (top nav, left nav & title) are stored in a PageBean.  This bean is stored in the
     * request using IOasisAction.KEY_PAGEBEAN
     *
     * @param request
     * @param className STRUTS Action Class Name
     * @return
     * @throws Exception
     * @throws IllegalArgumentException if illegal access is attempted*
     * @see dti.oasis.struts.IOasisAction
     * @see dti.oasis.util.PageBean
     */
    public static boolean secureNav(HttpServletRequest request, String className) throws Exception {
        Connection conn = null;
        if (l.isLoggable(Level.FINER)) {
            l.entering(ActionHelper.class.getName(), "secureNav", new Object[]{className});
        }
        boolean rc = false;
        try {
            conn = getConnection(request);

            // Get Fields
            String user = request.getRemoteUser();

            if (user == null)
                user = getCurrentUserId(request);
            if (user == null) {
                IllegalAccessException e = new IllegalAccessException("Unable to determine user.");
                l.throwing(clsName, "secureNav", e);
                throw e;
            }
            if (isUserValidForPage(conn, user, className)) {
                request.setAttribute(IOasisAction.KEY_PAGEBEAN,
                        NavigationManager.getInstance().getPageBean(conn, request, className, user, new DefaultPageDefLoadProcessor()));
                rc = true;
            }
            else {
                IllegalAccessException e = new IllegalAccessException(MessageManager.getInstance().formatMessage("core.denied.access.to.page", new String[]{user}));
                l.logp(Level.SEVERE, ActionHelper.class.getName(), "isUserValidForPage", "User " + user + " has been denied access to the page " +
                        "identified by STRUTS Action " + className + '.');
                l.throwing(clsName, "securePage", e);
                throw e;
            }

            l.exiting(clsName, "secureNav", String.valueOf(rc));
            return rc;
        }
        finally {
                if (conn != null) DatabaseUtils.close(conn);
        }

    }

    /**
     * Determines whether a user should have access to a page.
     *
     * @param request
     * @param className          Fully qualified name of Struts Action Class
     * @return true if page was secured, false if not
     * @throws Exception                General Purpose Exception
     * @throws IllegalArgumentException if illegal access is attempted
     * @see dti.oasis.struts.IOasisAction
     * @see dti.oasis.tags.OasisFields
     * @see dti.oasis.tags.OasisElements
     * @see dti.oasis.util.PageBean
     */
    public static boolean isUserValidForPage(HttpServletRequest request, String className)
            throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(ActionHelper.class.getName(), "isUserValidForPage", new Object[]{className});
        }
        Connection conn = null;
        boolean rc = true;
        try {
            conn = getConnection(request);

            // Get Fields
            String user = getCurrentUser(request).getUserId();

            if (!isUserValidForPage(conn, user, className)) {

                IllegalAccessException e = new IllegalAccessException(MessageManager.getInstance().formatMessage("core.denied.access.to.page", new String[]{user}));
                l.logp(Level.SEVERE, ActionHelper.class.getName(), "isUserValidForPage", "User " + user + " has been denied access to the page " +
                        "identified by STRUTS Action " + className + '.');

                l.throwing(clsName, "securePage", e);
                throw e;
            }

            l.exiting(clsName, "isUserValidForPage", String.valueOf(rc));
            return rc;
        }
        finally {
                if (conn != null) DatabaseUtils.close(conn);
        }
    }

    /**
     * Determines whether a user should have access to a page
     *
     * @param conn      JDBC Connection
     * @param userId    User Id
     * @param className STRUTS ACTION CLASS
     * @return true if ok, false if not
     * @throws SQLException
     */
    public static boolean isUserValidForPage(Connection conn, String userId, String className) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        if (l.isLoggable(Level.FINER)) {
            l.entering(ActionHelper.class.getName(), "isUserValidForPage", new Object[]{conn, userId, className});
        }
        boolean rc = true;
        try {
            stmt = conn.prepareStatement(SQL_CHECKPAGE);
            stmt.setString(1, className);
            stmt.setString(2, userId);
            l.fine(new StringBuffer("Executing: ").append(SQL_CHECKPAGE).append(" with ").
                    append(className).append(',').append(userId).toString());
            rs = stmt.executeQuery();
            rc = rs.next();
            l.exiting(clsName, "isUserValidForPage", String.valueOf(rc));
            return rc;
        }
        finally {
                if (rs != null) DatabaseUtils.close(rs);
                if (stmt != null) DatabaseUtils.close(stmt);
        }
    }

    /**
     * Secures a web page by instantiating OasisFields and OasisElements
     * objects for the Struts Action Class referenced by the className parm.
     * These objects are then stored in the request using IOasisAction.KEY_FIELDS
     * and IOasisAction.KEY_ELEMENTS, respectively. If a userid cannot be
     * determined, the page will not be secured and the OasisFields and OasisElements
     * objects will not be instantiated
     * Additionally, the web page's navigation is loaded and secured.  The navigation
     * (top nav, left nav & title) are stored in a PageBean.  This bean is stored in the
     * request using IOasisAction.KEY_PAGEBEAN
     * This loads a single layer's set of fields.  All layers are loaded, just not all
     * the fields on those layers.
     *
     * @param request
     * @param className          Fully qualified name of Struts Action Class
     * @param includeLayerFields Whether to load all fields in all layers
     * @return true if page was secured, false if not
     * @throws Exception                General Purpose Exception
     * @throws IllegalArgumentException if illegal access is attempted
     * @see dti.oasis.struts.IOasisAction
     * @see dti.oasis.tags.OasisFields
     * @see dti.oasis.tags.OasisElements
     * @see dti.oasis.util.PageBean
     */
    public static boolean securePage(HttpServletRequest request, String className, boolean includeLayerFields)
            throws Exception {
        return securePage(request, className, true, includeLayerFields, DefaultPageDefLoadProcessor.getInstance());
    }

    /**
     * Secures a web page by instantiating OasisFields and OasisElements
     * objects for the Struts Action Class referenced by the className parm.
     * These objects are then stored in the request using IOasisAction.KEY_FIELDS
     * and IOasisAction.KEY_ELEMENTS, respectively. If a userid cannot be
     * determined, the page will not be secured and the OasisFields and OasisElements
     * objects will not be instantiated
     * Additionally, the web page's navigation is loaded and secured.  The navigation
     * (top nav, left nav & title) are stored in a PageBean.  This bean is stored in the
     * request using IOasisAction.KEY_PAGEBEAN
     *
     * @param request
     * @param className Fully qualified name of Struts Action Class
     * @param pageDefLoadProcessor Instance of sub-system PageDefLoadProcessor to enforce sub-system level security
     * @return true if page was secured, false if not
     * @throws Exception                General Purpose Exception
     * @throws IllegalArgumentException if illegal access is attempted
     * @see dti.oasis.struts.IOasisAction
     * @see dti.oasis.tags.OasisFields
     * @see dti.oasis.tags.OasisElements
     * @see dti.oasis.util.PageBean
     */
    public static boolean securePage(HttpServletRequest request, String className, PageDefLoadProcessor pageDefLoadProcessor)
            throws Exception {
        return securePage(request, className, true, true, pageDefLoadProcessor);
    }

    /**
     * Secures a web page by instantiating OasisFields and OasisElements
     * objects for the Struts Action Class referenced by the className parm.
     * These objects are then stored in the request using IOasisAction.KEY_FIELDS
     * and IOasisAction.KEY_ELEMENTS, respectively. If a userid cannot be
     * determined, the page will not be secured and the OasisFields and OasisElements
     * objects will not be instantiated
     * Additionally, the web page's navigation is loaded and secured.  The navigation
     * (top nav, left nav & title) are stored in a PageBean.  This bean is stored in the
     * request using IOasisAction.KEY_PAGEBEAN
     *
     * @param request
     * @param className Fully qualified name of Struts Action Class
     * @return true if page was secured, false if not
     * @throws Exception                General Purpose Exception
     * @throws IllegalArgumentException if illegal access is attempted
     * @see dti.oasis.struts.IOasisAction
     * @see dti.oasis.tags.OasisFields
     * @see dti.oasis.tags.OasisElements
     * @see dti.oasis.util.PageBean
     */
    public static boolean securePage(HttpServletRequest request, String className)
            throws Exception {
        return securePage(request, className, true, true, DefaultPageDefLoadProcessor.getInstance());
    }

    /**
     * Secures a web page by instantiating OasisFields and OasisElements
     * objects for the Struts Action Class referenced by the className parm.
     * These objects are then stored in the request using IOasisAction.KEY_FIELDS
     * and IOasisAction.KEY_ELEMENTS, respectively. If a userid cannot be
     * determined, the page will not be secured and the OasisFields and OasisElements
     * objects will not be instantiated
     * Additionally, the web page's navigation is loaded and secured.  The navigation
     * (top nav, left nav & title) are stored in a PageBean.  This bean is stored in the
     * request using IOasisAction.KEY_PAGEBEAN
     * This loads a single layer's set of fields.  All layers are loaded, just not all
     * the fields on those layers.
     *
     * @param request
     * @param className          Fully qualified name of Struts Action Class
     * @param includeLayerFields Whether to load all fields in all layers
     * @param pageDefLoadProcessor Instance of sub-system PageDefLoadProcessor to enforce sub-system level security
     * @return true if page was secured, false if not
     * @throws Exception                General Purpose Exception
     * @throws IllegalArgumentException if illegal access is attempted
     * @see dti.oasis.struts.IOasisAction
     * @see dti.oasis.tags.OasisFields
     * @see dti.oasis.tags.OasisElements
     * @see dti.oasis.util.PageBean
     */
    public static boolean securePage(HttpServletRequest request, String className, boolean includeLayerFields, PageDefLoadProcessor pageDefLoadProcessor) throws Exception {
        return securePage(request, className, true, includeLayerFields, pageDefLoadProcessor);
    }

    /**
     * Secure that the user has access to the web page.
     * If the loadFields parameter is true, this method instantiates the OasisFields and OasisElements
     * objects for the Struts Action Class referenced by the className parm.
     * These objects are then stored in the request using IOasisAction.KEY_FIELDS
     * and IOasisAction.KEY_ELEMENTS, respectively. If a userid cannot be
     * determined, the page will not be secured and the OasisFields and OasisElements
     * objects will not be instantiated
     * Additionally, the web page's navigation is loaded and secured.  The navigation
     * (top nav, left nav & title) are stored in a PageBean.  This bean is stored in the
     * request using IOasisAction.KEY_PAGEBEAN
     * This loads a single layer's set of fields.  All layers are loaded, just not all
     * the fields on those layers.
     *
     * @param request
     * @param className          Fully qualified name of Struts Action Class
     * @param includeLayerFields Whether to load all fields in all layers
     * @param pageDefLoadProcessor Instance of sub-system PageDefLoadProcessor to enforce sub-system level security
     * @return true if page was secured, false if not
     * @throws Exception                General Purpose Exception
     * @throws IllegalArgumentException if illegal access is attempted
     * @see dti.oasis.struts.IOasisAction
     * @see dti.oasis.tags.OasisFields
     * @see dti.oasis.tags.OasisElements
     * @see dti.oasis.util.PageBean
     */
    public static boolean securePage(HttpServletRequest request, String className, boolean loadFields, boolean includeLayerFields, PageDefLoadProcessor pageDefLoadProcessor) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(ActionHelper.class.getName(), "securePage", new Object[]{className, loadFields, includeLayerFields});
        }
        Connection conn = null;
        boolean rc = false;
        try {
            // Setup the REQUEST_URI request attribute with the request uri of this invocation so that JSPs and Tags can use it.
            // Otherwise, when a JSP/Tag calls request.getRequestURI(), it returns the URI of the JSP page.
            request.setAttribute(RequestIds.REQUEST_URI, request.getRequestURI());

            // Get Fields
            String user = getCurrentUser(request).getUserId();

            conn = getConnection(request);

            if (isUserValidForPage(conn, user, className)) {
                if (loadFields) {
                    // Get OasisFields
                    OasisFields flds = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS_CACHED);
                    if(flds != null){
                        l.logp(Level.FINER, ActionHelper.class.getName(), "securePage","OasisFields are CACHED. OasisFields: Class Name:: "+  flds.getActionClassName());
                        request.removeAttribute(IOasisAction.KEY_FIELDS_CACHED);
                        request.setAttribute(IOasisAction.KEY_FIELDS, flds);
                    } else {
                        l.logp(Level.FINER, ActionHelper.class.getName(), "securePage"," OasisFields are NOT CACHED. Creating a new instance...");

                        flds = OasisFields.createInstance(className, user, conn, includeLayerFields, pageDefLoadProcessor);
                        l.logp(Level.FINER, ActionHelper.class.getName(), "securePage","ActionHelper:: ADD TO REQUEST: "+flds);
                        request.setAttribute(IOasisAction.KEY_FIELDS, flds);

                        if(flds==null){
                            l.logp(Level.FINE, ActionHelper.class.getName(), "securePage","OasisFields STILL NULL");
                        }
                    }
                    // record for non-grid fields
                    Record record = (Record) request.getAttribute(RequestIds.NON_GRID_FIELDS_RECORD);
                    if (record == null) {
                        record = new Record();
                    }
                    if (flds != null) {
                        String gridHeaderSuffix = GridHelper.getGridHeaderOasisFieldNameSuffix();
                        Iterator iter = flds.getFieldIds().iterator();
                        while (iter.hasNext()) {
                            String fieldId = (String) iter.next();
                            if (fieldId.indexOf(" on LayerId") > 0) {
                                fieldId = fieldId.substring(0, fieldId.indexOf(" on LayerId"));
                            }
                            if (!StringUtils.isBlank(gridHeaderSuffix) && fieldId.endsWith(gridHeaderSuffix)) {
                                //grid header fields
                                continue;
                            }
                            record.setFieldValue(fieldId, null);
                        }
                    }
                    request.setAttribute(RequestIds.NON_GRID_FIELDS_RECORD, record);
                    // Add the Fields to the Request Storage in case other classes need the info.
                    if (RequestStorageManager.getInstance().isSetupForRequest()) {
                        RequestStorageManager.getInstance().set(IOasisAction.KEY_FIELDS, flds);
                        RequestStorageManager.getInstance().set(RequestIds.NON_GRID_FIELDS_RECORD, record);
                    }
                    if (flds != null) {
                        // publish default value if field is not in request parameter
                        Map defaultValueMap = new HashMap();
                        Map requestParameterMap = request.getParameterMap();
                        for (OasisFormField field : flds.getAllFieldList()) {
                            if (!requestParameterMap.containsKey(field.getFieldId())&&!requestParameterMap.containsKey(field.getFieldId()+"_IS_CHECKBOX")
                                    && !StringUtils.isBlank(field.getDefaultValue())) {
                                defaultValueMap.put(field.getFieldId(), field.getDefaultValue());
                            }
                        }
                        ActionHelper.mapToBeans(request, defaultValueMap, flds);
                    }

                    // Get OasisElements
                    OasisElements els = OasisElements.createInstance(className, user, conn, pageDefLoadProcessor);
                    request.setAttribute(IOasisAction.KEY_ELEMENTS, els);
                    // Add the Elements to the Request Storage in case other classes need the info.
                    if (RequestStorageManager.getInstance().isSetupForRequest()) {
                        RequestStorageManager.getInstance().set(IOasisAction.KEY_ELEMENTS, els);
                    }
                   rc = true;
                }
                // Set the NavHelper as the Page Bean
                request.setAttribute(IOasisAction.KEY_PAGEBEAN,
                        NavigationManager.getInstance().getPageBean(conn, request, className, user, pageDefLoadProcessor));
            }
            else {
                IllegalAccessException e = new IllegalAccessException(MessageManager.getInstance().formatMessage("core.denied.access.to.page", new String[]{user}));
                l.logp(Level.SEVERE, ActionHelper.class.getName(), "isUserValidForPage", "User " + user + " has been denied access to the page " +
                        "identified by STRUTS Action " + className + '.');
                l.throwing(clsName, "securePage", e);
                throw e;
            }

            l.exiting(clsName, "securePage", String.valueOf(rc));
            return rc;
        }
        finally {
                if (conn != null) DatabaseUtils.close(conn);
        }
    }

    /**
     * The web page's navigation is loaded and secured.  The navigation
     * (top nav, left nav & title) are stored in a PageBean.  This bean is stored in the
     * request using IOasisAction.KEY_PAGEBEAN
     *
     * @param request
     * @param className Fully qualified name of Struts Action Class
     * @throws Exception                General Purpose Exception
     * @throws IllegalArgumentException if illegal access is attempted
     * @see dti.oasis.struts.IOasisAction
     * @see dti.oasis.util.PageBean
     */
    public static void loadPageBean(HttpServletRequest request, String className)
        throws Exception {

        Connection conn = null;
        try {
            // Get Fields
            String user = getCurrentUser(request).getUserId();

            conn = getConnection(request);

            // Set the NavHelper as the Page Bean
            request.setAttribute(IOasisAction.KEY_PAGEBEAN,
                NavigationManager.getInstance().getPageBean(conn, request, className, user, DefaultPageDefLoadProcessor.getInstance()));
        }
        finally {
            if (conn != null) DatabaseUtils.close(conn);
        }
    }

    /**
     * Initialize OasisUser and cache it to the session
     *
     * @param request
     * @return OasisUser
     * @throws Exception
     */
    public static OasisUser initializeOasisUser(HttpServletRequest request) throws Exception {
        l.entering(ActionHelper.class.getName(), "initializeOasisUser");

        // Get the user ID for the request
        String userId = request.getRemoteUser();

        OasisUser userBean = initializeOasisUser(request, userId);

        l.exiting(ActionHelper.class.getName(), "initializeOasisUser", userBean);

        return userBean;
    }

    /**
     * Initialize OasisUser and cache it to the session
     *
     * @param request
     * @param userId
     * @return OasisUser
     * @throws Exception
     */
    public static OasisUser initializeOasisUser(HttpServletRequest request, String userId) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(ActionHelper.class.getName(), "initializeOasisUser", new Object[]{userId});
        }

        HttpSession session = request.getSession();
        OasisUser userBean = null;
        // Get the cached OasisUser in session if available
        if (session.getAttribute(IOasisAction.KEY_OASISUSER) != null) {
            userBean = (OasisUser) session.getAttribute(IOasisAction.KEY_OASISUSER);
        }
        else {
            // Create a new bean for Oasis user and set it to HTTP session
            userBean = new OasisUser();
        }

        // Try to retrieve it from ActionHelper, if there's no userId in the request - for interactive UFE form request.
        if (userId == null) {
            if (request.getParameter(IOasisAction.KEY_UFE_USER_Id) == null &&
                    session.getAttribute(IOasisAction.KEY_OASISUSER) != null) {
                // If the "db" parameter is not passed, then the UFE request must be from within CS.
                // User must be logged on into the application and already initialized - Get the userId from ActionHelper.
                userId = ActionHelper.getCurrentUserId(request);
                l.logp(Level.FINEST, clsName, "initializeOasisUser", "User Id (from ActionHelper.getCurrentUserId call):" + userId);
            }
        }

        // ODS uses this parameter to pass the userId in batch mode through an unauthenticated request
        // Also, the direct requests from the RTF Editor ActiveX components passes "db" parameter with userId information.
        if (request.getParameter(IOasisAction.KEY_UFE_USER_Id) != null && userId == null) {
            String UFEDBPoolId = "";
            if (request.getParameter(IOasisAction.KEY_UFE_DBPOOL_ID) != null) {
                UFEDBPoolId = request.getParameter(IOasisAction.KEY_UFE_DBPOOL_ID);
            }
            // Try to retrieve the userId from request parameter if there's no userId in the request - for non interactive UFE form.
            userId = request.getParameter(IOasisAction.KEY_UFE_USER_Id);
            l.logp(Level.FINEST, clsName, "initializeOasisUser", "User Id (from request.getParameter() call):" + userId);

            //Determine whether the userId is passed as encrypted string.
            boolean isUfeConfiguredAsIntranetApp = true;
            boolean isUfeEncrypt = false;
            if (ApplicationContext.getInstance().hasProperty("ufe.intranet")) {
                isUfeConfiguredAsIntranetApp = YesNoFlag.getInstance(
                        ApplicationContext.getInstance().getProperty("ufe.intranet")).booleanValue();
            }
            if (!isUfeConfiguredAsIntranetApp) {
                isUfeEncrypt = true;
            } else {
                // For intranet configuration, check whether the system parameter UFE_ENCRYPT_B is setup to be encrypted.
                isUfeEncrypt = YesNoFlag.getInstance(
                        SysParmProvider.getInstance().get(UFEDBPoolId, "UFE_ENCRYPT_B", "N")).booleanValue();
            }
            if (isUfeEncrypt) {
                //The supplied user id is encrypted. Decrypt it to get the actual user id.
                userId = DatabaseUtils.getDecryptedString(UFEDBPoolId, CryptoToolkit.hexToString(userId));
                l.logp(Level.FINEST, clsName, "initializeOasisUser", "User Id (after decryption):" + userId);
            }

            String defaultDBPoolId = UFEDBPoolId;
            if (StringUtils.isBlank(defaultDBPoolId)) {
                // for backward compatibility.
                if (!DatabaseUtils.isRoleBasedDBPoolIdRequired()) {
                    defaultDBPoolId = DatabaseUtils.getDefaultDBPoolId();
                }
            }

            if (!StringUtils.isBlank(defaultDBPoolId)) {
                // store in session
                session.setAttribute(IOasisAction.KEY_DBPOOLID, defaultDBPoolId);
            } else {
                ConfigurationException ce = new ConfigurationException("DBPoolId is required, but not passed for non-interactive ufe form requests.");
                l.throwing(ActionHelper.class.getName(), "initializeOasisUser", ce);
                throw ce;
            }

            userBean = new OasisUser(userId, false);
            // store in session
            session.setAttribute(IOasisAction.KEY_OASISUSER, userBean);


        }

        // If no userId is found, check if anonymous access is supported
        if (userId == null) {
            l.logp(Level.FINEST, clsName, "initializeOasisUser", "About get userId from [anonUserId] property:" + userId);
            userId = ApplicationContext.getInstance().getProperty(IOasisAction.KEY_ANONUSERID, "");
            // create Anonymous User object
            userBean = new OasisUser(userId, true);
            // store in session
            session.setAttribute(IOasisAction.KEY_OASISUSER, userBean);
        }

        // Initialize OasisUser
        // Check if user matches userId in the session. If it does not match, clear out prior OasisUser and switch to new user.
        if (userBean.getUserId() != null && !(userBean.getUserId().equals(userId))) {
            session.removeAttribute(IOasisAction.KEY_OASISUSER);
            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, clsName, "initializeOasisUser", "Changing userId from <" + userBean.getUserId() + "> to <" + userId + ">");
            }
            userBean = new OasisUser();
        }

        // Initialize the OasisUser when the userId is null in the user bean
        if ((userBean.getUserId() == null)) {
            // Validate the User
            String dbPoolId = ActionHelper.getDbPoolId(request);
            l.logp(Level.FINEST, clsName, "initializeOasisUser", "About to validate userId [" + userId + "] against DB Pool Id [" + dbPoolId + "]");
            String msg = Authenticator.isUserValid(userId, dbPoolId);
            if (msg != null) {
                IllegalAccessException e = new IllegalAccessException(msg);
                l.throwing(clsName, "initializeOasisUser", e);
                throw e;
            }
            // Load and cache the Oasis User, and Update the last logged in date
            userBean = Authenticator.getUser(userId, dbPoolId);
            if (userBean != null) {
                session.setAttribute(IOasisAction.KEY_OASISUSER, userBean);
            }
        }

        // Initialize SourceContext to OASIS by default.
        if(userBean!=null)
            userBean.setSourceContext(OasisUser.SourceContextEnum.WEB);

        l.exiting(ActionHelper.class.getName(), "initializeOasisUser", userBean);

        return userBean;
    }

    /**
     * Returns the database pool id.  The db pool id is found by first looking
     * in the HttpSession using IOasisAction.KEY_DBPOOLID. If not found it is
     * looked for in the web context (web.xml) using IOasisAction.KEY_ENVDBPOOLID.
     * If found, the pool id is stored in the session.
     * Additionally, the availability
     * of the Oracle XDK is determined first by looking in the HttpSession using
     * IOasisAction.KEY_XDK. If it not found, it is looked for in the web context using
     * IOasisAction.KEY_ENVXDK. If found, the flag is stored in the session.
     *
     * @param request
     * @return DB PoolId
     * @throws AppException
     */
    public static String getDbPoolId(HttpServletRequest request) {
        l.entering(ActionHelper.class.getName(), "getDbPoolId");
        HttpSession session = request.getSession();

        String dbPoolId = null;
        String xdkInstalled = null;

        // First check for the Database Pool Id from HttpSession
        dbPoolId = (String) session.getAttribute(IOasisAction.KEY_DBPOOLID);
        if (StringUtils.isBlank(dbPoolId)) {
            String roleNameAndDBPoolId = getRoleNameAndDBPoolId(request);
            dbPoolId = roleNameAndDBPoolId.split(":")[1];

            // Store the dbPoolId in the session
            session.setAttribute(IOasisAction.KEY_DBPOOLID, dbPoolId);

            // Store the dbPoolIdRoleName
            String dbPoolIdRoleName = roleNameAndDBPoolId.split(":")[0];
            session.setAttribute(IOasisAction.KEY_DBPOOLIDROLENAME, dbPoolIdRoleName);
        }

        // check if Oracle XDK is installed for specified pool
        try {
            xdkInstalled = ApplicationContext.getInstance().getProperty(new StringBuffer(dbPoolId).append("_").
                    append(IOasisAction.KEY_ENVXDK).toString());
        }
        catch (AppException e) {
            xdkInstalled = "N";
        }
        session.setAttribute(IOasisAction.KEY_XDK, xdkInstalled);
        l.exiting(clsName, "getDbPoolId", dbPoolId);
        return dbPoolId;
    }

    /**
     * Determine the name of the role associated with the logged in user that has a dbPoolId configured for it
     * or an empty string if using the default dbPoolId.
     *
     * @param request the HttpServletRequest
     * @return the roleName associated with the logged in user that is configured with a dbPoolId
     * @throws ConfigurationException if there are dbPoolIds configured for more than one of the roles this user is associated with.
     *  It is also thrown if none of the associated roles are configured for a dbPoolId and role-based dbPoolId is required.
     */
    public static String getDBPoolIdRoleName(HttpServletRequest request) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(ActionHelper.class.getName(), "getDBPoolIdRoleName", new Object[]{request});
        }
        HttpSession session = request.getSession();

        String dbPoolIdRoleName = (String) session.getAttribute(IOasisAction.KEY_DBPOOLIDROLENAME);
        if (StringUtils.isBlank(dbPoolIdRoleName)) {
            String roleNameAndDBPoolId = getRoleNameAndDBPoolId(request);
            // Store the dbPoolIdRoleName
            dbPoolIdRoleName = roleNameAndDBPoolId.split(":")[0];
            session.setAttribute(IOasisAction.KEY_DBPOOLIDROLENAME, dbPoolIdRoleName);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(ActionHelper.class.getName(), "getDBPoolIdRoleName", dbPoolIdRoleName);
        }
        return dbPoolIdRoleName;
    }

    /**
     * Returns the roleName + ":" + dbPoolId configured for the given user.
     * If no dbPoolId is configured for any of the user's Roles,
     * and the role-based dbPoolId is not required, then ":" + defaultDBPoolId is returned.
     *
     * @param request the HttpServletRequest
     * @return a string containing the roleName + ":" + dbPoolId
     * @throws ConfigurationException if there are dbPoolIds configured for more than one of the roles this user is associated with.
     *  It is also thrown if none of the associated roles are configured for a dbPoolId and role-based dbPoolId is required.
     */
    private static String getRoleNameAndDBPoolId(HttpServletRequest request) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(ActionHelper.class.getName(), "getRoleNameAndDBPoolId", new Object[]{request});
        }

        if (c_roleNameDBPoolIdMap.size() == 0) {
            Iterator propNames = ApplicationContext.getInstance().getPropertyNames();
            while (propNames.hasNext()) {
                String name = (String) propNames.next();
                if (name.indexOf(".dbPoolId") > 0 && !"require.role.based.dbPoolId".equals(name)) {
                    String roleName = name.substring(0, name.indexOf(".dbPoolId"));
                    String dbPoolId = ApplicationContext.getInstance().getProperty(name);
                    l.logp(Level.FINE, ActionHelper.class.getName(), "getRoleNameAndDBPoolId", "Found the dbPoolId '" + dbPoolId + "' configured for roleName '" + roleName + "'");
                    c_roleNameDBPoolIdMap.put(roleName, dbPoolId); // roleName -> dbPoolId value
                }
            }
        }

        String userId = null;
        if (request.getUserPrincipal() != null) {
            userId = request.getUserPrincipal().getName();
        }
        else {
            userId = getCurrentUser(request).getUserId();
        }
        Iterator entries = c_roleNameDBPoolIdMap.entrySet().iterator();
        String dbPoolIdRoleName = "";
        String dbPoolId = null;
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            String roleName = (String) entry.getKey();
            if (request.isUserInRole(roleName)) {
                if (dbPoolId == null) {
                    dbPoolIdRoleName = roleName;
                    dbPoolId = (String) entry.getValue();
                    if (StringUtils.isBlank(dbPoolId)) {
                        ConfigurationException ce = new ConfigurationException("The dbPoolId configuration '" + roleName + ".dbPoolId' is illegally defined with an empty value.");
                        l.throwing(ActionHelper.class.getName(), "getRoleNameAndDBPoolId", ce);
                        throw ce;
                    }
                }
                else {
                    // Fail if the User belongs to > 1 Group that has a dbPoolId defined for it.
                    ConfigurationException ce = new ConfigurationException("The user '" + userId + "' is a associated with multiple Roles configured with a dbPoolId, including '" + dbPoolIdRoleName + "' and '" + roleName + "'.");
                    l.throwing(ActionHelper.class.getName(), "getRoleNameAndDBPoolId", ce);
                    throw ce;
                }
            }
        }

        // Get the DefaultDBPoolId if none of the associated roles are configured for a dbPoolId and role-based dbPoolId is not required
        if (StringUtils.isBlank(dbPoolId)) {
            if (DatabaseUtils.isRoleBasedDBPoolIdRequired()) {
                ConfigurationException ce = new ConfigurationException("The user '" + userId + "' is not associated with any Roles configured with a dbPoolId, and role-base dbPoolId configuration is required.");
                l.throwing(ActionHelper.class.getName(), "getRoleNameAndDBPoolId", ce);
                throw ce;
            }
            dbPoolId = DatabaseUtils.getDefaultDBPoolId();
        }
        else {
            l.logp(Level.FINE, ActionHelper.class.getName(), "getRoleNameAndDBPoolId", "Found the dbPoolId '" + dbPoolId + "' configured for Role '" + dbPoolIdRoleName + "' for user '" + userId + "'");
        }

        String roleNameAndDBPoolId = dbPoolIdRoleName + ":" + dbPoolId;
        if (l.isLoggable(Level.FINER)) {
            l.exiting(ActionHelper.class.getName(), "getRoleNameAndDBPoolId", roleNameAndDBPoolId);
        }
        return roleNameAndDBPoolId;
    }
    private static Map c_roleNameDBPoolIdMap = new Hashtable();

    /**
     * Returns a pooled JDBC connection.  It calls DatabaseUtils.setWebContext
     * before returning the connection.  You should generally call ActionHelper.securePage
     * prior to calling this method if you want your protected fields to be set.
     *
     * @param request
     * @return JDBC Connection
     * @throws Exception
     * @see dti.oasis.util.DatabaseUtils#setWebContext(Connection, String, String, String, String, String)
     */
    public static Connection getConnection(HttpServletRequest request)
            throws Exception {
        l.entering(ActionHelper.class.getName(), "getConnection");
        Connection conn = DBPool.getConnection(getDbPoolId(request));
        // Find OasisFields collection.
        OasisFields fields = getFields(request);
        // If no fields collection found, then there are no protected fields.
        String protFields = (fields != null) ? fields.getProtectedFields() : null;
        // Get current user from request.
        OasisUser oasisUser = getCurrentUser(request);
        // Set up the web context information in db
        DatabaseUtils.setWebContext(conn,
            oasisUser.getUserId(),
            request.getSession().getId(),
            protFields,
            oasisUser.getSourceContext().name(),
            oasisUser.getRequestedTransactionTime());
        l.exiting(clsName, "getConnection", conn);
        return conn;
    }

    /**
     * Returns true if value is fundamentally blank
     *
     * @param val value to check
     * @return true if blank
     * @deprecated Replaced by {@link dti.oasis.util.StringUtils#isBlank(java.lang.String)}
     */
    public static boolean isBlank(String val) {
        return StringUtils.isBlank(val);

    }

    /**
     * Returns true if value is fundamentally blank
     *
     * @param val      value to check
     * @param dropDown used by dropdown? if so, then "-1" is considered a blank as well
     * @return true if blank
     * @deprecated Replaced by {@link dti.oasis.util.StringUtils#isBlank(java.lang.String, boolean)}
     */
    public static boolean isBlank(String val, boolean dropDown) {
        return StringUtils.isBlank(val, dropDown);
    }

    /**
     * For each attribute found in the DynaActionForm, this method creates
     * a single attribute DynaBean when a related OasisFormField is
     * found in the OasisFormFields collection.
     * The fieldId is used for attribute name.  The bean is stored in the
     * request using the fieldid as the key.
     *
     * @param request
     * @param form    STRUTS ActionForm containing
     * @param fields
     * @throws JspException
     */
    public static void formToBeans(HttpServletRequest request, DynaActionForm form,
                                   OasisFields fields) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(ActionHelper.class.getName(), "formToBeans", new Object[]{form, fields});
        }
        mapToBeans(request, form.getMap(), fields);
        l.exiting(clsName, "formToBeans");
    }

    /**
     * For each attribute found in the Map, this method creates
     * a single attribute DynaBean when a related OasisFormField is
     * found in the OasisFormFields collection.
     * The fieldId is used for attribute name.  The bean is stored in the
     * request using the fieldid as the key.
     *
     * @param request
     * @param map     Map containing name/value pairs
     * @param fields
     * @throws JspException
     */
    public static void mapToBeans(HttpServletRequest request, Map map,
                                  OasisFields fields) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(ActionHelper.class.getName(), "mapToBeans", new Object[]{map, fields});
        }

        Iterator it = map.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            OasisFormField fld = (OasisFormField) fields.get(key);

            if (fld != null) {
                Object oValue = encodeMaskedField(key, map.get(key),  fields);
                request.setAttribute(key, BeanDtiUtils.createValueBean(fld,oValue));
            }
        }
        l.exiting(clsName, "mapToBeans");
    }

    /**
     * For each field found in the Record, this method creates
     * a single attribute DynaBean when a related OasisFormField is
     * found in the OasisFormFields collection.
     * The fieldId is used for attribute name.  The bean is stored in the
     * request using the fieldid as the key.
     *
     * @param request
     * @param record
     * @param fields
     * @throws JspException
     */
    public static void recordToBeans(HttpServletRequest request, Record record,
                                     OasisFields fields) throws Exception {
        recordToBeans(request, record, fields, false);
    }

    /**
     * For each field found in the Record, this method creates
     * a single attribute DynaBean when a related OasisFormField is
     * found in the OasisFormFields collection.
     * The fieldId is used for attribute name.  The bean is stored in the
     * request using the fieldid as the key.
     *
     * @param request
     * @param record
     * @param oasisFields
     * @param mapAllFields map all fields, regardless of if there is a field defined for it.
     * @throws JspException
     */
    public static void recordToBeans(HttpServletRequest request, Record record,
                                     OasisFields oasisFields, boolean mapAllFields) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(ActionHelper.class.getName(), "recordToBeans", new Object[]{record, oasisFields});
        }

        String pageURI = (String) request.getAttribute(RequestIds.REQUEST_URI);
        PageEntitlementGroup pageEntitlementGroup = PageEntitlementManager.getInstance().getPageEntitlementGroup(pageURI);
        Iterator it = record.getFields();
        while (it.hasNext()) {
            Field recField = (Field) it.next();
            String key = recField.getName();
            if (mapAllFields && dti.oasis.http.RequestIds.CACHE_ID_FOR_PAGE_VIEW.equals(key)) {
                continue;
            }
            boolean hasOasisField = oasisFields.get(key) != null;
            if (mapAllFields || hasOasisField ) {
                Object fieldValue = null;
                String dispType   = null;
                if (hasOasisField) {
                    fieldValue = recField.getValue();
                    OasisFormField field = oasisFields.getField(key);
                    dispType = field.getDisplayType();
                }
                // If we have a multi-select, and the value is a string,
                //     split it up into a String[]...
                //     This will allow for the proper selection in the multi-select list
                if (("MULTISELECT".equalsIgnoreCase(dispType) ||
                     "MULTISELECTPOPUP".equalsIgnoreCase(dispType))
                     && (fieldValue instanceof String &&
                         fieldValue != null)) {
                    request.setAttribute(key, BeanDtiUtils.createValueBean(key, ((String)recField.getValue()).split(",")));
                }

                else {
                    Object oValue = encodeMaskedField(key, recField.getValue(),  oasisFields);
                    request.setAttribute(key, BeanDtiUtils.createValueBean(key, oValue));
                    record.setFieldValue(key, oValue);
                }
            }
            else {
                if(pageEntitlementGroup.isIndicatorFieldConfigured(key)||"readOnly".equalsIgnoreCase(key)||"handleLayer".equalsIgnoreCase(key)) {
                    request.setAttribute(key, recField.getValue());
                }
            }
        }

        l.exiting(clsName, "recordToBeans");
    }

    /**
     * add records for grid fields
     * @param request
     * @param recordSet
     */
    public static void addGridFieldRecords(HttpServletRequest request, String gridId, RecordSet recordSet) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(ActionHelper.class.getName(), "addGridFieldRecords", new Object[]{request, gridId, recordSet});
        }
        HashMap recordSetMap = (HashMap) request.getAttribute(RequestIds.RECORDSET_MAP);
        if (recordSetMap == null) {
            recordSetMap = new HashMap();
            request.setAttribute(RequestIds.RECORDSET_MAP, recordSetMap);
        }
        if (StringUtils.isBlank(gridId)) {
            gridId =  RequestIds.DATA_BEAN;
        }
        recordSetMap.put(gridId, recordSet);
        l.exiting(ActionHelper.class.getName(), "addGridFieldRecords");
    }


    /**
     * Checks for a value in a collection of LabelValueBean objects
     *
     * @param list  collection of LabelValueBean objects
     * @param value the value to look for
     * @return true/false
     * @see org.apache.struts.util.LabelValueBean
     * @deprecated Replaced by {@link dti.oasis.util.CollectionUtils#isValueInListOfValues(java.util.Collection, java.lang.String)}
     */
    public static boolean isValueInListOfValues(Collection list, String value) {
        return CollectionUtils.isValueInListOfValues(list, value);
    }

    /**
     * Write out Excel header for ResultSetMetaData
     * @param wri Output
     * @param rsmd ResultSet MetaData
     * @throws SQLException
     */
    private static void writeExcelHeader(PrintWriter wri, ResultSetMetaData rsmd) throws SQLException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(ActionHelper.class.getName(), "writeExcelHeader", new Object[]{wri,rsmd});
        }
        int count = rsmd.getColumnCount();
        wri.println("<TR>");
        for (int i = 1; i <= count; i++) {
            wri.print("<TH>");
            wri.print(rsmd.getColumnLabel(i));
            wri.println("</TH>");
        }
        wri.println("</TR>");
        l.exiting(clsName, "writeExcelHeader");
    }

    /**
     * Write out an excel header given the metadata in a DisconnectedResultSet.
     * @param wri Output
     * @param rs DisconnectedResultSet
     */
    private static void writeExcelHeader(PrintWriter wri, DisconnectedResultSet rs) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(ActionHelper.class.getName(), "writeExcelHeader", new Object[]{wri, rs});
        }
        int count = rs.getColumnCount();
        wri.println("<TR>");
        for (int i = 1; i <= count; i++) {
            wri.print("<TH>");
            wri.print(rs.getColumn(i).getColumnName());
            wri.println("</TH>");
        }
        wri.println("</TR>");
        l.exiting(clsName, "writeExcelHeader");
    }

    /**
     * Converts a DisconnectedResultSet and XMLGridHeader into an HTML table
     * with a content-type of excel.
     *
     * @param response
     * @param rs
     * @param header
     * @return true if excel sheet was created, false if not (empty resultset)
     * @throws IOException
     */
    public static boolean toExcel(HttpServletResponse response, DisconnectedResultSet rs, XMLGridHeader header) throws IOException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(ActionHelper.class.getName(), "toExcel", new Object[]{rs, header});
        }
        if (rs == null || rs.getRowCount() == 0) {
            l.exiting(clsName, "getConnection", String.valueOf(false));
            return false;
        }
        response.setContentType("application/vnd.ms-excel");
        //response.setHeader("Cache-control", "no-cache");
        //response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        response.setHeader("content-disposition", "inline; filename=oasis.xls");
        PrintWriter wri = null;
        try {
            wri = response.getWriter();
            wri.println("<HTML><BODY><TABLE BORDER='1'>");
            int count = header.size();
            wri.println("<TR>");
            for (int i = 1; i <= count; i++) {
                wri.print("<TH>");
                wri.print(header.getHeaderMap(i).get(XMLGridHeader.CN_NAME));
                wri.println("</TH>");
            }
            wri.println("</TR>");
            rs.beforeFirst();
            while (rs.next()) {
                wri.println("<TR>");
                for (int i = 1; i <= count; i++) {
                    wri.print("<TD>");
                    wri.print(rs.getString(i));
                    wri.println("</TD>");
                }
                wri.println("</TD>");
            }
            wri.println("</TABLE></BODY></HTML>");
            l.exiting(clsName, "toExcel", String.valueOf(true));
            return true;
        }
        finally {
            if (wri != null) {
                wri.flush();
                wri.close();
            }
        }
    }

    /**
     * Return the real path but include \\
     *
     * @param servletContext
     * @return the real servlet path
     */
    public static String getRealPath(ServletContext servletContext) {
        l.entering(ActionHelper.class.getName(), "getRealPath");
        String realPath = servletContext.getRealPath("/");
        if (realPath == null) {
            throw new ConfigurationException("The ServletContext.getRealPath returns a null. This application is propably deployed as a WAR or EAR.");
        }
        if (!realPath.substring(realPath.length() - 1, realPath.length() - 1).equals("\\"))
            realPath += "\\";
        l.exiting(clsName, "getRealPath", realPath);
        return realPath;
    }

    /**
     * Get the resource as an InputStream.
     * First look for it within the the Servlet Context's web root directory.
     * Next, look for it within the Application Server's root directory.
     * Lastly, look for it within the Web Application's Classpath.
     */
    public static InputStream getResourceAsInputStream(ServletContext servletContext, String resourceName) throws FileNotFoundException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(ActionHelper.class.getName(), "getResourceAsInputStream", new Object[]{resourceName});
        }

        InputStream resource = null;
        String realPath = null;
        String resourcPathname = null;
        try {
            // Try to open the resource from the ServletContext
            realPath = getRealPath(servletContext);
            resourcPathname = new StringBuffer(realPath).append("/").append(resourceName).toString();
            resource = new FileInputStream(resourcPathname);
        }
        catch (ConfigurationException e) {
            l.fine(e.getDebugMessage());
        }
        catch (FileNotFoundException e) {
            l.fine("Failed to find the resource '" + resourcPathname + "' using the ServletContext path.");
        }

        if (resource == null) {
            // Lastly, try the class path
            try {
                resource = getResourceAsInputStream(resourceName);
            } catch (FileNotFoundException e) {
                throw new FileNotFoundException("The ActionHelper failed to locate the resource '" + resourceName + " in the ServletContext path, the server's home directory, and the classpath.");
            }
        }

        return resource;
    }

    /**
     * Get the resource as an InputStream.
     * First, look for it within the Application Server's root directory.
     * Finally, look for it within the Web Application's Classpath.
     */
    public static InputStream getResourceAsInputStream(String resourceName) throws FileNotFoundException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(ActionHelper.class.getName(), "getResourceAsInputStream", new Object[]{resourceName});
        }

        InputStream resource = null;
        if (resource == null) {
            // Check in the Server's home directory
            try {
                l.fine("Looking for the resource '" + resource + "' in the server's home directory.");
                resource = new FileInputStream(resourceName);
            } catch (FileNotFoundException e) {
                l.fine("Failed to find the resource '" + resourceName + "' in the server's home directory.");
            }
        }

        if (resource == null) {
            // Check in the Web Application's Classpath
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader == null) {
                l.logp(Level.WARNING, ActionHelper.class.getName(), "getResourceAsInputStream", "The Thread's ContextClassLoader is not available... getting the ClassLoader for Struts");
                classLoader = RequestUtils.class.getClassLoader();
            }
            resource = classLoader.getResourceAsStream(resourceName);
        }

        if (resource == null) {
            throw new FileNotFoundException("Failed to locate the resource '" + resourceName + " in the classpath.");
        }

        l.logp(Level.FINE, ActionHelper.class.getName(), "getResourceAsInputStream", "Loaded the resource '" + resourceName + "' from the Web Application's class loader.");

        if (l.isLoggable(Level.FINER)) {
            l.exiting(ActionHelper.class.getName(), "getResourceAsInputStream", resource);
        }
        return resource;
    }

    /**
     * Write ResultSet out as an HTML table with a content type of Excel
     *
     * @param response
     * @param rs         DisconnectedResultSet
     * @param showHeader true if column header labels from metadata should be displayed in 1st row
     * @return true if processed, false if not (e.g. empty resultset)
     * @throws IOException
     */
    public static boolean toExcel(HttpServletResponse response, DisconnectedResultSet rs, boolean showHeader)
            throws IOException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(ActionHelper.class.getName(), "toExcel", new Object[]{rs, showHeader});
        }
        if (rs == null || !rs.next()) {
            l.exiting(clsName, "toExcel", String.valueOf(false));
            return false;
        }
        response.setContentType("application/vnd.ms-excel");
        //response.setHeader("Cache-control", "no-cache");
        //response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        response.setHeader("content-disposition", "inline; filename=oasis.xls");
        PrintWriter wri = null;
        try {
            wri = response.getWriter();
            wri.println("<HTML><BODY><TABLE BORDER='1'>");
            int count = rs.getColumnCount();
            if (showHeader)
                writeExcelHeader(wri, rs);
            do {
                wri.println("<TR>");
                for (int i = 1; i <= count; i++) {
                    wri.print("<TD>");
                    wri.print(rs.getString(i));
                    wri.println("</TD>");
                }
                wri.println("</TD>");
            } while (rs.next());
            wri.println("</TABLE></BODY></HTML>");
            l.exiting(clsName, "toExcel", String.valueOf(true));
            return true;
        }
        finally {
            if (wri != null) {
                wri.flush();
                wri.close();
            }
        }
    }

    /**
     * Write ResultSet out as an HTML table with a content type of Excel
     *
     * @param response
     * @param rs         Open ResultSet
     * @param showHeader true if column header labels from metadata should be displayed in 1st row
     * @return true if processed, false if not (e.g. empty resultset)
     * @throws IOException
     * @throws SQLException
     */
    public static boolean toExcel(HttpServletResponse response, ResultSet rs, boolean showHeader)
            throws IOException, SQLException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(ActionHelper.class.getName(), "toExcel", new Object[]{rs, showHeader});
        }
        if (rs == null || !rs.next()) {
            l.exiting(clsName, "toExcel", String.valueOf(false));
            return false;
        }
        response.setContentType("application/vnd.ms-excel");
        //response.setHeader("Cache-control", "no-cache");
        //response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        response.setHeader("content-disposition", "inline;filename=oasis.xls");
        PrintWriter wri = null;
        try {
            wri = response.getWriter();
            wri.println("<HTML><BODY><TABLE BORDER='1'>");
            ResultSetMetaData rsmd = rs.getMetaData();
            int count = rsmd.getColumnCount();
            if (showHeader)
                writeExcelHeader(wri, rsmd);
            do {
                wri.println("<TR>");
                for (int i = 1; i <= count; i++) {
                    wri.print("<TD>");
                    wri.print(rs.getString(i));
                    wri.println("</TD>");
                }
                wri.println("</TD>");
            } while (rs.next());
            wri.println("</TABLE></BODY></HTML>");
            l.exiting(clsName, "toExcel", String.valueOf(true));
            return true;
        }
        finally {
            if (wri != null) {
                wri.flush();
                wri.close();
            }
        }
    }

    /**
     * Convert a DynaActionForm to an xml representation
     *
     * @param form DynaActionForm
     * @return String xml
     * @deprecated Replaced by {@link dti.oasis.util.XMLUtils#formToXML(org.apache.struts.action.DynaActionForm)}
     */
    public static String formToXML(DynaActionForm form) {
        return XMLUtils.formToXML(form);
    }

    /**
     * Convert a Map to an xml representation
     *
     * @param map
     * @return String xml
     * @deprecated Replaced by {@link dti.oasis.util.XMLUtils#mapToXML(java.util.Map)}
     */
    public static String mapToXML(Map map) {
        return XMLUtils.mapToXML(map);
    }

    /**
     * @param actionName
     * @param pageContext
     * @return
     * @throws JspException
     */
    public static String getFormFromAction(String actionName, PageContext pageContext) throws JspException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(ActionHelper.class.getName(), "getFormFromAction", new Object[]{actionName});
        }
        // Look up the module configuration information we need
        ModuleConfig moduleConfig = TagUtils.getInstance().getModuleConfig(pageContext);

        if (moduleConfig == null)
            throw new JspException("Cannot find ActionMappings or ActionFormBeans collection");

        // Look up the action mapping we will be submitting to
        ActionMapping mapping = (ActionMapping) moduleConfig.findActionConfig(TagUtils.getInstance().getActionMappingName(actionName));
        if (mapping == null)
            throw new JspException("Cannot retrieve mapping for action " + actionName);
        String formName = mapping.getAttribute();
        if (formName == null)
            throw new JspException("Cannot retrieve FormBean attribute for action " + actionName);
        l.exiting(clsName, "getFormFromAction", formName);
        return formName;
    }

    /**
     * Returns a String value from a DynaActionForm
     *
     * @param form ActionForm that must be castable to a DynaActionForm
     * @param key  name of attribute in form
     * @return value of attribute or NULL if value is not a String or
     *         if form is not a DynaActionForm
     */
    public static String getFormString(ActionForm form, String key) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(ActionHelper.class.getName(), "getFormString", new Object[]{form, key});
        }
        String rc = null;
        if (form instanceof DynaActionForm) {
            Object o = ((DynaActionForm) form).get(key);

            if (o instanceof String)
                rc = (String) o;
        }
        l.exiting(clsName, "getFormString", rc);
        return rc;
    }

    public static String[] getFormStringArray(ActionForm form, String key) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(ActionHelper.class.getName(), "getFormStringArray", new Object[]{form, key});
        }
        String[] rc = null;
        if (form instanceof DynaActionForm) {
            Object o = ((DynaActionForm) form).get(key);

            if (o instanceof String[])
                rc = (String[]) o;
            if (o instanceof String)
               rc = new String[]{(String) o};
        }
        l.exiting(clsName, "getFormStringArray", rc);
        return rc;
    }

    /**
     * Returns a apache FormFile from a DynaActionForm
     *
     * @param form ActionForm that must be castable to a DynaActionForm
     * @param key  name of attribute in form
     * @return value of attribute or NULL if value is not a apache FormFile or
     *         if form is not a DynaActionForm
     */
    public static FormFile getFormFile(ActionForm form, String key) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(ActionHelper.class.getName(), "getFormFile", new Object[]{form, key});
        }
        FormFile rc = null;
        FormFileWrapper formFileWrapper = new FormFileWrapper();

        if (form instanceof DynaActionForm) {
            Object o = ((DynaActionForm) form).get(key);

            if (o instanceof FormFileWrapper) {
                formFileWrapper = (FormFileWrapper)o;
            } else {
                rc = (FormFile)o;
                formFileWrapper.setFormFile(rc);

            }
        }
        l.exiting(clsName, "getFormFile", rc);
        return formFileWrapper;
    }

    /**
     * Gets all the LOVS out of a request and generates XML
     *
     * @param request
     * @return String
     */
    public static String lovToXML(HttpServletRequest request) {
        l.entering(ActionHelper.class.getName(), "lovToXML");
        OasisFields fields = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);
        StringBuffer buff = new StringBuffer("<lovs>");
        ArrayList pageFields = fields.getPageFields();
        int sz = pageFields.size();
        for (int i = 0; i < sz; i++) {
            String id = ((OasisFormField) pageFields.get(i)).getFieldId();
            ArrayList lov = (ArrayList) request.getAttribute(id + "LOV");
            if (lov != null)
                buff.append(XMLUtils.lovToXML(id, lov));

        }
        ArrayList layerIds = fields.getLayerIds();
        sz = layerIds.size();
        for (int i = 0; i < sz; i++) {
            ArrayList layerFields = (ArrayList) fields.getLayerFields((String) layerIds.get(i));
            int sz1 = layerFields.size();
            for (int j = 0; j < sz1; j++) {
                String id = ((OasisFormField) layerFields.get(j)).getFieldId();
                ArrayList lov = (ArrayList) request.getAttribute(id + "LOV");
                if (lov != null)
                    buff.append(XMLUtils.lovToXML(id, lov));
            }
        }
        buff.append("</lovs>");
        l.exiting(clsName, "lovToXML", buff.toString());
        return buff.toString();
    }

    /**
     * Sends the lov values as xml back to browser
     *
     * @param request
     * @param response
     * @throws IOException
     */
    public static void sendLovs(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        l.entering(ActionHelper.class.getName(), "sendLovs");
        response.setContentType("text/xml;charset=utf-8");
//        response.setContentType("text/html");

        PrintWriter wri = response.getWriter();
        wri.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        wri.write(lovToXML(request));
        wri.flush();
        l.exiting(clsName, "sendLovs");
    }

    /**
     * Convenience method to return OasisFields collection from request.
     *
     * @param request
     * @return An OasisFields object.  It will return null if none is present.
     * @throws ClassCastException This will be thrown if some object other than
     *                            an OasisFields object has been stored in the request using the key
     *                            IOasisAction.KEY_FIELDS
     * @see dti.oasis.struts.IOasisAction#KEY_FIELDS
     */
    public static OasisFields getFields(HttpServletRequest request) {
        return (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);
    }

    /**
     * Convenience method to return OasisElements collection from request.
     *
     * @param request
     * @return An OasisElements object.  It will return null if none is present.
     * @throws ClassCastException This will be thrown if some object other than
     *                            an OasisElements object has been stored in the request using the key
     *                            IOasisAction.KEY_ELEMENTS
     * @see dti.oasis.struts.IOasisAction#KEY_ELEMENTS
     */
    public static OasisElements getElements(HttpServletRequest request) {
        return (OasisElements) request.getAttribute(IOasisAction.KEY_ELEMENTS);
    }

    /**
     * Validates request parameters values against the current OasisFields collection.
     * It iterates through the parameters in a request and tries to find a matching
     * OasisFormField in the OasisFields collection where the parameter name equals
     * the OasisFormField.fieldId.  For each field it finds, it validates as as follows:<br>
     * 1. Required fields checking:<br>
     * If the field is visible, required, and NOT read-only, the parameter value must
     * be non-blank and not null.<br>
     * 2. Type checking:<br>
     * If the field is numeric or currency based, the parameter value must either be
     * blank, null, or numeric.<br>
     * If the field is date based, the parameter value must either be blank, null, or
     * a date.<br>
     * <i>Note that type checking is performed regardless of whether the field is visible,
     * required or read-only.</i>
     *
     * @param request Current HttpServletRequest containing parameter values
     * @param fields  OasisFields collection
     * @return Returns a validation message formatted very simply for HTML.  The message
     *         consists of individual validation messages for each problem parameter, separated
     *         by a HTML linebreak tag &lt;BR&gt;.  If no validation errors are found or if the OasisFields
     *         collection is null, this returns null.
     */
    public static String getValidationMessages(HttpServletRequest request, OasisFields fields) {
        return getValidationMessages(request, fields, "<BR>");
    }

    /**
     * Validates request parameters values against the current OasisFields collection in
     * the request (keyed by IOasisAction.KEY_FIELDS).
     * It iterates through the parameters in a request and tries to find a matching
     * OasisFormField in the OasisFields collection where the parameter name equals
     * the OasisFormField.fieldId.  For each field it finds, it validates as as follows:<br>
     * 1. Required fields checking:<br>
     * If the field is visible, required, and NOT read-only, the parameter value must
     * be non-blank and not null.<br>
     * 2. Type checking:<br>
     * If the field is numeric or currency based, the parameter value must either be
     * blank, null, or numeric.<br>
     * If the field is date based, the parameter value must either be blank, null, or
     * a date.<br>
     * <i>Note that type checking is performed regardless of whether the field is visible,
     * required or read-only.</i>
     *
     * @param request Current HttpServletRequest containing parameter values
     * @return Returns a validation message formatted very simply for HTML.  The message
     *         consists of individual validation messages for each problem parameter, separated
     *         by a HTML linebreak tag &lt;BR&gt;.  If no validation errors are found or if the OasisFields
     *         collection is null, this returns null.
     * @see dti.oasis.struts.IOasisAction#KEY_FIELDS
     */
    public static String getValidationMessages(HttpServletRequest request) {
        return getValidationMessages(request, getFields(request), "<BR>");
    }

    /**
     * Validates request parameters values against the current OasisFields collection in
     * the request (keyed by IOasisAction.KEY_FIELDS).
     * It iterates through the parameters in a request and tries to find a matching
     * OasisFormField in the OasisFields collection where the parameter name equals
     * the OasisFormField.fieldId.  For each field it finds, it validates as as follows:<br>
     * 1. Required fields checking:<br>
     * If the field is visible, required, and NOT read-only, the parameter value must
     * be non-blank and not null.<br>
     * 2. Type checking:<br>
     * If the field is numeric or currency based, the parameter value must either be
     * blank, null, or numeric.<br>
     * If the field is date based, the parameter value must either be blank, null, or
     * a date.<br>
     * <i>Note that type checking is performed regardless of whether the field is visible,
     * required or read-only.</i>
     *
     * @param request      Current HttpServletRequest containing parameter values
     * @param msgdelimiter Delimiter to be placed between individual parameter messages.
     * @return Returns a validation message formatted very simply for HTML.  The message
     *         consists of individual validation messages for each problem parameter, separated
     *         by the passed delimiter.  If no validation errors are found or if the OasisFields
     *         collection is null, this returns null.
     * @see dti.oasis.struts.IOasisAction#KEY_FIELDS
     */
    public static String getValidationMessages(HttpServletRequest request, String msgdelimiter) {
        return getValidationMessages(request, getFields(request), msgdelimiter);
    }

    /**
     * Validates request parameters values against the OasisFields collection parameter.
     * It iterates through the parameters in a request and tries to find a matching
     * OasisFormField in the OasisFields collection where the parameter name equals
     * the OasisFormField.fieldId.  For each field it finds, it validates as as follows:<br>
     * 1. Required fields checking:<br>
     * If the field is visible, required, and NOT read-only, the parameter value must
     * be non-blank and not null.<br>
     * 2. Type checking:<br>
     * If the field is numeric or currency based, the parameter value must either be
     * blank, null, or numeric.<br>
     * If the field is date based, the parameter value must either be blank, null, or
     * a date.<br>
     * <i>Note that type checking is performed regardless of whether the field is visible,
     * required or read-only.</i>
     *
     * @param request      Current HttpServletRequest containing parameter values
     * @param fields       OasisFields collection
     * @param msgdelimiter Delimiter to be placed between individual parameter messages.
     * @return Returns a validation message formatted very simply for HTML.  The message
     *         consists of individual validation messages for each problem parameter, separated
     *         by the passed delimiter. If no validation errors are found or if the OasisFields
     *         collection is null, this returns null.
     */
    public static String getValidationMessages(HttpServletRequest request, OasisFields fields,
                                               String msgdelimiter) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(ActionHelper.class.getName(), "getValidationMessages", new Object[]{fields, msgdelimiter});
        }

        // no fields, no errors
        if (fields == null) {
            l.exiting(clsName, "getValidationMessages", null);
            return null;
        }
        // get parms
        Enumeration parms = request.getParameterNames();
        StringBuffer msg = new StringBuffer();
        // iterate through parms
        while (parms.hasMoreElements()) {
            // get parm name & value
            String parmName = (String) parms.nextElement();
            String value = request.getParameter(parmName);
            // Find field whose fieldId matches the parm name.
            Object o = fields.get(parmName);
            l.fine(new StringBuffer("Validating parm name:").append(parmName).
                    append(", parm value:").append(value).append(", OasisFormField:").
                    append(o).toString());
            // if no object was found or it is not an OasisFormField, continue
            if (o == null || !(o instanceof OasisFormField))
                continue;
            // this is a field
            OasisFormField field = (OasisFormField) o;
            l.fine(field.dumpFields());
            // field required, visible and editable but not provided
            if (field.getIsRequired() && field.getIsVisible() &&
                    !field.getIsReadOnly() && StringUtils.isBlank(value)) {
                msg.append(field.getLabel()).append(" is required.").append(msgdelimiter);
            }
            // now do type checking.  If we have no type, then continue.
            if (field.getDatatype() == null)
                continue;
            // field should be numeric but is not
            if ((field.getDatatype().equals(OasisFields.TYPE_NUMBER) ||
                    field.getDatatype().equals(OasisFields.TYPE_CURRENCY)) &&
                    !StringUtils.isNumeric(value, true)) {
                msg.append(field.getLabel()).append(" must be a number.").append(msgdelimiter);
            }
            // field should be date but is not
            if (field.getDatatype().equals(OasisFields.TYPE_DATE) &&
                    !StringUtils.isBlank(value) && !FormatUtils.isDate(value)) {
                msg.append(field.getLabel()).append(" must be a date.").append(msgdelimiter);
            }
        }
        // If the StringBuffer length is 0, return null, otherwise strip off
        // the trailing <BR>.
        String sMsg = (msg.length() == 0) ? null :
                msg.substring(0, msg.length() - msgdelimiter.length()).toString();
        l.exiting(clsName, "getValidationMessages", sMsg);
        return sMsg;
    }

    /**
     * Return a boolean based on a value in a Y/N field in the form.
     *
     * @param form         The action form.
     * @param fieldName    The name of the Y/N field.
     * @param defaultIsYes Is the default "Y" (true)?
     * @return boolean
     * @throws Exception
     */
   public static boolean getBooleanFromYNField(ActionForm form, String fieldName,
                                               boolean defaultIsYes)
            throws Exception {
       if (l.isLoggable(Level.FINER)) {
           l.entering(ActionHelper.class.getName(), "getBooleanFromYNField", new Object[]{form, fieldName, defaultIsYes});
       }
       if (form == null) {
            String excMsg = "null form argument";
            l.severe(excMsg);
            throw new IllegalArgumentException(excMsg);
        }
        if (StringUtils.isBlank(fieldName)) {
            String excMsg = "null field name argument";
            l.severe(excMsg);
            throw new IllegalArgumentException(excMsg);
        }
        boolean retVal = true;
        String szBoolVal = ActionHelper.getFormString(form, fieldName);
        if (StringUtils.isBlank(szBoolVal) ||
                (!szBoolVal.equals(VALUE_FOR_YES) && !szBoolVal.equals(VALUE_FOR_NO))) {
            if (StringUtils.isBlank(szBoolVal)) {
                l.warning(new StringBuffer().append("value for field ").
                        append(fieldName).
                        append(" in form is null of blank;  ").
                        append(defaultIsYes ? VALUE_FOR_YES : VALUE_FOR_NO).
                        append(" will be used as value").
                        toString());
            }
            else {
                l.warning(new StringBuffer().append("unexpected value for field ").
                        append(fieldName).
                        append(" found in form;  value found is '").
                        append(szBoolVal).append("';  ").
                        append(defaultIsYes ? VALUE_FOR_YES : VALUE_FOR_NO).
                        append(" will be used as value").
                        toString());
            }
            szBoolVal = defaultIsYes ? VALUE_FOR_YES : VALUE_FOR_NO;
        }
        retVal = FormatUtils.stringToBool(szBoolVal);
        return retVal;
    }

    /**
     * Gets the default value for a specified field.
     * @param fields      Fields map.
     * @param fieldName   Field name.
     * @return String
     */
    public static String getDefaultValue(OasisFields fields, String fieldName)
    throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(ActionHelper.class.getName(), "getDefaultValue", new Object[]{fields, fieldName});
        }
        String dfltVal = null;
        if (fields == null) {
            String excMsg = "null fields map argument";
            l.severe(excMsg);
            throw new IllegalArgumentException(excMsg);
        }
        else {
            if (StringUtils.isBlank(fieldName)) {
                String excMsg = "null field name argument";
                l.severe(excMsg);
                throw new IllegalArgumentException(excMsg);
            }
            else {
                OasisFormField fld = (OasisFormField) fields.get(fieldName);
                if (fld != null) {
                    dfltVal = fld.getDefaultValue();
                    if (StringUtils.isBlank(dfltVal, true)) {
                        dfltVal = "";
                    }
                }
                else {
                    l.warning(new StringBuffer().append("field ").
                            append(fieldName).append(" not found in fields map").
                            toString());
                }
            }
        }
        return dfltVal;
    }

    /**
     * Check X-FORWARDED-FOR in header. Return remote address if can not find a valid address in X-FORWARDED-FOR
     *
     * @param request the http request to examine.
     * @return m_the originating IP of the request as a String.
     *         if the header field cannot be parsed as an IP address.
     */
    public static String getOriginalIP(HttpServletRequest request) {
        l.entering(ActionHelper.class.getName(), "getOriginalIP");
        // Check if we got anything
        String originalAddress = null;
        String forwardedForHeader = request.getHeader(FORWARDED_FOR_HEADER_NAME);
        if (forwardedForHeader != null && forwardedForHeader.trim().length() > 0) {
            // Get the leftmost address if there are more than one
            int commaIndex = forwardedForHeader.indexOf(',');
            if (commaIndex > -1) {
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, ActionHelper.class.getName(), "getOriginatingIP", "there are many IPs in the header, getting the first one.");
                }
                originalAddress = forwardedForHeader.substring(0, commaIndex);
            } else {
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE,ActionHelper.class.getName(), "getOriginatingIP", "there is only one ip in the header.");
                }
                originalAddress = forwardedForHeader;
            }
            // User remote address if X-FORWARDED-FOR is unknown
            if ("unknown".equalsIgnoreCase(originalAddress)) {
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, ActionHelper.class.getName(), "getOriginatingIP", "X-FORWARDED-FOR is unknown, use remote address");

                }
                originalAddress = request.getRemoteAddr();
            }
        } else {
            originalAddress = request.getRemoteAddr();
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(ActionHelper.class.getName(), "getOriginatingIP", originalAddress);
        }
        return originalAddress;
    }

    /**
     * Encode field that has been masked into Base64
     * @param paramName
     * @param objValue
     * @param fields
     * @return
     */
    public static Object encodeMaskedField(String paramName, Object objValue,  OasisFields fields) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(ActionHelper.class.getName(), "encodeMaskedField", new Object[]{paramName, objValue,fields});
        }

        Object oValue = objValue;
        Map mapMaskedFields = null;
        boolean encodeField = false;

        boolean systemParameterFlag = YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm("ENCODE_MASKED_FIELD", "N")).booleanValue();

        if (objValue != null && fields != null && systemParameterFlag) {
            if (fields.hasField(paramName) && fields.hasField(paramName + "_GH")) {
                if (fields.getField(paramName).getIsMasked() && fields.getField(paramName + "_GH").getIsMasked()) encodeField = true;
            } else if (fields.hasField(paramName) && !fields.hasField(paramName + "_GH")) {
                if (fields.getField(paramName).getIsMasked()) encodeField = true;
            } else if(!fields.hasField(paramName) && fields.hasField(paramName + "_GH")) {
                if (fields.getField(paramName + "_GH").getIsMasked()) encodeField = true;
            }

            //encoding will occur on the following condition:
            //- field is masked in both form field and grid header field "_GH"
            //- field is masked in form field and there is no associated grid header field "_GH"
            //- field is masked in grid header and there is no associated form field
            if (encodeField) {
                if (isObjectApplicableForBase64(objValue)) {
                    try {
                        oValue = Base64.getEncoder().encodeToString(String.valueOf(objValue).getBytes(CHAR_SET_NAME));

                        mapMaskedFields = getCurrentPageViewCacheMap(RequestIds.OASIS_MASKED_FIELDS);
                        mapMaskedFields.put(paramName.trim().toUpperCase(), paramName.trim().toUpperCase());
                    } catch (Exception ex) {
                        l.logp(Level.WARNING, ActionHelper.class.getName(),
                           "encodeMaskedField", "Unable to encode paramName: " + paramName + " objValue: " + String.valueOf(objValue).toString());
                    }
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            String s = "paramName: " + paramName + "   objValue: " + objValue + " oValue: " + oValue;
            l.exiting(ActionHelper.class.getName(), "encodeMaskedField", new Object[]{s, mapMaskedFields});
        }

        return oValue;

    }

    /**
     * Decode field from Base64
     * @param paramName
     * @param objValue
     * @param mapMaskedFields
     * @return
     */
    public static Object decodeMaskedField(String paramName, Object objValue, Map mapMaskedFields){
        if (l.isLoggable(Level.FINER)) {
            l.entering(ActionHelper.class.getName(), "decodeMaskedField", new Object[]{paramName, objValue,mapMaskedFields});
        }

        Object oValue = objValue;

        if (objValue != null && paramName != null && mapMaskedFields != null && mapMaskedFields.containsKey(paramName.trim().toUpperCase())) {
            oValue = decodeField(objValue);
        }

        if (l.isLoggable(Level.FINER)) {
            String s = "paramName: " + paramName + "   objValue: " + String.valueOf(objValue) + "  oValue: " + String.valueOf(oValue);
            l.exiting(ActionHelper.class.getName(), "decodeMaskedField", new Object[]{s});
        }
        return oValue;
    }

    /**
     * Get the map of all the masked fields that haas been cache.
     * @return
     */
    public static Map getMaskedPageViewCacheMap() {
        Map map = loadPageViewCachedMap(RequestIds.OASIS_MASKED_FIELDS);
        return map;
    }

    /**
     * Get the map of all the fields that overrides xss system parameter flag.
     * @return
     */
    public static Map getXssOverridesPageViewCacheMap() {
        Map map = loadPageViewCachedMap(RequestIds.OASIS_XSS_OVERRIDES_FIELDS);
        return map;
    }

    /**
     * retrieves a map of specific type.
     * @param type - the type to retrieve.
     * @return
     */
    public static Map loadPageViewCachedMap(String type) {
        Map map = getCurrentPageViewCacheMap(type);
        return map;
    }

    /**
     * retrieves the page view cache map for the specific type
     * @param type
     * @return
     */
    public static Map getCurrentPageViewCacheMap(String type) {
        Map map  = null;
        if (!StringUtils.isBlank(type)) {
            map = (Map) getRequestHelper().getPageViewCacheMap().get(type);
            if (map == null) {
                map = new HashMap();
                getRequestHelper().getPageViewCacheMap().put(type, map);
            }
        }
        return map;
    }

    /**
     * Check if jqxGrid is enabled on the page.
     * @param request
     * @param className
     * @return
     */
    public static boolean isPageUseJqxGrid(HttpServletRequest request, String className) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(ActionHelper.class.getName(), "isPageUseJqxGrid", new Object[]{request, className});
        }

        boolean useJqxGrid = NavigationManager.getInstance().isPageUseJqxGrid(request, className);;

        if (l.isLoggable(Level.FINER)) {
            l.exiting(ActionHelper.class.getName(), "isPageUseJqxGrid", useJqxGrid);
        }
        return useJqxGrid;
    }

    /**
     * Check if jqxGrid is enabled in project
     * @param request
     * @return
     */
    public static boolean isJqxGridEnabled(HttpServletRequest request) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(ActionHelper.class.getName(), "isJqxGridEnabled", new Object[]{request});
        }

        boolean jqxGridEnabled = false;

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(RequestIds.USE_JQX_GRID) != null) {
            // The USE_JQX_GRID property in session is used for easily debug.
            jqxGridEnabled = (boolean) session.getAttribute(RequestIds.USE_JQX_GRID);
        } else {
            jqxGridEnabled = YesNoFlag.getInstance(ApplicationContext.getInstance().getProperty("jqxGrid.enabled", "N")).booleanValue();
        }

        return jqxGridEnabled;
    }

    /**
     * Check to see if the string has been encoded using Base64
     * @param stringBase64
     * @return
     */
    public static boolean isBase64(Object stringBase64){
        if (l.isLoggable(Level.FINER)) {
            l.entering(ActionHelper.class.getName(), "isBase64", new Object[]{stringBase64});
        }

        boolean bRc = false;

        if (!StringUtils.isBlank(String.valueOf(stringBase64)) && String.valueOf(stringBase64).length() >= 4) {
            if (isObjectApplicableForBase64(stringBase64)) {
                try {
                    //check for all ASCII characters only.
                    String pattern = "^[ -~]*$";

                    String str = new String(Base64.getDecoder().decode(String.valueOf(stringBase64).getBytes(CHAR_SET_NAME)));
                    if (str.matches(pattern)) {
                        bRc = true;
                    }
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(ActionHelper.class.getName(), "isBase64", new Object[]{str, stringBase64});
                    }
                } catch(Exception ex) {
                }
            }
            if (l.isLoggable(Level.FINER)) {
                l.exiting(ActionHelper.class.getName(), "isBase64", new Object[]{stringBase64, bRc});
            }
        }

        return bRc;
    }

    /**
     * check to see if object is applicable for encode / decode
     * @param obj
     * @return
     */
    private static boolean isObjectApplicableForBase64(Object obj) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(ActionHelper.class.getName(), "isObjectApplicableForBase64", new Object[]{obj});
        }

        boolean bRc = false;

        if (!(obj instanceof String[]) && !(obj instanceof YesNoFlag)) {
            bRc = true;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(ActionHelper.class.getName(), "isObjectApplicableForBase64", new Object[]{obj, bRc});
        }
        return bRc;
    }

    /**
     * decode base64 field
     * @param objValue
     * @return
     */
    public static Object decodeField(Object objValue) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(ActionHelper.class.getName(), "decodeField", new Object[]{objValue});
        }

        Object obj = objValue;

        if (isBase64(objValue)) {
            try {
                obj = new String(Base64.getDecoder().decode(String.valueOf(objValue).getBytes(CHAR_SET_NAME)));
            } catch (Exception ex) {
                l.warning(new StringBuffer().append("Object ").
                   append(String.valueOf(objValue)).append(" unable to be decoded").
                   toString());
            }
        }

        if (l.isLoggable(Level.FINER)) {
            String s = "objValue: " + String.valueOf(objValue) + " obj: " + String.valueOf(obj);
            l.exiting(ActionHelper.class.getName(), "decodeField", new Object[]{s});
        }
        return obj;
    }

    /**
     * get a reference to the RequestHelper object to get the PageViewCacheMap
     * @return
     */
    public static RequestHelper getRequestHelper() {
        if (requestHelper == null) {
            requestHelper = new RequestHelper();
        }
        return requestHelper;
    }

    /**
     * The request header name that holds the forwarded chain information.
     */
    private static final String FORWARDED_FOR_HEADER_NAME = "X-FORWARDED-FOR";
    private static final Logger l = LogUtils.getLogger(ActionHelper.class);
    private static final String CHAR_SET_NAME = "UTF-8";
    private static RequestHelper requestHelper;
}
