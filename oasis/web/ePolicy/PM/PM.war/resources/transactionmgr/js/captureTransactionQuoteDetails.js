//-----------------------------------------------------------------------------
// for capture transaction quote detail
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:
// Author:
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 07/26/2013       adeng       117011 - Modified handleOnButtonClick() to pass value of "transactionComment2" to call
//                                       parentWindow's handleOnCaptureTransactionQuoteDetails() method .
// 12/04/2018       clm         195889 - Grid Replacement using getParentWindow and closeWindow
//-----------------------------------------------------------------------------
var accountingDateValid = false;

function handleOnButtonClick(asBtn) {
    var parentWindow = getParentWindow();
    switch (asBtn) {
        case 'OK':
            if (isAccountingDateValid()) {
                var declineReasonCode = "";
                if (hasObject("declineReasonCode")) {
                    declineReasonCode = getObjectValue("declineReasonCode");
                }
                closeWindow(function() {
                    parentWindow.handleOnCaptureTransactionQuoteDetails(
                            getObject("accountingDate").value,
                            getObject("transactionComment").value,
                            getObject("endorsementCode").value,
                            declineReasonCode,
                            getObject("quoteTransactionCode").value,
                            getObject("transactionComment2").value
                    );
                });
            }
            break;

        case 'CANCEL':
            closeWindow();
            break;
    }
}

function isAccountingDateValid() {
    var accountingDate = getObjectValue("accountingDate");
    if (!isStringValue(accountingDate)) {
        var noAccountingDateMessage = getMessage("pm.transactionmgr.captureTransationDetails.noAccountingDate.error");
        handleError(noAccountingDateMessage, "accountingDate");
        return false;
    }

    var policyTermHisotryId = getObjectValue("policyTermHistoryId");
    // AJAX call to do the field validtion for accountingDate.
    url = "captureTransactionDetails.do?accountingDate=" + accountingDate
        + "&policyTermHistoryId=" + policyTermHisotryId
        + "&transactionCode=" + getObjectValue("transactionCode")
        + "&process=validateTransactionDetails"+"&date=" + new Date();

    if (hasObject("declineReasonCode") && getObjectValue("declineReasonCode") != "") {
        url += "&declineReasonCode=" + getObjectValue("declineReasonCode");
    }
    new AJAXRequest("get", url, '', verifyAccountingDateDone, false);
    return accountingDateValid;
}

function verifyAccountingDateDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            // reset field values regardless if we got messages or not
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                setFormFieldValuesByObject(oValueList[0]);
                accountingDateValid = false;
            } else {
                 accountingDateValid = true;
            }
            handleAjaxMessages(data, null);
        }
    }
}