<%--
  Description: Audit Trail Page
  Author: Hong Yuan
  Date: Oct 31, 2005

  Revision Date    Revised By  Description
  ---------------------------------------------------
  05/15/2007       MLM         Added UI2 Changes
  07/04/2007       Mark        Added UI2 Changes,Moved "Audit Records for ..." to title
  07/13/2007       Mark        Moved page title to tab title
  09/03/2007       Jerry       Remove UIStyleEdition;
                               change to panel tag;
  03/19/2009       kenney      Added Form Letter support for eCIS
  09/30/2010       wfu         111776: Replaced hardcode string with resource definition
  07/07/2016       Elvin       Issue 177718: remove initPage, selectFirstRowInGrid when handleOnLoad
  04/19/2018       ylu         Issue 109179:  refactor old style code
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  ---------------------------------------------------
  (C) 2004 Delphi Technology, inc. (dti)
--%>
<%@ page language="java" import="dti.ci.auditmgr.AuditTrailFields,
                                 dti.oasis.tags.WebLayer" %>
<%@ page import="dti.ci.helpers.ICIConstants" %>
<%@ page import="dti.oasis.util.StringUtils"%>
<%@ page import="dti.oasis.util.BaseResultSet" %>
<%@ page import="dti.oasis.messagemgr.MessageManager" %>

<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<%
    String entityNameDisplay = (String) request.getAttribute(ICIConstants.ENTITY_NAME_PROPERTY);
    if (StringUtils.isBlank(entityNameDisplay)) {
        entityNameDisplay = MessageManager.getInstance().formatMessage("ci.entity.auditTrail.form.title");
    } else {
        entityNameDisplay = MessageManager.getInstance().formatMessage("ci.entity.search.label.auditTrail") + " " + entityNameDisplay;
    }
%>

<c:set var="globalActionItemGroupId" value="CI_FOLDER_AG"></c:set>

<%@include file="/core/header.jsp"%>
<jsp:include page="/CI_EntitySelect.jsp"/>
<jsp:include page="/cicore/common.jsp"/>

<c:set var="tabMenuGroupId" value="${tabGroupId}"></c:set>
<%@ include file="/core/tabheader.jsp" %>

<script type='text/javascript' src="<%=cisPath%>/auditmgr/js/auditTrail.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<form name="CIAuditTrailForm" action="ciAuditTrail.do" method="POST">
    <%@ include file="/cicore/commonFormHeader.jsp" %>
    <jsp:include page="/cicore/ciFolderCommon.jsp" />

    <tr valign="top">
        <td colspan="6" class="tabTitle">
            <b>
                <%=entityNameDisplay%>
            </b>
        </td>
    </tr>
    <tr>
        <td>
            <fmt:message key="ci.entity.audit.trail.filter" var="trailFilterTitle" scope="request"/>
            <% String trailFilterTitle = (String) request.getAttribute("trailFilterTitle"); %>
            <%-- Iterate through form fields! --%>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="divId" value="CIAuditTrail"/>
                <jsp:param name="headerText" value="<%=  trailFilterTitle %>"/>
                <jsp:param name="excludeAllLayers" value="true"/>
                <jsp:param name="actionItemGroupId" value="CI_AUDITRL_AIG"/>
            </jsp:include>
        </td>
    </tr>
    <tr>
        <td>
            <oweb:panel panelContentId="panelContentForauditTrailListHeader"
                        panelTitleId="panelTitleIdForauditTrailListHeader"
                        panelTitleLayerId="<%=AuditTrailFields.AUDIT_TRAIL_LIST_GRID_HEADER_LAYER%>">
                <tr>
                    <td>
                        <c:set var="gridDisplayFormName" value="CIAuditTrailForm" scope="request"/>
                        <c:set var="gridDisplayGridId" value="testgrid" scope="request"/>
                        <c:set var="gridDetailDivId" value="auditTrailDetailDiv" scope="request"/>
                        <c:set var="datasrc" value="#testgrid1" scope="request"/>
                        <%@ include file="/core/gridDisplay.jsp" %>
                    </td>
                </tr>
                <%--Detail panel begin--%>
                <tr>
                    <td id="formfields">
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="dataBeanName" value="dataBean"/>
                            <jsp:param name="gridID" value="testgrid"/>
                            <jsp:param name="includeLayerIds"
                                       value="<%=AuditTrailFields.AUDIT_TRAIL_DETAIL_LAYER%>"/>
                            <jsp:param name="headerTextLayerId" value="<%=AuditTrailFields.AUDIT_TRAIL_DETAIL_LAYER%>"/>
                            <jsp:param name="isLayerVisibleByDefault" value="true"/>
                            <jsp:param name="divId" value="auditTrailDetailDiv"/>
                            <jsp:param name="excludePageFields" value="true"/>
                        </jsp:include>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>

<%@ include file="/core/tabfooter.jsp" %>

<jsp:include page="/core/footer.jsp"/>
