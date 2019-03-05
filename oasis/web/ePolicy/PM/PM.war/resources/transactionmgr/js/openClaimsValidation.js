function handleOnButtonClick(btn) {
    switch (btn) {
        case 'Close':
            commonOnSubmit('validateOpenClaims', true, true, true);
            break;
    }

    return true;
}