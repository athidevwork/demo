<%@ page import="dti.oasis.tags.XMLGridHeader" %>
<%--
  Description:

  Author: zlzhu
  Date: Dec 12, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  Dec 12, 2007         zlzhu      Created
  05/082009            Leo        Issue 93604
  11/15/2018           eyin       Issue 194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<c:set var="isForDivPopup" value="true"></c:set>
<c:set var="skipHeaderFooterContent" value="true"></c:set>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<script type="text/javascript" src="<%=appPath%>/policymgr/js/policySummary.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<form action="javascript:void();" method=post name="policySummary">
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>

    <c:if test="${dataBean != null}">
    <tr>
        <td align=center>
            <fmt:message key="pm.policy.policySummary.header" var="panelTitleIdForPolicySummary" scope="page">
            </fmt:message>
            <%
                String panelTitleIdForPolicySummary = (String) pageContext.getAttribute("panelTitleIdForPolicySummary");
            %>
            <oweb:panel panelTitleId="panelTitleIdForPolicySummary" panelContentId="panelContentIdForPolicySummary" panelTitle="<%= panelTitleIdForPolicySummary %>">
            <tr>
                <td align=center>
                    <c:set var="gridDisplayGridId" value="policySummaryGrid" scope="request"/>
                    <c:set var="gridId" value="policySummaryGrid" scope="request"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.transaction.transactionGrid.header" var="panelTitleIdForTransaction" scope="page">
            </fmt:message>
            <%
                String panelTitleIdForTransaction = (String) pageContext.getAttribute("panelTitleIdForTransaction");
            %>
            <oweb:panel panelTitleId="panelTitleIdForTransaction" panelContentId="panelTitleIdForTransactionContent" panelTitle="<%= panelTitleIdForTransaction %>">
            <tr><td>
                <iframe id="iframeTransaction" scrolling="no" allowtransparency="true" width="100%" height="155" frameborder="0" src=""></iframe>
            </td></tr>
            </oweb:panel>

        </td>
   </tr>

   <tr>
        <td align=center>
            <fmt:message key="pm.maintainRisk.riskForm.header" var="panelTitleIdForRisk" scope="page">
            </fmt:message>
            <%
                String panelTitleIdForRisk = (String) pageContext.getAttribute("panelTitleIdForRisk");
            %>
            <oweb:panel panelTitleId="panelTitleIdForRisk" panelContentId="panelTitleIdForRiskContent" panelTitle="<%= panelTitleIdForRisk %>">
                <tr><td>
                    <iframe id="iframeRisk" scrolling="no" allowtransparency="true" width="100%" height="155" frameborder="0" src=""></iframe>
                </td></tr>
            </oweb:panel>
       </td>
    </tr>

    <tr>
        <td align=center>
            <fmt:message key="pm.maintainCoverage.coverageForm.header" var="panelTitleIdForCoverage" scope="page">
            </fmt:message>
            <%
                String panelTitleIdForCoverage = (String) pageContext.getAttribute("panelTitleIdForCoverage");
            %>
            <oweb:panel panelTitleId="panelTitleIdForCoverage" panelContentId="panelContentIdForCoverage" panelTitle="<%= panelTitleIdForCoverage %>">
            <tr><td>
                <iframe id="iframeCoverage" scrolling="no" allowtransparency="true" width="100%" height="155" frameborder="0" src=""></iframe>
            </td></tr>
            </oweb:panel>

        </td>
   </tr>

    <tr>
        <td align=center>
            <fmt:message key="pm.agentmgr.maintainAgent.formHeader" var="panelTitleIdForAgent" scope="page">
            </fmt:message>
            <%
                String panelTitleIdForAgent = (String) pageContext.getAttribute("panelTitleIdForAgent");
            %>
            <oweb:panel panelTitleId="panelTitleIdForAgent" panelContentId="panelTitleIdForAgentContent" panelTitle="<%= panelTitleIdForAgent %>">
            <tr><td>
                <iframe id="iframeAgent" scrolling="no" allowtransparency="true" width="100%" height="155" frameborder="0" src=""></iframe>
            </td></tr>
            </oweb:panel>

        </td>
   </tr>
    </c:if>
<jsp:include page="/core/footerpopup.jsp"/>
