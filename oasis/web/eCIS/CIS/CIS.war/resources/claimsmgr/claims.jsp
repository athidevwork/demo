<%--
  Description: claims page

  Author: Hong Yuan
  Date: Dec 7, 2005

  Revision Date    Revised By  Description
  ---------------------------------------------------
  01/11/2007       PXS         Added hidden field for DBPoolAppendix
                               that's coming from header.jsp
  05/15/2007       MLM         Added UI2 Changes
  07/05/2007       Mark        Added UI2 Changes
  08/30/2007       Kenney      remove UIStyleEdition;
                               change to panel tag;
                               change to compiledFormField page
  04/09/2008       wer         Removed passing dbPoolId appendix to comply with new Role-based dbPoolId configuration.
  03/19/2009       kenney      Added Form Letter support for eCIS
  09/30/2010       wfu         111776: Replaced hardcode string with resource definition
  05/25/2018       ylu         192609: accodingly update for refactor test.
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  10/08/2018       kshen       195835. Grid replacement.
  11/12/2018       hxk         196950
                               1)  Add restrictBaseB field.
  ------------------------------------------------------------------
  (C) 2004 Delphi Technology, inc. (dti)
--%>
<%@ page import="dti.ci.helpers.ICIClaimsConstants,
                 dti.oasis.tags.WebLayer" %>
<%@ page import="dti.ci.helpers.ICIConstants" %>
<%@ page import="dti.oasis.navigationmgr.NavigationManager" %>
<%@ page import="dti.oasis.util.MenuBean" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="dti.oasis.util.StringUtils" %>
<%@ page import="dti.oasis.app.ApplicationContext" %>
<%@ page import="dti.oasis.util.BaseResultSet" %>
<%@ page import="dti.oasis.tags.XMLGridHeader" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<%
    String message = (String) request.getAttribute(ICIConstants.MSG_PROPERTY);
    if (StringUtils.isBlank(message) || message.equalsIgnoreCase("null")) {
        message = "";
    }
%>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="participantsGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="participantsGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="companionGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="companionGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<c:set var="globalActionItemGroupId" value="CI_FOLDER_AG"></c:set>
<%@ include file="/core/header.jsp" %>
<jsp:include page="/cicore/common.jsp"/>
<jsp:include page="/CI_EntitySelect.jsp"/>
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
        var rowGotoClaim = getSingleObject("<%=gotoClaimRowId%>");
        if (rowGotoClaim != null) {
            rowGotoClaim.removeNode(true);
        }
    <%
        }
        // If we get the id of "Goto Case", remove the row.
        if (!StringUtils.isBlank(gotoClaimRowId)) {
    %>
        var rowGotoCase = getSingleObject("<%=gotoCaseRowId%>");
        if (rowGotoCase != null) {
            rowGotoCase.removeNode(true);
        }
    <%
        }
    %>
    }
</script>

<c:set var="tabMenuGroupId" value="${tabGroupId}"></c:set>
<%@ include file="/core/tabheader.jsp" %>

<script type='text/javascript' src="<%=cisPath%>/claimsmgr/js/claims.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>


<form name="CIClaimsForm" action="ciClaims.do" method="POST">
    <%@ include file="/cicore/commonFormHeader.jsp" %>
    <jsp:include page="/cicore/ciFolderCommon.jsp" />
    <tr>
        <td>
            <input type="hidden" name=casePK value="<c:out value="${casePK}"/>"/>
            <INPUT type="hidden" name="restrictB" value="<c:out value="${restrictB}"/>"/>
            <INPUT type="hidden" name="restrictCaseB" value="<c:out value="${restrictCaseB}"/>"/>
        </td>
    </tr>
        <tr valign="top">
            <td colspan="6" class="tabTitle">
                <oweb:message/>
            </td>
        </tr>
        <tr>
            <td colspan="6">
                <b><%=message%></b>
            </td>
        </tr>
    <c:choose>
        <c:when test="${claimId == '' || claimId == null}">
        <tr>
            <td class="tabTitle">
                <b><fmt:message key="ci.entity.noClaims.form.title"/>
                    <c:out value="${param.entityName}"/>
                </b>
            </td>
        </tr>
        </c:when>
        <c:otherwise>
        <tr>
            <td class="tabTitle"><b><fmt:message key="ci.entity.search.label.claims"/>
                <c:out value="${param.entityName}"/>
            </b></td>
        </tr>
        <tr>
            <td>
                <%
                    String panelCaption = ApplicationContext.getInstance().getProperty("claims.legend.claimInfo", "Claim Info");
                %>
                <jsp:include page="/core/compiledFormFields.jsp">
                    <jsp:param name="isGridBased" value="false"/>
                    <jsp:param name="divId" value="claimInfoDIV"/>
                    <jsp:param name="headerText" value="<%=panelCaption%>"/>
                    <jsp:param name="excludeAllLayers" value="true"/>
                    <jsp:param name="actionItemGroupId" value="CI_CLAIMS_AIG"/>
                </jsp:include>
            </td>
        </tr>
        <tr>
            <td>
                <table width="100%">
                    <tr>
                        <td width="45%" valign="top">
                            <oweb:panel panelContentId="panelContentForParticipants"
                                        panelTitleId="panelTitleIdForParticipants"
                                        panelTitleLayerId="Participants_Layer">
                                <tr>
                                    <td width="100%">
                                        <c:set var="gridDisplayFormName" value="CIClaimsForm" scope="request"/>
                                        <c:set var="gridDisplayGridId" value="participantsGrid" scope="request"/>
                                        <c:set var="datasrc" value="#participantsGrid1" scope="request"/>
                                        <c:set var="cacheResultSet" value="false"/>
                                        <%
                                            BaseResultSet dataBean = participantsGridDataBean;
                                            XMLGridHeader gridHeaderBean = participantsGridHeaderBean;
                                        %>
                                        <%@ include file="/core/gridDisplay.jsp" %>
                                    </td>
                                </tr>
                            </oweb:panel>
                        </td>
                        <td width="55%" valign="top">
                            <oweb:panel panelContentId="panelContentForCompanionClaims"
                                        panelTitleId="panelTitleIdForCompanionClaims"
                                        panelTitleLayerId="Companion_Claims_Layer">
                                <tr>
                                    <td width="100%">
                                        <c:set var="gridDisplayFormName" value="CIClaimsForm" scope="request"/>
                                        <c:set var="gridDisplayGridId" value="companionGrid" scope="request"/>
                                        <c:set var="datasrc" value="#companionGrid1" scope="request"/>
                                        <c:set var="cacheResultSet" value="false"/>
                                        <%
                                            BaseResultSet dataBean = companionGridDataBean;
                                            XMLGridHeader gridHeaderBean = companionGridHeaderBean;
                                        %>
                                        <%@ include file="/core/gridDisplay.jsp" %>
                                    </td>
                                </tr>
                            </oweb:panel>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        </c:otherwise>
    </c:choose>

    <%@ include file="/core/tabfooter.jsp" %>

<jsp:include page="/core/footer.jsp"/>