<%@ page language="java"%>
<%--
  Description:

  Author: bhong
  Date: Dec 23, 2004


  Revision Date    Revised By  Description
  ---------------------------------------------------
 06/29/2007       James       Added UI2 Changes
  ---------------------------------------------------
  (C) 2004 Delphi Technology, inc. (dti)
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<jsp:useBean id="pageBean" class="dti.oasis.util.PageBean" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request" />
<%
    ArrayList list = null;
    String layerId = request.getParameter("layerId");
    list = fieldsMap.getLayerFields(layerId);
    if (list == null) return;
    String divId=  request.getParameter("divId");
    if( divId.equals("Address_List_Grid_Header_Layer_USA_read_Only"))
     divId="AddressusaDivID";
    else
    divId="AddressForeignDivID";

    request.setAttribute("dumpLayerPanelTitle",request.getParameter("layerTitle"));
    request.setAttribute("dumpLayerPanelContentID","panelContentFor"+layerId);
%>
<DIV  style="display:block;">
<tr id="<%=layerId%>"><td colspan="3" id="<%=divId%>">

<c:choose>
<c:when test="${UIStyleEdition=='2'}">
      <c:set var="panelTitle" value="${dumpLayerPanelTitle}"></c:set>
      <c:set var="panelContentId" value="${dumpLayerPanelContentID}"></c:set>
    <%@ include file="/core/panelheader.jsp" %>
    <tr><td width="100%">
</c:when>
<c:otherwise>
    <fieldset><legend><%=request.getParameter("layerTitle")%></legend>
</c:otherwise>
</c:choose>

<table width="100%" cellpadding="1" cellspacing="1" border="0">
<%
    String row = "0";
    boolean first = true;
%>
  <logic:iterate id="field" collection="<%=list%>"
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
    <%String dataFdID = "C" + field.getFieldId().substring(field.getFieldId().indexOf('_') + 1).toUpperCase();%>
    <%
        String datasource = request.getParameter("datasource");
    %>
    <%request.setAttribute("datasrc", datasource);%>
    <%request.setAttribute("datafld", dataFdID);%>

    <%  String fn = null;
        try {
            fn = (String) request.getParameter("formName");
            if (StringUtils.isBlank(fn))
                fn = "forms[0]";
        } catch (ClassCastException ce) {
            fn = "forms[0]";
        }
        request.setAttribute("formName", fn);
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
</DIV>

<tr><td style="font-size:1pt">&nbsp;</td></tr>
<script language="javascript">
    checkForm();
    setFocusToFirstEditableFormField("testgrid");
</script>