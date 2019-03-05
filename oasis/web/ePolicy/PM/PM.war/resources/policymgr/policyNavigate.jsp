<%@ page language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%--
  Description: Policy navigation form

  Author: sxm
  Date: Aug 21, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  09/10/2010       wfu         111776: Replaced hardcode string with resource definition
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<fmt:message key="pm.maintainCoverage.navigate.header" var="navigation" scope="request"/>
<%  String navigation = (String) request.getAttribute("navigation"); %>
<jsp:include page="/core/compiledFormFields.jsp">
    <jsp:param name="divId" value="navigate" />
    <jsp:param name="isGridBased" value="FALSE" />
    <jsp:param name="headerText" value="<%= navigation %>" />
    <jsp:param name="includeLayersWithPrefix" value="PM_NAVIGATE" />
    <jsp:param name="isLayerVisibleByDefault" value="TRUE" />
    <jsp:param name="excludePageFields" value="TRUE" />
    <jsp:param name="isPanelCollaspedByDefault" value="FALSE" />
    <jsp:param name="collaspeTitleForPanel" value="<%= navigation %>" />
</jsp:include>
