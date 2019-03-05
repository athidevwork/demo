<%@ page import="dti.oasis.session.UserSessionManager" %>

<%--
  Description: Select Policy Insured Risks page.

  Author: Joe
  Date: November 14, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  05/22/2008       fcb         80759: multiRiskRelation and riskEntityId hidden fields added.
  07/28/2010       syang       109479: System should hide rather than skip the detail div, some field depends on the hidden field.
                                       Added gridDetailDivId to selectPolInsRiskGrid, if no data found, detail div will be hidden.
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2007 Delphi Technology, inc. (dti)
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
<script type="text/javascript" src="js/selectPolInsRiskRelation.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<tr>
    <td colspan=8>
        <table cellpadding=0 cellspacing=0 width=100%>
            <tr>
                <td><oweb:message/></td>
            </tr>
        </table>
    </td>
</tr>
<form action="" name ="selectPolInsRiskForm">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <input type="hidden" name="multiRiskRelation" value="<c:out value="${multiRiskRelation}"/>" />
    <input type="hidden" name="riskEntityId" value="<c:out value="${riskEntityId}"/>" />
        
    <tr>
        <td align=center>
            <fmt:message key="pm.maintainRiskRelation.selectPolInsRiskList.header" var="panelTitleForSelPolInsRisk" scope="page">
            </fmt:message>
            <%
                String panelTitleForSelPolInsRisk = (String) pageContext.getAttribute("panelTitleForSelPolInsRisk");
            %>
            <oweb:panel panelTitleId="panelTitleIdForSelPolInsRisk" panelContentId="panelContentIdForSelPolInsRisk"
                        panelTitle="<%= panelTitleForSelPolInsRisk %>" >
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="selectPolInsRiskForm" scope="request"/>
                    <c:set var="gridDisplayGridId" value="selectPolInsRiskGrid" scope="request"/>
                    <c:set var="gridDetailDivId" value="polInsDetailDiv" scope="request"/>
                    <c:set var="cacheResultSet" value="false"/>
                    <c:set var="selectable" value="true"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            <%-- Display grid form --%>
            <tr>
                <td align=center>
                    <fmt:message key="pm.maintainRiskRelation.selectPolInsRiskForm.header" var="selectPolInsRiskFormHeader" scope="request"/>
                    <% String selectPolInsRiskFormHeader = (String) request.getAttribute("selectPolInsRiskFormHeader"); %>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="headerText" value="<%= selectPolInsRiskFormHeader %>" />
                        <jsp:param name="divId" value="polInsDetailDiv"/>
                        <jsp:param name="isGridBased" value="true" />
                    </jsp:include>
                </td>
            </tr>

            </oweb:panel>
            <tr>
                <td align=center>
                    <oweb:actionGroup actionItemGroupId="PM_SEL_POL_INS_RISK_AIG"/>
                </td>
            </tr>

<jsp:include page="/core/footerpopup.jsp"/>