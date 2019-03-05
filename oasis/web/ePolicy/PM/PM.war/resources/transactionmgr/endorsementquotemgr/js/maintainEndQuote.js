//-----------------------------------------------------------------------------
// for maintain Endorsement quote, including apply/delete/copy endorsement quote.
//
// (C) 2013 Delphi Technology, inc. (dti)
// Date:   September 06, 2013
// Author: adeng
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 09/06/2013       adeng       147468 - Modified processAjaxResponseForCopyEndorsementQuote
//                                       to reset risk id to empty to reload primary risk's
//                                       coverage after copying EndQuote in coverage tab. This
//                                       will handle the case if new risk and coverage are added
//                                       and after copying, new risk base record id is generated,
//                                       no coverage list can be loaded after refreshing page if
//                                       we still use the old risk id to load coverage.
// 07/25/2014       awu         152034 - Modified to send 'ALL' to refreshPage after Copy/Delete/Apply quote.
// 10/09/2014       wdang       156038 - Removed the logic of emptying field riskId in processAjaxResponseForCopyEndorsementQuote().
//-----------------------------------------------------------------------------

function deleteEndQuoteTransaction() {
    // if (!confirm("Are you sure you wish to delete endorsement/renewal quote?")) {

    var params = new Array(getQuoteType());
    if (!confirm(getMessage("pm.deleteEndQuote.confirmed.warning", params))) {
        return;
    }
    var path = getAppPath() + "/transactionmgr/maintainEndorsementQuote.do?"
        + commonGetMenuQueryString();
    postAjaxSubmit(path, "deleteEndQuote", false, false, processAjaxResponseForDeleteEndQuoteTransaction);
}

function processAjaxResponseForDeleteEndQuoteTransaction(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data)) {
                return;
            }            // handle confirmations
            if (isConfirmationMessagesProcessed()) {
                repostAjaxSubmitWithConfirmationValue();
            }
            // no confirmations, refresh page
            else {
                refreshPage("ALL");
            }
        }
    }
}

function applyEndorsementQuote() {
    // if (!confirm("Are you sure you wish to apply endorsement/renewal quote?")) {
    var params = new Array(getQuoteType());
    if (!confirm(getMessage("pm.applyEndQuote.confirmed.warning", params))) {
        return;
    }
    var path = getAppPath() + "/transactionmgr/maintainEndorsementQuote.do?"
        + commonGetMenuQueryString();
    postAjaxSubmit(path, "applyEndQuote", false, false, processAjaxResponseForApplyEndorsementQuote);
}

function processAjaxResponseForApplyEndorsementQuote(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data)) {
                return;
            }            // handle confirmations
            if (isConfirmationMessagesProcessed()) {
                repostAjaxSubmitWithConfirmationValue();
            }
            // no confirmations, refresh page
            else {
                refreshPage("ALL");
            }
        }
    }
}

function copyEndorsementQuote() {
    captureTransactionDetails("ENDORSE", "submitCopyEndorsementQuote");
}

function submitCopyEndorsementQuote() {
    var path = getAppPath() + "/transactionmgr/maintainEndorsementQuote.do?"
        + commonGetMenuQueryString();
    postAjaxSubmit(path, "copyEndQuote", false, false, processAjaxResponseForCopyEndorsementQuote);
}

function getQuoteType() {
    var quoteType = "endorsement" ;
    var transactionTypeCode = policyHeader.lastTransactionInfo.transactionTypeCode;
    if (transactionTypeCode == 'RENEWAL') {
        quoteType = "renewal";
    }    
    return quoteType;

}
function processAjaxResponseForCopyEndorsementQuote(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }
            // handle confirmations
            if (isConfirmationMessagesProcessed()) {
                repostAjaxSubmitWithConfirmationValue();
            }
            // no confirmations, we're done
            else {
                refreshPage("ALL");
            }
        }
    }
}