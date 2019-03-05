<%--
  Description: Mailing Event Questionnaire.

  Author: yhyang
  Date: June 14, 2008


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  09/09/2010       syang       Issue 106500 - Added attribute CheckBoxSpan to display checkbox in the appropriate position.
  10/07/2010       syang       Issue 106500 - Set excludePageFields to true for layer PM_MAILING_EVENT_QUESTION_FIELDS
                                              since this layer maybe configured to invisible(The excludePageFields must
                                              be true if we want to hide a layer in webwb).
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<c:set var="isForDivPopup" value="true"></c:set>
<c:set var="skipHeaderFooterContent" value="true"></c:set>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<script type="text/javascript" src="js/renewalMailingEventQuestionnaire.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<form name="renewalMailingEventQuestionList" action="renewalMailingEventQuestionnaire.do" method=post>
    <%request.setAttribute("CheckBoxSpan","2");%>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
    <tr>
        <td colspan="6">
            <oweb:actionGroup actionItemGroupId="PM_RENEW_QUESMAIL_ADD_AIG" layoutDirection="horizontal"
                              cssColorScheme="gray"/>
        </td>
    </tr>
    <tr>
        <td colspan="6" align=center><br/>
            <c:set var="gridDisplayFormName" value="renewalMailingEventQuestionList" scope="request"/>
            <c:set var="gridDisplayGridId" value="renewalMailingEventQuestionListGrid" scope="request"/>
            <c:set var="datasrc" value="#renewalMailingEventQuestionListGrid1" scope="request"/>
            <c:set var="gridDetailDivId" value="renewQuestionFields" scope="request"/>
            <c:set var="gridSortable" value="false" scope="request"/>
            <c:set var="cacheResultSet" value="true"/>
            <%@ include file="/pmcore/gridDisplay.jsp" %>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.renewalQuestionnaireMailingEvent.questionnaire.detail.header"
                         var="renewQuestionDetailHeader"
                         scope="request"/>
            <% String renewQuestionDetailHeader = (String) request.getAttribute("renewQuestionDetailHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%= renewQuestionDetailHeader%>"/>
                <jsp:param name="divId" value="renewQuestionFields"/>
                <jsp:param name="isGridBased" value="true"/>
                <jsp:param name="excludePageFields" value="true"/>
                <jsp:param name="isLayerVisibleByDefault" value="true"/>
                <jsp:param name="includeLayersWithPrefix" value="PM_MAILING_EVENT_QUESTION_FIELDS"/>
            </jsp:include>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>