<%--
  Description:

  Author: jmpotosky
  Date: Nov 10, 2006


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  08/23/2007       sxm         Added policy list navigation
  09/27/2007       sxm         Removed pageBackLink
  10/02/2007       sxm         Let Message tag handle policy lock message
  03/14/2008       sxm         Moved maintainEndQuote.js include to policyHeader.jsp
  09/10/2008       yyh         Add maintainComponent.js and policy component
  06/11/2010       bhong       108653 -  added policyScreenMode hidden field
  09/10/2010       wfu         111776: Replaced hardcode string with resource definition
  04/06/2011       fcb         119324: added isNewPolicyCreated
  07/06/2016       tzeng       167531 - Added commonTab.js to be included.
  03/10/2017       wli         180675 - 1. Initialized the parameters which  named "subFrameId, secondlyTabDivId,
                                           requiredSubmitMainPageTabArr"
                                        2. Modified the way of page displayed for UI change.
  07/17/2017       ssheng      185382 - Add TAIL for requiredSubmitMainPageTabArr.
  07/12/2017       lzhang      186847   Reflect grid replacement project changes
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ include file="/core/header.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<%@ include file="/core/invokeWorkflow.jsp"%>
<input type="hidden" name="isNewPolicyCreated" value="<%= request.getAttribute(RequestIds.IS_NEW_POLICY_CREATED) %>">

<script type="text/javascript" src="<%=csPath%>/js/csLoadNotes.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=appPath%>/componentmgr/js/maintainComponent.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

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
<script type="text/javascript"> if (getObject("pageHeader")) { hideShowElementByClassName(getObject("pageHeader"), true);} </script>
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

<script type="text/javascript" src="js/maintainPolicy.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=appPath%>/pmcore/js/commonTab.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<%
    if(pmUIStyle.equals("T")) {
%>
	<script type="text/javascript" src="<%=appPath%>/pmcore/js/commonSecondlyTab.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
	<script type="text/javascript">
		subFrameId = "policyPageFrame";
		secondlyTabDivId = "policySubTab";
		requiredSubmitMainPageTabArr = ["UNDERWRITER", "CHG_EXP_DATE", "QUOTE_STATUS", "TAIL"];
	</script>
<%
    }
%>

<c:set scope="request" var="paymentPlanLstCOLSPAN" value="3"/>
<form action="maintainPolicy.do" method=post name="policyInfo">
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
<tr>
    <td align=center>
        <fmt:message key="pm.maintainPolicy.policyForm.header" var="policyFormHeader" scope="request"/>
        <% String policyFormHeader = (String) request.getAttribute("policyFormHeader"); %>
        <%-- load action group based on policyCycleCode --%>
        <% String actionGroupId = "";
            if (policyHeader.getPolicyCycleCode().isPolicy()) {
                actionGroupId = "PM_POLICY_TAB_AIG";
            }
            else if (policyHeader.getPolicyCycleCode().isQuote()) {
                actionGroupId = "PM_QT_POLICY_TAB_AIG";
            }%>
        <jsp:include page="/core/compiledFormFields.jsp">
            <jsp:param name="divId" value="policyDetailDiv" />
            <jsp:param name="headerText" value="<%=  policyFormHeader %>" />
            <jsp:param name="isGridBased" value="false" />
            <jsp:param name="actionItemGroupId" value="<%= actionGroupId %>"/>
            <jsp:param name="actionItemGroupIdCss" value="gray"/>
            <jsp:param name="actionItemGroupAlign" value="left"/>
            <jsp:param name="actionItemGroupLocation" value="top"/>
            <jsp:param name="excludeLayerIds" value=",PM_POLICY_COMP_GH,PM_POLICY_COMP_FORM,"/>
        </jsp:include>
    </td>
</tr>
<%
    if(pmUIStyle.equals("T")) {
%>
    <c:if test="${policyHeader.policyCycleCode=='POLICY'}">
        <c:set var="tabMenuGroupId" value="PM_POLICY_PAGE_TG"></c:set>
        <script type="text/javascript">
            secondlyTabPrefix = "PM_PP_";
            secondlyTabMenuGroupId = "PM_POLICY_PAGE_TG";
        </script>
    </c:if>
    <c:if test="${policyHeader.policyCycleCode=='QUOTE'}">
        <c:set var="tabMenuGroupId" value="PM_QT_POLICY_PAGE_TG"></c:set>
        <script type="text/javascript">
            secondlyTabPrefix = "PM_QT_PP_";
            secondlyTabMenuGroupId = "PM_QT_POLICY_PAGE_TG";
        </script>
    </c:if>
    <%@ include file="/core/tabheader.jsp" %>
	<tr>
		<td align=center>
			<div id="policySubTab">
				<div id="componentDetail" <%=((useJqxGrid)?"class='dti-hide'":"style='display:none'")%>>
					<fmt:message key="pm.maintainCoverage.componentList.header" var="componentListHeader" scope="page"/>
					<% String componentListHeader = (String) pageContext.getAttribute("componentListHeader"); %>
					<oweb:panel panelTitleId="panelTitleIdForComponent" panelContentId="panelContentIdForComponent" panelTitle="<%= componentListHeader %>" >
					<tr>
						<td colspan="6">
							<oweb:actionGroup actionItemGroupId="PM_COMP_GRID_AIG" layoutDirection="horizontal" cssColorScheme="gray"/>
						</td>
					</tr>
					<tr>
						<td align=center>
							<c:set var="gridDisplayFormName" value="policyInfo" scope="request"/>
							<c:set var="gridDisplayGridId" value="componentListGrid" scope="request"/>
							<c:set var="gridDetailDivId" value="componentDetailDiv" scope="request"/>
							<c:set var="gridId" value="componentListGrid" scope="request"/>
							<c:set var="gridSizeFieldIdPrefix" value="PM_POLICY_COMP_GH"/>
							<c:set var="datasrc" value="#componentListGrid1" scope="request"/>
							<%@ include file="/pmcore/gridDisplay.jsp" %>
						</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
					</tr>
					 <tr>
						<td align=center>
							<fmt:message key="pm.maintainCoverage.componentForm.header" var="componentFormHeader" scope="request"/>
							<% String componentFormHeader = (String) request.getAttribute("componentFormHeader"); %>
							<jsp:include page="/core/compiledFormFields.jsp">
								<jsp:param name="headerText" value="<%= componentFormHeader%>"/>
								<jsp:param name="isGridBased" value="true"/>
								<jsp:param name="divId" value="componentDetailDiv"/>
								<jsp:param name="isLayerVisibleByDefault" value="true"/>
								<jsp:param name="excludePageFields" value="true"/>
								<jsp:param name="includeLayersWithPrefix" value="PM_POLICY_COMP_FORM"/>
							</jsp:include>
						</td>
					</tr>
					</oweb:panel>
				</div>
				<iframe id="policyPageFrame" scrolling="no" allowtransparency="true" height="100%"
						frameborder="0" src="" onload="handleOnSubTabOnload()"></iframe>
			</div>
		</td>
	</tr>

	<%@ include file="/core/tabfooter.jsp" %>
<%
    }else if(pmUIStyle.equals("B")) {
%>
		<tr>
			<td align=center>
				<fmt:message key="pm.maintainCoverage.componentList.header" var="componentListHeader" scope="page"/>
					<% String componentListHeader = (String) pageContext.getAttribute("componentListHeader"); %>
				<oweb:panel panelTitleId="panelTitleIdForComponent" panelContentId="panelContentIdForComponent" panelTitle="<%= componentListHeader %>" >
					<tr>
						<td colspan="6">
							<oweb:actionGroup actionItemGroupId="PM_COMP_GRID_AIG" layoutDirection="horizontal" cssColorScheme="gray"/>
						</td>
					</tr>
					<tr>
						<td align=center>
							<c:set var="gridDisplayFormName" value="policyInfo" scope="request"/>
							<c:set var="gridDisplayGridId" value="componentListGrid" scope="request"/>
							<c:set var="gridDetailDivId" value="componentDetailDiv" scope="request"/>
							<c:set var="gridId" value="componentListGrid" scope="request"/>
							<c:set var="gridSizeFieldIdPrefix" value="PM_POLICY_COMP_GH"/>
							<c:set var="datasrc" value="#componentListGrid1" scope="request"/>
							<%@ include file="/pmcore/gridDisplay.jsp" %>
						</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td align=center>
							<fmt:message key="pm.maintainCoverage.componentForm.header" var="componentFormHeader" scope="request"/>
							<% String componentFormHeader = (String) request.getAttribute("componentFormHeader"); %>
							<jsp:include page="/core/compiledFormFields.jsp">
								<jsp:param name="headerText" value="<%= componentFormHeader%>"/>
								<jsp:param name="isGridBased" value="true"/>
								<jsp:param name="divId" value="componentDetailDiv"/>
								<jsp:param name="isLayerVisibleByDefault" value="true"/>
								<jsp:param name="excludePageFields" value="true"/>
								<jsp:param name="includeLayersWithPrefix" value="PM_POLICY_COMP_FORM"/>
							</jsp:include>
						</td>
					</tr>
				</oweb:panel>
			</td>
		</tr>
<%
    }
%>
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
</form>
