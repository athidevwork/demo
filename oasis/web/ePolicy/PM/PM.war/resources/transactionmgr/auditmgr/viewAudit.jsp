<%--
  Description:

  Author: rlli
  Date: Sep 26, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  04/26/2010       syang       106401 - Add gridDetailDivId in auditList grid.
  03/20/2017       eyin        180675 - Changed message tag for UI change.
  11/15/2018       lzhang      194100   add buildNumber Parameter
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
<script type="text/javascript" src="js/viewAudit.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<form name="auditList" action="viewAudit.do" method=post>

    <input type=hidden name=process value="<c:out value="${process}"/>"/>
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
            <fmt:message key="pm.viewAudit.auditList.header" var="panelTitleForAudit" scope="page"/>
            <%
                String panelTitleForAudit = (String) pageContext.getAttribute("panelTitleForAudit");
            %>
            <oweb:panel panelTitleId="panelTitleIdForAudit" panelContentId="panelContentIdForAudit"
                        panelTitle="<%= panelTitleForAudit %>">

                <tr>
                    <td colspan="6" align=center><br/>
                        <c:set var="gridDisplayFormName" value="auditList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="auditListGrid" scope="request"/>
                        <c:set var="gridDetailDivId" value="auditDetail" scope="request"/>
                        <c:set var="datasrc" value="#auditListGrid1" scope="request"/>
                        <c:set var="cacheResultSet" value="true"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>

            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.viewAudit.auditDetail.header" var="auditFilterHeader" scope="request"/>
            <% String auditFilterHeader = (String) request.getAttribute("auditFilterHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%= auditFilterHeader %>"/>
                <jsp:param name="divId" value="auditDetail"/>
                <jsp:param name="isGridBased" value="true"/>
                <jsp:param name="excludeAllLayers" value="true"/>
            </jsp:include>
        </td>
    </tr>
    <tr>
        <td colspan="6" align=center>
            <oweb:actionGroup actionItemGroupId="PM_VIEW_LAYER_AIG"/>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>