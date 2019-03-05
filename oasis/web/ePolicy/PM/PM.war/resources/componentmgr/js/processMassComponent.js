//-----------------------------------------------------------------------------
// Javascript file for processMassComponent.jsp.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   June 29, 2010
// Author: syang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 06/29/2010       syang       108782 - Passed percentValueB to indicate the component sign is $ or %.
// 01/03/2013       tcheng      137076 - Modified handleOnLoad(),componentListGrid_selectRow(),handleOnButtonClick()
//                                       to make sure no js error.
// 07/23/2015       kxiang      163584 - 1. When action is move and component is not a New doctor component or a
//                                          cycled component, component value will be set to 0.
//                                       2. When action is not move and it's not a New doctor component or a cycled
//                                          component, component value will be set to original low value
//                                       3. Modified componentListGrid_setInitialValues to add cycledB.
// 03/20/2017       eyin        180675 - Made change to open view Validation Error popup if error exists.
// 05/23/2017       lzhang      185079 - pass parameter when call getParentWindow()
// 12/13/2017       eyin        190085 - Modified handleOnLoad(), to refresh page immediately if NO validation error
//                                       exists in tab style.
//-----------------------------------------------------------------------------
var productCovComponentId;
var shortDescription;
var componentTypeCode;
var componentSign;
var code;
var lowValue;
var highValue;
var percentValueB;
var cycledB;

function handleOnLoad() {
    if (isEmptyRecordset(componentListGrid1.recordset)) {
        if(hasObject('PM_COMP_UPDATE_DELETE')){
            hideShowField(getObject("PM_COMP_UPDATE_DELETE"), true);
        }
        if(hasObject('PM_COMP_UPDATE_APPLY')){
            hideShowField(getObject("PM_COMP_UPDATE_APPLY"), true);
        }
    }
    if(isTabStyle()){
        if(applyResult == '-1' || applyResult == '0'){
            var policyNo = getParentWindow(true).policyHeader.policyNo;
            var path = getAppPath() + "/transactionmgr/viewValidationError.do?process=loadAllValidationError" +
                    "&policyNo=" + policyNo;
            getOpenCtxOfDivPopUp().openDivPopup("", path, true, true, null, null, 800, 520, "", "", "", false);
        }else if(applyResult == '1'){
            /*
             * issue 190085
             * Do NOT 'apply' automatically once user tries to switch grid row/sub-tab
             * After user clicks Apply button, need to refresh page immediately if NO validation error exists.
             */
            alert(getMessage("pm.process.mass.component.apply.info"));
            getParentWindow().refreshPage();
        }
    }
}

function isNeedToRefreshParentB(){
    return applyResult == "1";
}

function componentListGrid_setInitialValues() {
    var url = getAppPath() + "/componentmgr/processComponent.do?process=getInitialValuesForAddProcessComponent"
        + "&productCovComponentId=" + productCovComponentId + "&code=" + code + "&componentSign=" + componentSign
        + "&shortDescription=" + shortDescription + "&componentTypeCode=" + componentTypeCode
        + "&lowValue=" + lowValue + "&highValue=" + highValue + "&percentValueB=" + percentValueB + "&cycledB=" + cycledB + "&date=" + new Date();

    new AJAXRequest("get", url, '', setInitialValuesForAddProcessComponent, false);
}

//-----------------------------------------------------------------------------
// Set Initial value for component
//-----------------------------------------------------------------------------
function setInitialValuesForAddProcessComponent(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                /* no default value found */
                return;
            }

            var selectedDataGrid = getXMLDataForGridName("componentListGrid");
            /* Parse xml and get inital values(s) */
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                setRecordsetByObject(selectedDataGrid, oValueList[0]);
            }
        }
    }
}

function componentListGrid_selectRow(id) {

    if (!isEmptyRecordset(componentListGrid1.recordset)) {
        if(hasObject('PM_COMP_UPDATE_DELETE')){
            hideShowField(getObject("PM_COMP_UPDATE_DELETE"), false);
        }
        if(hasObject('PM_COMP_UPDATE_APPLY')){
            hideShowField(getObject("PM_COMP_UPDATE_APPLY"), false);
        }
    }

    // If the lowValue equals highValue, system should make component value disabled.
    if (componentListGrid1.recordset("CORIGLOWVALUE").value == componentListGrid1.recordset("CORIGHIGHVALUE").value) {
        getObject("componentValue").disabled = true;
    }
    else {
        getObject("componentValue").disabled = false;
    }
}

function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'ADDCOMP':
            selectComponent();
            break;
        case 'DELETE':
            if (isEmptyRecordset(componentListGrid1.recordset)) {
                if(hasObject('PM_COMP_UPDATE_DELETE')){
                    hideShowField(getObject("PM_COMP_UPDATE_DELETE"), true);
                }
                if(hasObject('PM_COMP_UPDATE_APPLY')){
                    hideShowField(getObject("PM_COMP_UPDATE_APPLY"), true);
                }
            }
            break;
        case 'RATE':
            performRating();
            break;
        case 'PREMIUM':
            viewPremium();
            break;
        case 'CLOSE':
            closePage();
            break;
    }
}
function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'APPLY':
            document.forms[0].process.value = "applyMassComponent";
            showProcessingDivPopup();
            break;
        default:
            proceed = false;
    }
    return proceed;
}

function hasDataChanged(){
    if (!isEmptyRecordset(componentListGrid1.recordset))
        return true;
    else
        return false;
}

function selectComponent() {
    var productCoverageCode = getObjectValue("productCoverageCode");
    var coverageBaseRecordId = getObjectValue("coverageBaseRecordId");
    var coverageBaseEffectiveFromDate = getObjectValue("coverageBaseEffectiveFromDate");
    var riskId = getObjectValue("riskId");
    var policyNo = getObjectValue("policyNo");
    var coverageId = getObjectValue("coverageId");
    var url = getAppPath() + "/componentmgr/selectComponent.do?"
        + commonGetMenuQueryString() + "&productCoverageCode=" + productCoverageCode
        + "&coverageBaseRecordId=" + coverageBaseRecordId
        + "&coverageBaseEffectiveFromDate=" + coverageBaseEffectiveFromDate
        + "&riskId=" + riskId + "&coverageId=" + coverageId;
    if(isTabStyle()){
        url += "&subFrameId=" + getParentWindow(true).subFrameId;
    }
    var divPopupId = getOpenCtxOfDivPopUp().openDivPopup("", url, true, true, "", "", "", "", "", "", "", false);
}

//-----------------------------------------------------------------------------
// Add one component
//-----------------------------------------------------------------------------
function addOneComponent(component, showMessage) {
    productCovComponentId = component.PRODUCTCOVCOMPONENTID;
    code = component.CODE;
    componentSign = component.COMPONENTSIGN;
    shortDescription = component.SHORTDESCRIPTION;
    componentTypeCode = component.COMPONENTTYPECODE;
    lowValue = component.LOWVALUE;
    highValue = component.HIGHVALUE;
    percentValueB = component.PERCENTVALUEB;
    cycledB = component.CYCLEDB;
    // Add component and set default values
    commonAddRow("componentListGrid");
    return true;
}

//-----------------------------------------------------------------------------
// Add components and their dependent componets
//-----------------------------------------------------------------------------
function addComponents(oCompList, showMessage) {
    try {
        //begin inserting multiple rows
        beginAddMultipleRow("componentListGrid");
        var compSize = oCompList.length;

        // validate exist component type first
        for (var i = 0; i < compSize; i++) {
            var comp = oCompList[i];
            if (!componentDulplicateValidations(comp, true)) {
                return false;
            }
        }

        var validCompList = new Array();
        var addedCount = 0;
        for (var i = 0; i < compSize; i++) {
            var comp = oCompList[i];
            // Check if added one component
            if (addOneComponent(comp, showMessage)) {
                validCompList[addedCount] = comp;
                addedCount ++;
            }
        }
    }
    finally {
        //end inserting multiple rows
        endAddMultipleRow("componentListGrid");
    }
}

//-----------------------------------------------------------------------------
// Dulplicated component validations
//-----------------------------------------------------------------------------
function componentDulplicateValidations(oComponent, showMessage) {
    if (!getTableProperty(getTableForGrid("componentListGrid"), "hasrows")) {
        return true;
    }
    var rowIndex = 0;
    first(componentListGrid1);
    while (!componentListGrid1.recordset.eof) {
        if (oComponent.PRODUCTCOVCOMPONENTID == componentListGrid1.recordset("ID").value) {
            if (showMessage == true) {
                var parms = new Array(oComponent.SHORTDESCRIPTION);
                handleError(getMessage("pm.process.mass.component.duplicated.error", parms), "", "");
            }
            return false;
        }

        rowIndex ++;
        next(componentListGrid1);
    }

    return true;
}

//-----------------------------------------------------------------------------
// Set the component value to zero when select "Remove" from action list.
//-----------------------------------------------------------------------------
function handleOnChange(field) {
    if (field.name == "componentAction" && field.value == 'M' &&
        componentListGrid1.recordset("CCYCLEDB").value != 'Y' && componentListGrid1.recordset("CCODE").value != 'NEWDOCTOR') {
        componentListGrid1.recordset("CCOMPONENTVALUE").value = 0;
    }
    if (field.name == "componentValue" && componentListGrid1.recordset("CCOMPONENTACTION").value == 'M') {
        componentListGrid1.recordset("CCOMPONENTVALUE").value = 0;
    }
    if (field.name == "componentAction" && field.value != 'M' &&
        (componentListGrid1.recordset("CORIGLOWVALUE").value == componentListGrid1.recordset("CORIGHIGHVALUE").value) &&
         componentListGrid1.recordset("CCYCLEDB").value != 'Y' && componentListGrid1.recordset("CCODE").value != 'NEWDOCTOR') {
        componentListGrid1.recordset("CCOMPONENTVALUE").value = componentListGrid1.recordset("CORIGLOWVALUE").value;
    }
}

//-----------------------------------------------------------------------------
// Perform Rating
//-----------------------------------------------------------------------------
function performRating() {

    var url = getAppPath() + "/componentmgr/processComponent.do";
    postAjaxSubmit(url, "performRating", false, false, performRatingDone);
}

function performRatingDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }
            var selectedDataGrid = getXMLDataForGridName("componentListGrid");
            /* Parse xml and get inital values(s) */
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                if (oValueList[0]["RATE"] == "FAILED") {
                    handleError(getMessage("pm.process.mass.component.rate.error"));
                }
                else {
                    handleError(getMessage("pm.process.mass.component.rate.info"));
                }
            }
        }
    }
}

function closePage() {
    var divPopup = window.frameElement.document.parentWindow.getDivPopupFromDivPopupControl(this.frameElement);
    if (divPopup) {
        var functionExists = eval("window.commonIsOkToChangePages");
        if (functionExists) {
            var isOkToProceed = commonIsOkToChangePages("DIV_PUP", "");
            if (!isOkToProceed) {
                return;
            }
        }

        if (window.isOkToChangePages) {
            if (!isOkToChangePages("DIV_PUP", "")) {
                return;
            }
        }
        showProcessingDivPopup();
        window.frameElement.document.parentWindow.closeDiv(divPopup);
        window.frameElement.document.parentWindow.refreshPage();
    }
}