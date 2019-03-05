<%@ page language="java" %>
<%@ page import="dti.oasis.messagemgr.MessageManager" %>
<%@ page import="dti.ci.addressmgr.AddressFields" %>

<%--
  Description:  Address List

  Author: Gerald C. Carney
  Date: Jan 23, 2004


  Revision Date    Revised By  Description
  --------------------------------------------------------------------
  04/12/2005       HXY         Added logic for controlling grid size.
  09/21/2006       ligj        Issue #62554
  05/15/2007       MLM         Added UI2 Changes
  06/29/2007       James       Added UI2 Changes
  08/30/2007       Kenney      remove UIStyleEdition;
                               change to panel tag;
                               change to compiledFormField page
  10/16/2007       kshen       Changed codes to get message from ApplicationResources
  11/27/2008       Leo         For issue 88568.
  03/19/2009       kenney      Added Form Letter support for eCIS
  08/09/2011       Michael     for issue 101250
  05/02/2013       kshen       Issue 141148.
  07/01/2013       hxk         Issue 141840
                               Move message tag before name for consistency.
  07/12/2013       Elvin       Issue 145303: exclude effective_to date = today when filtering active
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  10/16/2018       Elvin       Issue 195835: grid replacement
  --------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="/WEB-INF/fmt.tld" %>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="entityResultList" class="java.util.ArrayList" scope="request"/>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>

<%
    String entityName = (String) request.getAttribute(AddressFields.ENTITY_NAME);
    if (StringUtils.isBlank(entityName)) {
        entityName = "";
    } else {
        entityName = "for " + entityName;
    }
    String formTitle = MessageManager.getInstance().formatMessage("ci.entity.addressesList.info.formTitle", new String[]{ entityName });
%>
<c:set var="globalActionItemGroupId" value="CI_FOLDER_AG"></c:set>
<%@ include file="/core/header.jsp" %>
<jsp:include page="/cicore/common.jsp"/>
<jsp:include page="/CI_EntitySelect.jsp"/>
<c:set var="tabMenuGroupId" value="${tabGroupId}"></c:set>
<%@ include file="/core/tabheader.jsp" %>

<script type="text/javascript" src="<%=cisPath%>/addressmgr/js/openAddressRoleChgPopup.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=cisPath%>/addressmgr/addresslistmgr/js/maintainAddressList.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<oweb:constant constantClass="dti.ci.addressmgr.AddressFields"/>
<FORM  name="CIAddressListForm" action="ciAddressList.do" method="POST">
    <tr>
        <td colspan="6" class="tabTitle">
            <oweb:message/>
        </td>
    </tr>

    <tr>
        <td colspan='6' class="tabTitle">
            <b><%=formTitle%></b>
        </td>
    </tr>

    <%@ include file="/cicore/commonFormHeader.jsp" %>
    <jsp:include page="/cicore/ciFolderCommon.jsp" />

    <input type="hidden" name="<%=AddressFields.ENTITY_LOCK_FLAG%>" value="<%=(String) request.getAttribute(AddressFields.ENTITY_LOCK_FLAG)%>" />
    <input type="hidden" name="<%=AddressFields.CS_ALLOWADDLOCKEDPOL%>" value="<%=SysParmProvider.getInstance().getSysParm(AddressFields.CS_ALLOWADDLOCKEDPOL, "N")%>" />
    <input type="hidden" name="<%=AddressFields.CI_ADDR_PCT_PRAC_FLD%>" value="<%=SysParmProvider.getInstance().getSysParm(AddressFields.CI_ADDR_PCT_PRAC_FLD, "")%>" />
    <input type="hidden" name="<%=AddressFields.CI_ADDR_PCT_PRAC_TYP%>" value="<%=SysParmProvider.getInstance().getSysParm(AddressFields.CI_ADDR_PCT_PRAC_TYP, "")%>" />
    <input type="hidden" name="<%=AddressFields.CI_ADDR_PCT_PRAC_MSG%>" value="<%=SysParmProvider.getInstance().getSysParm(AddressFields.CI_ADDR_PCT_PRAC_MSG, "ERROR")%>" />
    <input type="hidden" name="<%=AddressFields.CS_SHOWLOCKEDPOL%>" value="<%=SysParmProvider.getInstance().getSysParm(AddressFields.CS_SHOWLOCKEDPOL, "N")%>" />

    <%@ include file="/cicore/commonFilter.jsp" %>

    <tr>
        <td colspan="6">
            <oweb:panel panelContentId="panelContentForAddress" panelTitleId="panelTitleIdForAddress" panelTitleLayerId="Address_List_Grid_Header_Layer">
                <tr>
                    <td>
                        <oweb:actionGroup actionItemGroupId="CI_ADDR_LIST_GRID_AIG" cssColorScheme="gray" cssWidthInPX="120" layoutDirection="horizontal"/>
                    </td>
                </tr>

                <tr>
                    <td colspan="6"><b><fmt:message key="ci.entity.addressesList.info.changeView"/></b></td>
                </tr>
                <tr>
                    <td colspan="6">
                        <c:set var="gridDisplayFormName" value="CIAddressListForm" scope="request"/>
                        <c:set var="gridDisplayGridId" value="addressListGrid" scope="request"/>
                        <c:set var="datasrc" value="#addressListGrid1" scope="request"/>
                        <c:set var="gridDetailDivId" value="addressDetail" scope="request"/>
                        <%@ include file="/core/gridDisplay.jsp" %>
                    </td>
                </tr>
                <tr>
                    <td colspan="6">
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="isGridBased" value="true"/>
                            <jsp:param name="gridID" value="addressListGrid"/>
                            <jsp:param name="includeLayerIds" value="Address_List_Grid_Header_Layer_USA_read_Only"/>
                            <jsp:param name="headerTextLayerId" value="Address_List_Grid_Header_Layer_USA_read_Only"/>
                            <jsp:param name="removeFieldPrefix" value="true"/>
                            <jsp:param name="isLayerVisibleByDefault" value="true"/>
                            <jsp:param name="divId" value="addressDetail"/>
                            <jsp:param name="excludePageFields" value="true"/>
                        </jsp:include>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>

     <tr>
        <td align="center">
            <oweb:actionGroup actionItemGroupId="CI_ADDR_LIST_AIG" cssColorScheme="blue" layoutDirection="horizontal"/>
        </td>
    </tr>

    <%@ include file="/core/tabfooter.jsp" %>
    <jsp:include page="/core/footer.jsp"/>
