<%--
  Description:

  Author: Bhong
  Date: Dec 01, 2009


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  09/10/2010       wfu         111776: Replaced hardcode string with resource definition
  03/13/2017       eyin        180675: 1. Changed the error msg to be located in parent frame for UI change.
                                       2. Modified the logic of displaying page for UI change.
  11/13/2018       tyang       194100 - Add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2009 Delphi Technology, inc. (dti)
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<c:set var="isForDivPopup" value="true"></c:set>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<%@ include file="/pmcore/handleConfirmations.jsp" %>

<script type="text/javascript" src="js/maintainExcessPremium.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<form name="excessPremiumForm" action="maintainExcessPremium.do" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <input type="hidden" name="showSummary" value="<c:out value="${showSummary}"/>"/>
    <input type="hidden" name="fromCoverage" value="<c:out value="${param.fromCoverage}"/>"/>
    <input type="hidden" name="productCoverageCode" value="<c:out value="${param.productCoverageCode}"/>"/>
    <input type="hidden" name="coverageEffectiveFromDate"
           value="<c:out value="${param.coverageEffectiveFromDate}"/>"/>
    <input type="hidden" name="practiceStateCode" value="<c:out value="${param.practiceStateCode}"/>"/>
    <input type="hidden" name="coverageLimitCode" value="<c:out value="${param.coverageLimitCode}"/>"/>
    <input type="hidden" name="transactionLogId" value="<c:out value="${param.transactionLogId}"/>"/>
    <tr>
        <td colspan=8>
            <%
                if(pmUIStyle.equals("T")) {
            %>
            <oweb:message displayMessagesOnParent="true"/>
            <%
                }
            %>
            <%
                if(pmUIStyle.equals("B")) {
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
        <%
            if(pmUIStyle.equals("T")) {
        %>
        <td>
        <%
            }
        %>
        <%
            if(pmUIStyle.equals("B")) {
        %>
        <td align=center>
        <%
            }
        %>
            <oweb:actionGroup actionItemGroupId="PM_MXS_PREM_AIG" layoutDirection="horizontal"/>
        </td>
    </tr>        

    <tr>
        <td align=center>
            <fmt:message key="pm.maintainExcessPremium.form.header" var="excessPremium" scope="request"/>
            <%  String excessPremium = (String) request.getAttribute("excessPremium"); %>
            <oweb:panel panelTitleId="panelTitleIdForExcessPremium"
                        panelContentId="panelContentIdForExcessPremium"
                        panelTitle="<%= excessPremium %>" >
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="excessPremiumForm" scope="request" />
                    <c:set var="gridDisplayGridId" value="excessPremiumGrid" scope="request" />
                    <c:set var="gridDetailDivId" value="excessPremiumDetailDiv" scope="request" />
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="PM_MXS_PREM_AIG" layoutDirection="horizontal"/>
        </td>
    </tr>

<jsp:include page="/core/footerpopup.jsp"/>
