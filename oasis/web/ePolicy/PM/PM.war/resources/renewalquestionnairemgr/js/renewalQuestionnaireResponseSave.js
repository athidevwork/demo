function handleOnButtonClick(action) {
    switch (action) {
        case 'OK':
            var parentWindow = window.frameElement.document.parentWindow;
            commonOnButtonClick('CLOSE_RO_DIV');
            parentWindow.getSaveAsOption(getObject("saveAsCode").value);
            break;
        default:break;
    }
}
