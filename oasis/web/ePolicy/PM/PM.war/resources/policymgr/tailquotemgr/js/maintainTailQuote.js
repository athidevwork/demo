//-----------------------------------------------------------------------------
// JavaScript file for tail quote.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:
// Author:
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 07/12/2017       lzhang      186847 - Reflect grid replacement project changes
//-----------------------------------------------------------------------------
function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'SAVE':
            submitSave("saveAllTailQuote");
            break;
    }
}

function submitSave(processValue) {
    if (commonValidateForm() && commonValidateGrid('tailListGrid')) {
        alternateGrid_update('tailListGrid');
        setInputFormField("processCode", 'SAVE');
        var tranGridXMLData = window.frameElement.document.parentWindow.getXMLDataForGridName("transactionListGrid");
        setInputFormField("tailQuoteTransactionLogId", tranGridXMLData.recordset("ID").value);
        document.forms[0].process.value = processValue;
        showProcessingDivPopup();
        submitFirstForm();
    }
}

function clearFilter() {
    window.frameElement.document.parentWindow.getObject("riskTypeFilter").value = "";
    window.frameElement.document.parentWindow.getObject("riskNameFilter").value = "";

    filterQuoteTailData();
}

function filterQuoteTailData() {
    var riskTypeCodeValue = window.frameElement.document.parentWindow.getObjectValue("riskTypeFilter");
    var riskBaseIdValue = window.frameElement.document.parentWindow.getObjectValue("riskNameFilter");
    var filterStr = "";
    filterStr = addFilterCondition(filterStr, "CRISKTYPECODE", "=", riskTypeCodeValue);
    filterStr = addFilterCondition(filterStr, "CRISKBASERECORDID", "=", riskBaseIdValue);
    filterStr = addFilterCondition(filterStr, "UPDATE_IND", "!=", "D");
    // must set selectedTableRowNo property to null, else it will go to wrong logic in common.js userReadyStateReady() function.
    setTableProperty(eval("tailListGrid"), "selectedTableRowNo", null);
    tailListGrid_filter(filterStr);

    var tailXmlData = getXMLDataForGridName("tailListGrid");
    if (!isEmptyRecordset(tailXmlData.recordset)) {
        showNonEmptyTable(getTableForXMLData(tailXmlData));
        reconnectAllFields(document.forms[0]);
        hideShowElementByClassName(getObject("tailDetailDiv"), false);
    }
}