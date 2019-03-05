<%--
  Description:

  Author:
  Date:


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------
  06/29/2007       James       Added UI2 Changes
  -----------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<jsp:useBean id="pageBean" class="dti.oasis.util.PageBean" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request" />
<tr><td colspan="3" id="formfields">

<c:choose>
<c:when test="${UIStyleEdition=='2'}">
    <c:set var="panelTitle" value="${pageBean.title}"></c:set>
    <c:set var="panelContentId" value="panelContentForPageFiels"></c:set>
    <%@ include file="panelheader.jsp" %>
    <tr><td width="100%">
</c:when>
<c:otherwise>
    <fieldset><legend><%=pageBean.getTitle()%></legend>
</c:otherwise>
</c:choose>

<table width="100%" cellpadding="1" cellspacing="1" border="0">
<%
String row = "0";
boolean first = true;
%>
  <logic:iterate id="field" collection="<%=fieldsMap.getPageFields()%>"
      type="dti.oasis.tags.OasisFormField" >
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
  <tr>
<%
}
%>
<%String dataFdID = "C"+field.getFieldId().substring(field.getFieldId().indexOf('_')+1).toUpperCase();%>
<%request.setAttribute("datasrc","#testgrid1");%>
<%request.setAttribute("datafld",dataFdID);%>

  <%@include file="/core/tagfactory.jsp"%>

  </logic:iterate>
<%
if (!first) {
%>
  </tr>
<%
}
%>
</table>

<c:choose>
<c:when test="${UIStyleEdition=='2'}">
    </td></tr>
    <%@ include file="/core/panelfooter.jsp" %>
</c:when>
<c:otherwise>
    </fieldset>
</c:otherwise>
</c:choose>

</td></tr>
<tr><td style="font-size:1pt">&nbsp;</td></tr>

<script type="text/javascript">
    checkForm();
    setFocusToFirstEditableFormField("testgrid");
</script>