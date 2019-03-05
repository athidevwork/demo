<%--
  Description:

  Author: Bhong
  Date: Mar 25, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  -----------------------------------------------------------------------------
  (C) 2007 Delphi Technology, inc. (dti)
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<c:set var="isForDivPopup" value="true"></c:set>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<%-- Show error message --%>
<tr>
    <td colspan=8>
        <oweb:message/>
    </td>
</tr>
<br>
<form action="" name ="cycleDetailForm">
<%@ include file="/pmcore/commonFormHeader.jsp" %>

<c:if test="${dataBean.rowCount!=0}">
    <tr>
        <td colspan="6" align=center>
            <c:set var="gridDisplayFormName" value="cycleDetailForm" scope="request"/>
            <c:set var="gridDisplayGridId" value="cycleDetailGrid" scope="request"/>
            <c:set var="cacheResultSet" value="false"/>
            <c:set var="selectable" value="true"/>
            <%@ include file="/pmcore/gridDisplay.jsp" %>
        </td>
    </tr>
</c:if>
<tr>
    <td colspan="6" align=center>
        <oweb:actionGroup actionItemGroupId="PM_CYCLE_DETAIL_AIG"/>
    </td>
</tr>

<jsp:include page="/core/footerpopup.jsp"/>