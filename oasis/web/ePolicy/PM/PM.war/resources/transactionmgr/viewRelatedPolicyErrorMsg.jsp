<%--
  Description: View Related Policy Errors page

  Author: rlli
  Date: Sep 13, 2007

  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/15/2018       lzhang      194100   Add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>
<c:set var="isForDivPopup" value="true"></c:set>
<c:set target="${pageBean}" property="title" value=""/>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<script type="text/javascript" src="<%=appPath%>/transactionmgr/js/viewRelatedPolicyErrorMsg.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form action="<%=appPath%>/transactionmgr/viewRelatedPolicy.do" method="POST" name="viewRelatedPolicy">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <input type=hidden name=workflowState value="<c:out value="${workflowState}"/>">
    <input type=hidden name=confirmed value="<c:out value="${confirmed}"/>">
    <tr>
        <td>
            <table cellpadding=0 cellspacing=0 width=100%>
                <tr>
                    <td>
                        <oweb:message showAllMessages="true"/>
                    </td>
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
        <td colspan=8 align=center>
            <input type="button" name="Close" value="Close" onclick="javascript:commonOnButtonClick('CLOSE')"
                   class="buttonText">

        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>