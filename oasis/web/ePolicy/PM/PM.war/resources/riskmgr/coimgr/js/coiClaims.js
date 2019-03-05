// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 11/22/2018       clm         195889 -  Grid replacement using closeWindow
//-----------------------------------------------------------------------------
function handleOnButtonClick(asBtn) {
    var parentWindow = getParentWindow();
    switch (asBtn) {
        case 'SAVE_COI_CLAIM':
            var coiSelectLetter = getObjectValue("coiSelectLetter");
            var coiIncludeExcludeClaim = getObjectValue("coiIncludeExcludeClaim");
            var coverageType = getObjectValue("coverageType");
            var claimType = getObjectValue("claimType");
            var paymentType = getObjectValue("paymentType");
            var coiCutoffDate = getObjectValue("coiCutoffDate");
            //alert(coiSelectLetter+"|"+coiIncludeExcludeClaim+"|"+coverageType+"|"+claimType+"|"+paymentType+"|"+coiCutoffDate);
            closeWindow(function () {
                // call parent page's handleOnCaptureCoiClaimHistory function
                getReturnCtxOfDivPopUp().handleOnCaptureCoiClaimHistory(
                        coiSelectLetter, coiIncludeExcludeClaim, coverageType, claimType, paymentType, coiCutoffDate);
            });
            break;
    }
}

function handleOnChange(obj) {
    if (obj.name == 'coiIncludeExcludeClaim') {
        var coiIncludeExcludeClaim = getObjectValue("coiIncludeExcludeClaim");
        var covgType = getObject("coverageType");
        var claimType = getObject("claimType");
        var paymentType = getObject("paymentType");
        if (coiIncludeExcludeClaim == 'INCCLAIM') {
            enableDisableField(covgType, false);
            enableDisableField(claimType, false);
            enableDisableField(paymentType, false);
        }
        else if (coiIncludeExcludeClaim == 'EXCCLAIM') {
            enableDisableField(covgType, true);
            enableDisableField(claimType, true);
            enableDisableField(paymentType, true);
        }
    }
}