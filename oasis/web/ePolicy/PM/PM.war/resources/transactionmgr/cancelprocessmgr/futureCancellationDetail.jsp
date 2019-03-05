<%@ page import="dti.oasis.messagemgr.MessageManager" %>
<%--
  Description:

  Author: eyin
  Date: July 05, 2016


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  07/05/2016       eyin        176476 - Add Future Cancellation Details Popup.
  11/15/2018       lzhang      194100   add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2007 Delphi Technology, inc. (dti)
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<c:set var="isForDivPopup" value="true"/>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>
<script type="text/javascript" src="js/futureCancellationDetail.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<style type="text/css">
.pageTitle {
    word-break: break-all;
    width:680px;
    }
</style>
<form action="" name="futureCancellationDetailsForm">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <c:set var="policyHeaderDisplayMode" value="invisible"/>
        <input type="hidden" name="status" value='<%=request.getAttribute("status")%>'>
    <tr>
        <td colspan=10>
            <oweb:message/>
        </td>
    </tr>
    <tr>
        <td colspan=10 align=center>
            <%@ include file="/policymgr/policyHeader.jsp" %>
        </td>
    </tr>
    <tr>
        <td align=center>
            <!-- Display Grid -->
            <fmt:message key="pm.maintainCancellation.futureCancellationList.header" var="futureCancelGridPanelTitle" scope="page"/>
                <% String futureCancelGridPanelTitle = (String) pageContext.getAttribute("futureCancelGridPanelTitle"); %>
            <oweb:panel panelTitleId="panelTitleIdForFutureCancelGrid" panelContentId="panelContentIdForFutureCancelGrid"
                        panelTitle="<%= futureCancelGridPanelTitle %>">
                <tr>
                    <td>
                        <c:set var="gridDisplayFormName" value="futureCancellationDetails" scope="request"/>
                        <c:set var="gridDisplayGridId" value="futureCancellationDetailsGrid" scope="request"/>
                        <c:set var="datasrc" value="#futureCancellationDetailsGrid1" scope="request"/>
                        <c:set var="gridDetailDivId" value="futureCancellation" scope="request"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td colspan="10" align=center>
            <oweb:actionGroup actionItemGroupId="PM_FUTURE_CANCEL_DTL_AIG"/>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>
