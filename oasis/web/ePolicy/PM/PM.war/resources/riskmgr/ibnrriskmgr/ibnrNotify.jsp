<%--
  Description: Default IBNR Details Notify page

  Author: jshen
  Date: Apr 7, 2011


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>

<c:set var="isForDivPopup" value="true"></c:set>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<script type="text/javascript" src="<%=appPath%>/riskmgr/ibnrriskmgr/js/ibnrNotify.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<form action="<%=appPath%>/riskmgr/ibnrriskmgr/ibnrNotify.do" method="POST" name="ibnrNotify">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <input type=hidden name=workflowState value="<c:out value="${workflowState}"/>">
    <input type=hidden name=associatedRiskCount value="<c:out value="${associatedRiskCount}"/>">
    <input type=hidden name=toAssociatedRiskId value="<c:out value="${toAssociatedRiskId}"/>">
        
    <tr>
        <td>
            <table cellpadding=0 cellspacing=0 width=100%>
                <tr>
                    <td><oweb:message hideConfirmation="false"/></td>
                </tr>
            </table>
        </td>
    </tr>
    <c:set var="policyHeaderDisplayMode" value="invisible"/>
    <tr>
        <td colspan=8 align=center>
            <%@ include file="/policymgr/policyHeader.jsp" %>
        </td>
    </tr>
    <tr>
        <td colspan="6" align=center>
            <oweb:actionGroup actionItemGroupId="PM_NOTIFY_AIG"/>
        </td>
    </tr>

    <jsp:include page="/core/footerpopup.jsp" />