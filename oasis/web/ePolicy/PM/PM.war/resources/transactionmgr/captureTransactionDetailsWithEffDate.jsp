<%@ page import="dti.pm.core.http.RequestIds"%>
<%@ page import="dti.oasis.recordset.Record"%>
<%@ page language="java" %>
<%--
  Description:

  Author: rlli
  Date: May 9, 2008


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/13/2018 lzhang 194100 add buildNumber Parameter
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
<script language="javascript" src="<%=appPath%>/transactionmgr/js/captureTransactionDetails.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<FORM action="captureTransactionDetailsWithEffDate.do" method="POST" NAME ="captureTransactionDetailsFORM">
<%@ include file="/pmcore/commonFormHeader.jsp" %>

<tr>
    <td align=left>
        <fmt:message key="pm.transactionmgr.captureTransationDetails.formHeader" var="captureDetailsHeader" scope="request"/>
        <% String captureDetailsHeader = (String) request.getAttribute("captureDetailsHeader"); %>
        <jsp:include page="/core/compiledFormFields.jsp">
            <jsp:param name="headerText" value="<%=  captureDetailsHeader %>" />
            <jsp:param name="divId" value="transactionDetailDiv" />
            <jsp:param name="isGridBased" value="false" />
        </jsp:include>
    </td>
</tr>
<tr>
    <td colspan="6" align=center>
        <oweb:actionGroup actionItemGroupId="PM_CPT_TRAN_AIG"/>
    </td>
</tr>

  <%
    // store messages for js to use
    MessageManager mm = MessageManager.getInstance();
    String messageKey = null;
 %>
 <script type="text/javascript">
     <% messageKey = "pm.transactionmgr.captureTransationDetails.noAccountingDate.error"; %>
      setMessage("<%=messageKey%>",'<%=mm.formatMessage(messageKey) %>');
 </script>

<jsp:include page="/core/footerpopup.jsp" />

