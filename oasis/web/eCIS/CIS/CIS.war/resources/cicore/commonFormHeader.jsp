<%@ page import="dti.ci.helpers.ICIConstants" %>
<%@ page import="org.apache.struts.taglib.html.Constants" %>
<%@ page import="org.apache.struts.Globals" %>
<%--
  Description: The common form header for CIS jsp pages.

  Author: kshen
  Date: Apr 24, 2008


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<input type="hidden" name="<%=ICIConstants.PROCESS_PROPERTY%>" value="">
<input type="hidden" name="<%=Constants.TOKEN_KEY%>"
       value="<%=request.getSession().getAttribute(Globals.TRANSACTION_TOKEN_KEY)%>">