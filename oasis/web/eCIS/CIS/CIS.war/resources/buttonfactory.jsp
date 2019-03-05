<%@ page language="java"%>
<%--
  Description: Builds the buttons on the page

  Author: jbe
  Date: Nov 23, 2004


  Revision Date    Revised By  Description
  ---------------------------------------------------
  07/13/2007       Mark        Added UI2 Changes

  ---------------------------------------------------
  (C) 2004 Delphi Technology, inc. (dti)
--%>
<jsp:useBean id="elementsMap" class="dti.oasis.tags.OasisElements" scope="request"/>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<logic:iterate id="elementName" collection="<%=elementsMap.keySet()%>" type="java.lang.String" >
<oweb:button element="<%=elementsMap.get(elementName)%>" styleClass="buttons" />&nbsp;&nbsp;
</logic:iterate>