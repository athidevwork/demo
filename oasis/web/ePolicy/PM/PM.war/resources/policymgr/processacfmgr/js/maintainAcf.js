//-----------------------------------------------------------------------------
// Java script file for maintainAcf.jsp
//
// (C) 2011 Delphi Technology, inc. (dti)
// Date:   Mar 29, 2011
// Author: syang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 03/15/2012       syang       128939 - Disable checkbox on the top of grid if the page is readonly.
// 03/16/2012       syang       131636 - Removed the logic of collapsing overrides panel.
// 02/19/2013       adeng       137009 - Added Function filterData() and clearFilter().
// 06/14/2013       adeng       144987 - Added function getTotalValue() to count total value of column.
// 09/27/2013       adeng       148169 - Modified secondGrid_setInitialValues() to set the initial
//                                       value for external_id.
// 07/12/2017       lzhang      186847 - Reflect grid replacement project changes
//-----------------------------------------------------------------------------
var isAddNewOverride = false;
function handleOnLoad(){
    // Disable the "All" checkbox on the top of grid.
    if(hasObject("isSaveAvailable") && getObjectValue("isSaveAvailable") == 'N' && hasObject("chkCSELECT_ALL")){
        getObject("chkCSELECT_ALL").disabled = true;
    }
}
function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case "ADD_OVERRIDE":
            handleOnAddOverride(false);
            break;
        case 'ADD_NEW':
            handleOnAddOverride(true);
            break;
        case 'DELETE_OVERRIDE':
            commonDeleteRow("secondGrid");
            break;
        case 'ADD_FEE':
            commonAddRow("fourthGrid");
            break;
        case 'DELETE_FEE':
            commonDeleteRow("fourthGrid");
            break;
        case 'CLEAR':
            clearFilter();
            break;
        case 'FILTER':
            filterData();
            break;
    }
}

//-----------------------------------------------------------------------------
// To handle the submit event
//-----------------------------------------------------------------------------
function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'SAVE':
            document.forms[0].process.value = "saveAllAcf";
            break;
        default:
            proceed = false;
    }
    return proceed;
}

//-----------------------------------------------------------------------------
// Instruct submit data for multiple grids
//-----------------------------------------------------------------------------
function submitMultipleGrids() {
    return true;
}

function firstGridFormList_btnClick(asBtn) {
    currentlySelectedGridId = "firstGrid";
    updateAllSelectInd(asBtn);
}

function handleOnAddOverride(isNew) {
    isAddNewOverride = isNew;
    if (isNew) {
        commonAddRow("secondGrid");
    }
    else {
        var selectedCount = 0;
        first(firstGrid1);
        while (!firstGrid1.recordset.eof) {
            if (firstGrid1.recordset("CSELECT_IND").value == -1) {
                selectedCount ++;
                commonAddRow("secondGrid");
            }
            next(firstGrid1);
        }
        first(firstGrid1);
        if (selectedCount == 0) {
            handleError(getMessage("pm.processAcf.add.override.noProductSelected"));
        }
        // Un-check the select all.
        getObject("chkCSELECT_ALL").checked = false;
    }
}

function secondGrid_setInitialValues() {
    var url = getAppPath() + "/policymgr/processacfmgr/maintainAcf.do?"
            + commonGetMenuQueryString() + "&process=getInitialValuesForOverride";

    if (!isAddNewOverride) {
        var entityId = firstGrid1.recordset("CENTITYID").value;
        var allocType = firstGrid1.recordset("CALLOCTYPE").value;
        var allocAmt = firstGrid1.recordset("CALLOCAMT").value;
        var commType = firstGrid1.recordset("CCOMMTYPE").value;
        var commAmt = firstGrid1.recordset("CCOMMAMT").value;
        var productBrokerageId = firstGrid1.recordset("ID").value;
        var overrideExternalId = firstGrid1.recordset("CEXTERNALID").value;
        url = url + "&overrideEntityId=" + entityId + "&overrideAllocType=" + allocType + "&overrideAllocAmt=" + allocAmt
                + "&overrideCommType=" + commType + "&overrideCommAmt=" + commAmt + "&productBrokerageId=" + productBrokerageId
                + "&overrideExternalId=" + overrideExternalId;
    }
    currentlySelectedGridId = "secondGrid";
    new AJAXRequest("get", url, '', commonHandleOnGetInitialValues, false);
}

function fourthGrid_setInitialValues() {
    var url = getAppPath() + "/policymgr/processacfmgr/maintainAcf.do?"
            + commonGetMenuQueryString() + "&process=getInitialValuesForFee";
    var termType = firstGrid1.recordset("CTERMTYPECODE").value;
    url = url + "&termType=" + termType;
    currentlySelectedGridId = "fourthGrid";
    new AJAXRequest("get", url, '', commonHandleOnGetInitialValues, false);
}
//-----------------------------------------------------------------------------
// The allocation amount field should be null if "PREM_DIFF" is selected.
//-----------------------------------------------------------------------------
function handleOnChange(obj) {
    if (obj.name == 'overrideAllocType' && obj.value == 'PREM_DIFF') {
        getObject("overrideAllocAmt").value = "";
    }
}
//-----------------------------------------------------------------------------
// Override setFocusToFirstField() to don't put focus to the edit field.
//-----------------------------------------------------------------------------
function setFocusToFirstField(){

}

//-----------------------------------------------------------------------------
// Filter data by risk criteria
//-----------------------------------------------------------------------------
function filterData() {
    var riskFilter = getObjectValue("riskFilter");
    var overrideFilterStr = "";
    var resultFilterStr = "";

    if (riskFilter != null && riskFilter != "") {
        overrideFilterStr = "contains('" + riskFilter + "',COVERRIDERISKID)";
        resultFilterStr = "contains('" + riskFilter + "',CRISKID)";
    }
    // must set selectedTableRowNo property to null, else it will go to wrong logic in common.js userReadyStateReady() function.
    setTableProperty(eval("secondGrid"), "selectedTableRowNo", null);
    //Filter Override data by risk criteria
    secondGrid_filter(overrideFilterStr);
    if (isEmptyRecordset(secondGrid1.recordset)) {
        hideEmptyTable(getTableForXMLData(secondGrid1));
        hideGridDetailDiv("secondGrid");
    }
    else {
        showNonEmptyTable(getTableForXMLData(secondGrid1));
        reconnectAllFields(document.forms[0]);
        hideShowElementByClassName(getObject("secondGrid"), false);
    }

    // must set selectedTableRowNo property to null, else it will go to wrong logic in common.js userReadyStateReady() function.
    setTableProperty(eval("thirdGrid"), "selectedTableRowNo", null);
    //Filter Result data by risk criteria
    thirdGrid_filter(resultFilterStr);
    if (isEmptyRecordset(thirdGrid1.recordset)) {
        hideEmptyTable(getTableForXMLData(thirdGrid1));
    }
    else {
        showNonEmptyTable(getTableForXMLData(thirdGrid1));
    }
    var transTivTotal=getTotalValue("CTRANSACTIONALTIV",false);
    getSingleObject("transTivTotalROSPAN").innerHTML = transTivTotal;
    getObject("transTivTotal").value= transTivTotal;

    var allocAmtTotal=getTotalValue("CRTALLOCAMT",true);
    getSingleObject("allocAmtTotalROSPAN").innerHTML = allocAmtTotal;
    getObject("allocAmtTotal").value= allocAmtTotal;

    var commAmtTotal=getTotalValue("CRTCOMMAMT",true);
    getSingleObject("commAmtTotalROSPAN").innerHTML = commAmtTotal;
    getObject("commAmtTotal").value= transTivTotal;

    var transWPTotal=getTotalValue("CDELTAAMT",false);
    getSingleObject("transWPTotalROSPAN").innerHTML = transWPTotal;
    getObject("transWPTotal").value= transWPTotal;
}

//-----------------------------------------------------------------------------
// clears all filter criteria and redisplays all data
//-----------------------------------------------------------------------------
function clearFilter() {
    var riskFilterArray = getSingleObject("riskFilter");
    var x = riskFilterArray.options.length;
    if (riskFilterArray.options[0].value == "") {
        riskFilterArray.options[0].selected = true;
    }
    else {
        riskFilterArray.options[0].selected = false;
    }
    for (var y = 1; y < x; y++) {
        riskFilterArray.options[y].selected = false;
    }
    getObject("riskFilterMultiSelectText").value = "-SELECT-";
    filterData();
}

//-----------------------------------------------------------------------------
// Get total value by column Id in data island.
// For the Transactional TIV and Transaction Written Premium columns the totals
// are sum of the amount once per transaction and risk.
//-----------------------------------------------------------------------------
function getTotalValue(columnId,countDup) {
    var result = 0;
    var records = thirdGrid1.documentElement.selectNodes("//ROW[DISPLAY_IND = 'Y']");
    var s = "";
    for (i = 0; i < records.length; i++) {
        currentRecord = records.item(i);
        var theString = currentRecord.selectNodes("CTRANSACTIONLOGID")(0).text + "_" + currentRecord.selectNodes("CRISKID")(0).text;
        if (countDup || s.indexOf(theString) == -1) {
            s = s + "," + theString;
            var sValue = unformatMoneyStrValAsStr(currentRecord.selectNodes(columnId)(0).text);
            if (!isEmpty(sValue) && isSignedFloat(sValue)) {
                result = result + parseFloat(sValue);
            }
        }
    }
    return formatMoneyStrValAsStr(result);
}