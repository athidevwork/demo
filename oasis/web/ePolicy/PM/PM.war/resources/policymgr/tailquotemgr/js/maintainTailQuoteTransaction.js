//-----------------------------------------------------------------------------
// Javascript file for maintainTailQuoteTransaction.jsp.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   Match 20, 2013
// Author:  adeng
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 03/14/2013       adeng       142688 - 1)Added hideShowFilterTailListPanel() to hide/show filter div and tail list panel.
//                                       2)Modified handleOnLoad() to hide filter div and tail list panel when
//                                       transactionListGrid is empty.
//                                       3)Modified transactionListGrid_setInitialValues() to display filter div and
//                                       tail list panel.
// 03/10/2017       wli         180675 - 1)Initialized the parameter which named "autoSaveResultType" and call
//                                         syncResultToParent() in the method which named "submitSave" for UI change.
//                                       2)Added condition when call handleOnLoad() for UI change.
// 07/12/2017       lzhang      186847   Reflect grid replacement project changes
//-----------------------------------------------------------------------------
var selectedTailQuoteTransactionRowId;
function handleOnButtonClick(asBtn) {
    var tailQuoteFrame = window.frames['iframeSeparateTailQuotes'];
    switch (asBtn) {
        case 'PROCESS':
            if (checkUnsavedTailQuoteData())
                submitSave("performProcessTailQuoteTransaction");
            break;
        case 'FILTER':
            tailQuoteFrame.filterQuoteTailData();
            break;
        case 'CLEAR':
            tailQuoteFrame.clearFilter();
            break;
        case 'CLOSE_TAILQUOTE':
            if (checkUnsavedTailQuoteData()) {
                commonOnButtonClick("CLOSE_DIV");
            }
            break;
    }
}

function submitSave(processValue) {
    if (commonValidateForm() && commonValidateGrid('transactionListGrid')) {
        alternateGrid_update("transactionListGrid");
        setInputFormField("processCode", 'SAVE');
        document.forms[0].process.value = processValue;
        showProcessingDivPopup();
		autoSaveResultType = commonOnSubmitReturnTypes.submitSuccessfully;
        submitFirstForm();
    }else {
        autoSaveResultType = commonOnSubmitReturnTypes.commonValidationFailed;
    }
    syncResultToParent(autoSaveResultType);
}

function addTailQuote() {
    if (checkUnsavedTailQuoteData()) {
        commonAddRow("transactionListGrid");
    }
}

function transactionListGrid_setInitialValues() {
    currentlySelectedGridId = "transactionListGrid";

    var url = getAppPath() + "/policymgr/tailquotemgr/maintainTailQuote.do?"
        + commonGetMenuQueryString() + "&process=getInitialValuesForTailQuoteTransaction";

    new AJAXRequest("get", url, '', commonHandleOnGetInitialValues, false);

    //display filter div and tail list panel
    hideShowFilterTailListPanel('block');
    setInputFormField("isAddAvailable", "N");
    setInputFormField("isProcessAvailable", "Y");
}

function transactionListGrid_selectRow(id) {
    if (selectedTailQuoteTransactionRowId == id) {
        return;
    }

    if (checkUnsavedTailQuoteData()) {
        selectedTailQuoteTransactionRowId = id;
        loadSeparateTailQuotes();
    }else{
        selectRowById("transactionListGrid", selectedTailQuoteTransactionRowId);
    }
}

function loadSeparateTailQuotes() {
    var tranGridXMLData = getXMLDataForGridName("transactionListGrid");
    var url = getAppPath() + "/policymgr/tailquotemgr/maintainTailQuote.do?"
        + commonGetMenuQueryString() + "&process=loadAllTailQuote";
    url = url + "&tailQuoteTransactionLogId=" + tranGridXMLData.recordset("ID").value
    getObject("iframeSeparateTailQuotes").src = url;

}

function checkUnsavedTailQuoteData() {
    var tailQuoteFrame = window.frames['iframeSeparateTailQuotes'];
    if (eval("window.frames['iframeSeparateTailQuotes'].isPageGridsDataChanged") &&
        tailQuoteFrame.isPageGridsDataChanged()) {
        if (!confirm(getMessage("pm.maintainTailQuote.saveChange.prompt"))) {
            return false;
        }
    }
    return true;
}

function handleOnLoad() {
    setInputFormField("needToHandleExitWorkFlow", "Y");
    if (isExeInvokeWorkFlow()) {
        invokeWorkflow();
    }

    //hide filter div and tail list panel when transactionListGrid is empty
    if(isEmptyRecordset(getXMLDataForGridName("transactionListGrid").recordset)) {
        hideShowFilterTailListPanel('none');
    }
}

var isInHandleExitWorkflow = false;
function handleExitWorkflow() {
    isInHandleExitWorkflow = true;
    refreshPage();
    isInHandleExitWorkflow = false;
}

function getMenuQueryString(id, url) {
    var tempUrl = "";
    if (isInHandleExitWorkflow) {
        tempUrl = "&process=loadAllTailQuoteTransaction";
    }
    return tempUrl;
}

function hideShowFilterTailListPanel(style) {
    if (style == 'none'){
        hideShowElementByClassName(getObject("filterDiv"), true);
        hideShowElementByClassName(getObject("tailListPanel"), true);
    }else{
        hideShowElementByClassName(getObject("filterDiv"), false, style);
        hideShowElementByClassName(getObject("tailListPanel"), false, style);
    }
}

