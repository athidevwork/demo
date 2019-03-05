//-----------------------------------------------------------------------------
// Javascript file for maintainRiskAddtlExposure.jsp.
//
// (C) 2015 Delphi Technology, inc. (dti)
// Author: eyin
// Date: May 24, 2017
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 09/21/2017       eyin        169483, Initial version.
// 11/08/2017       eyin        169483, modified to update the rollback value for parent window.
// 11/09/2017       tzeng       187689, Modified handleOnButtonClick() to set flag to skip processAutoSaveSubTab() for
//                                      the case which do not need to process.
// 11/02/2018       clm         195889 -  Grid replacement using dispatchElementEvent instead of fireEvent
//-----------------------------------------------------------------------------

var isOoseRiskAddtlExposureValid = true;
var ooseRowData;
var origRiskAddtlExposureId = "";
var changePracticeType = "";
//-----------------------------------------------------------------------------
// The entry function for clicking button
//-----------------------------------------------------------------------------
function handleOnButtonClick(asBtn, rowId) {
    switch (asBtn) {
        case 'ADD':
            addAdditionalExposure();
            break;
        case 'DEL':
            delAdditionalExposure();
            break;
        case 'SELECT_ADDRESS':
            selectLocation();
            break;
        case 'CLOSE_DIV':
            var XMLData = getParentWindow(true).riskListGrid1;
            if (getObjectValue("dataSavedB") == 'Y') {
                var selectedRowId = XMLData.recordset("ID").value;
                var riskBaseRecordId = "";
                if(typeof rowId != 'undefined'){
                    if (!isEmptyRecordset(XMLData.recordset)) {
                        first(XMLData);
                        while (!XMLData.recordset.eof) {
                            if(rowId == XMLData.recordset("ID").value){
                                riskBaseRecordId = XMLData.recordset("CRISKBASERECORDID").value;
                            }
                            next(XMLData);
                        }
                        first(XMLData);
                        while (!XMLData.recordset.eof) {
                            if(riskBaseRecordId == XMLData.recordset("CRISKBASERECORDID").value){
                                XMLData.recordset("CMULTIEXPOSUREB").value =
                                        getObjectValue("addtlPracticeSize") == "0" ? "N" : "Y";
                            }
                            next(XMLData);
                        }
                        first(XMLData);
                        // Select the current selected row.
                        getParentWindow(true).setSkipAutoSave(true);
                        getParentWindow(true).selectRowById("riskListGrid", selectedRowId);
                    }
                }else{
                    XMLData.recordset("CMULTIEXPOSUREB").value =
                            getObjectValue("addtlPracticeSize") == "0" ? "N" : "Y";
                }
            }
            break;
        case "CHANGEPRACTICEVALUE":
            // First check if there is modified record already
            // Reset oose risk valid indicator
            isOoseRiskAddtlExposureValid = true;
            if (preOoseChangeValidation("additional exposure", "riskAddtlExposureListGrid", "CRISKADDTLEXPBASERECORDID")) {
                // Check Change option first by ajax call
                sendAJAXRequest("validateForOoseRiskAddtlExposure");
                if (isOoseRiskAddtlExposureValid) {
                    changePracticeType = "changePracticeValue";
                    addOoseRiskAddtlExposure();
                }
            }
            break;
        case "CHANGEPRACTICEDATE":
            // First check if there is modified record already
            // Reset oose risk valid indicator
            isOoseRiskAddtlExposureValid = true;
            if (preOoseChangeValidation("additional exposure", "riskAddtlExposureListGrid", "CRISKADDTLEXPBASERECORDID")) {
                // Check Change option first by ajax call
                sendAJAXRequest("validateForOoseRiskAddtlExposure");
                if (isOoseRiskAddtlExposureValid) {
                    changePracticeType = "changePracticeDate";
                    addOoseRiskAddtlExposure();
                }
            }
            break;
    }
}

function addOoseRiskAddtlExposure() {
    // Save current row's data into object
    origRiskAddtlExposureId = riskAddtlExposureListGrid1.recordset("ID").value;
    ooseRowData = getObjectFromRecordset(riskAddtlExposureListGrid1);
    currentlySelectedGridId = getRiskAddtlExposureListGrid();
    commonAddRow(currentlySelectedGridId);
}

//-----------------------------------------------------------------------------
//Select the location address for the insured entity
//-----------------------------------------------------------------------------
function selectLocation(){
    currentlySelectedGridId = getRiskAddtlExposureListGrid();
    var xmlData = getXMLDataForGridName(currentlySelectedGridId);
    var addtlExpAddrId = xmlData.recordset("CADDRESSID").value;
    if(isEmpty(addtlExpAddrId)){
        addtlExpAddrId = -1;
    }
    var selAddrUrl = getAppPath() + "/policymgr/selectAddress.do?" + commonGetMenuQueryString() + "&type=RISK"
            + "&entityId=" + xmlData.recordset("CRISKENTITYID").value
            + "&riskBaseRecordId=" + xmlData.recordset("CRISKBASERECORDID").value
            + "&riskStatus=" + xmlData.recordset("CRISKSTATUS").value
            + "&isFromExposure=Y&addtlExpAddrId="+addtlExpAddrId;
    var divPopupId = getOpenCtxOfDivPopUp().openDivPopup("", selAddrUrl, true, true, "", "", 600, 500, "", "", "", false);
}

//-----------------------------------------------------------------------------
// Call back function for select location
//-----------------------------------------------------------------------------
function handleOnSelectLocation(locations) {
    var locationCount = locations.length;
    if (locationCount > 0) {
        setInputFormField("addressId",locations[0].addressId);
        //fire commonOnChange event to, update UPDATE_IND
        commonOnChange(getObject("addressId"));

        if(isEmpty(getObjectValue("practiceStateCode"))){
            setInputFormField("practiceStateCode", locations[0].stateCode);
            dispatchElementEvent(getObject("practiceStateCode"),"change");
        }

        if(isEmpty(getObjectValue("riskCounty"))){
            setInputFormField("riskCounty", locations[0].countyCode);
            dispatchElementEvent(getObject("riskCounty"),"change");
        }
    }
}

//-----------------------------------------------------------------------------
//The entry when clicking Delete button
//-----------------------------------------------------------------------------
function delAdditionalExposure() {
    currentlySelectedGridId = getRiskAddtlExposureListGrid();
    commonDeleteRow(currentlySelectedGridId);
}

//-----------------------------------------------------------------------------
// The entry when clicking Add button
//-----------------------------------------------------------------------------
function addAdditionalExposure() {
    currentlySelectedGridId = getRiskAddtlExposureListGrid();
    commonAddRow(currentlySelectedGridId);
}

//-----------------------------------------------------------------------------
// The entry when adding row
//-----------------------------------------------------------------------------
function riskAddtlExposureListGrid_setInitialValues() {
    if (isForOose == "Y") {
        //Copy original data to new row
        setRecordsetByObject(riskAddtlExposureListGrid1, ooseRowData);
        sendAJAXRequest("getInitialValuesForOoseRiskAddtlExposure");
        // Reset flag
        isForOose = "N";
        return;
    }

    sendAJAXRequest("getInitialValuesForAddRiskAddtlExposure");
}

//-----------------------------------------------------------------------------
// Send AJAX request
//-----------------------------------------------------------------------------
function sendAJAXRequest(process) {
    // set url
    var url = getAppPath() + "/riskmgr/addtlexposuremgr/maintainRiskAddtlExposure.do?"
        + commonGetMenuQueryString() + "&process=" + process;

    switch (process) {
        case 'getInitialValuesForAddRiskAddtlExposure':
            url += "&riskId=" + policyHeader.riskHeader.riskId
                    + "&exposureRiskExpDate=" + getObjectValue("exposureRiskExpDate");
            break;

        case 'validateForOoseRiskAddtlExposure':
            var riskEffFromDate = riskAddtlExposureListGrid1.recordset("CRISKEFFECTIVEFROMDATE").value;
            var riskBaseRecordId = riskAddtlExposureListGrid1.recordset("CRISKBASERECORDID").value;
            var riskAddtlExpEffectiveFromDate = riskAddtlExposureListGrid1.recordset("CEFFECTIVEFROMDATE").value;
            url += "&riskId=" + policyHeader.riskHeader.riskId
                    + "&riskEffectiveFromDate=" + riskEffFromDate
                    + "&riskBaseRecordId=" + riskBaseRecordId
                    + "&effectiveFromDate=" + riskAddtlExpEffectiveFromDate;
            break;

        case 'getInitialValuesForOoseRiskAddtlExposure':
            var selectedDataGrid = getXMLDataForGridName("riskAddtlExposureListGrid");
            var riskEffFromDate = riskAddtlExposureListGrid1.recordset("CRISKEFFECTIVEFROMDATE").value;
            url += "&riskId=" + policyHeader.riskHeader.riskId+
                    "&riskAddtlExposureId=" + origRiskAddtlExposureId +
                    "&changeType=" + changePracticeType +
                    "&recordModeCode=" + selectedDataGrid.recordset("CRECORDMODECODE").value +
                    "&riskAddtlExpBaseRecordId=" + selectedDataGrid.recordset("CRISKADDTLEXPBASERECORDID").value +
                    "&riskEffectiveFromDate=" + riskEffFromDate +
                    "&riskEffectiveToDate=" + selectedDataGrid.recordset("CRISKEFFECTIVETODATE").value +
                    "&effectiveFromDate=" + selectedDataGrid.recordset("CEFFECTIVEFROMDATE").value +
                    "&effectiveToDate=" + selectedDataGrid.recordset("CEFFECTIVETODATE").value;
            break;
    }

    // initiate call
    var ajaxResponseHandler = "handleOn" + process.substr(0, 1).toUpperCase() + process.substr(1);
    new AJAXRequest("get", url, "", eval(ajaxResponseHandler), false);
}

function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'SAVE':
            setInputFormField("riskId", policyHeader.riskHeader.riskId);
            document.forms[0].process.value = "saveAllAdditionalExposure";
            alternateGrid_update('primaryExposureListGrid');
            alternateGrid_update('riskAddtlExposureListGrid');
            break;
        default:
            proceed = false;
    }
    return proceed;
}

//-----------------------------------------------------------------------------
// The call back function when validate change button is available
//-----------------------------------------------------------------------------
function handleOnValidateForOoseRiskAddtlExposure(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null))
                isOoseRiskAddtlExposureValid = false;
        }
    }
}

//-----------------------------------------------------------------------------
// The call back function when a row is added
//-----------------------------------------------------------------------------
function handleOnGetInitialValuesForAddRiskAddtlExposure(ajax) {
    commonHandleOnGetInitialValues(ajax);
}

function handleOnGetInitialValuesForOoseRiskAddtlExposure(ajax) {
    commonHandleOnGetInitialValues(ajax);
    if (changePracticeType == "changePracticeValue") {
        getObject("percentPractice").select();
    }
    else if (changePracticeType == "changePracticeDate") {
        getObject("effectiveToDate").select();
    }
}

//-----------------------------------------------------------------------------
// Handle the url of risk name column
//-----------------------------------------------------------------------------
function handlePostAddRow(table) {

}

function submitMultipleGrids() {
    return true;
}

function postAjaxRefresh(field, AjaxUrls) {
    if (AjaxUrls.indexOf('fieldId=territory')>0) {
        getObject("territoryLOVLABELSPAN").innerText = getObject("territory").innerText;
    }
    if (AjaxUrls.indexOf('fieldId=premiumClass')>0) {
        getObject("premiumClassLOVLABELSPAN").innerText = getObject("premiumClass").innerText;
    }
}

function getRiskAddtlExposureListGrid(){
    return "riskAddtlExposureListGrid";
}