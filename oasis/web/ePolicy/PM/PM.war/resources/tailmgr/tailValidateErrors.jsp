<%--
  Description:

  Author: yhchen
  Date: Jul 31, 2007

  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/15/2018       lzhang      194100   add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<script type="text/javascript" src="<%=appPath%>/tailmgr/js/processTail.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=appPath%>/tailmgr/js/tailValidationErrors.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>


<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<form action="<%=appPath%>/tailmgr/maintainTail.do" method=post name="coverageList">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <input type=hidden name=policyNo value="<c:out value="${policyHeader.policyNo}"/>">
    <input type="hidden" name="captureTransactionDetail" value="<c:out value="${captureTransactionDetail}"/>"/>
    <input type="hidden" name="captureCancellationDetail" value="<c:out value="${captureCancellationDetail}"/>"/>
    <input type="hidden" name="captureFinancePercentage" value="<c:out value="${captureFinancePercentage}"/>"/>
    <input type="hidden" name="accountingDate" value="<c:out value="${accountingDate}"/>"/>
    <input type="hidden" name="transactionCode" value="<c:out value="${transactionCode}"/>"/>
    <input type="hidden" name="processCode" value="<c:out value="${processCode}"/>"/>
    <%-- Show error message --%>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>

    <c:set var="policyHeaderDisplayMode" value="invisible"/>
    <tr>
        <td colspan=8 align=center>
            <%@ include file="/policymgr/policyHeader.jsp" %>
        </td>
    </tr>


    <tr>
        <td align=center>
                <tr>
                   <td>
                       <fmt:message key="pm.maintainTail.tailList.Header" var = "panelTitleForTailValidationError" scope="page"/>

                        <%
                         String panelTitleForTailValidationError = "";
                         if (pageContext.getAttribute("panelTitleForTailValidationError") != null) {
                             panelTitleForTailValidationError = (String) pageContext.getAttribute("panelTitleForTailValidationError");
                         }
                         %>
                         <oweb:panel panelTitleId="panelTitleIdForTailValidationError" panelContentId="panelContentIdForTailValidationError" panelTitle="<%= panelTitleForTailValidationError %>" >
                   </td>
                </tr>
                <tr>
                    <td colspan="6" align=center><br/>
                        <c:set var="gridDisplayFormName" value="coverageList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="coverageListGrid" scope="request"/>
                        <c:set var="gridSizeFieldIdPrefix" value="tail_"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
                </oweb:panel>
        </td>
    </tr>
    <tr>
        <td align = center>
            <oweb:actionGroup actionItemGroupId="PM_TAIL_ERROR_AIG" />
        </td>
    </tr>
    <%-- Display buttons --%>
<jsp:include page="/core/footerpopup.jsp"/>