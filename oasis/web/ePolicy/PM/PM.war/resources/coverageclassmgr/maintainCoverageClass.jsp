<%@ page import="org.apache.struts.Globals"%>
<%@ page import="org.apache.struts.taglib.html.Constants"%>
<%@ page import="dti.oasis.session.UserSessionManager" %>
<%--
  Description:

  Author: gjlong
  Date: Jan 10, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  07/09/2007       sxm         Added logic to get the last selected coverageId from user session
  08/22/2007       sxm         Added navigation layer
  08/24/2007       sxm         Added policy list navigation
  09/27/2007       sxm         Removed pageBackLink
  10/02/2007       sxm         Let Message tag handle policy lock message
  09/10/2010       wfu         111776: Replaced hardcode string with resource definition
  02/13/2014       awu         147405 - Changed the value of riskStatusCode and coverageStatusCode to its base record status.
  07/25/2014       awu         152034 - Modified to get the currentRiskId from policyHeader.
  07/06/2016       tzeng       167531 - Added commonTab.js to be included.
  03/10/2017       sjin        180675 - Added commonSecondlyTab.js to be included, initialized the parameters which named
                                        "subFrameId" and "secondlyTabDivId", changed the page displayed way for UI change.
  07/12/2017       lzhang      186847: Reflect grid replacement project changes
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
<%@ include file="/core/invokeWorkflow.jsp"%>

<script type="text/javascript" src="js/maintainCoverageClass.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=appPath%>/pmcore/js/commonTab.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=appPath%>/pmcore/js/commonSecondlyTab.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

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
<script type="text/javascript">if (getObject("pageHeader")) { hideShowElementByClassName(getObject("pageHeader"), true);}
subFrameId = "coverageClassPageFrame";
secondlyTabDivId = "coverageClassSubTab";
</script>
<%@ include file="/policymgr/policyListNavigate.jsp" %>
<%@ include file="/core/pageheader.jsp" %>

<form action="maintainCoverageClass.do" method=post name="coverageClassList">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <input type="hidden" name="transEffectiveFromDate"
           value="<c:out value="${policyHeader.lastTransactionInfo.transEffectiveFromDate}"/>">
    <input type="hidden" name="coverageEffectiveFromDate"
           value="<c:out value="${policyHeader.coverageHeader.coverageEffectiveFromDate}"/>">
    <input type="hidden" name="coverageEffectiveToDate"
           value="<c:out value="${policyHeader.coverageHeader.coverageEffectiveToDate}"/>">
    <input type="hidden" name="coverageBaseRecordId"
           value="<c:out value="${policyHeader.coverageHeader.coverageBaseRecordId}"/>">
    <input type="hidden" name="retroactiveDate"
           value="<c:out value="${policyHeader.coverageHeader.retroactiveDate}"/>">
    <input type="hidden" name="coveragePartConfigured"
           value="<c:out value="${policyHeader.coveragePartConfigured}"/>">
    <input type="hidden" name="screenModeCode"
           value="<c:out value="${policyHeader.screenModeCode}"/>">
    <input type="hidden" name="policyStatus" value="<c:out value="${policyHeader.policyStatus}"/>"/>
    <input type="hidden" name="termBaseRecordId"
           value="<c:out value="${policyHeader.termBaseRecordId}"/>">
    <input type="hidden" name="riskStatusCode"
           value="<c:out value="${policyHeader.riskHeader.baseRiskStatusCode}"/>">
    <input type="hidden" name="riskBaseRecordId"
           value="<c:out value="${policyHeader.riskHeader.riskBaseRecordId}"/>">
    <input type="hidden" name="coverageStatusCode"
           value="<c:out value="${policyHeader.coverageHeader.baseCoverageStatusCode}"/>">
    <input type="hidden" name="riskEffectiveFromDate"
           value="<c:out value="${policyHeader.riskHeader.earliestContigEffectiveDate}"/>">
    <input type="hidden" name="riskEffectiveToDate"
           value="<c:out value="${policyHeader.riskHeader.dateChangeAllowedRiskDate}"/>">

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
            <jsp:include page="/policymgr/policyNavigate.jsp" />
        </td>
    </tr>
    <c:if test="${dataBean != null}">
    <tr>
        <td align=center>
            <% String gridTitle = " ";
                if(policyHeader.getRiskHeader()!=null&&policyHeader.getCoverageHeader()!=null){
                    gridTitle += policyHeader.getRiskHeader().getRiskName()+", Coverage: "+ policyHeader.getCoverageHeader().getCoverageName();
                 }

            %>
            <fmt:message key="pm.maintainCoverageClass.coverageClassList.header" var="panelTitleForCvgClass" scope="page"/>
            <%
                  String panelTitleForCvgClass = (String) pageContext.getAttribute("panelTitleForCvgClass") + gridTitle;
            %>
            <%-- load action group based on policyCycleCode --%>
            <% String actionGroupId = "";
                if (policyHeader.getPolicyCycleCode().isPolicy()) {
                actionGroupId = "PM_COVG_CLASS_AIG";
                }
                else if (policyHeader.getPolicyCycleCode().isQuote()) {
                         actionGroupId = "PM_QT_COVG_CLASS_AIG";
                }%>
            <oweb:panel panelTitleId="panelTitleIdForCvgClass" panelContentId="panelContentIdForCvgClass" panelTitle="<%= panelTitleForCvgClass %>" >
    <tr>
        <td colspan="6">
            <oweb:actionGroup actionItemGroupId="<%= actionGroupId %>" layoutDirection="horizontal" cssColorScheme="gray"/>
        </td>
    </tr>
    <tr>
        <td colspan="6" align=center>
            <c:set var="gridDisplayFormName" value="coverageClassList" scope="request" />
            <c:set var="gridDisplayGridId" value="coverageClassListGrid" scope="request" />
            <c:set var="gridDetailDivId" value="coverageClassDetailDiv" scope="request" />
            <c:set var="datasrc" value="#coverageClassListGrid1" scope="request" />
            <%@ include file="/pmcore/gridDisplay.jsp" %>
        </td>
    </tr>
    <tr><td>&nbsp;</td></tr>
    <%
        if(pmUIStyle.equals("B")) {
    %>
    <tr>
        <td align=center>
            <fmt:message key="pm.maintainCoverageClass.coverageClassForm.header" var="coverageClassFormHeader" scope="request"/>
            <% String coverageClassFormHeader = (String) request.getAttribute("coverageClassFormHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%=  coverageClassFormHeader %>" />
                <jsp:param name="includeLayersWithPrefix" value="PM_Coverage_Class" />
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
        <c:set var="tabMenuGroupId" value="PM_COVERAGE_CLASS_PAGE_TG"></c:set>
    <script type="text/javascript">
        secondlyTabPrefix = "PM_CCP_";
        secondlyTabMenuGroupId = "PM_COVERAGE_CLASS_PAGE_TG";
    </script>
    </c:if>
    <c:if test="${policyHeader.policyCycleCode=='QUOTE'}">
        <c:set var="tabMenuGroupId" value="PM_QT_COVCLASS_PAGE_TG"></c:set>
    <script type="text/javascript">
        secondlyTabPrefix = "PM_QT_CCP_";
        secondlyTabMenuGroupId = "PM_QT_COVCLASS_PAGE_TG";
    </script>
    </c:if>
    <%@ include file="/core/tabheader.jsp" %>
    <tr>
        <td align=center>
            <div id="coverageClassSubTab">
                <div id="coverageClassDetail">
                    <fmt:message key="pm.maintainCoverageClass.coverageClassForm.header" var="coverageClassFormHeader" scope="request"/>
                    <% String coverageClassFormHeader = (String) request.getAttribute("coverageClassFormHeader"); %>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="headerText" value="<%=  coverageClassFormHeader %>" />
                        <jsp:param name="includeLayersWithPrefix" value="PM_Coverage_Class" />
                    </jsp:include>
                </div>
                <iframe id="coverageClassPageFrame" scrolling="no" allowtransparency="true" height="100%"
                        frameborder="0" src="" hidden="true"></iframe>
            </div>
        </td>
    </tr>
    <%@ include file="/core/tabfooter.jsp" %>
    <%
         }
    %>
    </c:if>

    <script type="text/javascript">
        var currentCoverageClassId = <%=policyHeader.getCurrentSelectedId(RequestIds.COVERAGE_CLASS_ID)%>;

    </script>

    <tr><td>&nbsp;</td></tr>

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