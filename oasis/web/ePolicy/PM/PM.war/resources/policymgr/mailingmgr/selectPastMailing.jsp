<%--
  Description:

  Author: rlli
  Date: Dec 13, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
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
<script type="text/javascript" src="js/selectPastMailing.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form action="" name="selectPastmailingnForm">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <input type=hidden name=policyMailingId value="<c:out value="${policyMailingId}"/>">
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
    <!-- Display grid -->
    <tr>
        <td>
            <oweb:panel panelTitleId="panelTitleIdForSharedDetail" panelContentId="panelContentIdForSharedDetail"
                        panelTitle="">
        </td>
    </tr>
    <tr>
        <td colspan="6" align=center><br/>
            <c:set var="gridDisplayFormName" value="selectPastMailingForm" scope="request"/>
            <c:set var="gridDisplayGridId" value="selectPastMailingGrid" scope="request"/>
            <c:set var="cacheResultSet" value="false"/>
            <c:set var="selectable" value="true"/>
            <%@ include file="/pmcore/gridDisplay.jsp" %>
        </td>
    </tr>
    </oweb:panel>
    <%-- Display buttons --%>
    <tr>
        <td colspan="7" align="center">
            <oweb:actionGroup actionItemGroupId="PM_SEL_PAST_MAILING_AIG"/>
        </td>
    </tr>

    <jsp:include page="/core/footerpopup.jsp"/>
