//-----------------------------------------------------------------------------
// Javascript file for maintainTransaction.jsp.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   July 06, 2010
// Author: syang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 07/06/2010       syang       103797 - Handled to open the Entity Detail page from transaction page.
// 08/12/2010       dzhang      103800 - Handled to open the Process Quick Pay page from transaction page.
// 03/17/2011       fcb        112664 & 107021 - case EXCEL_WORKSHEET added.
// 10/07/2011       fcb         107021 - passed also the filter values that are used on Premium Screen to the report.
//                                       The transaction screen does not have filter, but we must initialize these
//                                       values in order for the report to not fail.
// 10/11/2011       fcb         125838 - Changes due to move of filtering of data from JS to DB
// 02/19/2013       jshen       141982 - 1. Use current policy term base Id to load term transaction data when clicking Show Term button.
//                                       2. Modify showTerm() to call loadAllTransaction method instead of loadTransactionByTerm.
//                                       3. Changed transaction information grid anchor column name from transactionLogId to rowNo.
// 07/20/2015       tzeng       164086 - Added handleReadyStateReady() to set negative value to red.
// 10/27/2016       ssheng      180643 - Added common validation function validateTransactionForPremium,
//                                       use common validation when export excel worksheet or open premium pop up.
// 07/26/2017       lzhang      182246 - Clean up unsaved message for page changes
//-----------------------------------------------------------------------------
var url = getAppPath()+"/transactionmgr/maintainTransaction.do?";
var SHOW_ALL = "PM_VIEW_TRANS_SHOW_ALL";
var SHOW_TERM= "PM_VIEW_TRANS_SHOW_TERM";
var SHOW_WHAT = "showAllOrShowTerm";
var TERM = "term";
var ALL = "all";
var currentTransactionId = "";
var reportCode = 'PM_PREMIUM_WORKSHEET';
function handleOnLoad(){
    //get the query string to find what to show
    var query = window.location.search.substring(0);
    //if show all
    if(query.indexOf(SHOW_WHAT+"="+ALL)>0){
        hideShowField(getObject(SHOW_ALL),true);
    }
    //default is show term
    else{
        hideShowField(getObject(SHOW_TERM),true);
    }
    //105611, select the selected transaction in view cancellation detail page.
    if(!isEmpty(getObjectValue("selectedTransactionLogId"))){
        selectRowById("transactionGrid", getObjectValue("selectedTransactionLogId"));
    }
}
function transactionGrid_selectRow(id) {
    var selectedDataGrid = getXMLDataForGridName("transactionGrid");
    var policyNo = getObjectValue("policyNo");
    var transactionLogId = selectedDataGrid.recordset("CTRANSACTIONLOGID").value;
    currentTransactionId = transactionLogId;
    var termBaseId = selectedDataGrid.recordset("CTERMBASERECORDID").value;
    loadChangeDetails(policyNo,transactionLogId);
    loadTransactionForm(policyNo,termBaseId,transactionLogId);

}

function loadChangeDetails(policyNo,transId) {
    var changeDetailUrl = url + "process=loadChangeDetail";
    changeDetailUrl += "&policyId=" + policyHeader.policyId;
    changeDetailUrl += "&transactionId=" + transId;
    changeDetailUrl +="&termEffectiveDate="+policyHeader.termEffectiveFromDate;
    changeDetailUrl +="&termExpirationDate="+policyHeader.termEffectiveToDate;

    // need the follwing fields passed within request
    getObject("iframeChangeDetails").src = changeDetailUrl;
}
function loadTransactionForm(policyNo,termBaseId,transId) {
    var transFormUrl = url + "process=loadTransactionForm";
    transFormUrl += "&termBaseRecordId=" + termBaseId;
    transFormUrl += "&policyId=" + policyHeader.policyId;
    transFormUrl += "&transactionId=" + transId;
    // need the follwing fields passed within request
    getObject("iframeTransactionForms").src = transFormUrl;
}
function handleOnButtonClick(btn) {
    var proceed = true;
    switch (btn) {
        case 'SHOW_ALL':
            showAll();
        break;
    case 'SHOW_TERM':
        showTerm();
    break;
    case 'HISTORY':
        showHistory();
    break;
    case 'PREMIUM':
        validateTransactionForPremium('showPremium');
        break;
    case 'WORKSHEET':
        validateTransactionForPremium('validateTransactionForPremiumWorksheetDone');
        break;
    case 'EXCEL_WORKSHEET':
        validateTransactionForPremium('validateTransactionForPremiumExcelWorksheetDone');
        break;
    case 'ACCOUNT':
        var selectedDataGrid = getXMLDataForGridName("transactionGrid");
        var policyId = selectedDataGrid.recordset("CPOLICYID").value;
        var termEffectiveDate = selectedDataGrid.recordset("CTERMBASEEFFECFROMDATE").value;
        var termExpirationDate = selectedDataGrid.recordset("CTERMBASEEFFECTODATE").value;
        var transEffDate = selectedDataGrid.recordset("CTRANSEFFECFROMDATE").value;
        var accountingDate = selectedDataGrid.recordset("CACCOUNTINGFROMDATE").value;
        var accountingUrl = getAppPath() + "/policymgr/premiummgr/viewPremiumAccounting.do?"
            + "&process=getInitialValuesForPremiumAccounting" + "&policyId="+policyId
            + "&termEffectiveDate="+termEffectiveDate + "&termExpirationDate="+termExpirationDate
            + "&transEffDate="+transEffDate +"&accountingDate="+accountingDate;
        var divPopupId = openDivPopup("", accountingUrl, true, true, "820", "600", "", "", "", "", "", false);
        break;
    case 'MXS_PREM':
        var selectedDataGrid = getXMLDataForGridName("transactionGrid");
        var transLogId = selectedDataGrid.recordset("CTRANSACTIONLOGID").value;
        var url = getAppPath() + "/coveragemgr/excesspremiummgr/maintainExcessPremium.do?" +
                      commonGetMenuQueryString() + "&transactionLogId=" + transLogId + "&fromCoverage=N";
        window.frameElement.document.parentWindow.openDivPopup("", url, true, true, "", "", "950", "780", "", "", "", false);
        break;
        case 'ENTITYDTL':
            var saveRequired = commonSaveRequiredToChangePages("Policy Transaction", "Professional Entity Details", "N", "");
            if (saveRequired) {
                break;
            }
            else {
                var selectedDataGrid = getXMLDataForGridName("transactionGrid");
                var policyId = policyHeader.policyId;
                var transactionLogId = selectedDataGrid.recordset("CTRANSACTIONLOGID").value;
                var termBaseRecordId = selectedDataGrid.recordset("CTERMBASERECORDID").value;
                var termEffectiveFromDate = policyHeader.termEffectiveFromDate;
                var termEffectiveToDate = policyHeader.termEffectiveToDate;
                var url = getAppPath() + "/transactionmgr/viewProfessionalEntityDetail.do?policyId=" + policyId +
                          "&transactionLogId=" + transactionLogId + "&termBaseRecordId=" + termBaseRecordId +
                          "&termEffectiveFromDate=" + termEffectiveFromDate + "&termEffectiveToDate=" + termEffectiveToDate;
                var divPopupId = openDivPopup("", url, true, true, "", "", 850, 700, 842, 672, "", false);
            }
            break;

        case "GIVEQPPERCENT":
            var selectedDataGrid = getXMLDataForGridName("transactionGrid");
            var policyId = selectedDataGrid.recordset("CPOLICYID").value;
            var termBaseId = selectedDataGrid.recordset("CTERMBASERECORDID").value;
            var transactionLogId = selectedDataGrid.recordset("CTRANSACTIONLOGID").value;
            var url = getAppPath() + "/policymgr/quickpaymgr/processQuickPay.do?"
                    + "&process=loadAllQuickPay"
                    + "&policyId=" + policyId
                    + "&termBaseId=" + termBaseId
                    + "&transactionLogId=" + transactionLogId
                    + "&openMode=GIVEQPPERCENT";
            var divPopupId = openDivPopup("", url, true, true, "", "", 850, 700, 842, 672, "", false);
            break;
        case "GIVEQPDISCOUNT":
            var selectedDataGrid = getXMLDataForGridName("transactionGrid");
            var policyId = selectedDataGrid.recordset("CPOLICYID").value;
            var termBaseId = selectedDataGrid.recordset("CTERMBASERECORDID").value;
            var transactionLogId = selectedDataGrid.recordset("CTRANSACTIONLOGID").value;
            var url = getAppPath() + "/policymgr/quickpaymgr/processQuickPay.do?"
                    + "&process=loadAllQuickPay"
                    + "&policyId=" + policyId
                    + "&termBaseId=" + termBaseId
                    + "&transactionLogId=" + transactionLogId
                    + "&openMode=GIVEQPDISCOUNT";
            var divPopupId = openDivPopup("", url, true, true, "", "", 850, 700, 842, 672, "", false);
            break;
        case "REMOVEQP":
            var selectedDataGrid = getXMLDataForGridName("transactionGrid");
            var policyId = selectedDataGrid.recordset("CPOLICYID").value;
            var termBaseId = selectedDataGrid.recordset("CTERMBASERECORDID").value;
            var transactionLogId = selectedDataGrid.recordset("CTRANSACTIONLOGID").value;
            var url = getAppPath() + "/policymgr/quickpaymgr/processQuickPay.do?"
                    + "&process=loadAllQuickPay"
                    + "&policyId=" + policyId
                    + "&termBaseId=" + termBaseId
                    + "&transactionLogId=" + transactionLogId
                    + "&openMode=REMOVE";
            var divPopupId = openDivPopup("", url, true, true, "", "", 850, 700, 842, 672, "", false);
            break;
    }
    return proceed;
}
function handleOnSubmit(btn){
    var proceed = false;
    document.forms[0].process.value = "saveTransactionDetail";
    if (isSaveInProgress() == false) {
        //checks if the value is valid
        showProcessingDivPopup();
        // Enable and hide all disabled fields in a form before submit
        enableFieldsForSubmit(document.forms[0]);
        //dynamicly add a hidden field,to store data
        alternateGrid_update('transactionGrid');
        baseOnSubmit(document.transactionForm);
    }
    else {
        handleError(getMessage("pm.transaction.save.error"));
    }
    return proceed;
}

function showTerm() {
    var termBaseRecordId = getObjectValue("termBaseRecordId");
    var newURL = url + commonGetMenuQueryString()
            + "&process=loadAllTransaction"
            + "&termBaseRecordId=" + termBaseRecordId
            + "&" + SHOW_WHAT + "=" + TERM;
    setWindowLocation(newURL);
}

function showAll() {
    var newURL = url + commonGetMenuQueryString()
            + "&process=loadAllTransaction"
            + "&" + SHOW_WHAT + "=" + ALL;
    setWindowLocation(newURL);
}

function showPremium(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null))
                return;
            // If there is no error message, system will open transaction pop up.
            var selectedDataGrid = getXMLDataForGridName("transactionGrid");
            var termBaseId = selectedDataGrid.recordset("CTERMBASERECORDID").value;
            var viewPremiumUrl = getAppPath() + "/policymgr/premiummgr/viewPremium.do?"
                    + commonGetMenuQueryString() + "&process=loadAllPremium"
                    + "&transactionId="+currentTransactionId
                    + "&termBaseRecordId="+termBaseId;
            var divPopupId = openDivPopup("", viewPremiumUrl, true, true, "15", "", 900, 600, 892, 592, "", false);
        }
    }
}

function showHistory(){
      var viewAuditUrl = getAppPath() + "/transactionmgr/auditmgr/viewAudit.do?"
                + commonGetMenuQueryString() + "&process=loadAllAudit"+"&fromPage=transaction-transaction"+"&contextId="+currentTransactionId;
            var divPopupId = openDivPopup("", viewAuditUrl, true, true, "15", "", "", "", "", "", "", false);
}

function validateTransactionForPremiumWorksheetDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null))
                return;
            // If there is no error message, system will generate pdf report.
            var selectedDataGrid = getXMLDataForGridName("transactionGrid");
            var policyId = selectedDataGrid.recordset("CPOLICYID").value;
            var termBaseId = selectedDataGrid.recordset("CTERMBASERECORDID").value;
            var tranLogId = selectedDataGrid.recordset("CTRANSACTIONLOGID").value;
            var termEffDate = selectedDataGrid.recordset("CTERMBASEEFFECFROMDATE").value;
            var termExpDate = selectedDataGrid.recordset("CTERMBASEEFFECTODATE").value;
            var paramsObj = new Object();
            paramsObj.reportCode = reportCode;
            paramsObj.policyId = policyId;
            paramsObj.termBaseId = termBaseId;
            paramsObj.tranLogId = tranLogId;
            paramsObj.termEff = termEffDate;
            paramsObj.termExp = termExpDate;
            viewPolicyReport(paramsObj);
        }
    }
}

function validateTransactionForPremiumExcelWorksheetDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null))
                return;
            // If there is no error message, system will generate excel report.
            var selectedDataGrid = getXMLDataForGridName("transactionGrid");
            var policyId = selectedDataGrid.recordset("CPOLICYID").value;
            var termId = selectedDataGrid.recordset("CTERMBASERECORDID").value;
            var transactionId = selectedDataGrid.recordset("CTRANSACTIONLOGID").value;
            var paramsObj = new Object();
            paramsObj.reportCode = "PM_VIEW_PREMIUM_EXCEL";
            paramsObj.policyId = policyId;
            paramsObj.termId = termId;
            paramsObj.transactionId = transactionId;
            paramsObj.premiumType = 'ALL';
            paramsObj.changeRecord = 'ALL';
            paramsObj.riskBaseRecordId = '-1';
            if(gridExportExcelCsvType=='XLSX')
                viewPolicyReport(paramsObj, "XLSX");
            else if(gridExportExcelCsvType=='XLS')
                viewPolicyReport(paramsObj, "XLS");
            else
                viewPolicyReport(paramsObj, "CSV");
        }
    }
}

function validateTransactionForPremium(callback){
    var currentTermBaseId = getObjectValue("termBaseRecordId");
    var selectedDataGrid = getXMLDataForGridName("transactionGrid");
    var policyId = selectedDataGrid.recordset("CPOLICYID").value;
    var termBaseId = selectedDataGrid.recordset("CTERMBASERECORDID").value;
    var tranLogId = selectedDataGrid.recordset("CTRANSACTIONLOGID").value;
    var termEffDate = selectedDataGrid.recordset("CTERMBASEEFFECFROMDATE").value;
    var termExpDate = selectedDataGrid.recordset("CTERMBASEEFFECTODATE").value;
    var url = getAppPath() + "/policymgr/premiummgr/maintainPremiumWorkSheet.do?"
            + "&process=validateTransactionForPremiumWorksheet" + "&currentTermBaseId="+currentTermBaseId
            + "&policyId=" + policyId + "&termBaseId=" + termBaseId + "&tranLogId=" + tranLogId
            + "&termEff=" + termEffDate + "&termExp=" + termExpDate + "&reportCode=" + reportCode;

    if (callback == 'showPremium')
        new AJAXRequest("get", url, '', showPremium, false);
    else if (callback == 'validateTransactionForPremiumWorksheetDone')
        new AJAXRequest("get", url, '', validateTransactionForPremiumWorksheetDone, false);
    else if (callback == 'validateTransactionForPremiumExcelWorksheetDone')
        new AJAXRequest("get", url, '', validateTransactionForPremiumExcelWorksheetDone, false);
}

function handleReadyStateReady() {
    setNegativeRed(getTableForXMLData(transactionGrid1));
}
