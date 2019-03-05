<%@ page import="dti.oasis.util.BaseResultSet" %>
<%@ page import="dti.oasis.tags.XMLGridHeader" %>
<%@ page language="java" %>
<%--
  Description:

  Author: jdingle
  Date: 03/08/2016

  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  -----------------------------------------------------------------------------
  (C) 2016 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<c:set var="isForDivPopup" value="true"></c:set>

<%@ include file="/core/headerpopup.jsp" %>
<jsp:include page="/cicore/common.jsp"/>

<script language="javascript" src="<%=cisPath%>/credentialrequestmgr/js/maintainCredentialRequest.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="detailListDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="detailListHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="accountListDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="accountListHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>

<FORM NAME="credReqForm" action="ciCredentialRequest.do" method="POST">
  <%@ include file="/cicore/commonFormHeader.jsp" %>
    <input type="hidden" name="pk" value="<c:out value="${pk}"/>"/>
    <input type="hidden" name="entityType" value="<c:out value="${entityType}"/>"/>
    <input type="hidden" name="ciCredReqId" value="<c:out value="${ciCredReqId}"/>"/>
    <input type="hidden" name="submitRequest" value="<c:out value="${submitRequest}"/>"/>
  <c:if test="${isEntityReadOnlyYN !='Y'}">
  <tr>
    <td colspan=8>
      <oweb:message/>
    </td>
  </tr>
  </c:if>

    <tr>
      <td align=center>
        <fmt:message key="ci.credentialRequest.form.title" var="crInfo" scope="request"/>
        <% String crInfo = (String) request.getAttribute("crInfo"); %>
        <jsp:include page="/core/compiledFormFields.jsp">
          <jsp:param name="isGridBased" value="false"/>
          <jsp:param name="excludeAllLayers" value="true"/>
          <jsp:param name="divId" value="credentialRequestForm"/>
          <jsp:param name="headerText" value="<%=crInfo%>"/>
        </jsp:include>
      </td>
    </tr>
    <tr>
      <td align=center>
        <oweb:panel panelTitleId="credentialRequestDetail"
                    panelContentId="panelContentIdForCredentialRequestDetail"
                    panelTitleLayerId="CI_CREDREQ_DETAIL_GH">
    <tr>
      <td colspan="6">
        <oweb:actionGroup actionItemGroupId="CI_CREDREQDET_AIG"
                          layoutDirection="horizontal"
                          cssColorScheme="gray"/>
      </td>
    </tr>
    <tr>
      <td colspan="6" align=center>
        <c:set var="gridDisplayFormName" value="credReqForm" scope="request"/>
        <c:set var="gridDisplayGridId" value="detailListGrid" scope="request"/>
        <c:set var="gridDetailDivId" value="detailListDiv" scope="request"/>
        <c:set var="datasrc" value="#detailListGrid1" scope="request"/>
        <% BaseResultSet dataBean = detailListDataBean;
          XMLGridHeader gridHeaderBean = detailListHeaderBean; %>
        <%@ include file="/core/gridDisplay.jsp" %>
      </td>
    </tr>
    <tr>
      <td align=center>
        <c:set var="datasrc" value="#detailListGrid1" scope="request"/>
        <jsp:include page="/core/compiledFormFields.jsp">
          <jsp:param name="gridID" value="detailListGrid"/>
          <jsp:param name="divId" value="detailListDiv"/>
          <jsp:param name="isGridBased" value="true"/>
          <jsp:param name="hasPanelTitle" value="false"/>
          <jsp:param name="excludePageFields" value="true"/>
          <jsp:param name="isLayerVisibleByDefault" value="true"/>
          <jsp:param name="includeLayerIds" value="CI_CREDREQ_DETAIL"/>
        </jsp:include>
      </td>
    </tr>
    </oweb:panel>
  </td>
  </tr>

    <tr>
      <td align=center>
        <oweb:panel panelTitleId="panelTitleIdForAccount"
                    panelContentId="panelContentIdForAccount"
                    panelTitleLayerId="CI_CREDREQ_ACCOUNT_GH">
    <tr>
      <td colspan="6">
        <oweb:actionGroup actionItemGroupId="CI_CREDREQACCT_AIG"
                          layoutDirection="horizontal"
                          cssColorScheme="gray"/>
      </td>
    </tr>
    <tr>
      <td colspan="6" align=center>
        <c:set var="gridDisplayFormName" value="credReqForm" scope="request"/>
        <c:set var="gridDisplayGridId" value="accountListGrid" scope="request"/>
        <c:set var="gridDetailDivId" value="accountListDiv" scope="request"/>
        <c:set var="datasrc" value="#accountListGrid1" scope="request"/>
        <c:set var="cacheResultSet" value="false"/>
        <c:set var="selectable" value="false" scope="request"/>
        <% BaseResultSet dataBean = accountListDataBean;
          XMLGridHeader gridHeaderBean = accountListHeaderBean; %>
        <%@ include file="/core/gridDisplay.jsp" %>
      </td>
    </tr>
    </oweb:panel>
    </td>
    </tr>
  </td>
  </tr>
  <tr>
    <td align=center>
      <oweb:actionGroup actionItemGroupId="CI_CREDREQ_AIG" layoutDirection="horizontal"/>
    </td>
  </tr>

  <jsp:include page="/core/footerpopup.jsp"/>
