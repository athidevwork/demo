<%--
  Description:

  Author: yhchen
  Date: Jun 21, 2007

  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  10/25/2011       xnie        126107 - Added a page level hidden field childRiskBaseRecordId.
  03/10/2017       wrong       180675 - Added code to display message on parent Window in new
                                              UI tab style.
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<%@ include file="/core/invokeWorkflow.jsp" %>

<script type="text/javascript" src="<%=appPath%>/riskmgr/empphysmgr/js/maintainEmployedPhysician.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>


<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<form action="<%=appPath%>/riskmgr/empphysmgr/maintainEmployedPhysician.do" method=post name="empphysList">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <input type="hidden" name="riskId" value="<%= policyHeader.getRiskHeader().getRiskId() %>">
    <input type="hidden" name="childRiskBaseRecordId" value="<%= policyHeader.getRiskHeader().getRiskBaseRecordId() %>">

    <%-- Show error message --%>
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
        <td align=center>
            <fmt:message key="pm.maintainEmployedPhysician.hospitalLayer.header" var="hospitalFormHeader" scope="request"/>
            <% String hospitalFormHeader = (String) request.getAttribute("hospitalFormHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%=  hospitalFormHeader %>" />
                <jsp:param name="divId" value="hosptialDiv" />
                <jsp:param name="isGridBased" value="false" />
                <jsp:param name="isLayerVisibleByDefault" value="true" />
                <jsp:param name="collaspeTitleForPanel" value="<%=  hospitalFormHeader %>" />
                <jsp:param name="excludePageFields" value="true" />
                <jsp:param name="includeLayersWithPrefix" value="PM_EMP_PHYS_HOPITAL" />
                <jsp:param name="actionItemGroupId" value="PM_EMP_PHYS_HOSP_AIG" />
            </jsp:include>
        </td>
    </tr>
    <c:if test="${dataBean != null}">
    <tr>
        <td align=center>
            <fmt:message key="pm.maintainEmployedPhysician.grid.header" var="panelTitleForTailList" scope="page"/>
            <%
                String panelTitleForEmpphysList = (String) pageContext.getAttribute("panelTitleForTailList");
            %>
            <oweb:panel panelTitleId="panelTitleForEmpphysList" panelContentId="panelContentIdForEmpphysList" panelTitle="<%= panelTitleForEmpphysList %>" >
            <tr>
                <td colspan="6">
                    <oweb:actionGroup actionItemGroupId="PM_EMP_PHYS_GRID_AIG" layoutDirection="horizontal" cssColorScheme="gray" cssWidthInPX="75"/>
                </td>
            </tr>
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="empphysList" scope="request"/>
                    <c:set var="gridDisplayGridId" value="empphysListGrid" scope="request"/>
                    <c:set var="gridDetailDivId" value="empphysDetailDiv" scope="request"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            <tr> &nbsp;</tr>
            <tr>
                <td align=center>
                    <fmt:message key="pm.maintainEmployedPhysician.detail.header" var="empphysDetailFormHeader" scope="request"/>
                    <% String empphysDetailFormHeader = (String) request.getAttribute("empphysDetailFormHeader"); %>
                    <c:set var="datasrc" value="#empphysListGrid1" scope="request"/>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="headerText" value="<%= empphysDetailFormHeader %>" />
                        <jsp:param name="isGridBased" value="true" />
                        <jsp:param name="divId" value="empphysDetailDiv" />
                        <jsp:param name="isLayerVisibleByDefault" value="true" />
                        <jsp:param name="excludePageFields" value="false" />
                        <jsp:param name="includeLayersWithPrefix" value="PM_EMP_PHYS_DETAIL" />
                    </jsp:include>
                </td>
            </tr>

            </oweb:panel>
        </td>
    </tr>
    </c:if>
    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="PM_EMP_PHYS_AIG" layoutDirection="horizontal"/>
        </td>
    </tr>
    <jsp:include page="/core/footerpopup.jsp"/>
