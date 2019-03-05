<%@ page import="dti.oasis.tags.XMLGridHeader" %>
<%@ page import="dti.oasis.util.BaseResultSet" %>
<%--
  Description: CIS Summary Billing Grid List iframe

  Author: cyzhao
  Date: June 17, 2008


  Revision Date    Revised By  Description
  ---------------------------------------------------
  09/28/2011       Michael Li   for issue 125283
  ---------------------------------------------------
  (C) 2008 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<c:set var="isForDivPopup" value="true"></c:set>

<c:set var="headerHidden" value="Y"></c:set>
<c:set var="isTabMenuHidden" value="Y"></c:set>
<%@ include file="/core/header.jsp" %>
<c:if test="${isTabMenuHidden == 'Y'}">
    <script type="text/javascript"> if (getObject("pageHeader")) {
        getObject("pageHeader").style.display = "none";
    } </script>
</c:if>
<jsp:include page="/cicore/common.jsp"/>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="billingListGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>

<form action="javascript:void();" method="post" >
    <jsp:include page="/cicore/commonFormHeader.jsp"/>
    <tr>
        <td>
            <c:set var="gridDisplayFormName" value="billingList" scope="request"/>
            <c:set var="gridDisplayGridId" value="billingListGrid" scope="request"/>
            <c:set var="gridSizeFieldIdPrefix" value="billing_"/>
            <%
                XMLGridHeader gridHeaderBean = billingListGridHeaderBean;
            %>
            <%@ include file="/core/gridDisplay.jsp" %>
        </td>
    </tr>
    <c:if test="${headerHidden == 'Y'}">
        <jsp:include page="/core/footerpopup.jsp"/>
    </c:if>

    <c:if test="${headerHidden != 'Y'}">
        <jsp:include page="/core/footer.jsp"/>
    </c:if>