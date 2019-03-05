package dti.oasis.accesstrailmgr.impl;

import dti.oasis.accesstrailmgr.AccessTrailRequestIds;
import dti.oasis.accesstrailmgr.AccessTrailManager;
import dti.oasis.accesstrailmgr.dao.AccessTrailDAO;
import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.struts.ActionHelper;
import dti.oasis.struts.AddSelectIndLoadProcessor;
import dti.oasis.struts.IOasisAction;
import dti.oasis.util.LogUtils;
import dti.oasis.util.PageBean;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.http.RequestIds;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements methods that will record and retrieve user activity information perforced across
 * various web applications.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 12, 2010
 *
 * @author James
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class AccessTrailManagerImpl extends AccessTrailManager {


    public AccessTrailManagerImpl() {
        initialize();
    }

    /**
     * initialize
     */
    public void initialize() {
        String baseExcludedURLs = ApplicationContext.getInstance().getProperty(BASE_EXCLUDED_URL_LIST, "");
        String baseExcludedMethods = ApplicationContext.getInstance().getProperty(BASE_EXCLUDED_METHOD_LIST, "");
        String customExcludedURLs = ApplicationContext.getInstance().getProperty(CUSTOM_EXCLUDED_URL_LIST, "");
        String customExcludedMethods = ApplicationContext.getInstance().getProperty(CUSTOM_EXCLUDED_METHOD_LIST, "");
        if (!StringUtils.isBlank(customExcludedURLs)) {
            setExcludeURLs(baseExcludedURLs + "," + customExcludedURLs);
        } else {
            setExcludeURLs(baseExcludedURLs);
        }
        if (!StringUtils.isBlank(customExcludedMethods)) {
            setExcludeMethods(baseExcludedMethods + "," + customExcludedMethods);
        } else {
            setExcludeMethods(baseExcludedMethods);
        }
    }


    public void initializeForRequest(HttpServletRequest request) {
        PageBean pageBean = (PageBean) request.getAttribute(IOasisAction.KEY_PAGEBEAN);
        if (pageBean != null && pageBean.getId() != null) {
            String pageCode = pageBean.getId();
            String sourceTableName = (String) request.getAttribute(AccessTrailRequestIds.ACCESS_TRAIL_SOURCE_TABLE_NAME);

            if (StringUtils.isBlank(sourceTableName) && pageCode.startsWith("CI")) {
                String sourceRecordFk = request.getParameter("pk");
                if (!StringUtils.isBlank(sourceRecordFk)) {
                    sourceTableName = "ENTITY";
                    //sourceRecordNo is handled by procedure.
                    String sourceRecordNo = "";
                    request.setAttribute(AccessTrailRequestIds.ACCESS_TRAIL_SOURCE_TABLE_NAME, sourceTableName);
                    request.setAttribute(AccessTrailRequestIds.ACCESS_TRAIL_SOURCE_RECORD_NO, sourceRecordNo);
                    request.setAttribute(AccessTrailRequestIds.ACCESS_TRAIL_SOURCE_RECORD_FK, sourceRecordFk);
                }
                // else for globalSearch or from WorkCenter Activity History page, the CIEntityList class will set it
            }
        }
    }

    /**
     * This method accepts the user activity information and saves it into database.
     *
     * @param oasisUserId
     * @param osUser          Operating System ID of the user that created the oracle connection.
     * @param ipAddress
     * @param subsystemCode
     * @param pageCode
     * @param method
     * @param sourceTableName
     * @param sourceRecordNo
     * @param sourceRecordFk
     */
    public void recordAccessTrail(String oasisUserId, String osUser, String ipAddress,
                                  String subsystemCode, String pageCode, String method,
                                  String sourceTableName, String sourceRecordNo, String sourceRecordFk) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "recordAccessTrail", new Object[]{oasisUserId, osUser,
                    ipAddress, subsystemCode, pageCode, method,
                    sourceTableName, sourceRecordNo, sourceRecordFk});
        }

        Record inputRecord = new Record();

        inputRecord.setFieldValue("oasisUserId", oasisUserId);
        inputRecord.setFieldValue("osUser", osUser);
        inputRecord.setFieldValue("ipAddress", ipAddress);
        inputRecord.setFieldValue("subsystemCode", subsystemCode);
        inputRecord.setFieldValue("pageCode", pageCode);
        inputRecord.setFieldValue("method", method);
        inputRecord.setFieldValue("sourceTableName", sourceTableName);
        inputRecord.setFieldValue("sourceRecordNo", sourceRecordNo);
        inputRecord.setFieldValue("sourceRecordId", sourceRecordFk);

        getAccessTrailDAO().addAccessTrail(inputRecord);

        l.exiting(getClass().getName(), "recordAccessTrail");
    }

    /**
     * record access trail
     *
     * @param request
     */
    public void recordAccessTrail(HttpServletRequest request) {
        l.entering(getClass().getName(), "recordAccessTrail");
        try {
            Object error = request.getAttribute(IOasisAction.KEY_ERROR);
            Object validateError = request.getAttribute(IOasisAction.KEY_VALIDATION_ERROR);
            boolean isAjaxRequest = (YesNoFlag.getInstance(request.getParameter("__isAjaxRequest")).booleanValue()) ||
                                    request.getParameterMap().containsKey("javax.faces.source");
            if (error == null && validateError == null && !isAjaxRequest) {
                if (!isExcludeURL(request.getRequestURI())) {
                    PageBean pageBean = (PageBean) request.getAttribute(IOasisAction.KEY_PAGEBEAN);
                    if (pageBean != null && pageBean.getId() != null) {
                        boolean addTrail = false;
                        boolean defaultPageAudit = YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm(WEB_DEFAULT_PAGE_AUDIT, "N")).booleanValue();

                        if ("Y".equals(pageBean.getAccessTrailB())) {
                            addTrail = true;
                        } else if ("N".equals(pageBean.getAccessTrailB())) {
                            addTrail = false;
                        } else {
                            addTrail = defaultPageAudit;
                        }

                        if (addTrail) {
                            String method = (String) request.getAttribute(AccessTrailRequestIds.ACCESS_TRAIL_METHOD);
                            if (method == null) {
                                method = request.getParameter(RequestIds.PROCESS);
                                if (StringUtils.isBlank(method)) {
                                    method = "unspecified";
                                }
                            }
                            if (!isExcludeMethod(method)) {

                                String oasisUserId = ActionHelper.getCurrentUser(request).getUserId();
                                String osUser = System.getProperty("user.name");
                                String ipAddress = ActionHelper.getOriginalIP(request);
                                String subsystemCode = ApplicationContext.getInstance().getProperty("applicationId", "");
                                String pageCode = pageBean.getId();

                                initializeForRequest(request);
                                String sourceTableName = (String) request.getAttribute(AccessTrailRequestIds.ACCESS_TRAIL_SOURCE_TABLE_NAME);
                                String sourceRecordNo = (String) request.getAttribute(AccessTrailRequestIds.ACCESS_TRAIL_SOURCE_RECORD_NO);
                                String sourceRecordFk = (String) request.getAttribute(AccessTrailRequestIds.ACCESS_TRAIL_SOURCE_RECORD_FK);
                                if(StringUtils.isBlank(LogUtils.getPage()))
                                    LogUtils.setPage("RequestURI:"+request.getRequestURI());
                                recordAccessTrail(oasisUserId, osUser, ipAddress,
                                        subsystemCode, pageCode, method, sourceTableName, sourceRecordNo, sourceRecordFk);
                                LogUtils.setPage(null);

                            } else {
                                if (l.isLoggable(Level.FINE)) {
                                    l.logp(Level.FINE, getClass().getName(), "recordAccessTrail",
                                            "Do not record access trail because method is exclued. method:["
                                                    + method + "]");
                                }
                            }

                        } else {
                            if (l.isLoggable(Level.FINE)) {
                                l.logp(Level.FINE, getClass().getName(), "recordAccessTrail",
                                        "Do not record access trail because of page and system parameter setting. page:["
                                                + pageBean.getId() + "], access_trail_b:[" + pageBean.getAccessTrailB()
                                                + "], system parameter WEB_DFLT_PAGE_AUDIT:[" + defaultPageAudit + "]");
                            }
                        }

                    } else {
                        if (l.isLoggable(Level.FINE)) {
                            l.logp(Level.FINE, getClass().getName(), "recordAccessTrail",
                                    "Do not record access trail because pageBean is not found or pageId is null. request uri="
                                            + request.getRequestURI());
                        }
                    }

                } else {
                    if (l.isLoggable(Level.FINE)) {
                        l.logp(Level.FINE, getClass().getName(), "recordAccessTrail",
                                "Do not record access trail because the uri is excluded. uri:["
                                        + request.getRequestURI() + "]");
                    }
                }
            } else {
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "recordAccessTrail",
                            "Do not record access trail because there is error in request or it is an Ajax request.");
                }
            }
        } catch (Exception e) {
            l.logp(Level.SEVERE, getClass().getName(), "recordAccessTrail", "Fail to log access trail", e);
        }
    }

    /**
     * This method accepts the user activity information and saves it into database.
     *
     * @param oasisUserId
     * @param osUser          Operating System ID of the user that created the oracle connection.
     * @param ipAddress
     * @param subsystemCode
     * @param pageCode
     * @param method
     * @param sourceTableName
     * @param sourceRecordNo
     * @param sourceRecordFk
     * @param webSessionId
     */
    public void recordSessionInfo(String oasisUserId, String osUser, String ipAddress,
                                  String subsystemCode, String pageCode, String method,
                                  String sourceTableName, String sourceRecordNo, String sourceRecordFk,
                                  String webSessionId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "recordSessionInfo", new Object[]{oasisUserId, osUser,
                    ipAddress, subsystemCode, pageCode, method,
                    sourceTableName, sourceRecordNo, sourceRecordFk, webSessionId});
        }

        Record inputRecord = new Record();

        inputRecord.setFieldValue("oasisUserId", oasisUserId);
        inputRecord.setFieldValue("osUser", osUser);
        inputRecord.setFieldValue("ipAddress", ipAddress);
        inputRecord.setFieldValue("subsystemCode", subsystemCode);
        inputRecord.setFieldValue("pageCode", pageCode);
        inputRecord.setFieldValue("method", method);
        inputRecord.setFieldValue("sourceTableName", sourceTableName);
        inputRecord.setFieldValue("sourceRecordNo", sourceRecordNo);
        inputRecord.setFieldValue("sourceRecordId", sourceRecordFk);
        inputRecord.setFieldValue("webSessionId", webSessionId);

        getAccessTrailDAO().addSessionInfo(inputRecord);

        l.exiting(getClass().getName(), "recordSessionInfo");
    }

    /**
     * record access trail
     *
     * @param request
     */
    public void recordSessionInfo(HttpServletRequest request) {
        l.entering(getClass().getName(), "recordSessionInfo");
        try {
            Object error = request.getAttribute(IOasisAction.KEY_ERROR);
            Object validateError = request.getAttribute(IOasisAction.KEY_VALIDATION_ERROR);
            boolean isAjaxRequest = (YesNoFlag.getInstance(request.getParameter("__isAjaxRequest")).booleanValue()) ||
                                     request.getParameterMap().containsKey("javax.faces.source");
            if (error == null && validateError == null && !isAjaxRequest) {
                String pageCode = null;
                if (!isExcludeURLForRecordSessionInfo(request.getRequestURI())) {
                    // Get the pageId from the PageBean or from the request URI
                    pageCode = null;
                    PageBean pageBean = (PageBean) request.getAttribute(IOasisAction.KEY_PAGEBEAN);
                    if (pageBean != null && pageBean.getId() != null) {
                        pageCode = pageBean.getId();
                    } else {
                        String requestURI = request.getRequestURI();
                        if (requestURI.indexOf("javax.faces.resource") < 0 &&
                            (requestURI.endsWith("dti") || requestURI.endsWith("do") || requestURI.endsWith("jsp"))) {
                            // Skip requests for ancillary requests, such as images
                            pageCode = requestURI.substring(requestURI.lastIndexOf("/") + 1, requestURI.lastIndexOf("."));
                            if (pageCode.length() > 30) {
                                pageCode = pageCode.substring(0, 30);
                            }
                        }
                        l.logp(Level.FINE, getClass().getName(), "recordSessionInfo", "requestURI = " + requestURI + "   pageCode = " + pageCode);
                    }
                }

                if (pageCode != null) {
                    String method = (String) request.getAttribute(AccessTrailRequestIds.ACCESS_TRAIL_METHOD);
                    if (method == null) {
                        method = request.getParameter(RequestIds.PROCESS);
                        if (StringUtils.isBlank(method)) {
                            method = "unspecified";
                        }
                    }

                    String oasisUserId = ActionHelper.getCurrentUser(request).getUserId();
                    String osUser = System.getProperty("user.name");
                    String ipAddress = ActionHelper.getOriginalIP(request);
                    String subsystemCode = ApplicationContext.getInstance().getProperty("applicationId", "");


                    initializeForRequest(request);
                    String sourceTableName = (String) request.getAttribute(AccessTrailRequestIds.ACCESS_TRAIL_SOURCE_TABLE_NAME);
                    String sourceRecordNo = (String) request.getAttribute(AccessTrailRequestIds.ACCESS_TRAIL_SOURCE_RECORD_NO);
                    String sourceRecordFk = (String) request.getAttribute(AccessTrailRequestIds.ACCESS_TRAIL_SOURCE_RECORD_FK);
                    String webSessionId = ActionHelper.getJSessionId(request);

                    if(webSessionId != null && !StringUtils.isBlank(oasisUserId))
                        if(StringUtils.isBlank(LogUtils.getPage()))
                            LogUtils.setPage("RequestURI:"+request.getRequestURI());
                        recordSessionInfo(oasisUserId, osUser, ipAddress,
                                subsystemCode, pageCode, method, sourceTableName, sourceRecordNo, sourceRecordFk, webSessionId);
                        LogUtils.setPage(null);
                } else {
                    l.logp(Level.FINE, getClass().getName(), "recordSessionInfo",
                            "Do not record session info because pageBean is not found or pageId is null. request uri="
                                    + request.getRequestURI());
                }

            } else {
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "recordSessionInfo",
                            "Do not record session info because there is error in request or it is an Ajax request.");
                }
            }
        } catch (Exception e) {
            l.logp(Level.SEVERE, getClass().getName(), "recordSessionInfo", "Fail to log session info", e);
        }
    }

    /**
     * record access trail
     *
     * @param request
     */
    public void processAccessInfo(HttpServletRequest request) {
        recordSessionInfo(request);
        recordAccessTrail(request);
    }


    /**
     * This method get last login date/time.
     *
     * @param oasisUserId
     * @param webSessionId
     */
    public String getPriorLoginTimestamp(String oasisUserId, String webSessionId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPriorLoginTimestamp", new Object[]{oasisUserId, webSessionId});
        }

        Record inputRecord = new Record();

        inputRecord.setFieldValue("oasisUserId", oasisUserId);
        inputRecord.setFieldValue("webSessionId", webSessionId);

        String returnValue = getAccessTrailDAO().getPriorLoginTimestamp(inputRecord);

        l.exiting(getClass().getName(), "getPriorLoginTimestamp");

        return returnValue;
    }

    /**
     * load latest active users
     *
     * @param record
     * @return
     */
    public RecordSet loadAllActiveUsers(Record record) {
        String methodName = "loadAllActiveUsers";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllActiveUsers", new Object[]{record});
        }
        RecordSet rs = getAccessTrailDAO().loadAllActiveUsers(record, AddSelectIndLoadProcessor.getInstance());
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllActiveUsers", rs);
        }
        return rs;
    }


    /**
     * verify config.
     */
    public void verifyConfig() {
        if (getAccessTrailDAO() == null)
            throw new ConfigurationException("The required property 'accessTrailDAO' is missing.");
    }

    /**
     * set excluded urls
     *
     * @param excludes A comma separated list of regular expressions
     */
    protected static void setExcludeURLs(String excludes) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(AccessTrailManagerImpl.class.getName(), "setExcludeURLs", new Object[]{excludes});
        }

        if (!StringUtils.isBlank(excludes)) {
            String[] excludeURLs = excludes.split(",");
            for (int i = 0; i < excludeURLs.length; i++) {
                String excludeURL = excludeURLs[i].trim();
                if (excludeURL.length() > 0) {
                    c_excludeURLs.add(excludeURL);
                    l.logp(Level.FINE, AccessTrailManagerImpl.class.getName(), "setExcludeURLs", "added: " + excludeURL + " to excludeURLs");
                    if (excludeURL.indexOf("eAdmin/SysInfo") < 0 &&
                        excludeURL.indexOf("eAdmin/ConfigProp") < 0 &&
                        excludeURL.indexOf("eAdmin/Security") < 0 ) {
                        c_excludeURLsForRecordSessionInfo.add(excludeURL);
                        l.logp(Level.FINE, AccessTrailManagerImpl.class.getName(), "setExcludeURLs", "added: " + excludeURL + " to excludeURLsForRecordSessionInfo");
                    }
                }
            }
        }
        l.exiting(AccessTrailManagerImpl.class.getName(), "setExcludeURLs");
    }

    /**
     * set excluded methods
     *
     * @param excludes
     */
    protected static void setExcludeMethods(String excludes) {
        if (!StringUtils.isBlank(excludes)) {
            m_excludeMethods = excludes.split(",");
            for (int i = 0; i < m_excludeMethods.length; i++) {
                m_excludeMethods[i] = m_excludeMethods[i].trim();
            }
        }
    }

    /**
     * check whether the uri is excluded
     *
     * @param uri
     * @return
     */
    protected static boolean isExcludeURL(String uri) {
        boolean excluded = false;
        if (c_excludeURLs != null && c_excludeURLs.size() > 0) {
            for (String excludes : c_excludeURLs) {
                if (uri.matches(excludes)) {
                    excluded = true;
                    break;
                }
            }
        }
        return excluded;
    }

    /**
     * check whether the uri is excluded
     *
     * @param uri
     * @return
     */
    protected static boolean isExcludeURLForRecordSessionInfo(String uri) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(AccessTrailManagerImpl.class.getName(), "isExcludeURLForRecordSessionInfo", new Object[]{uri});
        }

        boolean excluded = false;
        if (c_excludeURLsForRecordSessionInfo != null && c_excludeURLsForRecordSessionInfo.size() > 0) {
            for (String excludes : c_excludeURLsForRecordSessionInfo) {
                if (uri.matches(excludes)) {
                    excluded = true;
                    break;
                }
            }
        }
        l.logp(Level.FINE, AccessTrailManagerImpl.class.getName(), "isExcludeURLForRecordSessionInfo", "The following uri " + (excluded ? "IS " : "IS NOT ") + "excluded for recording session info: " + uri);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(AccessTrailManagerImpl.class.getName(), "isExcludeURLForRecordSessionInfo", excluded);
        }
        return excluded;
    }


    /**
     * check whether the method is excluded
     *
     * @param method
     * @return
     */
    protected static boolean isExcludeMethod(String method) {
        boolean excluded = false;
        if (m_excludeMethods != null && m_excludeMethods.length > 0) {
            for (int i = 0; i < m_excludeMethods.length; i++) {
                String excludes = m_excludeMethods[i];
                if (method.matches(excludes)) {
                    excluded = true;
                    break;
                }
            }
        }
        return excluded;
    }


    public AccessTrailDAO getAccessTrailDAO() {
        return m_accessTrailDAO;
    }

    public void setAccessTrailDAO(AccessTrailDAO accessTrailDAO) {
        this.m_accessTrailDAO = accessTrailDAO;
    }

    private AccessTrailDAO m_accessTrailDAO;

    private static ArrayList<String> c_excludeURLs = new ArrayList<String>();
    private static ArrayList<String> c_excludeURLsForRecordSessionInfo = new ArrayList<String>();

    private static String[] m_excludeMethods;

    /**
     * The request header name that holds the forwarded chain information.
     */
    private static final String FORWARDED_FOR_HEADER_NAME = "X-FORWARDED-FOR";

    private static final Logger l = LogUtils.getLogger(AccessTrailManagerImpl.class);
}
