var url = getAppPath()+"/changeTermExpirationDate.do?process=changeTermExpirationDate"+
    "&"+commonGetMenuQueryString();

function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'DONE':
            if(isButtonStyle() || isPageDataChanged()){
                showProcessingDivPopup();
                // Enable and hide all disabled fields in a form before submit
                enableFieldsForSubmit(document.forms[0]);
                getObject("process").value = "changeTermExpirationDate";
            }else{
                proceed = false;
                autoSaveResultType = commonOnSubmitReturnTypes.noDataChange;
            }
            break;
    }
    return proceed;
}

