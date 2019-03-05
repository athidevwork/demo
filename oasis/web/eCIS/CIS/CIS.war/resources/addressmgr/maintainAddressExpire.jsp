<%@ page language="java" %>

<%--
  Description:
  JSP page for expire address
  Author: kshen
  Date: Feb 1, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  08/30/2007       Kenney      remove UIStyleEdition;
                               change to panel tag;
                               change to compiledFormField page
  10/06/2010       wfu         111776: Replaced hardcode string with resource definition
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<%@ include file="/core/headerpopup.jsp" %>
<jsp:include page="/cicore/common.jsp"/>

<jsp:include page="/addressmgr/addressCommon.jsp"/>

<script type="text/javascript" src="<%=cisPath%>/addressmgr/js/openAddressRoleChgPopup.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=cisPath%>/addressmgr/js/maintainAddressExpire.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<FORM action="ciAddressExpire.do" method="POST">
    <tr>
        <td colspan="6">
            <oweb:message/>
        </td>
    </tr>

    <%@ include file="/cicore/commonFormHeader.jsp" %>

    <tr>
        <td colspan="6">
            <jsp:include page="/core/compiledFormFields.jsp">
                <jsp:param name="hasPanelTitle" value="false"/>
                <jsp:param name="isGridBased" value="false"/>
                <jsp:param name="divId" value="panelContentForAddressExpire"/>
                <jsp:param name="excludeAllLayers" value="true"/>
            </jsp:include>
        </td>
    </tr>
    <tr colspan="6" align="center">
        <td>
            <oweb:actionGroup actionItemGroupId="CI_ADDRESS_EXPIRE_AIG" cssColorScheme="blue" layoutDirection="horizontal" />
        </td>
    </tr>

<jsp:include page="/core/footerpopup.jsp"/>