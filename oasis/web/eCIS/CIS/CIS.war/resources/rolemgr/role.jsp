<%@ page import="dti.oasis.struts.ActionHelper,
                 dti.oasis.util.StringUtils,
                 dti.ci.helpers.ICIConstants"%>
<%@ page import="dti.oasis.tags.OasisFormField"%>
<%@ page import="dti.oasis.util.*" %>
<%@ page import="dti.oasis.app.ApplicationContext"%>
<%@ page language="java"%>
<%--
  Description:

  Author: Gerald C. Carney
  Date: Apr 12, 2004


  Revision Date    Revised By  Description
  --------------------------------------------------------------------
  04/12/2005       HXY         Added logic for controlling grid size.
  05/15/2007       MLM         Added UI2 Changes
  07/03/2007       James       Added UI2 Changes
  08/31/2007       Jerry       remove UIStyleEdition;
                               change to panel tag;
                               change to compiledFormField page
  09/13/2007       Jerry       Move the buttons into the actionGroup
  09/13/2007       James       Change action group name.
  09/14/2007       James       Change align of refresh button to right
  03/19/2009       kenney      Added Form Letter support for eCIS
  09/28/2010       wfu         111776: Replaced hardcode string with resource definition
  01/20/2011       Michael Li  Issue:116335
  07/01/2013       hxk         Issue 141840
                               1)  Add common.jsp so we include security.
                               2)  Add panelId FilterCriteria2
                                   so security can act on default filter div FilterCriteria
                                   and secondary filter div FilterCriteria2.
                               3)  Change variable message to msg to avoid conflict with newly included
                                   common.jsp variable of same name.
                               4)  Add message tag.
  05/30/2018       ylu         Issue 109175: refactor update
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  09/10/2018       Elvin       Issue 195381: add gridDisplayFormName
  10/9/2018       dzou         Grid replacement
  --------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="/WEB-INF/fmt.tld" %>

<c:set var="globalActionItemGroupId" value="CI_FOLDER_AG"></c:set>

<%@include file="/core/header.jsp" %>
<%@ include file="/cicore/common.jsp" %>
<jsp:include page="/CI_EntitySelect.jsp"/>

<c:set var="tabMenuGroupId" value="${tabGroupId}"></c:set>
<%@ include file="/core/tabheader.jsp" %>

<script language="javascript" src="<%=cisPath%>/rolemgr/js/role.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<FORM name="CIEntityRoleListForm" action="ciRole.do" method="POST">
    <%@ include file="/cicore/commonFormHeader.jsp" %>
    <jsp:include page="/cicore/ciFolderCommon.jsp" />

  <INPUT type="hidden" name="restrictSourceList" value="<c:out value="${restrictSourceList}"/>"/>
  <jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
  <jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
  <jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
      <tr valign="top">
          <td colspan="6" class="tabTitle">
              <oweb:message/>
          </td>
      </tr>

    <tr>
        <td class="tabTitle">
            <b><fmt:message key="ci.entity.search.label.roles"/> <c:out value="${param.entityName}"/></b>
        </td>
    </tr>
    <%@include file="/cicore/commonFilter.jsp" %>

    <fmt:message key="ci.common.search.filter.criteria" var="filterCriteria" scope="request"/>
    <%
        String filterCriteria = (String) request.getAttribute("filterCriteria");
        String panelCaption = ApplicationContext.getInstance().getProperty("role.lengend.filter", filterCriteria);
    %>
      <tr>
          <td>
              <jsp:include page="/core/compiledFormFields.jsp">
                  <jsp:param name="isGridBased" value="false"/>
                  <jsp:param name="divId" value="formfields"/>
                  <jsp:param name="panelId" value="FilterCriteria2"/>
                  <jsp:param name="headerText" value="<%=panelCaption%>"/>
                  <jsp:param name="excludeAllLayers" value="true"/>
                  <jsp:param name="actionItemGroupId" value="CI_ROLE_SEARCH_AIG"/>
              </jsp:include>
          </td>
      </tr>
      <tr>
          <td colspan="6">

              <oweb:panel panelContentId="panelContentForRoleList"
                          panelTitleId="panelTitleIdForRoleList" panelTitleLayerId="Entity_Role_List_Grid_Header_Layer">
                  <tr>
                      <td align="left">
                          <oweb:actionGroup actionItemGroupId="CI_ENTITY_ROLE_AIG"
                                            cssColorScheme="blue" layoutDirection="horizontal">
                          </oweb:actionGroup>
                      </td>
                  </tr>
                  <tr>
                      <td width="100%">
                          <c:set var="gridDisplayFormName" value="CIEntityRoleListForm" scope="request"/>
                          <c:set var="gridDisplayGridId" value="testgrid" scope="request"/>
                          <c:set var="datasrc" value="#testgrid1" scope="request"/>
                          <%@ include file="/core/gridDisplay.jsp" %>
                      </td>
                  </tr>
              </oweb:panel>
          </td>
      </tr>

<%@ include file="/core/tabfooter.jsp" %>

<jsp:include page="/core/footer.jsp" />
