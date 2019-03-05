function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'DONE':
            var selectedRows = getSelectedRows(coverageListGrid1);
            if (selectedRows.length == 0) {
                handleError(getMessage("pm.maintainTail.noTailSelectedError"));
            }
            else {
                var accountingDate = getObjectValue("accountingDate");
                var transactionCode = getObjectValue("transactionCode");
                if (getObjectValue("captureCancellationDetail") == 'Y') {
                    performCancellation("TAIL","","","", accountingDate);
                }
                else if (getObjectValue("captureTransactionDetail") == 'Y') {
                    captureTransactionDetails(transactionCode, 'captureFinancePercentage', accountingDate);
                }

            }
            break;
        case 'CANCEL':
            reloadPage();
            break;
    }
}