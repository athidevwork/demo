package dti.oasis.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.filter.XssFilter;
import dti.oasis.http.RequestIds;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.messagemgr.MessageManagerAdmin;
import dti.oasis.obr.RequestHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestLifecycleAdvisor;
import dti.oasis.request.RequestStorageIds;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.session.PageViewStateAdmin;
import dti.oasis.session.UserSessionManager;
import dti.oasis.tags.OasisFields;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import org.apache.struts.action.*;
import org.apache.struts.config.ForwardConfig;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.util.RequestUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * This class extends the Struts RequestProcessor functionality
 * to adapt the Struts Request Workflow with the M1 PM Application Framework.
 * Specifically, this class overrides the processActionCreate and processActionPerform methods.
 * <p/>
 * The processActionCreate method is extended to adapt Struts to use Spring to load the Action classes.
 * The 'type' attribute of action mapping defined in the struts-config.xml file is used as the bean name
 * to locate the Action Bean definition in the Struts Config file.
 * If a bean does not exists in the Spring config file, the default processActionCreate behavior
 * is used to create the Action class.
 * <p/>
 * The processActionPerform method is extended to hook in the RequestLifecycleAdvisor around each
 * execution on an Action class. The RequestLifecycleAdvisor initializes the RequestStorageManager
 * and the UserSessionManager for the request, and invokes all registered RequestLifecycleListener objects
 * with the Request Lifecycle Events to indicate that the request is initializing or terminating.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 25, 2006
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 02/07/2007       wer         Added Support for multiple Message Resource Files
 * 04/09/2008       wer         refactord logic to initialize the resource bundle for a Servlet into initResourceBundleForServletContext() to make it reusable.
 * 06/16/2008       wer         Refactored to work with JDK 1.5
 * 05/17/2017       cesar       182477 - Added  decodeMaskedFields() to decode any masked fields from the form.getmap(). Some pages CIS uses form.getMap() directly.
 * 09/11/2017       kshen       Changed the method processActionPerform to support checking if a page uses jqxGrid by AJAX call.
 * 05/14/2018       cesar       192983 - Modified decodeMaskedFields () to include xssFilterOverries.
 * 11/13/2018       wreeder     196147 - Set the OASIS Fields into the request attributes from cache for Ajax requests so we don't need to reload them
 * ---------------------------------------------------
 */
public class DefaultRequestProcessor extends RequestProcessor {

    /**
     * The name of the Request Attribute where a Properties object is stored with the parameters that
     * should be added to a forward url.
     */
    public static final String FORWARD_PARAMETERS = RequestIds.FORWARD_PARAMETERS;

    /**
     * <p>Initialize this request processor instance.</p>
     *
     * @param servlet      The ActionServlet we are associated with
     * @param moduleConfig The ModuleConfig we are associated with.
     * @throws javax.servlet.ServletException If an error occor during initialization
     */
    public void init(ActionServlet servlet, ModuleConfig moduleConfig) throws ServletException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "init", new Object[]{servlet, moduleConfig});
        }

        super.init(servlet, moduleConfig);

        MessageManagerAdmin messageManager = ((MessageManagerAdmin) MessageManager.getInstance());
        messageManager.initResourceBundleForServletContext(servlet.getServletContext());

        // get excluded file list for load action
        String excludedString = ApplicationContext.getInstance().getProperty("oasis.obr.load.excluded.files");
        m_excludedFiles = excludedString.split(",");

        m_requestHelper = new RequestHelper();

        l.exiting(getClass().getName(), "init");
    }

    /**
     * <p>Return an <code>Action</code> instance that will be used to process
     * the current request, creating a new one if necessary.</p>
     *
     * @param request  The servlet request we are processing
     * @param response The servlet response we are creating
     * @param mapping  The mapping we are using
     * @throws java.io.IOException if an input/output error occurs
     */
    protected Action processActionCreate(HttpServletRequest request, HttpServletResponse response, ActionMapping mapping) throws IOException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processActionCreate", new Object[]{request, response, mapping});
        }

        Action action = null;
        if (getApplicationContext().hasBean(mapping.getType())) {
            action = (Action) getApplicationContext().getBean(mapping.getType());
            action.setServlet(this.servlet);
            l.logp(Level.FINE, getClass().getName(), "processActionCreate", "Loaded the action '" + mapping.getType() + "' from the Spring configuration.");
        }
        else {
            action = super.processActionCreate(request, response, mapping);
            l.logp(Level.FINE, getClass().getName(), "processActionCreate", "Loaded the action '" + mapping.getType() + "' from the Struts configuration.");
        }

        l.exiting(getClass().getName(), "processActionCreate", action);
        return action;
    }

    /**
     * <P>Ask the specified <code>Action</code> instance to handle this
     * request. Return the <code>ActionForward</code> instance (if any)
     * returned by the called <code>Action</code> for further processing.
     * </P>
     *
     * @param request  The servlet request we are processing
     * @param response The servlet response we are creating
     * @param action   The Action instance to be used
     * @param form     The ActionForm instance to pass to this Action
     * @param mapping  The ActionMapping instance to pass to this Action
     * @throws java.io.IOException            if an input/output error occurs
     * @throws javax.servlet.ServletException if a servlet exception occurs
     */
    protected ActionForward processActionPerform(HttpServletRequest request, HttpServletResponse response, Action action, ActionForm form, ActionMapping mapping) throws IOException, ServletException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processActionPerform", new Object[]{request, response, action, form, mapping});
        }

        ActionForward forward = null;
        if (!request.getMethod().equalsIgnoreCase("HEAD")) {
            ApplicationContext.getInstance().exposeMessageSourceForJstl(servlet.getServletContext(), request);

            try {
                // Initialize the Request Lifecycle
                getRequestLifecycleAdvisor().initialize(request);

                decodeMaskedFields(form, request);

                String process = request.getParameter(RequestIds.PROCESS);
                boolean isProcessExcludedForObr = isProcessExcludedForObr(request, action);
                if (OBR_ON_SAVE.equals(process) && action instanceof BaseAction) {
                    BaseAction baseAction = (BaseAction) action;
                    if (isProcessExcludedForObr) {
                        baseAction.writeAjaxXmlResponse(response, new Record());
                    } else {
                        executeBeforeSave(baseAction, form, request, response);
                    }
                } else if ((OBR_ON_CHANGE.equals(process) || OBR_ON_ADD.equals(process)) && action instanceof BaseAction) {
                    BaseAction baseAction = (BaseAction) action;
                    if (isProcessExcludedForObr) {
                        baseAction.writeAjaxXmlResponse(response, new Record());
                    } else {
                        executeOnChange(baseAction, form, request, response);
                    }
                } else if (isAJAXRequestToCheckForJqxGridUsageOnThePage(request)) {
                    boolean useJqxGrid = ActionHelper.isPageUseJqxGrid(request, action.getClass().getName());

                    Record record = new Record();
                    record.setFieldValue(RequestIds.USE_JQX_GRID, useJqxGrid ? "Y" : "N");

                    BaseAction baseAction = (BaseAction) action;
                    baseAction.writeAjaxResponse(response, record, true);
                } else {
                    // useMapWithoutPrefixes
                    boolean useMapWithoutPrefixes = false;
                    if (action instanceof BaseAction) {
                        useMapWithoutPrefixes = ((BaseAction) action).getUseMapWithoutPrefixes();
                    }
                    RequestStorageManager requestStorageManager = RequestStorageManager.getInstance();
                    if (isProcessExcludedForObr) {
                        requestStorageManager.set(RequestStorageIds.IS_PROCESS_EXCLUDED_FOR_OBR, true);
                    } else {
                        requestStorageManager.set(RequestStorageIds.IS_PROCESS_EXCLUDED_FOR_OBR, false);
                        requestStorageManager.set(RequestStorageIds.STRUTS_ACTION_CLASS, action);
                    }
                    if (isAjaxRequest(request)) {
                        // Set the OASIS Fields into the request attributes from cache for Ajax requests
                        PageViewStateAdmin pageViewStateAdmin = (PageViewStateAdmin) UserSessionManager.getInstance().getUserSession();
                        Map pageViewData = pageViewStateAdmin.getPageViewData();
                        OasisFields fields = (OasisFields) pageViewData.get(IOasisAction.KEY_FIELDS);

                        if (fields != null) {
                            request.setAttribute(IOasisAction.KEY_FIELDS, fields);
                        }
                    }

                    try {
                        forward = action.execute(mapping, form, request, response);
                        if (isLoadAction(forward) && !isProcessExcludedForObr) {
                            m_requestHelper.executeOnPageLoad(request, useMapWithoutPrefixes, action);
                        }
                        propagatePageViewStateForwardOnValidationFailure(request);
                    } catch (Exception e) {
                        // Attempt to fix the problem
                        l.logp(Level.FINE, getClass().getName(), "processActionPerform", "Failed to process the Action. Attempting to fix the error... " + e.getMessage());
                        if (getRequestLifecycleAdvisor().failure(e)) {
                            // The problem was fixed. Re-try the request.
                            forward = action.execute(mapping, form, request, response);
                            if (isLoadAction(forward) && !isProcessExcludedForObr) {
                                m_requestHelper.executeOnPageLoad(request, useMapWithoutPrefixes, action);
                            }
                            propagatePageViewStateForwardOnValidationFailure(request);
                        } else {
                            // The problem could not be fixed. Re-throw the exception to allow normal exception handling.
                            throw e;
                        }
                    }
                }
            } catch (Throwable e) {
                l.logp(Level.SEVERE, getClass().getName(), "processActionPerform", "Failed to process the Action", e);
                // Make the exception available for the Error Page
                request.setAttribute(IOasisAction.KEY_ERROR, e);
                forward = mapping.findForward(IOasisAction.ERROR_ACTION_FWD);
            }

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "processActionPerform", forward);
            }
        }
        return forward;
    }

    protected boolean isAjaxRequest(HttpServletRequest request) {
        boolean isAjaxRequest = YesNoFlag.getInstance(request.getParameter("__isAjaxRequest")).booleanValue();
        return isAjaxRequest;
    }

    /**
     * check if it is a load page action
     * @param forward
     * @return
     */
    protected boolean isLoadAction(ActionForward forward) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isLoadAction", new Object[]{forward});
        }
        boolean isLoadAction = false;
        if (forward != null) {
            String path = forward.getPath();
            if (path.indexOf(".jsp") > 0) {
                boolean isInList = false;
                for (String file : m_excludedFiles) {
                    if (path.indexOf(file) > 0) {
                        isInList = true;
                        break;
                    }
                }
                if (!isInList) {
                    isLoadAction = true;
                }
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isLoadAction", isLoadAction);
        }
        return isLoadAction;
    }

    /**
     * check if process is excluded for OBR
     * @param request
     * @param action
     * @return
     */
    protected boolean isProcessExcludedForObr(HttpServletRequest request, Action action) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isProcessExcludedForObr", new Object[]{action});
        }
        boolean isProcessExcludedForObr = false;
        String process = request.getParameter("process");
        if (action != null && action instanceof BaseAction && process != null) {
            BaseAction baseAction = (BaseAction) action;
            String obrExcludeProcessNames = baseAction.getObrExcludeProcessNames();
            if (!StringUtils.isBlank(obrExcludeProcessNames)) {
                String[] excludeNames = obrExcludeProcessNames.split(",");
                for (String excludeName : excludeNames) {
                    if (process.equals(excludeName)) {
                        isProcessExcludedForObr = true;
                        break;
                    }
                }
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isProcessExcludedForObr", isProcessExcludedForObr);
        }
        return isProcessExcludedForObr;
    }

    /**
     * <p>Forward or redirect to the specified destination, by the specified
     * mechanism.  This method uses a <code>ForwardConfig</code> object instead
     * an <code>ActionForward</code>.</p>
     *
     * @param request  The servlet request we are processing
     * @param response The servlet response we are creating
     * @param forward  The ForwardConfig controlling where we go next
     * @throws java.io.IOException            if an input/output error occurs
     * @throws javax.servlet.ServletException if a servlet exception occurs
     */
    protected void processForwardConfig(HttpServletRequest request, HttpServletResponse response, ForwardConfig forward) throws IOException, ServletException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processForwardConfig", new Object[]{request, response, forward});
        }

        if (!request.getMethod().equalsIgnoreCase("HEAD")) {
            try {
                if (forward == null) {
                    return;
                }

                if (log.isDebugEnabled()) {
                    log.debug("processForwardConfig(" + forward + ")");
                }

                //Remove RequestStorageManager cache (Issue 188111, change#315175)
                RequestStorageManager rsm = RequestStorageManager.getInstance();
                Set keys = rsm.getCopy().keySet();
                for (Object key : keys) {
                    String keyStr = key.toString();
                    if (keyStr.endsWith("_LOVsLoaded")) {
                        rsm.remove(keyStr);
                    }
                }

                String forwardPath = forward.getPath();
                String uri = null;

                // paths not starting with / should be passed through without any processing
                // (ie. they're absolute)
                if (forwardPath.startsWith("/")) {
                    uri = RequestUtils.forwardURL(request, forward, null);    // get module relative uri
                } else {
                    uri = forwardPath;
                }

                uri = addForwardParameters(request, uri);

                if (forward.getRedirect()) {
                    // only prepend context path for relative uri
                    if (uri.startsWith("/")) {
                        uri = request.getContextPath() + uri;
                    }
                    response.sendRedirect(response.encodeRedirectURL(uri));

                } else {
                    doForward(uri, request, response);
                }
            } finally {
                // Terminate the Request Lifecycle
                getRequestLifecycleAdvisor().terminate();
            }
        }
        l.exiting(getClass().getName(), "processForwardConfig");
    }

    protected String addForwardParameters(HttpServletRequest request, String uri) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addForwardParameters", new Object[]{request, uri});
        }

        StringBuffer buf = new StringBuffer(uri);
        Properties props = (Properties) request.getAttribute(BaseAction.FORWARD_PARAMETERS);
        if (props != null) {
            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(), "addForwardParameters", "forward uri before adding forward parameters: " + uri);
            }

            String sep = uri.indexOf("?") > 0 ? "&" : "?";
            Enumeration en = props.propertyNames();
            while (en.hasMoreElements()) {
                String name = (String) en.nextElement();
                // Add the parameter and encoded value to the URI
                buf.append(sep).append(name).append("=").append(URLEncoder.encode(props.getProperty(name)));
                sep = "&";
            }
            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(), "addForwardParameters", "Added forward parameters to create the forward URI: " + buf);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addForwardParameters", buf);
        }
        return buf.toString();
    }

    /**
     * Set the given parameter name/value pair in a Properties object stored in the request attributes.
     * The Properties will added to the forward url as request parameters by the DefaultRequestParameter.
     *
     * @param request the HttpServletRequest
     * @param name    the name of the forward parameter
     * @param value   the value to set for the given forward parameter
     */
    protected void setForwardParameter(HttpServletRequest request, String name, String value) {
        Properties props = (Properties) request.getAttribute(FORWARD_PARAMETERS);
        if (props == null) {
            props = new Properties();
        }

        props.setProperty(name, value);
        request.setAttribute(name, value);
        request.setAttribute(FORWARD_PARAMETERS, props);
    }

    /**
     * handle ajax request for obr save events
     * @param baseAction
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    protected void executeBeforeSave(BaseAction baseAction, ActionForm form, HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "executeBeforeSave", new Object[]{baseAction, form, request, response});
        }
        try {
            baseAction.securePage(request, form);
            m_requestHelper.executeBeforeSave(request, baseAction);
            baseAction.writeAjaxXmlResponse(response, new Record());
        } catch (Exception e) {
            baseAction.handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to execute obr logic for save events.", e, response);
        }
        l.exiting(getClass().getName(), "executeBeforeSave");
    }

    /**
     * handle ajax request for obr change events
     * @param baseAction
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    protected void executeOnChange(BaseAction baseAction, ActionForm form, HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "executeOnChange", new Object[]{baseAction, form, request, response});
        }
        try {
            baseAction.securePage(request, form);
            m_requestHelper.executeOnChange(request, baseAction);
            RecordSet recordSet = (RecordSet) request.getAttribute("OBREnforcedRecordSet");
            if (recordSet != null) {
                RequestStorageManager.getInstance().set("RecordSetForOBROnChange", true);
                baseAction.writeAjaxXmlResponse(response, recordSet);
            }else{
                Record record = null;
                baseAction.writeAjaxXmlResponse(response, record);
            }
        } catch (Exception e) {
            baseAction.handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to execute obr logic for change.", e, response);
        }
        l.exiting(getClass().getName(), "executeOnChange");
    }


    protected ApplicationContext getApplicationContext() {
        if (m_applicationContext == null) {
            m_applicationContext = ApplicationContext.getInstance();
        }
        return m_applicationContext;
    }

    protected RequestLifecycleAdvisor getRequestLifecycleAdvisor() {
        if (m_requestLifecycleAdvisor == null) {
            m_requestLifecycleAdvisor = (RequestLifecycleAdvisor) getApplicationContext().getBean(RequestLifecycleAdvisor.BEAN_NAME);
        }
        return m_requestLifecycleAdvisor;
    }

    /**
     * Check if the request is to checking if the current page uses jqxGrid.
     * @param request
     * @return
     */
    private boolean isAJAXRequestToCheckForJqxGridUsageOnThePage(HttpServletRequest request) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isAJAXRequestToCheckForJqxGridUsageOnThePage", new Object[]{request});
        }

        boolean checkIfPageUseJqxGrid =  "Y".equals(request.getParameter(CHECK_IF_PAGE_USE_JQX_GRID));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isAJAXRequestToCheckForJqxGridUsageOnThePage", checkIfPageUseJqxGrid);
        }
        return checkIfPageUseJqxGrid;
    }

    private void propagatePageViewStateForwardOnValidationFailure(HttpServletRequest request) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "propagatePageViewStateForwardOnValidationFailure", new Object[]{request});
        }

        // The page view state data will be placed into the request storage manager, only if the prior request
        // has been failed for validation.
        Map pageViewStateData = new Hashtable();
        boolean isLastRequestFailedForValidation = false;
        String newPageViewStateId = "";
        String pageViewStateId = "";
        RequestStorageManager requestStorageManager = RequestStorageManager.getInstance();
        if (requestStorageManager.has(RequestIds.CACHE_ID_FOR_PAGE_VIEW_DATA)) {
            pageViewStateData = (Map) requestStorageManager.get(RequestIds.CACHE_ID_FOR_PAGE_VIEW_DATA);
            pageViewStateId = (String) requestStorageManager.get(RequestIds.CACHE_ID_FOR_PAGE_VIEW);
            isLastRequestFailedForValidation = true;
        }
        if (isLastRequestFailedForValidation) {
            newPageViewStateId = ((PageViewStateAdmin) UserSessionManager.getInstance().getUserSession()).getNewPageViewId();
            l.logp(Level.FINE, getClass().getName(), "processActionPerform", "Validation failure occurred. New page view state id [" + newPageViewStateId + "] has been generated");
            Map newPageViewStateData = ((PageViewStateAdmin) UserSessionManager.getInstance().getUserSession()).getPageViewData(newPageViewStateId);
            newPageViewStateData.putAll(pageViewStateData);
            l.logp(Level.FINE, getClass().getName(), "processActionPerform", "Copied page view state data from page view id [" + pageViewStateId + "] to new page view id [" + newPageViewStateId + "]");
            setForwardParameter(request, RequestIds.CACHE_ID_FOR_PAGE_VIEW, newPageViewStateId);
        }
        l.exiting(getClass().getName(), "propagatePageViewStateForwardOnValidationFailure");
    }

    private void decodeMaskedFields(ActionForm form, HttpServletRequest request) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "decodeMaskedFields", new Object[]{form});
        }

        if (form != null && ((DynaActionForm) form).getMap() != null) {
            Map map = ((DynaActionForm) form).getMap();

            if (map != null) {
                Map mapMaskFields = ActionHelper.getMaskedPageViewCacheMap();
                if (mapMaskFields != null) {
                    Iterator m = map.entrySet().iterator();
                    Iterator it = mapMaskFields.keySet().iterator();

                    while (it.hasNext()) {
                        String key = (String) it.next();
                        while (m.hasNext()) {
                            Map.Entry pair = (Map.Entry)m.next();
                            String mapKey =(String)pair.getKey();
                            if (mapKey.equalsIgnoreCase(key)) {
                                Object val = ActionHelper.decodeMaskedField(key, pair.getValue(), mapMaskFields);
                                map.put(mapKey, String.valueOf(val));
                                if (l.isLoggable(Level.FINER)) {
                                    l.logp(Level.FINER,
                                            DefaultRequestProcessor.class.getName(),
                                        "decodeMaskedFields/maskedFields",
                                        "mapKey: " + mapKey);
                                }
                                break;
                            }
                        }
                    }
                }

                Map xssOverridesFieldsMap = ActionHelper.getXssOverridesPageViewCacheMap();
                List<Pattern> patternsList = XssFilter.getXssPatternList(request);
                if (xssOverridesFieldsMap != null) {
                    Iterator m = map.entrySet().iterator();
                    Iterator it = xssOverridesFieldsMap.keySet().iterator();

                    while (it.hasNext()) {
                        String key = (String) it.next();
                        while (m.hasNext()) {
                            Map.Entry pair = (Map.Entry)m.next();
                            String mapKey =(String)pair.getKey();
                            if (((String)pair.getKey()).equalsIgnoreCase(key)) {
                                Object val = XssFilter.sanitizeParameter(patternsList, xssOverridesFieldsMap, key, String.valueOf(pair.getValue()));
                                map.put(mapKey, String.valueOf(val));
                                if (l.isLoggable(Level.FINER)) {
                                    l.logp(Level.FINER,
                                        DefaultRequestProcessor.class.getName(),
                                        "decodeMaskedFields/xssOverrides",
                                        "mapKey: " + mapKey);
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
        l.exiting(getClass().getName(), "decodeMaskedFields");
    }

    private String[] m_excludedFiles = new String[0];
    private RequestHelper m_requestHelper;
    private ApplicationContext m_applicationContext;
    private RequestLifecycleAdvisor m_requestLifecycleAdvisor;

    private final Logger l = LogUtils.getLogger(getClass());

    private static final String OBR_ON_SAVE = "ObrOnSave";
    private static final String OBR_ON_CHANGE = "ObrOnChange";
    private static final String OBR_ON_ADD = "ObrOnAdd";
    private static final String CHECK_IF_PAGE_USE_JQX_GRID = "checkIfPageUseJqxGrid";
}