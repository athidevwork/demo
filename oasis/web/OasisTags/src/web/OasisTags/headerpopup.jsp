<%--
    Description: Standard dti header for popup pages - no nav

    Instructions for creating your own JSP.
    1. Include the headerpopup.jsp at the beginning of your JSP
    2. Provide your content.
       It should be in the context of an existing table element.
       Create and close your own row, cell & embedded table elements only.
    3. Include footerpopup.jsp at the end of your JSP

    Sample: your.jsp
    ---------------------------------
    <%@ include file="headerpopup.jsp" %>
    <form name="myform" method="post" action="foo.do">
    <tr>
    <td>My content</td>
    </tr>
    </form>
    <jsp:include page="footerpopup.jsp" />
    ---------------------------------
    Author: jbe
    Date: Nov 13, 2003


    Revision Date Revised By Description
    ---------------------------------------------------------------------------
    9/13/2005         jbe    Use type='text/javascript' instead of language='javascript'
    11/01/2006	      mdz	 Add IE7 support
    11/02/2006        GCC    Added include of NumberFormatUtil.js.
    01/23/2007        lmm    Reformated UI with tables
    01/23/2007        wer   Changed appPath to corePath, using the Module.getCorePath();
                            Added javascript functions to get the AppPath, CorePath, CSPath, CISPath;
                            Added baseOnLoad() and baseOnUnload() default handlers, moving the onUnload logic to the baseOnLoad()
    02/07/2007  wer         Added Support for multiple Message Resource Files
    03/09/2007  sxm         Added import of org.apache.struts.xxx per Bill Reeder's request
    07/31/2007  cyzhao      Added javascript function to get the UIStyleEdition
    12/14/2007  Kenney      Issue 77710 - Added the following infomation on OASIS eClaims logo in the upper left corner,
                                                including build number, User ID for the session and database connection
    01/10/2007  James       Add fieldDep.jsp
    03/24/2008  Joe         Skip the header/footer for the popup page as default. Skip page title only if skipPageTitle is true.
    04/09/2008   wer        Enhanced to support configuring a dbPoolId for a role associated with a user or it's group, and removed passing of DBPOOLID as request parameter
    06/23/2008  fcb         83145: changed logic for viewHelp.
    08/26/2008  Fred        Block displaying Processing div if the request url is forgotpassword.jsp
    04/09/2009  mxg         82494: Added include, formatutils.jsp, to handle Date Format Internationalization
    10/07/2009  mgitelm     98004: Changed browser indentificaton
    01/18/2010  James       Issue#101408 Record user access trail
    03/03/2010  James       Issue#104605 For a popup window that is configured to log access to the
                            oasis_access_trail table, a log record is added for the parent window as well
                            as the popup window. The printout only contains data for the popup window,
                            not the parent window.
    04/01/2010  kshen       Added codes to support email text field.
    04/08/2010  kshen       Chenck if SysParmProvider is available before get values for mail client type.
    04/27/2010  kshen       Added no email address message.
    08/30/2010  wfu         109875: Import LocaleUtils to use currency symbol for multiple symbol support.
    10/11/2010  wfu         111776: Move several variables to headerCommonInclude.jsp.
    10/15/2010  wfu         109875 - Change to support Chinese Date format.
    10/19/2010  gzeng       112909 - Set isScrolling to false for special pages.
    10/21/2010  tzhao       109875 - Move the inclusion of xmlproc.js before <%@ include file="headerCommonInclude.jsp" %>.
                                     Because the definition of the currency_symbol has been moved to xmlproc.js.
    12/10/2010  James       115406 - Add support for loading custom styles in a customStyles.css file
    02/16/2011  Blake       Modified for issue 112690:Make Identifier Prominent .
    04/14/2011  mlm         Added obr.js reference.
    09/20/2011  mxg         Issue #100716: Display Type FORMATTEDNUMBER: Added jQuery Plugins
    09/30/2011  bhong       Removed call to initializOasisUser
    11/08/2011  bhong       112837 - Added logics to support standardize processing dialog.
                                     Moved showProcessingImgIndicator and hideProcessingImgIndicator to divpopup.js
    12/09/2011  jxgu        127611 - add html id on tables for page
    01/15/2012  kshen       140326. Added system parameter to control if open the notes window in a new popup window.
    05/07/2013  jxgu        140985 - use jquery dialog
    08/30/2013  jxgu        Issue#147685 upgrade jquery to 1.7.2
    05/15/2015  Elvin       Issue 163132: Replace FCKEditor with latest version of CKEditor
    09/17/2015  Parker      Issue#165637 - Use ThreadLocal to make SimpleDateFormat thread safe.
    10/17/2015  Parker      Issue#166802 Fix the javascript error 'AJAXRequest is undefined'.
    08/26/2016  Elvin       Issue 177515: add velocity URL
    09/11/2017  kshen       Grid replacement. Import the OasisTags common.js. Import the jqx.oasis.css for change
                            the style of jqxGrid to be similar with the current grid in oasis.
    09/21/2017  kshen       Grid replacemnet. Removed jqxDateDisplayFormat and jqxDateTimeDisplayFormat.
                            Added jqxgrid.aggregates.js
    10/12/2017  kshen       Grid replacement: pass event object to baseOnXxx methods for supporting firefox.
    02/21/2018  mlm         191625 - Refactored to add support for different IE_X_UA_Compatible_Value
                                     and HTML fixes to support IE Edge mode.
    04/12/2018  cesar       192560 - check if userSession is available.
    11/13/2018  wreeder     196147 - Change loading-div DIV to support not disabling the page while loading, and to add a blue border to make it stand out more
    ---------------------------------------------------------------------------
    (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ page import="dti.oasis.app.ApplicationContext,
                                 dti.oasis.codelookupmgr.CodeLookupManager,
                                 dti.oasis.security.Authenticator,
                                 dti.oasis.struts.ActionHelper" errorPage="/core/ErrorPage.jsp"%>
<%@ page import="dti.oasis.util.DatabaseUtils"%>
<%@ page import="dti.oasis.util.OasisUser"%>
<%@ page import="dti.oasis.util.DateUtils" %>
<%@ page import="dti.oasis.util.LocaleUtils"%>
<%@ page import="javax.servlet.jsp.PageContext"%>
<%@ page import="java.text.DateFormat"%>
<%@ page import="java.util.Date"%>
<%@ page import="dti.oasis.util.StringUtils" %>
<%@ page import="org.apache.struts.taglib.html.Constants"%>
<%@ page import="org.apache.struts.Globals"%>
<%@ page import="dti.oasis.struts.IOasisAction" %>
<%@ page import="dti.oasis.util.BrowserUtils" %>
<%@ page import="dti.oasis.filter.CharacterEncodingFilter" %>
<%@ page import="dti.oasis.accesstrailmgr.AccessTrailRequestIds" %>
<%@ page import="dti.oasis.accesstrailmgr.AccessTrailManager" %>
<%@ page import="dti.oasis.messagemgr.MessageManager" %>
<%@ page import="dti.oasis.busobjs.YesNoFlag" %>
<%@ page import="dti.oasis.tags.OasisTagHelper" %>
<%@ page import="dti.oasis.request.RequestStorageManager" %>
<%@ page import="dti.oasis.session.pageviewstate.PageViewStateManager" %>
<%@ page import="dti.oasis.http.*" %>
<%@ page import="dti.oasis.util.LogUtils" %>
<%@ page import="java.util.logging.Logger" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="fmt" uri="/WEB-INF/fmt.tld" %>
<%@ taglib prefix="c" uri="/WEB-INF/c.tld"  %>


<jsp:useBean id="pageBean" class="dti.oasis.util.PageBean" scope="request"/>
<jsp:useBean id="userBean" class="dti.oasis.util.OasisUser" scope="session"/>
<%!
    Logger lHeaderPopupJsp = LogUtils.enterLog(getClass(), "headerpopup.jsp");
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

    String IE_X_UA_Compatible_Value = (String) RequestStorageManager.getInstance().get(dti.oasis.http.RequestIds.IE_X_UA_COMPATIBLE_VALUE, "");
    request.setAttribute(dti.oasis.http.RequestIds.IE_X_UA_COMPATIBLE_VALUE, IE_X_UA_Compatible_Value);

    String applicationId = (String)request.getAttribute(dti.oasis.http.RequestIds.APPLICATION_ID);
    if (StringUtils.isBlank(applicationId))
        applicationId = ApplicationContext.getInstance().getProperty("applicationId", "");    

    String passwordChangeJSP = corePath + "/" + Authenticator.getPasswordChangeJSP();
    if (Authenticator.isPasswordExpired(userBean)) {
        request.setAttribute(IOasisAction.PARM_URL, "index.jsp");
        request.getRequestDispatcher(passwordChangeJSP).forward(request, response);
        return;
    }

    boolean requestFromForgotPassword = ((request.getRequestURI() != null) &&
            (request.getRequestURI().indexOf("forgotpassword.jsp") > 0));

    Date last = userBean.getLastLoggedIn();
    String dtLogged = "&nbsp;";
    if (last != null) {
        String usDateTimeString= DateUtils.formatDateTimeUSWithTimeZone(last);
        dtLogged = MessageManager.getInstance().formatMessage("label.header.page.lastAccessed", new String[]{usDateTimeString});
    }

    request.setAttribute(dti.oasis.http.RequestIds.USE_JQX_GRID, OasisTagHelper.isUseJqxGrid(pageContext));

    boolean useJqxGrid = (boolean) request.getAttribute(dti.oasis.http.RequestIds.USE_JQX_GRID);

    String browser = request.getHeader("User-Agent");
    boolean goodBrowser = BrowserUtils.isIE11(browser) || useJqxGrid;
    String display = (goodBrowser) ? "table;" : "none;";
    String cssClass = "";
    if(useJqxGrid) {
        display = "table;";
        cssClass = goodBrowser ? "" : "dti-hide";
    }

    String dbid = getDbPoolId(request);

    String dbTitle = "&nbsp;";
    boolean disallowPassword = YesNoFlag.getInstance(ApplicationContext.getInstance().getProperty(IOasisAction.KEY_ENVDISALLOWPASSWORDCHANGE, "N")).booleanValue();
    boolean subscribeEvents = YesNoFlag.getInstance(ApplicationContext.getInstance().getProperty(IOasisAction.KEY_ENVSUBSCRIBEEVENTS, "N")).booleanValue();
    boolean showUserSettings = (subscribeEvents || !disallowPassword);

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

    String environmentName = "";
    if (SysParmProvider.getInstance().isAvailable()) {
        environmentName = SysParmProvider.getInstance().getSysParm(request, "ENVIRONMENTNAME");
    }
    boolean isEnvironmentNameOverride = false;
    String environmentNameBase = ApplicationContext.getInstance().getProperty("environmentName", "");
    if (StringUtils.isBlank(environmentName))
        environmentName = environmentNameBase;
    else
        isEnvironmentNameOverride = true;

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

    String globalSearchDefaultHint = ApplicationContext.getInstance().getProperty(IOasisAction.KEY_IS_GLOBAL_SEARCH_DEF_HINT, "");
    String globalSearchVisibility = ApplicationContext.getInstance().getProperty(IOasisAction.KEY_IS_GLOBAL_SEARCH_VISIBLE, "true");
    globalSearchVisibility = (StringUtils.isBlank(globalSearchVisibility) ? "true" : globalSearchVisibility);
    pageContext.setAttribute("globalSearchVisibility", globalSearchVisibility, pageContext.APPLICATION_SCOPE);

    String topNavURLs = "";
    Iterator topNavIt = pageBean.getTopNavMenu().iterator();
    while (topNavIt.hasNext()) {
        MenuBean topNav = (MenuBean) topNavIt.next();
        topNavURLs += (StringUtils.isBlank(topNavURLs) ? "" : "~^~") + topNav.getId() + "^" + topNav.getUrl();
    }
    String applicationTitle = (String)request.getAttribute(dti.oasis.http.RequestIds.APPLICATION_TITLE);
    if (StringUtils.isBlank(applicationTitle))
        applicationTitle = ApplicationContext.getInstance().getProperty("applicationTitle", " ");
    if(isEnvironmentNameOverride) {
        applicationTitle = applicationTitle.replace(environmentNameBase, environmentName);
    }

    String strLogoTipInfo = MessageManager.getInstance().formatMessage("label.header.page.logoTipInfo",
            new String[]{applicationTitle, userBean.getUserId(), (String)session.getAttribute(IOasisAction.KEY_PRIOR_LOGIN_TS), ("&nbsp;".equals(dbTitle) || StringUtils.isBlank(dbTitle) ? dbid : dbTitle)});

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<c:if test="${useJqxGrid}">
    <meta name="format-detection" content="telephone=no"/>
</c:if>
<c:if test="${!useJqxGrid}">
    <c:choose>
        <c:when test="${not empty IE_X_UA_Compatible_Value}">
            <meta http-equiv="X-UA-Compatible" content="<%= IE_X_UA_Compatible_Value %>">
        </c:when>
        <c:otherwise>
    <meta http-equiv="X-UA-Compatible" content="IE=8">
        </c:otherwise>
    </c:choose>
</c:if>
<title>
  	<jsp:getProperty name="pageBean" property="title"/>
</title>
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
<!-- CKEditor -->
<script type="text/javascript" src="<%=corePath%>/ckeditor/ckeditor.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<c:if test="${useJqxGrid}">
    <link rel="stylesheet" href="<%=corePath%>/lib/js/jqwidgets-ver4.5.0/styles/jqx.base.css" type="text/css" />
    <script type="text/javascript" src="<%=corePath%>/lib/js/jqwidgets-ver4.5.0/jqxcore.js"></script>
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

<script type="text/javascript">
var isPopup = true;

//this variable is declared in xmlproc.js that derives the logic for multi grid support.
isMultiGridSupported = <%= isMultiGridSupportEnabled %> ;

//set last refresh time
var lastRefreshTime = "<%=getLastRefreshTime()%>";

// Initialize Javascript log debugging. This variable is declared in edits.js
enableJavascriptLogging = "<%= ApplicationContext.getInstance().getProperty("javascript.logging.enable", "false")%>";

var emailClientType = "LOCAL";
var gridExportExcelCsvType = "<%= ApplicationContext.getInstance().getProperty("gridExportExcelCsvType", "XLSX")%>";
<%
    if (SysParmProvider.getInstance().isAvailable()) {
%>
emailClientType = "<%=dti.oasis.util.SysParmProvider.getInstance().getSysParm("CS_EMAIL_CLIENT", "LOCAL")%>";
<%
    }
%>
setMessage("cs.oasisMail.noEmailAddressForClient", "<%=MessageManager.getInstance().formatMessage("cs.oasisMail.noEmailAddressForClient")%>");
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

var __appContextRoot=getAppPath();

<%
    AccessTrailManager.getInstance().initializeForRequest(request);
%>
function logPrintAccess() {

    if (isInIframe() && !isInDivPopup()) {
        //This page is in iframe but not Div Popup. Do not log print event.
    } else {
        if (!window.hasActivePopups || !hasActivePopups()) {
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

<c:choose>
   <c:when test="${UIStyleEdition=='0'}">
      <body onload="basePageOnLoad()" onunload="basePageOnUnload()"  onafterprint="logPrintAccess()">
        <link href="<%=corePath%>/css/oasisnew.css?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" rel="stylesheet" type="text/css"/>
        <link href="<%=corePath%>/css/jquery-ui.css?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" rel="stylesheet" type="text/css"/>
        <table width="100%" border="0" cellpadding="0" cellspacing="0" style="position:absolute; top:0; left:0">
        <tr height="75">
        <td width="180" valign="top" align="left"><div style="position:relative">
        <img src="<%=corePath%>/images/oasislogo.gif">
        <img style="position:absolute; top:0px; left:0px;z-index:0;overflow:hidden;" src="<%=corePath%>/images/topnavswoop.gif">
        </div></td>
        <td width="9999" valign="bottom">
            <div style="position:relative"><span class="apptitle" id="apptitleSpan"><br>&nbsp;<jsp:getProperty name="pageBean" property="title"/></span>
            <img style="position:absolute; top:0px; left:0px; width:100%; height:100%; z-index:-1;overflow:hidden;" src="<%=corePath%>/images/headernewright.jpg">
            </div>
        </td>
        </tr>
        <tr>
        <td colspan="2"><table cellspacing="0" cellpadding="0" border="0"><tr>
        <td class="content" width="2">&nbsp;</td>
        <td class="bodycontent" id="maincontent" valign="top" width="100%">
        <%
            if(!goodBrowser) {
        %>
        <center><span class="errortext" style="width:80%;font-size:12pt"><br><fmt:message key="label.required.browser"/></span></center>
        <%
            }
        %>
        <table width="100%" border="0" cellpadding="2" cellspacing="0" style="display:<%=display%>" class="<%=cssClass%>">
        <tr>
        <td colspan="8" class="spacer6">&nbsp;</td>
        </tr>
  </c:when>
  <c:when test="${UIStyleEdition=='1'}">
      <body class="body" onload="basePageOnLoad(event)" onunload="basePageOnUnload(event)" onmousemove="basePageOnMouseMove(event)"
            onmouseup="basePageOnMouseUp(event)" onreadystatechange="basePageOnReadyStateChange(this)"  onafterprint="logPrintAccess()">
        <c:set var="divPopupStyle" value="width:100%;"></c:set>
        <c:if test="${empty isForDivPopup}">
            <c:set var="isForDivPopup" value="false"></c:set>
        </c:if>
        <c:if test="${isForDivPopup==false}">
            <c:set var="divPopupStyle" value=""></c:set>
        </c:if>


       <link href="<%=corePath%>/css/oasisnew1.css?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" rel="stylesheet" type="text/css" />

        <table width=100% cellspacing=0 cellpadding=0>
            <!-- should the logo and app title be skipped?
            this can be set only for headerpopup.jsp, not for header.jsp -->
            <c:if test="${empty skipHeaderFooterContent}">
                <c:set var="skipHeaderFooterContent" value="true"></c:set>
                <c:set var="skipPageTitle" value="false"></c:set>
            </c:if>
            <c:if test="${empty skipPageTitle}">
                <c:set var="skipPageTitle" value="true"></c:set>
            </c:if>
            <c:if test="${skipHeaderFooterContent == false}">

            <tr class="header">
                <td>
                    <table class="header" cellspacing=0 cellpadding=0>
                        <tr>
                            <td class="header_logo">
                                <img style="width:180px; height:75px;" src="<%=corePath%>/images/dtilogo.gif">
                            </td>
                            <td class="header_cap" style="<c:out value="${divPopupStyle}"></c:out>">
                                <table cellspacing=0 cellpadding=0>
                                    <tr>
                                        <td class="header_info" style="<c:out value="${divPopupStyle}"></c:out>">
                                            <table cellspacing=0 cellpadding=0 style="width:100%;height:100%">
                                                <tr>
                                                    <td valign=bottom>
                                                        <div class="apptitle">&nbsp;<jsp:getProperty name="pageBean" property="title"/></div>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
            </c:if>
            <tr>
                <td>
                    <div id="bodyContent">
                        <div id="content" class="contentFull" style="<c:out value="${divPopupStyle}"></c:out>">
                            <%if(!goodBrowser) {%>
                            <center><span class="errortext" style="width:80%;font-size:12pt"><br><fmt:message key="label.required.browser"/></span></center>
                            <% } %>
                            <table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
                                <tr height=100%>
                                    <td valign=top>
                                    <table width="100%" border="0" cellpadding="2" cellspacing="0" style="display:<%=display%>" class="<%=cssClass%>">
                                      <tr>
                                      <td colspan="8" class="spacer6">&nbsp;</td>
                                      </tr>
</c:when>
<c:otherwise>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>

<body class="body" onload="basePageOnLoad(event)" onunload="basePageOnUnload(event)" onmousemove="<%=onMouseMoveFunction%>"
      onmouseup="<%=onMouseUpFunction%>" onreadystatechange="basePageOnReadyStateChange(this)"
      onafterprint="logPrintAccess()" onkeydown="return baseOnBodyKeyDown(event);">
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
  </script>

  <table class="mainTable" cellpadding="0" cellspacing="0">

   <!-- should the logo and app title be skipped?
  this can be set only for headerpopup.jsp, not for header.jsp -->
  <c:if test="${empty skipHeaderFooterContent}">
      <c:set var="skipHeaderFooterContent" value="true"></c:set>
      <c:set var="skipPageTitle" value="false"></c:set>
  </c:if>
  <c:if test="${empty skipPageTitle}">
      <c:set var="skipPageTitle" value="true"></c:set>
  </c:if>
  <c:if test="${skipHeaderFooterContent == false}">

  <!-- BEGIN: header -->
<tr>
  <td>
  <div class="header" style="<%= "background-image:url('" + productLogoImageFile + "');"%>">
      <div id="headerLogoTips" class="headerLogoTips" title="<%=strLogoTipInfo%>"></div>
      <table border="0" cellpadding="0" cellspacing="0" width="100%">
          <tr>
              <td><img src="<%=corePath%>/images/space.gif" height="1"/></td>
          </tr>
          <tr>
              <td>
                  <div class="txtWhite" style="text-align:right; margin-right: 10px;">
                  </div>
              </td>
          </tr>
      </table>
  </div>
  </td>
</tr>
  </c:if>
  <!-- END: header -->
<c:forEach var="topnavigationitem" items="${pageBean.topNavMenu}" varStatus="status">
<%
  MenuBean topnavitem = (MenuBean) pageContext.getAttribute("topnavigationitem") ;
  if (topnavitem.isLink()) {
      String url=topnavitem.getUrl();
      if(url.startsWith("javascript:openMain")) {
        url = StringUtils.replace(url, "javascript:openMain('", "");
        url = StringUtils.replace(url, "','HELP')", "");
        url = appPath + "/" + url;
        applicationHelpURL = url;
      }
 } %>
</c:forEach>
<script type="text/javascript">
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

    function viewHelp(url){
        if (url == 'null') {
            alert(getMessage("core.help.error.notFound"));
            return;
        }
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
</script>

<!-- START: Main Body Content -->
<tr>
<td>
<div>
     <%if(!goodBrowser) {%>
          <center><span class="errortext" style="width:80%;font-size:12pt"><br><fmt:message key="label.required.browser"/></span></center>
      <% } %>


       <table id="mainBodyTable" width="100%" height="100%" border="0" cellpadding="0" cellspacing="0" style="padding:5px">
           <tr>
               <td valign=top>
                 <table id="mainTableForPage" width="100%" border="0" cellpadding="2" cellspacing="0" style="display:<%=display%>" class="<%=cssClass%>">
                    <!-- should the page title be skipped?
                    this can be set only for headerpopup.jsp, not for header.jsp -->
                    <c:if test="${skipPageTitle == false}">
                      <%@ include file="pageheader.jsp" %>
                    </c:if>
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
                            <span id="ajaxUrls" value="<c:out value="${ajaxUrls}"/>"></span>
                         </td>
                     </tr>
</c:otherwise>
</c:choose>

<!--Date Format Internationalization -->
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
    <div id="confirmDialog" title="Confirm" align="center" style="display:none">
        <p id="confirmDialogText">&nbsp;</p>
    </div>
     </td>
 </tr>
