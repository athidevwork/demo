//-----------------------------------------------------------------------------
// MailingError javascript file.
//
// (C) 2013 Delphi Technology, inc. (dti)
// Date:   May 31, 2013
// Author: tcheng
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 05/31/2013       tcheng      Issue 145238 - Modified handleOnButtonClick() to reload Process Policy Mailing page when closing Mailing Error page.
//-----------------------------------------------------------------------------
function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'CLOSE':
            window.frameElement.document.parentWindow.reloadWindowLocation();
            break;
    }
}
