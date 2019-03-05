<%@ page import="dti.oasis.messagemgr.MessageManager" %>
<%--
  Description:

  Author: EChen
  Date: June 12, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  01/12/20101      syang       105832 - Add two new layers for discipline decline list.
  06/29/2014       JYang       149970 - Update cancel screen pagetitle after page loaded, and overwrite the page title
                                        style for this screen.
  07/12/2017       lzhang      186847 - Reflect grid replacement project changes
  11/15/2018       lzhang      194100   add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2007 Delphi Technology, inc. (dti)
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<c:set var="isForDivPopup" value="true"/>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>
<script type="text/javascript" src="js/captureCancellationDetail.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<style type="text/css">
.pageTitle {
    word-break: break-all;
    width:680px;
    }
</style>
<form action="" name="cancellationForm">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <c:set var="policyHeaderDisplayMode" value="invisible"/>
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
    <tr>
        <td colspan=8 align=center>
            <%@ include file="/policymgr/policyHeader.jsp" %>
        </td>
    </tr>
    <tr>
        <td align=left>
            <fmt:message key="pm.maintainCancellation.cancellationForm.header" var="cancellationFormHeader"
                         scope="request"/>
            <% String cancellationFormHeader = (String) request.getAttribute("cancellationFormHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="divId" value="cancellationDetailDiv"/>
                <jsp:param name="headerText" value="<%=  cancellationFormHeader %>"/>
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="excludeLayerIds" value=",PM_CANCEL_DDL_FORM,PM_CANCEL_RISK_DDL_FORM,"/>
            </jsp:include>
        </td>
    </tr>
    <tr>
        <td>
            <div id="disciplineDeclineListDiv" <%=((useJqxGrid)?"class='dti-hide'":"style='display:none'")%>>
            <fmt:message key="pm.maintainCancellation.ddl.grid.header" var="ddlGridHeader" scope="request"/>
                <% String ddlGridHeader = (String) request.getAttribute("ddlGridHeader"); %>
            <oweb:panel panelTitleId="panelTitleIdForDdl" panelContentId="ddlGridContentId" panelTitle="<%=ddlGridHeader%>">
                    <tr>
                        <td colspan="6" align=center>
                            <c:set var="gridDisplayFormName" value="ddlList" scope="request"/>
                            <c:set var="gridDisplayGridId" value="ddlListGrid" scope="request"/>
                            <c:set var="datasrc" value="#ddlListGrid1" scope="request"/>
                            <c:set var="gridDetailDivId" value="ddlDiv" scope="request"/>
                            <%@ include file="/pmcore/gridDisplay.jsp" %>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <fmt:message key="pm.maintainCancellation.ddl.form.header" var="ddlDetailHeader" scope="request"/>
                            <% String ddlDetailHeader = (String) request.getAttribute("ddlDetailHeader"); %>
                            <jsp:include page="/core/compiledFormFields.jsp">
                                <jsp:param name="headerText" value="<%= ddlDetailHeader%>"/>
                                <jsp:param name="isGridBased" value="true"/>
                                <jsp:param name="divId" value="ddlDiv"/>
                                <jsp:param name="excludePageFields" value="true"/>
                                <jsp:param name="isLayerVisibleByDefault" value="true"/>
                                <jsp:param name="includeLayersWithPrefix" value="PM_CANCEL_DDL_FORM"/>
                            </jsp:include>
                        </td>
                    </tr>
            </oweb:panel>
            </div>
        </td>
    </tr>
    <tr>
        <td>
            <div id="disciplineDeclineListDivForRisk" <%=((useJqxGrid)?"class='dti-hide'":"style='display:none'")%>>
            <fmt:message key="pm.maintainCancellation.ddl.form.header" var="ddlDetailHeader" scope="request"/>
            <% String ddlDetailHeader = (String) request.getAttribute("ddlDetailHeader"); %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="headerText" value="<%= ddlDetailHeader%>"/>
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="divId" value="ddlDivForRisk"/>
                <jsp:param name="excludePageFields" value="true"/>
                <jsp:param name="isLayerVisibleByDefault" value="true"/>
                <jsp:param name="includeLayersWithPrefix" value="PM_CANCEL_RISK_DDL_FORM"/>
            </jsp:include>
            </div>
        </td>
    </tr>
    <tr>
        <td colspan="6" align=center>
            <oweb:actionGroup actionItemGroupId="PM_CANCEL_AIG"/>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>
<script type="text/javascript">
    <%
    if (request.getAttribute("cancelDesc")!=null) {
    %>
    getObject("pageTitleForpageHeader").innerText = getObject("pageTitleForpageHeader").innerText
       + getObjectValue("cancelDesc");
    <%}%>
</script>
