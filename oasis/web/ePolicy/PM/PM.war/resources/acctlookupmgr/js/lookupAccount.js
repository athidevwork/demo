function handleOnButtonClick(btn) {
    switch (btn) {
        case 'Select':
            var selectedDataGrid = getXMLDataForGridName(getCurrentlySelectedGridId());
            var accountId = selectedDataGrid.recordset("ID");
            var accountNo = selectedDataGrid.recordset("CBILLINGACCOUNTNO");
            window.frameElement.document.parentWindow.handleOnLookupAccount(btn, accountId, accountNo);
            break;
        case 'Cancel':
            var accountId = "";
            var accountNo = "";
            window.frameElement.document.parentWindow.handleOnLookupAccount(btn, accountId, accountNo);
            break;
    }

    var divPopup = window.frameElement.document.parentWindow.getDivPopupFromDivPopupControl(this.frameElement);
    if (divPopup) {
        window.frameElement.document.parentWindow.closeDiv(divPopup);
    }

    return true;
}
