<%@ page language="java"%>
<%@ page import="dti.oasis.messagemgr.MessageManager" %>
<%@ page import="dti.ci.addressmgr.AddressFields" %>

<%--
  This is vendor address page. 

  Author: Hong Yuan
  Date: Apr 26, 2005


  Revision Date    Revised By  Description
  ---------------------------------------------------------------
  05/15/2007       MLM         Added UI2 Changes
  07/04/2007       James       Added UI2 Changes
  08/13/2007       FWCH        Added reference to asynchttp.js
  08/20/2007       Leo         Added zip lookup functionality
  09/03/2007       Kenney      remove UIStyleEdition;
                               change to panel tag;
                               change to compiledFormField page
  12/02/2008       kshen       Added hidden field for system parameter "ZIP_CODE_ENABLE",
                               "ZIP_OVERRIDE_ADDR", and "CS_SHOW_ZIPCD_LIST".
  03/19/2009       kenney      Added Form Letter support for eCIS
  07/01/2013       hxk         Issue 141840
                               1)  Add common.jsp so we include security.
                               2)  Add message tag.
  08/25/2014       Elvin       Issue 155305: add system parameter COUNTRY_CODE_CONFIG
  03/05/2018       dzhang      Issue 109177: vendor address refactor
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  ---------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<%
    String entityNameDisplay = (String) request.getAttribute(AddressFields.ENTITY_NAME);
    if (StringUtils.isBlank(entityNameDisplay)) {
        entityNameDisplay = "";
    } else {
        entityNameDisplay = "for " + entityNameDisplay;
    }
    String pageHeaderMessage = MessageManager.getInstance().formatMessage("ci.entity.vendor.vendorAddress.pageHeaderMessage", new String[]{ entityNameDisplay });
%>

<c:set var="globalActionItemGroupId" value="CI_FOLDER_AG"></c:set>
<%@include file="/core/header.jsp" %>
<jsp:include page="/CI_EntitySelect.jsp"/>
<jsp:include page="/cicore/common.jsp"/>

<jsp:include page="/addressmgr/addressCommon.jsp"/>

<c:set var="tabMenuGroupId" value="${tabGroupId}"></c:set>
<%@ include file="/core/tabheader.jsp" %>

<script type="text/javascript" src="<%=cisPath%>/vendormgr/vendoraddressmgr/js/vendorAddress.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<FORM action="ciVendorAddress.do" method="POST">
    <tr>
        <td>
            <oweb:message/>
        </td>
    </tr>
    <tr>
        <td colspan="6" class="tabTitle">
            <b><%=pageHeaderMessage%></b>
        </td>
    </tr>

    <%@ include file="/cicore/commonFormHeader.jsp" %>
    <jsp:include page="/cicore/ciFolderCommon.jsp" />

    <tr>
        <td colspan="6">
            <fmt:message key="ci.entity.vendor.vendorAddress.formTitle" var="vendorAddressTitle" scope="request"/>
            <% String vendorAddressTitle = (String) request.getAttribute("vendorAddressTitle"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="divId" value="VendorAddress"/>
                <jsp:param name="headerText" value="<%=vendorAddressTitle%>"/>
                <jsp:param name="excludeAllLayers" value="true"/>
            </jsp:include>
        </td>
    </tr>

    <tr>
        <td colspan="6" align="center">
            <oweb:actionGroup actionItemGroupId="CI_VENDOR_ADDRS_AIG" cssColorScheme="blue" layoutDirection="horizontal"/>
        </td>
    </tr>

<%@ include file="/core/tabfooter.jsp" %>

<jsp:include page="/core/footer.jsp"/>

