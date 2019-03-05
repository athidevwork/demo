<%--
  Description:
  Author: zlzhu
  Date: Aug 23, 2007
  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  Aug 23, 2007         zlzhu      Created
  09/05/2011       ryzhao      124622 - For pages with multiple grids, update the name of data bean
                               and grid header bean for the first grid.
                               The name of data bean should be "dataBean".
                               The name of grid header bean should be "gridHeaderBean".
  11/15/2018       lzhang      194100   Add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<script type="text/javascript" src="<%=appPath%>/transactionmgr/js/maintainTransaction.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<form action="<%=appPath%>/transactionmgr/maintainTransaction.do" method=post name="transactionForm">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    
    <input type="hidden" name="termBaseRecordId" value="<c:out value="${policyHeader.termBaseRecordId}"/>">
    <input type="hidden" name="selectedTransactionLogId" value="<c:out value="${param.transactionLogId}"/>">
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
        <td colspan="8" align="center">
            <oweb:actionGroup actionItemGroupId="PM_VIEW_TRANSACTION_AIG"/>
        </td>
    </tr>
    <c:if test="${dataBean != null}">
    <tr>
        <td align=center>
            <fmt:message key="pm.transaction.transactionGrid.header" var="panelTitleIdForTransactionGrid" scope="page">
                <fmt:param value="${policyHeader.policyNo}"/>
            </fmt:message>
            <%
                String panelTitleIdForTransactionGrid = (String) pageContext.getAttribute("panelTitleIdForTransactionGrid");
            %>
            <oweb:panel panelTitleId="panelTitleIdForTransactionGrid" panelContentId="panelContentIdForTransactionGrid" panelTitle="<%= panelTitleIdForTransactionGrid %>">
            <tr>
                <td align=center>
                    <c:set var="gridDisplayFormName" value="transactionForm" scope="request"/>
                    <c:set var="gridDisplayGridId" value="transactionGrid" scope="request"/>
                    <c:set var="gridId" value="transactionGrid" scope="request"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>

            <fmt:message key="pm.transaction.form.header" var="transactionFormTitle" scope="page">
                <fmt:param value="${policyHeader.policyNo}"/>
            </fmt:message>
            <%
                String transactionFormTitle = (String) pageContext.getAttribute("transactionFormTitle");
            %>
            <tr>
                <td align=center>
                <c:set var="datasrc" value="#transactionGrid1" scope="request"/>
                <jsp:include page="/core/compiledFormFields.jsp">
                    <jsp:param name="headerText" value="<%=  transactionFormTitle %>" />
                    <jsp:param name="isGridBased" value="true" />
                    <jsp:param name="isLayerVisibleByDefault" value="true" />
                    <jsp:param name="excludePageFields" value="true" />
                </jsp:include>
                </td>
            </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.transaction.changeDetailGrid.header" var="panelTitleIdForChangeDetail" scope="page">
                <fmt:param value="${policyHeader.policyNo}"/>
            </fmt:message>
            <%
                String panelTitleIdForChangeDetailGrid = (String) pageContext.getAttribute("panelTitleIdForChangeDetail");
            %>
            <oweb:panel panelTitleId="panelTitleIdForChangeDetailGrid" panelContentId="panelContentIdForChangeDetailGrid" panelTitle="<%= panelTitleIdForChangeDetailGrid %>">
            <tr><td>
                <iframe id="iframeChangeDetails" scrolling="no" allowtransparency="true" width="98%" height="155" frameborder="0" src=""></iframe>
            </td></tr>
            </oweb:panel>

        </td>
   </tr>

   <tr>
        <td align=center>
            <fmt:message key="pm.transaction.transactionFormGrid.header" var="panelTitleIdForTransactionGrid" scope="page">
            </fmt:message>
            <%
                String panelTitleIdForTransactionFormGrid = (String) pageContext.getAttribute("panelTitleIdForTransactionGrid");
            %>
            <oweb:panel panelTitleId="panelTitleIdForTransactionFormGrid" panelContentId="panelContentIdForTransactionFormGrid" panelTitle="<%= panelTitleIdForTransactionFormGrid %>">
                <tr><td>
                    <iframe id="iframeTransactionForms" scrolling="no" allowtransparency="true" width="98%" height="155" frameborder="0" src=""></iframe>
                </td></tr>
            </oweb:panel>
       </td>
    </tr>

    </c:if>
    <tr>
        <td colspan="8" align="center">
            <oweb:actionGroup actionItemGroupId="PM_VIEW_TRANSACTION_AIG"/>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>
