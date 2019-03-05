<%@ page language="java" %>
<%--
  Description:
  The Jsp page for the Window Period History page.


  Author: kshen
  Date: 2/25/14


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<%@ include file="/core/headerpopup.jsp" %>
<jsp:include page="/cicore/common.jsp"/>

<script type="text/javascript" src="riskmgr/windowperiodhistorymgr/js/windowPeriodHistory.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form name="WindowPeriodHistoryForm" action="windowPeriodHistory.do" method="POST">
    <jsp:include page="/cicore/commonFormHeader.jsp"/>

    <tr>
        <td>
            <oweb:message/>
        </td>
    </tr>

    <tr>
        <td align=center>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="divId" value="windowPeriodHistoryFilter"/>
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="headerTextLayerId" value="CI_WINDOW_PERIOD_HIST_FILTER"/>
                <jsp:param name="includeLayerIds" value="CI_WINDOW_PERIOD_HIST_FILTER"/>
                <jsp:param name="excludePageFields" value="true"/>
                <jsp:param name="isLayerVisibleByDefault" value="true"/>
                <jsp:param name="actionItemGroupId" value="CI_WND_PERIOD_HIST_AIG"/>
            </jsp:include>
        </td>
    </tr>

    <tr>
        <td>
            <oweb:panel panelContentId="panelContentForWindowPeriodHistoryList"
                        panelTitleId="panelTitleIdForWindowPeriodHistoryList"
                        panelTitleLayerId="CI_WND_PERIOD_HIST_GH">
                <tr>
                    <td colspan="6" align=center>
                        <c:set var="gridDisplayFormName" value="WindowPeriodHistoryForm" scope="request"/>
                        <c:set var="gridDisplayGridId" value="windowPeriodHistoryListGrid" scope="request" />
                        <%@ include file="/core/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>

<jsp:include page="/core/footerpopup.jsp"/>