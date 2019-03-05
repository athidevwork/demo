function handleOnButtonClick(action) {
    switch (action) {
        case 'DONE':
            var url = "/renewalquestionnairemgr/addRenewalQuestionnaire.do?";
            postAjaxSubmit(url, "addRenewalQuestionnare", false, false, handleOnGetInitialValues);
            break;
        default:break;
    }
}
function handleOnGetInitialValues(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data)) {
                return;
            }
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                if (oValueList[0]["addQuestionnaire"] == 'Y') {
                    commonOnButtonClick('CLOSE_RO_DIV');
                    var parentWindow = window.frameElement.document.parentWindow;
                    parentWindow.handleOnButtonClick('SEARCH');
                }
                else {
                    alert(getMessage("pm.renewalQuestionnaireMailingEvent.addRenewal.fail"));
                }
            }
        }
    }
}
//-----------------------------------------------------------------------------
// The handleOnKeyDown function will submit the form automatically,
// so system returns false to prevent submitting when the underwriter enters 'Enter' key.
//-----------------------------------------------------------------------------
function handleOnKeyDown(){
    var evt = window.event ;
    var code = evt.keyCode;
    if (code == 13) {
        return false;
    }
    return true;
}