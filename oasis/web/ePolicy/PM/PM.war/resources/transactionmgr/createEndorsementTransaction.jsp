<%--
  Description:

  Author: gjlong
  Date: Mar 26, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/13/2018       lzhang 194100 add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:set var="isForDivPopup" value="true"></c:set>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<script language="javascript" src="<%=appPath%>/transactionmgr/js/createEndorsementTransaction.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<FORM action="endorseTransaction.do" method="POST" NAME="endorseTransactionForm">
    <input type="hidden" name="isForCaptureTransactionDetail" value="<%=request.getAttribute("isForCaptureTransactionDetail")%>">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <tr>
        <td>&nbsp;</td>
    </tr>
    <tr>
        <td>
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
        <td align=left>
            <fmt:message key="pm.transactionmgr.createEndorsementTransaction.formHeader" var="endorseTransactionHeader"
                         scope="request"/>
            <% String endorseTransactionHeader = (String) request.getAttribute("endorseTransactionHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%=  endorseTransactionHeader %>"/>
                <jsp:param name="divId" value="transactionDetailDiv"/>
                <jsp:param name="isGridBased" value="false"/>
            </jsp:include>
        </td>
    </tr>
    <tr>
        <td colspan="6" align=center>
            <oweb:actionGroup actionItemGroupId="PM_ENDORSE_AIG"/>
        </td>
    </tr>

    <jsp:include page="/core/footerpopup.jsp"/>

