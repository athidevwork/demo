<%--
  Description: Policy system workflow processing specific page

  Author: jshen
  Date: Oct 12, 2012


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  02/04/2013       tcheng      141447 - Modified handleOnLoad to forward to premium screen in rating workflow.
  03/08/2013       tcheng      142691 - Modified handleOnLoad to forward to view premium page in rating workflow.
  03/06/2013       fcb         142697 - Removed the previous changes.
  04/09/2013       mlm         142697 - Changes to support refreshing of the Workflow Screen without moving
                                        to the next step.
  10/26/2917       kshen       Grid replacement. Changed to use getParentWindow to get parent window. Changed to use
                               closeThisDivPopup to close the current window.
   01/24/2018      dzhang      Grid replacement: change to use closewindow.
  07/13/2018       wrong       194418 - Remove fieldsMap useBean.
  11/15/2018       lzhang      194100 add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<c:set var="isForDivPopup" value="true"></c:set>
<c:set target="${pageBean}" property="title" value=""/>
<%@ include file="/core/headerpopup.jsp" %>

<form name="workflowProcessing" method=post>
    <c:set var="process" value="processNonPolicyWorkflow"></c:set>
    <c:if test="${empty process}">
        <c:set var="process" value=""></c:set>
    </c:if>
    <c:if test="${empty isNewValue}">
        <c:set var="isNewValue" value=""></c:set>
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
            <img src="<%=corePath%>/images/running.gif?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>" alt="saving"></img>
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
                    elems[i].style.visibility = "hidden";
                    elems[i].disabled = false;
                }
            }
        }
        function handleOnLoad() {
            setObjectValue("isPageRefreshed", "N") ;
            var workflowState = getObjectValue("workflowState");
            if (workflowState == "invokeReRateMsg") {
                document.forms[0].action = getAppPath() + "/workflowmgr/workflow.do?process=processNonPolicyWorkflow";
                enableFieldsForSubmit(document.forms[0]);
                submitFirstForm();
            }
            else if (workflowState == "invokeReRateReportMsg") {
                closeWindow(function () {
                    getParentWindow().openReRateResult("loadAllReRateResult", getObjectValue("workflowInstanceId"));
                });
            }
            else {
                document.forms[0].action = getAppPath() + "/workflowmgr/workflow.do";
                enableFieldsForSubmit(document.forms[0]);
                submitFirstForm();
            }
        }
    </script>
<jsp:include page="/core/footerpopup.jsp"/>
