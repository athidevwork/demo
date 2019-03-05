<%--
  Description: Maintain Acf page

  Author: syang
  Date: Mar 29, 2011


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  03/21/2012       syang       128978 - Add gridDetailDivId to thirdGrid.
  02/19/2013       adeng       137009 - Added Filter Criteria section.
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2011 Delphi Technology, inc. (dti)
--%>

<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<c:set var="isForDivPopup" value="true"></c:set>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<script type="text/javascript" src="js/maintainAcf.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<jsp:useBean id="firstDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="secondDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="thirdDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="fourthDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="firstGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="secondGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="thirdGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fourthGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>
<form action="maintainAcf.do" method=post name="acfFormList">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
     <%-- Display first grid: product data --%>
    <%
       dti.oasis.util.BaseResultSet dataBean = firstDataBean;
       dti.oasis.tags.XMLGridHeader gridHeaderBean = firstGridHeaderBean;
    %>
    <tr>
        <td align=center>
            <!-- Display Grid -->
            <fmt:message key="pm.processAcf.product.header" var="firstGridPanelTitle" scope="page"/>
            <%
                String firstGridPanelTitle = (String) pageContext.getAttribute("firstGridPanelTitle");
            %>
            <oweb:panel panelTitleId="panelTitleIdForFirstGrid" panelContentId="panelContentIdForFirstGrid"
                        panelTitle="<%= firstGridPanelTitle %>">
                <tr>
                    <td colspan="6" align=center><br/>
                        <c:set var="gridDisplayFormName" value="firstGridFormList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="firstGrid" scope="request"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    <!-- Display filter form -->
    <tr>
        <td align=center>
            <fmt:message key="pm.processAcf.filter.header" var="firstGridPanelTitle" scope="page"/>
            <%
                String filterPanelTitle = (String) pageContext.getAttribute("firstGridPanelTitle");
            %>
            <oweb:panel panelTitleId="panelTitleIdForFilter" panelContentId="panelContentIdForFilter"
                        panelTitle="<%= filterPanelTitle %>">
                <tr>
                    <td>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="isGridBased" value="false"/>
                            <jsp:param name="divId" value="filterlDiv"/>
                            <jsp:param name="isTogglableTitle" value="false"/>
                            <jsp:param name="hasTitleBorder" value="false"/>
                            <jsp:param name="isLayerVisibleByDefault" value="true"/>
                            <jsp:param name="displayAsPanel" value="false"/>
                            <jsp:param name="includeLayersWithPrefix" value="PM_PROCESS_ACF_FILTER_FORM"/>
                        </jsp:include>
                    </td>
                    <td>
                        <oweb:actionGroup actionItemGroupId="PM_PROCESS_ACF_FILTER_AIG" layoutDirection="horizontal"/>
                    </td>
                </tr>
            </oweb:panel>
        <td>
    </tr>

    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="PM_PROCESS_ACF_SAVE_AIG" layoutDirection="horizontal"/>
        </td>
    </tr>
    <%-- Display Second grid: override --%>
    <%
       dataBean = secondDataBean;
       gridHeaderBean = secondGridHeaderBean;
    %>
    <tr>
        <td align=center>
            <!-- Display Grid -->
            <fmt:message key="pm.processAcf.override.header" var="secondGridPanelTitle" scope="page"/>
            <%
                String secondGridPanelTitle = (String) pageContext.getAttribute("secondGridPanelTitle");
            %>
            <oweb:panel panelTitleId="panelTitleIdForSecondGrid" panelContentId="panelContentIdForSecondGrid"
                        panelTitle="<%= secondGridPanelTitle %>">
                <tr>
                    <td colspan="6">
                        <oweb:actionGroup actionItemGroupId="PM_PROCESS_ACF_OVER_AIG" layoutDirection="horizontal"
                                          cssColorScheme="gray"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="6" align=center><br/>
                        <c:set var="gridDisplayFormName" value="secondGridFormList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="secondGrid" scope="request"/>
                        <c:set var="gridDetailDivId" value="secondGridDetailDiv" scope="request"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
                <!-- Display form -->
                <c:set var="dataBean" value="${secondDataBean}" scope="request"/>
                <c:set var="datasrc" value="#secondGrid1" scope="request"/>
                <tr>
                    <td align=center>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="isGridBased" value="true"/>
                            <jsp:param name="divId" value="secondGridDetailDiv"/>
                            <jsp:param name="excludePageFields" value="true"/>
                            <jsp:param name="isTogglableTitle" value="false"/>
                            <jsp:param name="hasTitleBorder" value="false"/>
                            <jsp:param name="isLayerVisibleByDefault" value="true"/>
                            <jsp:param name="includeLayersWithPrefix" value="PM_PROCESS_ACF_OVERRIDE"/>
                        </jsp:include>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>

    <%-- Display Third grid: result --%>
  <%
       dataBean = thirdDataBean;
       gridHeaderBean = thirdGridHeaderBean;
    %>
    <tr>
        <td align=center>
            <!-- Display Grid -->
            <fmt:message key="pm.processAcf.result.header" var="thirdGridPanelTitle" scope="page"/>
            <%
                String thirdGridPanelTitle = (String) pageContext.getAttribute("thirdGridPanelTitle");
            %>
            <oweb:panel panelTitleId="panelTitleIdForThirdGrid" panelContentId="panelContentIdForThirdGrid"
                        panelTitle="<%= thirdGridPanelTitle %>">
                <tr>
                    <td colspan="6" align=center><br/>
                        <c:set var="gridDisplayFormName" value="thirdGridFormList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="thirdGrid" scope="request"/>
                        <c:set var="gridDetailDivId" value="thirdGridDetailDiv" scope="request"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
                <!-- Display form -->
                <tr>
                    <td align=center>
                        <fmt:message key="pm.processAcf.result.total.header" var="ResultTotalHeader" scope="page"/>
                        <% String resultTotalHeader = (String) pageContext.getAttribute("ResultTotalHeader"); %>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="headerText" value="<%= resultTotalHeader %>"/>
                            <jsp:param name="isGridBased" value="false"/>
                            <jsp:param name="divId" value="thirdGridDetailDiv"/>
                            <jsp:param name="isLayerVisibleByDefault" value="true"/>
                            <jsp:param name="includeLayersWithPrefix" value="PM_PROCESS_ACF_RESULT_TOTAL"/>
                        </jsp:include>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>

    <%-- Display Fourth grid: fee --%>
    <%
       dataBean = fourthDataBean;
       gridHeaderBean = fourthGridHeaderBean;
    %>
    <tr>
        <td align=center>
            <!-- Display Grid -->
            <fmt:message key="pm.processAcf.fee.header" var="fourthGridPanelTitle" scope="page"/>
            <%
                String fourthGridPanelTitle = (String) pageContext.getAttribute("fourthGridPanelTitle");
            %>
            <oweb:panel panelTitleId="panelTitleIdForFourthGrid" panelContentId="panelContentIdForFourthGrid"
                        panelTitle="<%= fourthGridPanelTitle %>">
                <tr>
                    <td colspan="6">
                        <oweb:actionGroup actionItemGroupId="PM_PROCESS_ACF_FEE_AIG" layoutDirection="horizontal"
                                          cssColorScheme="gray"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="6" align=center><br/>
                        <c:set var="gridDisplayFormName" value="fourthGridFormList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="fourthGrid" scope="request"/>
                        <c:set var="gridDetailDivId" value="fourthGridDetailDiv" scope="request"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
                <!-- Display form -->
                <c:set var="dataBean" value="${fourthDataBean}" scope="request"/>
                <c:set var="datasrc" value="#fourthGrid1" scope="request"/>
                <tr>
                    <td align=center>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="isGridBased" value="true"/>
                            <jsp:param name="divId" value="fourthGridDetailDiv"/>
                            <jsp:param name="isTogglableTitle" value="false"/>
                            <jsp:param name="hasTitleBorder" value="false"/>
                            <jsp:param name="excludePageFields" value="true"/>
                            <jsp:param name="isLayerVisibleByDefault" value="true"/>
                            <jsp:param name="includeLayersWithPrefix" value="PM_PROCESS_ACF_FEE"/>
                        </jsp:include>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="PM_PROCESS_ACF_SAVE_AIG" layoutDirection="horizontal"/>
        </td>
    </tr>

<jsp:include page="/core/footerpopup.jsp"/>