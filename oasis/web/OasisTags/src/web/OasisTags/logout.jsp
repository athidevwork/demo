<%@ page import="dti.oasis.security.Authenticator,
                 dti.oasis.security.J2EESecuritySelector"%>
<%@ page import="dti.oasis.util.StringUtils"%>
<%@ page import="dti.oasis.app.ApplicationContext" %>
<%@ page import="dti.oasis.request.RequestLifecycleAdvisor" %>
<%@ page import="dti.oasis.struts.IOasisAction" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="dti.oasis.util.PageBean" %>
<%@ page language="java"%>
<%--
  Revision Date    Revised By  Description
  ---------------------------------------------------
  07/06/2006       sxm         include logout pre-processor
  ---------------------------------------------------
  (C) 2006 Delphi Technology, inc. (dti)
--%>
<%
    response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
    response.setHeader("Cache-control", "no-store, no-cache, must-revalidate");
    response.addHeader("Cache-Control", "post-check=0, pre-check=0");
    response.setHeader("Pragma", "no-cache");
    try {
        try {
            RequestLifecycleAdvisor.getInstance().initialize(request);
            PageBean bean = new PageBean();
            bean.setLeftNavActions(new ArrayList());
            bean.setLeftNavMenu(new ArrayList());
            bean.setTopNavMenu(new ArrayList());
            bean.setId("LOG_OUT");
            request.setAttribute(IOasisAction.KEY_PAGEBEAN, bean);
        } finally {
            RequestLifecycleAdvisor.getInstance().terminate();
        }
        String logoutPreprocessor = ApplicationContext.getInstance().getProperty("logoutPreprocessor","");
        if (!StringUtils.isBlank(logoutPreprocessor)) {
%>
<jsp:include page="<%=logoutPreprocessor%>" />
<%
        }
        HttpSession httpSession = request.getSession();
        httpSession.invalidate();
        J2EESecuritySelector.getJ2EESecurityFactory().getInstance().logout(request);
    }
    catch(Exception ignore) { }
    String logoutForward = ApplicationContext.getInstance().getProperty("logoutForward","login.jsp");
    response.sendRedirect(logoutForward);
%>