//-----------------------------------------------------------------------------
// for tail
// Javascript file for maintainTail.js
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   Aug 18, 2010
// Author: gchitta
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 08/18/2010       gchitta     issue 106055 - Modified handleOnButtonClick to add a case for FINCHARGE
// 08/30/2010       syang       Issue 111417 - Modified handleOnLoad() to set isInOtherWF to true
//                                             if the page is in the cancellation tail workflow.
// 11/01/2010       dzhang      Issue 113002 - Modified coverageListGrid_selectRow() & filterTailData().
// 11/18/2010       syang       Issue 114279 - System should call installmentFinanceCharge() when "FINCHARGE" is clicked.
// 01/28/2011       fcb         117193 - document.all(gridDetailDivId) replaced with hasObject(gridDetailDivId)
// 03/15/2011       jshen       Issue 118616 - Allow modify Gross Premium field in prior term.
// 04/28/2011       dzhang      Issue 117525 - Modified filterTailData() and added a new method filterTailDataDone() to
//                                             make sure the close page action calls after filter done.
// 05/12/2011       syang       120617 - It is unnecessary to filter when closing page.
// 06/12/2012       ryzhao      123721 - Modified handleOnLoad() to call selectRow to hide the component grid
//                                       when there is no record in the tail coverage grid
// 01/04/2013       tcheng      138745 - Modified coverageListGrid_selectRow() to correct button invisible
//                                       on tail coverage page.
// 04/10/2014       xnie        153450 - 1) Modified handleOnLoad() to close tail coverage page if user clicks close
//                                          button and tail data is saved without error.
//                                       2) Modified handleOnButtonClick() to call submitSave() if user clicks Update
//                                          button to change tail data or current policy screen mode is WIP or
//                                          MANUAL_ENTRY or CANCELWIP or RENEWWIP, and then clicks close button.
// 05/27/2014       xnie        153450 - Roll backed prior changes.
// 08/22/2014       jyang       156829 - Updated handleOnValidateDone(), removed the condition of exclude multi-cancel.
// 03/01/2016       wdang       169688 - Added coverageListGrid_deleteDependentRow().
// 03/10/2017       wrong       180675 - 1) Added invokeWorkflow() case for new UI tab style.
//                                       2) Modified code to open div popups in primary page in new UI tab style.
// 06/28/2017       tzeng       186273 - Modified handleOnButtonClick() to change coverageBaseRecordId as tail coverage
//                                       base fk and add mainCoverageBaseRecordId as main coverage base fk.
// 06/24/2017       ssheng      185382 - 1. Modified handleOnButtonClick function to process auto save
//                                       in new UI tab style.
//                                       2. Add function isNeedToRefreshParentB.
// 07/12/2017       lzhang      186847 - Reflect grid replacement project changes
// 12/13/2017       wrong       190191 - Delete variable subTabGridId due to deleted function ignoreErrorMessageCheck
//                                       in commonSecondlyTab.js.
// 12/28/2017       tzeng       190488 - 1) Modified handleOnButtonClick() to add logic to check component changes when
//                                          case is 'SAVE'.
//                                       2) Added doBeforeAndCloseTailPage() to merge the same code in handleOnButtonClick().
// 06/12/2018       wrong       192895 - 1) Modified handleOnLoad() to set true for hasErrorMessages in invalid case.
//                                       2) Modified viewValidationError() to make popup display in the whole page.
// 09/25/2018       wrong       195793 - Added handleExitWorkflow() to add new logic after existing workflow for long
//                                       running transaction.
//-----------------------------------------------------------------------------
var isInOtherWF;
var isCloseTail = false;
function handleOnLoad() {
    var isWorkflowInvoked = false;
    //to see if this workflow is in extend term's workflow
    var startingState = getObjectValue("startingState");
    if (startingState == "extendCancelTerm" || startingState == "invokeRating" || startingState == "multiInvokeRating") {
        isInOtherWF = true;
    }

    if (!isInOtherWF) {
        if (isExeInvokeWorkFlow()) {
            invokeWorkflow();
        }
    }
    if (isWorkflowInvoked) {
        // work flow is invoked
    }
    else if (getObjectValue("validateResult") == 'INVALID') {
        hasErrorMessages = true;
        viewValidationError();
    }
    else if (getObjectValue("captureCancellationDetail") == 'Y') {
        var accountingDate = getObjectValue("accountingDate");
        performCancellation("TAIL", "", "", "", accountingDate);
    }
    else if (getObjectValue("captureTransactionDetail") == 'Y') {
        var accountingDate = getObjectValue("accountingDate");
        //use processCode as the transactionCode
        var tailTransactionCode = getObjectValue("tailTransactionCode");

        captureTransactionDetails(tailTransactionCode, 'captureFinancePercentage', accountingDate);
    }

    // Call selectRow to hide the component grid when there is no record in the tail coverage grid
    coverageListGrid_selectRow(0);
}

function coverageListGrid_selectRow(id) {
    var tailXmlData = getXMLDataForGridName("coverageListGrid");
    var compXmlData = getXMLDataForGridName("componentListGrid");

    // Filter component data
    //var covgBaseRecordId = selectedDataGrid.recordset("CCOVERAGEBASERECORDID").value;
    if (!isEmptyRecordset(tailXmlData.recordset)) {
        showNonEmptyTable(getTableForXMLData(tailXmlData));
        filterComponentData(tailXmlData);
        hideShowElementByClassName(getObject("coverageDetailDiv"), false);
        hideShowElementByClassName(getObject("compGridButtonDiv"), false);
    }
    else {
        hideShowElementByClassName(getObject("compGridButtonDiv"), true);
        hideEmptyTable(getTableForXMLData(compXmlData));
    }


    if (isEmptyRecordset(tailXmlData.recordset) || isEmptyRecordset(compXmlData.recordset)) {
        hideEmptyTable(getTableForXMLData(compXmlData));
        hideGridDetailDiv("componentListGrid");
    }
    else {
        showNonEmptyTable(getTableForXMLData(compXmlData));
        reconnectAllFields(document.forms[0]);
        hideShowElementByClassName(getObject("componentDetailDiv"), false);
        hideShowElementByClassName(getObject("compGridButtonDiv"), false);
    }

    return true;
}

function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'SAVE':
            if(isButtonStyle() || isPageDataChanged() ||
               (isTabStyle() && eval("getParentWindow().getAutoSaveProcessingB")
                && !getParentWindow().getAutoSaveProcessingB())) {
                submitSave();
            } else {
                var covgXmlData = getXMLDataForGridName('coverageListGrid');
                if (!isEmptyRecordset(covgXmlData.recordset)) {
                    var compXmlData = getXMLDataForGridName('componentListGrid');
                    var changedCovgNodes = null;
                    var changedCompNodes = null;
                    if (!isEmptyRecordset(covgXmlData.recordset)) {
                        changedCovgNodes = covgXmlData.documentElement.selectNodes("//ROW[CTAILRECORDMODECODE='TEMP']");
                    }
                    if(!isEmptyRecordset(compXmlData.recordset)) {
                        changedCompNodes = compXmlData.documentElement.selectNodes("//ROW[CRECORDMODECODE='TEMP']");
                    }
                    if ((changedCovgNodes && changedCovgNodes.length > 0) ||
                        (changedCompNodes && changedCompNodes.length > 0)) {
                        submitSave();
                    } else {
                        doBeforeAndCloseTailPage();
                    }
                }
                else {
                    syncResultToParent(commonOnSubmitReturnTypes.noDataChange);
                }
            }
            break;
        case 'CLEAR':
        /* pre-Insert validations */
            clearFilter();
            break;
        case 'FILTER':
            filterTailData();
            break;
        case 'ADDCOMP':
        // do pre-Insert validations
        //            if (!componentPreInsertValidations()) {
        //                return;
        //            }
            var currentCovgDataGrid = getXMLDataForGridName("coverageListGrid");
            var productCoverageCode = currentCovgDataGrid.recordset("CPRODUCTCOVERAGECODE").value;
            var coverageBaseRecordId = currentCovgDataGrid.recordset("ID").value;
            var coverageBaseEffectiveFromDate = currentCovgDataGrid.recordset("CEFFECTIVEFROMDATE").value;
            var riskId = currentCovgDataGrid.recordset("CRISKBASERECORDID").value;
            var coverageId = currentCovgDataGrid.recordset('CCOVERAGEID').value;
            var mainCoverageBaseRecordId = currentCovgDataGrid.recordset("CCOVERAGEBASERECORDID").value;

            var url = getAppPath() + "/componentmgr/selectComponent.do?"
                + commonGetMenuQueryString() + "&productCoverageCode=" + productCoverageCode
                + "&coverageBaseRecordId=" + coverageBaseRecordId
                + "&coverageBaseEffectiveFromDate=" + coverageBaseEffectiveFromDate
                + "&riskId=" + riskId + "&coverageId=" + coverageId
                + "&mainCoverageBaseRecordId=" + mainCoverageBaseRecordId;

            var processingDivId = openDivPopup("", url, true, true, "", "", "", "", "", "", "", false);
            break;
        case 'DELETECOMP':
            var gridId = "componentListGrid";
            commonDeleteRow(gridId);
            break;

        case 'RATE':
            performRate();
            break;
        case 'DECLINE':
            validateTailProcess('DECLINE');
            break;
        case 'ACTIVATE':
            validateTailProcess('ACTIVATE');
            break;
        case 'REINSTATE':
            validateTailProcess('REINSTATE');
            break;
        case 'ACCEPT':
            validateTailProcess('ACCEPT');
            break;
        case 'CANCEL':
            validateTailProcess('CANCEL');
            break;
        case 'UPDATE':
            validateTailProcess('UPDATE');
            break;
        case 'ADDTAIL':
            if (commonIsOkToChangePages()) {
                viewSelectManualTailPage();
            }
            break;
        case 'DELETETAIL':
            var gridId = "coverageListGrid";
            commonDeleteRow(gridId);
            break;
        case 'CLOSETAIL':
            doBeforeAndCloseTailPage();
            break;
        case 'ADJ_LIMIT':
            validateTailProcess('ADJ_LIMIT');
            break;
        case 'FINCHARGE':
            installmentFinanceCharge();
            break;
    }
}

function coverageListGrid_deleteDependentRow() {
    if (!getTableProperty(componentListGrid, "hasrows"))
        return;
    var covgBaseRecordId = coverageListGrid1.recordset("ID").value;

    first(componentListGrid1);
    beginDeleteMultipleRow("componentListGrid");
    while (!componentListGrid1.recordset.eof) {
        if (componentListGrid1.recordset("CCOVERAGEBASERECORDID").value == covgBaseRecordId) {
            setSelectedRow("componentListGrid", componentListGrid1.recordset("ID").value);
            componentListGrid_deleterow();
        }
        next(componentListGrid1);
    }
    endDeleteMultipleRow("componentListGrid");
    first(componentListGrid1);
    hideShowForm("componentListGrid");
}

function submitSave() {
    alternateGrid_update('coverageListGrid', "CSELECT_IND='-1' or CSELECT_IND='0'");
    alternateGrid_update('componentListGrid');
    setInputFormField("processCode", 'SAVE');
    document.forms[0].process.value = "saveAllTail";
    showProcessingDivPopup();
	syncResultToParent(commonOnSubmitReturnTypes.submitSuccessfully);
    submitFirstForm();
}

function performRate() {
    setInputFormField("rateTail", 'Y');
    submitSave();
}

function validateTailProcess(processCode) {
    //no tail selected check
    var selectedRows = getSelectedRows(coverageListGrid1);
    if (selectedRows.length == 0) {
        handleError(getMessage("pm.maintainTail.noTailSelectedError"));
    }
    else {
        alternateGrid_update('componentListGrid');
        alternateGrid_update('coverageListGrid', "CSELECT_IND='-1' or CSELECT_IND='0'");
        setInputFormField("processCode", processCode);
        // Issue 118616: set processAction field value also
        setInputFormField("processAction", processCode);
        document.forms[0].process.value = "validateTailProcess";
        showProcessingDivPopup();
        submitFirstForm();
    }
}


//-----------------------------------------------------------------------------
// Filter tail data by tail criteria
//-----------------------------------------------------------------------------
function filterTailData() {
    var riskTypeFilter = getObjectValue("tailRiskTypeFilter");
    var tailFilter = getObjectValue("tailFilter");
    var riskFilter = getObjectValue("riskFilter");
    var tailEffFilter = getObjectValue("tailEffFilter");
    var tailExpFilter = getObjectValue("tailExpFilter");
    var tailStatusFilter = getObjectValue("tailStatusFilter");
    var tailXmlData = getXMLDataForGridName("coverageListGrid");
    var compXmlData = getXMLDataForGridName("componentListGrid");

    var filterStr = "";

    filterStr = addFilterCondition(filterStr, "CRISKTYPECODE", "=", riskTypeFilter);
    filterStr = addFilterCondition(filterStr, "CRISKBASERECORDID", "=", riskFilter);
    filterStr = addFilterCondition(filterStr, "CTAILCOVERAGEDESC", "=", tailFilter);
    filterStr = addFilterCondition(filterStr, "CTAILCURRPOLRELSTATTYPECD", "=", tailStatusFilter);
    filterStr = addFilterCondition(filterStr, "CEFFECTIVEFROMDATE", ">=", tailEffFilter, XML_DATE);
    filterStr = addFilterCondition(filterStr, "CEFFECTIVETODATE", "<=", tailExpFilter, XML_DATE);

    // must set selectedTableRowNo property to null, else it will go to wrong logic in common.js userReadyStateReady() function.
    setTableProperty(eval("coverageListGrid"), "selectedTableRowNo", null);
    //sync before filter, filter function doesn't handle selectInd sync
    syncChanges(origcoverageListGrid1, coverageListGrid1, "CSELECT_IND='-1' or CSELECT_IND='0'");
    // 113002 Before filter, if system changed isAcceptAvailable to N, need to change it back to Y.
    if(hasObject("isAcceptAvailableNeedResetToY")) {
      setInputFormField("isAcceptAvailable", "Y");
    }
    if (!isCloseTail) {
        coverageListGrid_filter(filterStr);
    }
    //if filter result is empty, invoke the selectRow function
    if (isEmptyRecordset(tailXmlData.recordset)) {
        coverageListGrid_selectRow(0);
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

function validateTailData() {
    alternateGrid_update('componentListGrid');
    alternateGrid_update('coverageListGrid', "CSELECT_IND='-1' or CSELECT_IND='0'");
    var url = getAppPath() + "/tailmgr/maintainTail.do?";
    postAjaxSubmit(url, "validateTailData", false, false, handleOnValidateDone, false, isButtonStyle());
}

function viewValidationError() {
    var url = getAppPath() + "/transactionmgr/viewValidationError.do?"
        + commonGetMenuQueryString() + "process=loadAllValidationError";
    var processingDivId = getOpenCtxOfDivPopUp().openDivPopup("", url, true, true, "", "", "", "", "", "", "", false);
}

function handleOnValidateDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }

            // reset field values regardless if we got messages or not
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                setFormFieldValuesByObject(oValueList[0]);
            }

            var validateResult = getObjectValue("VALIDATERESULT");
            if (validateResult == "INVALID") {
                viewValidationError();
            }
            else {
                //if it's in other workflow
                //call invokeWorkflow will pop another div window,that will cause problem,so we just refresh instead
                if (isInOtherWF) {
                    window.frameElement.document.parentWindow.refreshPage(true);
                }
                else if (isTabStyle()) {
                    syncResultToParent(commonOnSubmitReturnTypes.noDataChange);
                }
                else {
                    commonOnButtonClick('CLOSE_RO_DIV');
                }
            }
        }
    }
}

//-----------------------------------------------------------------------------
// view select tail page
//-----------------------------------------------------------------------------
function viewSelectManualTailPage() {
    var tailXmlData = getXMLDataForGridName("coverageListGrid");
    var url = getAppPath() + "/tailmgr/selectTail.do?"
        + commonGetMenuQueryString()
        + "&productCoverageCode=" + tailXmlData.recordset("CMAINPRODUCTCOVERAGECODE").value
        + "&process=loadAllAvailableTail";

    var divPopupId = openDivPopup("", url, true, true, null, null, "", "", "", "", "", false);
}

//-----------------------------------------------------------------------------
// clears all filter criteria and redisplays all tail coverage data
//-----------------------------------------------------------------------------
function clearFilter() {
    getObject("tailRiskTypeFilter").value = "";
    getObject("tailFilter").value = "";
    getObject("riskFilter").value = "";
    getObject("tailEffFilter").value = "";
    getObject("tailExpFilter").value = "";
    getObject("tailStatusFilter").value = "";

    filterTailData();
}

//-----------------------------------------------------------------------------
// exclude filter fileds
//-----------------------------------------------------------------------------
function excludeFieldsForSettingUpdateInd() {
    return new Array(
        "tailRiskTypeFilter",
        "tailFilter",
        "tailEffFilter",
        "riskFilter",
        "tailStatusFilter",
        "tailExpFilter"
        );
}

//-----------------------------------------------------------------------------
// Overwrite getParentGridId and getChildGridId
//-----------------------------------------------------------------------------
function getParentGridId() {
    return "coverageListGrid";
}

function getChildGridId() {
    return "componentListGrid";
}

//refresh page for tail page
function refreshPage() {
    var tailUrl = getAppPath() + "/tailmgr/maintainTail.do?"
                + commonGetMenuQueryString() + "&process=loadAllTail";
    setWindowLocation(tailUrl);
}

function isNeedToRefreshParentB(){
    // compare wip no or official no to determine if policy picture is changed. If policy
    // picture has been changed, parent page should be refreshed when closing.
    var parentWindow = window.frameElement.document.parentWindow;
    if (parentWindow.hasObject("wipNo") && parentWindow.hasObject("offNo") &&
            hasObject("wipNo") && hasObject("offNo") &&
            (getObjectValue("wipNo") != parentWindow.getObjectValue("wipNo") ||
            getObjectValue("offNo") != parentWindow.getObjectValue("offNo"))) {
        return true;
    } else {
        return false;
    }
}

function doBeforeAndCloseTailPage() {
    if (!isInOtherWF) {
        isCloseTail = true;
        clearFilter();
        closePage();
    }
    //if it's after extend term
    else {
        closePage();
    }
}

function handleExitWorkflow() {
    var url = getAppPath() + "/tailmgr/maintainTail.do?"
            + commonGetMenuQueryString() + "&process=loadAllTail"
            + "&selectedIds=" + getObjectValue("selectedIds")
            + "&tailRecordMode=TEMP";
    setWindowLocation(url);
}
