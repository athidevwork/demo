<%@ page import="org.apache.struts.Globals" %>
<%@ page import="org.apache.struts.taglib.html.Constants" %>

<!-- How to use this page:
    1): Include this page right after the <Form> tag
-->
<%--
  Description:  common form header

  Author: sxm
  Date: Oct 03, 2007

  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  05/06/2011       fcb         Removed logic related to isNewValue
  03/10/2017       wli         Added Cache parameter for UI tab style.
  07/24/2017       eyin        185377, Added input 'noLoadingDiv' when attribute noLoadingDiv value is true.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<c:if test="${empty process}">
    <c:set var="process" value=""></c:set>
</c:if>

<input type=hidden name=process value="<c:out value="${process}"/>">
<input type=hidden name=isNewValue value="<c:out value="${isNewValue}"/>">
<input type="hidden" name="<%=Constants.TOKEN_KEY%>" value="<%=request.getSession().getAttribute(Globals.TRANSACTION_TOKEN_KEY)%>">
<input type="hidden" name="cacheTabIds" value='<%=request.getParameter("cacheTabIds")==null ? "" : request.getParameter("cacheTabIds")%>'>
<input type="hidden" name="cacheRowIds" value='<%=request.getParameter("cacheRowIds")==null ? "" : request.getParameter("cacheRowIds")%>'>
<input type="hidden" name="cacheBtnOperation" value='<%=request.getParameter("cacheBtnOperation")==null ? "" : request.getParameter("cacheBtnOperation")%>'>
<c:if test="${noLoadingDiv == true}">
    <input type="hidden" name="noLoadingDiv" id="noLoadingDiv" value='<c:out value="${noLoadingDiv}"/>'>
</c:if>