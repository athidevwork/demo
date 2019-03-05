<%--
  Description: WIP Inquiry Page
  Author: Hong Yuan
  Date: Dec 12, 2005

  Revision Date    Revised By  Description
  ---------------------------------------------------
  05/15/2007       MLM         Added UI2 Changes
  07/05/2007       Mark        Added UI2 Changes
  07/09/2007       James       Added UI2 Changes
  08/29/2007       Kenney      remove UIStyleEdition;
                               change to panel tag;
                               change to compiledFormField page
  019/20/2007      FWCH        Added hidden field CS_DBPOOLID
  04/09/2008       wer         Removed hidden field CS_DBPOOLID to comply with new Role-based dbPoolId configuration.
  03/19/2009       kenney      Added Form Letter support for eCIS
  09/30/2010       wfu         111776: Replaced hardcode string with resource definition
  04/17/2018       dpang       192648: Refactor WIP Inquiry.
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  10/19/2018       Elvin       Issue 195835: grid replacement
  ---------------------------------------------------
  (C) 2004 Delphi Technology, inc. (dti)
--%>
<%@ page import="dti.ci.helpers.ICIConstants, dti.ci.wipinquirymgr.WIPInquiryFields" %>

<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="c" uri="/WEB-INF/c.tld"  %>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>

<%
    String entityNameDisplay = (String) request.getAttribute(ICIConstants.ENTITY_NAME_PROPERTY);
    if (StringUtils.isBlank(entityNameDisplay)) {
        entityNameDisplay = MessageManager.getInstance().formatMessage("ci.entity.wipInquiry.form.title");
    } else {
        entityNameDisplay = MessageManager.getInstance().formatMessage("ci.entity.search.label.wipInquiry") + " " + entityNameDisplay;
    }
%>

<c:set var="globalActionItemGroupId" value="CI_FOLDER_AG"/>
<%@include file="/core/header.jsp" %>
<jsp:include page="/cicore/common.jsp"/>
<jsp:include page="/CI_EntitySelect.jsp"/>

<c:set var="tabMenuGroupId" value="${tabGroupId}"/>
<%@ include file="/core/tabheader.jsp" %>

<script language="javascript" src="<%=cisPath%>/wipinquirymgr/js/WIPInquiry.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script language="javascript" src="<%=csPath%>/js/csLoadNotes.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form name="CIWIPInquiryForm" action="ciWIPInquiry.do" method="POST">
    <tr>
        <td colspan="6" class="tabTitle">
            <b><%=entityNameDisplay%></b>
        </td>
    </tr>

    <%@ include file="/cicore/commonFormHeader.jsp" %>
    <jsp:include page="/cicore/ciFolderCommon.jsp" />

    <input type="hidden" value="<%=(String) request.getAttribute(WIPInquiryFields.SEARCH_CRITERIA_CLIENT_ENTITY_FK)%>" name="<%=WIPInquiryFields.SEARCH_CRITERIA_CLIENT_ENTITY_FK%>"/>
    <input type="hidden" value="<%=(String) request.getAttribute(WIPInquiryFields.RESTRICT_SOURCE_LIST)%>" name="<%=WIPInquiryFields.RESTRICT_SOURCE_LIST%>"/>

    <tr>
        <td>
            <fmt:message key="ci.entity.search.wip.inquiry" var="wipInquiryTitle" scope="request"/>
            <% String wipInquiryTitle = (String) request.getAttribute("wipInquiryTitle"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="divId" value="panelContentForWIP Inquiry"/>
                <jsp:param name="headerText" value="<%= wipInquiryTitle %>"/>
                <jsp:param name="excludeAllLayers" value="true"/>
                <jsp:param name="actionItemGroupId" value="CI_WIP_INQUIRY_FILTER_AIG"/>
            </jsp:include>
        </td>
    </tr>

    <tr>
        <td>
            <oweb:panel panelContentId="panelContentForwipInquiryList"
                        panelTitleId="panelTitleIdForwipInquiryList"
                        panelTitleLayerId="WIP_Inquiry_List_Grid_Header_Layer">
                <tr>
                    <td><span id="wipInquiryListLegend"></span></td>
                </tr>

                <tr>
                    <td width="100%">
                        <c:set var="gridDisplayGridId" value="testgrid" scope="request"/>
                        <c:set var="datasrc" value="#testgrid1" scope="request"/>
                        <%@ include file="/core/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>

    <tr>
        <td colspan="6" align="center">
            <oweb:actionGroup actionItemGroupId="CI_WIP_INQUIRY_AIG" cssColorScheme="blue" layoutDirection="horizontal"/>
        </td>
    </tr>

    <%@ include file="/core/tabfooter.jsp" %>
    <jsp:include page="/core/footer.jsp"/>