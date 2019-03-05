//-----------------------------------------------------------------------------
//  Description: agent select agent contract
//  Revision Date     Revised By      Description
//  10/16/2018        dzhang          Issue 195835: grid replacement
//  ---------------------------------------------------------------------------

var SELECT_CONTRACT_GRID_ID = "selContractListGrid";
var hasErrorMessages = "";
//-----------------------------------------------------------------------------
// handle on select contract
//-----------------------------------------------------------------------------
function handleOnLoad() {
    $.when(dti.oasis.grid.getLoadingPromise(SELECT_CONTRACT_GRID_ID)).then(function () {
        handleOnSearchContract();
    });
}

//-----------------------------------------------------------------------------
// handle on button click
//-----------------------------------------------------------------------------
function handleOnButtonClick(btn) {
    switch (btn) {
        case 'SEARCH':
            var searchBy = getObjectValue("searchBy");
            var searchString = getObjectValue("searchString");
            if (searchBy == "") {
                alert(getMessage("ci.agentmgr.selectcontract.searchByRequired"));
                return;
            }
            if (searchString == "") {
                alert(getMessage("ci.agentmgr.selectcontract.searchStringRequired"));
                return;
            }
            handleOnSearchContract();
            break;
        case 'SELECT':
            var selectedDataGrid = getXMLDataForGridName(getCurrentlySelectedGridId());
            var licenseId = selectedDataGrid.recordset("ID").value;
            var licenseNumber = selectedDataGrid.recordset("CLICENSENUMBER").value;
            handleOnSelectContract(btn, licenseId, licenseNumber);
            break;
        case 'CANCEL':
            handleOnSelectContract(btn, "", "");
            break;
    }
    return true;
}

function handleOnSelectContract(action, licenseId, licenseNumber) {
    closeWindow(function () {
        var parentWindow = getParentWindow();
        if (parentWindow && parentWindow.handleOnSelectContract != null) {
            parentWindow.handleOnSelectContract(action, licenseId, licenseNumber);
        }
    });
}

//select first row that matches the search string
function handleOnSearchContract() {
    var searchBy = getObjectValue("searchBy");
    var searchString = getObjectValue("searchString");
    if (searchBy != "" && searchString != "") {
        var searchColumn = "";
        var selectContractDataGrid = getXMLDataForGridName(SELECT_CONTRACT_GRID_ID);
        if (searchBy == "NUM") {
            //search by contract number
            searchColumn = "CLICENSENUMBER";
        }
        else {
            //search by name
            searchColumn = "CAGENTNAME";
        }

        var findContractId = "";
        //find match contract number
        first(selectContractDataGrid);
        while (!selectContractDataGrid.recordset.eof) {
            var searchColumnValue = selectContractDataGrid.recordset(searchColumn).value ;
            if (searchColumnValue.toUpperCase().indexOf(searchString.toUpperCase()) == 0) {
                findContractId = selectContractDataGrid.recordset("ID").value;
                break;
            }
            next(selectContractDataGrid);
        }
        //highlight the row
        if (findContractId == "") {
            alert(getMessage("ci.agentmgr.selectcontract.searchStringNotFound"));
            selectFirstRowInGrid(SELECT_CONTRACT_GRID_ID);
            return;
        }
        else {
            selectRowById(SELECT_CONTRACT_GRID_ID, findContractId);
        }
    }
}