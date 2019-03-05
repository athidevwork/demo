<%@ page import="dti.ci.helpers.ICIConstants" %>
<%@ page import="dti.oasis.util.StringUtils"%>
<%@ page import="dti.oasis.util.BaseResultSet" %>
<%@ page import="dti.oasis.app.ApplicationContext" %>
<%@ page import="dti.oasis.messagemgr.MessageManager" %>
<%@ page language="java" %>
<%--
  Description: Denominator

  Author: bhong
  Date: May 30, 2006


  Revision Date    Revised By  Description
  ---------------------------------------------------
  05/15/2007       MLM         Added UI2 Changes
  07/02/2007       Mark        Added UI2 Changes
  07/13/2007       Mark        Moved page title to tab title,
                               fixed the textarea alignment problem
  09/03/2007       Kenney      remove UIStyleEdition;
                               change to panel tag;
                               change to compiledFormField page
  09/13/2007       Jerry       Move the buttons into the actionGroup
  09/14/2007       James       HTML formatting
  03/19/2009       kenney      Added Form Letter support for eCIS
  09/28/2010       wfu         111776: Replaced hardcode string with resource definition
  01/20/2011       Michael Li  Issue:116335
  07/01/2013       hxk         Issue 141840
                               1)  Add common.jsp so we include security.
                               2)  Change divId from FilterCriteria to FilterCriteria2
                                   so security can act on default filter div FilterCriteria
                                   and secondary filter div FilterCriteria2.
                               3)  Add msg tag.
                               4)  Override getChanges js function because this function exists in xmlproc.js and
                                   common.js.  The version in xmlproc.js is the one that is copied into this JSP, and
                                   this one formats the XML so the back end works correctly.
  02/21/2017       dzhang      Issue 179102: Detail form should not be displayed when the grid is empty.
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  10/9/2018        dzou       Grid replacement
  ---------------------------------------------------
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
        entityNameDisplay = MessageManager.getInstance().formatMessage("ci.entity.denominator.form.title");
    } else {
        entityNameDisplay = MessageManager.getInstance().formatMessage("ci.entity.search.label.denominator") + " " + entityNameDisplay;
    }
%>

<c:set var="globalActionItemGroupId" value="CI_FOLDER_AG"></c:set>

<%@ include file="/core/header.jsp" %>
<jsp:include page="/CI_EntitySelect.jsp"/>
<%@ include file="/cicore/common.jsp" %>

<c:set var="tabMenuGroupId" value="${tabGroupId}"></c:set>
<%@ include file="/core/tabheader.jsp" %>

<%-- include some js --%>
<script type="text/javascript" src="<%=cisPath%>/entitydenominatormgr/js/maintainEntityDenominator.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form name="CIDenominatorForm" action="ciDenominator.do" method="POST">

    <jsp:include page="/cicore/commonFormHeader.jsp"/>
    <jsp:include page="/cicore/ciFolderCommon.jsp" />

    <tr valign="top">
        <td colspan="6">
            <oweb:message/>
        </td>
    </tr>

    <tr valign="top">
        <td colspan="6" class="tabTitle">
            <b>
                <%=entityNameDisplay%>
            </b>
        </td>
    </tr>

    <%-- Display Filter Criteria --%>
    <fmt:message key="ci.common.search.filter.criteria" var="filterCriteria" scope="request"/>
    <%@ include file="/cicore/commonFilter.jsp" %>

    <tr>
        <td>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="includeLayerIds" value="DENO_FILTER_CRITERIA"/>
                <jsp:param name="headerTextLayerId" value="DENO_FILTER_CRITERIA"/>
                <jsp:param name="isLayerVisibleByDefault" value="true"/>
                <jsp:param name="divId" value="FilterCriteria2"/>
                <jsp:param name="excludePageFields" value="true"/>
                <jsp:param name="actionItemGroupId" value="CI_DENOMINATOR_SEARCH_AIG"/>
            </jsp:include>
        </td>
    </tr>

    <tr>
        <td>
            <%-- Display list --%>
            <oweb:panel panelContentId="panelContentForDenominatorList"
                        panelTitleId="panelTitleIdForDenominatorList" panelTitleLayerId="DENO_GRIDHEADER">

                <tr>
                    <td>
                        <oweb:actionGroup actionItemGroupId="CI_DENOMINATOR_GRID_AIG" cssColorScheme="gray"
                                          layoutDirection="horizontal">
                        </oweb:actionGroup>
                    </td>
                </tr>

                <tr>
                    <td width="100%">
                        <c:set var="gridDisplayFormName" value="CIDenominatorForm" scope="request"/>
                        <c:set var="gridDisplayGridId" value="testgrid" scope="request"/>
                        <c:set var="gridDetailDivId" value="formfields" scope="request"/>
                        <c:set var="datasrc" value="#testgrid1" scope="request"/>

                        <%@ include file="/core/gridDisplay.jsp" %>
                    </td>
                </tr>

                <tr>
                    <td>
                        <!-- Display detail -->
                        <fmt:message key="ci.entity.detail.form.label" var="detailTitle" scope="request"/>
                        <% String detailTitle = (String) request.getAttribute("detailTitle"); %>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="dataBeanName" value="gridDataBean"/>
                            <jsp:param name="gridID" value="testgrid"/>
                            <jsp:param name="divId" value="formfields"/>
                            <jsp:param name="headerText" value="<%=detailTitle%>"/>
                            <jsp:param name="excludeAllLayers" value="true"/>
                        </jsp:include>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>

    <tr>
        <td colspan="6" align="center" style="padding-top:3px;padding-bottom:5px">
            <oweb:actionGroup actionItemGroupId="DENOMINATOR_AIG"
                              cssColorScheme="blue" layoutDirection="horizontal">
            </oweb:actionGroup>
        </td>
    </tr>

<%@ include file="/core/tabfooter.jsp" %>

<jsp:include page="/core/footer.jsp"/>