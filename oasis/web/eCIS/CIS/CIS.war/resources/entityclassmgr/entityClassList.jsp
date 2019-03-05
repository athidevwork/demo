<%@ page import="dti.oasis.util.StringUtils,
                 dti.ci.helpers.ICIConstants"%>
<%@ page import="dti.oasis.app.ApplicationContext" %>
<%@ page import="dti.oasis.messagemgr.MessageManager" %>
<%@ page language="java"%>
<%--
  Description: Entity Class List

  Author: Gerald C. Carney
  Date: Mar 30, 2004


  Revision Date    Revised By  Description
  -------------------------------------------------------------------
  04/12/2005       HXY         Added logic for controlling grid size.
  05/15/2007       MLM         Added UI2 Changes
  07/03/2007       James       Added UI2 Changes
  09/03/2007       Jerry       remove UIStyleEdition;
                               change to panel tag;
                               change to compiledFormField page
  09/13/2007       Jerry       Move the buttons into the actionGroup
  09/13/2007       James       Change action group name.
  03/19/2009       kenney      Added Form Letter support for eCIS
  09/28/2010       wfu         111776: Replaced hardcode string with resource definition
  01/20/2011       Michael Li  Issue:116335
  07/01/2013       hxk         Issue 141840
                               1)  Add common.jsp so we include security.
                               2)  Change divId from FilterCriteria to FilterCriteria2
                                   so security can act on default filter div FilterCriteria
                                   and secondary filter div FilterCriteria2.
                               3)  Add message tag.
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  -------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="/WEB-INF/fmt.tld" %>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>

<%
String entityNameDisplay = (String) request.getAttribute(ICIConstants.ENTITY_NAME_PROPERTY);
if (StringUtils.isBlank(entityNameDisplay)) {
  entityNameDisplay = MessageManager.getInstance().formatMessage("ci.entity.classifications.form.title");
}
else {
  entityNameDisplay = MessageManager.getInstance().formatMessage("ci.entity.search.label.classifications") + " " + entityNameDisplay;
}
%>

<c:set var="globalActionItemGroupId" value="CI_FOLDER_AG"></c:set>

<%@include file="/core/header.jsp" %>
<jsp:include page="/CI_EntitySelect.jsp"/>
<jsp:include page="/cicore/common.jsp"/>

<c:set var="tabMenuGroupId" value="${tabGroupId}"></c:set>
<%@ include file="/core/tabheader.jsp" %>

<script type="text/javascript" src="<%=cisPath%>/entityclassmgr/js/entityClassList.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<FORM name="CIEntityClassListForm" action="ciEntityClassList.do" method="POST">

  <jsp:include page="/cicore/commonFormHeader.jsp"/>
  <jsp:include page="/cicore/ciFolderCommon.jsp" />

  <html:hidden value="<%=(String) request.getAttribute(ICIConstants.LIST_DISPLAYED_PROPERTY)%>" property="<%=ICIConstants.LIST_DISPLAYED_PROPERTY%>"/>

      <tr valign="top">
          <td colspan="6" class="tabTitle">
              <oweb:message/>
          </td>
      </tr>

      <tr valign="top">
          <td colspan="6" class="tabTitle">
              <b><%=entityNameDisplay%></b>
          </td>
      </tr>

      <%@ include file="/cicore/commonFilter.jsp" %>

      <tr>
          <td width="100%">
              <jsp:include page="/core/compiledFormFields.jsp">
                  <jsp:param name="isGridBased" value="false"/>
                  <jsp:param name="gridID" value="testgrid"/>
                  <jsp:param name="includeLayerIds" value="Entity_Class_List_Filter_Layer"/>
                  <jsp:param name="headerTextLayerId" value="Entity_Class_List_Filter_Layer"/>
                  <jsp:param name="isLayerVisibleByDefault" value="true"/>
                  <jsp:param name="divId" value="FilterCriteria2"/>
                  <jsp:param name="excludePageFields" value="true"/>
                  <jsp:param name="actionItemGroupId" value="CI_ENTITY_CLASS_LIST_AIG"/>
              </jsp:include>
          </td>
      </tr>

      <tr>
          <td>
              <oweb:panel panelContentId="panelContentForClassificationList"
                          panelTitleId="panelTitleIdForClassificationList"
                          panelTitleLayerId="Entity_Class_List_Grid_Header_Layer">
                  <tr>
                      <td>
                          <oweb:actionGroup actionItemGroupId="CI_ENT_CLSS_LST_GRID_AIG" cssColorScheme="gray"
                                            layoutDirection="horizontal">
                          </oweb:actionGroup>
                      </td>
                  </tr>
                  <tr>
                      <td colspan="6">
                          <c:set var="gridDisplayFormName" value="CIEntityClassListForm" scope="request"/>
                          <c:set var="gridDisplayGridId" value="entityClassListGrid" scope="request"/>
                          <c:set var="datasrc" value="#entityClassListGrid1" scope="request"/>
                          <%@ include file="/core/gridDisplay.jsp" %>
                      </td>
                  </tr>
              </oweb:panel>

              <tr>
                  <td colspan="6" align="center">
                      <oweb:actionGroup actionItemGroupId="CI_ENT_CLSS_LST_AIG"
                                        cssColorScheme="blue" layoutDirection="horizontal">
                      </oweb:actionGroup>
                  </td>
              </tr>
          </td>
      </tr>

<%@ include file="/core/tabfooter.jsp" %>
<jsp:include page="/core/footer.jsp" />

