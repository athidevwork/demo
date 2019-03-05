
//-----------------------------------------------------------------------------
// Javascript file for maintainRisk.jsp.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   May 07, 2010
// Author: syang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 05/07/2010       syang       106894 - For adding risk batch, the "firstRiskB" is unnecessary.
// 05/12/2010       syang       107258 - Added policyTypeCode and riskTypeCode to Copy All page.
// 06/11/2010       dzhang      101253 - Added find fuction to popup select procedue code div.
//                              And disable the findtext 'Procedure Codes' input field.
// 06/28/2010       dzhang      101253 - Added function selectProcedureCodeDone.
// 07/14/2010       bhong       107682 - Encode riskTypeCode to deal with ampersand(&) character.
// 07/14/2010       dzhang      103806 - Added url parameter 'origPracticeStateCode' when click Risk Relation button.
// 08/04/2010       syang       103793 - Modified handleOnButtonClick() to open Maintain Surcharge Points in risk level.
// 08/09/2010       syang       110692 - Encode riskName to deal with ampersand(&) character.
// 09/13/2010       wfu         111776: Replaced hardcode string with resource definition
// 09/27/2010       syang       110819 - Get the endorsementQuoteId from policyHeader instead of selected risk.
// 11/25/2010       dzhang      114894 - Modified handleOnSelectRiskEntity() if newEntityIds.length == 1,
//                              directly set newEntityId to newEntityIds[0].
// 01/14/2011       ryzhao      113558 - Pass riskTypeCode to performCancellation for new carrier is available checking.
// 01/19/2011       wfu         113566 - Added logic to handle copying policy from risk.
// 01/19/2011       syang       105832 - Added openDisciplineDeclineListPage() to open discipline decline page.
// 03/17/2011       wfu         118437 - Replaced undefined Js message of pm common with same messages.
// 03/18/2011       ryzhao      Issue 113559 - Add "PROCESS_ERP" case in handleOnButtonClick() function.
// 03/31/2011       dzhang      94232 - Added isIbnrRisk to performCancellation.
// 04/08/2011       wqfu        117888 - Modified riskExists to accept multiple short term risks been added without gap.
// 04/21/2011       fcb         119793 - postAjaxRefresh added.
// 06/09/2011       jshen       120700 - modify url of suspension popup page because eclaim changed corresponding url
// 05/01/2011       fcb         Issue 105791 - Added convertCoverage, submitConvertCoverage, handleOnConvertCoverageTransaction
// 07/04/2011       ryzhao      Issue 121160 - Modified preAddOccupantValidations() to change the checking logic
//                              if a slot risk is occupied from checking the risk name against "VACANT"
//                              to checking if the entity id is zero.
// 07/04/2011       dzhang      115708 - Modified handleOnButtonClick() to open Maintain National Program in risk level.
// 08/05/2011       ryzhao      123475 - Modified handleOnAddOccupant(). We should also add a new row for oosWIP endorsement
//                              if the vacant slot risk record is newly added in the current transaction.
// 08/17/2011       ryzhao      Issue 121160 - Rollback the changes.
// 09/01/2011       dzhang      123960 - Remove the disable Procedure Codes field logic, handle it by WebWB's Stylesheet Class 'noEntryFinder'.
// 09/01/2011       dzhang      121130 - Modified handleOnButtonClick() to open Select Address in risk level.
// 09/21/2011       wfu         120554 - Fixed errors in quote pages and multiple grid pages.
// 10/10/2011       dzhang      123960 - Modified find(): the Select Procedure page view mode dependent on risk record's editable mode.
// 10/11/2011       lmjiang     124326 - Modified find(): Reset the global value of 'currentFinderFieldName' after the procedure code finder is popped up.
// 10/18/2011       lmjiang     103805 - Add a hidden field 'transEffDate' for OBR rule.
// 11/03/2011       lmjiang     126675 - Call Ajax to determine if the characters on buttons are italic when the row is switched.
// 01/04/2012       wfu         127802 - Modified addOccupant and handleOnAddOccupant to get entity name for occupant risk.
// 03/09/2012       jshen       131499 - Added handlePostAddRow() function to set Name url value after row is added.
// 05/04/2012       xnie        132993 - a) Modified handleOnGetInitialValuesForOoseRisk() to call
//                                       handleOnGetInitialValuesForAddRisk().
//                                       b) Modified handleOnButtonClick() to reset oose risk valid indicator.
// 06/01/2012       xnie        132114 - Modified handleOnButtonClick() to add affiliationRiskExpDate info to url.
// 06/13/2012       tcheng      134129 - Modified handleOnGetInitialValuesForSlotOccupant() to call handleOnGetInitialValuesForAddRisk().
// 06/29/2012       tcheng      133964 - Modified handleOnButtonClick() to add insured info to url.

// 07/17/2012       ryzhao      135662 - Variable italicsFieldId should have different value for POLICY or QUOTE.
//                                       Modified all the related buttons to set different value to italicsFieldId.
// 08/24/2012       tcheng      136680 - Modified handleOnButtonClick to update "EMPPHYSQUOTEB" to "EMPPHYQUOTEB" match WebWB's field.
// 11/02/2012       xnie        121875 - Modified sendAJAXRequest to add officialRecordId field for getRiskAddlInfo case.
// 12/27/2012       tcheng      139862 - Added handleExitWorkflow() to pop up warning message.
// 02/22/2013       adeng       139879 - Modified handleOnButtonClick to pass in risk id when click on Insured Info button.
// 05/13/2013       adeng       143400 - Pass viewMode & endorsementQuoteId(if exist) to retrieve record exist information.
// 10/28/2013       jshen       148387 - Pass in the riskSocietyId when loading the copyall page.
// 12/06/2013       Parker      148036 - Refactor maintainRecordExists code to make one call per subsystem to the database.
// 12/19/2013       jyang       148585 - Append coverageId to the url, to avoid the coverageId is emptied in usersession.
// 02/13/2014       awu         147405 - Modified handleOnButtonClick to use risk's base record status to reinstate.
// 03/11/2014       fcb         152685 - transaction log id passed to loadWarningMessage
// 05/06/2014       fcb         151632 - Added refreshParentB for Risk Relation screen.
// 05/21/2014       jyang       154280 - Modified handleOnSubmit, removed the statements which set the risk type of the
//                                       new add risk to the current chosen risk.
// 06/26/2014       awu         155102 - Added riskListGrid_beforeFireAjaxOnSelectRow, moved the sendAJAXRequest to it
//                                       from riskListGrid_selectRow.
// 05/29/2014       jyang       149970 - Modified handleOnButtonClick, pass risk name to cancellationDetail page when
//                                       cancel risk.
// 07/25/2014       awu         152034 - 1). Roll back the changes of issue148585.
//                                       2). Modified riskListGrid_beforeFireAjaxOnSelectRow to call getRiskAddlInfo once
//                                           select the different risk.
//                                       3). Modified handleOnSubmit to append risk/coverage/coverage class IDs to URL for RATE.
// 08/13/2014       kxiang      155534 - Modified handleOnGetInitialValuesForAddRisk to call common function
//                                       commonHandleOnGetInitialValues.
// 10/14/2014       kxiang      157730 - Modified selectProcedureCodeDone to add logical before set 'UPDATE_IND' value.
// 11/25/2014       kxiang      158657 - Removed codes about Location2, as it's obsolete.
// 12/17/2014       wdang       159454 - Removed the logic "only check other risks" in preAddOccupantValidations().
// 12/30/2014       jyang       157750 - Modified openDisciplineDeclineListPage to encode entityName before append it to
//                                       URL.
// 03/20/2015       wdang       161448 - 1) Shifted thisopenEntityMiniPopupWin and its related functions to common.js.
//                                       2) Added getLocationPropertyId() to support entity mini popup window for location risk.
// 08/10/2015       wdang       157211 - Added a button url for INSURED_TRACKING.
// 10/13/2015       tzeng       164679 - Modified handleOnSubmit to add save WIP indicator for display risk relation
//                                       result message after save WIP.
// 01/28/2016       wdang       169024 - Reverted changes of 164679.
// 08/15/2016       eyin        177410 - Modified handleOnChange(), Added validateTempCovgExist(),
//                                       handleOnValidateTempCovgExist(), performAutoDeleteTempCovgs() and
//                                       handleOnPerformAutoDeleteTempCovgs(); Added change event to delete temp
//                                       coverages under the risk when issue state was changed.
// 09/05/2016       xnie        179349 - Modified viewRiskCopyAllPage() to make risk copy all page taller.
// 03/10/2017       eyin        180675 - Added logic to handle case in new UI tab style.
// 07/04/2017       wrong       168374 - 1) Modified viewRiskCopyAllPage() to add new parameter isFundState in url.
//                                       2) Modified viewRiskDeleteAllPage() to add new parameter isFundState in url.
//                                       3) Modify handleOnChange() to post ajax request for changing lov values when
//                                          change risk county or risk specialty fields.
//                                       4) Modified postAjaxRefresh() to add logic to display pcf territory and
//                                          pcf class field value.
// 07/18/2017       eyin        186988 - Modified openSelectLocation() to store the parentWindow flag of div popup.
// 07/26/2017       lzhang      182246 - Clean up unsaved message for page changes
// 09/26/2017       wli         187808 - Modified riskListGrid_selectRow to exclude highlight fake tab for autosaving
//                                       when switch grid row.
// 09/21/2017       eyin        169483 - Added Exposure button.
// 10/31/2017       eyin        169483 - Added to support 'Multi-Exposure' in currentTabIsHideForCurrentRow().
// 11/08/2017       eyin        169483 - Don't change operation value when reset Multi-Exposure flag after load sub-tab.
// 11/09/2017       tzeng       187689 - 1) Modified handleOnButtonClick(), riskListGrid_selectRow() to set flag to
//                                          skip processAutoSaveSubTab() for the case which do not need to process.
//                                       2) Modified handleOnSubmit(), preButtonClick(), handleOnSecondaryTabClick(),
//                                          riskListGrid_selectRow() to support processAutoSaveSubTab().
//                                       3) Modified processMainPageAfterAutoSaveSubTab() to remove "delete" case to make
//                                          consistent with preButtonClick().
//                                       4) Modified handleOnShowPageInFrame() to use the new flag rather than "rollback"
//                                          to skip the processAutoSaveSubTab().
//                                       5) Modified handleOnButtonClick() to call clearOperationForTabStyle() to clean
//                                          the operation for the case which do not need.
// 11/30/2017       wrong       190014 - 1) Modified riskListGrid_beforeFireAjaxOnSelectRow() to invoke
//                                          sendAJAXRequest("getRecordExists") when insert a new row in grid.
// 12/13/2017       wrong       190191 - 1) Modified handleOnSecondaryTabClick to add clearOperationForTabStyle() to
//                                          clear operation value when switching sub tab in no data change case.
//                                       2) Modified riskListGrid_selectRow to add clearOperationForTabStyle() to clear
//                                          operation value when switching grid row in no data change case.
// 12/14/2017       wrong       190085 - 1) Modified handleAfterViewValidation() to support the case that 'View Validation
//                                          Error' popup opened after User clicked Process button on sub-tab COPY ALL,
//                                          and then close the popup, to check if need to refresh risk page.
// 12/25/2017       lzhang      190086 - 1) Modified handleOnButtonClick(): use 'openDivPopup' to open suspension page.
// 12/29/2017       wrong       190192 - Modified handleOnButtonClick/handleOnShowPageInFrame to support Select Address
//                                       button/tab italic.
// 01/04/2017       lzhang      188231 - 1) Modified riskListGrid_selectRow(): set selected riskId to riskHeaderRiskId
// 07/06/2018       xnie        187070 - Modified viewRiskCopyAllPage() to add input parameter isGr1CompVisible.
// 07/11/2018       wrong       193977 - Modified riskListGrid_selectRow() to change condition of setting value to
//                                       riskHeaderRiskId.
// 07/26/2018       ryzhao      194545 - Modified viewRiskDeleteAllPage() to add input parameter isGr1CompVisible.
// 08/28/2018       ryzhao      188891 - Modified handleOnButtonClick() to add logic for new button "Claims Free Query".
// 09/17/2018       ryzhao      195271 - Modified autoSaveSubTab() to set indicator if it is auto save data from sub-tab
//                                       when saving risk information.
//-----------------------------------------------------------------------------
var orgExpirationDate = "";
var orgRollingIbnrB = "";
var isValidExpirationDate = "N";
var isValidIBNR = "N";
var isOoseRiskValid = true;
var ooseRowData;
var origRiskId = "";
var isSlotOccupant = false;
var oRowData;
var origSlotRiskRowId = "";
var italicsFieldId = "NONE";
var selectedRiskId = "";
var isSameRiskB = true;
var pageName = "Risk";

function handleOnLoad() {
    setInputFormField("needToHandleExitWorkFlow", "Y");
    if(isTabStyle())
        commonOnUIProcess();
    invokeWorkflow();
}

function selectRowInGridOnPageLoad() {
    $.when(dti.oasis.grid.getLoadingPromise("riskListGrid")).then(function(){
        // select row by previousely selected risk ID if there was no error
        var isRiskGridEmpty = isEmptyRecordset(getXMLDataForGridName("riskListGrid").recordset);
        if (!hasErrorMessages && currentRiskId) {
            selectRowById("riskListGrid", currentRiskId);
        } else {
            if(isTabStyle()){
                if(getPreviousRow() == "") {
                    selectFirstRowInGrid("riskListGrid");
                    if(isRiskGridEmpty) {
                        selectFirstTab();
                    }
                }else{
                    rollback = true;
                    selectRowById("riskListGrid", getPreviousRow());
                }
            }else{
                selectFirstRowInGrid("riskListGrid");
            }
        }
    });
}


function riskListGrid_beforeFireAjaxOnSelectRow(rowId) {
    var loadAddlInfo = false;
    var selectedDataGrid = getXMLDataForGridName("riskListGrid");
    var riskId = selectedDataGrid.recordset("ID").value;
    var addlInfo1 = selectedDataGrid.recordset("CADDLINFO1").value.toUpperCase();
    var isRiskExpirationDateEditable = selectedDataGrid.recordset("CISRISKEFFECTIVETODATEEDITABLE").value;

    if(selectedDataGrid.recordset("UPDATE_IND").value != 'I'){
        if(riskId == selectedRiskId && (isEmpty(addlInfo1) || isRiskExpirationDateEditable == "X")){
            loadAddlInfo = true;
        }else if (riskId != selectedRiskId){
            loadAddlInfo = true;
        }

        if(selectedRiskId != "" && riskId != selectedRiskId) {
            isSameRiskB = false;
        }
    }
    if (loadAddlInfo) {
        sendAJAXRequest("getRiskAddlInfo");
    }
    sendAJAXRequest("getRecordExists");
    selectedRiskId = riskId;
}

function riskListGrid_selectRow(id) {
    if(isTabStyle()){
        if(!isMainPageRefreshedFlg && !rollback && !getSkipAutoSave()){
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
        if(rollback || getSkipAutoSave()) {
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
                /**
                 * for coverage detail and component tab auto save roll back, need to position previous tab
                 * for other tabs, nothing to do
                 */
                if(isReservedTab(getPreviousTab())) {
                    selectTabById(getPreviousTab());
                }
                rollback=false;
                setSkipAutoSave(false);
            }else{
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
        }else if(autoSaveResultType == commonOnSubmitReturnTypes.submitSuccessfullyWithPopup){
            switchGridRowFlg = true;
            setCacheRowIds(id + "," + id);
        }
    }
    var selectedRiskId = riskListGrid1.recordset("ID").value;
    if(isEmpty(getObjectValue('riskHeaderRiskId'))
       || getObjectValue('riskHeaderRiskId') != selectedRiskId){
        setObjectValue('riskHeaderRiskId', selectedRiskId);
    }
    // Set original value for expiration date
    if (orgExpirationDate == "") {
        orgExpirationDate = getObjectValue("riskEffectiveToDate");
    }
    // Set original value for IBNR Indicator
    orgRollingIbnrB = riskListGrid1.recordset("CROLLINGIBNRB").value;

    if(!isElementHidden(getObject('riskNotes'))){
        var noteInd = getObjectValue("noteB");
        if (noteInd == 'Y') {
            setObjectValue("riskNotes", noteInd);
        }
        else {
            setObjectValue("riskNotes", "");
        }
    }
    maintainNoteImageForAllNoteFields();
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

function handleOnSubmit(action) {
    var proceed = true;

    if(isTabStyle()){
        operation = action;
        removeMessagesForFrame();
        if(!isReservedTab(getPreviousTab()) && !isPreviewButtonClicked() && action != "SAVE"
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
            document.forms[0].action = buildMenuQueryString("PM_PT_VIEWRISK", getFormActionAttribute());
            document.forms[0].process.value = "saveAllRisk";
            loadSaveOptions("PM_RISK", "submitForm");
            proceed = false;
            break;
        case 'SAVEWIP':
            document.forms[0].action = buildMenuQueryString("PM_PT_VIEWRISK", getFormActionAttribute());
            document.forms[0].process.value = "saveAllRisk";
            handleSaveOptionSelection("WIP");
            break;
        case 'RATE':
            document.forms[0].action = buildMenuQueryString("PM_PT_VIEWRISK", getFormActionAttribute());
            document.forms[0].process.value = "saveAllRisk";
            setInputFormField("newSaveOption", "WIP");
            setInputFormField("processRatingB", "Y");
            break;
       case 'ADD_RISK_BATCH':
           clearCacheTabIds();
           clearCacheRowIds();
           document.forms[0].process.value = "addRiskBatch";
           setInputFormField("newEntityIdList", getObject("newEntityId").value);
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

function handleOnButtonClick(btn) {
    switch (btn) {
        case 'ADD_RISK':
            if (getSysParmValue("PM_ADD_MAPPED_DFLTS") == 'Y') {
                if (isPrimaryRiskChanged()) {
                    alert(getMessage("pm.addRisk.unsaved.changes.error"));
                    break;
                }
            }
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
                var riskStatus = riskListGrid1.recordset("CRISKBASESTATUS");
                var cancelTransId = riskListGrid1.recordset("CTRANSACTIONLOGID");
                if (valReinstateIbnrRisk(policyHeader.policyId, riskListGrid1.recordset("CENTITYID").value, cancelTransId.value) == "INVALID") {
                    alert(getMessage("pm.maintainRisk.isReinstateIbnrRiskValid.info"));
                }
                performReinstate("RISK", sRiskBaseRecordId, sRiskId, effFromDate, effToDate, riskStatus, cancelTransId);
            }
            break;

        case "COI":
            var UpdateInd = riskListGrid1.recordset("UPDATE_IND").value;
            var saveRequired = commonSaveRequiredToChangePages(pageName, "COI Holder", "Y", UpdateInd);
            if (saveRequired) {
                break;
            }
            else if (commonIsOkToChangePages()) {
                var url = getAppPath() + "/riskmgr/coimgr/maintainCoi.do?"
                    + commonGetMenuQueryString() + "&process=loadAllCoiHolder"
                    + "&riskId=" + riskListGrid1.recordset("ID").value;
                var idName = 'R_menuitem_PM_RISK_COI_PUP';
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
            }
            break;
        case "AFFILIATION":
            var UpdateInd = riskListGrid1.recordset("UPDATE_IND").value;
            var saveRequired = commonSaveRequiredToChangePages(pageName, "Affiliation", "Y", UpdateInd);
            if (saveRequired) {
                break;
            }
            else if (commonIsOkToChangePages()) {
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
            }
            break;
        case "EMPPHYS":
            var saveRequired = commonSaveRequiredToChangePages(pageName, "Employed Physician", "N", "");
            if (saveRequired) {
                break;
            }
            else {
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
            }
            break;
        case "COPYALL":
            var UpdateInd = riskListGrid1.recordset("UPDATE_IND").value;
            var saveRequired = commonSaveRequiredToChangePages(pageName, "Copy All", "Y", UpdateInd);
            if (saveRequired) {
                break;
            }else if (commonIsOkToChangePages()) {
                showProcessingDivPopup();
                var riskId = riskListGrid1.recordset("id").value;
                var url = getAppPath() + "/riskmgr/performRiskCopyAll.do?"
                    + commonGetMenuQueryString() + "&process=validateRiskCopySource"
                    + "&riskId=" + riskId
                    + "&riskTypeCode=" + escape(riskListGrid1.recordset("CRISKTYPECODE").value)
                    + "&policyTypeCode=" + policyHeader.policyTypeCode;

                new AJAXRequest("get", url, "", handleOnValidateRiskCopySource, true);
            }
            break;
        case "DELETEALL":
            if (commonIsOkToChangePages()) {
                viewRiskDeleteAllPage();
            }
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
                var saveRequired = commonSaveRequiredToChangePages(pageName, "Risk Relation", "N", "");
                if (saveRequired) {
                    break;
                }
                else {
                    var url = getAppPath() + "/riskmgr/maintainRiskRelation.do?"
                            + commonGetMenuQueryString() + "&process=loadAllRiskRelation"
                            + "&riskId=" + riskListGrid1.recordset("ID").value
                            + "&riskBaseRecordId=" + riskListGrid1.recordset("CRISKBASERECORDID").value
                            + "&currentRiskTypeCode=" + escape(riskListGrid1.recordset("CRISKTYPECODE").value)
                            + "&endorsementQuoteId=" + policyHeader.lastTransactionInfo.endorsementQuoteId
                            + "&riskEffectiveFromDate=" + riskListGrid1.recordset("CRISKEFFECTIVEFROMDATE").value
                            + "&origRiskEffectiveFromDate=" + riskListGrid1.recordset("CRISKEFFECTIVEFROMDATE").value
                            + "&riskEffectiveToDate=" + riskListGrid1.recordset("CRISKEFFECTIVETODATE").value
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
            }
            break;
        case "HISTORY":
            var url = getAppPath() + "/riskmgr/viewInsuredHistory.do?"
                + commonGetMenuQueryString() + "&rEntityId=" + riskListGrid1.recordset("CENTITYID").value +
                      "&process=loadAllInsuredHistory";
            var divPopupId = openDivPopup("", url, true, true, "", "", "600", "450", "", "", "", false);
            break;
        case "INSUREDINFO":
            var UpdateInd = riskListGrid1.recordset("UPDATE_IND").value;
            var saveRequired = commonSaveRequiredToChangePages(pageName, "Insured Info", "Y", UpdateInd);
            if (saveRequired) {
                break;
            }
            else {
                var url = getAppPath() + "/riskmgr/viewInsuredInfo.do?"
                        + commonGetMenuQueryString() + "&rEntityId=" + riskListGrid1.recordset("CENTITYID").value +
                        "&process=loadAllInsuredInfo&riskId=" + riskListGrid1.recordset("ID").value;
                openDivPopup("", url, true, true, "", "", "600", "450", "", "", "", false);
            }
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
           var divPopupId = openDivPopup("", url, true, true, "", "", "800", "600", "800", "400", "", false);
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
            var UpdateInd = riskListGrid1.recordset("UPDATE_IND").value;
            var saveRequired = commonSaveRequiredToChangePages(pageName, "Schedule", "Y", UpdateInd);
            if (saveRequired) {
                break;
            }
            else {
                var url = getAppPath() + "/schedulemgr/maintainSchedule.do?" + commonGetMenuQueryString() +
                        "&process=loadAllSchedule&riskId=" + riskListGrid1.recordset("ID").value;

                if (policyHeader.policyCycleCode == 'POLICY') {
                    italicsFieldId = "RISKSCHEDB";
                }
                else {
                    italicsFieldId = "RISKSCHEDQUOTEB";
                }
                var divPopupId = openDivPopup("", url, true, true, "", "", "", "", "", "", "", false);
            }
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
                var saveRequired = commonSaveRequiredToChangePages(pageName, "National Program", "N", "");
                if (saveRequired) {
                    break;
                }
                else {
                    var url = getAppPath() + "/riskmgr/nationalprogrammgr/maintainNationalProgram.do?"
                            + commonGetMenuQueryString() + "&process=loadAllNationalProgram"
                            + "&riskBaseRecordId=" + riskListGrid1.recordset("CRISKBASERECORDID").value
                            + "&riskId=" + riskListGrid1.recordset("ID").value;
                    var divPopupId = openDivPopup("", url, true, true, "", "", "900", "650", "", "", "", false);
                }
            }
            break;
        case "SELECT_ADDRESS":
            var UpdateInd = riskListGrid1.recordset("UPDATE_IND").value;
            var saveRequired = commonSaveRequiredToChangePages(pageName, "Select Address", "Y", UpdateInd);
            if (saveRequired) {
                break;
            }
            else {
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
            }
            break;
        case "INSURED_TRACKING":
            var UpdateInd = riskListGrid1.recordset("UPDATE_IND").value;
            var saveRequired = commonSaveRequiredToChangePages(pageName, "Insured Tracking", "Y", UpdateInd);
            if (saveRequired) {
                break;
            }
            else {
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
            }
            break;
        case "EXPOSURE":
            var UpdateInd = riskListGrid1.recordset("UPDATE_IND").value;
            var saveRequired = commonSaveRequiredToChangePages(pageName, "Multi Exposure", "Y", UpdateInd);
            if (saveRequired) {
                break;
            }
            else {
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
                var divPopupId = openDivPopup("", url, true, true, "", "", 850, 850, "", "", "", false);
            }
            break;
        case 'PREVIEW':
            onPreviewButtonClick();
            break;
        case 'DELETE':
            commonDeleteRow("riskListGrid");
            break;
        case 'ADD':
            if(isTabStyle()){
                clearCacheTabIds();
                clearCacheRowIds();
                setSkipAutoSave(true);
            }
            commonAddRow("riskListGrid");
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

function preButtonClick(asBtn) {
    if(isReservedTab(getCurrentTab())) {
        operation = asBtn;
        return true;
    }
    var proceed = false;
    switch (asBtn) {
        case "ADD_RISK":
        case 'ADD_EXISTING':
        case 'OCCUPANT':
        case "CHANGE":
        case "SEARCH":
        case "PREVIEW":
            operation = asBtn;
            processAutoSaveSubTab(getCurrentTab());

            if(autoSaveResultType == commonOnSubmitReturnTypes.noDataChange) {
                proceed = true;
            }else if(getCurrentTab() == "COPYALL" || getCurrentTab() == "DELETEALL"){
                if(autoSaveResultType == commonOnSubmitReturnTypes.submitSuccessfully ||
                        autoSaveResultType == commonOnSubmitReturnTypes.commonValidationFailed){
                    if(!getIFrameWindow().hasValidationErrorForAllRisk && getIFrameWindow().isNeedToRefreshParentB()){
                        if(autoSaveResultType == commonOnSubmitReturnTypes.submitSuccessfully){
                            setBtnOperation(operation);
                        }
                        refreshPage();
                    }
                }else if(autoSaveResultType == commonOnSubmitReturnTypes.submitSuccessfullyWithPopup){
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

function addOneRisk(entityId) {
    getObject("newEntityId").value = entityId;
    handleOnSelectRiskEntity();
}
function handleOnSelectLocation(action, locations) {
    if (action == "Select") {
        var locationCount = locations.length;
        for (var i = 0; i < locationCount; i++) {
            setInputFormField("newLocation", locations[i].locationId);
            sendAJAXRequest("validateForAddRisk");
        }
    }
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
                    if(isTabStyle()){
                        if(getPreviousRow() != getCurrentRow()){
                            rollback = true;
                            selectRowById("riskListGrid", getPreviousRow());
                        }
                        if(getPreviousTab() != getCurrentTab()){
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
    if(isTabStyle()){
        showPageInFrame(path, subFrameId, "COPYALL");
    }else{
        var divPopupId = openDivPopup("", path, true, true, null, null, 930, 930, 900, 1400, "loadAllCopyRisk", false);
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
        + "&isFundState=" + riskListGrid1.recordset("CISFUNDSTATE").value
        + "&isGr1CompVisible=" + riskListGrid1.recordset("CISGR1COMPVISIBLE").value;
    if(isTabStyle()){
        showPageInFrame(path, subFrameId, "DELETEALL");
    }else{
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
    //alert(path);
    var divPopupId = openDivPopup("", path, true, true,
        null, null, "", "", "", "", "selectLocation", false);
    if(isTabStyle()){
        handleOnPutParentWindowOfDivPopup(divPopupId, parentWindowFlagReturnTypes.ParentWindow);
    }
}

function handleOnSelectLocation(action, locations) {
    if (action == "Select") {
        var locationCount = locations.length;
        for (var i = 0; i < locationCount; i++) {
            setInputFormField("newLocation", locations[i].locationId);
            sendAJAXRequest("validateForAddRisk");
        }
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

function handleOnGetInitialValuesForAddRisk(ajax) {
    commonHandleOnGetInitialValues(ajax, "riskNameHref");
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
    handleOnGetInitialValuesForAddRisk(ajax);
}

function sendAJAXRequest(process) {
    // set url
    var url = "maintainRisk.do?process=" + process +
              "&" + commonGetMenuQueryString("PM_MAINTAIN_RISK", "");
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
                   "&firstRiskB=" + firstRiskB;
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
                   "&riskName=" + escape(selectedDataGrid.recordset("CRISKNAME").value);
            break;

        case 'getInitialValuesForSlotOccupant':
            var sRiskEffFromDate = riskListGrid1.recordset("CRISKEFFECTIVEFROMDATE").value;
            var sRiskEffToDate = riskListGrid1.recordset("CRISKEFFECTIVETODATE").value;
            url += "&riskId=" + origSlotRiskRowId + "&riskEffectiveToDate=" + sRiskEffToDate;
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
    var isFundState = getObjectValue("isFundState");
    // Modify County Code
    if (obj.name == "riskCounty") {
        var riskCounty = getObjectValue(obj.name);
        var showWarning = getSysParmValue("PM_GIVE_COUNTY_WRNNG");
        var transCode = policyHeader.lastTransactionInfo.transactionCode;
        if (isFundState == 'Y') {
            // Set default value for pcf county when county changes.
            setDefaultValueForPcfRiskCounty(riskCounty);
        }
        if (showWarning == "Y" && (transCode != "NEWBUS" && transCode != "CONVRENEW" && transCode != "CONVREISSU"))
            handleError(getMessage("pm.maintainRisk.countyCode.warning"));
        return true;
    }

    // Modify State
    if (obj.name == "practiceStateCode") {
        var practiceStateCode = getObjectValue(obj.name);
        validateTempCovgExist();
        loadIsFundStateValue(practiceStateCode);
    }

    // Set default value for pcf specialty when specialty changes.
    if (obj.name == "riskClass" && isFundState == 'Y') {
        var riskClass = getObjectValue(obj.name);
        setDefaultValueForPcfRiskClass(riskClass);
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
    handleOnGetInitialValuesForAddRisk(ajax);
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
    setWindowLocation("maintainRisk.do?policyNo=" + policyNo + "&policyTermHistoryId=" + policyTermHistoryId);
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

//-----------------------------------------------------------------------------
// Load Risk RTE Notes
//-----------------------------------------------------------------------------
function loadRiskNotes() {
    if (window.loadNotes) {
        var riskBaseRecordId = getObjectValue("riskBaseRecordId");
        loadNotesWithReloadOption(riskBaseRecordId, "RISK", "RISK", true,'',"handleNotesExist");
    }
    else {
        alert(getMessage("pm.common.notes.functionality.notAvailable.error"));
    }
}

//-----------------------------------------------------------------------------
//change the note image depending on whether note exists
//-----------------------------------------------------------------------------
function handleNotesExist(notesInList, srcTblNameVal, srcRecFkVal) {
    if (notesInList) {
      setObjectValue("riskNotes", "Y");
      setObjectValue("noteB", "Y");
    }
    else {
      setObjectValue("riskNotes", "");
      setObjectValue("noteB", "N");
    }
    maintainNoteImageForAllNoteFields();
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

function find(fieldId) {
    if (fieldId == "procedureCodes") {
        var isReadOnly = 'Y';
        if (riskListGrid1.recordset("EDIT_IND").value == 'Y') {
            isReadOnly = 'N';
        }

        var path = getAppPath() +
                   "/riskmgr/selectProcedureCode.do?" + commonGetMenuQueryString()
            + "&procedureCodes=" + getObjectValue("procedureCodes")
            + "&termEffectiveFromDate=" + getObjectValue("termEffectiveFromDate")
            + "&riskTypeCode=" + escape(getObjectValue("riskTypeCode"))
            + "&practiceStateCode=" + getObjectValue("practiceStateCode")
            + "&isReadOnly=" + isReadOnly;
        var divPopupId = openDivPopup("", path, true, true, "", "", 842, 668, 834, 640, "selectProcedureCode", false);
        //reset field name for next finder.
        currentFinderFieldName = "";
    }
}

//-----------------------------------------------------------------------------
// Write back the selected procedure codes to 'Procedure Codes' field on risk tab
//-----------------------------------------------------------------------------
function selectProcedureCodeDone(procedureCodeList) {
    if(hasObject("procedureCodes")) {
        getObject("procedureCodes").value = procedureCodeList;
        if(riskListGrid1.recordset("UPDATE_IND").value == 'N') {
            riskListGrid1.recordset("UPDATE_IND").value = "Y";
        }
    }
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
        getObject("territoryLOVLABELSPAN").innerText = getObject("territory").innerText;
    }
    if (AjaxUrls.indexOf('fieldId=premiumClass')>0) {
        getObject("premiumClassLOVLABELSPAN").innerText = getObject("premiumClass").innerText;
    }
    if (AjaxUrls.indexOf('fieldId=pcfTerritory')>0) {
        getObject("pcfTerritoryLOVLABELSPAN").innerText = getObject("pcfTerritory").innerText;
    }
    if (AjaxUrls.indexOf('fieldId=pcfClass')>0) {
        getObject("pcfClassLOVLABELSPAN").innerText = getObject("pcfClass").innerText;
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
            "&pageCode=PM_RISK" +
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

/**
 * Handle the url of risk name column
 * @param table
 */
function handlePostAddRow(table) {
    if (table.id == "riskListGrid") {
        var xmlData = getXMLDataForGridName("riskListGrid");
        var fieldCount = xmlData.recordset.Fields.count;
        var riskNameCount;
        for (var i = 0; i < fieldCount; i++) {
            if (xmlData.recordset.Fields.Item(i).name == "CRISKNAME") {
                riskNameCount = i;
            }
            if (xmlData.recordset.Fields.Item(i).name.substr(4) == "" + riskNameCount) {
                var href = "javascript:void(0);";
                if (!isEmpty(getObjectValue("riskNameHref"))) {
                    href = "javascript:handleOnGridHref('riskListGrid', '" + getObjectValue("riskNameHref") + "');";
                }
                xmlData.recordset.Fields.Item(i).value = href;
            }
        }
        var selectedRow = getTableProperty(table, "selectedTableRowNo");
        table.rows[selectedRow].click();
    }
}

function handleExitWorkflow() {
    var transactionLogId = policyHeader.lastTransactionId;
    var url = "maintainRisk.do?&process=loadWarningMessage&date=" + new Date() + "&transactionLogId=" + transactionLogId;
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

        if(getPreviousTab() == "COPYALL" || getPreviousTab() == "DELETEALL"){
            if(getIFrameWindow().isNeedToRefreshParentB()){
                setCacheTabIds(tabId + "," + tabId);
                refreshPage();
            }
        }
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
    }else if(autoSaveResultType == commonOnSubmitReturnTypes.submitSuccessfullyWithPopup){
        switchSecondlyTabFlg = true;
        setCacheTabIds(tabId + "," + tabId);
    }
}

function handleOnUIProcess() {
    mainPageLock.initialLock();
}

//-----------------------------------------------------------------------------
// If 1) sub-tab is swithced,
//    2) coverage row is switched,
//    3) primary tab is switched,
//  then auto save logic will be invoked,
//  will check if the data change exists on sub-tab firstly.
//-----------------------------------------------------------------------------
function autoSaveSubTab(toBeSavedTab) {
    // set autoSaveResultType as undefined
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
            }else{
                setAutoSavedTabResultType(commonOnSubmitReturnTypes.noDataChange);
            }
            break;
        case 'AFFILIATION':
            var functionExists = eval("getIFrameWindow().commonOnSubmit");
            if(functionExists){
                getIFrameWindow().commonOnSubmit('SAVE');
            }else{
                setAutoSavedTabResultType(commonOnSubmitReturnTypes.noDataChange);
            }
            break;
        case 'EMPPHYS':
            var functionExists = eval("getIFrameWindow().commonOnSubmit");
            if(functionExists){
                getIFrameWindow().commonOnSubmit('SAVE');
            }else{
                setAutoSavedTabResultType(commonOnSubmitReturnTypes.noDataChange);
            }
            break;
        case 'RISK_RELATION':
            var functionExists = eval("getIFrameWindow().commonOnSubmit");
            if(functionExists){
                getIFrameWindow().commonOnSubmit('SAVE');
            }else{
                setAutoSavedTabResultType(commonOnSubmitReturnTypes.noDataChange);
            }
            break;
        case 'SCHEDULE':
            var functionExists = eval("getIFrameWindow().commonOnSubmit");
            if(functionExists){
                getIFrameWindow().commonOnSubmit('SAVE');
            }else{
                setAutoSavedTabResultType(commonOnSubmitReturnTypes.noDataChange);
            }
            break;
        case "SURCHARGE_POINTS":
            var functionExists = eval("getIFrameWindow().commonOnSubmit");
            if(functionExists){
                getIFrameWindow().commonOnSubmit('SAVE');
            }else{
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
            break;
        case 'HISTORY':
            setAutoSavedTabResultType(commonOnSubmitReturnTypes.noDataChange);
            break;
        case 'INSUREDINFO':
            setAutoSavedTabResultType(commonOnSubmitReturnTypes.noDataChange);
            break;
        case 'COPY_NEW':
            setAutoSavedTabResultType(commonOnSubmitReturnTypes.noDataChange);
            break;
        case 'INSURED_TRACKING':
            var functionExists = eval("getIFrameWindow().commonOnButtonClick");
            if(functionExists){
                getIFrameWindow().commonOnButtonClick('SAVE');
            }else{
                setAutoSavedTabResultType(commonOnSubmitReturnTypes.noDataChange);
            }
            break;
        case 'NATIONAL_PROGRAM':
            var functionExists = eval("getIFrameWindow().commonOnSubmit");
            if(functionExists){
                getIFrameWindow().commonOnSubmit('SAVE');
            }else{
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
        case 'SELECT_ADDRESS':
            var functionExists = eval("getIFrameWindow().commonOnButtonClick");
            if(functionExists){
                getIFrameWindow().commonOnButtonClick('SAVE');
            }else{
                setAutoSavedTabResultType(commonOnSubmitReturnTypes.noDataChange);
            }
            break;
        case 'RISK':
            var functionExists = eval("window.commonOnSubmit");
            if(functionExists){
                setInputFormField("callFromSubTabB", "Y");
                commonOnSubmit('SAVEWIP', true, true, false);
            }else{
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
            setCacheRowIds(getCurrentRow() + "," + getCurrentRow());
            if(requiredSubmitMainPage(getPreviousTab()) && eval("getIFrameWindow().isNeedToRefreshParentB") &&
                    getIFrameWindow().isNeedToRefreshParentB()) {
                setCacheTabIds(getCurrentTab() + "," + getCurrentTab());
                refreshPage();
                return;
            }
            selectTabById(getCurrentTab());
        }else if(switchPrimaryTabFlgLoc){
            showProcessingImgIndicator();
            setWindowLocation(nextPrimaryTabAction);
        }else{
            if(requiredSubmitMainPage(getCurrentTab()) && eval("getIFrameWindow().isNeedToRefreshParentB") &&
                    getIFrameWindow().isNeedToRefreshParentB()) {
                refreshPage();
            }
        }
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
                document.forms[0].action = buildMenuQueryString("PM_PT_VIEWRISK", getFormActionAttribute());
                document.forms[0].process.value = "saveAllRisk";
            }
            manualSaveWIP("PM_PT_VIEWRISK", "saveAllRisk");
            break;
        case "OFFICIAL":
            if((getCurrentTab() == "DELETEALL" || getCurrentTab() ==  "COPYALL") && isUndefined(currentSubmitAction)){
                currentSubmitAction = "SAVE";
                eventHandler = "submitForm";
                document.forms[0].action = buildMenuQueryString("PM_PT_VIEWRISK", getFormActionAttribute());
                document.forms[0].process.value = "saveAllRisk";
            }
            manualSaveOfficial();
            break;
        case "RATE":
            if((getCurrentTab() == "DELETEALL" || getCurrentTab() ==  "COPYALL") && isUndefined(currentSubmitAction)){
                currentSubmitAction = "RATE";
            }
            setInputFormField("processRatingB", "Y");
            manualSaveWIP("PM_PT_VIEWRISK", "saveAllRisk");
            break;
        case "ADD_RISK":
        case 'ADD_EXISTING':
        case 'OCCUPANT':
        case "CHANGE":
        case "SEARCH":
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
        default:
            handleSaveOptionSelection(operation);
    }
}

function handleOnShowPageInFrame(tabId) {
    switch (tabId) {
        case "COI":
            var url = getAppPath() + "/riskmgr/coimgr/maintainCoi.do?"
                    + commonGetMenuQueryString() + "&process=loadAllCoiHolder"
                    + "&riskId=" + riskListGrid1.recordset("ID").value;
            var idName = 'R_menuitem_PM_RISK_COI_PUP';
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
            var url = getAppPath() + "/riskmgr/maintainRiskRelation.do?"
                    + commonGetMenuQueryString() + "&process=loadAllRiskRelation"
                    + "&riskId=" + riskListGrid1.recordset("ID").value
                    + "&riskBaseRecordId=" + riskListGrid1.recordset("CRISKBASERECORDID").value
                    + "&currentRiskTypeCode=" + escape(riskListGrid1.recordset("CRISKTYPECODE").value)
                    + "&endorsementQuoteId=" + policyHeader.lastTransactionInfo.endorsementQuoteId
                    + "&riskEffectiveFromDate=" + riskListGrid1.recordset("CRISKEFFECTIVEFROMDATE").value
                    + "&origRiskEffectiveFromDate=" + riskListGrid1.recordset("CRISKEFFECTIVEFROMDATE").value
                    + "&riskEffectiveToDate=" + riskListGrid1.recordset("CRISKEFFECTIVETODATE").value
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
            break;
        case 'SCHEDULE':
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
        case "SURCHARGE_POINTS":
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
        case 'DELETEALL':
            viewRiskDeleteAllPage();
            break;
        case 'HISTORY':
            var url = getAppPath() + "/riskmgr/viewInsuredHistory.do?"
                    + commonGetMenuQueryString() + "&rEntityId=" + riskListGrid1.recordset("CENTITYID").value +
                    "&process=loadAllInsuredHistory";
            showPageInFrame(url, subFrameId, tabId);
            break;
        case 'INSUREDINFO':
            var url = getAppPath() + "/riskmgr/viewInsuredInfo.do?"
                    + commonGetMenuQueryString() + "&rEntityId=" + riskListGrid1.recordset("CENTITYID").value +
                    "&process=loadAllInsuredInfo&riskId=" + riskListGrid1.recordset("ID").value;
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
        case 'INSURED_TRACKING':
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
        case 'NATIONAL_PROGRAM':
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
        case 'SELECT_ADDRESS':
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
        case 'RISK':
            if (policyHeader.policyCycleCode == 'POLICY') {
                italicsFieldId = "RISKDETAILTABB";
            }
            else {
                italicsFieldId = "RISKDETAILTABQUOTEB";
            }
            showPageInFrame("", "riskDetail", tabId);
            break;
    }
    //
    // var functionExists = eval("window.handleOnItalicTabStyle");
    // if (functionExists) {
    //     handleOnItalicTabStyle();
    // }
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
