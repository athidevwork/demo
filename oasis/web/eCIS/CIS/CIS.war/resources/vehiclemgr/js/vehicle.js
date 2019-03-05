//-----------------------------------------------------------------------------
// javascript file.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:
// Author:
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 10/14/2010        tzhao      issue#109875 - Modified money format script to support multiple currency.
// 01/06/2015        ylu        159740: 1).removed mandatory description required check, it is done by workbench
//                                      2).update incorrect weight check
// 01/26/2015        bzhu       1) Issue 159832 has been fixed by 159740.
//                              2) Use isNaN to check "not a number" field.
// 11/17/2015        Elvin      Issue 167139: remove js validations, all the logic moved into page rules
// 10/16/2018        Elvin      Issue 195835: grid replacement
//-----------------------------------------------------------------------------

function isOkToChangePages(id, url) {
    if (isPageDataChanged()) {
        if (!confirm(getMessage("js.lose.changes.confirmation"))) {
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

function CIVehicleForm_btnClick(asBtn) {
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

function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'REFRESH':
            if (isOkToChangePages()) {
                reloadWindowLocation();
            }
            break;
    }
}

function testgrid_setInitialValues() {
    testgrid1.recordset("CENTITYID").value = getObjectValue("pk");
    testgrid1.recordset('CSYSTIME').value = formatDate(new Date(), 'mm/dd/yyyy');
}