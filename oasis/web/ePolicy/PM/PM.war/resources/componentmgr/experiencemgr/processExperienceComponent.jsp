<%--
  Description:Process Experience Component page.

  Author: gchitta
  Date: Feb 17, 2009


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/13/2018       tyang    194100   -Add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ include file="/core/header.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<script type="text/javascript" src="js/processExperienceComponent.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<form name="experienceComponent" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <%-- Show error message --%>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>

    <%-- Filter Layer --%>
    <fmt:message key="pm.processExperienceComponent.search.header" var="filterTitle" scope="request"/>
    <% String filterTitle = (String) request.getAttribute("filterTitle");%>
    <tr>
        <td align=center>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%=filterTitle%>"/>
                <jsp:param name="divId" value="experienceCompSearch"/>
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="isLayerVisibleByDefault" value="true"/>
                <jsp:param name="includeLayersWithPrefix" value="PM_EXPERIENCE_COMPONENT_SEARCH"/>
                <jsp:param name="actionItemGroupId" value="PM_EXPERIENCE_COMP_AIG"/>
            </jsp:include>
        </td>
    </tr>

    <c:if test="${dataBean != null && dataBean.rowCount > 0}">
    <tr>
        <td align=center>
            <fmt:message key="pm.processExperienceComponent.list.header" var="panelTitleForExperienceDetail"
                         scope="page"/>
            <%
                String panelTitleForExperienceDetail = (String) pageContext.getAttribute("panelTitleForExperienceDetail");
            %>
            <oweb:panel panelTitleId="panelTitleForExperienceDetail" panelContentId="panelContentIdForExperienceDetail"
                        panelTitle="<%= panelTitleForExperienceDetail %>">
                <tr>
                    <td colspan="6" align=center>
                        <c:set var="gridDisplayFormName" value="experienceDetailList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="experienceDetailListGrid" scope="request"/>
                        <c:set var="datasrc" value="#experienceDetailListGrid1" scope="request"/>
                        <c:set var="cacheResultSet" value="false"/>
                       <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    </c:if>
    <%
    // Initialize Sys Parms for JavaScript to use
    String sysParmCascadeDel = SysParmProvider.getInstance().getSysParm("PM_NUMDAYS_FOR_PRDT", "0");
    %>
    <script type="text/javascript">
        setSysParmValue("PM_NUMDAYS_FOR_PRDT", '<%=sysParmCascadeDel%>');
    </script>
    <jsp:include page="/core/footerpopup.jsp"/>