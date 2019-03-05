<%@ page import="dti.oasis.util.*" %>
<%@ page import="dti.oasis.tags.XMLGridHeader" %>
<%--
  Description: IBNR Inactive Risk page

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

<jsp:useBean id="firstDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="secondDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="thirdDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="firstGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="secondGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="thirdGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<c:set var="isForDivPopup" value="true"></c:set>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<script type="text/javascript" src="<%=appPath%>/riskmgr/ibnrriskmgr/js/maintainIbnrRisk.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form action="maintainIbnrRisk.do" method="POST" name="maintainIbnrRisk">
    <input type="hidden" name="isInWorkflow" value="<c:out value="${isInWorkflow}"/>" />

<%@ include file="/pmcore/commonFormHeader.jsp" %>
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

    <c:set var="policyHeaderDisplayMode" value="invisible"/>
    <tr>
        <td colspan=8 align=center>
            <%@ include file="/policymgr/policyHeader.jsp" %>
        </td>
    </tr>

    <%-- Display first grid: forms list --%>
    <%
       BaseResultSet dataBean = firstDataBean;
       XMLGridHeader gridHeaderBean = firstGridHeaderBean;
    %>
    <tr>
        <td align=center>
            <fmt:message key="pm.associatedRisk.header" var="panelTitleForAssociatedRisk" scope="page"/>
            <%
                String panelTitleForAssociatedRisk = (String) pageContext.getAttribute("panelTitleForAssociatedRisk");
            %>
            <oweb:panel panelTitleId="panelTitleIdForAssociatedRisk" panelContentId="panelContentIdForAssociatedRisk" panelTitle="<%= panelTitleForAssociatedRisk %>" >

           <tr>
                <td colspan="6">
                    <oweb:actionGroup actionItemGroupId="PM_OPT_ASSO_RISK_AIG" layoutDirection="horizontal" cssColorScheme="gray"/>
                </td>
           </tr>
           <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="maintainIbnrRiskForm" scope="request" />
                    <c:set var="gridDisplayGridId" value="firstGrid" scope="request" />
                    <c:set var="gridDetailDivId" value="firstGridDetailDiv" scope="request"/>
                    <c:set var="datasrc" value="#firstGrid1" scope="request" />
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
           </tr>

           <!-- Display form -->
           <c:set var="dataBean" value="${firstDataBean}" scope="request"/>
           <c:set var="datasrc" value="#firstGrid1" scope="request"/>
           <tr>
                <td align=center>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="headerText" value="" />
                        <jsp:param name="isGridBased" value="true" />
                        <jsp:param name="isLayerVisibleByDefault" value="true"/>
                        <jsp:param name="divId" value="firstGridDetailDiv"/>
                        <jsp:param name="includeLayersWithPrefix" value="PM_IBNR_ASSO_RISK_FORM"/>
                    </jsp:include>
                </td>
           </tr>
           </oweb:panel>
        </td>
    </tr>

    <%-- Display Second grid: form versions --%>
    <%
       dataBean = secondDataBean;
       gridHeaderBean = secondGridHeaderBean;
    %>
    <tr>
        <td align=center>
            <fmt:message key="pm.ibnrInactiveRisk.header" var="panelTitleForIbnrInactiveRisk" scope="page"/>
            <%
                String panelTitleForIbnrInactiveRisk = (String) pageContext.getAttribute("panelTitleForIbnrInactiveRisk");
            %>
            <oweb:panel panelTitleId="panelTitleIdForIbnrInactiveRisk" panelContentId="panelContentIdForIbnrInactiveRisk" panelTitle="<%= panelTitleForIbnrInactiveRisk %>" >

           <tr>
                <td colspan="6">
                    <oweb:actionGroup actionItemGroupId="PM_OPT_INACTIVE_RISK_AIG" layoutDirection="horizontal" cssColorScheme="gray"/>
                </td>
           </tr>
           <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="maintainIbnrRiskForm" scope="request" />
                    <c:set var="gridDisplayGridId" value="secondGrid" scope="request" />
                    <c:set var="gridDetailDivId" value="secondGridDetailDiv" scope="request"/>
                    <c:set var="datasrc" value="#secondGrid1" scope="request" />
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
           </tr>

           <!-- Display form -->
           <c:set var="dataBean" value="${secondDataBean}" scope="request"/>
           <c:set var="datasrc" value="#secondGrid1" scope="request"/>
           <tr>
                <td align=center>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="headerText" value="" />
                        <jsp:param name="isGridBased" value="true" />
                        <jsp:param name="isLayerVisibleByDefault" value="true"/>
                        <jsp:param name="divId" value="secondGridDetailDiv"/>
                        <jsp:param name="includeLayersWithPrefix" value="PM_IBNR_INACTIVE_RISK_FORM"/>
                    </jsp:include>
                </td>
           </tr>
           </oweb:panel>

        </td>
    </tr>

    <%-- Display Third grid: form versions --%>
    <%
       dataBean = thirdDataBean;
       gridHeaderBean = thirdGridHeaderBean;
    %>
    <tr>
        <td align=center>
            <fmt:message key="pm.associatedRiskForInactiveRisk.header" var="panelTitleForAssoForInactiveRisk" scope="page"/>
            <%
                String panelTitleForAssoForInactiveRisk = (String) pageContext.getAttribute("panelTitleForAssoForInactiveRisk");
            %>
            <oweb:panel panelTitleId="panelTitleIdForAssoForInactiveRisk" panelContentId="panelContentIdForAssoForInactiveRisk" panelTitle="<%= panelTitleForAssoForInactiveRisk %>" >

           <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="maintainIbnrRiskForm" scope="request" />
                    <c:set var="gridDisplayGridId" value="thirdGrid" scope="request" />
                    <c:set var="datasrc" value="#thirdGrid1" scope="request" />
                    <c:set var="cacheResultSet" value="false"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
           </tr>
           </oweb:panel>

        </td>
    </tr>

    <tr>
        <td colspan="6" align=center>
            <oweb:actionGroup actionItemGroupId="PM_MNT_IBNR_RISK_AIG"/>
        </td>
    </tr>

<jsp:include page="/core/footerpopup.jsp" />
