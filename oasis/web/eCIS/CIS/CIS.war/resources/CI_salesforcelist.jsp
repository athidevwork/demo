<%--
  Description: salesforce contact list page

  Author: James
  Date: Dec 12, 2007

  Revision Date    Revised By  Description
  ---------------------------------------------------
  06/28/2018       dpang         194157: Add buildNumber parameter to static file references to improve performance
  ---------------------------------------------------
  (C) 2007 Delphi Technology, inc. (dti)
--%>
<%@ page language="java" import="dti.ci.helpers.ICIContactConstants,
                                 dti.oasis.tags.WebLayer,
                                 org.apache.struts.Globals,
                                 org.apache.struts.taglib.html.Constants" %>
<%@ page import="dti.oasis.util.BaseResultSet" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<jsp:useBean id="gridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<%@ include file="/core/header.jsp" %>
<%@ include file="/CI_common.jsp" %>
<script type='text/javascript' src="js/gridbtnclicks.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type='text/javascript' src="js/CISalesforceList.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form name="CISalesforceListForm" action="ciSalesforce.do" method="POST">
    <tr>
        <td>
            <html:hidden property="<%=ICIContactConstants.PROCESS_PROPERTY%>" name="CISalesforceListForm"/>
            <html:hidden property="contactID" name="CISalesforceListForm" value=""/>
            <input type="hidden" name="<%=Constants.TOKEN_KEY%>"
                   value="<%=request.getSession().getAttribute(Globals.TRANSACTION_TOKEN_KEY)%>">

        </td>
    </tr>
    <c:if test="${msg!=null}">
    <tr>
        <td class="errortext" colspan="2" align="center">
            <c:out value="${msg}"></c:out>
        </td>
    </tr>
    </c:if>
    <tr>
        <td>
            <fmt:message key="ci.entity.contacts.form.title" var="contactsListTitle" scope="request"/>
            <% String contactsListTitle = (String) request.getAttribute("contactsListTitle"); %>
            <oweb:panel panelContentId="panelContentIdForList"
                        panelTitleId="panelTitleIDForList"
                        isTogglableTitle="false"
                        panelTitle="<%=contactsListTitle%>">
                <tr>
                    <td>
                        <c:set var="gridDisplayFormName" value="CISalesforceListForm" scope="request"/>
                        <c:set var="gridDisplayGridId" value="testgrid" scope="request"/>
                        <c:set var="datasrc" value="#testgrid1" scope="request"/>
                        <%
                            BaseResultSet dataBean = gridDataBean;
                        %>
                        <%@ include file="/core/gridDisplay.jsp" %>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="">
                        <oweb:actionGroup actionItemGroupId="CI_SALESFORCE_AIG">
                        </oweb:actionGroup>
                    </td>
                </tr>

            </oweb:panel>
        </td>
    </tr>
<jsp:include page="/core/footer.jsp"/>