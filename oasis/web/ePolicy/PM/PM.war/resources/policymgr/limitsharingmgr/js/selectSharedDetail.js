//-----------------------------------------------------------------------------
// Javascript file for selectSharedDetail.js
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   June 20, 2011
// Author: jshen
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 06/20/2011       jshen       121917 - Fix the js error when multiple pages exists
// 08/22/2011       lmjiang     124372 - Fix the error when the owners fields still be disabled while
//                                       the 'Select All' check box is selected. 
// 09/14/2011       ryzhao      125162 - When some coverage has been selected for shared group,
//                                       the select all checkbox should be disable.
// 10/12/2011       ryzhao      123053 - 1) Delete handleOnLoad() function.
//                                       2) Delete some logic in selectSharedDetailForm_btnClick() function.
//                                       3) Added handleReadyStateReady() function to enable/disable check box.
// 11/09/2011       xnie        125517 - 1) Modified userRowchange to set value for field CADDDETAILB.
//                                       2) Modified handleOnButtonClick to use CADDDETAILB instead of CSELECT_IND
//                                          to decide if add current record as detail.
//                                       3) Modified handleReadyStateReady to change owner checkbox ready only property.
// 11/25/2011       xnie        127405 - 1) Added a new function setAddDetailB to set value for field CADDDETAILB.
//                                       2) Modified selectSharedDetailForm_btnClick to call function setAddDetailB.
// 12/09/2011       bhong       128085 - Added function to find parent TR row when retrieving row index to solve problem in IE9
// 12/09/2011       bhong       128085 - Moved "findParentTrRow" to edits.js
// 07/13/2012       fcb         135468 - 1) handleReadyStateReady: added logic to avoid null pointer error when the
//                                          shared group details grid is empty.
// 07/05/2013       adeng       145576 - Modified handleReadyStateReady() to first check if first column checked and it
//                                       can be set as owner, enable it's owner checkbox. Then check if it has added as
//                                       a validate row, disable it's owner checkbox.
// 03/30/2017       eyin        180675 - Use getReturnCtxOfDivPopUp() to get correct parent window in tab style.
// 07/12/2017       lzhang      186847   Reflect grid replacement project changes
// 07/31/2018       mlm         193967 - Refactored to promote and rename moveToFirstRowInTable into framework.
//-----------------------------------------------------------------------------
var selectSharedDetailGridId;

function handleOnButtonClick(asBtn) {
    var divPopup = window.frameElement.document.parentWindow.getDivPopupFromDivPopupControl(this.frameElement);

    switch (asBtn) {
        case 'DONE':
            var selectedRecords = selectSharedDetailGrid1.documentElement.selectNodes("//ROW[(CADDDETAILB = 'Y')]");
            if (selectedRecords.length == 0) {
                handleError(getMessage("pm.addSharedDetail.noSelection.error"));
                break;
            }
            getReturnCtxOfDivPopUp().addSharedDetails(selectedRecords, true);
            hideShowElementByClassName(getReturnCtxOfDivPopUp().getSingleObject("SharedDetailDetailDiv"), false);
            if (divPopup) {
                window.frameElement.document.parentWindow.closeDiv(divPopup);
            }

            break;
        case 'CANCEL':
            if (divPopup) {
                window.frameElement.document.parentWindow.closeDiv(divPopup);
            }
            break;
    }
}

function userRowchange(obj) {
    var parentTrRow = findParentTrRow(obj);
    var myrow = parentTrRow.rowIndex - 1;
    var vIsCovgSharedOwner = selectSharedDetailGrid1.recordset("CISCOVGSHAREDOWNER").value;
    if ( obj.name == "chkCSELECT_IND") {
        // if we checked the select check box and not already an owner, enable owner
        if (obj.checked){
           if (vIsCovgSharedOwner == 'N') {
               getObjectInRow("chkCSHAREDTLOWNERB",myrow).disabled = false;
           }
           selectSharedDetailGrid1.recordset("CADDDETAILB").value = 'Y';
        }
        // Selection was unchecked , Uncheck the owner
        else {
           getObjectInRow("chkCSHAREDTLOWNERB",myrow).checked = false;
           if (vIsCovgSharedOwner == 'N') {
               getObjectInRow("chkCSHAREDTLOWNERB",myrow).disabled = true;
           }
           selectSharedDetailGrid1.recordset("CADDDETAILB").value = 'N';
        }
    }
 }


function getObjectInRow(id, row) {
    var obj = getObject(id);
    // if it has a length property and no OPTIONS property, it is an array of objects
    if (obj) {
        if (obj.length && !obj.options)
            return obj[row];
        else
            return obj;
    } else {
        return obj;
    }

}
//-----------------------------------------------------------------------------
// Fix issue 97824.
// When the underwriter click selectAll checkbox, system should disable/enable the owner checkbox.
// The logic refers to the function userRowchange().
//-----------------------------------------------------------------------------
function selectSharedDetailForm_btnClick(asBtn) {
    updateAllSelectInd(asBtn);
    var checked;
    if (asBtn == 'SELECT') {
        checked = true;
        setAddDetailB('Y');
    } else if (asBtn == 'DESELECT') {
        checked = false;
        setAddDetailB('N');
    }

    var ownerFields = getObject('chkCSHAREDTLOWNERB');
    for (var i = 0; i < ownerFields.length; i ++) {
        enableDisableField(ownerFields[i], !checked);
        if (!checked) {
            ownerFields[i].checked = false;
        }
    }
}

//-----------------------------------------------------------------------------
// Fix issue 127405.
// When user select all or unselect all coverages, the CADDDETAILB should be set.
//-----------------------------------------------------------------------------
function setAddDetailB(asBtn) {
    if (!isEmptyRecordset(selectSharedDetailGrid1.recordset)) {
        first(selectSharedDetailGrid1);
        while (!selectSharedDetailGrid1.recordset.eof) {
            selectSharedDetailGrid1.recordset("CADDDETAILB").value = asBtn;
            next(selectSharedDetailGrid1);
        }
        first(selectSharedDetailGrid1);
    }
}

//-----------------------------------------------------------------------------
// Fix issue 123053.
// Enable/Disable the check box when we turning page or sort the data.
//-----------------------------------------------------------------------------
function handleReadyStateReady(table) {
    if (table.id == "selectSharedDetailGrid") {
        var chkSelAll = getObject("chkCSELECT_ALL");
        var disableChkSelectAll = false;
        // When there is pagination, not all records are in table. So move to the proper record first.
        resetRecordPointerToFirstRowInGridCurrentPage(selectSharedDetailGrid);

        // Initialize the select check boxes in table
        var XMLData = selectSharedDetailGrid1;
        var chkSelArray = getObject("chkCSELECT_IND");
        var chkSelOwnerArray = getObject("chkCSHAREDTLOWNERB");
        if (!isEmptyRecordset(XMLData.recordset)) {
            var size = chkSelArray.length;
            for (var i = 0; i < size; i++) {
                // Handle owner check box
                if (chkSelArray[i].checked && XMLData.recordset('CSHAREDTLOWNERB').value == '0' ) {
                    chkSelOwnerArray[i].disabled = false;
                }
                else {
                    chkSelOwnerArray[i].disabled = true;
                }
                // Handle row check box
                if (XMLData.recordset('CVALIDCOVERAGEB').value == 'N') {
                    chkSelArray[i].disabled = true;
                    chkSelOwnerArray[i].disabled = true;
                    disableChkSelectAll = true;
                }
                else {
                    chkSelArray[i].disabled = false;
                    if (chkSelArray[i].checked) {
                        chkSelOwnerArray[i].disabled = false;
                    }
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
        else if (chkSelAll != null) {
            chkSelAll.disabled = true;
        }
        // Move back to where we started
        resetRecordPointerToFirstRowInGridCurrentPage(selectSharedDetailGrid);
    }
}
