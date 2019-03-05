//-----------------------------------------------------------------------------
// Javascript file for maintainSpecialHandling.jsp.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   Aug 23, 2010
// Author: syang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 08/23/2010       syang       Issue 108651 - Updated handleOnChange() to handle Renew indicator when change effective to date.
// 07/17/2012       ryzhao      135662 - Variable italicsFieldId should have different value for POLICY or QUOTE.
//                                       Modified handleOnButtonClick to set different value to italicsFieldId
//                                       for special handling before closing the pop up div.
// 10/24/2018       xgong      195889 - update handleOnButtonClick/handleOnChange for grid replacement
//-----------------------------------------------------------------------------
function specialHandlingListGrid_selectRow() {
}

function handleOnChange(obj) {
    var name = obj.name;
    if (name == 'effectiveFromDate' || name == 'effectiveToDate') {
        // If the start date equals with end date, set renewalB to N
        var fromDateVal = getObject("effectiveFromDate").value;
        var toDateVal = getObject("effectiveToDate").value;
        if (isValueDate(fromDateVal) && isValueDate(toDateVal) && fromDateVal == toDateVal) {
            setObjectValue("renewalB", "N");
        }
    }
    // Issue 108651, handle Renew indicator.
    if (obj.name == "effectiveToDate") {
        var effectiveToDate = getObjectValue("effectiveToDate");
        var termExpirationDate = policyHeader.termEffectiveToDate;
        enableDisableRenewIndicator(effectiveToDate, termExpirationDate, "renewalB", "isRenewalBAvailable", "specialHandlingListGrid");
    }
}

function validateGrid() {
    return true;
}

function handleOnSubmit(action) {
    var proceed = false;

    switch (action) {
        case 'SAVE':
            document.forms[0].process.value = "saveAllSpecialHandlings";
            captureTransactionDetails("SPHANDLING", "submitForm");    //TLDECLINE, SPHANDLING
            break;
    }

    return proceed;
}

function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'CLOSE':
            var policyFk = getObjectValue("policyId");
            var functionExists = eval("getParentWindow().handleSpecialHandlingExist");
            if (functionExists != null) {
                getParentWindow().handleSpecialHandlingExist("SPECIAL_HANDLING", policyFk);
            }
            if (policyHeader.policyCycleCode == 'POLICY') {
                getParentWindow().italicsFieldId = "SPECIALHANDLINGB";
            }
            else {
                getParentWindow().italicsFieldId = "SPECIALHANDLINGQUOTEB";
            }
            // Close div popup
            closeThisDivPopup(false);
            break;
    }
}

function specialHandlingListGrid_setInitialValues() {
    var path = getAppPath() + "/policymgr/specialhandlingmgr/maintainSpecialHandling.do?"
        + commonGetMenuQueryString() + "&process=getInitialValuesForSpecialHandling";
    new AJAXRequest("get", path, '', commonHandleOnGetInitialValues, false);
}
