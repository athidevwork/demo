<%@ page import="dti.oasis.util.BaseResultSet" %>
<%@ page import="dti.oasis.tags.XMLGridHeader" %>
<%@ page import="dti.oasis.session.UserSessionManager" %>
<%@ page import="dti.pm.core.http.RequestIds" %>
<%--
  Description:

  Author: yhchen
  Date: Jun 29, 2007

  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  06/12/2012       ryzhao      123721 - Add compGridButtonDiv to hide buttons
                               when the component grid has data but is hidden.
  05/29/2013       jshen       141758 - Added js variable currentPolicyCovComponentId
  11/13/2018       tyang       194100 -Add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%-- Component Layer --%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<jsp:useBean id="componentListGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="componentListGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>
<%String appPath = request.getContextPath();%>

<script type="text/javascript" src="<%=appPath%>/componentmgr/js/maintainComponent.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<c:if test="${empty componentListHeader}">
    <c:set var="componentListHeader" value=""></c:set>
</c:if>

<c:if test="${componentListGridDataBean != null}">
    <tr>
        <td align=center>
            <%
                String componentListHeader = "";
                if (request.getAttribute("componentListHeader")!=null) {
                    componentListHeader = (String) request.getAttribute("componentListHeader");
                } else if (pageContext.getAttribute("componentListHeader")!=null) {
                    componentListHeader = (String) pageContext.getAttribute("componentListHeader");
                }
            %>
            <oweb:panel panelTitleId="panelTitleIdForComponent" panelContentId="panelContentIdForComponent" panelTitle="<%= componentListHeader %>" >
            <tr>
                <td colspan="6">
                    <div id="compGridButtonDiv" style="display:block">
                    <oweb:actionGroup actionItemGroupId="PM_COMP_GRID_AIG" layoutDirection="horizontal" cssColorScheme="gray"/>
                    </div>
                </td>
            </tr>
            <tr>
                <td align=center>
                    <c:set var="gridDisplayGridId" value="componentListGrid" scope="request"/>
                    <c:set var="gridDetailDivId" value="componentDetailDiv" scope="request"/>
                    <c:set var="gridId" value="componentListGrid" scope="request"/>
                    <c:set var="gridSizeFieldIdPrefix" value="comp_"/>
                    <% BaseResultSet dataBean = componentListGridDataBean;
                       XMLGridHeader gridHeaderBean = componentListGridHeaderBean; %>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>


            <tr>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td align=center>
                    <c:set var="datasrc" value="#componentListGrid1" scope="request"/>
                    <% String headerText = (String) request.getAttribute("headerText"); %>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="headerText" value="<%= headerText%>"/>
                        <jsp:param name="isGridBased" value="true"/>
                        <jsp:param name="divId" value="componentDetailDiv"/>
                        <jsp:param name="isLayerVisibleByDefault" value="true"/>
                        <jsp:param name="excludePageFields" value="true"/>
                        <jsp:param name="includeLayersWithPrefix" value="PM_COMP"/>
                    </jsp:include>
                </td>
            </tr>
            </oweb:panel>
        </td>
    </tr>
</c:if>

<script type="text/javascript">
    var currentPolicyCovComponentId = <%=UserSessionManager.getInstance().getUserSession().get(RequestIds.POLICY_COV_COMPONENT_ID)%>;
</script>

<c:remove var="componentListHeader"/>