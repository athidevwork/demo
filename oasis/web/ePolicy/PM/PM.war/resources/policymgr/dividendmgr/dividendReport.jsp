<%--
  Description: dividend report.

  Author: wfu
  Date: Mar 30, 2011


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2011 Delphi Technology, inc. (dti)
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ include file="/core/header.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="dividendReportDetailListGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="dividendReportDetailListGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<script type="text/javascript" src="js/dividendReport.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form name="dividendReportList" action="dividendReport.do" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.dividend.report.search.header" var="panelTitleIdForDividendReportSearch" scope="page"/>
            <%
                String dividendReportSearch = (String) pageContext.getAttribute("panelTitleIdForDividendReportSearch");
            %>
            <oweb:panel panelTitleId="panelTitleIdForDividendReportSearchHeader"
                        panelContentId="panelContentIdForDividendReportSearchHeader"
                        panelTitle="<%= dividendReportSearch %>">
                <tr>
                    <td align=center>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="displayAsPanel" value="false"/>
                            <jsp:param name="hasPanelTitle" value="false"/>
                            <jsp:param name="divId" value="dividendReportSearchDiv" />
                            <jsp:param name="isGridBased" value="false" />
                            <jsp:param name="isLayerVisibleByDefault" value="true" />
                            <jsp:param name="excludePageFields" value="true" />
                            <jsp:param name="includeLayersWithPrefix" value="PM_DIVIDEND_REP_SEL"/>
                        </jsp:include>
                    </td>
                    <td align=left width="50%">
                        <oweb:actionGroup actionItemGroupId="PM_DIV_REP_SEL_AIG" layoutDirection="horizontal"/>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.dividend.report.list.header" var="panelTitleIdForDividendReportList" scope="page"/>
            <%
                String dividendReportList = (String) pageContext.getAttribute("panelTitleIdForDividendReportList");
            %>
            <oweb:panel panelTitleId="panelTitleIdForDividendReportListHeader"
                        panelContentId="panelContentIdForDividendReportListHeader"
                        panelTitle="<%= dividendReportList %>">
                <tr>
                    <td colspan="6" align=center>
                        <c:set var="gridDisplayFormName" value="dividendReportList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="dividendReportListGrid" scope="request"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.dividend.report.filter.header" var="panelTitleIdForDividendReportFilter" scope="page"/>
            <%
                String dividendReportFilter = (String) pageContext.getAttribute("panelTitleIdForDividendReportFilter");
            %>
            <oweb:panel panelTitleId="panelTitleIdForDividendReportFilterHeader"
                        panelContentId="panelContentIdForDividendReportFilterHeader"
                        panelTitle="<%= dividendReportFilter %>">
                <tr>
                    <td align=center>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="displayAsPanel" value="false"/>
                            <jsp:param name="hasPanelTitle" value="false"/>
                            <jsp:param name="divId" value="dividendReportFilterDiv" />
                            <jsp:param name="isGridBased" value="false" />
                            <jsp:param name="isLayerVisibleByDefault" value="true" />
                            <jsp:param name="excludePageFields" value="true" />
                            <jsp:param name="includeLayersWithPrefix" value="PM_DIVIDEND_REP_FILTER"/>
                        </jsp:include>
                    </td>
                    <td align=left width="70%">
                        <oweb:actionGroup actionItemGroupId="PM_DIV_REP_FILTER_AIG" layoutDirection="horizontal"/>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.dividend.report.detail.header" var="panelTitleIdForDividendReportDetail" scope="page"/>
            <%
                String dividendReportDetail = (String) pageContext.getAttribute("panelTitleIdForDividendReportDetail");
            %>
            <oweb:panel panelTitleId="panelTitleIdForDividendReportDetailHeader"
                        panelContentId="panelContentIdForDividendReportDetailHeader"
                        panelTitle="<%= dividendReportDetail %>">
                <tr>
                    <td colspan="6" align=center>
                        <c:set var="gridDisplayFormName" value="dividendReportDetailList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="dividendReportDetailListGrid" scope="request"/>
                        <% 
                            dataBean = dividendReportDetailListGridDataBean;
                            gridHeaderBean = dividendReportDetailListGridHeaderBean;
                        %>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
<jsp:include page="/core/footer.jsp"/>