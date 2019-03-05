<%@ page import="dti.oasis.http.Module" %>
<%@ page import="dti.oasis.app.ApplicationContext" %>
<%@ page language="java" %>

<%--
  Description:

  Author: kshen
  Date: 4/16/2018


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<script type="text/javascript" src="<%=Module.getCISPath(request)%>/js/ciFolderCommon.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:include page="/cicore/entityCommonFields.jsp"/>