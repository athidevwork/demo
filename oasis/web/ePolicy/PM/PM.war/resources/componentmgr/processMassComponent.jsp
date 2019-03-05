<%--
  Description: Process Mass Component.

  Author: yhyang
  Date: Aug 8, 2009


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  03/13/2017       eyin        180675 - Changed the error msg tag for UI change.
  11/13/2018       tyang       194100 - Add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2009 Delphi Technology, inc. (dti)
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ page import="dti.oasis.request.RequestStorageManager" %>
<c:set var="isForDivPopup" value="true"></c:set>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<script type="text/javascript" src="js/processMassComponent.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<%
    String applyResult = "undefined";
    if(RequestStorageManager.getInstance().has("applyResult")){
        applyResult = String.valueOf(RequestStorageManager.getInstance().get("applyResult"));
    }
%>
<script type="text/javascript">
    var applyResult = '<%=applyResult%>';
</script>

<form name="processComponentForm" action="processComponent.do" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <input type="hidden" name="productCoverageCode" value="<c:out value="${param.productCoverageCode}" />">
    <tr>
        <td colspan=8>
            <%
                if(pmUIStyle.equals("T")) {
            %>
            <oweb:message displayMessagesOnParent="true"/>
            <%
                }
            %>
            <%
                if(pmUIStyle.equals("B")) {
            %>
            <oweb:message/>
            <%
                }
            %>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.process.mass.component.header" var="componentHeader" scope="page"/>
            <% String componentHeader = (String) pageContext.getAttribute("componentHeader"); %>
            <oweb:panel panelTitleId="panelTitleIdForComponentHeader"
                        panelContentId="panelContentIdForComponentHeader"
                        panelTitle="<%= componentHeader %>">
                <tr>
                    <td colspan="6">
                        <oweb:actionGroup actionItemGroupId="PM_COMP_UPDATE_ADD_AIG" layoutDirection="horizontal"
                                          cssColorScheme="gray"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="6" align=center><br/>
                        <c:set var="gridDisplayFormName" value="processComponentForm" scope="request"/>
                        <c:set var="gridDisplayGridId" value="componentListGrid" scope="request"/>
                        <c:set var="datasrc" value="#componentListGrid1" scope="request"/>
                        <c:set var="gridDetailDivId" value="componentDetailDiv" scope="request"/>
                        <c:set var="gridSortable" value="false" scope="request"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
                <tr>
                    <td align=center>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="headerText" value=""/>
                            <jsp:param name="divId" value="componentDetailDiv"/>
                            <jsp:param name="isGridBased" value="true"/>
                            <jsp:param name="isLayerVisibleByDefault" value="true"/>
                        </jsp:include>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>

    <tr>
        <td colspan="6" align=center>
            <oweb:actionGroup actionItemGroupId="PM_COMP_UPDATE_APPLY_AIG"/>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>