<%@ page import="dti.oasis.tags.OasisFormField" %>
<%@ page import="dti.oasis.util.BaseResultSet" %>
<%@ page import="dti.oasis.tags.XMLGridHeader" %>

<%@ page language="java"%>
<%--
  Description:
  
  Author: Jacky
  Date: Dec 10, 2008
  
  
  Revision Date    Revised By  Description
  ---------------------------------------------------
  12/10/2008       Jacky       create
  10/26/2017       kshen       Grid replacement. Change to use getParentWindow to get parent window.
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  ---------------------------------------------------
  (C) 2004 Delphi Technology, inc. (dti)
--%>
    
    <jsp:useBean id="polQteListGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
    <jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
    	<jsp:useBean id="polQteListGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>

<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<c:set var="isForDivPopup" value="true"></c:set>
<%@ include file="/core/headerpopup.jsp" %>
<jsp:include page="/cicore/common.jsp"/>
<script type="text/javascript" src="<%=cisPath%>/entitysearch/listpolicy/js/CIEntityListPolicy.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<form name="EntityListPolicyForm" action="loadEntityListPolicy.do" method="POST">
<%
    String entityName =request.getAttribute("entityName").toString();
    
%>
    <tr>
        <td colspan="6" align=left><%=entityName%>
        </td>
    </tr>
    <tr>
        <td colspan="6" align=center>
            <c:set var="gridDisplayFormName" value="CIRiskManagementForm" scope="request"/>
            <c:set var="gridDisplayGridId" value="polQteListGrid" scope="request"/>
            <c:set var="datasrc" value="#polQteListGrid1" scope="request"/>
            <%
                BaseResultSet dataBean = polQteListGridDataBean;
                XMLGridHeader gridHeaderBean = polQteListGridHeaderBean;
            %>
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


