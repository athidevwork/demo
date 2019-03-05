<%@ page import="dti.oasis.tags.XMLGridHeader" %>
<%@ page import="dti.oasis.util.BaseResultSet" %>
<%--
  Description:

  Author: zlzhu
  Date: Oct 9, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  Oct 9, 2007      zlzhu       Created
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<script type="text/javascript" src="<%=appPath%>/riskmgr/js/selectExistingRisk.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<form action="<%=appPath%>/riskmgr/selectExistingRisk.do" method=post name="transactionForm">
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
    <c:if test="${dataBean != null}">
    <tr>
        <td align=center>
            <fmt:message key="pm.maintainRisk.riskList.header" var="panelTitleIdForTransactionGrid" scope="page">
                <fmt:param value="${policyHeader.policyNo}"/>
            </fmt:message>
            <%
                String panelTitleIdForTransactionGrid = (String) pageContext.getAttribute("panelTitleIdForTransactionGrid");
            %>
            <oweb:panel panelTitleId="panelTitleIdForTransactionGrid" panelContentId="panelContentIdForTransactionGrid" panelTitle="<%= panelTitleIdForTransactionGrid %>">
            <tr>
                <td align=center>
                    <c:set var="gridDisplayFormName" value="riskGridForm" scope="request"/>
                    <c:set var="gridDisplayGridId" value="riskGrid" scope="request"/>
                    <c:set var="gridId" value="riskGrid" scope="request"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            </oweb:panel>
        </td>
    </tr>
    </c:if>
    <tr>
        <td colspan="8" align="center">
            <oweb:actionGroup actionItemGroupId="PM_ADD_EXT_RISK_AIG"/>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>
