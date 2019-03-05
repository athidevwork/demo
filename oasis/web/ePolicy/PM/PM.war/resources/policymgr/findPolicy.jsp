<%@ page import="dti.pm.core.http.RequestIds"%>
<%@ page import="dti.pm.policymgr.struts.PolicyList" %>
<%@ page import="dti.oasis.recordset.Record" %>
<%@ page import="dti.oasis.recordset.RecordSet" %>
<%@ page import="dti.oasis.recordset.BaseResultSetRecordSetAdaptor" %>
<%@ page import="dti.oasis.session.UserSessionManager" %>
<%@ page import="dti.pm.core.session.UserSessionIds" %>
<%@ page language="java"%>
<%--
  Description:

  Author: gjlong
  Date: Jan 31, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  09/10/2009       fcb         98097: showRowCntOnePage passed to gridDisplay.
  09/10/2010       wfu         111776: Replaced hardcode string with resource definition
  04/26/2012       ryzhao      128030: Add logic to initialize Sys Parms for JavaScript to use
  10/18/2012       xnie        133766: Added system parameter 'PM_MAX_ONDEMAND_RATE'.
  03/25/2016       eyin        170323: Add hidden buttons orgSortColumn/orgSortType/orgSortOrder/orgRowId/returnToList.
  08/29/2017       wrong       187744: Added hidden field "policyTermHistoryId".
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<%@ include file="/core/header.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>

<jsp:include page="/core/fieldlayerdep.jsp"/>

<script language="javascript" src="<%=appPath%>/policymgr/js/findPolicy.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<%
    request.setAttribute("policyHolderNameCOLSPAN","3");
    request.setAttribute("issueCompanyEntityIdCOLSPAN","3");
    request.setAttribute("lastTermBCheckBoxSpan","3");

    String orgSortColumn = "";
    String orgSortType = "";
    String orgSortOrder = "";
    String orgRowId = "";
    if (UserSessionManager.getInstance().getUserSession(request).has(RequestIds.BACK_TO_LIST_ORG_INFO)) {
        orgSortColumn = ((Record) UserSessionManager.getInstance().getUserSession(request).get(RequestIds.BACK_TO_LIST_ORG_INFO))
                .getStringValue(RequestIds.ORG_SORT_COLUMN,"");
        orgSortType = ((Record) UserSessionManager.getInstance().getUserSession(request).get(RequestIds.BACK_TO_LIST_ORG_INFO))
                .getStringValue(RequestIds.ORG_SORT_TYPE,"");
        orgSortOrder = ((Record) UserSessionManager.getInstance().getUserSession(request).get(RequestIds.BACK_TO_LIST_ORG_INFO))
                .getStringValue(RequestIds.ORG_SORT_ORDER,"");
        orgRowId = ((Record) UserSessionManager.getInstance().getUserSession(request).get(RequestIds.BACK_TO_LIST_ORG_INFO))
                .getStringValue(RequestIds.ORG_ROW_ID,"");
    }
%>

<form name="searchPolicyForm" action="findPolicy.do" method="POST">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <input type="hidden" name="orgSortColumn" value="<%= orgSortColumn %>">
    <input type="hidden" name="orgSortType" value="<%= orgSortType %>">
    <input type="hidden" name="orgSortOrder" value="<%= orgSortOrder %>">
    <input type="hidden" name="orgRowId" value="<%= orgRowId %>">
    <input type="hidden" name="returnToList" value="<%=request.getAttribute("returnToList")%>">
    <input type="hidden" name="policyTermHistoryId"
           value="<%=request.getParameter("policyTermHistoryId")%>">

    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.findPolicyAction.findPolicyList.criteria.header" var="underwriterCriteria" scope="request"/>
            <% String underwriterCriteria = (String) request.getAttribute("underwriterCriteria");
                boolean isPanelCollapsed = YesNoFlag.getInstance(ApplicationContext.getInstance().getProperty("collapse.panel.after.policy.search")).booleanValue();
                isPanelCollapsed = dataBean.getRowCount() == 0 ? false : isPanelCollapsed;
            %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%=  underwriterCriteria %>" />
                <jsp:param name="isPanelCollaspedByDefault" value="<%=  isPanelCollapsed %>" />
                <jsp:param name="divId" value="searchCriteria"/>
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="actionItemGroupId" value="PM_SPOL_MORE_AIG"/>
                <jsp:param name="actionItemGroupAlign" value="left"/>
                <jsp:param name="actionItemGroupIdCss" value="gray"/>

            </jsp:include>
        </td>
    </tr>
    <tr>
        <td colspan="6" align=center>
            <oweb:actionGroup actionItemGroupId="PM_SPOL_AIG"/>
        </td>
    </tr>


<% if ((request.getAttribute(RequestIds.PROCESS) == "findAllPolicy")||
    (request.getAttribute(RequestIds.PROCESS) == "saveUserView")) { %>
<c:if test="${(dataBean != null)}">
    <tr>
        <td align=center>
            <fmt:message key= "pm.findPolicyAction.findPolicyList.results.header" var="resultHeader" scope="page" />
            <%
                boolean isSearchAborted = false;
                String resultHeader = (String) pageContext.getAttribute("resultHeader");
                String totalRowsReturned = request.getAttribute(RequestIds.POLICY_SEARCH_TOTAL_ROWS).toString();
                String maxRowsConfigured = request.getAttribute(RequestIds.POLICY_SEARCH_MAX_ROWS).toString();
                int intTotalRowsReturned = Integer.parseInt(totalRowsReturned);
                int intMaxRowsConfigured = Integer.parseInt(maxRowsConfigured);
                if ((intTotalRowsReturned == 0) ||
                    (intMaxRowsConfigured == 0) ||
                    (intMaxRowsConfigured == -1) ||
                    (intTotalRowsReturned < intMaxRowsConfigured)) {
                    resultHeader += dataBean.getRowCount();
                }
                else {
                    resultHeader = MessageManager.getInstance().formatMessage("pm.common.searchResult.header",new String[]{(Integer.toString(dataBean.getRowCount()))});
                    isSearchAborted = true;
                }
                pageContext.setAttribute("resultHeader", resultHeader);
            %>
            <oweb:panel panelContentId="resultList" panelTitle="<%= resultHeader %>">

             <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="findPolicyList" scope="request" />
                    <c:set var="gridDisplayGridId" value="findPolicyListGrid" scope="request" />
                    <c:set var="datasrc" value="#findPolicyListGrid1" scope="request" />
                    <c:set var="showRowCntOnePage" value="true" scope="request"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>

            </oweb:panel>
        </td>
    </tr>

        <%
            // Store the policy list in user session after the gridDisplay.jsp sorts the dataBean
            Iterator iter = ((BaseResultSetRecordSetAdaptor)dataBean).getRecords();
            PolicyList policyList = new PolicyList();
            while (iter.hasNext()) {
                Record policyInfo = (Record) iter.next();
                policyList.add(policyInfo);
            }
            UserSessionManager.getInstance().getUserSession().set(UserSessionIds.POLICY_LIST, policyList);
        %>
</c:if>

<% } %>

<%
    // Initialize Sys Parms for JavaScript to use
    String clientListPersonFmt = SysParmProvider.getInstance().getSysParm("CSW_CLNTLIST_PERSFMT", "");
    String clientNameFmt = SysParmProvider.getInstance().getSysParm("CS_CLIENTNAME_FORMAT", "");
    String maxOndemandRate = SysParmProvider.getInstance().getSysParm("PM_MAX_ONDEMAND_RATE", "100");
%>
<script language="javascript">
    if (eval("window.processDeps")) {
       processDeps();
    }
    setSysParmValue("CSW_CLNTLIST_PERSFMT", '<%= clientListPersonFmt %>');
    setSysParmValue("CS_CLIENTNAME_FORMAT", '<%= clientNameFmt %>');
    setSysParmValue("PM_MAX_ONDEMAND_RATE", '<%= maxOndemandRate %>');
</script>

<jsp:include page="/core/footer.jsp"/>
<c:if test="${requestScope.FeedbackMsg!=null}">
    <script type="text/javascript">
        warnInvalid(getObject("policyNo"),'<%= request.getAttribute("FeedbackMsg")%>');
    </script>
</c:if>
