<%--
  Description: Renewal Questionnaire Response.
  The policy No Criteria , risk Id and policyRenewFormId should be hidden in the popup page.

  Author: rlli
  Date: Dec 24, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  09/13/2010       wfu         111776: Replaced hardcode string with resource definition
  02/25/2014       jyang2      145733: Updated closeWindow() to reload the popup window
                                       instead of refreshPage.
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
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<form name="result" action="" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <tr>
        <td colspan=8>
            <oweb:message showAllMessages="true"/>
        </td>
    </tr>

    <tr>
        <td colspan=8 align=center>
            <input type="button" name="Ok" value="<fmt:message key='pm.common.page.button.ok'/>" onclick="javascript:closeWindow();" class="buttonText">
        </td>
    </tr>
    <script type="text/javascript">
        function closeWindow() {
            window.frameElement.document.parentWindow.reloadWindowLocation();
        }
    </script>       
<jsp:include page="/core/footerpopup.jsp"/>
