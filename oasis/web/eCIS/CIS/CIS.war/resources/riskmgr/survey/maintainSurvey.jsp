<%--
  Description:

  Author: Guang Long
  Date: Aug 10, 2009

  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  08/07/2009       gjlong      initial impl for enhancement 94091
  07/01/2013       hxk         Issue 141840
                               Add common.jsp for security.
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  10/19/2018       dzou       grid replacement
  -----------------------------------------------------------------------------
  (C) 2009 Delphi Technology, inc. (dti)
--%>
<%@ page language="java" import="dti.ci.helpers.ICIClaimsConstants" %>
<%@ page import="dti.oasis.tags.WebLayer" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request" />
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<c:set var="globalActionItemGroupId" value="CI_FOLDER_AG"></c:set>

<%@ include file="/core/header.jsp" %>
 <jsp:include page="/CI_EntitySelect.jsp"/>
<jsp:include page="/cicore/common.jsp"/>

<c:set var="tabMenuGroupId" value="${tabGroupId}"></c:set>
<%@ include file="/core/tabheader.jsp" %>

<script type='text/javascript' src="<%=appPath%>/riskmgr/survey/js/maintainSurvey.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<FORM action="maintainSurvey.do" method="POST" name="surveyDetail">
 <%@ include file="/cicore/commonFormHeader.jsp" %>
     <jsp:include page="/cicore/ciFolderCommon.jsp" />

<tr>
    <td id="message" colspan=8>
        <oweb:message/>
    </td>
</tr>
<tr>

    <td align=center>
             <oweb:panel panelTitleId="panelTitleIdForSurvey"
                        panelContentId="panelContentIdForSurvey"
                         panelTitleLayerId="CI_RM_SURVEY_GRID">

            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="surveyDetail" scope="request"/>
                    <c:set var="gridDisplayGridId" value="surveyListGrid" scope="request"/>
                    <c:set var="gridDetailDivId" value="surveyDetailDiv" scope="request"/>
                    <c:set var="datasrc" value="#surveyListGrid1" scope="request"/>
                    <c:set var="cacheResultSet" value="false"/>
                    <%@ include file="/core/gridDisplay.jsp" %>
                </td>
            </tr>
            <tr>
                <td align=center>
                    <c:set var="datasrc" value="#surveyListGrid1" scope="request"/>
                    <jsp:include page="/core/compiledFormFields.jsp">
                        <jsp:param name="headerTextLayerId" value="CI_RM_SURVEY_DETAIL"/>
                        <jsp:param name="isGridBased" value="true"/>
                        <jsp:param name="gridID" value="surveyListGrid"/>
                        <jsp:param name="divId" value="surveyDetailDiv"/>
                        <jsp:param name="excludePageFields" value="true"/>
                        <jsp:param name="isLayerVisibleByDefault" value="true"/>
                        <jsp:param name="includeLayerIds" value="CI_RM_SURVEY_DETAIL"/>
                    </jsp:include>
                </td>
            </tr>
        </oweb:panel>
    </td>
</tr>

<tr>
    <td align=center>
        <oweb:actionGroup actionItemGroupId="CI_RM_SURVEY_AIG" layoutDirection="horizontal"/>
    </td>
</tr>
<%@ include file="/core/tabfooter.jsp" %>
<jsp:include page="/core/footer.jsp"/>
