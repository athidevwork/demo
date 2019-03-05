<%@ page import="dti.oasis.tags.XMLGridHeader" %>
<%@ page import="dti.oasis.util.BaseResultSet" %>
<%@ page language="java" %>
<%--
  Description: Expert Witness

  Author: bhong
  Date: Feb 01, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  06/19/2007       GCC         Added UI2 Changes
  09/04/2007       Jerry       remove UIStyleEdition;
                               change to panel tag;
  09/12/2007       James       Remove set pageBean line
  03/19/2009       kenney      Added Form Letter support for eCIS
  09/01/2009       hxk         Add message tag to pick up msgs from msg mgr.
  10/08/2010       wfu         111776: Replaced hardcode string with resource definition
  02/08/2018       dpang       191377: Split expertWitness.jsp to two jsps to avoid 'exceeding the 65535 bytes limit' error.
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  10/19/2018       kshen       195835. CIS grid replacement.
  -----------------------------------------------------------------------------
  (C) 2007 Delphi Technology, inc. (dti)
--%>
<!--load some libs-->
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="addressListDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="addressListHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="phoneListDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="phoneListHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="educationListDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="educationListHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<c:set var="globalActionItemGroupId" value="CI_FOLDER_AG"></c:set>

<%@ include file="/core/header.jsp" %>
<%-- add message tag --%>
<tr><td><oweb:message/></td></tr>
<jsp:include page="/CI_EntitySelect.jsp"/>

<c:set var="tabMenuGroupId" value="${tabGroupId}"></c:set>
<%@ include file="/core/tabheader.jsp" %>

<%@ include file="/cicore/common.jsp" %>
<%-- include some js --%>
<script type='text/javascript' src="<%=cisPath%>/js/gridbtnclicks.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=cisPath%>/expertwitnessmgr/js/expertWitness.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=csPath%>/js/csLoadNotes.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<%-- Person --%>
<form name="CIPersonForm" action="ciExpertWitness.do" method="POST">
    <%@ include file="/cicore/commonFormHeader.jsp" %>
    <jsp:include page="/cicore/ciFolderCommon.jsp" />

<tr>
    <td>
        <fmt:message key="ci.entity.type.person" var="personTitle" scope="request"/>
        <% String personTitle = (String) request.getAttribute("personTitle"); %>
        <jsp:include page="/core/compiledFormFields.jsp">
            <jsp:param name="isGridBased" value="false"/>
            <jsp:param name="divId" value="CIPersonForm"/>
            <jsp:param name="headerText" value="<%=personTitle%>"/>
            <jsp:param name="excludeAllLayers" value="true"/>
            <jsp:param name="actionItemGroupId" value="CI_EXPWIT_AIG"/>
        </jsp:include>
    </td>
</tr>

<!-- Address -->
    <tr>
        <td>
            <oweb:panel panelContentId="panelContentForAddress"
                        panelTitleId="panelTitleIdForAddressID"
                        panelTitleLayerId="EXPWTN_ADDRESS_GRIDHEADER">
            <tr>
                <td width="100%">
                    <c:set var="gridDisplayFormName" value="CIPersonForm" scope="request"/>
                    <c:set var="gridDisplayGridId" value="addressGrid" scope="request"/>
                    <c:set var="datasrc" value="#addressGrid1" scope="request"/>
                    <%
                        BaseResultSet dataBean = addressListDataBean;
                        XMLGridHeader gridHeaderBean = addressListHeaderBean;
                    %>
                    <%@ include file="/core/gridDisplay.jsp" %>
                </td>
            </tr>
            </oweb:panel>
        </td>
    </tr>

<!-- Phone -->
    <tr>
        <td>
            <oweb:panel panelContentId="panelContentForPhone"
                        panelTitleId="panelTitleIdForPhoneID"
                        panelTitleLayerId="EXPWTN_PHONE_GRIDHEADER">
            <tr>
                <td width="100%">
                    <c:set var="gridDisplayFormName" value="CIPersonForm" scope="request"/>
                    <c:set var="gridDisplayGridId" value="phoneGrid" scope="request"/>
                    <c:set var="datasrc" value="#phoneGrid1" scope="request"/>
                    <%
                        BaseResultSet dataBean = phoneListDataBean;
                        XMLGridHeader gridHeaderBean = phoneListHeaderBean;
                    %>
                    <%@ include file="/core/gridDisplay.jsp" %>
                </td>
            </tr>
            </oweb:panel>
        </td>
    </tr>

<!-- Education -->
    <tr>
        <td>
            <oweb:panel panelContentId="panelContentForEducation"
                        panelTitleId="panelTitleIdForEducationID"
                        panelTitleLayerId="EXPWTN_EDUCATION_GRIDHEADER">
            <tr>
                <td width="100%">
                    <c:set var="gridDisplayFormName" value="CIPersonForm" scope="request"/>
                    <c:set var="gridDisplayGridId" value="educationGrid" scope="request"/>
                    <c:set var="datasrc" value="#educationGrid1" scope="request"/>
                    <%
                        BaseResultSet dataBean = educationListDataBean;
                        XMLGridHeader gridHeaderBean = educationListHeaderBean;
                    %>
                    <%@ include file="/core/gridDisplay.jsp" %>
                </td>
            </tr>
            </oweb:panel>
        </td>
    </tr>

<jsp:include page="/expertwitnessmgr/expertWitnessContentPart2.jsp"/>

<%@ include file="/core/tabfooter.jsp" %>
<jsp:include page="/core/footer.jsp"/>
