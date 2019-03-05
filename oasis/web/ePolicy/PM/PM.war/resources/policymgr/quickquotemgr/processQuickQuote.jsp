<%--
  Description:

  Author: Bhong
  Date: Aug 11, 2009


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  09/10/2010       wfu         111776: Replaced hardcode string with resource definition
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2009 Delphi Technology, inc. (dti)
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<c:set var="isForDivPopup" value="true"></c:set>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<script type="text/javascript" src="<%=csPath%>/fileuploadmgr/js/openFileUpload.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="js/processQuickQuote.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<form name="processQuickQuoteForm" action="processQuickQuote.do" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
        <input type="hidden" name="policyLoadEventHeaderId" value="<c:out value="${policyLoadEventHeaderId}"/>">
        <input type="hidden" name="oasisFileId" value="<c:out value="${oasisFileId}"/>">
        <input type="hidden" name="termBaseRecordId" value="<c:out value="${policyHeader.termBaseRecordId}"/>">

        <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
    <c:set var="policyHeaderDisplayMode" value="invisible"/>
    <tr>
        <td colspan=8 align=center>
            <%@ include file="/policymgr/policyHeader.jsp" %>
        </td>
    </tr>

    <!-- Display filter portion -->
    <tr>
        <td align=center>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="Filter"/>
                <jsp:param name="divId" value="filterDiv"/>
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="isLayerVisibleByDefault" value="false"/>
                <jsp:param name="excludePageFields" value="false"/>
                <jsp:param name="actionItemGroupId" value="PM_QUICK_QUOTE_SEARCH_AIG"/>
                <jsp:param name="actionItemGroupIdCss" value="gray"/>        
            </jsp:include>
        </td>
    </tr>
    <!-- Display load result grid -->
    <tr>
        <td align=center>
            <fmt:message key="pm.processQuickQuote.loadResult.header" var="loadResult" scope="request"/>
            <%  String loadResult = (String) request.getAttribute("loadResult"); %>
            <oweb:panel panelTitleId="panelTitleIdForLoadResult" panelContentId="panelContentIdForLoadResult"
                        panelTitle="<%= loadResult %>">
                <tr>
                    <td colspan="6" align=center>
                        <c:set var="gridDisplayFormName" value="processQuickQuoteForm" scope="request"/>
                        <c:set var="gridDisplayGridId" value="processQuickQuoteGrid" scope="request"/>                        
                        <c:set var="cacheResultSet" value="false"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>

    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="PM_QUICK_QUOTE_AIG" layoutDirection="horizontal"/>
        </td>
    </tr>

  <% // Initialize Sys Parms for JavaScript to use
      String hasFileHeader = SysParmProvider.getInstance().getSysParm("PM_LOAD_FILE_HEADER", "Y");
  %>
  <script type="text/javascript">
      setSysParmValue("PM_LOAD_FILE_HEADER", '<%=hasFileHeader %>');
  </script>

    <jsp:include page="/core/footerpopup.jsp"/>
