//-----------------------------------------------------------------------------
// Javascript file for createPolicy.jsp.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   July 27, 2010
// Author: syang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 07/27/2010       syang       110147 - Modified handleOnChange() to make sure the termEffectiveFrom/ToDate is valid before call ajax.
// 01/19/2011       wfu         113566 - Added logic to handle copying policy from risk.
// 04/06/2011       fcb         119324 - handleOnCreatePolicy: isNewPolicyCreated added.
// 11/11/2016       eyin        181257 - 1) Modified handleOnButtonClick('Create'), make the lock refresh feature
//                                          disabled from the parent screen.
//                                       2) Modified closeThis(), if user just click Cancel to undo the create, enable
//                                          the lock refresh feature.
// 12/27/2017       dzhang      190568 - Grid replacement
// 03/19/2018       cesar       189605 - call dti.csrf.setupCSRFTokenForURL() to add token.
// 07/11/2018       cesar       193446 - modified handleOnCreatePolicy() to set iframe src to blank by calling dti.divpopuputils.clearIFrameSrc().
// 11/09/2018       wrong       194062 - Modified createPolicyListGrid_selectRow() to set policy type for layer field.
//-----------------------------------------------------------------------------
function handleOnChange(field) {
    var fieldName = field.name;
    if (((fieldName == "termEffectiveFromDate" || fieldName == "termEffectiveToDate") && datemaskclear()) ||
        fieldName == "accountingDate" || fieldName == "issueCompanyEntityId" ||
        fieldName == "issueStateCode" || fieldName ==  "practiceStateCode") {
        sendAJAXRequest(fieldName);
    }
}

function sendAJAXRequest(fieldName) {
    // set flag
    if (fieldName == "termEffectiveToDate")
        document.forms[0].isTermEffectiveToDateChanged.value = "Y";

    // set url
    var url = "createPolicy.do?process=validateFields&fieldName=" + fieldName +
              "&termEffectiveFromDate=" + document.forms[0].termEffectiveFromDate.value +
              "&termEffectiveToDate=" + document.forms[0].termEffectiveToDate.value +
              "&isTermEffectiveToDateChanged=" + document.forms[0].isTermEffectiveToDateChanged.value +
              "&accountingDate=" + document.forms[0].accountingDate.value +
              "&issueCompanyEntityId=" + document.forms[0].issueCompanyEntityId.value +
              "&issueStateCode=" + document.forms[0].issueStateCode.value +
              "&practiceStateCode=" + document.forms[0].practiceStateCode.value +
              "&regionalOffice=" + document.forms[0].regionalOffice.value +
              "&policyTypeCode=" + document.forms[0].policyTypeCode.value;

    // initiate call
    new AJAXRequest("get", url, "", processAJAXResponse, false);
}

function processAJAXResponse(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            // reset field values regardless if we got messages or not
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                setFormFieldValuesByObject(oValueList[0]);
            }
            handleAjaxMessages(data, null);
        }
    }
}

function createPolicyListGrid_selectRow(rowId) {
    var selectedDataGrid = getXMLDataForGridName(getCurrentlySelectedGridId());
    var policyTypeCode = selectedDataGrid.recordset("CPOLICYTYPECODE").value;
    document.forms[0].policyTypeCode.value = policyTypeCode;
    // Syn policy type value from grid to layer fields.
    document.forms[0].termTypeCode.value = selectedDataGrid.recordset("CTERMTYPECODE").value;
    setObjectValue("policyTypeCode", policyTypeCode, true);
    sendAJAXRequest("policyTypeCode");
}

function handleOnSubmit(action) {
    switch (action) {
        case 'findAllPolicyType':
            document.forms[0].policyTypeCode.value = "";
            document.forms[0].termTypeCode.value = "";
            break;
    }

    return true;
}

function handleOnButtonClick(btn) {
    switch (btn) {
        case 'Create':
            var process = hasObject("isFromCopyNew") ? "copyNewPolicyFromRisk" : "create";
            var parentWindow = getParentWindow();
            if (parentWindow) {
                if(parentWindow.objPolicyLockTimer){
                    parentWindow.objPolicyLockTimer.stopTimer();
                }
            }
            postAjaxSubmit("/policymgr/createPolicy.do", process, true, false, handleOnCreatePolicy, false);
            break;

        case 'Cancel':
            closeThis();
            break;
    }

    return true;
}

function handleOnCreatePolicy(ajax){
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }

            // handle confirmations
            if (isConfirmationMessagesProcessed()) {
                repostAjaxSubmitWithConfirmationValue();
            }
            // no confirmations, we're done
            else {
                var oValueList = parseXML(data);
                if (oValueList.length > 0) {
                    setFormFieldValuesByObject(oValueList[0]);
                }

                closeWindow(function () {
                    // If the page is invoked by "Amalgamate" page, pass the policyNo
                    // to parent page and close current page
                    var parentWindow = getParentWindow();
                    if (parentWindow) {
                        if(parentWindow.objPolicyLockTimer) {
                            parentWindow.objPolicyLockTimer.resetTimer();
                        }
                    }
                    if (parentWindow.amalgamateToNewPolicyDone) {
                        // refresh parent page's token value
                        if (parentWindow.refreshStrutsToken) {
                            parentWindow.refreshStrutsToken();
                        }
                        parentWindow.amalgamateToNewPolicyDone(getObjectValue("policyNo"));
                    }
                    else {
                        var path = getAppPath() + "/policymgr/maintainPolicy.do?process=loadPolicyDetail&policyNo=" +
                                getObjectValue("policyNo") + "&isNewPolicyCreated=Y";

                        path = dti.csrf.setupCSRFTokenForUrl(path);

                        if (parentWindow) {
                            dti.divpopuputils.clearIFrameSrc();
                            parentWindow.setWindowLocation(path);
                            parentWindow.showProcessingImgIndicator();
                        }
                        else {
                            setWindowLocation(path);
                            showProcessingImgIndicator();
                        }
                    }
                });
            }
        }
    }
}

function closeThis() {
    closeWindow(function () {
        var parentWindow = getParentWindow();
        if (parentWindow) {
            if (parentWindow.objPolicyLockTimer) {
                parentWindow.objPolicyLockTimer.resetTimer();
            }
        }
    });
}
