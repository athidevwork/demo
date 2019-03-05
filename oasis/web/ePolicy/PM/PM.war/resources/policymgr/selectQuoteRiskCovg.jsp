<%--
<%@ page language="java"%>
  Description:

  Author: fcbibire
  Date: Apr 17, 2008


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<c:set var="isForDivPopup" value="true"></c:set>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<script type="text/javascript" src="<%=appPath%>/policymgr/js/selectQuoteRiskCovg.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<form name="selectQuoteRiskCovg" action="<%=appPath%>/policymgr/selectQuoteRiskCoverage.do" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
    <c:set var="policyHeaderDisplayMode" value="invisible"/>
    <tr>
        <td colspan=8 align=center>
            <%@ include file="/policymgr/policyHeader.jsp" %>
        </td>
    </tr>

    <tr>
        <td align=center>
            <fmt:message key="pm.maintainQuote.addlRiskCovgInfo.header" var="addlRiskCovgInfoFormHeader" scope="request"/>
            <% String addlRiskCovgInfoFormHeader = (String) request.getAttribute("addlRiskCovgInfoFormHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%=  addlRiskCovgInfoFormHeader %>" />
                <jsp:param name="divId" value="riskCovgAddlInfoDiv" />
                <jsp:param name="isGridBased" value="false" />
                <jsp:param name="isLayerVisibleByDefault" value="true" />
                <jsp:param name="excludePageFields" value="true" />
                <jsp:param name="includeLayersWithPrefix" value="PM_ADDL" />                
            </jsp:include>
        </td>
    </tr>

    <tr>
        <fmt:message key="pm.maintainQuote.selectRiskCovg.header" var="panelTitleForSelectRiskCovg" scope="page"/>
        <td align=center>
            <fmt:message key="pm.maintainQuote.selectRiskCovg.header" var="panelTitleForSelectRiskCovg" scope="page"/>
            <%
                String panelTitleForSelectQuoteRiskCovg = (String) pageContext.getAttribute("panelTitleForSelectRiskCovg");
            %>
            <oweb:panel panelTitleId="panelTitleForSelectRiskCovg" panelContentId="panelContentIdForSelectRiskCovg"
                        panelTitle="<%= panelTitleForSelectQuoteRiskCovg %>">
            <tr>
                <td colspan="6" align=center><br/>
                    <c:set var="gridDisplayFormName" value="selectQuoteRiskCovg" scope="request"/>
                    <c:set var="gridDisplayGridId" value="selectQuoteRiskCovgGrid" scope="request"/>
                    <c:set var="datasrc" value="#selectQuoteRiskCovgGrid1" scope="request"/>
                    <c:set var="cacheResultSet" value="false"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            </oweb:panel>

            <tr>
                <td colspan="6" align=center>
                    <oweb:actionGroup actionItemGroupId="PM_SELQUOTERISKCOVG_AIG"/>
                </td>
            </tr>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>
