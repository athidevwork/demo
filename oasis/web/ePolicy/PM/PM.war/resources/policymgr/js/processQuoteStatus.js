//-------------------------------------------------------------------------------------------------------------
// Javascript file for processQuoteStatus.jsp.
//
// (C) 2015 Delphi Technology, inc. (dti)
// Date:   Sep 12, 2017
// Author: tzeng
//
// Revision Date    Revised By  Description
//-------------------------------------------------------------------------------------------------------------
// 09/12/2017       tzeng       188041 - Modified handleOnButtonClick() to remove the wrong extra parentheses.
// 08/17/2018       wrong       195160 - Add function handleOnChange to make isChanged set true only in "status" field
//                                       selected with some value.
//-------------------------------------------------------------------------------------------------------------
function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'SAVE':
            document.forms[0].process.value = "saveQuoteStatus";
            break;
        default : proceed = false;
    }
    return proceed;
}

function handleConfirmations() {
    if (document.forms[0].hasMessage.value == 'Y') {
        if (confirm(getMessage("pm.processQuoteStatus.same.status"))) {
            setInputFormField("pm.processQuoteStatus.same.status.confirmed", "Y");
            commonOnSubmit('SAVE', true, true, true);
            return true;
        }
    }
    return false;
}

function handleOnLoad(){
    if (hasObject("isTriggerForms") && getObjectValue("isTriggerForms") == "Y") {
        eventHandler = "submitTriggerForms";
        var path = getAppPath() + "/transactionmgr/captureTransactionDetails.do?process=display"
                 + "&policyNo=" + policyHeader.policyNo
                 + "&wipNo=" + getObjectValue("wipNo")
                 + "&offNo=" + getObjectValue("offNo");
        var divPopupId = openDivPopup("", path, true, true, "", "", "", "", "", "", "", false);
    }
}

function submitTriggerForms(){
    var url = getAppPath() + "/policymgr/processQuoteStatus.do";
    postAjaxSubmit(url, "triggerForms", false, false, handleOnSubmitTriggerForms_Ajax);
}

function handleOnSubmitTriggerForms_Ajax(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null))
                return;
        }
    }
}

function handleOnButtonClick(btn) {
    switch (btn) {
        case "CLOSE":
            if (isNeedToRefreshParentB()) {
                window.frameElement.document.parentWindow.refreshPage();
            }
            commonOnButtonClick('CLOSE_RO_DIV');
            break;
        default:break;
    }
}

function isNeedToRefreshParentB(){
    if (isDefined(window.frameElement) && getObjectValue("isTriggerForms") == "Y") {
        return true;
    }else{
        return false;
    }
}

function handleOnChange(obj) {
    if (isEmpty(obj.value)) {
        isChanged = false;
    }
}