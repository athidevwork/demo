<%@ page import="dti.oasis.request.RequestLifecycleAdvisor" %>
<%@ page import="java.util.logging.Logger" %>
<%@ page import="dti.oasis.util.LogUtils" %>
<%@ page import="dti.oasis.healthcheckmgr.HealthCheckManager" %>
<%@ page import="java.util.logging.Level" %>
<%@ page import="dti.oasis.messagemgr.MessageManager" %>
<%@ page import="dti.oasis.http.Module" %>
<%@ page import="dti.oasis.app.ApplicationContext" %>
<%@ page import="dti.oasis.healthcheckmgr.impl.HealthCheckManagerImpl" %>
<%@ page import="java.util.List" %>
<%@ page import="dti.oasis.healthcheckmgr.HealthCheckWebAppModule" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="dti.oasis.healthcheckmgr.impl.WebServicesHealthCheckImpl" %>
<%@ page import="dti.oasis.messagemgr.MessageCategory" %>
<%@ page import="dti.oasis.struts.IOasisAction" %>
<%--
  Description: JSP file to check Web Services health.

  Author: fcbibire
  Date: Dec 09, 2010


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------

  -----------------------------------------------------------------------------
  (C) 2010 Delphi Technology, inc. (dti)
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ taglib prefix="fmt" uri="/WEB-INF/fmt.tld" %>
<%@ taglib prefix="oweb" uri="/WEB-INF/oasis-web.tld" %>
<%@ taglib prefix="c" uri="/WEB-INF/c.tld"  %>

<%
    try {
        RequestLifecycleAdvisor.getInstance().initialize(request);
        final String PAGE_NAME = "healthCheckChild.jsp";
        Logger l = LogUtils.enterLog(getClass(), PAGE_NAME);

        response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        response.setHeader("Cache-control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");

        String appPath = request.getContextPath();
        String corePath = Module.getCorePath(request);

        String moduleName = request.getParameter("moduleName").replaceAll("%$","");
        String displayName = null;
        HealthCheckWebAppModule module = null;

        MessageManager messageManager = MessageManager.getInstance();

        try {
            HealthCheckManager healthCheckManager = (HealthCheckManager) dti.oasis.app.ApplicationContext.getInstance().getBean("HealthCheckManager");
            module = healthCheckManager.getModuleByName(moduleName);
            module.checkHealth(request, response, appPath);
            displayName = module.getDisplayName();
        }
        catch (Exception e) {
            l.logp(Level.SEVERE, getClass().getName(), PAGE_NAME, "Failed to check application state!", e);
            messageManager.addErrorMessage("core.healthcheck.webservices.fail");
        }

        ApplicationContext.getInstance().exposeMessageSourceForJstl(pageContext.getServletContext(), request);

        List serviceList = (List)request.getAttribute(moduleName+WebServicesHealthCheckImpl.serviceList);
        if (serviceList != null) {
            messageManager.addVerbatimMessage("If you see a 403--Forbidden Error, then your user is not a member of the OWSUSER group, which is required to access any eOASIS web services.", dti.oasis.messagemgr.MessageCategory.INFORMATION);
        }
%>

<html>
<head>
    <title>Health Check Child</title>
<script type="text/javascript">
    function getAppPath() {
        return '<%=appPath%>';
    }
    function getCorePath() {
        return '<%=corePath%>';
    }
</script>

</head>

<link href="<%=corePath%>/css/dti.css?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" rel="stylesheet" type="text/css"/>
<link href="<%=corePath%>/css/button.css?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="<%=corePath%>/js/gui.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<body class="body">
<div>
    <table width="100%" border="0" cellpadding="0" cellspacing="0" style="padding:5px">
        <tr>
            <td valign=top>
                <table width="100%" border="0" cellpadding="2" cellspacing="0">
                    <form name="healthCheckChild" action="healthCheckChild.jsp" method="post">
                        <input type="hidden" name="process" value="">
                        <tr>
                            <td>
                                <oweb:message/>
                            </td>
                        </tr>
                    </form>
                </table>
            </td>
        </tr>
    <%
        if (serviceList!=null) {
            Iterator it = serviceList.iterator();

            while(it.hasNext()) {
                String serviceName = (String)it.next();
                String iFrameId = "healthCheckInnerChildIframe" + module.getName() + serviceName;

                String iFrameTitleId = "healthCheckInnerChildIframe" + module.getName();
//                String iFrameSrc = appPath + "/" + serviceName + "?WSDL";
                String iFrameSrc = appPath + "/" + serviceName;
    %>
                            <tr>
                                <td>
                                    <div id="<%=iFrameTitleId%>" class="pageTitle">Service <%=serviceName%></div>
                                </td>
                            </tr>

                            <tr>
                                <td>
                                    <iframe id="<%=iFrameId%>" allowtransparency="true" width="100%" height="180"
                                            frameborder="1" marginwidth="0" scrolling="yes" src="<%=iFrameSrc%>"></iframe>
                                </td>
                            </tr>

    <%      }
        }
    %>
    </table>
</div>

<%
    } finally {
        RequestLifecycleAdvisor.getInstance().terminate();
    }
%>