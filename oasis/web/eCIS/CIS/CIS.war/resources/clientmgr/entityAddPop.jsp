<%@ page language="java"%>
<%@ page import="dti.ci.helpers.ICIConstants"%>
<%@ page import="dti.ci.helpers.ICIEntityConstants" %>

<%--
  Description:  Add entity pop up page.
  Author: HXY
  Date: Nov 9, 2005

  Revision Date    Revised By  Description
  ---------------------------------------------------
  01/11/2007       PXS issue 66659 - pass dbpoolid appendix
  that is set by headerpopup.jsp in inline javascirpt call
  01/30/2007       GCC         Dummy comment to force
                               P4 propagation.
  02/05/2007       kshen       Refresh county's LOV when state is changed.(Issue 61440)

  02/16/2007       kshen       Deleted redundant jsp include CI_entityAddContent.jsp
  04/09/2008       wer         Removed passing dbPoolId appendix to comply with new Role-based dbPoolId configuration.
  10/29/2013       ldong       Issue 138932
  08/04/2015       Elvin       Issue 164298: add hidden field provinceForOtherCountry
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  11/16/2018       Elvin       Issue 195835: grid replacement
  ---------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>

<%@include file="/core/headerpopup.jsp" %>
<jsp:include page="/addressmgr/addressCommon.jsp"/>
<jsp:include page="/cicore/common.jsp"/>

<script type='text/javascript' src="<%=cisPath%>/clientmgr/js/entityAddCommon.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type='text/javascript' src="<%=cisPath%>/clientmgr/js/entityAddPop.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<FORM action="<%=(String) request.getAttribute(ICIConstants.FORM_ACTION_PROPERTY)%>" method="POST">
    <input type="hidden" value="<%=(String) request.getAttribute(ICIEntityConstants.CI_ENTITY_CONTINUE_ADD)%>" name="<%=ICIEntityConstants.CI_ENTITY_CONTINUE_ADD%>"/>
    <input type="hidden" value="<%=(String) request.getAttribute(ICIConstants.IS_NEW_VAL_PROPERTY)%>" name="<%=ICIConstants.IS_NEW_VAL_PROPERTY%>"/>
    <input type="hidden" value="<%=(String) request.getAttribute("CI_ENTY_ADD_REUSE_FIELDS_ADDRESS")%>" name="CI_ENTY_ADD_REUSE_FIELDS_ADDRESS"/>
    <input type="hidden" value="<%=(String) request.getAttribute("CI_ENTY_ADD_REUSE_FIELDS_PHONE")%>" name="CI_ENTY_ADD_REUSE_FIELDS_PHONE"/>
    <input type="hidden" value="<%=(String) request.getAttribute("CI_ENTY_ADD_REUSE_FIELDS_CLASSIFICATION")%>" name="CI_ENTY_ADD_REUSE_FIELDS_CLASSIFICATION"/>
    <input type="hidden" value="<%=(String) request.getAttribute("CI_REUSE_ADDRESS_CLEAR")%>" name="CI_REUSE_ADDRESS_CLEAR"/>
    <input type="hidden" value="<%=(String) request.getAttribute("CI_REUSE_PHONE_CLEAR")%>" name="CI_REUSE_PHONE_CLEAR"/>
    <input type="hidden" value="<%=(String) request.getAttribute("CI_REUSE_CLASSIFICATION_CLEAR")%>" name="CI_REUSE_CLASSIFICATION_CLEAR"/>
    <input type="hidden" value="<%=(String) request.getAttribute("clientId")%>" name="clientId"/>

    <jsp:include page="/clientmgr/entityAddContent.jsp" />
    <tr>
        <td align="center" colspan='6'>
            <oweb:actionGroup actionItemGroupId="CI_ENT_POP_ADD"/>
        </td>
    </tr>

<jsp:include page="/core/footerpopup.jsp"/>
