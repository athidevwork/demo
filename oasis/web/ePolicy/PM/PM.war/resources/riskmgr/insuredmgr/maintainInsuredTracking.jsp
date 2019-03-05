<%@ page import="dti.oasis.util.DateUtils" %>
<%--
  Description:

  Author: wdang
  Date: April 08, 2015


  Revision Date     Revised By  Description
  -----------------------------------------------------------------------------
  04/08/2015        wdang       157211 - Initial Version.
  03/10/2017        eyin        180675 - Added code to display message on parent Window in new
                                         UI tab style.
  11/15/2018        eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2015 Delphi Technology, inc. (dti)
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<c:set var="isForDivPopup" value="true"></c:set>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<script type="text/javascript" src="js/maintainInsuredTracking.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form name="maintainInsuredTrackingForm" action="<%=appPath%>/riskmgr/insuredmgr/maintainInsuredTracking.do" method="post">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <tr>
        <td>
            <%
                if(pmUIStyle.equals("T")) {
            %>
            <oweb:message displayMessagesOnParent="true"/>
            <%
                }
            %>
            <%
                if(pmUIStyle.equals("B")) {
            %>
            <oweb:message/>
            <%
                }
            %>
        </td>
    </tr>

    <c:set var="policyHeaderDisplayMode" value="invisible"/>
    <tr>
        <td align=center>
            <%@ include file="/policymgr/policyHeader.jsp" %>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.maintainInsuredTracking.searchCriteria.header" var="panelTitleForSearch" scope="page"/>
            <% String panelTitleForSearch = (String) pageContext.getAttribute("panelTitleForSearch"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%=panelTitleForSearch %>" />
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="divId" value="searchDiv"/>
                <jsp:param name="isLayerVisibleByDefault" value="true"/>
                <jsp:param name="excludePageFields" value="true"/>
                <jsp:param name="includeLayersWithPrefix" value="PM_INS_TRK_SEARCH" />
                <jsp:param name="actionItemGroupId" value="PM_INS_TRK_SEARCH_AIG" />
            </jsp:include>
        </td>
    </tr>
    <tr>
        <td>&nbsp;</td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.maintainInsuredTracking.insuredList.header" var="panelTitleForList" scope="page">
                <fmt:param><%= policyHeader.getRiskHeader().getRiskName() %></fmt:param>
                <fmt:param><%= policyHeader.getRiskHeader().getEarliestContigEffectiveDate() %></fmt:param>
                <fmt:param><%= policyHeader.getRiskHeader().getDateChangeAllowedRiskDate() %></fmt:param>
            </fmt:message>
            <% String panelTitleForList = (String) pageContext.getAttribute("panelTitleForList"); %>
            <oweb:panel panelTitleId="panelTitleForList" panelContentId="panelContentIdForList" panelTitle="<%= panelTitleForList %>" >
            <tr>
                <td align=left>
                    <oweb:actionGroup actionItemGroupId="PM_INS_TRK_GRID_AIG" layoutDirection="horizontal" cssColorScheme="gray" cssWidthInPX="75"/>
                </td>
            </tr>
            <tr>
                <td align=center>
                    <c:set var="gridDisplayFormName" value="maintainInsuredTrackingForm" scope="request"/>
                    <c:set var="gridDisplayGridId" value="maintainInsuredTrackingListGrid" scope="request"/>
                    <c:set var="gridDetailDivId" value="maintainInsuredTrackingListGridDiv" scope="request" />
                    <c:set var="datasrc" value="#maintainInsuredTrackingListGrid1" scope="request"/>
                    <c:set var="cacheResultSet" value="false"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            <tr>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td align=center>
                    <fmt:message key="pm.maintainInsuredTracking.insuredForm.header" var="panelTitleForForm" scope="page" />
                    <% String panelTitleForForm = (String) pageContext.getAttribute("panelTitleForForm"); %>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="headerText" value="<%=panelTitleForForm %>" />
                        <jsp:param name="isGridBased" value="true"/>
                        <jsp:param name="divId" value="maintainInsuredTrackingListGridDiv"/>
                        <jsp:param name="isLayerVisibleByDefault" value="true"/>
                        <jsp:param name="excludePageFields" value="true"/>
                        <jsp:param name="includeLayersWithPrefix" value="PM_INS_TRK_FORM" />
                    </jsp:include>
                </td>
            </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td>&nbsp;</td>
    </tr>
    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="PM_INS_TRK_AIG"/>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>
