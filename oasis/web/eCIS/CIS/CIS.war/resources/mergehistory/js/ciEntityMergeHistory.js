//-----------------------------------------------------------------------------
// Functions to support Entity Merge History page.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:
// Author:
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 10/09/2015       ylu         Issue 164517
//-----------------------------------------------------------------------------

function handleOnButtonClick(sBtn) {
    if (sBtn == "close") {
        closeWindow();
    } else if (sBtn == "unmerge") {
        var dataArray = getSelectedKeys(historyListGrid1);
        if (dataArray.length != 1) {
            alert(getMessage("ci.common.error.onlyOneRow.noSelect"));
            return;
        }
        if (confirm(getMessage("ci.maintainClientDup.unmerge.warning"))) {

            var pk = dataArray[0];
            selectRowById("historyListGrid", pk);
//          selectRow("historyListGrid", dataArray[0]);
//          var pk = historyListGrid1.recordset("CENTITYMERGEHISTORYID").value;
            setObjectValue("entityMergeHistoryId", pk);
            setInputFormField("process", "unMergeProcess");
            showProcessingDivPopup();
            submitFirstForm();
        }
    }
}

function EntityMergeHistoryForm_btnClick(asBtn) {
    switch (asBtn) {
        case 'SELECT':
            historyListGrid_updatenode("CSELECT_IND", -1);
            first(historyListGrid1);
            selectFirstRowInGrid("historyListGrid");
            break;
        case 'DESELECT':
            historyListGrid_updatenode("CSELECT_IND", 0);
            first(historyListGrid1);
            selectFirstRowInGrid("historyListGrid");
            break;
    }
}

