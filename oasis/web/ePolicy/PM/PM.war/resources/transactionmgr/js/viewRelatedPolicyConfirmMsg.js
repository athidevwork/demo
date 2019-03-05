function handleOnButtonClick(btn) {
    switch (btn) {
        case 'YES':
            document.forms[0].process.value = "confirmSaveOfficial";
            document.forms[0].confirmed.value="Y";
            submitFirstForm();
            break;
     case 'NO':
            document.forms[0].process.value = "confirmSaveOfficial";
            document.forms[0].confirmed.value="N";
            submitFirstForm();
            break;
    }
    return true;
}