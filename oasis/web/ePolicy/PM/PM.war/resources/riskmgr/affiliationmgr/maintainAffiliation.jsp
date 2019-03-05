<%--
  Description: Maintain Affiliation Page

  Author: Simon Li
  Date: May 5, 2008

  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  06/01/2011       wqfu        120960 - Modified the panel title to use defined date format.
  06/01/2012       xnie        132114 - 1) Modified risk effective to date value of panel title.
                                        2) Added hidden field affiliationRiskExpDate.
  03/10/2017       wli         Issue 180675 - Changed the message tag for new UI change.
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
<%@ include file="/core/invokeWorkflow.jsp" %>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>
<% // Initialize Sys Parms for JavaScript to use
    String isCisDesired = SysParmProvider.getInstance().getSysParm("PM_AFF_CS_SEARCH", "Y");
    String affCsRoles = SysParmProvider.getInstance().getSysParm("PM_AFF_CS_ROLES","");
%>
<script type="text/javascript">
    setSysParmValue("PM_AFF_CS_SEARCH", '<%=isCisDesired %>');
    setSysParmValue("PM_AFF_CS_ROLES", '<%=affCsRoles %>');
</script>
<script type="text/javascript" src="js/maintainAffiliation.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form action="maintainAffiliation.do" name="maintainAffiliationForm" method="post">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <input type="hidden" name="needToCaptureTransaction" value="<c:out value="${needToCaptureTransaction}"/>"/>
    <input type="hidden" name="affiliationRiskExpDate" value="<c:out value="${param.affiliationRiskExpDate}"/>"/>
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
    <tr>
        <td>&nbsp;</td>
    </tr>

    <tr>
        <td align=center>
            <fmt:message key="pm.maintainAffiliation.affiliationGridList.header" var="panelTitleForAffiliation"
                         scope="page">
                <fmt:param value="${riskName}"/>
                <fmt:param><%= FormatUtils.formatDateForDisplay(request.getAttribute("riskEffectiveFromDate").toString()) %></fmt:param>
                <fmt:param><%= FormatUtils.formatDateForDisplay(request.getParameter("affiliationRiskExpDate").toString()) %></fmt:param>
            </fmt:message>
            <%
                String panelTitleForAffiliation = (String) pageContext.getAttribute("panelTitleForAffiliation");
            %>

            <oweb:panel panelTitleId="panelTitleIdForAffiliation" panelContentId="panelContentIdForAffiliation"
                        panelTitle="<%= panelTitleForAffiliation %>">
                <tr>
                    <td colspan="6">
                        <oweb:actionGroup actionItemGroupId="PM_AFFILIATION_GRID_AIG" layoutDirection="horizontal"
                                          cssColorScheme="gray"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="6" align=center>
                        <c:set var="gridDisplayFormName" value="maintainAffiliationForm" scope="request"/>
                        <c:set var="gridDisplayGridId" value="maintainAffiliationListGrid" scope="request"/>
                        <c:set var="gridDetailDivId" value="maintainAffiliationListGridDiv" scope="request"/>
                        <c:set var="datasrc" value="#maintainAffiliationListGrid1" scope="request"/>
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
                        <fmt:message key="pm.maintainAffiliation.affiliationForm.header" var="affiliationFormHeader"
                                     scope="request"/>
                        <% String affiliationFormHeader = (String) request.getAttribute("affiliationFormHeader"); %>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="headerText" value="<%=  affiliationFormHeader %>"/>
                            <jsp:param name="isGridBased" value="true"/>
                        </jsp:include>
                    </td>
                </tr>
            </oweb:panel>
            <tr>
                <td colspan="6" align=center>
                    <oweb:actionGroup actionItemGroupId="PM_AFFILIATION_AIG"/>
                </td>
            </tr>
        </td>
    </tr>
    <br>

    <jsp:include page="/core/footerpopup.jsp"/>
