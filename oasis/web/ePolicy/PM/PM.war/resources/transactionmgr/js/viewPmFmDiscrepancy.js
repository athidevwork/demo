function handleOnButtonClick(btn) {
    switch (btn) {
        case 'Close':
            document.forms[0].process.value = "closePmFmDiscrepancy";
            //commonOnSubmit('closePmFmDiscrepancy', true, true, true);
            submitFirstForm();
            break;
    }

    return true;
}
