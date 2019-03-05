//-----------------------------------------------------------------------------
// Functions For Special Handling page
// Author:
// Date:
// Modifications:
//-----------------------------------------------------------------------------
//  04/15/2010       Kenney      Issue#106087: Load initial values when adding special handling
//  08/11/2010       Ldong       Issue#110376: Add Note function
//  12/03/2012       Elvin       Issue 139619.
//  09/13/2013       kshen       Issue 144341.
//  12/06/2013       Parker      Issue 148036 Refactor maintainRecordExists code to make one call per subsystem to the database.
//  10/10/2018       dzou        Grid replacement
//-----------------------------------------------------------------------------
var gridID = "specialHandlingListGrid";
validateGridId = "specialHandlingListGrid";
currentlySelectedGridId = gridID;
function handleOnChange(obj) {
    isChanged = true;
    gridDataChange = true;
    if (window.postOnChange) {
        postOnChange(obj);
    }
}

function specialHandlingListGrid_selectRow(pk) {
    rowid = pk;
    getRow(specialHandlingListGrid1, pk);
}

function specialHandlingListForm_btnClick(btnID) {
    var tempGrid = getObject(gridID);
    if (tempGrid) {
        switch (btnID) {
            case 'SELECT':
                specialHandlingListGrid_updatenode("CSELECT_IND", -1);
            // when clicking the top checkbox to check all records, the form will lost connection with the grid, force to select the first record.
                first(specialHandlingListGrid1);
                selectFirstRowInGrid(gridID);
                break;
            case 'DESELECT':
                specialHandlingListGrid_updatenode("CSELECT_IND", 0);
            // when clicking the top checkbox to check all records, the form will lost connection with the grid, force to select the first record.
                first(specialHandlingListGrid1);
                selectFirstRowInGrid(gridID);
                break;
        }
    }
}

function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'ADD':
             commonAddRow(gridID);
            break;
        case 'DELETE':
            commonDeleteRow("specialHandlingListGrid");
            break;
        case "CLOSE":
            if (isPageGridsDataChanged()) {
                if (!confirm(ciDataChangedConfirmation)) {
                    return false;
                }
            }
            closeWindow();
            break;
    }
}

function handleOnSubmit(action) {
    var proceed = false;
    switch (action) {
        case 'SAVE':
            document.forms[0].action = getAppPath() + "/demographic/clientmgr/specialhandlingmgr/maintainSpecialHandling.do?pk=" + getObjectValue("pk");
            setObjectValue("process", "saveAllSpecialHandlings");
            proceed = true;
            break;
        default:
            proceed = false;
    }
    return proceed;
}

function specialHandlingListGrid_setInitialValues() {
    specialHandlingListGrid1.recordset("CSOURCERECORDID").value = getObjectValue("pk");
    specialHandlingListGrid1.recordset("CSOURCETABLENAME").value = 'ENTITY';
    var path = getAppPath() + "/demographic/clientmgr/specialhandlingmgr/maintainSpecialHandling.do?"
            + "process=getInitialValuesForAddSpecialHandling";
    //add time to change the url, so it will get the initial value from server each time
    path += "&currectTime=" + Date.parse(new Date());
    new AJAXRequest("get", path, '', handleOnGetInitialValuesForAddSpecialHandling, false);
}

//-----------------------------------------------------------------------------
// handle on ajax return for set initial values for Special Handling list
//-----------------------------------------------------------------------------
function handleOnGetInitialValuesForAddSpecialHandling(ajax) {
    currentlySelectedGridId = gridID;
    commonHandleOnGetInitialValues(ajax);
}

function loadNotesForSpHandling(pk) {

    //handle the problem window.resizeTo will occurs error 'Access is denied' when run in IE & JqxGrid mode.
    setTimeout(function () {
        window.resizeTo(900,750);
    }, 500);
    loadNotesWithReloadOption(pk, 'SPECIAL_HANDLING', 'SPECIAL_HANDLING', true, true);
}

function handleOnLoad() {
    var functionExists = eval("getParentWindow().commonOnSetButtonItalics");
    if (functionExists) {
        getParentWindow().commonOnSetButtonItalics(false);
    }

    window.resizeTo(900,500);
}

