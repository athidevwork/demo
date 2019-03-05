//-----------------------------------------------------------------------------
// Javascript file for createCoiRenewal.jsp.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   Jun 17, 2010
// Author: Dzhang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 07/05/2010       dzhang      Change createCOIRenewal to createCoiRenewal & remove unusefull code.
// 07/06/2010       dzhang      Rename fieldId for "Term Exp To" & "Term Exp From".
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
// To handle the on submit event.
//-----------------------------------------------------------------------------
function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'GENCOI':
            showProcessingDivPopup();
            getObject("process").value = "createCoiRenewal";
            break;
        default:
            proceed = false;
    }
    return proceed;
}

//-----------------------------------------------------------------------------
// To handle the on change event.
// Set the value in "Term Exp To" to the value entered in "Term Exp From" field.
//-----------------------------------------------------------------------------
function handleOnChange(obj) {
    if (obj.name == "effDate") {
        if (datemaskclear()) {
            getObject("expDate").value = obj.value;
        }       
    }
}