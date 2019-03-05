<%@ page import="dti.oasis.util.BaseResultSet" %>
<%@ page import="dti.oasis.tags.XMLGridHeader" %>
<%@ page import="dti.pm.policymgr.taxmgr.TaxFields" %>
<%--
  Description:

  Author: wdang
  Date: Oct 13, 2014


  Revision Date     Revised By  Description
  -----------------------------------------------------------------------------
  10/13/2014        wdang       Initial Version.
  03/10/2017        eyin        180675 - change message tag for UI change.
  11/15/2018        eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2014 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<script type="text/javascript" src="<%=appPath%>/policymgr/taxmgr/js/maintainTax.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<%dti.oasis.util.BaseResultSet dataBean = null;%>
<%dti.oasis.tags.XMLGridHeader gridHeaderBean = null;%>
<jsp:useBean id="riskListGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="riskListGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="taxListGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="taxListGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<form name="taxList" action="<%=appPath%>/policymgr/taxmgr/maintainTax.do" method="post">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <input type="hidden" name="selectRiskId" value="<%=request.getParameter("selectRiskId")%>"/>
    
    <tr>
        <td>
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
        <td align=center>
            <%@ include file="/policymgr/policyHeader.jsp" %>
        </td>
    </tr>
    
    <tr id="maintainTaxDiv">
        <td align=center>
        <fmt:message key="pm.maintainTax.riskList.header" var="panelTitleForRiskList" scope="page"/>
        <%
            String panelTitleForRiskList = (String) pageContext.getAttribute("panelTitleForRiskList");
        %>
        <oweb:panel panelTitleId="panelTitleForRiskList" panelContentId="panelContentIdForRiskList" panelTitle="<%= panelTitleForRiskList %>">
            <tr>
                <td align=center>
                    <c:set var="gridDisplayFormName" value="riskList" scope="request"/>
                    <c:set var="gridDisplayGridId" value="riskListGrid" scope="request"/>
                    <c:set var="gridDetailDivId" value="riskDetailDiv" scope="request"/>
                    <% gridHeaderBean = riskListGridHeaderBean; %>
                    <% dataBean = riskListGridDataBean; %>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            <tr>
                <td>&nbsp;</td>
            </tr>
        </oweb:panel>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.maintainTax.taxList.header" var="panelTitleForTaxList" scope="page"/>
            <%
                String panelTitleForTaxList = (String) pageContext.getAttribute("panelTitleForTaxList");
            %>
            <oweb:panel panelTitleId="panelTitleForTaxList" panelContentId="panelContentIdForTaxList" panelTitle="<%= panelTitleForTaxList %>" >
            
            <tr>
                <td align=left>
                    <oweb:actionGroup actionItemGroupId="PM_MAINTAIN_TAX_GRID_AIG" layoutDirection="horizontal" cssColorScheme="gray" cssWidthInPX="75"/>
                </td>
            </tr>
            <tr>
                <td align=center>
                    <c:set var="gridDisplayFormName" value="taxList" scope="request"/>
                    <c:set var="gridDisplayGridId" value="taxListGrid" scope="request"/>
                    <c:set var="gridDetailDivId" value="taxDetailDiv" scope="request"/>
                    <% gridHeaderBean = taxListGridHeaderBean; %>
                    <% dataBean = taxListGridDataBean; %>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            <tr>
                <td align=center>
                    <fmt:message key="pm.maintainTax.taxForm.header" var="panelTitleForTaxForm" scope="page"/>
                    <% String panelTitleForTaxForm = (String) request.getAttribute("panelTitleForTaxForm"); %>
                    <c:set var="datasrc" value="#taxListGrid1" scope="request"/>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="headerText" value="<%=panelTitleForTaxForm%>" />
                        <jsp:param name="isGridBased" value="true"/>
                        <jsp:param name="divId" value="taxDetailDiv"/>
                        <jsp:param name="isLayerVisibleByDefault" value="true"/>
                        <jsp:param name="excludePageFields" value="true"/>
                        <jsp:param name="dataBeanName" value="taxListGridDataBean" />
                        <jsp:param name="includeLayersWithPrefix" value="PM_MAINTAIN_TAX"/>
                    </jsp:include>
                </td>
            </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td align=center>
             <oweb:actionGroup actionItemGroupId="PM_MAINTAIN_TAX_AIG"/>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>
