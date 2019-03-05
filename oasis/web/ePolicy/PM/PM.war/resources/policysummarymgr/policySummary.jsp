<%--
  Description:

  Author: wdang
  Date: Dec 14, 2015


  Revision Date     Revised By  Description
  -----------------------------------------------------------------------------
  12/14/2015        wdang       168069 - Initial Version.
  07/06/2016        tzeng       167531 - Added commonTab.js to be included.
  05/11/2017        lzhang      185023 - Added commonSecondlyTab.js to be included.
  07/12/2017        lzhang      186847   Reflect grid replacement project changes
  11/15/2018        eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2015 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ include file="/core/header.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<%@ include file="/core/invokeWorkflow.jsp" %>
<script type="text/javascript" src="<%=appPath%>/policysummarymgr/js/policySummary.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=appPath%>/pmcore/js/commonTab.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<%
    if(pmUIStyle.equals("T")) {
%>
<script type="text/javascript" src="<%=appPath%>/pmcore/js/commonSecondlyTab.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript">
    subFrameId = "policySummaryPageFrame";
</script>
<%
    }
%>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<%-- load golobal policy actions group based on policyCycleCode --%>
<c:if test="${policyHeader.policyCycleCode=='POLICY'}">
    <c:set var="globalActionItemGroupId" value="PM_POLICY_FOLDER_AG"></c:set>
</c:if>
<c:if test="${policyHeader.policyCycleCode=='QUOTE'}">
    <c:set var="globalActionItemGroupId" value="PM_QT_POLICY_FOLDER_AG"></c:set>
</c:if>
<c:set var="pageHeaderDivId" value="pageHeaderForPolicyFolder"></c:set>
<c:set var="pageTitle" value=" "></c:set>

<fmt:message key="pm.common.policy.actions" var="policyActions" scope="request"/>
<c:set var="dropdownSelectFromDesc" value="${policyActions}"></c:set>

<c:set var="showNextPrev" value="true"></c:set>
<c:set var="labelForNextPrev" value="Policy List"></c:set>
<c:set var="policyHeaderDisplayMode" value="hide"/>
<script type="text/javascript"> if (getObject("pageHeader")) { hideShowElementByClassName(getObject("pageHeader"), true); } </script>
<%@ include file="/policymgr/policyListNavigate.jsp" %>
<%@ include file="/core/pageheader.jsp" %>
<input type="hidden" name="policyScreenMode" value="<%= policyHeader.getScreenModeCode() %>">
<tr>
    <td colspan=8>
        <table cellpadding=0 cellspacing=0 width=100%>
            <tr>
                <td><oweb:message/></td>
            </tr>
        </table>
    </td>
</tr>
<form action="viewPolicySummary.do" method="post" name="policySummaryForm">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <tr>
        <td colspan=8 align=center>
            <%@ include file="/policymgr/policyHeader.jsp" %>
        </td>
    </tr>
    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="PM_COMMON_TABS_AIG"/>
        </td>
    </tr>
    <c:set var="tabMenuGroupId" value="PM_POLICY_FOLDER_TG"></c:set>
    <%@ include file="/core/tabheader.jsp" %>

    <c:if test="${dataBean != null}">
    <tr>
        <td align=center>
        <fmt:message key="pm.policySummary.summaryList.header" var="panelTitleForPolicySummary" scope="page"/>
        <%String panelTitleForPolicySummary = (String) pageContext.getAttribute("panelTitleForPolicySummary");%>
        <oweb:panel panelTitleId="panelTitleIdForPolicySummary" panelContentId="panelContentIdForPolicySummary"
                    panelTitle="<%= panelTitleForPolicySummary %>">
        <tr>
            <td align=center>
                <c:set var="gridDisplayFormName" value="policySummaryForm" scope="request"/>
                <c:set var="gridDisplayGridId" value="policySummaryListGrid" scope="request"/>
                <c:set var="gridDetailDivId" value="policySummaryListGridDiv" scope="request"/>
                <c:set var="datasrc" value="#policySummaryListGrid1" scope="request"/>
                <%@ include file="/pmcore/gridDisplay.jsp" %>
            </td>
        </tr>
        </oweb:panel>
        </td>
    </tr>
    </c:if>
<%@ include file="/core/tabfooter.jsp" %>
<tr>
    <td align=center>
        <oweb:actionGroup actionItemGroupId="PM_COMMON_TABS_AIG"/>
    </td>
</tr>
<%@ include file="/core/pagefooter.jsp" %>
<script type="text/javascript">
    var pageTitle = hasObject("policyNo")
            ? "<fmt:message key='pm.common.policy.folder'/> " + getObjectValue("policyNo")
            : "<fmt:message key='pm.common.policy.information'/>";
    var transaction = policyHeader.lastTransactionInfo;
    if ((!isEmpty(transaction.endorsementQuoteId))&&(transaction.endorsementQuoteId!='null')){
        pageTitle=pageTitle +"   "+ transaction.endorsementQuoteId;
    }
    setPageTitle("pageTitleForpageHeaderForPolicyFolder", pageTitle);
</script>

<jsp:include page="/core/footer.jsp"/>