//-----------------------------------------------------------------------------
// Javascript file for maintainCOIRenewalEvent.jsp.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   Jun 21, 2010
// Author: Dzhang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 07/05/2010       dzhang      Renamed this file.
// 07/06/2010       dzhang      Changed funtion handleOnChange().
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
// Load the event detail records by the selected event record's id.
//-----------------------------------------------------------------------------
function loadCOIRenewalDetails(renewalEventId) {
    var frameObj = getObject("iframeCoiEventDetails");

    if (typeof(frameObj) == "undefined") {
        return;
    }
    var url = getAppPath() + "/transactionmgr/coirenewalmgr/maintainCoiRenewalEvent.do?"
        + commonGetMenuQueryString() + "&process=loadAllCoiRenewalEventDetail"
        + "&coiRenewalEventId=" + renewalEventId;
    frameObj.src = url;
}

//-----------------------------------------------------------------------------
// To handle the select event list grid row event.
// Load the event detail records by the selected event record's id.
//-----------------------------------------------------------------------------
function COIRenewalEventListGrid_selectRow(id) {
    loadCOIRenewalDetails(id);
}

//-----------------------------------------------------------------------------
// To handle the button click event.
//-----------------------------------------------------------------------------
function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'SEARCH':
            document.forms[0].process.value = "loadAllCoiRenewalEvent";
            submitForm(true);
            break;
        case 'FIND':
            var policyNoFilter = getObjectValue("policyNoFilter");
            var filterStr = "";

            if (!isEmpty(policyNoFilter)) {
                filterStr = filterStr + "CPOLICYNO[starts-with(.,'" + policyNoFilter.toUpperCase() + "')]";
            }

            setTableProperty(eval("COIRenewalDetailListGrid"), "selectedTableRowNo", null);
            COIRenewalDetailListGrid_filter(filterStr);

            if (isEmptyRecordset(COIRenewalDetailListGrid1.recordset)) {
                hideEmptyTable(getTableForXMLData(COIRenewalDetailListGrid1));
                alert(getMessage("pm.coiRenewal.noPolicy.found.error"));
            }
            else {
                showNonEmptyTable(getTableForXMLData(COIRenewalDetailListGrid1));
            }
            break;
    }
}

//-----------------------------------------------------------------------------
// To handle the on change event.
// Set the value in "End Search Date" to the value entered in "Start Search Date" field.
//-----------------------------------------------------------------------------
function handleOnChange(obj) {
    if (obj.name == "startSearchDateFilter") {
        if (datemaskclear()) {
            getObject("endSearchDateFilter").value = obj.value;
        }
    }
}