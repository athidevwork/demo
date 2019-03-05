<%@ page import="dti.oasis.tags.OasisFormField" %>
<%@ page import="dti.oasis.tags.XMLGridHeader" %>
<%@ page import="dti.oasis.util.BaseResultSet" %>
<%@ page language="java"%>
<%--
  Description:
  
  Author: ldong
  Date: Mar 24, 2008
  
  
  Revision Date    Revised By  Description
  ---------------------------------------------------
  11/26/2008  Jacky  Added a process to close parent window's 'process div'
  10/26/2017       kshen       Grid replacement. Change to use getParentWindow to get parent window.
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  ---------------------------------------------------
  (C) 2004 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<c:set var="isForDivPopup" value="true"></c:set>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file= "/cicore/common.jsp" %> 
<script type="text/javascript" src="<%=cisPath%>/entitysearch/listrole/js/CIEntityListRole.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<form name="EntityListRoleForm" action="ciEntityListRole.do" method="POST">
    <jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
    <jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
    <jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
    <input type='hidden' name='fromDocProcess' value='<c:out value="${fromDocProcess}"/>' >
<%
    String entityName =request.getAttribute("entityName").toString();
%>
    <tr>
        <td colspan="6" align=left><%=entityName%>
        </td>
    </tr>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
    <tr>
        <td align=center>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="divId" value="filterDiv"/>
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="isLayerVisibleByDefault" value="true"/>
                <jsp:param name="excludePageFields" value="true"/>
                <jsp:param name="includeLayerIds" value="CI_LIST_ROLES_FILTER"/>
                <jsp:param name="headerTextLayerId" value="CI_LIST_ROLES_FILTER"/>
            </jsp:include>
        </td>
    </tr>
    <tr>
        <td colspan="6" align=center>
            <c:set var="gridDisplayFormName" value="CIRiskManagementForm" scope="request"/>
            <c:set var="gridDisplayGridId" value="roleListGrid" scope="request"/>
            <c:set var="datasrc" value="#roleListGrid1" scope="request"/>
            <%@ include file="/core/gridDisplay.jsp" %>
        </td>
    </tr>
    <tr>
        <td colspan="6" align=center>
            <oweb:actionGroup actionItemGroupId="CI_LISTROLE_PAGE_AIG" layoutDirection="horizontal"
                              cssColorScheme="blue"/>
        </td>
    </tr>
    <jsp:include page="/core/footerpopup.jsp"/>
<script type="text/javascript">
    if(getParentWindow() != null && getParentWindow().closeProcessingDiv) {
        getParentWindow().closeProcessingDiv();
    }
</script>

