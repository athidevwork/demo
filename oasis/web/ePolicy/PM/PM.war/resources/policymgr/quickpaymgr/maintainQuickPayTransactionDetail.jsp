<%--
  Description: Quick Pay Transaction Details page

  Author: Dzhang
  Date: July 27, 2010


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2010 Delphi Technology, inc. (dti)
--%>

<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<script type="text/javascript" src="js/maintainQuickPayTransactionDetail.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>


<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<form name="maintainQuickPayDetailFrom" action="maintainQuickPayDetail.do" method="post">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <input type="hidden" name="policyId" value="<c:out value="${param.policyId}"/>" />
    <input type="hidden" name="termBaseId" value="<c:out value="${param.termBaseId}"/>" />
    <input type="hidden" name="origTransId" value="<c:out value="${param.origTransId}"/>" />
    <input type="hidden" name="lastQpTransLogId" value="<c:out value="${lastQpTransLogId}"/>" />
    <input type="hidden" name="openMode" value="<c:out value="${param.openMode}"/>" />

    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>

    <c:if test="${dataBean != null && dataBean.rowCount != 0}">
    <tr>
        <td align=center>

            <fmt:message key="pm.quickPayTransactionDetail.transactionSummaryLayer.header" var="summaryHeader" scope="request"/>
            <%
                String summaryHeader = (String) request.getAttribute("summaryHeader");
            %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%=  summaryHeader %>"/>
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="isLayerVisibleByDefault" value="true"/>
                <jsp:param name="includeLayersWithPrefix" value="PM_QP_TRANS_SUMMARY_LAYER"/>
            </jsp:include>
        </td>
    </tr>

    <tr>
        <td align=center>
            <fmt:message key="pm.quickPayTransactionDetail.riskCoverageList.header" var="riskCoverageGridPanelTitle"
                         scope="page"/>
                <%
            String riskCoverageGridPanelTitle = (String) pageContext.getAttribute("riskCoverageGridPanelTitle");
        %>
            <oweb:panel panelTitleId="panelTitleIdForRiskCoverageGrid" panelContentId="panelContentIdForRiskCoverageGrid"
                        panelTitle="<%= riskCoverageGridPanelTitle %>">
        <!-- Display Grid -->
        <tr>
            <td colspan="6" align=center><br/>
              <c:set var="gridDisplayFormName" value="maintainQuickPayDetailFrom" scope="request"/>
              <c:set var="gridDisplayGridId" value="riskCoverageGrid" scope="request"/>
              <c:set var="gridDetailDivId" value="riskCoverageDetailDiv" scope="request"/>
              <c:set var="datasrc" value="#riskCoverageGrid1" scope="request" />
              <%@ include file="/pmcore/gridDisplay.jsp" %>
            </td>
        </tr>
        <!-- Display form -->
        <tr>
            <td align=center>
                <jsp:include page="/core/compiledFormFields.jsp">
                    <jsp:param name="isGridBased" value="true"/>
                    <jsp:param name="divId" value="riskCoverageDetailDiv"/>
                    <jsp:param name="excludePageFields" value="true"/>
                    <jsp:param name="isLayerVisibleByDefault" value="true"/>
                    <jsp:param name="includeLayersWithPrefix" value="PM_QP_RISK_COVERAGE"/>
                </jsp:include>
            </td>
        </tr>
        </oweb:panel>
        </td>
    </tr>
    </c:if>

    <tr>
        <td colspan="6" align=center>
            <oweb:actionGroup actionItemGroupId="PM_QUICK_PAY_TRANS_DETAIL"/>
        </td>
    </tr>

<jsp:include page="/core/footerpopup.jsp"/>