//-----------------------------------------------------------------------------
// JavaScript file for calculate dividend.
//
// (C) 2011 Delphi Technology, inc. (dti)
// Date:    Mar 30, 2011
// Author:  wfu
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
//
//-----------------------------------------------------------------------------

function handleOnButtonClick(btn) {
    switch (btn) {
        case 'PROCESS':
            var url = getAppPath() + "/policymgr/dividendmgr/calculateDividend.do";
            var process = "validateCalculateDividend";
            postAjaxSubmit(url, process, true, false, handleOnValidateCalculateDividend, false);
            break;
        default:break;
    }
}

function handleOnValidateCalculateDividend(ajax){
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }

            if (confirm(getMessage("pm.dividend.calculate.process.confirm"))) {
                // To calculate dividend
                var url = getAppPath() + "/policymgr/dividendmgr/calculateDividend.do";
                var process = "calculateDividend";
                postAjaxSubmit(url, process, true, false, handleOnCalculateDividend, false);
            }
        }
    }
}

function handleOnCalculateDividend(ajax){
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }

            if (isDefined(window.frameElement)) {
                window.frameElement.document.parentWindow.setInputFormField("policyType", getObjectValue("policyType"));
                window.frameElement.document.parentWindow.document.forms[0].process.value = "loadAllPriorDividend";
                window.frameElement.document.parentWindow.submitFirstForm();
                window.frameElement.document.parentWindow.showProcessingImgIndicator();
            }
            commonOnButtonClick('CLOSE_RO_DIV');
        }
    }
}