<%@ page language="java" %>
<%@ page import="dti.ci.relationshipmgr.RelationshipFields" %>
<%--
  Description: relationship page

  Author: Hong Yuan
  Date: Oct 10, 2005

  Revision Date    Revised By  Description
  ---------------------------------------------------
  05/15/2007       MLM         Added UI2 Changes
  07/04/2007       James       Added UI2 Changes
  08/29/2007       Jerry       Remove UIStyleEdition
  09/14/2007       James       Change panel title from Filter to
                               Filter Criteria
  03/19/2009       kenney      Added Form Letter support for eCIS
  09/23/2010       kshen       102450. Changed to suport bulk expire relation.
  10/06/2010       wfu         111776: Replaced hardcode string with resource definition
  01/20/2011       Michael Li  Issue:116335
  07/06/2016       Elvin       Issue 177662: use c:out to set entityName in order to avoid display problem
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  10/11/2018       dmeng       Grid replacement
  11/09/2018       Elvin       Issue 195835: grid replacement
  ---------------------------------------------------
  (C) 2004 Delphi Technology, inc. (dti)
--%>

<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib prefix="fmt" uri="/WEB-INF/fmt.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>

<c:set var="globalActionItemGroupId" value="CI_FOLDER_AG"></c:set>
<%@include file="/core/header.jsp"%>
<jsp:include page="/CI_EntitySelect.jsp"/>

<c:set var="tabMenuGroupId" value="${tabGroupId}"></c:set>
<%@ include file="/core/tabheader.jsp" %>

<%@ include file="/cicore/common.jsp" %>

<script type='text/javascript' src="<%=csPath%>/js/csLoadNotes.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type='text/javascript' src="<%=cisPath%>/relationshipmgr/js/relationshipList.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form name="CIRelationshipForm" action="ciRelationship.do" method="POST">
    <tr>
        <td class="tabTitle">
            <b><fmt:message key="ci.entity.search.label.relationships"/> <c:out value="${param.entityName}"/></b>
        </td>
    </tr>

    <tr>
        <td colspan=6>
            <oweb:message/>
        </td>
    </tr>

    <%@ include file="/cicore/commonFormHeader.jsp" %>
    <jsp:include page="/cicore/ciFolderCommon.jsp" />


    <input type="hidden" name="<%=RelationshipFields.ENTITY_CHILD_FK%>" value="<%=(String) request.getAttribute(RelationshipFields.ENTITY_CHILD_FK)%>">
    <input type="hidden" name="<%=RelationshipFields.NAME_COMPUTED%>" value="<%=(String) request.getAttribute(RelationshipFields.NAME_COMPUTED)%>">
    <input type="hidden" name="selectedRecordIds" value="">

    <%@ include file="/cicore/commonFilter.jsp" %>

    <tr>
        <td>
            <oweb:panel panelContentId="panelContentForRelationList"
                        panelTitleId="panelTitleIdForRelationList" panelTitleLayerId="CI_RELATION_GH">
                <tr>
                    <td width="100%" align="left">
                        <tr>
                            <td width="100%">
                                <table>
                                    <tr>
                                        <td align="left">
                                            <oweb:actionGroup actionItemGroupId="CI_RELAT_GRID_AIG" cssColorScheme="gray" layoutDirection="horizontal"/>
                                        </td>

                                        <c:set var="field" value="${fieldsMap.expDate}" scope="request"/>
                                        <jsp:include page="/core/compiledTagFactory.jsp" ></jsp:include>
                                        <c:remove var="field" scope="request"/>
                                    </tr>
                                </table>
                            <td>
                        </tr>
                    </td>
                </tr>

                <tr>
                    <td width="100%">
                        <c:set var="gridDisplayFormName" value="CIRelationshipForm" scope="request"/>
                        <c:set var="gridDisplayGridId" value="testgrid" scope="request"/>
                        <c:set var="datasrc" value="#testgrid1" scope="request"/>
                        <%@ include file="/core/gridDisplay.jsp" %>
                    </td>
                </tr>
                <tr>
                    <td align="center">
                        <oweb:actionGroup actionItemGroupId="CI_RELAT_AIG" layoutDirection="horizontal"/>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>

    <%@ include file="/core/tabfooter.jsp" %>
    <jsp:include page="/core/footer.jsp"/>