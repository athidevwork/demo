<%@ page import="dti.oasis.util.BaseResultSet" %>
<%@ page import="dti.oasis.tags.XMLGridHeader" %>
<%--
  Description: Preview dividend list

  Author: wfu
  Date: March 13, 2012


  Revision Date    Revised By  Description
  ---------------------------------------------------

  ---------------------------------------------------
  (C) 2012 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<c:set var="isForDivPopup" value="true"></c:set>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<form action="javascript:void();" method="post" name="calculatedDividendList">
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.dividend.process.calculated.header" var="panelTitleForCalculatedDividend" scope="page"/>
            <% String calculatedDividend = (String) pageContext.getAttribute("panelTitleForCalculatedDividend"); %>
            <oweb:panel panelTitleId="panelTitleForCalculatedDividend"
                        panelContentId="panelContentIdForCalculatedDividend"
                        panelTitle="<%= calculatedDividend %>">
            <tr>
                <td align=center>
                <c:set var="gridDisplayFormName" value="calculatedDividendList" scope="request"/>
                <c:set var="gridDisplayGridId" value="calculatedDividendListGrid" scope="request"/>
                <c:set var="datasrc" value="#calculatedDividendListGrid1" scope="request"/>
                <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td colspan="6" align=center>
            <oweb:actionGroup actionItemGroupId="PM_DIV_PRE_AIG" layoutDirection="horizontal"
                              cssColorScheme="gray"/>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>