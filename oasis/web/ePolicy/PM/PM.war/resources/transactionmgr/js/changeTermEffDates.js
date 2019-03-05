//-----------------------------------------------------------------------------
// Javascript file for changeTermEffDates.jsp
//
// (C) 2011 Delphi Technology, inc. (dti)
// Date:   July 27, 2011
// Author:
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 11/20/2018       xgong       195889 - 1. Updated refreshParentPage for grid replacement
//-----------------------------------------------------------------------------
function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'DONE':
            postAjaxSubmit("/transactionmgr/changeTermDates.do", "saveChangedTermDates", false, false, refreshParentPage);
            break;
    }
}

function refreshParentPage(ajax){
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
            // no confirmations, refresh page
            else {
                if (getParentWindow()) {
                    getParentWindow().refreshPage(true);
                }
                else {
                    refreshPage();
                }
            }
        }
    }
}
