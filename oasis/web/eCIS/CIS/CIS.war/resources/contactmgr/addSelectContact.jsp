<%--
  Description: The jsp page for contactPopup page.

  Author: kshen
  Date: Jan 11, 2013

  Revision Date    Revised By  Description
  ---------------------------------------------------
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  ---------------------------------------------------
  (C) 2004 Delphi Technology, inc. (dti)
--%>
<%@ page language="java" import="dti.oasis.tags.WebLayer" %>
<%@ page import="dti.ci.helpers.ICIConstants" %>
<%@ page import="dti.oasis.util.BaseResultSet"  %>
<%@ page import="dti.oasis.app.ApplicationContext" %>
<%@ page import="dti.oasis.messagemgr.MessageManager" %>
<%@ page import="dti.ci.contactmgr.ContactFields" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<%@include file="/core/headerpopup.jsp"%>

<%@ include file="/cicore/common.jsp" %>

<script type='text/javascript' src="<%=cisPath%>/contactmgr/js/addSelectContactCommon.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type='text/javascript' src="<%=cisPath%>/contactmgr/js/addSelectContact.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form name="CIContactForm" action="addSelectContact.do" method="POST">
    <%@ include file="/cicore/commonFormHeader.jsp" %>

    <tr valign="top">
        <td colspan="6">
            <oweb:message/>
        </td>
    </tr>

    <input type="hidden" name="pk" value="<c:out value="${pk}"/>"/>
    <input type="hidden" name="eventName" value="<c:out value="${eventName}"/>">
    <input type="hidden" name="contactIdFieldName" value="<c:out value="${contactIdFieldName}"/>">
    <input type="hidden" name="contactNameFiledName" value="<c:out value="${contactNameFiledName}"/>">

    <tr>
        <td>
            <oweb:panel panelContentId="panelContentForContactList"
                        panelTitleId="panelTitleIdForContactList"
                        panelTitleLayerId="CI_CONTACT_LIST_GRID">

                <tr>
                    <td>
                        <oweb:actionGroup actionItemGroupId="CI_ADDSEL_CONT_GRID_AIG" cssColorScheme="gray"
                                          layoutDirection="horizontal">
                        </oweb:actionGroup>
                    </td>
                </tr>

                <tr>
                    <td>
                        <c:set var="gridDisplayFormName" value="CIContactForm" scope="request"/>
                        <c:set var="gridDisplayGridId" value="testgrid" scope="request"/>
                        <c:set var="datasrc" value="#testgrid1" scope="request"/>
                        <c:set var="gridDetailDivId" value="formfields" scope="request"/>
                        <%@ include file="/core/gridDisplay.jsp" %>
                    </td>
                </tr>

                <tr>
                    <td>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="isGridBased" value="true"/>
                            <jsp:param name="gridID" value="testgrid"/>
                            <jsp:param name="divId" value="formfields"/>
                            <jsp:param name="headerTextLayerId" value="CI_CONTACT_DETAIL"/>
                            <jsp:param name="excludePageFields" value="true"/>
                            <jsp:param name="removeFieldPrefix" value="true"/>
                            <jsp:param name="includeLayerIds" value="CI_CONTACT_DETAIL"/>
                            <jsp:param name="isLayerVisibleByDefault" value="true"/>
                        </jsp:include>
                    </td>
                </tr>

                <tr>
                    <td colspan="6" align="center" style="padding-top:3px;padding-bottom:5px">
                        <oweb:actionGroup actionItemGroupId="CI_ADDSEL_CONT_AIG"
                                          cssColorScheme="blue" layoutDirection="horizontal">
                        </oweb:actionGroup>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>

<jsp:include page="/core/footerpopup.jsp"/>