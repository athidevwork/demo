<%--
  Description: Select Policy Insured Risks page.

  Author: Joe
  Date: November 14, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
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
<script type="text/javascript" src="js/selectCompInsRiskRelation.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<tr>
    <td colspan=8>
        <table cellpadding=0 cellspacing=0 width=100%>
            <tr>
                <td><oweb:message/></td>
            </tr>
        </table>
    </td>
</tr>
<form action="" name ="selectCompInsRiskForm">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <tr>
        <td align=center>
            <fmt:message key="pm.maintainRiskRelation.selectPolInsRiskList.header" var="panelTitleForSelCompInsRisk" scope="page">
            </fmt:message>
            <%
                String panelTitleForSelCompInsRisk = (String) pageContext.getAttribute("panelTitleForSelCompInsRisk");
            %>
            <oweb:panel panelTitleId="panelTitleIdForSelCompInsRisk" panelContentId="panelContentIdForSelCompInsRisk"
                        panelTitle="<%= panelTitleForSelCompInsRisk %>" >
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="selectCompInsRiskForm" scope="request"/>
                    <c:set var="gridDisplayGridId" value="selectCompInsRiskGrid" scope="request"/>
                    <c:set var="cacheResultSet" value="false"/>
                    <c:set var="selectable" value="true"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            </oweb:panel>
            <tr>
                <td align=center>
                    <oweb:actionGroup actionItemGroupId="PM_SEL_COMP_INS_RISK_AIG"/>
                </td>
            </tr>

<jsp:include page="/core/footerpopup.jsp"/>