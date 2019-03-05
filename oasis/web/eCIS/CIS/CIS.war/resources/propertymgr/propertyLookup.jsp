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
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
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

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/cicore/common.jsp" %>
<%-- include some js --%>
<script type="text/javascript" src="<%=cisPath%>/propertymgr/js/propertyLookup.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<c:set var="property_noteIndROURL" value="javascript:loadPropertyNotes()" scope="request"/>
<!-- form -->
<form name="CIPropertyForm" action="ciProperty.do" method="POST">
     <%@ include file="/cicore/commonFormHeader.jsp" %>
    <tr>
        <td id="message" colspan=8>
            <oweb:message/>
        </td>
    </tr>

    <input type="hidden" name="pk" value="<c:out value="${entityFK}"/>"/>
    <input type="hidden" name="entityType" value="<c:out value="${entityType}"/>"/>

    <%-- Display Filter Criteria --%>
    <fmt:message key="ci.common.search.filter.criteria" var="filterCriteria" scope="request"/>
    <tr >
        <td>
            <%
                String filterCriteria = (String) request.getAttribute("filterCriteria");
                String panelCaptionForFilter = ApplicationContext.getInstance().getProperty("property.lengend.filter", filterCriteria);
            %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="includeLayerIds" value="PROP_FILTER_CRITERIA"/>
                <jsp:param name="headerText" value="<%=panelCaptionForFilter%>"/>
                <jsp:param name="isLayerVisibleByDefault" value="true"/>
                <jsp:param name="divId" value="Filter"/>
                <jsp:param name="excludePageFields" value="true"/>
                <jsp:param name="actionItemGroupId" value="CI_PROP_LOOKUP_FLTR_AIG"/>
            </jsp:include>
        </td>
    </tr>
    <tr>
        <td>
            <%-- Display list --%>
            <%
                String propertyListTitle = MessageManager.getInstance().formatMessage("property.lengend.list");
                String panelCaptionForList = ApplicationContext.getInstance().getProperty("property.lengend.list", propertyListTitle);
            %>
            <oweb:panel panelContentId="panelContentForList" panelTitleId="panelTitleIdForList"
                        panelTitle="<%=panelCaptionForList%>">
                <tr>
                    <td width="100%">
                        <c:set var="gridDisplayFormName" value="CIPropertyForm" scope="request"/>
                        <c:set var="gridDisplayGridId" value="testgrid" scope="request"/>
                        <c:set var="datasrc" value="#testgrid1" scope="request"/>
                        <%@ include file="/core/gridDisplay.jsp" %>
                    </td>
                </tr>

            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td colspan="6" align="center" style="padding-top:3px;padding-bottom:5px">
            <oweb:actionGroup actionItemGroupId="CI_PROPERTY_LOOKUP_AIG"
                              cssColorScheme="blue" layoutDirection="horizontal">
            </oweb:actionGroup>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>