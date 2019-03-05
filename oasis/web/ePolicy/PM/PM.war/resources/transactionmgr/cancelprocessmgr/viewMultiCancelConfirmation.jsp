<%--
  Description:  Multi Cancel Confirmation page
  
  Author: syang
  Date: Aug 18, 2011

  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/02/2011       syang       126447 - Added cancelLevel, it will be used to handle column in Transaction Detail grid for different level.
  11/15/2018       lzhang      194100   add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2011 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<%@ include file="/core/invokeWorkflow.jsp" %>
<script type="text/javascript" src="<%=appPath%>/transactionmgr/cancelprocessmgr/js/viewMultiCancelConfirmation.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<jsp:useBean id="confirmationListGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="confirmationListGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="transDetailListGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="transDetailListGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<form action="viewMultiCancelConfirmation.do" method=post name="cancelConfirmation">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <input type="hidden" name="cancelLevel" value="<c:out value="${param.cancellationLevel}"/>"/>
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
    <%
       dti.oasis.util.BaseResultSet dataBean = confirmationListGridDataBean;
       dti.oasis.tags.XMLGridHeader gridHeaderBean = confirmationListGridHeaderBean;
    %>
    <tr>
        <td align=center>
            <!-- Display Grid -->
            <fmt:message key="pm.maintainCancellation.confirmation.header" var="firstGridPanelTitle" scope="page"/>
            <% String firstGridPanelTitle = (String) pageContext.getAttribute("firstGridPanelTitle"); %>
            <oweb:panel panelTitleId="panelTitleIdForFirstGrid" panelContentId="panelContentIdForFirstGrid"
                        panelTitle="<%= firstGridPanelTitle %>">
                <tr>
                    <td colspan="6" align=center><br/>
                        <c:set var="gridDisplayFormName" value="confirmationListGridForm" scope="request"/>
                        <c:set var="gridDisplayGridId" value="confirmationListGrid" scope="request"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.maintainCancellation.confirmation.trans.header" var="secondGridPanelTitle" scope="page"/>
            <% String secondGridPanelTitle = (String) pageContext.getAttribute("secondGridPanelTitle"); %>
            <oweb:panel panelTitleId="panelTitleIdForTailList" panelContentId="panelContentIdForTailList"
                        panelTitle="<%= secondGridPanelTitle %>">
                <tr>
                    <td colspan="6" align=center>
                        <c:set var="gridDisplayFormName" value="transDetailListGridForm" scope="request"/>
                        <c:set var="gridDisplayGridId" value="transDetailListGrid" scope="request"/>
                        <% dataBean = transDetailListGridDataBean;
                            gridHeaderBean = transDetailListGridHeaderBean; %>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    <!-- It is used to populate the page fields in page. -->
    <tr>
        <td>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="divId" value="dummyDiv"/>
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="displayAsPanel" value="false"/>
                <jsp:param name="isLayerVisibleByDefault" value="false"/>
            </jsp:include>
        </td>
    </tr>
    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="PM_MULTI_CANCEL_CONF_AIG" layoutDirection="horizontal"/>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>
