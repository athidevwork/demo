<%@ page import="dti.oasis.util.BaseResultSet" %><%--
  Description: View Experience Discount History  page

  Author: ryzhao
  Date: Aug 23, 2018


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  08/31/2018       ryzhao      188891 - Initial version.
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2018 Delphi Technology, inc. (dti)
--%>

<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<script type="text/javascript" src="js/viewExpDiscHistory.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>


<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<form name="expDiscHistory" action="viewExpDiscHistory.do" method="post">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <input type="hidden" name="riskBaseId" value="<c:out value="${param.riskBaseId}"/>"/>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>

    <tr>
        <td align=center>
            <fmt:message key="pm.viewExpDiscHistory.expDiscHistoryInfo.header" var="historyTitle" scope="page">
                <fmt:param value="${entityName}"/>
                <fmt:param value="${riskTypeDesc}"/>
                <fmt:param value="${clientId}"/>
            </fmt:message>
            <%
            String historyTitle = (String) pageContext.getAttribute("historyTitle");
            %>
            <oweb:panel panelTitleId="panelTitleIdForHistoryGrid" panelContentId="panelContentIdForHistoryGrid"
                        panelTitle="<%= historyTitle %>">
            <tr>
                <td colspan="6" align=center><br/>
                    <c:set var="gridDisplayFormName" value="expDiscHistory" scope="request"/>
                    <c:set var="gridDisplayGridId" value="expDiscHistoryGrid" scope="request"/>
                    <c:set var="datasrc" value="#expDiscHistoryGrid1" scope="request"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td>&nbsp;</td>
    </tr>
    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="PM_CLOSE_AIG"/>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>