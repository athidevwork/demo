//-----------------------------------------------------------------------------
// JavaScript file for maintain dividend rule.
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
        case 'SEARCH':
            document.forms[0].process.value = "loadAllDividendRule";
            submitFirstForm();
            break;
        default:break;
    }
}

function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'SAVE':
            document.forms[0].process.value = "saveAllDividendRule";
            break;
        default:
            proceed = false;
    }
    return proceed;
}

function dividendListGrid_setInitialValues() {
    var url = getAppPath() + "/policymgr/dividendmgr/maintainDividendRule.do"
                           + "?process=getInitialValuesForAddDividendRule"
                           + "&policyTypeCode=" + getObjectValue("policyType")
                           + "&date=" + new Date();
    new AJAXRequest("get", url, '', commonHandleOnGetInitialValues, false);
}
