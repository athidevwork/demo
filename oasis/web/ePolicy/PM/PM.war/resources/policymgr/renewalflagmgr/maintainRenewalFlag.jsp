<%--
  Description:

  Author: tzeng
  Date: 2/16/2016


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  02/24/2015       tzeng       167532 - Initial Version.
  03/10/2017       wli         180675 - Changed the error msg to be located in parent frame for UI change.
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<c:set var="isForDivPopup" value="true"></c:set>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<script type="text/javascript" src="js/maintainRenewalFlag.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form name="maintainRenewalFlagList" action="<%=appPath%>/policymgr/renewalflagmgr/maintainRenewalFlag.do" method="post">
  <%@ include file="/pmcore/commonFormHeader.jsp" %>
  <tr>
    <td>
      <%
        if (pmUIStyle.equals("T")) {
      %>
      <oweb:message displayMessagesOnParent="true"/>
      <%
        }
      %>
      <%
        if (pmUIStyle.equals("B")) {
      %>
      <oweb:message/>
      <%
        }
      %>
    </td>
  </tr>

  <c:set var="policyHeaderDisplayMode" value="hide"/>
  <tr>
    <td align=center>
      <%@ include file="/policymgr/policyHeader.jsp" %>
    </td>
  </tr>

  <input type="hidden" name="termBaseRecordId" value="<c:out value="${policyHeader.termBaseRecordId}"/>"/>
  <input type="hidden" name="recordMode" value="<c:out value="${policyHeader.recordMode.name}"/>"/>
  <input type="hidden" name="transactionLogId" value="<c:out value="${transactionLogId}"/>"/>
  <input type="hidden" name="endorsementQuoteId" value="<c:out value="${policyHeader.lastTransactionInfo.endorsementQuoteId}"/>"/>
  <tr>
    <td align=center>
      <fmt:message key="pm.maintainRenewalFlag.renewalFlagList.header" var="panelTitleForRenewalFlagList" scope="request"/>
      <% String panelTitleForRenewalFlagList = (String) request.getAttribute("panelTitleForRenewalFlagList"); %>
      <oweb:panel panelTitleId="panelTitleIdForRenewalFlagList" panelContentId="panelContentIdForRenewalFlagList" panelTitle="<%= panelTitleForRenewalFlagList %>" >
        <tr>
          <td align=left>
            <oweb:actionGroup actionItemGroupId="PM_RENEWAL_FLAG_GRID_AIG" layoutDirection="horizontal" cssColorScheme="gray"/>
          </td>
        </tr>
        <tr>
          <td align=center>
          <c:set var="gridDisplayFormName" value="maintainRenewalFlagList" scope="request"/>
          <c:set var="gridDisplayGridId" value="maintainRenewalFlagListGrid" scope="request"/>
          <c:set var="gridDetailDivId" value="maintainRenewalFlagDetailDiv" scope="request" />
          <c:set var="datasrc" value="#maintainRenewalFlagListGrid1" scope="request"/>
          <c:set var="cacheResultSet" value="false"/>
          <%@ include file="/pmcore/gridDisplay.jsp" %>
          </td>
        </tr>
        <tr>
          <td align=center>
          <fmt:message key="pm.maintainRenewalFlag.renewalFlagForm.header" var="panelTitleForRenewalFlagForm" scope="page" />
          <% String panelTitleForRenewalFlagForm = (String) pageContext.getAttribute("panelTitleForRenewalFlagForm"); %>
          <jsp:include page="/core/compiledFormFields.jsp">
            <jsp:param name="headerText" value="<%= panelTitleForRenewalFlagForm %>" />
            <jsp:param name="isGridBased" value="true"/>
          </jsp:include>
          </td>
        </tr>
      </oweb:panel>
    </td>
  </tr>

  <tr>
    <td align=center>
      <oweb:actionGroup actionItemGroupId="PM_RENEWAL_FLAG_AIG" layoutDirection="horizontal"/>
    </td>
  </tr>

<jsp:include page="/core/footerpopup.jsp"/>