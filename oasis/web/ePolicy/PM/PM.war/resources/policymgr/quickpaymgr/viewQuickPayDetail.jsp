<%--
  Description: View Quick Pay Details page

  Author: Dzhang
  Date: July 21, 2010


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  09/06/2010       dzhang      #103800 - Adjust Transaction Summary section's width.
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
<script type="text/javascript" src="js/viewQuickPayDetail.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>


<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<form name="ViewQuickPayDetailAction" action="loadAllQuickPayDetail.do" method="post">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <input type="hidden" name="policyId" value="<c:out value="${param.policyId}"/>"/>
    <input type="hidden" name="termBaseId" value="<c:out value="${param.termBaseId}"/>"/>
    <input type="hidden" name="origTransId" value="<c:out value="${param.origTransId}"/>"/>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>

    <tr>
        <td align=center>
            <!-- Display Grid -->
            <fmt:message key="pm.viewQuickPayDetails.transactionSummaryLayer.header" var="summaryPanelTitle"
                         scope="page"/>
            <%
                String summaryPanelTitle = (String) pageContext.getAttribute("summaryPanelTitle");
            %>

            <tr>
                <td align=center><br/>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="headerText" value="<%=  summaryPanelTitle %>"/>
                        <jsp:param name="isGridBased" value="false"/>
                        <jsp:param name="isLayerVisibleByDefault" value="true"/>
                        <jsp:param name="includeLayersWithPrefix" value="PM_QP_TRANS_SUMMARY_LAYER"/>
                    </jsp:include>
                </td>
            </tr>
        </td>
    </tr>

    <tr>
        <td align=center>
            <fmt:message key="pm.viewQuickPayDetails.quickPayTransactionList.header" var="firstGridPanelTitle"
                         scope="page"/>
            <%
            String firstGridPanelTitle = (String) pageContext.getAttribute("firstGridPanelTitle");
            %>
            <oweb:panel panelTitleId="panelTitleIdForFirstGrid" panelContentId="panelContentIdForFirstGrid"
                        panelTitle="<%= firstGridPanelTitle %>">
            <tr>
                <td colspan="6" align=center><br/>
                    <c:set var="gridDisplayGridId" value="firstGrid" scope="request"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            </oweb:panel>
        </td>
    </tr>

    <tr>
        <td align=center>
            <fmt:message key="pm.viewQuickPayDetails.riskCoverageList.header" var="thirdGridPanelTitle"
                         scope="page"/>
            <%
                String thirdGridPanelTitle = (String) pageContext.getAttribute("thirdGridPanelTitle");
            %>
            <oweb:panel panelTitleId="panelTitleIdForThirdGrid" panelContentId="panelContentIdForThirdGrid"
                        panelTitle="<%= thirdGridPanelTitle %>">
            <tr>
                <td>
                    <iframe id="iframeRiskCoverage" scrolling="no" allowtransparency="true" width="100%"
                            height="160" frameborder="0" src=""></iframe>
                </td>
            </tr>
            </oweb:panel>
        </td>
    </tr>

    <tr>
        <td colspan="6" align=center>
            <oweb:actionGroup actionItemGroupId="PM_VIEW_QP_DETAIL_AIG"/>
        </td>
    </tr>

<jsp:include page="/core/footerpopup.jsp"/>