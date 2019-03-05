//-----------------------------------------------------------------------------
// Javascript file for viewMultiCancelInfo.js
//
// (C) 2008 Delphi Technology, inc. (dti)
// Date:
// Author:
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 07/31/2018       mlm         193967 - Refactored to promote and rename moveToFirstRowInTable into framework.
//-----------------------------------------------------------------------------
function initGridSelectCheckbox() {
    var chkSelAll = document.getElementsByName("chkCSELECT_ALL")[0];
    // Do nothing if the table is empty
    if (!getTableProperty(cancelListGrid, "hasrows")) {
        return;
    }

    var disableChkSelectAll = true;
    // When there is pagination, not all records are in table. So move to the proper record first.
    resetRecordPointerToFirstRowInGridCurrentPage(cancelListGrid);

    // Initialize the select check boxes in table
    var XMLData = cancelListGrid1;
    var chkSelArray = document.getElementsByName("chkCSELECT_IND");
    var size = chkSelArray.length;
    if (!isEmptyRecordset(XMLData.recordset)) {
        for (var i = 0; i < size; i++) {
            if (XMLData.recordset('CSTATUS').value == 'INVALID') {
                chkSelArray[i].disabled = true;
            }
            else {
                disableChkSelectAll = false;
            }
            next(XMLData);
        }

        if (disableChkSelectAll) {
            chkSelAll.disabled = true;
        }
        else {
            chkSelAll.disabled = false;
        }
    }
    else {
        chkSelAll.disabled = true;
    }
    // Move back to where we started
    resetRecordPointerToFirstRowInGridCurrentPage(cancelListGrid);

    if(disableChkSelectAll){
        hideShowField(getObject('PM_CC_CONTINUE'), true);
    }
}

function userRowchange(obj) {
    if (obj.name == "chkCSELECT_IND") {
        var XMLData = cancelListGrid1;
        if (XMLData.recordset('CSTATUS').value == 'INVALID') {
            window.event.returnValue = false;
        }
    }
}

function handleReadyStateReady(table) {
    var cancelLevel = getObjectValue("cancellationLevel");
    if (table.id == "cancelListGrid") {
        initGridSelectCheckbox();
    }
}

function cancelList_btnClick(asBtn) {
    switch (asBtn) {
        case 'SELECT':
            updateSelectInd(-1);
            break;
        case 'DESELECT':
            updateSelectInd(0);
            break;
    }
}

function updateSelectInd(selectValue) {
    var XMLData = cancelListGrid1;
    if (!isEmptyRecordset(XMLData.recordset)) {
        var absPosition = XMLData.recordset.AbsolutePosition;
        first(XMLData);
        while (!XMLData.recordset.eof) {
            if (XMLData.recordset('CSTATUS').value != 'INVALID') {
                XMLData.recordset('CSELECT_IND').value = selectValue;
            }
            next(XMLData);
        }
        first(XMLData);
        XMLData.recordset.move(absPosition - 1);
    }
}