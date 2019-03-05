<%@ page language="java" %>
<%--
  Description: Maintain Policy Agent Output Options page

  Author: yjmiao
  Date: Mar 9, 2011


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  10/17/2018       dzhang      195835: Grid replacement: add page layer id and form action.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/cicore/common.jsp" %>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<script type="text/javascript" src="js/maintainPolicyAgentOutputOptions.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<jsp:include page="/core/fieldlayerdep.jsp"/>

<form name="polAgentOutputOptionForm" method="post" action="maintainPolicyAgentOutputOptions.do">
    <%@ include file="/cicore/commonFormHeader.jsp" %>

    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>

    <input type="hidden" name="agentId" value="<%=request.getParameter("agentId")%>"/>
    <input type="hidden" name="entityId" value="<%=request.getParameter("entityId")%>"/>

    <c:set value="" var="paramForTitle" scope="page"/>

    <c:if test="${!empty param.policyId}">
        <%-- Agent Output Option List Grid --%>
            <%
               String agentUrl = cisPath + "/agentmgr/agentOutputOptions.do?agentId=" + request.getParameter("agentId")
                                         + "&entityId=" + request.getParameter("entityId");
            %>
        <c:set value="Policy" var="paramForTitle" scope="page"/>
        <input type="hidden" name="policyId" value="<%=request.getParameter("policyId")%>"/>
    <tr>
        <td align=center>
            <iframe id="iframeAgentOutputOption" allowtransparency="true" width="100%" height="300"
                    marginwidth="0" marginheight="0" hspace="0" vspace="0" frameborder="0" scrolling="no"
                    src="<%=agentUrl%>">
            </iframe>
        </td>
    </tr>
    </c:if>

    <%-- Policy Agent Output Options Grid --%>
    <c:if test="${dataBean != null}">
    <tr>
        <td align=center>
                <fmt:message key="ci.agentmgr.output.option.agent.grid.title"
                             var="panelTitleForPolAgentOutputOptionGrid"
                             scope="page">
                    <fmt:param value="${paramForTitle}"/>
                </fmt:message>
                    <%
                        String panelTitleForPolAgentOutputOptionGrid = (String) pageContext.getAttribute("panelTitleForPolAgentOutputOptionGrid");
                    %>
            <oweb:panel panelTitleId="panelTitleIdForPolAgentOutputOptionGrid"
                        panelContentId="panelContentIdForPolAgentOutputOptionGrid"
                        panelTitle="<%=panelTitleForPolAgentOutputOptionGrid%>"
                        panelTitleLayerId="CI_AGENT_OUTPUT_GH">
    <tr>
        <td align=left>
            <oweb:actionGroup actionItemGroupId="CI_COMMON_ADD_DEL_AIG" layoutDirection="horizontal"
                              cssColorScheme="gray"/>
        </td>
    </tr>
    <tr>
        <td colspan="7" align="center">
            <c:set var="gridDisplayFormName" value="polAgentOutputOptionForm" scope="request"/>
            <c:set var="gridDisplayGridId" value="polAgentOutputOptionListGrid" scope="request"/>
            <c:set var="gridDetailDivId" value="polAgentOutputOptionDiv" scope="request"/>
            <c:set var="datasrc" value="#polAgentOutputOptionListGrid1" scope="request"/>
            <c:set var="cacheResultSet" value="false"/>
            <%@ include file="/core/gridDisplay.jsp" %>
        </td>
    </tr>
        <%-- Display grid form --%>
    <tr>
        <td colspan="7" align=center>
            <fmt:message key="ci.agentmgr.output.option.details.agent.title" var="optionDetailsTitle" scope="page">
                <fmt:param value="${paramForTitle}"/>
            </fmt:message>
            <% String optionDetailsTitle = (String) pageContext.getAttribute("optionDetailsTitle"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%=  optionDetailsTitle %>"/>
                <jsp:param name="isLayerVisibleByDefault" value="true"/>
                <jsp:param name="isGridBased" value="true"/>
                <jsp:param name="excludePageFields" value="true"/>
                <jsp:param name="includeLayerIds" value="CI_AGENT_OUTPUT_FORM"/>
            </jsp:include>
        </td>
    </tr>
    </oweb:panel>
    </td>
    </tr>
    </c:if>

    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="CI_COMMON_SAVE_AIG" layoutDirection="horizontal"/>
        </td>
    </tr>

    <jsp:include page="/core/footerpopup.jsp"/>

