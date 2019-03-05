//-----------------------------------------------------------------------------
// Functions to support Change Address Role page.
// Author: cyzhao
// Date:   unknown
// Modifications:
//-----------------------------------------------------------------------------
// 04/19/2011  Michael Li   for issue119534
// 08/30/2012  bzhu         For issue 136720
// 11/15/2012  kshen        Issue 139047.
// 10/26/2018  Elvin        Issue 195835: grid replacement
//-----------------------------------------------------------------------------

var filterCondition;

function handleOnLoad() {
}

function handleOnButtonClick(btnId) {
    switch (btnId) {
        case 'CANCEL':
            closeWindow();
            break;
        case 'MOVE':
            OPEN_SELECT_ADDRESS_PARAMETERS.entityId = getObjectValue(addressfields.ENTITY_ID);
            OPEN_SELECT_ADDRESS_PARAMETERS.addressId = getObjectValue(addressfields.ADDRESS_ID);
            OPEN_SELECT_ADDRESS_PARAMETERS.callbackEvent = "handleOnAddressSelected";
            openSelectAddressDivPopup();
            break;
        case 'DONE':
            updateAddressRoles();
            break;
        case 'OK':
            getParentWindow().SELECTED_ADDRESS_ROLE_CHG_INFO.isTransRolesToNewAddress = "Y";
            closeWindow(getParentWindow().OPEN_ADDRESS_ROLE_CHG_PARAMETERS.callbackEvent);
            break;
        case 'SKIP':
            getParentWindow().SELECTED_ADDRESS_ROLE_CHG_INFO.isTransRolesToNewAddress = "N";
            closeWindow(getParentWindow().OPEN_ADDRESS_ROLE_CHG_PARAMETERS.callbackEvent);
            break;
        default:
            break;
    }
}

function changeAddressRoleForm_btnClick(asBtn) {
    switch (asBtn) {
        case 'SELECT':
            handleChildRows(-1);
            break;
        case 'DESELECT':
            handleChildRows(0);
            break;
    }
}

function addressRoleListGrid_selectRow(rowId) {
}

function handleOnChange(field) {
}

//-----------------------------------------------------------------------------
// Address Selected
//-----------------------------------------------------------------------------
function handleOnAddressSelected() {
    setObjectValue(addressfields.TO_ADDRESS_DESC, SELECTED_ADDRESS_INFO.addressSingleLine);
    setObjectValue(addressfields.TO_ADDRESS_ID, SELECTED_ADDRESS_INFO.addressId);
}

function updateAddressRoles() {
    if (getObjectValue(addressfields.TO_ADDRESS_ID) <= 0 || isEmpty(getObjectValue(addressfields.TO_ADDRESS_ID))) {
        alert(getMessage("ci.address.addressRoleChgMgr.msg.warning.toAddressNotSelected"));
        return;
    }

    if (!isEmpty(filterCondition)) {
        addressRoleListGrid_filter();
    }

    var dataArray = getSelectedKeys(addressRoleListGrid1);
    if (dataArray.length == 0) {
        alert('Select at least one row to proceed.');

        if (!isEmpty(filterCondition)) {
            addressRoleListGrid_filter(filterCondition);
        }
        return;
    }

    var entityRoleIdList = "entityRoleIdList=";
    for (var i = 0; i < dataArray.length; i++) {
        var id = dataArray[i];
        if (id > 0) {
            entityRoleIdList += id + ",";
        }
    }

    if (!isEmpty(filterCondition)) {
        addressRoleListGrid_filter(filterCondition);
    }

    enableDisableField(getObject("CI_CHG_ADR_ROLE_DONE"), true);
    var url = getCISPath() + "/ciChgAddressRole.do?addressId=" + getObjectValue(addressfields.ADDRESS_ID)
            + "&toAddressId=" + getObjectValue(addressfields.TO_ADDRESS_ID)
            + "&process=updateAddressRoles";
    new AJAXRequest("POST", url, entityRoleIdList, afterUpdateAddressRoles, false);
}


function afterUpdateAddressRoles(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseText;
            if (data != "Y") {
                alert(getMessage("ci.address.addressRoleChgMgr.msg.error.failToTransfer"));
                enableDisableField(getObject("CI_CHG_ADR_ROLE_DONE"), false);
                return;
            } else {
                alert(getMessage("ci.address.addressRoleChgMgr.msg.addressRolesTransferred"));
                closeWindow(getParentWindow().OPEN_ADDRESS_ROLE_CHG_PARAMETERS.callbackEvent);
            }
        }
    }
}

//-----------------------------------------------------------------------------
// To display image in the grid
//-----------------------------------------------------------------------------
function handleReadyStateReady(tbl) {
    if (!dti.oasis.page.useJqxGrid()) {
        processPlusMinusImages("CGROUPEXPANDCOLLAPSE");
    }
}

//-----------------------------------------------------------------------------
// Expand or Collapse the role data
//-----------------------------------------------------------------------------
function groupExpandCollapse(pk) {
    var selectAllStatus = getSelectAllStatus();

    var currentRowId = addressRoleListGrid1.recordset("ID").value;

    var currentExpandCollapse = addressRoleListGrid1.recordset("CGROUPEXPANDCOLLAPSE").value;
    if (currentExpandCollapse == '+') {
        addressRoleListGrid1.recordset("CGROUPEXPANDCOLLAPSE").value = "-";
    } else {
        addressRoleListGrid1.recordset("CGROUPEXPANDCOLLAPSE").value = "+";
    }
    // set the update_ind so that when doing filter the changes will be synchronized
    // if not, the - will not be changed to + after filter
    addressRoleListGrid1.recordset("UPDATE_IND").value = 'Y';

    var roleParentCondition = '';
    first(addressRoleListGrid1);
    while (!addressRoleListGrid1.recordset.eof) {
        var parent = addressRoleListGrid1.recordset("CROLEPARENT").value;
        var expandCollapse = addressRoleListGrid1.recordset("CGROUPEXPANDCOLLAPSE").value;
        if (expandCollapse == '-') {
            roleParentCondition += " or CROLEPARENT='" + parent + "'";
        }
        next(addressRoleListGrid1);
    }

    filterCondition = "CROLEGROUP!='' or (CROLEGROUP='' and CROLEPARENT='')";
    if (roleParentCondition != '') {
        filterCondition += roleParentCondition;
    }
    addressRoleListGrid_filter(filterCondition);
    selectRowById("addressRoleListGrid", currentRowId);

    refreshSelectAllStatus(selectAllStatus);
}

//-----------------------------------------------------------------------------
// To handle onclick event for the select checkbox
//-----------------------------------------------------------------------------
function userRowchange(c) {
    switch (c.name) {
        case "chkCSELECT_IND":
            addressRoleListGrid1.recordset("UPDATE_IND").value = 'Y';

            var roleParent = addressRoleListGrid1.recordset("CROLEPARENT").value;
            var roleTypeCode = addressRoleListGrid1.recordset("CROLETYPECODE").value;
            var currentRowId = addressRoleListGrid1.recordset("ID").value;

            if (roleTypeCode == "***") {
                addressRoleListGrid_filter();

                if (c.checked) {
                    // check on all child rows
                    first(addressRoleListGrid1);
                    while (!addressRoleListGrid1.recordset.eof) {
                        var parent = addressRoleListGrid1.recordset("CROLEPARENT").value;
                        var rowId = addressRoleListGrid1.recordset("ID").value;
                        if (parent == roleParent && rowId != currentRowId) {
                            addressRoleListGrid1.recordset("CSELECT_IND").value = -1;
                            addressRoleListGrid1.recordset("UPDATE_IND").value = 'Y';
                        }
                        next(addressRoleListGrid1);
                    }
                } else {
                    // uncheck all child rows
                    first(addressRoleListGrid1);
                    while (!addressRoleListGrid1.recordset.eof) {
                        var parent = addressRoleListGrid1.recordset("CROLEPARENT").value;
                        var rowId = addressRoleListGrid1.recordset("ID").value;
                        if (parent == roleParent && rowId != currentRowId) {
                            addressRoleListGrid1.recordset("CSELECT_IND").value = 0;
                            addressRoleListGrid1.recordset("UPDATE_IND").value = 'Y';
                        }
                        next(addressRoleListGrid1);
                    }
                }

                addressRoleListGrid_filter(filterCondition);
                selectRowById("addressRoleListGrid", currentRowId);
            }
            break;
    }
}

function getSelectAllStatus() {
    var checked = $("#".concat("addressRoleListGrid", "_chkCSELECT_ALL")).hasClass("jqx-checkbox-check-checked");
    return checked;
}

function refreshSelectAllStatus(checked) {
    // The Select All checkbox status will be lost after grid filter, we need to remain the checked/unchecked status after filter
    if (dti.oasis.page.useJqxGrid()) {
        if (checked) {
            $("#".concat("addressRoleListGrid", "_chkCSELECT_ALL")).addClass("jqx-checkbox-check-checked");
        } else {
            $("#".concat("addressRoleListGrid", "_chkCSELECT_ALL")).removeClass("jqx-checkbox-check-checked");
        }
    }
}

function handleChildRows(selectInd) {
    if (!isEmpty(filterCondition)) {
        addressRoleListGrid_filter();
    }

    addressRoleListGrid_updatenode("CSELECT_IND", selectInd);
    addressRoleListGrid_updatenode("UPDATE_IND", "Y");

    if (!isEmpty(filterCondition)) {
        addressRoleListGrid_filter(filterCondition);
    }

    first(addressRoleListGrid1);
    selectFirstRowInGrid("addressRoleListGrid");
}

function handleOnSelectAll(gridId, checked) {
    if (checked) {
        handleChildRows(-1);
        refreshSelectAllStatus(true);
    } else {
        handleChildRows(0);
        refreshSelectAllStatus(false);
    }
}

function displayExpandCollapseImage(row, columnfield, value, defaulthtml, columnproperties) {
    if (value == "+") {
        return "<div align='center'><img src='" + getCorePath() + "/images/plus.gif' onclick='groupExpandCollapse()' style='margin-top:5px'/></div>";
    } else if (value == "-") {
        return "<div align='center'><img src='" + getCorePath() + "/images/minus.gif' onclick='groupExpandCollapse()' style='margin-top:5px'/></div>";
    } else {
        return "";
    }
}

function handleGetCustomPageOptions() {
    return dti.oasis.page.newCustomPageOptions().cellsRenderer("addressRoleListGrid", "CGROUPEXPANDCOLLAPSE", displayExpandCollapseImage);
}