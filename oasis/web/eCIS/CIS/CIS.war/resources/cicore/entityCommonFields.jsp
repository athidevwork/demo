<%@ page import="dti.ci.helpers.ICIConstants" %>
<%@ page import="dti.oasis.http.Module" %>
<%@ page language="java" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%--
  Description:

  Author: kshen
  Date: 4/19/2018


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<input type="hidden" name="<%=ICIConstants.PK_PROPERTY%>" value="<c:out value="${pk}"/>"/>
<input type="hidden" name="<%=ICIConstants.ENTITY_NAME_PROPERTY%>" value="<c:out value="${entityName}"/>"/>
<input type="hidden" name="<%=ICIConstants.ENTITY_TYPE_PROPERTY%>" value="<c:out value="${entityType}"/>"/>
