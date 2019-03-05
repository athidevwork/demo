<%@ page import="dti.oasis.app.ApplicationContext" %>
<%@ page import="dti.oasis.http.Module" %>
<%@ page import="dti.oasis.security.Authenticator" %>
<%@ page import="dti.oasis.struts.IOasisAction" %>
<%@ page import="dti.oasis.util.PageBean" %>
<%@ page import="dti.oasis.util.StringUtils" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="dti.oasis.messagemgr.MessageManager" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="dti.oasis.messagemgr.Message" %>
<%@ page import="dti.oasis.util.DateUtils" %>
<%@ page import="dti.oasis.http.RequestIds" %>
<%@ page import="dti.oasis.tags.OasisTagHelper" %>
<%@ page import="dti.oasis.struts.ActionHelper" %>
<%@ page import="dti.oasis.util.SysParmProvider" %>
<%@ page language="java" %>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>

<%--
  Description:

  Author: mlmanickam
  Date: Apr 10, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
    04/10/2007       mlm       JSP creation
    04/09/2008       wer       removed passing of DBPOOLID as request parameter
    07/10/2008       Fred      Eliminate js error for Forgot Your Password page
    05/11/2008       mlm       Update for logo url reference
    02/16/2010       fcb       When msgClass = "successtext", removed the text "Error:"
    10/11/2010       wfu       111776: String literals refactoring.
    12/19/2011       bhong     118066 - Allow forgot password page to be configured with external URL.
    08/30/2013       jxgu      Issue#147685 upgrade jquery to 1.7.2
    09/22/2017       kshen     Grid replacement. Use onkeypress event instand of onkeydown event to avoid submit form
                               when pressing enter key on autocomplete box.
    10/12/2017       kshen     Grid replacement. Supporting firefox.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<jsp:useBean id="pageBean" class="dti.oasis.util.PageBean" scope="request"/>

<%!
    public String getMaintenanceMessage(HttpServletRequest request) {
        String maintenanceMsg = null;
        try {
            maintenanceMsg = SysParmProvider.getInstance().getSysParm(request, "MAINTENANCE_MESSAGE");
        } catch (Exception e){
            // Ignore if not found
        }

        if (StringUtils.isBlank(maintenanceMsg)) {
            try {
                maintenanceMsg = MessageManager.getInstance().formatMessage("label.maintenanceMsg");
            } catch (Exception e) {
                // Ignore if not found
            }
        }
        if (null == maintenanceMsg) {
            maintenanceMsg = "";
        }
        return maintenanceMsg;
    }
%>
<%
    String UIStyleEdition = (String) request.getAttribute("UIStyleEdition");
    if (UIStyleEdition == null) {
        UIStyleEdition = ApplicationContext.getInstance().getProperty("UIStyleEdition", "0");
        request.setAttribute("UIStyleEdition", UIStyleEdition);
    }

    String productLogoConfigKey = "productLoginBG" + (UIStyleEdition.equalsIgnoreCase("0") || UIStyleEdition.equalsIgnoreCase("1") ? "" : UIStyleEdition.trim());
    String productLoginLogoImageFile = Module.getRelativePath(request, ApplicationContext.getInstance().getProperty(productLogoConfigKey, "productlogo.jpg"));
    productLoginLogoImageFile += "?build.number=" + ApplicationContext.getInstance().getProperty(IOasisAction.KEY_BUILD_NUMBER);

    boolean isEnvSpecified = ApplicationContext.getInstance().getProperty("environmentName", "").equals("")?false:true;

    String userId = "";
    String error = "";
    String dbpool = "";
    String msgClass = "errortext";
    Object o = request.getAttribute(IOasisAction.KEY_ERROR);
    if (o instanceof Exception)
        error = ((Exception) o).getMessage();
    if (o instanceof String)
        error = (String) o;
    if (StringUtils.isBlank(error)) {
        error = (String) request.getAttribute("enrollMessage");
        if (!StringUtils.isBlank(error))
            msgClass = "successtext";
    }
    if (request.getParameter("j_username") != null) {
        userId = request.getParameter("j_username");
        dbpool = request.getParameter(IOasisAction.KEY_DBPOOLID);

        if (StringUtils.isBlank(error))
            error = MessageManager.getInstance().formatMessage("label.login.error.invalidInfo");
    }
    if (error == null) error = "";

    PageBean bean = new PageBean();
    bean.setLeftNavActions(new ArrayList());
    bean.setLeftNavMenu(new ArrayList());
    bean.setTopNavMenu(new ArrayList());
    String appTitle = ApplicationContext.getInstance().getProperty("applicationTitle", MessageManager.getInstance().formatMessage("label.login.error.pleaseLogin"));
    bean.setTitle(appTitle);
    request.setAttribute(IOasisAction.KEY_PAGEBEAN, bean);
    session.removeAttribute(IOasisAction.PARM_LOGIN);
    String autoCompleteInd = ApplicationContext.getInstance().getProperty(IOasisAction.KEY_ENVDISABLELOGINAUTOCOMPLETE, "N").toUpperCase();
    boolean autoCompleteOff = false;
    if (autoCompleteInd.equals("Y") || autoCompleteInd.equals("YES") || autoCompleteInd.equals("TRUE"))
        autoCompleteOff = true;
    String autoComplete = "";
    if (autoCompleteOff)
        autoComplete = "AUTOCOMPLETE=\"OFF\"";

    String appPath = request.getContextPath();
    String corePath = Module.getCorePath(request);
    String csPath = Module.getCSPath(request);

    // Use the external.forgot.password.URL if it is defined as the URL for the fogot password link.
    String forgotPasswordURL = ApplicationContext.getInstance().getProperty("external.forgot.password.URL", "forgotpassword.jsp");
//  error = "Invalid userid and/or password";

    String runProcess = MessageManager.getInstance().formatMessage("label.process.info.processing");
    String progressIndicatorPosition = ApplicationContext.getInstance().getProperty("processingDialog.Position","DEFAULT");

    request.setAttribute(dti.oasis.http.RequestIds.USE_JQX_GRID, OasisTagHelper.isUseJqxGrid(pageContext));
    boolean useJqxGrid = (boolean) request.getAttribute(dti.oasis.http.RequestIds.USE_JQX_GRID);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=8">
<title>
    <jsp:getProperty name="pageBean" property="title"/>
</title>

<link rel="shortcut icon" href="<%=corePath%>/images/logo-ftr.gif" />
<link href="<%=corePath%>/css/dti.css?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" rel="stylesheet" type="text/css"/>
<link href="<%=corePath%>/css/button.css?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" rel="stylesheet" type="text/css"/>
<link href="<%=corePath%>/css/jquery-ui.css?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" rel="stylesheet" type="text/css"/>
<link href="<%=corePath%>/customStyles.jsp?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" rel="stylesheet" type="text/css"/>

<script type="text/javascript" src="<%=corePath%>/js/jquery-1.11.2.min.js"></script>
<script type="text/javascript" src="<%=corePath%>/js/jquery-ui-1.10.3.js"></script>
<script type="text/javascript" src="<%=corePath%>/js/gui.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=corePath%>/js/validation.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=corePath%>/js/scriptlib.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=corePath%>/js/edits.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=corePath%>/js/sessionTimeout.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=corePath%>/js/divPopup.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=corePath%>/js/asynchttp.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<script type="text/javascript">
    $(document).ready(function() {
        $("#processingDialog").dialog({modal:true, autoOpen:false,closeOnEscape:false,dialogClass:'processingIndicator',  position: ['right','top'],height:39, width:120, resizable:false,draggable:false});
    });
    var progressIndicatorPosition = "<%=progressIndicatorPosition%>";

    // Page View State Cache Key
    var PAGE_VIEW_STATE_CACHE_KEY = "<%= dti.oasis.http.RequestIds.CACHE_ID_FOR_PAGE_VIEW%>";
     <%
        MessageManager jsMessageManager = MessageManager.getInstance();
        jsMessageManager.addJsMessage("login.alreadyExist.warning");
        if (jsMessageManager.hasJsMessages()) {
            Iterator iter = jsMessageManager.getJsMessages();
            while(iter.hasNext()){
                Message message = (Message) iter.next();
    %>
    setMessage("<%=message.getMessageKey()%>", "<%=message.getMessage()%>");
    <%
            }
        }
    %>
    function getAppPath() {
        return '<%=appPath%>';
    }
    function getCorePath() {
        return '<%=corePath%>';
    }

     function getCSPath() {
         return '<%=csPath%>';
     }

    function forgotPassword() {
        var a = getFormActionAttribute();
        var t = document.forms[0].target;
        document.forms[0].action = '<%=forgotPasswordURL%>';
        document.forms[0].target = '_FGT';
        submitFirstForm();
        document.forms[0].action = a;
        document.forms[0].target = t;
    }

    function login() {
        var appPath = getAppPath();
        if (appPath.indexOf("ePolicy") != -1) {
            var url = getAppPath() + "/getsessionusername";
            new AJAXRequest("get", url, '', checkSessionUserName, false);
        }
        else {
            submitForm();
        }
    }

    function checkSessionUserName(ajax) {
        if (ajax.readyState == 4) {
            if (ajax.status == 200) {
                var data = ajax.responseText;
                var userIdInSession = data.toUpperCase();
                var inputUserName = document.forms[0].j_username.value.toUpperCase();
                //if a user has existed in session
                if ((userIdInSession != null) && (userIdInSession != "")) {
                    if (userIdInSession != inputUserName) {
                         var params = new Array(userIdInSession, inputUserName);
                        if (confirm(getMessage("login.alreadyExist.warning", params))) {
                            submitForm();
                        }
                    }
                    else {
                        submitForm();
                    }
                }
                else {
                    submitForm();
                }
            }
        }
    }

    function submitForm() {
        showProcessingImgIndicator();
        submitFirstForm();
    }
    function handleOnKeyPress(evt) {
        var code = evt.keyCode;
        if (code == 13) {
            login();
            return false;
        }
        return true;
    }

    function moveFocusToUserId() {
       try {
           getSingleObject('j_username').focus();
       } catch(err) {
       }
    }
</script>

</head>
<body class="loginBody" onload="javascript:moveFocusToUserId();">
<div id="processingDialog" title="" align="center" style="display:none">
    <img src="<%=corePath%>/images/running.gif?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" alt="processing"/>
    <span class="txtOrange" >&nbsp;<%=runProcess%></span>
</div>
<form id="loginform" action="j_security_check" method="post" name="processor">
<div class="centerWrapper">

<div class="loginTop">
        <span>
          <c:choose>
              <c:when test='${empty param.uc}'>
                  <fmt:message key="label.login.topcopy"/>
              </c:when>
              <c:otherwise>
                  <fmt:message key="label.login.topcopy.${param.uc}"/>
              </c:otherwise>
          </c:choose>
        </span>

    <%
        if (error != "" && msgClass == "successtext") {
    %>
    <div class="loginError">
        <span class="txtOrange"></span> <span class="txtBdRgt">  <%=error%> </span>
    </div>
    <%  }
        else if (error != "") {
    %>
    <div class="loginError">
        <span class="txtOrange"><fmt:message key="label.login.error.error"/></span> <span class="txtBdRgt">  <%=error%> </span>
    </div>
    <% } %>

</div>
<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr>
    <td>
        <% if(isEnvSpecified) { %>
        <div class="envLogin"><div id="test" class="loginEnvInfo">
                <%=ApplicationContext.getInstance().getProperty("applicationTitle", " ")%><br>
                <%=getMaintenanceMessage(request)%>
        </div>
        <%}%>
    </td>
</tr>
<tr>
    <td style="text-align: center;vertical-align: top;">
<div class="mainLogin" style="<%= "background-image:url('" + productLoginLogoImageFile + "');"%>">
<div class="loginLeft">
    <span class="txtGreyBold"><fmt:message key="label.header.page.broughtTo"/></span>
    <br><br>
    <img src="<%= corePath + "/images/login-dtilogo.gif?build.number=" + ApplicationContext.getInstance().getProperty(IOasisAction.KEY_BUILD_NUMBER) %>" border="0" width="167" height="98"/>
</div>

<div class="loginRight">
    <table border="0" cellpadding="3" cellspacing="0">
        <tr>
            <td></td>
            <td><img src="<%= corePath + "/images/loginTxt.gif?build.number=" + ApplicationContext.getInstance().getProperty(IOasisAction.KEY_BUILD_NUMBER) %>" border="0" width="101" height="21"/></td>
        </tr>
        <%
            if (autoCompleteOff){
                if(useJqxGrid){
        %>
                    <input type="password" class="dti-hide"/>
        <%
                } else {
        %>
                    <input type="password" style="display:none;"/>
        <%
                }
            }
        %>
        <tr>
            <td class="txtRight"><span class="txtBoldRight"><fmt:message key="label.header.page.userId"/></span></td>
            <td class="txtLeft"><input name="j_username" value="<%=userId%>" onkeypress="handleOnKeyPress(event);" type="text"
                                       class="mainForm"
                                       style="width: 170px" <%=autoComplete%> /></td>
        </tr>
        <tr>
            <td class="txtRight"><span class="txtBoldRight"><fmt:message key="label.header.page.password"/></span></td>
            <td class="txtLeft"><input name="j_password" type="password" class="mainForm" onkeypress="handleOnKeyPress(event);"
                                       style="width: 170px" <%=autoComplete%> /></td>
        </tr>
        <tr>
            <td></td>
            <td><input name="btnSearch" type="button" class="btBlueMedium" value="<fmt:message key='label.login.error.login'/>" title="<fmt:message key='label.header.page.logOff.title'/>"
                       onclick="login();"/></td>
        </tr>
    </table>
</div>

<div class="loginTxt">
    <span class="txtBlueBold"><fmt:message key="label.login.problems"/></span><br>

    <ul class="login">
        <!--if label.login.forgotPassword is not configured in resources file, do not display the link to forgotPassword page  -->

        <c:set var="loginForgotPassword">
            <fmt:message key="label.login.forgotPassword"/>
        </c:set>
        <c:if test="${ not empty loginForgotPassword }">
            <li>
                <a href="javascript:forgotPassword();" class="lkWhite"
                   title="<fmt:message key='label.login.error.retrievePassword' />">
                    <c:out value="${loginForgotPassword}"/>
                </a>
            </li>
        </c:if>
        <c:remove var="loginForgotPassword"/>

        <!--if label.login.support.mailto is not configured, then do not display the text: to contact support via email. -->
        <fmt:message key='label.login.support.mailto' var="supportEmail"/>
        <c:if test="${not empty supportEmail }">
            <li>
                <a href="mailto:<c:out value='${supportEmail}'/>"
                   class="lkWhite" title="<fmt:message key='label.login.error.contactSupport' />"><fmt:message key="label.login.error.click" /> <fmt:message key="label.login.error.here" /></a>
                <span class="txtBlueBold11px"><fmt:message key='label.login.error.emailSupport' /></span>
            </li>
        </c:if>
        <c:remove var="supportEmail"/>

        <fmt:message key='login.support.webPage' var="supportWebPage"/>
        <c:if test="${not empty supportWebPage }">
            <fmt:message key='label.login.support.webPage' var="supportWebPageLabel"/>
            <li>
                <a href="<c:out value='${supportWebPage}'/>"
                   class="lkWhite" title="<c:out value='${supportWebPageLabel}'/>"><fmt:message key="label.login.error.click" /> <fmt:message key="label.login.error.here" /></a>
                        <span class="txtBlueBold11px"> <c:out value='${supportWebPageLabel}'/>
                        </span>
            </li>
        </c:if>
        <c:remove var="supportWebPage"/>

    </ul>

    <!--if label.login.phonecontact is not configured in resources file, then do not the text:display contact support by phone -->
    <c:set var="loginPhoneContact">
        <fmt:message key="label.login.phonecontact"/>
    </c:set>
    <c:if test="${ not empty loginPhoneContact }">
        <span class="txtBlueBold11px"><fmt:message key="label.login.error.phoneSupport" /> <c:out value="${loginPhoneContact}"/>.</span>
    </c:if>
    <c:remove var="loginPhoneContact"/>

    <% if (ApplicationContext.getInstance().getProperty(IOasisAction.KEY_ENVCANENROLL, "N").equals("Y")) { %>
    <p/>
    <span style="padding-left:1px;" class="txtBlueBold"><fmt:message key="label.enroll.loginprompt"/></span>
    <br>
    <ul class="login">
        <li>
            <span class="txtBlueBold11px"><fmt:message key="label.login.error.havePolicy"/> </span>
            <a class="lkWhite" href="enroll.jsp"><fmt:message key="label.login.error.click" /> <fmt:message key="label.login.error.here" /></a>
            <span class="txtBlueBold11px"> <fmt:message key="label.login.error.obtainId" /></span>
        </li>
        <%
            if (ApplicationContext.getInstance().getProperty(IOasisAction.KEY_ENVCANREGISTERCIS, "N").equals("Y")) {
                String url = ApplicationContext.getInstance().getProperty(IOasisAction.KEY_ENVREGISTERCISPERSONURL, null);
                if (!StringUtils.isBlank(url)) {
        %>
        <li>
            <span class="txtBlueBold11px"><fmt:message key="label.login.error.noPolicy" /> </span>
            <fmt:message key="label.login.error.canStill" /> <a class="lkWhite" href="<%=url%>"><fmt:message key="label.login.error.register" /></a>.
        </li>
        <% }
            url = ApplicationContext.getInstance().getProperty(IOasisAction.KEY_ENVREGISTERCISORGURL, null);
            if (!StringUtils.isBlank(url)) {
        %>
        <li><span class="txtBlueBold11px"><fmt:message key="label.login.error.orgRegister" /> </span> <a class="lkWhite" href="<%=url%>"><fmt:message key="label.login.error.here" /></a>.
        </li>
        <%
                }
            } %>
    </ul>
    <% }
    %>
    <p/>
    <span><fmt:message key="label.login.bottomcopy"/></span>
</div>

<!-- BEGIN: footer -->
<div class="loginFooter">&copy;
    <fmt:message key="label.footer.copyright"/>
</div>
<!-- END: footer -->
</div>
    </td>
</tr>
</table>
</div>
</form>
</body>
</html>
