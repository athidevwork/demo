function handleOnButtonClick(btn) {
    switch (btn) {
        case 'Close':
            commonOnSubmit('processConfirmation', true, true, true);
            break;
    }

    return true;
}
