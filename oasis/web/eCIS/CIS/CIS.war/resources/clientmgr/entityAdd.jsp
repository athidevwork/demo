<%@ page language="java"%>
<%@ page import="dti.ci.helpers.ICIConstants"%>
<%@ page import="dti.ci.helpers.ICIEntityConstants" %>

<%--
  Description: The add entity page.

  Author: Kyle Shen
  Date: Oct 17, 2008


  Revision Date    Revised By  Description
  ---------------------------------------------------
  07/04/2011       Michale     Issue 117347
  10/29/2013       ldong       Issue 138932
  01/23/2014       Elvin       Issue 138932: reset process when nagative to ciEntityPersonModify in afterSave,
                                             to avoid data lossing error
  04/15/2015       bzhu        Issue 159178. Remain province for other country.
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  11/16/2018       Elvin       Issue 195835: grid replacement
  ---------------------------------------------------
  (C) 2008 Delphi Technology, inc. (dti)
--%>

<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="duplicateList" class="java.util.ArrayList" scope="request"/>
<%
    String formAction = (String) request.getAttribute(ICIConstants.FORM_ACTION_PROPERTY);
    String newVal = (String) request.getAttribute(ICIConstants.IS_NEW_VAL_PROPERTY);
    String newPk = String.valueOf(request.getAttribute("pk"));
%>

<%@include file="/core/header.jsp" %>
<jsp:include page="/cicore/common.jsp"/>

<jsp:include page="/addressmgr/addressCommon.jsp"/>

<script type='text/javascript' src="<%=cisPath%>/clientmgr/js/entityAddCommon.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type='text/javascript' src="<%=cisPath%>/clientmgr/js/entityAdd.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<FORM action="<%=formAction%>" method="POST">
    <input type="hidden" name ="<%=ICIConstants.IS_NEW_VAL_PROPERTY%>" value="<%=newVal%>" />
    <input type="hidden" name ="<%=ICIEntityConstants.CI_ENTITY_CONTINUE_ADD%>" value="<%=(String) request.getAttribute(ICIEntityConstants.CI_ENTITY_CONTINUE_ADD)%>" />
    <input type="hidden" name ="CI_ENTY_ADD_REUSE_FIELDS_ADDRESS" value="<%=(String) request.getAttribute("CI_ENTY_ADD_REUSE_FIELDS_ADDRESS")%>" />
    <input type="hidden" name ="CI_ENTY_ADD_REUSE_FIELDS_PHONE" value="<%=(String) request.getAttribute("CI_ENTY_ADD_REUSE_FIELDS_PHONE")%>" />
    <input type="hidden" name ="CI_ENTY_ADD_REUSE_FIELDS_CLASSIFICATION" value="<%=(String) request.getAttribute("CI_ENTY_ADD_REUSE_FIELDS_CLASSIFICATION")%>" />
    <input type="hidden" name ="CI_REUSE_ADDRESS_CLEAR" value="<%=(String) request.getAttribute("CI_REUSE_ADDRESS_CLEAR")%>" />
    <input type="hidden" name ="CI_REUSE_PHONE_CLEAR" value="<%=(String) request.getAttribute("CI_REUSE_PHONE_CLEAR")%>" />
    <input type="hidden" name ="CI_REUSE_CLASSIFICATION_CLEAR" value="<%=(String) request.getAttribute("CI_REUSE_CLASSIFICATION_CLEAR")%>" />
    <input type="hidden" value="<%=newPk%>" name="newPk"/>
<jsp:include page="entityAddContent.jsp" />

<jsp:include page="/core/footer.jsp"/>
