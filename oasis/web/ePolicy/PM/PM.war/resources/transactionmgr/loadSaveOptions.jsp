<%@ page import="dti.pm.core.http.RequestIds"%>
<%@ page import="dti.oasis.recordset.Record"%>
<%@ page language="java" %>
<%--
  Description:

  Author: gjlong
  Date: Feb 20, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/15/2018       lzhang      194100   Add buildNumber Parameter
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

<script language="javascript" src="<%=appPath%>/transactionmgr/js/loadSaveOptions.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<FORM action="loadSaveOptions.do" method="POST" NAME ="loadSaveOptionsForm">
<%@ include file="/pmcore/commonFormHeader.jsp" %>

<tr>
   <td align=left>
       <fmt:message key="pm.transactionmgr.loadSaveOptions.formHeader" var="saveOptionsHeader" scope="request"/>
       <% String saveOptionsHeader = (String) request.getAttribute("saveOptionsHeader"); %>
       <jsp:include page="/core/compiledFormFields.jsp">
           <jsp:param name="headerText" value="<%=  saveOptionsHeader %>" />
           <jsp:param name="divId" value="saveOptionsDetailDiv" />
           <jsp:param name="isGridBased" value="false" />
       </jsp:include>
    </td>
</tr>

<tr>
    <td colspan="7">
    <oweb:message/>
    </td>
</tr>

<tr>
    <td align=center>
        <oweb:actionGroup actionItemGroupId="PM_SAVE_OPTIONS_AIG" layoutDirection="horizontal"/>
    </td>
</tr>
<jsp:include page="/core/footerpopup.jsp" />


