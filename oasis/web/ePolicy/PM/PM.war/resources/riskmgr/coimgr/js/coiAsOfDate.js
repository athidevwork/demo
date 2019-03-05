var parentWindow = getParentWindow();

function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'SAVE_COI_DATE':
            sendAJAXRequest("validateAsOfDate");
            break;
    }
}

function sendAJAXRequest(process) {
    // set url
    var url = "captureCoiAsOfDate.do?process=" + process +
              "&" + commonGetMenuQueryString("PM_COI_AS_OF_DATE", "");

    switch (process) {
        case 'validateAsOfDate':
            url += "&coiAsOfDate=" + getObjectValue("coiAsOfDate");
            break;
    }

    // initiate call
    var ajaxResponseHandler = "handleOn" + process.substr(0, 1).toUpperCase() + process.substr(1);
    new AJAXRequest("get", url, "", eval(ajaxResponseHandler), false);
}

function handleOnValidateAsOfDate(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null))
                return;
            closeWindow(function () {
               // call parent page's handleOnCaptureAsOfDate function
                getReturnCtxOfDivPopUp().handleOnCaptureAsOfDate(getObjectValue("coiAsOfDate"));
            });
        }
    }
}