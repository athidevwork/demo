/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/05/2011       lmjiang     Issue 123574 Pass correct date to manuscript entitlement load processor.
 * 11/01/2011       lmjiang     Issue 126315 - Pass 'isDeleteAvailable' to Manuscript Detail Page to show/hide the Delete button
 *                              on Manuscript Detail Page.
 * 02/10/2012       wfu         125055 - Added functions to generate and handle RTF file import/export.
 * 05/24/2012       jshen       132118 - Set coverageBaseEffectiveToDate value before submitting the data to save.
 * 06/12/2012       xnie        134250 - Added handleOnChange(), formatMoney(), and outputCents() to set format for
 *                              manuscript premium based on system parameter PM_MANU_PREMIUM_ROUND.
 * 06/15/2012       xnie        134250 - Roll backed.
 * 06/25/2012       tcheng      134650 - 1)Modified openFileUploadDone function to change ajax transmit way into post ajax.
 *                                       2)Modified handleOnButtonClick function to set verification flag for validating if it will save record before clicking upload RTF file or not.
 * 07/23/2012       tcheng      135128 - Modified openFileUploadDone function to remove maintainManuscriptListGrid_update.
 * 09/05/2012       xnie        136023 - Roll backed 132118 fix.
 * 01/15/2014       adeng       150450 - Added function handleOnChange() to handle Renew indicator when the expiration
 *                                       date is changed to a date prior to term expiration date.
 * 07/04/2014       Jyang       154814 - Modified handleOnChange() to control Renew indicator's value only when the expiration
 *                                       date is changed..
 * 10/09/2014       wdang       156038 - 1) Replaced getObject('riskId') with policyHeader.riskHeader.riskId. 
 *                                       2) Replaced getObject('coverageId') with policyHeader.coverageHeader.coverageId.
 * 11/04/2014       wdang       158813 - Corrected a spelling error in reLoadPage().
 * 09/18/2015       eyin        166007 - Modified handleOnChange(), make change to control Renew indicator's value based on
 *                                       policy expiration date instead of current term expiration date.
 * 07/14/2016       mlm         170307 - Integration of Ghostdraft.
 * 07/27/2016       mlm         178416 - Enhanced to support os_form.template_id.
 * 03/13/2017       eyin        180675 - Used 'getOpenCtxOfDivPopUp()' to call the method 'openDivPopup'.
 * 03/30/2016       mlm         184455 - Refactored to handle data hold form for FM.
 * 07/28/2017       wrong       186656 - Modified getDataEntryUrl() to add new parameter transactionLogId for url.
 * 09/01/2017       wrong       186656 - Added logic to check if current policy is configured to process output as job
 *                                       when clicking 'Data Entry'. If true, alert warning message.
 * ---------------------------------------------------
 */
var formCode = "";
var MANUSCRIPT_DATAENTRY_POPUP_ID = "manuscriptDataEntry";
var originalWidth = -1;
var originalHeight = -1;

function maintainManuscriptListGrid_setInitialValues() {
    // Ajax call to get initial values
    sendAJAXRequest("getInitialValuesForAddManuscript");
}

function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'SAVE':
            setInputFormField("riskId", policyHeader.riskHeader.riskId);
            setInputFormField("coverageId", policyHeader.coverageHeader.coverageId);
            document.forms[0].process.value = "saveAllManuscript";
            break;
        default:
            proceed = false;
    }
    return proceed;
}

function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'ADD_MANUSCRIPT':
            var url = getAppPath() + "/coveragemgr/manuscriptmgr/selectManuscript.do?" +
                      commonGetMenuQueryString();
            var processingDivId = getOpenCtxOfDivPopUp().openDivPopup("", url, true, true, "", "", "", "", "", "", "", false);
            break;
        case 'DETAIL':
            var url = getAppPath() + "/coveragemgr/manuscriptmgr/maintainManuscriptDetail.do?" +
                      commonGetMenuQueryString() +
                      "&manuscriptEndorsementId=" + maintainManuscriptListGrid1.recordset("ID").value +
                      "&formCode=" + maintainManuscriptListGrid1.recordset("CFORMCODE").value +
                      "&recordModeCode=" + maintainManuscriptListGrid1.recordset("CRECORDMODECODE").value +
                      "&officialRecordId=" + maintainManuscriptListGrid1.recordset("COFFICIALRECORDID").value +
                      "&afterImageRecordB=" + maintainManuscriptListGrid1.recordset("CAFTERIMAGERECORDB").value +
                      "&manuscriptEffectiveFromDate=" + maintainManuscriptListGrid1.recordset("CMANUSCRIPTEFFECTIVEFROMDATE").value +
                      "&manuscriptEffectiveToDate=" + maintainManuscriptListGrid1.recordset("CMANUSCRIPTEFFECTIVETODATE").value +
                      "&effectiveFromDate=" + maintainManuscriptListGrid1.recordset("CEFFECTIVEFROMDATE").value +
                      "&effectiveToDate=" + maintainManuscriptListGrid1.recordset("CEFFECTIVETODATE").value +
                      "&isDeleteAvailable=" + maintainManuscriptListGrid1.recordset("CISDELETEAVAILABLE").value;
            var processingDivId = getOpenCtxOfDivPopUp().openDivPopup("", url, true, true, "", "", "", "", "", "", "", false);
            break;
        case 'UPLOAD_RTF':
            if (isPageDataChanged()) {
                setInputFormField("saveManuscriptB", 'Y');
            }
            openFileUpload("", "", "RTF", "manuscript endorsement file", null, "N", "Y");
            break;
        case 'EXTRACT_RTF':
            var url = getAppPath() + "/coveragemgr/manuscriptmgr/maintainManuscript.do" +
                      "?manuscriptEndorsementId=" + maintainManuscriptListGrid1.recordset("ID").value +
                      "&process=loadAttachment" +
                      "&date=" + new Date();
            setWindowLocation(url);
            break;
        case 'DATAENTRY':
            if (isPageDataChanged()) {
                alert(getMessage("pm.maintainManu.dataEntry.saveFirst"));
                return;
            }
            if (getSysParmValue("OS_MANUSCRIPT_EXPORT") == "EXTRACT") {
                if (commonCheckIsJobBasedOutput()) {
                    alert(getMessage("cs.outputmgr.processOutput.job.based.output.warning", new Array("Data Entry")));
                    return;
                }
            }
            var url = getCSPath() + "/csProcessingIFrame.jsp";
            var dataEntryWindow = openPopup(url, "ManuscriptDataEntry", 1050, 750, '60', '60', 'yes');
            break;
    }
}

function getDataEntryUrl() {
    var formId = maintainManuscriptListGrid1.recordset("CFORMCODE").value;
    //First 2 characters are usually statecode in the configuration.
    //Strip off the 2 characters to match it up with form id configured in forms configuration.
    formId = formId.substring(2);
    var templateId = maintainManuscriptListGrid1.recordset("CTEMPLATEID").value;
    var manuscriptFormDescription = maintainManuscriptListGrid1.recordset("CFORMCODELOVLABEL").value;
    var manuscriptEndId = maintainManuscriptListGrid1.recordset("ID").value;
    var externalId = policyHeader.policyNo;
    var docGenPrdName = maintainManuscriptListGrid1.recordset("CDOCGENPRDNAME").value;
    var selectedTermId = policyHeader.termBaseRecordId;

    var dataEntryUrl = getCSPath() + "/outputmgr/processOutput.do?process=handleDataEntry" +
        "&manuscriptEndorsementId=" + manuscriptEndId + "&externalId=" + externalId +
        "&templateId=" + templateId + "&formId=" + formId + "&formType=MANUSCRIPT&termId="+ selectedTermId +
        "&termEff=" + policyHeader.termEffectiveFromDate +
        "&termExp=" + policyHeader.termEffectiveToDate  +
        "&docGenPrdName=" + docGenPrdName +
        "&subSystemCode=PMS" +
        "&formDesc=" + manuscriptFormDescription +
        "&transactionLogId=" + policyHeader.curTransactionId;
    return dataEntryUrl;
}

function addManuscripts(oManuscriptList, showMessage) {
    if (showMessage == null) {
        showMessage = true;
    }

    var manuSize = oManuscriptList.length;
    for (var i = 0; i < manuSize; i++) {
        var manuscript = oManuscriptList[i];
        addManuscript(manuscript, showMessage);
    }

}

function addManuscript(manuscript, showMessage) {
    formCode = manuscript.FORMCODE;
    // insert one empty record
    commonOnButtonClick('ADD');
}

function sendAJAXRequest(process) {
    // set url
    var url = "maintainManuscript.do?" + commonGetMenuQueryString() +
              "&process=" + process;

    switch (process) {
        case 'getInitialValuesForAddManuscript':
            url += "&formCode=" + formCode +
                   "&riskId=" + policyHeader.riskHeader.riskId +
                   "&coverageId=" + policyHeader.coverageHeader.coverageId;
            break;
    }

    // initiate call
    var ajaxResponseHandler = "handleOn" + process.substr(0, 1).toUpperCase() + process.substr(1);
    new AJAXRequest("get", url, "", eval(ajaxResponseHandler), false);
}

function handleOnGetInitialValuesForAddManuscript(ajax) {
    commonHandleOnGetInitialValues(ajax);
}

function reLoadPage() {
 var url = getAppPath() + "/coveragemgr/manuscriptmgr/maintainManuscript.do?"
                + commonGetMenuQueryString() + "&process=loadAllManuscript"
                + "&riskId=" + policyHeader.riskHeader.riskId
                + "&coverageId=" + policyHeader.coverageHeader.coverageId
                + "&coverageBaseRecordId=" + maintainManuscriptListGrid1.recordset("CCOVERAGEBASERECORDID").value
                + "&coverageEffectiveToDate=" + getObjectValue("coverageEffectiveToDate")
                + "&policyViewMode=" + getObjectValue("policyViewMode");
    setWindowLocation(url);
}

function openFileUploadDone(oResult) {
    // If file upload succeeds, import the file, otherwise display error message
    var rc = oResult.isSucceed;
    if (rc == "Y") {
        var url = getAppPath() + "/coveragemgr/manuscriptmgr/maintainManuscript.do";
        // update txtXML field
        modValue = getChanges(maintainManuscriptListGrid1);
        document.maintainManuscriptForm.txtXML.value = modValue;
        setInputFormField("manuscriptEndorsementId", maintainManuscriptListGrid1.recordset("ID").value);
        setInputFormField("recordModeCode", maintainManuscriptListGrid1.recordset("CRECORDMODECODE").value);
        setInputFormField("importFilePath", oResult.fileFullPath);
        postAjaxSubmit(url, "saveAttachment", false, false, handleOnUploadDone, false, false);
    }
    else {
        alert(oResult.errorMessage);
    }
}

function handleOnUploadDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            // do nothing if we don't have initial values or we got error
            if (!handleAjaxMessages(data, null)) {
                return;
            }
            reLoadPage();
        }
    }
}

function handleOnChange(obj) {
    //change renew indicator if effective to date is less than term expiration date
    if (obj.name == "effectiveToDate") {
        var effectiveToDate = obj.value;
        var policyExpirationDate = policyHeader.policyExpirationDate;
        resetRenewIndicator(effectiveToDate, policyExpirationDate, "renewB", "maintainManuscriptListGrid");
    }
    return true;
}

function handleOnUnloadForDivPopup(divPopFrame) {
    if (divPopFrame.parentElement.id == MANUSCRIPT_DATAENTRY_POPUP_ID) {
        window.setTimeout("resizeToOriginalDimension();", 100);
    }
}

function resizeToOriginalDimension() {
    var parentWindow = window.frameElement.document.parentWindow;
    var divPopupControl = parentWindow.getDivPopupFromDivPopupControl(window.frameElement);
    divPopupControl.parentElement.style.width = originalWidth;
    parentWindow.resizeJQueryDialog(divPopupControl, originalWidth, originalHeight);
}
