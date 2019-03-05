
function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'Close':
                  //commonOnButtonClick('CLOSE_RO_DIV');
                  window.frameElement.document.parentWindow.refreshPage();
            break;
    }

}
