<%@ page import="dti.oasis.util.BaseResultSet" %>
<%@ page import="dti.oasis.tags.XMLGridHeader" %>
<%--
  Description:
  For maintain tail quote
  
  Author: yhchen
  Date: Jan 29, 2008


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  09/05/2011       ryzhao      124622 - For pages with multiple grids, update the name of data bean
                               and grid header bean for all but the first grid.
                               The name of data bean should be gridId + "DataBean".
                               The name of grid header bean should be gridId + "HeaderBean".
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ page import="dti.oasis.tags.XMLGridHeader" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<c:set var="isForDivPopup" value="true"></c:set>
<c:set var="skipHeaderFooterContent" value="true"></c:set>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<script type="text/javascript" src="<%=appPath%>/policymgr/tailquotemgr/js/maintainTailQuote.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>
<jsp:useBean id="tailListGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="tailListGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>

<form action="<%=appPath%>/policymgr/tailquotemgr/maintainTailQuote.do" method=post name="dummyForm">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <c:set var="policyHeaderDisplayMode" value="invisible"/>
    <tr>
        <td colspan=8 align=center>
            <%@ include file="/policymgr/policyHeader.jsp" %>
        </td>
    </tr>

    <c:if test="${tailListGridDataBean != null}">
    <tr>
        <td colspan="6">
            <oweb:actionGroup actionItemGroupId="PM_TAIL_QUOTE_INFO_AIG" layoutDirection="horizontal"
                              cssColorScheme="gray"/>
        </td>
    </tr>
    <tr>
        <td colspan="8" align=center>
            <c:set var="gridDisplayGridId" value="tailListGrid" scope="request"/>
            <c:set var="gridDetailDivId" value="tailDetailDiv" scope="request"/>
            <c:set var="gridId" value="tailListGrid" scope="request"/>
            <%  BaseResultSet dataBean = tailListGridDataBean;
                XMLGridHeader gridHeaderBean = tailListGridHeaderBean; %>
            <%@ include file="/pmcore/gridDisplay.jsp" %>
        </td>
    </tr>

    <tr>
        <td>&nbsp;</td>
    </tr>
    <tr>
        <td align=center>
            <c:set var="datasrc" value="#tailListGrid1" scope="request"/>
            <fmt:message key="pm.maintainTailQuote.tailQuoteForm.header" var="tailFormHeader" scope="request"/>
            <% String tailFormHeader = (String) request.getAttribute("tailFormHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%= tailFormHeader%>"/>
                <jsp:param name="isGridBased" value="true"/>
                <jsp:param name="divId" value="tailDetailDiv"/>
                <jsp:param name="isLayerVisibleByDefault" value="true"/>
                <jsp:param name="excludePageFields" value="true"/>
                <jsp:param name="includeLayersWithPrefix" value="PM_TAIL_QUOTE_FM"/>
            </jsp:include>
        </td>
    </tr>
    </c:if>
    <jsp:include page="/core/footerpopup.jsp"/>
