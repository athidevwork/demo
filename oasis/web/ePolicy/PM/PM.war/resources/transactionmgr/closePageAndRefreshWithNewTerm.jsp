<%@ page import="dti.oasis.messagemgr.MessageManager" %>
<%--
  Description:

  Author: Dzhang
  Date: Oct 12, 2010

  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  -----------------------------------------------------------------------------
  (C) 2010 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<c:set var="isForDivPopup" value="true"></c:set>
<c:set target="${pageBean}" property="title" value=""/>
<%@ include file="/core/headerpopup.jsp" %>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<form name="workflowExitAndRefreshWithNewTerm" action="" method=post>
    <input type="hidden" name="<%=Constants.TOKEN_KEY%>"
           value="<%=request.getSession().getAttribute(Globals.TRANSACTION_TOKEN_KEY)%>">
    <tr>
        <td colspan=8>
            <oweb:message showAllMessages="true"/>
        </td>
    </tr>

    <tr>
        <td colspan=8 align=center>
            <input type="button" name="workflowExit_Ok" value="OK" onclick="javascript:closeWindow();"
                   class="buttonText">
        </td>
    </tr>

    <script type="text/javascript">
        function closeWindow() {
            window.frameElement.document.parentWindow.refreshWithNewPolicyTermHistory(true);
        }
    </script>
    <jsp:include page="/core/footerpopup.jsp"/>



