<%--
  Description: View Credit Stacking page.

  Author: syang
  Date: May 26, 2011

  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2011 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<c:set var="isForDivPopup" value="true"></c:set>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<script type="text/javascript" src="js/viewCreditStacking.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<jsp:useBean id="firstDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="secondDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="firstGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="secondGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>
<form action="viewCreditStacking.do" method=post name="creditStackingFormList">
    <input type="hidden" name="searchB" value="Y"/>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
    <%-- Search --%>
    <tr>
        <td align=center>
            <fmt:message key="pm.creditStacking.search.header" var="panelTitleForSearch" scope="request"/>
            <% String panelTitleForSearch = (String) request.getAttribute("panelTitleForSearch"); %>
             <oweb:panel panelTitleId="panelTitleIdForSearch" panelContentId="panelContentIdForSearch"
                        panelTitle="<%= panelTitleForSearch %>">
             <tr>
                 <td align=center>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="divId" value="searchDiv"/>
                        <jsp:param name="isGridBased" value="false"/>
                        <jsp:param name="displayAsPanel" value="false"/>
                        <jsp:param name="hasPanelTitle" value="false"/>
                        <jsp:param name="excludeLayerIds" value=",PM_CREDIT_STACK_FILTER,"/>
                        <jsp:param name="isLayerVisibleByDefault" value="true"/>
                    </jsp:include>
                </td>
                <td align=center >
                      <oweb:actionGroup actionItemGroupId="PM_CREDIT_STACK_SCH_AIG"/>
                </td>
            </tr>
            </oweb:panel>
        </td>
    </tr>
    <%-- Display first grid --%>
    <%
       dti.oasis.util.BaseResultSet dataBean = firstDataBean;
       dti.oasis.tags.XMLGridHeader gridHeaderBean = firstGridHeaderBean;
    %>
    <tr>
        <td align=center>
            <!-- Display Grid -->
            <fmt:message key="pm.creditStacking.headerInfo.header" var="firstGridPanelTitle" scope="page"/>
            <% String firstGridPanelTitle = (String) pageContext.getAttribute("firstGridPanelTitle"); %>
            <oweb:panel panelTitleId="panelTitleIdForFirstGrid" panelContentId="panelContentIdForFirstGrid"
                        panelTitle="<%= firstGridPanelTitle %>">
                <tr>
                    <td colspan="6" align=center><br/>
                        <c:set var="gridDisplayFormName" value="firstGridFormList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="firstGrid" scope="request"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    <%-- Filter --%>
    <tr>
        <td align=center>
            <fmt:message key="pm.creditStacking.filter.header" var="panelTitleForFilter" scope="request"/>
            <% String panelTitleForFilter = (String) request.getAttribute("panelTitleForFilter"); %>
             <oweb:panel panelTitleId="panelTitleIdForFilter" panelContentId="panelContentIdForFilter"
                        panelTitle="<%= panelTitleForFilter %>" isPanelCollaspedByDefault="true">
             <tr>
                 <td align=center>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="divId" value="filterDiv"/>
                        <jsp:param name="isGridBased" value="false"/>
                        <jsp:param name="displayAsPanel" value="false"/>
                        <jsp:param name="hasPanelTitle" value="false"/>
                        <jsp:param name="isLayerVisibleByDefault" value="true"/>
                        <jsp:param name="excludePageFields" value="true"/>
                        <jsp:param name="includeLayersWithPrefix" value="PM_CREDIT_STACK_FILTER"/>
                    </jsp:include>
                </td>
                <td>
                      <oweb:actionGroup actionItemGroupId="PM_CREDIT_STACK_FT_AIG"/>
                </td>
            </tr>
            </oweb:panel>
        </td>
    </tr>
    <%-- Display Second grid --%>
    <%
       dataBean = secondDataBean;
       gridHeaderBean = secondGridHeaderBean;
    %>
    <tr>
        <td align=center>
            <!-- Display Grid -->
            <fmt:message key="pm.creditStacking.appliedInfo.header" var="secondGridPanelTitle" scope="page"/>
            <% String secondGridPanelTitle = (String) pageContext.getAttribute("secondGridPanelTitle"); %>
            <oweb:panel panelTitleId="panelTitleIdForSecondGrid" panelContentId="panelContentIdForSecondGrid"
                        panelTitle="<%= secondGridPanelTitle %>">
                <tr>
                    <td colspan="6" align=center><br/>
                        <c:set var="gridDisplayFormName" value="secondGridFormList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="secondGrid" scope="request"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="PM_CREDIT_STACK_CLOSE_AIG" layoutDirection="horizontal"/>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>