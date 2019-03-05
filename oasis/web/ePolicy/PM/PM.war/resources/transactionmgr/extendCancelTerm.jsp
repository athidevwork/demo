<%--
  Description:

  Author: zlzhu
  Date: Jan 23, 2008

  This file is the page for extend cancel.
  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  Jan 23, 2008         zlzhu      Created
  11/15/2018          lzhang      194100   Add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<c:set var="isForDivPopup" value="true"></c:set>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<script type="text/javascript" src="<%=appPath%>/transactionmgr/js/extendCancelTerm.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form action="" name ="extendCancelTermForm" method="post">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <input type="hidden" name="workflowState" value="<c:out value="${workflowState}"/>">
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>

    <c:set var="policyHeaderDisplayMode" value="invisible"/>
    <tr>
        <td colspan=8 align=center>
            <%@ include file="/policymgr/policyHeader.jsp" %>
        </td>
    </tr>
    <tr>
        <td colspan="6" align=center><br/>
            <fmt:message key="pm.transactionmgr.extendCancelTerm.header.info" var="extendCancelTermHeader" scope="request"/>
            <% String extendCancelTermHeader = (String) request.getAttribute("extendCancelTermHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%=  extendCancelTermHeader %>" />
                <jsp:param name="divId" value="extendCancelTermDiv" />
                <jsp:param name="isGridBased" value="false" />
            </jsp:include>
        </td>
    </tr>
    <tr>
        <td colspan="6" align=center>
            <oweb:actionGroup actionItemGroupId="PM_EXTEND_CANCEL_TERM_AIG"/>
        </td>
    </tr>

<jsp:include page="/core/footerpopup.jsp"/>