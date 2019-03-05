function handleOnButtonClick(btn) {
    switch (btn) {
        case 'Close':
            commonOnSubmit('processResponse', true, true, true);
            if (getObjectValue("ibnrNotify.confirmed") == "Y" && hasObject("associatedRiskCount") && getObjectValue("associatedRiskCount") == "1") {
                closeThisDivPopup(true);
                window.frameElement.document.parentWindow.refreshPage();
            }
            break;
    }

    return true;
}
