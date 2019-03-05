<%@ page language="java" %>
<%@ page import="dti.ci.addressmgr.AddressFields" %>
<%--
  Description: address search / add page

  Author: hxy
  Date: July 5, 2005

  Revision Date    Revised By  Description
  --------------------------------------------------------------
  07/03/2006       Bhong        use ADDR_SEARCH_TOKEN
  07/10/2007       FWCH         Eliminated Choose County button
  07/05/2006       Mark         Added UI2 Changes
  08/01/2007       kenney       Added UI2 change: Make address list panel include detailed panel
  09/06/2007       Kenney       remove UIStyleEdition;
                                change to panel tag;
                                change to compiledFormField page
  11/07/2007       James        Move the descrition out of grid holder
  05/04/2010       Jacky        Issue #105600
  10/06/2010       wfu         111776: Replaced hardcode string with resource definition
  10/17/2012       kshen       Issue 136646.
  12/04/2012       ldong       Issue 136682.
  06/05/2014       bzhu        Issue 153402.
  06/10/2015       bzhu        Issue 163122: Move obsolete "<script for=" checkbox selection from JSP to JS.
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  09/07/2018       jdingle     195635 - Prevent existing record deletion.
  --------------------------------------------------------------

  (C) 2004 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<%@include file="/core/headerpopup.jsp" %>

<jsp:include page="/cicore/common.jsp"/>

<jsp:include page="/addressmgr/addressCommon.jsp"/>

<script type="text/javascript" src="<%=cisPath%>/addressmgr/js/maintainAddressSearchAdd.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form name="AddressSearchAddForm" action="ciAddressSearchAdd.do" method="POST">
    <tr>
        <td colspan="6">
            <oweb:message/>
        </td>
    </tr>

    <%@ include file="/cicore/commonFormHeader.jsp" %>

    <input type="hidden" name="<%=AddressFields.ENTITY_NAME%>" value="<%=(String) request.getAttribute(AddressFields.ENTITY_NAME)%>"/>
    <input type="hidden" name="<%=AddressFields.DUMMY_SOURCE_RECORD_ID%>" value="<%=(String) request.getAttribute(AddressFields.DUMMY_SOURCE_RECORD_ID)%>"/>
    <input type="hidden" name="<%=AddressFields.ORIG_SOURCE_RECORD_ID%>" value="<%=(String) request.getAttribute(AddressFields.ORIG_SOURCE_RECORD_ID)%>"/>
    <input type="hidden" name="<%=AddressFields.ORIG_ADDRESS_ID%>" value="<%=(String) request.getAttribute(AddressFields.ORIG_ADDRESS_ID)%>"/>
    <input type="hidden" name="<%=AddressFields.ORIG_ADDRESS_TYPE_CODE%>" value="<%=(String) request.getAttribute(AddressFields.ORIG_ADDRESS_TYPE_CODE)%>"/>
    <input type="hidden" name="<%=AddressFields.ORIG_SOURCE_TABLE_NAME%>" value="<%=(String) request.getAttribute(AddressFields.ORIG_SOURCE_TABLE_NAME)%>"/>
    <input type="hidden" name="<%=AddressFields.ALLOW_OTHER_CLIENT%>" value="<%=(String) request.getAttribute(AddressFields.ALLOW_OTHER_CLIENT)%>"/>
    <input type="hidden" name="<%=AddressFields.READ_ONLY%>" value="<%=(String) request.getAttribute(AddressFields.READ_ONLY)%>"/>
    <input type="hidden" name="allowDeleteExistRecordB" value="N"/>

    <tr>
        <td colspan="6">
            <oweb:panel panelContentId="panelContentForAddressList" hasTitle="false">
                <tr>
                    <td>
                        <span>
                            <b><fmt:message key="ci.entity.addressList.form.title"/>
                                <c:if test="${!empty entityName}">
                                    <c:out value="for ${entityName}"/>
                                </c:if>
                            </b>
                        </span>
                    </td>
                </tr>
                <tr>
                    <td>
                        <oweb:actionGroup actionItemGroupId="CI_ADDRS_SCH_ADD_GRID_AIG" cssColorScheme="gray" layoutDirection="horizontal"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="6">
                        <c:set var="gridDisplayFormName" value="AddressSearchAddForm" scope="request"/>
                        <c:set var="gridDisplayGridId" value="testgrid" scope="request"/>
                        <c:set var="gridDetailDivId" value="AddressDetail" scope="request"/>
                        <c:set var="datasrc" value="#testgrid1" scope="request"/>
                        <c:set var="selectable" value="false" scope="request"/>
                        <%@ include file="/core/gridDisplay.jsp" %>
                    </td>
                </tr>

                <tr>
                    <td colspan="6">
                        <fmt:message key="ci.entity.address.detail.label" var="addressDetailTitle" scope="request"/>
                        <% String addressDetailTitle = (String) request.getAttribute("addressDetailTitle"); %>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="isGridBased" value="true"/>
                            <jsp:param name="headerText" value="<%=addressDetailTitle%>"/>
                            <jsp:param name="divId" value="AddressDetail"/>
                            <jsp:param name="gridID" value="testgrid"/>
                            <jsp:param name="excludeAllLayers" value="true"/>
                        </jsp:include>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    <%-- render buttons --%>
    <tr align="center">
        <td colspan="10" align="center">
            <oweb:actionGroup actionItemGroupId="CI_ADDRS_SCH_ADD_FORM_AIG" cssColorScheme="blue" layoutDirection="horizontal"/>
        </td>
    </tr>

<jsp:include page="/core/footerpopup.jsp"/>
