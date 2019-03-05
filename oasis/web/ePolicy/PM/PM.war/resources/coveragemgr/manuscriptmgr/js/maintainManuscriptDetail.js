/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/13/2017       eyin        180675 - 1. Added condition 'isButtonStyle()' in the method 'handleOnButtonClick' for UI change.
 * 12/13/2018       huixu       195889 grid replacement - PM_MANU_DETAIL
 * ---------------------------------------------------
 */
function maintainManuscriptDetailListGrid_setInitialValues() {
    // Ajax call to get initial values
    sendAJAXRequest("getInitialValuesForAddManuscriptDetail");
}

function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'SAVE':
            document.forms[0].process.value = "saveAllManuscriptDetail";
            break;
        default:
            proceed = false;
    }
    return proceed;
}

function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'CLOSE_PAGE':
            var supportVersion = getSysParmValue("PM_MANU_DTL_VERSION");

            if(isButtonStyle()){
                if (supportVersion == "Y") {
                    getParentWindow().reLoadPage();
                }else{
                    closeThisDivPopup();
                }
            }else{
                getReturnCtxOfDivPopUp().reLoadPage();
                closeThisDivPopup();
            }
            break;
    }
}

function sendAJAXRequest(process) {
    // set url
    var url = "maintainManuscriptDetail.do?" + commonGetMenuQueryString() +
              "&process=" + process;

    switch (process) {
        case 'getInitialValuesForAddManuscriptDetail':
            url += "&manuscriptEndorsementId=" + getObjectValue("manuscriptEndorsementId");
            break;
    }

    // initiate call
    var ajaxResponseHandler = "handleOn" + process.substr(0, 1).toUpperCase() + process.substr(1);
    new AJAXRequest("get", url, "", eval(ajaxResponseHandler), false);
}

function handleOnGetInitialValuesForAddManuscriptDetail(ajax) {
    commonHandleOnGetInitialValues(ajax);
}