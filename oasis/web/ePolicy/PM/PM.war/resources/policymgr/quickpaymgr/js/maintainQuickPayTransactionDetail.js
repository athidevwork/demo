//-----------------------------------------------------------------------------
// Javascript file for maintainQuickPayTransactionDetail.jsp.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   July 29, 2010
// Author: dzhang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 08/19/2010       dzhang      Update per Bill's comments.
//-----------------------------------------------------------------------------

function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'SAVE':
            document.forms[0].process.value = "saveAllQuickPayTransactionDetail";
            break;
        default:
            proceed = false;
    }
    return proceed;
}


function handleOnChange(field) {
    if (field.name == "riskCoverageQpAmount") {
        var qpEligAmout = riskCoverageGrid1.recordset("CRISKCOVERAGEQPELIGAMOUNT").value;
        if (!isEmpty(qpEligAmout) && qpEligAmout != 0) {
            getObject("riskCoverageQpRatio").value = Math.round(((Math.abs(getObjectValue("riskCoverageQpAmount"))) / Math.abs(qpEligAmout) ) * 100 * 100) / 100;
        }
    }
}

function handleOnButtonClick(btn) {
    switch (btn) {
        case "CLOSE_QUICK_PAY_TRANS":
            if (hasObject("openMode") && getObjectValue("openMode") != "VIEW_ONLY") {
                var parentWindow = window.frameElement.document.parentWindow;
                parentWindow.handleOnButtonClick('SEARCH');
            }
            commonOnButtonClick("CLOSE_RO_DIV");    
            break;
    }
}

function handleOnLoad() {
    if (hasObject("openMode") && getObjectValue("openMode") == "VIEW_ONLY") {
        hideGridDetailDiv("riskCoverageGrid");
    }
}

