<%--
  Description:

  Author: zlzhu
  Date: Oct 29, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  Nov 09, 2007         zlzhu      Created
  11/13/2018 lzhang 194100 add buildNumber Parameter
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

<script type="text/javascript" src="js/changePolicyAdministrator.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form action="" name ="changePolicyAdminForm">
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
            <fmt:message key="pm.transactionmgr.changePolicyAdministrator.header.info" var="changePolicyAdminHeader" scope="request"/>
            <% String changePolicyAdminHeader = (String) request.getAttribute("changePolicyAdminHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%=  changePolicyAdminHeader %>" />
                <jsp:param name="divId" value="changePolicyAdminDiv" />
                <jsp:param name="isGridBased" value="false" />
            </jsp:include>
        </td>
    </tr>
    <tr>
        <td colspan="6" align=center>
            <oweb:actionGroup actionItemGroupId="PM_CHG_POLICY_ADMIN_AIG"/>
        </td>
    </tr>

<jsp:include page="/core/footerpopup.jsp"/>