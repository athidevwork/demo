<%--
  Description: Select Associated Risk page

  Author: Dzhang
  Date: Mar 07, 2011

  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2011 Delphi Technology, inc. (dti)
--%>

<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<c:set var="isForDivPopup" value="true"></c:set>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<script type="text/javascript" src="<%=appPath%>/riskmgr/ibnrriskmgr/js/selectAssociatedRisk.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form action="selectAssociatedRisk.do" method="POST" name="selectAssociatedRiskList">
<%@ include file="/pmcore/commonFormHeader.jsp" %>
    <input type="hidden" name="openFrom" value="<c:out value="${param.openFrom}"/>">
    <input type="hidden" name="orgAssociatedRiskId" value="<c:out value="${param.orgAssociatedRiskId}"/>">     
    <input type="hidden" name="transEffDate" value="<c:out value="${param.transEffDate}"/>">
    <input type="hidden" name="transactionLogId" value="<c:out value="${param.transactionLogId}"/>">
    <input type="hidden" name="issueCompanyId" value="<c:out value="${param.issueCompanyId}"/>">
    <input type="hidden" name="isInWorkflow" value="<c:out value="${isInWorkflow}"/>" />
    <input type="hidden" name="riskId" value="<c:out value="${riskId}"/>" />

    <c:set var="policyHeaderDisplayMode" value="invisible"/>
    <tr>
        <td colspan=8 align=center>
            <%@ include file="/policymgr/policyHeader.jsp" %>
        </td>
    </tr>

    <tr>
        <td colspan=8>
            <table cellpadding=0 cellspacing=0 width=100%>
                <tr>
                    <td>
                        <oweb:message/>
                    </td>
                </tr>
            </table>
        </td>
    </tr>

    <tr>
        <td align=center>
            <fmt:message key="pm.selectAssociatedRisk.header"var="panelTitleForSelAssociatedRisk" scope="page"/>
            <%
                String panelTitleForSelAssociatedRisk = (String) pageContext.getAttribute("panelTitleForSelAssociatedRisk");
            %>
            <oweb:panel panelTitleId="panelTitleIdForSelAssociatedRisk" panelContentId="panelContentIdForSelAssociatedRisk" panelTitle="<%= panelTitleForSelAssociatedRisk %>" >
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="selectAssociatedRiskList" scope="request" />
                    <c:set var="gridDisplayGridId" value="selectAssociatedRiskGrid" scope="request" />
                    <c:set var="cacheResultSet" value="false"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            </oweb:panel>
            <tr>
                <td>&nbsp;</td>
            </tr>
        </td>
    </tr>

    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="PM_SEL_ASSO_RISK_AIG"/>
        </td>
    </tr>

<jsp:include page="/core/footerpopup.jsp" />
