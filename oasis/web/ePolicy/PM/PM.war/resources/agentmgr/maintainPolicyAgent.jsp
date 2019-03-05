<%--
  Description:

  Author: gjlong
  Date: Apr 26, 2007
                                                        N

  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/13/2018       tyang 194100 Add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:set var="isForDivPopup" value="true"></c:set>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<script language="javascript" src="<%=appPath%>/agentmgr/js/maintainPolicyAgent.js?
            <%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>

<%
    request.setAttribute("producerAgentLicIdCOLSPAN","7");
    request.setAttribute("subproducerAgentLicIdCOLSPAN","7");
    request.setAttribute("countersignerAgentLicIdCOLSPAN","7");
    request.setAttribute("authorizedrepAgentLicIdCOLSPAN","7");
    request.setAttribute("effectiveFromDateCOLSPAN","2");
 %>

<FORM action="maintainPolicyAgent.do" method="POST" NAME ="agentList">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <input type="hidden" name="subProducerIdCounterForSave" value="0"/>

    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
    <c:set var="policyHeaderDisplayMode" value="hide"/>
    <tr>
        <td colspan=8 align=center>
            <%@ include file="/policymgr/policyHeader.jsp" %>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.agentmgr.maintainAgent.listHeader" var="panelTitleForAgent" scope="page"/>
            <%
                String panelTitleForAgent = (String) pageContext.getAttribute("panelTitleForAgent");
            %>
            <oweb:panel panelTitleId="panelTitleIdForAgent" panelContentId="panelContentIdForAgent" panelTitle="<%= panelTitleForAgent %>" >
            <tr>
                <td colspan="6">
                    <oweb:actionGroup actionItemGroupId="PM_AGENT_GRID_AIG" layoutDirection="horizontal" cssColorScheme="gray"/>
                </td>
            </tr>
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="agentList" scope="request" />
                    <c:set var="gridDisplayGridId" value="agentListGrid" scope="request" />
                    <c:set var="gridDetailDivId" value="agentDetailDiv" scope="request"/>
                    <c:set var="datasrc" value="#agentListGrid1" scope="request" />
                    <c:set var="cacheResultSet" value="false"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.agentmgr.maintainAgent.formHeader" var="maintainAgentHeader" scope="request"/>
            <% String maintainAgentHeader = (String) request.getAttribute("maintainAgentHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%=  maintainAgentHeader %>" />
                <jsp:param name="isGridBased" value="true" />
                <jsp:param name="isLayerVisibleByDefault" value="true" />
            </jsp:include>
        </td>
    </tr>
    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="PM_AGENT_AIG" layoutDirection="horizontal"/>
        </td>
   </tr>
<%
    // Initialize Sys Parms for JavaScript to use
    String isRateReadyOnly = SysParmProvider.getInstance().getSysParm("COMM. RATES READONLY", "N");

%>

<script type="text/javascript">
     setSysParmValue("areRateFieldsReadOnly",'<%=isRateReadyOnly %>');     
</script>

<jsp:include page="/core/footerpopup.jsp" />
