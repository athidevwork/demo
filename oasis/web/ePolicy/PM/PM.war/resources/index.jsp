<%@ page language="java" %>
<%@ page import="dti.oasis.struts.IOasisAction" %>
<%@ page import="dti.oasis.util.StringUtils" %>
<%@ page import="dti.oasis.struts.ActionHelper" %>
<%@ page import="dti.oasis.security.Authenticator" %>
<%@ page import="dti.oasis.util.SysParmProvider" %>
<%@ page import="dti.oasis.http.Module" %>
<%@ page import="dti.oasis.security.J2EESecuritySelector" %>
<%@ page import="dti.oasis.app.ApplicationContext" %>
<%@ page import="dti.oasis.busobjs.YesNoFlag" %>
<%--
  Description:

  Author: wreeder
  Date: Jul 12, 2006


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%
    boolean useFindPageAsHome = YesNoFlag.getInstance(ApplicationContext.getInstance().getProperty("use.find.page.as.home", "true")).booleanValue();
    String url = "home.do";
    if(useFindPageAsHome) {
        url = "policymgr/findPolicy.do";
    }
%>
<jsp:forward page="<%=url%>" />