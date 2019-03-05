//-----------------------------------------------------------------------------
// JavaScript file for maintain dividend declaration.
//
// (C) 2012 Delphi Technology, inc. (dti)
// Date:    March 12, 2012
// Author:  wfu
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 09/01/2017       xnie        187996 Modified handleOnButtonClick to replace 'PREVIEW' with 'PREVIEW_DIVIDEND'.
// 07/31/2018       mlm         193967 - Refactored to promote and rename moveToFirstRowInTable into framework.
// 11/27/2018       xgong       195889 - 1) Update disableSelectInd for gird replacement
//                                       2) Add a new function handleGetCustomPageOptions
//-----------------------------------------------------------------------------

function handleOnButtonClick(btn) {
    switch (btn) {
        case 'SEARCH':
            document.forms[0].process.value = "loadAllDividendDeclare";
            submitFirstForm();
            break;
        case 'PREVIEW_DIVIDEND':
            if (isRecordSelected(dividendListGrid1)) {
                var divIds = getSelectedKeysString(dividendListGrid1,'SELECT_IND',',');
                if (divIds.length>1) {
                    divIds = divIds.substring(0, divIds.length-1);
                }

                var url = getAppPath() + "/policymgr/dividendmgr/previewDividend.do"
                                       + "?process=loadAllDividendForPreview"
                                       + "&dividendRuleId=" + divIds;
                var divPopupId = openDivPopup("", url, true, true, "", "", 850, 600, "", "", "", false);

            } else {
                alert(getMessage("pm.dividend.process.post.noRecord.select"));
                return;
            }
            break;
        case 'PROCESS':
            if (isRecordSelected(dividendListGrid1)) {
                var divIds = ',' + getSelectedKeysString(dividendListGrid1,'SELECT_IND',',');
                var url = getAppPath() + "/policymgr/dividendmgr/maintainDividendDeclare.do"
                        + "?process=performProcessDividend"
                        + "&dividendRuleId=" + divIds
                        + "&date=" + new Date();
                new AJAXRequest("get", url, '', handleOnProcessDividend, false);

            } else {
                alert(getMessage("pm.dividend.process.post.noRecord.select"));
                return;
            }
            break;
        default:break;
    }
}

function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'SAVE':
            document.forms[0].process.value = "saveAllDividendDeclare";
            break;
        default:
            proceed = false;
    }
    return proceed;
}

function dividendListGrid_setInitialValues() {
    var url = getAppPath() + "/policymgr/dividendmgr/maintainDividendDeclare.do"
                           + "?process=getInitialValuesForAddDividendDeclare"
                           + "&date=" + new Date();
    new AJAXRequest("get", url, '', commonHandleOnGetInitialValues, false);
}

function handleOnProcessDividend(ajax){
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }

            alert(getMessage("pm.dividend.maintain.process.success.info"));
            // To retrieve prior dividend list
            commonOnButtonClick('SEARCH');
        }
    }
}

function handlePostAddRow(dividendListGrid) {
    disableSelectInd();
}

function dividendList_btnClick(asBtn) {
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
    var XMLData = dividendListGrid1;
    if (!isEmptyRecordset(XMLData.recordset)) {
        var absPosition = XMLData.recordset.AbsolutePosition;
        first(XMLData);
        while (!XMLData.recordset.eof) {
            if (XMLData.recordset("CSTATUS").value == 'Pending') {
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
    if (!getTableProperty(dividendListGrid, "hasrows") || dti.oasis.page.useJqxGrid()) {
        return;
    }

    // Initialize the select check boxes in table
    var XMLData = dividendListGrid1;
    var rowId = XMLData.recordset("ID").value;
    resetRecordPointerToFirstRowInGridCurrentPage(dividendListGrid);
    var chkSelArray = document.getElementsByName("chkCSELECT_IND");
    var size = chkSelArray.length;
    if (!isEmptyRecordset(XMLData.recordset)) {
        for (var i = 0; i < size; i++) {
            var status = XMLData.recordset("CSTATUS").value;
            chkSelArray[i].disabled = status != "Pending";
            next(XMLData);
        }
    }
    resetRecordPointerToFirstRowInGridCurrentPage(dividendListGrid);
    getRow(XMLData, rowId) ;
}

//-----------------------------------------------------------------------------
// To disable/enable Select to Generate checkbox for jqx grid
//-----------------------------------------------------------------------------
function handleGetCustomPageOptions(){
    function __isCellEditable(jqxRowIndex, datafield, columntype, value) {
        return  dti.oasis.grid.getRowDataByJqxRowIndex("dividendListGrid", jqxRowIndex)["CSTATUS"] == "Pending";
    }

    return dti.oasis.page.newCustomPageOptions()
            .cellBeginEdit("dividendListGrid", "CSELECT_IND", __isCellEditable)
            .addIsCellEditableFunction("dividendListGrid", "CSELECT_IND", __isCellEditable)
}