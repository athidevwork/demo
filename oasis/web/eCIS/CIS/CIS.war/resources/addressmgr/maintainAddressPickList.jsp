<%@ page language="java" %>
<%--
  Description: Address Pick List Page.

  Author: cyzhao
  Date: Feb 18, 2011


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  -----------------------------------------------------------------------------
  (C) 2009 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<%@ include file="/core/headerpopup.jsp" %>
<jsp:include page="/cicore/common.jsp"/>
<script language="javascript" src="<%=cisPath%>/addressmgr/js/maintainAddressPickList.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script language="javascript" src="<%=cisPath%>/addressmgr/addresslistmgr/js/openSelectAddressPopup.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<form name="addressPickListForm" action="ciAddressPickList.do" method="post">
    <tr>
        <td colspan=6>
            <oweb:message/>
        </td>
    </tr>

    <%@ include file="/cicore/commonFormHeader.jsp" %>

    <tr>
        <td align=center>
            <!-- Display Grid -->
            <oweb:panel panelTitleId="panelTitleForAddressPickList" panelContentId="panelContentIdForAddressPickList" panelTitleLayerId="CI_ADDR_PICK_LIST_GH">
                <tr>
                    <td colspan="6" align=center>
                        <c:set var="gridDisplayFormName" value="addressPickListForm" scope="request"/>
                        <c:set var="gridDisplayGridId" value="addressPickListGrid" scope="request"/>
                        <%@ include file="/core/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
     <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="CI_ADDR_PICK_LIST_AIG" layoutDirection="horizontal" cssColorScheme="blue"/>
        </td>
    </tr>

<jsp:include page="/core/footerpopup.jsp"/>