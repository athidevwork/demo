<%--
  Description: Process Quick Pay page

  Author: Dzhang
  Date: July 23, 2010


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2010 Delphi Technology, inc. (dti)
--%>
<%@ page import="dti.oasis.util.BaseResultSet" %>
<%@ page import="dti.oasis.tags.XMLGridHeader" %>

<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<script type="text/javascript" src="js/processQuickPay.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>


<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<form name="processQuickPayFrom" action="processQuickPay.do" method="post">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <input type="hidden" name="openMode" value="<c:out value="${param.openMode}"/>" />
    <input type="hidden" name="policyId" value="<c:out value="${param.policyId}"/>" />
    <input type="hidden" name="termBaseId" value="<c:out value="${param.termBaseId}"/>" />
    <input type="hidden" name="transactionLogId" value="<c:out value="${param.transactionLogId}"/>" />
    <input type="hidden" name="eligibleCount" value="<c:out value="${eligibleCount}"/>" />
    <input type="hidden" name="hasAlreadySubmitted" value="<c:out value="${hasAlreadySubmitted}"/>" />
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>

    <tr>
        <td align=center>

            <fmt:message key="pm.processQuickPay.origTransactionLayer.header" var="origTransHeader" scope="request"/>
            <%
                String origTransHeader = (String) request.getAttribute("origTransHeader");
            %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%=  origTransHeader %>"/>
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="isLayerVisibleByDefault" value="true"/>
                <jsp:param name="includeLayersWithPrefix" value="PM_PROC_QP_ORIG_TRANS_LAYER"/>
            </jsp:include>
        </td>
    </tr>

    <tr>
        <td align=center>

            <fmt:message key="pm.processQuickPay.quickPayAccounting.header" var="qpAccountingHeader" scope="request"/>
            <%
                String qpAccountingHeader = (String) request.getAttribute("qpAccountingHeader");
            %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%=  qpAccountingHeader %>"/>
                <jsp:param name="divId" value="qpAccountLayer"/>
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="isLayerVisibleByDefault" value="true"/>
                <jsp:param name="includeLayersWithPrefix" value="PM_PROC_QP_ACCOUNT_LAYER"/>
            </jsp:include>
        </td>
    </tr>

    <tr>
        <td align=center>
            <fmt:message key="pm.processQuickPay.riskCoverageList.header" var="riskCoverageGridPanelTitle"
                         scope="page"/>
          <%
            String riskCoverageGridPanelTitle = (String) pageContext.getAttribute("riskCoverageGridPanelTitle");
        %>
            <oweb:panel panelTitleId="panelTitleIdForRiskCoverageGrid" panelContentId="panelContentIdForRiskCoverageGrid"
                        panelTitle="<%= riskCoverageGridPanelTitle %>">
        <!-- Display Grid -->
        <tr>
            <td colspan="6" align=center><br/>
              <c:set var="gridDisplayFormName" value="processQuickPayFrom" scope="request"/>
              <c:set var="gridDisplayGridId" value="riskCoverageGrid" scope="request"/>
              <%@ include file="/pmcore/gridDisplay.jsp" %>
            </td>
        </tr>
        </oweb:panel>
        </td>
    </tr>

    <tr>
        <td colspan="6" align=center>
            <oweb:actionGroup actionItemGroupId="PM_PROC_QUICK_PAY_AIG"/>
        </td>
    </tr>

<jsp:include page="/core/footerpopup.jsp"/>