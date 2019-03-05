<%--
  Description: COI Holder Page

  Author: Joe Shen
  Date: May 18, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  03/20/2008       fcb         WorkflowAgent added.
  12/22/2010       dzhang      Issue 103792 - added logic for get & set system parameter.
  05/04/2011       dzhang      Issue 119903 - change the panel title to support i18n. 
  03/10/2017       wrong       Issue 180675 - Changed the message tag for new UI change.
  11/15/2018       eyin        Issue 194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2007 Delphi Technology, inc. (dti)
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<c:set var="isForDivPopup" value="true"></c:set>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<%@ include file="/core/invokeWorkflow.jsp" %>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>
<% // Initialize Sys Parms for JavaScript to use
    String isCisDesired = SysParmProvider.getInstance().getSysParm("PM_COI_CS_SEARCH", "Y");
    String coiCsRoles = SysParmProvider.getInstance().getSysParm("PM_COI_CS_ROLES","COI_HOLDER");
%>
<script type="text/javascript">
    setSysParmValue("PM_COI_CS_SEARCH", '<%=isCisDesired %>');
    setSysParmValue("PM_COI_CS_ROLES", '<%=coiCsRoles %>');
</script>
<script type="text/javascript" src="js/maintainCoi.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form action="maintainCoi.do" name="maintainCoiForm" method="post">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <input type="hidden" name="needToCaptureTransaction" value="<c:out value="${needToCaptureTransaction}"/>"/>
    <input type="hidden" name="pmCoiClaimsParam" value="<c:out value="${pmCoiClaimsParam}"/>"/>

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
            <%
                String panelTitleForCOI = MessageManager.getInstance().formatMessage("pm.maintainCoi.coiGridList.header",
                                             new String[]{request.getAttribute("riskName").toString(),
                                                  FormatUtils.formatDateTimeForDisplay(request.getAttribute("riskEffectiveFromDate").toString()),
                                                  FormatUtils.formatDateTimeForDisplay(request.getAttribute("riskEffectiveToDate").toString())});
            %>
            <oweb:panel panelTitleId="panelTitleIdForCOI" panelContentId="panelContentIdForCOI" panelTitle="<%= panelTitleForCOI %>" >
            <tr>
                <td colspan="6">
                    <oweb:actionGroup actionItemGroupId="PM_COI_GRID_AIG" layoutDirection="horizontal" cssColorScheme="gray"/>
                </td>
            </tr>
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="maintainCoiForm" scope="request"/>
                    <c:set var="gridDisplayGridId" value="maintainCoiListGrid" scope="request"/>
                    <c:set var="gridDetailDivId" value="maintainCoiListGridDiv" scope="request" />
                    <c:set var="datasrc" value="#maintainCoiListGrid1" scope="request"/>
                    <c:set var="cacheResultSet" value="false"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            <tr>
                <td>&nbsp;</td>
            </tr>
            <%-- Display grid form --%>
            <tr>
                <td align=center>
                    <fmt:message key="pm.maintainCoi.coiForm.header" var="coiFormHeader" scope="request"/>
                    <% String coiFormHeader = (String) request.getAttribute("coiFormHeader"); %>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="headerText" value="<%=  coiFormHeader %>" />
                        <jsp:param name="isGridBased" value="true" />
                    </jsp:include>
                </td>
            </tr>
            </oweb:panel>
            <tr>
                <td colspan="6" align=center>
                    <oweb:actionGroup actionItemGroupId="PM_COI_AIG"/>
                </td>
            </tr>
        </td>
    </tr>
    <br>

<jsp:include page="/core/footerpopup.jsp"/>
