<%--
  Description:

  Author: zlzhu
  Date: Sep 25, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  10/02/2007       sxm         Let Message tag handle policy lock message
  11/15/2018       lzhang      194100   add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ include file="/core/header.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<script type="text/javascript" src="<%=appPath%>/transactionmgr/batchrenewalprocessmgr/js/createBatchRenewalProcess.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<form action="<%=appPath%>/transactionmgr/batchrenewalprocessmgr/createBatchRenewalProcess.do" method=post name="batchRenewalProcessForm">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <%-- Show error message --%>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
    <tr>
        <td align=center>
            <tr>
                <fmt:message key="pm.createBatchRenewalProcess.header" var="renewalCriteriaHeader" scope="page"/>
                <%
                    String renewalCriteriaHeader = (String) pageContext.getAttribute("renewalCriteriaHeader");
                %>
                <td align=center>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="headerText" value="<%= renewalCriteriaHeader %>"/>
                        <jsp:param name="isGridBased" value="false" />
                    </jsp:include>
                </td>
            </tr>
        </td>
    </tr>
    <tr>
        <td colspan="8" align="center">
            <oweb:actionGroup actionItemGroupId="PM_CRT_BATCH_RENEWAL_AIG"/>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>
