<%@ page language="java" %>
<%--
  Description:

  Author: yhchen
  Date: Oct 30, 2007


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

<c:set var="isForDivPopup" value="true"></c:set>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<script type="text/javascript" src="<%=appPath%>/riskmgr/empphysmgr/js/selectFteRisk.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form action="selectLocation.do" method="POST" name="selectFteRiskList">
<%@ include file="/pmcore/commonFormHeader.jsp" %>

<input type=hidden name=entityId value="<c:out value="${entityId}"/>">

<tr>
    <td>
        <table cellpadding=0 cellspacing=0 width=100%>
            <tr>
                <td>&nbsp;&nbsp;</td>
                <td><oweb:message/></td>
            </tr>
        </table>
    </td>
</tr>
<tr><td>&nbsp;</td></tr>

<c:if test="${dataBean != null}">
    <tr>
        <td align=center>
            <fmt:message key="pm.selectFteRisk.header" var="panelTitleForSelectFteRisk" scope="page"/>
            <%
                String panelTitleForSelectFteRisk = (String) pageContext.getAttribute("panelTitleForSelectFteRisk");
            %>
            <oweb:panel panelTitleId="panelTitleIdForSelectFteRisk" panelContentId="panelContentIdForSelectFteRisk" panelTitle="<%= panelTitleForSelectFteRisk %>" >
            <tr>
                <td colspan="6" align=center><br/>
                    <c:set var="gridDisplayFormName" value="selectFteRiskList" scope="request" />
                    <c:set var="gridDisplayGridId" value="selectFteRiskGrid" scope="request" />
                    <c:set var="datasrc" value="#selectFteRiskGrid1" scope="request" />
                    <c:set var="cacheResultSet" value="false"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            </oweb:panel>
        </td>
    </tr>
</c:if>

<tr>
    <td align=center>
        <oweb:actionGroup actionItemGroupId="PM_SEL_TFERISK_AIG" layoutDirection="horizontal"/>
    </td>
</tr>
<jsp:include page="/core/footerpopup.jsp" />
