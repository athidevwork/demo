//-----------------------------------------------------------------------------
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
//07/25/2014        awu         152034 - Modified handleOnCancelFlatDone to send 'ALL' to refreshPage.
//-----------------------------------------------------------------------------

function flatCancelPolicy() {
    captureTransactionDetails("CANCEL", "performCancelFlat");
}

function performCancelFlat() {
    postAjaxSubmit("/transactionmgr/cancelprocessmgr/flatCancelPolicy.do", "flatCancelPolicy", false, false, handleOnCancelFlatDone);
}

function handleOnCancelFlatDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }
            refreshPage("ALL");
        }
    }
}