<%@ page import="dti.oasis.busobjs.YesNoFlag" %>
<%--
  Description:

  Author: yhchen
  Date: Jul 17, 2007

  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  09/13/2010       wfu         111776: Replaced hardcode string with resource definition
  11/02/2018       clm         195889 -  Grid replacement using getParentWindow
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%-- Show error message --%>

<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<%
    // Setup the REQUEST_URI request attribute with the request uri of this invocation so that JSPs and Tags can use it.
    // Otherwise, when a JSP/Tag calls request.getAttribute(RequestIds.REQUEST_URI),
    // it returns the URI of the initial request causing  the forward to the closePage.jsp page.
    request.setAttribute(RequestIds.REQUEST_URI, request.getRequestURI());
%>
<script>

    function performClosePage() {
        var parentWindow = getParentWindow();
    <%if(YesNoFlag.getInstance((String)request.getAttribute("refreshPage")).booleanValue()){
    %>
        parentWindow.refreshPage();
    <%  }
    else{%>
        var divPopup = parentWindow.getDivPopupFromDivPopupControl(this.frameElement);
        parentWindow.closeDiv(divPopup);
    <%}%>
    }
</script>

<%
    if (MessageManager.getInstance().hasMessages()) {

%>

<%-- Show error message --%>
<tr>
    <td colspan=8>
        <oweb:message showAllMessages="true"/>
    </td>
</tr>
<tr>
    <td colspan=8 align=center>
        &nbsp;
    </td>
</tr>
<tr>
    <td colspan=8 align=center>
        <div id="closebtn">
            <input type=button class="buttonText" value="<fmt:message key='pm.common.page.button.ok'/>"
                   onClick="javascript:performClosePage();">
        </div>
    </td>
</tr>


<%
}
else {
%>
<script>
    performClosePage();
</script>
<%}%>

<jsp:include page="/core/footerpopup.jsp"/>
<script type="text/javascript">
    <%
    if (MessageManager.getInstance().hasErrorMessages()) {
    %>
      getObject("pageTitleForpageHeader").innerText = "<fmt:message key='pm.pmcore.closePage.error'/>";
    <%}else{%>
      getObject("pageTitleForpageHeader").innerText = "<fmt:message key='pm.pmcore.closePage.information'/>";
    <%}%>
</script>