<%@ page import="dti.oasis.tags.WebLayer" %>
<%@ page import="dti.ci.trainingmgr.TrainingFields" %>
<%@ page import="dti.oasis.tags.OasisFormField" %>
<%@ page import="dti.ci.helpers.ICIConstants" %>
<%@ page import="dti.oasis.util.FormatUtils" %>
<%--
  Description: view claim code history

  Author: msnadar
  Date: June 5, 2008
  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  02/01/2012       kshen       Issue 108498
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  10/08/2018       kshen       195853. Grid replacement. Removed grid detail section since there are only a PK field in
                               details section and the detail form is always hidden.
  -----------------------------------------------------------------------------
  (C) 2008 Delphi Technology, inc. (dti)
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ include file="/core/headerpopup.jsp" %>
<jsp:include page="/cicore/common.jsp"/>
<script language="javascript" src="<%=cisPath%>/claimcodehistory/js/viewClaimCodeHistory.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type='text/javascript' src="<%=cisPath%>/js/gridbtnclicks.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>


<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<form action="claimCodeHistory.do" name="claimCodeHistoryList"  method=POST>
      <jsp:include page="/cicore/commonFormHeader.jsp"/>
<tr>
<td align=center>
    <c:choose>
        <c:when test="${isCodeType eq 'EXPWITNESS_STATUS'}">
            <fmt:message key="ci.expertWitnessStatusHistory.header" var="panelTitleForClaimCodeHistory" scope="page"/>
        </c:when>
        <c:when test="${isCodeType eq 'VENDOR_STATUS'}">
            <fmt:message key="ci.vendorStatusHistory.header" var="panelTitleForClaimCodeHistory" scope="page"/>
        </c:when>
        <c:when test="${isCodeType eq 'VENDOR_TYPE'}">
            <fmt:message key="ci.vendorTypeHistory.header" var="panelTitleForClaimCodeHistory" scope="page"/>
        </c:when>
        <c:when test="${isCodeType eq 'DEMAND_AMT'}">
            <fmt:message key="cm.demandAmtHistory.header" var="panelTitleForClaimCodeHistory" scope="page"/>
        </c:when>
        <c:when test="${isCodeType eq 'SETTLEMENT_AMT'}">
            <fmt:message key="cm.settlementAmtHistory.header" var="panelTitleForClaimCodeHistory" scope="page"/>
        </c:when>
        <c:when test="${isCodeType eq 'VERDICT_AMT'}">
            <fmt:message key="cm.verdictAmtHistory.header" var="panelTitleForClaimCodeHistory" scope="page"/>
        </c:when>
        <c:when test="${isCodeType eq 'REMITTITUR_AMT'}">
            <fmt:message key="cm.remittiurAmtHistory.header" var="panelTitleForClaimCodeHistory" scope="page"/>
        </c:when>
        <c:otherwise>
            <fmt:message key="ci.claimcodehistory.header" var="panelTitleForClaimCodeHistory" scope="page"/>
        </c:otherwise>
    </c:choose>

<%
String panelTitleForClaimCodeHistory = (String) pageContext.getAttribute("panelTitleForClaimCodeHistory");

%>
<oweb:panel panelTitleId="panelTitleIdForClaimCodeHistory"
      panelContentId="panelContentIdForClaimCodeHistory"
      panelTitle="<%= panelTitleForClaimCodeHistory %>">
<tr>
<td colspan="6" align=center>
  <c:set var="gridDisplayFormName" value="claimCodeHistoryList" scope="request"/>
  <c:set var="gridDisplayGridId" value="claimCodeHistoryListGrid" scope="request"/>
  <c:set var="pageSize" value="10" scope="request"/>
  <c:set var="datasrc" value="#claimCodeHistoryListGrid1" scope="request"/>
  <%@ include file="/core/gridDisplay.jsp" %>
</td>
</tr>
</oweb:panel>
<tr>
<td align=center>
  <oweb:actionGroup actionItemGroupId="CI_CLAIMCODEHISTORY_AIG"/>
</td>
</tr>

<jsp:include page="/core/footerpopup.jsp"/>
