
//-----------------------------------------------------------------------------
// Javascript file for maintainRiskDetail.jsp.
//
// (C) 2013 Delphi Technology, inc. (dti)
// Date:   Dec 25, 2013
// Author: xnie
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 01/07/2014       xnie        148083 - Initial version.
// 03/17/2014       xnie        152969 - 1) Modified handleOnSubmit() to close risk detail page directly when no any
//                                          changes on form.
//                                       2) Modified closePage() to delete new added row when user didn't save new
//                                          added row.
// 05/07/2014       xnie        154373 - 1) Modified handleOnLoad() to set isChanged to true when user tries to add a new
//                                          risk or oose a risk.
//                                       2) Modified handleOnSubmit() to set isChanged to false after user saves changes.
// 07/23/2014       xnie        156208 - 1) Modified handleOnLoad() to
//                                          a. Send ajax to get risk level additional information when user edit/view
//                                             risk detail information.
//                                          b. Call pageEntitlements.
//                                       2) Added sendAJAXRequest() for ajax call.
//                                       3) Added handleOnGetRiskAddlInfo() to set risk level additional information back
//                                          to page.
//                                       4) Modified find() to use getObjectValue to replace grid.recordset.value due to
//                                          this page has no form.
//                                       5) Modified selectProcedureCodeDone() to use isChanged = true to replace
//                                          grid.recordset.value due to this page has no form.
// 10/14/2014       kxiang      157730 - 1) Modified selectProcedureCodeDone() to add logical before set 'UPDATE_IND'
//                                          value.
// 12/30/2014       jyang       157750 - 1) Modified sendAJAXRequest() to encode riskTypeCode before append it to URL.
// 10/13/2015       tzeng       164679 - Modified handleOnLoad() to put message label value to parent window before
//                                       refresh page.
// 01/28/2016       wdang       169024 - Reverted changes of 164679.
// 08/15/2016       eyin        177410 - Modified handleOnChange(), Added change event for issue state field.
// 03/22/2017       wrong       180675 - Added case to set operation to undefined in closePage() when
//                                       UI tab style.
// 05/23/2017       lzhang      185079 - pass parameter when call getParentWindow()
// 07/20/2017       wrong       168374 - 1) Modify handleOnChange() to post ajax request for changing lov values when
//                                          change risk county or risk specialty fields.
//                                       2) Modified postAjaxRefresh() to add logic to display pcf territory and
//                                          pcf class field value.
// 11/09/2017       tzeng       187689 - Modified closePage() to set flag to skip processAutoSaveSubTab() for the case
//                                       which do not need to process.
// 11/02/2018       clm         195889 -  Grid replacement using getParentWindow and closeWindow
//-----------------------------------------------------------------------------
var savedB = "N";
var parentWindow = getParentWindow();

function handleOnLoad(){
    if (hasObject("saveCloseB") && getObjectValue("saveCloseB") == "Y") {
        setObjectValue("saveCloseB", "N");
        closeWindow(function () {
            if (isTabStyle()) {
                getParentWindow(true).clearCacheRowIds();
                getParentWindow(true).clearCacheTabIds();
            }
            parentWindow.refreshPage();
        });
    }

    if (hasObject("savedB") && getObjectValue("savedB") == "Y") {
        savedB = "Y";
    }

    if ((hasObject("slotOccupantB") && getObjectValue("slotOccupantB") == "Y")
            || (hasObject("newRiskB") && getObjectValue("newRiskB") == "Y")) {
        isChanged = true;
    }

    // This form has no grid, so commonEnableDisableGridDetailFields doesn't work.
    // We need to call commonEnableDisableFormFields to enable or disable form fields here.
    //commonEnableDisableFormFields("riskDetailDiv");

    maintainNoteImageForAllNoteFields();

    // If edit or view existed risk detail, call ajax to get risk additional information.
    if (!isChanged) {
        sendAJAXRequest("getRiskAddlInfo");
    }

    // Editable/Disable fields logic is handled by pageEntitlements.
    var functionExists = eval("window.pageEntitlements");
    if (functionExists) {
        pageEntitlements(false);
    }
}

function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'SAVE':
            setInputFormField("newSaveOption", "WIP");
            setInputFormField("slotOccupantB", "N");
            setInputFormField("newRiskB", "N");
            document.forms[0].process.value = "saveRiskDetail";
            setInputFormField("savedB", "Y");
            break;
        case 'SAVE_CLOSE':
            if (!isChanged) {
                proceed = false;
                closePage();
                return;
            }
            else {
                setInputFormField("saveCloseB", "Y");
                handleOnSubmit('SAVE');
            }
    }
    return proceed;
}

function handleOnButtonClick(btn) {
    switch (btn) {
        case 'CLOSE':
            closePage();
            break;
    }
}

//-----------------------------------------------------------------------------
// Validations
//-----------------------------------------------------------------------------
function handleOnChange(obj) {
    var isFundState = getObjectValue("isFundState");
    if (getObjectValue("UPDATE_IND") == "N") {
        setObjectValue("UPDATE_IND", "Y");
    }
    // Modify County Code
    if (obj.name == "riskCounty") {
        var riskCounty = getObjectValue(obj.name);
        var showWarning = getSysParmValue("PM_GIVE_COUNTY_WRNNG");
        var transCode = policyHeader.lastTransactionInfo.transactionCode
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
        parentWindow.validateTempCovgExist();
        loadIsFundStateValue(practiceStateCode);
    }
    // Set default value for pcf specialty when specialty changes.
    if (obj.name == "riskClass" && isFundState == 'Y') {
        var riskClass = getObjectValue(obj.name);
        setDefaultValueForPcfRiskClass(riskClass);
    }

    return true;
}

function find(fieldId) {
    if (fieldId == "procedureCodes") {
        var isReadOnly = 'Y';
        if (hasObject("EDIT_IND") && getObjectValue("EDIT_IND") == "Y") {
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
        if(getObjectValue("UPDATE_IND") == 'N') {
            setObjectValue("UPDATE_IND", "Y");
        }
        isChanged = true;
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

function closePage() {
    var divPopup = parentWindow.getDivPopupFromDivPopupControl(this.frameElement);
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

        closeWindow(function () {
            if (savedB == "Y") {
                parentWindow.refreshPage();
            }
            else {
                if (getObjectValue("newRiskB") == "Y") {
                    if(isTabStyle() && eval("getParentWindow(true).setSkipAutoSave")){
                        getParentWindow(true).setSkipAutoSave(true);
                    }
                    parentWindow.riskListGrid_deleterow();
                }
            }
        });
    }
}

function sendAJAXRequest(process) {
    // set url
    var url = "maintainRiskDetail.do?process=" + process +
            "&" + commonGetMenuQueryString("PM_RISK_DETAIL", "");

    switch (process) {
        case 'getRiskAddlInfo':
            url += "&riskId=" + getObject("riskDetailId").value +
                    "&recordModeCode=" + getObject("recordModeCode").value +
                    "&riskTypeCode=" + encodeURIComponent(getObject("riskTypeCode").value) +
                    "&officialRecordId=" + getObject("officialRecordId").value;
            break;
    }

    // initiate call
    var ajaxResponseHandler = "handleOn" + process.substr(0, 1).toUpperCase() + process.substr(1);
    new AJAXRequest("get", url, "", eval(ajaxResponseHandler), false);
}

function handleOnGetRiskAddlInfo(ajax) {
    commonHandleOnGetAddlInfo(ajax);
}