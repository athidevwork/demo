<%@ page import="dti.oasis.request.RequestLifecycleAdvisor" %>
<%@ page import="java.util.logging.Logger" %>
<%@ page import="dti.oasis.http.Module" %>
<%@ page import="dti.oasis.healthcheckmgr.HealthCheckManager" %>
<%@ page import="dti.oasis.healthcheckmgr.impl.HealthCheckManagerImpl" %>
<%@ page import="dti.oasis.app.ApplicationContext" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="dti.oasis.healthcheckmgr.HealthCheckWebAppModule" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="dti.oasis.util.*" %>
<%@ page language="java" %>
<%--
  Description: Heath Check of the Web Services.

  Author: fcb
  Date: Dec 7, 2010


  Revision Date    Revised By  Description
  ---------------------------------------------------
  ---------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%--<%@ taglib prefix="fmt" uri="/WEB-INF/fmt.tld" %>--%>
<%@ taglib prefix="oweb" uri="/WEB-INF/oasis-web.tld" %>
<%@ taglib prefix="c" uri="/WEB-INF/c.tld"  %>

<%
    HealthCheckManager healthCheckManager = (HealthCheckManager) dti.oasis.app.ApplicationContext.getInstance().getBean("HealthCheckManager");
    PageBean bean = new PageBean();
    bean.setLeftNavActions(new ArrayList());
    bean.setLeftNavMenu(new ArrayList());
    bean.setTopNavMenu(new ArrayList());
    bean.setTitle("Health Check");
    request.setAttribute(IOasisAction.KEY_PAGEBEAN, bean);
%>

<c:set var="skipHeaderFooterContent" value="true"></c:set>
<c:set var="skipPageTitle" value="true"></c:set>

<%
    try {
        RequestLifecycleAdvisor.getInstance().initialize(request);
        final String PAGE_NAME = "healthCheck.jsp";
        Logger l = LogUtils.enterLog(getClass(), PAGE_NAME);
    
        response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        response.setHeader("Cache-control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");

        ApplicationContext.getInstance().exposeMessageSourceForJstl(pageContext.getServletContext(), request);
        String applicationName = ApplicationContext.getInstance().getApplicationName();
%>
<%@ include file="headerpopup.jsp" %>
<script type="text/javascript">
    function getAppPath() {
        return '<%=appPath%>';
    }
    function getCorePath() {
        return '<%=corePath%>';
    }
</script>
</head>

<link href="<%=corePath%>/css/dti.css" rel="stylesheet" type="text/css"/>
<link href="<%=corePath%>/css/button.css" rel="stylesheet" type="text/css"/>

<body class="body">
<div class="header">
    <table border="0" cellpadding="0" cellspacing="0" width="100%">
        <tr>
            <td><img src="<%=corePath%>/images/space.gif" width="990" height="1"/></td>
        </tr>
        <tr>
            <td>
                <div class="txtWhite" style="text-align:right; margin-right: 10px;">
                </div>
            </td>
        </tr>
    </table>
</div>

<script type='text/javascript'>
    function handleOnButtonClick(asBtn) {
        switch (asBtn) {
            case "Check":
                document.healthCheck.process.value = "Check";
                var appContext = "<%=appPath + "/core/healthCheck.jsp"%>";
                document.healthCheck.action = appContext;
                baseOnSubmit(document.healthCheck);
                break;
            case "Home":
                var appContext = "<%=appPath%>";
                document.healthCheck.action = appContext;
                baseOnSubmit(document.healthCheck);
                break;
            case "LogOff":
                var logoutPage = "<%=corePath + "/logout.jsp"%>";
                document.healthCheck.action = logoutPage;
                baseOnSubmit(document.healthCheck);
                break;
        }
    }
</script>

<div>
    <table width="100%" border="0" cellpadding="0" cellspacing="0" style="padding:5px">
        <tr>
            <td valign=top>
                <table width="100%" border="0" cellpadding="0" cellspacing="0">
                    <form name="healthCheck" action="healthCheck.jsp" method="post">
                        <input type="hidden" name="process" value="">
                        <tr>
                            <td>
                                <tr>
                                    <td align="left">
                                        <div class='horizontalButtonCollection'>
                                            <dl>
                                                <dd>
                                                    <dt>
                                                        <span class='leftBlueButtonArea'>
                                                            <span class='rightBlueButtonArea'>
                                                                <span class='BlueButtonHolder'>
                                                                    <input type=button class="buttonText" value="Check"
                                                                           onClick="handleOnButtonClick('Check');">
                                                                </span>
                                                            </span>
                                                        </span>
                                                    </dt>
                                                    <dt>
                                                        <span class='leftBlueButtonArea'>
                                                            <span class='rightBlueButtonArea'>
                                                                <span class='BlueButtonHolder'>
                                                                    <input type="button" class="BlueButton" value="Home"
                                                                           onClick="handleOnButtonClick('Home');">
                                                                </span>
                                                            </span>
                                                        </span>
                                                    </dt>
                                                    <dt>
                                                        <span class='leftBlueButtonArea'>
                                                            <span class='rightBlueButtonArea'>
                                                                <span class='BlueButtonHolder'>
                                                                    <input type="button" class="BlueButton" value="Log Off"
                                                                           onClick="handleOnButtonClick('LogOff');">
                                                                </span>
                                                            </span>
                                                        </span>
                                                    </dt>
                                                </dd>
                                            </dl>
                                        </div>
                                    </td>
                                </tr>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <br>
                            </td>
                        </tr>
<%
    Iterator modules = healthCheckManager.getModuleIterator();

    while(modules.hasNext()) {
        HealthCheckWebAppModule module = (HealthCheckWebAppModule)modules.next();
        String iFrameId = "healthCheckChildIframe" + module.getName();
        String panelContentId = "moduleContentPanel" + module.getName();
        String panelTitleId = "moduleTitlePanel" + module.getName();
        String moduleDisplayName = module.getDisplayName();

        String iFrameSrc = appPath + "/../" + module.getName() + "/core/" + "healthCheckChild.jsp?moduleName=" + module.getName();
        String height = module.getDisplayHeight();
%>
                        <tr>
                            <td>
                                <oweb:panel panelTitleId="<%=panelTitleId%>" panelContentId="<%=panelContentId%>" panelTitle="<%=moduleDisplayName%>">
                                    <iframe id="<%=iFrameId%>" allowtransparency="true" width="100%" height="<%=height%>"
                                            frameborder="1" marginwidth="0" scrolling="yes" src="<%=iFrameSrc%>"></iframe>
                                </oweb:panel>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <br>
                            </td>
                        </tr>
<%  }

%>
                        <tr>
                            <td>
                                <tr>
                                    <td align="left">
                                        <div class='horizontalButtonCollection'>
                                            <dl>
                                                <dd>
                                                    <dt>
                                                        <span class='leftBlueButtonArea'>
                                                            <span class='rightBlueButtonArea'>
                                                                <span class='BlueButtonHolder'>
                                                                    <input type=button class="buttonText" value="Check"
                                                                           onClick="handleOnButtonClick('Check');">
                                                                </span>
                                                            </span>
                                                        </span>
                                                    </dt>
                                                    <dt>
                                                        <span class='leftBlueButtonArea'>
                                                            <span class='rightBlueButtonArea'>
                                                                <span class='BlueButtonHolder'>
                                                                    <input type="button" class="BlueButton" value="Home"
                                                                           onClick="handleOnButtonClick('Home');">
                                                                </span>
                                                            </span>
                                                        </span>
                                                    </dt>
                                                    <dt>
                                                        <span class='leftBlueButtonArea'>
                                                            <span class='rightBlueButtonArea'>
                                                                <span class='BlueButtonHolder'>
                                                                    <input type="button" class="BlueButton" value="Log Off"
                                                                           onClick="handleOnButtonClick('LogOff');">
                                                                </span>
                                                            </span>
                                                        </span>
                                                    </dt>
                                                </dd>
                                            </dl>
                                        </div>
                                    </td>
                                </tr>
                            </td>
                        </tr>
                    </form>
                    <jsp:include page="footer.jsp"/>
<%
    } finally {
        RequestLifecycleAdvisor.getInstance().terminate();
    }
%>
<jsp:include page="footerpopup.jsp"/>

