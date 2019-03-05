//-----------------------------------------------------------------------------
// Javascript file for loadSaveOptions.jsp.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   April 27, 2010
// Author: syang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 04/27/2010       syang       106470 - Set parent variable eventHandler null when click Cancel button.
// 10/13/2015       tzeng       164679 - Modified handleOnButtonClick to add save WIP indicator to display risk relation
//                                       result message after save WIP at popup window.
// 01/28/2016       wdang       169024 - Reverted changes of 164679.
// 03/10/2017       wli         180675 - Added auto-save logic for new UI tab style.
// 11/09/2017       tzeng       187689 - Modified processAutoSaveIframe() and remove autoSaveSubTab() to support
//                                       processAutoSaveSubTab();
// 11/02/2018       clm         195889 -  Grid replacement using getParentWindow and closeWindow
// 12/05/2018        xjli        195889 - Reflect grid replacement project changes.
//-----------------------------------------------------------------------------

var parentWindow = getParentWindow();
function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'OK':
            if(getUIStyle() == "T") {
                if(!parentWindow.isReservedTab(parentWindow.getCurrentTab())) {
                    processAutoSaveIframe();
                    return;
                }
            }
        // If user choose save as offical, make ajax call to check if there need comfirmation here.
            if (getObjectValue("saveAsCode") == "OFFICIAL") {
                // Get policyId from parent page
                var transactionId = "";
                if (parentWindow.policyHeader) {
                    transactionId = parentWindow.policyHeader.lastTransactionId;
                }
                if (!isEmpty(transactionId)) {
                    var url = "loadSaveOptions.do?transactionId=" + transactionId + commonGetMenuQueryString() + "&process=isSourcePolicyInWip";
                    // initiate async call
                    new AJAXRequest("get", url, '', checkSourcePolicyWipDone, false);
                }
            }
            else {
                closeWindow(function () {
                    parentWindow.handleSaveOptionSelection(getObjectValue("saveAsCode"));
                });
            }

            break;

        case 'CANCEL':
            closeWindow(function () {
                //if its parent window is reRatePolicy,it should close that div the same time
                if (parentWindow.cloveReRateDivSelf) {
                    parentWindow.cloveReRateDivSelf();
                }
                parentWindow.eventHandler = null;
            });
            break;
    }
}

function checkSourcePolicyWipDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;

            if (!handleAjaxMessages(data, null)) {
                return;
            }
            var oValueList = parseXML(data);

            if (oValueList.length > 0) {
                var wipB = oValueList[0]["RETURNVALUE"];
                if (wipB == "Y") {
                    if (!confirm(getMessage("pm.amalgamation.saveAsOfficialConfirm.info"))) {
                        // Cancel current action if user chooses "No"
                        return;
                    }
                }

                // continue save process
                closeWindow(function () {
                    parentWindow.handleSaveOptionSelection(getObjectValue("saveAsCode"));
                });
            }
        }
    }
}

function processAutoSaveIframe() {
    setSaveOption();
    closeWindow(function () {
        parentWindow.processAutoSaveSubTab(parentWindow.getCurrentTab());
        if (parentWindow.getCurrentTab() == "COPYALL" || parentWindow.getCurrentTab() == "DELETEALL") {
            if (parentWindow.autoSaveResultType == commonOnSubmitReturnTypes.submitSuccessfullyWithPopup) {
                parentWindow.setBtnOperation(parentWindow.operation);
                return;
            }
            else if (parentWindow.autoSaveResultType == commonOnSubmitReturnTypes.commonValidationFailed) {
                if (parentWindow.getIFrameWindow().hasValidationErrorForAllRisk || !parentWindow.getIFrameWindow().isNeedToRefreshParentB()) {
                    return;
                }
            }
        }
        else if (parentWindow.autoSaveResultType != commonOnSubmitReturnTypes.noDataChange) {
            return;
        }
        parentWindow.processMainPageAfterAutoSaveSubTab();
    });

}

function setSaveOption() {
    parentWindow.operation = getObjectValue("saveAsCode");
}