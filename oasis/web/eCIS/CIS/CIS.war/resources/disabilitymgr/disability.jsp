<%@ page import="dti.ci.helpers.ICIConstants" %>
<%@ page import="dti.ci.disabilitymgr.DisabilityFields" %>
<%@ page import="dti.oasis.app.ApplicationContext" %>
<%@ page import="dti.oasis.util.StringUtils" %>
<%@ page import="dti.oasis.util.BaseResultSet" %>
<%@ page import="dti.oasis.messagemgr.MessageManager" %>
<%@ page language="java" %>
<%--
  Description: Disability
  
  Author: bhong
  Date: May 12, 2006
  
  
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
  09/28/2010       wfu         111776: Replaced hardcode string with resource definition
  01/20/2011       Michael Li  Issue:116335
  07/01/2013       hxk         Issue 141840
                               1)  change divid of FilterCriteria to FilterCriteria2 so security
                                   can enable elements of priimary and secondary filters that are
                                   at first disabled.
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  10/08/2018       dzou        Grid replacement
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
        entityNameDisplay = MessageManager.getInstance().formatMessage("ci.entity.disability.form.title");
    } else {
        entityNameDisplay = MessageManager.getInstance().formatMessage("ci.entity.search.label.disability") + " " + entityNameDisplay;
    }
%>

<c:set var="globalActionItemGroupId" value="CI_FOLDER_AG"></c:set>

<%@ include file="/core/header.jsp" %>
<jsp:include page="/cicore/common.jsp"/>
<jsp:include page="/CI_EntitySelect.jsp"/>

<c:set var="tabMenuGroupId" value="${tabGroupId}"></c:set>
<%@ include file="/core/tabheader.jsp" %>

<%-- include some js --%>
<script type='text/javascript' src="<%=cisPath%>/js/gridbtnclicks.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=cisPath%>/disabilitymgr/js/disability.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<%-- form --%>
<form name="CIDisabilityForm" action="ciDisability.do" method="POST">
    <%@ include file="/cicore/commonFormHeader.jsp" %>
    <jsp:include page="/cicore/ciFolderCommon.jsp" />

    <tr valign="top">
        <td colspan="6" class="tabTitle">
            <oweb:message/>
        </td>
    </tr>

    <tr valign="top"><td colspan="6" class="tabTitle">
        <b><%=entityNameDisplay%></b>
    </td></tr>
    <%-- Display Filter Criteria --%>
    <fmt:message key="ci.common.search.filter.criteria" var="filterCriteria" scope="request"/>
                  <%@ include file="/cicore/commonFilter.jsp" %>
    <tr>
        <td>
            <%
                String filterCriteria = (String) request.getAttribute("filterCriteria");
                String panelCaptionForFilter = ApplicationContext.getInstance().getProperty("disability.lengend.filter", filterCriteria);
            %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="divId" value="Filter"/>
                <jsp:param name="panelId" value="FilterCriteria2"/>
                <jsp:param name="headerText" value="<%=panelCaptionForFilter%>"/>
                <jsp:param name="excludeAllLayers" value="true"/>
                <jsp:param name="actionItemGroupId" value="CI_DISABILITY_SEARCH_AIG"/>
            </jsp:include>
        </td>
    </tr>
        <%-- Display list --%>
    <tr>
        <td>

            <oweb:panel panelContentId="panelContentForDisplayList" panelTitleId="panelTitleIdForDisplayList" panelTitleLayerId="DISABILITY_GRIDHEADER">
                <tr>
                    <td>
                        <oweb:actionGroup actionItemGroupId="CI_DISABILITY_GRID_AIG" cssColorScheme="gray"
                                          layoutDirection="horizontal">
                        </oweb:actionGroup>
                    </td>
                </tr>
                <tr>
                    <td width="100%">
                        <c:set var="gridDisplayFormName" value="CIDisabilityForm" scope="request"/>
                        <c:set var="gridDisplayGridId" value="testgrid" scope="request"/>
                        <c:set var="datasrc" value="#testgrid1" scope="request"/>
                        <%@ include file="/core/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
</form>

    <tr>
        <td colspan="6" align="center" style="padding-top:3px;padding-bottom:5px">
            <oweb:actionGroup actionItemGroupId="CI_DISABILITY_AIG"
                              cssColorScheme="blue" layoutDirection="horizontal">
            </oweb:actionGroup>
        </td>
    </tr>
<%@ include file="/core/tabfooter.jsp" %>
<jsp:include page="/core/footer.jsp"/>