
<%--
  Description:

  Author: mlmanickam
  Date: Jul 12, 2006


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  07/09/2007       sxm         Added logic to get the last selected riskId from user session
  08/24/2007       sxm         Added policy list navigation
  09/27/2007       sxm         Removed pageBackLink
  10/02/2007       sxm         Let Message tag handle policy lock message
  10/09/2009       gxc         Add reference to csLoadNotes.js to handle RTE notes display
  09/10/2010       wfu         111776: Replaced hardcode string with resource definition
  06/06/2012       xnie        132114 Added a hidden field policyExpirationDate.
  12/19/2013       jyang       148585 Add currentCoverageId variable.
  07/25/2014       awu         152034 1). Roll back the changes of issue148585.
                                      2). Modified currentRiskId to get the value from the policyHeader.
  10/13/2015       tzeng       164679 - Add riskRelationMessageType input label to transfer value to display message at
                                        parent view risk summary page.
  11/10/2015       eyin        167335 - Add logic to check if value of attribute 'riskRelationMessageType' is available
                                        before we take the same from RequestStorageManager.
  01/28/2016       wdang       169024 - Reverted changes of 164679&167335.
  07/06/2016        tzeng      167531 - Added commonTab.js to be included.
  03/17/2017       xnie        183463 - Added the missing hidden field lastTransactionId by 142975 back.
  03/10/2017       eyin        180675 - Added code to display the page in new UI tab style.
  07/12/2017       lzhang      186847 - Reflect grid replacement project changes
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
<%@ include file="/core/invokeWorkflow.jsp" %>

<script type="text/javascript" src="<%=csPath%>/js/csLoadNotes.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="js/maintainRisk.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=appPath%>/pmcore/js/commonTab.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=appPath%>/pmcore/js/commonSecondlyTab.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<form action="maintainRisk.do" method=post name="riskList">
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
subFrameId = "riskPageFrame";
secondlyTabDivId = "riskSubTab";
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
<input type="hidden" name="lastTransactionId" value="<c:out value="${policyHeader.lastTransactionId}"/>"/>
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
        <fmt:message key="pm.maintainRisk.riskList.header" var="panelTitleForRisk" scope="page"/>
        <%
            String panelTitleForRisk = (String) pageContext.getAttribute("panelTitleForRisk");
        %>
        <%-- load action group based on policyCycleCode --%>
        <% String actionGroupId = "";
            if (policyHeader.getPolicyCycleCode().isPolicy()) {
                actionGroupId = "PM_RISK_AIG";
            }
            else if (policyHeader.getPolicyCycleCode().isQuote()) {
                actionGroupId = "PM_QT_RISK_AIG";
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
            <%
                if(pmUIStyle.equals("B")) {
            %>
            <tr>
                <td align=center>
                    <fmt:message key="pm.maintainRisk.riskForm.header" var="riskFormHeader" scope="request"/>
                    <% String riskFormHeader = (String) request.getAttribute("riskFormHeader"); %>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="headerText" value="<%=  riskFormHeader %>"/>
                    </jsp:include>
                </td>
            </tr>
            <%
                }
            %>
        </oweb:panel>
    </td>
</tr>

<%
    if(pmUIStyle.equals("T")) {
%>
<c:if test="${policyHeader.policyCycleCode=='POLICY'}">
    <c:set var="tabMenuGroupId" value="PM_RISK_PAGE_TG"></c:set>
    <script type="text/javascript">
        secondlyTabPrefix = "PM_RP_";
        secondlyTabMenuGroupId = "PM_RISK_PAGE_TG";
    </script>
</c:if>
<c:if test="${policyHeader.policyCycleCode=='QUOTE'}">
    <c:set var="tabMenuGroupId" value="PM_QT_RISK_PAGE_TG"></c:set>
    <script type="text/javascript">
        secondlyTabPrefix = "PM_QT_RP_";
        secondlyTabMenuGroupId = "PM_QT_RISK_PAGE_TG";
    </script>
</c:if>
<%@ include file="/core/tabheader.jsp" %>
<tr>
    <td align=center>
        <div id="riskSubTab">
            <div id="riskDetail" style='display:none'>
                <fmt:message key="pm.maintainRisk.riskForm.header" var="riskFormHeader" scope="request"/>
                <% String riskFormHeader = (String) request.getAttribute("riskFormHeader"); %>
                <jsp:include page="/core/compiledFormFields.jsp">
                    <jsp:param name="headerText" value="<%=  riskFormHeader %>"/>
                </jsp:include>
            </div>
            <iframe id="riskPageFrame" scrolling="no" allowtransparency="true" height="100%"
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
    String pmLocToPolholder  = SysParmProvider.getInstance().getSysParm("PM_LOC_TO_POLHOLDER", "N");
    String pmGiveCountyWanng = SysParmProvider.getInstance().getSysParm("PM_GIVE_COUNTY_WRNNG",  "N");
    String pmAddMappedDefaults = SysParmProvider.getInstance().getSysParm("PM_ADD_MAPPED_DFLTS",  "N");


%>
<script type="text/javascript">
    setSysParmValue("PM_LOC_TO_POLHOLDER", '<%=pmLocToPolholder %>');
    setSysParmValue("PM_GIVE_COUNTY_WRNNG", "<%=pmGiveCountyWanng%>");
    setSysParmValue("PM_ADD_MAPPED_DFLTS", "<%=pmAddMappedDefaults%>");

    var currentRiskId = <%=policyHeader.getCurrentSelectedId(RequestIds.RISK_ID)%>;
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
