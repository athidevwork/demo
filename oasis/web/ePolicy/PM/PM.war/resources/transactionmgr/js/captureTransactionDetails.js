//-----------------------------------------------------------------------------
// for capture transaction detail page
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:
// Author:
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 06/21/2013       adeng       117011 - Modified handleOnButtonClick() to pass value of new field
//                                       "transactionComment2" to handleOnCaptureTransactionDetails().
// 03/10/2017       wrong       180675 - Modified to use getReturnCtxOfDivPopUp function to get parent window for tab style.
// 11/02/2018       clm         195889 -  Grid replacement using getParentWindow and closeWindow
//-----------------------------------------------------------------------------
var accountingDateValid = false;

function handleOnButtonClick(asBtn) {
    var parentWindow = getParentWindow();
    var divPopup = parentWindow.getDivPopupFromDivPopupControl(this.frameElement);
    var divPopupId = divPopup.id;
    switch (asBtn) {
        case 'OK':
            if (isAccountingDateValid()) {              
                var declineReasonCode = "";
                if (hasObject("declineReasonCode")) {
                    declineReasonCode = getObjectValue("declineReasonCode");
                }
                closeWindow(function() {
                    getReturnCtxOfDivPopUp(divPopupId).handleOnCaptureTransactionDetails(
                            getObject("accountingDate").value,
                            getObject("transactionComment").value,
                            getObject("endorsementCode").value,
                            declineReasonCode,
                            '',
                            '',
                            '',
                            getObject("transactionComment2").value);
                });
            }
            break;

        case 'CANCEL':
            closeWindow(function() {
                var functionExists = eval("getReturnCtxOfDivPopUp(divPopupId).processAfterCancelAutoSaveSubTab");
                if (functionExists) {
                    getReturnCtxOfDivPopUp(divPopupId).processAfterCancelAutoSaveSubTab();
                }
            });
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