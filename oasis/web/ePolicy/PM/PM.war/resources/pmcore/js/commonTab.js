//-----------------------------------------------------------------------------
// Common tab javascript file.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   July 06, 2016
// Author: tzeng
//
// Revision Date    Revised By  Description
// 07/06/2016       tzeng       167531 - Initial version, Javascript for common tabs in policy system.
// 08/05/2016       tzeng       177134 - Show message after save official/pre-renewal successfully.
// 08/26/2016       wdang       167534 - Added support for Renewal Quote.
//-----------------------------------------------------------------------------
function handleConfirmations() {
    var process;
    if (getMessage("pm.batchRenewalProcess.existInBatch.afterSaveOfficial.info") &&
        getObjectValue("policyViewMode") == "OFFICIAL") {
        alert(getMessage("pm.batchRenewalProcess.existInBatch.afterSaveOfficial.info"));
    }
    else if (getMessage("pm.batchRenewalProcess.processAutoRenewal.manually.phaseNotQualify.info") &&
             getObjectValue("policyViewMode") == "WIP") {
        alert(getMessage("pm.batchRenewalProcess.processAutoRenewal.manually.phaseNotQualify.info"));
    }
    else if (getMessage("pm.batchRenewalProcess.addToBatch.qualify.afterAutoRenew.prompt") &&
             getObjectValue("policyViewMode") == "WIP") {
        if (confirm(getMessage("pm.batchRenewalProcess.addToBatch.qualify.afterAutoRenew.prompt"))) {
            process = "addPolicyToBatch";
        }
        else {
            process = "excludePolicyFromBatch";
            if(getMessage("pm.batchRenewalProcess.processAutoRenewal.manually.info")) {
                alert(getMessage("pm.batchRenewalProcess.processAutoRenewal.manually.info"));
            }
        }
    }
    else if (getMessage("pm.batchRenewalProcess.addToBatch.notQualify.afterAutoRenew.prompt") &&
             getObjectValue("policyViewMode") == "WIP") {
        if (confirm(getMessage("pm.batchRenewalProcess.addToBatch.notQualify.afterAutoRenew.prompt"))) {
            process = "addPolicyToBatch";
        }
        else if (getMessage("pm.batchRenewalProcess.processAutoRenewal.manually.info")){
            alert(getMessage("pm.batchRenewalProcess.processAutoRenewal.manually.info"));
        }
    }
    else if (getMessage("pm.maintainQuoteTransfer.autoPendingRenewal")) {
        process = "mergePendingRenewal";
    }
    else if (getMessage("pm.maintainQuoteTransfer.autoOpenWindow")) {
        process = "loadQuoteTransfer";
    }

    var url = getAppPath();
    switch (process) {
        case 'addPolicyToBatch':
        case 'excludePolicyFromBatch':
            url = url + "/transactionmgr/batchrenewalprocessmgr/maintainBatchRenewalProcess.do?"
            + commonGetMenuQueryString() + "&process=" + process;
            new AJAXRequest("post", url, "", null, true);
            break;
        case 'mergePendingRenewal':
            url = url + "/quotemgr/maintainQuoteTransfer.do?"
            + commonGetMenuQueryString() + "&process=performMerge";
            new AJAXRequest("post", url, "", commonHandleOnPostAjaxSubmitDone, true);
            break;
        case 'loadQuoteTransfer':
            maintainQuoteTransfer();
            break;
    }
}
