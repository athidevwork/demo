<%--
  Description: Maintain Batch Renewal Detail page

  Author: Joe Shen
  Date: October 04, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  09/10/2010       wfu         111776: Replaced hardcode string with resource definition
  03/12/2013       adeng       138243: Added a hidden variable to get value of "isAllExcluded" from request, it will be
                               used as the condition to show/hide Issue, Batch Print, ReRate, and Delete WIP buttons for
                               selected event.
  11/15/2018       lzhang      194100   add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2007 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<c:set var="isForDivPopup" value="false"></c:set>
<c:set var="skipHeaderFooterContent" value="true"></c:set>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<script type="text/javascript" src="js/maintainBatchRenewalDetailProcess.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<form action="maintainBatchRenewalDetailProcess.do" method=post name="batchRenewalDetailProcessForm">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <input type="hidden" name="isAllExcluded" value="<c:out value="${isAllExcluded}"/>"/>
    <%-- Show error message --%>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
    <tr>
        <fmt:message key="pm.maintainBatchRenewalDetailProcess.filter.header" var="batchRenewal" scope="request"/>
        <%  String batchRenewal = (String) request.getAttribute("batchRenewal"); %>
        <td align=center>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%= batchRenewal %>" />
                <jsp:param name="divId" value="batchRenewalDetailFilter" />
                <jsp:param name="isGridBased" value="false" />
                <jsp:param name="excludeAllLayers" value="true" />
                <jsp:param name="actionItemGroupId" value="PM_BAT_REN_DET_FILTER_AIG" />
            </jsp:include>
        </td>
    </tr>

    <c:if test="${dataBean != null && dataBean.rowCount != 0}">
    <tr>
        <td align=center>
            <%
                String panelTitleIdForBatchRenewalDetail = (String) request.getAttribute("resultHeader");
            %>
            <oweb:panel panelTitleId="panelTitleIdForBatchRenewalDetail" panelContentId="panelContentIdForBatchRenewalDetail"
                        panelTitle="<%= panelTitleIdForBatchRenewalDetail %>">
                <tr>
                    <td colspan="6">
                        <oweb:actionGroup actionItemGroupId="PM_BAT_RENEW_DETAIL_AIG" layoutDirection="horizontal" cssColorScheme="gray"/>
                    </td>
                </tr>
                <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="batchRenewalDetailProcessForm" scope="request"/>
                    <c:set var="gridDisplayGridId" value="batchRenewalDetailListGrid" scope="request"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            </oweb:panel>
            <tr>
                <td>&nbsp;</td>
            </tr>
        </td>
    </tr>
    </c:if>
<jsp:include page="/core/footerpopup.jsp"/>