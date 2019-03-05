<%--
  Description: COI Claims History Page

  Author: Joe Shen
  Date: Aug 1, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
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

<script type="text/javascript" src="js/coiClaims.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form action="processCoiClaim.do" name="processCoiForm" method="post">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>

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
          <oweb:actionGroup actionItemGroupId="PM_COI_CM_AIG" layoutDirection="horizontal" />
      </td>
  </tr>
<jsp:include page="/core/footerpopup.jsp"/>