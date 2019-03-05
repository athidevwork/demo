<%--
  Description: Renewal Questionnaire Response.
  The policy No Criteria , risk Id and policyRenewFormId should be hidden in the popup page.

  Author: rlli
  Date: Dec 24, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  09/13/2010       wfu         111776: Replaced hardcode string with resource definition
  01/02/2013       tcheng      139862: Modified closeWindow() to pop up warning message.
  12/15/2015       jyang2      167179: Modified closeWindow() to set needToForwardToEndQuote parameter.
  10/21/2016       kxiang      180685: Modified closeWindow() to set needtoForwardToQuote to Y and
                                       disable the page when it is called to avoid duplicate click.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<c:set var="isForDivPopup" value="true"></c:set>
<c:set target="${pageBean}" property="title" value=""/>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<form name="saveEndQuoteResult" action="<%=appPath%>/transactionmgr/saveAsEndorsementQuote.do" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <input type=hidden name=workflowState value="<c:out value="${workflowState}"/>">
    <input type=hidden name=endorsementQuoteId value="<c:out value="${endorsementQuoteId}"/>">
    <tr>
        <td colspan=8>
            <oweb:message showAllMessages="true"/>
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
            <input type="button" name="exit" value="<fmt:message key='pm.common.page.button.ok'/>" onclick="javascript:closeWindow();"
                   class="buttonText">
        </td>
    </tr>

    <script type="text/javascript">
        function closeWindow() {
           showProcessingImgIndicator();
           if(eval("window.frameElement.document.parentWindow.handleExitWorkflow")){
               window.frameElement.document.parentWindow.setInputFormField("needToForwardToQuote", "Y");
               window.frameElement.document.parentWindow.handleExitWorkflow('');
           }
           window.frameElement.document.parentWindow.loadPageByViewMode("ENDQUOTE", getObjectValue('endorsementQuoteId'));
        }
    </script>
    <jsp:include page="/core/footerpopup.jsp"/>
