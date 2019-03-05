<%--
  Description: view application history page

  Author: Bhong
  Date: May 10, 2012


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------

  -----------------------------------------------------------------------------
  (C) 2012 Delphi Technology, inc. (dti)
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

<tr>
    <td colspan=8>
        <table cellpadding=0 cellspacing=0 width=100%>
            <tr>
                <td><oweb:message/></td>
            </tr>
        </table>
    </td>
</tr>
<form action="" name="applicationHistoryForm">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <tr>
        <td align=center>
            <fmt:message key="pm.viewApplicationHistory.header" var="panelTitleForAppHistory" scope="page"/>
                <%
                String panelTitleForAppHistory = (String) pageContext.getAttribute("panelTitleForAppHistory");
            %>
            <oweb:panel panelTitleId="panelTitleIdForAppHistory" panelContentId="panelContentIdForAppHistory"
                        panelTitle="<%= panelTitleForAppHistory %>">
    <tr>
        <td colspan="6" align=center>
            <c:set var="gridDisplayFormName" value="applicationHistoryForm" scope="request"/>
            <c:set var="gridDisplayGridId" value="applicationHistoryGrid" scope="request"/>
            <c:set var="cacheResultSet" value="false"/>
            <c:set var="selectable" value="true"/>
            <%@ include file="/pmcore/gridDisplay.jsp" %>
        </td>
    </tr>
    </oweb:panel>
    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="PM_VIEW_APP_HIST_AIG"/>
        </td>
    </tr>

<jsp:include page="/core/footerpopup.jsp"/>