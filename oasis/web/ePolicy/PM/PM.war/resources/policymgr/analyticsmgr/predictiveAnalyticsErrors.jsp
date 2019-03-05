<%@ page import="dti.oasis.util.BaseResultSet" %>
<%@ page import="dti.oasis.tags.XMLGridHeader" %>
<%@ page language="java" %>
<%--
  Description:
  The jsp page for Analytics Error page.

  Author: kshen
  Date: Jun 10, 2011


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
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<script type="text/javascript" src="js/predictiveAnalyticsErrors.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<form name="opaErrorsForm" action="opaErrors.do" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>

    <tr>
        <td align=center>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerTextLayerId" value="PM_SCORING_ERROR_SEARCH"/>
                <jsp:param name="divId" value="searchCriteriaDiv"/>
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="isLayerVisibleByDefault" value="true"/>
                <jsp:param name="includeLayerIds" value="PM_SCORING_ERROR_SEARCH"/>
                <jsp:param name="excludePageFields" value="true"/>
                <jsp:param name="hasPanelTitle" value="true"/>
                <jsp:param name="actionItemGroupId" value="PM_OPA_ERROR_SEARCH_AIG"/>
            </jsp:include>
        </td>
    </tr>

<%
    BaseResultSet dataBean = (BaseResultSet) request.getAttribute("dataBean");
    if (dataBean != null) {
        XMLGridHeader gridHeaderBean = (XMLGridHeader) request.getAttribute("gridHeaderBean");      
%>
    <tr>
        <td align=center>
            <oweb:panel panelTitleId="panelTitleIdForOpaRequest" panelContentId="panelContentIdForOpaRequest"
                        panelTitleLayerId="PM_OPA_SCORE_REQ_GH">
                <tr>
                    <td colspan="6" align=center>
                        <c:set var="gridDisplayFormName" value="scoringErrorForm" scope="request"/>
                        <c:set var="gridDisplayGridId" value="scoringErrorGrid" scope="request"/>
                        <c:set var="gridSizeFieldIdPrefix" value="opaScoreReq_"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>

    <tr>
        <td align=center>
            <oweb:panel panelContentId="panelContentForOpaScoureErrorLog"
                        panelTitleId="panelTitleIdForOpaScoureErrorLog"
                        panelTitleLayerId="PM_OPA_ERROR_LOG_GH">
                <tr>
                    <td colspan="6" align=center>
                        <iframe id="iframeOpaScoreErrorLog" scrolling="no" allowtransparency="true" width="100%"
                                height="200" frameborder="0" src=""></iframe>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
<%
    }
%>
<jsp:include page="/core/footerpopup.jsp"/>