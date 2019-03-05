<%@ page language="java" %>
<%@ page import="dti.oasis.util.BaseResultSet" %>
<%@ page import="dti.oasis.tags.XMLGridHeader" %>
<%@ page import="dti.ci.helpers.ICIConstants"%>

<%--
  Description: CIS summary JSP

  Author: cyzhao
  Date: June 17, 2008


  Revision Date    Revised By  Description
  ---------------------------------------------------
  03/19/2009       kenney      Added Form Letter support for eCIS
  09/28/2010       wfu         111776: Replaced hardcode string with resource definition
  06/24/2011       Michael      for issue 121773
  11/12/2013       hxk         Issue 149981
                               Add CI_SUMMARY_ACCT_AIG so we include the Show All button.
  07/06/2016       Elvin       Issue 177662: use c:out to set entityName in order to avoid display problem
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  10/26/2018       Elvin       Issue 195835: grid replacement
  ---------------------------------------------------
  (C) 2008 Delphi Technology, inc. (dti)
--%>

<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="polQteListGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="combinedPolQteListGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="combinedRiskListGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="accountListGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="claimsListGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="polQteListGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="combinedPolQteListGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="combinedRiskListGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="accountListGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="claimsListGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>

<c:set var="globalActionItemGroupId" value="CI_FOLDER_AG"></c:set>
<%@ include file="/core/header.jsp" %>
<jsp:include page="/cicore/common.jsp"/>
<jsp:include page="/CI_EntitySelect.jsp"/>

<c:set var="tabMenuGroupId" value="${tabGroupId}"></c:set>
<%@ include file="/core/tabheader.jsp" %>

<script type='text/javascript' src="<%=cisPath%>/summarymgr/js/maintainSummary.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form name="CISummaryForm" action="ciSummary.do" method="POST">
    <tr>
        <td class="tabTitle">
            <b>
                <fmt:message key="ci.entity.search.label.summary"/><%=" " + request.getParameter(ICIConstants.ENTITY_NAME_PROPERTY)%>
            </b>
        </td>
    </tr>

    <tr>
        <td colspan="6">
            <oweb:message/>
        </td>
    </tr>

    <jsp:include page="/cicore/commonFormHeader.jsp"/>
    <jsp:include page="/cicore/ciFolderCommon.jsp" />

    <input type="hidden" name="getAllTerms" value="<c:out value='${getAllTerms}'/>" >
    <input type="hidden" name="isPolicyCombined" value="<c:out value='${isPolicyCombined}'/>" >

    <tr>
        <td colspan="6" align="center">
            <oweb:actionGroup actionItemGroupId="CI_SUMMARY_AIG" cssColorScheme="blue" layoutDirection="horizontal"/>
         </td>
    </tr>

    <c:if test="${isPolicyCombined=='N'}">
    <tr>
        <td>
            <oweb:panel panelContentId="panelContentForPolQte"
                        panelTitleId="panelTitleIdForPolQte"
                        panelTitleLayerId="CI_SUMMARY_POLQTE_GH">
                <tr>
                    <td align=center>
                        <c:set var="gridDisplayFormName" value="polQteList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="polQteListGrid" scope="request"/>
                        <c:set var="gridSizeFieldIdPrefix" value="polQte_"/>
                        <c:set var="cacheResultSet" value="false"/>
                        <%
                            BaseResultSet dataBean = polQteListGridDataBean;
                            XMLGridHeader gridHeaderBean = polQteListGridHeaderBean;
                        %>
                        <%@ include file="/core/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    </c:if>

    <c:if test="${isPolicyCombined=='Y'}">
    <tr>
        <td>
            <oweb:panel panelContentId="panelContentForCombinedPolQte"
                        panelTitleId="panelTitleIdForCombinedPolQte"
                        panelTitleLayerId="CI_SUMMARY_COMBINED_POLQTE_GH">
                <tr>
                    <td align=center>
                        <c:set var="gridDisplayFormName" value="combinedPolQteList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="combinedPolQteListGrid" scope="request"/>
                        <c:set var="gridSizeFieldIdPrefix" value="combinedPolQte_"/>
                        <c:set var="cacheResultSet" value="false"/>
                        <%
                            BaseResultSet dataBean = combinedPolQteListGridDataBean;
                            XMLGridHeader gridHeaderBean = combinedPolQteListGridHeaderBean;
                        %>
                        <%@ include file="/core/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>

    <tr>
        <td>
            <oweb:panel panelContentId="panelContentForCombinedRisk"
                        panelTitleId="panelTitleIdForCombinedRisk"
                        panelTitleLayerId="CI_SUMMARY_COMBINED_RISK_GH">
                <tr>
                    <td align=center>
                        <c:set var="gridDisplayFormName" value="combinedRiskList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="combinedRiskListGrid" scope="request"/>
                        <c:set var="gridSizeFieldIdPrefix" value="combinedRisk_"/>
                        <c:set var="cacheResultSet" value="false"/>
                        <%
                            BaseResultSet dataBean = combinedRiskListGridDataBean;
                            XMLGridHeader gridHeaderBean = combinedRiskListGridHeaderBean;
                        %>
                        <%@ include file="/core/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    </c:if>

    <tr>
        <td>
            <oweb:panel panelContentId="panelContentForAccount"
                        panelTitleId="panelTitleIdForAccount"
                        panelTitleLayerId="CI_SUMMARY_ACCOUNT_GH">
                <tr>
                    <td>
                        <oweb:actionGroup actionItemGroupId="CI_SUMMARY_ACCT_AIG" cssColorScheme="gray" layoutDirection="horizontal"/>
                    </td>
                </tr>

                <tr>
                    <td align=center>
                        <c:set var="gridDisplayFormName" value="accountList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="accountListGrid" scope="request"/>
                        <c:set var="gridSizeFieldIdPrefix" value="account_"/>
                        <c:set var="cacheResultSet" value="false"/>
                        <%
                            BaseResultSet dataBean = accountListGridDataBean;
                            XMLGridHeader gridHeaderBean = accountListGridHeaderBean;
                        %>
                        <%@ include file="/core/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>

    <tr>
        <td>
            <oweb:panel panelContentId="panelContentForBilling"
                        panelTitleId="panelTitleIdForBilling"
                        panelTitleLayerId="CI_SUMMARY_BILLING_GH">
                <tr>
                    <td>
                        <iframe id="iframeAccountBillingDetails" scrolling="no" allowtransparency="true" width="100%" height="180" frameborder="0" src=""></iframe>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>

    <c:if test="${showClaim=='Y'}">
    <tr>
        <td>
            <oweb:panel panelId="claimPanel"
                        panelContentId="panelContentForClaims"
                        panelTitleId="panelTitleIdForClaims"
                        panelTitleLayerId="CI_SUMMARY_CLAIMS_GH">
                <tr>
                    <td>
                        <oweb:actionGroup actionItemGroupId="CI_SUMMARY_CLAIM_AIG" cssColorScheme="gray" layoutDirection="horizontal"/>
                    </td>
                </tr>

                <tr>
                    <td align=center>
                        <c:set var="gridDisplayFormName" value="claimsList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="claimsListGrid" scope="request"/>
                        <c:set var="gridSizeFieldIdPrefix" value="claims_"/>
                        <c:set var="cacheResultSet" value="false"/>
                        <%
                            BaseResultSet dataBean = claimsListGridDataBean;
                            XMLGridHeader gridHeaderBean = claimsListGridHeaderBean;
                        %>
                        <%@include file="/core/gridDisplay.jsp" %>
                    </td>
                </tr>

                <tr>
                    <td align=center>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="isGridBased" value="false"/>
                            <jsp:param name="divId" value="claimsStatistic"/>
                            <jsp:param name="hasPanelTitle" value="false"/>
                            <jsp:param name="isTogglableTitle" value="false"/>
                            <jsp:param name="excludeAllLayers" value="true"/>
                        </jsp:include>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    </c:if>

    <%@ include file="/core/tabfooter.jsp" %>
    <jsp:include page="/core/footer.jsp"/>