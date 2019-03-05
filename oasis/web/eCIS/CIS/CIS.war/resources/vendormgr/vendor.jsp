<%@ page language="java"%>
<%@ page import="dti.ci.helpers.ICIConstants" %>
<%@ page import="dti.oasis.util.StringUtils" %>
<%@ page import="dti.oasis.util.*" %>
<%@ page import="dti.oasis.tags.XMLGridHeader" %>
<%@ page import="dti.oasis.messagemgr.MessageManager" %>

<%--
  Description: vendor page

  Author: Gerald C. Carney
  Date: Apr 15, 2004


  Revision Date    Revised By  Description
  --------------------------------------------------------------------------
  04/13/2005       HXY         Added logic for controlling grid size.
  05/02/2005       HXY         Set request attribute beanName.
  04/25/2006       Bhong       Add hidden field vendor_vendorPK
  05/15/2007       MLM         Added UI2 Changes
  07/04/2007       James       Added UI2 Changes
  09/03/2007       Kenney      remove UIStyleEdition;
                               change to panel tag;
                               change to compiledFormField page
  03/19/2009       kenney      Added Form Letter support for eCIS
  04/28/2009       Fred        Removed paymentInfoGrid
  09/30/2010       wfu         111776: Replaced hardcode string with resource definition.
  05/13/2011       kshen       Added hidden field CI_CHK_BANK_FOR_EFT to the page.
  07/01/2013       hxk         Issue 141840
                               1)  Add common.jsp so we include security.
                               2)  Add message tag.
  04/03/2018       JLD         Issue 109176. Refactor Vendor.
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  10/16/2018       Elvin       Issue 195835: grid replacement
  --------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<%
String entityNameDisplay = (String) request.getAttribute(ICIConstants.ENTITY_NAME_PROPERTY);
if (StringUtils.isBlank(entityNameDisplay)) {
  entityNameDisplay = MessageManager.getInstance().formatMessage("ci.entity.vendorInformation.form.title");
} else {
  entityNameDisplay = MessageManager.getInstance().formatMessage("ci.entity.search.label.vendorInformation") + " " + entityNameDisplay;
}
%>

<jsp:useBean id="paymentTotalsGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="paymentTotalsGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="entityResultList" class="java.util.ArrayList" scope="request"/>

<c:set var="globalActionItemGroupId" value="CI_FOLDER_AG"></c:set>
<%@ include file="/core/header.jsp" %>
<jsp:include page="/cicore/common.jsp"/>
<jsp:include page="/CI_EntitySelect.jsp"/>
<c:set var="tabMenuGroupId" value="${tabGroupId}"></c:set>
<%@ include file="/core/tabheader.jsp" %>

<script language="javascript" src="<%=cisPath%>/vendormgr/js/vendor.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<FORM name="CIVendorForm" action="ciVendor.do" method="POST">
    <tr>
        <td colspan="6" class="tabTitle">
            <b><%=entityNameDisplay%></b>
        </td>
    </tr>

    <tr>
        <td colspan="6">
            <oweb:message/>
        </td>
    </tr>

    <%@ include file="/cicore/commonFormHeader.jsp" %>
    <jsp:include page="/cicore/ciFolderCommon.jsp" />

    <input type="hidden" name="CI_CHK_BANK_FOR_EFT" value="<%=SysParmProvider.getInstance().getSysParm("CI_CHK_BANK_FOR_EFT", "N")%>">

    <tr>
        <fmt:message key="ci.entity.vendor.form.title" var="vendorTitle" scope="request"/>
        <% String vendorTitle = (String) request.getAttribute("vendorTitle"); %>
        <td colspan="6">
          <jsp:include page="/core/compiledFormFields.jsp">
              <jsp:param name="isGridBased" value="false"/>
              <jsp:param name="divId" value="Vendor"/>
              <jsp:param name="headerText" value="<%=vendorTitle%>"/>
              <jsp:param name="excludeAllLayers" value="true"/>
              <jsp:param name="actionItemGroupId" value="CI_VENDOR_AIG"/>
              <jsp:param name="actionItemGroupIdCssWidthInPX" value="120px"/>
          </jsp:include>
      </td>
    </tr>

    <tr>
        <td colspan="6">
            <oweb:panel panelContentId="panelContentForPaymentTotals"
                        panelTitleId="panelTitleIdForPaymentTotals"
                        panelTitleLayerId="Vendor_Payment_Total_Grid_Header_Layer">
                <tr>
                    <td width="100%">
                        <c:set var="gridDisplayFormName" value="CIVendorForm" scope="request"/>
                        <c:set var="gridDisplayGridId" value="paymentTotalsGrid" scope="request"/>
                        <c:set var="datasrc" value="#paymentTotalsGrid1" scope="request"/>
                        <c:set var="excludeLayerIds" value="PAGE_FIELDS_LAYER" scope="request"/>
                        <%
                            BaseResultSet dataBean = paymentTotalsGridDataBean;
                            XMLGridHeader gridHeaderBean = paymentTotalsGridHeaderBean;
                        %>
                        <%@ include file="/core/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>

    <%@ include file="/core/tabfooter.jsp" %>
    <jsp:include page="/core/footer.jsp"/>

