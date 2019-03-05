<%@ page import="dti.oasis.util.BaseResultSet" %>
<%@ page import="dti.oasis.tags.XMLGridHeader" %>
<%--
  Description: Display calculated dividend list

  Author: wfu
  Date: Mar 30, 2011


  Revision Date    Revised By  Description
  ---------------------------------------------------
  09/05/2011       ryzhao      124622 - For pages with multiple grids, update the name of data bean
                               and grid header bean for all but the first grid.
                               The name of data bean should be gridId + "DataBean".
                               The name of grid header bean should be gridId + "HeaderBean".
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  ---------------------------------------------------
  (C) 2011 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<c:set var="isForDivPopup" value="true"></c:set>
<c:set var="skipHeaderFooterContent" value="true"></c:set>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<jsp:useBean id="calculatedDividendListGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="calculatedDividendListGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<script type="text/javascript" src="js/calculatedDividend.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form action="javascript:void();" method="post" name="calculatedDividendList">
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
    <tr>
        <td colspan="6">
            <oweb:actionGroup actionItemGroupId="PM_DIV_PRO_GRID_AIG" layoutDirection="horizontal"
                    cssColorScheme="gray"/>
        </td>
    </tr>
    <%
       BaseResultSet dataBean = calculatedDividendListGridDataBean;
       XMLGridHeader gridHeaderBean = calculatedDividendListGridHeaderBean;       
    %>
    <tr>
        <td>
            <c:set var="gridDisplayFormName" value="calculatedDividendList" scope="request"/>
            <c:set var="gridDisplayGridId" value="calculatedDividendListGrid" scope="request"/>
            <%@ include file="/pmcore/gridDisplay.jsp" %>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>