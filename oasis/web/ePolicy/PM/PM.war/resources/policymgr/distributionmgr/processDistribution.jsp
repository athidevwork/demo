<%--
  Description: process distribution.

  Author: wfu
  Date: Mar 11, 2011


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

<script type="text/javascript" src="js/processDistribution.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form name="distributionList" action="processDistribution.do" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
    <tr>
        <td align=center>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="hasPanelTitle" value="false"/>
                <jsp:param name="divId" value="distributionFilterDiv" />
                <jsp:param name="isGridBased" value="false" />
                <jsp:param name="isLayerVisibleByDefault" value="true" />
                <jsp:param name="actionItemGroupId" value="PM_DISTRIBUTION_SEL_AIG" />
                <jsp:param name="excludePageFields" value="true" />
            </jsp:include>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.process.distribution.header" var="panelTitleIdForDistribution" scope="page"/>
            <%
                String distributionTitle = (String) pageContext.getAttribute("panelTitleIdForDistribution");
            %>
            <oweb:panel panelTitleId="panelTitleIdForDistributionHeader"
                        panelContentId="panelContentIdForDistributionHeader"
                        panelTitle="<%= distributionTitle %>">
                <tr>
                    <td colspan="6">
                        <oweb:actionGroup actionItemGroupId="PM_DISTRIBUTION_GRID_AIG" layoutDirection="horizontal"
                                cssColorScheme="gray"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="6" align=center><br/>
                        <c:set var="gridDisplayFormName" value="distributionList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="distributionListGrid" scope="request"/>
                        <c:set var="gridDetailDivId" value="distributionListGridDiv" scope="request" />
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
                <tr>
                    <td align=center>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="hasPanelTitle" value="false"/>
                            <jsp:param name="gridID" value="distributionListGrid"/>
                            <jsp:param name="divId" value="distributionListGridDiv"/>
                            <jsp:param name="isTogglableTitle" value="false"/>
                            <jsp:param name="includeLayersWithPrefix" value="PM_DISTRIBUTION_GH"/>
                        </jsp:include>
                    </td>
                </tr>
            </oweb:panel>

        </td>
    </tr>
    <tr>
        <td colspan="6" align=center>
            <oweb:actionGroup actionItemGroupId="PM_DISTRIBUTION_AIG"/>
        </td>
    </tr>
<jsp:include page="/core/footer.jsp"/>