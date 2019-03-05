//-----------------------------------------------------------------------------
// Javascript file for selectProcedureCode.jsp.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   Jun 10, 2010
// Author: Dzhnag
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 06/28/2010       dzhang      move the disable checkbox logic to handleReadyStateReady,
//                              move the initial selected procedures status to checked in java class,
//                              and disable selectAll checkbox when page loaded.
// 11/30/2010       dzhang           Modify handleReadyStateReady()
// 11/28/2013       awu         149239 - Modified handleReadyStateReady to get isDoneAvailable from page.
//-----------------------------------------------------------------------------


//-----------------------------------------------------------------------------
// To handle the onload event.
// 1.Initial the records to be selected  which in risk tab's 'Procedure Codes' field
// 2.if page is readOnly mode, the Selected Only checkbox should be checked
//   and only display the selected records. If no record show empty table.
//-----------------------------------------------------------------------------
function handleOnLoad() {
    var procedureCodes = "";
    if (getObject("procedureCodes")) {
        procedureCodes = getObject("procedureCodes").value;
    }
    if (getObjectValue("isDoneAvailable") == 'N') {
        if (hasObject("filterSelectedOnly")) {
            getObject("filterSelectedOnly").checked = true;
            procedureListGrid_filter("CSELECT_IND = -1");
        }
    }
    if (isEmptyRecordset(procedureListGrid1.recordset)) {
        hideEmptyTable(getTableForXMLData(procedureListGrid1));
    }
    else {
        showNonEmptyTable(getTableForXMLData(procedureListGrid1));
    }

    // disable selectAll checkbox
    if (hasObject("chkCSELECT_ALL")) {
        getObject("chkCSELECT_ALL").disabled = true;
    }
}

function handleOnButtonClick(btn) {
    switch (btn) {
        case "DONE":
            var selectedRecords = procedureListGrid1.documentElement.selectNodes("//ROW[(CSELECT_IND = '-1')]");
            var size = selectedRecords.length;
            var selectedProcedures = "";
            for (var i = 0; i < size; i++) {
                var currentRecord = selectedRecords.item(i);
                var procCode = currentRecord.selectNodes("CPROCEDURECODE")(0).text;
                if (i == 0) {
                    selectedProcedures = procCode;
                }
                else {
                    selectedProcedures = selectedProcedures + "," + procCode;
                }
            }
            window.frameElement.document.parentWindow.selectProcedureCodeDone(selectedProcedures);

            closeThisDivPopup(true);
            break;
        case "FILTER":
            filterProcedureCode();
            break;
        case "CLEAR":
            clearFilter();
            break;
    }
}

//-----------------------------------------------------------------------------
// filter the procedure list grid data, 
// 1.Deselected the 'Selected Only' checkbox.
// 2.Filter and display the procedure list accrodingly. If no record, hidden the table.
//-----------------------------------------------------------------------------
function filterProcedureCode() {

    if (getObject("filterSelectedOnly").checked) {
        getObject("filterSelectedOnly").checked = false;
    }

    var procedureCodeFilter = getObjectValue('filterProcedureCode');
    var longDescriptionFilter = getObjectValue('filterLongDescription');
    var premiumClassFilter = getObjectValue('filterPremiumClass');
    var filterStr = "";

    if (!isEmpty(procedureCodeFilter)) {
        filterStr = filterStr + "CPROCEDURECODE[contains(.,'" + procedureCodeFilter.toUpperCase() + "')]";
    }
    if (!isEmpty(longDescriptionFilter)) {
        if (!isEmpty(filterStr)) {
            filterStr = filterStr + " and ";
        }
        filterStr = filterStr + "CLONGDESCRIPTION[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'" + longDescriptionFilter.toLowerCase() + "')]";
    }
    if (!isEmpty(premiumClassFilter)) {
        if (!isEmpty(filterStr)) {
            filterStr = filterStr + " and ";
        }
        filterStr = filterStr + "CPREMIUMCLASS[contains(.,'" + premiumClassFilter.toUpperCase() + "')]";
    }

    setTableProperty(eval("procedureListGrid"), "selectedTableRowNo", null);
    procedureListGrid_filter(filterStr);

    if (isEmptyRecordset(procedureListGrid1.recordset)) {
        hideEmptyTable(getTableForXMLData(procedureListGrid1));
    }
    else {
        showNonEmptyTable(getTableForXMLData(procedureListGrid1));
    }

}

//-----------------------------------------------------------------------------
// To handle clear filter event and system should display all the procedure code
//-----------------------------------------------------------------------------
function clearFilter() {
    getObject("filterProcedureCode").value = "";
    getObject("filterLongDescription").value = "";
    getObject("filterPremiumClass").value = "";
    filterProcedureCode();
}

//-----------------------------------------------------------------------------
// To handle onclick event for the select checkbox.
// In order to sync the selected records, system set the update_ind to 'Y'.
//-----------------------------------------------------------------------------
function userRowchange(obj) {
    var objName = obj.name;
    if (objName == 'chkCSELECT_IND') {
        if (procedureListGrid1.recordset("UPDATE_IND").value == "N") {
            procedureListGrid1.recordset("UPDATE_IND").value = "Y";
        }
        setTableProperty(procedureListGrid1, "gridDataChange", true);
        if (window.postOnChange) {
            postOnChange(field);
        }
    }
}

//-----------------------------------------------------------------------------
// To handle onclick event for the selected only checkbox on the filter form.
// 1.Clear the filter string in 'Procedure Code', 'Long Description', 'Premium Class'
// 2.Display the selected records(Selected status) or all the records(Deselected status)
//-----------------------------------------------------------------------------
function handleOnChange(obj) {
    if (obj.name == "filterSelectedOnly") {

        getObject("filterProcedureCode").value = "";
        getObject("filterLongDescription").value = "";
        getObject("filterPremiumClass").value = "";

        var ischecked = getObject("filterSelectedOnly").checked;
        if (ischecked) {
            setTableProperty(eval("procedureListGrid"), "selectedTableRowNo", null);
            procedureListGrid_filter("CSELECT_IND = -1");
        }
        else {
            setTableProperty(eval("procedureListGrid"), "selectedTableRowNo", null);
            procedureListGrid_filter("CSELECT_IND = -1 or CSELECT_IND = 0");
        }


        if (isEmptyRecordset(procedureListGrid1.recordset)) {
            hideEmptyTable(getTableForXMLData(procedureListGrid1));
        }
        else {
            showNonEmptyTable(getTableForXMLData(procedureListGrid1));
        }
    }
}

//-----------------------------------------------------------------------------
// If is read only mode, disable all the checkbox.
//-----------------------------------------------------------------------------
function handleReadyStateReady() {
    if (getObjectValue("isDoneAvailableInd") == 'false' && (hasObject("chkCSELECT_IND"))) {
        var procArry = getObject("chkCSELECT_IND");
        if ((!procArry.length) || procArry.length == 0) {
            procArry.disabled = true;
        }
        else {
            for (var i = 0; i < procArry.length; i++) {
                procArry[i].disabled = true;
            }
        }
    }
}
