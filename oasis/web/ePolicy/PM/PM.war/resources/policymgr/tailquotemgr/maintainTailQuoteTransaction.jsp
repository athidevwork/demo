<%--
  Description:
  For maintain Tail Quote transaction
  
  Author: yhchen
  Date: Jan 23, 2008


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  03/20/2013       adeng       142688 - Add div 'tailListPanel'
  06/25/2014       jyang2      155366 - Add scrollbar for tail quote list iframe.
  03/10/2017       wli         180675 - Changed the error msg to be located in parent frame for UI change.
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<%@ include file="/core/invokeWorkflow.jsp" %>

<script type="text/javascript" src="<%=appPath%>/policymgr/tailquotemgr/js/maintainTailQuoteTransaction.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>


<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<form action="<%=appPath%>/policymgr/tailquotemgr/maintainTailQuote.do" method=post name="transactionList">
<%@ include file="/pmcore/commonFormHeader.jsp" %>

<input type="hidden" name="processCode" value="<c:out value="${processCode}"/>"/>

<%-- Show error message --%>
<tr>
    <td colspan=8>
        <%
            if (pmUIStyle.equals("T")) {
        %>
        <oweb:message displayMessagesOnParent="true"/>
        <%
            }
        %>
        <%
            if (pmUIStyle.equals("B")) {
        %>
        <oweb:message/>
        <%
            }
        %>
    </td>
</tr>

<c:set var="policyHeaderDisplayMode" value="invisible"/>
<tr>
    <td colspan=8 align=center>
        <%@ include file="/policymgr/policyHeader.jsp" %>
    </td>
</tr>


<c:if test="${dataBean != null}">
<tr>
    <td align=center>
        <fmt:message key="pm.maintainTailQuote.transactionList.header" var="panelTitleForTransactionlList"
                     scope="page"/>
        <%
            String panelTitleForTransactionList = (String) pageContext.getAttribute("panelTitleForTransactionlList");
        %>
        <oweb:panel panelTitleId="panelTitleIdForTailTranList" panelContentId="panelContentIdForTailTranList"
                    panelTitle="<%= panelTitleForTransactionList %>">
            <tr>
                <td colspan="6">
                    <oweb:actionGroup actionItemGroupId="PM_TAIL_QUOTE_TRAN_AIG" layoutDirection="horizontal"
                                      cssColorScheme="gray" cssWidthInPX="75"/>
                </td>
            </tr>
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="transactionList" scope="request"/>
                    <c:set var="gridDisplayGridId" value="transactionListGrid" scope="request"/>
                    <c:set var="gridDetailDivId" value="transactionDetailDiv" scope="request"/>
                    <c:set var="gridSizeFieldIdPrefix" value="transaction_"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            <tr> &nbsp;</tr>
            <tr>
                <td align=center>
                    <fmt:message key="pm.maintainTailQuote.transactionForm.header" var="tranDetailFormHeader"
                                 scope="request"/>
                    <% String tranDetailFormHeader = (String) request.getAttribute("tranDetailFormHeader"); %>
                    <c:set var="datasrc" value="#transactionListGrid1" scope="request"/>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="headerText" value="<%=  tranDetailFormHeader %>"/>
                        <jsp:param name="isGridBased" value="true"/>
                        <jsp:param name="divId" value="transactionDetailDiv"/>
                        <jsp:param name="isLayerVisibleByDefault" value="true"/>
                        <jsp:param name="excludePageFields" value="true"/>
                        <jsp:param name="includeLayersWithPrefix" value="PM_TAIL_QUOTE_TRAN_FM"/>
                    </jsp:include>
                </td>
            </tr>
        </oweb:panel>
        <tr>
            <td align=center>
                <fmt:message key="pm.maintainTail.filterCriteria.Header" var="filterFormHeader" scope="request"/>
                <% String filterFormHeader = (String) request.getAttribute("filterFormHeader"); %>
                <c:set var="datasrc" value="" scope="request"/>
                <c:set var="datafld" value="" scope="request"/>
                <jsp:include page="/core/compiledFormFields.jsp">
                    <jsp:param name="headerText" value="<%=  filterFormHeader %>"/>
                    <jsp:param name="divId" value="filterDiv"/>
                    <jsp:param name="isGridBased" value="false"/>
                    <jsp:param name="isLayerVisibleByDefault" value="true"/>
                    <jsp:param name="collaspeTitleForPanel" value="<%=  filterFormHeader %>"/>
                    <jsp:param name="isPanelCollaspedByDefault" value="false"/>
                    <jsp:param name="excludePageFields" value="false"/>
                    <jsp:param name="includeLayersWithPrefix" value="PM_TAIL_QUOTE_FILTER"/>
                    <jsp:param name="actionItemGroupId" value="PM_TAIL_FILTER_AIG"/>
                </jsp:include>
            </td>
        </tr>
        <tr>
            <td align=center>
                <div id="tailListPanel" style="display:block;">
                <fmt:message key="pm.maintainTailQuote.tailQuoteList.header" var="tailListHeader" scope="request"/>
                <% String tailListHeader = (String) request.getAttribute("tailListHeader"); %>
                <oweb:panel panelTitleId="panelTitleIdForTailList" panelContentId="panelContentIdForTailList"
                            panelTitle="<%= tailListHeader %>">
                    <iframe id="iframeSeparateTailQuotes" scrolling="auto" allowtransparency="true" width="98%"
                            height="320"
                            frameborder="0" src=""></iframe>
                </oweb:panel>
                </div>
            </td>
        </tr>
    </td>
</tr>
</c:if>

<tr>
    <td align=center>
        <oweb:actionGroup actionItemGroupId="PM_TAIL_QUOTE_AIG" layoutDirection="horizontal"/>
    </td>
</tr>


<jsp:include page="/core/footerpopup.jsp"/>