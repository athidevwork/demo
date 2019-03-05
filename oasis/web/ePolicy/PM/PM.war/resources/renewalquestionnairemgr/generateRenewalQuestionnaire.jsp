<%--
  Description: Generate Renewal Questionnaire. 

  Author: yhyang
  Date: May 14, 2008


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  08/09/2010       bhong      110551 - Fixed a minor error
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
<script type="text/javascript" src="js/generateRenewalQuestionnaire.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<form name="renewalQuestionnaireList" action="generateRenewalQuestionnaire.do" method=post>
    <input type="hidden" name="searchQuestionnaire" value="Y"/>
    <input type="hidden" name="generateQuestionnaire" value="<%=request.getAttribute("generateQuestionnaire")%>"/>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.generateRenewalQuestionnaire.search.header" var="genRenewQuestSchHeader"
                         scope="request"/>
            <% String genRenewQuestSchHeader = (String) request.getAttribute("genRenewQuestSchHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%= genRenewQuestSchHeader%>"/>
                <jsp:param name="divId" value="genRenewQuestSch"/>
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="isLayerVisibleByDefault" value="true"/>
                <jsp:param name="actionItemGroupId" value="PM_GEN_RENEW_QUES_SCH_AIG"/>
                <jsp:param name="includeLayersWithPrefix" value="PM_QUESTIONNAIRE_SEARCH"/>
            </jsp:include>
        </td>
    </tr>
    <c:if test="${secondMailingDateAvailable eq 'Y' or thirdMailingDateAvailable eq 'Y' or deadlineDateAvailable eq 'Y'}">
    <tr>
        <td align=center>
            <fmt:message key="pm.generateRenewalQuestionnaire.mailingdate.header" var="genRenewQuestMailHeader"
                         scope="request"/>
            <% String genRenewQuestMailHeader = (String) request.getAttribute("genRenewQuestMailHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%= genRenewQuestMailHeader%>"/>
                <jsp:param name="divId" value="genRenewQuestMailDate"/>
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="isLayerVisibleByDefault" value="true"/>
                <jsp:param name="includeLayersWithPrefix" value="PM_QUESTIONNAIRE_MAILING_DATE"/>
            </jsp:include>
        </td>
    </tr>
   </c:if>
    <tr>
        <td align=center>
            <fmt:message key="pm.generateRenewalQuestionnaire.generate.header" var="genRenewQuestGenHeader"
                         scope="page"/>
            <% String genRenewQuestGenHeader = (String) pageContext.getAttribute("genRenewQuestGenHeader"); %>
            <oweb:panel panelTitleId="panelTitleIdForGenerateRenewQuestionniare"
                        panelContentId="panelContentIdForGenerateRenewQuestionniare"
                        panelTitle="<%= genRenewQuestGenHeader %>">
                <tr>
                    <td colspan="6" align=center><br/>
                        <c:set var="gridDisplayFormName" value="renewalQuestionnaireList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="renewalQuestionnaireListGrid" scope="request"/>
                        <c:set var="datasrc" value="#renewalQuestionnaireListGrid1" scope="request"/>
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
            <oweb:actionGroup actionItemGroupId="PM_GEN_RENEW_QUES_GEN_AIG"/>
        </td>
    </tr>
<jsp:include page="/core/footer.jsp"/>