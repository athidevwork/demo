<%--
  Description: Renewal Questionnaire Response. 
  The policy No Criteria , risk Id and policyRenewFormId should be hidden in the popup page.

  Author: yhyang
  Date: July 08, 2008


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  09/13/2010       wfu         111776: Replaced hardcode string with resource definition
  05/03/2011       ryzhao      117394 - Display Response section only when sysparm PM_WEB_URL is not null.
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<script type="text/javascript" src="js/renewalQuestionnaireResponse.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<form name="renewalQuestionResponseList" action="renewalQuestionnaireResponse.do" method=post>
    <input type="hidden" name="appStatus" value=""/>
    <input type="hidden" name="policyNoCriteria" value="<%=request.getAttribute("policyNoCriteria")%>"/>
    <input type="hidden" name="policyRenewFormId" value="<%=request.getAttribute("policyRenewFormId")%>"/>
    <input type="hidden" name="riskId"
           value="<%=request.getAttribute("riskId")==null?"":request.getAttribute("riskId")%>"/>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
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
                    <td colspan="6" align=left><b><%= pageContext.getAttribute("responseInfo")%></b></td>
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
    <tr>
        <td colspan="6" align=center>
            <oweb:actionGroup actionItemGroupId="PM_QUESTION_RESPS_CLS_AIG"/>
        </td>
    </tr>
    <script type="text/javascript">
        var policyNo = getObject("policyNoCriteria").value;
        var policyURL = getAppPath()+"/policymgr/maintainPolicy.do?policyNo=" + policyNo;
        // System doesn't convert the innerText content to html to display.
        // Therefore,we should set the link value to the innerHTML, system will convert the innerHMTL to html to display.
        getObject("pageTitleForpageHeader").innerHTML = getObject("pageTitleForpageHeader").innerText +
                 "- " + "<fmt:message key='pm.renewalQuestionResponsePopup.header.policyNo'/>" + "<a href=" + policyURL + " target=\"_blank\">" +
                 "<span style=\'text-decoration:underline;font-weight:bold;\'>" + policyNo + "</span></a>";
    </script>
<jsp:include page="/core/footerpopup.jsp"/>