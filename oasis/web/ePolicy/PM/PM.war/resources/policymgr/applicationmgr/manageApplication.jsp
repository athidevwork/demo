<%--
  Description: Application management page.

  Author: bhong
  Date: May 09, 2012


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2012 Delphi Technology, inc. (dti)
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ include file="/core/header.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<script type="text/javascript" src="js/manageApplication.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<form name="applicationList" action="<%=appPath%>/policymgr/applicationmgr/manageApplication.do" method="post">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
    <%--Display filter layer--%>
    <tr>
        <td align=center>
            <fmt:message key="pm.applicationManagement.search.header" var="filterTitle"
                         scope="request"/>
            <% String filterTitle = (String) request.getAttribute("filterTitle"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%= filterTitle%>"/>
                <jsp:param name="divId" value="filterDiv"/>
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="isLayerVisibleByDefault" value="true"/>
                <jsp:param name="actionItemGroupId" value="PM_APP_MGR_FILTER_AIG"/>
                <jsp:param name="includeLayersWithPrefix" value="PM_APP_MANAGE_FILTER_LAYER"/>
            </jsp:include>
        </td>
    </tr>
    <%--Display application list --%>
    <c:if test="${loadGridData!='N'}">
    <tr>
        <td align=center>
            <fmt:message key="pm.applicationManagement.detail.header" var="detailTitle" scope="page"/>
            <%
                String detailTitle = (String) pageContext.getAttribute("detailTitle");
            %>
            <oweb:panel panelTitleId="panelTitleIdForApp" panelContentId="panelContentIdForApp" panelTitle="<%= detailTitle %>" >
            <tr>
                <td colspan="6">
                    <oweb:actionGroup actionItemGroupId="PM_APP_MGR_GRID_AIG" layoutDirection="horizontal" cssColorScheme="gray"/>
                </td>
            </tr>
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="applicationList" scope="request" />
                    <c:set var="gridDisplayGridId" value="applicationListGrid" scope="request" />
                    <c:set var="gridDetailDivId" value="applicationDetailDiv" scope="request" />
                    <c:set var="selectable" value="true"/>
                    <c:set var="gridSortable" value="false"/>
                    <c:set var="cacheResultSet" value="false"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="PM_APP_MGR_AIG" layoutDirection="horizontal"/>
        </td>
    </tr>
    </c:if>

<jsp:include page="/core/footerpopup.jsp"/>