<%@ page import="dti.ci.helpers.ICIConstants" %>
<%@ page import="dti.oasis.app.ApplicationContext" %>
<%@ page import="dti.oasis.messagemgr.MessageManager" %>
<%--
  Description: training page

  Author: Hong Yuan
  Date: Jan 12, 2006

  Revision Date    Revised By  Description
  ---------------------------------------------------
  05/15/2007       MLM         Added UI2 Changes
  07/03/2007       James       Added UI2 Changes
  08/31/2007       Jerry       Remove UIStyleEdition
  03/19/2009       kenney      Added Form Letter support for eCIS
  10/19/2010       wfu         111776 - Replaced hardcode string with resource definition
  11/09/2010       kshen       Added codes to handle the Delete button when a record is checked.
  07/01/2013       hxk         Issue 141840
                               1)  Add common.jsp for security.
                               2)  Add message tag.
                               3)  Override getChanges js function because this function exists in xmlproc.js and
                                   common.js.  The version in xmlproc.js is the one that is copied into this JSP, and
                                   this one formats the XML so the back end works correctly.
  01/03/2013       Elvin       Issue 150900: use Entity Select Search window instead of Select Institution window
  07/04/2014       bzhu        Issue 154822
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  09/07/2018       Elvin       Issue 194134: delete hidden field which reading values from datasrc (also useless now)
  10/16/2018       Elvin       Issue 195835: grid replacement
  ---------------------------------------------------
  (C) 2004 Delphi Technology, inc. (dti)
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<%
    String entityNameDisplay = (String) request.getAttribute(ICIConstants.ENTITY_NAME_PROPERTY);
    if (StringUtils.isBlank(entityNameDisplay)) {
        entityNameDisplay = MessageManager.getInstance().formatMessage("ci.entity.training.form.title");
    } else {
        entityNameDisplay = MessageManager.getInstance().formatMessage("ci.entity.search.label.trainings") + " " + entityNameDisplay;
    }
%>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<c:set var="globalActionItemGroupId" value="CI_FOLDER_AG"></c:set>
<%@ include file="/core/header.jsp" %>
<jsp:include page="/cicore/common.jsp"/>
<jsp:include page="/CI_EntitySelect.jsp"/>

<c:set var="tabMenuGroupId" value="${tabGroupId}"></c:set>
<%@ include file="/core/tabheader.jsp" %>

<script type="text/javascript" src="<%=cisPath%>/trainingmgr/js/training.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form name="CITrainingForm" action="ciTraining.do" method="POST">
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

    <input type='hidden' name="dateOfBirth" value="<c:out value="${dateOfBirth}"/>">
    <input type='hidden' name="ciSchoolClass" value="<%=SysParmProvider.getInstance().getSysParm("CI_HOSPITAL_CLASS", "HOSPITAL")%>">
    <input type='hidden' name="ciInstitutionFlt" value="<%=SysParmProvider.getInstance().getSysParm("CI_CLNT_LIST4HOSP", "N")%>">
    <input type='hidden' name="CM_DEL_TRAINING" value="<%=SysParmProvider.getInstance().getSysParm("CM_DEL_TRAINING", "N")%>">

    <tr>
        <td>
            <oweb:panel panelContentId="panelContentForEducation"
                        panelTitleId="panelTitleIdForEducation"
                        panelTitleLayerId="TRAINING_LIST_LAYER">
                <tr>
                    <td>
                        <oweb:actionGroup actionItemGroupId="CI_TRAINING_GRID_AIG" cssColorScheme="gray" layoutDirection="horizontal"/>
                    </td>
                </tr>

                <tr>
                    <td colspan="6">
                        <c:set var="gridDisplayFormName" value="CITrainingForm" scope="request"/>
                        <c:set var="gridDisplayGridId" value="testgrid" scope="request"/>
                        <c:set var="datasrc" value="#testgrid1" scope="request"/>
                        <c:set var="gridDetailDivId" value="formfields" scope="request"/>
                        <%@ include file="/core/gridDisplay.jsp" %>
                    </td>
                </tr>

                <tr>
                    <td colspan="6">
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="isGridBased" value="true"/>
                            <jsp:param name="gridID" value="testgrid"/>
                            <jsp:param name="divId" value="formfields"/>
                            <jsp:param name="headerTextLayerId" value="TRAINING_DETAIL_LAYER"/>
                            <jsp:param name="removeFieldPrefix" value="true"/>
                            <jsp:param name="excludePageFields" value="true"/>
                            <jsp:param name="includeLayerIds" value="TRAINING_DETAIL_LAYER"/>
                            <jsp:param name="isLayerVisibleByDefault" value="true"/>
                        </jsp:include>
                    </td>
                </tr>

                <tr>
                    <td align="center">
                        <oweb:actionGroup actionItemGroupId="CI_TRAINING_AIG" cssColorScheme="blue" layoutDirection="horizontal"/>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>

    <%@ include file="/core/tabfooter.jsp" %>
    <jsp:include page="/core/footer.jsp"/>