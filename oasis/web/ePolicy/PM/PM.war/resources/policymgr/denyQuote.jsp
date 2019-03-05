<%@ page import="dti.oasis.messagemgr.MessageManager" %>
<%--
  Description:

  Author: Bhong
  Date: Mar 12, 2007


  Revision Date    Revised By   Description
  -----------------------------------------------------------------------------
  01/14/2008       fcb          processDenyQuote.js included here. processDenyQuote.js was created in
                                order to move handleOnButtonClick out of denyQuote.js. denyQuote.js is
                                included in common.jsp, and that was taken precedence over any other
                                js file that was defining handleOnButtonClick.
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2007 Delphi Technology, inc. (dti)
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<c:set var="isForDivPopup" value="true"></c:set>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<script type="text/javascript" src="<%=appPath%>/policymgr/js/processDenyQuote.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form action="" name ="denyQuoteForm">
<%@ include file="/pmcore/commonFormHeader.jsp" %>

<tr>
    <td colspan="6" align=center>
        <fmt:message key="pm.maintainQuote.denyQuoteForm.header" var="denyQuoteFormHeader" scope="request"/>
        <% String denyQuoteFormHeader = (String) request.getAttribute("denyQuoteFormHeader"); %>
        <jsp:include page="/core/compiledFormFields.jsp">
            <jsp:param name="divId" value="policyDetailDiv" />
            <jsp:param name="headerText" value="<%=  denyQuoteFormHeader %>" />
            <jsp:param name="isGridBased" value="false" />
        </jsp:include>

    </td>
</tr>
<tr>
    <td colspan="6" align=center>
        <oweb:actionGroup actionItemGroupId="PM_QUOTE_DENY_AIG"/>
    </td>
</tr>

<jsp:include page="/core/footerpopup.jsp"/>