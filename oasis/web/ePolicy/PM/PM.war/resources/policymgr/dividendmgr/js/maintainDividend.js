//-----------------------------------------------------------------------------
// JavaScript file for view and post dividend.
//
// (C) 2012 Delphi Technology, inc. (dti)
// Date:    March 13, 2011
// Author:  wfu
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 07/31/2018       mlm         193967 - Refactored to promote and rename moveToFirstRowInTable into framework.
// 11/27/2018       xgong       195889 - 1) Update disableSelectInd for gird replacement
//                                       2) Add a new function handleGetCustomPageOptions
//-----------------------------------------------------------------------------

function handleOnButtonClick(btn) {
    switch (btn) {
        case 'SEARCH':
            document.forms[0].process.value = "loadAllProcessedDividend";
            showProcessingDivPopup();
            submitFirstForm();
            break;
        case 'POST':
            if (isRecordSelected(calculateDividendListGrid1)) {
                if (confirm(getMessage("pm.dividend.process.post.confirm"))) {
                    var divIds = ',' + getSelectedKeysString(calculateDividendListGrid1,'SELECT_IND',',');
                    var url = getAppPath() + "/policymgr/dividendmgr/maintainDividend.do"
                            + "?process=performPostDividend"
                            + "&dividendId=" + divIds
                            + "&manualB=Y"
                            + "&date=" + new Date();
                    new AJAXRequest("get", url, '', handleOnPostDividend, false);
                }
            } else {
                alert(getMessage("pm.dividend.process.post.noRecord.select"));
                return;
            }
            break;
        default:break;
    }
}

function handleOnPostDividend(ajax){
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }

            alert(getMessage("pm.dividend.process.post.success.info"));
            // To retrieve prior dividend list
            commonOnButtonClick('SEARCH');
        }
    }
}

function calculateDividendList_btnClick(asBtn) {
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
    var XMLData = calculateDividendListGrid1;
    if (!isEmptyRecordset(XMLData.recordset)) {
        var absPosition = XMLData.recordset.AbsolutePosition;
        first(XMLData);
        while (!XMLData.recordset.eof) {
            if (XMLData.recordset("CSTATUS").value == 'PROCESSED') {
                XMLData.recordset('CSELECT_IND').value = selectValue;
            }
            next(XMLData);
        }
        first(XMLData);
        XMLData.recordset.move(absPosition - 1);
    }
}

//-----------------------------------------------------------------------------
// To disable/enable Select to Generate checkbox
//-----------------------------------------------------------------------------
function handleReadyStateReady() {
    disableSelectInd();
}

function disableSelectInd() {
    // Do nothing if the table is empty
    if (!getTableProperty(calculateDividendListGrid, "hasrows") || dti.oasis.page.useJqxGrid()) {
        return;
    }

    // Initialize the select check boxes in table
    var XMLData = calculateDividendListGrid1;
    var rowId = XMLData.recordset("ID").value;
    resetRecordPointerToFirstRowInGridCurrentPage(calculateDividendListGrid);
    var chkSelArray = document.getElementsByName("chkCSELECT_IND");
    var size = chkSelArray.length;
    if (!isEmptyRecordset(XMLData.recordset)) {
        for (var i = 0; i < size; i++) {
            var status = XMLData.recordset("CSTATUS").value;
            chkSelArray[i].disabled = status != "PROCESSED";
            next(XMLData);
        }
    }
    resetRecordPointerToFirstRowInGridCurrentPage(calculateDividendListGrid);
    getRow(XMLData, rowId) ;
}


//-----------------------------------------------------------------------------
// To disable/enable Select to Generate checkbox for jqx grid
//-----------------------------------------------------------------------------
function handleGetCustomPageOptions() {
    function __isCellEditable(jqxRowIndex, datafield, columntype, value) {
        return  dti.oasis.grid.getRowDataByJqxRowIndex("calculateDividendListGrid", jqxRowIndex)["CSTATUS"] == "PROCESSED";
    }

    return dti.oasis.page.newCustomPageOptions()
            .cellBeginEdit("calculateDividendListGrid", "CSELECT_IND", __isCellEditable)
            .addIsCellEditableFunction("calculateDividendListGrid", "CSELECT_IND", __isCellEditable)
}