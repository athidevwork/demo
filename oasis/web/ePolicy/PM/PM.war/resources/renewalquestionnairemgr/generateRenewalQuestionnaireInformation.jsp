<%--
  Description: Generate Renewal Questionnaire. 

  Author: yhyang
  Date: May 14, 2008


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
<script type="text/javascript" src="js/generateRenewalQuestionnaireInformation.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<form name="renewalQuestionInfoList" action="generateRenewalQuestionnaire.do" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.generateRenewalQuestionnaire.information.header" var="genRenewQuestInfoHeader"
                         scope="page"/>
            <% String genRenewQuestInfoHeader = (String) pageContext.getAttribute("genRenewQuestInfoHeader");%>
            <oweb:panel panelTitleId="panelTitleIdForGenerateRenewQuestionniareInfo"
                        panelContentId="panelContentIdForGenerateRenewQuestionniareInfo"
                        panelTitle="<%= genRenewQuestInfoHeader %>">
                <tr>
                    <td colspan="6" align=center><br/>
                        <c:set var="gridDisplayFormName" value="renewalQuestionInfoList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="renewalQuestionInfoListGrid" scope="request"/>
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
            <oweb:actionGroup actionItemGroupId="PM_GEN_RENEW_QUES_INF_AIG"/>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>