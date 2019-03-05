//-----------------------------------------------------------------------------
// Javascript file for renewalQuestionnaireMailingEvent.jsp.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   May 10, 2010
// Author: syang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 05/10/2010       syang       107547 - Added userReadyStateReady to handle the empty row in grid.
// 10/19/2010       gzeng       112909 - Set isScrolling to false for special pages.
// 06/26/2014       xnie        151631 - Reverted 107547 fix due to 107547 is fixed by 108878.
//-----------------------------------------------------------------------------

var comments;
var isInvokeSearch = false;
var currentSelectedNoteContent;
var isForNoteDivPopup = false;
var isForCaptureDivPopup = false;
var commentChanged = 'N';
var isSaveInformationForPrint = false;
//isOkSaveInformation is only invoked by printOption.js
var isOkSaveInformation = false;
function handleOnButtonClick(action) {
    switch (action) {
        case 'SEARCH':
            if (isDateChanged() && !isInvokeSearch) {
                if (confirm(getMessage("pm.renewalQuestionnaireMailingEvent.save.changed"))) {
                    isSaveInformationForPrint = false;
                    handleOnButtonClick('SAVE');
                }
                else {
                    if (confirm(getMessage("pm.renewalQuestionnaireMailingEvent.save.continue"))) {
                        clearAllForm();
                        commonOnSubmit('SEARCH');
                    }
                }
            }
            else {
                clearAllForm();
                commonOnSubmit('SEARCH');
            }
            break;
        case 'FILTER':
            getObject("filterMailingEvent").value = "Y";
            commonOnSubmit('SEARCH');
            break;
        case 'CLEAR':
            clearAllForm();
            commonOnSubmit('SEARCH');
            break;
        case 'PRINT':
            if (isDateChanged()) {
                alert(getMessage("pm.renewalQuestionnaireMailingEvent.print.unsavedDate"));
                return false;
            }
            var selectedDataGrid = getXMLDataForGridName("renewalQuestionMailingEventListGrid");
            var polRenfrmMasterId = selectedDataGrid.recordset("CPOLICYRENEWFORMMASTERID").value;
            var sendDate = selectedDataGrid.recordset("CSENDDATE").value;
            var printUrl = getAppPath() + "/renewalquestionnairemgr/renewalMailingEventPrint.do?"
                + commonGetMenuQueryString() + "&polRenfrmMasterId=" + polRenfrmMasterId + "&sendDate=" + sendDate
                + "&process=getInitialValuesForPrintRenewalQuestionnare";
            var divPopupId = openDivPopup("", printUrl, true, true, "", "", 400, 300, "", "", "", false,"","",false);
            break;
        case 'MAIL':
            break;
        case 'FIND':
            var policyNo = getObject("findPolicyNo").value;
            var name = getObject("findName").value;
            var event = getObject("event").value;
            if (iframeMailingEvent != undefined && iframeMailingEvent.findRenewalMailingEventQuestion) {
                iframeMailingEvent.findRenewalMailingEventQuestion(policyNo, name, event);
            }
            break;
        case 'MARKALL':
            iframeMailingEvent.handleAllResend('MARKALL');
            break;
        case 'CLEARALL':
            iframeMailingEvent.handleAllResend('CLEARALL');
            break;
        case 'ADD':
            var selectedDataGrid = getXMLDataForGridName("renewalQuestionMailingEventListGrid");
            var polRenfrmMasterId = selectedDataGrid.recordset("CPOLICYRENEWFORMMASTERID").value;
            var riskClass = selectedDataGrid.recordset("CRISKCLASSCODE").value;
            var effToDate = selectedDataGrid.recordset("CENDSEARCHDATE").value;
            var addUrl = getAppPath() + "/renewalquestionnairemgr/addRenewalQuestionnaire.do?"
                + commonGetMenuQueryString() + "&process=getInitialValuesForAddRenewalQuestionnare"
                + "&polRenfrmMasterId=" + polRenfrmMasterId + "&riskClass=" + riskClass + "&effToDate=" + effToDate ;
            var divPopupId = openDivPopup("", addUrl, true, true, "", "", 550, 300, "", "", "", false,"","",false);
            break;
        case 'SAVE':
            if (iframeMailingEvent != undefined && iframeMailingEvent.getChangesList) {
                var selectedDataGrid = getXMLDataForGridName("renewalQuestionMailingEventListGrid");
                var polRenfrmMasterId = selectedDataGrid.recordset("CPOLICYRENEWFORMMASTERID").value;
                var comments = selectedDataGrid.recordset("CCOMMENTS").value;
                var currentUrl = "&polRenfrmMasterId=" + polRenfrmMasterId + "&commentChanged=" + commentChanged + "&comments=" + comments;
                document.forms[0].txtXML.value = iframeMailingEvent.getChangesList();
                var url = "/renewalquestionnairemgr/renewalQuestionnaireMailingEvent.do?" + currentUrl;
                postAjaxSubmit(url, "saveAllMailingQuestionnaire", false, false, handleOnGetInitialValues);
            }
            break;
        case 'RESPONSE':
            if (iframeMailingEvent != undefined && iframeMailingEvent.getResponseURL) {
                var responseUrl = getAppPath() + "/renewalquestionnairemgr/renewalQuestionnaireResponse.do?"
                    + commonGetMenuQueryString() + iframeMailingEvent.getResponseURL() + "&process=loadAllQuestionnaireResponse";
                var divPopupId = openDivPopup("", responseUrl, true, true, "", "", 824, 800, 800, 820, "", "", "", false);
            }
            break;
        case 'FILES':
            var procecessFileUrl = getCSPath() + "/csAttach.do?" + commonGetMenuQueryString("PM_PROCESS_FILE");
            var id = iframeMailingEvent.currentPolicyRenewFormId();
            if (id == 0) {
                return false;
            }
            procecessFileUrl += "&sourceTableName=POLICY_RENEW_FORM&sourceRecordFk=" + id;
            var divPopupId = openDivPopup("", procecessFileUrl, true, true, "", "", "830", "600", "820", "590", "", true);
            break;
        default:break;
    }
}
function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'SEARCH':
            document.forms[0].process.value = "loadAllMailingEvent";
            showProcessingDivPopup();
            break;
        default : proceed = false;
    }
    return proceed;
}
function clearAllForm() {
    getObject("filterPolicyNo").value = "";
    getObject("filterName").value = "";
    getObject("findPolicyNo").value = "";
    getObject("findName").value = "";
    getObject("event").value = "";
}
function handleOnLoad() {
    var selectedDataGrid = getXMLDataForGridName("renewalQuestionMailingEventListGrid");
    if (isEmptyRecordset(selectedDataGrid.recordset)) {
        loadQuestionnaireForMailingEvent();
    }
    if (getObject("filterSuccess").value == 'Y') {
        getObject("findPolicyNo").value = getObject("filterPolicyNo").value;
        getObject("findName").value = getObject("filterName").value;
        // After filter mailing event, system should find questionnaire for the specified parameters.
        handleOnButtonClick('FIND');
    }
}
function renewalQuestionMailingEventListGrid_selectRow(id) {
    var selectedDataGrid = getXMLDataForGridName("renewalQuestionMailingEventListGrid");
    var policyTypeCode = selectedDataGrid.recordset("CPOLICYTYPECODE").value;
    var riskClassCode = selectedDataGrid.recordset("CRISKCLASSCODE").value;
    var polRenfrmMasterId = selectedDataGrid.recordset("CPOLICYRENEWFORMMASTERID").value;
    var pracStateCd = selectedDataGrid.recordset("CPRACTICESTATECODEA").value;
    // The following parameters are for the Add option's show/hide.
    var totalMailings = selectedDataGrid.recordset("CTOTALMAILINGS").value;
    var mailingNo = selectedDataGrid.recordset("CMAILINGNO").value;
    var lastMailing = selectedDataGrid.recordset("CLASTMAILING").value;
    var addQuestB = selectedDataGrid.recordset("CADDQUESTB").value;
    loadQuestionnaireForMailingEvent(policyTypeCode, riskClassCode, polRenfrmMasterId, pracStateCd,
        totalMailings, mailingNo, lastMailing, addQuestB);
    // Option availablity,the add option should be disabled at first.
    setOptionValue();
}
function loadQuestionnaireForMailingEvent(policyTypeCode, riskClassCode, polRenfrmMasterId, pracStateCd,
                                          totalMailings, mailingNo, lastMailing, addQuestB) {
    var questionnaireType = getObject("questionnaireType").value;
    var url = getAppPath() + "/renewalquestionnairemgr/renewalMailingEventQuestionnaire.do?" + commonGetMenuQueryString() +
              "&process=loadAllMailingEventQuestionnaire" + "&questionnaireType=" + questionnaireType;
    if (!isEmpty(polRenfrmMasterId)) {
        url = url + "&policyTypeCode=" + policyTypeCode + "&riskClassCode=" + riskClassCode +
              "&polRenfrmMasterId=" + polRenfrmMasterId + "&pracStateCd=" + pracStateCd + "&totalMailings=" +
              totalMailings + "&mailingNo=" + mailingNo + "&lastMailing=" + lastMailing + "&addQuestB=" + addQuestB;
    }
    getObject("iframeMailingEvent").src = url;
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
                saveQuestionnaireInformation(oValueList[0]["saveQuestionInfo"]);
            }
        }
    }
}
function saveQuestionnaireInformation(action) {
    if (action == 'Y') {
        isOkSaveInformation = true;
        //When print options is Resend,system doesn't refresh the page.
        if (!isSaveInformationForPrint) {
            // Refresh page with current search criteria.
            isInvokeSearch = true;
            handleOnButtonClick('SEARCH');
            closeProcessingDivPopup();
            alert(getMessage("pm.renewalQuestionnaireMailingEvent.save.success"));
        }
    }
    else {
        isOkSaveInformation = false;
        // Popup page.
        if (isSaveInformationForPrint) {
            isForCaptureDivPopup = false;
        }
        else {
            isForCaptureDivPopup = true;
        }
        var captureUrl = getAppPath() + "/renewalquestionnairemgr/captureResponseError.do?"
            + commonGetMenuQueryString() + "&process=loadAllResponseError";
        var divPopupId = openDivPopup("", captureUrl, true, true, "", "", 600, 400, "", "", "", false);
    }
}
function isDateChanged() {
    if (isGridDataChanged("renewalQuestionMailingEventListGrid")) {
        return true;
    }
    else if (iframeMailingEvent != undefined && iframeMailingEvent.isQuestionnaireGridDataChanged) {
        return iframeMailingEvent.isQuestionnaireGridDataChanged();
    }
    else {
        return false;
    }
}
// Begin: For notes.
function mailingEventNote() {
    isForNoteDivPopup = true;
    var selectedDataGrid = getXMLDataForGridName("renewalQuestionMailingEventListGrid");
    comments = selectedDataGrid.recordset("CCOMMENTS").value;
    currentSelectedNoteContent = comments;
    openDivPopup("Notes", getCorePath() + "/note.html", true, true, 150, 250, 500, 250, 446, 174, null, false,"","",false);
}
function handleOnUnloadForDivPopup(divPopFrame) {
    if (isForNoteDivPopup) {
        var newcomments = divPopFrame.document.parentWindow.currentSelectedNoteContent;
        if (comments != newcomments) {
            commentChanged = 'Y';
            var selectedDataGrid = getXMLDataForGridName("renewalQuestionMailingEventListGrid");
            selectedDataGrid.recordset("CCOMMENTS").value = newcomments;
            if (isEmpty(newcomments)) {
                selectedDataGrid.recordset("CNOTE").value = 'No';
            }
            else {
                selectedDataGrid.recordset("CNOTE").value = 'Yes';
            }
            selectedDataGrid.recordset("UPDATE_IND").value = 'Y';
        }
    }
    //When close the capture response error div, system refresh the page.
    if (isForCaptureDivPopup) {
        isInvokeSearch = true;
        handleOnButtonClick('SEARCH');
    }
}
// End: For notes.
// When underwriter selects RESEND in print option page, system should save mailing information.
function setMailingInformation() {
    var maxLastResendNumber = 0;
    if (iframeMailingEvent != undefined && iframeMailingEvent.changeReceivedBToYES) {
        maxLastResendNumber = iframeMailingEvent.changeReceivedBToYES();
    }
    var today = new Date();
    var year = today.getYear();
    var month = today.getMonth() + 1;
    var day = today.getDate();
    var formatToday = month + "/" + day + "/" + year;
    var selectedDataGrid = getXMLDataForGridName("renewalQuestionMailingEventListGrid");
    selectedDataGrid.recordset("CCOMMENTS").value = "*Resent#" + maxLastResendNumber + ":" + formatToday;
    selectedDataGrid.recordset("UPDATE_IND").value = 'Y';
    commentChanged = 'Y';
    //After save, system refresh page with current search criteria.
    isSaveInformationForPrint = true;
    handleOnButtonClick('SAVE');
}
// Begin: Set the mail option label.
function setOptionValue() {
    var selectedDataGrid = getXMLDataForGridName("renewalQuestionMailingEventListGrid");
    var isMailAvailable = selectedDataGrid.recordset("CISMAILAVAILABLE").value;
    var mailingNo = selectedDataGrid.recordset("CMAILINGNO").value;
    if (isMailAvailable == 'Y') {
        getObject("PM_RENEW_QUEST_MAIL").value = getCardinalNumber(mailingNo) + "Mail";
    }
}
// The label of mail button should be the shortening cardinal number of mailingNo+1;
function getCardinalNumber(number) {
    if (!isEmpty(number)) {
        var cardinalNumber;
        number = number + 1;
        var numLength = number.toString().length;
        var lastNumber = number.toString().substring(numLength - 1);
        switch (lastNumber) {
            case '1':cardinalNumber = number + "rt ";  break;
            case '2':cardinalNumber = number + "nd ";  break;
            case '3':cardinalNumber = number + "rd ";  break;
            default:cardinalNumber = number + "th ";  break;
        }
        return cardinalNumber;
    }
    return;
}
// End: Set options availability.
// This function is used to check whether all the received indication is 'Y'
function isAllQuestionnaireReceived() {
    if (iframeMailingEvent != undefined && iframeMailingEvent.isAllQuestionnaireReceived) {
        return iframeMailingEvent.isAllQuestionnaireReceived();
    }
    else {
        return false;
    }
}
