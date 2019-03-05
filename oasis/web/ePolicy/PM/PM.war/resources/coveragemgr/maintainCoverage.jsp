<%--
  Description:

  Author: jmpotosky
  Date: Jan 10, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  05/07/2007       Joe         Add component layer
  07/09/2007       sxm         Added logic to get the last selected coverageId from user session
  08/21/2007       sxm         Added navigation layer
  08/24/2007       sxm         Added policy list navigation
  09/27/2007       sxm         Removed pageBackLink
  10/02/2007       sxm         Let Message tag handle policy lock message
  09/10/2010       wfu         111776: Replaced hardcode string with resource definition
  02/11/2011       ryzhao      112571: Initialize system parameter PM_RETRIEVE_CVG_ADDL
  07/18/2011       syang       121208 - Add PM_COVG_FILTER layer for filtering coverage.
  02/13/2014       awu         147405 - Changed the value of riskStatusCode to risk's base record status.
  07/25/2014       awu         152034 - Modified to get the currentCoverageId from the policyHeader.
  02/26/2015       kxiang      161002 - Add hidden input for policyScreenMode.
  07/06/2016       tzeng       167531 - Added commonTab.js to be included.
  03/13/2017       eyin        180675 - 1. Added commonTab.js to be included.
                                        2. Initialized the parameters 'subFrameId, secondlyTabDivId, requiredSubmitMainPageTabArr'.
                                        3. Modified the logic of page display for UI change.
  07/12/2017       lzhang      186847   Reflect grid replacement project changes
  06/11/2018       cesar       193651   1 - removed style attribute for coverageDetail and componentDetail.
  11/13/2018       tyang       194100 - Add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ include file="/core/header.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<%@ include file="/core/invokeWorkflow.jsp" %>

<script type="text/javascript" src="js/maintainCoverage.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=appPath%>/pmcore/js/commonTab.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=appPath%>/pmcore/js/commonSecondlyTab.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<%
    String coverageFilterDisplay = "N";
    OasisFormField coverageFilter = (OasisFormField) fieldsMap.get("coverageFilterDisplay");
    if (coverageFilter != null && !StringUtils.isBlank(coverageFilter.getDefaultValue())) {
        coverageFilterDisplay = coverageFilter.getDefaultValue();
    }
    pageContext.setAttribute("coverageFilterDisplay", coverageFilterDisplay.toUpperCase());
%>

<form action="maintainCoverage.do" method=post name="coverageList">

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
<script type="text/javascript">
if (getObject("pageHeader")) {
    hideShowElementByClassName(getObject("pageHeader"), true);
}
subFrameId = "coveragePageFrame";
secondlyTabDivId = "coverageSubTab";
requiredSubmitMainPageTabArr = ["COMP_UPDATE", "VL_COVG"];
</script>
<%@ include file="/policymgr/policyListNavigate.jsp" %>
<%@ include file="/core/pageheader.jsp" %>
<%@ include file="/pmcore/commonFormHeader.jsp" %>

<input type="hidden" name="dateChangeAllowedB"
       value="<c:out value="${policyHeader.riskHeader.dateChangeAllowedB}"/>">
<input type="hidden" name="riskBaseRecordId"
       value="<c:out value="${policyHeader.riskHeader.riskBaseRecordId}"/>">
<input type="hidden" name="riskStatusCode"
       value="<c:out value="${policyHeader.riskHeader.baseRiskStatusCode}"/>">
<input type="hidden" name="practiceStateCode"
       value="<c:out value="${policyHeader.riskHeader.practiceStateCode}"/>">
<input type="hidden" name="wipB"
       value="<c:out value="${policyHeader.wipB}"/>">
<input type="hidden" name="transEffectiveFromDate"
       value="<c:out value="${policyHeader.lastTransactionInfo.transEffectiveFromDate}"/>">
<input type="hidden" name="riskEffectiveFromDate"
       value="<c:out value="${policyHeader.riskHeader.earliestContigEffectiveDate}"/>">
<input type="hidden" name="riskEffectiveToDate"
       value="<c:out value="${policyHeader.riskHeader.dateChangeAllowedRiskDate}"/>">
<input type="hidden" name="coveragePartConfigured"
       value="<c:out value="${policyHeader.coveragePartConfigured}"/>">
<input type=hidden name=isCoverageSpecified value="Y">
<input type="hidden" name="termBaseRecordId"
       value="<c:out value="${policyHeader.termBaseRecordId}"/>">
<input type="hidden" name="policyStatus" value="<c:out value="${policyHeader.policyStatus}"/>"/>
<input type="hidden" name="policyScreenMode" value="<c:out value="${policyHeader.screenModeCode}"/>">
<%-- Show error message --%>
<tr>
    <td colspan=8>
        <oweb:message/>
    </td>
</tr>

<c:set var="policyHeaderDisplayMode" value="hide"/>
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
<tr>
    <td colspan=8 align=center>
        <c:choose>
            <c:when test="${coverageFilterDisplay eq 'Y'}">
                <table cellpadding="0" cellspacing="0" width="100%">
                    <tr>
                        <td width="65%">
                            <jsp:include page="/policymgr/policyNavigate.jsp"/>
                        </td>
                        <td width="1px">&nbsp;</td>
                        <td width="35%">
                            <jsp:include page="/core/compiledFormFields.jsp">
                                <jsp:param name="headerText" value="Filter"/>
                                <jsp:param name="isGridBased" value="false"/>
                                <jsp:param name="divId" value="coverageFilterDiv"/>
                                <jsp:param name="isLayerVisibleByDefault" value="true"/>
                                <jsp:param name="excludePageFields" value="true"/>
                                <jsp:param name="includeLayersWithPrefix" value="PM_COVG_FILTER"/>
                            </jsp:include>
                        </td>
                    </tr>
                </table>
            </c:when>
            <c:otherwise>
                <jsp:include page="/policymgr/policyNavigate.jsp"/>
            </c:otherwise>
        </c:choose>
    </td>
</tr>
<c:if test="${dataBean != null}">
<tr>
    <td align=center>
        <fmt:message key="pm.maintainCoverage.coverageList.header" var="panelTitleForCoverage" scope="page"/>
        <%
            String panelTitleForCoverage = (String) pageContext.getAttribute("panelTitleForCoverage");
            if (policyHeader.getRiskHeader() != null) {
                panelTitleForCoverage += " " + policyHeader.getRiskHeader().getRiskName();
            }
            else {
                panelTitleForCoverage += " ";
            }

        %>
        <%-- load action group based on policyCycleCode --%>
        <% String actionGroupId = "";
            if (policyHeader.getPolicyCycleCode().isPolicy()) {
                actionGroupId = "PM_COVG_GRID_AIG";
            }
            else if (policyHeader.getPolicyCycleCode().isQuote()) {
                actionGroupId = "PM_QT_COVG_GRID_AIG";
            }%>
        <oweb:panel panelTitleId="panelTitleIdForCoverage" panelContentId="panelContentIdForCoverage"
                    panelTitle="<%= panelTitleForCoverage %>">
            <tr>
                <td colspan="6">
                    <oweb:actionGroup actionItemGroupId="<%= actionGroupId %>" layoutDirection="horizontal"
                                      cssColorScheme="gray"/>
                </td>
            </tr>
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="coverageList" scope="request"/>
                    <c:set var="gridDisplayGridId" value="coverageListGrid" scope="request"/>
                    <c:set var="gridDetailDivId" value="coverageDetailDiv" scope="request"/>
                    <c:set var="datasrc" value="#coverageListGrid1" scope="request"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            <tr>
                <td>&nbsp;</td>
            </tr>
            <%
                if(pmUIStyle.equals("B")) {
            %>
            <tr>
                <td align=center>
                    <fmt:message key="pm.maintainCoverage.coverageForm.header" var="coverageFormHeader"
                                 scope="request"/>
                    <% String coverageFormHeader = (String) request.getAttribute("coverageFormHeader"); %>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="headerText" value="<%=  coverageFormHeader %>"/>
                        <jsp:param name="includeLayersWithPrefix" value="PM_COVG_GH"/>
                    </jsp:include>
                </td>
            </tr>
            <tr>
                <td>&nbsp;</td>
            </tr>
            <%
                }
            %>
        </oweb:panel>
    </td>
</tr>

<%
    if(pmUIStyle.equals("B")) {
%>
<%-- Component Layer --%>
<fmt:message key="pm.maintainCoverage.componentList.header" var="componentListHeader" scope="request"/>
<fmt:message key="pm.maintainCoverage.componentForm.header" var="headerText" scope="request"/>
<jsp:include page="/componentmgr/maintainComponent.jsp"/>
<%
    }
%>

<%
    if(pmUIStyle.equals("T")) {
%>
<c:if test="${policyHeader.policyCycleCode=='POLICY'}">
    <c:set var="tabMenuGroupId" value="PM_COVERAGE_PAGE_TG"></c:set>
    <script type="text/javascript">
        secondlyTabPrefix = "PM_CP_";
        secondlyTabMenuGroupId = "PM_COVERAGE_PAGE_TG";
    </script>
</c:if>
<c:if test="${policyHeader.policyCycleCode=='QUOTE'}">
    <c:set var="tabMenuGroupId" value="PM_QT_COVERAGE_PAGE_TG"></c:set>
    <script type="text/javascript">
        secondlyTabPrefix = "PM_QT_CP_";
        secondlyTabMenuGroupId = "PM_QT_COVERAGE_PAGE_TG";
    </script>
</c:if>
<%@ include file="/core/tabheader.jsp" %>
<tr>
    <td align=center>
        <div id="coverageSubTab">
            <div id="coverageDetail" >
                <fmt:message key="pm.maintainCoverage.coverageForm.header" var="coverageFormHeader"
                             scope="request"/>
                <% String coverageFormHeader = (String) request.getAttribute("coverageFormHeader"); %>
                <jsp:include page="/core/compiledFormFields.jsp">
                    <jsp:param name="headerText" value="<%=  coverageFormHeader %>"/>
                    <jsp:param name="includeLayersWithPrefix" value="PM_COVG_GH"/>
                </jsp:include>
            </div>
            <div id="componentDetail" >
                <table style="width:100%">
                    <fmt:message key="pm.maintainCoverage.componentList.header" var="componentListHeader" scope="request"/>
                    <fmt:message key="pm.maintainCoverage.componentForm.header" var="headerText" scope="request"/>
                    <jsp:include page="/componentmgr/maintainComponent.jsp"/>
                </table>
            </div>
            <iframe id="coveragePageFrame" scrolling="no" allowtransparency="true" height="100%"
                    frameborder="0" src="" onload = "handleOnSubTabOnload()"></iframe>
        </div>
    </td>
</tr>
<%@ include file="/core/tabfooter.jsp" %>
<%
   }
%>
</c:if>

<%

    // Initialize Sys Parms for JavaScript to use
    String pmCheckExcess = SysParmProvider.getInstance().getSysParm("PM_CHECK_EXCESS", "N");
    String pmRetrieveCvgAddl = SysParmProvider.getInstance().getSysParm("PM_RETRIEVE_CVG_ADDL", "N");
%>
<script type="text/javascript">
    setSysParmValue("PM_CHECK_EXCESS", '<%=pmCheckExcess%>');
    setSysParmValue("PM_RETRIEVE_CVG_ADDL", '<%=pmRetrieveCvgAddl%>');

    var currentCoverageId = <%=policyHeader.getCurrentSelectedId(RequestIds.COVERAGE_ID)%>;
</script>

<tr>
    <td>&nbsp;</td>
</tr>

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
    var transaction = new Transaction();
    if ((!isEmpty(transaction.endorsementQuoteId)) && (transaction.endorsementQuoteId != 'null')) {
        pageTitle = pageTitle + "   " + transaction.endorsementQuoteId;
    }
    setPageTitle("pageTitleForpageHeaderForPolicyFolder",pageTitle);
</script>
<jsp:include page="/core/footer.jsp"/>
</form>