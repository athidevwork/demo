<%--
  Description: contact page

  Author: Hong Yuan
  Date: Sep 22, 2005

  Revision Date    Revised By  Description
  ---------------------------------------------------
  05/15/2007       MLM         Added UI2 Changes
  06/29/2007       James       Added UI2 Changes
  08/30/2007       Kenney      remove UIStyleEdition;
                               change to panel tag;
                               change to compiledFormField page
  03/19/2009       kenney      Added Form Letter support for eCIS
  10/19/2010       wfu         111776 - Replaced hardcode string with resource definition
  01/20/2011       Michael Li  Issue:116335
  09/08/2011       parker      for iss123482.correct contact logic
  02/21/2017       dzhang      Issue 179102: Detail form should not be displayed when the grid is empty.
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
<c:set var="globalActionItemGroupId" value="CI_FOLDER_AG"></c:set>
<%@include file="/core/header.jsp"%>
<jsp:include page="/CI_EntitySelect.jsp"/>

<c:set var="tabMenuGroupId" value="${tabGroupId}"></c:set>
<%@ include file="/core/tabheader.jsp" %>

<%@ include file="/cicore/common.jsp" %>

<script type='text/javascript' src="<%=cisPath%>/contactmgr/js/contact.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form name="CIContactForm" action="ciContact.do" method="POST">
    <%@ include file="/cicore/commonFormHeader.jsp" %>
    <jsp:include page="/cicore/ciFolderCommon.jsp" />

        <tr valign="top">
            <td colspan="6" class="tabTitle">
                <oweb:message/>
            </td>
        </tr>

        <tr>
            <td class="tabTitle">
                <b><fmt:message key="ci.entity.search.label.contacts"/> <c:out value="${param.entityName}"/></b>
            </td>
        </tr>

        <%@ include file="/cicore/commonFilter.jsp" %>

        <tr>
            <td>
                <oweb:panel panelContentId="panelContentForContactList"
                            panelTitleId="panelTitleIdForContactList"
                            panelTitleLayerId="Contact_List_Grid_Header_Layer">

                    <tr>
                        <td>
                            <oweb:actionGroup actionItemGroupId="CI_CONTACT_GRID_AIG" cssColorScheme="gray"
                                              layoutDirection="horizontal">
                            </oweb:actionGroup>
                        </td>
                    </tr>

                    <tr>
                        <td width="100%">
                            <c:set var="gridDisplayFormName" value="CIContactForm" scope="request"/>
                            <c:set var="gridDisplayGridId" value="testgrid" scope="request"/>
                            <c:set var="datasrc" value="#testgrid1" scope="request"/>
                            <c:set var="gridDetailDivId" value="formfields" scope="request"/>
                            <%@ include file="/core/gridDisplay.jsp" %>
                        </td>
                    </tr>

                    <tr>
                        <td>
                            <%
                                String contactDetailTitle = MessageManager.getInstance().formatMessage("ci.entity.contactDetail.form.label");
                                String panelCaptionForDetail = ApplicationContext.getInstance().getProperty("contact.legend.detail", contactDetailTitle);
                            %>

                            <jsp:include page="/core/compiledFormFields.jsp">
                                <jsp:param name="isGridBased" value="true"/>
                                <jsp:param name="gridID" value="testgrid"/>
                                <jsp:param name="divId" value="formfields"/>
                                <jsp:param name="headerText" value="<%=panelCaptionForDetail%>"/>
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
                <oweb:actionGroup actionItemGroupId="CI_CONTACT_AIG"
                                  cssColorScheme="blue" layoutDirection="horizontal">
                </oweb:actionGroup>
            </td>
        </tr>

        <script type='text/javascript'>
            var addrIsSet = <%=((String)session.getAttribute("addrIsSet")).equals("true") ? "true" : "false"%>;
        </script>

<%@ include file="/core/tabfooter.jsp" %>
<jsp:include page="/core/footer.jsp"/>