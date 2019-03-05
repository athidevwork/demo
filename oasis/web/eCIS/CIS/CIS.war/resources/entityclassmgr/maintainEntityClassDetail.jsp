<%@ page import="dti.ci.core.CIFields" %>
<%@ page import="dti.ci.helpers.ICIConstants" %>
<%@ page import="dti.ci.entityclassmgr.EntityClassFields" %>
<%@ page import="dti.oasis.http.RequestIds" %>
<%@ page language="java"%>

<%--
  Description:  Entity Class Add

  Author: Gerald C. Carney
  Date: Apr 1, 2004


  Revision Date    Revised By  Description
  ---------------------------------------------------
  04/22/2005       HXY         Added </FORM> tag.
  07/03/2007       James       Added UI2 Changes
  09/03/2007       Jerry       remove UIStyleEdition;
                               change to panel tag;
                               change to compiledFormField page
  09/14/2007       James       restored button position
  09/17/2007       James       restored panel title and set isTogglableTitle
                               false
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  10/30/2018       ylu         195835: per code review,
                               1) remove entityClassId,SQL_OPERATION_PROPERTY for Add & Modify Class page,
                               2) add entityClassId web field in webWB for Class Modify page, (Add Class page needn't this web field)
                               3) replace csVendorReqTaxid with csVendorReqTaxId
  ---------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<%--<jsp:useBean id="dataBean"  class="java.util.HashMap" scope="request"/>--%>

<%@include file="/core/headerpopup.jsp" %>
<jsp:include page="/cicore/common.jsp"/>

<%
String formAction = (String) request.getAttribute(EntityClassFields.FORM_ACTION_PROPERTY);
%>

<script type="text/javascript" src="<%=cisPath%>/entityclassmgr/js/maintainEntityClassDetail.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<!-- Content -->
<FORM action="<%=formAction%>" method="POST">
    <jsp:include page="/cicore/commonFormHeader.jsp"/>
    <html:hidden value="<%=(String) request.getAttribute(CIFields.ENTITY_ID)%>" property="<%=CIFields.ENTITY_ID%>"/>
    <html:hidden value="<%=(String) request.getAttribute(CIFields.ENTITY_HAS_TAX_ID_EXISTS)%>" property="<%=CIFields.ENTITY_HAS_TAX_ID_EXISTS%>" />
    <html:hidden value="<%=(String) request.getAttribute(EntityClassFields.ADD_WITH_ERROR)%>" property="<%=EntityClassFields.ADD_WITH_ERROR%>"/>
    <%--if system parameter indicates to do so, enforce that SSN or TIN is filled in if we have a VENDOR classification--%>
    <input type="hidden" name="csVendorReqTaxId" value="<%=SysParmProvider.getInstance().getSysParm("CS_VENDOR_REQ_TAXID", "N")%>">
    <tr>
        <td colspan=6>
            <oweb:message/>
        </td>
    </tr>

    <tr>
        <td>
            <fmt:message key="ci.entity.classification.form.label" var="classificationTitle" scope="request"/>
            <% String classificationTitle = (String) request.getAttribute("classificationTitle"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="isTogglableTitle" value="false"/>
                <jsp:param name="headerText" value="<%=classificationTitle%>"/>
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="divId" value="formfields"/>
                <jsp:param name="excludeAllLayers" value="true"/>
            </jsp:include>
        </td>
    </tr>

    <tr>
        <td align="center" colspan="6">
            <oweb:actionGroup actionItemGroupId="CI_ENTITY_CLASS_AIG"
                              cssColorScheme="blue" layoutDirection="horizontal">
            </oweb:actionGroup>
        </td>
    </tr>

<jsp:include page="/core/footerpopup.jsp" />