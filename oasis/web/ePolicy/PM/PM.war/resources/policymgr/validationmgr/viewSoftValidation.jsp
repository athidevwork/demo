<%--
  Description:

  Author: wdang
  Date: 11/11/2016


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/11/2016       tzeng       166929 - Initial version.
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2016 Delphi Technology, inc. (dti)
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

<script type="text/javascript" src="js/viewSoftValidation.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form name="softValidationList" action="<%=appPath%>/policymgr/validationmgr/viewsoftvalidation.do" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <tr>
        <td>
            <oweb:message/>
        </td>
    </tr>
    <c:set var="policyHeaderDisplayMode" value="invisible"/>
    <tr>
        <td align=center>
            <%@ include file="/policymgr/policyHeader.jsp" %>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.viewSoftValidation.validationFilter.header" var="softValidationFilterHeader" scope="request"/>
            <% String softValidationFilterHeader = (String) request.getAttribute("softValidationFilterHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%=  softValidationFilterHeader %>" />
                <jsp:param name="divId" value="viewSoftValidationFilter" />
                <jsp:param name="isGridBased" value="false" />
                <jsp:param name="excludeAllLayers" value="true" />
            </jsp:include>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.viewSoftValidation.validationTrans.header" var="softValidationTransHeader" scope="request"/>
            <% String softValidationTransHeader = (String) request.getAttribute("softValidationTransHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%=  softValidationTransHeader %>" />
                <jsp:param name="divId" value="viewSoftValidationTrans" />
                <jsp:param name="isGridBased" value="false" />
                <jsp:param name="excludePageFields" value="true" />
                <jsp:param name="isLayerVisibleByDefault" value="true" />
            </jsp:include>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.viewSoftValidation.validationList.header" var="panelTitleForSoftValidationList" scope="page"/>
            <% String panelTitleForSoftValidationList = (String) pageContext.getAttribute("panelTitleForSoftValidationList"); %>
            <oweb:panel panelTitleId="panelTitleIdForSoftValidationList" panelContentId="panelContentIdForSoftValidationList"
                        panelTitle="<%= panelTitleForSoftValidationList %>" >
            <tr>
                <td align=center>
                <c:set var="gridDisplayFormName" value="softValidationList" scope="request"/>
                <c:set var="gridDisplayGridId" value="softValidationListGrid" scope="request"/>
                <c:set var="datasrc" value="#softValidationListGrid1" scope="request"/>
                <c:set var="gridSortable" value="false" scope="request"/>
                <c:set var="cacheResultSet" value="true"/>
                <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="PM_VIEW_SOFT_VALID_AIG" layoutDirection="horizontal"/>
        </td>
    </tr>

<jsp:include page="/core/footerpopup.jsp"/>