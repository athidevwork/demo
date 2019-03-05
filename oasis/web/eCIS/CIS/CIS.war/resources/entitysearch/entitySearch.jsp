<%@ page import="dti.oasis.recordset.Record" %>
<%@ page import="dti.oasis.session.UserSession" %>
<%@ page language="java"%>
<%--
  JSP for displaying entity search criteria with a list of entities.

  Author: Gerald C. Carney
  Date: Oct 21, 2003


  Revision Date    Revised By  Description
  ---------------------------------------------------

  12/30/2016       dpang       181349 - Reference common.js for sorting grid in handleOnLoad
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  ---------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib prefix="c" uri="/WEB-INF/c.tld"  %>
<%@ taglib prefix="fmt" uri="/WEB-INF/fmt.tld" %>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<%
  String orgSortColumn = "";
  String orgSortType = "";
  String orgSortOrder = "";
  String orgRowId = "";

  UserSession userSession = UserSessionManager.getInstance().getUserSession(request);
  if (userSession.has("backToEntityListOrgInfo")) {
    Record orgInfoRecord = (Record) (userSession.get("backToEntityListOrgInfo"));

    orgSortColumn = orgInfoRecord.getStringValue("orgSortColumn", "");
    orgSortType = orgInfoRecord.getStringValue("orgSortType", "");
    orgSortOrder = orgInfoRecord.getStringValue("orgSortOrder", "");
    orgRowId = orgInfoRecord.getStringValue("orgRowId", "");

    userSession.remove("backToEntityListOrgInfo");
  }
%>

<%@include file="/core/header.jsp" %>
<jsp:include page="/cicore/common.jsp"/>

<script language="javascript" src="<%=cisPath%>/entitysearch/js/entitySearch.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<!-- Content -->
<FORM name="CIEntitySearchForm" action="ciEntitySearch.do" method="POST">
  <%@ include file="/cicore/commonFormHeader.jsp" %>
  <input type="hidden" name="claimPK" value="<%=request.getAttribute("claimPK")%>"/>
  <input type="hidden" name="CI_PHONE_PART_SRCH" value="<%=(String) request.getAttribute("CI_PHONE_PART_SRCH") %>">
  <input type="hidden" name="orgSortColumn" value="<%=orgSortColumn%>">
  <input type="hidden" name="orgSortType" value="<%=orgSortType%>">
  <input type="hidden" name="orgSortOrder" value="<%=orgSortOrder%>">
  <input type="hidden" name="orgRowId" value="<%=orgRowId%>">

  <tr>
    <td colspan="6">
      <oweb:message/>
    </td>
  </tr>

  <%
    boolean isPanelCollapsed = YesNoFlag.getInstance(ApplicationContext.getInstance().getProperty("collapse.panel.after.cis.search")).booleanValue();
    isPanelCollapsed = dataBean.getRowCount() == 0 ? false : isPanelCollapsed;
  %>
  <tr>
    <fmt:message key="ci.common.search.filter.criteria" var="filterCriteria" scope="request"/>
    <% String entitySearchTitle = (String) request.getAttribute("filterCriteria"); %>

    <td colspan="6">
      <jsp:include page="/core/compiledFormFields.jsp">
        <jsp:param name="headerText" value="<%=   entitySearchTitle%>"/>
        <jsp:param name="isGridBased" value="false"/>
        <jsp:param name="divId" value="SearchCriteria"/>
        <jsp:param name="isPanelCollaspedByDefault" value="<%=  isPanelCollapsed %>"/>
        <jsp:param name="excludeAllLayers" value="true"/>
        <jsp:param name="actionItemGroupId" value="CI_ENT_SEARCH_AIG"/>
      </jsp:include>
    </td>
  </tr>

    <% if (dataBean.getRowCount() > 0) {   %>
    <tr>
      <td colspan="6">
        <oweb:panel panelTitleLayerId="Entity_List_Grid_Header_Layer" panelTitleId="panelTitleForEntityList"
                    panelContentId="panelContentForEntityList">
    <tr>
      <td colspan="6">
        <oweb:actionGroup actionItemGroupId="CI_ENTITY_SEARCH_LIST_AIG" layoutDirection="horizontal"
                          cssColorScheme="gray"/>
      </td>
    </tr>
    <tr>
      <td width="100%">
        <c:set var="gridDisplayFormName" value="CIEntitySearchForm" scope="request"/>
        <c:set var="gridDisplayGridId" value="entityListGrid" scope="request"/>
        <c:set var="datasrc" value="#entityListGrid1" scope="request"/>
        <%@ include file="/core/gridDisplay.jsp" %>
      </td>
    </tr>
    </oweb:panel>
    <% } %>

<jsp:include page="/core/footer.jsp"/>