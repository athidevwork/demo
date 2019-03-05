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

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<script type="text/javascript" src="js/viewQuote.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form name="viewQuoteList" action="<%=appPath%>/quotemgr/viewQuote.do" method="post">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
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

    <tr>
        <td align=center>
            <fmt:message key="pm.viewQuote.viewQuoteList.header" var="panelTitleForViewQuoteList" scope="request"/>
                <% String panelTitleForViewQuoteList = (String) request.getAttribute("panelTitleForViewQuoteList"); %>
            <oweb:panel panelTitleId="panelTitleIdForViewQuoteList" panelContentId="panelContentIdForViewQuoteList" panelTitle="<%= panelTitleForViewQuoteList %>" >
    <tr>
        <td align=center>
            <c:set var="gridDisplayFormName" value="viewQuoteList" scope="request"/>
            <c:set var="gridDisplayGridId" value="viewQuoteListGrid" scope="request"/>
            <c:set var="gridDetailDivId" value="viewQuoteDetailDiv" scope="request" />
            <c:set var="datasrc" value="#viewQuoteListGrid1" scope="request"/>
            <c:set var="cacheResultSet" value="false"/>
            <%@ include file="/pmcore/gridDisplay.jsp" %>
        </td>
    </tr>
    </oweb:panel>
    </td>
    </tr>

    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="PM_VIEW_QUOTE_AIG" layoutDirection="horizontal"/>
        </td>
    </tr>

<jsp:include page="/core/footerpopup.jsp"/>