<%@ page import="dti.ci.helpers.ICIPhoneNumberConstants,
                 dti.ci.helpers.ICIConstants,
                 dti.oasis.tags.OasisFormField,
                 org.apache.struts.Globals"%>
<%@ page import="org.apache.struts.taglib.html.Constants"%>
<%@ page import="dti.oasis.util.*" %>
<%@ page language="java"%>
<%--
  Description: Phone Number List

  Author: Gerald C. Carney
  Date: Mar 23, 2004


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------
  04/13/2005       HXY         Added logic for controlling grid size.
  05/15/2007       MLM         Added UI2 Changes
  06/29/2007       James       Added UI2 Changes
  08/30/2007       Kenney      remove UIStyleEdition;
                               change to panel tag;
  03/19/2009       kenney      Added Form Letter support for eCIS
  7/2/2010         Blake       Add All source function for issue 103463
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  10/09/2018       dmeng       Issue 195835:grid replacement
  -----------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<c:set var="globalActionItemGroupId" value="CI_FOLDER_AG"></c:set>
<%@include file="/core/header.jsp" %>
<jsp:include page="/CI_EntitySelect.jsp"/>

<c:set var="tabMenuGroupId" value="${tabGroupId}"></c:set>
<%@ include file="/core/tabheader.jsp" %>

<%@ include file="/cicore/common.jsp" %>
<script type='text/javascript' src="<%=csPath%>/js/csLoadNotes.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type='text/javascript' src="js/gridbtnclicks.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script language="javascript" src="phonemgr/js/maintainPhoneList.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<!-- Content -->
<FORM name="CIPhoneNumberListForm" action="ciPhoneNumberList.do" method="POST">
    <jsp:include page="/cicore/ciFolderCommon.jsp" />

  <html:hidden value="Y" property="<%=ICIConstants.LIST_DISPLAYED_PROPERTY%>"/>

  <input type="hidden" name="<%=Constants.TOKEN_KEY%>"
         value="<%=request.getSession().getAttribute(Globals.TRANSACTION_TOKEN_KEY)%>">

    <jsp:include page="/phonemgr/phoneNumberSource.jsp"/>

      <tr>
          <td colspan="6">
              <oweb:panel panelContentId="panelContentForPhoneNumber"
                          panelTitleId="panelTitleIdForPhoneNumber" panelTitleLayerId="CI_PHONE_NUMBER_LIST_GH">
                  <tr>
                      <td>
                          <oweb:actionGroup actionItemGroupId="CI_PHONE_LIST_GRID_AIG" cssColorScheme="gray"
                                            layoutDirection="horizontal">
                          </oweb:actionGroup>
                      </td>
                  </tr>
                  <tr>
                      <td colspan="6">
                          <c:set var="gridDisplayFormName" value="CIPhoneNumberListForm" scope="request"/>
                          <c:set var="gridDisplayGridId" value="testgrid" scope="request"/>
                          <c:set var="gridDetailDivId" value="phoneDetailedDivId" scope="request"/>
                          <c:set var="datasrc" value="#phoneNumberListGrid1" scope="request"/>
                          <c:set var="selectable" value="true"/>
                          <%@ include file="/core/gridDisplay.jsp" %>
                      </td>
                  </tr>
                 <!--Display detail-->
                <tr>
                    <td colspan="6">
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="gridID" value="testgrid"/>
                            <jsp:param name="includeLayerIds" value="CI_PHONE_NUMBER_LIST_GH_DETAIL"/>
                            <jsp:param name="headerTextLayerId" value="CI_PHONE_NUMBER_LIST_GH_DETAIL"/>
                            <jsp:param name="removeFieldPrefix" value="true"/>
                            <jsp:param name="isLayerVisibleByDefault" value="true"/>
                            <jsp:param name="divId" value="phoneDetailedDivId"/>
                            <jsp:param name="excludePageFields" value="true"/>
                        </jsp:include>

                    </td>
                </tr>
              </oweb:panel>
          </td>
      </tr>

      <tr>
          <td colspan="6" align="center" style="padding-top:3px;padding-bottom:5px">
              <oweb:actionGroup actionItemGroupId="CI_PHONE_LIST_AIG"
                                cssColorScheme="blue" layoutDirection="horizontal">
              </oweb:actionGroup>
          </td>
      </tr>

<%@ include file="/core/tabfooter.jsp" %>
<jsp:include page="/core/footer.jsp"/>