//-----------------------------------------------------------------------------
// Javascript file for accept/decline short term policy.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   Jun 23, 2011
// Author: ryzhao
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
//
//-----------------------------------------------------------------------------
function acceptShortTermPolicy() {
    captureTransactionDetails("ACCEPTPOL", "createAcceptPolicyTransaction");
}

function createAcceptPolicyTransaction() {
    // It posts ajax submit to ShortTermPolicyAction.createAcceptPolicyTransaction() to create transaction.
    var url = getAppPath() + "/transactionmgr/shorttermpolicymgr/shortTermPolicyAction.do?" + commonGetMenuQueryString();
    postAjaxSubmit(url, "createAcceptPolicyTransaction", false, false, handleOnCreateAcceptPolicyTransactionDone);
}

function handleOnCreateAcceptPolicyTransactionDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }
            // Refresh the page to start accept short term policy workflow
            refreshPage();
        }
    }
}

function declineShortTermPolicy() {
    captureTransactionDetails("DECLINEPOL", "declinePolicy");
}

function declinePolicy() {
    var url = getAppPath() + "/transactionmgr/shorttermpolicymgr/shortTermPolicyAction.do?" + commonGetMenuQueryString();
    postAjaxSubmit(url, "performDeclinePolicy", false, false, handleOnDeclinePolicyDone);
}

function handleOnDeclinePolicyDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }
            // Refresh the page to start save official workflow
            refreshPage();
        }
    }
}
