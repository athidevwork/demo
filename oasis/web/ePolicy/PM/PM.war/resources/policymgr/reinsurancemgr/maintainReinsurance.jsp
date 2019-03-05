<%--
  Description:
  Author: rlli
  Date: Oct 30, 2007
  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  03/01/2013       tcheng      142106 - Add a hidden field transactionLogId.
  03/10/2017       wli         180675 - Changed the error msg to be located in parent frame for UI change.
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
<%@ include file="/pmcore/handleConfirmations.jsp" %>
<script type="text/javascript" src="js/maintainReinsurance.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>
<form name="reinsuranceList" action="maintainReinsurance.do" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <input type="hidden" name="transactionLogId" value="<c:out value="${policyHeader.lastTransactionId}"/>"/>
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
    <c:set var="policyHeaderDisplayMode" value="hide"/>
    <tr>
        <td colspan=8 align=center>
            <%@ include file="/policymgr/policyHeader.jsp" %>
        </td>
    </tr>
    <tr><td>&nbsp;</td></tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.maintainReinsurance.reinsuranceList.header" var="panelTitleForReinsurance" scope="page"/>
            <%
                String panelTitleForReinsurance = (String) pageContext.getAttribute("panelTitleForReinsurance");
            %>
            <oweb:panel panelTitleId="panelTitleForReinsurance" panelContentId="panelContentIdForReinsurance" panelTitle="<%= panelTitleForReinsurance %>" >
            <tr>
                <td colspan="6">
                    <oweb:actionGroup actionItemGroupId="PM_REINSURANCE_GRID_AIG" layoutDirection="horizontal" cssColorScheme="gray"/>
                </td>
            </tr>
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="reinsuranceList" scope="request" />
                    <c:set var="gridDisplayGridId" value="reinsuranceListGrid" scope="request" />
                    <c:set var="gridDetailDivId" value="reinsuranceDetailDiv" scope="request" />
                    <c:set var="datasrc" value="#reinsuranceListGrid1" scope="request" />
                    <c:set var="cacheResultSet" value="false"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr>
                <td align=center>
                    <fmt:message key="pm.maintainReinsurance.reinsuranceForm.header" var="reinsuranceFormHeader" scope="request"/>
                    <% String reinsuranceFormHeader = (String) request.getAttribute("reinsuranceFormHeader"); %>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="headerText" value="<%=  reinsuranceFormHeader %>" />
                        <jsp:param name="isGridBased" value="true" />
                    </jsp:include>
                </td>
            </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="PM_REINSURANCE_AIG" layoutDirection="horizontal"/>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>