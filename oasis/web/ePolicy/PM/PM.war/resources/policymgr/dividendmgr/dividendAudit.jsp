<%--
  Description: jsp file for display dividend audit.
  Author: awu
  Date: Dec 26, 2013
  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<c:set var="isForDivPopup" value="true"></c:set>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<script type="text/javascript" src="<%=appPath%>/policymgr/dividendmgr/js/dividendAudit.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form action="<%=appPath%>/policymgr/dividendmgr/viewDividendAudit.do" method="POST" name="dividendAuditList">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <tr>
        <td>
            <table cellpadding=0 cellspacing=0 width=100%>
                <tr>
                    <td><oweb:message/></td>
                </tr>
            </table>
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
           <fmt:message key="pm.view.dividend.audit.search.header" var="panelTitleIdForDividendSearch" scope="page"/>
                <% String dividendSearchTitle = (String) pageContext.getAttribute("panelTitleIdForDividendSearch");%>
           <oweb:panel panelTitleId="panelTitleIdForDividendSearchHeader"
                 panelContentId="panelContentIdForDividendSearchHeader"
                 panelTitle="<%= dividendSearchTitle %>">
        <tr>
            <td align=center>
               <jsp:include page="/core/compiledFormFields.jsp">
                  <jsp:param name="displayAsPanel" value="false"/>
                  <jsp:param name="hasPanelTitle" value="false"/>
                  <jsp:param name="divId" value="dividendAuditSearchDiv" />
                  <jsp:param name="isGridBased" value="false" />
                  <jsp:param name="isLayerVisibleByDefault" value="true" />
               </jsp:include>
            </td>
            <td align=left width="50%">
               <oweb:actionGroup actionItemGroupId="PM_DIVIDEND_AUD_AIG" layoutDirection="horizontal"/>
            </td>
         </tr>
         </oweb:panel>
         </td>
    </tr>

    <c:if test="${dataBean != null}">
        <tr>
            <td align=center>
                <fmt:message key="pm.view.dividend.audit.header" var="panelTitleForDividendAudit"/>
                <% String panelTitleForDividendAudit = (String) pageContext.getAttribute("panelTitleForDividendAudit"); %>
                <oweb:panel panelTitleId="panelTitleForDividendAudit" panelContentId="panelContentIdForDividendAudit" panelTitle="<%= panelTitleForDividendAudit %>" >
        <tr>
        <td align=center><br/>
            <c:set var="gridDisplayFormName" value="dividendAuditList" scope="request" />
            <c:set var="gridDisplayGridId" value="dividendAuditListGrid" scope="request" />
            <c:set var="datasrc" value="#dividendAuditListGrid1" scope="request" />
            <c:set var="cacheResultSet" value="false"/>
            <%@ include file="/pmcore/gridDisplay.jsp" %>
        </td>
        </tr>

        </oweb:panel>
        </td>
    </tr>
    </c:if>
    <tr>
        <td colspan="6" align=center>
            <oweb:actionGroup actionItemGroupId="PM_VIEW_DIVIDEND_AUD_AIG"/>
        </td>
    </tr>

<jsp:include page="/core/footerpopup.jsp" />