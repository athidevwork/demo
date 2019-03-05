//-----------------------------------------------------------------------------
// Javascript file for maintainBatchRenewalProcess.jsp.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   Sep 28, 2011
// Author: Unknown
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 09/28/2011       dzhang      Issue 123437 - Modified handleOnButtonClick to handel delete renewal WIPs and ReRate process.
// 02/16/2012       wfu         126027 - Added functions to use new sysparm to suppress printer selection page.
// 03/07/2013       adeng       138243 - Added new function hideShowButton() to hide/show Issue, Batch Print, ReRate,
//                                       and Delete WIP buttons when the exclusion indicator is on for all policies
//                                       or not in selected event.
// 04/25/2013       tcheng      143208 - Modify handleOnButtonClick to sync unchecked changes when clicking Issue/Merge/Delete WIP/ReRate/Batch Print.
// 08/04/2014       awu         156019 - Modified handleOnButtonClick to handle the Release Rate Letter process.
//-----------------------------------------------------------------------------
var renewalEventId;
var processCode;
var needReloadDetail = true;
var isAllExcludedB;

function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'SEARCH':
            document.forms[0].process.value = "loadAllRenewalEvent";
            submitForm(true);
            break;
        case 'ISSUEBATCHRENEWAL':
            if (canIssuePrintMergeProceed()) {
                // check if not select any record
                if (!isRenewalEventSelected()) {
                    handleError(getMessage("pm.batchRenewalProcess.noSelection.error"));
                    return;
                }

                // by default, the syncChanges only sync chnages to rows with update_ind = Y,
                // but for select, We need to tell the xmlProcs to sync Changes for rows selected
                // so we can get value into txtXML
                syncChanges(origbatchRenewalEventListGrid1, batchRenewalEventListGrid1, "CSELECT_IND = '-1' or CSELECT_IND = '0' ");

                document.forms[0].process.value = "saveIssueRenewalBatches";
                submitForm(true);
            }
            break;
        case 'PRINTBATCHRENEWAL':
            if (canIssuePrintMergeProceed()) {
                // check if not select any record
                if (!isRenewalEventSelected()) {
                    handleError(getMessage("pm.batchRenewalProcess.noSelection.error"));
                    return;
                }

                syncChanges(origbatchRenewalEventListGrid1, batchRenewalEventListGrid1, "CSELECT_IND = '-1' or CSELECT_IND = '0' ");

                // do validation and if passed, it will call captureRenewalEventPrinter() to display capture printer page.
                validatePrinterSelection();
            }
            break;
        case 'MERGEBATCHRENEWAL':
            if (canIssuePrintMergeProceed()) {
                // check if not select any record or only select one record
                if (!isRenewalEventSelected() || isOneRenewalEventSelected()) {
                    handleError(getMessage("pm.batchRenewalProcess.merge.selection.error"));
                    return;
                }
                syncChanges(origbatchRenewalEventListGrid1, batchRenewalEventListGrid1, "CSELECT_IND = '-1' or CSELECT_IND = '0' ");

                document.forms[0].process.value = "mergeIssueRenewalBatches";
                submitForm(true);
            }
            break;
        case 'DELETEBATCHRENEWAL':
            if (canIssuePrintMergeProceed()) {
                // check if not select any record
                if (!isRenewalEventSelected()) {
                    handleError(getMessage("pm.batchRenewalProcess.noSelection.error"));
                    return;
                }

                // by default, the syncChanges only sync chnages to rows with update_ind = Y,
                // but for select, We need to tell the xmlProcs to sync Changes for rows selected
                // so we can get value into txtXML
                syncChanges(origbatchRenewalEventListGrid1, batchRenewalEventListGrid1, "CSELECT_IND = '-1' or CSELECT_IND = '0' ");

                document.forms[0].process.value = "deleteRenewalWipBatches";
                submitForm(true);
            }
            break;
        case 'RERATEBATCHRENEWAL':
            if (canIssuePrintMergeProceed()) {
                // check if not select any record
                if (!isRenewalEventSelected()) {
                    handleError(getMessage("pm.batchRenewalProcess.noSelection.error"));
                    return;
                }

                // by default, the syncChanges only sync chnages to rows with update_ind = Y,
                // but for select, We need to tell the xmlProcs to sync Changes for rows selected
                // so we can get value into txtXML
                syncChanges(origbatchRenewalEventListGrid1, batchRenewalEventListGrid1, "CSELECT_IND = '-1' or CSELECT_IND = '0' ");

                document.forms[0].process.value = "rerateRenewalPolicyBatches";
                submitForm(true);
            }
            break;
        case 'RELEASE':
            if (canIssuePrintMergeProceed()) {
                // check if not select any record
                if (!isRenewalEventSelected()) {
                    handleError(getMessage("pm.batchRenewalProcess.noSelection.error"));
                    return;
                }

                if (isAllExcludedB == "Y") {
                    handleError(getMessage("pm.batchRenewalProcess.release.no.policy.info"));
                    return;
                }

                // by default, the syncChanges only sync chnages to rows with update_ind = Y,
                // but for select, We need to tell the xmlProcs to sync Changes for rows selected
                // so we can get value into txtXML
                syncChanges(origbatchRenewalEventListGrid1, batchRenewalEventListGrid1, "CSELECT_IND = '-1' or CSELECT_IND = '0' ");
                document.forms[0].process.value = "releaseOutput";
                submitForm(true);
            }
            break;
    }
}

function handleOnLoad() {
    if (!renewalEventId) {
        loadBatchRenewalDetails();
    }

    // disable selectAll checkbox
    if (hasObject("HCSELECT_IND")) {
        getObject("HCSELECT_IND").disabled = true;
    }
}

function loadBatchRenewalDetails(policyNoFilter, viewByFilter) {
    var frameObj = getObject("iframeEventDetails");
    if (typeof(frameObj) == "undefined") {
        return;
    }
    var url = getAppPath() + "/transactionmgr/batchrenewalprocessmgr/maintainBatchRenewalDetailProcess.do?"
        + commonGetMenuQueryString();

    if (!renewalEventId) {
        frameObj.src = url + "&process=maintainRenewalDetail";
    }
    else {
        url = url + "&process=loadAllRenewalDetail" +
                                      "&renewalEventId=" + renewalEventId +
                                      "&processCode=" + processCode;
        if(policyNoFilter){
            url = url + "&policyNoFilter=" + policyNoFilter;
        }
        if(viewByFilter) {
            url = url + "&viewByFilter=" + viewByFilter;
        }

        frameObj.src = url;
    }
}

function batchRenewalEventListGrid_selectRow(id) {
    // check if there any unsaved data (detail grid)
    if(renewalEventId == id){
        return;
    }
    if (renewalEventId) {
        if (isDetailDataChanged()) {
            if (!confirm(getMessage("pm.batchRenewalProcess.unsaved.data.error"))) {
                //set reload to no to keep current detail selections,and remember current renewalEventId
                needReloadDetail = false;
                var oldEventId = renewalEventId;
                selectRowById("batchRenewalEventListGrid", renewalEventId);
                //because after selectRowById,renewalEventId changed,should recover it back,and should
                //reset detailUpdated = Y
                setDetailUpdate("Y");
                renewalEventId = oldEventId;
                return;
            }
            else {
                setDetailUpdate("N");
                needReloadDetail = true;
            }
        }
    }

    renewalEventId = id;
    processCode = batchRenewalEventListGrid1.recordset("CPROCESSCODE").value;
    //if this time should not reload detail,then don't reload
    if(needReloadDetail){
        loadBatchRenewalDetails();
    }

}

function isDetailDataChanged() {
    var detailUpdated = getObjectValue("detailUpdated");
    return detailUpdated == 'Y';
}

//------------------------------------------------------------
// Check if there are any renewal event detail data changed
// when user want to move to anothter renewal event record.
//------------------------------------------------------------
function canIssuePrintMergeProceed() {
    var proceed = false;
    if (!isDetailDataChanged()) {
        proceed = true;
    }
    else {
        if (confirm(getMessage("pm.batchRenewalProcess.unsaved.data.error"))) {
            proceed = true;
        }
    }
    return proceed;
}

//------------------------------------------------------------
// Check that there is at least one renewal event record has been selected.
//------------------------------------------------------------
function isRenewalEventSelected() {
    var selectedXML = batchRenewalEventListGrid1.documentElement.selectNodes("//ROW[CSELECT_IND=-1]");
    return selectedXML.length != 0;
}

//------------------------------------------------------------
// Check that if there is one renewal event record has been selected.
//------------------------------------------------------------
function isOneRenewalEventSelected() {
    var selectedXML = batchRenewalEventListGrid1.documentElement.selectNodes("//ROW[CSELECT_IND=-1]");
    return selectedXML.length == 1;
}

function validatePrinterSelection() {
    sendAJAXRequest("validateForCaptureRenewalBatchPrinter");
}

function captureRenewalEventPrinter() {
    if (getSysParmValue("PM_BATCH_RENPRT") == 'N') {
        submitBatchPrintJob();
    } else {
        var url = getAppPath() + "/transactionmgr/batchrenewalprocessmgr/captureRenewalBatchPrinter.do?"
                  + commonGetMenuQueryString() + "&process=display&renewalEventId=" + renewalEventId;
        var divPopupId = openDivPopup("", url, true, true, "", "", "500", "400", "", "", "", false);
    }
}

function sendAJAXRequest(process) {
    // set url
    var url = "captureRenewalBatchPrinter.do?process=" + process +
              "&" + commonGetMenuQueryString();

    switch (process) {
        case 'validateForCaptureRenewalBatchPrinter':
            url += "&renewalEventId=" + renewalEventId;
            break;
    }

    // initiate call
    var ajaxResponseHandler = "handleOn" + process.substr(0, 1).toUpperCase() + process.substr(1);
    new AJAXRequest("get", url, "", eval(ajaxResponseHandler), false);
}

function handleOnValidateForCaptureRenewalBatchPrinter(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null))
                return;

            captureRenewalEventPrinter();
        }
    }
}
function setDetailUpdate(status){
    getObject("detailUpdated").value = status;
}
function setUnchanged(){
    batchRenewalEventListGrid1.recordset("UPDATE_IND").value = "N";
    isChanged = false;
}
function setForceReload(){
    needReloadDetail = true;
}

function submitBatchPrintJob() {
    var url = "captureRenewalBatchPrinter.do?process=saveSubmitPrintingJob" +
              "&renewalEventId=" + renewalEventId +
              "&date=" + new Date();
    new AJAXRequest("get", url, "", handleOnSubmitBatchPrintJob, false);
}

function handleOnSubmitBatchPrintJob(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null))
                return;
            // need to reload the page
            handleOnButtonClick("SEARCH");
        }
    }
}

//------------------------------------------------------------
// hide/show Issue, Batch Print, ReRate,and Delete WIP buttons
// when the exclusion indicator is on for all policies or not
// in selected completed event.
//------------------------------------------------------------
function hideShowButtons(isAllExcluded) {
    isAllExcludedB = isAllExcluded;
    //only when process code is PRERENEWAL, this function will be called.
    //if any changes below logic will be done for these buttons availability in the future
    if (isAllExcluded != 'Y' && batchRenewalEventListGrid1.recordset("CSTATUS").value == 'COMPLETE') {
        batchRenewalEventListGrid1.recordset("CISPRINTAVAILABLE").value = 'Y';
        batchRenewalEventListGrid1.recordset("CISISSUEAVAILABLE").value = 'Y';
        batchRenewalEventListGrid1.recordset("CISDELETEWIPAVAILABLE").value = 'Y';
        batchRenewalEventListGrid1.recordset("CISRERATEAVAILABLE").value = 'Y';
    }
    else {
        batchRenewalEventListGrid1.recordset("CISISSUEAVAILABLE").value = 'N';
        batchRenewalEventListGrid1.recordset("CISDELETEWIPAVAILABLE").value = 'N';
        batchRenewalEventListGrid1.recordset("CISRERATEAVAILABLE").value = 'N';
        batchRenewalEventListGrid1.recordset("CISPRINTAVAILABLE").value = 'N';
    }
    functionExists = eval("window.pageEntitlements");
    if (functionExists) {
        pageEntitlements(true, 'batchRenewalEventListGrid');
    }
}
