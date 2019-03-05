<%--
  Description:

  Author: Jshen
  Date: May 29, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  07/03/3007       sxm         Removed inclusion of handleConfirmation.jsp since page submittion
                               is handled by postAjaxSubmit now
  11/13/2018       lzhang      194100 add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2007 Delphi Technology, inc. (dti)
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

<script type="text/javascript" src="js/changeTermEffDates.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form action="" name ="changeTermEffDatesForm">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <tr>
        <td colspan=8>
            <oweb:message/>
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
            <fmt:message key="pm.transactionmgr.changeTermDates.form.header" var="changeTermEffDatesHeader" scope="request"/>
            <% String changeTermEffDatesHeader = (String) request.getAttribute("changeTermEffDatesHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%=  changeTermEffDatesHeader %>" />
                <jsp:param name="divId" value="changeTermEffDatesDetailDiv" />
                <jsp:param name="isGridBased" value="false" />
            </jsp:include>
        </td>
    </tr>
    <tr>
        <td colspan="6" align=center>
            <oweb:actionGroup actionItemGroupId="PM_CHG_EFFDATE_AIG"/>
        </td>
    </tr>

<jsp:include page="/core/footerpopup.jsp"/>