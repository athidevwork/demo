<%--
  Description: certification page

  Author: Hong Yuan
  Date: Mar 22, 2006

  Revision Date    Revised By  Description
  ---------------------------------------------------
  05/15/2007       MLM         Added UI2 Changes
  07/03/2007       James       Added UI2 Changes
  08/31/2007       Jerry       Remove UIStyleEdition
  09/12/2007       James       Remove set datasrc line
  03/19/2009       kenney      Added Form Letter support for eCIS
  10/19/2010       wfu         111776 - Replaced hardcode string with resource definition
  11/09/2010       kshen       Added codes to handle the Delete button when a record is checked.
  01/20/2011       Michael Li  Issue:116335
  07/01/2013       hxk         Issue 141840
                               Move message tag before name for consistency.
  02/21/2017       dzhang      Issue 179102: Detail form should not be displayed when the grid is empty.
  12/25/2017       ylu         Issue 190396: fix secured column raise exception error when save.
  02/01/2018       dpang       Issue 191109: add system parameter to enable deleting existing certification record.
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  09/25/2018       dmeng       Add panelTitleLayerId
  ---------------------------------------------------
  (C) 2004 Delphi Technology, inc. (dti)
--%>
<%@ page language="java" import="dti.ci.helpers.ICICertificationConstants,
                                 dti.oasis.tags.WebLayer,
                                 org.apache.struts.Globals,
                                 org.apache.struts.taglib.html.Constants" %>
<%@ page import="dti.ci.helpers.ICIConstants" %>
<%@ page import="dti.oasis.util.BaseResultSet" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<c:set var="globalActionItemGroupId" value="CI_FOLDER_AG"></c:set>

<%@include file="/core/header.jsp"%>
<jsp:include page="/CI_EntitySelect.jsp"/>

<c:set var="tabMenuGroupId" value="${tabGroupId}"></c:set>
<%@ include file="/core/tabheader.jsp" %>
<jsp:include page="/cicore/common.jsp"/>

<script type='text/javascript' src="<%=csPath%>/js/csLoadNotes.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type='text/javascript' src="<%=cisPath%>/js/gridbtnclicks.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=cisPath%>/certificationmgr/js/maintainCertification.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form name="CICertificationForm" action="ciCertification.do" method="POST">
    <jsp:include page="/cicore/ciFolderCommon.jsp" />
<tr><td>
    <input type="hidden" name="dateOfBirth" value="<c:out value="${dateOfBirth}"/>"/>
    <input type="hidden" name="entityClassCode" value="<c:out value="${entityClassCode}"/>"/>
  <jsp:include page="/cicore/commonFormHeader.jsp"/>
</td></tr>
    <tr valign="top">
        <td colspan="6">
            <oweb:message/>
        </td>
    </tr>
    <tr valign="top">
        <td colspan="6" class="tabTitle">
            <b><fmt:message key="ci.entity.search.label.certifications"/>
                <c:out value="${entityName}"/>
            </b>
        </td>
    </tr>

    <%@ include file="/cicore/commonFilter.jsp" %>
<tr>
    <td>
        <oweb:panel panelContentId="panelContentForCertificationList"
                    panelTitleId="panelTitleIdForCertificationList"
                    panelTitleLayerId="CERTIFICATION_LIST_LAYER">
            <tr>
                <td>
                    <oweb:actionGroup actionItemGroupId="CI_CERTIFICATIO_GRID_AIG" cssColorScheme="gray"
                                      layoutDirection="horizontal">
                    </oweb:actionGroup>
                </td>
            </tr>
            <tr>
                <td width="100%">
                    <c:set var="gridDisplayFormName" value="CICertificationForm" scope="request"/>
                    <c:set var="gridDisplayGridId" value="testgrid" scope="request"/>
                    <c:set var="datasrc" value="#testgrid1" scope="request"/>
                    <c:set var="selectable" value="true"/>
                    <c:set var="gridDetailDivId" value="detailFieldset" scope="request"/>
                    <%@ include file="/core/gridDisplay.jsp" %>
                </td>
            </tr>
            <tr>
                <fmt:message key="ci.entity.detail.form.label" var="detailTitle" scope="request"/>
                <% String detailTitle = (String) request.getAttribute("detailTitle"); %>
                <td>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="gridID" value="testgrid"/>
                        <jsp:param name="divId" value="detailFieldset"/>
                        <jsp:param name="headerText" value="<%=detailTitle%>"/>
                        <jsp:param name="removeFieldPrefix" value="true"/>
                        <jsp:param name="excludeAllLayers" value="true"/>
                    </jsp:include>
                </td>
            </tr>
        </oweb:panel>
    </td>
</tr>

<script type='text/javascript'>
    var check_BoardName_CertType_EXCL= "<%=SysParmProvider.getInstance().getSysParm("CI_BORD_CERTYPE_VLD", "N")%>";
    var check_BoardDt_CertExp_Dt_EXCL= "<%=SysParmProvider.getInstance().getSysParm("CI_BORDT_CERTEXP_VLD", "N")%>";
    var check_BoardName_Date_REQ     = "<%=SysParmProvider.getInstance().getSysParm("CI_BORD_NAME_DT_REQ", "N")%>";
    var check_CertType_Date_REQ      = "<%=SysParmProvider.getInstance().getSysParm("CI_CERT_TYPE_DT_REQ", "N")%>";
    var sys_parm_ci_del_certification = "<%=SysParmProvider.getInstance().getSysParm("CI_DEL_CERTIFICATION", "N")%>";

</script>
<tr>
    <td colspan="6" align="center" style="padding-top:3px;padding-bottom:5px">
        <oweb:actionGroup actionItemGroupId="CI_CERTIFICATIO_AIG"
                          cssColorScheme="blue" layoutDirection="horizontal">
        </oweb:actionGroup>
    </td>
</tr>
<%@ include file="/core/tabfooter.jsp" %>

<jsp:include page="/core/footer.jsp"/>
