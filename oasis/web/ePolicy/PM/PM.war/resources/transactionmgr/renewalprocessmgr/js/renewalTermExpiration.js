function handleOnButtonClick(asBtn) {
    var divPopup = window.frameElement.document.parentWindow.getDivPopupFromDivPopupControl(this.frameElement);

    switch (asBtn) {
        case 'DONE':
            if (divPopup&&validate(document.forms[0], true)) {
                window.frameElement.document.parentWindow.handleOnRenewalTermExpiration(
                getObject("renewalTermEffDate").value,
                getObject("renewalTermExpDate").value
                );               
            }
            break;
        case 'CANCEL':
            if (divPopup) {
                window.frameElement.document.parentWindow.closeDiv(divPopup);
            }
            break;
    }
}
