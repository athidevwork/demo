var divPopup;

function handleOnButtonClick(asBtn) {
    divPopup = window.frameElement.document.parentWindow.getDivPopupFromDivPopupControl(this.frameElement);
    switch (asBtn) {
        case 'SAVE_REN_BAT_PRINT':
            if (getObjectValue("printDevice") != '') {
                sendAJAXRequest("saveSubmitPrintingJob");
            }
            else {
                handleError(getMessage("pm.batchRenewalProcess.print.noSelection.error"));
            }
            break;
        case 'CANCEL':
            commonOnButtonClick('CLOSE_RO_DIV');
            break;
    }
}

function sendAJAXRequest(process) {
    // set url
    var url = "captureRenewalBatchPrinter.do?process=" + process +
              "&" + commonGetMenuQueryString("", "");
    
    switch (process) {
        case 'saveSubmitPrintingJob':
            url += "&renewalEventId=" + getObjectValue("renewalEventId");
            url += "&printDevice=" + getObjectValue("printDevice");
            break;
    }

    // initiate call
    var ajaxResponseHandler = "handleOn" + process.substr(0, 1).toUpperCase() + process.substr(1);
    new AJAXRequest("get", url, "", eval(ajaxResponseHandler), false);
}

function handleOnSaveSubmitPrintingJob(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null))
                return;

            // need to reload the parent page
            window.frameElement.document.parentWindow.handleOnButtonClick("SEARCH");
            if (divPopup) {
                window.frameElement.document.parentWindow.closeDiv(divPopup);
            }
        }
    }
}