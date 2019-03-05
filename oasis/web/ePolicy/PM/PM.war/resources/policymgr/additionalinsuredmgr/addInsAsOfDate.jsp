<%--
  Description: Additional Insured As of Date Page

  Author: xnie
  Date: Feb 28, 2013


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  02/28/2013       xnie        138026 - Initial version.
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2013 Delphi Technology, inc. (dti)
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

<script type="text/javascript" src="js/addInsAsOfDate.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form action="maintainAddInsAsOfDate.do" name="addInsAsOfDateForm" method="post">
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
    <tr><td>&nbsp;</td></tr>
    <%-- Display form --%>
    <tr>
        <td align=center>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="isGridBased" value="false" />
            </jsp:include>
        </td>
    </tr>
    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="PM_ADDINS_ASDT_AIG" layoutDirection="horizontal"/>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>