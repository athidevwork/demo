<%@ page import="dti.ci.helpers.CIEntityHelper,
                 dti.oasis.util.StringUtils,
                 dti.ci.helpers.ICIEntityConstants,
                 dti.ci.helpers.ICIConstants,
                 dti.oasis.tags.OasisFormField,
                 dti.oasis.util.FormatUtils" %>
<%@ page import="dti.ci.emailaddressmgr.EmailAddressManager" %>
<%@ page import="dti.ci.emailaddressmgr.EmailAddressFields" %>
<%@ page import="dti.oasis.recordset.Record" %>
<%@ page import="dti.oasis.tags.WebLayer" %>
<%@ page import="dti.oasis.app.ApplicationContext" %>
<%@ page import="org.apache.struts.Globals" %>
<%@ page import="org.apache.struts.taglib.html.Constants" %>
<%@ page import="dti.oasis.tags.XMLGridHeader" %>
<%@ page import="dti.oasis.util.BaseResultSet" %>
<%@ page language="java" %>
<%--
  Description: Entity Mini Popup

  Author: Gerald C. Carney
  Date: Apr 22, 2004


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  04/13/2005       HXY         Added logic for controlling grid size.
  04/22/2005       HXY         Added </FORM> tag.
  01/11/2007       PXS         Added hidden field for DBPoolAppendix
                               that's coming from headerpopup.jsp
  02/06/2007       GCC         Added toString (for StringBuffer) fieldsMap.get
                               calls.
  07/06/2007       Mark        Added UI2 Changes
  07/31/2007       Kenney      Add UI2 change
  09/07/2007       Kenney      remove UIStyleEdition;
                               change to panel tag;
                               change to compiledFormField page
  11/15/2007       kshen       Relabeled header layer.
  04/09/2008       wer         Removed passing dbPoolId appendix to comply with new Role-based dbPoolId configuration.
  09/02/2008       kshen       Added "Email Address" filed.
  04/16/2008       kshen       Used Email Text display type to display email icon for entity.  
  04/28/2010       shchen      Add contact list layer for this page.
  09/28/2010       wfu         111776: Replaced hardcode string with resource definition
  05/31/2013       jxgu        Issue#145434 Duplicate vertical scroll bars and horizontal scroll bars
                               in Entity Mini Popup page
  04/17/2018       dzhang      Issue 192649: entity mini popup refactor
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  10/17/2018       dmeng       195835: grid relacement
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="fmt" uri="/WEB-INF/fmt.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<%--address list--%>
<jsp:useBean id="entityAddressListDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="entityAddressListHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<%--entity phone list--%>
<jsp:useBean id="entityPhoneListDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="entityPhoneListHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<%--address phone list--%>
<jsp:useBean id="addressPhoneListDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="addressPhoneListHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<%--entity contact list--%>
<jsp:useBean id="entityContactListDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="entityContactListHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>

<%@ include file="/core/headerpopup.jsp" %>
<jsp:include page="/cicore/common.jsp"/>

<script language="javascript" src="<%=cisPath%>/entityminipopupmgr/js/entityMiniPopup.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<FORM name="CIEntityMiniPopupForm" action="ciEntityMiniPopup.do" method="POST">

    <%@ include file="/cicore/commonFormHeader.jsp" %>
    <jsp:include page="/cicore/entityCommonFields.jsp"/>

<tr>
    <td align="center" colspan="6">
        <oweb:actionGroup actionItemGroupId="CI_ENTITY_MINI_POPUP_AIG"
                        cssColorScheme="blue" layoutDirection="horizontal">
        </oweb:actionGroup>
    </td>
</tr>

<fmt:message key="ci.entity.mini.search.header" var="miniHeader" scope="request"/>
<%
    String miniHeader = (String) request.getAttribute("miniHeader");
%>
<tr>
    <td colspan="6">
        <jsp:include page="/core/compiledFormFields.jsp">
            <jsp:param name="isGridBased" value="false"/>
            <jsp:param name="divId" value="Head"/>
            <jsp:param name="headerText" value="<%=miniHeader%>"/>
            <jsp:param name="excludeAllLayers" value="true"/>
        </jsp:include>
    </td>
</tr>
<tr>
    <td colspan="6">
        <oweb:panel panelContentId="panelContentForAddresses" panelTitleId="panelTitleIdForAddresses"
                    panelTitleLayerId="Entity_MiniPopup_Address_Grid_Header_Layer">
            <tr>
                <td colspan="6">
                    <%
                        BaseResultSet dataBean = entityAddressListDataBean;
                        XMLGridHeader gridHeaderBean = entityAddressListHeaderBean;
                    %>
                    <c:set var="gridDisplayFormName" value="CIEntityMiniPopupForm" scope="request"/>
                    <c:set var="gridDisplayGridId" value="addressGrid" scope="request"/>
                    <c:set var="datasrc" value="#addressGrid1" scope="request"/>
                    <%@ include file="/core/gridDisplay.jsp" %>
                </td>
            </tr>
        </oweb:panel>
    </td>
</tr>
<tr>
    <td colspan="6">
        <oweb:panel panelContentId="panelContentForGeneraPhoneNumbers" panelTitleId="panelTitleIdForGeneraPhoneNumbers"
                    panelTitleLayerId="Entity_MiniPopup_General_Phone_Grid_Header_Layer">
            <tr>
                <td colspan="6">
                     <%
                        BaseResultSet dataBean = entityPhoneListDataBean;
                        XMLGridHeader gridHeaderBean = entityPhoneListHeaderBean;
                    %>
                    <c:set var="gridDisplayFormName" value="CIEntityMiniPopupForm" scope="request"/>
                    <c:set var="gridDisplayGridId" value="entityPhoneGrid" scope="request"/>
                    <c:set var="datasrc" value="#entityPhoneGrid1" scope="request"/>
                    <%@ include file="/core/gridDisplay.jsp" %>
                </td>
            </tr>
        </oweb:panel>
    </td>
</tr>
<tr>
    <td colspan="6">
        <oweb:panel panelContentId="panelContentForAddressPhoneNumbers"
                    panelTitleId="panelTitleIdForAddressPhoneNumbers"
                    panelTitleLayerId="Entity_MiniPopup_Address_Phone_Grid_Header_Layer">
            <tr>
                <td colspan="6">
                    <%
                        BaseResultSet dataBean = addressPhoneListDataBean;
                        XMLGridHeader gridHeaderBean = addressPhoneListHeaderBean;
                    %>
                    <c:set var="gridDisplayFormName" value="CIEntityMiniPopupForm" scope="request"/>
                    <c:set var="gridDisplayGridId" value="addressPhoneGrid" scope="request"/>
                    <c:set var="datasrc" value="#addressPhoneGrid1" scope="request"/>
                    <%@ include file="/core/gridDisplay.jsp" %>
                </td>
            </tr>
        </oweb:panel>
    </td>
</tr>
<tr>
    <td  colspan="6">
        <oweb:panel panelContentId="panelContentForContactList"
                    panelTitleId="panelTitleIdForContactList"
                    panelTitleLayerId="Entity_MiniPopup_Contact_Grid_Header_Layer">
            <tr>
                <td colspan="6">
                    <%
                        BaseResultSet dataBean = entityContactListDataBean;
                        XMLGridHeader gridHeaderBean = entityContactListHeaderBean;
                    %>
                    <c:set var="gridDisplayFormName" value="CIEntityMiniPopupForm" scope="request"/>
                    <c:set var="gridDisplayGridId" value="entityContactList" scope="request"/>
                    <c:set var="datasrc" value="#CIEntityContactList1" scope="request"/>
                    <%@ include file="/core/gridDisplay.jsp" %>
                </td>
            </tr>
        </oweb:panel>
    </td>
</tr>
<tr>
    <td style="padding-top:6px" align="center" colspan="6">
        <oweb:actionGroup actionItemGroupId="CI_ENTITY_MINI_POPUP_AIG"
                        cssColorScheme="blue" layoutDirection="horizontal">
        </oweb:actionGroup>
    </td>
</tr>
</FORM>
<jsp:include page="/core/footerpopup.jsp"/>
