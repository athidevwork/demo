<%--
  Description: View Claims Summary page for policy action Claims Summary

  Author: wfu
  Date: Jan 20, 2011


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2011 Delphi Technology, inc. (dti)
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<c:set var="isForDivPopup" value="true"></c:set>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<script type="text/javascript" src="js/viewClaimsSummary.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form name="claimList" action="viewClaimsSummary.do" method=post>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
    <tr>
        <td>
            <fmt:message key="pm.viewClaimsSummary.header.claims" var="panelTitleForClaims" scope="page"/>
            <%
                String strClaimsGridTitle = (String) pageContext.getAttribute("panelTitleForClaims");
            %>
            <oweb:panel panelContentId="panelContentForClaims" panelTitleId="panelTitleIdForClaims"
                        panelTitle="<%=strClaimsGridTitle%>">
                <tr>
                    <td align=center>
                        <c:set var="gridDisplayFormName" value="claimsList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="claimsListGrid" scope="request"/>
                        <c:set var="gridDetailDivId" value="claimsListGridDiv" scope="request" />
                        <c:set var="gridSizeFieldIdPrefix" value="claims_"/>
                        <%@include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
                <tr>
                    <td align=center>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="hasPanelTitle" value="false"/>
                            <jsp:param name="isTogglableTitle" value="false"/>
                        </jsp:include>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td colspan="6" align=center>
            <oweb:actionGroup actionItemGroupId="PM_VIEW_CLAIM_AIG"/>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>