<%@ page import="dti.oasis.request.RequestLifecycleAdvisor" %>
<%@ page import="dti.oasis.struts.IOasisAction" %>
<%@ page import="dti.oasis.util.PageBean" %>
<%@ page import="dti.oasis.accesstrailmgr.AccessTrailRequestIds" %>
<%@ page import="dti.oasis.app.ApplicationContext" %>
<%@ page import="dti.oasis.filter.CharacterEncodingFilter" %>
<%@ page language="java" %>
<%--
  Description: Utility JSP for logging access trail .

  Author: James
  Date: Jan 18, 2010


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------

  -----------------------------------------------------------------------------
  (C) 2004 Delphi Technology, inc. (dti)
--%>
<%
    try {
        RequestLifecycleAdvisor.getInstance().initialize(request);

        String encoding = ApplicationContext.getInstance().getProperty(CharacterEncodingFilter.CHARACTER_ENCODING_DEFAULT);
        response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        response.setHeader("Cache-control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Content-Type", "text/html; charset=" + encoding);
        response.setHeader("Pragma", "no-cache");

        String pageCode = request.getParameter("pageCode");
        String accessTrailB = request.getParameter("accessTrailB");
        String sourceTableName = request.getParameter("sourceTableName");
        String sourceRecordNo = request.getParameter("sourceRecordNo");
        String sourceRecordFk = request.getParameter("sourceRecordFk");

        PageBean bean = new PageBean();
        bean.setId(pageCode);
        bean.setAccessTrailB(accessTrailB);
        request.setAttribute(IOasisAction.KEY_PAGEBEAN, bean);
        request.setAttribute(AccessTrailRequestIds.ACCESS_TRAIL_METHOD, "Print");
        request.setAttribute(AccessTrailRequestIds.ACCESS_TRAIL_SOURCE_TABLE_NAME, sourceTableName);
        request.setAttribute(AccessTrailRequestIds.ACCESS_TRAIL_SOURCE_RECORD_NO, sourceRecordNo);
        request.setAttribute(AccessTrailRequestIds.ACCESS_TRAIL_SOURCE_RECORD_FK, sourceRecordFk);
    } finally {
        RequestLifecycleAdvisor.getInstance().terminate();
    }
%>
<html>
<head>
</head>
<body>
Access Trail Page
</body>
</html>
