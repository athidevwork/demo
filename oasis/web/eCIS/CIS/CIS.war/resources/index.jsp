<%@ page import="dti.oasis.app.ApplicationContext" %>
<%@ page import="dti.oasis.busobjs.YesNoFlag" %>
<%@ page language="java" %>
<%--
  Description:

  Author: gcc
  Date: Dec 9, 2003


  Revision Date    Revised By  Description
  ---------------------------------------------------


  ---------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%
    boolean useFindPageAsHome = YesNoFlag.getInstance(ApplicationContext.getInstance().getProperty("use.find.page.as.home", "true")).booleanValue();
    String url = "CIS.do";
    if(useFindPageAsHome) {
        url = "ciEntitySearch.do";
    }
%>
<jsp:forward page="<%=url%>" />
