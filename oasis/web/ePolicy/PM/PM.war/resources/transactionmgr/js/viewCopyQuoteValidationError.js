function handleOnButtonClick(btn) {
    switch (btn) {
        case 'Close':
            if (isEmpty(getObjectValue("workflowState"))) {
               var remapPolicyNo = getObjectValue("remapPolicyNo");
               if( !isEmpty(remapPolicyNo)) {
                   window.frameElement.document.parentWindow.handleOnShowCopyQuoteErrors(remapPolicyNo);
                   closeErrorWindow();
               }
               else {
                   closeErrorWindow();
               }
            }
            else {
                commonOnSubmit('closePage', true, true, true);
            }
            break;
    }

    return true;
}


function closeErrorWindow() {
    var divPopup = window.frameElement.document.parentWindow.getDivPopupFromDivPopupControl(this.frameElement);
    window.frameElement.document.parentWindow.closeDiv(divPopup);
}