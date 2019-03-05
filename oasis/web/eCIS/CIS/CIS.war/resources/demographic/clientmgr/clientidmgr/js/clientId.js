//-----------------------------------------------------------------------------
// Functions For detailId page
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:
// Author:
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
//07/22/2011       parker      Issue#122838: could not delete clientId.
//09/25/2018       dmeng       Issue 195835: grid replacement.
//-----------------------------------------------------------------------------

var gridID = "clientIdListGrid";
var primaryBFld = "entityIdNumber_primaryB";
var isProcessing = false;
validateGridId = "clientIdListGrid";

function handleOnChange(obj) {
    isChanged = true;
    gridDataChange = true;
    if (window.postOnChange) {
        postOnChange(obj);
    }
    if (obj.name == primaryBFld && obj.value == "Y") {
        var currentRowId = clientIdListGrid1.recordset("ID").value;
        var primaryIdRcds = clientIdListGrid1.documentElement
                .selectNodes("//ROW[CPRIMARYB='Y' and UPDATE_IND != 'D' and @id != '" + currentRowId + "']");
        var len = primaryIdRcds.length;
        for (i = 0; i < len; i++) {
            primaryIdRcds.item(i).getElementsByTagName("CPRIMARYB")[0].text = "";
            primaryIdRcds.item(i).getElementsByTagName("CPRIMARYBLOVLABEL")[0].text = "";

            if (primaryIdRcds.item(i).getElementsByTagName("UPDATE_IND")[0].text != "I") {
                primaryIdRcds.item(i).getElementsByTagName("UPDATE_IND")[0].text = "Y";
            }
        }
    }
}

function clientIdListGrid_selectRow(pk) {
    rowid = pk;
    getRow(clientIdListGrid1, pk);
}

function ClientIdListForm_btnClick(btnID) {
    var tempGrid = getObject(gridID);
    if (tempGrid) {
        switch (btnID) {
            case 'SELECT':
                clientIdListGrid_updatenode("CSELECT_IND", -1);
                first(clientIdListGrid1);
                selectFirstRowInGrid("clientIdListGrid");
                break;
            case 'DESELECT':
                clientIdListGrid_updatenode("CSELECT_IND", 0);
                first(clientIdListGrid1);
                selectFirstRowInGrid("clientIdListGrid");
                break;
        }
    }
}

function btnClick(btnID) {
    switch (btnID) {
        case 'ADD':
            commonAddRow(gridID);
            break;
        case 'DELETE':
            rowid = commonDeleteRow("clientIdListGrid");
            break;
        case 'SAVE':
            if (!isProcessing) {
                if (getTableProperty(getTableForGrid("clientIdListGrid"), "hasrows")) {
                    if (!validateGrid()) {
                        return;
                    }
                }
                isProcessing = true;
                setObjectValue("process", "saveAllClientIds");
                clientIdListGrid_update();
            }
            break;
        case "REFRESH":
            if (isGridDataChanged("clientIdListGrid")) {
                if (!confirm(ciDataChangedConfirmation)) {
                    return false;
                }
            }
            setObjectValue("process", "loadAllClientIds");
            submitFirstForm();
            break;
        case "CLOSE":
            if (isGridDataChanged("clientIdListGrid")) {
                if (!confirm(ciDataChangedConfirmation)) {
                    return false;
                }
            }
            closeWindow();
            break;
    }
}

function validateGrid() {
    var msg = '';
    var count = 0;
    clientIdListGrid1.recordset.movefirst();
    while (!clientIdListGrid1.recordset.eof) {
        msg = '';
        count = count + 1;
        var upd_ind = clientIdListGrid1.recordset("UPDATE_IND").value;
        if (upd_ind == 'Y' || upd_ind == 'I') {
            //ID type is required
            if (!isStringValue(clientIdListGrid1.recordset("CENTITYIDNOTYPECODE").value)) {
                msg += getMessage("ci.maintainClientId.clientIdType.required", [count+""]) + "\n";
            }

            //ID Number is required
            if (!isStringValue(clientIdListGrid1.recordset("CEXTERNALID").value)) {
                msg += getMessage("ci.maintainClientId.externalId.required",  [count+""]) + "\n";
            }

            // effectiveFromDate <= effectiveToDate
            effectiveFromDate = clientIdListGrid1.recordset("CEFFECTIVEFROMDATE").value;
            if (effectiveFromDate != null && effectiveFromDate != '') {
                if (!isValueDate(effectiveFromDate))
                    msg += getMessage("ci.maintainClientId.invalidEffectiveFromDate.error",  [count+""]) + "\n";
            }
            effectiveToDate = clientIdListGrid1.recordset("CEFFECTIVETODATE").value;
            if (effectiveToDate != null && effectiveToDate != '') {
                if (!isValueDate(effectiveToDate))
                    msg += getMessage("ci.maintainClientId.invalidEffectiveToDate.error",  [count+""]) + "\n";
            }
            if (effectiveFromDate != null && effectiveFromDate != '' &&
                effectiveToDate != null && effectiveToDate != '') {
                if (isDate2OnOrAfterDate1(effectiveFromDate, effectiveToDate) == 'N') {
                    msg += getMessage("ci.maintainClientId.EndDateBeforeStartDate.error",  [count+""]) + "\n";
                }
            }
            if (msg != '') {
                selectRowById('clientIdListGrid', clientIdListGrid1.recordset("ID").value);
                alert(msg);
                return false;
            }
        }
        clientIdListGrid1.recordset.movenext();
    }
    return true;
}

function clientIdListGrid_setInitialValues(pk) {
    clientIdListGrid1.recordset("CENTITYID").value = getObjectValue("pk");
    clientIdListGrid1.recordset("CEFFECTIVEFROMDATE").value = formatDate(new Date(), 'mm/dd/yyyy');
}