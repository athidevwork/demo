<%@ page import="dti.oasis.util.BaseResultSet" %>
<%--
  Description: Maintain COI Renewal Detail page

  Author: Dzhang
  Date: June 21, 2010


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  07/05/2010       dzhang      Renamed this file.
  09/05/2011       ryzhao      124622 - For pages with multiple grids, update the name of data bean
                               for all but the first grid.
                               The name of data bean should be gridId + "DataBean".
  11/15/2018       lzhang      194100   Add buildNumber Parameter
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
<script type="text/javascript" src="js/maintainCoiRenewalEvent.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="COIRenewalDetailListGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<form action="" method=post name="COIRenewalDetailForm">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <%-- Show error message --%>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
    <tr>
        <td colspan="6" align=center>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="divId" value="COIRenewalEventDetailFilter"/>
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="isLayerVisibleByDefault" value="true"/>
                <jsp:param name="actionItemGroupId" value="PM_COI_REN_DET_FILTER_AIG"/>
                <jsp:param name="includeLayersWithPrefix" value="PM_COI_RENEWAL_EVENT_DETAIL_FILTER"/>
            </jsp:include>
    </tr>
    <tr>
        <td align=center>
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="COIRenewalDetailForm" scope="request"/>
                    <c:set var="gridDisplayGridId" value="COIRenewalDetailListGrid" scope="request"/>
                    <% BaseResultSet dataBean = COIRenewalDetailListGridDataBean; %>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
        <td>&nbsp;</td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>