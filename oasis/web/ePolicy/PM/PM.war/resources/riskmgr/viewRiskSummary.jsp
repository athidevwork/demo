
<%--
  Description:

  Author: xnie
  Date: Dec 25, 2013


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  12/25/2013       xnie        148083 - Initial version
  07/25/2014       awu         152034 Modified currentRiskId to get the value from the policyHeader.
  10/13/2015       tzeng       164679 Add riskRelationMessageType input label to transfer value to display message at
                                      this page.
  01/28/2016       wdang       169024 - Reverted changes of 164679.
  07/06/2016       tzeng       167531 - Added commonTab.js to be included.
  03/10/2017       wrong       180676 - Added code to display the page in new UI tab style.
  07/12/2017       lzhang      186847   Reflect grid replacement project changes
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2013 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ include file="/core/header.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<%@ include file="/core/invokeWorkflow.jsp" %>

<script type="text/javascript" src="js/viewRiskSummary.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=appPath%>/pmcore/js/commonTab.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=appPath%>/pmcore/js/commonSecondlyTab.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<form action="viewRiskSummary.do" method=post name="riskList">
<%@ include file="/pmcore/commonFormHeader.jsp" %>

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
    subFrameId = "riskSummaryPageFrame";
    secondlyTabDivId = "riskSummarySubTab";
    requiredSubmitMainPageTabArr = ["RISK_RELATION","COPYALL","DELETEALL"];
</script>
<%@ include file="/policymgr/policyListNavigate.jsp" %>
<%@ include file="/core/pageheader.jsp" %>
<input type="hidden" name="openWhichWindow" value="">
<input type="hidden" name="policyScreenMode" value="<%= policyHeader.getScreenModeCode() %>">
<input type="hidden" name="policyStatus" value="<c:out value="${policyHeader.policyStatus}"/>"/>
<input type="hidden" name="termBaseRecordId" value="<c:out value="${policyHeader.termBaseRecordId}"/>"/>
<input type="hidden" name="isReinstateIbnrRiskValid" value="<c:out value="${isReinstateIbnrRiskValid}"/>"/>
<input type="hidden" name="policyExpirationDate" value="<c:out value="${policyHeader.policyExpirationDate}"/>"/>
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

<c:if test="${dataBean != null}">
<tr>
    <td align=center>
        <fmt:message key="pm.viewRiskSummary.riskList.header" var="panelTitleForRisk" scope="page"/>
        <%
            String panelTitleForRisk = (String) pageContext.getAttribute("panelTitleForRisk");
        %>
        <%-- load action group based on policyCycleCode --%>
        <% String actionGroupId = "";
            if (policyHeader.getPolicyCycleCode().isPolicy()) {
                actionGroupId = "PM_RISK_SUMMARY_AIG";
            }
            else if (policyHeader.getPolicyCycleCode().isQuote()) {
                actionGroupId = "PM_QT_RISK_SUMMARY_AIG";
            }%>
        <oweb:panel panelTitleId="panelTitleIdForRisk" panelContentId="panelContentIdForRisk"
                    panelTitle="<%= panelTitleForRisk %>">
            <tr>
                <td colspan="6">
                    <oweb:actionGroup actionItemGroupId="<%= actionGroupId %>" layoutDirection="horizontal"
                                      cssColorScheme="gray"/>
                </td>
            </tr>
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="riskList" scope="request"/>
                    <c:set var="gridDisplayGridId" value="riskListGrid" scope="request"/>
                    <c:set var="gridDetailDivId" value="riskDetailDiv" scope="request"/>
                    <c:set var="datasrc" value="#riskListGrid1" scope="request"/>
                    <c:set var="cacheResultSet" value="true"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            <tr>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td align=center>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="isPanelHiddenByDefault" value="true"/>
                    </jsp:include>
                </td>
            </tr>
        </oweb:panel>
    </td>
</tr>
</c:if>

<%
  if (pmUIStyle.equals("T")) {
%>
<c:if test="${policyHeader.policyCycleCode=='POLICY'}">
    <c:set var="tabMenuGroupId" value="PM_RISK_SUMMARY_PAGE_TG"></c:set>
    <script type="text/javascript">
        secondlyTabPrefix = "PM_RSP_";
        secondlyTabMenuGroupId = "PM_RISK_SUMMARY_PAGE_TG";
    </script>
</c:if>
<c:if test="${policyHeader.policyCycleCode=='QUOTE'}">
    <c:set var="tabMenuGroupId" value="PM_QT_RS_SUMMARY_PAGE_TG"></c:set>
    <script type="text/javascript">
        secondlyTabPrefix = "PM_QT_RSP_";
        secondlyTabMenuGroupId = "PM_QT_RS_SUMMARY_PAGE_TG";
    </script>
</c:if>
<%@ include file="/core/tabheader.jsp" %>
<tr>
    <td align="center">
        <div id="riskSummarySubTab">
            <iframe id="riskSummaryPageFrame" scrolling="no" allowtransparency="true" height="100%"
                    frameborder="0" src="" onload = "handleOnSubTabOnload()"></iframe>
        </div>
    </td>
</tr>
<%@ include file="/core/tabfooter.jsp" %>
<%
  }
%>




<%


    // Initialize Sys Parms for JavaScript to use
    String pmLocToPolholder  = SysParmProvider.getInstance().getSysParm("PM_LOC_TO_POLHOLDER", "N");
    String pmGiveCountyWanng = SysParmProvider.getInstance().getSysParm("PM_GIVE_COUNTY_WRNNG",  "N");
    String pmAddMappedDefaults = SysParmProvider.getInstance().getSysParm("PM_ADD_MAPPED_DFLTS",  "N");


%>
<script type="text/javascript">
    setSysParmValue("PM_LOC_TO_POLHOLDER", '<%=pmLocToPolholder %>');
    setSysParmValue("PM_GIVE_COUNTY_WRNNG", "<%=pmGiveCountyWanng%>");
    setSysParmValue("PM_ADD_MAPPED_DFLTS", "<%=pmAddMappedDefaults%>");

    var currentRiskId = <%=policyHeader.getRiskHeader()==null?null:policyHeader.getRiskHeader().getRiskId()%>;
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
    setPageTitle("pageTitleForpageHeaderForPolicyFolder", pageTitle);
</script>

<jsp:include page="/core/footer.jsp"/>
</form>