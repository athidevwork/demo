<%@ page import="org.apache.struts.Globals"%>
<%@ page import="org.apache.struts.taglib.html.Constants"%>
<%@ page import="dti.pm.core.http.RequestIds"%>
<%@ page import="dti.oasis.busobjs.YesNoFlag" %>
<%@ page language="java"%>
<%--
  Description: Create Policy page

  Author: Sharon Ma
  Date: Feb 14, 2007

  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  07/05/2007       sxm         made the page a DivPopup
  01/19/2011       wfu         113566 - Added logic to handle copying policy from risk.
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>

<c:set var="isForDivPopup" value="true"></c:set>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<script type="text/javascript" src="js/createPolicy.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form action="createPolicy.do" method="POST" name="createPolicyList">
<%@ include file="/pmcore/commonFormHeader.jsp" %>

<c:if test="${isFromCopyNew == 'Y'}">
<input type="hidden" name="isFromCopyNew" id="isFromCopyNew" value='<c:out value="${isFromCopyNew}"/>'>
<input type="hidden" name="policyNo" id="policyNo" value='<c:out value="${policyNo}"/>'>
<input type="hidden" name="riskId" id="riskId" value='<c:out value="${riskId}"/>'>
</c:if>

<tr>
    <td>
        <table cellpadding=0 cellspacing=0 width=100%>
            <tr>
                <td><oweb:message/></td>
            </tr>
        </table>
    </td>
</tr>

<tr>
    <td align=center>
        <fmt:message key="pm.createPolicyForm.header" var="createPolicyFormHeader" scope="request"/>
        <% String createPolicyFormHeader = (String) request.getAttribute("createPolicyFormHeader"); %>
        <jsp:include page="/core/compiledFormFields.jsp">
            <jsp:param name="divId" value="createPolicyFormDiv" />
            <jsp:param name="headerText" value="<%=  createPolicyFormHeader %>" />
            <jsp:param name="isGridBased" value="false" />
        </jsp:include>
    </td>
</tr>
<tr>
    <td align=center>
        <oweb:actionGroup actionItemGroupId="PM_CREPOL_AIG" layoutDirection="horizontal"/>
    </td>
</tr>

<% if (request.getAttribute(RequestIds.DATA_BEAN) != null) { %>
    <jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
    <tr>
        <td align=center>
            <fmt:message key="pm.createPolicyList.header" var="panelTitleIdForPolicyList" scope="page"/>
            <%
                String panelTitleIdForPolicyList = (String) pageContext.getAttribute("panelTitleIdForPolicyList");
            %>
            <oweb:panel panelTitleId="panelTitleIdForPolicyList" panelContentId="panelContentIdForPolicyList" panelTitle="<%= panelTitleIdForPolicyList %>" >

            <tr>
                <td colspan="6" align=center><br/>
                    <c:set var="gridDisplayFormName" value="createPolicyList" scope="request" />
                    <c:set var="gridDisplayGridId" value="createPolicyListGrid" scope="request" />
                    <c:set var="datasrc" value="#createPolicyListGrid1" scope="request" />
                    <c:set var="cacheResultSet" value="false"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            <tr>
                <td align=center>
                    <oweb:actionGroup actionItemGroupId="PM_CREPOL_GRID_AIG" layoutDirection="horizontal"/>
                </td>
            </tr>
            </oweb:panel>
        </td>
    </tr>
<%
    // check if we should create policy automatically
    if (dataBean.getRowCount() == 1 &&
        YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm("PM_AUTO_SEL_NEW_POL", "N")).booleanValue()) {
%>
<script type="text/javascript">
    function handleOnLoad() {
        if (!isEmptyRecordset(createPolicyListGrid1.recordset) && !hasObject("isFromCopyNew"))
            handleOnButtonClick("Create");
    }
</script>
<%
    }
}
%>
<tr><td>&nbsp;</td></tr>
<jsp:include page="/core/footerpopup.jsp" />

