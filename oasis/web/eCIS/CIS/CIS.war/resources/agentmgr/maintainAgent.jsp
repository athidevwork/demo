<%@ page import="dti.oasis.util.BaseResultSet" %>
<%@ page import="dti.oasis.tags.XMLGridHeader" %>
<%--
  Description:

  Author: James
  Date: Mar 26, 2008

  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  10/07/2008       yhyang      Issue#86934 Move CIS Agent to eCIS.
  07/01/2013       hxk         Issue 141840
                               If the entity is not readonly, include the message
                               tag here and not on parent page of iFrame.
                               This makes sure that when entity is readonly, we
                               put the messages above the name to make it consistent
                               w/in CIS.
  07/15/2016       iwang       177546: Added panels for Agent and Agent Override.
  05/31/2018       ylu         Issue 109213: refactor update to fix save error.
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  10/16/2018       dzhang      195835: Grid replacement
  -----------------------------------------------------------------------------
  (C) 2008 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<%
    String entityNameDisplay = (String) request.getAttribute(ICIConstants.ENTITY_NAME_PROPERTY);
    if (StringUtils.isBlank(entityNameDisplay)) {
        entityNameDisplay = MessageManager.getInstance().formatMessage("ci.agentmgr.maintainAgent.formHeader");
    } else {
        entityNameDisplay = MessageManager.getInstance().formatMessage("ci.entity.search.label.agent") + " " + entityNameDisplay;
    }
%>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="payListGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="payListGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="contractListGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="contractListGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="commissionListGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="commissionListGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="agentStaffListGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="agentStaffListGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="agentStaffOverrideListGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="agentStaffOverrideListGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>

<c:set var="globalActionItemGroupId" value="CI_FOLDER_AG"></c:set>
<%@ include file="/core/header.jsp" %>
<jsp:include page="/cicore/common.jsp"/>
<jsp:include page="/CI_EntitySelect.jsp"/>

<c:set var="tabMenuGroupId" value="${tabGroupId}"></c:set>
<%@ include file="/core/tabheader.jsp" %>

<script type='text/javascript' src="<%=cisPath%>/js/gridbtnclicks.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=cisPath%>/agentmgr/js/maintainAgent.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<FORM action="ciAgent.do" method="POST" NAME="agentList">
    <%@ include file="/cicore/commonFormHeader.jsp" %>
    <%--TODO Use PK to replace entity FK. --%>
    <jsp:include page="/cicore/ciFolderCommon.jsp" />
    <input type="hidden" name="<%=ICIConstants.ENTITY_FK_PROPERTY%>"
           value="<%=(String) request.getAttribute(ICIConstants.ENTITY_FK_PROPERTY)%>"/>

<c:if test="${isEntityReadOnlyYN !='Y'}">
<tr>
    <td colspan=8>
        <oweb:message/>
    </td>
</tr>



</c:if>

<tr>
    <td align=center>
        <fmt:message key="ci.agentmgr.maintainAgent.formHeader" var="maintainAgentHeader" scope="request"/>
        <% String maintainAgentHeader = (String) request.getAttribute("maintainAgentHeader"); %>
        <jsp:include page="/core/compiledFormFields.jsp">
            <jsp:param name="headerText" value="<%=  maintainAgentHeader %>"/>
            <jsp:param name="isGridBased" value="false"/>
            <jsp:param name="excludeAllLayers" value="true"/>
            <jsp:param name="divId" value="agentFields"/>
            <jsp:param name="actionItemGroupId" value="CI_AGENT_OUTPUT_AIG"/>
        </jsp:include>
    </td>
</tr>

<tr>
    <td align=center>

        <oweb:panel panelTitleId="panelTitleIdForAgentPayCommission"
                    panelContentId="panelContentIdForAgentPayCommission"
                    panelTitleLayerId="CI_AGENT_PAY_COMMISSION_GH">
            <tr>
                <td colspan="6">
                    <oweb:actionGroup actionItemGroupId="CI_AGENT_PAY_GRID_AIG"
                                      layoutDirection="horizontal"
                                      cssColorScheme="gray"/>
                </td>
            </tr>
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="payList" scope="request"/>
                    <c:set var="gridDisplayGridId" value="payListGrid" scope="request"/>
                    <c:set var="gridDetailDivId" value="payDetailDiv" scope="request"/>
                    <c:set var="datasrc" value="#payListGrid1" scope="request"/>
                    <c:set var="cacheResultSet" value="false"/>
                    <% BaseResultSet dataBean = payListGridDataBean;
                        XMLGridHeader gridHeaderBean = payListGridHeaderBean; %>
                    <%@ include file="/core/gridDisplay.jsp" %>
                </td>
            </tr>
            <tr>
                <td align=center>
                    <c:set var="datasrc" value="#payListGrid1" scope="request"/>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="headerTextLayerId" value="CI_AGENT_PAY_COMMISSION_DETAIL"/>
                        <jsp:param name="gridID" value="payListGrid"/>
                        <jsp:param name="divId" value="payDetailDiv"/>
                        <jsp:param name="excludePageFields" value="true"/>
                        <jsp:param name="isLayerVisibleByDefault" value="true"/>
                        <jsp:param name="includeLayerIds" value="CI_AGENT_PAY_COMMISSION_DETAIL"/>
                    </jsp:include>
                </td>
            </tr>
        </oweb:panel>
    </td>
</tr>
<tr>
    <td align=center>
        <oweb:panel panelTitleId="panelTitleIdForAgentPayContract"
                    panelContentId="panelContentIdForAgentContract"
                    panelTitleLayerId="CI_AGENT_CONTRACT_GH">
            <tr>
                <td colspan="6">
                    <oweb:actionGroup actionItemGroupId="CI_AGENT_CONT_GRID_AIG"
                                      layoutDirection="horizontal"
                                      cssColorScheme="gray"/>
                </td>
            </tr>
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="contractList" scope="request"/>
                    <c:set var="gridDisplayGridId" value="contractListGrid" scope="request"/>
                    <c:set var="gridDetailDivId" value="contractDetailDiv" scope="request"/>
                    <c:set var="datasrc" value="#contractListGrid1" scope="request"/>
                    <c:set var="cacheResultSet" value="false"/>
                    <% BaseResultSet dataBean = contractListGridDataBean;
                        XMLGridHeader gridHeaderBean = contractListGridHeaderBean; %>
                    <%@ include file="/core/gridDisplay.jsp" %>
                </td>
            </tr>
            <tr>
                <td align=center>
                    <c:set var="datasrc" value="#contractListGrid1" scope="request"/>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="isGridBased" value="true"/>
                        <jsp:param name="gridID" value="contractListGrid"/>
                        <jsp:param name="divId" value="contractDetailDiv"/>
                        <jsp:param name="excludePageFields" value="true"/>
                        <jsp:param name="isLayerVisibleByDefault" value="true"/>
                        <jsp:param name="headerTextLayerId" value="CI_AGENT_CONTRACT_DETAIL"/>
                        <jsp:param name="includeLayerIds" value="CI_AGENT_CONTRACT_DETAIL"/>
                    </jsp:include>
                </td>
            </tr>
        </oweb:panel>
    </td>
</tr>
<tr>
    <td align=center>
        <oweb:panel panelTitleId="panelTitleIdForAgentContractCommission"
                    panelContentId="panelContentIdForAgentContractCommission"
                    panelTitleLayerId="CI_AGENT_CONTRACT_COMMISSION_GH">
            <tr>
                <td colspan="6">
                    <oweb:actionGroup actionItemGroupId="CI_AGENT_COMM_GRID_AIG"
                                      layoutDirection="horizontal"
                                      cssColorScheme="gray"/>
                </td>
            </tr>
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="commissionList" scope="request"/>
                    <c:set var="gridDisplayGridId" value="commissionListGrid" scope="request"/>
                    <c:set var="gridDetailDivId" value="commissionDetailDiv" scope="request"/>
                    <c:set var="datasrc" value="#commissionListGrid1" scope="request"/>
                    <c:set var="cacheResultSet" value="false"/>
                    <% BaseResultSet dataBean = commissionListGridDataBean;
                        XMLGridHeader gridHeaderBean = commissionListGridHeaderBean; %>
                    <%@ include file="/core/gridDisplay.jsp" %>
                </td>
            </tr>
            <tr>
                <td align=center>
                    <c:set var="datasrc" value="#commissionListGrid1" scope="request"/>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="headerTextLayerId" value="CI_AGENT_CONTRACT_COMMISSION_DETAIL"/>
                        <jsp:param name="gridID" value="commissionListGrid"/>
                        <jsp:param name="divId" value="commissionDetailDiv"/>
                        <jsp:param name="excludePageFields" value="true"/>
                        <jsp:param name="isLayerVisibleByDefault" value="true"/>
                        <jsp:param name="includeLayerIds" value="CI_AGENT_CONTRACT_COMMISSION_DETAIL"/>
                    </jsp:include>
                </td>
            </tr>
        </oweb:panel>
    </td>
</tr>

<tr>
    <td align=center>
        <oweb:panel panelTitleId="panelTitleIdForAgentStaff"
                    panelContentId="panelContentIdForAgentStaff"
                    panelTitleLayerId="CI_AGENT_STAFF_GH">
            <tr>
                <td colspan="6">
                    <oweb:actionGroup actionItemGroupId="CI_AGENT_STAFF_GRID_AIG"
                                      layoutDirection="horizontal"
                                      cssColorScheme="gray"/>
                </td>
            </tr>
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="agentStaffList" scope="request"/>
                    <c:set var="gridDisplayGridId" value="agentStaffListGrid" scope="request"/>
                    <c:set var="gridDetailDivId" value="agentStaffDetailDiv" scope="request"/>
                    <c:set var="datasrc" value="#agentStaffListGrid1" scope="request"/>
                    <c:set var="cacheResultSet" value="false"/>
                    <% BaseResultSet dataBean = agentStaffListGridDataBean;
                        XMLGridHeader gridHeaderBean = agentStaffListGridHeaderBean; %>
                    <%@ include file="/core/gridDisplay.jsp" %>
                </td>
            </tr>
            <tr>
                <td align=center>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="headerTextLayerId" value="CI_AGENT_STAFF_DETAIL"/>
                        <jsp:param name="isGridBased" value="true"/>
                        <jsp:param name="gridID" value="agentStaffListGrid"/>
                        <jsp:param name="divId" value="agentStaffDetailDiv"/>
                        <jsp:param name="excludePageFields" value="true"/>
                        <jsp:param name="isLayerVisibleByDefault" value="true"/>
                        <jsp:param name="includeLayerIds" value="CI_AGENT_STAFF_DETAIL"/>
                    </jsp:include>
                </td>
            </tr>
        </oweb:panel>
    </td>
</tr>


<tr>
    <td align=center>
        <oweb:panel panelTitleId="panelTitleIdForAgentStaffOverride"
                    panelContentId="panelContentIdForAgentStaffOverride"
                    panelTitleLayerId="CI_AGENT_STAFF_OVERRIDE_GH">
            <tr>
                <td colspan="6">
                    <oweb:actionGroup actionItemGroupId="CI_AGENT_STAFF_OVRD_AIG"
                                      layoutDirection="horizontal"
                                      cssColorScheme="gray"/>
                </td>
            </tr>
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="agentStaffOverrideList" scope="request"/>
                    <c:set var="gridDisplayGridId" value="agentStaffOverrideListGrid" scope="request"/>
                    <c:set var="gridDetailDivId" value="agentStaffOverrideDetailDiv" scope="request"/>
                    <c:set var="datasrc" value="#agentStaffOverrideListGrid1" scope="request"/>
                    <c:set var="cacheResultSet" value="false"/>
                    <% BaseResultSet dataBean = agentStaffOverrideListGridDataBean;
                        XMLGridHeader gridHeaderBean = agentStaffOverrideListGridHeaderBean; %>
                    <%@ include file="/core/gridDisplay.jsp" %>
                </td>
            </tr>
            <tr>
                <td align=center>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="headerTextLayerId" value="CI_AGENT_STAFF_OVERRIDE_DETAIL"/>
                        <jsp:param name="gridID" value="agentStaffOverrideListGrid"/>
                        <jsp:param name="divId" value="agentStaffOverrideDetailDiv"/>
                        <jsp:param name="excludePageFields" value="true"/>
                        <jsp:param name="isLayerVisibleByDefault" value="true"/>
                        <jsp:param name="includeLayerIds" value="CI_AGENT_STAFF_OVERRIDE_DETAIL"/>
                    </jsp:include>
                </td>
            </tr>
        </oweb:panel>
    </td>
</tr>


<tr>
    <td align=center>
        <oweb:actionGroup actionItemGroupId="CI_AGENT_AIG" layoutDirection="horizontal"/>
    </td>
</tr>

<%@ include file="/core/tabfooter.jsp" %>

<jsp:include page="/core/footer.jsp"/>
