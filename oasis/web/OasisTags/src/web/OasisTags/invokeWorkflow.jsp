<%@ page import="dti.oasis.workflowmgr.WorkflowAgent" %>
<%@ page import="dti.oasis.workflowmgr.impl.WorkflowAgentImpl" %>
<%@ page import="dti.pm.busobjs.SysParmIds" %>

<%--
  Description:

  Author: jmpotosky
  Date: Jun 1, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  04/07/2008       Joe         Refactor into OasisTags from ePolicy.
  10/19/2010       gzeng       112909 - Set isScrolling to false for special pages.
  04/09/2011       Joe         94232 - Reset the popup windown size if it is to invoke view ibnr inactive risk page.
  04/22/2011       dzhang      117758 - Reset the popup windown size to avoid duplicate scroll bars for tail page.
  09/02/2011       syang       121201 - During multi cancellation, if tail is created, system should refresh page to display tail coverage page.
  03/12/2013       tcheng      142691 - Modified invokeWorkFlow to pop up premium screen in rate flow.
  03/06/2013       fcb         142697 - Added logic to handle the workflow when the View Premium is configured to
                                        show during the workflow.
  04/09/2013       mlm         142697 - Changes to support refreshing of the Workflow Screen without moving
                                        to the next step.
  07/18/2016       xnie        178031 - Changed codes to support scroll control bar for view related policy page.
  03/13/2017       eyin        180675 - Support Main page && Sub-tab invoke workFlow for ePolicy New UI change.
  01/16/2018       eyin        190859 - Added to call function setWorkFlowPopupOpenedB();
  11/13/2018       wrong       192773 - Modified to use getOpenCtxOfDivPopUp() to call setWorkFlowPopupOpenedB().
   -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<script type="text/javascript">
    function invokeWorkflow() {
    <%
        WorkflowAgent workflowAgent = WorkflowAgentImpl.getInstance();
        String workflowInstanceIdName = workflowAgent.getWorkflowInstanceIdName();
        String workflowInstanceId = null;
        if (request.getAttribute(workflowInstanceIdName) != null) {
            workflowInstanceId = request.getAttribute(workflowInstanceIdName).toString();
        }
        if (workflowInstanceId != null && workflowAgent.hasWorkflow(workflowInstanceId)) {
            String startingState = workflowAgent.getWorkflowCurrentState(workflowInstanceId);
    %>
        var oParentWindow = window;
        var processingDivId = "";
        var functionExists = eval("window.isTabStyle");
        if (functionExists && isTabStyle()) {
            // for ePolicy module in tab style, we need to display popup of sub-tab on tbe top of main screen.
            oParentWindow = getOpenCtxOfDivPopUp();
        }

        var url = getAppPath() + "/workflowmgr/workflow.do?" +
                  "&workflowState=<%=startingState%>" +
                  "&<%=workflowInstanceIdName%>=<%=workflowInstanceId%>" +
                  "&date=" + new Date();
    <%  if (startingState.equals("invokeViewTail")||
                startingState.equals("invokeTailRateNotifyAndSaveAsOfficialDetail") ||
                startingState.equals("invokeViewTailMsgForMultiCancel")) {
    %>
        processingDivId = oParentWindow.openDivPopup("", url, true, true, "", "", 830, 800, "", "", "", false);
    <%      } else if (startingState.equals("invokeViewIbnrInactiveRiskMsg")) {
    %>
        processingDivId = oParentWindow.openDivPopup("", url, true, true, "", "", 910, 950, 905, 945, "", false,"","",false);
    <%      } else if (startingState.equals("invokeViewRelPolicy")) {
    %>
        processingDivId = oParentWindow.openDivPopup("", url, true, true, "", "", 700, 500, "", "", "", false);
    <%      } else {
    %>
        processingDivId = oParentWindow.openDivPopup("", url, true, true, "", "", 600, 150, "", "", "", false);
    <%
            }
    %>
        if (functionExists && isTabStyle()) {
            var oParentWindowFlag = typeof subFrameId != 'undefined' ? "ParentWindow" : "iFrameWindow";
            functionExists = eval("getOpenCtxOfDivPopUp().handleOnPutParentWindowOfDivPopup");
            if(functionExists){
                //for ePolicy module in tab style, as the popup is always displaying on the top of main screen,
                //irrespective the popup is opened from sub-tab or main page, so system needs to keep the flag
                //where this popup is opened in here, it will be used when invoke handle function from popup.
                getOpenCtxOfDivPopUp().handleOnPutParentWindowOfDivPopup(processingDivId, oParentWindowFlag);
            }

            functionExists = eval("getOpenCtxOfDivPopUp().setWorkFlowPopupOpenedB");
            if(functionExists){
                getOpenCtxOfDivPopUp().setWorkFlowPopupOpenedB(true);
            }
        }
        return true;
    <%
        }
    %>
        return false;
    }
</script>
