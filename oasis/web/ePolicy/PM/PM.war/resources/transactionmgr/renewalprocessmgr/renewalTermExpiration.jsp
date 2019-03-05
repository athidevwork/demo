<%@ page import="dti.oasis.messagemgr.MessageManager" %>
<%--
  Description:

  Author: EChen
  Date: Mar 12, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/15/2018       lzhang      194100   Add buildNumber Parameter
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
<script type="text/javascript" src="js/renewalTermExpiration.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form action="" name ="renewTermForm">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

<!-- Display grid -->
<tr>
    <td colspan="6" align=center><br/>
        <fmt:message key="pm.maintainRenewal.renewalTermForm.header" var="renewalTermFormHeader" scope="request"/>
        <% String renewalTermFormHeader = (String) request.getAttribute("renewalTermFormHeader"); %>
        <jsp:include page="/core/compiledFormFields.jsp">
            <jsp:param name="divId" value="policyDetailDiv" />
            <jsp:param name="headerText" value="<%=  renewalTermFormHeader %>" />
            <jsp:param name="isGridBased" value="false" />
        </jsp:include>
    </td>
</tr>
<tr>
    <td>
        <oweb:actionGroup actionItemGroupId="PM_RENEW_AIG" layoutDirection="horizontal" cssColorScheme="gray"/>        
    </td>
</tr>
<jsp:include page="/core/footerpopup.jsp"/>