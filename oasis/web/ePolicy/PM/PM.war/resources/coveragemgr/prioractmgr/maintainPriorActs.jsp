<%@ page import="dti.oasis.util.BaseResultSet" %>
<%@ page import="dti.oasis.tags.XMLGridHeader" %>
<%--
  Description:

  Author: yhchen
  Date: Dec 4, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  06/10/2011       wqfu        103799 - Added hidden field isCopyActsStats.
  12/27/2011       xnie        128433 - Modified most of panel title.
  03/13/2017       eyin        180675 - Changed the error msg to be located in parent frame for UI change.
  11/13/2018       tyang       194100 - Add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<%@ include file="/core/invokeWorkflow.jsp" %>

<script type="text/javascript" src="<%=appPath%>/coveragemgr/prioractmgr/js/maintainPriorActs.js
?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<%dti.oasis.tags.XMLGridHeader gridHeaderBean = null;%>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="riskListGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="coverageListGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="coverageListGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<form action="<%=appPath%>/coveragemgr/prioractmgr/maintainPriorActAction.do" method=post name="priorActList">
<%@ include file="/pmcore/commonFormHeader.jsp" %>

<input type="hidden" name="processCode" value="<c:out value="${processCode}"/>"/>
<input type="hidden" name="isCopyActsStats" value="<c:out value="${isCopyActsStats}"/>"/>
<%-- Show error message --%>
<tr>
    <td colspan=8>
    <%
        if(pmUIStyle.equals("T")) {
    %>
    <oweb:message displayMessagesOnParent="true"/>
    <%
        }
    %>
    <%
        if(pmUIStyle.equals("B")) {
    %>
    <oweb:message/>
    <%
        }
    %>
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
        <fmt:message key="pm.maintainPriorActs.riskList.header" var="panelTitleForRiskList" scope="page"/>
        <%
            String panelTitleForRiskList = (String) pageContext.getAttribute("panelTitleForRiskList");
        %>
        <oweb:panel panelTitleId="panelTitleForRiskList" panelContentId="panelContentIdForRiskList" panelTitle="<%= panelTitleForRiskList %>">
            <tr>
                <td colspan="6">
                    <oweb:actionGroup actionItemGroupId="PM_PA_RISK_AIG" layoutDirection="horizontal" cssColorScheme="gray" cssWidthInPX="75"/>
                </td>
            </tr>
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="riskList" scope="request"/>
                    <c:set var="gridDisplayGridId" value="riskListGrid" scope="request"/>
                    <c:set var="gridDetailDivId" value="riskDetailDiv" scope="request"/>
                    <% gridHeaderBean = riskListGridHeaderBean; %>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            <tr>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td align=center>
                    <fmt:message key="pm.maintainPriorActs.riskForm.header" var="riskDetailFormHeader" scope="request"/>
                    <% String riskDetailFormHeader = (String) request.getAttribute("riskDetailFormHeader"); %>
                    <c:set var="datasrc" value="#riskListGrid1" scope="request"/>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="headerText" value="<%=  riskDetailFormHeader %>"/>
                        <jsp:param name="isGridBased" value="true"/>
                        <jsp:param name="divId" value="riskDetailDiv"/>
                        <jsp:param name="isLayerVisibleByDefault" value="true"/>
                        <jsp:param name="excludePageFields" value="false"/>
                        <jsp:param name="includeLayersWithPrefix" value="PM_PA_RISK_GD"/>
                    </jsp:include>
                </td>
            </tr>
        </oweb:panel>
        <br/>
        <fmt:message key="pm.maintainPriorActs.coverageList.header" var="panelTitleForCoverageList" scope="page"/>
        <%
            String panelTitleForCoverageList = (String) pageContext.getAttribute("panelTitleForCoverageList");
        %>
        <oweb:panel panelTitleId="panelTitleForCoverageList" panelContentId="panelContentIdForCovgList"
                    panelTitle="<%= panelTitleForCoverageList %>" panelCollapseTitle="<%= panelTitleForCoverageList %>">
            <tr>
                <td colspan="6">
                    <oweb:actionGroup actionItemGroupId="PM_PA_COVG_AIG" layoutDirection="horizontal"
                                      cssColorScheme="gray" cssWidthInPX="75"/>
                </td>
            </tr>
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="coverageList" scope="request"/>
                    <c:set var="gridDisplayGridId" value="coverageListGrid" scope="request"/>
                    <c:set var="gridDetailDivId" value="coverageDetailDiv" scope="request"/>
                        <%--TODO <c:set var="gridSizeFieldIdPrefix" value="tail_"/>--%>
                    <% dataBean = coverageListGridDataBean;
                        gridHeaderBean = coverageListGridHeaderBean; %>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            <tr>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td align=center>
                    <fmt:message key="pm.maintainPriorActs.coverageForm.header" var="covgDetailFormHeader"
                                 scope="request"/>
                    <% String covgDetailFormHeader = (String) request.getAttribute("covgDetailFormHeader"); %>
                    <c:set var="datasrc" value="#coverageListGrid1" scope="request"/>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="headerText" value="<%=  covgDetailFormHeader %>"/>
                        <jsp:param name="isGridBased" value="true"/>
                        <jsp:param name="divId" value="coverageDetailDiv"/>
                        <jsp:param name="isLayerVisibleByDefault" value="true"/>
                        <jsp:param name="excludePageFields" value="true"/>
                        <jsp:param name="includeLayersWithPrefix" value="PM_PA_COVG_GD"/>
                    </jsp:include>
                </td>
            </tr>
        </oweb:panel>
    </td>
</tr>
</c:if>

<fmt:message key="pm.maintainPriorActs.componentList.header" var="componentListHeader"/>
<c:set var="componentListHeader" value="${componentListHeader}" scope="request"/>
<fmt:message key="pm.maintainPriorActs.componentForm.header" var="headerText" scope="request"/>
<jsp:include page="/componentmgr/maintainComponent.jsp"/>

<tr>
    <td align=center>
        <oweb:actionGroup actionItemGroupId="PM_PA_AIG" layoutDirection="horizontal"/>
    </td>
</tr>
<jsp:include page="/core/footerpopup.jsp"/>