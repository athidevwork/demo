<%@ page import="dti.ci.helpers.ICIConstants" %>
<%@ page import="dti.oasis.http.Module" %>
<%@ page language="java" %>
<%--
  Description: License

  Author: bhong
  Date: Apr 17, 2006


  Revision Date    Revised By  Description
  ---------------------------------------------------
  05/15/2007       MLM         Added UI2 Changes
  07/03/2007       James       Added UI2 Changes
  08/31/2007       Jerry       Remove UIStyleEdition
  09/26/2008       Larry       Issue 86826 DB connection leakage change
  03/19/2009       kenney      Added Form Letter support for eCIS
  10/19/2010       wfu         111776 - Replaced hardcode string with resource definition
  11/09/2010       kshen       Added codes to handle the Delete button when a record is checked.
  01/20/2011       Michael Li  Issue:116335
  07/01/2013       hxk         Issue 141840
                               Move message tag before name for consistency.
  02/21/2017       dzhang      Issue 179102: Detail form should not be displayed when the grid is empty.
  12/25/2017       ylu         Issue 190396: fix secured column save encrypt unmeanningful data to DB problem
  02/01/2018       dpang       Issue 191109: add system parameter to enable deleting existing license record.
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  10/10/2018       dmeng       Issue 195835: grid replacement.
  ---------------------------------------------------
  (C) 2004 Delphi Technology, inc. (dti)
--%>
<!--load some libs-->
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<c:set var="globalActionItemGroupId" value="CI_FOLDER_AG"></c:set>
<%@ include file="/core/header.jsp" %>
<jsp:include page="/CI_EntitySelect.jsp"/>
<c:set var="tabMenuGroupId" value="${tabGroupId}"></c:set>
<%@ include file="/core/tabheader.jsp" %>
<jsp:include page="/cicore/common.jsp"/>

<script type='text/javascript' src="<%=csPath%>/js/csLoadNotes.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type='text/javascript' src="js/gridbtnclicks.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="licensemgr/js/maintainLicense.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<!--form-->
<form name="CILicenseForm" action="ciLicense.do" method="POST">
    <input type="hidden" name="ci_license_valid_b" value="<%=SysParmProvider.getInstance().getSysParm("CI_DEF_LICENSE_VALID", "")%>">
    <jsp:include page="/cicore/commonFormHeader.jsp"/>
    <jsp:include page="/cicore/ciFolderCommon.jsp" />
    <tr valign="top">
        <td colspan="6">
            <oweb:message/>
        </td>
    </tr>
    <tr valign="top">
        <td colspan="6" class="tabTitle">
            <b><fmt:message key="ci.entity.search.label.license"/>
                <c:out value="${entityName}"/>
            </b>
        </td>
    </tr>

    <%@ include file="/cicore/commonFilter.jsp" %>
    <tr>
        <td>
            <oweb:panel panelContentId="panelContentForLicenseList"
                       panelTitleId="panelTitleIdForLicenseList" panelTitleLayerId="CI_LICENSE_GH">
                <tr>
                    <td>
                        <oweb:actionGroup actionItemGroupId="CI_LICENSE_GRID_AIG" cssColorScheme="gray"
                                          layoutDirection="horizontal">
                        </oweb:actionGroup>
                    </td>
                </tr>
                <tr>
                    <td colspan="6" align=center>
                        <c:set var="gridDisplayFormName" value="CILicenseForm" scope="request"/>
                        <c:set var="gridDisplayGridId" value="testgrid" scope="request"/>
                        <c:set var="datasrc" value="#licenseGrid1" scope="request"/>
                        <c:set var="cacheResultSet" value="false"/>
                        <c:set var="selectable" value="true"/>
                        <c:set var="gridDetailDivId" value="formfields" scope="request"/>
                        <%@ include file="/core/gridDisplay.jsp" %>
                    </td>
                </tr>
                <tr>
                    <fmt:message key="ci.entity.detail.form.label" var="detailTitle" scope="request"/>
                    <% String detailTitle = (String) request.getAttribute("detailTitle"); %>
                    <td>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="dataBeanName" value="gridDataBean"/>
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
        <td colspan="6" align="center" style="padding-top:3px;padding-bottom:5px">
            <oweb:actionGroup actionItemGroupId="CI_LICENSE_AIG"
                              cssColorScheme="blue" layoutDirection="horizontal">
            </oweb:actionGroup>
        </td>
    </tr>

<script type="text/javascript">
    var sys_parm_ci_del_license = "<%=SysParmProvider.getInstance().getSysParm("CI_DEL_LICENSE", "N")%>";
</script>
<%@ include file="/core/tabfooter.jsp" %>
<jsp:include page="/core/footer.jsp"/>
