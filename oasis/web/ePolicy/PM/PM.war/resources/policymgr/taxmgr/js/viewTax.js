function handleOnChange(field) {
    var fieldName = field.name;
    switch (fieldName) {
        case "transactionLogId":
            var transactionClause = ""
            var transactionLogValue = getObjectValue("transactionLogId");
            if (transactionLogValue == -1) {
                transactionClause = "&transactionLogId=0"
            }
            else {
                transactionClause = "&transactionLogId=" + transactionLogValue;
            }
            document.forms[0].action = getAppPath() + "/policymgr/taxmgr/viewTax.do?" +
                commonGetMenuQueryString() + "&process=loadAllTax&" + transactionClause;
            submitForm(true);
            break;
    }
}

function handleOnButtonClick(btn) {
    var proceed = true;

    switch (btn) {
        case 'Close':
            if (isEmpty(getObjectValue("workflowState"))) {
                closeWindow();
            }
            else {
                commonOnSubmit('closePage', true, true, true);
            }
            break;
    }
    return proceed;
}

function closeWindow() {
    commonOnButtonClick("CLOSE_DIV");
}
