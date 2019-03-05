<%--
  Description:

  Author: rlli
  Date: Dec 25, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
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
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<form name="mailingEventProcessing" action="<%=appPath%>/policymgr/mailingmgr/maintainPolicyMailing.do" method=post>

    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <input type=hidden name=policyMailingId value="<c:out value="${policyMailingId}"/>">
    <input type=hidden name=selectedMailingDtls value="<c:out value="${selectedMailingDtls}"/>">
    <tr>
        <td colspan=1>
            <img src="<%=corePath%>/images/running.gif?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" alt="saving"></img>
        </td>
        <td colspan=7>
            <oweb:message/>
        </td>
    </tr>
    <script type="text/javascript">
        function handleOnLoad() {
            submitFirstForm();
        }
    </script>
    <jsp:include page="/core/footerpopup.jsp"/>
