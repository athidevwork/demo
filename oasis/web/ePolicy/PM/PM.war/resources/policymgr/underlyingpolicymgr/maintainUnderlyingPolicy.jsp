<%--
  Description:
  JSP page for maintain underlying policy
  Author: yhchen
  Date: Jun 4, 2008


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  03/10/2017       wli         180675 - Changed the error msg to be located in parent frame for UI change.
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

<script type="text/javascript" src="<%=appPath%>/policymgr/underlyingpolicymgr/js/maintainUnderlyingPolicy.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<form name="underlyingPolicyList" action="<%=appPath%>/policymgr/underlyingpolicymgr/maintainUnderlyingPolicy.do"
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
                        <oweb:actionGroup actionItemGroupId="PM_UNDERPOL_AIG" layoutDirection="horizontal"
                                          cssColorScheme="gray"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="6" align=center>
                        <c:set var="gridDisplayFormName" value="underlyingPolicyList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="underlyingPolicyListGrid" scope="request"/>
                        <c:set var="gridDetailDivId" value="underlyingPolicyDetailDiv" scope="request"/>
                        <c:set var="datasrc" value="#underlyingPolicyListGrid1" scope="request"/>
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
                                     var="underPolFormHeader" scope="request"/>
                        <% String underPolFormHeader = (String) request.getAttribute("underPolFormHeader"); %>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="headerText" value="<%=  underPolFormHeader %>"/>
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