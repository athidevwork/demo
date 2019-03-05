<%--
    Revision Date    Revised By  Description

    06/18/2009       Kenney      Added getAppPath(), it is used in footer.jsp
    10/05/2009       fcb         Removed the SC_FORBIDDEN response, it is handled now
                                 via regular AppException with error key.
    10/11/2010       wfu         111776: String literals refactoring.
    05/13/2011       mlm         120693: Enhanced to raise generic application exception,
                                 if the required exception didn't get raised from the java layer.
    09/30/2011       bhong       Removed call to initializOasisUser
    11/23/2012       Parker      enhancement issue 138228. Add more detail client message in exception and error page
    04/21/2016       huixu       Issue#169769 Fix WebLogicSecurity.getAuthenticators to work in WebLogic 12.2.1
    ---------------------------------------------------
    (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ page language="java" import="dti.oasis.security.Authenticator,
                                 dti.oasis.struts.ActionHelper,
                                 dti.oasis.struts.IOasisAction,
                                 dti.oasis.util.LogUtils,
                                 dti.oasis.util.PageBean,
                                 dti.oasis.messagemgr.MessageManager,
                                 javax.servlet.http.HttpServletResponse,
                                 java.io.PrintWriter,
                                 java.io.StringWriter,
                                 java.util.ArrayList,
                                 java.util.logging.Logger"
         isErrorPage="true" %>
<%@ page import="dti.oasis.app.AppException"%>
<%@ page import="dti.oasis.http.Module"%>
<%@ page import="dti.oasis.app.ApplicationContext" %>
<%@ page import="dti.oasis.filter.CharacterEncodingFilter" %>
<%@ page import="dti.oasis.busobjs.YesNoFlag" %>
<%@ page import="java.util.Date" %>
<%@ page import="dti.oasis.util.DateUtils" %>
<%@ page import="java.util.logging.Level" %>
<%@ page import="dti.oasis.error.ConfiguredDBException" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="/WEB-INF/c.tld"  %>
<%
    Logger l = LogUtils.getLogger("ErrorPage.jsp");
    String verboseMsg = null;
    String displayableMessage = "An error occurred. Please contact support";
    boolean isConfiguredDBException = false;
    Throwable e = null;

    String remoteAddress = request.getRemoteAddr();
    remoteAddress = remoteAddress == null ? "" : remoteAddress;
    String userId = ActionHelper.getCurrentUserId(request);
    String forwardedFor = request.getHeader("X-Forwarded-For");
    forwardedFor = forwardedFor == null ? "" : forwardedFor;
    String currentTime = DateUtils.formatDateTimeAndTimeZone(new Date());
    PageBean pb = (PageBean) request.getAttribute(IOasisAction.KEY_PAGEBEAN);
    String originalPageName = "";
    if (pb != null) {
        originalPageName = pb.getTitle() + " (" + pb.getId()+ ")" ;
    }

    Object keyError = request.getAttribute(IOasisAction.KEY_ERROR);
    if (keyError instanceof String) {
        verboseMsg = (String) keyError;
    }
    else if (keyError instanceof Throwable) {
        e = (Throwable) keyError;
    }
    if (e == null) {
        e = exception;
    }

    if (e != null && e instanceof AppException && (((AppException) e).getMessageKey() != null)) {
        if(e instanceof ConfiguredDBException){
            isConfiguredDBException = true;
        }
        try {
            AppException ae = (AppException) e;
            // If nested exception is an IllegalException, output the message from the nested exception.
            if (ae.getCause() != null && ae.getCause() instanceof IllegalAccessException) {
                verboseMsg = ae.getCause().getMessage();
            }
            else if (ae.hasMessageParameters()) {
                verboseMsg = MessageManager.getInstance().formatMessage(ae.getMessageKey(), ae.getMessageParameters());
            }
            else {
                verboseMsg = MessageManager.getInstance().formatMessage(ae.getMessageKey());
            }
            displayableMessage = verboseMsg;
        }
        catch (Exception e1) {
            verboseMsg = (e.getMessage() != null) ? e.getMessage() : "An application error has occurred." +
                " Please contact support.";
        }
    }
    else {
        if (e != null) {
            if (e.getMessage() != null) {
                verboseMsg = e.getMessage();
                displayableMessage = verboseMsg;
            }
            if (!(e instanceof IllegalAccessException)) {
                verboseMsg += "An application error has occurred. Please contact support.";
            }
        } else {
            // The java layer did not raise any exception - instead it did a blind forward to ErrorPage.JSP page.
            // So, raise a generic application exception instead of throwing 500 - Internal Server Error.
            // (Issue 120693)

            verboseMsg = "An application error has occurred. Please contact support.";
            e = new AppException(verboseMsg);
        }
    }
    Level logLevel = Level.SEVERE;
    if (verboseMsg.startsWith("Connection reset by peer")) {
        logLevel = Level.INFO;
    }
    if (l.isLoggable(logLevel)) {
        l.logp(logLevel, "", "", "*** ErrorPage.jsp: " + verboseMsg + " URI:" + request.getRequestURI());
    }

    StringWriter s = new StringWriter();
    e.printStackTrace(new PrintWriter(s));
    s.flush();

    if (logLevel == Level.INFO) {
        logLevel = Level.FINE;
    }
    if (l.isLoggable(logLevel)) {
        l.logp(logLevel, "", "", "***CAUGHT EXCEPTION: " + s.toString());
    }
    s.close();

    PageBean bean = new PageBean();
    bean.setLeftNavActions(new ArrayList());
    bean.setLeftNavMenu(new ArrayList());
    bean.setTopNavMenu(new ArrayList());
    bean.setTitle("Error");
    request.setAttribute(IOasisAction.KEY_PAGEBEAN, bean);

    boolean verbose = YesNoFlag.getInstance(ApplicationContext.getInstance().getProperty(IOasisAction.KEY_ENVVERBOSEERROR, "FALSE")).booleanValue();

    String UIStyleEdition = (String) request.getAttribute("UIStyleEdition");
    if (UIStyleEdition == null) {
        UIStyleEdition = ApplicationContext.getInstance().getProperty("UIStyleEdition", "0");
        request.setAttribute("UIStyleEdition", UIStyleEdition);
    }
%>
<%-- This is where header.jsp used to be!!!--%>
<jsp:useBean id="userBean" class="dti.oasis.util.OasisUser" scope="session"/>
<%
    String encoding = ApplicationContext.getInstance().getProperty(CharacterEncodingFilter.CHARACTER_ENCODING_DEFAULT);
    response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
    response.setHeader("Cache-control", "no-store, no-cache, must-revalidate");
    response.addHeader("Cache-Control", "post-check=0, pre-check=0");
    response.setHeader("Content-Type", "text/html; charset=" + encoding);
    response.setHeader("Pragma", "no-cache");
    String appPath = request.getContextPath();
    String corePath = Module.getCorePath(request);
%>
<jsp:useBean id="pageBean" class="dti.oasis.util.PageBean" scope="request"/>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=8">
    <title>Error</title>
    <script type="text/JavaScript">
        function MM_preloadImages() { //v3.0
            var d = document;
            if (d.images) {
                if (!d.MM_p)
                    d.MM_p = new Array();
                var i,j = d.MM_p.length,a = MM_preloadImages.arguments;
                for (i = 0; i < a.length; i++)
                    if (a[i].indexOf("#") != 0) {
                        d.MM_p[j] = new Image;
                        d.MM_p[j++].src = a[i];
                    }
            }
        }

        function MM_swapImgRestore() { //v3.0
            var i,x,a = document.MM_sr;
            for (i = 0; a && i < a.length && ( x = a[i]) && x.oSrc; i++)
                x.src = x.oSrc;
        }

        function MM_findObj(n, d) { //v4.01
            var p,i,x;
            if (!d) d = document;
            if ((p = n.indexOf("?")) > 0 && parent.frames.length) {
                d = parent.frames[n.substring(p + 1)].document;
                n = n.substring(0, p);
            }
            if (!(x = d[n]) && d.all)
                x = d.all[n];
            for (i = 0; !x && i < d.forms.length; i++)
                x = d.forms[i][n];
            for (i = 0; !x && d.layers && i < d.layers.length; i++)
                x = MM_findObj(n, d.layers[i].document);
            if (!x && d.getElementById)
                x = d.getElementById(n);
            return x;
        }

        function MM_swapImage() { //v3.0
            var i,j = 0,x,a = MM_swapImage.arguments;
            document.MM_sr = new Array;
            for (i = 0; i < (a.length - 2); i += 3)
                if ((x = MM_findObj(a[i])) != null) {
                    document.MM_sr[j++] = x;
                    if (!x.oSrc)
                        x.oSrc = x.src;
                    x.src = a[i + 2];
                }
        }
        function getAppPath() {
            return '<%=appPath%>';
        }
    </script>
</head>

<c:choose>
   <c:when test="${UIStyleEdition=='0'|| UIStyleEdition=='1'}">
      <c:if test="${UIStyleEdition=='0'}">
          <link href="<%=corePath%>/css/oasisnew.css?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" rel="stylesheet" type="text/css"/>
      </c:if>
      <c:if test="${UIStyleEdition=='1'}">
          <link href="<%=corePath%>/css/oasisnew1.css?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" rel="stylesheet" type="text/css"/>
      </c:if>

     <body onload="MM_preloadImages('<%=corePath%>/images/orangebutton_f2.jpg')">
     <table width="100%" border="0" cellpadding="0" cellspacing="0" style="position:absolute; top:0; left:0">
         <tr height="75">
             <td width="180" valign="top" align="left"><div style="position:relative">
                 <img src="<%=corePath%>/images/oasislogo.gif?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>">
                 <img style="position:absolute; top:0px; left:0px;-index:0;overflow:hidden;"
                      src="<%=corePath%>/images/topnavswoop.gif?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>">
             </div></td>
             <td width="9999" valign="bottom">
                 <div style="position:relative">
                     <img style="position:absolute; top:0px; left:0px; width:100%; height:100%; z-index:-1;overflow:hidden;"
                          src="<%=corePath%>/images/headernewright.jpg?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>">
                 </div>
             </td>
         </tr>
         <tr>
             <td colspan="2">
             <table cellspacing="0" cellpadding="0" border="0"><tr>
                 <td class="content" width="2">&nbsp;</td>
                 <td class="bodycontent" id="maincontent" valign="top" width="100%">
                 <table width="100%" border="0" cellpadding="2" cellspacing="0">
                     <tr>
                         <td colspan="8" class="spacer6">&nbsp;</td>
                     </tr>
                     <%-- END header.jsp used to be --%>
   </c:when>
   <c:otherwise>
      <link href="<%=corePath%>/css/dti.css?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" rel="stylesheet" type="text/css"/>
      <link href="<%=corePath%>/css/button.css?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" rel="stylesheet" type="text/css"/>
      <body class="body">
          <!-- BEGIN: header -->
          <div class="header">
            <table border="0" cellpadding="0" cellspacing="0" width="100%">
              <tr>
                <td><img src="<%=corePath%>/images/space.gif?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" width="990" height="1" /></td>
              </tr>
              <tr>
                <td>
                  <div class="txtWhite" style="text-align:right; margin-right: 10px;">
                  </div>
                </td>
              </tr>
            </table>
          </div>
          <!-- END: header -->
          <!-- START: Main Body Content -->
              <div>
                 <table width="100%" border="0" cellpadding="0" cellspacing="0" style="padding:5px">
                     <tr>
                         <td valign=top>
                           <table width="100%" border="0" cellpadding="2" cellspacing="0">
                               <tr>
                                   <td colspan="8">&nbsp;</td>
                               </tr>

   </c:otherwise>
 </c:choose>


                <script type='text/javascript'>
                    <% if(verbose) { %>
                    function showErr(showIt) {
                        if (showIt) {
                            document.all("stack").style.display = 'block';
                            document.all("showbtn").style.display = 'none';
                        }
                        else {
                            document.all("stack").style.display = 'none';
                            document.all("showbtn").style.display = 'block';
                        }
                    }
                    <% } %>
                    function home() {
                        var appContext = "<%=appPath%>";
                        document.frmErr.action = appContext;
                        document.frmErr.submit();
                    }
                    function logoff() {
                        var logoutPage = "<%=corePath + "/logout.jsp"%>";
                        document.frmErr.action = logoutPage;
                        document.frmErr.submit();
                    }
                </script>

                <form name="frmErr">
                    <% if(verbose) {%>
                    <tr><td class="headerbox top left right bottom">
                        The following error was encountered:</td></tr>
                    <tr><td class="errortext"><%=verboseMsg%></td></tr>
                    <tr><td>
                    <% if(isConfiguredDBException) {%>
                        <li><fmt:message key="appException.configured.db.error.prefix" />&nbsp;<fmt:message key="label.login.error.click" /> <a href="<%=corePath + "/refreshparms.jsp"%>"  target="_BLANK"><fmt:message key="label.login.error.here"/></a> <fmt:message key="appException.configured.db.error.suffix"/>
                    <% } %>
                    </td></tr>
                    <% } else { %>
                    <tr><td class="errortext"><%=displayableMessage%></td></tr>
                    <tr><td>
                        <li><fmt:message key="label.login.error.click" /> <a href="mailto:<fmt:message key="label.login.support.mailto" />"><fmt:message key="label.login.error.here"/></a> <fmt:message key="label.login.error.emailSupport"/>
                        <li><fmt:message key="label.login.error.phoneSupport"/>
                            <fmt:message key="label.login.phonecontact"/>.
                    </td></tr>
                    <% } %>
                    <tr><td>&nbsp;</td></tr>
                    <tr><td>
                        <input type=button class="buttonText" value="Go Back" onClick="javascript:history.go(-1);">
                        &nbsp;<input type=button class="buttonText" value="Home" onClick="javascript:home();">
                        &nbsp;<input type=button class="buttonText" value="Log off" onClick="javascript:logoff();">
                    </td></tr>

                    <tr><td>&nbsp;</td></tr>
                    <tr><td><b>Page Name:</b> <%=originalPageName%></td></tr>
                    <tr><td><b>Oasis User Name:</b> <%=userId%></td></tr>
                    <tr><td><b>Error Time:</b> <%=currentTime%></td></tr>
                    <tr><td><b>Remote IP Address:</b> <%=remoteAddress%></td></tr>
                    <tr><td><b>X-Forwarded-For:</b> <%=forwardedFor%></td></tr>
                    <tr><td>&nbsp;</td></tr>

                    <% if(verbose) {%>
                    <tr><td>&nbsp;
                        <div id="showbtn">
                            <input type=button class="buttonText" value="Show Details"
                                   onClick="javascript:showErr(true);">
                        </div>

                        <div id="stack" style="display: none;">
                            <table><tr><td>
                                <input type=button class="buttonText" value="Hide Details"
                                       onClick="javascript:showErr(false);"></td></tr>
                                <tr><td class="bottom"><b>Stack Trace:</b></td></tr>
                                <tr><td class="top left bottom right">
                                    <pre style="font-size:8pt">
                                        <%e.printStackTrace(new PrintWriter(out));%>
                                    </pre>
                                </td></tr></table>
                        </div></td></tr>
                    <% } %>
<jsp:include page="footer.jsp"/>
