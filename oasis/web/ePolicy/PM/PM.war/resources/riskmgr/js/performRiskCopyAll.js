//-----------------------------------------------------------------------------
// Javascript file for performRiskCopyAll.jsp.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   May 07, 2010
// Author: syang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 05/07/2010       syang       106876 - Set target risk effective from/to Date to riskEffectiveFromDate/riskEffectiveToDate.
// 09/24/2010       dzhang      108261 - Copy All.
// 10/15/2010       dzhang      112064 - Rollback the changes done by 112064.
// 10/22/2010       syang       113025 - When click "Select All" or "Deselect All" in source panel, system should only select
//                              the visible fields. Added isVisibleField() to check whether the field is visible. 
// 10/28/2010       syang       113025 - System should find the capital fieldId from invisibleFieldIds.
// 04/16/2012       syang       127216 - Modified filterTagetRisks() to deselect the check box on top of risk grid.
// 07/27/2012       awu         135411 - Modified processCopyAll() to removed the select row.
// 08/02/2012       ryzhao      134938 - Modified isVisibleField to add one more condition to check if field has been
//                              hidden by field dependency, page entitlement, etc.
// 04/16/2013       tcheng      143484 - Modified selectAllGridAttributes() to show/hidden fields on each coverage.
// 10/10/2013       tcheng      148387 - 1. Revert 143484 change;
//                                       2. Explicitly select each row when loop records.
// 07/04/2014       kxiang      155331 - Modified function filterTagetRisks()  to support Multi-Select.
// 10/09/2014       wdang       156038 - Replaced getObject('riskId') with policyHeader.riskHeader.riskId.
// 07/11/2016       lzhang      177681 - add maintain retroactive date page link after
//                                       click ok button of successful or error message
// 08/04/2016       xnie        178475 - Modified maintianRetroDate() to reset hasValidationErrorForAllRisk and
//                                       toCovgBaseRecordIds after copy all.
// 12/05/2016       ssheng      181436 - Modified function filterTagetRisks() to avoid that system lists all risks
//                                       of the policy when only check the Allied option of Target section.
// 03/10/2017       eyin        180675 - Added logic to process copy all and delete all in new UI tab style case.
// 12/13/2017       eyin        190085 - Modified processCopyAll() and processDeleteAll(), to refresh page immediately
//                                       once processing is done without any error and there is no 'View Validation Error'
//                                       && 'Maintain Retro Date' popup is opened.
// 11/02/2018       clm         Issue 195889 - Grid replacement using getParentWindow and modify the logic to handle
//                                             checkboxes in userRowchange
//-----------------------------------------------------------------------------
var srcRiskFormId = "srcRiskFormId";
var srcCovgGridId = "coverageListGrid";
var srcCompGridId = "componentListGrid";
var srcCovgClassGridId = "coverageClassListGrid";
var srcCoiGridId = "coiListGrid";
var srcAffiGridId = "affiliationListGrid";
var srcScheduleGridId = "scheduleListGrid";
var tgtRiskGridId = "riskListGrid";
var selectedCoiIds = new Array();
var selectedAffiIds = new Array();
var selectedScheduleIds = new Array();
var isLastSelectedRisk = false;
var isFirstSelectedRisk = false;
var copyFailureFlag = false;
var isValidateErrorExist = false;
var isRefreshRiskPageRequired = false;
var isProcessing = false;
var errorOccurs = false;

var deleteSourceFailureFlag = false;
var deleteTargetFailureFlag = false;
var srcRiskFormFields ;
var srcCovgGridFields ;
var srcCompGridFields ;
var srcCovgClassGridFields ;
var toCovgBaseRecordIds = "";
var hasValidationErrorForAllRisk = false;


function handleOnLoad() {
    filterTagetRisks();
    //register all form fields
    srcRiskFormFields = getObjectValue("srcRiskFormFields").split(",");
    srcCovgGridFields = getObjectValue("srcCovgGridFields").split(",");
    srcCompGridFields = getObjectValue("srcCompGridFields").split(",");
    srcCovgClassGridFields = getObjectValue("srcCovgClassGridFields").split(",");
}

function isInArray(vArray, vStr) {
    for (var i = 0; i < vArray.length; i++) {
        if (vStr == vArray[i]) {
            return true;
        }
    }
    return false;
}

function viewValidationError() {
    var url = getAppPath() + "/transactionmgr/viewValidationError.do?"
        + commonGetMenuQueryString() + "process=loadAllValidationError";
    var processingDivId = getOpenCtxOfDivPopUp().openDivPopup("", url, true, true, "", "", "", "", "", "", "", false);
}

function handleOnButtonClick(btn) {
    switch (btn) {
        case "PROCESS_COPYALL":
            if (!isDeleteCopyAll()) {
                //check if source has been selected
                var isSourceSelected = isAttributesSelected(srcRiskFormFields) || isGridItemSelected(srcCovgGridId)
                    || isGridItemSelected(srcCompGridId) || isGridItemSelected(srcCovgClassGridId)
                    || getObjectValue("coiSelectedB") == 'Y' || getObjectValue("affiliationSelectedB") == "Y"
                    || getObjectValue("scheduleSelectedB") == 'Y';

                //check if target has been selected
                var isTargetSelected = isGridItemSelected(tgtRiskGridId);

                if(!isSourceSelected && !isTargetSelected){
                    autoSaveResultType = commonOnSubmitReturnTypes.noDataChange;
                    syncResultToParent(autoSaveResultType);
                    break;
                }
                if (!isSourceSelected) {
                    handleError(getMessage("pm.maintainRiskCopy.sourceNotSelected.error"));
                    autoSaveResultType = commonOnSubmitReturnTypes.commonValidationFailed;
                    syncResultToParent(autoSaveResultType);
                    break;
                }
                if (!isTargetSelected) {
                    handleError(getMessage("pm.maintainRiskCopy.targetNotSelected.error"));
                    autoSaveResultType = commonOnSubmitReturnTypes.commonValidationFailed;
                    syncResultToParent(autoSaveResultType);
                    break;
                }
                if (isSourceSelected && isTargetSelected) {
                    if (confirm(getMessage("pm.maintainRiskCopy.processCopy.confirm"))) {
                        if(isButtonStyle()){
                            setTimeout("processCopyAll()", 0);
                            showProcessingDivPopup();
                        }else{
                            showProcessingDivPopup();
                            processCopyAll();
                        }
                    }else{
                        autoSaveResultType = commonOnSubmitReturnTypes.noDataChange;
                    }
                }
            }
            else {
                //check if source has been selected
                var isSourceSelected = isGridItemSelected(srcCovgGridId)
                    || isGridItemSelected(srcCompGridId) || isGridItemSelected(srcCovgClassGridId);

                //check if target has been selected
                var isTargetSelected = isGridItemSelected(tgtRiskGridId);

                if(!isSourceSelected && !isTargetSelected){
                    autoSaveResultType = commonOnSubmitReturnTypes.noDataChange;
                    syncResultToParent(autoSaveResultType);
                    break;
                }
                if (!isSourceSelected) {
                    handleError(getMessage("pm.maintainRiskCopy.deleteAll.sourceNotSelected.error"));
                    autoSaveResultType = commonOnSubmitReturnTypes.commonValidationFailed;
                    syncResultToParent(autoSaveResultType);
                    break;
                }
                if (!isTargetSelected) {
                    handleError(getMessage("pm.maintainRiskCopy.deleteAll.targetNotSelected.error"));
                    autoSaveResultType = commonOnSubmitReturnTypes.commonValidationFailed;
                    syncResultToParent(autoSaveResultType);
                    break;
                }
                if (isSourceSelected && isTargetSelected) {
                    if (confirm(getMessage("pm.maintainRiskCopy.deleteAll.processDelete.confirm"))) {
                        if(isButtonStyle()){
                            setTimeout("processDeleteAll()", 0);
                            showProcessingDivPopup();
                        }else{
                            showProcessingDivPopup();
                            processDeleteAll();
                        }
                    }else{
                        autoSaveResultType = commonOnSubmitReturnTypes.noDataChange;
                    }
                }
            }
            syncResultToParent(autoSaveResultType);
            break;
        case "SEL_AFFI":
            var url = getAppPath() + "/riskmgr/selectAffiliation.do?"
                + commonGetMenuQueryString() + "process=loadAllAffiliation"
                + "&riskId=" + policyHeader.riskHeader.riskId;
            var processingDivId = openDivPopup("", url, true, true, "", "", "", "", "", "", "", false);
            break;
        case "SEL_COI":
            var url = getAppPath() + "/riskmgr/selectCoi.do?"
                + commonGetMenuQueryString() + "process=loadAllCoiHolder"
                + "&riskId=" + policyHeader.riskHeader.riskId;
            var processingDivId = openDivPopup("", url, true, true, "", "", "", "", "", "", "", false);
            break;
        case "SEL_SCHEDULE":
            var url = getAppPath() + "/riskmgr/selectSchedule.do?"
                + commonGetMenuQueryString() + "process=loadAllSchedule"
                + "&riskId=" + policyHeader.riskHeader.riskId;
            var processingDivId = openDivPopup("", url, true, true, "", "", "", "", "", "", "", false);
            break;
    
        case "SELALL_RISK":
            selectAllRiskAttributes("Y");
            break;
        case "DESELALL_RISK":
            selectAllRiskAttributes("N");
            break;
        case "SELALL_COVG":
            selectAllGridAttributes(srcCovgGridId, "Y");
            break;
        case "DESELALL_COVG":
            selectAllGridAttributes(srcCovgGridId, "N");
            break;
        case "SELALL_COMP":
            selectAllGridAttributes(srcCompGridId, "Y");
            break;
        case "DESELALL_COMP":
            selectAllGridAttributes(srcCompGridId, "N");
            break;
        case "SELALL_SUBCOVG":
            selectAllGridAttributes(srcCovgClassGridId, "Y");
            break;
        case "DESELALL_SUBCOVG":
            selectAllGridAttributes(srcCovgClassGridId, "N");
            break;
        case "CLOSE_COPYALL":
            closeCopyAll();
            break;
    }
}

function closeCopyAll() {
    if (isNeedToRefreshParentB()) {
        getParentWindow().refreshPage(true);
    }
    else {
        commonOnButtonClick("CLOSE_DIV");
    }
}

function isNeedToRefreshParentB(){
    return isRefreshRiskPageRequired;
}

function selectAllRiskAttributes(ynFlag) {
    if (!isDeleteCopyAll()) {
        for (var i = 0; i < srcRiskFormFields.length; i++) {
            // For issue 108261 copy all
            if(isVisibleField("invisibleRiskFormFields", srcRiskFormFields[i])) {
                getObject(srcRiskFormFields[i]).value = ynFlag;
            }
        }
    }
    selectAllGridAttributes(srcCovgGridId, ynFlag);
    selectAllGridAttributes(srcCompGridId, ynFlag);
    selectAllGridAttributes(srcCovgClassGridId, ynFlag);
}

function selectAllGridAttributes(gridId, ynFlag) {
    var gridXMLData = getXMLDataForGridName(gridId);
    if (!isEmptyRecordset(gridXMLData.recordset)) {
        if (ynFlag == 'Y') {
            updateAllSelectInd("SELECT", gridId);
        }
        else {
            updateAllSelectInd("DESELECT", gridId);
        }

        //update select all check box
        var chkSelAllArray = document.getElementsByName("chkCSELECT_ALL");
        for (var i = 0; i < chkSelAllArray.length; i++) {
            if (chkSelAllArray[i] && getTableForGridElement(chkSelAllArray[i]).id == gridId) {
                chkSelAllArray[i].checked = (ynFlag == 'Y');
            }
        }
        var absPosition = gridXMLData.recordset.AbsolutePosition;
        var rowId = gridXMLData.recordset("ID").value;
        first(gridXMLData);
        while (!gridXMLData.recordset.eof) {
            selectRowById(gridId, gridXMLData.recordset("ID").value);
            updateRowAttributes(gridId, ynFlag);
            next(gridXMLData);
        }
        first(gridXMLData);
        gridXMLData.recordset.move(absPosition - 1);
        selectRowById(gridId, rowId);
    }
}

function processCopyAll() {

    updateGrid(srcCovgGridId);
    updateGrid(srcCompGridId);
    updateGrid(srcCovgClassGridId);

    setInputFormField("processCode", 'COPYRISKALL');
    setInputFormField("riskId", policyHeader.riskHeader.riskId);
    var riskListXMLData = getXMLDataForGridName(tgtRiskGridId);

    var selectedCount = getSelectedRows(riskListXMLData, tgtRiskGridId).length;
    var selectedIndex = 0;
    if (!isEmptyRecordset(riskListXMLData.recordset)) {
        isProcessing = true;
        var absPosition = riskListXMLData.recordset.AbsolutePosition;
        first(riskListXMLData);
        while (!riskListXMLData.recordset.eof) {
            if (riskListXMLData.recordset('CSELECT_IND').value == -1) {

                isFirstSelectedRisk = (selectedIndex == 0);
                selectedIndex++;
                isLastSelectedRisk = (selectedIndex == selectedCount);

                setInputFormField("toRiskBaseRecordId", riskListXMLData.recordset("CRISKBASERECORDID").value);
                setInputFormField("toRiskId", riskListXMLData.recordset("ID").value);
                setInputFormField("riskEffectiveFromDate", riskListXMLData.recordset("CEFF1").value);
                setInputFormField("riskEffectiveToDate", riskListXMLData.recordset("CEXP1").value);
                
                if (isLastSelectedRisk) {
                    setInputFormField("isLastSelectedRisk", 'Y');
                }
                else {
                    setInputFormField("isLastSelectedRisk", 'N');
                }
                if (isFirstSelectedRisk) {
                    setInputFormField("isFirstSelectedRisk", 'Y');
                }
                else {
                    setInputFormField("isFirstSelectedRisk", 'N');
                }
                postAjaxSubmit("/riskmgr/performRiskCopyAll.do", "copyAllRisk", false, false, handleOnCopyAllDone, false, false);

                if (!isProcessing) {
                    break;
                }
            }

            next(riskListXMLData);
        }
        first(riskListXMLData);
        riskListXMLData.recordset.move(absPosition - 1);

        /*
         * issue 190085
         * Do NOT copy all automatically once user tries to switch grid row/sub-tab
         * After user clicks Process button, need to refresh page immediately once the COPY ALL Processing is done,
         * It is applicable for below 2 situation in tab style:
         * 1. hasValidationErrorForAllRisk = false - means popup 'View Validation Error' is NOT opened.
         * 2. toCovgBaseRecordIds.length = 0 - means popup 'Maintain Retro Date' is NOT opened.
         * 3. errorOccurs = false - means no error occurs
         */
        if(isTabStyle() && !hasValidationErrorForAllRisk && !errorOccurs
                && toCovgBaseRecordIds.length == 0){
            getParentWindow().refreshPage();
        }else{
            isRefreshRiskPageRequired = true;
        }

        closeProcessingDivPopup();
        if(!hasValidationErrorForAllRisk){
            maintianRetroDate();
        }else{
            autoSaveResultType = commonOnSubmitReturnTypes.commonValidationFailed;
        }
    }else{
        autoSaveResultType = commonOnSubmitReturnTypes.noDataChange;
    }
    syncResultToParent(autoSaveResultType);
}

function processDeleteAll() {
    updateGrid(srcCovgGridId);
    updateGrid(srcCompGridId);
    updateGrid(srcCovgClassGridId);

    setInputFormField("processCode", 'DELETERISKALL');
    setInputFormField("riskId", policyHeader.riskHeader.riskId);
    var riskListXMLData = getXMLDataForGridName("riskListGrid");

    var selectedCount = getSelectedRows(riskListXMLData, tgtRiskGridId).length;
    var selectedIndex = 0;
    if (!isEmptyRecordset(riskListXMLData.recordset)) {
        isProcessing = true;
        var absPosition = riskListXMLData.recordset.AbsolutePosition;
        first(riskListXMLData);
        while (!riskListXMLData.recordset.eof) {
            if (riskListXMLData.recordset('CSELECT_IND').value == -1) {
                //select row
                selectRowById(tgtRiskGridId, riskListXMLData.recordset("ID").value);

                isFirstSelectedRisk = (selectedIndex == 0);
                selectedIndex++;
                isLastSelectedRisk = (selectedIndex == selectedCount);

                setInputFormField("toRiskId", riskListXMLData.recordset("ID").value)
                setInputFormField("toRiskBaseRecordId", riskListXMLData.recordset("CRISKBASERECORDID").value)
                
                if (isLastSelectedRisk) {
                    setInputFormField("isLastSelectedRisk", 'Y');
                }
                else {
                    setInputFormField("isLastSelectedRisk", 'N');
                }
                if (isFirstSelectedRisk) {
                    setInputFormField("isFirstSelectedRisk", 'Y');
                }
                else {
                    setInputFormField("isFirstSelectedRisk", 'N');
                }
                postAjaxSubmit("/riskmgr/performRiskCopyAll.do", "deleteAllCopiedRisk", false, false, handleOnDeleteAllDone, false, false);

            }

            next(riskListXMLData);
        }
        first(riskListXMLData);
        riskListXMLData.recordset.move(absPosition - 1);

        closeProcessingDivPopup();
        // if it is last selected risk, handle validation errors and copy failure flag
        if (isLastSelectedRisk && deleteTargetFailureFlag) {
            handleError(getMessage("pm.maintainRiskCopy.deleteAll.deleteTargetFail.error"));
            errorOccurs = true;
        }

        //delete source risk
        if (confirm(getMessage("pm.maintainRiskCopy.deleteAll.deleteSource.confirm"))) {
            setInputFormField("toRiskId", policyHeader.riskHeader.riskId);
            setInputFormField("toRiskBaseRecordId", getObjectValue("sourceRiskBaseRecordId"));
            postAjaxSubmit("/riskmgr/performRiskCopyAll.do", "deleteAllCopiedRisk", false, false, handleOnDeleteAllDone, false, false);
        }

        if (deleteSourceFailureFlag) {
            handleError(getMessage("pm.maintainRiskCopy.deleteAll.deleteSourceFail.error"));
        }

        if (!deleteTargetFailureFlag && !deleteSourceFailureFlag) {
            alert(getMessage("pm.maintainRiskCopy.deleteSucess.msg"));
            autoSaveResultType = commonOnSubmitReturnTypes.submitSuccessfully;
        }else{
            autoSaveResultType = commonOnSubmitReturnTypes.commonValidationFailed;
        }

        isLastSelectedRisk = false;
        isFirstSelectedRisk = false;
        deleteTargetFailureFlag = false;
        deleteSourceFailureFlag = false;

        isRefreshRiskPageRequired = true;
        /*
         * issue 190085
         * Do NOT delete all automatically once user tries to switch grid row/sub-tab
         * After user clicks Process button,
         * need to refresh page immediately once the DELETE ALL Processing is done without any error.
         */
        if(isTabStyle() && !errorOccurs){
            getParentWindow().refreshPage();
        }else{
            isRefreshRiskPageRequired = true;
        }
    }else{
        autoSaveResultType = commonOnSubmitReturnTypes.noDataChange;
    }
    syncResultToParent(autoSaveResultType);
}

function getSelectedRows(xmlData, gridId) {
    var selectedXML = xmlData.documentElement.selectNodes("//ROW[CSELECT_IND=-1]");
    return selectedXML;
}

function updateGrid(gridId) {
    alternateGrid_update(gridId, "CSELECT_IND='-1' or CSELECT_IND='0'")
}

function userRowchange(obj) {
    var objName = obj.name;
    if (objName == 'chkCSELECT_IND') {
        if (!obj.checked) {
            if (getTableForGridElement(obj).id == srcCovgGridId) {
                updateRowAttributes(srcCovgGridId, 'N');
            }
            if (getTableForGridElement(obj).id == srcCompGridId) {
                updateRowAttributes(srcCompGridId, 'N');
            }
            else if (getTableForGridElement(obj).id == srcCovgClassGridId) {
                updateRowAttributes(srcCovgClassGridId, 'N');
            }

            if (!window["useJqxGrid"]) {
                //update select all check box
                var chkSelAllArray = document.getElementsByName("chkCSELECT_ALL");
                for (var i = 0; i < chkSelAllArray.length; i++) {
                    if (chkSelAllArray[i] && chkSelAllArray[i].checked
                            && getTableForGridElement(chkSelAllArray[i]).id == getTableForGridElement(obj).id) {
                        chkSelAllArray[i].checked = false;
                    }
                }
            }
        }
    }

}

function handleOnChange(obj) {
    var objName = obj.name;
    if (obj.name == "riskTypeFilter") {
        filterTagetRisks();
    }

    if (obj.name == 'countyB' && obj.value == 'Y' && getObjectValue('stateB') == 'N') {
        getObject('stateB').value = 'Y';
    }
    else if (obj.name == 'stateB' && obj.value == 'N' && getObjectValue('countyB') == 'Y') {
        handleError(getMessage("pm.maintainRiskCopy.deselectState.error"));
        getObject('stateB').value = 'Y';
    }

    if (!isEmpty(obj.value) && obj.value != 'N') {
        if (isInArray(srcCovgGridFields, obj.name)) {
            updateSelectInd(-1, srcCovgGridId);
        }
        else if (isInArray(srcCompGridFields, obj.name)) {
            updateSelectInd(-1, srcCompGridId);
        }
        else if (isInArray(srcCovgClassGridFields, obj.name)) {
            updateSelectInd(-1, srcCovgClassGridId);
        }
    }
}

function handleOnCopyAllDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var riskListXMLData = getXMLDataForGridName("riskListGrid");
            var data = ajax.responseXML;
            var tempToCovgBaseRecordIds = parseXML(data)[0]["toCovgBaseRecordIds"];
            if (toCovgBaseRecordIds.length > 0){
                toCovgBaseRecordIds = toCovgBaseRecordIds + ',' + tempToCovgBaseRecordIds;
            }
            else{
                toCovgBaseRecordIds = tempToCovgBaseRecordIds;
            }

            if (!handleAjaxMessages(data, null)) {
                updateProcessStatus(riskListXMLData, true);
                errorOccurs = true;
                isProcessing = false;
                return;
            }


            // handle confirmations
            if (isConfirmationMessagesProcessed()) {
                if (getConfirmationResponse("pm.maintainRiskCopy.validation.confirm") == 'N') {
                    //set incomplete status to "Y"
                    updateProcessStatus(riskListXMLData, true);
                }
                else {
                    repostAjaxSubmitWithConfirmationValue(false);
                    //reset confirmation field
                    setInputFormField("pm.maintainRiskCopy.validation.confirm.confirmed", "N");
                }
            }
            // no confirmations
            else {
                var oValueList = parseXML(data);
                if (oValueList.length > 0) {
                    if (oValueList[0]["validateErrorFlag"] == 'Y') {
                        updateProcessStatus(riskListXMLData, true);
                        isValidateErrorExist = true;
                        copyFailureFlag = true;
                    }
                    else if (oValueList[0]["copyFailureFlag"] == 'Y') {
                        updateProcessStatus(riskListXMLData, true);
                        copyFailureFlag = true;
                    }
                    else {
                        updateProcessStatus(riskListXMLData, false);
                    }
                }

                // if it is last selected risk, handle validation errors and copy failure flag
                if (isLastSelectedRisk && copyFailureFlag) {
                    closeProcessingDivPopup();
                    errorOccurs = true;
                    handleError(getMessage("pm.maintainRiskCopy.failure.error"));
                }
                if (isLastSelectedRisk && isValidateErrorExist) {
                    viewValidationError();
                    if (!hasValidationErrorForAllRisk){
                       hasValidationErrorForAllRisk = true;
                    }
                }
                if (isLastSelectedRisk) {
                    if (!copyFailureFlag) {
                        alert(getMessage("pm.maintainRiskCopy.copySucess.msg"));
                    }
                    isLastSelectedRisk = false;
                    isFirstSelectedRisk = false;
                    copyFailureFlag = false;
                    isValidateErrorExist = false;
                }
            }
        }
    }
}

function handleOnDeleteAllDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var riskListXMLData = getXMLDataForGridName("riskListGrid");
            var data = ajax.responseXML;
            //handleAjaxMessages is not called in callback function, so need to call closeProcessingDivPopup here
            closeProcessingDivPopup();
            //not call handleAjaxMessages to show any error message
            //Just set isComplete status according to the returned deleteFailure flag
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                if (oValueList[0]["deleteTargetFailureFlag"] == 'Y') {
                    updateProcessStatus(riskListXMLData, true);
                    deleteTargetFailureFlag = true;
                }
                else if (oValueList[0]["deleteSourceFailureFlag"] == 'Y') {
                    deleteSourceFailureFlag = true;
                }
                else {
                    updateProcessStatus(riskListXMLData, false);
                }
            }

        }
    }
}

function updateProcessStatus(riskListXMLData, status) {
    if (status) {
        riskListXMLData.recordset("CSTATUS").value = "Y";
        riskListXMLData.recordset("CSTATUSLOVLABEL").value = "Yes";
    }
    else {
        riskListXMLData.recordset("CSTATUS").value = "N";
        riskListXMLData.recordset("CSTATUSLOVLABEL").value = "No";
    }
}

function coverageList_btnClick(asBtn) {
    updateAllSelectInd(asBtn, srcCovgGridId);
}

function componentList_btnClick(asBtn) {
    updateAllSelectInd(asBtn, srcCompGridId);
}

function coverageClassList_btnClick(asBtn) {
    updateAllSelectInd(asBtn, srcCovgClassGridId);
}

function riskList_btnClick(asBtn) {
    updateAllSelectInd(asBtn, tgtRiskGridId);
}

function updateRowAttributes(gridId, ynFlag) {
    var XMLData = getXMLDataForGridName(gridId);
    if (!isEmptyRecordset(XMLData.recordset) && !isDeleteCopyAll()) {
        if (gridId == srcCovgGridId) {
            for (var i = 0; i < srcCovgGridFields.length; i++) {
                if(isVisibleField("invisibleCovgGridFields", srcCovgGridFields[i])) {
                   XMLData.recordset('C' + srcCovgGridFields[i].toUpperCase()).value = ynFlag;
                }
            }
        }
        else if (gridId == srcCompGridId) {
            for (var i = 0; i < srcCompGridFields.length; i++) {
                if (srcCompGridFields[i] != 'componentValue') {
                    if(isVisibleField("invisibleCompGridFields", srcCompGridFields[i])) {
                       XMLData.recordset('C' + srcCompGridFields[i].toUpperCase()).value = ynFlag;
                    }
                }
            }
        }
        else if (gridId == srcCovgClassGridId) {
            for (var i = 0; i < srcCovgClassGridFields.length; i++) {
                if (srcCovgClassGridFields[i] != 'exposureUnit') {
                    if(isVisibleField("invisibleCovgClassGridFields", srcCovgClassGridFields[i])) {                
                        XMLData.recordset('C' + srcCovgClassGridFields[i].toUpperCase()).value = ynFlag;
                    }
                }
            }
        }
    }
}

function updateSelectInd(selectValue, gridId) {
    var XMLData = getXMLDataForGridName(gridId);
    if (!isEmptyRecordset(XMLData.recordset)) {
        XMLData.recordset('CSELECT_IND').value = selectValue;
    }
}

function isGridItemSelected(gridId) {
    var xmlData = getXMLDataForGridName(gridId);
    return isRecordSelected(xmlData);
}

function isAttributesSelected(fieldArray) {
    for (var i = 0; i < fieldArray.length; i++) {
        if (hasObject(fieldArray[i]) && getObject(fieldArray[i]).value == 'Y') {
            return true;
        }
    }
    return false;
}

function filterTagetRisks() {
    var riskTypeFilter = getObjectValue('riskTypeFilter');
    var filterStr = "";
    var riskTypeFilterBackup = "," + riskTypeFilter + ",";
    if(riskTypeFilterBackup.indexOf(",ALL,") > -1){
        // select all risks
        filterStr = "";
    }else if (isEmpty(riskTypeFilter)){
        // no risk selected
        filterStr = "1=2";
    }
    else{
        var riskTypeFilterFld = getObject('riskTypeFilter');
        filterStr = addMultiFilterConditions(filterStr, "CRISKTYPECODE", riskTypeFilterFld);
    }
    setTableProperty(eval("riskListGrid"), "selectedTableRowNo", null);
    riskListGrid_filter(filterStr);

    // Hide empty table if there's no row after filtering.
    var detailXmlData = getXMLDataForGridName("riskListGrid");
    if (isEmptyRecordset(detailXmlData.recordset)) {
        hideEmptyTable(getTableForXMLData(detailXmlData));
    }
    else {
        showNonEmptyTable(getTableForXMLData(detailXmlData));
    }

    // Update select all check box in risk grid.
    var chkSelAllArray = document.getElementsByName("chkCSELECT_ALL");
    for (var i = 0; i < chkSelAllArray.length; i++) {
        if ("riskListGrid" == getTableForGridElement(chkSelAllArray[i]).id && chkSelAllArray[i].checked) {
            chkSelAllArray[i].checked = false;
            break;
        }
    }
}

function isDeleteCopyAll() {
    return getObjectValue("operation") == "deleteAll";
}

//-----------------------------------------------------------------------------
// Check whether the field is visible by the fieldId.
// System should find the capital fieldId from invisibleFieldIds since it is capitalization.
//-----------------------------------------------------------------------------
function isVisibleField(invisibleFieldIds, fieldId) {
    var isVisible = false;
    if (hasObject(invisibleFieldIds) && hasObject(fieldId)
            && getObjectValue(invisibleFieldIds).indexOf("," + fieldId.toUpperCase() + ",") == -1
            && !isFieldHidden(fieldId)) {
        isVisible = true;
    }
    return  isVisible;
}

function handleAfterViewValidation(){
    maintianRetroDate();
}

function maintianRetroDate (){
    if (toCovgBaseRecordIds.length != 0){
        var retroDateUrl = getAppPath() + "/riskmgr/maintainRetroDate.do?" + commonGetMenuQueryString()
                + "&process=loadAllNewCopiedCMCoverage"
                + "&toCovgBaseRecordIds=" + toCovgBaseRecordIds;
        var divPopupId = getOpenCtxOfDivPopUp().openDivPopup("", retroDateUrl, true, true, "", "", 600, 500, "", "", "", false);
        toCovgBaseRecordIds = "";
        autoSaveResultType = commonOnSubmitReturnTypes.submitSuccessfullyWithPopup;
    }else{
        autoSaveResultType = commonOnSubmitReturnTypes.submitSuccessfully;
    }
    hasValidationErrorForAllRisk = false;
}