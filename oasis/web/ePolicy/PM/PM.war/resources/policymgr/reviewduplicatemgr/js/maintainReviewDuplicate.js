//-----------------------------------------------------------------------------
// Javascript file for processQuickQuote.js.
//
// (C) 2016 Delphi Technology, inc. (dti)
// Date:   June 28, 2016
// Author: ssheng
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 06/28/2016        ssheng      164927 - Created this action class for Quote Import enhancement.
// 03/30/2017        lzhang      184424 - Override submitMultipleGrids() instead of submitForm()
// 12/05/2018        xjli        195889 - Reflect grid replacement project changes.
//-----------------------------------------------------------------------------

var checkFlag = 0;
function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'SAVE':
            commonOnSubmit('saveReviewDuplicate', true, true, true, true);
            break;
        case 'CLOSE':
            if (isEmpty(getObjectValue("workflowState"))) {
                closeWindow();
                break;
            }
            else {
                if (confirm(getMessage("pm.reviewDuplicate.confirmation.info"))) {
                    commonOnSubmit('saveAllToCIS', true, true, true, true);
                }
                else {
                    commonOnSubmit('closePage', true, true, true, true);
                }
            }
            break;
    }
}

function closeWindow() {
    getParentWindow().refreshPage();
}

//-----------------------------------------------------------------------------
// Since there are two grid in this page and system only checks the current grid (regionalTeamMemberListGrid) in commonOnSubmit(),
// we should call commonValidateGrid(regionalTeamListGrid) to ensure this grid will be validated.
//-----------------------------------------------------------------------------
function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'saveReviewDuplicate':
            document.forms[0].process.value = "saveReviewDuplicate";
            break;
        case 'saveAllToCIS':
            document.forms[0].process.value = "saveAllToCIS";
            alternateGrid_update(getRosterRiskGridId());
            break;
        case 'closePage':
            document.forms[0].process.value = "closePage";
            break;
        default:
            proceed = false;
    }
    return proceed;
}

//-----------------------------------------------------------------------------
// Instruct submit data for multiple grids
//-----------------------------------------------------------------------------
function submitMultipleGrids() {
    return true;
}

//-----------------------------------------------------------------------------
// disable Roster Risk grid checkbox
//-----------------------------------------------------------------------------
function disableForRosterRiskTable() {
    var tbl = getTableForXMLData(getXMLDataForGridName(getRosterRiskGridId()));

    // The length of available records for the current grid page
    var rowLength = tbl.rows.length - 1;
    var row = 0;

    // disable Roster Risk grid checkbox.
    while (row < rowLength && !rosterRiskListGrid1.recordset.eof) {
        if (rosterRiskListGrid1.recordset.Fields("CCISSAVEDB").value == 'Y') {
            var rosterRiskCellAddToCIS = $(tbl.rows[row + 1]).find("td")[8];
            rosterRiskCellAddToCIS.all[0].disabled = 'true';
        }
        if(getObjectValue("noDuplicate") == 'Y' && rosterRiskListGrid1.recordset.Fields("CCISSAVEDB").value == "N"){
            rosterRiskListGrid1.recordset.Fields("CADDTOCIS").value = "-1";
            rosterRiskListGrid1.recordset.Fields("UPDATE_IND").value = "Y";
        }
        row ++;
        next(rosterRiskListGrid1);
    }
    first(rosterRiskListGrid1);
}

//-----------------------------------------------------------------------------
// disable CIS Duplicate grid checkbox
//-----------------------------------------------------------------------------
function disableForCISDupTable() {
    var tbl = getTableForXMLData(getXMLDataForGridName(getCISDuplicateGridId()));

    // The length of available records for the current grid page
    var rowLength = tbl.rows.length - 1;
    var row = 0;

    // disable CIS Duplicate grid checkbox
    while (row < rowLength && !cisDuplicateListGrid1.recordset.eof) {
        if (cisDuplicateListGrid1.recordset.Fields("CCISSAVEDB").value == 'Y') {
            var cisDuplicateCellAddToCIS = $(tbl.rows[row + 1]).find("td")[8];
            cisDuplicateCellAddToCIS.all[0].disabled = 'true';
        }
        row ++;
        next(cisDuplicateListGrid1);
    }
    first(cisDuplicateListGrid1);
}

function rosterRiskListGrid_selectRow(id) {
    if(rosterRiskListGrid1.recordset.Fields("CADDTOCIS").value == -1 && cisDuplicateListGrid1.recordset.Fields("CPOLICYLOADEVENTDETAILID").value == rosterRiskListGrid1.recordset.Fields("CPOLICYLOADEVENTDETAILID").value) {
        cisDuplicateListGrid1.recordset.Fields("CUSECISRECORD").value = '0';
    }

    filterCISDuplicateData(rosterRiskListGrid1);
    var testCode = 'getTableProperty(getTableForGrid(\"' + getCISDuplicateGridId() + '\"), "isUserReadyStateReadyComplete")'
            + '&&!getTableProperty(getTableForGrid(\"' + getCISDuplicateGridId() + '\"), "filtering")';
    var callbackCode = 'disableForCISDupTable();';
    executeWhenTestSucceeds(testCode, callbackCode, 50);
}

function cisDuplicateListGrid_selectRow(id) {
    var entity_id = cisDuplicateListGrid1.recordset.Fields("CENTITYID").value;

    if (checkFlag == 1 && rosterRiskListGrid1.recordset.Fields("CADDTOCIS").value == -1) {
        first(cisDuplicateListGrid1);
        while(!cisDuplicateListGrid1.recordset.eof) {
            cisDuplicateListGrid1.recordset.Fields("CUSECISRECORD").value = '0';
            next(cisDuplicateListGrid1);
        }

        first(cisDuplicateListGrid1);
        checkFlag = 0;
    }

    if(cisDuplicateListGrid1.recordset.Fields("CPOLICYLOADEVENTDETAILID").value == rosterRiskListGrid1.recordset.Fields("CPOLICYLOADEVENTDETAILID").value && cisDuplicateListGrid1.recordset.Fields("CUSECISRECORD").value == -1) {
        rosterRiskListGrid1.recordset.Fields("CADDTOCIS").value = '0';

        first(cisDuplicateListGrid1);
        while(!cisDuplicateListGrid1.recordset.eof) {
            if(cisDuplicateListGrid1.recordset.Fields("CENTITYID").value != entity_id){
                cisDuplicateListGrid1.recordset.Fields("CUSECISRECORD").value = '0';
            }
            next(cisDuplicateListGrid1);
        }

        first(cisDuplicateListGrid1);
        while(cisDuplicateListGrid1.recordset.Fields("CENTITYID").value != entity_id) {
            next(cisDuplicateListGrid1);
        }
    }
    checkFlag = 0;
}
//-----------------------------------------------------------------------------
// Filter component data by coverage base record Id
//-----------------------------------------------------------------------------
function filterCISDuplicateData(rosterRiskListGrid) {
    checkFlag = 1;
    var policyLoadEventDetailId = rosterRiskListGrid.recordset("CPOLICYLOADEVENTDETAILID").value;
    setTableProperty(eval(getCISDuplicateGridId()), "selectedTableRowNo", null);
    cisDuplicateListGrid_filter("CPOLICYLOADEVENTDETAILID=" + policyLoadEventDetailId);

    var cisDupXmlData = getXMLDataForGridName(getCISDuplicateGridId());
    if (isEmptyRecordset(cisDupXmlData.recordset)) {
        hideEmptyTable(getTableForXMLData(cisDupXmlData));
    }
    else {
        showNonEmptyTable(getTableForXMLData(cisDupXmlData));
    }
}

function getRosterRiskGridId() {
    return "rosterRiskListGrid";
}

function getCISDuplicateGridId() {
    return "cisDuplicateListGrid";
}

function handleOnLoad() {
    disableForRosterRiskTable();
}
