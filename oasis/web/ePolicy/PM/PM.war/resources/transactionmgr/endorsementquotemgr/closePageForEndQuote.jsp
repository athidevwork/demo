<%--
  Description:

  Author: rlli
  Date: Mar 12, 2008

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

<script>

    function performClosePage() {
        getParentWindow().loadPageByViewMode("ENDQUOTE", <%=request.getAttribute("endorsementQuoteId")%>);
        }
</script>

<%
    if (MessageManager.getInstance().hasErrorMessages()) {

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
    getObject("pageTitleForpageHeader").innerText = "<fmt:message key='pm.pmcore.closePage.error'/>";
</script>