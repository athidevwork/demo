<%@ page import="dti.oasis.util.BaseResultSet" %>
<%--
  Description:

  Author: Bhong
  Date: Mar 30, 2010


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  09/05/2011       ryzhao      124622 - For pages with multiple grids, update the name of data bean
                               for all but the first grid.
                               The name of data bean should be gridId + "DataBean".
  11/15/2018       lzhang      194100   add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2010 Delphi Technology, inc. (dti)
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<c:set var="isForDivPopup" value="true"></c:set>
<c:set var="skipHeaderFooterContent" value="true"></c:set>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<script type="text/javascript" src="js/viewTransactionSnapshot.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<jsp:useBean id="coverageComponentSnapshotGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<tr>
    <td>
        <c:set var="gridDisplayFormName" value="coverageComponentSnapshotForm" scope="request"/>
        <c:set var="gridDisplayGridId" value="coverageComponentSnapshotGrid" scope="request"/>
        <% BaseResultSet dataBean = coverageComponentSnapshotGridDataBean; %>
        <%@ include file="/pmcore/gridDisplay.jsp" %>
    </td>
</tr>
<jsp:include page="/core/footerpopup.jsp"/>