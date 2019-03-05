<%@ page import="dti.oasis.messagemgr.MessageManager" %>
<%@ page import="dti.oasis.workflowmgr.impl.WorkflowAgentImpl" %>
<%@ page import="dti.oasis.workflowmgr.WorkflowAgent" %>
<%@ page import="dti.oasis.http.RequestIds" %>
<%@ page import="dti.oasis.util.PageBean" %>
<%--
  Description:

  Author: jmpotosky
  Date: Jun 1, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  06/29/2007       sxm         Set attribute "showAllMessage" of OasisMessage tag to true
                               to display both error and informational messages
  04/07/2008       Joe         Refactor into OasisTags from ePolicy.
  04/17/2008       fcb         Re-added back policyNo hidden field.
  04/18/2008       Joe         Since it will get workflowInstanceIdName from workflowAgent
                               and then get the value of it from request, so we don't need to have the hidden policyNo here.
  10/12/2010       dzhang      iss103810 - Rollback the changes done by 103810.
  03/14/2013       tcheng      iss142196 - Modified closeWindow() function to forward to new quote page when copy to quote.
  05/03/2013       tcheng      144379 - Modified closeWindow() function to add a parameter for forwarding to quote page.
  07/23/2015       Elvin       Issue 160360.
  12/15/2015       jyang2      167179 - Corrected the one sentence in closeWindow() which missed a semicolon.
  07/14/2016       mlm         170307 - Fix to refer to parentWindow to get term effective dates.
  10/21/2016       kxiang      180685 - Modified closeWindow() to disable the page when it is called to avoid duplicate click.
  01/26/2017       mlm         182975 - Start the call to Eloquence immediately instead of waiting for user to click on Ok button.
  02/28/2017       mlm         183387 - Refactored to handle preview for long running transactions.
  03/13/2017       eyin        180675 - Support Main page && Sub-tab invoke workFlow for ePolicy New UI change.
  01/16/2018       eyin        190859 - Added to call function setWorkFlowPopupOpenedB();
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

<form name="workflowExit" action="workflow.do" method=post>
    <c:set var="process" value="processWorkflow"></c:set>
    <c:if test="${empty process}">
        <c:set var="process" value=""></c:set>
    </c:if>
    <c:if test="${empty isNewValue}">
        <c:set var="isNewValue" value=""></c:set>
    </c:if>

    <input type=hidden name=process value="<c:out value="${process}"/>">
    <input type=hidden name=isNewValue value="<c:out value="${isNewValue}"/>">
    <input type="hidden" name="<%=Constants.TOKEN_KEY%>"
           value="<%=request.getSession().getAttribute(Globals.TRANSACTION_TOKEN_KEY)%>">

    <input type=hidden name=workflowState value="<c:out value="${workflowState}"/>">
    <tr>
        <td colspan=8>
            <oweb:message showAllMessages="true"/>
        </td>
    </tr>

    <tr>
        <td colspan=8 align=center>
            <input type="button" name="workflowExit_Ok" value="OK" onclick="javascript:closeWindow();"
                   class="buttonText">
        </td>
    </tr>

    <script type="text/javascript">
        function closeWindow() {
    <%
        WorkflowAgent workflowAgent = WorkflowAgentImpl.getInstance();
        String workflowInstanceIdName = workflowAgent.getWorkflowInstanceIdName();
        String workflowInstanceId = null;
        if (request.getAttribute(workflowInstanceIdName) != null) {
            workflowInstanceId = request.getAttribute(workflowInstanceIdName).toString();
        }
    %>
            showProcessingImgIndicator();
            var oParentWindow = getParentWindow();
            var popupDivId = "";
            var functionExists = eval("getParentWindow(true).isTabStyle");
            if (functionExists && getParentWindow(true).isTabStyle()) {
                functionExists = eval("getParentWindow(true).getReturnCtxOfDivPopUp");
                if(functionExists){
                    popupDivId = oParentWindow.getDivPopupFromDivPopupControl(this.frameElement).id;
                    //for ePolicy module in tab style, system needs to know the correct parent window where this popup is opened
                    oParentWindow = getParentWindow(true).getReturnCtxOfDivPopUp(popupDivId);
                }
            }

            var needToHandleExitWorkFlow = (oParentWindow.hasObject('needToHandleExitWorkFlow') &&
                                            (oParentWindow.getObjectValue('needToHandleExitWorkFlow') == 'Y'));
            var needToForwardQuote = (oParentWindow.hasObject('needToForwardQuote') &&
                                            (oParentWindow.getObjectValue('needToForwardQuote') == 'Y'));
        <%if (MessageManager.getInstance().hasErrorMessages()) {%>
            if (needToHandleExitWorkFlow && eval("oParentWindow.handleExitWorkflowWithError")) {
                var divPopup = oParentWindow.getDivPopupFromDivPopupControl(this.frameElement);
                oParentWindow.handleExitWorkflowWithError(divPopup, '<%=workflowInstanceId%>');
                oParentWindow.setInputFormField("needToHandleExitWorkFlow", "N");
            }
            oParentWindow.refreshPage(true);
        <%}else{%>
            if (needToHandleExitWorkFlow && eval("oParentWindow.handleExitWorkflow")) {
                if (needToForwardQuote && eval("oParentWindow.handleExitWorkflowForQuote")) {
                    oParentWindow.handleExitWorkflowForQuote('<%=workflowInstanceId%>');
                }
                else {
                    oParentWindow.handleExitWorkflow('<%=workflowInstanceId%>');
                }
                oParentWindow.setInputFormField("needToHandleExitWorkFlow", "N");
                oParentWindow.setInputFormField("needToForwardQuote", "N");
            }
            else {
                oParentWindow.refreshPage(true);
            }
        <%}%>
            functionExists = eval("getParentWindow(true).isTabStyle");
            if(functionExists && getParentWindow(true).isTabStyle()){
                functionExists = eval("getParentWindow(true).getParentWindowOfDivPopup");
                if(functionExists){
                    var parentWindowFlag = getParentWindow(true).getParentWindowOfDivPopup(popupDivId);
                    //for ePolicy module in tab style, system needs to know if this popup is opened from sub-tab
                    //if yes, we need to close popup window after sub-tab is refreshed.
                    if(parentWindowFlag == "iFrameWindow"){
                        closeThisDivPopup();
                        hideProcessingImgIndicator();
                    }
                }

                functionExists = eval("getParentWindow(true).setWorkFlowPopupOpenedB");
                if(functionExists){
                    getParentWindow(true).setWorkFlowPopupOpenedB(false);
                }
            }
        }
    </script>
    <jsp:include page="footerpopup.jsp"/>
