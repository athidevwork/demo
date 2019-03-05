<%@ page import="dti.oasis.util.BaseResultSet" %>
<%--
  Description: View Quick Pay Details - Risks/Coverages page

  Author: Dzhang
  Date: July 27, 2010


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  09/05/2011       ryzhao      124622 - For pages with multiple grids, update the name of data bean
                               for all but the first grid.
                               The name of data bean should be gridId + "DataBean".
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2010 Delphi Technology, inc. (dti)
--%>

<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<c:set var="isForDivPopup" value="false"></c:set>
<c:set var="skipHeaderFooterContent" value="true"></c:set>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<script type="text/javascript" src="js/viewQuickPayDetail.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="thirdGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<form action="js" method=post name="riskCoverageForm">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <tr>
        <td align=center>
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayGridId" value="thirdGrid" scope="request"/>
                    <% BaseResultSet dataBean = thirdGridDataBean; %>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
        <td>&nbsp;</td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>