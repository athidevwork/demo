<%--
  Description: process dividend.

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
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<script type="text/javascript" src="js/processDividend.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form name="priorDividendList" action="processDividend.do" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <input type="hidden" name="isFirstLoaded" value="<c:out value="${isFirstLoaded}"/>"/>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
    <tr>
        <td colspan="6" align=center>
            <oweb:actionGroup actionItemGroupId="PM_DIVIDEND_PRO_AIG"/>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.dividend.process.search.header" var="panelTitleIdForSearchPriorDividend" scope="page"/>
            <%
                String searchPriorDividend = (String) pageContext.getAttribute("panelTitleIdForSearchPriorDividend");
            %>
            <oweb:panel panelTitleId="panelTitleIdForSearchPriorDividendHeader"
                        panelContentId="panelContentIdForSearchPriorDividendHeader"
                        panelTitle="<%= searchPriorDividend %>">
                <tr>
                    <td align=center>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="displayAsPanel" value="false"/>
                            <jsp:param name="hasPanelTitle" value="false"/>
                            <jsp:param name="divId" value="priorDividendSearchDiv" />
                            <jsp:param name="isGridBased" value="false" />
                            <jsp:param name="isLayerVisibleByDefault" value="true" />
                            <jsp:param name="excludePageFields" value="true" />
                            <jsp:param name="includeLayersWithPrefix" value="PM_DIVIDEND_PRO_SEL"/>
                        </jsp:include>
                    </td>
                    <td align=left width="70%">
                        <oweb:actionGroup actionItemGroupId="PM_DIV_PRO_SEL_AIG"/>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.dividend.process.priorList.header" var="panelTitleIdForPriorDividend" scope="page"/>
            <%
                String priorDividend = (String) pageContext.getAttribute("panelTitleIdForPriorDividend");
            %>
            <oweb:panel panelTitleId="panelTitleIdForPriorDividendHeader"
                        panelContentId="panelContentIdForPriorDividendHeader"
                        panelTitle="<%= priorDividend %>">
                <tr>
                    <td colspan="6">
                        <oweb:actionGroup actionItemGroupId="PM_DIV_PRO_PRIOR_AIG" layoutDirection="horizontal"
                                cssColorScheme="gray"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="6" align=center>
                        <c:set var="gridDisplayFormName" value="priorDividendList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="priorDividendListGrid" scope="request"/>
                        <c:set var="datasrc" value="#priorDividendListGrid1" scope="request"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.dividend.process.filter.header" var="panelTitleIdForFilterCalculatedDividend" scope="page"/>
            <%
                String filterCalculatedDividend = (String) pageContext.getAttribute("panelTitleIdForFilterCalculatedDividend");
            %>
            <oweb:panel panelTitleId="panelTitleIdForFilterCalculatedDividendHeader"
                        panelContentId="panelContentIdForFilterCalculatedPriorDividendHeader"
                        panelTitle="<%= filterCalculatedDividend %>">
                <tr>
                    <td align=center>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="displayAsPanel" value="false"/>
                            <jsp:param name="hasPanelTitle" value="false"/>
                            <jsp:param name="divId" value="calculatedDividendFilterDiv" />
                            <jsp:param name="isGridBased" value="false" />
                            <jsp:param name="isLayerVisibleByDefault" value="true" />
                            <jsp:param name="excludePageFields" value="true" />
                            <jsp:param name="includeLayersWithPrefix" value="PM_DIVIDEND_PRO_FILTER"/>
                        </jsp:include>
                    </td>
                    <td align=left width="60%">
                        <oweb:actionGroup actionItemGroupId="PM_DIV_PRO_FILTER_AIG"/>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.dividend.process.calculated.header" var="panelTitleForCalculatedDividend" scope="page"/>
            <%
                String calculatedDividend = (String) pageContext.getAttribute("panelTitleForCalculatedDividend");
            %>
            <oweb:panel panelTitleId="panelTitleForCalculatedDividend" panelContentId="panelContentIdForCalculatedDividend"
                        panelTitle="<%= calculatedDividend %>">
                <tr>
                    <td>
                        <iframe id="iframeCalculatedDividend" scrolling="no" allowtransparency="true" width="100%"
                                height="300" frameborder="0" src=""></iframe>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td colspan="6" align=center>
            <oweb:actionGroup actionItemGroupId="PM_DIVIDEND_PRO_AIG"/>
        </td>
    </tr>
<jsp:include page="/core/footer.jsp"/>