<%--
  Description:   view layer detial jsp

  Author: rlli
  Date: July 19, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
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

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<form name="layerDetailList" action="viewLayerDetail.do" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <tr>
        <td colspan=8>
            <oweb:message/>
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
            <fmt:message key="pm.viewLayerDetail.layerDetail.header" var="panelTitleForLayerDetail" scope="page">
                <fmt:param value="${coverageCode}"/>
            </fmt:message>
            <%
                String panelTitleForLayerDetail = (String) pageContext.getAttribute("panelTitleForLayerDetail");
            %>
            <oweb:panel panelTitleId="panelTitleIdForLayerDetail" panelContentId="panelContentIdForLayerDetail" panelTitle="<%= panelTitleForLayerDetail %>" >
            <tr>
                <td colspan="6" align=center><br/>
                    <c:set var="gridDisplayFormName" value="layerDetailList" scope="request"/>
                    <c:set var="gridDisplayGridId" value="layerDetailListGrid" scope="request"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            </oweb:panel>
            <tr>
                <td colspan="6" align=center>
                    <oweb:actionGroup actionItemGroupId="PM_VIEW_LAYER_AIG"/>
                </td>
            </tr>
        </td>
    </tr>

<jsp:include page="/core/footerpopup.jsp"/>