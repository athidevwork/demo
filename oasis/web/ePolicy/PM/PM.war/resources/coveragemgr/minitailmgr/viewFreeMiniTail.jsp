<%--
  Description:

  Author: Bhong
  Date: July 29, 2009


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  09/10/2010       wfu         111776: Replaced hardcode string with resource definition
  -----------------------------------------------------------------------------
  (C) 2009 Delphi Technology, inc. (dti)
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<c:set var="isForDivPopup" value="true"></c:set>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>
<%@ include file="/core/invokeWorkflow.jsp"%>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<tr>
    <td colspan=8>
        <table cellpadding=0 cellspacing=0 width=100%>
            <tr>
                <td>
                    <oweb:message/>
                </td>
            </tr>
        </table>
    </td>
</tr>

<form action="<%=appPath%>/coveragemgr/minitailmgr/viewFreeMiniTail.do" name="viewFreeMiniTailForm">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    
    <c:set var="policyHeaderDisplayMode" value="invisible"/>
    <tr>
        <td colspan=8 align=center>
            <%@ include file="/policymgr/policyHeader.jsp" %>
        </td>
    </tr>

    <tr>
        <td align=center>
            <fmt:message key="pm.miniTail.form.header" var="miniTail" scope="request"/>
            <%  String miniTail = (String) request.getAttribute("miniTail"); %>
            <oweb:panel panelTitleId="panelTitleIdForFreeMiniTail" panelContentId="panelContentIdForFreeMiniTail"
                        panelTitle="<%= miniTail %>">
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="viewFreeMiniTailForm" scope="request"/>
                    <c:set var="gridDisplayGridId" value="freeMiniTailGrid" scope="request"/>
                    <c:set var="cacheResultSet" value="false"/>
                    <c:set var="selectable" value="true"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
            </oweb:panel>
            <tr>
                <td align=center>
                    <oweb:actionGroup actionItemGroupId="PM_FREE_MINI_TAIL_AIG"/>
                </td>
            </tr>

<jsp:include page="/core/footerpopup.jsp"/>