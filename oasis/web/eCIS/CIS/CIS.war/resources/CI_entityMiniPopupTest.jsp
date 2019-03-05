<%@ page language="java"%>
<%--
  Description:

  Author: Gerald C. Carney
  Date: Apr 22, 2004


  Revision Date    Revised By  Description
  ---------------------------------------------------


  ---------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="elementsMap" class="dti.oasis.tags.OasisElements" scope="request"/>

<%@include file="/core/header.jsp" %>

<%@include file="CI_common.jsp" %>

<FORM name="CIEntityMiniTestForm" method="POST">
<script language="javascript">
function handleOnChange(field) {
  return;
}
function btnClick(btnID)
{
  if (btnID == 'MINIPOPUP' || btnID == 'minipopup') {
    openEntityMiniPopupWin(document.forms[0].entity_entityPK.value);
  }
}
</script>
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
  <%@include file="/core/tagfactory.jsp"%>
  </logic:iterate>
<%
  if (!first) {
%>
  </tr>
<%
  }
%>

  <tr>
    <td align="center" colspan="6">
      <oweb:button element="<%=elementsMap.get(\"BTN_TEST\")%>"/>
    </td>
  </tr>

<jsp:include page="/core/footer.jsp" />
