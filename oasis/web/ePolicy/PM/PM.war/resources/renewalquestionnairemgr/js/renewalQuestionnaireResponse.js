//-----------------------------------------------------------------------------
// JavaScript file for risk summary.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:
// Author:
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 09/13/2010       wfu         111776: Replaced hardcode string with resource definition
// 01/14/2011       ryzhao      116590: Set responseURL to null before doing search operation.
//                                      Set page entitlement indField to initial value "N" before doing search operation.
// 07/12/2017       lzhang      186847: Reflect grid replacement project changes
//-----------------------------------------------------------------------------
var idDataChanged = false;
function handleOnButtonClick(action) {
    switch (action) {
        case 'SEARCH':
            commonOnSubmit(action);
            break;
        case 'SAVEDATE':
            handleOnSubmit(action);
            break;
        case 'SAVE':
            var saveUrl = getAppPath() + "/renewalquestionnairemgr/renewalQuestionnaireResponseSave.do?"
                + commonGetMenuQueryString() + "&process=displaySaveAsOption";
            var divPopupId = openDivPopup("", saveUrl, true, true, "", "", 400, 300, "", "", "", false);
            break;
        case 'REOPEN':
            if (!isEmpty(getObject("policyId").value)) {
                handleOnSubmit(action);
            }
            break;
        case 'CLOSE':
            var url = getAppPath() + "/renewalquestionnairemgr/renewalQuestionnaireResponse.do?"
                + "process=getDataChanged" + "&webAppHeaderId=" + getObjectValue("webAppHeaderId");
            new AJAXRequest("get", url, '', handleOnGetDataChanged, false);
            break;
        default:break;
    }
}
function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'SEARCH':
            setInputFormField("searchDate", 'Y');
            document.forms[0].process.value = "loadAllQuestionnaireResponse";
            // issue 116590: If the former risk has questionnaire information but the latter risk has no questionnaire information,
            // the iframeResponse will still show the questionnaire information of the former risk.
            // Here we set the responseURL to null to fix the bug.
            if(document.all.responseURL){
                document.all.responseURL.value = "";
            }
            // Set page entitlement indField to initial value "N".
            if(document.all.isResponseReopenAvailable){
                document.all.isResponseReopenAvailable.value = "N";
            }
            if(document.all.isResponseSaveAvailable){
                document.all.isResponseSaveAvailable.value = "N";
            }
            submitFirstForm();
            break;
        case 'SAVEDATE':
            var url = "/renewalquestionnairemgr/renewalQuestionnaireResponse.do?";
            postAjaxSubmit(url, "saveResponseDate", false, false, handleOnGetInitialValues);
            break;
        case 'SAVE':
            document.forms[0].process.value = "saveResponses";
            submitFirstForm();
            showProcessingDivPopup();
            break;
        case 'REOPEN':
            document.forms[0].process.value = "reopenRenewQuestionnaireResponse";
            submitFirstForm();
            break;
        default : proceed = false;
    }
    return proceed;
}
function handleOnLoad() {
    if (getObject("iframeResponse")) {
        if (!isEmpty(getObject("responseURL").value)) {
            getObject("iframeResponse").src = getObject("responseURL").value;
        }
        else {
            getObject("iframeResponse").height = "20px";
        }
    }
}
// When the iframe on load, system hides the div.
function iframeOnLoad() {
    var buttonGroups = iframeResponse.$(".horizontalButtonCollection");
    if (buttonGroups.length > 0) {
        hideShowElementByClassName(buttonGroups[0], true);
    }
}
function getSaveAsOption(option) {
    if (!isEmpty(option)) {
        setInputFormField("appStatus", option);
        handleOnSubmit('SAVE');
    }
}
function handleOnGetInitialValues(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data)) {
                return;
            }
            var oValueList = parseXML(data);
            if (oValueList.length > 0 && oValueList[0]["saveDateSuccess"] == 'Y') {
                alert(getMessage("pm.renewalQuestionnaireResponse.saveDate.success"));
            }
        }
    }
}
function handleOnGetDataChanged(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data)) {
                return;
            }
            var oValueList = parseXML(data);
            if (oValueList.length > 0 && oValueList[0]["dataChanged"] == 'Y') {
                if (confirm(getMessage("pm.renewalQuestionnaireMailingEvent.save.changed"))) {
                    idDataChanged = true;
                    handleOnButtonClick('SAVE');
                    return;
                }
            }
            idDataChanged = false;
            if (getObject('comeFromMailingEvent').value == 'Y') {
                commonOnButtonClick('CLOSE_RO_DIV');
            }
        }
    }
}
//-----------------------------------------------------------------------------
// If the underwriter wants to leave the current page, system will change whether the data has been changed.
// If the Ajax return value is false, system will change whether the page data is changed.
//-----------------------------------------------------------------------------
function commonIsOkToChangePages() {
    if (getObject("webAppHeaderId")) {
        handleOnButtonClick('CLOSE');
    }
    if (!idDataChanged) {
        if (isPageDataChanged()) {
            if (!confirm(getMessage("pm.common.clickOk.changesLost.confirm"))) {
                return false;
            }
        }
        return true;
    }
    return !idDataChanged;
}
//-----------------------------------------------------------------------------
// The handleOnKeyDown function will submit the form automatically,
// so system returns false to prevent submitting when the underwriter enters 'Enter' key.
//-----------------------------------------------------------------------------
function handleOnKeyDown(field) {
    var evt = window.event ;
    var code = evt.keyCode;
    if (code == 13) {
        return false;
    }
    return true;
}