<%@ page import="dti.ci.helpers.ICIConstants" %>
<%@ page import="dti.oasis.util.StringUtils" %>
<%@ page import="dti.oasis.app.ApplicationContext" %>
<%@ page import="dti.oasis.util.BaseResultSet" %>
<%@ page import="dti.oasis.messagemgr.MessageManager" %>
<%@ page language="java" %>
<%--
  Description: Correspondence
  
  Author: bhong
  Date: Jul 17, 2006
  
  
  Revision Date    Revised By  Description
  ---------------------------------------------------
  05/15/2007       MLM         Added UI2 Changes
  07/04/2007       Mark        Added UI2 Changes
  09/03/2007       Jerry       remove UIStyleEdition;
                               change to panel tag;
  09/14/2007       James       HTML formatting
  03/19/2009       kenney      Added Form Letter support for eCIS
  09/28/2010       wfu         111776: Replaced hardcode string with resource definition
  04/11/2018       dzhang      Issue 109204: correspondence refactor
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
        entityNameDisplay = MessageManager.getInstance().formatMessage("ci.entity.correspondence.form.title");
    } else {
        entityNameDisplay = MessageManager.getInstance().formatMessage("ci.entity.search.label.correspondence") + " " + entityNameDisplay;
    }
%>

<c:set var="globalActionItemGroupId" value="CI_FOLDER_AG"></c:set>
<%@ include file="/core/header.jsp" %>
<jsp:include page="/CI_EntitySelect.jsp"/>
<jsp:include page="/cicore/common.jsp"/>

<c:set var="tabMenuGroupId" value="${tabGroupId}"></c:set>
<%@ include file="/core/tabheader.jsp" %>

<%-- include some js --%>
<script type='text/javascript' src="<%=cisPath%>/js/gridbtnclicks.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=cisPath%>/correspondencemgr/js/maintainCorrespondence.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<c:set var="noteshowLabel" value="false" scope="request"/>
<!-- form -->
<form name="CICorresForm" action="ciCorres.do" method="POST">

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
            <%-- Display list --%>
            <oweb:panel panelContentId="panelContentForCorrespondence"
                        panelTitleId="Correspondence"
                        panelTitleLayerId="CORRES_GRIDHEADER">
                <tr>
                    <td width="100%">
                        <c:set var="gridDisplayFormName" value="CICorresForm" scope="request"/>
                        <c:set var="gridDisplayGridId" value="testgrid" scope="request"/>
                        <c:set var="gridDetailDivId" value="CIAuditTrailForm" scope="request"/>
                        <c:set var="datasrc" value="#testgrid1" scope="request"/>
                        <%@ include file="/core/gridDisplay.jsp" %>
                    </td>
                </tr>

                <tr>
                    <fmt:message key="ci.entity.notes.form.title" var="notesTitle" scope="request"/>
                    <% String notesTitle = (String) request.getAttribute("notesTitle"); %>
                    <td>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="dataBeanName" value="dataBean"/>
                            <jsp:param name="gridID" value="testgrid"/>
                            <jsp:param name="divId" value="CIAuditTrailForm"/>
                            <jsp:param name="headerText" value="<%=notesTitle%>"/>
                            <jsp:param name="removeFieldPrefix" value="true"/>
                            <jsp:param name="excludeAllLayers" value="true"/>
                        </jsp:include>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
</form>

<%@ include file="/core/tabfooter.jsp" %>

<jsp:include page="/core/footer.jsp"/>