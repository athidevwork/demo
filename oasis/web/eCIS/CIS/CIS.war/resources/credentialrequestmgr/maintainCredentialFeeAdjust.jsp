<%@ page language="java" %>
<%--
  Description:

  Author: jdingle
  Date: 03/08/2016

  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  10/15/2018       dpang       195835: Grid replacement
  -----------------------------------------------------------------------------
  (C) 2016 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<%@ include file="/core/headerpopup.jsp" %>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>

<jsp:include page="/cicore/common.jsp"/>
<script language="javascript" src="<%=cisPath%>/credentialrequestmgr/js/maintainCredentialFeeAdjust.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form action="ciCredentialFeeAdjust.do" method="POST" name="CredentialFeeAdjustForm">
  <%@ include file="/cicore/commonFormHeader.jsp" %>
    <tr>
      <td colspan=8>
        <oweb:message/>
      </td>
    </tr>
  <input type="hidden" name="billingAccountId" value="<c:out value="${billingAccountId}"/>"/>
  <input type="hidden" name="feeProcessed" value="<c:out value="${feeProcessed}"/>"/>
  <tr>
    <td>
      <oweb:panel panelContentId="panelContentForHeader" hasTitle="false">
  <tr>
    <td>
      <c:set var="gridDisplayFormName" value="CredentialFeeAdjustForm" scope="request"/>
      <c:set var="gridDisplayGridId" value="testGrid" scope="request"/>
      <c:set var="datasrc" value="#testGrid1" scope="request"/>
      <c:set var="cacheResultSet" value="false"/>
      <c:set var="selectable" value="false" scope="request"/>
      <%@ include file="/core/gridDisplay.jsp" %>
    </td>
  </tr>
  </oweb:panel>
  </td>
  </tr>
  <tr>
    <td align=center>
      <oweb:actionGroup actionItemGroupId="CI_CREDFEE_AIG" layoutDirection="horizontal"/>
    </td>
  </tr>

<jsp:include page="/core/footerpopup.jsp"/>