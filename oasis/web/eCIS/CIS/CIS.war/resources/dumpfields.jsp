<%@ page language="java"%>
<%--
  Description:

  Author: jbe
  Date: Nov 24, 2004


  Revision Date    Revised By  Description
  ---------------------------------------------------


  ---------------------------------------------------
  (C) 2004 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<jsp:useBean class="dti.oasis.tags.OasisFields" id="fieldsMap" scope="request"/>
<%
    ArrayList list = null;
    String layerId = request.getParameter("layerId");
    if(StringUtils.isBlank(layerId))
        list = fieldsMap.getPageFields();
    else
        list = fieldsMap.getLayerFields(layerId);
    if(list==null) return;
%>
<%
String row = "0";
boolean first = true;
%>
  <logic:iterate id="field" collection="<%=list%>" type="dti.oasis.tags.OasisFormField" >
<%
if (!field.getRowNum().equals(row)) {
  row = field.getRowNum();
  if (first)
    first = false;
  else {
%>
  </tr>
<%
  }
%>
  <tr valign="top">
<%
}
%>
  <%@include file="/core/tagfactory.jsp"%>
  </logic:iterate>
<%
if (!first) {
%>
  </tr>
<%
}
%>