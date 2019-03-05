
<%--
  Description:

  Author: xnie
  Date: Dec 25, 2013


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  12/25/2013       xnie        148083 - Initial version
  03/18/2014       xnie        152969 - Added hidden field newRiskB.
  10/13/2015       tzeng       164679 - Add riskRelationMessageType input label to transfer value to display message
                                        after rating or save official at the first time.
  01/28/2016       wdang       169024 - Reverted changes of 164679.
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2013 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<c:set var="isForDivPopup" value="true"></c:set>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<script type="text/javascript" src="js/maintainRiskDetail.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<form action="maintainRiskDetail.do" method=post name="riskForm">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <fmt:message key="pm.common.policy.actions" var="policyActions" scope="request"/>
    <c:set var="dropdownSelectFromDesc" value="${policyActions}"></c:set>

    <input type="hidden" name="openWhichWindow" value="">
    <input type="hidden" name="policyScreenMode" value="<%= policyHeader.getScreenModeCode() %>">
    <input type="hidden" name="policyStatus" value="<c:out value="${policyHeader.policyStatus}"/>"/>
    <input type="hidden" name="termBaseRecordId" value="<c:out value="${policyHeader.termBaseRecordId}"/>"/>
    <input type="hidden" name="isReinstateIbnrRiskValid" value="<c:out value="${isReinstateIbnrRiskValid}"/>"/>
    <input type="hidden" name="policyExpirationDate" value="<c:out value="${policyHeader.policyExpirationDate}"/>"/>
    <input type="hidden" name="policyExpirationDate" value="<c:out value="${policyHeader.policyExpirationDate}"/>"/>
    <input type="hidden" name="UPDATE_IND" value="<%=request.getAttribute("UPDATE_IND")%>">
    <input type="hidden" name="EDIT_IND" value="<%=request.getAttribute("EDIT_IND")%>">
    <input type="hidden" name="origRiskEffectiveToDate" value="<%=request.getAttribute("origRiskEffectiveToDate")%>">
    <input type="hidden" name="origRollingIbnrB" value="<%=request.getAttribute("origRollingIbnrB")%>">
    <input type="hidden" name="saveCloseB" value="<%=request.getAttribute("saveCloseB")%>">
    <input type="hidden" name="savedB" value="<%=request.getAttribute("savedB")%>">
    <input type="hidden" name="slotOccupantB" value="<%=request.getAttribute("slotOccupantB")%>">
    <input type="hidden" name="newRiskB" value="<%=request.getAttribute("newRiskB")%>">
    <%-- Show error message --%>
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
            <fmt:message key="pm.maintainRiskDetail.riskForm.header" var="riskFormHeader" scope="request"/>
            <% String riskFormHeader = (String) request.getAttribute("riskFormHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%=  riskFormHeader %>"/>
                <jsp:param name="divId" value="riskDetailDiv"/>
                <jsp:param name="isGridBased" value="false" />
            </jsp:include>
        </td>
    </tr>
    <tr>
        <td colspan="6" align=center>
            <oweb:actionGroup actionItemGroupId="PM_RISK_DETAIL_AIG"/>
        </td>
    </tr>

    <tr>
        <td>&nbsp;</td>
    </tr>

    <jsp:include page="/core/footerpopup.jsp"/>
