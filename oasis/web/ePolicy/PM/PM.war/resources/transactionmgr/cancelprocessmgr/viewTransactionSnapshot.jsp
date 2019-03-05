<%--
  Description:

  Author: Bhong
  Date: Mar 30, 2010


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/15/2018       lzhang      194100   Add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2010 Delphi Technology, inc. (dti)
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<c:set var="isForDivPopup" value="true"></c:set>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<script type="text/javascript" src="js/viewTransactionSnapshot.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>
<form name="transactionSnapshotForm" action="viewTransactionSnapshot.do" method="post">
<%@ include file="/pmcore/commonFormHeader.jsp" %>
<c:set var="policyHeaderDisplayMode" value="invisible"/>
<tr>
    <td colspan=8 align=center>
        <%@ include file="/policymgr/policyHeader.jsp" %>
    </td>
</tr>
<tr>
    <td align=center>
        <oweb:actionGroup actionItemGroupId="PM_VIEW_CANCEL_DETAIL_AIG"
                          layoutDirection="horizontal"
                          cssColorScheme="blue"/>
    </td>
</tr>

<!-- Transaction snapshot -->
<tr>
    <td align=center>
        <fmt:message key="pm.viewCancellationDetail.transactionSnapshot.header"
                     var="panelTitleForTransactionSnapshot" scope="page"/>
        <%
            String panelTitleForTransactionSnapshot = (String) pageContext.getAttribute("panelTitleForTransactionSnapshot");
        %>
        <oweb:panel panelTitleId="panelTitleIdForTransactionSnapshot"
                    panelContentId="panelContentIdForTransactionSnapshot"
                    panelTitle="<%= panelTitleForTransactionSnapshot %>">
            <tr>
                <td align="left">
                    <c:set var="gridDisplayFormName" value="transactionSnapshotForm" scope="request"/>
                    <c:set var="gridDisplayGridId" value="transactionSnapshotGrid" scope="request"/>
                    <c:set var="gridSizeFieldIdPrefix" value="trans_" scope="request"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
        </oweb:panel>
    </td>
</tr>

<!-- Term snapshot -->
<tr>
    <td align=center>
        <fmt:message key="pm.viewCancellationDetail.termSnapshot.header" var="panelTitleForTermSnapshot"
                     scope="page"/>
        <%
            String panelTitleForTermSnapshot = (String) pageContext.getAttribute("panelTitleForTermSnapshot");
        %>
        <oweb:panel panelTitleId="panelTitleIdForTermSnapshot"
                    panelContentId="panelContentIdForTermSnapshot"
                    panelTitle="<%= panelTitleForTermSnapshot %>">
            <iframe id="termSnapshotIframe" scrolling="no" allowtransparency="true" width="100%" height="145"
                    frameborder="0" marginwidth="0" src=""></iframe>
        </oweb:panel>
    </td>
</tr>

<!-- Policy Component snapshot -->
<tr>
    <td align=center>
        <fmt:message key="pm.viewCancellationDetail.policyComponentSnapshot.header"
                     var="panelTitleForPolicyComponentSnapshot"
                     scope="page"/>
        <%
            String panelTitleForPolicyComponentSnapshot = (String) pageContext.getAttribute("panelTitleForPolicyComponentSnapshot");
        %>
        <oweb:panel panelTitleId="panelTitleIdForPolicyComponentSnapshot"
                    panelContentId="panelContentIdForPolicyComponentSnapshot"
                    panelTitle="<%= panelTitleForPolicyComponentSnapshot %>">
            <iframe id="policyComponentSnapshotIframe" scrolling="no" allowtransparency="true" width="100%"
                    height="115"
                    frameborder="0" marginwidth="0" src=""></iframe>
        </oweb:panel>
    </td>
</tr>

<!-- Risk snapshot -->
<tr>
    <td align=center>
        <fmt:message key="pm.viewCancellationDetail.riskSnapshot.header"
                     var="panelTitleForRiskSnapshot"
                     scope="page"/>
        <%
            String panelTitleForRiskSnapshot = (String) pageContext.getAttribute("panelTitleForRiskSnapshot");
        %>
        <oweb:panel panelTitleId="panelTitleIdForRiskSnapshot"
                    panelContentId="panelContentIdForRiskSnapshot"
                    panelTitle="<%= panelTitleForRiskSnapshot %>">
            <iframe id="riskSnapshotIframe" scrolling="no" allowtransparency="true" width="100%"
                    height="180"
                    frameborder="0" marginwidth="0" src=""></iframe>
        </oweb:panel>
    </td>
</tr>

<!-- Coverage snapshot -->
<tr>
    <td align=center>
        <fmt:message key="pm.viewCancellationDetail.coverageSnapshot.header"
                     var="panelTitleForCoverageSnapshot"
                     scope="page"/>
        <%
            String panelTitleForCoverageSnapshot = (String) pageContext.getAttribute("panelTitleForCoverageSnapshot");
        %>
        <oweb:panel panelTitleId="panelTitleIdForCoverageSnapshot"
                    panelContentId="panelContentIdForCoverageSnapshot"
                    panelTitle="<%= panelTitleForCoverageSnapshot %>">
            <iframe id="coverageSnapshotIframe" scrolling="no" allowtransparency="true" width="100%"
                    height="180"
                    frameborder="0" marginwidth="0" src=""></iframe>
        </oweb:panel>
    </td>
</tr>

<!--  Coverage Component snapshot -->
<tr>
    <td align=center>
        <fmt:message key="pm.viewCancellationDetail.coverageComponentSnapshot.header"
                     var="panelTitleForCoverageComponentSnapshot"
                     scope="page"/>
        <%
            String panelTitleForCoverageComponentSnapshot = (String) pageContext.getAttribute("panelTitleForCoverageComponentSnapshot");
        %>
        <oweb:panel panelTitleId="panelTitleIdForCoverageComponentSnapshot"
                    panelContentId="panelContentIdForCoverageComponentSnapshot"
                    panelTitle="<%= panelTitleForCoverageComponentSnapshot %>">
            <iframe id="coverageComponentSnapshotIframe" scrolling="no" allowtransparency="true" width="100%"
                    height="150"
                    frameborder="0" marginwidth="0" src=""></iframe>
        </oweb:panel>
    </td>
</tr>

<tr>
    <td align=center>
        <oweb:actionGroup actionItemGroupId="PM_VIEW_CANCEL_DETAIL_AIG"
                          layoutDirection="horizontal"
                          cssColorScheme="blue"/>
    </td>
</tr>
<jsp:include page="/core/footerpopup.jsp"/>
