//-----------------------------------------------------------------------------
// Functions used on denyQuote.jsp page for capturing deny data.
//-----------------------------------------------------------------------------
function handleOnButtonClick(asBtn) {
    var divPopup = window.frameElement.document.parentWindow.getDivPopupFromDivPopupControl(this.frameElement);
    switch (asBtn) {
        case 'DONE':
            if (divPopup && validate(document.forms[0], true)) {
                window.frameElement.document.parentWindow.handleOnCaptureDenyDone(
                getObject("denyReason").value,
                getObject("denyEffDate").value,
                getObject("comments").value
                );

                window.frameElement.document.parentWindow.closeDiv(divPopup);
            }
            break;
        case 'CANCEL':
            if (divPopup) {
                window.frameElement.document.parentWindow.closeDiv(divPopup);
            }
            break;
    }
}