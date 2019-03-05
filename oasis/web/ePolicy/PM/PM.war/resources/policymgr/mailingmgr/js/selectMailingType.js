//-----------------------------------------------------------------------------
// javascript file.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:
// Author:
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
//   11/30/2018       xjli        195889 - Reflect grid replacement project changes.
//-----------------------------------------------------------------------------
function handleOnButtonClick(btn) {
    switch (btn) {
        case 'Done':
            var productMailingId = getObjectValue("productMailingId");
            if (isEmpty(productMailingId)) {
                handleError(getMessage("pm.selectMailingType.noSelection.error"));
            }
            else {
                var parentWindow = getParentWindow();

                var divPopup = parentWindow.getDivPopupFromDivPopupControl(window.frameElement);
                function doCloseWindow() {
                    if (parentWindow.closeDiv) {
                        parentWindow.closeDiv(divPopup);
                    }
                }
                parentWindow.maintainMailing(productMailingId);
                doCloseWindow();
            }
            break;
    }

}