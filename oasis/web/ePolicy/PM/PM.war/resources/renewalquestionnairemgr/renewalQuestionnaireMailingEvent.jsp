<%--
  Description: Renewal Questionnaire Mailing Event. 

  Author: yhyang
  Date: June 14, 2008


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
<%@ include file="/core/header.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<script type="text/javascript" src="js/renewalQuestionnaireMailingEvent.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<form name="renewalQuestionMailingEventList" action="renewalQuestionnaireMailingEvent.do" method=post>
<input type="hidden" name="searchMailingEvent" value="Y"/>
<input type="hidden" name="filterMailingEvent" value=""/>
<input type="hidden" name="filterSuccess" value="<%=request.getAttribute("filterSuccess")%>"/>
<%@ include file="/pmcore/commonFormHeader.jsp" %>
<tr>
    <td colspan=8>
        <oweb:message/>
    </td>
</tr>
<tr>
    <td align=center>
        <fmt:message key="pm.renewalQuestionnaireMailingEvent.search.header" var="renewQuestSchHeader"
                     scope="request"/>
        <% String renewQuestSchHeader = (String) request.getAttribute("renewQuestSchHeader"); %>
        <jsp:include page="/core/compiledFormFields.jsp">
            <jsp:param name="headerText" value="<%= renewQuestSchHeader%>"/>
            <jsp:param name="divId" value="renewQuestSch"/>
            <jsp:param name="isGridBased" value="false"/>
            <jsp:param name="isLayerVisibleByDefault" value="true"/>
            <jsp:param name="actionItemGroupId" value="PM_RENEW_QUESMAIL_SCH_AIG"/>
            <jsp:param name="includeLayersWithPrefix" value="PM_QUESTION_MAILING_SEARCH"/>
        </jsp:include>
    </td>
</tr>
<tr>
    <td align=center>
        <fmt:message key="pm.renewalQuestionnaireMailingEvent.mailingevent.header" var="renewMailingEventHeader"
                     scope="page"/>
        <% String renewMailingEventHeader = (String) pageContext.getAttribute("renewMailingEventHeader"); %>
        <oweb:panel panelTitleId="panelTitleIdForRenewQuestionniareMailingEvent"
                    panelContentId="panelContentIdForRenewQuestionniareMailingEvent"
                    panelTitle="<%= renewMailingEventHeader %>">
            <tr>
                <td align=center>
                    <fmt:message key="pm.renewalQuestionnaireMailingEvent.filter.header" var="renewQuestionFilterHeader"
                                 scope="request"/>
                    <% String renewQuestionFilterHeader = (String) request.getAttribute("renewQuestionFilterHeader"); %>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="headerText" value="<%= renewQuestionFilterHeader%>"/>
                        <jsp:param name="divId" value="renewQuestionMailEvent"/>
                        <jsp:param name="isGridBased" value="false"/>
                        <jsp:param name="isLayerVisibleByDefault" value="true"/>
                        <jsp:param name="actionItemGroupId" value="PM_RENEW_QUESMAIL_FIT_AIG"/>
                        <jsp:param name="includeLayersWithPrefix" value="PM_QUESTION_MAILING_FILTER"/>
                    </jsp:include>
                </td>
            </tr>
            <tr>
                <td colspan="6">
                    <oweb:actionGroup actionItemGroupId="PM_RENEW_QUESMAIL_PRT_AIG" layoutDirection="horizontal"
                                      cssColorScheme="gray"/>
                </td>
            </tr>
            <tr>
                <td colspan="6" align=center><br/>
                    <c:set var="gridDisplayFormName" value="renewalQuestionMailingEventList" scope="request"/>
                    <c:set var="gridDisplayGridId" value="renewalQuestionMailingEventListGrid" scope="request"/>
                    <c:set var="gridSortable" value="false" scope="request"/>
                    <c:set var="cacheResultSet" value="true"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
        </oweb:panel>
    </td>
</tr>
<tr>
    <td align=center>
        <fmt:message key="pm.renewalQuestionnaireMailingEvent.questionnaire.header" var="panelTitleIdForQuestionnaire"
                     scope="page"/>
        <%String panelTitleIdForQuestionnaire = (String) pageContext.getAttribute("panelTitleIdForQuestionnaire"); %>
        <oweb:panel panelTitleId="panelTitleIdForQuestionnaireGrid" panelContentId="panelContentIdForQuestionnaireGrid"
                    panelTitle="<%= panelTitleIdForQuestionnaire %>">
            <tr>
                <td align=center>
                    <fmt:message key="pm.renewalQuestionnaireMailingEvent.find.header" var="renewQuestionFindHeader"
                                 scope="request"/>
                    <% String renewQuestionFindHeader = (String) request.getAttribute("renewQuestionFindHeader"); %>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="headerText" value="<%= renewQuestionFindHeader%>"/>
                        <jsp:param name="divId" value="renewQuestionFind"/>
                        <jsp:param name="isGridBased" value="false"/>
                        <jsp:param name="isLayerVisibleByDefault" value="true"/>
                         <jsp:param name="actionItemGroupId" value="PM_RENEW_QUESMAIL_FID_AIG"/>
                        <jsp:param name="includeLayersWithPrefix" value="PM_QUESTION_MAILING_FIND"/>
                    </jsp:include>
                </td>
            </tr>
            <tr>
                <td>
                    <iframe id="iframeMailingEvent" scrolling="no" allowtransparency="true" width="100%" height="340"
                            frameborder="0" src=""></iframe>
                </td>
            </tr>
        </oweb:panel>
    </td>
</tr>
<jsp:include page="/core/footerpopup.jsp"/>