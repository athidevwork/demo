<%@ page import="dti.oasis.util.StringUtils" %>
<%@ page import="dti.oasis.messagemgr.MessageManager" %>
<%@ page import="dti.ci.core.CIFields" %>
<%@ page language="java"%>
<%--
  Description: Maintain  Discount Points Hist

  Author: Michael
  Date: 02/21/2011


  Revision Date    Revised By  Description
  ---------------------------------------------------
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  10/16/2018       ylu         195835: Grid Replacement
  ---------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<%
String entityNameDisplay = (String) request.getAttribute(CIFields.ENTITY_NAME_PROPERTY);
if (StringUtils.isBlank(entityNameDisplay)) {
  entityNameDisplay = MessageManager.getInstance().formatMessage("ci.entity.classifications.form.title");
}
else {
  entityNameDisplay = MessageManager.getInstance().formatMessage("ci.entity.search.label.HistoricalData") + " " + entityNameDisplay;
}

String message = (String) request.getAttribute(ICIConstants.MSG_PROPERTY);
if (StringUtils.isBlank(message) || message.equalsIgnoreCase("null")) {
  message = "";
}
%>

<c:set var="globalActionItemGroupId" value="CI_FOLDER_AG"></c:set>

<%@include file="/core/header.jsp" %>
<jsp:include page="/CI_EntitySelect.jsp"/>
<jsp:include page="/cicore/common.jsp"/>
<c:set var="tabMenuGroupId" value="${tabGroupId}"></c:set>
<%@ include file="/core/tabheader.jsp" %>

<script language="javascript" src="<%=cisPath%>/entityhistoricaldatamgr/js/maintainEntityHistoricalData.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form name="frmGrid" action="ciEntityHistoricalData.do" method="POST">
  <%@ include file="/cicore/commonFormHeader.jsp" %>
  <jsp:include page="/cicore/ciFolderCommon.jsp" />
  
      <tr valign="top">
          <td colspan="6" class="tabTitle">
              <b><%=entityNameDisplay%></b>
          </td>
      </tr>

    <tr>
        <td>
            <oweb:message/>
        </td>
    </tr>
      <tr>

        <td colspan="6">
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="divId" value="EntityHistoricalDataPara"/>
                <jsp:param name="headerText" value="Entity Historical Data Para"/>
                <jsp:param name="excludeAllLayers" value="true"/>
                <jsp:param name="displayAsPanel" value="false"/>
            </jsp:include>
        </td>
    </tr>
      <tr>
          <td width="100%">
              <jsp:include page="/core/compiledFormFields.jsp">
                  <jsp:param name="isGridBased" value="false"/>
                  <jsp:param name="includeLayerIds" value="PAGE_SEARCH_LAYER"/>
                  <jsp:param name="headerTextLayerId" value="PAGE_SEARCH_LAYER"/>
                  <jsp:param name="removeFieldPrefix" value="true"/>
                  <jsp:param name="isLayerVisibleByDefault" value="true"/>
                  <jsp:param name="divId" value="FilterCriteria"/>
                  <jsp:param name="excludePageFields" value="true"/>
                  <jsp:param name="actionItemGroupId" value="CI_ENT_HIST_DATA_AIG"/>
              </jsp:include>
          </td>
      </tr>
      <tr>
          <td>
              <oweb:panel panelContentId="panelContentForEntityHistoricalData"
                          panelTitleId="panelTitleIdForEntityHistoricalData"
                          panelTitleLayerId="PAGE_GRID_LAYER">

              <tr>
                  <td>
                      <c:set var="gridDisplayFormName" value="frmGrid" scope="request"/>
                      <c:set var="gridDisplayGridId" value="ciEntityHistoricalDataGrid" scope="request"/>
                      <c:set var="gridDetailDivId" value="ciEntityHistoricalDataDetailDiv" scope="request"/>
                      <c:set var="datasrc" value="#ciEntityHistoricalDataGrid1" scope="request"/>
                      <c:set var="selectable" value="false" scope="request"/>
                      <%@ include file="/core/gridDisplay.jsp" %>
                  </td>
              </tr>
              <tr>
                  <td>
                      <jsp:include page="/core/compiledFormFields.jsp">
                          <jsp:param name="gridID" value="ciEntityHistoricalDataGrid"/>
                          <jsp:param name="divId" value="ciEntityHistoricalDataDetailDiv"/>
                          <jsp:param name="includeLayerIds" value="PAGE_GRID_DETAIL_LAYER"/>
                          <jsp:param name="isGridBased" value="true"/>
                          <jsp:param name="headerTextLayerId" value="PAGE_GRID_DETAIL_LAYER"/>
                          <jsp:param name="removeFieldPrefix" value="true"/>
                          <jsp:param name="excludePageFields" value="true"/>
                          <jsp:param name="isLayerVisibleByDefault" value="true"/>
                      </jsp:include>
                  </td>
              </tr>
      </oweb:panel>
      </td>
      </tr>

<%@ include file="/core/tabfooter.jsp" %>
<jsp:include page="/core/footer.jsp" />