//-----------------------------------------------------------------------------
// for renew policy
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:
// Author:
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 06/21/2013       adeng       117011 - Modified submitRenewal() to set value of new field "transactionComment2"
//                              to input form field "transactionComment2" if it exists.
// 10/18/2016       lzhang      180263 - Modified validateAutoRenewalDone(), add performAutoRenewal(),
//                              checkPRTConfirmationRequired() and checkPRTConfirmationRequiredDone():
//                              add check PRT Confirmation after validate AutoRenewal
//-----------------------------------------------------------------------------
var renewTermDivId;

function renewPolicy() {
    var url = getAppPath() + "/transactionmgr/renewalprocessmgr/renewPolicy.do?"
        + commonGetMenuQueryString() + "&process=getRenewalParms" ;
    new AJAXRequest("get", url, '', loadRenewalParmsDone, false);
}

function loadRenewalParmsDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            // set neddCofirmPrt and renexpdtConfiged parameters
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null) && !processedConfirmationMessages) {
                return;
            }

            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                setFormFieldValuesByObject(oValueList[0]);
            }

            //handle confirm apply PRT
            if (data.documentElement != null && processedConfirmationMessages) {
                handleApplyPRTConfirmation();
            }
            else {
                captureTransactionDetails("MANRENEW", "captureRenewalTermExpiration");
            }
        }
    }
}

function handleApplyPRTConfirmation() {
    if (getConfirmationResponse("pm.maintainRenewal.confirm.applyPRT") == "Y") {
        submitRenewal();
    }
    else {
        captureTransactionDetails("MANRENEW", "captureRenewalTermExpiration");
    }
}

function captureRenewalTermExpiration() {
    if (getObjectValue("RENEXPDTCONFIGED") == 'Y') {
        var url = getAppPath() + "/transactionmgr/renewalprocessmgr/renewPolicy.do?"
            + commonGetMenuQueryString() + "&process=captureRenewalTermExpiration";
        renewTermDivId = openDivPopup("Renewal Term Expiration", url, true, true, 50, 300, 515, 460, 450, 375, "", false);
    }
    else {
        submitRenewal();
    }
}

function submitRenewal() {
    if(objectComment2){
        setInputFormField("transactionComment2", objectComment2.value);
    }
    var url = getAppPath() + "/transactionmgr/renewalprocessmgr/renewPolicy.do";
    postAjaxSubmit(url, "renewPolicy", false, false, handleOnRenewalDone, true);
}

function handleOnRenewalDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null) && !processedConfirmationMessages) {
                return;
            }
            //if no confirmation for PRT, need to handle this again
            if (data.documentElement != null && processedConfirmationMessages) {
                handleApplyPRTConfirmation();
                return;
            }
            refreshPage();
        }
    }
}

function handleOnRenewalTermExpiration(termEffDate, termExpDate) {

    // insert fields dyanamically to the caller's form
    // This function is called by  clicking the button OK from captureRenewalTermExpiration.handleOnButtonClick
    // or after ajax call,
    objectTermEff = setInputFormField("renewalTermEffDate", termEffDate);
    objectTermExp = setInputFormField("renewalTermExpDate", termExpDate);

    submitRenewal();
}

//-----------------------------------------------------------------------------
// Below functions are for auto renew
//-----------------------------------------------------------------------------
function autoRenewPolicy() {
    // The first step is do valdiations
    var url = getAppPath() + "/transactionmgr/renewalprocessmgr/renewPolicy.do?"
        + commonGetMenuQueryString() + "&process=validateAutoRenewal" ;
    new AJAXRequest("get", url, '', validateAutoRenewalDone, false);
}

function validateAutoRenewalDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }
            // Validation pass, ask user to confirm auto renew action.
            if (confirm(getMessage("pm.autoRenewal.confirmation.info"))) {
                checkPRTConfirmationRequired();
            }
        }
    }
}

function performAutoRenewal() {
    // Perform auto renewal
    var url = getAppPath() + "/transactionmgr/renewalprocessmgr/renewPolicy.do?"
            + commonGetMenuQueryString() + "&process=performAutoRenewal" ;
    postAjaxSubmit(url, "performAutoRenewal", false, false, performAutoRenewalDone, true);
}

function performAutoRenewalDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }
            // Refresh page if action succeeds
            refreshPage();
        }
    }
}

function checkPRTConfirmationRequired() {
    // check PRT confirmation required
    var url = getAppPath() + "/transactionmgr/renewalprocessmgr/renewPolicy.do?"
            + commonGetMenuQueryString() + "&process=checkPRTConfirmationRequired" ;
    new AJAXRequest("get", url, '', checkPRTConfirmationRequiredDone, false);
}

function checkPRTConfirmationRequiredDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            // set neddCofirmPrt and renexpdtConfiged parameters
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null) && !processedConfirmationMessages) {
                return;
            }

            performAutoRenewal();
        }
    }
}