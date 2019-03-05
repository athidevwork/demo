<%--
  Description:Application List page.

  Author: gchitta
  Date: June 17, 2009


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%--<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>--%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<script type="text/javascript" src="js/applicationList.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<form name="applicationList" action="maintainApplication.do" method=post>
    <input type="hidden" name="policyNo" value="<c:out value="${policyHeader.policyNo}"/>">

    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
    <tr>
        <td align=center>
            <fmt:message key="pm.applicationList.policyTerm.header" var="policyTermHeader"
                         scope="page"/>
            <% String policyTermHeader = (String) pageContext.getAttribute("policyTermHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%= policyTermHeader %>"/>
                <jsp:param name="divId" value="policyTermHeader"/>
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="isLayerVisibleByDefault" value="true"/>
                <jsp:param name="actionItemGroupId" value="PM_APP_LIST_SEARCH_AIG"/>
                <jsp:param name="includeLayersWithPrefix" value="PM_APP_TERM_LIST"/>
            </jsp:include>
        </td>
    </tr>

    <c:if test="${dataBean != null && dataBean.rowCount > 0}">
    <tr>
        <td align=center>
            <fmt:message key="pm.applicationList.list.header" var="panelTitleForapplicationList"
                         scope="page"/>
            <%
                String panelTitleForapplicationList = (String) pageContext.getAttribute("panelTitleForapplicationList");
            %>
            <oweb:panel panelTitleId="panelTitleForapplicationList" panelContentId="panelTitleForapplicationList"
                        panelTitle="<%= panelTitleForapplicationList %>">
                <tr>
                    <td colspan="6" align=center>
                        <c:set var="gridDisplayFormName" value="applicationList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="applicationListGrid" scope="request"/>
                        <c:set var="datasrc" value="#applicationListGrid1" scope="request"/>
                        <c:set var="cacheResultSet" value="false"/>
                       <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>

                <tr>
                    <td colspan="6">
                        <oweb:actionGroup actionItemGroupId="PM_APPLICATION_VIEW_AIG" layoutDirection="horizontal"
                                      cssColorScheme="gray"/>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>

    <tr>
        <td align=center>
            <fmt:message key="pm.applicationList.display.header" var="panelTitleIdForAppDisplay"
                         scope="page"/>
            <%String panelTitleIdForAppDisplay = (String) pageContext.getAttribute("panelTitleIdForAppDisplay"); %>
            <oweb:panel panelTitleId="panelTitleIdForAppDisplay" panelContentId="panelTitleIdForAppDisplay"
                        panelTitle="<%= panelTitleIdForAppDisplay %>">
                <fmt:message key="pm.renewalQuestionnaireResponse.response.info" var="responseInfo" scope="page"/>
                <tr>
                    <td colspan="6" align=left><b><%= pageContext.getAttribute("responseInfo")%></b>
                    </td>
                </tr>
                <tr>
                    <td>
                        <iframe id="iframeResponse" scrolling="yes" allowtransparency="true" width="100%" height="250"
                                frameborder="0" src="" onload="iframeOnLoad()"></iframe>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
   
    </c:if>

    <tr>
        <td align=center colspan="6">
            <oweb:actionGroup actionItemGroupId="PM_APPLICATION_AIG" layoutDirection="horizontal"/>
        </td>
    </tr>
<%
// Initialize Sys Parms for JavaScript to use
String sysParmCascadeDel = SysParmProvider.getInstance().getSysParm("PM_WEB_URL", "XXX");
%>
<script type="text/javascript">
    setSysParmValue("PM_WEBAPP_URL", '<%=sysParmCascadeDel%>');
</script>
<jsp:include page="/core/footerpopup.jsp"/>