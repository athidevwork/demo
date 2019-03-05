//-----------------------------------------------------------------------------
// Javascript file for loadReRateOptions.jsp.
//
// (C) 2012 Delphi Technology, inc. (dti)
// Date:   September 27, 2012
// Author: xnie
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 09/27/2012       xnie        133766 - Initial version.
//-----------------------------------------------------------------------------

function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'OK':
            var submitAsCode = getObjectValue("submitAsCode");
            closeWindow(function() {
                var parentWindow = getParentWindow();
                if (submitAsCode == "ONDEMAND") {
                    parentWindow.onDemandReRate();
                }
                else if (submitAsCode == "BATCH") {
                    parentWindow.batchReRate();
                }
                else if (submitAsCode == "REPORT") {
                    parentWindow.openReRateResult();
                }
            });
            break;

        case 'CLOSE':
            closeWindow();
            break;
    }
}