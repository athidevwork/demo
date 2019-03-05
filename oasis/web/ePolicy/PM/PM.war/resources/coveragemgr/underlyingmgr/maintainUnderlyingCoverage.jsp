<%--
  Description:
  JSP page for maintain underlying policy
  Author: wrong
  Date: Aug 29, 2018


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  08/29/2018       wrong         188391 - Initial version.
  11/13/2018       tyang         194100 - Add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2018 Delphi Technology, inc. (dti)
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<c:set var="isForDivPopup" value="true"></c:set>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<script type="text/javascript" src="<%=appPath%>/coveragemgr/underlyingmgr/js/maintainUnderlyingCoverage.js
?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<form name="underlyingCoverageList" action="<%=appPath%>/coveragemgr/underlyingmgr/maintainUnderlyingCoverage.do"
      method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

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
            <fmt:message key="pm.maintainunderlyingPolicy.underlyingPolicyList.header" var="panelTitleForUnderPol"
                         scope="page"/>
                <%
                String panelTitleForUnderPol = (String) pageContext.getAttribute("panelTitleForUnderPol");
            %>
            <oweb:panel panelTitleId="panelTitleIdForUnderPol" panelContentId="panelContentIdForUnderPol"
                        panelTitle="<%=panelTitleForUnderPol%>">
    <tr>
        <td colspan="6">
            <oweb:actionGroup actionItemGroupId="PM_UNDERCOVG_AIG" layoutDirection="horizontal"
                              cssColorScheme="gray"/>
        </td>
    </tr>
    <tr>
        <td colspan="12" align=center>
            <c:set var="gridDisplayFormName" value="underlyingCoverageList" scope="request"/>
            <c:set var="gridDisplayGridId" value="underlyingCoverageListGrid" scope="request"/>
            <c:set var="gridDetailDivId" value="underlyingCoverageDetailDiv" scope="request"/>
            <c:set var="datasrc" value="#underlyingCoverageListGrid1" scope="request"/>
            <c:set var="cacheResultSet" value="false"/>
            <%@ include file="/pmcore/gridDisplay.jsp" %>
        </td>
    </tr>
    <tr>
        <td>&nbsp;</td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.maintainunderlyingPolicy.underlyingPolicyForm.header"
                         var="underCovgFormHeader" scope="request"/>
            <% String underCovgFormHeader = (String) request.getAttribute("underCovgFormHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%=  underCovgFormHeader %>"/>
                <jsp:param name="isGridBased" value="true"/>
            </jsp:include>
        </td>
    </tr>
    </oweb:panel>
    </td>
    </tr>
    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="PM_SAVE_CLOSE_AIG" layoutDirection="horizontal"/>
        </td>
    </tr>

<jsp:include page="/core/footerpopup.jsp"/>