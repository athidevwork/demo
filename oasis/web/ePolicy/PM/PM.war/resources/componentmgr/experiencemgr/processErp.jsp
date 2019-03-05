<%@ page import="dti.pm.core.http.RequestIds" %>
<%--
  Description:Process Experience Rating Programs page.

  Author: ryzhao
  Date: Mar 08, 2011

  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  05/06/2011       ryzhao      120440 - Added Java Script if it is a popup page and no ERP data found
                                        show hint message and close the popup window.
  05/09/2011       ryzhao      120440 - Move the Java Script to processErp.js per code review comments.
  11/13/2018       tyang       194100 - Add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<c:set var="headerHidden" value="${param.headerHidden}"/>
<%@ include file="/core/header.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<script type="text/javascript" src="js/processErp.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>

<form name="processErpForm" method=post>
<%@ include file="/pmcore/commonFormHeader.jsp" %>
<input type="hidden" name="headerHidden" value="<c:out value="${param.headerHidden}"/>"/>
<input type="hidden" name="policySearchTotalRows" value="<c:out value="${PolicySeachTotalRows}"/>"/>
<input type="hidden" name="policyNo" value="<c:out value="${param.policyNo}"/>"/>
<input type="hidden" name="policyId" value="<c:out value="${param.policyId}"/>"/>
<input type="hidden" name="riskId" value="<c:out value="${param.riskId}"/>"/>
<input type="hidden" name="transLogId" value="<c:out value="${param.transLogId}"/>"/>
<input type="hidden" name="termId" value="<c:out value="${param.termId}"/>"/>
<input type="hidden" name="termEff" value="<c:out value="${param.termEff}"/>"/>
<input type="hidden" name="termExp" value="<c:out value="${param.termExp}"/>"/>
<input type="hidden" name="transEff" value="<c:out value="${param.transEff}"/>"/>

<%-- Show error message --%>
<tr>
    <td colspan=8>
        <oweb:message/>
    </td>
</tr>

<tr>
    <td align=center>
        <oweb:actionGroup actionItemGroupId="PM_ERP_PROCESS_AIG"/>
    </td>
</tr>

<%-- Filter Layer --%>
<fmt:message key="pm.processErp.search.header" var="filterTitle" scope="request"/>
<% String filterTitle = (String) request.getAttribute("filterTitle");%>
<tr>
    <td align=center>
        <jsp:include page="/core/compiledFormFields.jsp">
            <jsp:param name="headerText" value="<%=filterTitle%>"/>
            <jsp:param name="divId" value="erpSearch"/>
            <jsp:param name="isGridBased" value="false"/>
            <jsp:param name="isLayerVisibleByDefault" value="true"/>
            <jsp:param name="includeLayersWithPrefix" value="PM_ERP_PROCESS_SEARCH"/>
            <jsp:param name="actionItemGroupId" value="PM_ERP_SEARCH_AIG"/>
        </jsp:include>
    </td>
</tr>

<c:if test="${dataBean != null && dataBean.columnCount > 0 && dataBean.rowCount > 0}">
    <tr>
    <td align=center>
    <fmt:message key="pm.processErp.erpList.header" var="panelTitleForErpList" scope="page"/>
    <% String panelTitleForErpList = (String) pageContext.getAttribute("panelTitleForErpList");%>
    <oweb:panel panelTitleId="panelTitleIdForErpList" panelContentId="panelContentIdForErpList"
                panelTitle="<%= panelTitleForErpList %>">
        <tr>
        <td colspan="6" align=center>
        <fmt:message key="pm.processErp.erpGrid.header" var="panelTitleForErpGrid" scope="page">
            <fmt:param value="${PolicySeachTotalRows}"/>
        </fmt:message>
        <%
            String panelTitleForErpGrid = (String) pageContext.getAttribute("panelTitleForErpGrid");
        %>
        <oweb:panel panelTitleId="panelTitleIdForErpGrid" panelContentId="panelContentIdForErpGrid"
                    panelTitle="<%= panelTitleForErpGrid %>">
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="erpList" scope="request"/>
                    <c:set var="gridDisplayGridId" value="erpListGrid" scope="request"/>
                    <c:set var="gridDetailDivId" value="erpDetailDiv" scope="request"/>
                    <c:set var="datasrc" value="#erpListGrid1" scope="request"/>
                    <c:set var="cacheResultSet" value="true"/>
                    <c:set var="showRowCntOnePage" value="true" scope="request"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            <tr>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td align=center>
                    <fmt:message key="pm.processErp.erpForm.header" var="erpFormHeader" scope="request"/>
                    <% String erpFormHeader = (String) request.getAttribute("erpFormHeader"); %>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="headerText" value="<%=  erpFormHeader %>"/>
                        <jsp:param name="gridID" value="erpListGrid"/>
                        <jsp:param name="divId" value="erpDetailDiv"/>
                        <jsp:param name="isLayerVisibleByDefault" value="true"/>
                        <jsp:param name="includeLayerIds" value="PM_ERP_PROCESS_FIELDS"/>
                    </jsp:include>
                </td>
            </tr>
            <tr>
                <td>&nbsp;</td>
            </tr>
        </oweb:panel>
        </td>
        </tr>
    </oweb:panel>
    </td>
    </tr>
    <tr>
    <td align=center>
    <fmt:message key="pm.processErp.claimDetail.header" var="panelTitleForClaimDetail" scope="page"/>
    <%
        String panelTitleForClaimDetail = (String) pageContext.getAttribute("panelTitleForClaimDetail");
    %>
    <oweb:panel panelTitleId="panelTitleIdForClaimDetail" panelContentId="panelContentIdForClaimDetail"
                panelTitle="<%= panelTitleForClaimDetail %>">
        <tr>
            <td>
                <iframe id="iframeClaimDetails" scrolling="no" allowtransparency="true" width="98%" height="500"
                        frameborder="0" src=""></iframe>
            </td>
        </tr>
    </oweb:panel>

    </td>
    </tr>

    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="PM_ERP_PROCESS_AIG"/>
        </td>
    </tr>
</c:if>

<c:if test="${empty param.policyId}">
    <jsp:include page="/core/footer.jsp"/>
</c:if>
<c:if test="${not empty param.policyId}">
    <jsp:include page="/core/footerpopup.jsp"/>
</c:if>
