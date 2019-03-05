<%@ page import="org.apache.struts.Globals"%>
<%@ page import="org.apache.struts.taglib.html.Constants"%>
<%@ page import="dti.pm.core.http.RequestIds"%>
<%@ page language="java"%>
<%--
  Description: Select Risk Type page

  Author: sxm
  Date: Mar 20, 2007

  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  10/10/2007       Mark        Added hidden field "openWhichWindow"
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

<c:set var="isForDivPopup" value="true"></c:set>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<script type="text/javascript" src="js/selectRiskType.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form action="selectRiskType.do" method="POST" name="selectRiskTypeList">
<%@ include file="/pmcore/commonFormHeader.jsp" %>
<input type="hidden" name="openWhichWindow" value="<c:out value="${openWhichWindow}"/>">
<tr>
    <td>
        <td><oweb:message/></td>
    </td>
</tr>

<% if (request.getAttribute(RequestIds.DATA_BEAN) != null) { %>
    <jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
    <tr>
        <td align=center>
            <fmt:message key="pm.selectRiskType.header"var="panelTitleForAddRisk" scope="page"/>
            <%
                String panelTitleForAddRisk = (String) pageContext.getAttribute("panelTitleForAddRisk");
            %>
            <oweb:panel panelTitleId="panelTitleIdForAddRisk" panelContentId="panelContentIdForAddRisk" panelTitle="<%= panelTitleForAddRisk %>" >
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="selectRiskTypeList" scope="request" />
                    <c:set var="gridDisplayGridId" value="selectRiskTypeGrid" scope="request" />
                    <c:set var="datasrc" value="#selectRiskTypeGrid1" scope="request" />
                    <c:set var="cacheResultSet" value="false"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            </oweb:panel>
            <tr>
                <td align=center>
                    <oweb:actionGroup actionItemGroupId="PM_SELRISKTYPE_AIG"/>
                </td>
            </tr>            
        </td>
    </tr>
<% } %>

<jsp:include page="/core/footerpopup.jsp" />
