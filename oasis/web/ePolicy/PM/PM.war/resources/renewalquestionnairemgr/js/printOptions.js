//-----------------------------------------------------------------------------
// Javascript file for printOptions.jsp.
//
// (C) 2008 Delphi Technology, inc. (dti)
// Date:   June 25, 2008
// Author: syang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 11/06/2012       xnie        138374 - Modified handleOnButtonClick to add a parameter for performPrintRenewalQuestionnare.
//-----------------------------------------------------------------------------

function handleOnButtonClick(action) {
    switch (action) {
        case 'DONE':
            var performB = "";
            if (getObject("PrintOptions").value == 'RESEND') {
                if (isEmpty(getObject("sendDate").value)) {
                    alert(getMessage("pm.renewalQuestionnaireMailingEvent.print.sendDate.empty"));
                    return false;
                }
                if (window.frameElement.document.parentWindow.isAllQuestionnaireReceived()) {
                    alert(getMessage("pm.renewalQuestionnaireMailingEvent.print.resend.nothing"));
                    return false;
                }
                setMailingInformation();

                performB = "performB=N";
            }
            else {
                performB = "performB=Y";
            }
            var url = "/renewalquestionnairemgr/renewalMailingEventPrint.do?" + performB;
            postAjaxSubmit(url, "performPrintRenewalQuestionnare", false, false, handleOnGetInitialValues);
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
                if (oValueList[0]["printOptions"] == 'Y') {
                    var curDivParentWindow = window.frameElement.document.parentWindow;
//                    curDivParentWindow.isSaveInformationForPrint = false;
                    commonOnButtonClick('CLOSE_RO_DIV');
                    curDivParentWindow.isInvokeSearch = true;
                    alert(getMessage("pm.renewalQuestionnaireMailingEvent.print.success"));
                    if(curDivParentWindow.isOkSaveInformation){
                      curDivParentWindow.handleOnButtonClick('SEARCH');
                    }
                    else{
                      curDivParentWindow.isForCaptureDivPopup = true;  
                    }
                }
                else {
                    alert(getMessage("pm.renewalQuestionnaireMailingEvent.print.fail"));
                }
            }
        }
    }
}
//When underwriter selects RESEND,system sets mailing information and save this information.
function setMailingInformation() {
    var curDivParentWindow = window.frameElement.document.parentWindow;
    curDivParentWindow.setMailingInformation();
}