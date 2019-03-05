<%--
  Description: Predictive Analytics page.

  Author: syang
  Date: May 06, 2011

  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2011 Delphi Technology, inc. (dti)
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<script type="text/javascript" src="js/processPredictiveAnalytics.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="resultListGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="reasonListGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="resultListGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="reasonListGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<form name="processOpaForm" action="processOpa.do" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
    <tr>
        <td align=center>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value=""/>
                <jsp:param name="divId" value="typeDiv"/>
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="isLayerVisibleByDefault" value="true"/>
                <jsp:param name="excludeAllLayers" value="true"/>
                <jsp:param name="hasPanelTitle" value="false"/>
            </jsp:include>
        </td>
    </tr>
    <%-- Requests grid --%>
    <tr>
        <td align=center colspan="6">
            <oweb:actionGroup actionItemGroupId="PM_OPA_PROCESS_AIG" layoutDirection="horizontal"/>
        </td>
    </tr>        
    <tr>
        <td align=center>
            <fmt:message key="pm.predictiveAnalytics.request.header" var="panelTitleForRequest" scope="page"/>
            <% String panelTitleForRequest = (String) pageContext.getAttribute("panelTitleForRequest"); %>
            <oweb:panel panelTitleId="panelTitleIdForRequest" panelContentId="panelContentIdForRequest"
                        panelTitle="<%= panelTitleForRequest %>">
                <tr>
                    <td colspan="6" align=center>
                        <c:set var="gridDisplayFormName" value="requestList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="requestListGrid" scope="request"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    <%-- Filter --%>
    <tr>
        <td align=center>
            <fmt:message key="pm.predictiveAnalytics.filter.header" var="panelTitleForFilter" scope="request"/>
            <% String panelTitleForFilter = (String) request.getAttribute("panelTitleForFilter"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%= panelTitleForFilter%>"/>
                <jsp:param name="divId" value="filterDiv"/>
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="isLayerVisibleByDefault" value="true"/>
                <jsp:param name="isPanelCollaspedByDefault" value="true"/>           
                <jsp:param name="excludePageFields" value="true"/>
                <jsp:param name="actionItemGroupId" value="PM_OPA_FILTER_AIG"/>
                <jsp:param name="includeLayersWithPrefix" value="OPA_SCORING_FILTER"/>
            </jsp:include>
        </td>
    </tr>
    <%-- Results grid --%>
    <tr>
        <td align=center>
            <fmt:message key="pm.predictiveAnalytics.result.header" var="panelTitleForResult" scope="page"/>
            <% String panelTitleForResult = (String) pageContext.getAttribute("panelTitleForResult"); %>
            <oweb:panel panelTitleId="panelTitleIdForResult" panelContentId="panelContentIdForResult"
                        panelTitle="<%= panelTitleForResult %>">
                <tr>
                    <td colspan="6" align=center>
                        <c:set var="gridDisplayFormName" value="resultList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="resultListGrid" scope="request"/>
                        <% dataBean = resultListGridDataBean;
                            gridHeaderBean = resultListGridHeaderBean; %>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    <%-- Reasons grid --%>
    <tr>
        <td align=center>
            <fmt:message key="pm.predictiveAnalytics.reason.header" var="panelTitleForReason" scope="page"/>
            <% String panelTitleForReason = (String) pageContext.getAttribute("panelTitleForReason"); %>
            <oweb:panel panelTitleId="panelTitleIdForReason" panelContentId="panelContentIdForReason"
                        panelTitle="<%= panelTitleForReason %>">
                <tr>
                    <td colspan="6" align=center>
                        <c:set var="gridDisplayFormName" value="reasonList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="reasonListGrid" scope="request"/>
                        <% dataBean = reasonListGridDataBean;
                            gridHeaderBean = reasonListGridHeaderBean; %>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>

    <tr>
        <td align=center colspan="6">
            <oweb:actionGroup actionItemGroupId="PM_OPA_PROCESS_AIG" layoutDirection="horizontal"/>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>