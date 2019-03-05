//-----------------------------------------------------------------------------
// JavaScript file for maintainPremiumAdjustment.jsp.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:
// Author:
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 07/12/2017       lzhang      186847 - Reflect grid replacement project changes
//-----------------------------------------------------------------------------
var coverageBaseRecordId;

function validateGrid() {
    return true;
}

function processSavePremiumAdjustment() {
    alternateGrid_update('premiumAdjustmentListGrid');
    if (!isChanged && !isPageGridsDataChanged()) {
        commonOnButtonClick('CLOSE_DIV');
    }
    var path = getAppPath() + "/transactionmgr/premiumadjustmentprocessmgr/maintainPremiumAdjustment.do?"
        + commonGetMenuQueryString();
    showProcessingDivPopup();
    postAjaxSubmit(path, "saveAllPremiumAdjustment", false, false, processAjaxResponseForSavePremiumAdjustment);
}


function processAjaxResponseForSavePremiumAdjustment(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data)) {
                return;
            }            // handle confirmations
            if (isConfirmationMessagesProcessed()) {
                repostAjaxSubmitWithConfirmationValue();
            }
            else {
                var currentRecord = root.getElementsByTagName("ROW").item(0)
                var processResult = currentRecord.selectNodes("SUCCESS")(0).text;
                if (processResult == 'N') {
                    handleError(getMessage("pm.maintainPremiumAdjustment.save.error"));
                }
                else {
                    handleError(getMessage("pm.maintainPremiumAdjustment.save.success"));
                    window.frameElement.document.parentWindow.refreshPage();
                }
            }
        }
    }
}

function coverageListGrid_selectRow(id) {
    coverageBaseRecordId = id;
    setTableProperty(eval("premiumAdjustmentListGrid"), "selectedTableRowNo", null);
    premiumAdjustmentListGrid_filter("CCOVERAGEBASERECORDID=" + id);
    var currentPremiumAdjustment = getXMLDataForGridName("premiumAdjustmentListGrid");
}

function premiumAdjustmentListGrid_selectRow(id) {
    var currentPremiumAdjustment = getXMLDataForGridName("premiumAdjustmentListGrid");
    if (currentPremiumAdjustment.recordset.recordcount > 1) {
        document.forms[0].componentSign.disabled = true;
    }
    else {
        document.forms[0].componentSign.readOnly = false;
    }
}

function handleOnChange(field) {
    if (field.name == 'componentSign') {
        var currentPremiumAdjustment = getXMLDataForGridName("premiumAdjustmentListGrid");
        var signValue = document.forms[0].componentSign.value;
        if (signValue == "1") {
            currentPremiumAdjustment.recordset("CSHORTDESCRIPTION").value = "Premium Debit";
        }
        else {
            currentPremiumAdjustment.recordset("CSHORTDESCRIPTION").value = "Premium Credit";
        }
    }
}

function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'DONE':
            processSavePremiumAdjustment();
            break;
    }
}
//-----------------------------------------------------------------------------
// Overwrite hideShowForm
//-----------------------------------------------------------------------------
function hideShowForm() {
    // Get the currently selected grid
    var currentGrid = "coverageListGrid";
    var currentTbl = getTableForXMLData(getXMLDataForGridName(currentGrid));
    var gridDetailDivId = getTableProperty(currentTbl, "gridDetailDivId");

    if (isStringValue(gridDetailDivId)) {
        if (hasObject(gridDetailDivId))
            hideShowElementByClassName(getSingleObject(gridDetailDivId),
                    ( (getTableProperty(currentTbl, "hasrows") == true)) ? false : true);
    }
}
//for goto error
function getParentGridId() {
    return "coverageListGrid";
}

function getChildGridId() {
    return "premiumAdjustmentListGrid";
}
