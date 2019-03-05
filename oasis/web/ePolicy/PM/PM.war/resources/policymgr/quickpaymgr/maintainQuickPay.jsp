<%--
  Description: Maintain Quick Pay page

  Author: Dzhang
  Date: July 23, 2010


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  09/03/2010       dzhang      103800 - Include policyHeader.jsp
  09/09/2010       dzhang      103800 - Add hidden field requestFromPageItself, remove field policyId & needLoadGridPortion.
  03/10/2017       wli         180675 - Changed the error msg to be located in parent frame for UI change.
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2010 Delphi Technology, inc. (dti)
--%>

<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<script type="text/javascript" src="js/maintainQuickPay.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>


<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>
<form action="maintainQuickPay.do" method=post name="transHistoryList">
    <c:set var="policyHeaderDisplayMode" value="invisible"/>
    <tr>
        <td colspan=8 align=center>
            <%@ include file="/policymgr/policyHeader.jsp" %>
        </td>
    </tr>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <input type="hidden" name="requestFromPageItself" value="Y" />
    <input type="hidden" name="wipQpTransLogId" value="<c:out value="${wipQpTransLogId}"/>" />
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
    <tr>
        <td align=center>

            <fmt:message key="pm.manageQuickPay.search.header" var="searchFormHeader" scope="page"/>
            <%
                String searchFormHeader = (String) pageContext.getAttribute("searchFormHeader");
            %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%=  searchFormHeader %>"/>
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="divId" value="searchLayer"/>
                <jsp:param name="isLayerVisibleByDefault" value="true"/>
                <jsp:param name="includeLayersWithPrefix" value="PM_MANAGE_QP_SEARCH_CRITERIA"/>
                <jsp:param name="excludeLayerIds" value="PM_MANAGE_QP_SUMMARY"/>  
                <jsp:param name="actionItemGroupId" value="PM_QUICK_PAY_SEARCH_AIG"/>
            </jsp:include>
        </td>
    </tr>

    <tr>
        <td align=center>

            <fmt:message key="pm.manageQuickPay.summaryLayer.header" var="summaryHeader" scope="page"/>
            <%
                String summaryHeader = (String) pageContext.getAttribute("summaryHeader");
            %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%=  summaryHeader %>"/>
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="divId" value="summaryLayer"/>
                <jsp:param name="isLayerVisibleByDefault" value="true"/>
                <jsp:param name="includeLayersWithPrefix" value="PM_MANAGE_QP_SUMMARY"/>
            </jsp:include>
        </td>
    </tr>

    <tr>
        <td align=center>
            <fmt:message key="pm.manageQuickPay.transactionHistoryList.header" var="transHistoryGridPanelTitle"
                         scope="page"/>
                <%
            String transHistoryGridPanelTitle = (String) pageContext.getAttribute("transHistoryGridPanelTitle");
        %>
            <oweb:panel panelTitleId="panelTitleIdForTransHistoryGrid" panelContentId="panelContentIdForTransHistoryGrid"
                        panelTitle="<%= transHistoryGridPanelTitle %>">

        <tr>
            <td colspan="6">
                <oweb:actionGroup actionItemGroupId="PM_QUICK_PAY_TAB_AIG" layoutDirection="horizontal" cssColorScheme="gray"/>
            </td>
        </tr>

        <tr>
            <td colspan="6" align=center><br/>
              <c:set var="gridDisplayFormName" value="transHistoryList" scope="request" />
              <c:set var="gridDisplayGridId" value="transHistoryListGrid" scope="request"/>
              <%@ include file="/pmcore/gridDisplay.jsp" %>
            </td>
        </tr>
        </oweb:panel>
        </td>
    </tr>
   <tr>
       <td colspan="6" align=center>
           <oweb:actionGroup actionItemGroupId="PM_MANAGE_QUICK_PAY_AIG"/>
       </td>
   </tr>

<jsp:include page="/core/footerpopup.jsp"/>