//-----------------------------------------------------------------------------
// Java script file for maintainCoverageClass.jsp.
//
// (C) 2011 Delphi Technology, inc. (dti)
// Date:   April 29, 2011
// Author: syang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 04/29/2011       syang       120316 - Added addCoverageClassDone() and selectFirstInsertedCoverageClass() to
//                                       select the first inserted coverage class.
// 05/01/2011       fcb         105791 - Added convertCoverage() for safety.
// 07/20/2011       syang       121208 - Modified addOneCoverageClass() to group coverage.
// 08/12/2011       syang       121208 - Modified handleOnButtonClick() to open select coverage class page with different width.
// 08/30/2011       ryzhao      124458 - Modified coverageClassDateValidations() to use formatDateForDisplay to format date parameters.
// 07/24/2012       awu         129250 - Added getCoverageClassAutoSaveQueryString(), autoSaveSelectedCoverageClassWip(),
//                                       handleOnAutoSaveSelectedCoverageClassWip(), resetCoverageDropdownList().
// 12/27/2012       tcheng      139862 - Added handleExitWorkflow() to pop up warning message.
// 04/26/2013       awu         141758 - Added addAllCoverageClass().
// 05/17/2013       tcheng      143761 - Modified coverageClassDulplicateValidations() to filter flat cancel temp
//                                       coverage class in renewal wip for duplicate validation.
// 01/01/2014       Parker      148029 - Cache risk header, coverage header and policy navigation information to policy header.
// 02/13/2014       awu         147405 - 1. Modified handleOnButtonClick to get the cancel date for reinstate.
//                                       2. Use the coverage class' base record status for reinstate.
// 03/11/2014       fcb         152685 - transaction log id passed to loadWarningMessage
// 05/29/2014       jyang       149970 - Modified handleOnButtonClick,pass coverage class description to cancellationDetail
//                                       page.
// 07/24/2014       jyang       156146 - Modified autoSaveSelectedCoverageClassWIP, corrected syncChange parameters sequence.
// 08/25/2014       AWU         152034 - Modified handleOnSubmit to append risk/coverage/coverage class IDs to URL for RATE.
// 10/09/2014       wdang       156038 - 1. Replaced getObject('riskId') with policyHeader.riskHeader.riskId. 
//                                       2. Replaced getObject('coverageId') with policyHeader.coverageHeader.coverageId.
// 03/10/2017       sjin        180675 - 1. Added methods call in the function handleOnLoad() and selectRowInGridOnPageLoad
//                                          when changed the buttons to the secondary tabs.
//                                       2. Added functions "autoSaveSubTab(), coverageClassListGrid_selectRow(),
//                                          handleOnShowPageInFrame() for UI change.
// 09/26/2017       wli         187808 - Modified coverageClassListGrid_selectRow to exclude highlight fake tab for
//                                       autosaving when switch grid row.
// 11/09/2017       tzeng       187689 - Modified coverageClassListGrid_selectRow() to support processAutoSaveSubTab().
// 09/17/2018       ryzhao      195271 - Modified autoSaveSubTab() to set indicator if it is auto save data from sub-tab
//                                       when saving coverage class information.
//-----------------------------------------------------------------------------
var currentCoverageClassCode;
var isOoseCovgClassValid = true;
var origCovgClassId = "";
var currentExposureUnit;
var showCoverageClassAutoSaveProcessDiv = true;
var italicsFieldId = "NONE";
function handleOnLoad() {
    // Set the navigation "Level" value and call commonOnChange to set the "Go To" value
    var policyNavLevelCode = "";
    if (getObject("policyNavLevelCode")) {
        policyNavLevelCode = getObjectValue("policyNavLevelCode");
    }
    setInputFormField("needToHandleExitWorkFlow", "Y");
    if(isTabStyle())
        commonOnUIProcess();
    invokeWorkflow();
}

function selectRowInGridOnPageLoad() {
    $.when(dti.oasis.grid.getLoadingPromise("coverageClassListGrid")).then(function(){
        // select row by previousely selected coverage class ID if there was no error
        var isCoverageClassGridEmpty = isEmptyRecordset(getXMLDataForGridName("coverageClassListGrid").recordset);
        if (!hasErrorMessages && !isCoverageClassGridEmpty && currentCoverageClassId) {
            selectRowById("coverageClassListGrid", currentCoverageClassId);
        } else {
            if(isTabStyle()){
                if(getPreviousRow() == "") {
                    selectFirstRowInGrid("coverageClassListGrid");
                }else {
                    rollback = true;
                    selectRowById("coverageClassListGrid", getPreviousRow());
                }
                if(isCoverageClassGridEmpty) {
                    selectFirstTab();
                }
            }else{
                selectFirstRowInGrid("coverageClassListGrid");
            }
        }
    });
}

function coverageClassListGrid_setInitialValues() {
    // If OOSE Coverage Class, return directly.
    if (isForOose == "Y") {
        // Copy original data to new row
        setRecordsetByObject(coverageClassListGrid1, ooseRowData);
        sendAJAXRequest("getInitialValuesForOoseCoverageClass");
        // Reset flag
        isForOose = "N";
        return;
    }
    // Call Ajax call to get addtional default value.
    var url = "maintainCoverageClass.do?" + commonGetMenuQueryString() +
              "&productCoverageClassCode=" + currentCoverageClassCode +
              "&process=getInitialValuesForCoverageClass" +
              "&exposureUnit="+currentExposureUnit;

    if (policyHeader.riskHeader) {
        url = url + "&riskId=" + policyHeader.riskHeader.riskId;
    }

    if (policyHeader.coverageHeader) {
        url = url + "&coverageId=" + policyHeader.coverageHeader.coverageId;
    }

    // initiate async call
    new AJAXRequest("get", url, '', commonHandleOnGetInitialValues, false);
}

function addAllCoverageClass(oCoverageClassList, divPopup) {
    var len = oCoverageClassList.length;
    var coveClassStr = "";
    for (var i = 0; i < len; i++) {
        if (!coverageClassDulplicateValidations(oCoverageClassList[i], true)) {
            continue;
        }
        coveClassStr += oCoverageClassList[i].PRODUCTCOVERAGECLASSCODE + "@ "
                + oCoverageClassList[i].EXPOSUREUNIT + "@,"
    }
    closeDiv(divPopup);
    if (coveClassStr == "") {
        return;
    }

    showProcessingDivPopup();
    setInputFormField("selectedCoverageClass", coveClassStr);
    setInputFormField("riskId", policyHeader.riskHeader.riskId);
    setInputFormField("coverageId", policyHeader.coverageHeader.coverageId);
    enableFieldsForSubmit(document.forms[0]);
    document.forms[0].process.value = "addAllCoverageClass";
    alternateGrid_update('coverageClassListGrid');
    document.forms[0].action = buildMenuQueryString("", getFormActionAttribute());
    baseOnSubmit(document.coverageClassList);
}

function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'SAVE':
            document.forms[0].action = buildMenuQueryString("PM_PT_VIEWCLASS", getFormActionAttribute());
            document.forms[0].process.value = "saveAllCoverageClass";
            loadSaveOptions("PM_COVERAGE_CLASS", "submitForm");
            proceed = false;
            break;
        case 'SAVEWIP':
            document.forms[0].action = buildMenuQueryString("PM_PT_VIEWCLASS", getFormActionAttribute());
            document.forms[0].process.value = "saveAllCoverageClass";
            handleSaveOptionSelection("WIP");
            break;

        case 'RATE':
            document.forms[0].action = buildMenuQueryString("PM_PT_VIEWCLASS", getFormActionAttribute());
            document.forms[0].process.value = "saveAllCoverageClass";
            setInputFormField("newSaveOption", "WIP");
            setInputFormField("processRatingB", "Y");
            break;

        case 'NAVIGATE':
            document.forms[0].process.value = "loadAllCoverageClass";
            var policyNavLevelCode = "";
            var navSourceIds = "";
            if (getObject("policyNavLevelCode")) {
                policyNavLevelCode = getObjectValue("policyNavLevelCode");
            }
            if (policyNavLevelCode == "RISK") {
                navSourceIds = getObjectValue("riskNavSourceId").split(":");
            } else if (policyNavLevelCode == "COVERAGE") {
                navSourceIds = getObjectValue("coverageNavSourceId").split(":");
            }
            setInputFormField("riskId", navSourceIds[0]);
            setInputFormField("coverageId", navSourceIds[1]);
            break;

        default:
            proceed = false;
    }
    return proceed;
}

function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'ADD_COVERAGE_CLASS':
            var url = getAppPath() + "/coverageclassmgr/selectProductCoverageClass.do?" +
                      commonGetMenuQueryString() + getMenuQueryString();
            var pageWidth = "";
            if(getObjectValue("selectCoverageClassDetailDisplay") == 'Y'){
                pageWidth = 900;
            }
            url = url + '&coverageClassDetailDisplay='+getObjectValue("selectCoverageClassDetailDisplay");
            var processingDivId = openDivPopup("", url, true, true, "", "", pageWidth, "", "", "", "", false);
            break;
        case 'CANCEL':
            var sCoverageBaseRecordId = coverageClassListGrid1.recordset("CCOVERAGECLASSBASERECORDID").value;
            var effFromDate = coverageClassListGrid1.recordset("CCOVERAGECLASSEFFECTIVEFROMDATE").value;
            var effToDate = coverageClassListGrid1.recordset("CCOVERAGECLASSEFFECTIVETODATE").value;
            var cCoverageClassDesc = coverageClassListGrid1.recordset("CCOVERAGECLASSLONGDESCRIPTION").value;
            performCancellation("COVERAGE CLASS", sCoverageBaseRecordId, effFromDate, effToDate, "", "", "", cCoverageClassDesc);
            break;
        case 'CHANGE':
        // First check if there is modified record already
            if (preOoseChangeValidation("coverage class", "coverageClassListGrid", "CCOVERAGECLASSBASERECORDID")) {
                // Check Change option first by ajax call
                sendAJAXRequest("validateForOoseCoverageClass");
                if (isOoseCovgClassValid) {
                    addOoseCovgClass();
                }
            }
            else{
                isForOose = "N";
            }
            break;
        case "REINSTATE":
            var coverageStatus = getObjectValue("coverageStatusCode")
            var riskStatus = getObjectValue("riskStatusCode");
            var policyStatus = getObjectValue("policyStatus");
            var cancelDate = coverageClassListGrid1.recordset("CCANCELDATE");
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
                var cancelTransId = coverageClassListGrid1.recordset("CTRANSACTIONLOGID");
                performReinstate("RISK", sRiskBaseRecordId, "", effFromDate, cancelDate, riskStatus, cancelTransId);
            }
            else if (coverageStatus == "CANCEL") {
                // Perform coverage level reinstate
                var sCoverageBaseRecordId = getObjectValue("coverageBaseRecordId");
                var effFromDate = getObjectValue("coverageEffectiveFromDate")
                var cancelTransId = coverageClassListGrid1.recordset("CTRANSACTIONLOGID");
                performReinstate("COVERAGE", sCoverageBaseRecordId, "", effFromDate, cancelDate, coverageStatus, cancelTransId);
            }
            else {
                // Perform current level reinstate
                var sCoverageClassBaseRecordId = coverageClassListGrid1.recordset("CCOVERAGECLASSBASERECORDID").value;
                var sCoverageClassId = coverageClassListGrid1.recordset("id").value;
                var effFromDate = coverageClassListGrid1.recordset("CCOVERAGECLASSEFFECTIVEFROMDATE").value;
                var coverageClassStatus = coverageClassListGrid1.recordset("CCOVERAGECLASSBASESTATUS");
                var cancelTransId = coverageClassListGrid1.recordset("CTRANSACTIONLOGID");
                performReinstate("COVERAGE CLASS", sCoverageClassBaseRecordId, sCoverageClassId, effFromDate, cancelDate, coverageClassStatus, cancelTransId);
            }
            break;
        case "DETAIL":
            var transactionLogId = policyHeader.lastTransactionId;
            var termEffectiveFromDate = policyHeader.termEffectiveFromDate;
            var termEffectiveToDate = policyHeader.termEffectiveToDate;
            var screenModeCode = getObject("screenModeCode").value;
            var parentCoverageBaseRecordId = coverageClassListGrid1.recordset("CPARENTCOVERAGEBASERECORDID").value;
            if(isEmpty(parentCoverageBaseRecordId)){
                parentCoverageBaseRecordId = 0;
            }
            var paramsObj = new Object();
            paramsObj.transactionLogId = transactionLogId;
            paramsObj.reportCode = "PM_COVG_CLASS_WORKSHEET";
            paramsObj.termEffDate = termEffectiveFromDate;
            paramsObj.termExpDate = termEffectiveToDate;
            paramsObj.screenModeCode = screenModeCode;
            paramsObj.parentCoverageBaseRecordId = parentCoverageBaseRecordId;
            viewPolicyReport(paramsObj);
            break;
    }
}

function getMenuQueryString(id, url) {
    var tempUrl = "";

    if (policyHeader.riskHeader) {
        tempUrl = tempUrl + "&riskId=" + policyHeader.riskHeader.riskId;
    }

    if (policyHeader.coverageHeader) {
        tempUrl = tempUrl + "&coverageId=" + policyHeader.coverageHeader.coverageId;
    }

    var selectedDataGrid = getXMLDataForGridName(getCurrentlySelectedGridId()) ;
    if (selectedDataGrid != null) {
        if (getCurrentlySelectedGridId() == "coverageClassListGrid") {
            try {
                var coverageClassId = selectedDataGrid.recordset('ID').value;
                tempUrl = tempUrl + "&coverageClassId=" + coverageClassId;
            }
            catch (ex) {
                //ignore error
            }
        }
    }
    return tempUrl;
}

function getCoverageClassAutoSaveQueryString() {
    var tempUrl = "";
    var policyNavLevelCode = "";
    var objectName = "policyNavSourceId";
    if (getObject("policyNavLevelCode")) {
        policyNavLevelCode = getObjectValue("policyNavLevelCode");
    }
    if (policyNavLevelCode == "RISK") {
        objectName = "riskNavSourceId";
    } else if (policyNavLevelCode == "COVERAGE") {
        objectName = "coverageNavSourceId";
    }
    if (hasObject(objectName)) {
        var policyNavSourceIds = getObjectValue(objectName).split(":");
        var riskId = policyNavSourceIds[0];
        tempUrl = tempUrl + "&riskId=" + riskId;
        var coverageId = policyNavSourceIds[1];
        tempUrl = tempUrl + "&coverageId=" + coverageId;
    }

    var selectedDataGrid = getXMLDataForGridName(getCurrentlySelectedGridId());
    if (selectedDataGrid != null) {
        if (getCurrentlySelectedGridId() == "coverageClassListGrid") {
            try {
                var coverageClassId = selectedDataGrid.recordset('ID').value;
                tempUrl = tempUrl + "&coverageClassId=" + coverageClassId;
            }
            catch (ex) {
                //ignore error
            }
        }
    }
    if (hasObject("policyNavLevelCode")) {
        var policyNavLevelCode = getObjectValue("policyNavLevelCode");
        tempUrl = tempUrl + "&policyNavLevelCode=" + policyNavLevelCode;
    }
    return tempUrl;
}

function addOneCoverageClass(oCoverageClass, showMessage) {
    if (showMessage == null) {
        showMessage = true;
    }

    /* coverage class duplicate validations */
    if (!coverageClassDulplicateValidations(oCoverageClass, showMessage)) {
        return false;
    }

    /* coverage class date validations */
    if (!coverageClassDateValidations(oCoverageClass, showMessage)) {
        return false;
    }

    currentCoverageClassCode = oCoverageClass.PRODUCTCOVERAGECLASSCODE;
    currentExposureUnit = oCoverageClass.EXPOSUREUNIT;
    /* insert one empty record */
    commonOnButtonClick('ADD');
}

//-----------------------------------------------------------------------------
// Dulplicated coverage class validations
//-----------------------------------------------------------------------------
function coverageClassDulplicateValidations(oCoverageClass, showMessage) {
    if (!getTableProperty(getTableForGrid("coverageClassListGrid"), "hasrows")) {
        return true;
    }

    var rowIndex = 0;
    first(coverageClassListGrid1);

    while (!coverageClassListGrid1.recordset.eof) {
        // use PRODUCTCOVERAGECLASSCODE here (instead of PRODUCTCOVERAGECODE), it is defined in store procedure.
        if (oCoverageClass.PRODUCTCOVERAGECLASSCODE == coverageClassListGrid1.recordset("CPRODUCTCOVERAGECLASSCODE").value  &&
            coverageClassListGrid1.recordset("CRECORDMODECODE").value != "OFFICIAL" &&
            coverageClassListGrid1.recordset("CCOVERAGECLASSEFFECTIVEFROMDATE").value !=
                coverageClassListGrid1.recordset("CCOVERAGECLASSEFFECTIVETODATE").value) {
            if (showMessage) {
                var paras = new Array(oCoverageClass.COVERAGECLASSSHORTDESCRIPTION);
                var msg = getMessage("pm.addCoverageClass.duplicateCoverageClass.error", paras);
                handleError(msg, "", coverageClassListGrid1.recordset("ID").value);
            }
            return false;
        }

        rowIndex = rowIndex + 1;
        next(coverageClassListGrid1);
    }

    return true;
}

//-----------------------------------------------------------------------------
// Coverage class date validations
//-----------------------------------------------------------------------------
function coverageClassDateValidations(oCoverage, showMessage) {
    var covgClassEffFromDate = policyHeader.lastTransactionInfo.transEffectiveFromDate;
    var covgEffFromDate = getObjectValue("coverageEffectiveFromDate");
    if (isDate2OnOrAfterDate1(covgEffFromDate, covgClassEffFromDate) != "Y") {
        if (showMessage) {
            var paras = new Array("", oCoverage.COVERAGECLASSSHORTDESCRIPTION, formatDateForDisplay(covgEffFromDate));
            var msg = getMessage("pm.addCoverageClass.covgClassFromDate.error", paras);
            handleError(msg);
        }
        return false;
    }
    return true;
}

function handleOnValidateForOoseCoverageClass(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                isOoseCovgClassValid = false;
            }
        }
    }
}

function addOoseCovgClass() {
    // Save current row's data into object
    origCovgClassId = coverageClassListGrid1.recordset("ID").value;
    ooseRowData = getObjectFromRecordset(coverageClassListGrid1);

    commonOnButtonClick('ADD');
}

function handleOnGetInitialValuesForOoseCoverageClass(ajax) {
    commonHandleOnGetInitialValues(ajax);
}

function sendAJAXRequest(process) {
    // set url
    var url = "maintainCoverageClass.do?process=" + process +
              "&" + commonGetMenuQueryString("PM_MAINTAIN_COVERAGE_CLASS", "");
    // Fix 101838, system should pass the parameters "recordModeCode" and "ratingModuleCode".
    switch (process) {
        case 'getInitialValuesForOoseCoverageClass':
            var selectedDataGrid = getXMLDataForGridName("coverageClassListGrid");
            url += "&coverageClassId=" + origCovgClassId +
                   "&riskId=" + policyHeader.riskHeader.riskId +
                   "&recordModeCode=" + selectedDataGrid.recordset("CRECORDMODECODE").value +
                   "&officialRecordId=" + selectedDataGrid.recordset("COFFICIALRECORDID").value +
                   "&policyFormCode=" + selectedDataGrid.recordset("CPOLICYFORMCODE").value +
                   "&ratingModuleCode=" + selectedDataGrid.recordset("CRATINGMODULECODE").value;
            break;
        case 'validateForOoseCoverageClass':
            var selectedDataGrid = getXMLDataForGridName("coverageClassListGrid");
            url += "&coverageClassEffectiveFromDate=" +
                   selectedDataGrid.recordset("CCOVERAGECLASSEFFECTIVEFROMDATE").value +
                   "&coverageClassBaseRecordId=" + selectedDataGrid.recordset("CCOVERAGECLASSBASERECORDID").value +
                   "&recordModeCode=" + selectedDataGrid.recordset("CRECORDMODECODE").value +
                   "&ratingModuleCode=" + selectedDataGrid.recordset("CRATINGMODULECODE").value;
            break;
    }

    // initiate call
    var ajaxResponseHandler = "handleOn" + process.substr(0, 1).toUpperCase() + process.substr(1);
    //alert(url);
    new AJAXRequest("get", url, "", eval(ajaxResponseHandler), false);
}

function handleNavigateRecords(policyNo, policyTermHistoryId) {
    setWindowLocation("maintainCoverageClass.do?policyNo=" + policyNo + "&policyTermHistoryId=" + policyTermHistoryId);
}

//-----------------------------------------------------------------------------
// exclude filter fileds
//-----------------------------------------------------------------------------
function excludeFieldsForSettingUpdateInd() {
    return new Array(
        "policyNavLevelCode",
        "policyNavSourceId",
        "riskNavSourceId",
        "coverageNavSourceId"
        );
}
function viewAuditHistory(contextId){
     var viewAuditUrl = getAppPath() + "/transactionmgr/auditmgr/viewAudit.do?"
                + commonGetMenuQueryString() + "&process=loadAllAudit"+"&fromPage=coverageclass-class"+"&contextId="+contextId;
            var divPopupId = openDivPopup("", viewAuditUrl, true, true, "", "", "", "", "", "", "", false);
}

//-----------------------------------------------------------------------------
// Instruct display special warning messages.
//-----------------------------------------------------------------------------
function showSpecialWarning() {
    return true;
}

//-----------------------------------------------------------------------------
// Select the first inserted coverage class.
//-----------------------------------------------------------------------------
var firstCoverageClassId = 0;
function addCoverageClassDone(coverageClassId) {
    firstCoverageClassId = coverageClassId;
    var testCode = 'getTableProperty(getTableForGrid(\"coverageClassListGrid\"), "isUserReadyStateReadyComplete")'
            + '&&!getTableProperty(getTableForGrid(\"coverageClassListGrid\"), "filtering")';
    var callbackCode = 'selectFirstInsertedCoverageClass()';
    executeWhenTestSucceeds(testCode, callbackCode, 50);
}

function selectFirstInsertedCoverageClass() {
    selectRowById("coverageClassListGrid", firstCoverageClassId);
}

function autoSaveSelectedCoverageClassWip() {
    var action;
    var url = getAppPath();
    syncChanges(origcoverageClassListGrid1, coverageClassListGrid1);
    setInputFormField("txtXML", getChanges(coverageClassListGrid1));
    setInputFormField("riskId", policyHeader.riskHeader.riskId);
    setInputFormField("coverageId", policyHeader.coverageHeader.coverageId);
    action = "autoSaveAllCoverageClass";
    url += "/coverageclassmgr/maintainCoverageClass.do?";
    enableFieldsForSubmit(document.forms[0]);
    url += "newSaveOption=WIP&date=" + new Date();
    postAjaxSubmitWithProcessingDiv(url, action, false, false, handleOnAutoSaveSelectedCoverageClassWip, false, false, false);
    if (showCoverageClassAutoSaveProcessDiv == true) {
        showProcessingDivPopup();
    }
}


function handleOnAutoSaveSelectedCoverageClassWip(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                resetCoverageDropdownList();
                return;
            }
            setWindowLocation(directionUrl);
        }
    }
}

function resetCoverageDropdownList() {
    showCoverageClassAutoSaveProcessDiv = false;
    var preCoverageId = policyHeader.coverageHeader.coverageId;
    var preRiskId = policyHeader.riskHeader.riskId;
    var policyNavLevelCode = "";
    var objectName = "policyNavSourceId";
    if (getObject("policyNavLevelCode")) {
        policyNavLevelCode = getObjectValue("policyNavLevelCode");
    }
    if (policyNavLevelCode == "RISK") {
        objectName = "riskNavSourceId";
    } else if (policyNavLevelCode == "COVERAGE") {
        objectName = "coverageNavSourceId";
    }
    var coverageObjsList = getObject(objectName);
    if (coverageObjsList) {
        for (i = 0; i < coverageObjsList.length; i++) {
            if (coverageObjsList[i].value == preRiskId + ":" + preCoverageId) {
                coverageObjsList[i].selected = true;
            }
        }
    }
}

function handleExitWorkflow() {
    var transactionLogId = policyHeader.lastTransactionId;
    var url = "maintainCoverageClass.do?&process=loadWarningMessage&date=" + new Date() + "&transactionLogId=" + transactionLogId;
    // initiate async call
    new AJAXRequest("get", url, '', handleOnGetWarningMsg, false);
}

function autoSaveSubTab(toBeSavedTab) {
    setAutoSavedTabResultType(commonOnSubmitReturnTypes.noDataChange);
    if(!isReservedTab(toBeSavedTab)) {
        mainPageLock.lock();
    }
    switch (toBeSavedTab) {
        case 'COVERAGE_CLASS':
            var functionExists = eval("window.commonOnSubmit");
            if(functionExists){
                setInputFormField("callFromSubTabB", "Y");
                commonOnSubmit('SAVEWIP', true, true, false);
            }
            break;
    }
    if(autoSaveResultType == commonOnSubmitReturnTypes.noDataChange
            || autoSaveResultType == commonOnSubmitReturnTypes.commonValidationFailed
            || autoSaveResultType == commonOnSubmitReturnTypes.saveInProgress) {
        mainPageLock.unlock();
    }
}

function coverageClassListGrid_selectRow(id) {
   if(isTabStyle()){
       var selectedDataGrid = getXMLDataForGridName("coverageClassListGrid");
       if (selectedDataGrid.recordset("UPDATE_IND").value != 'I') {
           italicsCurrentGridName = "coverageClassListGrid";
           commonOnSetButtonItalics(true);
       }

       // cache the default tab ids and coverage class ids when first time loaded
        if(getPreviousRow() == "") {
            setCacheRowIds(id + "," + id);
            setCacheTabIds(getFirstTab() + "," + getFirstTab());
        }
        var autoSaveResult = commonOnSubmitReturnTypes.noDataChange;
        if(rollback) {
            /**
             *  for coverage class detail, roll back row in function selectRowInGridOnPageLoad()--> selectRowById
             *  for other tabs, roll back row in function callBackAutoSaveForFrame()-->selectRowById
             *  what we do:
             *  cache the roll back coverage class row id
             */
            setCacheRowIds(id + "," + id);
        }else {
            // for coverage class detail, no data change, no need to auto save
            if(isReservedTab(getPreviousTab()) && !isPageGridsDataChanged()) {
                setCacheRowIds(id + "," + id);
            }else {
                setCacheRowIds(getPreviousRow() + "," + id);
                // if not switch row, no need to auto save
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
                 * for coverage class detail auto save roll back, need to position previous tab
                 * for other tabs, nothing to do
                 */
                if(isReservedTab(getPreviousTab())) {
                    selectTabById(getPreviousTab());
                }
                rollback=false;
            }else{
                setCacheRowIds(id + "," + id);
                selectTabById(getCurrentTab());
            }
        }
        else if (autoSaveResult == commonOnSubmitReturnTypes.commonValidationFailed ||
                autoSaveResult == commonOnSubmitReturnTypes.saveInProgress) {
            rollback=true;
            selectRowById("coverageClassListGrid", getPreviousRow());
        }

    }
}

function handleOnShowPageInFrame(tabId) {
    switch (tabId) {
        case 'COVERAGE_CLASS':
            if (policyHeader.policyCycleCode == 'POLICY') {
                italicsFieldId = "COVGCLASSDETAILB";
            }
            else {
                italicsFieldId = "COVGCLASSDETAILQUOTEB";
            }
            showPageInFrame("", "coverageClassDetail", tabId);
            break;
    }
}

function getRecordExistsUrl() {
    var selectedDataGrid = getXMLDataForGridName("coverageClassListGrid");
    var url = getCSPath() + "/recordexistsmgr/maintainRecordExists.do?process=retrieveRecordExistsIndicator" +
            "&policyId=" + policyHeader.policyId;

    url += "&coverageClassBaseRecordId=" + selectedDataGrid.recordset("CCOVERAGECLASSBASERECORDID").value +
            "&termEffectiveFromDate=" + policyHeader.termEffectiveFromDate +
            "&pageCode=PM_COVERAGE_CLASS" +
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

function isEmptyMainPageGridRecordset(){
    return isEmptyRecordset(getXMLDataForGridName("coverageClassListGrid").recordset);
}