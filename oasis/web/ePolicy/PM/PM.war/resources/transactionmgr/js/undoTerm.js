//-----------------------------------------------------------------------------
// Javascript file for undo term.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   September 16, 2010
// Author: dzhang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 08/30/2011       ryzhao      124458 - Modified undoTerm() to use formatDateForDisplay to format date parameters.
// 03/10/2014       awu         152706 - Modified processAjaxResponseForUndoTerm to use refreshWithNewPolicyTermHistory.
// 12/30/2015       kxiang      168449 - Modified processAjaxResponseForUndoTerm to add param for refreshWithNewPolicyTermHistory.
//-----------------------------------------------------------------------------

function undoTerm() {
    var paras = new Array(formatDateForDisplay(policyHeader.termEffectiveFromDate));
    if (!confirm(getMessage("pm.undoTerm.confirm.continue", paras))) {
        return false;
    }

    captureTransactionDetails("UNDOTERM", "captureTransactionDetailsDone");
}

function captureTransactionDetailsDone() {
    var path = getAppPath() + "/transactionmgr/performUndoTerm.do?"
            + commonGetMenuQueryString();
    postAjaxSubmit(path, "processUndoTerm", false, false, processAjaxResponseForUndoTerm);
}

function processAjaxResponseForUndoTerm(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data)) {
                return;
            }
            // handle confirmations
            if (isConfirmationMessagesProcessed()) {
                repostAjaxSubmitWithConfirmationValue();
            }
            // no confirmations, refresh page
            else {
                refreshWithNewPolicyTermHistory("UNDOTERM");
            }
        }
    }
}