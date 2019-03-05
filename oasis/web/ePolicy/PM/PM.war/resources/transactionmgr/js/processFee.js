function handleOnButtonClick(btn) {
    switch (btn) {
        case 'Close':
            commonOnSubmit('processFee', true, true, true);
            break;
    }

    return true;
}
