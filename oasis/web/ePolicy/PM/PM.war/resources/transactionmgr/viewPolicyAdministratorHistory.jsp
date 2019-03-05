<%--
  Description:

  Author: yhyang
  Date: Mar 26, 2008


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  03/20/2017       eyin        180675 - Changed message tag for UI change.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<c:set var="isForDivPopup" value="true"></c:set>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>
<form name="policyAdminHistoryList" action="viewPolicyAdministratorHistory.do" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <c:set scope="request" var="commentsCOLSPAN" value="7"/>
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
    <c:set var="policyHeaderDisplayMode" value="invisible"/>
    <tr>
        <td colspan=8 align=center>
            <%@ include file="/policymgr/policyHeader.jsp" %>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.viewPolicyAdminHistoryInfo.adminHistoryList.header" var="panelTitleForPolicyAdmin" scope="page"/>
            <%
                String panelTitleForPolicyAdmin = (String) pageContext.getAttribute("panelTitleForPolicyAdmin");
            %>
            <oweb:panel panelTitleId="panelTitleIdForPolicyAdmin" panelContentId="panelContentIdForPolicyAdmin"
                        panelTitle="<%= panelTitleForPolicyAdmin %>">
            <tr>
                <td colspan="6" align=center><br/>
                    <c:set var="gridDisplayFormName" value="policyAdminHistoryList" scope="request"/>
                    <c:set var="gridDisplayGridId" value="policyAdminHistoryListGrid" scope="request"/>
                    <c:set var="gridSortable" value="true" scope="request"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
           </oweb:panel>
        </td>
    </tr>
    <tr>
        <td colspan="6" align=center>
            <oweb:actionGroup actionItemGroupId="PM_VIEW_POLICYADMINHI_AIG"/>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>