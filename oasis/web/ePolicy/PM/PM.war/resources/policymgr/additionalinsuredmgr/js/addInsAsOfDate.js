//-----------------------------------------------------------------------------
// Java script file for addInsAsOfDate.jsp.
//
// (C) 2013 Delphi Technology, inc. (dti)
// Date:   Feb 28, 2013
// Author: xnie
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 02/28/2013       xnie        138026 - Initial version.
// 03/10/2017       wli         180675 - Changed to "getReturnCtxOfDivPopUp()" when
//                                       call parent page's handleOnCaptureAsOfDate function.
// 10/18/2018      xgong        195889 - Updated handleOnValidateAsOfDate for grid replacement
//-----------------------------------------------------------------------------
function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'SAVE_ADDINS_DATE':
            sendAJAXRequest("validateAsOfDate");
            break;
    }
}

function sendAJAXRequest(process) {
    // set url
    var url = "captureAddInsAsOfDate.do?process=" + process +
              "&" + commonGetMenuQueryString("PM_ADDINS_AS_OF_DATE", "");

    switch (process) {
        case 'validateAsOfDate':
            url += "&addInsAsOfDate=" + getObjectValue("addInsAsOfDate");
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

            // call parent page's handleOnCaptureAsOfDate function
           closeWindow(function(){
               getReturnCtxOfDivPopUp().handleOnCaptureAsOfDate(getObjectValue("addInsAsOfDate"));
           });
        }
    }
}