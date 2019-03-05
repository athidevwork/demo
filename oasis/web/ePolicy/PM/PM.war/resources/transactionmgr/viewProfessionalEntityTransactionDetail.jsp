<%--
  Description: View professional Entity Transaction.
  Author: syang
  Date: July 02, 2010

  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------

  -----------------------------------------------------------------------------
  (C) 2010 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<c:set var="isForDivPopup" value="true"></c:set>
<c:set var="skipHeaderFooterContent" value="true"></c:set>
<%@ include file="/core/headerpopup.jsp" %>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<form action="javascript:void();" method=post name="dummyForm">
    <tr>
        <td align=center>
            <c:set var="gridDisplayFormName" value="transDetailForm" scope="request"/>
            <c:set var="gridDisplayGridId" value="TransDetailGrid" scope="request"/>
            <%@ include file="/pmcore/gridDisplay.jsp" %>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>
