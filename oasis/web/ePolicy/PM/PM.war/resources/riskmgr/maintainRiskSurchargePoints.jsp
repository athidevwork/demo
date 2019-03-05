<%--
  Description: maintain risk surcharge points.

  Author: syang
  Date: Aug 03, 2010

  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/15/2018        eyin        194100 - Add buildNumber parameter to static file references to improve performance.
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
<script type="text/javascript" src="js/maintainRiskSurchargePoints.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<form action="maintainRiskSurchargePoints.do" name="riskSurchargePointsForm" method="post">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <input type="hidden" name="riskId" value="<c:out value="${riskId}"/>">
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
            <fmt:message key="pm.maintainRiskSurchargePoints.page.header" var="panelTitleForSurChrgPts" scope="page"/>
                <% String panelTitleForSurChrgPts = (String) pageContext.getAttribute("panelTitleForSurChrgPts"); %>
            <oweb:panel panelTitleId="panelTitleIdForSurChrgPts" panelContentId="panelContentIdForSurChrgPts"
                        panelTitle="<%= panelTitleForSurChrgPts %>">
                <tr>
                    <td colspan="6" align=center>
                        <c:set var="gridDisplayFormName" value="riskSurchargePointsForm" scope="request"/>
                        <c:set var="gridDisplayGridId" value="riskSurchargePointsListGrid" scope="request"/>
                        <c:set var="gridDetailDivId" value="riskSurchargePointsDetailDiv" scope="request"/>
                        <c:set var="datasrc" value="#riskSurchargePointsListGrid1" scope="request"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
                <tr>
                    <td align=center>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="headerText" value=""/>
                            <jsp:param name="isGridBased" value="true"/>
                            <jsp:param name="excludeAllLayers" value="true"/>
                        </jsp:include>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td colspan="7" align="center">
            <oweb:actionGroup actionItemGroupId="PM_RISK_SUR_PNT_AIG"/>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>