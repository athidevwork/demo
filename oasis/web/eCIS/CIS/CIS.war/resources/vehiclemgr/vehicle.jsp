<%@ page language="java" %>
<%@ page import="dti.ci.helpers.ICIConstants" %>
<%@ page import="dti.oasis.util.StringUtils"%>
<%@ page import="dti.oasis.messagemgr.MessageManager" %>

<%--
  Description:

  Author: gjli
  Date: June 26, 2006


  Revision Date    Revised By  Description
  ---------------------------------------------------
  05/15/2007       MLM         Added UI2 Changes
  07/02/2007       Mark        Added UI2 Changes
  09/03/2007       Kenney      remove UIStyleEdition;
                               change to panel tag;
                               change to compiledFormField page
  03/19/2009       kenney      Added Form Letter support for eCIS
  09/28/2010       wfu         111776: Replaced hardcode string with resource definition
  12/25/2017       ylu         Issue 190396: fix secured column save encrypt un-meanningful data to DB problem
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  10/16/2018       Elvin       Issue 195835: grid replacement
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
        entityNameDisplay = MessageManager.getInstance().formatMessage("ci.entity.vehicle.form.title");
    } else {
        entityNameDisplay = MessageManager.getInstance().formatMessage("ci.entity.search.label.vehicle") + " " + entityNameDisplay;
    }
%>

<c:set var="globalActionItemGroupId" value="CI_FOLDER_AG"></c:set>

<%@ include file="/core/header.jsp" %>
<%@ include file="/cicore/common.jsp" %>
<jsp:include page="/CI_EntitySelect.jsp"/>

<c:set var="tabMenuGroupId" value="${tabGroupId}"></c:set>
<%@ include file="/core/tabheader.jsp" %>

<script type="text/javascript" src="<%=cisPath%>/vehiclemgr/js/vehicle.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form name="CIVehicleForm" action="ciVehicle.do" method="POST">
    <tr>
        <td colspan="6" class="tabTitle">
            <b><%=entityNameDisplay%></b>
        </td>
    </tr>

    <tr>
        <td colspan="6">
            <oweb:message/>
        </td>
    </tr>

    <%@ include file="/cicore/commonFormHeader.jsp" %>
    <jsp:include page="/cicore/ciFolderCommon.jsp" />

    <tr>
        <td>
            <oweb:panel panelContentId="panelContentForVehicleList"
                        panelTitleId="panelTitleIdForVehicleList"
                        panelTitleLayerId="Vehicle_Info_Grid_Header_Layer">
                <tr>
                    <td>
                        <oweb:actionGroup actionItemGroupId="CI_VEHICLE_GRID_AIG" cssColorScheme="gray" layoutDirection="horizontal"/>
                    </td>
                </tr>

                <tr>
                    <td width="100%">
                        <c:set var="gridDisplayFormName" value="CIVehicleForm" scope="request"/>
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
        <td colspan="6" align="center">
            <oweb:actionGroup actionItemGroupId="CI_VEHICLE_AIG" cssColorScheme="blue" layoutDirection="horizontal"/>
        </td>
    </tr>

    <%@ include file="/core/tabfooter.jsp" %>
    <jsp:include page="/core/footer.jsp"/>