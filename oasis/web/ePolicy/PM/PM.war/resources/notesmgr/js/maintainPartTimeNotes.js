//-----------------------------------------------------------------------------
// Common javascript file.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   July 14, 2010
// Author: bhong
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 07/14/2010       bhong       107682 - Encode riskTypeCode to deal with ampersand(&) character.
// 01/05/2015       tzeng       168463 - Modified handleOnButtonClick to set the clear search fields value logic after
//                                       prompt window logic, and  put clearScreen to N if user cancel the prompt window.
//-----------------------------------------------------------------------------
var setPolicyHolder = false;
var curPolicyHolder = "";
function partTimeNotesGrid_setInitialValues() {
    // Set effective to date to open date
    var sPolicyNo = getObjectValue("policyNumber");
    var sRiskBaseRecordId = getObjectValue("riskBaseRecordId");
    var sOccupant = getObjectValue("occupant");
    var sRiskTypeCode = getObjectValue("riskTypeCode");
    var path = getAppPath() + "/notesmgr/maintainPartTimeNotes.do?"
        + "process=getInitialValuesForNotes" +
               "&policyNumber=" + sPolicyNo +
               "&riskBaseRecordId=" + sRiskBaseRecordId +
               "&occupant=" + sOccupant +
               "&riskTypeCode=" + escape(sRiskTypeCode) + "&date=" + new Date();
    new AJAXRequest("get", path, '', commonHandleOnGetInitialValues, false);
}

function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'SEARCH':
            document.forms[0].process.value = "loadAllPartTimeNotes";
            showProcessingDivPopup();
            submitFirstForm();
            break;
        case 'CLEAR':
            document.forms[0].process.value = "loadAllPartTimeNotes";
            setInputFormField("clearScreen", "Y");
            if (isPageDataChanged()) {
                if (!confirm(getMessage("pm.partTimeNotes.clearConfirm"))) {
                    setInputFormField("clearScreen", "N");
                    break;
                }
            }
            getObject("policyNumber").value = "";
            getObject("riskBaseRecordId").value = "";
            showProcessingDivPopup();
            submitFirstForm();
            break;
        case 'ADD':
        // Add special logics to disable Add option after commonAddRow is invoked.
            var count = partTimeNotesGrid1.documentElement.selectNodes("//ROW[UPDATE_IND='I']").length;
            if (count >= 1) {
                // Hide add option
                getObject("isAddAvailable").value = "N";
                pageEntitlements(false);
            }
            break;
        case 'CLOSE':
            closeThisDivPopup(false);
            // refresh ParentPage if applicable
            var closeParentPage = getObjectValue("refreshParentPageOnClose");
            if (!isEmpty(closeParentPage) && closeParentPage == "Y") {
                window.frameElement.document.parentWindow.refreshPage();
            }
            // Update parent page's notes status if applicable
            var oGrid = getObject("partTimeNotesGrid1");
            // Only update status if grid is exist, otherwise the program does not know if the notes exist
            if (oGrid) {
                var isNotesExist = false;
                if (!isEmptyRecordset(partTimeNotesGrid1.recordset)) {
                    isNotesExist = true;
                }
                var refreshNoteStatus = getObjectValue("refreshNoteStatus");
                if (!isEmpty(refreshNoteStatus) && refreshNoteStatus == "Y") {
                    var functionExists = eval("window.frameElement.document.parentWindow.iframeMailingEvent.refreshNoteStatus");
                    if (functionExists) {
                        window.frameElement.document.parentWindow.iframeMailingEvent.refreshNoteStatus(isNotesExist);
                    }
                }
            }
    }
}

//-----------------------------------------------------------------------------
// Override this method to only check grid changes
//-----------------------------------------------------------------------------
function isPageDataChanged() {
    return isPageGridsDataChanged();
}

function handleOnChange(field) {
    if (field.name == "policyNumber") {
        var policyNo = field.value;
        url = getAppPath() + "/notesmgr/maintainPartTimeNotes.do?policyNumber=" + policyNo + "&process=validatePolicyNo";
        // initiate async call
        new AJAXRequest("get", url, '', validatePolicyNoDone, false);
    }
}

function validatePolicyNoDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                // disable add/save option if policy no is invalid
                getObject("isAddAvailable").value = "N";
                getObject("isSaveAvailable").value = "N";
                pageEntitlements(false);
            }
            else {
                // Select policyholder from the risk list
                var oValueList = parseXML(data);
                if (oValueList.length > 0) {
                    var policyHolder = oValueList[0]["POLICYHOLDER"];
                    setPolicyHolder = true;
                    curPolicyHolder = policyHolder;
                }
                // Enable add/save option if policy no is valid
                var oGrid = getObject("partTimeNotesGrid1");
                if (oGrid) {
                    var count = partTimeNotesGrid1.documentElement.selectNodes("//ROW[UPDATE_IND='I']").length;
                    if (count == 0) {
                        getObject("isAddAvailable").value = "Y";
                    }
                    getObject("isSaveAvailable").value = "Y";
                    pageEntitlements(false);
                }
            }
        }
    }
}

//-----------------------------------------------------------------------------
// Select Policy holder from the risk list
//-----------------------------------------------------------------------------
function postAjaxRefresh() {
    if (setPolicyHolder) {
        var oRiskList = getObject("riskBaseRecordId");
        var len = oRiskList.options.length;
        for (var i = 0; i < len; i++) {
            var optionText = oRiskList.options[i].text;
            if (optionText.indexOf(curPolicyHolder) != -1) {
                oRiskList.options[i].selected = true;
                break;
            }
        }
        setPolicyHolder = false;
    }
}

function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'SAVE':
            document.forms[0].process.value = "saveAllPartTimeNotes";
            break;

        default:
            proceed = false;
    }
    return proceed;
}
