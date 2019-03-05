<%@ page import="dti.pm.core.http.RequestIds"%>
<%@ page import="dti.oasis.recordset.Record"%>
<%@ page language="java" %>
<%--
  Description:

  Author: xnie
  Date: September 27, 2012


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  09/27/2012       xnie        133766 - Initial version.
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>


<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:set var="isForDivPopup" value="true"></c:set>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<script language="javascript" src="<%=appPath%>/policymgr/js/loadReRateOptions.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<FORM action="loadReRateOptions.do" method="POST" NAME ="loadReRateOptionsForm">
<%@ include file="/pmcore/commonFormHeader.jsp" %>

<tr>
    <td colspan="7">
        <oweb:message/>
    </td>
</tr>

<tr>
   <td align=left>
       <fmt:message key="pm.reRatePolicy.reRate.loadReRateOptions.formHeader" var="reRateOptionsHeader" scope="request"/>
       <% String reRateOptionsHeader = (String) request.getAttribute("reRateOptionsHeader"); %>
       <jsp:include page="/core/compiledFormFields.jsp">
           <jsp:param name="headerText" value="<%=  reRateOptionsHeader %>" />
           <jsp:param name="divId" value="reRateOptionsDetailDiv" />
           <jsp:param name="isGridBased" value="false" />
       </jsp:include>
    </td>
</tr>

<tr>
    <td align=center>
        <oweb:actionGroup actionItemGroupId="PM_RERATE_OPTIONS_AIG" layoutDirection="horizontal"/>
    </td>
</tr>
<jsp:include page="/core/footerpopup.jsp" />


