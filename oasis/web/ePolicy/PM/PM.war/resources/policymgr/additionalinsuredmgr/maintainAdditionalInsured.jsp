<%--
  Description:
  Maintain Additional Insured information
  Author: yhchen
  Date: Oct 16, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  03/20/2008       fcb         WorkflowAgent added.
  08/23/2010       syang       Added policyExpirationDate.
  09/16/2010       syang       Issue 111445 - Added layer "PM_ADDIINS_COVERAGE" and system parameter "PM_ADDINS_COVG_DATA".
  03/10/2017       wli         Issue 180675 - Changed the message tag for new UI change.
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
<%@ include file="/core/invokeWorkflow.jsp" %>

<script type="text/javascript" src="<%=appPath%>/policymgr/additionalinsuredmgr/js/maintainAdditionalInsured.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<form name="additionalInsuredList" action="<%=appPath%>/policymgr/additionalinsuredmgr/maintainAdditionalInsured.do" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <input type="hidden" name="policyExpirationDate" value="<c:out value="${policyHeader.policyExpirationDate}"/>"/>
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
            <fmt:message key="pm.maintainAdditionalInsured.additionalInsuredList.header" var="panelTitleForAddiIns" scope="page"/>
            <%
                String panelTitleForAddiIns = (String) pageContext.getAttribute("panelTitleForAddiIns");
            %>
            <oweb:panel panelTitleId="panelTitleIdForSplHandling" panelContentId="panelContentIdForSplHandling" panelTitle="<%=panelTitleForAddiIns%>" >
            <tr>
                <td colspan="6">
                    <oweb:actionGroup actionItemGroupId="PM_ADDIINS_GRID_AIG" layoutDirection="horizontal" cssColorScheme="gray"/>
                </td>
            </tr>
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="additionalInsuredList" scope="request" />
                    <c:set var="gridDisplayGridId" value="additionalInsuredListGrid" scope="request" />
                    <c:set var="gridDetailDivId" value="additionalInsuredDetailDiv" scope="request" />
                    <c:set var="datasrc" value="#additionalInsuredListGrid1" scope="request" />
                    <c:set var="cacheResultSet" value="false"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr>
                <td align=center>
                    <fmt:message key="pm.maintainAdditionalInsured.additionalInsuredForm.header" var="addiInsFormHeader" scope="page"/>
                    <% String addiInsFormHeader = (String) pageContext.getAttribute("addiInsFormHeader"); %>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="headerText" value="<%=  addiInsFormHeader %>" />
                        <jsp:param name="isGridBased" value="true" />
                        <jsp:param name="excludeLayerIds" value=",PM_ADDIINS_COVERAGE,"/>
                    </jsp:include>
                </td>
            </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.maintainAdditionalInsured.coverageData.header" var="coverageDataHeader" scope="page"/>
            <% String coverageDataHeader = (String) pageContext.getAttribute("coverageDataHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%=coverageDataHeader%>"/>
                <jsp:param name="divId" value="coverageLayer"/>
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="isLayerVisibleByDefault" value="true"/>
                <jsp:param name="excludePageFields" value="true"/>
                <jsp:param name="includeLayersWithPrefix" value="PM_ADDIINS_COVERAGE"/>
            </jsp:include>
        </td>
    </tr>
    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="PM_ADDIINS_AIG" layoutDirection="horizontal"/>
        </td>
    </tr>
<%
    // Initialize Sys Parms for JavaScript to use
    String pmAddInsCovgData  = SysParmProvider.getInstance().getSysParm("PM_ADDINS_COVG_DATA", "N");
%>
<script type="text/javascript">
    setSysParmValue("PM_ADDINS_COVG_DATA", '<%=pmAddInsCovgData%>');
</script>
<jsp:include page="/core/footerpopup.jsp"/>

