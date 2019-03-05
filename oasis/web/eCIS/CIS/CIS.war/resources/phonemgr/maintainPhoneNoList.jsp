<%@ page import="dti.ci.helpers.ICIPhoneNumberConstants,
                 org.apache.struts.Globals,
                 org.apache.struts.taglib.html.Constants"%>
<%@ page import="dti.ci.helpers.ICIConstants" %>
<%@ page language="java"%>
<%--
  Description: Phone Number List

  Author: Gerald C. Carney
  Date: Mar 22, 2004


  Revision Date    Revised By  Description
  ---------------------------------------------------
  05/15/2007       MLM         Added UI2 Changes
  06/29/2007       James       Added UI2 Changes
  08/30/2007       Kenney      remove UIStyleEdition;
  03/19/2009       kenney      Added Form Letter support for eCIS
  12/18/3014       bzhu        Issue 158705.
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  ---------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:set var="globalActionItemGroupId" value="CI_FOLDER_AG"></c:set>

<%@include file="/core/header.jsp" %>
<jsp:include page="/CI_EntitySelect.jsp"/>

<c:set var="tabMenuGroupId" value="${tabGroupId}"></c:set>
<%@ include file="/core/tabheader.jsp" %>

<script language="javascript" src="phonemgr/js/maintainPhoneList.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<!-- Content -->
<FORM name="CIPhoneNumberListForm" action="ciPhoneNumberList.do" method="POST">
    <jsp:include page="/cicore/ciFolderCommon.jsp" />
    <html:hidden value="N" property="<%=ICIConstants.LIST_DISPLAYED_PROPERTY%>"/>

    <jsp:include page="/phonemgr/phoneNumberSource.jsp"/>
    <input type="hidden" name="<%=Constants.TOKEN_KEY%>"
           value="<%=request.getSession().getAttribute(Globals.TRANSACTION_TOKEN_KEY)%>">
    <tr>
        <td colspan="6" align="center" style="padding-top:3px;padding-bottom:5px">
            <oweb:actionGroup actionItemGroupId="CI_PHONE_LIST_AIG"
                              cssColorScheme="blue" layoutDirection="horizontal">
            </oweb:actionGroup>
        </td>
    </tr>

<%@ include file="/core/tabfooter.jsp" %>
<jsp:include page="/core/footer.jsp"/>

