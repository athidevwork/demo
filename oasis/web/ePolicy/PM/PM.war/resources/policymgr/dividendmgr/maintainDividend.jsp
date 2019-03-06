<%--
  Description: view and post dividend.

  Author: wfu
  Date: March 13, 2012


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2012 Delphi Technology, inc. (dti)
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

<script type="text/javascript" src="js/maintainDividend.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form name="calculateDividendList" action="maintainDividend.do" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.dividend.maintain.search.header" var="panelTitleIdForDividendSearch" scope="page"/>
            <% String dividendSearchTitle = (String) pageContext.getAttribute("panelTitleIdForDividendSearch");%>
            <oweb:panel panelTitleId="panelTitleIdForDividendSearchHeader"
                        panelContentId="panelContentIdForDividendSearchHeader"
                        panelTitle="<%= dividendSearchTitle %>">
                <tr>
                    <td colspan="6" align=center>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="displayAsPanel" value="false"/>
                            <jsp:param name="hasPanelTitle" value="false"/>
                            <jsp:param name="divId" value="calculateDividendDiv" />
                            <jsp:param name="isGridBased" value="false" />
                            <jsp:param name="isLayerVisibleByDefault" value="true" />
                        </jsp:include>
                    </td>
                </tr>
                <tr>
                    <td colspan="6" align=center>
                        <oweb:actionGroup actionItemGroupId="PM_DIV_POST_SEL_AIG" layoutDirection="horizontal"
                                          cssColorScheme="gray"/>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.dividend.process.calculated.header" var="panelTitleForCalculatedDividend" scope="page"/>
                <% String calculatedDividend = (String) pageContext.getAttribute("panelTitleForCalculatedDividend"); %>
            <oweb:panel panelTitleId="panelTitleForCalculatedDividend"
                        panelContentId="panelContentIdForCalculatedDividend"
                        panelTitle="<%= calculatedDividend %>">
                <tr>
                    <td colspan="6">
                        <oweb:actionGroup actionItemGroupId="PM_DIV_POST_GRID_AIG" layoutDirection="horizontal"
                                cssColorScheme="gray"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="6" align=center>
                        <c:set var="gridDisplayFormName" value="calculateDividendList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="calculateDividendListGrid" scope="request"/>
                        <c:set var="datasrc" value="#calculateDividendListGrid1" scope="request"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
<jsp:include page="/core/footer.jsp"/>