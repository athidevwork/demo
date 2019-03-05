<%@ page import="dti.oasis.util.PageBean" %>
<%--
  Description:

  Author: jmpotosky
  Date: Jun 1, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  04/06/2008       Joe         Refactor into OasisTags from ePolicy - not include policyHeader.jsp now.
  09/02/2011       syang       121201 - During multi cancellation, if tail is created, system should refresh page to display tail coverage page.
  04/09/2013       mlm         142697 - Changes to support refreshing of the Workflow Screen without moving
                                        to the next step.
  11/09/2018       dzhang      196632 - Grid replacement.
                                        1) The useJqxGrid can't be override after loadPageBean.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<c:set var="isForDivPopup" value="true"></c:set>
<%
    boolean originalUseJqxGrid = (boolean) OasisTagHelper.isUseJqxGrid(pageContext);
    ActionHelper.loadPageBean(request, "dti.oasis.workflowmgr.struts.WorkflowAction");
    PageBean CurrentPageBean = (PageBean) request.getAttribute(IOasisAction.KEY_PAGEBEAN);
    CurrentPageBean.setUseJqxGridB(originalUseJqxGrid ? "Y" : "N");
%>
<c:set target="${pageBean}" property="title" value=""/>
<%@ include file="headerpopup.jsp" %>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<form name="workflowProcessing" action="<%=appPath%>/workflowmgr/workflow.do" method=post>
    <c:set var="process" value="processWorkflow"></c:set>
    <c:if test="${empty process}">
        <c:set var="process" value=""></c:set>
    </c:if>
    <c:if test="${empty isNewValue}">
        <c:set var="isNewValue" value="Y"></c:set>
    </c:if>
    <c:if test="${empty isPageRefreshed}">
        <c:set var="isPageRefreshed" value="Y"></c:set>
    </c:if>

    <input type=hidden name=process value="<c:out value="${process}"/>">
    <input type=hidden name=isNewValue value="<c:out value="${isNewValue}"/>">
    <input type=hidden name=isPageRefreshed value="<c:out value="${isPageRefreshed}"/>">
    <input type="hidden" name="<%=Constants.TOKEN_KEY%>"
           value="<%=request.getSession().getAttribute(Globals.TRANSACTION_TOKEN_KEY)%>">

    <input type=hidden name=workflowState value="<c:out value="${workflowState}"/>">
    <input type="hidden" name="<c:out value="${workflowInstanceIdName}"/>" value="<c:out value="${workflowInstanceId}"/>"/>

    <tr>
        <td colspan=1>
            <img src="<%=corePath%>/images/running.gif" alt="saving"></img>
        </td>
        <td colspan=7>
            <oweb:message/>
        </td>
    </tr>

    <script type="text/javascript">
        function enableFieldsForSubmit(theform) {
            var elems = theform.elements;

            for (var i = 0; i < elems.length; i++) {
                if (elems[i].disabled) {
                    hideShowElementByClassName(elems[i], true);
                    elems[i].disabled = false;
                }
            }
        }
        function handleOnLoad() {
            if(getObjectValue("workflowState")=="invokeViewTailMsgForMultiCancel"){
                getParentWindow().refreshPage();
            }
            else {
                setObjectValue("isPageRefreshed", "N") ;
                enableFieldsForSubmit(document.forms[0]);
                submitFirstForm();
            }
        }
    </script>
<jsp:include page="footerpopup.jsp"/>
