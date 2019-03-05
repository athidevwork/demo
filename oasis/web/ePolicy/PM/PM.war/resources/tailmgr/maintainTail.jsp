<%@ page import="dti.oasis.workflowmgr.WorkflowAgent" %>
<%--
  Description:

  Author: yhchen
  Date: Jun 21, 2007

  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  03/15/2011       jshen       Issue 118616 - Allow modify Gross Premium field in prior term. Add a hidden field processAction.
  04/09/2014       xnie        Issue 153450 - Added hidden fields policyScreenMode and saveAndCloseB.
  05/27/2014       xnie        Issue 153450 - Roll backed prior changes.
  03/10/2017       eyin        180675 - change message tag for UI change.
  09/25/2018       wrong       195793 - Added needToHandleExitWorkFlow and selectedIds hidden fields for long running
                                        transaction.
  11/15/2018       lzhang      194100   add buildNumber Parameter
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

<script type="text/javascript" src="<%=appPath%>/tailmgr/js/maintainTail.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=appPath%>/tailmgr/js/processTail.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>


<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<form action="<%=appPath%>/tailmgr/maintainTail.do" method=post name="tailList">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <input type="hidden" name="validateResult" value="<c:out value="${validateResult}"/>"/>
    <input type="hidden" name="captureTransactionDetail" value="<c:out value="${captureTransactionDetail}"/>"/>
    <input type="hidden" name="captureCancellationDetail" value="<c:out value="${captureCancellationDetail}"/>"/>
    <input type="hidden" name="captureFinancePercentage" value="<c:out value="${captureFinancePercentage}"/>"/>
    <input type="hidden" name="accountingDate" value="<c:out value="${accountingDate}"/>"/>
    <input type="hidden" name="tailTransactionCode" value="<c:out value="${tailTransactionCode}"/>"/>
    <input type="hidden" name="processCode" value="<c:out value="${processCode}"/>"/>
    <input type="hidden" name="cancellationType" value="<c:out value="${cancellationType}"/>"/>
    <input type="hidden" name="processAction" value="<c:out value="${processAction}"/>"/>
    <%
        //set startingState and let jsp know which workflow is been invoked.
        WorkflowAgent wa = WorkflowAgentImpl.getInstance();
        if (wa.hasWorkflow(policyHeader.getPolicyNo())) {
            boolean isInOtherWF = wa.hasWorkflowAttribute(policyHeader.getPolicyNo(),dti.pm.core.http.RequestIds.WORKFLOW_FOR);
            if (isInOtherWF) {
                String value = wa.getWorkflowAttribute(policyHeader.getPolicyNo(),dti.pm.core.http.RequestIds.WORKFLOW_FOR).toString();
                request.setAttribute("startingState",value);
            }
        }
    %>
    <input type="hidden" name="startingState" value="<c:out value="${startingState}"/>"/>
    <input type="hidden" name="needToHandleExitWorkFlow" value="<c:out value="${needToHandleExitWorkFlow}"/>"/>
    <input type="hidden" name="selectedIds" value="<c:out value="${selectedIds}"/>"/>

    <%-- Show error message --%>
    <tr>
        <td colspan=8>
            <%
                if (pmUIStyle.equals("T")) {
            %>
            <oweb:message displayMessagesOnParent="true"/>
            <%
                }
            %>
            <%
                if (pmUIStyle.equals("B")) {
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
    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="PM_TAIL_AIG" layoutDirection="horizontal"/>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.maintainTail.filterCriteria.Header" var="filterFormHeader" scope="request"/>
            <% String filterFormHeader = (String) request.getAttribute("filterFormHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%=  filterFormHeader %>" />
                <jsp:param name="divId" value="filterDiv" />
                <jsp:param name="isGridBased" value="false" />
                <jsp:param name="isLayerVisibleByDefault" value="true" />
                <jsp:param name="collaspeTitleForPanel" value="<%=  filterFormHeader %>" />
                <jsp:param name="isPanelCollaspedByDefault" value="true" />
                <jsp:param name="excludePageFields" value="false" />
                <jsp:param name="includeLayersWithPrefix" value="PM_TAIL_FILTER" />
                <jsp:param name="actionItemGroupId" value="PM_TAIL_FILTER_AIG" />
            </jsp:include>
        </td>
    </tr>
    <c:if test="${dataBean != null}">
    <tr>
        <td align=center>
            <fmt:message key="pm.maintainTail.tailList.Header" var="panelTitleForTailList" scope="page"/>
            <%
                String panelTitleForTailList = (String) pageContext.getAttribute("panelTitleForTailList");
            %>
            <oweb:panel panelTitleId="panelTitleIdForTailList" panelContentId="panelContentIdForTailList" panelTitle="<%= panelTitleForTailList %>" >
            <tr>
                <td colspan="6">
                    <oweb:actionGroup actionItemGroupId="PM_TAIL_GRID_AIG" layoutDirection="horizontal" cssColorScheme="gray" cssWidthInPX="75"/>
                </td>
            </tr>
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="coverageList" scope="request"/>
                    <c:set var="gridDisplayGridId" value="coverageListGrid" scope="request"/>
                    <c:set var="gridDetailDivId" value="coverageDetailDiv" scope="request"/>
                    <c:set var="gridSizeFieldIdPrefix" value="tail_"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            <tr> &nbsp;</tr>
            <tr>
                <td align=center>
                    <fmt:message key="pm.maintainTail.tailDetail.Header" var="tailDetailFormHeader" scope="request"/>
                    <% String tailDetailFormHeader = (String) request.getAttribute("tailDetailFormHeader"); %>
                    <c:set var="datasrc" value="#coverageListGrid1" scope="request"/>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="headerText" value="<%=  tailDetailFormHeader %>" />
                        <jsp:param name="isGridBased" value="true" />
                        <jsp:param name="divId" value="coverageDetailDiv" />
                        <jsp:param name="isLayerVisibleByDefault" value="true" />
                        <jsp:param name="excludePageFields" value="true" />
                        <jsp:param name="includeLayersWithPrefix" value="PM_TAILDETAIL" />
                    </jsp:include>
                </td>
            </tr>

            <fmt:message key="pm.maintainTail.componentList.Header" var="componentListHeader"/>
            <c:set var="componentListHeader" value="${componentListHeader}" scope="request"/>
            <jsp:include page="/componentmgr/maintainComponent.jsp"/>

            </oweb:panel>
        </td>
    </tr>
    </c:if>
    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="PM_TAIL_AIG" layoutDirection="horizontal"/>
        </td>
    </tr>
    <jsp:include page="/core/footerpopup.jsp"/>
