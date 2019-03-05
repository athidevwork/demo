<%--
  Description: jsp file for maintain limit sharing
  Author: ssheng
  Date: Jun 23, 2016
  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
   06/23/2016     ssheng   164927 - Created this file for quick import enhancement.
   06/29/2018     wrong    194202 - Include commonFormHeader.jsp to pass token field in page.
   11/15/2018     eyin     194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2016 Delphi Technology, inc. (dti)
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<c:set var="isForDivPopup" value="true"></c:set>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<script type="text/javascript" src="<%=appPath%>/policymgr/reviewduplicatemgr/js/maintainReviewDuplicate.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="cisDuplicateListGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="cisDuplicateListGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<form name="reviewDuplicateForm" action="<%=appPath%>/policymgr/reviewduplicatemgr/maintainReviewDuplicate.do" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <input type="hidden" name="workflowState" value="<c:out value="${workflowState}"/>">
    <input type="hidden" name="noDuplicate" value="<%=request.getAttribute("noDuplicate")%>">


    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>

    <c:set var="policyHeaderDisplayMode" value="invisible"/>
    <tr>
        <td align=center>
            <%@ include file="/policymgr/policyHeader.jsp" %>
        </td>
    </tr>
    <!-- Display load result grid -->
    <tr>
        <td align=center>
            <fmt:message key="pm.reviewDuplicate.rosterRisk.header" var="rosterRiskHeader" scope="request"/>
            <%  String rosterRiskHeader = (String) request.getAttribute("rosterRiskHeader"); %>
            <oweb:panel panelTitleId="panelTitleIdForRosterRiskHeader" panelContentId="panelContentIdForRosterRiskHeader"
                        panelTitle="<%= rosterRiskHeader %>">
                <tr>
                    <td colspan="6" align=center>
                        <c:set var="gridDisplayFormName" value="rosterRiskList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="rosterRiskListGrid" scope="request"/>
                        <c:set var="datasrc" value="#rosterRiskListGrid1" scope="request"/>
                        <c:set var="gridDetailDivId" value="rosterRiskDetailDiv" scope="request"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>

    <!-- Display load result grid -->
    <tr>
        <td align=center>
            <fmt:message key="pm.reviewDuplicate.CISDuplicate.header" var="cisDuplicateHeader" scope="request"/>
                <%  String cisDuplicateHeader = (String) request.getAttribute("cisDuplicateHeader"); %>
            <oweb:panel panelTitleId="panelTitleIdForReviewDuplicateHeader" panelContentId="panelContentIdForReviewDuplicateHeader"
                        panelTitle="<%= cisDuplicateHeader %>">
    <tr>
        <td colspan="6" align=center>
            <c:set var="gridDisplayFormName" value="cisDuplicateList" scope="request"/>
            <c:set var="gridDisplayGridId" value="cisDuplicateListGrid" scope="request"/>
            <c:set var="gridDetailDivId" value="cisDuplicateDetailDiv" scope="request"/>
            <c:set var="datasrc" value="#cisDuplicateListGrid1" scope="request"/>
            <% gridHeaderBean = cisDuplicateListGridHeaderBean; %>
            <% dataBean = cisDuplicateListGridDataBean; %>
            <%@ include file="/pmcore/gridDisplay.jsp" %>
        </td>
    </tr>
    </oweb:panel>
    </td>
    </tr>

    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="PM_REVIEW_DUP_AIG" layoutDirection="horizontal"/>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>
