<%@ page language="java" %>
<%@ page import="dti.ci.relationshipmgr.RelationshipFields" %>

<%--
  Description: relationship modify page

  Author: Hong Yuan
  Date: Oct 17, 2005

  Revision Date    Revised By  Description
  ---------------------------------------------------
  02/12/2007       kshen       Issue 63166
                               close this page and refresh parent page when
                               saving data successful
  07/04/2007       James       Added UI2 Changes
  09/03/2007       Jerry       Remove UIStyleEdition
  09/17/2007       James       restored panel title and set isTogglableTitle
                               false
  03/25/2009       jdingle     Issue 84653 - Add CS_UNIQUE_RLTNCD
  09/15/2009       Jacky       Issue 97105
                               'Data' field in 'RelationshipModify' page should be editable
  02/15/2011       kshen       Changed for 112658.
  12/22/2011       Michael     Changed for 127479 refactor this page.  
  04/14/2016       ylu         Issue 170594: load getUSA/CanadaContryCode() function correctly
  05/22/2018       dpang       Issue 109089: Entity address refactor
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  10/16/2018       dmeng       Issue 195835: grid replacement
  11/09/2018       Elvin       Issue 195835: grid replacement
  ---------------------------------------------------
  (C) 2004 Delphi Technology, inc. (dti)
--%>

<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<%@include file="/core/headerpopup.jsp" %>
<jsp:include page="/cicore/common.jsp"/>

<jsp:include page="/addressmgr/addressCommon.jsp"/>

<script type='text/javascript' src="<%=cisPath%>/relationshipmgr/js/maintainRelationshipModify.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form name="CIRelationshipModifyForm" action="ciRelationshipModify.do" method="POST">
    <tr>
        <td colspan=6>
            <oweb:message/>
        </td>
    </tr>

    <%@ include file="/cicore/commonFormHeader.jsp" %>

    <input type="hidden" name="PM_CIS_WIP_CHG" value="<%=SysParmProvider.getInstance().getSysParm(RelationshipFields.SYSPARM_PM_CIS_WIP_CHG, "N")%>"/>
    <input type="hidden" name="PM_CIS_WIP_CHG_RTYPE" value="<%=SysParmProvider.getInstance().getSysParm(RelationshipFields.SYSPARM_PM_CIS_WIP_CHG_RTYPE, RelationshipFields.SYSPARM_PM_CIS_WIP_CHG_RTYPE_DEFAULT)%>"/>
    <input type="hidden" name="CM_CLIENTREL_ADDL1" value="<%=SysParmProvider.getInstance().getSysParm(RelationshipFields.SYSPARM_CM_CLIENTREL_ADDL1, "")%>"/>
    <input type="hidden" name="CM_CLIENTREL_ADDL2" value="<%=SysParmProvider.getInstance().getSysParm(RelationshipFields.SYSPARM_CM_CLIENTREL_ADDL2, "")%>"/>

    <input type="hidden" name="pk" value="<%=request.getAttribute(RelationshipFields.PK_PROPERTY) == null ? "" : (String) request.getAttribute(RelationshipFields.PK_PROPERTY)%>" />
    <input type="hidden" name="saveSuccess" value="<%=request.getAttribute(RelationshipFields.SAVE_SUCCESS_DESC)%>"/>
    <input type="hidden" name="info_policy_number" value="<%=request.getAttribute(RelationshipFields.INFO_POLICY_NO) == null ? "" : (String) request.getAttribute(RelationshipFields.INFO_POLICY_NO)%>" />
    <input type="hidden" name="dateOfBirth" value="<%=request.getAttribute(RelationshipFields.DATE_OF_BIRTH) == null ? "" : (String) request.getAttribute(RelationshipFields.DATE_OF_BIRTH)%>" />
    <input type="hidden" name="entityHasPolicy" value="<%=request.getAttribute(RelationshipFields.ENTITY_HAS_POLICY) == null ? "" : (String) request.getAttribute(RelationshipFields.ENTITY_HAS_POLICY)%>" />

    <tr>
        <td  id="formfields" colspan="6">
            <fmt:message key="relationship.legend.relationshipModify" var="entityRelationShipTitle" scope="page"/>
            <%
                String panelCaption = (String) pageContext.getAttribute("entityRelationShipTitle");
            %>
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="isTogglableTitle" value="false"/>
                <jsp:param name="headerText" value="<%=panelCaption%>"/>
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="divId" value="EntityRelationship"/>
                <jsp:param name="removeFieldPrefix" value="true"/>
                <jsp:param name="excludeAllLayers" value="true"/>
            </jsp:include>
        </td>
    </tr>

    <tr>
        <td align="center">
            <oweb:actionGroup actionItemGroupId="CI_RELAT_MOD_AIG" cssColorScheme="blue" layoutDirection="horizontal"/>
        </td>
    </tr>

    <jsp:include page="/core/footerpopup.jsp" />
