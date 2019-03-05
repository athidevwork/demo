<%@ page import="dti.oasis.util.BaseResultSet" %>
<%@ page import="dti.oasis.tags.XMLGridHeader" %>
<%--
  Description: Risk summary section on policy summary page.

  Author: zlzhu
  Date: Dec 12, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  Dec 12, 2007         zlzhu      Created
  09/05/2011       ryzhao      124622 - For pages with multiple grids, update the name of data bean
                               and grid header bean for all but the first grid.
                               The name of data bean should be gridId + "DataBean".
                               The name of grid header bean should be gridId + "HeaderBean".
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<c:set var="isForDivPopup" value="true"></c:set>
<c:set var="skipHeaderFooterContent" value="true"></c:set>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<script type="text/javascript" src="<%=appPath%>/policymgr/js/riskSummary.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<jsp:useBean id="riskSummaryGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="riskSummaryGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<form action="javascript:void();" method=post name="dummyForm">
    <input type="hidden" name="termEffectiveFromDate" value="<c:out value="${termEffectiveFromDate}"/>"/>
    <input type="hidden" name="termEffectiveToDate" value="<c:out value="${termEffectiveToDate}"/>"/>
    <input type="hidden" name="policyId" value="<c:out value="${policyId}"/>"/>
    <input type="hidden" name="policyNo" value="<c:out value="${policyNo}"/>"/>

    <c:if test="${riskSummaryGridDataBean != null}">
    <tr>
        <td colspan="6">
            <oweb:actionGroup actionItemGroupId="PM_VIEW_POL_SUM_AIG" layoutDirection="horizontal"
                              cssWidthInPX="100px"
                              cssColorScheme="gray"/>
        </td>
    </tr>
    <%
       BaseResultSet dataBean = riskSummaryGridDataBean;
       XMLGridHeader gridHeaderBean = riskSummaryGridHeaderBean;
    %>
    <tr>
        <td align=center>
            <c:set var="gridDisplayGridId" value="riskSummaryGrid" scope="request"/>
            <c:set var="gridId" value="policySummaryGrid" scope="request"/>
            <c:set var="gridSizeFieldIdPrefix" value="risk_"/>
            <%@ include file="/pmcore/gridDisplay.jsp" %>
        </td>
    </tr>
    </c:if>
<jsp:include page="/core/footerpopup.jsp"/>