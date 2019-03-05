//-----------------------------------------------------------------------------
// Javascript file
//
// (C) 2008 Delphi Technology, inc. (dti)
// Date:   Jan 31, 2008
// Author: yhchen
//
// Revision Date    Revised By  Description
//---------------------------------------------------------------------------------------------------------------------
// 01/31/2008       yhchen      78352 - Initial version.
// 03/14/2013       xnie        142699 - Removed unnecessary logic of handling error which is existed in action.
// 03/14/2013       tcheng      142196 - Renamed handleExitWorkflow to handleExitWorkflowForQuote for copy to quote.
// 05/03/2013       tcheng      144379 - Modified submitCopyToQuote() to add a parameter for forwarding to quote page.
// 07/01/2013       adeng       117011 - Modified submitCopyToQuote() to set value of new field
//                                       "transactionComment2" to input form field "transactionComment2".
// 08/26/2016       wdang       167534 - Added support for Renewal Quote.
//---------------------------------------------------------------------------------------------------------------------
function copyToQuote(quoteCycleCode) {
    setInputFormField("quoteCycleCode", quoteCycleCode ? quoteCycleCode : "NB");
    captureTransactionDetails("QUOTE", "submitCopyToQuote");
}

function submitCopyToQuote() {
    var quoteCycleCode = "NB";
    if (hasObject("quoteCycleCode")){
        quoteCycleCode = getObjectValue("quoteCycleCode");
    }
    if (quoteCycleCode == "NB") {
        setInputFormField("needToHandleExitWorkFlow", "Y");
        setInputFormField("needToForwardQuote", "Y");
        if (objectComment2) {
            setInputFormField("transactionComment2", objectComment2.value);
        }
        postAjaxSubmit("/policymgr/maintainPolicy.do", "copyPolicyToQuote", false, false, handleOnCopyToQuoteDone);
    }
    else {
        postAjaxSubmit("/quotemgr/maintainQuoteTransfer.do", "performCopy", false, false, handleOnCopyToQuoteDone);
    }
}

function handleOnCopyToQuoteDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            var parallelPolNo;
            var saveAsOfficial;
            if (!handleAjaxMessages(data, null)) {
                return;
            }

            // handle confirmations
            if (isConfirmationMessagesProcessed()) {
                repostAjaxSubmitWithConfirmationValue();
            }
            // no confirmations, we're done
            else {
                /* Parse xml and get inital values(s) */
                var oValue = parseXML(data);

                /* Set default value which fieldID is match with object's attribute name */
                if (oValue.length > 0) {
                    if (!isEmpty(oValue[0]["PARALLELPOLNO"])) {
                        parallelPolNo = oValue[0]["PARALLELPOLNO"];
                    }
                    else {
                        return;
                    }
                    if (!isEmpty(oValue[0]["SAVEASOFFICIAL"])) {
                        saveAsOfficial = oValue[0]["SAVEASOFFICIAL"];
                    }
                    else {
                        saveAsOfficial = "N";
                    }
                }
                if (saveAsOfficial == "Y") {
                    invokeSaveQuoteOfficialWorkflow(parallelPolNo);
                }
                else {
                    handleExitWorkflowForQuote(parallelPolNo);
                }
            }
        }
    }
}


function invokeSaveQuoteOfficialWorkflow(quoteNo) {
    var url = getAppPath() + "/workflowmgr/workflow.do?" +
              "policyNo=" + quoteNo +
              "&workflowState=invokeRateNotifyAndSaveAsOfficialDetail";

    var processingDivId = openDivPopup("", url, true, true, "", "", "", "", "", "", "", false);

    return true;
}

function handleExitWorkflowForQuote(quoteNo) {
    var url = getAppPath() + "/policymgr/maintainPolicy.do?policyNo=" + quoteNo
    setWindowLocation(url);
}