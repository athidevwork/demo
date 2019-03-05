<%--
  Description: jsp file for maintain premium adjustment
  Author: rlli
  Date: July 15, 2007
  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/15/2018       lzhang      194100   Add buildNumber Parameter
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
<script type="text/javascript" src="js/maintainPremiumAdjustment.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="premiumAdjustmentListGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="premiumAdjustmentListGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>
<form name="premiumAdjustmentList" action="maintainPremiumAdjustment.do" method=post>
<%@ include file="/pmcore/commonFormHeader.jsp" %>

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
    <td align=center>
        <fmt:message key="pm.maintainPremiumAdjustment.coverageList.header" var="panelTitleForCoverage" scope="page"/>
        <%
            String panelTitleForCoverage = (String) pageContext.getAttribute("panelTitleForCoverage");
        %>
        <oweb:panel panelTitleId="panelTitleIdForCoverage" panelContentId="panelContentIdForCoverage"
                    panelTitle="<%= panelTitleForCoverage %>">
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="coverageList" scope="request"/>
                    <c:set var="gridDisplayGridId" value="coverageListGrid" scope="request"/>
                    <c:set var="datasrc" value="#coverageListGrid1" scope="request"/>
                    <c:set var="cacheResultSet" value="false"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
        </oweb:panel>
    </td>
</tr>
<tr>
    <td align=center>
        <fmt:message key="pm.maintainPremiumAdjustment.premiumAdjustmentList.header" var="panelTitleForPremiumAdjustment"
                     scope="page"/>
        <%
            String panelTitleForPremiumAdjustment = (String) pageContext.getAttribute("panelTitleForPremiumAdjustment");
        %>
        <oweb:panel panelTitleId="panelTitleIdForPremiumAdjustment" panelContentId="panelContentIdForPremiumAdjustment"
                    panelTitle="<%= panelTitleForPremiumAdjustment %>">
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="premiumAdjustmentList" scope="request"/>
                    <c:set var="gridDisplayGridId" value="premiumAdjustmentListGrid" scope="request"/>
                    <c:set var="gridDetailDivId" value="premiumAdjustmentDetailDiv" scope="request"/>
                    <c:set var="gridSizeFieldIdPrefix" value="detail_"/>
                    <% dataBean = premiumAdjustmentListGridDataBean;
                        gridHeaderBean = premiumAdjustmentListGridHeaderBean; %>
                    <c:set var="cacheResultSet" value="false"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            <tr>
                <td align=center>
                    <c:set var="datasrc" value="#premiumAdjustmentListGrid1" scope="request"/>
                    <fmt:message key="pm.maintainPremiumAdjustment.premiumAdjustmentForm.header" var="premiumAdjustmentFormHeader"
                                 scope="request"/>
                    <% String premiumAdjustmentFormHeader = (String) request.getAttribute("premiumAdjustmentFormHeader"); %>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="headerText" value="<%=  premiumAdjustmentFormHeader %>"/>
                        <jsp:param name="isGridBased" value="true"/>
                        <jsp:param name="divId" value="premiumAdjustmentDetailDiv"/>
                        <jsp:param name="isLayerVisibleByDefault" value="true"/>
                        <jsp:param name="includeLayersWithPrefix" value="PM_PREM_ADJUSTMENT"/>
                    </jsp:include>
                </td>
            </tr>
        </oweb:panel>
    </td>
</tr>
<tr>
    <td align=center>
        <oweb:actionGroup actionItemGroupId="PM_PREMIUM_ADJUSEMENT_AIG" layoutDirection="horizontal"/>
    </td>
</tr>
<jsp:include page="/core/footerpopup.jsp"/>