function handleOnButtonClick(btn) {
    switch (btn) {
        case 'CLOSE':
            document.forms[0].process.value = "closePage";
            submitFirstForm();
            break;
    }

    return true;
}