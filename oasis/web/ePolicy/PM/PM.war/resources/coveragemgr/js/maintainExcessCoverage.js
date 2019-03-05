//-----------------------------------------------------------------------------
// JavaScript file for risk summary.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:
// Author: 
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 09/13/2010       wfu         111776: Replaced hardcode string with resource definition
//-----------------------------------------------------------------------------

function priorCarrierListGrid_setInitialValues() {
    var termEffectiveFromDate = getObjectValue("termEffectiveFromDate");
    var termEffectiveToDate = getObjectValue("termEffectiveToDate");
    var url = getAppPath() + "/coveragemgr/maintainExcessCoverage.do?"
        + commonGetMenuQueryString() + "&process=getInitialValuesForPriorCarrier"
        + "&termEffectiveFromDate=" + termEffectiveFromDate + "&termEffectiveToDate=" + termEffectiveToDate;

    new AJAXRequest("get", url, '', setInitialValuesForPriorCarrier, false);
}
function setInitialValuesForPriorCarrier(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }
            var selectedDataGrid = getXMLDataForGridName("priorCarrierListGrid");
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                setRecordsetByObject(selectedDataGrid, oValueList[0]);
            }
        }
    }
}
function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'SAVE':
            document.forms[0].process.value = "saveAllCarrier";
            break;
        default:
            proceed = false;
    }
    return proceed;
}

function handleOnBlur(field) {
    var fieldName = field.name;
    if (fieldName == 'limitIncident' || fieldName == 'excessLimit' || fieldName == 'attachmentPoint') {
        var fieldValue = field.value;
        if (!isSignedFloat(fieldValue, true)) {
            alert(getMessage("pm.maintainExcessCoverage.handleOnBlur.error"));
            field.value = "";
            return false;
        }
    }
    return true;
}