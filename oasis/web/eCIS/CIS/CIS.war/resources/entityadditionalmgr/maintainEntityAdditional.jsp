<%@ page import="dti.oasis.struts.ActionHelper,
                 dti.oasis.util.StringUtils,
                 dti.oasis.tags.OasisFormField,
                 dti.ci.helpers.ICIConstants,
                 org.apache.struts.taglib.html.Constants,
                 dti.ci.entityadditionalmgr.EntityAdditionalFields" %>
<%@ page import="dti.oasis.util.*" %>
<%@ page import="dti.oasis.app.ApplicationContext" %>
<%@ page import="dti.oasis.messagemgr.MessageManager" %>
<%@ page language="java" %>
<%--
  Description: Maintain  EntityAdditional

  Author: Michael
  Date: November 08, 2010


  Revision Date    Revised By  Description
  ---------------------------------------------------
  02/11/2011      Michael      for issue 113889
  07/01/2013      hxk          Issue 141840
                               Move message tag before name for consistency.
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  10/16/2018       dpang       195835 - Grid replacement.
  ---------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<%
String entityNameDisplay = (String) request.getAttribute(EntityAdditionalFields.ENTITY_NAME_PROPERTY);
if (StringUtils.isBlank(entityNameDisplay)) {
  entityNameDisplay = MessageManager.getInstance().formatMessage("ci.entity.additional.form.title");
}
else {
  entityNameDisplay = MessageManager.getInstance().formatMessage("ci.entity.search.label.additional") + " " + entityNameDisplay;
}

String message = (String) request.getAttribute(EntityAdditionalFields.MSG_PROPERTY);
if (StringUtils.isBlank(message) || message.equalsIgnoreCase("null")) {
  message = "";
}
%>

<c:set var="globalActionItemGroupId" value="CI_FOLDER_AG"></c:set>

<%@include file="/core/header.jsp" %>
<jsp:include page="/CI_EntitySelect.jsp"/>
<jsp:include page="/cicore/common.jsp"/>
<c:set var="tabMenuGroupId" value="${tabGroupId}"></c:set>
<%@ include file="/core/tabheader.jsp" %>

<script language="javascript" src="entityadditionalmgr/js/maintainEntityAdditional.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form name="frmGrid" action="ciEntityAdditional.do" method="POST">
    <%@ include file="/cicore/commonFormHeader.jsp" %>

    <jsp:include page="/cicore/ciFolderCommon.jsp" />

     <html:hidden value="<%=(String)request.getAttribute(ICIConstants.SQL_OPERATION_PROPERTY)%>"
                 property="<%=ICIConstants.SQL_OPERATION_PROPERTY%>"/>
    <tr valign="top">
        <td colspan="6" class="tabTitle">
            <oweb:message/>
        </td>
    </tr>
    <tr valign="top">
        <td colspan="6" class="tabTitle">
            <b><%=entityNameDisplay%>
            </b>
        </td>
    </tr>

    <tr>
        <fmt:message key="ci.entity.additional.form.title" var="entityAdditionalTitle" scope="request"/>
        <% String entityAdditionalTitle = (String) request.getAttribute("entityAdditionalTitle"); %>
        <td colspan="6">
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="divId" value="EntityAdditional"/>
                <jsp:param name="headerText" value="<%=entityAdditionalTitle%>"/>
                <jsp:param name="excludeAllLayers" value="true"/>
                <jsp:param name="actionItemGroupId" value="CI_ENTITY_ADDITIONAL_AIG"/>
            </jsp:include>
        </td>
    </tr>

<%@ include file="/core/tabfooter.jsp" %>
<jsp:include page="/core/footer.jsp" />