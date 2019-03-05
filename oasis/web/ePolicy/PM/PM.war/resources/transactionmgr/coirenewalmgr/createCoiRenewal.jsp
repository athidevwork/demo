<%--
  Description:

  Author: Dzhang
  Date: Jun 17, 2010


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  07/05/2010       dzhang      Renamed this file.
  11/15/2018       lzhang      194100   Add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2010 Delphi Technology, inc. (dti)
--%>

<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ include file="/core/header.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<script type="text/javascript" src="js/createCoiRenewal.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<form action="<%=appPath%>/transactionmgr/coirenewalmgr/createCoiRenewal.do" method=post
      name="COIRenewalForm">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <%-- Show error message --%>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
    <tr>
        <td align=center>

            <fmt:message key="pm.createCoiRenewal.search.header" var="filterFormHeader" scope="request"/>
            <%
                String filterFormHeader = (String) request.getAttribute("filterFormHeader");
            %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%=  filterFormHeader %>"/>
                <jsp:param name="isGridBased" value="false"/>
            </jsp:include>
        </td>
    </tr>

    <tr>
        <td colspan="8" align="center">
            <oweb:actionGroup actionItemGroupId="PM_CRT_COI_REN_AIG"/>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>