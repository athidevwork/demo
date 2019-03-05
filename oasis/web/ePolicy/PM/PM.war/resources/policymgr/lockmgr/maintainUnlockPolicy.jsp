<%--
  Description:
  JSP page for maintain unlock policies
  Author: yhchen
  Date: May 8, 2008


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%--
  Description:
  perform multi cancellation page
  Author: yhchen
  Date: Mar 19, 2008


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<script type="text/javascript"
        src="<%=appPath%>/policymgr/lockmgr/js/maintainUnlockPolicy.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<form action="<%=appPath%>/policymgr/lockmgr/maintainUnlockPolicy.do" method=post name="policyList">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <%-- Show error message --%>
    <tr>
        <td colspan=8>
            <oweb:message hideWarning="true"></oweb:message>
        </td>
    </tr>

    <c:if test="${dataBean != null}">

    <tr>
        <td align=center>
            <fmt:message key="pm.maitainUnlockPolicy.searchForm.header" var="searchFormHeader" scope="request"/>
            <% String searchFormHeader = (String) request.getAttribute("searchFormHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%=  searchFormHeader %>"/>
                <jsp:param name="divId" value="filterDiv"/>
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="isLayerVisibleByDefault" value="true"/>
                <jsp:param name="collaspeTitleForPanel" value="<%=  searchFormHeader %>"/>
                <jsp:param name="isPanelCollaspedByDefault" value="false"/>
                <jsp:param name="excludePageFields" value="false"/>
                <jsp:param name="includeLayersWithPrefix" value="PM_FILTER_FM"/>
                <jsp:param name="actionItemGroupId" value="PM_SEARCH_CLEAR_AIG"/>
            </jsp:include>
        </td>
    </tr>
    <!-- show warning message -->
    <tr>
        <td>
            <oweb:message hideError="true" hideInformation="true"></oweb:message>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.maitainUnlockPolicy.listHeader" var="resultHeader" scope="request"/>
            <%
                String panelTitleForPolicyList = (String) request.getAttribute("resultHeader");
            %>
            <oweb:panel panelTitleId="panelTitleIdForPolicyList" panelContentId="panelContentIdForPolicyList"
                        panelTitle="<%= panelTitleForPolicyList %>">
                <tr>
                    <td colspan="6" align=center>
                        <c:set var="gridDisplayFormName" value="policyList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="policyListGrid" scope="request"/>
                        <c:set var="cacheResultSet" value="true"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    </c:if>
    
    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="PM_UNLOCK_AIG" layoutDirection="horizontal"/>
        </td>
    </tr>
    <jsp:include page="/core/footerpopup.jsp"/>
