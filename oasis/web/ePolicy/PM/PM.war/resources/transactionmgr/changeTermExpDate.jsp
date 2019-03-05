<%--
  Description:

  Author: zlzhu
  Date: Oct 29, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  Oct 29, 2007         zlzhu      Created
  03/16/2017            eyin      changed message tag for UI change.
  11/13/2018           lzhang     194100 add buildNumber Parameter
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
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<script type="text/javascript" src="js/changeTermExpDate.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<script>
    function isNeedToRefreshParentB() {
        <%if(YesNoFlag.getInstance((String)request.getAttribute("refreshPage")).booleanValue()){
        %>
        return true;
        <%  }
        else{%>
        return false;
        <%}%>
    }
</script>

<form action="" name ="changeTermEffDatesForm">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <tr>
        <td colspan=8>
            <%
                if (pmUIStyle.equals("T")) {
            %>
            <oweb:message displayMessagesOnParent="true"/>
            <%
                }
            %>
            <%
                if (pmUIStyle.equals("B")) {
            %>
            <oweb:message/>
            <%
                }
            %>
        </td>
    </tr>

    <c:set var="policyHeaderDisplayMode" value="invisible"/>
    <tr>
        <td colspan=8 align=center>
            <%@ include file="/policymgr/policyHeader.jsp" %>
        </td>
    </tr>
    <tr>
        <td colspan="6" align=center><br/>
            <fmt:message key="pm.transactionmgr.changeTermExpirationDate.form.header" var="changeTermExpDatesHeader" scope="request"/>
            <% String changeTermExpDatesHeader = (String) request.getAttribute("changeTermExpDatesHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%=  changeTermExpDatesHeader %>" />
                <jsp:param name="divId" value="changeTermExpDatesDetailDiv" />
                <jsp:param name="isGridBased" value="false" />
            </jsp:include>
        </td>
    </tr>
    <tr>
        <td colspan="6" align=center>
            <oweb:actionGroup actionItemGroupId="PM_CHG_TERM_EXP_AIG"/>
        </td>
    </tr>

<jsp:include page="/core/footerpopup.jsp"/>