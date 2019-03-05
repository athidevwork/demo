<%@ page language="java" %>
<%--
  Description: Jsp file used to display Agent Ouput Option List grid in Policy Agent Output Option Page

  Author: yjmiao
  Date: Mar 9, 2011


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  10/20/2011       clm         issue 122671, fix Js error when openning the page
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:set var="isForDivPopup" value="false"></c:set>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/cicore/common.jsp" %>

<c:set var="skipHeaderFooterContent" value="true"></c:set>

<script type="text/javascript"> if (getObject("pageHeader")) {
    getObject("pageHeader").style.display = "none";
} </script>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<form name="agentOutputOptionForm" method=post>
    <c:if test="${dataBean != null}">
    <tr>
        <td align=center>

            <oweb:panel panelTitleId="panelTitleIdForPolAgentOutputOptionGrid"
                        panelContentId="panelContentIdForPolAgentOutputOptionGrid"
                        panelTitleLayerId="CI_AGENT_OUTPUT_OPTION_GH"
                        isTogglableTitle="false">
    <tr>
        <td colspan="7" align="center">
            <c:set var="gridDisplayFormName" value="agentOutputOptionForm" scope="request"/>
            <c:set var="gridDisplayGridId" value="agentOutputOptionListGrid" scope="request"/>
            <c:set var="datasrc" value="#agentOutputOptionListGrid1" scope="request"/>
            <c:set var="cacheResultSet" value="false"/>
            <%@ include file="/core/gridDisplay.jsp" %>
        </td>
    </tr>
    </oweb:panel>
    </td>
    </tr>
    </c:if>

<jsp:include page="/core/footerpopup.jsp"/>