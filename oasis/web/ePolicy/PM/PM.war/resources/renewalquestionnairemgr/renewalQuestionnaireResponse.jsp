<%--
  Description: Renewal Questionnaire Response.

  Author: yhyang
  Date: July 08, 2008


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  05/03/2011       ryzhao      117394 - Display Response section only when sysparm PM_WEB_URL is not null.
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ include file="/core/header.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<script type="text/javascript" src="js/renewalQuestionnaireResponse.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<form name="renewalQuestionResponseList" action="renewalQuestionnaireResponse.do" method=post>
    <input type="hidden" name="appStatus" value=""/>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.renewalQuestionnaireResponse.search.header" var="renewQuestSchHeader"
                         scope="request"/>
            <% String renewQuestSchHeader = (String) request.getAttribute("renewQuestSchHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%= renewQuestSchHeader%>"/>
                <jsp:param name="divId" value="renewQuestSch"/>
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="isLayerVisibleByDefault" value="true"/>
                <jsp:param name="excludePageFields" value="true"/>
                <jsp:param name="actionItemGroupId" value="PM_QUESTION_RESP_SCH_AIG"/>
                <jsp:param name="includeLayersWithPrefix" value="PM_RENEWAL_QUESTION_RESPONSE_SEARCH"/>
            </jsp:include>
        </td>
    </tr>
    <c:if test="${empty loadResponse}">
    <tr>
        <td align=center>
            <fmt:message key="pm.renewalQuestionnaireResponse.date.header" var="renewQuestDateHeader"
                         scope="page"/>
            <% String renewQuestDateHeader = (String) pageContext.getAttribute("renewQuestDateHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%= renewQuestDateHeader %>"/>
                <jsp:param name="divId" value="renewQuestionResponseDate"/>
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="isLayerVisibleByDefault" value="true"/>
                <jsp:param name="actionItemGroupId" value="PM_QUESTION_RESP_DATE_AIG"/>
                <jsp:param name="includeLayersWithPrefix" value="PM_RENEWAL_QUESTION_RESPONSE_DATE"/>
            </jsp:include>
        </td>
    </tr>
    </c:if>
    <c:if test="${empty loadResponse and empty showResponseSection}">
    <tr>
        <td align=center>
            <fmt:message key="pm.renewalQuestionnaireResponse.response.header" var="panelTitleIdForQuestionnaire"
                         scope="page"/>
            <%String panelTitleIdForQuestionnaire = (String) pageContext.getAttribute("panelTitleIdForQuestionnaire"); %>
            <oweb:panel panelTitleId="panelTitleIdForQuestionnaire" panelContentId="panelContentIdForQuestionnaire"
                        panelTitle="<%= panelTitleIdForQuestionnaire %>">
                <fmt:message key="pm.renewalQuestionnaireResponse.response.info" var="responseInfo" scope="page"/>
                <tr>
                    <td colspan="6" align=left><b><%= pageContext.getAttribute("responseInfo")%></b>
                    </td>
                </tr>
                <tr>
                    <td>
                        <iframe id="iframeResponse" scrolling="yes" allowtransparency="true" width="100%" height="400"
                                frameborder="0" src="" onload="iframeOnLoad()"></iframe>
                    </td>
                </tr>
                <tr>
                    <td colspan="6" align=center>
                        <oweb:actionGroup actionItemGroupId="PM_QUESTION_RESP_EAPP_AIG"/>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    </c:if>
<jsp:include page="/core/footer.jsp"/>