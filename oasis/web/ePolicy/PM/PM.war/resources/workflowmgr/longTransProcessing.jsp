<%--
  Description: Page to display status for long running transactions processing.

  Author: Florin Bibire
  Date:   03/06/2008


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  09/01/2010       dzhang      111441 - Added jsp:useBean id fieldsMap.
  01/05/2014       wdang       158738 - Removed term information from parameters for purge transaction, 
                                        because it will be wiped out after saving official.
  11/15/2018       lzhang      194100   add buildNumber Parameter
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

<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<form name="longTransProcessing" action="<%=appPath%>/workflowmgr/struts/monitorLongRunningTransaction.do" method=post>
    <c:set var="process" value="continueMonitoring"></c:set>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <input type="hidden" name="longTransProcessingMsgKey"
       value="<c:out value="${longTransProcessingMsgKey}"/>">
    <input type=hidden name=workflowState value="<c:out value="${workflowState}"/>">
    <c:set var="policyHeaderDisplayMode" value="invisible"/>
    <tr>
        <td colspan=8 align=center>
            <%@ include file="/policymgr/policyHeader.jsp" %>
        </td>
    </tr>

    <tr>
        <td colspan=1>
            <img src="<%=corePath%>/images/running.gif?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" alt="saving"></img>
        </td>
        <td colspan=7>
            <oweb:message/>
        </td>
    </tr>

<%
    String refreshRateVal = ApplicationContext.getInstance().getProperty("long.running.transaction.monitor.refresh.rate"); // in seconds
    int refreshRate = 1000 * Integer.parseInt(refreshRateVal);
%>
    <script type="text/javascript">
        function handleOnLoad() {
            if (policyHeader.lastTransactionInfo.transactionCode == "PURGE") {
                setInputFormField("policyTermHistoryId", "");
                setInputFormField("termBaseRecordId", "");
            }
            setTimeout('submitFirstForm()', <%=refreshRate%>);
        }
    </script>
<jsp:include page="/core/footerpopup.jsp"/>
