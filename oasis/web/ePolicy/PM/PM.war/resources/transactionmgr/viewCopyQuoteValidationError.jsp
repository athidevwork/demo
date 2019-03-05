<%@ page import="org.apache.struts.Globals"%>
<%@ page import="org.apache.struts.taglib.html.Constants"%>

<%--
  Description:

  Author: fcbibire
  Date: Apr 25, 2008


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/15/2018       lzhang      194100   Add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
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

<script type="text/javascript" src="<%=appPath%>/transactionmgr/js/viewCopyQuoteValidationError.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form action="<%=appPath%>/transactionmgr/viewCopyQuoteValidationError.do" method="POST" name="viewCopyQuoteValidationErrorList">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <% String remapTransactionId = (String) request.getAttribute("remapTransactionId"); %>
    <% String remapPolicyNo = (String) request.getAttribute("remapPolicyNo"); %>
    <input type=hidden name=workflowState value="<c:out value="${workflowState}"/>">

    <input type=hidden name=remapTransactionId value="<c:out value="${remapTransactionId}"/>" />
    <input type=hidden name=remapPolicyNo value="<c:out value="${remapPolicyNo}"/>" />

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
                            <fmt:message key="pm.viewValidationError.header" var="panelTitleForViewValidationError"/>
                            <% String panelTitleForViewValidationError = (String) pageContext.getAttribute("panelTitleForViewValidationError"); %>
                            <oweb:panel panelTitleId="panelTitleIdForViewValidationError" panelContentId="panelContentIdForViewValidationError" panelTitle="<%= panelTitleForViewValidationError %>" >
                        </td>
                    </tr>
                    <tr>
                        <td colspan="6" align=center><br/>
                            <c:set var="gridDisplayFormName" value="viewCopyQuoteValidationErrorList" scope="request" />
                            <c:set var="gridDisplayGridId" value="viewCopyQuoteValidationErrorGrid" scope="request" />
                            <c:set var="datasrc" value="#viewCopyQuoteValidationErrorGrid1" scope="request" />
                            <c:set var="cacheResultSet" value="false"/>
                            <c:set var="saveAsExcelCsv" value="true"/>
                            <c:set var="saveAsExcelHtml" value="true"/>
                            <%@ include file="/pmcore/gridDisplay.jsp" %>
                        </td>
                    </tr>
                </oweb:panel>
            </td>
        </tr>
    </c:if>
    <tr>
        <td colspan="6" align=center>
            <oweb:actionGroup actionItemGroupId="PM_VIEW_CQ_VAL_ERR_AIG"/>
        </td>
    </tr>

    <jsp:include page="/core/footerpopup.jsp" />

