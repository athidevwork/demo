<%--
  Description:

  Author: zlzhu
  Date: Dec 19, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  Dec 19, 2007     zlzhu       Created
  03/19/2009       kenney      Added Form Letter support for eCIS
  09/28/2010       wfu         111776: Replaced hardcode string with resource definition
  12/20/2013       hxk         139442
                               1)  Add addl info layer to page
                               2)  Refactor/rename jsp
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  10/23/2018       dpang       195835: Grid replacement.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ page language="java" import="dti.ci.helpers.ICIClaimsConstants" %>
<%@ page import="dti.ci.helpers.ICIConstants" %>
<%@ page import="dti.oasis.navigationmgr.NavigationManager" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="dti.oasis.util.StringUtils" %>
<%@ page import="dti.oasis.util.SysParmProvider" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<c:set var="globalActionItemGroupId" value="CI_FOLDER_AG"></c:set>
<%@ include file="/core/header.jsp" %>

<script type="text/javascript">
    // This scripts must go after header.jsp. !!!!!!!!!!!!!!!

    <%
        // The id of actionItem tr is "R_actionitem_" + actionItem.getLabel()
        // (see the codes in header.jsp, line:515),
        // and the label of a action item is configruable in webwb, we must get
        // the row id by program.
        ArrayList actionItems = null;
        actionItems = NavigationManager.getInstance().getAllActionItems(pageBean);

        String gotoClaimRowId = null;
        String gotoCaseRowId = null;
        for (int i=0, len = actionItems.size(); i < len; i++) {
             MenuBean actionItem = (MenuBean) actionItems.get(i);
             if ("claimsGoToClaim".equals(actionItem.getId())) {
                    gotoClaimRowId = actionItem.getId();
             } else if ("claimsGoToCase".equals(actionItem.getId())) {
                    gotoCaseRowId = actionItem.getId();
             }
        }
    %>

    var winName = window.name;
    // If this page is a popup page, remove "Goto Claim" & "Goto Case" action item.
    if (winName == "CISPopup") {
    <%
        // If we get the id of "Goto Claim", remove the row.
        if (!StringUtils.isBlank(gotoClaimRowId)) {
    %>
        var rowGotoClaim = document.getElementById("<%=gotoClaimRowId%>");
        if (rowGotoClaim != null) {
            rowGotoClaim.removeNode(true);
        }
    <%
        }
        // If we get the id of "Goto Case", remove the row.
        if (!StringUtils.isBlank(gotoClaimRowId)) {
    %>
        var rowGotoCase = document.getElementById("<%=gotoCaseRowId%>");
        if (rowGotoCase != null) {
            rowGotoCase.removeNode(true);
        }
    <%
        }
    %>
    }
</script>
<jsp:include page="/CI_EntitySelect.jsp"/>
<jsp:include page="/cicore/common.jsp"/>

<c:set var="tabMenuGroupId" value="${tabGroupId}"></c:set>
<%@ include file="/core/tabheader.jsp" %>

<script type="text/javascript" src="<%=cisPath%>/policysummarymgr/js/policySummary.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form name="CIPoliciesForm" action="ciPolicies.do" method="POST">
    <jsp:include page="/cicore/commonFormHeader.jsp"/>

    <jsp:include page="/cicore/ciFolderCommon.jsp" />

    <tr valign="top">
        <td colspan="6" class="tabTitle">
            <oweb:message/>
        </td>
    </tr>

    <tr>
        <td class="tabTitle">
            <b><fmt:message key="ci.entity.search.label.policy"/> <c:out value="${param.entityName}"/></b>
        </td>
    </tr>
<%   // Only show the Addl Info Layer if the sysparm is set to Y
     String showAddlInfo = SysParmProvider.getInstance().getSysParm("CI_POLTAB_ADDLINFO", "N");
     if (showAddlInfo.equals("Y")) {
%>
                <tr>
                    <td colspan="6">
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="isGridBased" value="false"/>
                            <jsp:param name="divId" value="addlInfo"/>
                            <jsp:param name="headerTextLayerId" value="POLICY_ADDLINFO_LAYER"/>
                            <jsp:param name="isLayerVisibleByDefault" value="true"/>
                            <jsp:param name="excludePageFields" value="true"/>
                            <jsp:param name="includeLayerIds" value="POLICY_ADDLINFO_LAYER"/>
                        </jsp:include>
                    </td>
                </tr>
<%
     }
%>
<tr>
    <td>
    <iframe id="iframePolicy" scrolling="no" allowtransparency="true" width="98%" height="1000" frameborder="0" src=""></iframe>
    </td>
</tr>

<%@ include file="/core/tabfooter.jsp" %>
<jsp:include page="/core/footer.jsp"/>
