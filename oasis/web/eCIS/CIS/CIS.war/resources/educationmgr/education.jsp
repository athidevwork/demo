<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="dti.ci.educationmgr.EducationFields"%>
<%@ page import="dti.ci.helpers.ICIConstants" %>
<%@ page import="dti.oasis.util.StringUtils" %>
<%@ page import="dti.oasis.app.ApplicationContext" %>
<%@ page import="dti.oasis.util.BaseResultSet" %>
<%@ page import="dti.oasis.messagemgr.MessageManager" %>
<%@ page language="java" %>
<%--
  Description: Education
  
  Author: Jerry
  Date: May 12, 2006
  
  
  Revision Date    Revised By  Description
  ---------------------------------------------------
  05/15/2007       MLM         Added UI2 Changes
  06/29/2007       James       Added UI2 Changes
  08/29/2007       Jerry       Remove UIStyleEdition
  02/05/2009       hxk         Added dateOfDeath
  03/19/2009       kenney      Added Form Letter support for eCIS
  02/09/2010       kenney      Modfied for issue 104106
  11/09/2010       kshen       Added codes to handle the Delete button when a record is checked.
  01/03/2013       Elvin       Issue 150900: use Entity Select Search window instead of Select Institution window
  07/14/2016       dpang       Issue 176370: add csCommon.js for setting initial values when adding education.
  12/22/2017       ylu         Issue 190396: fix secured column save encrypt unmeanningful data to DB problem
  02/01/2018       dpang       Issue 191109: add system parameter to enable deleting existing education record.
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  09/07/2018       Elvin       Issue 194134: delete hidden field which reading values from datasrc (also useless now)
  09/27/2018       dzou       Grid replacement
  ---------------------------------------------------
  (C) 2004 Delphi Technology, inc. (dti)
--%>
<!--load some libs-->
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>

<%
    String entityNameDisplay = (String) request.getAttribute(ICIConstants.ENTITY_NAME_PROPERTY);
    if (StringUtils.isBlank(entityNameDisplay)) {
        entityNameDisplay = MessageManager.getInstance().formatMessage("ci.entity.education.form.title");
    } else {
        entityNameDisplay = MessageManager.getInstance().formatMessage("ci.entity.search.label.education") + " " + entityNameDisplay;
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

<%-- include some js --%>
<script type='text/javascript' src="<%=cisPath%>/js/gridbtnclicks.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=cisPath%>/educationmgr/js/education.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<!--form-->
<form name="CIEducationForm" action="ciEducation.do" method="POST">
    <%@ include file="/cicore/commonFormHeader.jsp" %>
    <jsp:include page="/cicore/ciFolderCommon.jsp" />

    <tr valign="top">
        <td colspan="6" class="tabTitle">
            <oweb:message/>
        </td>
    </tr>

    <input type='hidden' name="educationProfile_dateOfBirth" value="<c:out value="${dateOfBirth}"/>">
    <input type='hidden' name="educationProfile_dateOfDeath" value="<c:out value="${dateOfDeath}"/>">
    <input type='hidden' name="ciSchoolClass" value="<%=SysParmProvider.getInstance().getSysParm("CI_SCHOOL_CLASS", "MEDSCHOOL")%>">
    <input type='hidden' name="ciInstitutionFlt" value="<%=SysParmProvider.getInstance().getSysParm("CIW_EDU_INST_FLT", "N")%>">
    <tr valign="top">
        <td colspan="6" class="tabTitle">
            <b><%=entityNameDisplay%></b>
        </td>
    </tr>
    <tr>
        <td>

            <oweb:panel panelContentId="panelContentForEducation"
                        panelTitleId="panelTitleIdForEducation" panelTitleLayerId="Education_Info_Grid_Header_Layer">
    <tr>
        <td>
            <oweb:actionGroup actionItemGroupId="CI_EDUCATION_GRID_AIG" cssColorScheme="gray"
                              layoutDirection="horizontal">
            </oweb:actionGroup>
            <c:set var="gridDisplayFormName" value="CIEducationForm" scope="request"/>
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
                <jsp:param name="removeFieldPrefix" value="true"/>
                <jsp:param name="excludeAllLayers" value="true"/>
            </jsp:include>
        </td>
    </tr>
    </oweb:panel>
    </td>
    </tr>


<script type="text/javascript">
    var sys_parm_ci_del_education = "<%=SysParmProvider.getInstance().getSysParm("CI_DEL_EDUCATION", "N")%>";
</script>
<tr>
    <td colspan="6" align="center" style="padding-top:3px;padding-bottom:5px">
        <oweb:actionGroup actionItemGroupId="CI_EDUCATION_AIG"
                          cssColorScheme="blue" layoutDirection="horizontal">
        </oweb:actionGroup>
    </td>
</tr>

<%@ include file="/core/tabfooter.jsp" %>

<jsp:include page="/core/footer.jsp"/>