<%--
  Description:
  JSP file for select active policy
  Author: yhchen
  Date: Jun 4, 2008


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
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

<script type="text/javascript" src="<%=appPath%>/policymgr/underlyingpolicymgr/js/selectActivePolicy.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<form name="underlyingPolicyList" action="<%=appPath%>/policymgr/underlyingpolicymgr/maintainUnderlyingPolicy.do"
      method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <tr>
        <td colspan=8>
            <oweb:message/>
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
            <fmt:message key="pm.maintainunderlyingPolicy.activePolicyList.header" var="panelTitleForActivePolList"
                         scope="page"/>
            <%
                String panelTitleForActivePolList = (String) pageContext.getAttribute("panelTitleForActivePolList");
            %>
            <oweb:panel panelTitleId="panelTitleIdForActivePolList" panelContentId="panelContentIdForActivePolList"
                        panelTitle="<%=panelTitleForActivePolList%>">
                <tr>
                    <td colspan="6" align=center>
                        <c:set var="gridDisplayFormName" value="activePolicyList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="activePolicyListGrid" scope="request"/>
                        <c:set var="datasrc" value="#activePolicyListGrid1" scope="request"/>                        
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="PM_SELECT_CANCEL_AIG" layoutDirection="horizontal"/>
        </td>
    </tr>

<jsp:include page="/core/footerpopup.jsp"/>