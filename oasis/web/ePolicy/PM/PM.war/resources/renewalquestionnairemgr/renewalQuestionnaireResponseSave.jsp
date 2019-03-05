<%--
  Description: Save As Option For Renewal Questionnaire Response.

  Author: yhyang
  Date: July 08, 2008


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
<script type="text/javascript" src="js/renewalQuestionnaireResponseSave.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<form name="renewalQuestionnaireResponseSave" action="javascript:void();" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
     <tr>
        <td align=center>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value=""/>
                <jsp:param name="divId" value="renewQuestionSaveOption"/>
                <jsp:param name="isGridBased" value="false"/>
                 <jsp:param name="isLayerVisibleByDefault" value="true"/>
                <jsp:param name="includeLayersWithPrefix" value="PM_RENEWAL_RESPONSE_SAVEAS"/>
            </jsp:include>
        </td>
    </tr>
    <tr>
        <td colspan="6" align=center>
            <oweb:actionGroup actionItemGroupId="PM_QUESTION_RESP_SAS_AIG"/>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>