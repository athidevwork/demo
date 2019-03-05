<%@ page import="dti.ci.auditmgr.AuditTrailFields" %>
<%--
  Description: Audit Trail Page
  Author: FWCH
  Date: Aug 08, 2007

  Revision Date    Revised By    Description
  ---------------------------------------------------
  09/06/2007       Kenney       remove UIStyleEdition;
                                change to panel tag;
                                change to compiledFormField page
  09/14/2007       James        Change panel title from Filter to
                                Filter Criteria
  09/20/2007       FWCH         Changed divId to audit_details
  09/28/2010       wfu          111776: Replaced hardcode string with resource definition
  04/19/2018       ylu          Issue 109179:  refactor old style code
  06/28/2018       dpang        194157: Add buildNumber parameter to static file references to improve performance
  9/22/2018        dpang        195835: grid replacement.
                                        1) Change dataBeanName so that detail can display when using jqxGrid.
  ---------------------------------------------------
  (C) 2006 Delphi Technology, inc. (dti)
--%>
<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<%@ include file="/core/headerpopup.jsp" %>
<jsp:include page="/cicore/common.jsp"/>

<script type='text/javascript' src="<%=cisPath%>/auditmgr/js/auditTrailPopup.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<form name="CIAuditTrailPopForm" action="ciAuditTrailPopup.do" method="POST">
    <%@ include file="/cicore/commonFormHeader.jsp" %>
    <input type="hidden" name="<%=AuditTrailFields.HISTORY_TYPE%>"
           value="<%=request.getAttribute(AuditTrailFields.HISTORY_TYPE)%>"/>
    <input type="hidden" name="<%=AuditTrailFields.SOURCE_NO%>"
           value="<%=request.getAttribute(AuditTrailFields.SOURCE_NO)%>"/>
    <input type="hidden" name="<%=AuditTrailFields.OPERATION_TABLE%>"
           value="<%=request.getAttribute(AuditTrailFields.OPERATION_TABLE)%>"/>
    <input type="hidden" name="<%=AuditTrailFields.OPERATION_ID%>"
           value="<%=request.getAttribute(AuditTrailFields.OPERATION_ID)%>"/>
    <!--Filter Layer-->
    <tr>
        <td colspan="6">
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="headerTextLayerId" value="AUDIT_TRAIL_FILTER"/>
                <jsp:param name="divId" value="panelContentForSearchCriteria"/>
                <jsp:param name="isLayerVisibleByDefault" value="true"/>
                <jsp:param name="includeLayerIds" value="AUDIT_TRAIL_FILTER"/>
                <jsp:param name="actionItemGroupId" value="CI_AUDIT_POP_FLT_AIG"/>
            </jsp:include>
        </td>
    </tr>
    <!--Filter layer end-->
    <!--Transaction list grid begin -->
    <tr>
        <td>
            <oweb:panel panelContentId="panelContentForAuditTrailList" panelTitleId="panelTitleIdForAuditTrailList"
                        panelTitleLayerId = "AUDIT_TRAIL_GRID_FIELD">
                <tr>
                    <td>
                        <c:set var="gridDisplayFormName" value="CIAuditTrailPopForm" scope="request"/>
                        <c:set var="gridDisplayGridId" value="testgrid" scope="request"/>
                        <c:set var="gridDetailDivId" value="audit_details" scope="request"/>
                        <c:set var="datasrc" value="#testgrid1" scope="request"/>
                        <%@ include file="/core/gridDisplay.jsp" %>
                    </td>
                </tr>
                <!--Transaction list end-->
                <!--Transaction details -->
                <tr>
                    <td>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="dataBeanName" value="dataBean"/>
                            <jsp:param name="gridID" value="testgrid"/>
                            <jsp:param name="includeLayerIds" value="AUDIT_TRAIL_DETAIL"/>
                            <jsp:param name="headerTextLayerId" value="AUDIT_TRAIL_DETAIL"/>
                            <jsp:param name="isLayerVisibleByDefault" value="true"/>
                            <jsp:param name="divId" value="audit_details"/>
                            <jsp:param name="excludePageFields" value="true"/>
                        </jsp:include>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
    <tr>
        <td align="center">
            <oweb:actionGroup actionItemGroupId="CI_AUDIT_POP_FRM_AIG"
                              cssColorScheme="blue" layoutDirection="horizontal">
            </oweb:actionGroup>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>
