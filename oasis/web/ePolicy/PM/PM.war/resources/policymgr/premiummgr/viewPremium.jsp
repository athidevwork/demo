<%@ page import="dti.oasis.workflowmgr.WorkflowAgent" %>
<%@ page import="dti.oasis.workflowmgr.impl.WorkflowAgentImpl" %>
<%@ page import="dti.pm.policymgr.premiummgr.PremiumFields" %>
<%--
  Description:

  Author: rlli
  Date: June 18, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  10/10/2007       fcb         gridSortable set to false.
  08/06/2011       wfu         124367 - cacheResultSet set to true to cache resultset instead of
                               writing it to web page directly.
  01/29/2013       tcheng      141447 - Added workflowFor hidden field for viewing premium
  03/06/2013       fcb         142697 - Added logic to set the workflow information to be used when the page
                                        is part of the workflow and it needs to move to the next step.
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
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
<script type="text/javascript" src="<%=appPath%>/policymgr/premiummgr/js/viewPremium.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<script type="text/javascript">
<%
    String isInWorkflow = request.getAttribute(PremiumFields.IS_IN_WORKFLOW).toString();
    if (YesNoFlag.getInstance(isInWorkflow).booleanValue()) {
        WorkflowAgent workflowAgent = WorkflowAgentImpl.getInstance();
        String workflowInstanceIdName = workflowAgent.getWorkflowInstanceIdName();
        String workflowInstanceId = null;
        if (request.getAttribute(workflowInstanceIdName) != null) {
            workflowInstanceId = request.getAttribute(workflowInstanceIdName).toString();
        }
        if (workflowInstanceId != null && workflowAgent.hasWorkflow(workflowInstanceId)) {
            String startingState = workflowAgent.getWorkflowCurrentState(workflowInstanceId);
%>
            var workflowUrl = getAppPath() + "/workflowmgr/workflow.do?" +
                                "&workflowState=<%=startingState%>" +
                                "&<%=workflowInstanceIdName%>=<%=workflowInstanceId%>" +
                                "&date=" + new Date();
<%
        }
    }
%>
</script>

<form name="premiumList" action="viewPremium.do" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <c:set scope="request" var="commentsCOLSPAN" value="7"/>
    <input type=hidden name=isInWorkflow value="<c:out value="${isInWorkflow}"/>">
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
            <fmt:message key="pm.viewPremiumInfo.premiumFilter.header" var="premiumFilterHeader" scope="request"/>
            <% String premiumFilterHeader = (String) request.getAttribute("premiumFilterHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%=  premiumFilterHeader %>" />
                <jsp:param name="divId" value="viewPremiumFilter" />
                <jsp:param name="isGridBased" value="false" />
                <jsp:param name="excludeAllLayers" value="true" />
            </jsp:include>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.viewPremiumInfo.premiumTrans.header" var="premiumTransHeader" scope="request"/>
            <% String premiumTransHeader = (String) request.getAttribute("premiumTransHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%=  premiumTransHeader %>" />
                <jsp:param name="divId" value="viewPremiumTrans" />
                <jsp:param name="isGridBased" value="false" />
                <jsp:param name="excludePageFields" value="true" />
                <jsp:param name="isLayerVisibleByDefault" value="true" />
            </jsp:include>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.viewPremiumInfo.premiumList.header" var="panelTitleForPremium" scope="page"/>
            <%
                String panelTitleForPremium = (String) pageContext.getAttribute("panelTitleForPremium");
            %>
            <oweb:panel panelTitleId="panelTitleIdForPremium" panelContentId="panelContentIdForPremium" panelTitle="<%= panelTitleForPremium %>" >

            <tr>
                <td colspan="6" align=center><br/>
                    <c:set var="gridDisplayFormName" value="premiumList" scope="request"/>
                    <c:set var="gridDisplayGridId" value="premiumListGrid" scope="request"/>
                    <c:set var="datasrc" value="#premiumListGrid1" scope="request"/>
                    <c:set var="gridSortable" value="false" scope="request"/>
                    <c:set var="cacheResultSet" value="true"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>

            </oweb:panel>
            <tr>
                <td colspan="6" align=center>
                    <oweb:actionGroup actionItemGroupId="PM_VIEW_PREMIUM_AIG"/>
                </td>
            </tr>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>