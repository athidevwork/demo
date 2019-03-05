<%@ page import="dti.ci.helpers.ICIConstants" %>
<%@ page import="dti.oasis.util.StringUtils"%>
<%@ page import="dti.oasis.util.BaseResultSet" %>
<%@ page import="dti.oasis.messagemgr.MessageManager" %>
<%@ page language="java" %>
<%--
  Description: Property
  
  Author: bhong
  Date: Jun 28, 2006
  
  
  Revision Date    Revised By  Description
  ---------------------------------------------------
  05/15/2007       MLM         Added UI2 Changes
  07/02/2007       Mark        Added UI2 Changes
  07/13/2007       Mark        Moved page title to tab title
  09/03/2007       Kenney      remove UIStyleEdition;
                               change to panel tag;
                               change to compiledFormField page
  09/13/2007       Jerry       Move the buttons into the actionGroup
  03/19/2009       kenney      Added Form Letter support for eCIS
  05/13/2009       kshen       Added codes to handle db error.
  09/28/2010       wfu         111776: Replaced hardcode string with resource definition
  07/01/2013       hxk         Issue 141840
                               Add panelId of FilterCriteria2 so security can act on default filter
                               div FilterCriteria and secondary filter div FilterCriteria2.
  12/25/2017       ylu         Issue 190396: fix secured column save encrypt unmeanningful data to DB problem
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  10/15/2018       dzou        Grid replacement
  ---------------------------------------------------
  (C) 2004 Delphi Technology, inc. (dti)
--%>
<!--load some libs-->
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<%
    String entityNameDisplay = (String) request.getAttribute(ICIConstants.ENTITY_NAME_PROPERTY);
    if (StringUtils.isBlank(entityNameDisplay)) {
        entityNameDisplay = MessageManager.getInstance().formatMessage("ci.entity.property.form.title");
    } else {
        entityNameDisplay = MessageManager.getInstance().formatMessage("ci.entity.search.label.property") + " " + entityNameDisplay;
    }
%>

<c:set var="globalActionItemGroupId" value="CI_FOLDER_AG"></c:set>

<%@ include file="/core/header.jsp" %>
<jsp:include page="/CI_EntitySelect.jsp"/>

<c:set var="tabMenuGroupId" value="${tabGroupId}"></c:set>
<%@ include file="/core/tabheader.jsp" %>

<%@ include file="/cicore/common.jsp" %>

<%-- include some js --%>
<script type="text/javascript" src="<%=csPath%>/js/csLoadNotes.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type='text/javascript' src="<%=cisPath%>/js/gridbtnclicks.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=cisPath%>/propertymgr/js/property.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<c:set var="property_noteIndROURL" value="javascript:loadPropertyNotes()" scope="request"/>
<!-- form -->
<form name="CIPropertyForm" action="ciProperty.do" method="POST">
    <%@ include file="/cicore/commonFormHeader.jsp" %>
    <jsp:include page="/cicore/ciFolderCommon.jsp" />

    <tr valign="top">
        <td colspan="6">
            <oweb:message/>
        </td>
    </tr>

    <tr valign="top">
        <td colspan="6" class="tabTitle">
            <b><%=entityNameDisplay%></b>
        </td>
    </tr>

    <%-- Display Filter Criteria --%>
    <tr>
        <td>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="includeLayerIds" value="PROP_FILTER_CRITERIA"/>
                <jsp:param name="headerTextLayerId" value="PROP_FILTER_CRITERIA"/>
                <jsp:param name="isLayerVisibleByDefault" value="true"/>
                <jsp:param name="divId" value="Filter"/>
                <jsp:param name="panelId" value="FilterCriteria2"/>
                <jsp:param name="excludePageFields" value="true"/>
                <jsp:param name="actionItemGroupId" value="CI_PROPERTY_CLEAR_AIG"/>
            </jsp:include>
        </td>
    </tr>
    <tr>
        <td>
            <%-- Display list --%>
            <oweb:panel panelContentId="panelContentForList" panelTitleId="panelTitleIdForList" panelTitleLayerId="PROP_GRIDHEADER">
                <tr>
                    <td>
                        <oweb:actionGroup actionItemGroupId="CI_PROPERTY_GRID_AIG" cssColorScheme="gray"
                                          layoutDirection="horizontal">
                        </oweb:actionGroup>
                    </td>
                </tr>
                <tr>
                    <td width="100%">
                        <c:set var="gridDisplayFormName" value="CIPropertyForm" scope="request"/>
                        <c:set var="gridDisplayGridId" value="testgrid" scope="request"/>
                        <c:set var="gridDetailDivId" value="formfields" scope="request"/>
                        <c:set var="datasrc" value="#testgrid1" scope="request"/>
                        <%@ include file="/core/gridDisplay.jsp" %>
                    </td>
                </tr>
                <tr>
                    <td>
                        <fmt:message key="ci.entity.detail.form.label" var="detailTitle" scope="request"/>
                        <% String detailTitle = (String) request.getAttribute("detailTitle"); %>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="isGridBased" value="true"/>
                            <jsp:param name="dataBeanName" value="dataBean"/>
                            <jsp:param name="gridID" value="testgrid"/>
                            <jsp:param name="divId" value="formfields"/>
                            <jsp:param name="headerText" value="<%=detailTitle%>"/>
                            <jsp:param name="removeFieldPrefix" value="true"/>
                            <jsp:param name="excludeAllLayers" value="true"/>
                        </jsp:include>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td colspan="6" align="center" style="padding-top:3px;padding-bottom:5px">
            <oweb:actionGroup actionItemGroupId="CI_PROPERTY_AIG"
                              cssColorScheme="blue" layoutDirection="horizontal">
            </oweb:actionGroup>
        </td>
    </tr>
<%@ include file="/core/tabfooter.jsp" %>
<jsp:include page="/core/footer.jsp"/>