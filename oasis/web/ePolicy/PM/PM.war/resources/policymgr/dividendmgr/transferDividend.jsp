<%--
//-----------------------------------------------------------------------------
// Transfer Dividend jsp file.
//
// (C) 2013 Delphi Technology, inc. (dti)
// Date:   Dec 23, 2013
// Author: awu
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
//11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
//-----------------------------------------------------------------------------
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<c:set var="isForDivPopup" value="true"></c:set>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<script type="text/javascript" src="<%=appPath%>/policymgr/dividendmgr/js/transferDividend.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form action="<%=appPath%>/policymgr/dividendmgr/transferDividend.do" method="POST" name="riskDividendList">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <input type=hidden name=workflowState value="<c:out value="${workflowState}"/>">
    <tr>
        <td>
            <table cellpadding=0 cellspacing=0 width=100%>
                <tr>
                    <td><oweb:message/></td>
                </tr>
            </table>
        </td>
    </tr>
    <c:set var="policyHeaderDisplayMode" value="invisible"/>
    <tr>
        <td colspan=8 align=center>
            <%@ include file="/policymgr/policyHeader.jsp" %>
        </td>
    </tr>

    <c:if test="${dataBean != null}">
    <tr>
        <td align=center>
    <tr>
        <td class="tablehdr">
            <fmt:message key="pm_transfer.dividend.header" var="panelTitleForDividendTransfer"/>
            <% String panelTitleForDividendTransfer = (String) pageContext.getAttribute("panelTitleForDividendTransfer"); %>
            <oweb:panel panelTitleId="panelTitleForDividendTransfer" panelContentId="panelContentIdForDividendTransfer" panelTitle="<%= panelTitleForDividendTransfer %>" >
        </td>
    </tr>
    <tr>
        <td colspan="6" align=center><br/>
            <c:set var="gridDisplayFormName" value="riskDividendList" scope="request" />
            <c:set var="gridDisplayGridId" value="riskDividendListGrid" scope="request" />
            <c:set var="datasrc" value="#riskDividendListGrid1" scope="request" />
            <c:set var="cacheResultSet" value="false"/>
            <%@ include file="/pmcore/gridDisplay.jsp" %>
        </td>
    </tr>
    </oweb:panel>
    </td>
    </tr>
    </c:if>
    <tr>
        <td colspan="6" align=center>
            <oweb:actionGroup actionItemGroupId="PM_TRANSFER_DIVIDEND_AIG"/>
        </td>
    </tr>

<jsp:include page="/core/footerpopup.jsp" />