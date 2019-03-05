//-----------------------------------------------------------------------------
// Javascript file for captureFinancePercent.jsp.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   Nov 02, 2010
// Author: syang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 11/02/2010       syang       Issue 113780 - RatePercent can't be equal or greater than 1000.
// 04/07/2011       wqfu        116939 - Any negative percentages should not be allowed,
//                                       nor should any amounts above 100% be allowed.
//-----------------------------------------------------------------------------
function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'DONE':
            var ratePercent = getObjectValue("ratePercent");
            if(parseFloat(ratePercent) > 100 || parseFloat(ratePercent) < 0){
                alert(getMessage("pm.maintainTail.captureFinancePercent.invalidRatePercent"));
            }
            else{
               window.frameElement.document.parentWindow.handleOnCaptureFinancePercentageDone(ratePercent);
            }

            break;
        case 'CANCEL':
            window.frameElement.document.parentWindow.handleOnCaptureFinancePercentageDone(0);
            break;
    }

}