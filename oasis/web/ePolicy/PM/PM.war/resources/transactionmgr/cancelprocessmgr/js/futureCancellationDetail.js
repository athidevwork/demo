//-----------------------------------------------------------------------------
// for cancellation
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:
// Author:
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
//07/05/2016        eyin        176476 - Add Future Cancellation Details Popup.
//-----------------------------------------------------------------------------
function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'CLOSE_DIV':
            handleOnViewMultiCancelInfo();
            break;
    }
}

function handleOnViewMultiCancelInfo() {
    if(getObjectValue("status") == 'INVALID' || getObjectValue("status") == 'WARNING'){
        window.frameElement.document.parentWindow.handleOnViewMultiCancelInfo(getObjectValue("status"));
    }
}