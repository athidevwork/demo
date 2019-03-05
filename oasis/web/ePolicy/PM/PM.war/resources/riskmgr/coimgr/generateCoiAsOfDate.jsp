<%--
  Description: As of Date Page for Generate Client COI

  Author: Joe Shen
  Date: Dec 21, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  08/18/2011       Michael    issue 122156
  11/15/2018       eyin       issue 194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2007 Delphi Technology, inc. (dti)
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

<script type="text/javascript" src="js/generateCoiAsOfDate.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form action="standaloneGenerateCoi.do" name="asOfDateForGenCoiForm" method="post">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <input type="hidden" name="dataLoaded" value="<c:out value="${dataLoaded}"/>"/>
    <input type="hidden" name="parentGridId" value="<c:out value="${parentGridId}"/>"/>
    <input type="hidden" name="pmCoiClaimsParam" value="<c:out value="${pmCoiClaimsParam}"/>"/>
    <input type="hidden" name="entityId" value="<c:out value="${entityId}"/>"/>
    <input type="hidden" name="saveCode" value="<c:out value="${saveCode}"/>"/>
    <input type="hidden" name="minimumDate" value="<c:out value="${minimumDate}"/>"/>
    <input type="hidden" name="maximumDate" value="<c:out value="${maximumDate}"/>"/>

    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>

    <%-- Display form --%>
    <tr>
        <td align=center>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="isGridBased" value="false"/>
            </jsp:include>
        </td>
    </tr>
    <%-- Display grid --%>
    <tr>
        <td align=center>
            <fmt:message key="pm.generateClientCoi.gridList.header" var="panelTitleForCoi" scope="page"/>
            <%
                String panelTitleForCoi = (String) pageContext.getAttribute("panelTitleForCoi");
            %>
            <oweb:panel panelTitleId="panelTitleForCoi" panelContentId="panelContentIdForCoi" panelTitle="<%= panelTitleForCoi %>">
                <tr>
                    <td colspan="6">
                        <oweb:actionGroup actionItemGroupId="PM_GENCLI_COI_ASDT_AIG" layoutDirection="horizontal" cssColorScheme="gray"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="6" align=center>
                        <c:set var="gridDisplayFormName" value="asOfDateForGenCoiForm" scope="request"/>
                        <c:set var="gridDisplayGridId" value="asOfDateForGenCoiListGrid" scope="request"/>
                        <c:set var="datasrc" value="#asOfDateForGenCoiListGrid1" scope="request"/>
                        <c:set var="cacheResultSet" value="false"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
            <tr>
                <td>&nbsp;</td>
            </tr>
        </td>
    </tr>
     <tr>
        <td align="center" colspan="6">
            <oweb:actionGroup actionItemGroupId="PM_GENCLI_COI_AIG"
                              cssColorScheme="blue" layoutDirection="horizontal">
            </oweb:actionGroup>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>