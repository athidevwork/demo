//-----------------------------------------------------------------------------
// Javascript file for maintainRiskSurchargePoints.jsp.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   Aug 03, 2010
// Author: syang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 
//-----------------------------------------------------------------------------
function handleOnSubmit(action) {
    var proceed = false;
    switch (action) {
        case 'SAVE':
            document.forms[0].process.value = "saveAllRiskSurchargePoint";
            proceed = true;
            break;
    }

    return proceed;
}