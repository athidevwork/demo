//-----------------------------------------------------------------------------
// Javascript file
//
// (C) 2013 Delphi Technology, inc. (dti)
// Date:   July 01, 2013
// Author: adeng
//
// Revision Date    Revised By  Description
//---------------------------------------------------------------------------------------------------------------------
// 07/01/2013       adeng       117011 - Modified handleOnButtonClick() to pass value of new field
//                                       "transactionComment2" to parentWindow.handleOnCaptureTransactionDetails().
// 03/24/2017       eyin        180675 - Modified to use getReturnCtxOfDivPopUp() to get the parent window in tab style.
// 11/02/2018       clm         195889 -  Grid replacement using getParentWindow
//---------------------------------------------------------------------------------------------------------------------
var divPopup
function handleOnButtonClick(asBtn) {
    var parentWindow = getParentWindow();
    divPopup = parentWindow.getDivPopupFromDivPopupControl(this.frameElement);
    var divPopupId = divPopup.id;
    switch (asBtn) {
        case 'OK':
            var isForCaptureTransactioinDetail = getObjectValue('isForCaptureTransactionDetail');
        //for create endorsement transaction
            if (isForCaptureTransactioinDetail != 'Y') {
                setInputFormField("process", "createEndorsementTransaction");
                // commonOnSubmit("createEndorsementTransaction",false,true);
                postAjaxSubmit("/transactionmgr/endorseTransaction.do", "createEndorsementTransaction", true, false, refreshParentPage);
            }
            //for capture transaction detail
            else {
                if (isAccountingDateValid()) {
                    var declineReasonCode = "";
                    if (hasObject("declineReasonCode")) {
                        declineReasonCode = getObjectValue("declineReasonCode");
                    }
                    var effectiveFromDate = "";
                    if (hasObject("effectiveFromDate")) {
                        effectiveFromDate = getObjectValue("effectiveFromDate");
                    }
                    var convertionType = "";
                    if (hasObject("convertionType")) {
                        convertionType = getObjectValue("convertionType");
                    }
                    var transactionCode = "";
                    if (hasObject("transactionCode")) {
                        transactionCode = getObjectValue("transactionCode");
                    }

                    getReturnCtxOfDivPopUp(divPopupId).handleOnCaptureTransactionDetails(
                        getObject("accountingDate").value,
                        getObject("transactionComment").value,
                        getObject("endorsementCode").value,
                        declineReasonCode,
                        effectiveFromDate,
                        convertionType,
                        transactionCode,
                        getObject("transactionComment2").value);
                    if (divPopup) {
                        parentWindow.closeDiv(divPopup);
                    }
                }
            }
            break;
        case 'CANCEL':
            var functionExists = eval("getReturnCtxOfDivPopUp(divPopupId).processAfterCancelAutoSaveSubTab");
            if(functionExists){
                getReturnCtxOfDivPopUp(divPopupId).processAfterCancelAutoSaveSubTab();
            }
            if (divPopup) {
                parentWindow.closeDiv(divPopup);
            }
            break;
    }
}

function refreshParentPage(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }
            if (isDefined(window.frameElement)) {
                showProcessingDivPopup();
                getParentWindow().refreshPage(true);
            }
            else {
                refreshPage();
            }
        }
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
        + "&process=validateTransactionDetails" + "&date=" + new Date();

    // Fix issue 98209, we should pass the effectiveFromDate since system validates it in java.
    if (hasObject("effectiveFromDate")) {
        url += "&effectiveFromDate=" + getObjectValue("effectiveFromDate");
        url += "&termEffectiveFromDate=" + policyHeader.termEffectiveFromDate + 
               "&termEffectiveToDate=" + policyHeader.termEffectiveToDate;
    }

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
            }
            else {
                accountingDateValid = true;
            }
            handleAjaxMessages(data, null);
        }
    }
}