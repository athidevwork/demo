<%@ page import="dti.oasis.accesstrailmgr.AccessTrailRequestIds,
                 dti.oasis.request.RequestLifecycleAdvisor,
                 dti.oasis.security.Authenticator,
                 dti.oasis.security.J2EESecuritySelector,
                 dti.oasis.struts.ActionHelper"%>
<%@ page import="dti.oasis.struts.IOasisAction" %>
<%@ page import="dti.oasis.util.PageBean" %>
<%@ page import="dti.oasis.messagemgr.MessageManager" %>
<%@ page language="java" %>

<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%
    try {
        RequestLifecycleAdvisor.getInstance().initialize(request);
        PageBean bean = new PageBean();
        bean.setId("CS_UPDATE_PASSWORD");
        request.setAttribute(IOasisAction.KEY_PAGEBEAN, bean);

        response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        response.setHeader("Cache-control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        String changeType = request.getParameter("changetype");
        request.setAttribute(AccessTrailRequestIds.ACCESS_TRAIL_METHOD, changeType);
        String user = MessageManager.getInstance().formatMessage("label.header.page.passUser");
        String msg = null;
        if(changeType==null || changeType.equals("PWD")) {
            if(!J2EESecuritySelector.getJ2EESecurityFactory().getInstance().changePassword(request,response))
                return;
            msg = MessageManager.getInstance().formatMessage("label.header.page.passChanged");
        }
        else {
            Authenticator.updatePasswordReminder(ActionHelper.getDbPoolId(request),
                    ActionHelper.getCurrentUserId(request), request.getParameter("reminder"));
            msg = MessageManager.getInstance().formatMessage("label.header.page.passReminder"); 
        }
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=8">
</head>

<script type="text/javascript">
alert("<%=user%><%= request.getRemoteUser() %><%=msg%>");
var origurl = "<%=request.getParameter(IOasisAction.PARM_URL)%>"
if(origurl!="null")
    document.location.href = origurl;
else
    window.close();
</script>

</html>

<%
    } finally {
        RequestLifecycleAdvisor.getInstance().terminate();
    }
%>

