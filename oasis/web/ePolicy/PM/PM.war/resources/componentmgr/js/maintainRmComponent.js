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
// 03/30/2017       lzhang      184424 - Override submitMultipleGrids() instead of submitForm()
//-----------------------------------------------------------------------------
function handleOnButtonClick(btn) {
    switch (btn) {
        case 'ADDEVENT':
            commonAddRow("processingEventListGrid");
            break;
        case 'DELETEEVENT':
            commonDeleteRow("processingEventListGrid");
            break;
        default:break;
    }
}

function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'SAVE':
            document.forms[0].process.value = "saveAllProcessingEvent";
            break;
        case 'PROCESS':
            if (isGridDataChanged("processingEventListGrid")) {
                alert(getMessage("pm.processingRmComponent.unsave.data"));
                proceed = false;
            }
            else {
                if (confirm(getMessage("pm.processingRmComponent.process.confirmation"))) {
                    setInputFormField("processRmtIndicator", "Y");
                }
                var pmRmProcessMstrId = processingEventListGrid1.recordset("ID").value;
                document.forms[0].process.value = "processEvent";
                setInputFormField("pmRmProcessMstrId", pmRmProcessMstrId);
            }
            break;

        default:
            proceed = false;
    }
    return proceed;
}

function getParentGridId() {
    return "processingEventListGrid";
}

function getChildGridId() {
    return "processingDetailListGrid";
}

function processingEventListGrid_selectRow(id) {
    // If process_status is "INPROGRESS", make all fields are editable.
    var status = processingEventListGrid1.recordset("CPROCESSSTATUS").value;
    if (status == "INPROGRESS") {
        enableDisableField(getObject("transactionEffectiveDate"), false);
        enableDisableField(getObject("effectiveFromDate"), false);
        enableDisableField(getObject("effectiveToDate"), false);
    }
    else {
        enableDisableField(getObject("transactionEffectiveDate"), true);
        enableDisableField(getObject("effectiveFromDate"), true);
        enableDisableField(getObject("effectiveToDate"), true);
    }
    // Filter out the processing detail for the selected event.
    setTableProperty(eval("processingDetailListGrid"), "selectedTableRowNo", null);
    processingDetailListGrid_filter("CPMRMPROCESSMSTRID = " + id);
    var memberXmlData = getXMLDataForGridName("processingDetailListGrid");
    if (isEmptyRecordset(memberXmlData.recordset)) {
        hideEmptyTable(getTableForXMLData(memberXmlData));
    }
    else {
        showNonEmptyTable(getTableForXMLData(memberXmlData));
        reconnectAllFields(document.forms[0]);
    }
}

function processingDetailListGrid_selectRow(id) {
    return true;
}

function processingEventListGrid_setInitialValues() {
    var url = getAppPath() + "/componentmgr/maintainRmComponent.do?"
        + commonGetMenuQueryString() + "&process=getInitialValuesForProcessingEvent";

    new AJAXRequest("get", url, '', setInitialValuesForProcessingEvent, false);
}

function setInitialValuesForProcessingEvent(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                /* no default value found */
                return;
            }
            var selectedDataGrid = getXMLDataForGridName("processingEventListGrid");
            /* Parse xml and get inital values(s) */
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                setRecordsetByObject(selectedDataGrid, oValueList[0]);
                // Handle the Save button.
                if(getObject("isSaveAvailable") && getObject("isSaveAvailable").value == 'N'){
                    setInputFormField("isSaveAvailable", "Y");
                    var functionExists = eval("window.pageEntitlements");
                    if (functionExists) {
                        pageEntitlements(false);
                    }
                }
            }
        }
    }
}

//-----------------------------------------------------------------------------
// Instruct submit data for multiple grids
//-----------------------------------------------------------------------------
function submitMultipleGrids() {
    return true;
}