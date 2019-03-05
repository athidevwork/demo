<%@ page import="dti.oasis.util.BaseResultSet" %>
<%@ page import="dti.oasis.tags.XMLGridHeader" %>
<%--
  Description:

  Author: zlzhu
  Date: Aug 23, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  Aug 23, 2007         zlzhu      Created
  09/05/2011       ryzhao      124622 - For pages with multiple grids, update the name of data bean
                               and grid header bean for all but the first grid.
                               The name of data bean should be gridId + "DataBean".
                               The name of grid header bean should be gridId + "HeaderBean".
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<c:set var="isForDivPopup" value="true"></c:set>
<c:set var="skipHeaderFooterContent" value="true"></c:set>
 <%@ include file="/core/headerpopup.jsp" %>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>
<jsp:useBean id="changeDetailGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="changeDetailGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<form action="javascript:void();" method=post name="dummyForm">
    <c:if test="${changeDetailGridDataBean != null}">
    <tr>
        <td align=center><br/>
                    <c:set var="gridDisplayFormName" value="transactionForm" scope="request"/>
                    <c:set var="gridDisplayGridId" value="changeDetailGrid" scope="request"/>
                    <c:set var="gridSizeFieldIdPrefix" value="detail_"/>
                    <%  BaseResultSet dataBean = changeDetailGridDataBean;
                        XMLGridHeader gridHeaderBean = changeDetailGridHeaderBean; %>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
        </td>
    </tr>
    </c:if>
<jsp:include page="/core/footerpopup.jsp"/>
