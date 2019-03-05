<%--
  Description:
  Select manual tail coverags for adding new tail
  Author: yhchen
  Date: Apr 29, 2008


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/15/2018       lzhang      194100   add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<c:set var="isForDivPopup" value="true"></c:set>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<script type="text/javascript" src="<%=appPath%>/tailmgr/js/selectTail.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form action="selectLocation.do" method="POST" name="selectFteRiskList">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <%-- Show error message --%>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
    <%-- Policy Info invisible panel --%>
    <c:set var="policyHeaderDisplayMode" value="invisible"/>
    <tr>
        <td colspan=8 align=center>
            <%@ include file="/policymgr/policyHeader.jsp" %>
        </td>
    </tr>

    <c:if test="${dataBean != null}">
    <tr>
        <td align=center>
            <fmt:message key="pm.selectTail.tailList.header" var="panelTitleForSelectTail" scope="page"/>
            <%
                String panelTitleForSelectTail = (String) pageContext.getAttribute("panelTitleForSelectTail");
            %>
            <oweb:panel panelTitleId="panelTitleIdForSelectTail" panelContentId="panelContentIdForSelectTail"
                        panelTitle="<%= panelTitleForSelectTail %>">
                <tr>
                    <td colspan="6" align=center><br/>
                        <c:set var="gridDisplayFormName" value="tailList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="tailListGrid" scope="request"/>
                       <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    </c:if>

    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="PM_SELECT_CANCEL_AIG" layoutDirection="horizontal"/>
        </td>
    </tr>
    <jsp:include page="/core/footerpopup.jsp"/>

