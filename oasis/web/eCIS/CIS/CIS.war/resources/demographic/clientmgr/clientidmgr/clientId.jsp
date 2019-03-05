<%@ page import="dti.ci.helpers.ICIClaimsConstants"%>
<%@ page import="dti.ci.helpers.ICIConstants"%>
<%@ page import="dti.oasis.tags.WebLayer" %>
<%--
  Description:

  Author: kshen
  Date: Jan 24, 2008


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  07/22/2011       parker      Issue#122838: could not delete clientId.
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<%@ include file="/core/headerpopup.jsp" %>
<jsp:include page="/cicore/common.jsp"/>
<script type='text/javascript' src="<%=cisPath%>/js/gridbtnclicks.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="js/clientId.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>


<form name="ClientIdListForm" action="ciMaintainClientId.do" method="POST">
    <jsp:include page="/cicore/commonFormHeader.jsp"/>
    <input type="hidden" name="<%=ICIConstants.PK_PROPERTY%>"
           value="<%=request.getAttribute(ICIConstants.PK_PROPERTY)%>"/>

    <tr>
        <td>
            <oweb:message/>
        </td>
    </tr>
    <tr>
        <td align=center>
            <oweb:panel panelTitleId="panelTitleIdForClientIdList"
                        panelContentId="panelContentIdForClientIdList"
                        panelTitleLayerId="CI_CLIENT_ID_GH">
                <tr>
                    <td colspan="6">
                        <oweb:actionGroup actionItemGroupId="CI_CLIENTID_GIRD_AIG" cssColorScheme="gray"
                                          layoutDirection="horizontal"></oweb:actionGroup>
                    </td>
                </tr>
                <tr>
                    <td colspan="6" align=center>
                        <c:set var="gridDisplayFormName" value="ClientIdListForm" scope="request"/>
                        <c:set var="gridDetailDivId" value="clientIdDetailDiv" scope="request"/>
                        <c:set var="gridDisplayGridId" value="clientIdListGrid" scope="request" />
                        <c:set var="gridSizeFieldIdPrefix" value="entityIdNumber_"/>
                        <c:set var="cacheResultSet" value="false"/>
                        <%@ include file="/core/gridDisplay.jsp" %>
                    </td>
                </tr>
                <tr>
                    <fmt:message key="ci.entity.detail.form.label" var="detailTitle" scope="request"/>
                    <% String detailTitle = (String) request.getAttribute("detailTitle"); %>
                    <td align=center>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="headerText" value="<%=detailTitle%>"/>
                            <jsp:param name="gridID" value="clientIdListGrid"/>
                            <jsp:param name="divId" value="clientIdDetailDiv"/>
                            <jsp:param name="removeFieldPrefix" value="true"/>
                            <jsp:param name="excludeAllLayers" value="true"/>
                        </jsp:include>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="CI_CLIENTID_AIG" layoutDirection="horizontal"/>
        </td>
    </tr>

<jsp:include page="/core/footerpopup.jsp"/>