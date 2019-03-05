<%--
  Description:

  Author: Bhong
  Date: Feb 04, 2009


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  10/08/2010       wfu         111776: Replaced hardcode string with resource definition
  -----------------------------------------------------------------------------
  (C) 2009 Delphi Technology, inc. (dti)
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<c:set var="isForDivPopup" value="true"></c:set>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/cicore/common.jsp" %>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<%-- Web form --%>
<form action="" name="lockedPolicyForm">
    <%@ include file="/cicore/commonFormHeader.jsp" %>
    <tr>
        <td align=center>

            <oweb:panel panelTitleId="panelTitleIdForLockedPolicy"
                        panelContentId="panelContentIdForLockedPolicy"
                        panelTitleLayerId="CI_LOCK_POL_GH">
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="lockedPolicyForm" scope="request"/>
                    <c:set var="gridDisplayGridId" value="lockedPolicyGrid" scope="request"/>
                    <c:set var="cacheResultSet" value="false"/>

                    <%@ include file="/core/gridDisplay.jsp" %>
                </td>
            </tr>
            </oweb:panel>
            <tr>
                <td align=center>
                    <oweb:actionGroup actionItemGroupId="CI_LOCKED_POL_AIG"/>
                </td>
            </tr>
<script type="text/javascript">
    function closePage() {
        closeWindow();
    }
</script>
<jsp:include page="/core/footerpopup.jsp"/>