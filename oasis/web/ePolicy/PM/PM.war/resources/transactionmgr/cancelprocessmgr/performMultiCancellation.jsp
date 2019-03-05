<%--
  Description:
  perform multi cancellation page
  Author: yhchen
  Date: Mar 19, 2008


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  01/14/2011       syang       105832 - Added new layer for discipline decline list.
  08/18/2011       syang       121201 - Remove the div container for DDL panel, it is used to display cancellation fields,
                                        the visibility of DDL fields are determined by the field markAsDdl. 
  11/11/2011       syang       127136 - System should not display cancel detail section for COI cancellation.
  05/14/2014       jyang       153212 - Add ddlDetailPanel div tag to surround cancel detail section to make the section
                                        able to hide.
  11/15/2018       lzhang      194100   add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<%@ include file="/core/invokeWorkflow.jsp" %>

<script type="text/javascript"
        src="<%=appPath%>/transactionmgr/cancelprocessmgr/js/performMultiCancellation.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>


<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>
<% boolean isCoiCancellation = false;
    if (request.getAttribute("cancellationLevel") != null && "COI".equalsIgnoreCase(request.getAttribute("cancellationLevel").toString())){
        isCoiCancellation = true;
    }
%>
<form action="<%=appPath%>/transactionmgr/cancelprocessmgr/performMultiCancellation.do" method=post name="cancelList">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <input type="hidden" name="validateResult" value="<c:out value="${status}"/>"/>
    <input type="hidden" name="processCode" value="<c:out value="${processCode}"/>"/>


    <%-- Show error message --%>
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
    <tr>
        <td colspan=8 align=center>
            <table id="cancellationTopForms" width="100%" border="0">
    <!--cancel information form -->
    <tr>
        <td align=center>
            <fmt:message key="pm.matainMultiCancel.cancelInfoFm.header" var="cacelInfoFormHeader" scope="request"/>
            <% String cacelInfoFormHeader = (String) request.getAttribute("cacelInfoFormHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%=  cacelInfoFormHeader %>"/>
                <jsp:param name="divId" value="cancelInfoDiv"/>
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="isLayerVisibleByDefault" value="true"/>
                <jsp:param name="collaspeTitleForPanel" value="<%=  cacelInfoFormHeader %>"/>
                <jsp:param name="isPanelCollaspedByDefault" value="false"/>
                <jsp:param name="excludePageFields" value="true"/>
                <jsp:param name="includeLayersWithPrefix" value="PM_CANCEL_INFO_FM"/>
                <jsp:param name="actionItemGroupId" value=""/>
            </jsp:include>
        </td>
    </tr>

    <tr>
        <td align=center>
            <fmt:message key="pm.matainMultiCancel.filterCriteriaFm.header" var="filterFormHeader" scope="request"/>
            <% String filterFormHeader = (String) request.getAttribute("filterFormHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%=  filterFormHeader %>"/>
                <jsp:param name="divId" value="filterDiv"/>
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="isLayerVisibleByDefault" value="true"/>
                <jsp:param name="collaspeTitleForPanel" value="<%=  filterFormHeader %>"/>
                <jsp:param name="isPanelCollaspedByDefault" value="false"/>
                <jsp:param name="excludePageFields" value="false"/>
                <jsp:param name="includeLayersWithPrefix" value="PM_FILTER_FM"/>
                <jsp:param name="actionItemGroupId" value="PM_FILTER_CLEAR_AIG"/>

            </jsp:include>
        </td>
    </tr>
            </table>
        </td>
    </tr>
    <c:if test="${dataBean != null}">
    <tr>
        <td align=center>
            <fmt:message key="pm.matainMultiCancel.cancelIterGh.header" var="panelTitleForCancelList" scope="page"/>
            <%
                String panelTitleForCancelList = (String) pageContext.getAttribute("panelTitleForCancelList");
            %>
            <oweb:panel panelTitleId="panelTitleIdForTailList" panelContentId="panelContentIdForTailList"
                        panelTitle="<%= panelTitleForCancelList %>">
                <tr>
                    <td colspan="6" align=center>
                        <c:set var="gridDisplayFormName" value="cancelList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="cancelListGrid" scope="request"/>
                        <c:set var="gridSortable" value="false" scope="request"/>
                        <c:set var="gridSizeFieldIdPrefix" value="cancel_"/>
                        <c:set var="cacheResultSet" value="true"/>
                        <% if (!isCoiCancellation) { %>
                        <c:set var="datasrc" value="#cancelListGrid1" scope="request"/>
                        <c:set var="gridDetailDivId" value="cancelDdlDiv" scope="request"/>
                        <% } %>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
                <% if (!isCoiCancellation) { %>
                <tr>
                    <td>
                        <div id="ddlDetailPanel" style="display:block;">
                            <fmt:message key="pm.maintainCancellation.ddl.form.header" var="ddlDetailHeader" scope="request"/>
                            <% String ddlDetailHeader = (String) request.getAttribute("ddlDetailHeader"); %>
                            <jsp:include page="/core/compiledFormFields.jsp">
                                <jsp:param name="headerText" value="<%= ddlDetailHeader%>"/>
                                <jsp:param name="isGridBased" value="true"/>
                                <jsp:param name="divId" value="cancelDdlDiv"/>
                                <jsp:param name="excludePageFields" value="true"/>
                                <jsp:param name="isLayerVisibleByDefault" value="true"/>
                                <jsp:param name="includeLayersWithPrefix" value="PM_CANCEL_DDL_FORM"/>
                            </jsp:include>
                        </div>
                    </td>
                 </tr>
                <% } %>
            </oweb:panel>
        </td>
    </tr>
    </c:if>
    <tr>
        <td align=center>
            &nbsp;
        </td>
    </tr>
    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="PM_PROCESS_CANCEL_AIG" layoutDirection="horizontal"/>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>
