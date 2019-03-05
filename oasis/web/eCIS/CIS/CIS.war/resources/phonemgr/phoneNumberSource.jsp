<%@ page language="java"%>
<%@ page import="dti.ci.helpers.ICIPhoneNumberConstants,
                 java.util.ArrayList,
                 dti.oasis.util.StringUtils"%>
<%@ page import="dti.ci.helpers.ICIConstants"%>
<%@ page import="dti.oasis.messagemgr.MessageManager" %>
<%--
  Description: Phone Number Source

  Author: Gerald C. Carney
  Date: Mar 22, 2004


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  01/25/2007       GCC         Changed the way the LOV ID string is constructed.
  06/29/2007       James       Added UI2 Changes
  08/30/2007       Kenney      remove UIStyleEdition;
                               change to panel tag;
  09/14/2007       James       Change panel title from Filter to
                               Filter Criteria
  09/28/2010       wfu         111776: Replaced hardcode string with resource definition
  07/01/2013       hxk         Issue 141840
                               1)  Add panelId of FilterCriteria2 so security can act on secondary filter
                                   div FilterCriteria2.
                               2)  Add message tag.
  05/29/2018       dpang       Issue 109161: Remove duplicated pk, entityType and entityName fields.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib prefix="c" uri="/WEB-INF/c.tld"  %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<%
String entityNameDisplay = (String) request.getAttribute(ICIConstants.ENTITY_NAME_PROPERTY);
if (StringUtils.isBlank(entityNameDisplay)) {
  entityNameDisplay = MessageManager.getInstance().formatMessage("ci.entity.phoneNumbers.form.title");
}
else {
  entityNameDisplay = MessageManager.getInstance().formatMessage("ci.entity.search.label.phoneNumbers") + " " + entityNameDisplay;
}

String lovID = ICIPhoneNumberConstants.PHONE_NUM_SRC_REC_FK_ID + "LOV";
%>

<tr valign="top">
    <td colspan="6" class="tabTitle">
        <oweb:message/>
    </td>
</tr>
<tr valign="top">
    <td colspan="6" class="tabTitle">
        <b><%=entityNameDisplay%></b>
    </td>
</tr>
<fmt:message key="ci.common.search.filter.criteria" var="filterCriteria" scope="request"/>
<% String filterCriteria = (String) request.getAttribute("filterCriteria"); %>
<tr>
    <td colspan="6">
        <oweb:panel panelContentId="panelContentForFilter"
                    panelId="FilterCriteria2"
                    panelTitleId="panelTitleIdForFilter" panelTitle="<%=  filterCriteria %>">
                   <tr valign="top">
                <td colspan="6"><b><fmt:message key="ci.phoneNumberList.select.warning" />
                </b></td>
            </tr>
            <tr valign="top">
                <oweb:select listOfValues="<%=(ArrayList) request.getAttribute(lovID)%>"
                             oasisFormField="<%=fieldsMap.get(ICIPhoneNumberConstants.PHONE_NUM_SRC_REC_FK_ID)%>"
                             name="<%=ICIPhoneNumberConstants.PHONE_NUM_SRC_REC_FK_ID%>"
                             onchange="javascript:changeSourceRecordFK()"/>
            </tr>
        </oweb:panel>
    </td>
</tr>
<html:hidden value="" property="<%=ICIConstants.PROCESS_PROPERTY%>" />

