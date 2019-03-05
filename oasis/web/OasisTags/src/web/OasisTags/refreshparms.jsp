<%@ page import="dti.oasis.app.ApplicationContext,
                 dti.oasis.codelookupmgr.CodeLookupManager" %>
<%@ page import="dti.oasis.navigationmgr.NavigationManager" %>
<%@ page import="dti.oasis.request.RequestLifecycleAdvisor" %>
<%@ page import="dti.oasis.struts.ActionHelper" %>
<%@ page import="dti.oasis.struts.IOasisAction" %>
<%@ page import="dti.oasis.util.DBPool" %>
<%@ page import="dti.oasis.util.LogUtils" %>
<%@ page import="dti.oasis.util.PageBean" %>
<%@ page import="dti.oasis.util.SysParmProvider" %>
<%@ page import="org.springframework.context.MessageSource" %>
<%@ page import="org.springframework.context.support.ReloadableResourceBundleMessageSource" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.logging.Level" %>
<%@ page import="java.util.logging.Logger" %>
<%@ page import="dti.oasis.data.StoredProcedureDAOHelper" %>
<%@ page import="dti.oasis.http.Module" %>
<%@ page import="dti.oasis.messagemgr.MessageManager" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="dti.oasis.util.DateUtils" %>
<%@ page import="dti.oasis.util.StringUtils" %>
<%@ page import="dti.oasis.obr.KnowledgeBaseManager" %>
<%@ page import="dti.oasis.cachemgr.UserCacheManager" %>
<%@ page import="java.util.List" %>
<%@ page import="dti.oasis.app.RefreshParmsEventRegistrar" %>
<%@ page language="java" %>
<%--
  Description: Refresh SysParm object for current db conn pool
  
  Author: jbe
  Date: Dec 5, 2003
  
  
  Revision Date    Revised By  Description
  ---------------------------------------------------
  9/13/2005         jbe       Use type='text/javascript' instead of language='javascript'  
  4/08/2008         James     Add last started time
  01/18/2010        James     Issue#101408 Record user access trail
  02/25/2010        James     Issue#104230 use restartConnectionPools instead of resetConnectionPools
  05/14/2010        James     Issue#107753 Enhance the refreshparms.jsp page to prompt
                              the user if they wish to restart the data source
  11/10/2010        James     Issue#113896 Fix refreshparms to work with a multi-datasource
                              for use in ASP
  ---------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib prefix="fmt" uri="/WEB-INF/fmt.tld" %>
<%@ taglib prefix="oweb" uri="/WEB-INF/oasis-web.tld" %>
<%@ taglib prefix="c" uri="/WEB-INF/c.tld"  %>

<%
    try {
        RequestLifecycleAdvisor.getInstance().initialize(request);
        final String PAGE_NAME = "refreshparms.jsp";
        Logger l = LogUtils.enterLog(getClass(), PAGE_NAME);

        response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        response.setHeader("Cache-control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");

        String appPath = request.getContextPath();
        String corePath = Module.getCorePath(request);

        String process = request.getParameter("process");
        String refreshSystemParameter = request.getParameter("refreshSystemParameter");
        PageBean bean = new PageBean();
        bean.setId("CS_REFRESH_PARAMETER");
        bean.setLeftNavActions(new ArrayList());
        bean.setLeftNavMenu(new ArrayList());
        bean.setTopNavMenu(new ArrayList());
        request.setAttribute(IOasisAction.KEY_PAGEBEAN, bean);

        //get retry seconds
        String retrySeconds = ApplicationContext.getInstance().getProperty("core.refresh.db.retry.seconds", "10");
        String newRetrySeconds = request.getParameter("newRetrySeconds");
        if (newRetrySeconds != null && StringUtils.isNumeric(newRetrySeconds)) {
            if (Integer.parseInt(newRetrySeconds) > 0) {
                retrySeconds = newRetrySeconds;
            }
        }

        MessageManager messageManager = MessageManager.getInstance();
        if (StringUtils.isBlank(process) || "Refresh".equalsIgnoreCase(process) || "Retry".equalsIgnoreCase(process)) {
            String dbPoolId = ActionHelper.getDbPoolId(request);
            String[] dbPoolIdList = DBPool.getDbPoolIdList(dbPoolId);
            if (!DBPool.hasShutDownDataSource(dbPoolIdList)) {
                int totalActiveConnCount = 0;
                boolean hasUnexpectedData = false;
                for (int i = 0; i < dbPoolIdList.length; i++) {
                    int count = DBPool.getActiveConnectionsCount(dbPoolIdList[i]);
                    if (count < 0) {
                        hasUnexpectedData = true;
                        messageManager.addErrorMessage("core.refresh.restartconnectionpool.fail");
                        l.logp(Level.SEVERE, getClass().getName(), PAGE_NAME, "Active connections is unexpectedly < 0, dbPoolId:" + dbPoolIdList[i]);
                        break;
                    }
                    totalActiveConnCount += count;
                }
                if (!hasUnexpectedData){
                    if (totalActiveConnCount == 0) {
                        // Restart Connection Pool
                        try {
                            for (int i = 0; i < dbPoolIdList.length; i++) {
                                DBPool.restartConnectionPools(dbPoolIdList[i]);
                                l.logp(Level.INFO, getClass().getName(), PAGE_NAME, "The Connection Pool has been restarted! dbPoolId:" + dbPoolIdList[i]);
                            }
                            messageManager.addInfoMessage("core.refresh.restartconnectionpool.success");

                            // Refresh the SysParmProvider
                            try {
                                SysParmProvider.getInstance().refresh(ActionHelper.getDbPoolId(request));
                                messageManager.addInfoMessage("core.refresh.systemparameters.success");
                                l.logp(Level.INFO, getClass().getName(), PAGE_NAME, "Refresh SysParmProvider successfully");
                            } catch (Exception e) {
                                l.logp(Level.SEVERE, getClass().getName(), PAGE_NAME, "Failed to refresh the SysParmProvider", e);
                                messageManager.addErrorMessage("core.refresh.systemparameters.fail");
                            }
                        } catch (Exception e) {
                            l.logp(Level.SEVERE, getClass().getName(), PAGE_NAME, "DB Connection Pool failed to restart!", e);
                            messageManager.addErrorMessage("core.refresh.restartconnectionpool.fail");
                        }
                    } else if (totalActiveConnCount > 0) {
                        // there is active connections.
                        request.setAttribute("NUMBER_OF_ACTIVE_CONNECTION", totalActiveConnCount);
                    }
                }
            } else {
                messageManager.addErrorMessage("core.refresh.connectionpool.stopped");
                l.logp(Level.WARNING, getClass().getName(), PAGE_NAME, "The Database Connection Pool is Stopped.");
            }
        } else if ("ForceRestart".equalsIgnoreCase(process)) {
            //force restart connection pool
            String dbPoolId = ActionHelper.getDbPoolId(request);
            String[] dbPoolIdList = DBPool.getDbPoolIdList(dbPoolId);
            if (!DBPool.hasShutDownDataSource(dbPoolIdList)) {
                try {
                    for (int i = 0; i < dbPoolIdList.length; i++) {
                        DBPool.restartConnectionPools(dbPoolIdList[i]);
                        l.logp(Level.INFO, getClass().getName(), PAGE_NAME, "The Connection Pool has been force restarted! dbPoolId:" + dbPoolIdList[i]);
                    }
                    messageManager.addInfoMessage("core.refresh.forcerestartconnectionpool.success");

                    if ("Y".equalsIgnoreCase(refreshSystemParameter)) {
                        // Refresh the SysParmProvider
                        try {
                            SysParmProvider.getInstance().refresh(ActionHelper.getDbPoolId(request));
                            messageManager.addInfoMessage("core.refresh.systemparameters.success");
                            l.logp(Level.INFO, getClass().getName(), PAGE_NAME, "Refresh SysParmProvider successfully");
                        } catch (Exception e) {
                            l.logp(Level.SEVERE, getClass().getName(), PAGE_NAME, "Failed to refresh the SysParmProvider", e);
                            messageManager.addErrorMessage("core.refresh.systemparameters.fail");
                        }
                    }

                } catch (Exception e) {
                    l.logp(Level.SEVERE, getClass().getName(), PAGE_NAME, "Failed to force restart connection pool!", e);
                    messageManager.addErrorMessage("core.refresh.forcerestartconnectionpool.fail");
                }
            } else {
                messageManager.addErrorMessage("core.refresh.connectionpool.stopped");
                l.logp(Level.WARNING, getClass().getName(), PAGE_NAME, "The Database Connection Pool is Stopped.");
            }
        }
        if (!"Retry".equalsIgnoreCase(process) && !"ForceRestart".equalsIgnoreCase(process)) {
            // Refresh the Messages
            try {
                MessageSource messageSource
                        = (MessageSource) dti.oasis.app.ApplicationContext.getInstance().getBean("messageSource");
                if (messageSource instanceof ReloadableResourceBundleMessageSource) {
                    ((ReloadableResourceBundleMessageSource) messageSource).clearCache();
                    messageManager.addInfoMessage("core.refresh.messages.success");
                    l.logp(Level.INFO, getClass().getName(), PAGE_NAME, "Messages have been refreshed!");
                }
            } catch (Exception e) {
                l.logp(Level.SEVERE, getClass().getName(), PAGE_NAME, "Failed to refresh the Messages", e);
                messageManager.addErrorMessage("Messages failed to refresh!");
            }

            RefreshParmsEventRegistrar refreshParmsEventRegistrar = (RefreshParmsEventRegistrar) dti.oasis.app.ApplicationContext.getInstance().getBean("RefreshParmsEventRegistrar");
            refreshParmsEventRegistrar.triggerRefreshParmsEvent(request);

            // Refresh the last refresh time
            try {
                ApplicationContext.getInstance().setLastRefreshTime(new Date());
                messageManager.addInfoMessage("core.refresh.lastrefreshtime.success",
                        new String[]{DateUtils.formatDateTime(ApplicationContext.getInstance().getLastRefreshTime())});
                l.logp(Level.INFO, getClass().getName(), PAGE_NAME, "Last refresh time has been refreshed!");

            } catch (Exception e) {
                l.logp(Level.SEVERE, getClass().getName(), PAGE_NAME, "Failed to refresh the last refresh time", e);
                messageManager.addErrorMessage("core.refresh.lastrefreshtime.fail");
            }
        }

        ApplicationContext.getInstance().exposeMessageSourceForJstl(pageContext.getServletContext(), request);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=8">
    <title>Refresh Parameters</title>
<script type="text/javascript">
    function getAppPath() {
        return '<%=appPath%>';
    }
    function getCorePath() {
        return '<%=corePath%>';
    }
</script>

</head>
<link rel="shortcut icon" href="<%=corePath%>/images/logo-ftr.gif" />
<link href="<%=corePath%>/css/dti.css?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" rel="stylesheet" type="text/css"/>
<link href="<%=corePath%>/css/button.css?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" rel="stylesheet" type="text/css"/>
<link href="<%=corePath%>/customStyles.jsp?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="<%=corePath%>/js/gui.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<body class="body" onload="handleOnLoad();">
<!-- BEGIN: header -->
<div class="header">
    <table border="0" cellpadding="0" cellspacing="0" width="100%">
        <tr>
            <td><img src="<%=corePath%>/images/space.gif?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" width="990" height="1"/></td>
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
<script type='text/javascript'>
    function handleOnButtonClick(asBtn) {
        clearWaitInputTimer();
        clearCountDownTimer();
        switch (asBtn) {
            case "Refresh":
                document.refreshform.process.value = "Refresh";
                document.refreshform.submit();
                break;
            case "Retry":
                document.refreshform.process.value = "Retry";
                document.refreshform.submit();
                break;
            case "ForceRestart":
                document.refreshform.process.value = "ForceRestart";
                document.refreshform.submit();
                break;
            case "GoBack":
                history.go(-1);
                break;
            case "Home":
                var appContext = "<%=appPath%>";
                document.refreshform.action = appContext;
                document.refreshform.submit();
                break;
            case "LogOff":
                var logoutPage = "<%=corePath + "/logout.jsp"%>";
                document.refreshform.action = logoutPage;
                document.refreshform.submit();
                break;
        }
    }
    function handleOnLoad() {
        beginCountDown();
    }
    function handleOnChange(field) {
        if (field.id == "retryingSeconds") {
            clearCountDownTimer();
            clearWaitInputTimer();
            beginCountDown();
        } else if (field.id == "refreshSystemParameter") {
            if (field.checked) {
                document.getElementById("restartConnectionPool").checked = true;
            }
        } else if (field.id == "restartConnectionPool") {
            if (!field.checked) {
                document.getElementById("refreshSystemParameter").checked = false;
            }
        }
    }
    var waitInputTimer = null;
    function handleOnKeyDown(field) {
        if (field.id == "retryingSeconds") {
            if (!isNumberFormat()){
                return false;
            }
            startWaiting();
        }
    }
    function startWaiting() {
        clearCountDownTimer();
        clearWaitInputTimer();
        waitInputTimer = window.setTimeout("beginCountDown()", 2000);
    }

    function isNumberFormat() {
        var evt = window.event ;
        if ((!evt.shiftKey || evt.keyCode == 9) && (isOkKey(evt) || (evt.keyCode >= 48 && evt.keyCode <= 57 ) || // 0 - 9
                                                    (evt.keyCode >= 96 && evt.keyCode <= 105 ) )) {
            return true;
        } else {
            window.event.cancelBubble = true;
            window.event.returnValue = false;
            return false;
        }
    }
    function isOkKey(evt) {
        if (evt.ctrlKey)
            return true;
        switch (evt.keyCode) {
            case 8:
            case 9:
            case 16:
            case 35:
            case 36:
            case 37:
            case 39:
            case 46:
                return true;
        }
    }

    var countDownTimer = null;
    function beginCountDown() {
        var field = document.getElementById("retryingSeconds");
        if (field && field.value != "") {
            document.getElementById("newRetrySeconds").value = field.value;
            countDownTimer = window.setInterval("doUpdate()", 1000);
        }
    }
    function doUpdate() {
        var field = document.getElementById("retryingSeconds");
        var leftSeconds = parseInt(field.value) - 1;
        if (leftSeconds <= 0) {
            clearCountDownTimer();
            document.refreshform.process.value = "Retry";
            document.refreshform.submit();
        } else {
            field.value = leftSeconds;
        }
    }
    function clearCountDownTimer() {
        if (countDownTimer != null) {
            clearInterval(countDownTimer);
            countDownTimer = null;
        }
    }
    function clearWaitInputTimer() {
        if (waitInputTimer != null) {
            clearTimeout(waitInputTimer);
            waitInputTimer = null;
        }
    }
</script>
<!-- START: Main Body Content -->
<div>
    <table width="100%" border="0" cellpadding="0" cellspacing="0" style="padding:5px">
        <tr>
            <td valign=top>
                <table width="100%" border="0" cellpadding="2" cellspacing="0">
                    <tr>
                        <td colspan="8">
                            <div id="pageHeader" class="pageHeader">
                                <div id="pageTitleForpageHeader" class="pageTitle">
                                    Refresh Cached Parameters
                                </div>
                                <div class="pageBackLink" id='resultBack'>
                                </div>
                                <div class="pagePrintHelpLinks">
                                </div>
                            </div>
                        </td>
                    </tr>
                    <form name="refreshform" action="refreshparms.jsp" method="post">
                        <input type="hidden" name="process" value="">
                        <tr>
                            <td>
                                <oweb:message/>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                    <c:choose>
                                        <c:when test="${not empty NUMBER_OF_ACTIVE_CONNECTION}">
                                            <oweb:panel panelContentId="refreshPanel" hasTitle="false">
                                            <tr>
                                                <td>
                                                    <span class='oasis_formlabel'>
                                                        <fmt:message key="core.refresh.wait.beforemessage">
                                                            <fmt:param value="${NUMBER_OF_ACTIVE_CONNECTION}"/>
                                                        </fmt:message>
                                                        <input id="retryingSeconds"
                                                               type="text"
                                                               name="retryingSeconds"
                                                               maxlength="3"
                                                               value="<%=retrySeconds%>"
                                                               size="3"
                                                               onblur="handleOnChange(this, event);"
                                                               onchange="handleOnChange(this, event);"
                                                               onkeypress="startWaiting();"
                                                               onKeyDown="handleOnKeyDown(this, event);"
                                                               onKeyUp="startWaiting(this);"
                                                               onfocus="startWaiting(this);"
                                                                >
                                                        <fmt:message key="core.refresh.wait.aftermessage"/>
                                                        <input id="newRetrySeconds" name="newRetrySeconds" type="hidden" value=""/>
                                                        <input id="refreshSystemParameter" type="hidden"
                                                               name="refreshSystemParameter"
                                                               value="<%=refreshSystemParameter==null?"":refreshSystemParameter%>">
                                                    </span>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td align="left">
                                                    <div class='horizontalButtonCollection'>
                                                        <dl>
                                                            <dd>
                                                                <dt>
                                                                    <span class='leftBlueButtonArea'>
                                                                        <span class='rightBlueButtonArea'>
                                                                            <span class='BlueButtonHolder'>
                                                                                <input type=button class="buttonText" value="Retry Now"
                                                                                       onClick="handleOnButtonClick('Retry');">
                                                                            </span>
                                                                        </span>
                                                                    </span>
                                                                </dt>
                                                                <dt>
                                                                    <span class='leftBlueButtonArea'>
                                                                        <span class='rightBlueButtonArea'>
                                                                            <span class='BlueButtonHolder'>
                                                                                <input type="button" class="BlueButton" value="Force Restart Database (terminating active connections)"
                                                                                       onClick="handleOnButtonClick('ForceRestart');">
                                                                            </span>
                                                                        </span>
                                                                    </span>
                                                                </dt>
                                                            </dd>
                                                        </dl>
                                                    </div>
                                                </td>
                                            </tr>
                                            </oweb:panel>
                                        </c:when>
                                    </c:choose>
                            </td>
                        </tr>
                        <tr>
                            <td align="left">
                                <div class='horizontalButtonCollection'>
                                    <dl>
                                        <dd>
                                        <c:choose>
                                            <c:when test="${empty NUMBER_OF_ACTIVE_CONNECTION}">
                                            <dt>
                                                <span class='leftBlueButtonArea'>
                                                    <span class='rightBlueButtonArea'>
                                                        <span class='BlueButtonHolder'>
                                                            <input type=button class="buttonText" value="Refresh"
                                                                   onClick="handleOnButtonClick('Refresh');">
                                                        </span>
                                                    </span>
                                                </span>
                                            </dt>
                                            </c:when>
                                        </c:choose>
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
                                        </dd>
                                    </dl>
                                </div>
                            </td>
                        </tr>
                    <jsp:include page="footer.jsp"/>

<%
    } finally {
        RequestLifecycleAdvisor.getInstance().terminate();
    }
%>