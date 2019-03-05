<%@ page import="org.apache.struts.Globals"%>
<%@ page import="org.apache.struts.taglib.html.Constants"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="dti.oasis.messagemgr.MessageManager" %>
<%@ page import="dti.oasis.messagemgr.Message" %>
<!--%@ page language="java" %-->  <!-- it is declared in policyHeader.jsp-->
<%--
  Description:

  Author: gjlong
  Date: Mar 5, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/13/2018       tyang 194100 Add buildNumber Parameter
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

<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<script type="text/javascript" src="js/maintainBilling.js
?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form name="maitainBillingForm" action="maintainBilling.do" method="post">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>    
    </tr>

    <c:set var="policyHeaderDisplayMode" value="hide"/>
    <tr>
        <td colspan=8 align=center>
            <%@ include file="/policymgr/policyHeader.jsp" %>
        </td>
    </tr>

    <tr>
        <td align=center>
            <fmt:message key="pm.maintainBilling.manageBillingSetupForm.header" var="BillsetupFormHeader" scope="request"/>
            <% String BillsetupFormHeader = (String) request.getAttribute("BillsetupFormHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="divId" value="billingSetupDetailDiv" />
                <jsp:param name="headerText" value="<%=  BillsetupFormHeader %>" />
                <jsp:param name="isGridBased" value="false" />
            </jsp:include>
        </td>
    </tr>
  <tr>
      <td align=center>
          <oweb:actionGroup actionItemGroupId="PM_BILLNG_AIG" layoutDirection="horizontal"/>
      </td>      
  </tr>  

<%
    // Initialize Sys Parms for JavaScript to use
    String fmBsEnterAcct  = SysParmProvider.getInstance().getSysParm("FM_BS_ENTER_ACCT", "N");

    MessageManager mm = MessageManager.getInstance();
    String messageKey = null;
%>
  <script type="text/javascript">
        <% messageKey = "pm.maintainBilling.validate.alert.billingRelationExists"; %>
        setMessage("<%=messageKey%>",'<%=mm.formatMessage(messageKey)%>');

        <% messageKey = "pm.maintainBilling.validate.error.noBillingFrequency"; %>
        setMessage("<%=messageKey%>",'<%=mm.formatMessage(messageKey)%>');

        <% messageKey = "pm.maintainBilling.alert.enableSearchByAcctHolder"; %>
        setMessage("<%=messageKey%>","<%=mm.formatMessage(messageKey)%>");

        <% messageKey = "pm.maintainBilling.validation.parameterNotConfiguredFMBSENTERACCT";%>
        setMessage("<%=messageKey%>",'<%=mm.formatMessage(messageKey)%>');

        <% messageKey = "pm.maintainBilling.validate.error.noAccountNoEntered" ;%>
        setMessage("<%=messageKey%>",'<%=mm.formatMessage(messageKey)%>');

        <% messageKey = "pm.maintainBilling.validate.error.noBillLeadDays" ;%>
        setMessage("<%=messageKey%>",'<%=mm.formatMessage(messageKey)%>');

         <% messageKey = "pm.maintainBilling.validate.error.noBaseBillMonthDay" ;%>
        setMessage("<%=messageKey%>",'<%=mm.formatMessage(messageKey)%>');

        <% messageKey = "pm.maintainBilling.validate.error.noBaseMMDDNoLeadDay" ;%>
        setMessage("<%=messageKey%>",'<%=mm.formatMessage(messageKey)%>');

        <% messageKey = "pm.maintainBilling.confirm.checkSinglePolicyB" ;%>
        setMessage("<%=messageKey%>",'<%=mm.formatMessage(messageKey)%>');

        <% messageKey = "pm.maintainBilling.label.buttonMoreLess.less"; %>
        setMessage("<%=messageKey%>",'<%=mm.formatMessage(messageKey)%>');

        <% messageKey = "pm.maintainBilling.label.buttonMoreLess.more"; %>
        setMessage("<%=messageKey%>",'<%=mm.formatMessage(messageKey)%>');
 </script>


<script type="text/javascript">
     setSysParmValue("FM_BS_ENTER_ACCT", '<%=fmBsEnterAcct %>');
</script>

<%
    // Initialize Sys Parms for JavaScript to use
    String showMultiAccount = SysParmProvider.getInstance().getSysParm("FM_SHO_ENT_MULTI_ACT", "N");
%>

<script type="text/javascript">
     setSysParmValue("showMultiAccount",'<%=showMultiAccount %>');
</script>

<script language="javascript">
    // System defaults to show "More >>>" and set showMoreFlag to 'N'
    getObject("PM_BILLNG_MORE").value = getMessage("pm.maintainBilling.label.buttonMoreLess.more");
    window.document.forms[0].showMoreFlag.value = 'N';
    if (eval("window.processDeps")) {
       processDeps();
    }
</script>

<!-- take care of the alert messages raised when getting init values -->
<script language="javascript">
 function handleConfirmations() {
    var confirmed = false;
    <%
      Iterator it = mm.getConfirmationPrompts();
      while (it.hasNext()) {
          Message message = (Message) it.next();
    %>
      if (! confirmed) {
          if ( confirmed = confirm("<%=message.getMessage()%>")) {;
               commonOnButtonClick('CLOSE_DIV');
          }
      }
    <%
      }
    %>
 }
</script>

<jsp:include page="/core/footerpopup.jsp"/>

