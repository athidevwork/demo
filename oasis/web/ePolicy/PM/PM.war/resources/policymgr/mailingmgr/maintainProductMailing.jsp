<%--
  Description: jsp file for maintain product mailing
  Author: awu
  Date: Oct 14, 2013
  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ include file="/core/header.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<script type="text/javascript" src="js/maintainProductMailing.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<form name="productMailingList" action="maintainProductMailing.do" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>

    <tr>
        <td align=center>
            <fmt:message key="pm.maintainProductMailing.header" var="productMailingHeader" scope="page"/>
                <% String productMailingHeader = (String) pageContext.getAttribute("productMailingHeader"); %>
            <oweb:panel panelTitleId="panelTitleIdForProductMailingHeader"
                        panelContentId="panelContentIdForProductMailingHeader"
                        panelTitle="<%= productMailingHeader %>">
    <tr>
        <td colspan="6">
           <oweb:actionGroup actionItemGroupId="PM_PRODUCT_MAILING_AIG" layoutDirection="horizontal"  cssColorScheme="gray"/>
        </td>
    </tr>
    <tr>
        <td colspan="6" align=center><br/>
            <c:set var="gridDisplayFormName" value="productMailingList" scope="request"/>
            <c:set var="gridDisplayGridId" value="productMailingListGrid" scope="request"/>
            <c:set var="datasrc" value="#productMailingListGrid1" scope="request"/>
            <c:set var="gridDetailDivId" value="productMailing" scope="request"/>
            <c:set var="gridSortable" value="false" scope="request"/>
            <%@ include file="/pmcore/gridDisplay.jsp" %>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.maintainProductMailing.information" var="productMailingInfoHeader" scope="page"/>
            <% String productMailingInfoHeader = (String) pageContext.getAttribute("productMailingInfoHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%= productMailingInfoHeader %>"/>
                <jsp:param name="divId" value="productMailing"/>
                <jsp:param name="isGridBased" value="true"/>
                <jsp:param name="isLayerVisibleByDefault" value="true"/>
                <jsp:param name="includeLayersWithPrefix" value="PAGE_FIELDS_LAYER"/>
            </jsp:include>
        </td>
    </tr>
    </oweb:panel>

    <tr>
        <td colspan="6" align=center>
            <oweb:actionGroup actionItemGroupId="PM_PRO_MAILING_SAVE_AIG"/>
        </td>
    </tr>

<jsp:include page="/core/footer.jsp"/>