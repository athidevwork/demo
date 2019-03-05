//-----------------------------------------------------------------------------
// Javascript file for maintainCoverage.jsp.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   June 10, 2010
// Author: syang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 06/10/2010       syang       100024 - Modified handleOnButtonClick() to pass lastTransactionLogId to component update page.
// 07/09/2010       syang       108715 - Modified addAllDefaultComponent() to add query string.
// 08/04/2010       syang       103793 - Modified handleOnButtonClick('SURCHARGE_POINTS') to check whether unsaved data exists.
// 09/13/2010       wfu         111776: Replaced hardcode string with resource definition
// 11/25/2010       bhong       114074 - Added riskBaseRecord to processMinitail.do
// 01/28/2011       fcb         117193 - document.all(gridDetailDivId) replaced with hasObject(gridDetailDivId)
// 02/11/2011       ryzhao      112571 - Modified coverageListGrid_selectRow() to add a new condition.
// 05/18/2011       dzhang      117246 - Added isAddCompAvailable, modified handleOnChange to handle the Add option's availability
//                                       under the risk's dateChangeAllow condition.
// 05/30/2011       ryzhao      121334 - Modified coverageListGrid_selectRow() to move the changes of issue 112571 to backend.
// 05/25/2011       gxc         105791 - Modified to pass coverage status when adding OOS coverage record
// 05/01/2011       fcb         105791 - Added convertCoverage() for safety.
// 06/10/2011       wfu         103799 - Adjust the prior act pop up page to avoid scrolls.
// 06/23/2011       wfu         121715 - Modified getInitialValuesForOoseCoverage to populate parameter coverageBaseRecordId.
// 07/20/2011       wfu         122721 - Modified to populate coverage practice state code for filtering manuscript records.
// 07/20/2011       syang       121208 - Modified addOneCoverage() to group coverage.
// 07/28/2011       wfu         122721 - revert related changes to use another solution.
// 08/12/2011       syang       121208 - Modified handleOnButtonClick() to open select coverage page with different width.
// 08/17/2011       lmjiang     123252 - Change delete component loop style.
// 08/30/2011       ryzhao      124458 - Modified coverageDateValidations() to use formatDateForDisplay to format date parameters.
// 09/21/2011       wfu         120554 - Fixed errors in quote pages and multiple grid pages.
// 10/11/2011       syang       125626 - Modified coverageListGrid_setInitialValues() to default fields if they were undefined.
// 11/03/2011       lmjiang     126675 - Call Ajax to determine if the characters on buttons are italic when the row is switched.
// 07/24/2012       awu         129250 - Added getCoverageAutoSaveQueryString(), autoSaveSelectedCoverageWip(), handleOnAutoSaveSelectedCoverageWip(),
//                                       resetRiskDropdownList().

// 07/17/2012       ryzhao      135662 - Variable italicsFieldId should have different value for POLICY or QUOTE.
//                                       Modified all the related buttons to set different value to italicsFieldId.
// 12/27/2012       tcheng      139862 - Added handleExitWorkflow() to pop up warning message.
// 03/14/2013       adeng       142891 - Modified handleOnButtonClick() to filter component data by coverage base record
//                                       Id after delete component.
// 04/26/2013       awu         141758 - Changed addAllDefaultComponent to addAllCoverage to add all selected coverages,
//                                      all their dependent coverages and default components.
//                                      Remove original functions which are useless now.
// 05/03/2013       tcheng      143761 - Modified coverageDulplicateValidations() to filter flat cancel temp
//                                       coverage in renewal wip for duplicate validation.
// 05/29/2013       jshen       141758 - Highlight the newly added component
// 05/13/2013       adeng       143400 - Pass viewMode & endorsementQuoteId(if exist) to retrieve record exist information.
// 07/23/2013       awu         146030 - Modified coverageListGrid_selectRow to set the scroll bar to bottom.
// 12/06/2013       Parker      148036 - Refactor maintainRecordExists code to make one call per subsystem to the database.
// 12/19/2013       jyang       148585 - Add judge to avoid appending coverageId to the url when it equals -9999.
// 01/01/2014       Parker      148029 - Cache risk header, coverage header and policy navigation information to policy header.
// 01/28/2014       awu         147405 - 1. Modified handleOnButtonClick to get the cancel date for reinstate.
//                                       2. Use the coverage's base record status for reinstate.
// 02/19/2014       adeng       151505 - Pass in riskId when open prior acts page.
// 03/03/2014       awu         150880 - Modified selectRowInGridOnPageLoad. Try to select the first row of the component list
//                                       grid when the coverage grid is empty.
// 03/11/2014       fcb         152685 - transaction log id passed to loadWarningMessage
// 06/18/2014       kxiang      155121 - delete the " " in string covgStr of function addAllCoverage().which is unnecessary.
// 06/18/2014       kxiang      155121 - Roll back changes 155121.
// 06/26/2014       awu         155102 - Added coverageListGrid_beforeFireAjaxOnSelectRow, moved the addlInfo logic to it
//                                       from coverageListGrid_selectRow.
// 06/29/2014       jyang       149970 - Modified handleOnButtonClick,pass coverageDesc to cancellationDetail page.
// 07/25/2014       awu         152034 - Roll back the changes of issue148585
// 08/25/2014       awu         152034 - 1. Modified getMenuQueryString to append the coverage id, coverage class id to URL.
//                                       2. Modified handleOnSubmit to append risk/coverage/coverage class IDs to URL for RATE.
// 10/14/2014       jyang2      157749 - Modified isAddCompAvailable(), added logic to set 'CISADDCOMPAVAILABLE' to 'N'
//                                       when none version of the selected coverage can add component.
// 10/16/2014       wdang       156038 - Replaced getObject('riskId') with policyHeader.riskHeader.riskId.
// 01/08/2015       awu         157105 - Added beginDeleteMultipleRow and endDeleteMultipleRow
//                                       to wrap the multiple rows deleting.
// 02/11/2015       kxiang      161002 - Modified coverageListGrid_selectRow to modify condition when invoke
//                                       isAddCompAvailable.
// 09/23/2015       Elvin       Issue 160360: since overrided commonOnButtonClick, need to catch Preview button here
// 09/23/2015       xnie        177836 - Modified coverageListGrid_beforeFireAjaxOnSelectRow to add input parameter
//                                       CCOVERAGEEFFECTIVEFROMDATE to url.
// 08/25/2016       xnie        179096 - Modified getInitialValuesForOoseCoverage to pass IBNRCovgB to url.
// 01/11/2017       lzhang      182312 - Add handleAfterViewValidation() to refresh page.
// 03/13/2017       eyin        180675 - 1. Added 'isTabStyle()' condition and corresponding operation for UI change.
//                                       2. Added  methods 'currentTabIsHideForCurrentRow, getFieldValueFromRecordSet,
//                                          handleOnSecondaryTabClick, handleOnUIProcess, autoSaveSubIFrameForNavigation,
//                                          autoSaveSubTab, callBackAutoSaveForFrame, processMainPageAfterAutoSaveSubTab,
//                                          handleOnShowPageInFrame' for UI change.
//                                       3. Added operations ' clearCacheTabIds(); clearCacheRowIds();' for UI change.
// 03/30/2017       lzhang      184424 - Override submitMultipleGrids() instead of submitForm()
// 07/12/2017       lzhang      186847 - Reflect grid replacement project changes
// 07/26/2017       lzhang      182246 - Clean up unsaved message for page changes
// 09/26/2017       wli         187808 - Modified coverageListGrid_selectRow to exclude highlight fake tab for
//                                       autosaving when switch grid row.
// 11/09/2017       tzeng       187689 - 1) Modified coverageListGrid_selectRow(), handleOnSubmit(), handleOnButtonClick(),
//                                          preButtonClick(), handleOnSecondaryTabClick(), autoSaveSubIFrameForNavigation()
//                                          to support processAutoSaveSubTab().
//                                       2) Modified processMainPageAfterAutoSaveSubTab() to always do extract if click
//                                          WIP/SAVEWIP and remove "delete" case to make consistent with preButtonClick().
//                                       3) Modified handleOnShowPageInFrame() to not prompt when switch to this sub tab.
// 12/04/2017       wrong       190014 - Modified coverageListGrid_selectRow to invoke commonOnSetButtonItalics when
//                                       insert a new coverage record.
// 12/13/2017       wrong       190191 - 1) Modified handleOnSecondaryTabClick to add clearOperationForTabStyle() to
//                                          clear operation value when switching sub tab in no data change case.
//                                       2) Modified coverageListGrid_selectRow to add clearOperationForTabStyle() to
//                                          clear operation value when switching grid row in no data change case.
// 01/04/2017       lzhang      188231 - 1) Modified coverageListGrid_selectRow:
//                                          set selected covgId to coverageHeaderCoverageId
// 06/11/2018       cesar       193651 - 1) Modified isAddCompAvailable() to include item(0) to retrieve value.
// 07/11/2018       wrong       193977 - Modified coverageListGrid_selectRow() to change condition of setting value to
//                                       coverageHeaderCoverageId.
// 09/17/2018       ryzhao      195271 - Modified autoSaveSubTab() to set indicator if it is auto save data from sub-tab
//                                       when saving coverage/component information.
// 10/15/2018       wrong       188391 - 1) Modified handleOnButtonClick() to support underlying coverage.
//                                       2) Modified autoSaveSubTab() to add save logic to support underlying coverage.
//                                       3) Modified handleOnShowPageInFrame() to support underlying coverage in tab
//                                          style.
//-----------------------------------------------------------------------------
var currentCoverageCode;
var shortTermB;
var isOoseCovgValid = true;
var ooseRowData;
var origCovgId = "";
var currentCoverageLimitCode;
var currentRetroDate;
var currentAnnualBaseRate;
var currentSharedLimitB;
var italicsFieldId = "NONE";
var showCoverageAutoSaveProcessDiv = true;
var fromPage = "";
var pageName = "Coverage";

function handleOnLoad() {
    if(hasObject("coverageGroup") && !isEmpty(getObjectValue("coverageGroup"))){
        filterCoverageData(getObjectValue("coverageGroup"));
    }
    setInputFormField("needToHandleExitWorkFlow", "Y");
    if(isTabStyle())
        commonOnUIProcess();
    invokeWorkflow();
}

function selectRowInGridOnPageLoad() {
    $.when(dti.oasis.grid.getLoadingPromise("coverageListGrid")).then(function(){
        // select row by previousely selected coverage ID if there was no error
        var isCoverageGridEmpty = isEmptyRecordset(getXMLDataForGridName("coverageListGrid").recordset);
        if (!hasErrorMessages && !isCoverageGridEmpty && currentCoverageId) {
            selectRowById("coverageListGrid", currentCoverageId);
        }
        else {
            if(isTabStyle()){
                if(getPreviousRow() == "") {
                    selectFirstRowInGrid("coverageListGrid");
                    if(isCoverageGridEmpty) {
                        selectFirstTab();
                    }
                }else {
                    rollback = true;
                    selectRowById("coverageListGrid", getPreviousRow());
                }
            }else{
                selectFirstRowInGrid("coverageListGrid");
            }

            if (isCoverageGridEmpty) {
                selectFirstRowInGrid("componentListGrid");
            }
        }
    });
}

//function processFieldDeps() {
//    var selectedCovgDataGrid = getXMLDataForGridName("coverageListGrid");
//    var selectedCompDataGrid = getXMLDataForGridName("componentListGrid");
//
//    if (!isEmptyRecordset(selectedCompDataGrid.recordset) && !isEmptyRecordset(selectedCovgDataGrid.recordset)) {
//        if (selectedCovgDataGrid.recordset("CISCHGCOMPDATEAVAILABLE").value == "N") {
//            if (selectedCompDataGrid.recordset("CISCHGCOMPDATEAVAILABLE").value == "Y") {
//                componentListGrid1.recordset("CISCHGCOMPDATEAVAILABLE").value = "N";
//            }
//        }
//        if (selectedCovgDataGrid.recordset("CISCHGCOMPVALUEAVAILABLE").value == "N") {
//            if (selectedCompDataGrid.recordset("CISCHGCOMPVALUEAVAILABLE").value == "Y") {
//                componentListGrid1.recordset("CISCHGCOMPVALUEAVAILABLE").value = "N";
//            }
//        }
//    }
//}

function coverageListGrid_setInitialValues() {
    // If OOSE Coverage
    if (isForOose == "Y") {
        //Copy original data to new row
        setRecordsetByObject(coverageListGrid1, ooseRowData);
        getInitialValuesForOoseCoverage();
        // Reset flag
        isForOose = "N";
        return;
    }

    /* Call Ajax call to get addtional default value */
    var url = "maintainCoverage.do?" + commonGetMenuQueryString() +
              "&productCoverageCode=" + currentCoverageCode + "&shortTermB=" + shortTermB +
              "&process=getInitialValuesForCoverage";
    // Add the additional fields.
    if(isUndefined(currentCoverageLimitCode)){
       currentCoverageLimitCode = "";
    }
    if(isUndefined(currentRetroDate)){
       currentRetroDate = "";
    }
    if(isUndefined(currentAnnualBaseRate)){
       currentAnnualBaseRate = "";
    }
    if(isUndefined(currentSharedLimitB)){
       currentSharedLimitB = "";
    }
    url += "&coverageLimitCode=" + currentCoverageLimitCode + "&retroDate=" + currentRetroDate +
            "&annualBaseRate=" + currentAnnualBaseRate + "&productDefaultSharedLimitB=" + currentSharedLimitB;

    if (policyHeader.riskHeader) {
        url = url + "&riskId=" + policyHeader.riskHeader.riskId;
    }

    // initiate async call
    new AJAXRequest("get", url, '', setInitialValues, false);

}

function coverageListGrid_beforeFireAjaxOnSelectRow(id) {
    // If OOSE Coverage, return directly.                                  a
    if (isForOose == "Y") {
        return;
    }
    var selectedDataGrid = getXMLDataForGridName("coverageListGrid");

    // Load Additional Info fields via AJAX call if this is not a newly inserted row
    // Add a new condition: RecordModeCode != 'REQUEST' which means the coverage is doing OOS Endorsement. Joe
    // If system parameter PM_RETRIEVE_CVG_ADDL is set to Y, we still need to call maintainCoverageAction.loadAddlInfo
    // to set Prior Act page entitlement info

    if (isEmpty(selectedDataGrid.recordset("CADDLINFO1").value.toUpperCase()) &&
            selectedDataGrid.recordset("UPDATE_IND").value != 'I' &&
            selectedDataGrid.recordset("CRECORDMODECODE").value != 'REQUEST') {
        url = "maintainCoverage.do?" + commonGetMenuQueryString() +
                "&coverageId=" + id +
                "&noseCoverageB=" + selectedDataGrid.recordset("CNOSECOVERAGEB").value +
                "&termEffectiveFromDate=" + policyHeader.termEffectiveFromDate +
                "&termEffectiveToDate=" + policyHeader.termEffectiveFromDate +
                "&retroDate=" + selectedDataGrid.recordset("CRETRODATE").value +
                "&coverageEffectiveFromDate=" + selectedDataGrid.recordset("CCOVERAGEEFFECTIVEFROMDATE").value +
                "&process=loadAddlInfo";
        if (policyHeader.riskHeader) {
            url = url + "&riskId=" + policyHeader.riskHeader.riskId;
        }
        // initiate async call
        new AJAXRequest("get", url, '', commonHandleOnGetAddlInfo, false);
    }
}

function coverageListGrid_selectRow(id) {
	// If OOSE Coverage, return directly.
    if (isForOose == "Y") {
        return;
    }

    var selectedDataGrid = getXMLDataForGridName("coverageListGrid");
    if (!isEmpty(italicsFieldIdList)) {
        italicsCurrentGridName = "coverageListGrid";
        commonOnSetButtonItalics(true);
    }

    var selectedCovgId = selectedDataGrid.recordset("ID").value;
    if(isEmpty(getObjectValue('coverageHeaderCoverageId'))
       || getObjectValue('coverageHeaderCoverageId') != selectedCovgId){
        setObjectValue('coverageHeaderCoverageId', selectedCovgId);
    }

    if(isTabStyle()){
        if(!isMainPageRefreshedFlg){
            operation = "switchGridRow";
        }else if (isMainPageRefreshedFlg && isDefined(getBtnOperation())) {
            operation = getBtnOperation();
            clearBtnOperation();
        }

        // cache the default tab ids and coverage ids when first time loaded
        if(getPreviousRow() == "") {
            setCacheRowIds(id + "," + id);
            setCacheTabIds(getFirstTab() + "," + getFirstTab());
        }

        var autoSaveResult = commonOnSubmitReturnTypes.noDataChange;
        if(rollback) {
            /**
             *  for coverage detail and component tab, roll back row in function selectRowInGridOnPageLoad()--> selectRowById
             *  for other tabs, roll back row in function callBackAutoSaveForFrame()-->selectRowById
             *  what we do:
             *  cache the roll back coverage row id
             */
            setCacheRowIds(id + "," + id);
        }else {
            // for coverage detail and component tab, no data change, no need to auto save
            if(isReservedTab(getPreviousTab()) && !isPageGridsDataChanged()) {
                setCacheRowIds(id + "," + id);
            }else {
                setCacheRowIds(getPreviousRow() + "," + id);
                // if not switch row, no need to auto save
                // exclude the fake tabs
                if(!isMainPageRefreshed() && !isReservedTab(getCurrentTab())) {
                    processAutoSaveSubTab(getCurrentTab());
                    autoSaveResult = autoSaveResultType;
                }
            }
        }

        if (autoSaveResult == commonOnSubmitReturnTypes.submitSuccessfully) {
            switchGridRowFlg = true;
        }
        else if (autoSaveResult == commonOnSubmitReturnTypes.noDataChange) {
            if(rollback) {
                /**
                 * for coverage detail and component tab auto save roll back, need to position previous tab
                 * for other tabs, nothing to do
                 */
                if(isReservedTab(getPreviousTab())) {
                    selectTabById(getPreviousTab());
                }
                rollback=false;
            }else{
                //Issue 190191: clear operation value after switch grid row for no data change case.
                clearOperationForTabStyle();
                setCacheRowIds(id + "," + id);
                selectTabById(getCurrentTab());
            }
        }
        else if (autoSaveResult == commonOnSubmitReturnTypes.commonValidationFailed ||
                autoSaveResult == commonOnSubmitReturnTypes.saveInProgress) {
            rollback=true;
            selectRowById("coverageListGrid", getPreviousRow());
        }
    }

    var gridId = "componentListGrid";
    var xmlTable = getTableForGrid(gridId);
    setTableProperty(xmlTable, "isUserReadyStateReadyComplete", false);
    // Filter component data
    filterComponentData(selectedDataGrid);

    var testCode = 'getTableProperty(getTableForGrid(\"' + gridId + '\"), "isUserReadyStateReadyComplete")';
    var callbackCode = 'selectRowById(\"' + "componentListGrid" + '\", ' + currentPolicyCovComponentId + ');';

    executeWhenTestSucceeds(testCode, callbackCode, 50);

    var compXmlData = getXMLDataForGridName("componentListGrid");
    if (isEmptyRecordset(compXmlData.recordset)) {
        hideEmptyTable(getTableForXMLData(compXmlData));
        hideGridDetailDiv("componentDetailDiv");
    }
    else {
        showNonEmptyTable(getTableForXMLData(compXmlData));
        reconnectAllFields(document.forms[0]);
        hideShowElementByClassName(getObject("componentDetailDiv"), false);
    }
    var policyScreenMode = getObjectValue("policyScreenMode");
    var coverageStatus = coverageListGrid1.recordset("CCOVERAGESTATUS").value;
    if (getObjectValue("policyViewMode") == "WIP" &&
        policyScreenMode != "CANCELWIP" && policyScreenMode != "REINSTATEWIP" && coverageStatus != "CANCEL" &&
        hasObject("componentOwner") && getObjectValue("componentOwner") == 'COVERAGE') {
        isAddCompAvailable();
    }
}

function currentTabIsHideForCurrentRow(tabId){
    var isHideB = true;
    switch (tabId) {
        case "PRIOR_ACT":
            isHideB = getFieldValueFromRecordSet("CISPRIORACTAVAILABLE");
            break;
        case 'MINI':
            isHideB = getFieldValueFromRecordSet("CISMINITAILAVAILABLE");
            break;
        case 'MANU':
            isHideB = getFieldValueFromRecordSet("CISMANUSCRIPTAVAILABLE");
            break;
        case 'COMP_UPDATE':
            var indValue = "Y";
            if(hasObject('isCompUpdateAvailable')){
                indValue=getObjectValue('isCompUpdateAvailable');
            }
            isHideB = (indValue == 'N') ? true : false;
            break;
        case 'MAN_EXCESS_PREM':
            isHideB = getFieldValueFromRecordSet("CISEXCESSPREMIUMAVAILABLE");
            break;
        case "SCH":
            isHideB = getFieldValueFromRecordSet("CISSCHEDULEAVAILABLE");
            break;
        case 'VL_COVG':
            isHideB = getFieldValueFromRecordSet("CISMOREAVAILABLE");
            break;
        case 'COVERAGE':
            isHideB = false;
            break;
        case 'COMPONENT':
            isHideB = false;
            break;
		default:
            isHideB = false;
			break;
    }
    return isHideB;
}

function getFieldValueFromRecordSet(fieldName){
    var selectedDataGrid = getXMLDataForGridName("coverageListGrid");
    var indValue = selectedDataGrid.recordset(fieldName).value;
    return indValue == "Y" ? false : true;
}
//-----------------------------------------------------------------------------
// Instruct the baseOnRowSelected to exec the processFieldDeps and pageEntitlements after _selectRow.
//-----------------------------------------------------------------------------
function isFieldDepsAndPageEntitlementsAfter_selectRow(gridId) {
    return true;
}

//-----------------------------------------------------------------------------
// Dulplicated coverage validations
//-----------------------------------------------------------------------------
function coverageDulplicateValidations(oCoverage, showMessage) {
    if (!getTableProperty(getTableForGrid("coverageListGrid"), "hasrows")) {
        return true;
    }

    var rowIndex = 0;
    first(coverageListGrid1);

    while (!coverageListGrid1.recordset.eof) {
        if (oCoverage.PRODUCTCOVERAGECODE == coverageListGrid1.recordset("CPRODUCTCOVERAGECODE").value &&
            coverageListGrid1.recordset("CRECORDMODECODE").value != "OFFICIAL" &&
            coverageListGrid1.recordset("CCOVERAGEEFFECTIVEFROMDATE").value !=
                    coverageListGrid1.recordset("CCOVERAGEEFFECTIVETODATE").value) {
            if (showMessage) {
                var parms = new Array(oCoverage.PRODUCTCOVERAGEDESC);
                handleError(getMessage("pm.addCoverage.duplicateCoverage.exist.error", parms),
                    "", "");
            }
            return false;
        }

        rowIndex ++;
        next(coverageListGrid1);
    }

    return true;
}

//-----------------------------------------------------------------------------
// Coverage date validations
//-----------------------------------------------------------------------------
function coverageDateValidations(effFromDate, riskEffectiveDate, oCoverage, showMessage) {
    // Coverage Date validation
    if (getRealDate(effFromDate) < getRealDate(riskEffectiveDate)) {
        if (showMessage) {
            var parms = new Array("", oCoverage.PRODUCTCOVERAGEDESC, formatDateForDisplay(riskEffectiveDate));
            handleError(getMessage("pm.addCoverage.coverageEffectiveDate.error", parms));
        }
        return false;
    }
    return true;
}



//-----------------------------------------------------------------------------
// Set Initial value for coverage
//-----------------------------------------------------------------------------
function setInitialValues(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;

            if (!handleAjaxMessages(data, null)) {
                /* no default value found */
                return;
            }

            //            var selectedDataGrid = getXMLDataForGridName(getCurrentlySelectedGridId());
            var selectedDataGrid = getXMLDataForGridName("coverageListGrid");
            /* Parse xml and get inital values(s) */
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                setRecordsetByObject(selectedDataGrid, oValueList[0]);
            }
        }
    }
}
//-----------------------------------------------------------------------------
// Delete component row when deleting coverage row. This function is called by commonDeleteRow() of common.js
//-----------------------------------------------------------------------------
function coverageListGrid_deleteDependentRow() {
    if (!getTableProperty(componentListGrid, "hasrows"))
        return;
    var covgBaseRecordId = coverageListGrid1.recordset("CCOVERAGEBASERECORDID").value;

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

//-----------------------------------------------------------------------------
// Callback function for validate OOSE coverage
//-----------------------------------------------------------------------------
function handleOnValidateForOoseCoverage(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                isOoseCovgValid = false;
                isForOose = "N";
            }
        }
    }
}

//-----------------------------------------------------------------------------
// To check if OOSE coverage is valid
//-----------------------------------------------------------------------------
function validateForOoseCoverage() {
    var selectedDataGrid = getXMLDataForGridName("coverageListGrid");
    var riskId = policyHeader.riskHeader.riskId;
    var url = "maintainCoverage.do?process=validateForOoseCoverage" +
              "&" + commonGetMenuQueryString("PM_MAINTAIN_COVERAGE", "") +
              "&coverageEffectiveFromDate=" + selectedDataGrid.recordset("CCOVERAGEEFFECTIVEFROMDATE").value +
              "&coverageBaseRecordId=" + selectedDataGrid.recordset("CCOVERAGEBASERECORDID").value +
              "&recordModeCode=" + selectedDataGrid.recordset("CRECORDMODECODE").value;
    url += "&riskId=" + riskId;

    new AJAXRequest("get", url, "", handleOnValidateForOoseCoverage, false);
}

//-----------------------------------------------------------------------------
// Callback function for getting oose coverage initial values
//-----------------------------------------------------------------------------
function handleOnGetInitialValuesForOoseCoverage(ajax) {
    commonHandleOnGetInitialValues(ajax);
}

//-----------------------------------------------------------------------------
// To get initial values for oose coverage
//-----------------------------------------------------------------------------
function getInitialValuesForOoseCoverage() {
    // set url
    var selectedDataGrid = getXMLDataForGridName("coverageListGrid");
    var url = "maintainCoverage.do?process=getInitialValuesForOoseCoverage" +
              "&" + commonGetMenuQueryString("PM_MAINTAIN_COVERAGE", "") +
              "&coverageId=" + origCovgId +
              "&riskId=" + policyHeader.riskHeader.riskId +
              "&coverageBaseRecordId=" + selectedDataGrid.recordset("CCOVERAGEBASERECORDID").value +
              "&coverageEffectiveFromDate=" + selectedDataGrid.recordset("CCOVERAGEEFFECTIVEFROMDATE").value +
              "&coverageEffectiveToDate=" + selectedDataGrid.recordset("CCOVERAGEEFFECTIVETODATE").value +
              "&ratingModuleCode=" + selectedDataGrid.recordset("CRATINGMODULECODE").value +
              "&policyFormCode=" + selectedDataGrid.recordset("CPOLICYFORMCODE").value +
              "&officialRecordId=" + selectedDataGrid.recordset("COFFICIALRECORDID").value +
              "&recordModeCode=" + selectedDataGrid.recordset("CRECORDMODECODE").value +
              "&coverageStatus=" + selectedDataGrid.recordset("CCOVERAGESTATUS").value +
              "&IBNRCovgB=" + selectedDataGrid.recordset("CIBNRCOVGB").value;


    new AJAXRequest("get", url, "", handleOnGetInitialValuesForOoseCoverage, false);
}

function addOoseCoverage() {
    // Save current row's data into object
    origCovgId = coverageListGrid1.recordset("ID").value;
    ooseRowData = getObjectFromRecordset(coverageListGrid1);

    commonOnButtonClick('ADD');
}

//-----------------------------------------------------------------------------
// Validatioins
//-----------------------------------------------------------------------------
function handleOnChange(obj) {
    // Coverage validations
    if (obj.name == "retroDate") {

        /* Check if prior acts coverage exists */
        /* Make Ajax call to check prior acts exist */

        url = "maintainCoverage.do?" + commonGetMenuQueryString() +
              "&coverageBaseRecordId=" + coverageListGrid1.recordset("CCOVERAGEBASERECORDID").value +
              "&process=validatePriorActsExist";

        if (policyHeader.riskHeader) {
            url = url + "&riskId=" + policyHeader.riskHeader.riskId;
        }

        // initiate async call
        new AJAXRequest("get", url, '', checkPriorActsExistDone, false);

        if (hasConfirmation("pm.maintainCoverage.priorActsExists.warning") &&
            getConfirmationResponse("pm.maintainCoverage.priorActsExists.warning") == "N") {
            obj.value = coverageListGrid1.recordset("CORIGRETRODATE").value;
            obj.select();
            return false;
        }
    }

    /* Modify Excess payor */
    var curTransCode = policyHeader.transactionCode;
    var sysParm = getSysParmValue("PM_CHECK_EXCESS");

    if (obj.name == "ratePayorDependCode") {
        if (curTransCode == "NEWBUS" && sysParm == "Y") {
            handleError(getMessage("pm.maintainCoverage.excessPayor.warning"));
        }
    }

    /* Handle modify coverage part shared indicator */
    if (obj.name == "covgPartSharedLimitB") {
        if (getObjectValue("coveragePartConfigured") == "true") {
            //todo:implement this once coverage part is done
        }
    }

    if (obj.name == "sharedLimitsB") {
        var sharedLimitsB = coverageListGrid1.recordset("CSHAREDLIMITSB").value;
        if (sharedLimitsB == "N") {
            url = "maintainCoverage.do?" + commonGetMenuQueryString() +
                  "&coverageId=" + coverageListGrid1.recordset("CCOVERAGEBASERECORDID").value +
                  "&termEff=" + policyHeader.termEffectiveFromDate +
                  "&termExp=" + policyHeader.termEffectiveFromDate +
                  "&transEffDt=" + policyHeader.lastTransactionInfo.transEffectiveFromDate +
                  "&transLogId=" + policyHeader.lastTransactionInfo.transactionLogId +
                  "&process=getCoverageLimitShared";

            // initiate async call
            new AJAXRequest("get", url, '', handleOnGetCoverageLimitShared, false);
        }
    }

    if (obj.name == "coverageEffectiveToDate") {
        isAddCompAvailable();
        functionExists = eval("window.pageEntitlements");
        if (functionExists) {
            pageEntitlements(true, "coverageListGrid");
        }
    }
    // Filter the coverage by coverage group.
    if (obj.name == "coverageGroup") {
        filterCoverageData(obj.value);
    }

    return true;
}

function checkPriorActsExistDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;

            handleAjaxMessages(data, null);
        }
    }
}

function getMenuQueryString(id, url) {
    var tempUrl = "";

    if (id == 'PM_COVG_SCH_PUP' || id == 'PM_CP_SCH') {
        var saveRequired = commonSaveRequiredToChangePages(pageName, "Schedule", "N", "");
        if (saveRequired) {
            return;
        }
        tempUrl = tempUrl + '&isFromCoverage=Y';
    }

    if (id == 'PM_COVG_MINI' || id == 'PM_CP_MINI') {
        tempUrl = tempUrl + '&isFromCoverage=Y';
    }

    if (policyHeader.riskHeader) {
        tempUrl = tempUrl + "&riskId=" + policyHeader.riskHeader.riskId;
    }

    try {
        var coverageId = coverageListGrid1.recordset('ID').value;
        tempUrl = tempUrl + "&coverageId=" + coverageId;
        if (currentCoverageId == coverageId) {
            var coverageClassId = getUrlParam(window.location, "coverageClassId");
            if (coverageClassId != null) {
                tempUrl = tempUrl + "&coverageClassId=" + coverageClassId;
            }
        }
    }
    catch (ex) {
        // ignore error
    }
    return tempUrl;
}

function getCoverageAutoSaveQueryString() {
    var tempUrl = "";
    if (hasObject("policyNavSourceId")) {
        var riskId = getObjectValue("policyNavSourceId");
        tempUrl = tempUrl + "&riskId=" + riskId;
    }
    try {
        var coverageId = coverageListGrid1.recordset('ID').value;
        tempUrl = tempUrl + "&coverageId=" + coverageId;
    }
    catch (ex) {
        // ignore error
    }
    if (hasObject("policyNavLevelCode")) {
        var policyNavLevelCode = getObjectValue("policyNavLevelCode");
        tempUrl = tempUrl + "&policyNavLevelCode=" + policyNavLevelCode;
    }
    return tempUrl;
}

function handleOnSubmit(action) {
    var proceed = true;

    if(isTabStyle()){
        operation = action;
        removeMessagesForFrame();
        if(!isReservedTab(getPreviousTab()) && !isPreviewButtonClicked() && action != "SAVE" && action != "NAVIGATE") {
            processAutoSaveSubTab(getCurrentTab());
            if (autoSaveResultType != commonOnSubmitReturnTypes.noDataChange) {
                return false;
            }
        }
    }

    switch (action) {
        case 'SAVE':
            document.forms[0].action = buildMenuQueryString("PM_PT_VIEWCVG", getFormActionAttribute());
            document.forms[0].process.value = "saveAllCoverage";
            loadSaveOptions("PM_COVERAGE", "submitForm");
            proceed = false
            break;
        case 'SAVEWIP':
            document.forms[0].action = buildMenuQueryString("PM_PT_VIEWCVG", getFormActionAttribute());
            document.forms[0].process.value = "saveAllCoverage";
            handleSaveOptionSelection("WIP");
            break;
        case 'RATE':
            document.forms[0].action = buildMenuQueryString("PM_PT_VIEWCVG", getFormActionAttribute());
            document.forms[0].process.value = "saveAllCoverage";
            setInputFormField("newSaveOption", "WIP");
            setInputFormField("processRatingB", "Y");
            break;

        case 'NAVIGATE':
            document.forms[0].process.value = "loadAllCoverage";
            clearCacheTabIds();
            clearCacheRowIds();
            setInputFormField("riskId", getObjectValue("policyNavSourceId"));
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

//-----------------------------------------------------------------------------
// Overwrite commonOnButtonClick
//-----------------------------------------------------------------------------
function commonOnButtonClick(asBtn) {
	if(isTabStyle()){
        if(preButtonClick(asBtn)) {
            handleOnButtonClick(asBtn);
        }
    }else{
        handleOnButtonClick(asBtn);
    }
}

function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'ADD_COVERAGE':
            var url = getAppPath() + "/coveragemgr/selectProductCoverage.do?" + commonGetMenuQueryString() + getMenuQueryString();
            var pageWidth = "";
            if (getObjectValue("selectCoverageDetailDisplay") == 'Y') {
                pageWidth = 900;
            }
            url = url + '&coverageDetailDisplay=' + getObjectValue("selectCoverageDetailDisplay");
            var processingDivId = openDivPopup("", url, true, true, "", "", pageWidth, "", "", "", "", false);
            clearOperationForTabStyle();
            break;
        case 'ADDCOMP':
            // do pre-Insert validations
            validateForOoseCoverage();
            if (!isOoseCovgValid) {
                return;
            }
            if (!componentPreInsertValidations()) {
                return;
            }
            var currentCovgDataGrid = getXMLDataForGridName("coverageListGrid");
            var productCoverageCode = currentCovgDataGrid.recordset("CPRODUCTCOVERAGECODE").value;
            var coverageBaseRecordId = currentCovgDataGrid.recordset("CCOVERAGEBASERECORDID").value;
            var coverageBaseEffectiveFromDate = currentCovgDataGrid.recordset("CCOVERAGEBASEEFFECTIVEFROMDATE").value;
            var riskId = policyHeader.riskHeader.riskId;
            var coverageId = currentCovgDataGrid.recordset('ID').value;
            var url = getAppPath() + "/componentmgr/selectComponent.do?"
                    + commonGetMenuQueryString() + "&productCoverageCode=" + productCoverageCode
                    + "&coverageBaseRecordId=" + coverageBaseRecordId
                    + "&coverageBaseEffectiveFromDate=" + coverageBaseEffectiveFromDate
                    + "&riskId=" + riskId + "&coverageId=" + coverageId;
            var processingDivId = openDivPopup("", url, true, true, "", "", "700", "500", "", "", "", false);
            break;
        case 'ADD_NEW_COMP':
            commonAddRow("componentListGrid");
            break;
        case 'DELETECOMP':
            // if officialRecordId=0, commonDeleteRow() will cause error. Set it to "" here directly.
            var gridId = "componentListGrid";
            var rs = getXMLDataForGridName(gridId).recordset;
            var officalRecordId = null;
            if (isFieldExistsInRecordset(rs, "COFFICIALRECORDID")) {
                officalRecordId = rs.Fields("COFFICIALRECORDID").value;
                //alert("officalRecordId="+officalRecordId);
                if (officalRecordId == "0") {
                    rs("COFFICIALRECORDID").value = "";
                }
            }
            commonDeleteRow(gridId);
            filterComponentData(getXMLDataForGridName("coverageListGrid"));
            break;
        case 'CYCLE_DETAIL':
            var sTransactionLogId = policyHeader.lastTransactionInfo.transactionLogId;
            var sRiskBaseRecordId = getObjectValue("riskBaseRecordId");
            var sProductCovComponentId = componentListGrid1.recordset("CPRODUCTCOVCOMPONENTID").value;
            var sRecordModeCode = componentListGrid1.recordset("CRECORDMODECODE").value;
            var sComponentEffectiveFromDate = componentListGrid1.recordset("CCOMPONENTEFFECTIVEFROMDATE").value;
            var sComponentEffectiveToDate = componentListGrid1.recordset("CCOMPONENTEFFECTIVETODATE").value;
            var sComponentCycleDate = componentListGrid1.recordset("CCOMPONENTCYCLEDATE").value;

            var url = getAppPath() + "/componentmgr/cycleDetail.do?"
                    + commonGetMenuQueryString() + "&transactionLogId=" + sTransactionLogId +
                    "&riskBaseRecordId=" + sRiskBaseRecordId + "&productCovComponentId=" + sProductCovComponentId +
                    "&recordModeCode=" + sRecordModeCode + "&componentEffectiveFromDate=" + sComponentEffectiveFromDate +
                    "&componentEffectiveToDate=" + sComponentEffectiveToDate + "&componentCycleDate=" + sComponentCycleDate;

            var processingDivId = openDivPopup("", url, true, true, "", "", "", "", "", "", "", false);
            break;
        case 'SURCHARGE_POINTS':
            var saveRequired = commonSaveRequiredToChangePages(pageName, "Surcharge Points", "N", "");
            if (saveRequired) {
                break;
            }
            else {
                var sTermBaseRecordId = getObjectValue("termBaseRecordId");
                var sTransactionLogId = policyHeader.lastTransactionInfo.transactionLogId;
                var sCoverageBaseRecordId = coverageListGrid1.recordset("CCOVERAGEBASERECORDID").value;
                var url = getAppPath() + "/componentmgr/maintainSurchargePoints.do?"
                        + commonGetMenuQueryString() + "&coverageBaseRecordId=" + sCoverageBaseRecordId;
                var processingDivId = openDivPopup("", url, true, true, "", "", 900, 900, "", "", "", false);
            }
            break;
        case 'PTNOTES':
            var sPolicyNo = getObjectValue("policyNo");
            var sRiskBaseRecordId = getObjectValue("riskBaseRecordId");
            var url = getAppPath() + "/notesmgr/maintainPartTimeNotes.do?"
                    + commonGetMenuQueryString() + "&process=loadAllPartTimeNotes&policyNumber=" +
                    sPolicyNo + "&riskBaseRecordId=" + sRiskBaseRecordId;
            if (!isPageDataChanged()) {
                url += "&refreshParentPageOnClose=Y";
            }
            var processingDivId = openDivPopup("", url, true, true, "", "", 900, 600, "", "", "", false);
            break;
        case 'DELETE':
            commonDeleteRow("coverageListGrid");
            break;
        case 'ADD':
            if (isTabStyle()) {
                clearCacheTabIds();
                clearCacheRowIds();
            }
            commonAddRow("coverageListGrid");
            break;
        case 'CANCEL':
            var sCoverageBaseRecordId = coverageListGrid1.recordset("CCOVERAGEBASERECORDID").value;
            var effFromDate = coverageListGrid1.recordset("CCOVERAGEEFFECTIVEFROMDATE").value;
            var effToDate = coverageListGrid1.recordset("CCOVERAGEEFFECTIVETODATE").value;
            var cCoverageDesc = coverageListGrid1.recordset("CPRODUCTCOVERAGEDESC").value;

            performCancellation("COVERAGE", sCoverageBaseRecordId, effFromDate, effToDate, "", "", "", cCoverageDesc);
            break;
        case 'CHANGE':
            // First check if there is modified record already
            if (preOoseChangeValidation("coverage", "coverageListGrid", "CCOVERAGEBASERECORDID")) {
                // Check Change option first by ajax call
                validateForOoseCoverage();
                if (isOoseCovgValid) {
                    addOoseCoverage();
                }
            }
            break;
        case 'CHGCOMPVALUE':
            // First check if there is modified record already
            if (preOoseChangeValidation("component", "componentListGrid", "CPOLCOVCOMPBASERECID")) {
                validateForOoseCoverage();
                if (isOoseCovgValid) {
                    changeComponentType = "chgCompValue";
                    addOoseComponent();
                }
            }
            break;
        case 'CHGCOMPDATE':
            // First check if there is modified record already
            if (preOoseChangeValidation("component", "componentListGrid", "CPOLCOVCOMPBASERECID")) {
                validateForOoseCoverage();
                if (isOoseCovgValid) {
                    changeComponentType = "chgCompDate";
                    addOoseComponent();
                }
            }
            break;
        case "PRIOR_ACTS":
            var saveRequired = commonSaveRequiredToChangePages(pageName, "Prior Acts", "N", "");
            if (saveRequired) {
                break;
            }
            else {
                var url = getAppPath() + "/coveragemgr/prioractmgr/maintainPriorActAction.do?"
                        + commonGetMenuQueryString() + "&process=loadAllPriorAct"
                        + "&riskBaseRecordId=" + coverageListGrid1.recordset("CRISKBASERECORDID").value
                        + "&riskEffectiveFromDate=" + getObjectValue("riskEffectiveFromDate")
                        + "&riskEffectiveToDate=" + getObjectValue("riskEffectiveToDate")
                        + "&coverageBaseRecordId=" + coverageListGrid1.recordset("CCOVERAGEBASERECORDID").value
                        + "&coverageId=" + coverageListGrid1.recordset("ID").value
                        + "&coverageBaseEffectiveFromDate=" + coverageListGrid1.recordset("CCOVERAGEBASEEFFECTIVEFROMDATE").value
                        + "&coverageEffectiveFromDate=" + coverageListGrid1.recordset("CCOVERAGEEFFECTIVEFROMDATE").value
                        + "&retroDate=" + coverageListGrid1.recordset("CRETRODATE").value
                        + "&productCoverageCode=" + coverageListGrid1.recordset("CPRODUCTCOVERAGECODE").value
                        + "&riskId=" + policyHeader.riskHeader.riskId;
            italicsFieldId = "PRIORACTSB";
            var divPopupId = openDivPopup("", url, true, true, "", "", 900, 880, "", "", "", false);
            }
            break;
        case 'REINSTATE':
            var riskStatus = getObjectValue("riskStatusCode");
            var policyStatus = getObjectValue("policyStatus");

            if (policyStatus == "CANCEL") {
                // Perform policy level reinstate
                var effFromDate = policyHeader.termEffectiveFromDate;
                var effToDate = policyHeader.termEffectiveToDate;
                var termBaseRecordId = getObjectValue("termBaseRecordId");
                performReinstate("POLICY", termBaseRecordId, policyHeader.policyId,
                    effFromDate, effToDate, policyStatus, "");
            }
            else if (riskStatus == "CANCEL") {
                // Perform risk level reinstate
                // Perform risk level reinstate
                var sRiskBaseRecordId = getObjectValue("riskBaseRecordId");
                var effFromDate = getObjectValue("riskEffectiveFromDate");
                var cancelDate = coverageListGrid1.recordset("CCANCELDATE");
                var cancelTransId = coverageListGrid1.recordset("CTRANSACTIONLOGID");
                performReinstate("RISK", sRiskBaseRecordId, "", effFromDate, cancelDate, riskStatus, cancelTransId);
            }
            else {
                // Perform current level reinstate
                var sCoverageBaseRecordId = coverageListGrid1.recordset("CCOVERAGEBASERECORDID").value;
                var sCoverageId = coverageListGrid1.recordset("id").value;
                var effFromDate = coverageListGrid1.recordset("CCOVERAGEBASEEFFECTIVEFROMDATE").value;
                var cancelDate = coverageListGrid1.recordset("CCANCELDATE");
                var coveragestatus = coverageListGrid1.recordset("CCOVERAGEBASESTATUS");
                var cancelTransId = coverageListGrid1.recordset("CTRANSACTIONLOGID");
                performReinstate("COVERAGE", sCoverageBaseRecordId, sCoverageId, effFromDate, cancelDate, coveragestatus, cancelTransId);
            }
            break;
        case 'MINI_TAIL':
            var riskBaseRecordId = getObjectValue("riskBaseRecordId");
            var url = getAppPath() + "/coveragemgr/minitailmgr/processMinitail.do?" + commonGetMenuQueryString() + getMenuQueryString();
            url += "&riskBaseRecordId=" + riskBaseRecordId;
            italicsFieldId = "MINITAILB";
            var processingDivId = openDivPopup("", url, true, true, "", "", "", "", "", "", "", false);
            break;
        case 'MANUSCRIPT':
            var saveRequired = commonSaveRequiredToChangePages(pageName, "Manuscript", "N", "");
            if (saveRequired) {
                break;
            }
            var url = getAppPath() + "/coveragemgr/manuscriptmgr/maintainManuscript.do?"
                + commonGetMenuQueryString() + "&process=loadAllManuscript"
                + "&riskId=" + policyHeader.riskHeader.riskId
                + "&coverageId=" + coverageListGrid1.recordset("id").value
                + "&coverageBaseRecordId=" + coverageListGrid1.recordset("CCOVERAGEBASERECORDID").value
                + "&coverageEffectiveToDate=" + coverageListGrid1.recordset("CCOVERAGEBASEEFFECTIVETODATE").value;
            var idName = 'R_menuitem_PM_MANU_PUP';
            var mi = getObject(idName);
            if (mi) {
                mi.children[0].style.backgroundImage = '';
            }
            if (policyHeader.policyCycleCode == 'POLICY') {
                italicsFieldId = "MANUSCRIPTENDORSEMENTB";
            }
            else {
                italicsFieldId = "MANUSCRIPTENDORSEMENTQUOTEB";
            }
            var divPopupId = openDivPopup("", url, true, true, "", "", "", "", "", "", "", false);
            break;
        case 'VLCOVG':
            if (commonIsOkToChangePages()) {
                viewVLCoverage();
            }
            break;
        case 'UPDATE':
            if (commonIsOkToChangePages()) {
                var currentCovgDataGrid = getXMLDataForGridName("coverageListGrid");
                if (!isEmptyRecordset(currentCovgDataGrid.recordset)) {
                    var productCoverageCode = currentCovgDataGrid.recordset("CPRODUCTCOVERAGECODE").value;
                    var coverageBaseRecordId = currentCovgDataGrid.recordset("CCOVERAGEBASERECORDID").value;
                    var coverageBaseEffectiveFromDate = currentCovgDataGrid.recordset("CCOVERAGEBASEEFFECTIVEFROMDATE").value;
                    var coverageId = currentCovgDataGrid.recordset('ID').value;
                    var riskId = policyHeader.riskHeader.riskId;
                    var policyId = getObjectValue("policyId");
                    var transactionLogId = policyHeader.lastTransactionInfo.transactionLogId;
                    var termBaseRecordId = getObjectValue("termBaseRecordId");

                    var url = getAppPath() + "/componentmgr/processComponent.do?"
                        + commonGetMenuQueryString() + "&productCoverageCode=" + productCoverageCode
                        + "&coverageBaseRecordId=" + coverageBaseRecordId + "&coverageBaseEffectiveFromDate=" + coverageBaseEffectiveFromDate
                        + "&riskId=" + riskId + "&coverageId=" + coverageId + "&policyId=" + policyId
                        + "&transactionLogId=" + transactionLogId + "&termBaseRecordId=" + termBaseRecordId;
                    var divPopupId = openDivPopup("", url, true, true, "", "", "850", "680", "", "", "", false);
                    fromPage = "compUpd";
                }
            }
            break;
        case 'MXS_PREM':
            // Check if there are unsaved chagnes
            var saveRequired = commonSaveRequiredToChangePages(pageName, "Manual Excess Premium", "N", "");
            if (saveRequired) {
                break;
            }
            var productCoverageCode = coverageListGrid1.recordset("CPRODUCTCOVERAGECODE").value;
            var coverageEffectiveFromDate = coverageListGrid1.recordset("CCOVERAGEBASEEFFECTIVEFROMDATE").value;
            var practiceStateCode = getObjectValue("practiceStateCode");
            var coverageLimitCode = coverageListGrid1.recordset("CCOVERAGELIMITCODE").value;

            var url = getAppPath() + "/coveragemgr/excesspremiummgr/maintainExcessPremium.do?" +
                      commonGetMenuQueryString() + "&productCoverageCode=" + productCoverageCode +
                      "&coverageEffectiveFromDate=" + coverageEffectiveFromDate +
                      "&practiceStateCode=" + practiceStateCode +
                      "&coverageLimitCode=" + coverageLimitCode +
                      "&fromCoverage=Y";
            openDivPopup("", url, true, true, "", "", "950", "780", "", "", "", false);
            break;
        case "SCHEDULE":
            var UpdateInd = coverageListGrid1.recordset("UPDATE_IND").value;
            var saveRequired = commonSaveRequiredToChangePages(pageName, "Schedule", "Y", UpdateInd);
            if (saveRequired) {
                break;
            }
            else {
                var url = getAppPath() + "/schedulemgr/maintainSchedule.do?" + commonGetMenuQueryString() +
                          "&process=loadAllSchedule&isFromCoverage=Y&coverageId=" + coverageListGrid1.recordset('ID').value;
                if (policyHeader.riskHeader) {
                    url = url + "&riskId=" + policyHeader.riskHeader.riskId;
                }
                if (policyHeader.policyCycleCode == 'POLICY') {
                    italicsFieldId = "COVGSCHEDB";
                }
                else {
                    italicsFieldId = "COVGSCHEDQUOTEB";
                }
                var divPopupId = openDivPopup("", url, true, true, "", "", "", "", "", "", "", false);
            }
            break;
        case 'PREVIEW':
            onPreviewButtonClick();
            break;
        case 'UNDER_COVG':
            var UpdateInd = coverageListGrid1.recordset("UPDATE_IND").value;
            var saveRequired = commonSaveRequiredToChangePages(pageName, "Underlying", "Y", UpdateInd);
            if (saveRequired) {
                break;
            }
            else {
                var url = getAppPath() + "/coveragemgr/underlyingmgr/maintainUnderlyingCoverage.do?"
                        + commonGetMenuQueryString() + "&process=loadAllUnderlyingCoverage&coverageId=" + coverageListGrid1.recordset('ID').value;
                if (policyHeader.policyCycleCode == 'POLICY') {
                    italicsFieldId = "UNDERLYINGCOVGB";
                }
                else {
                    italicsFieldId = "UNDERLYINGCOVGQUOTEB";
                }
                var divPopupId = openDivPopup("", url, true, true, "", "", "900", "700", "", "", "", false);
            }
            break;
    }
}

function preButtonClick(asBtn) {
    if(isReservedTab(getCurrentTab())) {
        operation = asBtn;
        return true;
    }
    var proceed = false;
    switch (asBtn) {
        case "ADD_COVERAGE":
        case "CHANGE":
        case "PREVIEW":
            operation = asBtn;
            processAutoSaveSubTab(getCurrentTab());
            if(autoSaveResultType == commonOnSubmitReturnTypes.noDataChange) {
                proceed = true;
            }
            break;
        default:
            proceed = true;
            break;
    }
    return proceed;
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
        if (hasObject(gridDetailDivId)){
            hideShowElementByClassName(getSingleObject(gridDetailDivId),
                    ( (getTableProperty(currentTbl, "hasrows") == true)) ? false : true);
        }
    }
}

function handleNavigateRecords(policyNo, policyTermHistoryId) {
    setWindowLocation("maintainCoverage.do?policyNo=" + policyNo + "&policyTermHistoryId=" + policyTermHistoryId);
}

function handleOnGetCoverageLimitShared(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }
            var oValueList = parseXML(data);

            if (oValueList.length > 0) {
                var sharedLimitsB = oValueList[0]["sharedLimitsB"];
                if (sharedLimitsB == "Y") {
                    setInputFormField("sharedLimitsB", sharedLimitsB);
                    alert(getMessage("pm.maintainCoverage.getLimitShared.error"));
                }
            }
        }
    }
}


//-----------------------------------------------------------------------------
// exclude filter fileds
//-----------------------------------------------------------------------------
function excludeFieldsForSettingUpdateInd() {
    return new Array(
        "policyNavLevelCode",
        "policyNavSourceId"
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
function viewCoverageAuditHistory(contextId) {
    var viewAuditUrl = getAppPath() + "/transactionmgr/auditmgr/viewAudit.do?"
        + commonGetMenuQueryString() + "&process=loadAllAudit" + "&fromPage=coverage-coverage" + "&contextId=" + contextId;
    var divPopupId = openDivPopup("", viewAuditUrl, true, true, "", "", "", "", "", "", "", false);
}
function viewComponentAuditHistory(contextId) {
    var viewAuditUrl = getAppPath() + "/transactionmgr/auditmgr/viewAudit.do?"
        + commonGetMenuQueryString() + "&process=loadAllAudit" + "&fromPage=coverage-component" + "&contextId=" + contextId;
    var divPopupId = openDivPopup("", viewAuditUrl, true, true, "", "", "", "", "", "", "", false);
}

function viewVLCoverage(tabId) {
    var viewUrl = getAppPath() + "/coveragemgr/vlcoveragemgr/maintainVLCoverage.do?"
        + commonGetMenuQueryString() + "&process=loadAllVLRisk"
        + "&coverageBaseRecordId=" + coverageListGrid1.recordset("CCOVERAGEBASERECORDID").value
        + "&coverageStatus=" + coverageListGrid1.recordset("CCOVERAGESTATUS").value
        + "&coverageBaseStatus=" + coverageListGrid1.recordset("CCOVERAGEBASESTATUS").value
        + "&coverageEffectiveFromDate=" + coverageListGrid1.recordset("CCOVERAGEEFFECTIVEFROMDATE").value;
    if(isTabStyle()){
        showPageInFrame(viewUrl, subFrameId, tabId);
    }else{
        var divPopupId = openDivPopup("", viewUrl, true, true, "", "", "700", "700", "", "", "", false);
    }
}

//-----------------------------------------------------------------------------
// Instruct display special warning messages.
//-----------------------------------------------------------------------------
function showSpecialWarning() {
    return true;
}

//-----------------------------------------------------------------------------
// Submit form and add default components in the back end
//-----------------------------------------------------------------------------
function addAllCoverage(oCoverageList) {
    var len = oCoverageList.length;
    var covgStr = "";
    /* Loop through selected coverages */
    for (var i = 0; i < len; i++) {
        // Coverage dulplicate validation
        if (!coverageDulplicateValidations(oCoverageList[i], true)) {
            continue;
        }
        covgStr += oCoverageList[i].PRODUCTCOVERAGECODE + "@"
                + oCoverageList[i].SHORTTERMB + "@"
                + oCoverageList[i].PRODUCTDEFAULTLIMITCODE + "@"
                + oCoverageList[i].RETRODATE + "@"
                + oCoverageList[i].PRODUCTDEFAULTSHAREDLIMITB + "@"
                + oCoverageList[i].ANNUALBASERATE + " @,";

    }

    if (covgStr == "") {
        return;
    }
    // Coverage date validations
    var dateChangeAllowedB = getObjectValue("dateChangeAllowedB");
    var effFromDate = "";
    var effToDate = "";

    var riskEffectiveDate = getObjectValue("riskEffectiveFromDate");
    if (dateChangeAllowedB == "Y") {
        effFromDate = riskEffectiveDate;
        effToDate = getObjectValue("riskEffectiveToDate");
    }
    else {
        effFromDate = policyHeader.lastTransactionInfo.transEffectiveFromDate;
        effToDate = policyHeader.termEffectiveToDate;
    }
    if (!coverageDateValidations(effFromDate, riskEffectiveDate, oCoverageList[0], true)) {
        return false;
    }
    setInputFormField("parentCovCode", covgStr);
    setInputFormField("riskId", policyHeader.riskHeader.riskId);

    showProcessingDivPopup();
    // Enable and hide all disabled fields in a form before submit
    enableFieldsForSubmit(document.forms[0]);
    document.forms[0].process.value = "addAllCoverage";
    alternateGrid_update('coverageListGrid');
    alternateGrid_update('componentListGrid');
    document.forms[0].action = buildMenuQueryString("", getFormActionAttribute());
    if(isTabStyle()){
        clearCacheTabIds();
        clearCacheRowIds();
    }
    baseOnSubmit(document.coverageList);
}

function isAddCompAvailable() {
    var transEffDate = getRealDate(policyHeader.lastTransactionInfo.transEffectiveFromDate);
    var selectedDataGrid = getXMLDataForGridName("coverageListGrid");
    var coverageBaseRecordId = selectedDataGrid.recordset("CCOVERAGEBASERECORDID").value;
    var selectedRecords = selectedDataGrid.documentElement.selectNodes("//ROW[(CBASERECORDB='N' and DISPLAY_IND = 'Y')"
            + " and (CCOVERAGEBASERECORDID = '" + coverageBaseRecordId + "')]");
    var size = selectedRecords.length;
    var addCompAvailable = 'N';
    for (var i = 0; i < size; i++) {
        var currentRecord = selectedRecords.item(i);
        var effDate = currentRecord.selectNodes("CCOVERAGEEFFECTIVEFROMDATE").item(0).text;
        var expDate = currentRecord.selectNodes("CCOVERAGEEFFECTIVETODATE").item(0).text;
        if ((getRealDate(effDate) <= transEffDate) && (getRealDate(expDate) > transEffDate)) {
            addCompAvailable = 'Y';
            if (selectedDataGrid.recordset("CISADDCOMPAVAILABLE").value != 'Y') {
                selectedDataGrid.recordset("CISADDCOMPAVAILABLE").value = 'Y';
                break;
            }
        }
    }
    if(addCompAvailable == 'N' && selectedDataGrid.recordset("CISADDCOMPAVAILABLE").value == 'Y'){
       selectedDataGrid.recordset("CISADDCOMPAVAILABLE").value = 'N';
    }
}

//-----------------------------------------------------------------------------
// Filter out the coverage by coverage group.
//-----------------------------------------------------------------------------
function filterCoverageData(coverageGroup) {
    setTableProperty(eval("coverageListGrid"), "selectedTableRowNo", null);
    if (isEmpty(coverageGroup)) {
        coverageListGrid_filter();
    }
    else {
        coverageListGrid_filter("CCOVERAGEGROUP='" + coverageGroup + "'");
    }
    if (isEmptyRecordset(coverageListGrid1.recordset)) {
        hideEmptyTable(getTableForXMLData(coverageListGrid1));
        hideGridDetailDiv("coverageListGrid");
        hideEmptyTable(getTableForXMLData(componentListGrid1));
        hideGridDetailDiv("componentListGrid");
    }
    else {
        showNonEmptyTable(getTableForXMLData(coverageListGrid1));
        showGridDetailDiv("coverageListGrid");
        // Filter component data
        filterComponentData(coverageListGrid1);
    }
}

function handleOnUnloadForDivPopup(divPopFrame) {
    if (italicsFieldId != "NONE") {
        italicsCurrentGridName = "coverageListGrid";
        italicsFieldIdList = italicsFieldId;
        commonOnSetButtonItalics(false);
        spliceTheRecordExistsFields();
    }
}

function getRecordExistsUrl() {
    var selectedDataGrid = getXMLDataForGridName("coverageListGrid");
    var url = getCSPath() + "/recordexistsmgr/maintainRecordExists.do?process=retrieveRecordExistsIndicator" +
            "&policyId=" + policyHeader.policyId;

    url += "&coverageBaseRecordId=" + selectedDataGrid.recordset("CCOVERAGEBASERECORDID").value +
            "&termEffectiveFromDate=" + policyHeader.termEffectiveFromDate +
            "&pageCode=PM_COVERAGE" +
            "&subSystemId=PMS";
    if (hasObject("policyViewMode")) {
        var policyViewMode = getObjectValue("policyViewMode");
        url = url + "&policyViewMode=" + policyViewMode;
    }
    if (typeof(policyHeader) != 'undefined') {
        var endorsementQuoteId = policyHeader.lastTransactionInfo.endorsementQuoteId;
        if (!isEmpty(endorsementQuoteId) && (endorsementQuoteId != 'null')) {
            url = url + "&endorsementQuoteId=" + endorsementQuoteId;
        }
    }
    return url;
}

function autoSaveSelectedCoverageWip() {
    var action;
    var url = getAppPath();
    alternateGrid_update('coverageListGrid');
    alternateGrid_update('componentListGrid');
    action = "autoSaveAllCoverage";
    url += "/coveragemgr/maintainCoverage.do?";
    enableFieldsForSubmit(document.forms[0]);
    url += "newSaveOption=WIP&date=" + new Date();
    url += "&riskId=" + policyHeader.riskHeader.riskId;
    postAjaxSubmitWithProcessingDiv(url, action, false, false, handleOnAutoSaveSelectedCoverageWip, false, false, false);
    if (showCoverageAutoSaveProcessDiv == true) {
        showProcessingDivPopup();
    }
}

function handleOnAutoSaveSelectedCoverageWip(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                resetRiskDropdownList();
                return;
            }
            setWindowLocation(directionUrl);
        }
    }
}

function resetRiskDropdownList() {
    showCoverageAutoSaveProcessDiv = false;
    var preRiskId = policyHeader.riskHeader.riskId;
    var riskObjsList = getObject("policyNavSourceId");
    for (i = 0; i < riskObjsList.length; i++) {
        if (riskObjsList[i].value == preRiskId) {
            riskObjsList[i].selected = true;
        }
    }
}

function handleExitWorkflow() {
    var transactionLogId = policyHeader.lastTransactionId;
    var url = "maintainCoverage.do?&process=loadWarningMessage&date=" + new Date() + "&transactionLogId=" + transactionLogId;
    // initiate async call
    new AJAXRequest("get", url, '', handleOnGetWarningMsg, false);
}

function handleOnSecondaryTabClick(tabId) {
    operation = "switchSecondlyTab";
    // if click current tab, do nothing
    if(tabId == getCurrentTab()) {
        return;
    }

    removeMessagesForFrame();

    // cache tab ids for sub tab further action
    setCacheTabIds(getCurrentTab() + "," + tabId);

    processAutoSaveSubTab(getPreviousTab());

    if (autoSaveResultType == commonOnSubmitReturnTypes.submitSuccessfully) {
        //the form of sub-tab was submitted.
        switchSecondlyTabFlg = true;
    }
    else if (autoSaveResultType == commonOnSubmitReturnTypes.noDataChange) {
        /**
         * if no any data change exists on sub-tab, select target tab and save it as default tab ids
         */
        //Issue 190191: clear operation value when in no data change situation.
        clearOperationForTabStyle();
        selectTabById(tabId);
    }
    else if (autoSaveResultType == commonOnSubmitReturnTypes.commonValidationFailed ||
            autoSaveResultType == commonOnSubmitReturnTypes.saveInProgress) {
        //common validation failed on the Grid/Form of sub-Tab
        //No actions.
        setCacheTabIds(getPreviousTab() + "," + getPreviousTab());
    }
}

function handleOnUIProcess() {
    mainPageLock.initialLock();
}

function autoSaveSubIFrameForNavigation(){
    processAutoSaveSubTab(getCurrentTab());
    if(autoSaveResultType == commonOnSubmitReturnTypes.submitSuccessfully) {
        operation = 'NAVIGATE';
    }else if(autoSaveResultType == commonOnSubmitReturnTypes.noDataChange) {
        commonOnSubmit('NAVIGATE', true, true, true);
    }else if (autoSaveResultType == commonOnSubmitReturnTypes.commonValidationFailed ||
            autoSaveResultType == commonOnSubmitReturnTypes.saveInProgress) {
        resetRiskDropdownList();
    }
}

//-----------------------------------------------------------------------------
// If 1) sub-tab is swithced,
//    2) coverage row is switched,
//    3) primary tab is switched,
//  then auto save logic will be invoked,
//  will check if the data change exists on sub-tab firstly.
//-----------------------------------------------------------------------------
function autoSaveSubTab(toBeSavedTab) {
    setAutoSavedTabResultType(commonOnSubmitReturnTypes.noDataChange);
    //if policy is official, return nodatachange
    if((!policyHeader.wipB && !allowToModifyWhenOfficial(toBeSavedTab)) || isUndefined(toBeSavedTab)) {
        return;
    }
    //remove messages on parent screen.
    removeMessagesForFrame();
    if (!isReservedTab(toBeSavedTab)) {
        mainPageLock.lock();
    }

    switch (toBeSavedTab) {
        case "PRIOR_ACT":
            var functionExists = eval("getIFrameWindow().commonOnButtonClick");
            if(functionExists){
                getIFrameWindow().commonOnButtonClick('SAVE');
            }
            break;
        case 'MINI':
            var functionExists = eval("getIFrameWindow().commonOnButtonClick");
            if(functionExists){
                getIFrameWindow().commonOnButtonClick('saveAllMinitail');
            }
            break;
        case 'MANU':
            var functionExists = eval("getIFrameWindow().commonOnSubmit");
            if(functionExists){
                getIFrameWindow().commonOnSubmit('SAVE');
            }
            break;
        case 'COMP_UPDATE':
            var functionExists = eval("getIFrameWindow().hasDataChanged");
            if(functionExists && getIFrameWindow().hasDataChanged()){
                functionExists = eval("getIFrameWindow().commonOnSubmit");
                if(functionExists){
                    getIFrameWindow().commonOnSubmit('APPLY');
                }
            }
            break;
        case 'MAN_EXCESS_PREM':
            var functionExists = eval("getIFrameWindow().commonOnSubmit");
            if(functionExists){
                getIFrameWindow().commonOnSubmit('SAVE');
            }
            break;
        case "SCH":
            var functionExists = eval("getIFrameWindow().commonOnSubmit");
            if(functionExists){
                getIFrameWindow().commonOnSubmit('SAVE');
            }
            break;
        case 'VL_COVG':
            var functionExists = eval("getIFrameWindow().commonOnSubmit");
            if(functionExists){
                getIFrameWindow().commonOnSubmit('saveAllVLRisk', false, false, false, true);
            }
            break;
        case 'COVERAGE':
            var functionExists = eval("window.commonOnSubmit");
            if(functionExists){
                setInputFormField("callFromSubTabB", "Y");
                commonOnSubmit('SAVEWIP', true, true, false);
            }
            break;
        case 'COMPONENT':
            var functionExists = eval("window.commonOnSubmit");
            if(functionExists){
                setInputFormField("callFromSubTabB", "Y");
                commonOnSubmit('SAVEWIP', true, true, false);
            }
            break;
        case 'UNDER_COVG':
            var functionExists = eval("getIFrameWindow().commonOnButtonClick");
            if(functionExists){
                getIFrameWindow().commonOnButtonClick('SAVE');
            }
            break;
    }

    if (autoSaveResultType == commonOnSubmitReturnTypes.noDataChange
            || autoSaveResultType == commonOnSubmitReturnTypes.commonValidationFailed
            || autoSaveResultType == commonOnSubmitReturnTypes.saveInProgress) {
        mainPageLock.unlock();
    }
}

function callBackAutoSaveForFrame(autoSaveResult) {
    var switchPrimaryTabFlgLoc = false;
    var switchGridRowFlgLoc = false;
    var switchSecondlyTabFlgLoc = false;

    if(switchPrimaryTabFlg || switchSecondlyTabFlg || switchGridRowFlg){
        operation = undefined;
    }

    if(switchPrimaryTabFlg){
        switchPrimaryTabFlgLoc = switchPrimaryTabFlg;
        switchPrimaryTabFlg = false;
    }

    if(switchSecondlyTabFlg){
        switchSecondlyTabFlgLoc = switchSecondlyTabFlg;
        switchSecondlyTabFlg = false;
    }
    if(switchGridRowFlg){
        switchGridRowFlgLoc = switchGridRowFlg;
        switchGridRowFlg = false;
    }

    updateMainTokenWithIframe(getObject(subFrameId));

    if(!autoSaveResult){
        if(isDefined(operation)) {
            if(operation == 'NAVIGATE'){
                resetRiskDropdownList();
            }
        }

        if(switchSecondlyTabFlgLoc){
            setCacheTabIds(getPreviousTab() + "," + getPreviousTab());
        }

        if(switchGridRowFlgLoc){
            rollback = true;
            selectRowById("coverageListGrid", getPreviousRow());
        }

        if(switchPrimaryTabFlgLoc){
            nextPrimaryTabId = "";
            nextPrimaryTabAction = "";
            return;
        }
    }

    if (autoSaveResult) {
        removeMessagesForFrame();

        var functionExists = eval("window.handleOnItalicTabStyle");
        if (functionExists) {
            handleOnItalicTabStyle();
        }

        if(!new RegExp("^switch.*$").test(operation) && isDefined(operation)) { // except switchPrimaryTab operation
            if(getPreviousTab() == 'COMP_UPDATE'){
                var applyResult = getIFrameWindow().applyResult;
                if(applyResult == '-1' || applyResult == '0'){
                    return;
                }
            }

            processMainPageAfterAutoSaveSubTab();
            return;
        }

        if(switchSecondlyTabFlgLoc || switchGridRowFlgLoc){
            if(getPreviousTab() == 'COMP_UPDATE'){
                var applyResult = getIFrameWindow().applyResult;
                if(applyResult == '-1' || applyResult == '0'){
                    if(switchGridRowFlgLoc){
                        rollback = true;
                        selectRowById("coverageListGrid", getPreviousRow());
                    }
                    return;
                }
            }

            setCacheRowIds(getCurrentRow() + "," + getCurrentRow());
            if(requiredSubmitMainPage(getPreviousTab()) && eval("getIFrameWindow().isNeedToRefreshParentB")
                    && getIFrameWindow().isNeedToRefreshParentB()) {
                setCacheTabIds(getCurrentTab() + "," + getCurrentTab());
                refreshPage();
                return;
            }
            selectTabById(getCurrentTab());
        }else if(switchPrimaryTabFlgLoc){
            showProcessingImgIndicator();
            setWindowLocation(nextPrimaryTabAction);
        }else{
            if(requiredSubmitMainPage(getCurrentTab()) && eval("getIFrameWindow().isNeedToRefreshParentB")
                    && getIFrameWindow().isNeedToRefreshParentB()) {
                refreshPage();
            }
        }
    }
}

function processMainPageAfterAutoSaveSubTab() {
    switch(operation) {
        case "WIP":
        case "SAVEWIP":
            if(isUndefined(currentSubmitAction) && operation == "WIP"){
                currentSubmitAction = "SAVE";
                eventHandler = "submitForm";
                document.forms[0].action = buildMenuQueryString("PM_PT_VIEWCVG", getFormActionAttribute());
                document.forms[0].process.value = "saveAllCoverage";
            }
            manualSaveWIP("PM_PT_VIEWCVG", "saveAllCoverage");
            break;
        case "OFFICIAL":
            if(isUndefined(currentSubmitAction)){
                currentSubmitAction = "SAVE";
                eventHandler = "submitForm";
                document.forms[0].action = buildMenuQueryString("PM_PT_VIEWCVG", getFormActionAttribute());
                document.forms[0].process.value = "saveAllCoverage";
            }
            manualSaveOfficial();
            break;
        case "RATE":
            if(isUndefined(currentSubmitAction)){
                currentSubmitAction = "SAVE";
            }
            setInputFormField("processRatingB", "Y");
            manualSaveWIP("PM_PT_VIEWCVG", "saveAllCoverage");
            break;
        case "CHANGE":
        case "ADD_COVERAGE":
            if(requiredSubmitMainPage(getCurrentTab()) && eval("getIFrameWindow().isNeedToRefreshParentB") &&
                    getIFrameWindow().isNeedToRefreshParentB()) {
                setBtnOperation(operation);
                refreshPage();
                return;
            }
            handleOnButtonClick(operation);
            break;
        case "PREVIEW":
            onPreviewButtonClick();
            break;
        case "NAVIGATE":
            commonOnSubmit('NAVIGATE', true, true, true);
            break;
        default:
            handleSaveOptionSelection(operation);
    }
}

function handleAfterViewValidation(){
    if (fromPage == "compUpd"){
        if(isTabStyle()){
            setBtnOperation(operation);
        }
        refreshPage();
        fromPage = "";
    }
}

function handleOnShowPageInFrame(tabId) {
    switch (tabId) {
        case "PRIOR_ACT":
            var url = getAppPath() + "/coveragemgr/prioractmgr/maintainPriorActAction.do?"
                    + commonGetMenuQueryString() + "&process=loadAllPriorAct"
                    + "&riskBaseRecordId=" + coverageListGrid1.recordset("CRISKBASERECORDID").value
                    + "&riskEffectiveFromDate=" + getObjectValue("riskEffectiveFromDate")
                    + "&riskEffectiveToDate=" + getObjectValue("riskEffectiveToDate")
                    + "&coverageBaseRecordId=" + coverageListGrid1.recordset("CCOVERAGEBASERECORDID").value
                    + "&coverageId=" + coverageListGrid1.recordset("ID").value
                    + "&coverageBaseEffectiveFromDate=" + coverageListGrid1.recordset("CCOVERAGEBASEEFFECTIVEFROMDATE").value
                    + "&coverageEffectiveFromDate=" + coverageListGrid1.recordset("CCOVERAGEEFFECTIVEFROMDATE").value
                    + "&retroDate=" + coverageListGrid1.recordset("CRETRODATE").value
                    + "&productCoverageCode=" + coverageListGrid1.recordset("CPRODUCTCOVERAGECODE").value
                    + "&riskId=" + policyHeader.riskHeader.riskId;
            if (policyHeader.policyCycleCode == 'POLICY') {
                italicsFieldId = "PRIORACTSB";
            }
            else {
                italicsFieldId = "PRIORACTSQUOTEB";
            }
            showPageInFrame(url, subFrameId, tabId);
            break;
        case 'MINI':
            var riskBaseRecordId = getObjectValue("riskBaseRecordId");
            var url = getAppPath() + "/coveragemgr/minitailmgr/processMinitail.do?" + commonGetMenuQueryString() + getMenuQueryString();
            url += "&riskBaseRecordId=" + riskBaseRecordId + "&isFromCoverage=Y";
            url += "&coverageBaseRecordId=" + coverageListGrid1.recordset("CCOVERAGEBASERECORDID").value;
            if (policyHeader.policyCycleCode == 'POLICY') {
                italicsFieldId = "MINITAILB";
            }
            else {
                italicsFieldId = "MINITAILQUOTEB";
            }
            showPageInFrame(url, subFrameId, tabId);
            break;
        case 'MANU':
            var url = getAppPath() + "/coveragemgr/manuscriptmgr/maintainManuscript.do?"
                    + commonGetMenuQueryString() + "&process=loadAllManuscript"
                    + "&riskId=" + policyHeader.riskHeader.riskId
                    + "&coverageId=" + coverageListGrid1.recordset("id").value
                    + "&coverageBaseRecordId=" + coverageListGrid1.recordset("CCOVERAGEBASERECORDID").value
                    + "&coverageEffectiveToDate=" + coverageListGrid1.recordset("CCOVERAGEBASEEFFECTIVETODATE").value;
            var idName = 'R_menuitem_PM_MANU_PUP';
            var mi = getObject(idName);
            if (mi) {
                mi.children[0].style.backgroundImage = '';
            }
            if (policyHeader.policyCycleCode == 'POLICY') {
                italicsFieldId = "MANUSCRIPTENDORSEMENTB";
            }
            else {
                italicsFieldId = "MANUSCRIPTENDORSEMENTQUOTEB";
            }
            showPageInFrame(url, subFrameId, tabId);
            break;
        case 'COMP_UPDATE':
            var currentCovgDataGrid = getXMLDataForGridName("coverageListGrid");
            if (!isEmptyRecordset(currentCovgDataGrid.recordset)) {
                var productCoverageCode = currentCovgDataGrid.recordset("CPRODUCTCOVERAGECODE").value;
                var coverageBaseRecordId = currentCovgDataGrid.recordset("CCOVERAGEBASERECORDID").value;
                var coverageBaseEffectiveFromDate = currentCovgDataGrid.recordset("CCOVERAGEBASEEFFECTIVEFROMDATE").value;
                var coverageId = currentCovgDataGrid.recordset('ID').value;
                var riskId = policyHeader.riskHeader.riskId;
                var policyId = getObjectValue("policyId");
                var transactionLogId = policyHeader.lastTransactionInfo.transactionLogId;
                var termBaseRecordId = getObjectValue("termBaseRecordId");
                fromPage = "compUpd";
                var url = getAppPath() + "/componentmgr/processComponent.do?"
                        + commonGetMenuQueryString() + "&productCoverageCode=" + productCoverageCode
                        + "&coverageBaseRecordId=" + coverageBaseRecordId + "&coverageBaseEffectiveFromDate=" + coverageBaseEffectiveFromDate
                        + "&riskId=" + riskId + "&coverageId=" + coverageId + "&policyId=" + policyId
                        + "&transactionLogId=" + transactionLogId + "&termBaseRecordId=" + termBaseRecordId;
                showPageInFrame(url, subFrameId, tabId);
            }
            break;
        case 'MAN_EXCESS_PREM':
            var productCoverageCode = coverageListGrid1.recordset("CPRODUCTCOVERAGECODE").value;
            var coverageEffectiveFromDate = coverageListGrid1.recordset("CCOVERAGEBASEEFFECTIVEFROMDATE").value;
            var practiceStateCode = getObjectValue("practiceStateCode");
            var coverageLimitCode = coverageListGrid1.recordset("CCOVERAGELIMITCODE").value;

            var url = getAppPath() + "/coveragemgr/excesspremiummgr/maintainExcessPremium.do?" +
                    commonGetMenuQueryString() + "&productCoverageCode=" + productCoverageCode +
                    "&coverageEffectiveFromDate=" + coverageEffectiveFromDate +
                    "&practiceStateCode=" + practiceStateCode +
                    "&coverageLimitCode=" + coverageLimitCode +
                    "&fromCoverage=Y";
            showPageInFrame(url, subFrameId, tabId);
            break;
        case "SCH":
            var url = getAppPath() + "/schedulemgr/maintainSchedule.do?" + commonGetMenuQueryString() +
                    "&process=loadAllSchedule&isFromCoverage=Y&coverageId=" + getCurrentRow();
            // "&process=loadAllSchedule&isFromCoverage=Y&coverageId=" + coverageListGrid1.recordset('ID').value;
            if (policyHeader.riskHeader) {
                url = url + "&riskId=" + policyHeader.riskHeader.riskId;
            }
            if (policyHeader.policyCycleCode == 'POLICY') {
                italicsFieldId = "COVGSCHEDB";
            }
            else {
                italicsFieldId = "COVGSCHEDQUOTEB";
            }
            showPageInFrame(url, subFrameId, tabId);
            break;
        case 'VL_COVG':
            viewVLCoverage(tabId);
            break;
        case 'COVERAGE':
            if (policyHeader.policyCycleCode == 'POLICY') {
                italicsFieldId = "COVGDETAILB";
            }
            else {
                italicsFieldId = "COVGDETAILQUOTEB";
            }
            showPageInFrame("", "coverageDetail", tabId);
            break;
        case 'COMPONENT':
            if (policyHeader.policyCycleCode == 'POLICY') {
                italicsFieldId = "COVGCOMPONENTB";
            }
            else {
                italicsFieldId = "COVGCOMPONENTQUOTEB";
            }
            showPageInFrame("", "componentDetail", tabId);
            break;
        case 'UNDER_COVG':
            var UpdateInd = coverageListGrid1.recordset("UPDATE_IND").value;
            var saveRequired = commonSaveRequiredToChangePages(pageName, "Underlying", "Y", UpdateInd);
            if (saveRequired) {
                break;
            }
            else {
                var url = getAppPath() + "/coveragemgr/underlyingmgr/maintainUnderlyingCoverage.do?"
                        + commonGetMenuQueryString() + "&process=loadAllUnderlyingCoverage&coverageId=" + coverageListGrid1.recordset('ID').value;
                if (policyHeader.policyCycleCode == 'POLICY') {
                    italicsFieldId = "UNDERLYINGCOVGB";
                }
                else {
                    italicsFieldId = "UNDERLYINGCOVGQUOTEB";
                }
                showPageInFrame(url, subFrameId, tabId);
            }
            break;
    }
}

function isEmptyMainPageGridRecordset(){
    return isEmptyRecordset(getXMLDataForGridName("coverageListGrid").recordset);
}
