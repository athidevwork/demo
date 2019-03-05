//-----------------------------------------------------------------------------
// Functions to support Address Add Popup page.
// Author: dpang
// Date:   05/04/2018
// Modifications:
//-----------------------------------------------------------------------------
// 05/04/2018   dpang      issue 192743 -  eCS-eCIS Refactoring: Add Person/ Add Organization
//-----------------------------------------------------------------------------
function afterSave() {
    switch (getObjectValue("processAfterSave")) {
        case "afterSaveForSelect":
            afterSaveForSelect();
            break;

        case "afterSaveForGotoclient":
            afterSaveForGotoclient();
            break;
    }
}

function afterSaveForSelect() {
    if(isOpenerLost()) {
        alert(getMessage("message.popup.opener.error"));
    } else {
        // Populate this newly created record in the parent search page.
        getParentWindow().searchByClientID(getObjectValue("clientId"));
    }

    baseCloseWindow();
}

function afterSaveForGotoclient() {
    if(isOpenerLost()) {
        alert(getMessage("message.popup.opener.error"));
        baseCloseWindow();
    } else {
        getParentWindow().searchByClientID(getObjectValue("clientId"));
        goToCISEntityModify(getObjectValue("pk"), getObjectValue("entityType"));
    }
}

/*
 Check whether opener window exists. If yes, check if the js function defined on opener.
 */
function isOpenerLost() {
    var lostOpener = true;
    if (getParentWindow() && !getParentWindow().closed) {
        if (getParentWindow().searchByClientID) {
            lostOpener = false;
        }
        getParentWindow().focus();
    }

    return lostOpener;
}