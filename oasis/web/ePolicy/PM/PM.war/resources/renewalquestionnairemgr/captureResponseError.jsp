<%--
  Description: Capture Response Error.

  Author: yhyang
  Date: June 23, 2008


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
<c:set var="isForDivPopup" value="true"></c:set>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<script type="text/javascript" src="js/captureResponseError.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<form name="renewalQuestionErrorList" action="captureResponseError.do" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.renewalQuestionnaireMailingEvent.capture.header" var="renewQuestCaptureHeader"
                         scope="page"/>
            <% String renewQuestCaptureHeader = (String) pageContext.getAttribute("renewQuestCaptureHeader");%>
            <oweb:panel panelTitleId="panelTitleIdForCaptureResponseError"
                        panelContentId="panelContentIdForCaptureResponseError"
                        panelTitle="<%= renewQuestCaptureHeader %>">
                <tr>
                    <td colspan="6" align=center><br/>
                        <c:set var="gridDisplayFormName" value="renewalQuestionErrorList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="renewalQuestionErrorListGrid" scope="request"/>
                        <c:set var="gridSortable" value="false" scope="request"/>
                        <c:set var="cacheResultSet" value="true"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td colspan="6" align=center>
            <oweb:actionGroup actionItemGroupId="PM_RENEW_CAPTURE_RES_AIG"/>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>