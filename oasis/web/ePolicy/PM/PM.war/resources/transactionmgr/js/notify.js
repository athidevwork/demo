//-----------------------------------------------------------------------------
// JavaScript file for product notify page.
//
// (C) 2011 Delphi Technology, inc. (dti)
// Date:   July 05, 2011
// Author: wqfu
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 07/05/2011       wqfu        122513 - Disable Close button after click it to avoid double click issue.
//-----------------------------------------------------------------------------

function handleOnButtonClick(btn) {
    switch (btn) {
        case 'Close':
            commonOnSubmit('processResponse', true, true, true);
            // Disable Close button after submit
            if (hasObject('PM_NOTIFY_CLOSE')) {
                getObject('PM_NOTIFY_CLOSE').disabled = true;    
            }

            break;
    }

    return true;
}
