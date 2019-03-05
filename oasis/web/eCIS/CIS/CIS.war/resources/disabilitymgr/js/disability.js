//  Revision Date    Revised By  Description
//  ---------------------------------------------------
//  09/21/2018      dzou        Issue 195835: Grid replacement. Use getObjectValue, setObjectValue. User dti.oasis.grid.getColumnLabel instead of getLabel.
//  10/22/2018      jdingle     Issue 160238: handle multiple deletes.
//  ---------------------------------------------------
var isChanged = false;

//-----------------------------------------------------------------------------
// Determines if OK to change pages.
//-----------------------------------------------------------------------------
function isOkToChangePages(id, url) {
    if (isPageDataChanged()) {
        if (!confirm(ciDataChangedConfirmation)) {
            return false;
        }
    }
    return cisEntityFolderIsOkToChangePages(id, url);
}

//-----------------------------------------------------------------------------
// Add parameters to the menu query string.
//-----------------------------------------------------------------------------
function getMenuQueryString(id, url) {
    return cisEntityFolderGetMenuQueryString(id, url);
}
function CIDisabilityForm_btnClick(asBtn){
    switch (asBtn) {
        case 'SELECT':
            testgrid_updatenode("CSELECT_IND", -1);
            first(testgrid1);
            gotopage(testgrid, 'F');
            selectFirstRowInGrid("testgrid");
            break;
        case 'DESELECT':
            testgrid_updatenode("CSELECT_IND", 0);
            first(testgrid1);
            gotopage(testgrid, 'F');
            selectFirstRowInGrid("testgrid");
            break;
    }
}
//-----------------------------------------------------------------------------
// Override function frmGrid_btnClick
//-----------------------------------------------------------------------------
function frmGrid_btnClick(asBtn) {
    switch (asBtn) {
        
        case 'DELETE':
            commonDeleteRow("testgrid");
            break;
        case 'SAVE':
            first(testgrid1);
            while (!testgrid1.recordset.eof) {
                var upd = testgrid1.recordset("UPDATE_IND").value;
                if (upd == 'I' || upd == 'Y')
                    if (!validate(document.forms[0]) || !validateGrid())
                        return;
                next(testgrid1);
            }
            setObjectValue("process", "saveDisabilityData");
            testgrid_update();
            break;
        case 'ADD':
            commonAddRow("testgrid");
            break;
        case 'REFRESH':
            if (isPageDataChanged()) {
                if (!confirm(ciRefreshPageConfirmation)) {
                    return;
                }
            }
            setObjectValue("process", "loadDisabilityList");
            submitFirstForm();
            break;
    }
}

//-----------------------------------------------------------------------------
// This function is needed to navi to some tabs(vendor,address...)
//-----------------------------------------------------------------------------
function btnClick(btnID) {
    alert("btnClick");
    if (btnID != 'save' && btnID != 'add' && btnID != ' delete' && isChanged) {
         if (btnID == 'refresh') {
            if (!confirm(ciRefreshPageConfirmation)) {
                return;
            }
        } else {
            if (!confirm(ciDataChangedConfirmation)) {
                return;
            }
        }
    }

    if (btnID == 'address'
            || btnID == 'phonenumber'
            || btnID == 'entityclass'
            || btnID == 'entityrole'
            || btnID == 'vendor'
            || btnID == 'vendorAddress') {
        // Go to the appropriate page.
        goToEntityModule(btnID, getObjectValue("pk"),
            getObjectValue("entityName"),
            getObjectValue("entityType"));
    } else if (btnID == 'entity') {
        goToEntityModify(getObjectValue("pk"),
            getObjectValue("entityType"));
    } else if (btnID == 'add') {
        testgrid_insertrow();
        getRow(testgrid1, lastInsertedId);
        // initialize entityfk to entity PK and sourcetable to policy
        setItem(testgrid1, "CENTITYFK", getObjectValue("pk"));
        setObjectValue("process", "add");
        initCertification();
    } else if (btnID == 'delete') {
        // get selected keys
        var dataArray = getSelectedKeys(testgrid1);
        if (dataArray.length == 0) {
            alert(getMessage("ci.common.error.rowSelect.delete"));
            return;
        }
        var upd_ind = '';
        var deleteExistRecordFlag = 'N';
        var deleteNewRecordFlag = 'N';
        if (confirm(getMessage("js.delete.confirmation"))) {
            beginDeleteMultipleRow("testgrid");
            for (var i = 0; i < dataArray.length; i++) {
                selectRow("testgrid", dataArray[i]);
                upd_ind = testgrid1.recordset("UPDATE_IND").value;
                if (upd_ind == 'I') {
                    testgrid_deleterow();
                    deleteNewRecordFlag = 'Y';
                    if (rowid == dataArray[i] || testgrid1.recordset.recordcount == 1)
                        rowid = '';
                } else {
                    deleteExistRecordFlag = 'Y';
                }
            }
            endDeleteMultipleRow("testgrid");
            //end for selected records loop
            if (deleteNewRecordFlag == 'Y' && deleteExistRecordFlag == 'Y')
                alert(getMessage("ci.common.error.newRecords.delete"));
            else if (deleteNewRecordFlag == 'N' && deleteExistRecordFlag == 'Y')
                alert(getMessage("ci.common.error.existRecords.delete"));
        }
        setObjectValue("process", "delete");
        initCertification();
    } else {
        if (btnID == 'save') {
            setObjectValue("process", "save");
            if (!validateGrid()) {
                return;
            }
            testgrid_update();
        } else if (btnID == 'refresh')
            setObjectValue("process", "refresh");
        // Submit the form;  it's either a save or a refresh.
        submitFirstForm();
    }
}

//-----------------------------------------------------------------------------
// Clear search Criteria
//-----------------------------------------------------------------------------
function clearFilter() {
    setObjectValue("entityDisability_categoryCode", '');
    setObjectValue("entityDisability_effectiveFromDate", '');
    setObjectValue("entityDisability_effectiveToDate", '');
    handleOnChange(getObject('entityDisability_categoryCode'));
}

function handleOnChange(obj) {
    if ((obj.name == 'entityDisability_effectiveFromDate' || obj.name == 'entityDisability_effectiveToDate') && datemaskclear()) {
        fromDT = getObjectValue('entityDisability_effectiveFromDate');
        ToDT   = getObjectValue('entityDisability_effectiveToDate');
        if (fromDT !='' && ToDT !='' ) {
            if (new Date(fromDT) > new Date(ToDT)) {
                alert(getMessage("cs.entity.message.filterCriteria.StartDateAfterEndDate.error"));
                return;
            }
        }
        setObjectValue("process", "loadDisabilityList"); //do query
        submitFirstForm();
    }
    if (obj.name == 'entityDisability_categoryCode') {
        setObjectValue("process", "loadDisabilityList"); //do query
        submitFirstForm();
    }
    return;
}

//-----------------------------------------------------------------------------
// Validate row data
//-----------------------------------------------------------------------------
function userRowchange(obj) {
    rowcount = testgrid1.recordset.recordCount;
    if (rowcount <= 0) {
        return;
    }
    valueLb = dti.oasis.grid.getColumnLabel(this.getGridId(), "CCATEGORYAMT");

    if (obj.name == "txtCCATEGORYAMT") {
        if (!isFloat(obj.value) || obj.value <= 0) {
            alert(getMessage("ci.common.error.value.number", new Array(valueLb)));
            window.event.returnValue = false;
            obj.select();
            return;
        }
        obj.value = Math.round(obj.value);
    }
}

//-----------------------------------------------------------------------------
// validate grid data
//-----------------------------------------------------------------------------
function validateGrid() {
    rowcount = testgrid1.recordset.recordCount;
    if (rowcount <= 0) {
        return;
    }
    divLb = dti.oasis.grid.getColumnLabel(this.getGridId(), "CDIVISIONNAME");
    catLb = dti.oasis.grid.getColumnLabel(this.getGridId(), "CCATEGORYCODE");
    valueLb = dti.oasis.grid.getColumnLabel(this.getGridId(), "CCATEGORYAMT");
    fromLb = dti.oasis.grid.getColumnLabel(this.getGridId(), "CEFFECTIVEFROMDATE");
    toLb = dti.oasis.grid.getColumnLabel(this.getGridId(), "CEFFECTIVETODATE");

    first(testgrid1);
    for (i = 0; i < rowcount; i++) {
        /* Divison may not be empty */
        if (testgrid1.recordset('CDIVISIONNAME').value == "") {
            alert(getMessage("ci.common.error.element.required", new Array(divLb, i+1)));
            return false;
        }

        /* Catory code may not be empty */
        if (testgrid1.recordset('CCATEGORYCODE').value == "") {
            alert(getMessage("ci.common.error.element.required", new Array(catLb, i+1)));
            return false;
        }

        /* Value may not be empty */
        if (testgrid1.recordset('CCATEGORYAMT').value == "") {
            alert(getMessage("ci.common.error.element.required", new Array(valueLb, i+1)));
            return false;
        }

        /* validate date field */
        fromDt = testgrid1.recordset("CEFFECTIVEFROMDATE").value;
        if (fromDt != '' && fromDt != '.' && !isValueDate(fromDt)) {
            alert(getMessage("ci.entity.message.date.enter", new Array(i+1)));
            return false;
        }

        toDt = testgrid1.recordset("CEFFECTIVETODATE").value;
        if (toDt != '' && toDt != '.' && !isValueDate(toDt)) {
            alert(getMessage("ci.entity.message.date.enter", new Array(i+1)));
            return false;
        }

        if (fromDt != '' && fromDt != '.' && toDt != '' && toDt != '.') {
            if (isDate2OnOrAfterDate1(fromDt, toDt) == 'N') {
                alert(getMessage("ci.common.error.element.before", new Array(fromLb, toLb, i+1)));
                return false;
            }
        }
        next(testgrid1);
    }

    return true;
}

//Added by Fred on 1/11/2007
//To confirm changes.
function confirmChanges() {
    return (isChanged ||
            isPageDataChanged());
}
function getGridId() {
    return 'testgrid';
}

//-----------------------------------------------------------------------------
//  Initial TRAININGTYPECODE Values*/
//-----------------------------------------------------------------------------
function testgrid_setInitialValues() {
    testgrid1.recordset("CENTITYID").value  = getObjectValue("pk");
}
