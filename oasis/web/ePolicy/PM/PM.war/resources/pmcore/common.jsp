<!-- How to use this page:
    1): Include this page right after include header.jsp



-->
<%--
  Description:

  Author: gjlong
  Date: Feb 21, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  02/27/2007       sxm         Included CICommon.js for create Policy/Quote
  02/28/2007       jmp         Logic to check request for existence of a
                               ValidationException and reset the isChanged JS variable
  04/30/2007       sxm         Set common JS messages
  03/28/208        fcb         New system parameter set here.
  09/13/2010       wfu         111776 - Replaced hardcode string with resource definition
  09/16/2010       dzhang      103813 - Include undoTerm.js for Undo Term.
  10/12/2010       wfu         111776 - Used addJsMessage Java function replacing setMessage
  06/09/2011       ryzhao      103808 - Include shortTermPolicy.js for accept/decline short term policy.
  03/22/2012       xnie        130643 - Included flatCancelPolicy.js for flat cancel policy.
  07/24/2012       awu         129250 - Added a new parameter PM_AUTO_SAVE_WIP.
  08/13/2012       awu         136472 - Set the default value of the parameter PM_AUTO_SAVE_WIP to Y.
  12/06/2013       Parker      148036 - Refactor maintainRecordExists code to make one call per subsystem to the database.
  12/21/2015       ssheng      168270 - Add JS messages.
  06/23/2016       tzeng       167531 - Added all confirmation prompts from request.
  06/17/2016       ssheng      164927 - filter PM entity
  08/26/2016       wdang       167534 - Add policyAttribute support.
  03/10/2017       wli         180675 - Initialized parameter pmUIStyle and modified recordExistsManager iterator
                                        for new UI tab style.
  04/14/2017       tzeng       166929 - 1) Added a new parameter PM_CHECK_SOFT_VAL_B and set default value as N.
                                        2) Added a new parameter EAPP_PM_INIT_ASYNC and set default value as Y.
                                           If it is Y, then add JS message for asynchronous prompt.
  05/09/2017       ssheng      185360 - added a new parameter PM_NB_QUICK_QUOTE.
  07/26/2017       lzhang      182246 - add js message
  09/01/2017       wrong       186656 - Added js message.
  11/09/2017       tzeng       187689 - Added js message.
  09/07/2018       xnie        195106 - Added a new system parameter PM_POL_FIRST_SUB_TAB.
  09/19/2018       tyang       195522 - Added a new system parameter PM_COV_FIRST_SUB_TAB.
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>


<%@ page import="dti.oasis.struts.IOasisAction"%>
<%@ page import="dti.oasis.util.SysParmProvider"%>
<%@ page import="dti.oasis.messagemgr.MessageManager" %>
<%@ page import="dti.oasis.tags.GridHelper" %>
<%@ page import="dti.pm.core.http.RequestIds" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="dti.oasis.messagemgr.Message" %>
<%@ page import="dti.oasis.app.ApplicationContext" %>
<%@ page import="java.util.Collection" %>
<%@ page import="dti.oasis.recordset.Record" %>
<%@ page import="dti.cs.recordexistsmgr.impl.RecordExistsManagerImpl" %>
<%@ page import="dti.cs.recordexistsmgr.RecordExistsPage" %>
<%@ page import="dti.cs.recordexistsmgr.RecordExistsButton" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="dti.oasis.util.PageBean" %>
<%@ page import="org.primefaces.json.JSONObject" %>
<%@ page import="org.primefaces.json.JSONArray" %>
<%@ page import="dti.pm.policyattributesmgr.PolicyAttributesFactory" %>
<%@ page import="dti.pm.policyattributesmgr.PmAttributeFields" %>
<%@ page import="dti.pm.busobjs.SysParmIds" %>
<%@ page import="dti.pm.policymgr.applicationmgr.ApplicationFields" %>

<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>

<script type="text/javascript" src="<%=cisPath%>/js/CICommon.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=csPath%>/js/csImaging.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=appPath%>/transactionmgr/renewalprocessmgr/js/renewPolicy.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=appPath%>/transactionmgr/cancelprocessmgr/js/performCancellation.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=appPath%>/transactionmgr/reinstateprocessmgr/js/performReinstate.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=appPath%>/transactionmgr/shorttermpolicymgr/js/shortTermPolicy.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=appPath%>/policymgr/js/denyQuote.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=appPath%>/policymgr/js/maintainQuote.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=appPath%>/policymgr/js/copyToQuote.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=appPath%>/policymgr/applicationmgr/js/openApplicationList.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=appPath%>/transactionmgr/cancelprocessmgr/js/purge.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=appPath%>/transactionmgr/cancelprocessmgr/js/flatCancelPolicy.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=appPath%>/transactionmgr/js/undoTerm.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=appPath%>/pmcore/js/common.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<input type="hidden" name="<%=RequestIds.SAVE_INPROGRESS%>" value="<%=request.getAttribute(RequestIds.SAVE_INPROGRESS)%>">

<script type="text/javascript">
    function isSaveInProgress() {
        var isInProgress='false';
        if(getObject("<%= RequestIds.SAVE_INPROGRESS %>")) {
            isInProgress = getObject("<%= RequestIds.SAVE_INPROGRESS %>").value;
        }
        return (isInProgress.toUpperCase()=='TRUE');
    }

    var hasErrorMessages = <%=MessageManager.getInstance().hasErrorMessages()%>;
</script>

<%
    // Initialize Sys Parms for JavaScript to use
    String skipCommentWindow = SysParmProvider.getInstance().getSysParm("PM_SKIP_COMMENT_WIND", "N");
    String useQuoteTransWindow = SysParmProvider.getInstance().getSysParm("PM_USE_QT_TRANS_WIND", "N");
    String convertCoverageForQuote = SysParmProvider.getInstance().getSysParm("PM_QT_CM_OCC_CONV", "N");
    String useDeclineTransWindow = SysParmProvider.getInstance().getSysParm("PM_DECLINE_TAIL_RESN", "N");
    String autoSaveIndicator = SysParmProvider.getInstance().getSysParm("PM_AUTO_SAVE_WIP", "Y");
	String pmUIStyle = SysParmProvider.getInstance().getSysParm("PM_UI_STYLE", "T");
    String softValidationPromptForQuote = SysParmProvider.getInstance().getSysParm(SysParmIds.PM_CHECK_SOFT_VAL_B, "N");
    String initiateEAppAsync = SysParmProvider.getInstance().getSysParm(ApplicationFields.EAPP_PM_INIT_ASYNC, "Y");
    String nbQuickQuote = SysParmProvider.getInstance().getSysParm("PM_NB_QUICK_QUOTE", "Y");
    String policyPageFirstTab = SysParmProvider.getInstance().getSysParm("PM_POL_FIRST_SUB_TAB", "COMPONENT");
    String coveragePageFirstTab = SysParmProvider.getInstance().getSysParm("PM_COV_FIRST_SUB_TAB", "COVERAGE");

    // Set JS messages
    MessageManager.getInstance().addJsMessage("appException.unexpected.error");
    MessageManager.getInstance().addJsMessage("pm.transactionmgr.extendCancelTerm.confirm.info");
    if ("Y".equals(initiateEAppAsync)) {
        MessageManager.getInstance().addJsMessage("pm.eApp.initiate.process.submit.info");
    }
    // Added for replacing hardcode messages
    MessageManager.getInstance().addJsMessage("pm.common.selected.record.delete.confirm");
    MessageManager.getInstance().addJsMessage("pm.common.process.notCompleted");
    MessageManager.getInstance().addJsMessage("pm.common.validate.policyNo.error");
    MessageManager.getInstance().addJsMessage("pm.common.handle.saveOption.error");
    MessageManager.getInstance().addJsMessage("pm.common.miniEntity.open.error");
    MessageManager.getInstance().addJsMessage("pm.common.row.selected.error");
    MessageManager.getInstance().addJsMessage("pm.common.getRecord.run.error");
    MessageManager.getInstance().addJsMessage("pm.common.list.validTerm.select.error");
    MessageManager.getInstance().addJsMessage("pm.common.miniPopup.noEntityId.error");
    MessageManager.getInstance().addJsMessage("pm.common.notes.functionality.notAvailable.error");
    MessageManager.getInstance().addJsMessage("pm.common.fileNumber.determine.error");
    MessageManager.getInstance().addJsMessage("pm.common.wip.delete.confirm");
    MessageManager.getInstance().addJsMessage("pm.common.clickOk.changesLost.confirm");
    MessageManager.getInstance().addJsMessage("cs.outputmgr.processOutput.job.based.output.warning");
    MessageManager.getInstance().addJsMessage("pm.subTab.reserved.clickOk.changesWitoutSave.confirm");
    MessageManager.getInstance().addJsMessage("pm.subTab.real.clickOk.changesLost.confirm");
    // set js messages for imageRight
    MessageManager.getInstance().addJsMessage("cs.imageRight.error.install.notProperly");
    MessageManager.getInstance().addJsMessage("cs.imageRight.error.information.notDetermine");
    MessageManager.getInstance().addJsMessage("pm.maintainPolicy.isPolicyEntity.filter");
    MessageManager.getInstance().addJsMessage("pm.common.unsaved.changes.error");
%>

<script type="text/javascript">
     policyAttributeObject.put('<%=PmAttributeFields.PM_DISP_COMMENT_WIND%>', <%=new JSONArray(PolicyAttributesFactory.getInstance().loadPmAttribute(PmAttributeFields.PM_DISP_COMMENT_WIND), true).toString() %>);
     setSysParmValue("PM_SKIP_COMMENT_WIND",'<%=skipCommentWindow %>');
     setSysParmValue("PM_USE_QT_TRANS_WIND",'<%=useQuoteTransWindow %>');    
     setSysParmValue("PM_QT_CM_OCC_CONV",'<%=convertCoverageForQuote %>');
     setSysParmValue("PM_DECLINE_TAIL_RESN",'<%=useDeclineTransWindow %>');
     setSysParmValue("PM_AUTO_SAVE_WIP",'<%=autoSaveIndicator %>');
	 setSysParmValue("PM_UI_STYLE",'<%=pmUIStyle %>');
     setSysParmValue('<%=SysParmIds.PM_CHECK_SOFT_VAL_B%>','<%=softValidationPromptForQuote %>');
     setSysParmValue('<%=ApplicationFields.EAPP_PM_INIT_ASYNC%>','<%=initiateEAppAsync %>');
     setSysParmValue('<%=SysParmIds.PM_NB_QUICK_QUOTE%>','<%=nbQuickQuote %>');
     setSysParmValue("PM_POL_FIRST_SUB_TAB",'<%=policyPageFirstTab %>');
     setSysParmValue("PM_COV_FIRST_SUB_TAB",'<%=coveragePageFirstTab %>');
</script>

<%
    // If we raised a validation exception, we have re-forwarded to the load and reset the JS variables
    // Reset here so if we later try to leave the page, we receive the is ok to change pages logic.
    if(!(request.getAttribute(IOasisAction.KEY_ERROR) == null)) {
        if((request.getAttribute(IOasisAction.KEY_ERROR) instanceof dti.oasis.error.ValidationException)) {
%>
<script type="text/javascript">
     isChanged = true;
</script>
<%      }
    }   %>

<!-- Handle message -->
<script type="text/javascript">
//set js messages in page
<%
    MessageManager jsMessageManager = MessageManager.getInstance();
    if (jsMessageManager.hasJsMessages()) {
        Iterator iter = jsMessageManager.getJsMessages();
        while(iter.hasNext()){
            Message message = (Message) iter.next();
%>
        setMessage("<%=message.getMessageKey()%>","<%=message.getMessage()%>");
<%
        }
    }
%>

//set confirmation messages in page
<%
    MessageManager promptMessageManager = MessageManager.getInstance();
    if (promptMessageManager.hasConfirmationPrompts()) {
        Iterator iter = promptMessageManager.getConfirmationPrompts();
        while(iter.hasNext()){
            Message message = (Message) iter.next();
%>
            setMessage("<%=message.getMessageKey()%>","<%=message.getMessage()%>");
<%
        }
    }
%>
</script>
<%
    // Initialize RecordExistsManager for italics logic
    RecordExistsManagerImpl recordExistsManager = (RecordExistsManagerImpl) dti.oasis.app.ApplicationContext.getInstance().getBean("RecordExistsManager");
    HashMap existsMap = recordExistsManager.getExistsMap();

    PageBean pageCommonBean = (PageBean) request.getAttribute("pageBean");
    String pageId = pageCommonBean.getId();
    if ("CI_ENTITY_PERSON_MODIFY".equals(pageId) || "CI_ENTITY_ORG_MODIFY".equals(pageId)) {
        pageId = "CI_ENTITY_MODIFY";
    }

    if (existsMap.containsKey(pageId)) {
        RecordExistsPage existsPage = (RecordExistsPage) existsMap.get(pageId);
        HashMap buttonMap = existsPage.getButtonMap();

        Iterator iterator = buttonMap.keySet().iterator();
        Integer idx = 0;
        Record valueRecord = null;
        if (request.getAttribute("recordExistResult") != null) {
            valueRecord = (Record) request.getAttribute("recordExistResult");
        }
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            RecordExistsButton button = (RecordExistsButton) buttonMap.get(key);
            String buttonItalicsFlag = "N";
            if (valueRecord != null)
                buttonItalicsFlag = valueRecord.getStringValue(key);
			/**
             *  since introduced tab style, the actionItemId is like 'A,B' which separated by ',',
             *  A is buttonId, B is corresponding tab id.
             *  @reference applicationConfig-cs.xml
             *  ---------------------------------------------------------------------------------------
             *  for reserved tabs, as there is no button corresponding to it, A could be empty string,
             *  only B is available
             *  @reference function isReservedTab() in commonSecondlyTab.js for reserved tabs
             */
			String[] actionItemIdArr = (button.getActionItemId()).split(",");
            String actionItemId = "";
            if(pmUIStyle.equals("T")) {
                if(!"".equals(actionItemIdArr[0]) && actionItemIdArr.length == 1){
                    actionItemId = actionItemIdArr[0];
                }else if(actionItemIdArr.length > 1 && !"".equals(actionItemIdArr[1])){
                    actionItemId = actionItemIdArr[1];
                }
            }else{
                if(!"".equals(actionItemIdArr[0])){
                    actionItemId = actionItemIdArr[0];
                }
            }
%>

<script type="text/javascript">
    italicsArrayInPageLevel.push(new italicsInfo('<%=key%>', '<%=actionItemId%>', '<%=buttonItalicsFlag%>'));
</script>

<%
            idx = idx + 1;
        }
%>

<%
    }
%>
