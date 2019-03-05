<%@ page import="dti.oasis.session.UserSessionManager" %>

<%--
  Description: Risk Relation page

  Author: Joe Shen
  Date: Nov 1, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  05/22/2008       fcb         80579: added multiRiskRelation hidden field.
  07/07/2010       dzhang      103806: added hidden fields isCompanyInsuredStr & origPracticeStateCode.
  05/05/2011       dzhang      119903: change the panel title to support i18n.
  09/18/2015       lzhang      165941 - add screenModeCode to hidden parameter
  03/10/2017       wrong       180675 - Added code to display message on parent window in new
                                        UI tab style.
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
<%@ include file="/core/invokeWorkflow.jsp"%>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<script type="text/javascript" src="js/maintainRiskRelation.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form action="maintainRiskRelation.do" name="riskRelationForm" method="post">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <input type="hidden" name="origRiskEffectiveFromDate" value="<c:out value="${origRiskEffectiveFromDate}"/>" />
    <input type="hidden" name="isReverse" value="<c:out value="${isReverse}"/>" />
    <input type="hidden" name="multiRiskRelation" value="<c:out value="${multiRiskRelation}"/>" />
    <input type="hidden" name="relPolicyNo" datasrc="#maintainRiskRelationListGrid1" datafld="CCHILDPOLICYNO" />
    <input type="hidden" name="isCompanyInsuredStr" value="<c:out value="${isCompanyInsuredStr}"/>" />
    <input type="hidden" name="origPracticeStateCode" value="<c:out value="${param.origPracticeStateCode}"/>" />
    <input type="hidden" name="refreshParentB" value="<c:out value="${refreshParentB}"/>" />
    <input type="hidden" name="screenModeCode" value="<c:out value="${screenModeCode}"/>" />

    <tr>
        <td colspan=8>
            <%
                if (pmUIStyle.equals("T")) {
            %>
            <oweb:message displayMessagesOnParent="true"/>
            <%
                }
            %>
            <%
                if (pmUIStyle.equals("B")) {
            %>
            <oweb:message/>
            <%
                }
            %>
        </td>
    </tr>
    <c:set var="policyHeaderDisplayMode" value="invisible"/>
    <tr>
        <td colspan=8 align=center>
            <%@ include file="/policymgr/policyHeader.jsp" %>
        </td>
    </tr>
    <tr><td>&nbsp;</td></tr>

    <tr>
        <td colspan=8>
            <b>
                <fmt:message key="pm.maintainRiskRelation.header">
                    <fmt:param value="${reverse}" />
                    <fmt:param value="${riskName}" />
                    <fmt:param><%= FormatUtils.formatDateForDisplay(request.getAttribute("riskEffectiveFromDate").toString()) %></fmt:param>
                    <fmt:param><%= FormatUtils.formatDateForDisplay(request.getAttribute("riskEffectiveToDate").toString()) %></fmt:param>
                </fmt:message>
            </b>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.maintainRiskRelation.riskRelationList.header" var="panelTitleForRiskRelation" scope="page">
                <fmt:param value="${countOfPolicyInsured}" />
                <fmt:param value="${countOfCompanyInsured}" />
                <fmt:param value="${countOfNonInsured}" />
                <fmt:param value="${countOfActPolicyInsured}" />
                <fmt:param value="${countOfActCompanyInsured}" />
                <fmt:param value="${countOfActNonInsured}" />
                <fmt:param value="${countOfCxlPolicyInsured}" />
                <fmt:param value="${countOfCxlCompanyInsured}" />
                <fmt:param value="${countOfCxlNonInsured}" />
            </fmt:message>
            <%
                String panelTitleForRiskRelation = (String) pageContext.getAttribute("panelTitleForRiskRelation");
            %>
            <oweb:panel panelTitleId="panelTitleIdForRiskRelation" panelContentId="panelContentIdForRiskRelation" panelTitle="<%= panelTitleForRiskRelation %>" >
            <tr>
                <td colspan="6">
                    <oweb:actionGroup actionItemGroupId="PM_RISK_RELATION_AIG" layoutDirection="horizontal" cssColorScheme="gray"/>
                </td>
            </tr>
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="riskRelationForm" scope="request"/>
                    <c:set var="gridDisplayGridId" value="maintainRiskRelationListGrid" scope="request"/>
                    <c:set var="gridDetailDivId" value="maintainRiskRelationListGridDiv" scope="request" />
                    <c:set var="datasrc" value="#maintainRiskRelationListGrid1" scope="request"/>
                    <c:set var="cacheResultSet" value="false"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            <tr>
                <td>&nbsp;</td>
            </tr>
            <%-- Display grid form --%>
            <tr>
                <td align=center>
                    <fmt:message key="pm.maintainRiskRelation.riskRelationForm.header" var="riskRelationFormHeader" scope="request"/>
                    <% String riskRelationFormHeader = (String) request.getAttribute("riskRelationFormHeader"); %>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="headerText" value="<%= riskRelationFormHeader %>" />
                        <jsp:param name="isGridBased" value="true" />
                    </jsp:include>
                </td>
            </tr>
            </oweb:panel>
            <tr>
                <td colspan="6" align=center>
                    <oweb:actionGroup actionItemGroupId="PM_RISK_REL_GRID_AIG"/>
                </td>
            </tr>
        </td>
    </tr>
    <br>

<jsp:include page="/core/footerpopup.jsp"/>
