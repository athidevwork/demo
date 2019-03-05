function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'GENERATE':
            document.forms[0].process.value = "generatePremiumAccounting";
            break;
        default:
            proceed = false;
    }
    return proceed;
}