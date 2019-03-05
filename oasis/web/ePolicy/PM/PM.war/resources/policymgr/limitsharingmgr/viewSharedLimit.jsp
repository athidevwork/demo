<%--
  Description: View Shared Limit Page

  Author: dzhang
  Date: January 12, 2011


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  03/10/2017       wli         180675 - Changed the error msg to be located in parent frame for UI change.
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2011 Delphi Technology, inc. (dti)
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<c:set var="isForDivPopup" value="true"></c:set>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<script type="text/javascript" src="js/viewSharedLimit.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<form name="sharedLimitList" action="viewSharedLimit.do" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

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
            <fmt:message key="pm.viewSharedLimitInfo.sharedLimitFilter.header" var="sharedLimitFilterHeader" scope="request"/>
            <% String sharedLimitFilterHeader = (String) request.getAttribute("sharedLimitFilterHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%=  sharedLimitFilterHeader %>" />
                <jsp:param name="divId" value="viewSharedLimitFilter" />
                <jsp:param name="isGridBased" value="false" />
                <jsp:param name="includeLayersWithPrefix" value="PM_VIEW_SHLIMIT_FILTER_LAYER"/>
                <jsp:param name="isLayerVisibleByDefault" value="true" />
                <jsp:param name="actionItemGroupId" value="PM_VIEW_SHLMT_FILTER_AIG"/>
            </jsp:include>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.viewSharedLimitInfo.sharedLimitList.header" var="panelTitleForSharedLimit" scope="page"/>
            <%
                String panelTitleForSharedLimit = (String) pageContext.getAttribute("panelTitleForSharedLimit");
            %>
            <oweb:panel panelTitleId="panelTitleForSharedLimit" panelContentId="panelContentIdForSharedLimit" panelTitle="<%= panelTitleForSharedLimit %>" >

            <tr>
                <td colspan="6" align=center><br/>
                    <c:set var="gridDisplayFormName" value="sharedLimitList" scope="request"/>
                    <c:set var="gridDisplayGridId" value="sharedLimitListGrid" scope="request"/>
                    <c:set var="datasrc" value="#sharedLimitListGrid1" scope="request"/>
                    <c:set var="gridSortable" value="false" scope="request"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>

            </oweb:panel>
            <tr>
                <td colspan="6" align=center>
                    <oweb:actionGroup actionItemGroupId="PM_VIEW_SHLMT_AIG"/>
                </td>
            </tr>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>