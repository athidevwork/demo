//-----------------------------------------------------------------------------
// JavaScript file for check clearing reminder page.
//
// (C) 2012 Delphi Technology, inc. (dti)
// Date:   12/26/2012
// Author: awu
//

function handleOnButtonClick(btn) {
    switch (btn) {
        case 'Close':
            commonOnSubmit('saveCheckClearingReminder', true, true, true);
            // Disable Close button after submit
            if (hasObject('PM_CLEARING_REMINDER_CLOSE')) {
                getObject('PM_CLEARING_REMINDER_CLOSE').disabled = true;
            }

            break;
    }

    return true;
}
