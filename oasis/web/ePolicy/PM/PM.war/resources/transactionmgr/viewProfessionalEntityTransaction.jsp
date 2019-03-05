<%--
  Description: View professional Entity Transaction.
  Author: syang
  Date: July 02, 2010

  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  08/06/2010       syang       103797 - Removed if condition and use pageentitlement to handle Print.
  03/20/2017       eyin        180675 - Changed message tag for UI change.
  11/15/2018       lzhang      194100   Add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2010 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<script type="text/javascript" src="<%=appPath%>/transactionmgr/js/viewProfessionalEntityDetail.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>
<form action="<%=appPath%>/transactionmgr/viewProfessionalEntityDetail.do" method=post name="transactionForm">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <input type="hidden" name="searchB" value="Y">
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
            <fmt:message key="pm.viewProfEntityDetails.search.header" var="panelTitleIdForSearch" scope="page"/>
            <%
                String panelTitleIdForSearch = (String) pageContext.getAttribute("panelTitleIdForSearch");
            %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%=panelTitleIdForSearch%>"/>
                <jsp:param name="divId" value="viewProfEntityDetailSearch"/>
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="isLayerVisibleByDefault" value="true"/>
                <jsp:param name="actionItemGroupId" value="PM_VIEW_PROF_SEARCH_AIG"/>
                <jsp:param name="excludeLayerIds" value=",PM_VIEW_PROF_TRANS,PM_VIEW_PROF_TRANS_DETAIL,"/>
            </jsp:include>
        </td>
    </tr>
    <tr>
        <td colspan="8" align="center">
            <oweb:actionGroup actionItemGroupId="PM_VIEW_PROF_PRINT_AIG"/>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.viewProfEntityDetails.trans.header" var="panelTitleIdForTransactionGrid" scope="page"/>
            <%
                String panelTitleIdForTransactionGrid = (String) pageContext.getAttribute("panelTitleIdForTransactionGrid");
            %>
            <oweb:panel panelTitleId="panelTitleIdForTransactionGrid" panelContentId="panelContentIdForTransactionGrid"
                        panelTitle="<%= panelTitleIdForTransactionGrid %>">
                <tr>
                    <td align=center>
                        <c:set var="gridDisplayFormName" value="transactionForm" scope="request"/>
                        <c:set var="gridDisplayGridId" value="transactionListGrid" scope="request"/>
                        <c:set var="datasrc" value="#transactionListGrid1" scope="request"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.viewProfEntityDetails.details.header" var="panelTitleIdForTransDetail" scope="page"/>
            <%
                String panelTitleIdForTransDetail = (String) pageContext.getAttribute("panelTitleIdForTransDetail");
            %>
            <oweb:panel panelTitleId="panelTitleIdForChangeDetailGrid"
                        panelContentId="panelContentIdForChangeDetailGrid"
                        panelTitle="<%= panelTitleIdForTransDetail %>">
                <tr>
                    <td>
                        <iframe id="iframeTransDetail" scrolling="no" allowtransparency="true" width="100%" height="155"
                                frameborder="0" src=""></iframe>
                    </td>
                </tr>
            </oweb:panel>

        </td>
    </tr>
    <tr>
        <td colspan="8" align="center">
            <oweb:actionGroup actionItemGroupId="PM_VIEW_PROF_PRINT_AIG"/>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>
