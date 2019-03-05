<%@ page import="dti.oasis.util.LogUtils" %>
<%@ page import="java.util.logging.Logger" %>
<%@ page import="java.util.logging.Level" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="dti.oasis.request.RequestLifecycleAdvisor" %>
<%@ page import="dti.oasis.session.UserSessionManager" %>
<%@ page import="dti.oasis.session.UserSession" %>
<%@ page import="dti.oasis.session.UserSessionManagerAdmin" %>
<%@ taglib prefix="html" uri="http://struts.apache.org/tags-html" %>
<%@ page language="java" %>

<%--
  Description:

  Author: mgitelman
  Date: 6/6/2017


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%
    Boolean result = false;
    try {
        RequestLifecycleAdvisor.getInstance().initialize(request);
        Logger l = LogUtils.enterLog(getClass(), "clearExpiredUserSessions.jsp");
        l.logp(Level.INFO, getClass().getName(), "jsp_service_method", "Entered clearExpiredUserSessions.jsp");

        l.logp(Level.INFO, getClass().getName(), "jsp_service_method", "CLEARING EXPIRED USERS'SESSIONS....");
        UserSessionManagerAdmin userSessionManagerAdmin = getUserSessionManagerAdmin();
        userSessionManagerAdmin.displayAllUserSessionsFromRequestStorageManager();
        result = userSessionManagerAdmin.clearExpiredUsersSessionsFromRequestStorageManager();
        userSessionManagerAdmin.displayAllUserSessionsFromRequestStorageManager();
        l.logp(Level.INFO, getClass().getName(), "jsp_service_method", "CLEARED EXPIRED USERS' SESSIONS: "+result);
    } finally {
        RequestLifecycleAdvisor.getInstance().terminate();
    }
%>
<%!
    UserSessionManagerAdmin userSessionManagerAdmin;
    UserSessionManagerAdmin getUserSessionManagerAdmin(){
        if (userSessionManagerAdmin == null) {
            userSessionManagerAdmin = (UserSessionManagerAdmin) UserSessionManager.getInstance();
        }
        return userSessionManagerAdmin;
    }

%>
<html>
<head>
    <title>User Session Management - Clear Expired User Sessions</title>
</head>
<body>
<form name="userSessionManagement" method="post" action="killUserSession.jsp">
    <table border="0" cellspacing="2" cellpadding="2" width="600px">
        <tr>
            <td>
                <fieldset>
                    <legend><b>Display Results:</b></legend>
                    <table border="0" cellspacing="2" cellpadding="2" width="100%">
                        <%
                            if(result){
                        %>
                        <tr>
                            <td><label>Expired User Sessions have been successfully cleared.</label>
                        </tr>
                        <%
                            } else {
                        %>
                        <tr>
                            <td><label>Expired User Sessions have not been found. Everything seems to be fine.</label>
                        </tr>
                        <%
                            }
                        %>
                    </table>
                </fieldset>
            </td>
        </tr>
    </table>
</form>
</body>
</html>
