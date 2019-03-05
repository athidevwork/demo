// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 09/03/2018       wrong         188391 - Initial version.
// 11/23/2018       wrong         197046 - Modified selectRelatedCovgListGrid_selectRow to remove getTableProperty().
//-----------------------------------------------------------------------------
// checkedRelatedCovgIds: an array to stored all selected related coverage Ids
var checkedRelatedCovgIds = new Array();
function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'PROCESS':
            if (selectCurrentCovgListGrid1.recordset("CCUR_SELECT_IND").value == 0
                || (checkedRelatedCovgIds.length == 0 && getObjectValue("hasAvailableRelatedCoverages") == "Y")) {
                handleError(getMessage("pm.maintainUnderlyingRelation.noSelection.error"));
                break;
            }
            getReturnCtxOfDivPopUp().handleOnSelectRelationDone(
                    getObjectValue("policyUnderPolId"),
                    getObjectValue("policyUnderPolNo"),
                    isEmpty(getObjectValue("policyUnderPolId")) ? "" : selectRelatedCovgListGrid1.recordset("ID").value);
            commonOnButtonClick("CLOSE_RO_DIV");
    }
}

/**
 * Deselect all rows in the xml first, all rows including rows from the next pages are deselected
 * then select the current row.
 *
 * Don't check CSELECT_IND field under below cases:
 * 1. When first load page.
 * 2. When select row in commonReadyStateReady.
 *
 * @param rowid
 */
function selectRelatedCovgListGrid_selectRow(rowid) {
    var isSelected = selectRelatedCovgListGrid1.recordset("CREL_SELECT_IND").value == -1;
    if (isProcessFieldDeps) {
        var absolutePosition = selectRelatedCovgListGrid1.recordset.AbsolutePosition;
        updateNode(selectRelatedCovgListGrid1, "CREL_SELECT_IND", "0");
        first(selectRelatedCovgListGrid1);
        selectRelatedCovgListGrid1.recordset.move(absolutePosition - 1);
        selectRelatedCovgListGrid1.recordset("CREL_SELECT_IND").value = "-1";
    }
    checkAllRelatedCovg(selectRelatedCovgListGrid1, isSelected);
}

//-----------------------------------------------------------------------------
// To save all checked related coverage ids into checkedRelatedCovgIds
//-----------------------------------------------------------------------------
function checkAllRelatedCovg(selectRelatedCovgListGrid, isSelected) {
    var pkSize = checkedRelatedCovgIds.length;
    var id = selectRelatedCovgListGrid1.recordset("ID").value;
    if (isSelected) {
        selectRelatedCovgListGrid1.recordset("CREL_SELECT_IND").value = -1;
        if (!isArrayContains(checkedRelatedCovgIds, id)) {
            checkedRelatedCovgIds = [];
            checkedRelatedCovgIds[pkSize] = id;
        }
    } else {
        selectRelatedCovgListGrid1.recordset("CREL_SELECT_IND").value = 0;
        checkedRelatedCovgIds = removeValueFromArray(checkedRelatedCovgIds, id);
    }
}
