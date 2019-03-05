
<%--
    Description: Standard dti header including top and left navigational
    menu elements.

    Instructions for creating your own JSP.
    1. Include the header.jsp at the beginning of your JSP
    2. Create a single FORM element after the include.
       ****IMPORTANT**** DO NOT USE THE STRUTS HTML:FORM AND DO NOT
       **** CLOSE THE FORM ELEMENT IN YOUR JSP!!!!!
       *** REMEMBER:
       *** NO <HTML:FORM
       *** NO </FORM>!!!!!
       *** The footer.jsp has a mandatory hidden form field and then
       *** closes the form for you

    3. Provide your content.
       It should be in the context of an existing table element.
       Create and close your own row, cell & embedded table elements only.
    3. Include footer.jsp at the end of your JSP

    Sample: your.jsp
    ---------------------------------
    <%@ include file="header.jsp" %>
    <form name="myform" method="post" action="foo.do">
    <tr>
    <td>My content</td>
    </tr>
    <jsp:include page="footer.jsp" />
    ---------------------------------
    Author: jbe
    Date: Oct 15, 2003


    Revision Date Revised By Description
    ---------------------------------------------------------------------------
    6/24/2004   jbe         Added doMenuItem and handling
    9/13/2005   jbe         Use type='text/javascript' instead of language='javascript'
    01/11/2006	gxc	        Issue 54932 - Added label.header.database
                            instead of hardcoding
    07/18/2006	sxm	    Change the session timeout factor from 1000 to 1010
    11/01/2006	mdz	        Add IE7 support
    11/02/2006  GCC         Added include of NumberFormatUtil.js.
    01/23/2007  lmm         Reformated UI with tables
    01/23/2007  wer         Changed appPath to corePath, using the Module.getCorePath();
                            Added javascript functions to get the AppPath, CorePath, CSPath, CISPath;
                            Added baseOnLoad() and baseOnUnload() default handlers, moving the onUnload logic to the baseOnLoad()
    02/01/2007  wer         Added function to get the SELECT option label.
    02/07/2007  wer         Added Support for multiple Message Resource Files
    02/08/2007  GCC         Re-worked logic dealing with user name variable and
                            display of database info, left NAV, and welcome
                            message.
    02/28/2007  jmp         Added commonIsOkToChangePages and commonGetQueryMenuString to doMenuItem
    03/09/2007  sxm         Added import of org.apache.struts.xxx per Bill Reeder's request
    07/31/2007  bhong       Move timeoutObject logic to separate "sessionTimeout.js", added codes to
                            get session Max Inactive Interval and configurate property values for
                            session time out logic.
    12/14/2007  Kenney      Issue 77710 - Added the following infomation on OASIS eClaims logo in the upper left corner,
                                            including build number, User ID for the session and database connection
    01/10/2007  James       Add fieldDep.jsp
    04/09/2008   wer        Enhanced to support configuring a dbPoolId for a role associated with a user or it's group, and removed passing of DBPOOLID as request parameter
    06/23/2008  fcb         83145: changed logic for viewHelp.
    08/208/2008 Larry       Add !isADUser to check if user is AD user.
    10/31/2008  fcb         87812: viewHelp: Added logic to handle cases when Common Services is
                            not set up and it does not appear in the top navigation menu.
    04/13/2009  mxg         82494: Added include, formatutils.jsp, to handle Date Format Internationalization
    04/29/2009  yhyang      91531: Add the parameter "headerHidden" to hide the header and menu.
    05/11/2009  Fred        Exclude oweb:rules for rams application
    05/11/2008  mlm       Update for logo url reference
    10/07/2009  mgitelm     98004: Changed browser indentificaton
    01/18/2010  James       Issue#101408 Record user access trail
    03/03/2010  James       Issue#104605 For a popup window that is configured to log access to the
                            oasis_access_trail table, a log record is added for the parent window as well
                            as the popup window. The printout only contains data for the popup window,
                            not the parent window.
    04/01/2010  kshen       Added codes to support email text field.
    04/08/2010  kshen       Chenck if SysParmProvider is available before get values for mail client type.
    04/27/2010  kshen       Added no email address message.
    05/12/2010  fcb         107461: baseOnLogOut() called for Logoff button.
    08/30/2010  wfu         109875: Import LocaleUtils to use currency symbol for multiple symbol support.
    10/11/2010  wfu         111776: Move several variables to headerCommonInclude.jsp.
    10/15/2010  wfu         109875 - Change to support Chinese Date format.
    10/20/2010  tzhao       109875 - Move the inclusion of xmlproc.js before <%@ include file="headerCommonInclude.jsp" %>.
                                     Because the definition of the currency_symbol has been moved to xmlproc.js.
    10/19/2010  gzeng       112909 - Set isScrolling to false for special pages.
    11/09/2010  syang       114193 - Defined showTopnavMenu to show/hide topnavMenu.
    12/10/2010  James       115406 - Add support for loading custom styles in a customStyles.css file
    01/26/2011  clm         113498 - Add onKeyDown event handler baseOnBodyKeyDown to the body
    02/16/2011  Blake       Modified for issue 112690:Make Identifier Prominent .
    04/14/2011  mlm         Added obr.js reference.
    09/20/2011  bhong       118066 - Move the call to isADUser on the top.
                                     Allow change password page to be configured with external URL.
    09/20/2011  mxg         Issue #100716: Display Type FORMATTEDNUMBER: Added jQuery Plugins 
    09/21/2011  bhong       118066 - Made changes to hide change password link when isADuser is true but external URL is empty.
    09/30/2011  bhong       Removed call to initializOasisUser
    11/08/2011  bhong       112837 - Added logics to support standardize processing dialog.
                                     Moved showProcessingImgIndicator and hideProcessingImgIndicator to divpopup.js
    12/09/2011  jxgu        127611 - add html id on tables for page
    08/03/2012  jxgu        136142 - Add the dbPoolId to applicationTitle in header if a role-based data source is
                                     in use and the environmentName custom property is defined.
    01/15/2012  kshen       140326. Added system parameter to control if open the notes window in a new popup window.
    05/07/2013  jxgu        140985 - use jquery dialog
    08/30/2013  jxgu        Issue#147685 upgrade jquery to 1.7.2
    05/15/2015  Elvin       Issue 163132: Replace FCKEditor with latest version of CKEditor
    09/17/2015  Parker      Issue#165637 - Use ThreadLocal to make SimpleDateFormat thread safe.
    10/17/2015  Parker      Issue#166802 Fix the javascript error 'AJAXRequest is undefined'.
    06/17/2016  iwang       177392 - Modify function jumpTo to check the destinationContextValue. If it contains the
                                     ':', then parse it to policy number and policy original code. After that, append
                                     the result to the jump URL.
    08/24/2016  Elvin       Issue 177515: add velocity URL, enhance jumpTo to include velocity/cm policies
    09/28/2016  ylu         Issue 179256: match current menuitem at page level, prior to applicationId
    10/18/2016  ddai        Issue 180408: Verify if the index is -1 before substring.
    03/31/2017  kshen       Issue 184520. Move the DIV about the space before global menu to a seperate TR for recent IE updates
    09/11/2017  kshen       Grid replacement. Import the OasisTags common.js. Import the jqx.oasis.css for change
                            the style of jqxGrid to be similar with the current grid in oasis.
    09/21/2017  kshen       Grid replacemnet. Removed jqxDateDisplayFormat and jqxDateTimeDisplayFormat.
                            Added jqxgrid.aggregates.js
    10/12/2017  kshen       Grid replacement: pass event object to baseOnXxx methods for supporting firefox.
    04/13/2018  cesar       192259 - send keypress event to dti.stringutils.replaceTypedChar()
	06/08/2018  dpang       Issue 109161: Change refreshApp for CIS refactor.
    07/31/2018  htwang      Issue 191837: fix the bug in isIEBrowser() that always returned true as long as jqxGrid used.
    11/13/2018  wreeder     196147 - Include jqxgrid-storage.js to support savestate/loadstate methods
                                   - Change loading-div DIV to support not disabling the page while loading, and to add a blue border to make it stand out more
    ---------------------------------------------------------------------------
    (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ page import="dti.oasis.app.ApplicationContext,
                                 dti.oasis.codelookupmgr.CodeLookupManager,
                                 dti.oasis.http.Module,
                                 dti.oasis.security.Authenticator,
                                 dti.oasis.struts.ActionHelper" errorPage="/core/ErrorPage.jsp"%>
<%@ page import="dti.oasis.util.DatabaseUtils"%>
<%@ page import="dti.oasis.util.OasisUser"%>
<%@ page import="dti.oasis.util.LocaleUtils"%>
<%@ page import="javax.servlet.jsp.PageContext"%>
<%@ page import="java.text.DateFormat"%>
<%@ page import="java.util.Date"%>
<%@ page import="org.apache.struts.taglib.html.Constants"%>
<%@ page import="org.apache.struts.Globals"%>
<%@ page import="dti.oasis.struts.IOasisAction" %>
<%@ page import="dti.oasis.util.StringUtils" %>
<%@ page import="dti.oasis.messagemgr.MessageManager" %>
<%@ page import="dti.oasis.security.WebLogicSecurity" %>
<%@ page import="dti.oasis.busobjs.YesNoFlag" %>
<%@ page import="dti.oasis.util.BrowserUtils" %>
<%@ page import="dti.oasis.filter.CharacterEncodingFilter" %>
<%@ page import="dti.oasis.accesstrailmgr.AccessTrailRequestIds" %>
<%@ page import="dti.oasis.accesstrailmgr.AccessTrailManager" %>
<%@ page import="dti.oasis.tags.OasisTagHelper" %>
<%@ page import="java.text.MessageFormat" %>
<%@ page import="dti.oasis.request.RequestStorageManager" %>
<%@ page import="dti.oasis.session.pageviewstate.PageViewStateManager" %>

<%@ page import="dti.oasis.util.LogUtils" %>
<%@ page import="dti.oasis.util.DateUtils" %>
<%@ page import="java.util.logging.Level" %>
<%@ page import="java.util.logging.Logger" %>
<%@ page import="java.util.List" %>
<%@ page import="org.apache.struts.taglib.TagUtils" %>
<%@ page import="dti.oasis.http.*" %>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="fmt" uri="/WEB-INF/fmt.tld" %>
<%@ taglib prefix="c" uri="/WEB-INF/c.tld"  %>


<jsp:useBean id="pageBean" class="dti.oasis.util.PageBean" scope="request"/>
<jsp:useBean id="userBean" class="dti.oasis.util.OasisUser" scope="session"/>
<%!
    Logger lHeaderJsp = LogUtils.enterLog(getClass(), "header.jsp");
    boolean isLoginPage = false;

    public String getMaintenanceMessage(HttpServletRequest request) {
        String maintenanceMsg = "";
        try {
            maintenanceMsg = SysParmProvider.getInstance().getSysParm(request, "MAINTENANCE_MESSAGE");
        } catch (Exception e){
            if(!isLoginPage) {
                lHeaderJsp.logp(Level.WARNING, getClass().getName(), "getMaintenanceMessage", "UNKNOWN ERROR " + e.getMessage());
                e.printStackTrace();
            }
        }

        if (StringUtils.isBlank(maintenanceMsg)) {
            String dbPoolId = getDbPoolId(request);
            if (!StringUtils.isBlank(dbPoolId)) {
                try {
                    maintenanceMsg = MessageManager.getInstance().formatMessage("label.maintenanceMsg." + dbPoolId);
                } catch (Exception e) {
                    // Ignore if not found
                }
            }
            if (StringUtils.isBlank(maintenanceMsg)) {
                try {
                    maintenanceMsg = MessageManager.getInstance().formatMessage("label.maintenanceMsg");
                } catch (Exception e) {
                    // Ignore if not found
                }
            }
        }
        return maintenanceMsg;
    }

    public String getDbPoolId(HttpServletRequest request) {
        String dbid = ActionHelper.getDbPoolId(request);
        if (dbid.startsWith("jdbc/"))
            dbid = dbid.substring(5);
        return dbid;
    }

    public String getPageViewStateId(HttpServletRequest request){
        String pageViewStateId = "";
        if (request.getAttribute(dti.oasis.http.RequestIds.CACHE_ID_FOR_PAGE_VIEW) != null) {
            pageViewStateId = (String) request.getAttribute(dti.oasis.http.RequestIds.CACHE_ID_FOR_PAGE_VIEW);
            RequestStorageManager.getInstance().set(PageViewStateManager.TERMINATE_PAGE_VIEW_STATE, Boolean.FALSE);
        }
        return pageViewStateId;
    }

    public Long getLastRefreshTime(){
        Date lastRefreshTime = ApplicationContext.getInstance().getLastRefreshTime();
        long longLastRefreshTime;
        if (lastRefreshTime != null) {
            longLastRefreshTime = lastRefreshTime.getTime();
        } else {
            longLastRefreshTime = new Date().getTime();
        }
        return longLastRefreshTime;
    }
%>
<c:if test= "${isLoginPage == true}" >
    <% isLoginPage = true;
        lHeaderJsp.logp(Level.WARNING, getClass().getName(), "jsp_service_method", "isLoginPage = " + isLoginPage);
    %>
</c:if>
<%

    ApplicationContext.getInstance().exposeMessageSourceForJstl(application, request);

    String encoding = ApplicationContext.getInstance().getProperty(CharacterEncodingFilter.CHARACTER_ENCODING_DEFAULT);

    Authenticator.validateRequest(request, response);
    response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
    response.setHeader("Cache-control", "no-store, no-cache, must-revalidate");
    response.addHeader("Cache-Control", "post-check=0, pre-check=0");
    response.setHeader("Content-Type", "text/html; charset=" + encoding);
    response.setHeader("Pragma", "no-cache");
    String appPath = request.getContextPath();
    String corePath = Module.getCorePath(request);
    String csPath = Module.getCSPath(request);
    String cisPath = Module.getCISPath(request);
    String applicationHelpURL = "";
    String envPath = Module.getEnvPath(request);
    String velocityApplicationURL = ApplicationContext.getInstance().getProperty("velocity.application.url", "");
    String viewVelocityPolicyURL = ApplicationContext.getInstance().getProperty("velocity.viewPolicy.url", "");

    boolean disallowPassword = YesNoFlag.getInstance(ApplicationContext.getInstance().getProperty(IOasisAction.KEY_ENVDISALLOWPASSWORDCHANGE, "N")).booleanValue();
    boolean subscribeEvents = YesNoFlag.getInstance(ApplicationContext.getInstance().getProperty(IOasisAction.KEY_ENVSUBSCRIBEEVENTS, "N")).booleanValue();
    boolean showUserSettings = (subscribeEvents || !disallowPassword);

    String passwordChangeJSP = corePath + "/" + Authenticator.getPasswordChangeJSP();
    boolean isADUser = WebLogicSecurity.getInstance().isADUser(userBean.getUserId(),request);
    if (isADUser) {
        String externalUrl = ApplicationContext.getInstance().getProperty("external.password.change.URL");
        if (!StringUtils.isBlank(externalUrl)) {
            passwordChangeJSP= MessageFormat.format(externalUrl, new String[]{userBean.getUserId()});
        } else {
            // Hide change password URL if the external URL has not been configured for AD user.
            showUserSettings = false;
        }
    }
    if (Authenticator.isPasswordExpired(userBean)) {
        request.setAttribute(IOasisAction.PARM_URL, "index.jsp");
        request.getRequestDispatcher(passwordChangeJSP).forward(request, response);
        return;
    }

    Date last = userBean.getLastLoggedIn();
    String dtLogged = "&nbsp;";
    if (last != null) {
        String usDateTimeString= DateUtils.formatDateTimeUSWithTimeZone(last);
        dtLogged = MessageManager.getInstance().formatMessage("label.header.page.lastAccessed", new String[]{usDateTimeString});
    }
    request.setAttribute(dti.oasis.http.RequestIds.USE_JQX_GRID, OasisTagHelper.isUseJqxGrid(pageContext));

    boolean useJqxGrid = (boolean) request.getAttribute(dti.oasis.http.RequestIds.USE_JQX_GRID);

    String browser = request.getHeader("User-Agent");
    boolean isIEBrowser = BrowserUtils.isIE11(browser);
    boolean goodBrowser = BrowserUtils.isIE11(browser) || useJqxGrid;
    String display = (goodBrowser) ? "block;" : "none;";
    String cssClass = "";
    if(useJqxGrid) {
        display = "table";
        cssClass = goodBrowser ? "" : "dti-hide";
    }

    String dbTitle = "&nbsp;";

    String environmentName = "";
    try {
        environmentName = SysParmProvider.getInstance().getSysParm(request, "ENVIRONMENTNAME");
    } catch (Exception e){
        if(isLoginPage)
            lHeaderJsp.logp(Level.WARNING, getClass().getName(), "jsp_service_method", "USER NOT LOGGED IN YET "+e.getMessage());
        else {
            lHeaderJsp.logp(Level.WARNING, getClass().getName(), "jsp_service_method", "UNKNOWN ERROR " + e.getMessage());
            e.printStackTrace();
        }
    }
    boolean isEnvironmentNameOverride = false;
    String environmentNameBase = ApplicationContext.getInstance().getProperty("environmentName", "");
    if (StringUtils.isBlank(environmentName))
        environmentName = environmentNameBase;
    else
        isEnvironmentNameOverride = true;

    String applicationTitle = (String)request.getAttribute(dti.oasis.http.RequestIds.APPLICATION_TITLE);
    if (StringUtils.isBlank(applicationTitle))
        applicationTitle = ApplicationContext.getInstance().getProperty("applicationTitle", " ");
    if(isEnvironmentNameOverride && !StringUtils.isBlank(environmentNameBase)) {
        applicationTitle = applicationTitle.replace(environmentNameBase, environmentName);
    } else {
        applicationTitle = environmentName;
    }
    String strLogoTipInfo = MessageManager.getInstance().formatMessage("label.header.page.version", new String[]{applicationTitle});
    String userName = "";
    if (request.getUserPrincipal() != null) {
        // User is logged in, meaning this is not being included from within the login.jsp page
        userName = userBean.getUserName();

        String dbid = getDbPoolId(request);

        // Update the Logo Tip Info with the User and DB info
        strLogoTipInfo = MessageManager.getInstance().formatMessage("label.header.page.logoTipInfo",
                new String[]{applicationTitle, userBean.getUserId(), (String)session.getAttribute(IOasisAction.KEY_PRIOR_LOGIN_TS), ("&nbsp;".equals(dbTitle) || StringUtils.isBlank(dbTitle) ? dbid : dbTitle)});
    }

    boolean showTopnavMenu = YesNoFlag.getInstance(ApplicationContext.getInstance().getProperty("showTopnavMenu", "Y")).booleanValue();

    String UIStyleEdition = (String) request.getAttribute("UIStyleEdition");
    if (UIStyleEdition == null) {
        UIStyleEdition = ApplicationContext.getInstance().getProperty("UIStyleEdition", "0");
        request.setAttribute("UIStyleEdition", UIStyleEdition);
        pageContext.setAttribute("UIStyleEdition", UIStyleEdition, pageContext.APPLICATION_SCOPE);
    }

    String productLogoConfigKey = "productLogo" + (UIStyleEdition.equalsIgnoreCase("0") || UIStyleEdition.equalsIgnoreCase("1") ? "" : UIStyleEdition.trim());
    String productLogoImage = (String)request.getAttribute(dti.oasis.http.RequestIds.PRODUCT_LOGO_2);
    if (StringUtils.isBlank(productLogoImage))
        productLogoImage = ApplicationContext.getInstance().getProperty(productLogoConfigKey, "productlogo.jpg");
    String productLogoImageFile = Module.getRelativePath(request, productLogoImage);
//    String productLogoImageFile = Module.getRelativePath(request, ApplicationContext.getInstance().getProperty(productLogoConfigKey, "productlogo.jpg"));
    productLogoImageFile += "?build.number=" + ApplicationContext.getInstance().getProperty(IOasisAction.KEY_BUILD_NUMBER);

    boolean isEnvSpecified = environmentName.equals("") ? false : true;
    if (isEnvSpecified && !isLoginPage && !StringUtils.isBlank(ActionHelper.getDBPoolIdRoleName(request))) {
        String dbPoolId = getDbPoolId(request);
        // Only add the dbPoolId if the current applicationTitle does not contain it
        if (!applicationTitle.toUpperCase().contains(dbPoolId.toUpperCase())) {
            applicationTitle += " " + dbPoolId;
        }
    }

    // Add Maintenance Message
    String maintenanceMsg = getMaintenanceMessage(request);
    System.out.println("maintenanceMsg = " + maintenanceMsg);
    if (!StringUtils.isBlank(maintenanceMsg)) {
        applicationTitle += "<br>" + maintenanceMsg;
    }
    System.out.println("applicationTitle = " + applicationTitle);

    String dbPoolIdToRenderInJS = "";

    boolean isDivPopupEnabled = false;
    if (ApplicationContext.getInstance().hasProperty("divpopup.enabled")) {
        isDivPopupEnabled = Boolean.valueOf(ApplicationContext.getInstance().getProperty("divpopup.enabled")).booleanValue();
    }
    if (request.getAttribute(IOasisAction.KEY_IS_DIV_POPUP_ENABLED) != null) {
        isDivPopupEnabled = Boolean.valueOf(request.getAttribute(IOasisAction.KEY_IS_DIV_POPUP_ENABLED).toString()).booleanValue();
    }

    boolean isMultiGridSupportEnabled = false;
    if (ApplicationContext.getInstance().hasProperty("multigridsupport.enabled")) {
        isMultiGridSupportEnabled = Boolean.valueOf(ApplicationContext.getInstance().getProperty("multigridsupport.enabled")).booleanValue();
    }
    if (request.getAttribute(IOasisAction.KEY_IS_MULTI_GRID_SUPPORT_ENABLED) != null) {
        isMultiGridSupportEnabled = Boolean.valueOf(request.getAttribute(IOasisAction.KEY_IS_MULTI_GRID_SUPPORT_ENABLED).toString()).booleanValue();
    }

    String selectOptionLabel = CodeLookupManager.getInstance().getSelectOptionLabel();

    // Initialize Messages for JavaScript to use
    String sessionTimeoutConfirm = MessageManager.getInstance().formatMessage(
            "label.sessionTimeout.confirm");

    String globalSearchDefaultHint = ApplicationContext.getInstance().getProperty(IOasisAction.KEY_IS_GLOBAL_SEARCH_DEF_HINT, "");
    String globalSearchVisibility = ApplicationContext.getInstance().getProperty(IOasisAction.KEY_IS_GLOBAL_SEARCH_VISIBLE, "false");
    String globalSearchDefaultUrl = ApplicationContext.getInstance().getProperty(IOasisAction.KEY_GLOBAL_SEARCH_FORWARD_PAGE_URL, "");
    globalSearchVisibility = (StringUtils.isBlank(globalSearchVisibility) ? "false" : globalSearchVisibility);
    pageContext.setAttribute("globalSearchVisibility", globalSearchVisibility, pageContext.APPLICATION_SCOPE);

    boolean globalMenuVisibility = Boolean.valueOf(ApplicationContext.getInstance().getProperty(IOasisAction.KEY_IS_GLOBAL_MENU_VISIBLE, "true")).booleanValue();
    request.setAttribute("globalMenuVisibility", String.valueOf(globalMenuVisibility));

    String applicationId = (String)request.getAttribute(dti.oasis.http.RequestIds.APPLICATION_ID);
    if (StringUtils.isBlank(applicationId))
        applicationId = ApplicationContext.getInstance().getProperty("applicationId", "");
    applicationHelpURL = ApplicationContext.getInstance().getProperty("helpURL", "");
    boolean openTopNavInNewWindow = Boolean.valueOf(ApplicationContext.getInstance().getProperty("openTopNavInNewWindow", "false")).booleanValue();

    String topNavURLs = "";
    Iterator topNavIt = pageBean.getTopNavMenu().iterator();
    while (topNavIt.hasNext()) {
        MenuBean topNav = (MenuBean) topNavIt.next();
        topNavURLs += (StringUtils.isBlank(topNavURLs) ? "" : "~^~") + topNav.getId() + "^" + topNav.getUrl();
    }

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<c:if test="${useJqxGrid}">
    <meta name="format-detection" content="telephone=no"/>
</c:if>
<c:if test="${!useJqxGrid}">
    <meta http-equiv="X-UA-Compatible" content="IE=8">
</c:if>
<script type="text/javascript">
    var useJqxGrid = false;
    <c:if test="${useJqxGrid}">
        useJqxGrid = true;
    </c:if>
</script>
<script type="text/javascript">
    function getUIStyleEdition() {
        return '<%= UIStyleEdition %>';
    }

    function isDivPopupEnabled() {
        return '<%= isDivPopupEnabled%>' ;
    }

    function getSelectOptionLabel() {
        return '<%= selectOptionLabel%>' ;
    }

    function getAppPath() {
        return '<%=appPath%>';
    }
    function getCorePath() {
        return '<%=corePath%>';
    }
    function getCSPath() {
        return '<%=csPath%>';
    }
    function getCISPath() {
        return '<%=cisPath%>';
    }

    function getEnvPath() {
        return '<%=envPath%>';
    }

    function getVelocityAppPath() {
        return '<%=velocityApplicationURL%>';
    }

    function getViewVelocityPolicyURL() {
        return '<%=viewVelocityPolicyURL%>';
    }

    function getUIStyleEdition() {
        return '<%= UIStyleEdition %>';
    }

    function isDivPopupEnabled() {
        return '<%= isDivPopupEnabled%>' ;
    }

    function isIEBrowser(){
        var result = '<%=isIEBrowser%>';
        return result;
    }

    function getIEVersion(){
        var result = '<%=BrowserUtils.getIEVersion(browser)%>';
        return result;
    }

    function xmlhttpNativeCheck() {
        var success = true;

        if(isIEBrowser()){
            if (!window.XMLHttpRequest) {//Fails ie8 test
                success = false;
            }
            if(success) {
                try {
                    var testUrl = getCorePath() + "/sessionKeepAlive.jsp" + "?date=" + new Date();
                    $.get(testUrl, function(responseText) {
//                        alert('Response XXX: '+responseText);
                    });
                } catch (e){
                    success = false;
                }
            }
        }

        if(!success){
            var hiddenDiv = document.getElementById("ajaxDisabled");
            if(useJqxGrid)
                hideShowElementByClassName(hiddenDiv, false);
            else
                hiddenDiv.style.display = "block";
        }
    }

    function getPageViewStateId() {
        return '<%= getPageViewStateId(request) %>';
    }

    var pageCode = "<%= pageBean.getId()==null ? "" : pageBean.getId() %>";

    var pageTitle = "<%= pageBean.getTitle()==null ? "" : pageBean.getTitle().replace("\"", "\\\"") %>";
</script>
<% if (corePath.endsWith("/core")) { %>
<jsp:include page="/core/headerVariables.jsp" />
<% } else {%>
<jsp:include page="/headerVariables.jsp" />
<%}%>
<link rel="shortcut icon" href="<%=corePath%>/images/logo-ftr.gif" />
<script type="text/javascript" src="<%=corePath%>/js/jquery-1.11.2.min.js"></script>
<script type="text/javascript" src="<%=corePath%>/js/globalize.js"></script>
<script type="text/javascript" src="<%=corePath%>/js/jquery-ui-1.10.3.js"></script>
<script type="text/javascript" src="<%=corePath%>/js/jMenu.jquery.js"></script>
<script type="text/javascript" src="<%=corePath%>/js/jshashtable-2.1.js"></script>
<script type="text/javascript" src="<%=corePath%>/js/jquery.numberformatter-1.2.2.js"></script>
<script type="text/javascript" src="<%=corePath%>/js/utils.xb.core.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=corePath%>/js/utils.xb.page.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=corePath%>/js/utils.xb.dataIsland.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=corePath%>/js/utils.xb.grid.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=corePath%>/js/utils.xb.filter.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=corePath%>/js/scriptlib.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=corePath%>/js/gui.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=corePath%>/js/asynchttp.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=corePath%>/js/edits.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=corePath%>/js/common.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<c:if test="${useJqxGrid}">
<script type="text/javascript" src="<%=corePath%>/js/edits.xb.adaptor.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=corePath%>/js/edits.gridReplacement.adaptor.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
</c:if>
<script type="text/javascript" src="<%=corePath%>/js/xmlproc.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<c:if test="${useJqxGrid}">
<script type="text/javascript" src="<%=corePath%>/js/xmlproc.gridReplacement.adaptor.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
</c:if>

<%@ include file="headerCommonInclude.jsp" %>
<script type="text/javascript" src="<%=corePath%>/js/validation.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=corePath%>/js/NumberFormatUtil.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=corePath%>/js/sessionTimeout.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=corePath%>/js/divPopup.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=corePath%>/js/viewSpecialConditionWarning.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=corePath%>/js/oasisMailUtil.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=corePath%>/js/obr.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<!--<script type="text/javascript" src="<%=corePath%>/js/var.js"></script>-->
<!-- CKEditor -->
<script type="text/javascript" src="<%=corePath%>/ckeditor/ckeditor.js"></script>

<c:if test="${useJqxGrid}">
    <link rel="stylesheet" href="<%=corePath%>/lib/js/jqwidgets-ver4.5.0/styles/jqx.base.css" type="text/css" />
    <script type="text/javascript" src="<%=corePath%>/lib/js/jqwidgets-ver4.5.0/jqxcore.js"></script>
    <script type="text/javascript" src="<%=corePath%>/lib/js/jqwidgets-ver4.5.0/jqxinput.js"></script>
    <script type="text/javascript" src="<%=corePath%>/lib/js/jqwidgets-ver4.5.0/jqxnumberinput.js"></script>
    <script type="text/javascript" src="<%=corePath%>/lib/js/jqwidgets-ver4.5.0/jqxcheckbox.js"></script>
    <script type="text/javascript" src="<%=corePath%>/lib/js/jqwidgets-ver4.5.0/jqxcalendar.js"></script>
    <script type="text/javascript" src="<%=corePath%>/lib/js/jqwidgets-ver4.5.0/jqxdatetimeinput.js"></script>
    <script type="text/javascript" src="<%=corePath%>/lib/js/jqwidgets-ver4.5.0/jqxdata.js"></script>
    <script type="text/javascript" src="<%=corePath%>/lib/js/jqwidgets-ver4.5.0/jqxbuttons.js"></script>
    <script type="text/javascript" src="<%=corePath%>/lib/js/jqwidgets-ver4.5.0/jqxscrollbar.js"></script>
    <script type="text/javascript" src="<%=corePath%>/lib/js/jqwidgets-ver4.5.0/jqxmenu.js"></script>
    <script type="text/javascript" src="<%=corePath%>/lib/js/jqwidgets-ver4.5.0/jqxlistbox.js"></script>
    <script type="text/javascript" src="<%=corePath%>/lib/js/jqwidgets-ver4.5.0/jqxdropdownlist.js"></script>
    <script type="text/javascript" src="<%=corePath%>/lib/js/jqwidgets-ver4.5.0/jqxgrid-custom.js"></script>
    <script type="text/javascript" src="<%=corePath%>/lib/js/jqwidgets-ver4.5.0/jqxgrid.edit-custom.js"></script>
    <script type="text/javascript" src="<%=corePath%>/lib/js/jqwidgets-ver4.5.0/jqxgrid.selection.js"></script>
    <script type="text/javascript" src="<%=corePath%>/lib/js/jqwidgets-ver4.5.0/jqxgrid.columnsresize.js"></script>
    <script type="text/javascript" src="<%=corePath%>/lib/js/jqwidgets-ver4.5.0/jqxgrid.filter.js"></script>
    <script type="text/javascript" src="<%=corePath%>/lib/js/jqwidgets-ver4.5.0/jqxgrid.sort-custom.js"></script>
    <script type="text/javascript" src="<%=corePath%>/lib/js/jqwidgets-ver4.5.0/jqxgrid.pager.js"></script>
    <script type="text/javascript" src="<%=corePath%>/lib/js/jqwidgets-ver4.5.0/jqxgrid.grouping.js"></script>
    <script type="text/javascript" src="<%=corePath%>/lib/js/jqwidgets-ver4.5.0/jqxgrid.aggregates.js"></script>
    <script type="text/javascript" src="<%=corePath%>/lib/js/jqwidgets-ver4.5.0/jqxgrid.storage.js"></script>
</c:if>

<%
    String ckEditorHeight = ApplicationContext.getInstance().getProperty("oasis.ckeditor.height", "");
    String ckEditorEnterMode = ApplicationContext.getInstance().getProperty("oasis.ckeditor.EnterMode", "2");
    String ckEditorLang = ApplicationContext.getInstance().getProperty("oasis.locale");
    String ckEditorFontSizes = ApplicationContext.getInstance().getProperty("oasis.ckeditor.FontSizes", "");
    String ckEditorFontNames = ApplicationContext.getInstance().getProperty("oasis.ckeditor.FontNames", "");
    String ckEditorDftFontLabel = ApplicationContext.getInstance().getProperty("oasis.ckeditor.DefaultFontLabel", "");
    String ckEditorDftFontSizeLabel = ApplicationContext.getInstance().getProperty("oasis.ckeditor.DefaultFontSizeLabel", "");
    String cursorBeforeHeader = ApplicationContext.getInstance().getProperty("oasis.ckeditor.cursorBeforeHeader", "false");
%>
<script type="text/javascript">
    var CKEditor_BasePath = '<%=corePath%>'+'/ckeditor/';
    var CKEditor_Height = '<%=ckEditorHeight%>';
    var CKEditor_EnterMode = '<%=ckEditorEnterMode%>';
    var CKEditor_Lang = '<%=ckEditorLang%>';
    var CKEditor_FontSizes = '<%=ckEditorFontSizes%>';
    var CKEditor_FontNames = '<%=ckEditorFontNames%>';
    var CKEditor_DftFontLabel = '<%=ckEditorDftFontLabel%>';
    var CKEditor_DftFontSizeLabel = '<%=ckEditorDftFontSizeLabel%>';
    var cursorBeforeHeader = '<%=cursorBeforeHeader%>';
</script>

<c:if test="${useJqxGrid}">
<script type="text/javascript">
    var useJqxGrid = true;
</script>
</c:if>

<title>
    <jsp:getProperty name="pageBean" property="title"/>
</title>
<!-- Initilize "skipSessionKeepAlive"-->
<c:if test="${empty skipSessionKeepAlive}">
    <c:set var="skipSessionKeepAlive" value="false"/>
</c:if>
<script type="text/javascript">
var isPopup = false;

//this variable is declared in xmlproc.js that derives the logic for multi grid support.
isMultiGridSupported = <%= isMultiGridSupportEnabled %> ;

// Set confirm message for session timeout
var sessionTimeoutMessage = "<%=sessionTimeoutConfirm%>";

// Retrieve configurate property values for timeoutObj
// maxInactiveInterval is in seconds
var maxInactiveInterval = "<%=session.getMaxInactiveInterval()%>";
var keepSessionAlive = "<%= ApplicationContext.getInstance().getProperty("keepSessionAlive","false")%>";
var promptForSessionKeepAlive = "<%= ApplicationContext.getInstance().getProperty("promptForSessionKeepAlive","false")%>";
var sessionTimeoutUrl ="<%= ApplicationContext.getInstance().getProperty("sessionTimeoutUrl","")%>";
var lastRefreshTime = "<%=getLastRefreshTime()%>";

var emailClientType = "LOCAL";

var gridExportExcelCsvType = "<%= ApplicationContext.getInstance().getProperty("gridExportExcelCsvType", "XLSX")%>";
<%
    if (SysParmProvider.getInstance().isAvailable()) {
%>
emailClientType = "<%=dti.oasis.util.SysParmProvider.getInstance().getSysParm("CS_EMAIL_CLIENT", "LOCAL")%>";
<%
    }
%>

var odsReportURL = "";
<%
    if (SysParmProvider.getInstance().isAvailable()) {
%>
odsReportURL = "<%=dti.oasis.util.SysParmProvider.getInstance().getSysParm("ODS_URL", "")%>";
<%
    }
%>

setMessage("cs.oasisMail.noEmailAddressForClient", "<%=MessageManager.getInstance().formatMessage("cs.oasisMail.noEmailAddressForClient")%>");

// Initialize Javascript log debugging. This variable is declared in edits.js
enableJavascriptLogging = "<%= ApplicationContext.getInstance().getProperty("javascript.logging.enable", "false")%>";

<!-- override value of "keepSessionAlive" depends on the value of "skipSessionKeepAlive"-->
<c:if test="${skipSessionKeepAlive eq true }">
keepSessionAlive = "false";
</c:if>

<%
    if (SysParmProvider.getInstance().isAvailable()) {
%>
setSysParmValue("CS_OPENNOTE_NEWWIN", "<%=SysParmProvider.getInstance().getSysParm("CS_OPENNOTE_NEWWIN", "N")%>");
<%
    } else {
%>
setSysParmValue("CS_OPENNOTE_NEWWIN", "N");
<%
    }
%>


function getTopNavApplicationUrl(applicationId) {
    var topNavURLs = "<%= topNavURLs %>";
    var url = "";
    if (topNavURLs.indexOf(applicationId) != -1) {
        url = topNavURLs.substr( topNavURLs.indexOf(applicationId) + (applicationId.length+1) );

        //Remove other web application urls.
        if (url.indexOf("~^~")!=-1) {
            url = url.substr(0, url.indexOf(("~^~")));    //get the javascript function call for opening the url
        }

        //get only the url by stripping off the javascript call.
        if (url.length > 10) {
            if (url.substr(0, 10).toLowerCase()=="javascript") {
                url = url.substr(url.indexOf("('")+2);      //remove [javascript:openWebApplication('] characters
                if (url.indexOf('~envPath/') != -1) {
                    url = getEnvPath() + url.substr(url.indexOf('~envPath/') + 8);
                }
            }
        }
        //remove xxx.do and closing [')] characters (if it is a javascript call)
        url = url.substr(0, url.lastIndexOf("/"));
    }
    return url;
}

//----------------------------------------------------------------------------------
// 84370: create a new function called  openPopupForHelp, since openPopup is being used by CMA already.
// 88430: Add myPopup.document.location.reload() to force reload the popup page.
//----------------------------------------------------------------------------------
function openPopupForHelp(sUrl, winName, width, height, top, left, resizable) {
    var myPopup = '';
    var redirectUrl = getCorePath()+"/redirectToHelp.jsp";
    sUrl = encodeURIComponent(sUrl);
    myPopup = window.open(redirectUrl+'?helpPageUrl='+sUrl, winName, 'scrollbars=yes,width=' + width + ',height=' + height + ',top=' + top + ',left=' + left + ',resizable=' + resizable);
    if (!myPopup.opener)
        myPopup.opener = self;
    myPopup.focus();
    return myPopup;
}


function refreshApp(applicationId) {
    var appUrl = getTopNavApplicationUrl(applicationId);
    var refreshparmsAppUrl = "";

    if (appUrl == "") {
        var csAppUrl = getTopNavApplicationUrl("CS");
        var baseUrl = csAppUrl.substring(0, csAppUrl.indexOf("/eCS/CS"));

        if ("CIS" == applicationId) {
            appUrl = baseUrl + "/eCIS/CIS"
        }
        else if ("Claims" == applicationId) {
            appUrl = baseUrl + "/eClaim/CM"
        }
        else if ("Policy" == applicationId) {
            appUrl = baseUrl + "/ePolicy/PM"
        }
        else if ("FM" == applicationId) {
            appUrl = baseUrl + "/eFM/FM"
        }
        else if ("RM" == applicationId) {
            appUrl = baseUrl + "/eRM/RM"
        }
        else if ("ClaimsAdmin" == applicationId) {
            appUrl = baseUrl + "/eCMA/CMA"
        }
        else if ("Web Config" == applicationId) {
            appUrl = baseUrl + "/WebWB"
        }
        else if ("eApp" == applicationId) {
            appUrl = baseUrl + "/eApp"
        }
        else if ("WCPS" == applicationId) {
            appUrl = baseUrl + "/eComp/WCPS"
        }
        else if ("eAdmin-Portal" == applicationId) {
            appUrl = baseUrl + "/eAdmin/Portal"
        }
        else if ("eAdmin-CustWebWB" == applicationId) {
            appUrl = baseUrl + "/eAdmin/CustWebWB"
        }
        else if ("eAdmin-ConfigProp" == applicationId) {
            appUrl = baseUrl + "/eAdmin/ConfigProp"
        }
        else if ("eAdmin-Security" == applicationId) {
            appUrl = baseUrl + "/eAdmin/Security"
        }
    }
    if (appUrl == "") {
        alert("Unknown application: " + applicationId);
    }
    else {
        if (!appUrl.endsWith("/")) appUrl += "/";
        if ("CS" == applicationId || "CIS" == applicationId || "ClaimsAdmin" == applicationId ||
            "Policy" == applicationId || "FM" == applicationId || "RM" == applicationId || "Claims" == applicationId || "WCPS" == applicationId ||
            "eAdmin-Portal" == applicationId || "eAdmin-CustWebWB" == applicationId || "eAdmin-ConfigProp" == applicationId || "eAdmin-Security" == applicationId) {
            refreshparmsAppUrl = appUrl + "core/refreshparms.jsp";
        }
        else {
            refreshparmsAppUrl = appUrl + "refreshparms.jsp";
        }
        openWebApplication(refreshparmsAppUrl, "true")
    }
}

var __appContextRoot=getAppPath();

<%
    AccessTrailManager.getInstance().initializeForRequest(request);
%>
function logPrintAccess() {
    if (isInIframe() && !isInDivPopup()) {
        //This page is in iframe but not Div Popup. Do not log print event.
    } else {
        if (!hasActivePopups()) {
            var pagecode = "<%= pageBean.getId()==null ? "" : pageBean.getId() %>";
            var accessTrailB = "<%= pageBean.getAccessTrailB()==null ? "" : pageBean.getAccessTrailB() %>";
            var sourceTableName = "<%= request.getAttribute(AccessTrailRequestIds.ACCESS_TRAIL_SOURCE_TABLE_NAME)==null ? "" : (String) request.getAttribute(AccessTrailRequestIds.ACCESS_TRAIL_SOURCE_TABLE_NAME) %>";
            var sourceRecordNo = "<%= request.getAttribute(AccessTrailRequestIds.ACCESS_TRAIL_SOURCE_RECORD_NO)==null ? "" : (String) request.getAttribute(AccessTrailRequestIds.ACCESS_TRAIL_SOURCE_RECORD_NO) %>";
            var sourceRecordFk = "<%= request.getAttribute(AccessTrailRequestIds.ACCESS_TRAIL_SOURCE_RECORD_FK)==null ? "" : (String) request.getAttribute(AccessTrailRequestIds.ACCESS_TRAIL_SOURCE_RECORD_FK) %>";
            var url = getCorePath() + "/logPrintAccess.jsp?pageCode=" + pagecode + "&accessTrailB=" + accessTrailB + "&sourceTableName=" + sourceTableName + "&sourceRecordNo=" + sourceRecordNo + "&sourceRecordFk=" + sourceRecordFk;
            // initiate async call
            new AJAXRequest("get", url, '', handleOnLogPrintAccess);
        }
    }
}

function handleOnLogPrintAccess(ajax){}

</script>

<% if (corePath.endsWith("/core")) { %>
<jsp:include page="/core/fieldDep.jsp"/>
<% } else {%>
<jsp:include page="/fieldDep.jsp"/>
<%}%>

</head>

<c:choose>
   <c:when test="${UIStyleEdition=='0'}">
<body onload="basePageOnLoad()" onunload="basePageOnUnload()" onafterprint="logPrintAccess()" onkeydown="baseOnBodyKeyDown()">
<link href="<%=corePath%>/css/oasisnew.css?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" rel="stylesheet" type="text/css"/>
<link href="<%=corePath%>/css/jquery-ui.css?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" rel="stylesheet" type="text/css"/>
<script type="text/javascript" >
function toggleHeader(obj,up) {
    if(up)
        obj.parentElement.style.backgroundImage='';
    else {
        obj.parentElement.style.backgroundImage = 'url(<%=corePath%>/images/headerpushed1.jpg)';
        obj.parentElement.style.backgroundRepeat='no-repeat';
        obj.parentElement.style.backgroundPosition='center center';
    }
}

function toggleLeftNav(obj,up) {
    if(up)
        obj.parentElement.style.backgroundImage='';
    else  {
      var img = "<%=corePath%>/images/";
        img += (obj.parentElement.offsetHeight>20) ? "leftnavbackdouble.jpg" : "leftnavback.jpg";
        obj.parentElement.style.backgroundImage='url('+img+')';
    }

}
</script>

<table width="100%" border="0" cellpadding="0" cellspacing="0" style="position:absolute; top:0; left:0">
<tr height="75">
<td width="180" valign="top" align="left"><div style="position:relative">
<img src="<%= corePath + "/images/oasislogo.gif?build.number=" + ApplicationContext.getInstance().getProperty(IOasisAction.KEY_BUILD_NUMBER) %>" >
<img style="position:absolute; top:0px; left:0px; width:180; height:75;z-index:0;overflow:hidden;" src="<%=corePath%>/images/topnavswoop.gif?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>">
</div></td>
<td colspan="2" width="803" valign="bottom">
    <div style="position:relative">
  <img style="position:absolute; top:0px; left:0px; width:100%; height:100%; z-index:-1;overflow:hidden;" src="<%=corePath%>/images/headernewright.jpg?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>">
    </div>
    <table cellpadding="2" border="0" width="100%" style="position:relative; z-index:0">
    <tr>
<%
    if(request.getUserPrincipal()!=null) {
%>
        <td colspan="3" class="small"><%=dtLogged%>&nbsp;&nbsp;&nbsp;<b><fmt:message key="label.header.database"/></b><%=dbTitle%></td>
<%  } else {%>
        <td colspan="3" class="small"><%=dtLogged%>&nbsp;&nbsp;&nbsp;</td>
<%  } %>
    <td style="font-size:1px" colspan="<%=pageBean.getTopNavMenu().size()-2%>">&nbsp;</td>
    <td rowspan="3" width="110" valign="bottom">
<%
    if(request.getUserPrincipal()!=null) {
%>
<table border="0" cellspacing="1" cellpadding="1"><tr><td>
            <a class="login" href="<%=corePath%>/logout.jsp" onmouseout="MM_swapImgRestore()"
                onmouseover="MM_swapImage('orangebutton1','','<%=corePath%>/images/orangebutton_f2.jpg',1)">
                <img hspace="1" src="<%=corePath%>/images/orangebutton.jpg?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" name="orangebutton1" width="9" height="9" border="0" id="orangebutton"/>&nbsp;Log Off</a>
        </td></tr><tr><td>
<% if(showUserSettings) { %>
            <a class="login" target="_chgpass" href="<%=passwordChangeJSP%>" onmouseout="MM_swapImgRestore()"
                onmouseover="MM_swapImage('orangebutton2','','<%=corePath%>/images/orangebutton_f2.jpg',1)">
                <img hspace="1" src="<%=corePath%>/images/orangebutton.jpg?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" name="orangebutton2" width="9" height="9" border="0" id="orangebutton"/>&nbsp;User Settings</a>
<% } else { %>&nbsp;<%}%>
        </td></tr></table>
<%  } else {
%>
    &nbsp;
<%  } %>
    </td>
    </tr>
    <tr><td colspan="3" class="apptitle">&nbsp;<jsp:getProperty name="pageBean" property="title"/></td></tr>
    <tr align="center">
    <td rowspan=2 width="5">&nbsp;</td>
<%  if(goodBrowser) {%>
    <td align="right"><table cellpadding="2" cellspacing="2"><tr>
    <logic:iterate id="topnavitem" name="pageBean" property="topNavMenu"
        type="dti.oasis.util.MenuBean">
        <%
            String background= (topnavitem.isLink()) ?"" : corePath + "/images/headerpushed1.jpg";
            String aStyle= (topnavitem.isLink()) ?"" : "color:#000000";

            String url = topnavitem.getUrl();
            String linkTarget = "_self";

            if (openTopNavInNewWindow) {
                url = StringUtils.replace(url, "')", "', true)");
            }
        %>
        <td background="<%=background%>">
            <a class="header" id="topnav_<%=topnavitem.getLabel()%>" href="<%=url%>" style="<%=aStyle%>"
                onmouseover="toggleHeader(this,false)" onmouseout="toggleHeader(this,true)">
                &nbsp;<%=topnavitem.getLabel()%>&nbsp;</a>
        </td>
        <td>&nbsp;</td>
    </logic:iterate>
    </tr></table></td>
<% }%>
    <td width="1">&nbsp;</td>

    </tr>
    </table>
</td>
<td>&nbsp;</td>
</tr>
<tr>
<td width="180" height="530" valign="top">
    <DIV id="leftnav" class="leftnav">
    <table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
    <tr>
        <td class="firstleftnavspacer">&nbsp;</td>
        <td class="firstleftnavspacer" style="width:1px;padding:0px">&nbsp;</td>
        <td class="firstleftnavspacer">&nbsp;</td>
    </tr>
<% if(goodBrowser) {%>
    <logic:iterate id="menuitem" name="pageBean" property="leftNavMenu"
        type="dti.oasis.util.MenuBean">
    <%
        String background= (menuitem.isLink()) ?"" : corePath +"/images/leftnavback.jpg";
        String href = (menuitem.getUrl().startsWith("javascript:")) ? menuitem.getUrl() :
                "javascript:doMenuItem('" + menuitem.getId() + "','" + menuitem.getUrl() + "')";
    %>
        <tr id="R_menuitem_<%=menuitem.getLabel()%>">
        <td class="leftnavspacer">&nbsp;</td>
            <td background="<%=background%>" class="leftnav">
                <a class="leftnav" id="menuitem_<%=menuitem.getLabel()%>"
                    href="<%=href%>"
                    onmouseover="toggleLeftNav(this,false)"
                    onmouseout="toggleLeftNav(this,true)">
                    <%=menuitem.getLabel()%></a>
            </td>
        </tr>
    </logic:iterate>
    <tr>
    <td>&nbsp;</td>
    </tr>
    <logic:iterate id="actionitem" name="pageBean" property="leftNavActions"
        type="dti.oasis.util.MenuBean">
        <tr id="R_actionitem_<%=actionitem.getLabel()%>">
        <td class="leftnavspacer">&nbsp;</td>
        <td>
            <a class="leftsubnav" id="actionitem_<%=actionitem.getLabel()%>"
                href="<%=actionitem.getUrl()%>"
                onmouseover="toggleLeftNav(this,false)"
                onmouseout="toggleLeftNav(this,true)">
            <img hspace="4" src="<%=corePath%>/images/orangebutton.jpg?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" width="9" height="9" border="0"/>
            <%=actionitem.getLabel()%></a>
        </td>
        </tr>
    </logic:iterate>
<%  } %>
    </table>
    </div>
</td>
<td class="content" width="2">&nbsp;</td>
<td class="bodycontent" id="maincontent" valign="top" width="798">
<%
    if(!goodBrowser) {
%>
<center><span class="errortext" style="width:80%;font-size:12pt"><br><fmt:message key="label.required.browser"/></span></center>
<%
    }
%>

<table width="100%" border="0" cellpadding="2" cellspacing="0" style="display:<%=display%>" class="<%=cssClass%>">
    <tr>
        <td colspan="8" class="spacer6" >&nbsp;</td>
    </tr>

</c:when>

<c:when test="${UIStyleEdition=='1'}">
<body class="body" onload="basePageOnLoad(event)" onunload="basePageOnUnload(event)" onmousemove="basePageOnMouseMove(event)"
      onmouseup="basePageOnMouseUp(event)" onreadystatechange="basePageOnReadyStateChange(this)"
      onafterprint="logPrintAccess()" onkeydown="baseOnBodyKeyDown(event)">
<link href="<%=corePath%>/css/oasisnew1.css?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" rel="stylesheet" type="text/css"/>
<script type="text/javascript">

 function toggleHeader(obj,up) {
    if(up)
        obj.parentElement.style.backgroundImage='';
    else {
        obj.parentElement.style.backgroundImage = 'url(<%=corePath%>/images/headerpushedgreen.jpg)';
        obj.parentElement.style.backgroundRepeat='no-repeat';
        obj.parentElement.style.backgroundPosition='center center';
    }
}

function toggleLeftNav(obj,up) {
    if(up)
        obj.parentElement.style.backgroundImage='';
    else  {
      var img = "<%=corePath%>/images/";
        img += (obj.parentElement.offsetHeight>20) ? "leftnavbackdoublegreen.jpg" : "leftnavbackgreen.jpg";
        obj.parentElement.style.backgroundImage='url('+img+')';
    }

}
 function viewHelp(url){
     var helpWidth=400;
     openPopupForHelp(url, 'HelpWindow', helpWidth, screen.height-55, 0, (screen.width-helpWidth)-10, 'yes');
 }
</script>

<table class="wholeBody" cellspacing=0 cellpadding=0>
    <tr class="header">
        <td>
            <table class="header" cellspacing=0 cellpadding=0>
                <tr>
                    <td class="header_logo">
                        <img style="width:180px; height:75px;" src="<%=corePath%>/images/dtilogo.gif?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>">
                    </td>
                    <td class="header_cap">
                        <table cellspacing=0 cellpadding=0>
                            <tr>
                                <td class="header_info">
                                    <table cellspacing=0 cellpadding=0 style="width:100%;height:100%">
                            <% if(userName!="") { %>
                                        <tr>
                                            <td valign=top colspan=2>
                                                <div  class="small"> <b><fmt:message key="header.welcome"><fmt:param  value="${userBean.userName}" /></fmt:message></b>&nbsp;
                                                    <b><fmt:message key="label.header.database" /></b><%=dbTitle%>
                                                </div>
                                                <div  class="small"> <%=dtLogged%>&nbsp;&nbsp;&nbsp;</div>
                                            </td>
                                        </tr>
                            <% } %>
                                        <tr>
                                            <td valign=bottom>
                                                <div class="apptitle"  valign=bottom>
                                                    <jsp:getProperty name="pageBean" property="title"/> &nbsp;
                                                </div>
                                            </td>
                                            <td valign=bottom align=right>
                                                <%  if(goodBrowser) {%>
                                                    <table cellpadding="0" cellspacing="3">
                                                        <tr>
                                                            <logic:iterate id="topnavitem" name="pageBean" property="topNavMenu"
                                                                type="dti.oasis.util.MenuBean">
                                                               <%String background = (topnavitem.isLink()) ? "" : corePath + "/images/headerpushedgreen.jpg";
                                                                 String aStyle= (topnavitem.isLink()) ?"" : "color:#000000";
                                                                 String url=topnavitem.getUrl();
                                                                %>
                                                                <td class="subsystem" background="<%= background%>" >

                                                                    <a id="topnav_<%=topnavitem.getLabel()%>" href="<%=url%>" style="<%=aStyle%>"
                                                                       onmouseover="toggleHeader(this,false)" onmouseout="toggleHeader(this,true)">
                                                                         |&nbsp;<%=topnavitem.getLabel()%>&nbsp;|</a>
                                                                </td>
                                                                <td>&nbsp;</td>
                                                            </logic:iterate>
                                                            <td valign=bottom> &nbsp;&nbsp;&nbsp;
                                                                <%
                                                                    String helpUrl = pageBean.getHelpUrl();
                                                                    String helpImg = corePath + "/images/help.gif";

                                                                    if(helpUrl!=null && helpUrl!="") { %>
                                                                        <a id="pghelp" href="javascript:void(0)" onclick="return viewHelp('<%= helpUrl%>')"><img style="vertical-align:bottom;"  src="<%=helpImg%>" alt="help"></a> &nbsp;
                                                                <%}%>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                <% }%>
                                            </td>
                                        </tr>
                                    </table>
                                </td>
                                <td class="header_info_right">
                                    <table width=100% height=100% cellpadding=0 cellspacing=0 class="header_info_right" style="<%= "background-image:url('" + productLogoImageFile + "');"%>">
                                      <tr>
                                          <td valign=bottom class="RightAlignLabel">
                                    <% if(request.getUserPrincipal()!=null) {
                                                 if(showUserSettings) { %>
                                                    <a class="login" target="_chgpass" href="<%=passwordChangeJSP%>" onmouseout="MM_swapImgRestore()"
                                                        onmouseover="MM_swapImage('orangebutton2','','<%=corePath%>/images/orangebutton_f2.jpg',1)">
                                                        <img hspace="1" src="<%=corePath%>/images/orangebutton.jpg" name="orangebutton2" width="9" height="9" border="0" id="orangebutton"/>&nbsp;User Settings&nbsp;</a>
                                        <% } %>
                                                <a class="login" href="<%=corePath%>/logout.jsp" onmouseout="MM_swapImgRestore()"
                                                     onmouseover="MM_swapImage('orangebutton1','','<%=corePath%>/images/orangebutton_f2.jpg',1)">
                                                    <img hspace="1" src="<%=corePath%>/images/orangebutton.jpg" name="orangebutton1" width="9" height="9" border="0" id="orangebutton"/>&nbsp;Log Off&nbsp;</a>
                                    <% } %>
                                          </td>
                                      </tr>
                                    </table>
                                </td>
                                <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    <tr>
        <td>
    <%
        String leftNavStyle = (userName=="") ?"display:none;" : "display:block;" ;
        String lftNavCssClass = (userName=="") ? "dti-hide" : "";
    %>
    <div id="bodyContent">
        <%
            if(useJqxGrid){
        %>
        <div id="leftNav" Style="display:block;" class="<%=lftNavCssClass%>">
        <%
            } else {
        %>
        <div id="leftNav" Style="<%= leftNavStyle %>">
        <%
            }
        %>
            <table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
                <tr>
                    <td class="firstleftnavspacer">&nbsp;</td>
                </tr>
                <% if(goodBrowser) {%>
                  <logic:iterate id="menuitem" name="pageBean" property="leftNavMenu"
                      type="dti.oasis.util.MenuBean">
                <%
                  if (StringUtils.isBlank(menuitem.getUrl())) menuitem.setUrl("#");

                      String background = (menuitem.isLink()) ? "" : corePath + "/images/leftnavbackgreen.jpg";
                      String href = (menuitem.getUrl().startsWith("javascript:")) ? menuitem.getUrl() :
                              "javascript:doMenuItem('" + menuitem.getId() + "','" + menuitem.getUrl() + "')";
                %>
                      <tr id="R_menuitem_<%=menuitem.getId()%>">
                          <td background="<%=background%>" class="leftnav">
                              <a class="leftnav" id="menuitem_<%=menuitem.getId()%>"
                                  href="<%=href%>"
                                  onmouseover="toggleLeftNav(this,false)"
                                  onmouseout="toggleLeftNav(this,true)">
                                <%=menuitem.getLabel()%></a>
                          </td>
                      </tr>
                  </logic:iterate>
                  <tr>
                  <td>&nbsp;</td>
                  </tr>

                <% if(pageBean.getLeftNavActions()!=null) {%>

                  <logic:iterate id="actionitem" name="pageBean" property="leftNavActions"
                      type="dti.oasis.util.MenuBean">
                  <tr id="R_actionitem_<%=actionitem.getId()%>">
                      <td>
                          <a class="leftsubnav" id="actionitem_<%=actionitem.getId()%>"
                              href="<%=actionitem.getUrl()%>"
                              onmouseover="toggleLeftNav(this,false); this.className='leftsubnavonmouseover';"
                              onmouseout="toggleLeftNav(this,true); this.className='leftsubnav';">
                          <img hspace="4" src="<%=corePath%>/images/orangebutton.jpg?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" width="9" height="9" border="0"/>
                        <%=actionitem.getLabel()%></a>
                      </td>
                  </tr>
                  </logic:iterate>
                <%  } %>
                <%  } %>
            </table>
        </div>
        <div id="content" class="content">
            <%if(!goodBrowser) {%>
            <center><span class="errortext" style="width:80%;font-size:12pt"><br><fmt:message key="label.required.browser"/></span></center>
            <% } %>
            <table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
                <tr height=100%>
                    <td valign=top>

                      <table width="100%" border="0" cellpadding="2" cellspacing="0" style="display:<%=display%>" class="<%=cssClass%>">
                          <tr>
                              <td colspan="8" class="spacer6" >&nbsp;</td>
                          </tr>

</c:when>
<c:otherwise>

  <%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>

  <body class="body" onload="basePageOnLoad(event)" onunload="basePageOnUnload(event)"
        onmousemove="<%=onMouseMoveFunction%>" onmouseup="<%=onMouseUpFunction%>"
        onreadystatechange="basePageOnReadyStateChange(this)" onafterprint="logPrintAccess()" onkeydown="return baseOnBodyKeyDown(event);">
  <c:if test="${noLoadingDiv != true}">
      <c:if test="${disablePageWhileLoading == true}">
          <%-- Disable the page while loading if requested to do so --%>
          <div id="overlay-div-for-loading" class="ui-widget-overlay" style="left:0;top:0;width:100%;height:100%;z-index:9998;"></div>
      </c:if>
      <div class="<%=loadingDivClass%> loading-div" style="background-color: white;  border:2px; border-style:solid;border-color: #003E7E; padding:2.64px; <%=loadingDivSize%> display: block; <%=alignDivString%>">
          <div class="ui-dialog-titlebar" style="height:28px">
              <span class=ui-dialog-title>&nbsp;</span>
          </div>
          <div style="min-height: 0px; width: auto; height: 0px; padding: 0.5em"
               align="center">
              <img alt="loading" src="<%=corePath%>/images/running.gif?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>">
              <span class=txtOrange>&nbsp;<%=runProcess%></span>
          </div>
      </div>
  </c:if>
  <link href="<%=corePath%>/css/dti.css?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" rel="stylesheet" type="text/css"/>
  <link href="<%=corePath%>/css/button.css?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" rel="stylesheet" type="text/css"/>
  <link href="<%=corePath%>/css/jquery-ui.css?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" rel="stylesheet" type="text/css"/>
  <link href="<%=corePath%>/css/jmenu.css?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" rel="stylesheet" type="text/css"/>
  <c:if test="${useJqxGrid}">
  <link href="<%=corePath%>/css/oasis-oxb.css?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" rel="stylesheet" type="text/css"/>
  <link rel="stylesheet" href="<%=corePath%>/lib/js/jqwidgets-ver4.5.0/jqx.oasis.css?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" type="text/css" />
  </c:if>
  <link href="<%=corePath%>/customStyles.jsp" rel="stylesheet" type="text/css"/>
  <script type="text/javascript">

  useJQueryDialog = <%=useJQueryDialog%>;

  var globalSearchIndicator = "";
  function showGlobalSearch() {
    if (getObject("tdGlobalSearch")) {
        if(useJqxGrid)
            hideShowElementAsInlineByClassName(getObject("tdGlobalSearch"), false);
        else
            getObject("tdGlobalSearch").style.display = "inline";

    }
  }

  function hideGlobalSearch() {
    if (getObject("tdGlobalSearch")) {
        if(useJqxGrid)
            hideShowElementByClassName(getObject("tdGlobalSearch"), true);
        else
            getObject("tdGlobalSearch").style.display = "none";
    }
  }

  function setHintDescriptionForGlobalSearch(hintDescription) {
    if (isEmpty(hintDescription)) {
      hintDescription = getDefaultHintDescriptionForGlobalSearch();
    }
    if (hintDescription) {
      if (getObject("globalSearch")) {
        getObject("globalSearch").value = hintDescription;
        getObject("globalSearch").title = hintDescription;
      }
    }
  }

  function getDefaultHintDescriptionForGlobalSearch() {
    return "<%= globalSearchDefaultHint %>"
  }

  function getDefaultUrlForGlobalSearch() {
      return "<%= globalSearchDefaultUrl%>"
  }
  </script>
  <table class="mainTable" cellpadding="0" cellspacing="0">

  <c:if test="${headerHidden != 'Y'}">
  <!-- BEGIN: header -->
  <tr>
  <td>
  <div id="globalHeader" class="header" style="<%= "background-image:url('" + productLogoImageFile + "');"%>">
      <div id="headerLogoTips" class="headerLogoTips" title="<%=strLogoTipInfo%>"></div>
      <table border="0" cellpadding="0" cellspacing="0" width="100%">
          <tr>
              <td colspan="2" style="padding:0px;height:1px">
                  <div style="height:100%;width:100%">
                      <img src="<%=corePath%>/images/space.gif?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" width="100%" height="1px"/>
                  </div>
              </td>
          </tr>
          <tr>
              <td>
                  <div class="headerline1">
                      <div class="headerline1Info">
                          <% if(isEnvSpecified) { %>
                          <span id="test" class="headerEnvInfo"><%=applicationTitle%></span>
                          <% } %>
                      </div>
                      <div class="headerline1links">
                          <input name="logoff" type="button" class="btHdr" value="<fmt:message key='label.header.page.logOff'/>" title="<fmt:message key='label.header.page.logOff.title'/>"
                                 onclick="javascript:baseOnLogOut('<%=corePath%>', event)"/>
                      </div>
                  </div>
                  <div class="headerline2">
                      <div class="txtWhite">
                          <% if (showUserSettings) { %>
                          <a href="<%=passwordChangeJSP%>" class="lkHdr" target="_blank"
                             title="<fmt:message key='label.header.page.changePassword.title'/>"><fmt:message key='label.header.page.changePassword'/></a>
                          <% }
                             if (showTopnavMenu) {

                                 boolean isEmployee = request.isUserInRole("EMPLOYEE") || request.isUserInRole("EMPLOYEEROLE");
                                 boolean isAdmin = request.isUserInRole("OASISSYSADMIN") || request.isUserInRole("OASISSYSADMINROLE");
                                 boolean isSysInfoAdmin = request.isUserInRole("OASISSYSINFOADMIN") || request.isUserInRole("OASISSYSINFOADMINROLE");
                                 boolean isCustWebWBAdmin = request.isUserInRole("OASISCUSTWEBWBADMIN") || request.isUserInRole("OASISCUSTWEBWBADMINROLE");
                                 boolean isPortalAdmin = request.isUserInRole("OASISPORTALADMIN") || request.isUserInRole("OASISPORTALADMINROLE");
                                 boolean isConfigPropAdmin = request.isUserInRole("OASISCONFIGPROPADMIN") || request.isUserInRole("OASISCONFIGPROPADMINROLE");
                                 boolean isSecurityAdmin = request.isUserInRole("OASISSECURITYADMIN") || request.isUserInRole("OASISSECURITYADMINROLE");
                                 boolean isProdDefWBAdmin = request.isUserInRole("OASISPRODDEFWBADMIN") || request.isUserInRole("OASISPRODDEFWBADMINROLE");
                                 boolean isCMAAdmin = request.isUserInRole("OASISCMAADMIN") || request.isUserInRole("OASISCMAADMINROLE");
                                 boolean isOasisUser = request.isUserInRole("OASISUSER") || request.isUserInRole("OASISUSERROLE");
                                 boolean isPMUser = request.isUserInRole("PMUSER") || request.isUserInRole("PMUSERROLE");
                                 boolean isCMUser = request.isUserInRole("CMUSER") || request.isUserInRole("CMUSERROLE");
                                 boolean isCISUser = request.isUserInRole("CISUSER") || request.isUserInRole("CISUSERROLE");
                                 boolean isFMUser = request.isUserInRole("FMUSER") || request.isUserInRole("FMUSERROLE");
                                 boolean isRMUser = request.isUserInRole("RMUSER") || request.isUserInRole("RMUSERROLE");

                                boolean isLinkAlreadyRendered = showUserSettings;

                                 String currentURL = (String) pageContext.getRequest().getAttribute(dti.oasis.http.RequestIds.REQUEST_URI);
                                 String currentTopNavMenuItemId = null;
                                 List topNavItems = pageBean.getTopNavMenu();
                                 for (int i = 0; i < topNavItems.size(); i++) {
                                     MenuBean currentMenu = (MenuBean) topNavItems.get(i);
                                     //match at page level,
                                     if (!StringUtils.isBlank(currentURL)) {
                                         String topNavItemURL = currentMenu.getUrl();
                                         if(topNavItemURL.indexOf("~envPath")>0){
                                             topNavItemURL = topNavItemURL.substring(topNavItemURL.indexOf("~envPath")+8); //remove ~envPath
                                         }
                                         if(topNavItemURL.indexOf("'")>0){
                                             topNavItemURL = topNavItemURL.substring(0, topNavItemURL.indexOf("'"));       //remove ')
                                         }
                                         if (!StringUtils.isBlank(topNavItemURL) && currentURL.indexOf(topNavItemURL) > -1) {
                                             currentTopNavMenuItemId = currentMenu.getId();
                                             break;
                                         }
                                     }
                                     //then, match at application level
                                     if (currentMenu.getId().equalsIgnoreCase(applicationId)) {
                                         currentTopNavMenuItemId = currentMenu.getId();
                                     }
                                 }
                          %>
                          <c:forEach var="topnavigationitem" items="${pageBean.topNavMenu}" varStatus="status">
                              <%
                                  MenuBean topnavitem = (MenuBean) pageContext.getAttribute("topnavigationitem");
                                  boolean isCurrentApplication = topnavitem.getId().equalsIgnoreCase(currentTopNavMenuItemId);
                                  boolean isHideItem = !isEmployee && !isAdmin &&
                                          ((topnavitem.getId().equalsIgnoreCase("ClaimsAdmin") && !isCMAAdmin) ||
                                          (topnavitem.getId().equalsIgnoreCase("eAdmin-SysInfo") && !isSysInfoAdmin) ||
                                          (topnavitem.getId().equalsIgnoreCase("eAdmin-CustWebWB") && !isCustWebWBAdmin) ||
                                          (topnavitem.getId().equalsIgnoreCase("eAdmin-Portal") && !isPortalAdmin) ||
                                          (topnavitem.getId().equalsIgnoreCase("eAdmin-ConfigProp") && !isConfigPropAdmin) ||
                                          (topnavitem.getId().equalsIgnoreCase("eAdmin-Security") && !isSecurityAdmin) ||
                                          (topnavitem.getId().equalsIgnoreCase("eAdmin-ProdDefWB") && !isProdDefWBAdmin));
                                      isHideItem =isHideItem || (!isOasisUser && ((topnavitem.getId().equalsIgnoreCase("Policy") && !isPMUser) ||
                                          (topnavitem.getId().equalsIgnoreCase("Claims") && !isCMUser) ||
                                          (topnavitem.getId().equalsIgnoreCase("CIS") && !isCISUser) ||
                                          (topnavitem.getId().equalsIgnoreCase("FM") && !isFMUser) ||
                                          (topnavitem.getId().equalsIgnoreCase("RM") && !isRMUser) ||
                                          (topnavitem.getId().equalsIgnoreCase("CS") && !isCISUser && !isPMUser && !isCMUser && !isFMUser && !isRMUser)));
                                  if (!isHideItem) {
                                      if (isLinkAlreadyRendered) {
                              %>
                                      |
                                  <%  }
                                      isLinkAlreadyRendered = true;
                                      if (isCurrentApplication) {
                                  %>
                                      <a id="topnav_<%=topnavitem.getId()%>" href="javascript:void(0)" class="topNavCurrentApp">
                                          <%=topnavitem.getLabel()%>
                                      </a>
                                  <% } else {
                                      String url = topnavitem.getUrl();
                                      String linkTarget = "_self";

                                      if (openTopNavInNewWindow) {
                                          url = StringUtils.replace(url, "')", "', true)");
                                      }
                                  %>
                                      <a id="topnav_<%=topnavitem.getId()%>" href="<%=url%>" class="lkHdr"
                                         target="<%=linkTarget %>"><%=topnavitem.getLabel()%>
                                      </a>

                                  <% }
                                      if (isLinkAlreadyRendered) {%>
                                  &nbsp;
                                  <%
                                      }
                                  }
                              %>

                          </c:forEach>
                          <% } %>
                          <% if (!StringUtils.isBlank(applicationHelpURL)) { %>
                          &nbsp; |
                          <a href="<%=applicationHelpURL%>" class="lkHdr" target="_blank"
                             title="<fmt:message key='label.header.page.applicationHelp'/>"><fmt:message key='label.header.page.help'/></a>
                          <% } %>
                      </div>
                    <%
                        if(useJqxGrid) {
                    %>
                      <div id="globalJumpNavDiv" class="globalJumpNavDiv dti-hide">
                      <%
                        } else {
                    %>
                          <div id="globalJumpNavDiv" class="globalJumpNavDiv">
                    <%
                       }
                    %>
                          <select id="gblJumpNav" name="gblJumpNav" class="globalActionItemList"
                                  onChange="processJumpTo(this)" style="padding:0px;padding-top:3px;width:200px">
                          </select>
                      </div>
                  </div>
              </td>
          </tr>
      </table>
  </div>
  </td>
  </tr>
  </c:if>
  <!-- END: header -->

  <script type="text/javascript">
      function viewHelp(url){
         if (url) {
             var helpWidth=400;
             openPopupForHelp(url, 'HelpWindow', helpWidth, screen.height-55, 0, (screen.width-helpWidth)-10, 'yes');
         } else {
             // If no context level help url is provided, by default open up the application level help.
              url = "<%= applicationHelpURL %>" ;
              if (url) {
                openMain(url, 'HELP');
              } else {
                alert(getMessage("core.help.error.notFound"));
              }
         }
      }

      var finalJumpUrl = "";
      var defaultJumpURL = "";
      var jumpToOptionList = "";
      function processJumpTo(id) {
          if (id.value) {
              finalJumpUrl = "";
              jumpToOptionList = "";
              var process = "getJumpNavInfo";
              var csTopNavApplication = getTopNavApplicationUrl("CS");
              if(!csTopNavApplication) {
                  alert(getMessage("core.jumpTo.error.contextPath"));
              }
              var url = csTopNavApplication + "/navigationmgr/loadJumpNavInfo.do?date="+ new Date() + "&process=" + process + "&id="+id.value;
              //alert(url)

              var httpreq = new XMLHttpRequest();
              httpreq.open("GET", url, false);
              httpreq.onreadystatechange = function() {
                  handleOnGetJumpNavInfo(httpreq)
              };
              httpreq.send();
              id.options[0].selected=true;
          }
      }

      function getFieldIdForJumpNavigation(contextCode, contextFieldId) {
          var fieldId = contextFieldId;
          if (!hasObject(fieldId)) {
              fieldId = fieldId.substr(0, fieldId.length-2) + "Pk";
              if (!hasObject(fieldId)) {
                 fieldId = fieldId.substr(0, fieldId.length-2) + "PK";
                 if (!hasObject(fieldId)) {
                    fieldId = fieldId.substr(0, fieldId.length-2) + "_Pk";
                    if (!hasObject(fieldId)) {
                       fieldId = fieldId.substr(0, fieldId.length-2) + "_PK";
                       if (!hasObject(fieldId)) {
                          fieldId = "pk";
                          if (!hasObject(fieldId)) {
                              fieldId = handleOnGetFieldIdForJumpNavigation();
                              if (!hasObject(fieldId)) {
                                 fieldId = "";
                              }
                          }
                       }
                    }
                 }
              }
          }
          return fieldId
      }

      function getJumpToContextCode (contextCode, contextFieldId) {
          var finalContextCode = contextCode;
          switch (contextCode) {
              case "POLICY":
                  if (contextFieldId.toUpperCase().substring(contextFieldId.length-2)=="NO") {
                      finalContextCode = 'POLICYNO';
                  } else {
                      finalContextCode = 'POLICY';
                  }
                  defaultJumpURL = getTopNavApplicationUrl("Policy");
                  break;
              case "QUOTE":
                  if (contextFieldId.toUpperCase().substring(contextFieldId.length-2)=="NO") {
                      finalContextCode = 'QUOTENO';
                  } else {
                      finalContextCode = 'QUOTE';
                  }
                  defaultJumpURL = getTopNavApplicationUrl("Policy");
                  break;
              case "APP":
                  if (contextFieldId.toUpperCase().substring(contextFieldId.length-2)=="NO") {
                      finalContextCode = 'APPLICATIONNO';
                  } else {
                      finalContextCode = 'APPLICATION';
                  }
                  // change this when eApp jumps are implemented
                  defaultJumpURL = getTopNavApplicationUrl("CS");
                  break;
              case 'CLAIM':
                  if (contextFieldId.toUpperCase().substring(contextFieldId.length-2)=="NO") {
                      finalContextCode = 'CLAIMNO';
                  } else {
                      finalContextCode = 'CLAIM';
                  }
                  defaultJumpURL = getTopNavApplicationUrl("Claims");
                  break;
              case 'FM':
                  if (contextFieldId.toUpperCase().substring(contextFieldId.length-2)=="NO") {
                      finalContextCode = 'BACCOUNTNO';
                  } else {
                      finalContextCode = 'BACCOUNT';
                  }
                  defaultJumpURL = getTopNavApplicationUrl("FM");
                  break;
              case 'CIS':
                  finalContextCode = 'CLIENT';
                  defaultJumpURL = getTopNavApplicationUrl("CIS");
                  break;
          }
          return finalContextCode;
      }

      function handleOnGetJumpNavInfo(ajax)
      {
           if (ajax.readyState == 4) {
                if (ajax.status == 200) {
                    var data = ajax.responseXML;
                    if (!handleAjaxMessages(data, null))
                        return;
                    var oValueList = parseXML(data);
                    if (oValueList.length > 0) {
                        var dataObject = oValueList[0];
                        var urlParms =  dataObject["urlParameter"];
                        var finalUrl = dataObject["navUrl"];
                        var destTopNavApplication = getTopNavApplicationUrl(dataObject["destinationApplicationId"]);
                        if(destTopNavApplication) {
                            if (finalUrl.substring(0,1)=="~") {
                                finalUrl = destTopNavApplication + finalUrl.substring(1);
                            } else {
                                finalUrl = destTopNavApplication + (finalUrl.substring(0,1)=="/" ? finalUrl : ("/" + finalUrl));
                            }
                        } else {
                            alert (getMessage("core.jumpTo.error.destination", new Array(dataObject["destinationApplicationId"])));
                            return;
                        }

                        finalUrl += "?date="+ new Date();
                        finalUrl += "&"+dataObject["destinationContextFieldId"] + "=^" + dataObject["destinationContextFieldId"] + "^";
                        while(urlParms.indexOf(",")!=-1) {
                            var fieldId = urlParms.substring(0, urlParms.indexOf(","));
                            urlParms = urlParms.substring(urlParms.indexOf(",")+1);
                            finalUrl += "&"+fieldId + "=";
                            if (getObjectValue(fieldId))
                               finalUrl +=  getObjectValue(fieldId);
                        }
                        if (urlParms) {
                          finalUrl += "&"+urlParms + "=";
                          if (getObjectValue(urlParms))
                             finalUrl +=  getObjectValue(urlParms)
                        }
                        //alert(finalUrl);
                        finalJumpUrl = finalUrl ;

                        var sourceContext = getJumpToContextCode(dataObject["sourceContextCode"], dataObject["sourceContextFieldId"]);
                        var destinationContext = getJumpToContextCode(dataObject["destinationContextCode"], dataObject["destinationContextFieldId"]);

                        var process = "getListOfValuesForJumpNav";
                        var csTopNavApplication = getTopNavApplicationUrl("CS");
                        if(!csTopNavApplication) {
                            alert(getMessage("core.jumpTo.error.contextPath"));
                        }
                        var url = csTopNavApplication + "/navigationmgr/loadJumpNavInfo.do?date="+ new Date() ;
                        url += "&process=" + process ;
                        url += "&currContextType=" + sourceContext;
                        url += "&currContextValue=" ;
                        var sourceFieldId = getFieldIdForJumpNavigation(dataObject["sourceContextCode"], dataObject["sourceContextFieldId"]);
                        if (sourceFieldId) {
                            url += getObjectValue(sourceFieldId);
                        } else {
                            alert(getMessage("core.jumpTo.error.sourceField"));
                        }
                        url += "&newContextType=" + destinationContext;
                        //alert(url)
                        // initiate call

                        var httpreq = new XMLHttpRequest();
                        httpreq.open("GET", url, false);
                        httpreq.onreadystatechange = function() {
                            handleOnGetListOfValuesForJumpNav(httpreq)
                        };
                        httpreq.send();
                    }
                }
            }
      }

     function handleOnGetListOfValuesForJumpNav(ajax)
      {
           if (ajax.readyState == 4) {
                if (ajax.status == 200) {
                    var data = ajax.responseXML;
                    if (!handleAjaxMessages(data, null))
                        return;
                    var oValueList = parseXML(data);
                    if (oValueList.length > 0) {
                        var dataObject = oValueList[0];
                        if (dataObject["returnValue"] > 0) {
                            if (dataObject["returnValue"] == 1) {
                                jumpTo(dataObject["newContextValue"]);
                            } else {
                                jumpToOptionList = dataObject["newContextValue"];
                                //alert(jumpToOptionList)
                                openDivPopup(" Select From List", getCorePath() + "/jumpToOptionList.html?date=" + new Date(), true, true,
                                        null, null, 454, 576, 400, 500, "jumpToOptionListDivPopupId", false);
                            }
                        } else {
                            if (confirm(getMessage("core.jumpTo.error.translating"))) {
                                jumpTo("");
                            }
                        }
                    }
                }
            }
      }

      var firstContextValue = '';
      var policyOriginCode = '';
      function jumpTo(destinationContextValue) {
          if (finalJumpUrl) {
              if (finalJumpUrl.indexOf("^")>0) {
                  if (destinationContextValue) {
                      var contextValue = destinationContextValue;
                      if (destinationContextValue.indexOf("^") > 0) {
                          contextValue = destinationContextValue.substr(0, destinationContextValue.indexOf("^"));
                      }
                      if (contextValue.indexOf(":") > 0) {
                          firstContextValue = contextValue.substring(0, contextValue.indexOf(":"));
                          policyOriginCode = contextValue.substring(contextValue.indexOf(":") + 1);
                          contextValue = firstContextValue + "&policyOriginCode=" + policyOriginCode;
                      }
                      finalJumpUrl = finalJumpUrl.substring(0, finalJumpUrl.indexOf("^")) + contextValue +
                                     finalJumpUrl.substring(finalJumpUrl.indexOf("^", (finalJumpUrl.indexOf("^")+1))+1) ;
                  } else {
                      finalJumpUrl = defaultJumpURL;
                  }
              }

              if (policyOriginCode == "VELOCITY_POLICY" || policyOriginCode == "CM_POLICY") {
                  commonGoToSource(firstContextValue, policyOriginCode, firstContextValue, true, true);
              } else {
                  openWebApplication(finalJumpUrl, 'Y')
              }
              firstContextValue = '';
              policyOriginCode = '';
          } else {
              alert(getMessage("core.jumpTo.error.initialized"));
          }
      }
  </script>

  <% if (globalMenuVisibility) { %>
  <!-- BEGIN: menu -->
  <c:if test="${headerHidden != 'Y'}">

  <tr>
      <td>
          <div id="spaceBeforeMenu">
              <table style="border:0px;border-collapse:collapse;cellpadding:0px;cellspacing:0px; width:100%">
                  <tr>
                      <td style="padding:0px;height:1px">
                          <div style="height:100%;width:100%">
                              <img src="<%=corePath%>/images/space.gif?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" width="100%" height="1px"/>
                          </div>
                      </td>
                  </tr>
              </table>
          </div>
      </td>
  </tr>

  <tr>
      <td>
  <div id="globalMenu" class="menu">
    <table style="border:0px;border-collapse:collapse;cellpadding:0px;cellspacing:0px; width:100%">
      <tr>
        <td valign="top" id="globalNavMenus" style="padding: 0px" width="75%">
            <oweb:globalNav></oweb:globalNav>
            <script>
                $("#jMenu").jMenu({});
            </script>
        </td>
            <%
              if(useJqxGrid) {
                  String styleForGlobalSearch = "style='text-align:right; ";
                  styleForGlobalSearch += "display:inline; ";
                  styleForGlobalSearch += "'";
                  String cssClassForGlobalSearch = globalSearchVisibility.equalsIgnoreCase("true") ? "" : "dti-hide";

            %>
          <td width=25% <%= styleForGlobalSearch %> class="<%=cssClassForGlobalSearch%>" id="tdGlobalSearch" align="right" valign="middle">
            <%
               } else {
            %>
            <%
                String styleForGlobalSearch = "style='text-align:right; ";
                styleForGlobalSearch += (globalSearchVisibility.equalsIgnoreCase("true") ? "display:inline; " : "display:none;");
                styleForGlobalSearch += "'";
            %>
        <td width=25% <%= styleForGlobalSearch %> id="tdGlobalSearch" align="right" valign="middle">
            <%
              }
            %>
            <span id="globalSearchContainer">
                <div class="globalSearchButtonHolderContainer">
                    <input id="globalSearch" name="globalSearch" type="text" maxlength="50" class="mainForm"
                           value="<%= globalSearchDefaultHint %>" title="<%= globalSearchDefaultHint %>"
                           onkeydown="javascript: if(event) { if (event.keyCode == 13) {
                                                                  baseOnGlobalSearch(this, event);
                                                                  return false; }
                                                              } return true;"
                           onkeypress="javascript: if (event) {
                                                        return dti.inpututils.replaceTypedChar(event, dti.inpututils.toUpperCase);
                                                    }"
                           onfocus="javascript:this.select();"/>
                    &nbsp;
                    <input name="search" type="button" class="btLightBlue" value="<fmt:message key='label.header.page.search'/>" title="<fmt:message key='label.header.page.search.title'/>"
                           onclick="return baseOnGlobalSearch(getSingleObject('globalSearch'), event);"/>
                </div>
            </span>
        </td>
      </tr>
    </table>
  </div>

      </td>
  </tr>
  </c:if>
<% } %>

  <!-- END: menu -->

  <!-- START: Main Body Content -->
  <tr>
      <td>
  <div>
       <%if(!goodBrowser) {%>
            <center><span class="errortext" style="width:80%;font-size:12pt"><br><fmt:message key="label.required.browser"/></span></center>
        <% } %>


         <table id="mainBodyTable" width="100%" border="0" cellpadding="0" cellspacing="0" style="padding:5px">
             <tr>
                 <td valign=top>
                   <table id="mainTableForPage" width="100%" border="0" cellpadding="2" cellspacing="0" style="display:<%=display%>;" class="<%=cssClass%>">
                       <%@ include file="pageheader.jsp" %>
                       <c:if test="${empty ajaxUrls}">
                           <c:set var="ajaxUrls" value=""></c:set>
                       </c:if>
                       <c:choose>
                        <c:when test="${useJqxGrid}">
                       <tr class="dti-hide">
                        </c:when>
                       <c:otherwise>
                       <tr style="display: none">
                       </c:otherwise>
                       </c:choose>
                           <td>
                               <span id="ajaxUrls" value="<c:out value="${ajaxUrls}"/>"/>
                           </td>
                       </tr>

  </c:otherwise>
</c:choose>
                       <c:choose>
                       <c:when test="${useJqxGrid}">
                           <div id="ajaxDisabled" style="display: block;" class="dti-hide">
                       </c:when>
                       <c:otherwise>
                           <div id="ajaxDisabled" style="display: none;">
                       </c:otherwise>
                       </c:choose>
                               <div style="padding-left:50px;">
                               <span class="errortext" style="width:80%;font-size:12pt;" ><br>
                                This application requires that Internet Explorer native XMLHTTP be enabled. <br/>
                               </span>
                               <span class="errortext" style="width:80%;font-size:11pt;">
                                        1. Open 'Tools' > 'Internet Options'.
                                        <br/>
                                        2. Select the 'Advanced' tab and ensure that the 'Enable native XMLHTTP support' box is checked under the 'Security' subsection.
                                        <br/>
                                        3. Refresh your browser page by pressing F5 key.
                                        <br/>
                                </span>
                               </div>
                           </div>
<!-- Data window rules-->
<%--
<%if (corePath == null || corePath.indexOf("RAMSweb") < 0) {%>
   <oweb:rules/>
<%}%>
--%>
<!--Local Format Internationalization -->
<%@ include file="formatutils.jsp" %>
<%--
    By using a jsp:include directive, your JSP begins executing here.
    You are in the middle of the main content table. Create and close only
    your own row, cell, & embedded table elements.
--%>

<script type="text/javascript">
  $(document).ready(function() {
       $("#savingDialog").dialog({modal:true, autoOpen:false,closeOnEscape:false,dialogClass:'savingIndicator',resizable:false,draggable:false, height:67, width:192});
       $("#processingDialog").dialog({modal:true, autoOpen:false,closeOnEscape:false,dialogClass:'processingIndicator',  position: ['right','top'],height:39, width:120, resizable:false,draggable:false});
       $("#confirmDialog").dialog({modal:false, autoOpen:false,closeOnEscape:false,dialogClass:'confirmDialog',resizable:false,draggable:false, height:146, width:393});
       window.onscroll=basePageOnScroll;
       window.onresize=basePageOnResize;
  });
  var progressIndicatorPosition = "<%=progressIndicatorPosition%>";
</script>
<tr style="display: none">
    <td>
    <div id="savingDialog" title="" align="center" style="display:none">
        <img src="<%=corePath%>/images/running.gif?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" alt="saving"/>
        <span class="txtOrange" >&nbsp;<%=runProcess%></span>
    </div>
    <div id="processingDialog" title="" align="center" style="display:none">
        <img src="<%=corePath%>/images/running.gif?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" alt="saving"/>
        <span class="txtOrange" >&nbsp;<%=runProcess%></span>
    </div>
    <div id="confirmDialog" title="Confirm" align="left" style="display:none">
        <p id="confirmDialogText">&nbsp;</p>
    </div>
    </td>
</tr>
<%
    String datawarehouseServer = ApplicationContext.getInstance().getProperty("datawarehouse.server.URL");
%>
<script type="text/javascript">
   var datawarehouseServer = "<%=datawarehouseServer%>";

   <%
       if (!isLoginPage) {
   %>
       $(window).load(function() {
           xmlhttpNativeCheck();
       });
   <%
       }
   %>
</script>
