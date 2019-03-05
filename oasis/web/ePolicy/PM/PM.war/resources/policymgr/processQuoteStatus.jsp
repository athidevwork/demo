<%--
  Description: Process Quote Status. 

  Author: yhyang
  Date: May 05, 2008


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  02/22/2011       wfu         113063 - Add parameter to support forms trigger
  03/10/2017       wli         180675 - Changed the error msg to be located in parent frame for UI change.
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
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
<script type="text/javascript" src="js/processQuoteStatus.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<form name="quoteStatusList" action="processQuoteStatus.do" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <input type="hidden" name="recentStatus" value="<c:out value="${recentStatus}"/>"/>
    <input type="hidden" name="isTriggerForms" value="<c:out value="${isTriggerForms}"/>"/>
        <input type="hidden" name="hasMessage"
               value="<%=MessageManager.getInstance().getConfirmationPrompts().hasNext()?'Y':'N'%>"/>
    <c:set scope="request" var="commentsCOLSPAN" value="7"/>
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
            <fmt:message key="pm.processQuoteStatus.selection.header" var="quoteStatusHeader" scope="request"/>
            <% String quoteStatusHeader = (String) request.getAttribute("quoteStatusHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%= quoteStatusHeader%>"/>
                <jsp:param name="divId" value="quoteStatusSelection"/>
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="isLayerVisibleByDefault" value="true"/>
                <jsp:param name="includeLayersWithPrefix" value="PM_STATUS_SELECTION"/>
            </jsp:include>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.processQuoteStatus.history.header" var="panelTitleForQuoteStatus" scope="page"/>
            <%
                String panelTitleForQuoteStatus = (String) pageContext.getAttribute("panelTitleForQuoteStatus");
            %>
            <oweb:panel panelTitleId="panelTitleIdForQuoteStatus" panelContentId="panelContentIdForQuoteStatus"
                        panelTitle="<%= panelTitleForQuoteStatus %>">
                <tr>
                    <td colspan="6" align=center><br/>
                        <c:set var="gridDisplayFormName" value="quoteStatusList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="quoteStatusListGrid" scope="request"/>
                        <c:set var="gridSortable" value="false" scope="request"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td colspan="6" align=center>
            <oweb:actionGroup actionItemGroupId="PM_PC_QUOTE_STATUS_AIG"/>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>