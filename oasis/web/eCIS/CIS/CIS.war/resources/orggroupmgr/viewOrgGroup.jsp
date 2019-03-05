<%@ page import="dti.ci.helpers.ICIConstants,
                 dti.oasis.tags.XMLGridHeader,
                 dti.oasis.util.BaseResultSet"%>
<%@ page language="java"%>
<%--
  Description:

  Author: Michael Nadar
  Date: May 28, 2009

  Revision Date    Revised By  Description
  --------------------------------------------------------------------
  07/28/2009       Leo         Issue 95771
  06/12/2018       dpang       Issue 193846: Refactor Org/Group page.
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  06/29/2018       ylu         Issue 194117: update for CSRF security.
  10/15/2018       dmeng       Issue 195835: grid replacement
  --------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:set var="globalActionItemGroupId" value="CI_FOLDER_AG"></c:set>

<%@include file="/core/header.jsp" %>
<jsp:include page="/cicore/common.jsp"/>
<jsp:include page="/CI_EntitySelect.jsp"/>

<c:set var="tabMenuGroupId" value="${tabGroupId}"></c:set>
<%@ include file="/core/tabheader.jsp" %>

<script type='text/javascript' src="<%=cisPath%>/orggroupmgr/js/viewOrgGroup.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<!-- Content -->

<FORM name="CIOrgGroupForm" action="orgGroupView.do" method="POST">
  <jsp:include page="/cicore/ciFolderCommon.jsp" />
  <%@ include file="/cicore/commonFormHeader.jsp" %>
  <jsp:useBean id="membersGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
  <jsp:useBean id="membersGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
  <jsp:useBean id="summaryGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
  <jsp:useBean id="summaryGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
  <jsp:useBean id="addressGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
  <jsp:useBean id="addressGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
  <jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
    <tr>
        <td colspan="6">
            <oweb:message/>
        </td>
    </tr>
    <tr id="AddressGridRowId">
      <td width="100%">
          <c:set var="gridDisplayFormName" value="CIOrgGroupForm" scope="request"/>
          <c:set var="gridDisplayGridId" value="AddressGrid" scope="request"/>
          <c:set var="datasrc" value="#AddressGrid1" scope="request"/>
          <c:set var="gridDetailDivId" value="groudAddressDiv" scope="request"/>
          <%
              BaseResultSet dataBean = addressGridDataBean;
              XMLGridHeader gridHeaderBean = addressGridHeaderBean;
          %>
          <%@ include file="/core/gridDisplay.jsp" %>
      </td>
    </tr>
    <tr>
      <td id="formfields1">
          <jsp:include page="/core/compiledFormFields.jsp">
              <jsp:param name="gridID" value="AddressGrid"/>
              <jsp:param name="includeLayerIds" value="GROUP_ADDRESS_LAYER"/>
              <jsp:param name="headerTextLayerId" value="GROUP_ADDRESS_LAYER"/>
              <jsp:param name="isLayerVisibleByDefault" value="true"/>
              <jsp:param name="divId" value="groudAddressDiv"/>
              <jsp:param name="excludePageFields" value="true"/>
          </jsp:include>
      </td>
  </tr>
    <%
        String panelCaption = ApplicationContext.getInstance().getProperty("orgGroup.lengend.filter", "View Group");
    %>
      <tr>
          <td>
              <jsp:include page="/core/compiledFormFields.jsp">
                  <jsp:param name="isGridBased" value="false"/>
                  <jsp:param name="divId" value="formfields"/>
                  <jsp:param name="headerText" value="<%=panelCaption%>"/>
                  <jsp:param name="excludeAllLayers" value="true"/>
                  <jsp:param name="actionItemGroupId" value="CI_ORGGROUP_SEARCH_AIG"/>
              </jsp:include>
          </td>
      </tr>
      <tr>
          <td colspan="6">
              <oweb:panel panelContentId="panelContentForMemberList"
                          panelTitleId="panelTitleIdForMemberList" panelTitleLayerId="GROUP_MEMBER_LIST_LAYER">
                  <tr>
                      <td width="100%">
                          <c:set var="gridDisplayFormName" value="CIOrgGroupForm" scope="request"/>
                          <c:set var="gridDisplayGridId" value="orgGroupGrid" scope="request"/>
                          <c:set var="datasrc" value="#orgGroupGrid1" scope="request"/>
                          <%
                              dataBean = membersGridDataBean;
                              gridHeaderBean = membersGridHeaderBean;
                          %>
                          <%@ include file="/core/gridDisplay.jsp" %>
                      </td>
                  </tr>
              </oweb:panel>
          </td>
      </tr>
  <tr id="summaryGridRowId">
      <td width="100%">
          <c:set var="gridDisplayFormName" value="CIOrgGroupForm" scope="request"/>
          <c:set var="gridDisplayGridId" value="SummaryGrid" scope="request"/>
          <c:set var="datasrc" value="#SummaryGrid1" scope="request"/>
          <c:set var="gridDetailDivId" value="detailFieldset" scope="request"/>
          <%
              dataBean = summaryGridDataBean;
              gridHeaderBean = summaryGridHeaderBean;
          %>
          <%@ include file="/core/gridDisplay.jsp" %>
      </td>
  </tr>
  <tr>
      <td id="formfields">
          <jsp:include page="/core/compiledFormFields.jsp">
              <jsp:param name="gridID" value="SummaryGrid"/>
              <jsp:param name="includeLayerIds" value="GROUP_MEMBER_SUMMARY_LAYER"/>
              <jsp:param name="headerTextLayerId" value="GROUP_MEMBER_SUMMARY_LAYER"/>
              <jsp:param name="isLayerVisibleByDefault" value="true"/>
              <jsp:param name="divId" value="detailFieldset"/>
              <jsp:param name="excludePageFields" value="true"/>
          </jsp:include>
      </td>
  </tr>

<%@ include file="/core/tabfooter.jsp" %>

<jsp:include page="/core/footer.jsp" />
