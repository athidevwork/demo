<%@ page language="java" %>
<%--
  Description: Change Address Role

  Author: cyzhao
  Date: Feb 16, 2011


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  -----------------------------------------------------------------------------
  (C) 2010 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<%@ include file="/core/headerpopup.jsp" %>
<jsp:include page="/cicore/common.jsp"/>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<script language="javascript" src="<%=cisPath%>/addressmgr/addresslistmgr/js/openSelectAddressPopup.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=cisPath%>/addressmgr/js/openAddressRoleChgPopup.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=cisPath%>/addressmgr/js/maintainAddressRoleChg.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<oweb:constant constantClass="dti.ci.addressmgr.AddressFields"/>

<form name="changeAddressRoleForm" action="ciChgAddressRole.do" method="post">
    <tr>
        <td colspan="6">
            <oweb:message/>
        </td>
    </tr>

    <%@ include file="/cicore/commonFormHeader.jsp" %>

    <tr>
        <td colspan="6" class="infomessage">
            <b><fmt:message key="ci.address.addressRoleChgMgr.msg.info.hasAssociatedRoles"/></b>
        </td>
    </tr>
    <tr>
        <td colspan="6" align=center>
            <oweb:panel panelTitleId="panelTitleForAddressRoleList" panelContentId="panelContentIdForAddressRoleList" panelTitleLayerId="CI_CHG_ADR_ROLE_LIST_GH">
                <tr>
                    <td colspan="6" align=center><br/>
                        <c:set var="gridDisplayFormName" value="changeAddressRoleForm" scope="request"/>
                        <c:set var="gridDisplayGridId" value="addressRoleListGrid" scope="request"/>
                        <c:set var="selectable" value="false" scope="request"/>
                        <%@ include file="/core/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td colspan="6" class="errortext">
            <b><fmt:message key="ci.address.addressRoleChgMgr.msg.info.confirmedToTransfer"/></b>
        </td>
    </tr>
    <tr>
        <td colspan="6" align=center>
            <oweb:actionGroup actionItemGroupId="CI_CHG_ADR_ROLE_WARN_AIG" layoutDirection="horizontal" cssColorScheme="blue"/>
        </td>
    </tr>

<jsp:include page="/core/footerpopup.jsp"/>