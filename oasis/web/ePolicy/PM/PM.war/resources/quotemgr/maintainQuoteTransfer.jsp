<%--
  Description:

  Author: wdang
  Date: 04/27/2016


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  08/26/2016       wdang       167534 - Initial Version.
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2016 Delphi Technology, inc. (dti)
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<c:set var="isForDivPopup" value="true"></c:set>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<%@ include file="/core/invokeWorkflow.jsp" %>
<script type="text/javascript" src="<%=appPath%>/quotemgr/js/maintainQuoteTransfer.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<form name="quoteTransferList" action="<%=appPath%>/quotemgr/maintainQuoteTransfer.do" method="post">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <input type="hidden" name="workflowState" value="<c:out value="${workflowState}"/>">
    <tr>
        <td>
            <oweb:message/>
        </td>
    </tr>

    <c:set var="policyHeaderDisplayMode" value="invisible"/>
    <tr>
        <td align=center>
            <%@ include file="/policymgr/policyHeader.jsp" %>
        </td>
    </tr>

<c:if test="${dataBean != null && dataBean.columnCount > 0}">
    <tr>
        <td align=center>
            <fmt:message key="pm.maintainQuoteTransfer.quoteTransferList.header" var="panelTitleForQuoteTransferList" scope="request"/>
                <% String panelTitleForQuoteTransferList = (String) request.getAttribute("panelTitleForQuoteTransferList"); %>
            <oweb:panel panelTitleId="panelTitleIdForQuoteTransferList" panelContentId="panelContentIdForQuoteTransferList" panelTitle="<%= panelTitleForQuoteTransferList %>" >
            <tr>
                <td align=center>
                    <c:set var="gridDisplayFormName" value="quoteTransferList" scope="request"/>
                    <c:set var="gridDisplayGridId" value="quoteTransferListGrid" scope="request"/>
                    <c:set var="gridDetailDivId" value="quoteTransferListGridDiv" scope="request" />
                    <c:set var="datasrc" value="#quoteTransferListGrid1" scope="request"/>
                    <c:set var="cacheResultSet" value="false"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            </oweb:panel>
        </td>
    </tr>
</c:if>
    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="PM_QUOTE_TRANSFER_AIG" layoutDirection="horizontal"/>
        </td>
    </tr>

<jsp:include page="/core/footerpopup.jsp"/>