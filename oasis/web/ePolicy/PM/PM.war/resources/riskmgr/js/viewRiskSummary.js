
//-----------------------------------------------------------------------------
// Javascript file for viewRiskSummary.jsp.
//
// (C) 2013 Delphi Technology, inc. (dti)
// Date:   Dec 25, 2013
// Author: xnie
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 12/25/2013       xnie        148083 - Initial version.
// 03/11/2014       fcb         152685 - transaction log id passed to loadWarningMessage
// 03/18/2014       xnie        152969 - Modified maintainRiskDetails() to
//                                       1) Not open risk detail page when the new risk tab page has changes.
//                                       2) Added newRiskB to url.
// 05/05/2014       awu         154377 - Modified handleOnButtonClick to set values to riskBaseRecordId, riskTypeCode,
//                                       riskEffectiveFromDate, riskEffectiveToDate.
// 05/06/2014       fcb         151632 - Added refreshParentB for Risk Relation screen.
// 05/21/2014       jyang       154280 - Modified handleOnSubmit, removed the statements which set the risk type of the
//                                       new add risk to the current chosen risk.
// 06/26/2014       awu         155102 - Changed the riskListGrid_selectRow to riskListGrid_beforeFireAjaxOnSelectRow.
// 06/29/2014       jyang       149970 - Modified handleOnButtonClick, pass risk name to cancellationDetail page when
//                                       cancel risk.
// 08/25/2014       awu         152034 - 1. Modified riskListGrid_beforeFireAjaxOnSelectRow to check whether the risk is switched.
//                                       2. Modified getMenuQueryString to append the coverage/coverage class IDs to URL.
//                                       3). Modified handleOnSubmit to append risk/coverage/coverage class IDs to URL for RATE.
// 10/14/2014       kxiang      158157 - Modified handleExitWorkflow to change viewRiskRiskSummary.do to
//                                       viewRiskSummary.do.
// 10/22/2014       kxiang      158259 - 1. Removed reduplicative function: handleOnSelectLocation().
//                                       2. Set value to  "locationCount" in handleOnSelectLocation().
//                                       3. Modified handleOnGetInitialValuesForAddRisk(), added logical before
//                                          calling maintainRiskDetails().
// 10/27/2014       kxiang      158657 - Removed codes about Location2, as it's obsolete.
// 12/17/2014       wdang       159454 - Removed the logic "only check other risks" in preAddOccupantValidations().
// 12/30/2014       jyang       157750 - Modified maintainRiskDetails() and openDisciplineDeclineListPage() to encode
//                                       the entityName and riskTypeCode before append it to URL.
// 03/20/2015       wdang       161448 - 1) Shifted thisopenEntityMiniPopupWin and its related functions to common.js.
//                                       2) Added getLocationPropertyId() to support entity mini popup window for location risk.
// 08/10/2015       wdang       157211 - Added a button url for INSURED_TRACKING.
// 08/15/2016       eyin        177410 - Added validateTempCovgExist(), handleOnValidateTempCovgExist(),
//                                       performAutoDeleteTempCovgs() and handleOnPerformAutoDeleteTempCovgs(), to
//                                       support to delete temp coverages under the risk after issue state was changed.
// 03/10/2017       wrong       180675 - Added logic to handle case in new UI tab style.
// 07/18/2017       eyin        186988 - Modified openSelectLocation() to store the parentWindow flag of div popup.
// 07/20/2017       wrong       168374 - 1) Modified viewRiskCopyAllPage() to add new parameter isFundState in url.
//                                       2) Modified viewRiskDeleteAllPage() to add new parameter isFundState in url.
//                                       3) Modified handleOnGetInitialValuesForAddRisk() to add logic to load
//                                          isFundState field value.
// 07/26/2017       lzhang      182246 - Clean up unsaved message for page changes
// 09/21/2017       eyin        169483 - Added Exposure button.
// 10/31/2017       eyin        169483 - Added to support 'Multi-Exposure' in currentTabIsHideForCurrentRow().
// 11/08/2017       eyin        169483 - Don't change operation value when reset Multi-Exposure flag after load sub-tab.
// 11/09/2017       tzeng       187689 - 1) Modified handleOnButtonClick(), riskListGrid_selectRow(), handleOnShowPageInFrame()
//                                          to set flag to skip processAutoSaveSubTab() for the case which do not need to process.
//                                       2) Modified handleOnButtonClick() to call clearOperationForTabStyle() to clean
//                                          the operation for the case which do not need.
//                                       3) Modified handleOnSubmit(), preButtonClick(), handleOnSecondaryTabClick(),
//                                          riskListGrid_selectRow() to support processAutoSaveSubTab().
//                                       4) Modified processMainPageAfterAutoSaveSubTab() to remove "delete" case to make
//                                          consistent with preButtonClick().
//                                       5) Modified handleOnShowPageInFrame() to use the new flag rather than "rollback"
//                                          to skip the processAutoSaveSubTab().
// 11/30/2017       wrong       190014 - 1) Modified riskListGrid_beforeFireAjaxOnSelectRow() to invoke
//                                          sendAJAXRequest("getRecordExists") when insert a new row in grid.
// 12/13/2017       wrong       190191 - 1) Modified handleOnSecondaryTabClick to add clearOperationForTabStyle() to
//                                          clear operation value when switching sub tab in no data change case.
//                                       2) Modified riskListGrid_selectRow to add clearOperationForTabStyle() to clear
//                                          operation value when switching grid row in no data change case.
// 12/14/2017       wrong       190085 - 1) Modified handleAfterViewValidation() to support the case that 'View Validation
//                                          Error' popup opened after User clicked Process button on sub-tab COPY ALL,
//                                          and then close the popup, to check if need to refresh risk page.
// 12/29/2017       wrong       190192 - Modified handleOnButtonClick/handleOnShowPageInFrame to support Select Address
//                                       button/tab italic.
// 01/04/2017       lzhang      188231 - 1) Modified riskListGrid_selectRow(): set selected riskId to riskHeaderRiskId
// 07/06/2018       xnie        187070 - Modified viewRiskCopyAllPage() to add input parameter isGr1CompVisible.
// 07/11/2018       wrong       193977 - Modified riskListGrid_selectRow() to change condition of setting value to
//                                       riskHeaderRiskId.
// 08/31/2018       ryzhao      188891 - Modified handleOnButtonClick() to add logic for new button "Claims Free Query".
//-----------------------------------------------------------------------------
var isOoseRiskValid = true;
var ooseRowData;
var origRiskId = "";
var isSlotOccupant = false;
var oRowData;
var origSlotRiskRowId = "";
var italicsFieldId = "NONE";
var selectedRiskId = "";
var isSameRiskB = true;
//Operation in Array will not change the operation to switchGridRow in
// riskListGrid_selectRow function, and proceed special handling in handleOnShowPageInFrame().
var resetOperationArray = ["ADD", "DELETE", "CHANGE"];
//Operation in this array will call commonAddRow function
// and change the operation to "ADD".
var changeToAddOperationArray = ["ADD_RISK", "OCCUPANT", "CHANGE"];

function handleOnLoad() {
    setInputFormField("needToHandleExitWorkFlow", "Y");
    if (isTabStyle()) {
        commonOnUIProcess();
    }
    invokeWorkflow();
}

function selectRowInGridOnPageLoad() {
    $.when(dti.oasis.grid.getLoadingPromise("riskListGrid")).then(function(){
        // select row by previousely selected risk ID if there was no error
        var isRiskGridEmpty = isEmptyRecordset(getXMLDataForGridName("riskListGrid").recordset);
        if ((!hasErrorMessages || (hasErrorMessages && isMainPageRefreshed())) && !isRiskGridEmpty && currentRiskId) {
            selectRowById("riskListGrid", currentRiskId);
        } else {
            if (isTabStyle()) {
                if(getPreviousRow() == "") {
                    selectFirstRowInGrid("riskListGrid");
                    if(isRiskGridEmpty) {
                        selectFirstTab();
                    }
                }else {
                    rollback = true;
                    selectRowById("riskListGrid", getPreviousRow());
                }
            }
            else {
                selectFirstRowInGrid("riskListGrid");
            }
            if (isRiskGridEmpty) {
                selectFirstRowInGrid("riskListGrid");
            }
        }
    });
}

function riskListGrid_beforeFireAjaxOnSelectRow(id) {
    // Get additional info
    var selectedDataGrid = getXMLDataForGridName("riskListGrid");
    var isRiskExpirationDateEditable = selectedDataGrid.recordset("CISRISKEFFECTIVETODATEEDITABLE").value;
    var riskId = selectedDataGrid.recordset("ID").value;
    if(selectedRiskId != "" && riskId != selectedRiskId) {
        isSameRiskB = false;
    }
    if (selectedDataGrid.recordset("UPDATE_IND").value != 'I' && (isRiskExpirationDateEditable == "X")) {
        sendAJAXRequest("getRiskAddlInfo");
    }

    sendAJAXRequest("getRecordExists");
    selectedRiskId = riskId;
}

//-----------------------------------------------------------------------------
// Instruct the baseOnRowSelected to exec the processFieldDeps and pageEntitlements after _selectRow.
//-----------------------------------------------------------------------------
function isFieldDepsAndPageEntitlementsAfter_selectRow(gridId) {
    return true;
}

function riskListGrid_setInitialValues() {
    if (isSlotOccupant) {
        //Copy original data to new row
        setRecordsetByObject(riskListGrid1, oRowData);
        //Set entitynName and entityId
        riskListGrid1.recordset("CRISKNAME").value = getObjectValue("occupantEntityName");
        riskListGrid1.recordset("CENTITYID").value = getObjectValue("occupantEntityId");
        sendAJAXRequest("getInitialValuesForSlotOccupant");
        // Reset flag
        isSlotOccupant = false;
        return;
    }

    if (isForOose == "Y") {
        //Copy original data to new row
        setRecordsetByObject(riskListGrid1, ooseRowData);
        sendAJAXRequest("getInitialValuesForOoseRisk");
        // Reset flag
        isForOose = "N";
        return;
    }

    // Get initial value for new added risk
    sendAJAXRequest("getInitialValuesForAddRisk");
}

function maintainRiskDetails(isNewRisk, slotOccupantB) {
    if (typeof(isNewRisk) == 'undefined') {
        isNewRisk = false;
    }
    var path = getAppPath() + "/riskmgr/maintainRiskDetail.do?" + commonGetMenuQueryString();
    if (!isNewRisk) {
        path = path + "&riskDetailId=" + riskListGrid1.recordset("ID").value
                + "&riskTypeCode=" + encodeURIComponent(riskListGrid1.recordset("CRISKTYPECODE").value)
                + "&entityId=" + riskListGrid1.recordset("CENTITYID").value
                + "&slotId=" + riskListGrid1.recordset("CSLOTID").value
                + "&newRiskB=N";
    }
    else {
        path = path + "&newRiskB=Y";
    }
    if (typeof(slotOccupantB) == 'undefined') {
        slotOccupantB = false;
    }
    if (slotOccupantB) {
        path = path + "&slotOccupantB=Y";
    }
    var divPopupId = openDivPopup("", path, true, true, "", "", "1000", "600", "", "", "", false);
}

function handleOnSubmit(action) {
    var proceed = true;
    if (isTabStyle()) {
        operation = action;
        removeMessagesForFrame();
        if (!isReservedTab(getPreviousTab()) && !isPreviewButtonClicked() &&  action != "SAVE"
                && !isEmptyRecordset(riskListGrid1.recordset)) {
            processAutoSaveSubTab(getCurrentTab());
            if(getCurrentTab() == "COPYALL" || getCurrentTab() == "DELETEALL"){
                if(autoSaveResultType == commonOnSubmitReturnTypes.submitSuccessfullyWithPopup){
                    setBtnOperation(operation);
                    return false;
                }
                if (autoSaveResultType == commonOnSubmitReturnTypes.commonValidationFailed) {
                    if(getIFrameWindow().hasValidationErrorForAllRisk || !getIFrameWindow().isNeedToRefreshParentB()){
                        return false;
                    }
                }
            }else if( autoSaveResultType != commonOnSubmitReturnTypes.noDataChange) {
                return false;
            }
        }

    }
    switch (action) {
        case 'SAVE':
            document.forms[0].action = buildMenuQueryString("PM_PT_VIEWRISKSUMMARY", document.forms[0].action);
            document.forms[0].process.value = "saveAllRiskSummary";
            loadSaveOptions("PM_RISK_SUMMARY", "submitForm");
            proceed = false;
            break;
        case 'SAVEWIP':
            document.forms[0].action = buildMenuQueryString("PM_PT_VIEWRISKSUMMARY", document.forms[0].action);
            document.forms[0].process.value = "saveAllRiskSummary";
            handleSaveOptionSelection("WIP");
            break;
        case 'RATE':
            document.forms[0].action = buildMenuQueryString("PM_PT_VIEWRISKSUMMARY", document.forms[0].action);
            document.forms[0].process.value = "saveAllRiskSummary";
            setInputFormField("newSaveOption", "WIP");
            setInputFormField("processRatingB", "Y");
            break;
        case 'ADD_RISK_BATCH':
            document.forms[0].process.value = "addRiskBatch";
            setInputFormField("newEntityIdList", getObject("newEntityId").value);
            clearCacheRowIds();
            clearCacheTabIds();
            break;

        default:
            proceed = false;
    }
    return proceed;
}

function getMenuQueryString(id, url) {
    var tempUrl = "";
    var selectedDataGrid = getXMLDataForGridName(getCurrentlySelectedGridId()) ;

    if (selectedDataGrid != null) {
        if (getCurrentlySelectedGridId() == "riskListGrid") {
            try { // xmldata might not have any rows at all
                var riskId = selectedDataGrid.recordset('ID').value;
                tempUrl = tempUrl + "&riskId=" + riskId;
                if (isSameRiskB) {
                    var coverageId = getUrlParam(window.location, "coverageId");
                    var coverageClassId = getUrlParam(window.location, "coverageClassId");
                    if (coverageId != null) {
                        tempUrl = tempUrl + "&coverageId=" + coverageId;
                    }
                    if (coverageClassId != null) {
                        tempUrl = tempUrl + "&coverageClassId=" + coverageClassId;
                    }
                }
            }
            catch (ex) {
                //
            }
        }
    }
    return tempUrl;
}

function handleOnButtonClick(btn) {
    switch (btn) {
        case 'ADD_RISK':
            // reset temp fields for new risk
            setInputFormField("addCode", "");
            setInputFormField("newRiskTypeCode", "");
            setInputFormField("newEntityId", 0);
            setInputFormField("newSlotId", "");
            setInputFormField("newLocation", "");
            // get risk type
            selectRiskType(policyHeader.policyTypeCode, policyHeader.termEffectiveFromDate,
                    policyHeader.termEffectiveToDate, "new");
            clearOperationForTabStyle();
            break;
        case 'ADD_EXISTING':
            // reset temp fields for new risk
            setInputFormField("addCode", "");
            setInputFormField("newRiskTypeCode", "");
            setInputFormField("newEntityId", 0);
            setInputFormField("newSlotId", "");
            setInputFormField("newLocation", "");
            // get risk type
            selectRiskType(policyHeader.policyTypeCode, policyHeader.termEffectiveFromDate,
                    policyHeader.termEffectiveToDate, "existing");
            clearOperationForTabStyle();
            break;
        case 'EDIT':
            maintainRiskDetails(false);
            clearOperationForTabStyle();
            break;
        case 'OCCUPANT':
            if (preAddOccupantValidations()) {
                addOccupant();
            }
            clearOperationForTabStyle();
            break;

        case "CANCEL":
            var sRiskBaseRecordId = riskListGrid1.recordset("CRISKBASERECORDID").value;
            var sRiskId = riskListGrid1.recordset("ID").value;
            var sSlotId = riskListGrid1.recordset("CSLOTID").value;
            var sIsSlotOccupant = riskListGrid1.recordset("CISSLOTOCCUPANT").value;
            var effFromDate = riskListGrid1.recordset("CRISKEFFECTIVEFROMDATE").value;
            var effToDate = riskListGrid1.recordset("CRISKEFFECTIVETODATE").value;
            var sRiskName = riskListGrid1.recordset("CRISKNAME").value;
            if (sIsSlotOccupant == 'Y') {
                performCancellation("SLOT", sRiskId, effFromDate, effToDate, "", "", "", sRiskName);
            }
            else {
                //issue 113558: Pass riskTypeCode to performCancellation for new carrier is available checking.
                var sRiskTypeCode = riskListGrid1.recordset("CRISKTYPECODE").value;
                var isIbnrRisk = false;
                var rollingIbnrB = riskListGrid1.recordset("CROLLINGIBNRB").value;
                if (!isEmpty(rollingIbnrB) && rollingIbnrB == 'Y') {
                    isIbnrRisk = true;
                }
                var addCode = riskListGrid1.recordset("CADDCODE").value;
                if (!isEmpty(addCode) && addCode == "SLOT") {
                    sRiskName = sSlotId;
                }
                performCancellation("RISK", sRiskBaseRecordId, effFromDate, effToDate, "", sRiskTypeCode, isIbnrRisk, sRiskName);
            }

            break;

        case "CHANGE":
            // First check if there is modified record already
            // Reset oose risk valid indicator
            isOoseRiskValid = true;
            if (preOoseChangeValidation("risk", "riskListGrid", "CRISKBASERECORDID")) {
                // Check Change option first by ajax call
                sendAJAXRequest("validateForOoseRisk");
                if (isOoseRiskValid) {
                    addOoseRisk();
                }
            }
            break;

        case "REINSTATE":
            var policyStatus = getObjectValue("policyStatus");
            if (policyStatus == "CANCEL") {
                // Perform policy level reinstate
                var effFromDate = policyHeader.termEffectiveFromDate;
                var effToDate = policyHeader.termEffectiveToDate;
                var termBaseRecordId = getObjectValue("termBaseRecordId");
                performReinstate("POLICY", termBaseRecordId, policyHeader.policyId,
                        effFromDate, effToDate, policyStatus, "");
            }
            else {
                // Perform risk level reinstate
                var sRiskBaseRecordId = riskListGrid1.recordset("CRISKBASERECORDID").value;
                var sRiskId = riskListGrid1.recordset("id").value;
                var effFromDate = riskListGrid1.recordset("CRISKEFFECTIVEFROMDATE").value;
                var effToDate = riskListGrid1.recordset("CRISKEFFECTIVETODATE").value;
                var riskStatus = riskListGrid1.recordset("CRISKSTATUS");
                var cancelTransId = riskListGrid1.recordset("CTRANSACTIONLOGID");
                if (valReinstateIbnrRisk(policyHeader.policyId, riskListGrid1.recordset("CENTITYID").value, cancelTransId.value) == "INVALID") {
                    alert(getMessage("pm.maintainRisk.isReinstateIbnrRiskValid.info"));
                }
                performReinstate("RISK", sRiskBaseRecordId, sRiskId, effFromDate, effToDate, riskStatus, cancelTransId);
            }
            break;

        case "COI":
            var url = getAppPath() + "/riskmgr/coimgr/maintainCoi.do?"
                    + commonGetMenuQueryString() + "&process=loadAllCoiHolder"
                    + "&riskId=" + riskListGrid1.recordset("ID").value;
            var idName = 'R_menuitem_PM_RISK_SUM_COI_PUP';
            var mi = getObject(idName);
            if (mi) {
                mi.children[0].style.backgroundImage = '';
            }
            if (policyHeader.policyCycleCode == 'POLICY') {
                italicsFieldId = "COIHOLDERB";
            }
            else {
                italicsFieldId = "COIHOLDERQUOTEB";
            }
            var divPopupId = openDivPopup("", url, true, true, "", "", "800", "", "", "", "", false);
            break;
        case "AFFILIATION":
            var riskEffectiveToDate = riskListGrid1.recordset("CRISKEFFECTIVETODATE").value;
            var riskBaseEffectiveToDate = riskListGrid1.recordset("CRISKBASEEFFECTIVETODATE").value;
            var riskStatus = riskListGrid1.recordset("CRISKSTATUS").value;
            var riskBaseStatus = riskListGrid1.recordset("CRISKBASESTATUS").value;
            var riskBaseRecordId = riskListGrid1.recordset("CRISKBASERECORDID").value;
            var XMLData = riskListGrid1;
            var selectedRowId = 0;
            var count = 0;
            var futureCancelRiskExistsB = 'N';
            var affiliationRiskExpDate = "";
            var riskExpDate = "";

            if (policyHeader.wipB) {
                riskExpDate = riskEffectiveToDate;
            }
            else {
                riskExpDate = riskBaseEffectiveToDate;
            }

            if (!isEmptyRecordset(XMLData.recordset)) {
                // Get the current selected row.
                selectedRowId = XMLData.recordset("ID").value;
                first(XMLData);
                while (!XMLData.recordset.eof) {
                    if(XMLData.recordset("CRISKBASERECORDID").value == riskBaseRecordId &&
                            XMLData.recordset("CRECORDMODECODE").value == "TEMP" &&
                            XMLData.recordset("CRISKSTATUS").value == 'CANCEL') {
                        count++;
                        break;
                    }
                    next(XMLData);
                }
                first(XMLData);
                // Select the current selected row.
                selectRowById("riskListGrid", selectedRowId);

                if (count != 0) {
                    futureCancelRiskExistsB = 'Y';
                }

                if (futureCancelRiskExistsB == 'Y') {
                    if (getRealDate(riskEffectiveToDate) < getRealDate(policyHeader.lastTransactionInfo.transEffectiveFromDate)) {
                        affiliationRiskExpDate = riskEffectiveToDate;
                    }
                    else {
                        affiliationRiskExpDate = policyHeader.lastTransactionInfo.transEffectiveFromDate;
                    }
                }
                else {
                    if (getRealDate(riskExpDate) < getRealDate(riskBaseEffectiveToDate) && riskStatus != 'CANCEL' && riskBaseStatus != 'CANCEL') {
                        affiliationRiskExpDate = riskBaseEffectiveToDate;
                    }
                    else {
                        if (policyHeader.lastTransactionInfo.transactionCode != "OOSENDORSE") {
                            affiliationRiskExpDate = riskExpDate;
                        }
                        else {
                            affiliationRiskExpDate = getObjectValue("policyExpirationDate");
                        }
                    }
                }
            }

            var url = getAppPath() + "/riskmgr/affiliationmgr/maintainAffiliation.do?"
                    + commonGetMenuQueryString() + "&process=loadAllAffiliation"
                    + "&riskId=" + riskListGrid1.recordset("ID").value
                    + "&riskEntityId=" + riskListGrid1.recordset("CENTITYID").value
                    + "&riskTypeCode=" + escape(riskListGrid1.recordset("CRISKTYPECODE").value)
                    + "&affiliationRiskExpDate=" + affiliationRiskExpDate;

            if (policyHeader.policyCycleCode == 'POLICY') {
                italicsFieldId = "AFFILIATIONB";
            }
            else {
                italicsFieldId = "AFFILIATIONQUOTEB";
            }
            var divPopupId = openDivPopup("", url, true, true, "", "", "800", "", "", "", "", false);
            break;
        case "EMPPHYS":
            var riskBaseRecordId = riskListGrid1.recordset("CRISKBASERECORDID").value;
            var riskTypeCode = riskListGrid1.recordset("CRISKTYPECODE").value;
            var fteEquivalent = riskListGrid1.recordset("CFTEEQUIVALENT").value;
            var fteFullTimeHrs = riskListGrid1.recordset("CFTEFULLTIMEHRS").value;
            var ftePartTimeHrs = riskListGrid1.recordset("CFTEPARTTIMEHRS").value;
            var ftePerDiemHrs = riskListGrid1.recordset("CFTEPERDIEMHRS").value;
            var riskStatus = riskListGrid1.recordset("CRISKSTATUS").value;
            var riskName = escape(riskListGrid1.recordset("CRISKNAME").value);
            var url = getAppPath() + "/riskmgr/empphysmgr/maintainEmployedPhysician.do?"
                    + commonGetMenuQueryString() + "&process=loadAllEmployedPhysician"
                    + "&riskName=" + riskName
                    + "&riskBaseRecordId=" + riskBaseRecordId
                    + "&riskTypeCode=" + escape(riskTypeCode)
                    + "&fteEquivalent=" + fteEquivalent
                    + "&fteFullTimeHrs=" + fteFullTimeHrs
                    + "&ftePartTimeHrs=" + ftePartTimeHrs
                    + "&ftePerDiemHrs=" + ftePerDiemHrs
                    + "&riskStatus=" + riskStatus
                    + "&riskId=" + riskListGrid1.recordset("ID").value;

            if (policyHeader.policyCycleCode == 'POLICY') {
                italicsFieldId = "EMPPHYSB";
            }
            else {
                italicsFieldId = "EMPPHYQUOTEB";
            }
            var divPopupId = openDivPopup("", url, true, true, "", "", 800, 720, "", "", "", false);
            break;
        case "COPYALL":
            showProcessingDivPopup();
            var riskId = riskListGrid1.recordset("id").value;
            var url = getAppPath() + "/riskmgr/performRiskCopyAll.do?"
                    + commonGetMenuQueryString() + "&process=validateRiskCopySource"
                    + "&riskId=" + riskId
                    + "&riskTypeCode=" + escape(riskListGrid1.recordset("CRISKTYPECODE").value)
                    + "&policyTypeCode=" + policyHeader.policyTypeCode;

            new AJAXRequest("get", url, "", handleOnValidateRiskCopySource, true);
            break;
        case "DELETEALL":
            viewRiskDeleteAllPage();
            break;
        case "SEARCH":
            var url = getAppPath() + "/riskmgr/findRisk.do?"
                    + commonGetMenuQueryString() + "&process=displayPage";
            var divPopupId = openDivPopup("", url, true, true, "", "", "800", "", "", "", "", false);
            clearOperationForTabStyle();
            break;
        case "RISK_RELATION":
            if (isEmptyRecordset(riskListGrid1.recordset)) {
                alert(getMessage("pm.maintainRiskRelation.noRisk.error"));
            }
            else {
                var riskBaseRecordId = riskListGrid1.recordset("CRISKBASERECORDID").value;
                var riskTypeCode = escape(riskListGrid1.recordset("CRISKTYPECODE").value);
                var riskEffectiveFromDate = riskListGrid1.recordset("CRISKEFFECTIVEFROMDATE").value;
                var riskEffectiveToDate = riskListGrid1.recordset("CRISKEFFECTIVETODATE").value;
                setInputFormField("riskBaseRecordId", riskBaseRecordId);
                setInputFormField("riskTypeCode", riskTypeCode);
                setInputFormField("riskEffectiveFromDate", riskEffectiveFromDate);
                setInputFormField("riskEffectiveToDate", riskEffectiveToDate);
                var url = getAppPath() + "/riskmgr/maintainRiskRelation.do?"
                        + commonGetMenuQueryString() + "&process=loadAllRiskRelation"
                        + "&riskId=" + riskListGrid1.recordset("ID").value
                        + "&riskBaseRecordId=" + riskBaseRecordId
                        + "&currentRiskTypeCode=" + riskTypeCode
                        + "&endorsementQuoteId=" + policyHeader.lastTransactionInfo.endorsementQuoteId
                        + "&riskEffectiveFromDate=" + riskEffectiveFromDate
                        + "&origRiskEffectiveFromDate=" + riskListGrid1.recordset("CRISKEFFECTIVEFROMDATE").value
                        + "&riskEffectiveToDate=" + riskEffectiveToDate
                        + "&riskCountyCode=" + riskListGrid1.recordset("CRISKCOUNTY").value
                        + "&reverse=N"
                        + "&refreshParentB=N"
                        + "&origPracticeStateCode=" + riskListGrid1.recordset("CPRACTICESTATECODE").value;

                if (policyHeader.policyCycleCode == 'POLICY') {
                    italicsFieldId = "RISKRELATIONB";
                }
                else {
                    italicsFieldId = "RISKRELATIONQUOTEB";
                }
                var divPopupId = openDivPopup("", url, true, true, "", "", "900", "650", "", "", "", false);
            }
            break;
        case "HISTORY":
            var url = getAppPath() + "/riskmgr/viewInsuredHistory.do?"
                    + commonGetMenuQueryString() + "&rEntityId=" + riskListGrid1.recordset("CENTITYID").value +
                    "&process=loadAllInsuredHistory";
            var divPopupId = openDivPopup("", url, true, true, "", "", "600", "450", "", "", "", false);
            break;
        case "INSUREDINFO":
            var url = getAppPath() + "/riskmgr/viewInsuredInfo.do?"
                    + commonGetMenuQueryString() + "&rEntityId=" + riskListGrid1.recordset("CENTITYID").value +
                    "&process=loadAllInsuredInfo&riskId=" + riskListGrid1.recordset("ID").value;
            openDivPopup("", url, true, true, "", "", "600", "450", "", "", "", false);
            break;
        case "SUSPENSION":
            var riskBaseRecordId = riskListGrid1.recordset("CRISKBASERECORDID").value;
            var policyScreenMode = getObjectValue("policyScreenMode");
            var readOnly = "N";
            if (policyScreenMode == "VIEW_POLICY" || policyScreenMode == "VIEW_ENDQUOTE" ||
                    policyScreenMode == "CANCELWIP" || policyScreenMode == "REINSTATEWIP") {
                readOnly = "Y";
            }
            var rootPath = getAppPath();
            var claimPath = rootPath.replace("ePolicy/PM", "eClaim/CM");
            var url = claimPath +
                    "/cmPolicySuspension.do?sourceTableName=RISK&startDate=01/01/1900&endDate=01/01/3000" +
                    "&sourceRecordId=" + riskBaseRecordId + "&readonly=" + readOnly;

            italicsFieldId="SUSPB";
            window.open(url, '', 'width=800,height=600,innerHeight=400,innerWidth=800,scrollbars');
            break;
        case "EXPCOMPWKST":
            var policyId = riskListGrid1.recordset("CPOLICYID").value;
            var riskBaseRecordId = riskListGrid1.recordset("CRISKBASERECORDID").value;
            var termBaseId = getObject("policyTermHistoryId").value;
            var paramsObj = new Object();
            paramsObj.reportCode = "PM_EXP_COMP_WORKSHEET";
            paramsObj.sourceRecordId = policyId;
            paramsObj.riskBaseRecordId = riskBaseRecordId;
            paramsObj.termBaseId = termBaseId;

            viewPolicyReport(paramsObj);
            break;
        case "SCHEDULE":
            var url = getAppPath() + "/schedulemgr/maintainSchedule.do?" + commonGetMenuQueryString() +
                    "&process=loadAllSchedule&riskId=" + riskListGrid1.recordset("ID").value;

            if (policyHeader.policyCycleCode == 'POLICY') {
                italicsFieldId = "RISKSCHEDB";
            }
            else {
                italicsFieldId = "RISKSCHEDQUOTEB";
            }
            var divPopupId = openDivPopup("", url, true, true, "", "", "", "", "", "", "", false);

            break;
        case 'SURCHARGE_POINTS':
            var url = getAppPath() + "/riskmgr/maintainRiskSurchargePoints.do?" + commonGetMenuQueryString()
                    + "&riskId=" + riskListGrid1.recordset("ID").value;
            var processingDivId = openDivPopup("", url, true, true, "", "", 900, 500, "", "", "", false);
            break;
        case 'COPY_NEW':
            var url = getAppPath() + "/policymgr/createPolicy.do?process=getInitialValuesForCopyNewPolicy"
                    + "&riskId=" + riskListGrid1.recordset("ID").value
                    + "&num1=" + riskListGrid1.recordset("CPOLICYNUM1").value
                    + "&num2=" + riskListGrid1.recordset("CPOLICYNUM2").value
                    + "&num3=" + riskListGrid1.recordset("CPOLICYNUM3").value
                    + "&char1=" + riskListGrid1.recordset("CPOLICYCHAR1").value
                    + "&char2=" + riskListGrid1.recordset("CPOLICYCHAR2").value
                    + "&char3=" + riskListGrid1.recordset("CPOLICYCHAR3").value
                    + "&date1=" + riskListGrid1.recordset("CPOLICYDATE1").value
                    + "&date2=" + riskListGrid1.recordset("CPOLICYDATE2").value
                    + "&date3=" + riskListGrid1.recordset("CPOLICYDATE3").value
                    + "&policyLayerCode=" + riskListGrid1.recordset("CPOLICYLAYERCODE").value
                    + "&policyFormCode=" + riskListGrid1.recordset("CPOLICYPOLICYFORMCODE").value
                    + "&policyNo=" + policyHeader.policyNo + "&isFromCopyNew=Y";
            var processingDivId = openDivPopup("", url, true, true, null, null, "", "", "", "", "createPolicy", false);
            break;
        case "PROCESS_ERP":
            var riskId = riskListGrid1.recordset("CRISKBASERECORDID").value;
            var policyViewMode = getObjectValue("policyViewMode");
            var transLogId = "";
            var transEffDate = "";
            // transLogId: Primary key value of the current transaction if WIP transaction exists and policy is in WIP mode
            if (policyHeader.wipB && policyViewMode == "WIP") {
                transLogId = policyHeader.lastTransactionInfo.transactionLogId;
                transEffDate = policyHeader.lastTransactionInfo.transEffectiveFromDate;
            }
            var processErpUrl = getAppPath() + "/componentmgr/experiencemgr/processErp.do?"
                    + commonGetMenuQueryString()
                    + "&headerHidden=Y"
                    + "&policyId=" + policyHeader.policyId
                    + "&transLogId=" + transLogId
                    + "&termId=" + getObjectValue("termBaseRecordId")
                    + "&termEff=" + policyHeader.termEffectiveFromDate
                    + "&termExp=" + policyHeader.termEffectiveToDate
                    + "&transEff=" + transEffDate
                    + "&riskId=" + riskId;
            var divPopupId = openDivPopup("", processErpUrl, true, true, "", "", 850, 700, 842, 672, "", false);
            break;
        case "NATIONAL_PROGRAM":
            if (isEmptyRecordset(riskListGrid1.recordset)) {
                alert(getMessage("pm.maintainNationalProgram.noRisk.error"));
            }
            else {
                var url = getAppPath() + "/riskmgr/nationalprogrammgr/maintainNationalProgram.do?"
                        + commonGetMenuQueryString() + "&process=loadAllNationalProgram"
                        + "&riskBaseRecordId=" + riskListGrid1.recordset("CRISKBASERECORDID").value
                        + "&riskId=" + riskListGrid1.recordset("ID").value;
                var divPopupId = openDivPopup("", url, true, true, "", "", "900", "650", "", "", "", false);
            }
            break;
        case "SELECT_ADDRESS":
            var selAddrUrl = getAppPath() + "/policymgr/selectAddress.do?" + commonGetMenuQueryString() + "&type=RISK"
                    + "&entityId=" + riskListGrid1.recordset("CENTITYID").value
                    + "&riskBaseRecordId=" + riskListGrid1.recordset("CRISKBASERECORDID").value
                    + "&riskStatus=" + riskListGrid1.recordset("CRISKSTATUS").value;
            if (policyHeader.policyCycleCode == 'POLICY') {
                italicsFieldId = "SELECTADDRESSB";
            }
            else {
                italicsFieldId = "SELECTADDRESSGQUOTEB";
            }
            var divPopupId = openDivPopup("", selAddrUrl, true, true, "", "", 600, 500, "", "", "", false);
            break;
        case "INSURED_TRACKING":
            var url = getAppPath() + "/riskmgr/insuredmgr/maintainInsuredTracking.do?" + commonGetMenuQueryString()
                    + "&process=loadAllInsuredTracking"
                    + "&riskId=" + riskListGrid1.recordset("ID").value
                    + "&riskBaseRecordId=" + riskListGrid1.recordset("CRISKBASERECORDID").value
                    + "&searchTermHistoryId=" + policyHeader.termBaseRecordId;
            if (policyHeader.policyCycleCode == 'POLICY') {
                italicsFieldId = "INSTRKB";
            }
            else {
                italicsFieldId = "INSTRKQUOTEB";
            }
            var divPopupId = openDivPopup("", url, true, true, "", "", 850, 750, "", "", "", false);
            break;
        case 'PREVIEW':
            onPreviewButtonClick();
            break;
        case 'DELETE':
            commonDeleteRow("riskListGrid");
            if (isEmptyRecordset(riskListGrid1.recordset)) {
                selectTabById(getFirstTab());
            }
            break;
        case 'ADD':
            if(isTabStyle()){
                setSkipAutoSave(true);
            }
            commonAddRow("riskListGrid");
            break;
        case "EXPOSURE":
            var riskEffectiveToDate = riskListGrid1.recordset("CRISKEFFECTIVETODATE").value;
            var riskBaseEffectiveToDate = riskListGrid1.recordset("CRISKBASEEFFECTIVETODATE").value;
            var riskStatus = riskListGrid1.recordset("CRISKSTATUS").value;
            var riskBaseStatus = riskListGrid1.recordset("CRISKBASESTATUS").value;
            var riskBaseRecordId = riskListGrid1.recordset("CRISKBASERECORDID").value;
            var XMLData = riskListGrid1;
            var selectedRowId = 0;
            var count = 0;
            var futureCancelRiskExistsB = 'N';
            var exposureRiskExpDate = "";
            var riskExpDate = "";

            if (policyHeader.wipB) {
                riskExpDate = riskEffectiveToDate;
            }
            else {
                riskExpDate = riskBaseEffectiveToDate;
            }

            if (!isEmptyRecordset(XMLData.recordset)) {
                // Get the current selected row.
                selectedRowId = XMLData.recordset("ID").value;
                first(XMLData);
                while (!XMLData.recordset.eof) {
                    if(XMLData.recordset("CRISKBASERECORDID").value == riskBaseRecordId &&
                            XMLData.recordset("CRECORDMODECODE").value == "TEMP" &&
                            XMLData.recordset("CRISKSTATUS").value == 'CANCEL') {
                        count++;
                        break;
                    }
                    next(XMLData);
                }
                first(XMLData);
                // Select the current selected row.
                selectRowById("riskListGrid", selectedRowId);

                if (count != 0) {
                    futureCancelRiskExistsB = 'Y';
                }

                if (futureCancelRiskExistsB == 'Y') {
                    if (getRealDate(riskEffectiveToDate) < getRealDate(policyHeader.lastTransactionInfo.transEffectiveFromDate)) {
                        exposureRiskExpDate = riskEffectiveToDate;
                    }
                    else {
                        exposureRiskExpDate = policyHeader.lastTransactionInfo.transEffectiveFromDate;
                    }
                }
                else {
                    if (getRealDate(riskExpDate) < getRealDate(riskBaseEffectiveToDate) && riskStatus != 'CANCEL' && riskBaseStatus != 'CANCEL') {
                        exposureRiskExpDate = riskBaseEffectiveToDate;
                    }
                    else {
                        if (policyHeader.lastTransactionInfo.transactionCode != "OOSENDORSE") {
                            exposureRiskExpDate = riskExpDate;
                        }
                        else {
                            exposureRiskExpDate = getObjectValue("policyExpirationDate");
                        }
                    }
                }
            }

            if(getRealDate(exposureRiskExpDate) > getRealDate(policyHeader.termEffectiveToDate)){
                exposureRiskExpDate = policyHeader.termEffectiveToDate;
            }

            var url = getAppPath() + "/riskmgr/addtlexposuremgr/maintainRiskAddtlExposure.do?" + commonGetMenuQueryString()
                    + "&process=loadAllRiskAddtlExposure"
                    + "&riskId=" + riskListGrid1.recordset("ID").value
                    + "&riskEffectiveFromDate=" + riskListGrid1.recordset("CRISKEFFECTIVEFROMDATE").value
                    + "&riskEffectiveToDate=" + riskListGrid1.recordset("CRISKEFFECTIVETODATE").value
                    + "&riskBaseRecordId=" + riskListGrid1.recordset("CRISKBASERECORDID").value
                    + "&exposureRiskExpDate=" + exposureRiskExpDate
                    + "&searchTermHistoryId=" + policyHeader.termBaseRecordId;
            if (policyHeader.policyCycleCode == 'POLICY') {
                italicsFieldId = "RISKADDTLEXPOSUREB";
            }
            else {
                italicsFieldId = "RISKADDTLEXPOSUREQUOTEB";
            }
            var divPopupId = openDivPopup("", url, true, true, "", "", 850, 850, "", "", "", false);
            break;
        case "EXP_HISTORY":
            if (!isEmptyRecordset(riskListGrid1.recordset)) {
                var url = getAppPath() + "/riskmgr/viewExpDiscHistory.do?"
                        + commonGetMenuQueryString() + "&process=loadExpHistoryInfo"
                        + "&riskBaseId=" + riskListGrid1.recordset("CRISKBASERECORDID").value;
                var divPopupId = openDivPopup("", url, true, true, "", "", "900", "650", "", "", "", false);
            }
            break;
    }
}

function selectRiskType(policyTypeCode, effectiveFromDate, effectiveToDate, openWhichWindow) {
    setOpenWindow(openWhichWindow);
    var path = getAppPath() +
            "/riskmgr/selectRiskType.do?"
            + commonGetMenuQueryString() +
            "&effectiveFromDate=" + effectiveFromDate +
            "&effectiveToDate=" + effectiveToDate;
    //if redirect to other pages
    if (openWhichWindow) {
        path += "&openWhichWindow=" + openWhichWindow;
    }
    var divPopupId = openDivPopup("", path, true, true, null, null, "", "", "", "", "selectRiskType", false);
}

function handleOnSelectRiskType(action, riskTypeCode) {
    if (action == "Select") {
        setInputFormField("newRiskTypeCode", riskTypeCode);
        sendAJAXRequest("getAddCodeForRisk");
    }
}
function openExistingRisk() {
    var riskBaseRecordId;
    if (isEmptyRecordset(riskListGrid1.recordset)) {
        riskBaseRecordId = 0;
    }
    else {
        riskBaseRecordId = riskListGrid1.recordset("CRISKBASERECORDID").value;
    }
    var path = getAppPath() + "/riskmgr/selectExistingRisk.do?"
            + commonGetMenuQueryString() + "&riskBaseRecordId=" + riskBaseRecordId;
    var divPopupId = openDivPopup("", path, true, true, null, null, "", "", "", "", "selectExistingRisk", false);
}

function handleOnValidateRiskCopySource(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }

            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                var validateResult = oValueList[0]["validateResult"];
                if (validateResult == "INVALID") {
                    viewValidationError();
                    if (isTabStyle()) {
                        if (getPreviousRow() != getCurrentRow()) {
                            rollback = true;
                            selectRowById("riskListGrid", getPreviousRow());
                        }
                        if (getPreviousTab() != getCurrentTab()) {
                            setCacheTabIds(getPreviousTab() + "," + getPreviousTab());
                        }
                    }
                }
                else {
                    viewRiskCopyAllPage();
                }
            }
        }
    }
}

function viewRiskCopyAllPage() {
    var riskBaseRecordId = riskListGrid1.recordset("CRISKBASERECORDID").value;
    var riskId = riskListGrid1.recordset("id").value;
    var path = getAppPath() + "/riskmgr/performRiskCopyAll.do?"
            + commonGetMenuQueryString() + "&riskBaseRecordId=" + riskBaseRecordId
            + "&riskId=" + riskId
            + "&riskSocietyId=" + riskListGrid1.recordset("CRISKSOCIETYID").value
            + "&riskEffectiveToDate=" + riskListGrid1.recordset("CRISKEFFECTIVETODATE").value
            + "&riskEffectiveFromeDate=" + riskListGrid1.recordset("CRISKEFFECTIVEFROMDATE").value
            + "&operation=copyAll"
            + "&isFundState=" + riskListGrid1.recordset("CISFUNDSTATE").value
            + "&isGr1CompVisible=" + riskListGrid1.recordset("CISGR1COMPVISIBLE").value;
    if (isTabStyle()) {
        showPageInFrame(path, subFrameId, "COPYALL");
    }
    else {
        var divPopupId = openDivPopup("", path, true, true, null, null, 930, 800, 900, 1400, "loadAllCopyRisk", false);
    }
}

function viewRiskDeleteAllPage() {
    var riskBaseRecordId = riskListGrid1.recordset("CRISKBASERECORDID").value;
    var riskId = riskListGrid1.recordset("id").value;
    var path = getAppPath() + "/riskmgr/performRiskCopyAll.do?"
            + commonGetMenuQueryString() + "&riskBaseRecordId=" + riskBaseRecordId
            + "&riskId=" + riskId
            + "&riskEffectiveToDate=" + riskListGrid1.recordset("CRISKEFFECTIVETODATE").value
            + "&riskEffectiveFromeDate=" + riskListGrid1.recordset("CRISKEFFECTIVEFROMDATE").value
            + "&operation=deleteAll"
            + "&isFundState=" + riskListGrid1.recordset("CISFUNDSTATE").value;
    if (isTabStyle()) {
        showPageInFrame(path, subFrameId, "DELETEALL");
    }
    else {
        var divPopupId = openDivPopup("", path, true, true, null, null, 930, 800, 900, 1000, "loadAllCopyRisk", false);
    }
}

function viewValidationError() {
    var url = getAppPath() + "/transactionmgr/viewValidationError.do?"
            + commonGetMenuQueryString() + "process=loadAllValidationError";
    var processingDivId = openDivPopup("", url, true, true, "", "", "", "", "", "", "", false);
}

function handleOnGetAddCodeForRisk(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null))
                return;

            // process add code
            var addCode;
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                setFormFieldValuesByObject(oValueList[0]);
                addCode = getObjectValue("addCode");
            }
            switch (addCode) {
                case 'ENTITY':
                    selectRiskEntity("handleOnSelectRiskEntity()");
                    break;

                case 'FTE':
                    handleSlot();
                    break;

                case 'HOSPITAL':
                    lookupEntity("HOSPITAL", policyHeader.termEffectiveFromDate,
                            'newEntityId', null, 'sendAJAXRequest("validateForAddRisk")');
                    break;

                case 'LOCATION':
                    selectLocation();
                    break;

                case 'SLOT':
                    handleSlot();
                    break;
            }
        }
    }
}

function selectRiskEntity(callbackFunctionName) {
    // set new risk entity ID holder
    setInputFormField("newEntityId", 0);

    // open client select page
    //if add existing risk
    if (getObjectValue("openWhichWindow") == "existing") {
        openExistingRisk();
    }
    else {
        openEntitySelectWinFullName("newEntityId", "", callbackFunctionName);
    }

}

function handleOnSelectRiskEntity() {
    if (getObjectValue("newEntityId") != "0") {
        var newEntityIds = getObjectValue("newEntityId").split(',');
        if (newEntityIds.length == 1) {
            setInputFormField("newEntityId", newEntityIds[0]);
            sendAJAXRequest("validateForAddRisk");
        }
        else {
            addRiskBatch(getObjectValue("newEntityId"));
        }
    }
}

function handleSlot() {
    setInputFormField("newSlotId", getSlotId(getObjectValue("newRiskTypeCode")));
    sendAJAXRequest("validateForAddRisk");
}

function selectLocation() {
    // set the entity ID
    var entityId;
    if (getSysParmValue("PM_LOC_TO_POLHOLDER") == 'Y') {
        entityId = policyHeader.policyHolderNameEntityId;
    }
    else {
        if (!isEmptyRecordset(riskListGrid1.recordset) && parseFloat(riskListGrid1.recordset("CENTITYID").value) > 0) {
            entityId = riskListGrid1.recordset("CENTITYID").value;
        }
        else {
            handleError(getMessage("pm.addRisk.noRiskSelected.error"));
            return;
        }
    }

    // select the location(s) for given entity ID
    openSelectLocation(entityId);
}

function openSelectLocation(entityId) {
    var path = getAppPath() + "/riskmgr/selectLocation.do?entityId=" + entityId + "&transEffDate=" + policyHeader.lastTransactionInfo.transEffectiveFromDate;
    var divPopupId = openDivPopup("", path, true, true,
            null, null, "", "", "", "", "selectLocation", false);
    if(isTabStyle()){
        handleOnPutParentWindowOfDivPopup(divPopupId, parentWindowFlagReturnTypes.ParentWindow);
    }
}

function handleOnSelectLocation(action, locations) {
    if (action == "Select") {
        var locationCount = locations.length;
        setInputFormField("locationCount", locationCount);
        for (var i = 0; i < locationCount; i++) {
            setInputFormField("newLocation", locations[i].locationId);
            sendAJAXRequest("validateForAddRisk");
        }
        setInputFormField("locationCount", 0);
    }
}

function handleOnValidateForAddRisk(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null))
                return;

            // do nothing if the risk already exists
            var addCode = getObjectValue("addCode");
            if (addCode != "FTE" && addCode != "SLOT" &&
                    riskExists(addCode, getObjectValue("newRiskTypeCode"),
                            getObjectValue("newEntityId"), getObjectValue("newLocation")))
                return;

            // add a new row
            commonOnButtonClick('ADD');
        }
    }
}
//-----------------------------------------------------------------------------
// For issue 100043, ePM should allow to add the same Risk as Canceled Risk.
//-----------------------------------------------------------------------------
function riskExists(addCode, riskTypeCode, entityId, location) {
    var findDup = false;

    if (!isEmptyRecordset(riskListGrid1.recordset)) {
        var rowIndex = 0;
        var transEffectiveFromDate = policyHeader.lastTransactionInfo.transEffectiveFromDate;

        first(riskListGrid1);
        while (!riskListGrid1.recordset.eof) {
            if (riskListGrid1.recordset("CRISKTYPECODE").value == riskTypeCode &&
                    isDate2OnOrAfterDate1(riskListGrid1.recordset("CRISKEFFECTIVEFROMDATE").value, transEffectiveFromDate) == "Y" &&
                    isDate2OnOrAfterDate1(riskListGrid1.recordset("CRISKEFFECTIVETODATE").value, transEffectiveFromDate) == "N" &&
                    ((addCode == "ENTITY" || addCode == "HOSPITAL") && riskListGrid1.recordset("CENTITYID").value == entityId ||
                            addCode == "LOCATION" && riskListGrid1.recordset("CLOCATION").value == location) &&
                    (riskListGrid1.recordset("CRISKSTATUS").value != 'CANCEL')) {
                findDup = true;
                handleError(getMessage("pm.addRisk.riskExists.error"), "", riskListGrid1.recordset("ID").value);
                break;
            }

            rowIndex ++;
            next(riskListGrid1);
        }
    }

    return findDup;
}

function getSlotId(riskTypeCode) {
    var newSlotId = 0;

    // get the max ID of new slot risk
    if (!isEmptyRecordset(riskListGrid1.recordset)) {
        first(riskListGrid1);
        while (!riskListGrid1.recordset.eof) {
            var transEffectiveFromDate = policyHeader.lastTransactionInfo.transEffectiveFromDate;
            if (riskListGrid1.recordset("CRISKTYPECODE").value == riskTypeCode &&
                    riskListGrid1.recordset("UPDATE_IND").value == "I" &&
                    parseFloat(riskListGrid1.recordset("CSLOTID").value) > newSlotId) {
                newSlotId = parseFloat(riskListGrid1.recordset("CSLOTID").value);
            }
            next(riskListGrid1);
        }

        // set the current slot ID if we found the max
        if (newSlotId > 0)
            newSlotId += 1;
    }

    return newSlotId;
}

function handleOnGetInitialValuesForAddRisk(ajax, slotOccupantB) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            // do nothing if we don't have initial values or we got error
            if (!handleAjaxMessages(data, null))
                return;
            // parse and set initial values
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                var selectedDataGrid = getXMLDataForGridName(getCurrentlySelectedGridId()) ;
                if (selectedDataGrid != null) {
                    setRecordsetByObject(selectedDataGrid, oValueList[0]);
                }
                else {
                    setFormFieldValuesByObject(oValueList[0]);
                }

                if(!(hasObject("locationCount") && getObjectValue("locationCount") > 1)) {
                    maintainRiskDetails(true, slotOccupantB);
                }
            }
        }
    }
}

function handleOnGetRiskAddlInfo(ajax) {
    commonHandleOnGetAddlInfo(ajax);
}

function handleOnValidateForOoseRisk(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null))
                isOoseRiskValid = false;
        }
    }
}

function addOoseRisk() {
    // Save current row's data into object
    origRiskId = riskListGrid1.recordset("ID").value;
    ooseRowData = getObjectFromRecordset(riskListGrid1);

    commonOnButtonClick('ADD');
}

function handleOnGetInitialValuesForOoseRisk(ajax) {
    handleOnGetInitialValuesForAddRisk(ajax, false);
}

function sendAJAXRequest(process) {
    // set url
    var url = "viewRiskSummary.do?process=" + process +
            "&" + commonGetMenuQueryString("PM_VIEW_RISK_SUMMARY", "");
    if (process != "getInitialValuesForOoseRisk"
            && process != "validateForOoseRisk"
            && process != "getInitialValuesForSlotOccupant"
            && process != "getRiskAddlInfo"
            && process != "getRecordExists") {
        url += "&riskTypeCode=" + escape(getObjectValue("newRiskTypeCode"));
    }

    switch (process) {
        case 'getAddCodeForRisk':
            break;

        case 'validateForAddRisk':
            url += "&addCode=" + getObjectValue("addCode") +
                    "&entityId=" + getObjectValue("newEntityId");
            break;

        case 'getInitialValuesForAddRisk':
            var firstRiskB = (riskListGrid1.recordset.RecordCount == 1) ? "Y" : "N";
            url += "&addCode=" + getObjectValue("addCode") +
                    "&entityId=" + getObjectValue("newEntityId") +
                    "&slotId=" + getObjectValue("newSlotId") +
                    "&location=" + getObjectValue("newLocation") +
                    "&firstRiskB=" + firstRiskB +
                    "&riskDetailB=Y";
            break;

        case 'validateForOoseRisk':
            var riskEffFromDate = riskListGrid1.recordset("CRISKEFFECTIVEFROMDATE").value;
            var riskBaseRecordId = riskListGrid1.recordset("CRISKBASERECORDID").value;
            url += "&riskEffectiveFromDate=" + riskEffFromDate + "&riskBaseRecordId=" + riskBaseRecordId;
            break;

        case 'getInitialValuesForOoseRisk':
            var selectedDataGrid = getXMLDataForGridName("riskListGrid");
            url += "&riskId=" + origRiskId +
                    "&riskTypeCode=" + escape(selectedDataGrid.recordset("CRISKTYPECODE").value) +
                    "&recordModeCode=" + selectedDataGrid.recordset("CRECORDMODECODE").value +
                    "&riskEffectiveToDate=" + selectedDataGrid.recordset("CRISKEFFECTIVETODATE").value +
                    "&riskName=" + escape(selectedDataGrid.recordset("CRISKNAME").value) +
                    "&riskDetailB=Y";
            break;

        case 'getInitialValuesForSlotOccupant':
            var sRiskEffToDate = riskListGrid1.recordset("CRISKEFFECTIVETODATE").value;
            url += "&riskId=" + origSlotRiskRowId + "&riskEffectiveToDate=" + sRiskEffToDate +
                    "&entityId=" + riskListGrid1.recordset("CENTITYID").value +
                    "&riskDetailB=Y";
            break;

        case 'getRiskAddlInfo':
            var selectedDataGrid = getXMLDataForGridName("riskListGrid");
            url += "&riskId=" + selectedDataGrid.recordset("ID").value +
                    "&recordModeCode=" + selectedDataGrid.recordset("CRECORDMODECODE").value +
                    "&riskTypeCode=" + escape(selectedDataGrid.recordset("CRISKTYPECODE").value) +
                    "&officialRecordId=" + selectedDataGrid.recordset("COFFICIALRECORDID").value;
            break;

        case 'getRecordExists':
            if (!isEmpty(italicsFieldIdList)) {
                italicsCurrentGridName = "riskListGrid";
                commonOnSetButtonItalics(true);
                return;
            } else {
                return;
            }
            break;
    }

    // initiate call
    var ajaxResponseHandler = "handleOn" + process.substr(0, 1).toUpperCase() + process.substr(1);
    new AJAXRequest("get", url, "", eval(ajaxResponseHandler), false);
}

//-----------------------------------------------------------------------------
// Validatioins
//-----------------------------------------------------------------------------
function handleOnChange(obj) {
    // Modify County Code
    if (obj.name == "riskCounty") {
        var showWarning = getSysParmValue("PM_GIVE_COUNTY_WRNNG");
        var transCode = policyHeader.lastTransactionInfo.transactionCode;
        if (showWarning == "Y" && (transCode != "NEWBUS" && transCode != "CONVRENEW" && transCode != "CONVREISSU"))
            handleError(getMessage("pm.maintainRisk.countyCode.warning"));
        return true;
    }

    return true;
}

//-----------------------------------------------------------------------------
// This method is invoked to validate if any temp coverage exists under the risk once issue state was changed
//-----------------------------------------------------------------------------
function validateTempCovgExist() {
    var riskBaseRecordId = riskListGrid1.recordset("CRISKBASERECORDID").value;
    var transactionLogId = riskListGrid1.recordset("CTRANSACTIONLOGID").value;

    var url = getAppPath() + "/riskmgr/maintainRisk.do?process=validateTempCovgExist" +
            '&' + commonGetMenuQueryString() + '&riskBaseRecordId=' + riskBaseRecordId
            + '&transactionLogId=' + transactionLogId;
    new AJAXRequest("get", url, '', handleOnValidateTempCovgExist, false);
}

function handleOnValidateTempCovgExist(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;

            // parse and check if any temp coverage exist under the risk
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                var coverageExistB = oValueList[0]["COVGEXISTB"];
                if (coverageExistB == "Y") {
                    if (confirm(getMessage("pm.maintainRisk.auto.delete.coverage.confirmation"))) {
                        performAutoDeleteTempCovgs();
                    }
                }
            }
        }
    }
}

//-----------------------------------------------------------------------------
// This method is invoked to delete the temp coverages under the risk after issue state was changed
//-----------------------------------------------------------------------------
function performAutoDeleteTempCovgs() {
    var riskBaseRecordId = riskListGrid1.recordset("CRISKBASERECORDID").value;
    var transactionLogId = riskListGrid1.recordset("CTRANSACTIONLOGID").value;

    var url = getAppPath() + "/riskmgr/maintainRisk.do?process=performAutoDeleteTempCovgs" +
            '&' + commonGetMenuQueryString() + '&riskBaseRecordId=' + riskBaseRecordId
            + '&transactionLogId=' + transactionLogId;
    new AJAXRequest("get", url, '', handleOnPerformAutoDeleteTempCovgs, false);
}

function handleOnPerformAutoDeleteTempCovgs(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            handleAjaxMessages(data, null);
        }
    }
}
//-----------------------------------------------------------------------------
// Pre Add Occupant validation
//-----------------------------------------------------------------------------
function preAddOccupantValidations() {

    var selRowid = riskListGrid1.recordset("ID").value;
    var selSlotId = riskListGrid1.recordset("CSLOTID").value;
    var selRiskTypeCode = riskListGrid1.recordset("CRISKTYPECODE").value;
    var selRiskBaseRecordId = riskListGrid1.recordset("CRISKBASERECORDID").value;
    var sTransEffectiveDate = policyHeader.lastTransactionInfo.transEffectiveFromDate;
    var transactionCode = policyHeader.lastTransactionInfo.transactionCode;
    var isValid = true;
    var isOOswipChangeRecordExist = false;
    var ooswipChangeRowId;

    first(riskListGrid1);
    while (!riskListGrid1.recordset.eof) {
        var sEffFromDate = riskListGrid1.recordset("CRISKEFFECTIVEFROMDATE").value;
        var sEffToDate = riskListGrid1.recordset("CRISKEFFECTIVETODATE").value;

        //Chck if Slot is already occupied
        if (riskListGrid1.recordset("CSLOTID").value == selSlotId &&
                riskListGrid1.recordset("CRISKTYPECODE").value == selRiskTypeCode &&
                getRealDate(sEffFromDate) <= getRealDate(sTransEffectiveDate) &&
                getRealDate(sEffToDate) > getRealDate(sTransEffectiveDate)) {
            if (riskListGrid1.recordset("CRISKNAME").value != "VACANT" ||
                    ((parseInt(riskListGrid1.recordset("CENTITYID").value)) > 0) &&
                            (riskListGrid1.recordset("CRECORDMODECODE").value == "TEMP")) {
                isValid = false;
                break;
            }
        }

        // Check OOSWIP
        if (transactionCode == "OOSENDORSE" &&
                riskListGrid1.recordset("CRISKBASERECORDID").value == selRiskBaseRecordId &&
                (riskListGrid1.recordset("CRECORDMODECODE").value == "TEMP" ||
                        riskListGrid1.recordset("CRECORDMODECODE").value == "REQUEST")) {
            isOOswipChangeRecordExist = true;
            ooswipChangeRowId = riskListGrid1.recordset("ID").value;
        }
        next(riskListGrid1);
    }

    // Go to first row to avoid unknown error in getRow function
    first(riskListGrid1);

    if (isOOswipChangeRecordExist) {
        // Go to changed row
        getRow(riskListGrid1, ooswipChangeRowId);
    }
    else {
        // Go back to original row
        getRow(riskListGrid1, selRowid);
    }

    // Alert messages based on validation result
    if (transactionCode == "OOSENDORSE" && !isOOswipChangeRecordExist) {
        handleError(getMessage("pm.addSlotOccupant.ooswipCheck.error"));
        isValid = false;
    }
    else if (!isValid) {
        handleError(getMessage("pm.addSlotOccupant.slotOccupied.error"));
    }
    return isValid;
}

function addOccupant() {
    setInputFormField("occupantEntityId", 0);
    setInputFormField("occupantEntityName", "");
    openEntitySelectWinFullName("occupantEntityId", "occupantEntityName", "handleFindClient()");
}

function handleFindClient() {
    var url = getAppPath() + "/entitymgr/lookupEntity.do?process=getEntityName"
            + "&entityId=" + getObjectValue("occupantEntityId")
            + "&entityNameFieldName=entityName" + "&date=" + new Date();
    new AJAXRequest("get", url, "", handleOnAddOccupant, false);
}

//-----------------------------------------------------------------------------
// Add Occupant after entity selection
//-----------------------------------------------------------------------------
function handleOnAddOccupant(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return false;
            }

            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                getObject("occupantEntityName").value = oValueList[0]["ENTITYNAME"];

                //If it's in oosWIP endorsement, simply set entityID and entityName into current selected row.
                //No need to add new row at this case
                var transactionCode = policyHeader.lastTransactionInfo.transactionCode;
                /**
                 * We should also add a new row for oosWIP endorsement if the vacant slot risk record is newly added in the current transaction.
                 * If we add a slot risk in the oosWIP endorsement transaction, we can set occupant to the vacant slot risk directly.
                 * It is not necessary to click Change button before setting occupant.
                 *
                 * For the case we click Change button before setting occupant, we can set data into current selected row directly.
                 * For the case we add a new vacant slot risk and then click Occupant button, we should add a new row just the same
                 * way we do when it is a common endorsement transaction.
                 *
                 * If official record id is null, then it is the case we add a new vacant slot risk in the current transaction.
                 */
                var officialRecordId = riskListGrid1.recordset("COFFICIALRECORDID").value;
                if (transactionCode == "OOSENDORSE" && !isEmpty(officialRecordId)) {
                    riskListGrid1.recordset("CRISKNAME").value = getObjectValue("occupantEntityName");
                    riskListGrid1.recordset("CENTITYID").value = getObjectValue("occupantEntityId");
                }
                else {
                    // Save current row's data into object
                    origSlotRiskRowId = riskListGrid1.recordset("ID").value;
                    oRowData = getObjectFromRecordset(riskListGrid1);
                    isSlotOccupant = true;
                    commonOnButtonClick('ADD');
                }
            }
        }
    }
}

function handleOnGetInitialValuesForSlotOccupant(ajax) {
    handleOnGetInitialValuesForAddRisk(ajax, true);
}

//-----------------------------------------------------------------------------
// Delete associated occupant if slot risk is deleted
//-----------------------------------------------------------------------------
function riskListGrid_deleteDependentRow() {
    var parentRowId = riskListGrid1.recordset("ID").value;

    first(riskListGrid1);
    while (!riskListGrid1.recordset.eof) {
        if (riskListGrid1.recordset("ID").value == parentRowId) {
            // only check other risks
            next(riskListGrid1);
            continue;
        }

        var offRowId = riskListGrid1.recordset("COFFICIALRECORDID").value;
        var curRowId = riskListGrid1.recordset("ID").value;

        if (offRowId == parentRowId &&
                (riskListGrid1.recordset("CRECORDMODECODE").value == "TEMP" ||
                        riskListGrid1.recordset("CRECORDMODECODE").value == "REQUEST")) {
            setSelectedRow("riskListGrid", riskListGrid1.recordset("ID").value);
            riskListGrid_deleterow();
            break;
        }
        next(riskListGrid1);
    }

    // Go back
    first(riskListGrid1);
    setSelectedRow("riskListGrid", parentRowId);
}

function handleNavigateRecords(policyNo, policyTermHistoryId) {
    setWindowLocation("viewRiskSummary.do?policyNo=" + policyNo + "&policyTermHistoryId=" + policyTermHistoryId);
}

function viewAuditHistory(contextId) {
    var viewAuditUrl = getAppPath() + "/transactionmgr/auditmgr/viewAudit.do?"
            + commonGetMenuQueryString() + "&process=loadAllAudit" + "&fromPage=risk-risk" + "&contextId=" + contextId;
    var divPopupId = openDivPopup("", viewAuditUrl, true, true, "", "", "", "", "", "", "", false);
}
function setOpenWindow(openWhich) {
    getObject("openWhichWindow").value = openWhich;
}

//only get changed record to improve performance
function getChanges(XMLData) {
    return getChangesOnly(XMLData);
}

//-----------------------------------------------------------------------------
// Instruct display special warning messages.
//-----------------------------------------------------------------------------
function showSpecialWarning() {
    return true;
}

//-----------------------------------------------------------------------------
// Add risk in batch
//-----------------------------------------------------------------------------
function addRiskBatch(entityIdList) {
    getObject("newEntityId").value = entityIdList;
    commonOnSubmit('ADD_RISK_BATCH', true,true,true,true);
}

function isPrimaryRiskChanged() {
    var xmlData = getXMLDataForGridName("riskListGrid");
    var origXmlData = getOrigXMLData(xmlData);
    var riskChangedNodes = xmlData.documentElement.selectNodes("//ROW[UPDATE_IND != 'N' and CPRIMARYRISKB = 'Y']");
    if (riskChangedNodes.length > 0) {
        return true;
    }
    return false;
}

//-----------------------------------------------------------------------------
// Get riskOwnerId for location risk to open entity mini Popup window.
//-----------------------------------------------------------------------------
function getLocationPropertyId() {
    return riskListGrid1.recordset("CLOCATION").value;
}

//-----------------------------------------------------------------------------
// Opens the discipline decline list page.
//-----------------------------------------------------------------------------
function openDisciplineDeclineListPage(entityPk) {
    if (entityPk == 0) {
        alert(getMessage("pm.common.miniEntity.open.error"));
        return;
    }
    var path = getCSPath() + "/disciplinedeclinemgr/maintainDisciplineDecline.do?" +
            "process=loadAllDisciplineDeclineEntity&forDivPopupB=Y&ddlEntityId=" + entityPk;

    var entityName = riskListGrid1.recordset("CRISKNAME").value;
    if (!isEmpty(entityName)) {
        path = path+ "&ddlEntityName=" + encodeURIComponent(entityName);
    }
    var divPopupId = openDivPopup("", path, true, true, "", "", "800", "730", "", "", "", false);
}

var isReinstateIbnrRiskValid = "";
function valReinstateIbnrRisk(policyId, entityId, cancelTransId) {
    if (hasObject("isReinstateIbnrRiskValid") && getObjectValue("isReinstateIbnrRiskValid") == "Y") {
        var path = getAppPath() + "/riskmgr/maintainRisk.do?process=valReinstateIbnrRisk&policyId=" + policyId
                + "&entityId=" + entityId
                + "&cancelTransId=" + cancelTransId
                + "&date=" + new Date();
        new AJAXRequest("get", path, '', handleValReinstateIbnrRiskDone, false);
        return isReinstateIbnrRiskValid;
    }
}

function handleValReinstateIbnrRiskDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return false;
            }

            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                isReinstateIbnrRiskValid = oValueList[0]["isReinstateIbnrRiskValid"];
            }
        }
    }
}

function postAjaxRefresh(field, AjaxUrls) {
    if (AjaxUrls.indexOf('fieldId=territory')>0) {
        if (hasObject("territory")) {
            getObject("territoryLOVLABELSPAN").innerText = getObject("territory").innerText;
        }
    }
    if (AjaxUrls.indexOf('fieldId=premiumClass')>0) {
        if (hasObject("premiumClass")) {
            getObject("premiumClassLOVLABELSPAN").innerText = getObject("premiumClass").innerText;
        }
    }
}

function convertCoverage(transactionCode) {
    captureTransactionDetailsWithEffDate(transactionCode, "submitConvertCoverage");
}

function submitConvertCoverage() {
    var sRiskBaseRecordId = riskListGrid1.recordset("CRISKBASERECORDID").value;
    postAjaxSubmit("/transactionmgr/endorseTransaction.do?riskBaseRecordId=" + sRiskBaseRecordId, "convertCoverageTransaction", true, false, handleOnConvertCoverageTransaction);
}

function handleOnConvertCoverageTransaction(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }
            refreshPage();
        }
    }
}

function handleOnGetRecordExists(ajax) {
    commonHandleOnGetRecordExists(ajax);
}

function handleOnUnloadForDivPopup(divPopFrame) {
    if (italicsFieldId != "NONE") {
        italicsCurrentGridName = "riskListGrid";
        italicsFieldIdList = italicsFieldId;
        commonOnSetButtonItalics(false);
        spliceTheRecordExistsFields();
    }
}

function getRecordExistsUrl() {
    var selectedDataGrid = getXMLDataForGridName("riskListGrid");
    var url = getCSPath() + "/recordexistsmgr/maintainRecordExists.do?process=retrieveRecordExistsIndicator" +
            "&policyId=" + policyHeader.policyId;
    url += "&riskId=" + selectedDataGrid.recordset("ID").value +
            "&entityId=" + selectedDataGrid.recordset("CENTITYID").value +
            "&riskBaseRecordId=" + selectedDataGrid.recordset("CRISKBASERECORDID").value +
            "&termEffectiveFromDate=" + policyHeader.termEffectiveFromDate +
            "&pageCode=PM_RISK_SUMMARY" +
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

function handleExitWorkflow() {
    var transactionLogId = policyHeader.lastTransactionId;
    var url = "viewRiskSummary.do?&process=loadWarningMessage&date=" + new Date() + "&transactionLogId=" + transactionLogId;
    // initiate async call
    new AJAXRequest("get", url, '', handleOnGetWarningMsg, false);
}

function callBackAutoSaveForFrame(autoSaveResult) {
    var switchPrimaryTabFlgLoc = false;
    var switchGridRowFlgLoc = false;
    var switchSecondlyTabFlgLoc = false;

    updateMainTokenWithIframe(getObject(subFrameId));

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

    if(!autoSaveResult){

        if(switchSecondlyTabFlgLoc){
            setCacheTabIds(getPreviousTab() + "," + getPreviousTab());
        }

        if(switchGridRowFlgLoc){
            rollback = true;
            selectRowById("riskListGrid", getPreviousRow());
        }

        if(switchPrimaryTabFlgLoc){
            nextPrimaryTabId = "";
            nextPrimaryTabAction = "";
            return;
        }
    }

    if (autoSaveResult) {
        removeMessagesForFrame();

        if(getPreviousTab() == "ADDTL_EXPOSURE"){
            getIFrameWindow().handleOnButtonClick("CLOSE_DIV", getPreviousRow());
        }

        var functionExists = eval("window.handleOnItalicTabStyle");
        if (functionExists) {
            handleOnItalicTabStyle();
        }

        if(!new RegExp("^switch.*$").test(operation) && isDefined(operation)) { // except switchPrimaryTab operation
            processMainPageAfterAutoSaveSubTab();
            return;
        }

        if(switchSecondlyTabFlgLoc || switchGridRowFlgLoc){
            if (isPageDataChanged()) {
                autoSaveWip();
            }else {
                setCacheRowIds(getCurrentRow() + "," + getCurrentRow());
                if(requiredSubmitMainPage(getPreviousTab()) && eval("getIFrameWindow().isNeedToRefreshParentB") &&
                        getIFrameWindow().isNeedToRefreshParentB()) {
                    setCacheTabIds(getCurrentTab() + "," + getCurrentTab());
                    refreshPage();
                    return;
                }
                selectTabById(getCurrentTab());
            }
        }else if(switchPrimaryTabFlgLoc){
            if (isPageDataChanged()) {
                autoSaveWip();
            }else {
                showProcessingImgIndicator();
                setWindowLocation(nextPrimaryTabAction);
            }
        }else {
            if(requiredSubmitMainPage(getCurrentTab()) && eval("getIFrameWindow().isNeedToRefreshParentB") &&
                    getIFrameWindow().isNeedToRefreshParentB()) {
                refreshPage();
            }
        }
    }
    if(switchPrimaryTabFlgLoc || switchSecondlyTabFlgLoc || switchGridRowFlgLoc){
        operation = undefined;
    }
}

function riskListGrid_selectRow(id) {
    var selectedDataGrid = getXMLDataForGridName("riskListGrid");

    if (selectedDataGrid.recordset("UPDATE_IND").value != 'I') {
        italicsCurrentGridName = "riskListGrid";
        commonOnSetButtonItalics(isButtonStyle());
    }

    var selectedRiskId = riskListGrid1.recordset("ID").value;
    if(isEmpty(getObjectValue('riskHeaderRiskId'))
       || getObjectValue('riskHeaderRiskId') != selectedRiskId){
        setObjectValue('riskHeaderRiskId', selectedRiskId);
    }

    if (isTabStyle()) {
        if(!isMainPageRefreshedFlg && !rollback && !getSkipAutoSave()
                && $.inArray(operation, resetOperationArray) == -1 ) {
            operation = "switchGridRow";
        }else if (isMainPageRefreshedFlg && isDefined(getBtnOperation())) {
            operation = getBtnOperation();
            clearBtnOperation();
        }
        if(getPreviousRow() == "") {
            setCacheRowIds(id + "," + id);
            setCacheTabIds(getFirstTab() + "," + getFirstTab());
        }
        var autoSaveResult = commonOnSubmitReturnTypes.noDataChange;
        if (rollback || getSkipAutoSave()) {
            setCacheRowIds(id + "," + id);
        }
        else {
            if(isReservedTab(getPreviousTab()) && !isPageGridsDataChanged()) {
                setCacheRowIds(id + "," + id);
            }else {
                setCacheRowIds(getPreviousRow() + "," + id);
                // if not switch row, no need to auto save
                if(!isMainPageRefreshed()) {
                    processAutoSaveSubTab(getCurrentTab());
                    autoSaveResult = autoSaveResultType;
                }
            }
        }
        if (autoSaveResult == commonOnSubmitReturnTypes.submitSuccessfully) {
            switchGridRowFlg = true;
            if(getCurrentTab() == "COPYALL" || getCurrentTab() == "DELETEALL"){
                if(getIFrameWindow().isNeedToRefreshParentB()){
                    setCacheRowIds(id + "," + id);
                    refreshPage();
                    return;
                }
            }
        }
        else if (autoSaveResult == commonOnSubmitReturnTypes.noDataChange) {
            if(rollback || getSkipAutoSave()) {
                if(isReservedTab(getPreviousTab())) {
                    selectTabById(getPreviousTab());
                }
                rollback=false;
                setSkipAutoSave(false);
            }else{
                if (isPageDataChanged() && operation == 'switchGridRow') {
                    autoSaveWip();
                    return;
                }
                if(getCurrentTab() == "COPYALL"){
                    setCacheRowIds(getPreviousRow() + "," + id);
                    }else{
                    setCacheRowIds(id + "," + id);
                    }
                //Issue 190191: clear operation value after switch grid row for no data change case.
                clearOperationForTabStyle();
                selectTabById(getCurrentTab());
            }
        }
        else if (autoSaveResult == commonOnSubmitReturnTypes.commonValidationFailed ||
                autoSaveResult == commonOnSubmitReturnTypes.saveInProgress) {
            if (getCurrentTab() != "COPYALL" || !getIFrameWindow().isNeedToRefreshParentB()) {
                rollback = true;
                selectRowById("riskListGrid", getPreviousRow());
            }

            if(autoSaveResult == commonOnSubmitReturnTypes.commonValidationFailed){
                if(getCurrentTab() == "DELETEALL" || getCurrentTab() == "COPYALL"){
                    if(!getIFrameWindow().hasValidationErrorForAllRisk && getIFrameWindow().isNeedToRefreshParentB()){
                        refreshPage();
                    }
                }
            }
        }
        else if (autoSaveResultType == commonOnSubmitReturnTypes.submitSuccessfullyWithPopup) {
            switchGridRowFlg = true;
            setCacheRowIds(id + "," + id);
        }
    }
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

function preButtonClick(asBtn) {
    if(isReservedTab(getCurrentTab())) {
        operation = asBtn;
        return true;
    }
    //After calling commonAddRow function, if previous operation is in Array,
    //set operation to "ADD" which will be specially handled in riskListGrid_selectRow().
    if ($.inArray(operation, changeToAddOperationArray) > -1 && asBtn =='ADD') {
        operation = asBtn;
    }

    var proceed = false;
    switch (asBtn) {
        case 'ADD_RISK':
        case 'ADD_EXISTING':
        case 'OCCUPANT':
        case "CHANGE":
        case "PREVIEW":
        case "SEARCH":
        case "EDIT":
            operation = asBtn;
            processAutoSaveSubTab(getCurrentTab());
            if (autoSaveResultType == commonOnSubmitReturnTypes.noDataChange) {
                proceed = true;
            }else if (getCurrentTab() == "COPYALL" || getCurrentTab() == "DELETEALL"){
                if(autoSaveResultType == commonOnSubmitReturnTypes.submitSuccessfully ||
                        autoSaveResultType == commonOnSubmitReturnTypes.commonValidationFailed) {
                    if (!getIFrameWindow().hasValidationErrorForAllRisk && getIFrameWindow().isNeedToRefreshParentB()) {
                        if(autoSaveResultType == commonOnSubmitReturnTypes.submitSuccessfully){
                            setBtnOperation(operation);
                        }
                        refreshPage();
                    }
                }else if (autoSaveResultType == commonOnSubmitReturnTypes.submitSuccessfullyWithPopup) {
                    setBtnOperation(operation);
                }
            }
            break;
        default:
            proceed = true;
            break;
    }
    return proceed;
}

function handleOnSecondaryTabClick(tabId) {
    // if click current tab, do nothing
    operation = "switchSecondlyTab";
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
        if(getPreviousTab() == "COPYALL" || getPreviousTab() == "DELETEALL"){
            if(getIFrameWindow().isNeedToRefreshParentB()){
                setCacheTabIds(tabId + "," + tabId);
                refreshPage();
            }
        }
    }
    else if (autoSaveResultType == commonOnSubmitReturnTypes.noDataChange) {
        //Issue 190191: clear operation value when in no data change situation.
        clearOperationForTabStyle();
        if (isPageDataChanged()) {
            autoSaveWip();
            return;
        }
        /**
         * if no any data change exists on sub-tab, select target tab and save it as default tab ids
         */
        selectTabById(tabId);
    }
    else if (autoSaveResultType == commonOnSubmitReturnTypes.commonValidationFailed ||
            autoSaveResultType == commonOnSubmitReturnTypes.saveInProgress) {
        //common validation failed on the Grid/Form of sub-Tab
        //No actions.
        if (getPreviousTab() != "COPYALL" || !getIFrameWindow().isNeedToRefreshParentB()) {
            setCacheTabIds(getPreviousTab() + "," + getPreviousTab());
        }

        if(autoSaveResultType == commonOnSubmitReturnTypes.commonValidationFailed){
            if(getPreviousTab() == "DELETEALL" || getPreviousTab() == "COPYALL"){
                if(!getIFrameWindow().hasValidationErrorForAllRisk && getIFrameWindow().isNeedToRefreshParentB()){
                    refreshPage();
                }
            }
        }
    }
    else if (autoSaveResultType == commonOnSubmitReturnTypes.submitSuccessfullyWithPopup) {
        switchSecondlyTabFlg = true;
        setCacheTabIds(tabId + "," + tabId);
    }
}

function autoSaveSubTab(toBeSavedTab) {
    setAutoSavedTabResultType();
    //if policy is official and tab is not allowed to modify, return nodatachange
    if((!policyHeader.wipB && !allowToModifyWhenOfficial(toBeSavedTab)) || isUndefined(toBeSavedTab)) {
        setAutoSavedTabResultType(commonOnSubmitReturnTypes.noDataChange);
        return;
    }
    //remove messages on parent screen.
    removeMessagesForFrame();

    if (!isReservedTab(toBeSavedTab)) {
        mainPageLock.lock();
    }

    switch (toBeSavedTab) {
        case "COI":
            var functionExists = eval("getIFrameWindow().commonOnSubmit");
            if(functionExists){
                getIFrameWindow().commonOnSubmit('SAVE');
            }
            else {
                setAutoSavedTabResultType(commonOnSubmitReturnTypes.noDataChange);
            }
            break;
        case "AFFILIATION":
            var functionExists = eval("getIFrameWindow().commonOnSubmit");
            if(functionExists){
                getIFrameWindow().commonOnSubmit('SAVE');
            }
            else {
                setAutoSavedTabResultType(commonOnSubmitReturnTypes.noDataChange);
            }
            break;
        case "EMPPHYS":
            var functionExists = eval("getIFrameWindow().commonOnSubmit");
            if(functionExists){
                getIFrameWindow().commonOnSubmit('SAVE');
            }
            else {
                setAutoSavedTabResultType(commonOnSubmitReturnTypes.noDataChange);
            }
            break;
        case "RISK_RELATION":
            var functionExists = eval("getIFrameWindow().commonOnSubmit");
            if (functionExists) {
                getIFrameWindow().commonOnSubmit('SAVE');
            }
            else {
                setAutoSavedTabResultType(commonOnSubmitReturnTypes.noDataChange);
            }
            break;
        case "SCHEDULE":
            var functionExists = eval("getIFrameWindow().commonOnSubmit");
            if (functionExists) {
                getIFrameWindow().commonOnSubmit('SAVE');
            }
            else {
                setAutoSavedTabResultType(commonOnSubmitReturnTypes.noDataChange);
            }
            break;
        case "SURCHARGE_POINTS":
            var functionExists = eval("getIFrameWindow().commonOnSubmit");
            if (functionExists) {
                getIFrameWindow().commonOnSubmit('SAVE',true,true,false);
            }
            else {
                setAutoSavedTabResultType(commonOnSubmitReturnTypes.noDataChange);
            }
            break;
        case 'COPYALL':
        case 'DELETEALL':
            var functionExists = eval("getIFrameWindow().commonOnButtonClick");
            if(functionExists){
                getIFrameWindow().commonOnButtonClick('PROCESS_COPYALL')
            }else{
                setAutoSavedTabResultType(commonOnSubmitReturnTypes.noDataChange);
            }
            break
        case 'HISTORY':
            setAutoSavedTabResultType(commonOnSubmitReturnTypes.noDataChange);
            break;
        case 'INSUREDINFO':
            setAutoSavedTabResultType(commonOnSubmitReturnTypes.noDataChange);
            break;
        case 'COPY_NEW':
            setAutoSavedTabResultType(commonOnSubmitReturnTypes.noDataChange);
            break;
        case "NATIONAL_PROGRAM":
            var functionExists = eval("getIFrameWindow().commonOnSubmit");
            if (functionExists) {
                getIFrameWindow().commonOnSubmit('SAVE');
            }
            else {
                setAutoSavedTabResultType(commonOnSubmitReturnTypes.noDataChange);
            }
            break;
        case 'ADDTL_EXPOSURE':
            var functionExists = eval("getIFrameWindow().commonOnSubmit");
            if(functionExists){
                getIFrameWindow().commonOnSubmit('SAVE');
            }else{
                setAutoSavedTabResultType(commonOnSubmitReturnTypes.noDataChange);
            }
            break;
        case "SELECT_ADDRESS":
            var functionExists = eval("getIFrameWindow().commonOnButtonClick");
            if (functionExists) {
                getIFrameWindow().commonOnButtonClick('SAVE');
            }
            else {
                setAutoSavedTabResultType(commonOnSubmitReturnTypes.noDataChange);
            }
            break;
        case "INSURED_TRACKING":
            var functionExists = eval("getIFrameWindow().commonOnButtonClick");
            if (functionExists) {
                getIFrameWindow().commonOnButtonClick('SAVE');
            }
            else {
                setAutoSavedTabResultType(commonOnSubmitReturnTypes.noDataChange);
            }
            break;
    }

   if (autoSaveResultType == commonOnSubmitReturnTypes.noDataChange
           || autoSaveResultType == commonOnSubmitReturnTypes.commonValidationFailed
           || autoSaveResultType == commonOnSubmitReturnTypes.saveInProgress
           || autoSaveResultType == commonOnSubmitReturnTypes.submitSuccessfullyWithPopup) {
       mainPageLock.unlock();
   }
}

function handleOnShowPageInFrame(tabId) {
    if ($.inArray(operation, resetOperationArray) > -1) {
        //if change or add operation, do not load sub tab.
        if (operation == 'ADD' || operation == 'CHANGE') {
            return;
        }
        operation = undefined;
    }
    switch (tabId) {
        case "COI":
                var url = getAppPath() + "/riskmgr/coimgr/maintainCoi.do?"
                        + commonGetMenuQueryString() + "&process=loadAllCoiHolder"
                        + "&riskId=" + riskListGrid1.recordset("ID").value;
                var idName = 'R_menuitem_PM_RSP_COI';
                var mi = getObject(idName);
                if (mi) {
                    mi.children[0].style.backgroundImage = '';
                }
                if (policyHeader.policyCycleCode == 'POLICY') {
                    italicsFieldId = "COIHOLDERB";
                }
                else {
                    italicsFieldId = "COIHOLDERQUOTEB";
                }
                showPageInFrame(url, subFrameId, tabId);
            break;
        case 'AFFILIATION':
                var riskEffectiveToDate = riskListGrid1.recordset("CRISKEFFECTIVETODATE").value;
                var riskBaseEffectiveToDate = riskListGrid1.recordset("CRISKBASEEFFECTIVETODATE").value;
                var riskStatus = riskListGrid1.recordset("CRISKSTATUS").value;
                var riskBaseStatus = riskListGrid1.recordset("CRISKBASESTATUS").value;
                var riskBaseRecordId = riskListGrid1.recordset("CRISKBASERECORDID").value;
                var XMLData = riskListGrid1;
                var selectedRowId = 0;
                var count = 0;
                var futureCancelRiskExistsB = 'N';
                var affiliationRiskExpDate = "";
                var riskExpDate = "";

                if (policyHeader.wipB) {
                    riskExpDate = riskEffectiveToDate;
                }
                else {
                    riskExpDate = riskBaseEffectiveToDate;
                }

                if (!isEmptyRecordset(XMLData.recordset)) {
                    // Get the current selected row.
                    selectedRowId = XMLData.recordset("ID").value;
                    first(XMLData);
                    while (!XMLData.recordset.eof) {
                        if(XMLData.recordset("CRISKBASERECORDID").value == riskBaseRecordId &&
                                XMLData.recordset("CRECORDMODECODE").value == "TEMP" &&
                                XMLData.recordset("CRISKSTATUS").value == 'CANCEL') {
                            count++;
                            break;
                        }
                        next(XMLData);
                    }
                    first(XMLData);
                    // Select the current selected row.
                    setSkipAutoSave(true);
                    selectRowById("riskListGrid", selectedRowId);

                    if (count != 0) {
                        futureCancelRiskExistsB = 'Y';
                    }

                    if (futureCancelRiskExistsB == 'Y') {
                        if (getRealDate(riskEffectiveToDate) < getRealDate(policyHeader.lastTransactionInfo.transEffectiveFromDate)) {
                            affiliationRiskExpDate = riskEffectiveToDate;
                        }
                        else {
                            affiliationRiskExpDate = policyHeader.lastTransactionInfo.transEffectiveFromDate;
                        }
                    }
                    else {
                        if (getRealDate(riskExpDate) < getRealDate(riskBaseEffectiveToDate) && riskStatus != 'CANCEL' && riskBaseStatus != 'CANCEL') {
                            affiliationRiskExpDate = riskBaseEffectiveToDate;
                        }
                        else {
                            if (policyHeader.lastTransactionInfo.transactionCode != "OOSENDORSE") {
                                affiliationRiskExpDate = riskExpDate;
                            }
                            else {
                                affiliationRiskExpDate = getObjectValue("policyExpirationDate");
                            }
                        }
                    }
                }

                var url = getAppPath() + "/riskmgr/affiliationmgr/maintainAffiliation.do?"
                        + commonGetMenuQueryString() + "&process=loadAllAffiliation"
                        + "&riskId=" + riskListGrid1.recordset("ID").value
                        + "&riskEntityId=" + riskListGrid1.recordset("CENTITYID").value
                        + "&riskTypeCode=" + escape(riskListGrid1.recordset("CRISKTYPECODE").value)
                        + "&affiliationRiskExpDate=" + affiliationRiskExpDate;

                if (policyHeader.policyCycleCode == 'POLICY') {
                    italicsFieldId = "AFFILIATIONB";
                }
                else {
                    italicsFieldId = "AFFILIATIONQUOTEB";
                }
                showPageInFrame(url, subFrameId, tabId);
            break;
        case 'EMPPHYS':
            var riskBaseRecordId = riskListGrid1.recordset("CRISKBASERECORDID").value;
            var riskTypeCode = riskListGrid1.recordset("CRISKTYPECODE").value;
            var fteEquivalent = riskListGrid1.recordset("CFTEEQUIVALENT").value;
            var fteFullTimeHrs = riskListGrid1.recordset("CFTEFULLTIMEHRS").value;
            var ftePartTimeHrs = riskListGrid1.recordset("CFTEPARTTIMEHRS").value;
            var ftePerDiemHrs = riskListGrid1.recordset("CFTEPERDIEMHRS").value;
            var riskStatus = riskListGrid1.recordset("CRISKSTATUS").value;
            var riskName = escape(riskListGrid1.recordset("CRISKNAME").value);
            var url = getAppPath() + "/riskmgr/empphysmgr/maintainEmployedPhysician.do?"
                    + commonGetMenuQueryString() + "&process=loadAllEmployedPhysician"
                    + "&riskName=" + riskName
                    + "&riskBaseRecordId=" + riskBaseRecordId
                    + "&riskTypeCode=" + escape(riskTypeCode)
                    + "&fteEquivalent=" + fteEquivalent
                    + "&fteFullTimeHrs=" + fteFullTimeHrs
                    + "&ftePartTimeHrs=" + ftePartTimeHrs
                    + "&ftePerDiemHrs=" + ftePerDiemHrs
                    + "&riskStatus=" + riskStatus
                    + "&riskId=" + riskListGrid1.recordset("ID").value;

            if (policyHeader.policyCycleCode == 'POLICY') {
                italicsFieldId = "EMPPHYSB";
            }
            else {
                italicsFieldId = "EMPPHYQUOTEB";
            }
            showPageInFrame(url, subFrameId, tabId);
            break;
        case 'RISK_RELATION':
            if (isEmptyRecordset(riskListGrid1.recordset)) {
                alert(getMessage("pm.maintainRiskRelation.noRisk.error"));
                hideRiskSummaryPageFrame(tabId);
            }
            else {
                var riskBaseRecordId = riskListGrid1.recordset("CRISKBASERECORDID").value;
                var riskTypeCode = escape(riskListGrid1.recordset("CRISKTYPECODE").value);
                var riskEffectiveFromDate = riskListGrid1.recordset("CRISKEFFECTIVEFROMDATE").value;
                var riskEffectiveToDate = riskListGrid1.recordset("CRISKEFFECTIVETODATE").value;
                setInputFormField("riskBaseRecordId", riskBaseRecordId);
                setInputFormField("riskTypeCode", riskTypeCode);
                setInputFormField("riskEffectiveFromDate", riskEffectiveFromDate);
                setInputFormField("riskEffectiveToDate", riskEffectiveToDate);
                var url = getAppPath() + "/riskmgr/maintainRiskRelation.do?"
                        + commonGetMenuQueryString() + "&process=loadAllRiskRelation"
                        + "&riskId=" + riskListGrid1.recordset("ID").value
                        + "&riskBaseRecordId=" + riskBaseRecordId
                        + "&currentRiskTypeCode=" + riskTypeCode
                        + "&endorsementQuoteId=" + policyHeader.lastTransactionInfo.endorsementQuoteId
                        + "&riskEffectiveFromDate=" + riskEffectiveFromDate
                        + "&origRiskEffectiveFromDate=" + riskListGrid1.recordset("CRISKEFFECTIVEFROMDATE").value
                        + "&riskEffectiveToDate=" + riskEffectiveToDate
                        + "&riskCountyCode=" + riskListGrid1.recordset("CRISKCOUNTY").value
                        + "&reverse=N"
                        + "&refreshParentB=N"
                        + "&origPracticeStateCode=" + riskListGrid1.recordset("CPRACTICESTATECODE").value;

                if (policyHeader.policyCycleCode == 'POLICY') {
                    italicsFieldId = "RISKRELATIONB";
                }
                else {
                    italicsFieldId = "RISKRELATIONQUOTEB";
                }
                showPageInFrame(url, subFrameId, tabId);
            }
            break;
        case "SCHEDULE":
            var url = getAppPath() + "/schedulemgr/maintainSchedule.do?" + commonGetMenuQueryString() +
                    "&process=loadAllSchedule&riskId=" + riskListGrid1.recordset("ID").value;

            if (policyHeader.policyCycleCode == 'POLICY') {
                italicsFieldId = "RISKSCHEDB";
            }
            else {
                italicsFieldId = "RISKSCHEDQUOTEB";
            }
            showPageInFrame(url, subFrameId, tabId);
            break;
        case 'SURCHARGE_POINTS':
            var url = getAppPath() + "/riskmgr/maintainRiskSurchargePoints.do?" + commonGetMenuQueryString()
                    + "&riskId=" + riskListGrid1.recordset("ID").value;
            showPageInFrame(url, subFrameId, tabId);
            break;
        case 'COPYALL':
            showProcessingDivPopup();
            var riskId = riskListGrid1.recordset("id").value;
            var url = getAppPath() + "/riskmgr/performRiskCopyAll.do?"
                    + commonGetMenuQueryString() + "&process=validateRiskCopySource"
                    + "&riskId=" + riskId
                    + "&riskTypeCode=" + escape(riskListGrid1.recordset("CRISKTYPECODE").value)
                    + "&policyTypeCode=" + policyHeader.policyTypeCode;
            new AJAXRequest("get", url, "", handleOnValidateRiskCopySource, true);
            break;
        case "DELETEALL":
            viewRiskDeleteAllPage();
            break;
        case "INSUREDINFO":
            var url = getAppPath() + "/riskmgr/viewInsuredInfo.do?"
                    + commonGetMenuQueryString() + "&rEntityId=" + riskListGrid1.recordset("CENTITYID").value +
                    "&process=loadAllInsuredInfo&riskId=" + riskListGrid1.recordset("ID").value;
            showPageInFrame(url, subFrameId, tabId);
            break;
        case "HISTORY":
            var url = getAppPath() + "/riskmgr/viewInsuredHistory.do?"
                    + commonGetMenuQueryString() + "&rEntityId=" + riskListGrid1.recordset("CENTITYID").value +
                    "&process=loadAllInsuredHistory";
            showPageInFrame(url, subFrameId, tabId);
            break;
        case 'COPY_NEW':
            var url = getAppPath() + "/policymgr/createPolicy.do?process=getInitialValuesForCopyNewPolicy"
                    + "&riskId=" + riskListGrid1.recordset("ID").value
                    + "&num1=" + riskListGrid1.recordset("CPOLICYNUM1").value
                    + "&num2=" + riskListGrid1.recordset("CPOLICYNUM2").value
                    + "&num3=" + riskListGrid1.recordset("CPOLICYNUM3").value
                    + "&char1=" + riskListGrid1.recordset("CPOLICYCHAR1").value
                    + "&char2=" + riskListGrid1.recordset("CPOLICYCHAR2").value
                    + "&char3=" + riskListGrid1.recordset("CPOLICYCHAR3").value
                    + "&date1=" + riskListGrid1.recordset("CPOLICYDATE1").value
                    + "&date2=" + riskListGrid1.recordset("CPOLICYDATE2").value
                    + "&date3=" + riskListGrid1.recordset("CPOLICYDATE3").value
                    + "&policyLayerCode=" + riskListGrid1.recordset("CPOLICYLAYERCODE").value
                    + "&policyFormCode=" + riskListGrid1.recordset("CPOLICYPOLICYFORMCODE").value
                    + "&policyNo=" + policyHeader.policyNo + "&isFromCopyNew=Y";
            showPageInFrame(url, subFrameId, tabId);
            break;
        case "NATIONAL_PROGRAM":
            var url = getAppPath() + "/riskmgr/nationalprogrammgr/maintainNationalProgram.do?"
                    + commonGetMenuQueryString() + "&process=loadAllNationalProgram"
                    + "&riskBaseRecordId=" + riskListGrid1.recordset("CRISKBASERECORDID").value
                    + "&riskId=" + riskListGrid1.recordset("ID").value;
            showPageInFrame(url, subFrameId, tabId);
            break;
        case "ADDTL_EXPOSURE":
            var riskEffectiveToDate = riskListGrid1.recordset("CRISKEFFECTIVETODATE").value;
            var riskBaseEffectiveToDate = riskListGrid1.recordset("CRISKBASEEFFECTIVETODATE").value;
            var riskStatus = riskListGrid1.recordset("CRISKSTATUS").value;
            var riskBaseStatus = riskListGrid1.recordset("CRISKBASESTATUS").value;
            var riskBaseRecordId = riskListGrid1.recordset("CRISKBASERECORDID").value;
            var XMLData = riskListGrid1;
            var selectedRowId = 0;
            var count = 0;
            var futureCancelRiskExistsB = 'N';
            var exposureRiskExpDate = "";
            var riskExpDate = "";

            if (policyHeader.wipB) {
                riskExpDate = riskEffectiveToDate;
            }
            else {
                riskExpDate = riskBaseEffectiveToDate;
            }

            if (!isEmptyRecordset(XMLData.recordset)) {
                // Get the current selected row.
                selectedRowId = XMLData.recordset("ID").value;
                first(XMLData);
                while (!XMLData.recordset.eof) {
                    if(XMLData.recordset("CRISKBASERECORDID").value == riskBaseRecordId &&
                            XMLData.recordset("CRECORDMODECODE").value == "TEMP" &&
                            XMLData.recordset("CRISKSTATUS").value == 'CANCEL') {
                        count++;
                        break;
                    }
                    next(XMLData);
                }
                first(XMLData);
                // Select the current selected row.
                setSkipAutoSave(true);
                selectRowById("riskListGrid", selectedRowId);

                if (count != 0) {
                    futureCancelRiskExistsB = 'Y';
                }

                if (futureCancelRiskExistsB == 'Y') {
                    if (getRealDate(riskEffectiveToDate) < getRealDate(policyHeader.lastTransactionInfo.transEffectiveFromDate)) {
                        exposureRiskExpDate = riskEffectiveToDate;
                    }
                    else {
                        exposureRiskExpDate = policyHeader.lastTransactionInfo.transEffectiveFromDate;
                    }
                }
                else {
                    if (getRealDate(riskExpDate) < getRealDate(riskBaseEffectiveToDate) && riskStatus != 'CANCEL' && riskBaseStatus != 'CANCEL') {
                        exposureRiskExpDate = riskBaseEffectiveToDate;
                    }
                    else {
                        if (policyHeader.lastTransactionInfo.transactionCode != "OOSENDORSE") {
                            exposureRiskExpDate = riskExpDate;
                        }
                        else {
                            exposureRiskExpDate = getObjectValue("policyExpirationDate");
                        }
                    }
                }
            }

            if(getRealDate(exposureRiskExpDate) > getRealDate(policyHeader.termEffectiveToDate)){
                exposureRiskExpDate = policyHeader.termEffectiveToDate;
            }

            var url = getAppPath() + "/riskmgr/addtlexposuremgr/maintainRiskAddtlExposure.do?" + commonGetMenuQueryString()
                    + "&process=loadAllRiskAddtlExposure"
                    + "&riskId=" + riskListGrid1.recordset("ID").value
                    + "&riskBaseRecordId=" + riskListGrid1.recordset("CRISKBASERECORDID").value
                    + "&riskEffectiveFromDate=" + riskListGrid1.recordset("CRISKEFFECTIVEFROMDATE").value
                    + "&riskEffectiveToDate=" + riskListGrid1.recordset("CRISKEFFECTIVETODATE").value
                    + "&exposureRiskExpDate=" + exposureRiskExpDate
                    + "&searchTermHistoryId=" + policyHeader.termBaseRecordId;
            if (policyHeader.policyCycleCode == 'POLICY') {
                italicsFieldId = "RISKADDTLEXPOSUREB";
            }
            else {
                italicsFieldId = "RISKADDTLEXPOSUREQUOTEB";
            }
            showPageInFrame(url, subFrameId, tabId);
            break;
        case "SELECT_ADDRESS":
            var selAddrUrl = getAppPath() + "/policymgr/selectAddress.do?" + commonGetMenuQueryString() + "&type=RISK"
                    + "&entityId=" + riskListGrid1.recordset("CENTITYID").value
                    + "&riskBaseRecordId=" + riskListGrid1.recordset("CRISKBASERECORDID").value
                    + "&riskStatus=" + riskListGrid1.recordset("CRISKSTATUS").value;
            if (policyHeader.policyCycleCode == 'POLICY') {
                italicsFieldId = "SELECTADDRESSB";
            }
            else {
                italicsFieldId = "SELECTADDRESSGQUOTEB";
            }
            showPageInFrame(selAddrUrl, subFrameId, tabId);
            break;
        case "INSURED_TRACKING":
                var url = getAppPath() + "/riskmgr/insuredmgr/maintainInsuredTracking.do?" + commonGetMenuQueryString()
                        + "&process=loadAllInsuredTracking"
                        + "&riskId=" + riskListGrid1.recordset("ID").value
                        + "&riskBaseRecordId=" + riskListGrid1.recordset("CRISKBASERECORDID").value
                        + "&searchTermHistoryId=" + policyHeader.termBaseRecordId;
                if (policyHeader.policyCycleCode == 'POLICY') {
                    italicsFieldId = "INSTRKB";
                }
                else {
                    italicsFieldId = "INSTRKQUOTEB";
                }
                showPageInFrame(url, subFrameId, tabId);
            break;
    }
}

function processMainPageAfterAutoSaveSubTab() {
    switch(operation) {
        case "WIP":
        case "SAVEWIP":
            if((getCurrentTab() == "DELETEALL" || getCurrentTab() ==  "COPYALL") &&
                    isUndefined(currentSubmitAction) && operation == "WIP"){
                currentSubmitAction = "SAVE";
                eventHandler = "submitForm";
                document.forms[0].action = buildMenuQueryString("PM_PT_VIEWRISKSUMMARY", getFormActionAttribute());
                document.forms[0].process.value = "saveAllRiskSummary";
            }
            manualSaveWIP("PM_PT_VIEWRISKSUMMARY", "saveAllRiskSummary");
            break;
        case "OFFICIAL":
            if((getCurrentTab() == "DELETEALL" || getCurrentTab() ==  "COPYALL") && isUndefined(currentSubmitAction)){
                currentSubmitAction = "SAVE";
                eventHandler = "submitForm";
                document.forms[0].action = buildMenuQueryString("PM_PT_VIEWRISKSUMMARY", getFormActionAttribute());
                document.forms[0].process.value = "saveAllRiskSummary";
            }
            manualSaveOfficial();
            break;
        case "RATE":
            if((getCurrentTab() == "DELETEALL" || getCurrentTab() ==  "COPYALL") && isUndefined(currentSubmitAction)){
                currentSubmitAction = "RATE";
            }
            setInputFormField("processRatingB", "Y");
            manualSaveWIP("PM_PT_VIEWRISKSUMMARY", "saveAllRiskSummary");
            break;
        case "PREVIEW":
            onPreviewButtonClick();
            break;
        case "ADD_RISK":
        case 'ADD_EXISTING':
        case 'OCCUPANT':
        case "CHANGE":
        case "SEARCH":
        case "EDIT":
            if (isPageDataChanged()
                    || (requiredSubmitMainPage(getCurrentTab()) && eval("getIFrameWindow().isNeedToRefreshParentB")
                    && getIFrameWindow().isNeedToRefreshParentB())) {
                setBtnOperation(operation);
                autoSaveWip();
                return;
            }
            handleOnButtonClick(operation);
            break;
        default:
            handleSaveOptionSelection(operation);
    }
}

function currentTabIsHideForCurrentRow(tabId){
    var isHideB = true;
    switch (tabId) {
        case "COI":
        case 'AFFILIATION':
            isHideB = getFieldValueFromRecordSet("CISCOIHOLDERAVAILABLE");
            break;
        case "ADDTL_EXPOSURE":
            isHideB = getFieldValueFromRecordSet("CISADDTLEXPOSUREAVAILABLE");
            break;
        case 'EMPPHYS':
            isHideB = getFieldValueFromRecordSet("CISEMPPHYSAVAILABLE");
            break;
        case 'RISK_RELATION':
        case 'SCHEDULE':
            isHideB = getFieldValueFromRecordSet("DISPLAY_IND");
            break;
        case "SURCHARGE_POINTS":
            isHideB = getFieldValueFromRecordSet("CISSURCHARGEPOINTSAVAILABLE");
            break;
        case 'COPYALL':
        case 'DELETEALL':
            var indValue = "Y";
            if(hasObject('isCopyAllAvailable')){
                indValue=getObjectValue('isCopyAllAvailable');
            }
            isHideB = (indValue == 'N') ? true : false;
            break;
        case 'HISTORY':
            isHideB = getFieldValueFromRecordSet("CISINSUREDHISTORYLAVAILABLE");
            break;
        case 'INSUREDINFO':
            isHideB = getFieldValueFromRecordSet("CISINSUREDINFOAVAILABLE");
            break;
        case 'SUSPENSION':
            isHideB = getFieldValueFromRecordSet("CISSUSPENSIONAVAILABLE");
            break;
        case 'COPY_NEW':
            isHideB = getFieldValueFromRecordSet("CISCOPYNEWAVAILABLE");
            break;
        case 'INSURED_TRACKING':
            isHideB = getFieldValueFromRecordSet("CISINSUREDTRACKINGAVAILABLE");
            break;
        case 'NATIONAL_PROGRAM':
        case 'SELECT_ADDRESS':
        case 'RISK':
            isHideB = false;
            break;
        default:
            isHideB = false;
            break;
    }
    return isHideB;
}

function getFieldValueFromRecordSet(fieldName){
    var selectedDataGrid = getXMLDataForGridName("riskListGrid");
    var indValue = selectedDataGrid.recordset(fieldName).value;
    return indValue == "Y" ? false : true;
}

function handleOnUIProcess() {
    mainPageLock.initialLock();
}

function hideRiskSummaryPageFrame (tabId) {
    highlightTab(tabId);
    setCacheTabIds(tabId + "," + tabId);
    $("#" + subFrameId).hide();
}

function handleAfterViewValidation(){
    if(isTabStyle()){
        //1. after refresh main page
        if(isMainPageRefreshed()){
            if(getCurrentTab() == "COPYALL") {
                if(eval("getIFrameWindow().isNeedToRefreshParentB") && getIFrameWindow().isNeedToRefreshParentB()){
                    //2. issue#190085, after close 'View Validation Error' popup,
                    //   which was opened after user clicked Process button
                    refreshPage();
                }else{
                    selectTabById(getFirstTab());
                }
                return;
            }
        }
        //3. switch grid row/secondly tab
        if(operation == "switchSecondlyTab"){
            setCacheTabIds(getPreviousTab() + "," + getPreviousTab());
        }else if(operation == "switchGridRow") {
            rollback=true;
            selectRowById("riskListGrid", getPreviousRow());
        }
        if(getCurrentTab() == "COPYALL" && eval("getIFrameWindow().isNeedToRefreshParentB")
                && getIFrameWindow().isNeedToRefreshParentB()){
            refreshPage();
        }
    }
}

function isEmptyMainPageGridRecordset(){
    return isEmptyRecordset(getXMLDataForGridName("riskListGrid").recordset);
}
