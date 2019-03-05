<%@ page import="dti.oasis.session.UserSessionManager" %>
<%--
  Description: jsp file for maintain limit sharing
  Author: rlli
  Date: Nov 12, 2007
  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  08/16/2010       syang       Issue 110383 - Add hidden fields limitShareType and selectedSharedGroupId.
  08/14/2012       adeng       135972 - Add hidden field hasRisk.
  08/31/2012       adeng       135972 - Roll backed changes,using new solution of hiding Limit Sharing button.
  03/10/2017       wli         Issue 180675 - Changed the error msg to be located in parent frame for UI change.
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
<script type="text/javascript" src="js/maintainLimitSharing.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="sharedDetailListGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="sharedDetailListGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>
<form name="limitSharingList" action="maintainLimitSharing.do" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <input type="hidden" name="limitShareType" value="<c:out value="${limitShareType}"/>">
    <input type="hidden" name="selectedSharedGroupId" value="<c:out value="${selectedSharedGroupId}"/>">
    <tr>
        <td colspan=8>
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
    <c:set var="policyHeaderDisplayMode" value="invisible"/>
    <tr>
        <td colspan=8 align=center>
            <%@ include file="/policymgr/policyHeader.jsp" %>
        </td>
    </tr>
    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="PM_LIMIT_SHARING_AIG" layoutDirection="horizontal"/>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.maintainLimitShareing.sharedGroupList.header" var="panelTitleForSharedGroup" scope="page"/>
                <%
            String panelTitleForSharedGroup = (String) pageContext.getAttribute("panelTitleForSharedGroup");
        %>
            <oweb:panel panelTitleId="panelTitleIdForSharedGroup" panelContentId="panelContentIdForSharedGroup"
                        panelTitle="<%= panelTitleForSharedGroup %>">
    <tr>
        <td colspan="6">
            <oweb:actionGroup actionItemGroupId="PM_SHARED_GROUP_GRID_AIG" layoutDirection="horizontal"
                              cssColorScheme="gray"/>
        </td>
    </tr>
    <tr>
        <td colspan="6" align=center>
            <c:set var="gridDisplayFormName" value="sharedGroupList" scope="request"/>
            <c:set var="gridDisplayGridId" value="sharedGroupListGrid" scope="request"/>
            <c:set var="gridDetailDivId" value="sharedGroupDetailDiv" scope="request"/>
            <c:set var="datasrc" value="#sharedGroupListGrid1" scope="request"/>
            <c:set var="cacheResultSet" value="false"/>
            <%@ include file="/pmcore/gridDisplay.jsp" %>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.maintainLimitSharing.sharedGroupForm.header" var="sharedGroupFormHeader"
                         scope="request"/>
            <% String sharedGroupFormHeader = (String) request.getAttribute("sharedGroupFormHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%=  sharedGroupFormHeader %>"/>
                <jsp:param name="isLayerVisibleByDefault" value="true"/>
                <jsp:param name="isGridBased" value="true"/>
                <jsp:param name="includeLayersWithPrefix" value="PM_SHARED_GROUP"/>
            </jsp:include>
        </td>
    </tr>
    </oweb:panel>
    </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.maintainLimitSharing.sharedDetailList.header" var="panelTitleForSharedDetail"
                         scope="page"/>
                <%
            String panelTitleForSharedDetail = (String) pageContext.getAttribute("panelTitleForSharedDetail");
        %>
            <oweb:panel panelTitleId="panelTitleIdForSharedDetail" panelContentId="panelContentIdForSharedDetail"
                        panelTitle="<%= panelTitleForSharedDetail %>">
    <tr>
        <td colspan="6">
            <oweb:actionGroup actionItemGroupId="PM_SHARED_DETAIL_GRID_AIG" layoutDirection="horizontal"
                              cssColorScheme="gray"/>
        </td>
    </tr>
    <tr>
        <td colspan="6" align=center>
            <c:set var="gridDisplayFormName" value="sharedDetailList" scope="request"/>
            <c:set var="gridDisplayGridId" value="sharedDetailListGrid" scope="request"/>
            <c:set var="gridDetailDivId" value="sharedDetailDetailDiv" scope="request"/>
            <c:set var="gridSizeFieldIdPrefix" value="detail_"/>
            <% dataBean = sharedDetailListGridDataBean;
                gridHeaderBean = sharedDetailListGridHeaderBean; %>
            <c:set var="cacheResultSet" value="false"/>
            <%@ include file="/pmcore/gridDisplay.jsp" %>
        </td>
    </tr>
    <tr>
        <td align=center>
            <c:set var="datasrc" value="#sharedDetailListGrid1" scope="request"/>
            <fmt:message key="pm.maintainLimitSharing.sharedDetailForm.header" var="sharedDetailFormHeader"
                         scope="request"/>
            <% String sharedDetailFormHeader = (String) request.getAttribute("sharedDetailFormHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%=  sharedDetailFormHeader %>"/>
                <jsp:param name="isGridBased" value="true"/>
                <jsp:param name="divId" value="sharedDetailDetailDiv"/>
                <jsp:param name="isLayerVisibleByDefault" value="true"/>
                <jsp:param name="includeLayersWithPrefix" value="PM_SHARED_DETAIL"/>
            </jsp:include>
        </td>
    </tr>
    </oweb:panel>
    </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.maintainLimitSharing.separateLimits.header" var="panelTitleIdForSeparateLimits"
                         scope="page"/>
                <%
            String panelTitleIdForSeparateLimitsGrid = (String) pageContext.getAttribute("panelTitleIdForSeparateLimits");
        %>
            <oweb:panel panelTitleId="panelTitleIdForSeparateLimitsGrid" panelContentId="panelContentIdForChangeDetailGrid"
                        panelTitle="<%= panelTitleIdForSeparateLimitsGrid %>">
    <tr>
        <td>
            <iframe id="iframeSeparateLimits" scrolling="no" allowtransparency="true" width="98%" height="155"
                    frameborder="0" src=""></iframe>
        </td>
    </tr>
    </oweb:panel>
    </td>
    </tr>
    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="PM_LIMIT_SHARING_AIG" layoutDirection="horizontal"/>
        </td>
    </tr>

        <%
// Initialize Sys Parms for JavaScript to use
String sysParmCascadeDel = SysParmProvider.getInstance().getSysParm("PM_SHR_GRPS_NODELDTL", "XXX");    
%>
    <script type="text/javascript">
        setSysParmValue("PM_SHR_GRPS_NODELDTL", '<%=sysParmCascadeDel%>');
    </script>
<jsp:include page="/core/footerpopup.jsp"/>