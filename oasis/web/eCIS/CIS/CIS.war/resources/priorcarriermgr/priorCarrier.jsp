<%@ page import="dti.ci.helpers.ICIConstants"%>
<%@ page import="dti.oasis.util.StringUtils"%>
<%@ page import="dti.oasis.util.BaseResultSet" %>
<%@ page import="dti.oasis.util.SysParmProvider" %>
<%@ page import="dti.oasis.messagemgr.MessageManager" %>
<%@ page import="java.util.*" %>
<%@ page import="dti.oasis.tags.OasisFormField" %>
<%@ page language="java" %>
<%--
  Description: JSP for Prior Carrier
  
  Author: ldong
  Date: Mar 15, 2007
  
  
  Revision Date    Revised By  Description
  ---------------------------------------------------
  07/04/2007       Mark        Added UI2 Changes
  09/04/2007       Jerry       remove UIStyleEdition;
                               change to panel tag;
  09/12/2007       James       Add headerText
  09/14/2007       James       Change panel title from Filter to
                               Filter Criteria
  03/19/2009       kenney      Added Form Letter support for eCIS
  09/22/2009       kshen       Added hidden field defalutTermYear.
  09/28/2010       wfu         111776: Replaced hardcode string with resource definition
  10/08/2012       kshen       Refactor the page.
  07/01/2013       hxk         Issue 141840
                               1)  Change divId from FilterCriteria to FilterCriteria2
                                   so security can act on default filter div FilterCriteria
                                   and secondary filter div FilterCriteria2.
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  10/12/2018       dzou        Grid replacement
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
        entityNameDisplay = MessageManager.getInstance().formatMessage("ci.entity.priorCarrier.form.title");
    } else {
        entityNameDisplay = MessageManager.getInstance().formatMessage("ci.entity.search.label.priorCarrier") + " " + entityNameDisplay;
    }
%>

<c:set var="globalActionItemGroupId" value="CI_FOLDER_AG"></c:set>

<%@ include file="/core/header.jsp" %>
<jsp:include page="/CI_EntitySelect.jsp"/>

<c:set var="tabMenuGroupId" value="${tabGroupId}"></c:set>
<%@ include file="/core/tabheader.jsp" %>

<%@ include file="/cicore/common.jsp" %>

<%-- include some js --%>
<script type='text/javascript' src="<%=cisPath%>/js/gridbtnclicks.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=cisPath%>/priorcarriermgr/js/priorCarrier.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<!--form-->
<form name="CIPriorCarrierForm" action="ciPriorCarrier.do" method="POST">
    <%@ include file="/cicore/commonFormHeader.jsp" %>
    <jsp:include page="/cicore/ciFolderCommon.jsp" />

    <tr valign="top">
        <td colspan="6">
            <oweb:message/>
        </td>
    </tr>

    <input type="hidden" name="isLogHist" value="<%=SysParmProvider.getInstance().getSysParm("CM_LOG_EXT_CLMS_HIST", "N")%>">
    <input type="hidden" name="defaultTermYear" value="<c:out value="${defaultTermYear}"/>">
    <input type="hidden" name="isUserAllowedToDeleteAuditRecords" value="<%=Authenticator.isUserInProfile("MLMIC DELETE AUDIT PRIOR CARRIER")? "Y": "N"%>">
    <input type="hidden" name="idAuditEnabled" value="<%=SysParmProvider.getInstance().getSysParm("CI_PRIOR_CARRIER_ADT", "N")%>">
    <input type="hidden" name="CI_PRIOR_CARR_RST_YR" value="<%=SysParmProvider.getInstance().getSysParm("CI_PRIOR_CARR_RST_YR", "Y")%>">

    <%
        StringBuffer filterFieldList = new StringBuffer("");
        for (OasisFormField field : (ArrayList<OasisFormField>) fieldsMap.getLayerFields("PRIOR_CARRIER_SEARCHING")) {
            filterFieldList.append(',').append(field.getFieldId());
        }
    %>
    <input type="hidden" name="filterFieldList" value="<%=filterFieldList.toString()%>">

    <tr valign="top">
        <td colspan="6" class="tabTitle">
            <b><%=entityNameDisplay%></b>
        </td>
    </tr>

    <tr>
        <td width="100%">
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="includeLayerIds" value="PRIOR_CARRIER_SEARCHING"/>
                <jsp:param name="headerTextLayerId" value="PRIOR_CARRIER_SEARCHING"/>
                <jsp:param name="isLayerVisibleByDefault" value="true"/>
                <jsp:param name="divId" value="Filter"/>
                <jsp:param name="panelId" value="FilterCriteria2"/>
                <jsp:param name="excludePageFields" value="true"/>
                <jsp:param name="actionItemGroupId" value="CI_PRIOR_CARRIE_FLT_AIG"/>
            </jsp:include>
        </td>
    </tr>

    <tr>
        <td>
            <%-- Display list --%>
            <oweb:panel panelContentId="panelContentForList"
                        panelTitleId="panelTitleIdForList"
                        panelTitleLayerId="PRIOR_CARRIER_HEADER">
                <tr>
                    <td>
                        <oweb:actionGroup actionItemGroupId="CI_PRIOR_CARRIE_GRID_AIG"
                                          cssColorScheme="gray" layoutDirection="horizontal">
                        </oweb:actionGroup>
                    </td>
                </tr>

                <tr>
                    <td width="100%">
                        <c:set var="gridDisplayFormName" value="CIPriorCarrierForm" scope="request"/>
                        <c:set var="gridDisplayGridId" value="testgrid" scope="request"/>
                        <c:set var="gridDetailDivId" value="formfields" scope="request"/>
                        <c:set var="datasrc" value="#testgrid1" scope="request"/>
                        <c:set var="selectable" value="false" scope="request"/>
                        <%@ include file="/core/gridDisplay.jsp" %>
                    </td>
                </tr>

                <tr>
                    <td>
                        <fmt:message key="ci.entity.prior.carrier.detail" var="priorCarrierDetailTitle" scope="request"/>
                        <% String priorCarrierDetailTitle = (String) request.getAttribute("priorCarrierDetailTitle"); %>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="dataBeanName" value="gridDataBean"/>
                            <jsp:param name="gridID" value="testgrid"/>
                            <jsp:param name="divId" value="formfields"/>
                            <jsp:param name="removeFieldPrefix" value="true"/>
                            <jsp:param name="excludeAllLayers" value="true"/>
                            <jsp:param name="headerText" value="<%=priorCarrierDetailTitle%>"/>
                        </jsp:include>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>

    <tr>
        <td colspan="6" align="center" style="padding-top:3px;padding-bottom:5px">
            <oweb:actionGroup actionItemGroupId="CI_PRIOR_CARRIE_AIG"
                              cssColorScheme="blue" layoutDirection="horizontal">
            </oweb:actionGroup>
        </td>
    </tr>

<%@ include file="/core/tabfooter.jsp" %>

<jsp:include page="/core/footer.jsp"/>

