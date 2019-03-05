//-----------------------------------------------------------------------------
// Functions to support Address List page.
// Author: kshen
// Date:   unknown
// Modifications:
//-----------------------------------------------------------------------------
// 02/05/2007    kshen       If the address to be modified is primary address,
//                           open add new address window to add a new address to
//                           replace current page. If the address is not primary
//                           address, open a expire window, so users can expire
//                           a non-primary address, without creating a new address.
// 10/20/2008    kshen       Changed to add usa address/foreign address with one
//                           same page.
// 11/27/2008    Leo         Change for issue 88568.
// 11/27/2008    Leo         Change for issue 105792, Mail Option.
// 04/15/2011    Michael Li   Change for issue 119537
// 08/09/2011    Michael Li   Change for issue 101250
// 08/09/2011    kshen       Changed for issue 123063.
// 08/11/2011    kshen       Changed for issue 99502.
// 09/21/2011    Michael Li   Change for issue 101250
// 10/20/2011    Michael Li   Change for issue 126099
// 11/28/2011    Michael Li   Change for issue 127482
// 03/19/2012    Parker       Change for issue 130837,pop up message to inform user select one record
// 04/24/2012    kshen        Issue 131983
// 12/25/2012    bzhu         Issue 127521
// 05/02/2013    kshen        Issue 141148
// 06/20/2013    kshen        Issue 146004
// 07/01/2013   hxk           Issue 141840
//                            If the entity is readonly, don't allow change window to open
// 05/21/2014    kshen        Issue 154094. If the entity is readonly, don't allow the
//                            button Chg Address Role be enabled.
// 06/06/2014    kshen        Issue 154094. Check changing address role button status when page was loaded.
// 01/19/2015    bzhu         Issue 159103
// 08/18/2016    dpang        Issue 178432 - Change isOkToChangePages to avoid duplicated warning message being popped up.
// 02/22/2017    dzhang       Issue 179102: Detail form should not be displayed when the grid is empty.
// 09/28/2018    Elvin        Issue 195351: use the highlighted row instead of selected keys when adding address copy
// 10/16/2018    Elvin        Issue 195835: grid replacement
// 10/31/2018    dzhang       Issue 195835: 1)Specific active and inactive filter condition
//                                          2)Add function to check if has exists future primary address.
// 11/09/2018    jdingle      Issue 196492: Delay check for role until after grid is loaded.
// 11/28/2018    ylu          Issue 195886: item22-decrease lock policy popup page's height.
//-----------------------------------------------------------------------------

function handleOnLoad() {
    $.when(dti.oasis.grid.getLoadingPromise("addressListGrid")).then(function () {
        if (getTableProperty(getTableForGrid("addressListGrid"), "hasrows")) {
            handleButtons();

            if (!dti.oasis.page.useJqxGrid()) {
                populateCountyLOV();
            }
        }
    });
}

function addressListGrid_selectRow(pk) {
    handleButtons();
}

function handleButtons() {
    if (isEntityReadOnlyYN === "Y") {
        // disable all buttons
        enableDisableField(getObject(addressfields.BTN_CI_ADDR_CHG_ROLE), true);
        enableDisableField(getObject(addressfields.BTN_CI_ADDR_PRIMARY_ADDR), true);
        enableDisableField(getObject(addressfields.BTN_CI_ADRLST_ADD), true);
        enableDisableField(getObject(addressfields.BTN_CI_ADDR_COPY), true);
        enableDisableField(getObject(addressfields.BTN_CI_ADDR_ADD_COPY), true);
        enableDisableField(getObject(addressfields.BTN_CI_ADDR_BULK_MODIFY), true);
    } else {
        if (addressListGrid1.recordset("CPRIMARYADDRESSB").value === "N") {
            enableDisableField(getObject(addressfields.BTN_CI_ADDR_PRIMARY_ADDR), false);
        } else {
            enableDisableField(getObject(addressfields.BTN_CI_ADDR_PRIMARY_ADDR), true);
        }

        setStatusOfChgAddressRoleButton(addressListGrid1.recordset("ID").value);
    }
}

function handleOnChange(field) {
    if (field.name === 'USAAddress_num1') {
        if (field.value === 0) {
            setInputFormField('USAAddress_char5', 'N');
        } else {
            setInputFormField('USAAddress_char5', 'Y');
        }
    }
    if (field.name === 'USAAddress_addressTypeCode') {
        clearUSADependFieldsValue();
    }
}

function handleOnButtonClick(asBtn) {
    switch (asBtn.toUpperCase()) {
        case 'ADDADDRESS':
            openAddWin();
            break;
        case 'COPYADDRESS':
            copyAddress();
            break;
        case 'REFRESH':
            setObjectValue("process", "loadAddressList");
            submitFirstForm();
            break;
        case 'CHANGEADDRESSROLE':
            openChgAddressRoleWin();
            break;
        case 'PRIMARYADDRESS':
            changePrimaryAddress();
            break;
        case 'ADDCOPY':
            addAddressCopy();
            break;
        case 'BULKMODIFY':
            bulkModifyAddress();
            break;
        case 'SAVE':
            if (!handleOnMailOption()) {
                break;
            }

            if (!validate(document.forms[0], true)) {
                return;
            }

            var subData = true;
            var fields = getObjectValue(addressfields.CI_ADDR_PCT_PRAC_FLD).split(",");
            var types =  getObjectValue(addressfields.CI_ADDR_PCT_PRAC_TYP);
            if (fields && types) {
                for (var selIndex = 0; selIndex < fields.length; selIndex++) {
                    if (fields[selIndex] && fields[selIndex] !== '') {
                        if (validatePercent(addressListGrid1,types, fields[selIndex])) {
                            // continue validate
                            continue;
                        } else {
                            subData = false;
                            break;
                        }
                    }
                }
            }
            if (subData) {
                setObjectValue("process", "saveAllAddress");
                addressListGrid_update();
            }
            break;
        default:
            break;
    }
}

function refreshPage() {
    commonOnButtonClick('refresh');
}

//-----------------------------------------------------------------------------
// open the address detail add popup window
//-----------------------------------------------------------------------------
function openAddWin() {
    var path = getCISPath() + "/ciAddressAdd.do?process=loadAddressDetail&sourceTableName=ENTITY";
    path += "&sourceRecordId=" + getObjectValue("pk");

    if (getObjectValue(addressfields.ENTITY_LOCK_FLAG) === 'Y' && getObjectValue(addressfields.CS_ALLOWADDLOCKEDPOL) === 'N') {
        handleAddressLocked();
        return;
    }
    openDivPopup("AddressDetail", path, true, true, 20, 10, 900, 600, "", "", "", true, "", "", true);
}

//-----------------------------------------------------------------------------
// Change an primary address, or expire a non-primary address
//-----------------------------------------------------------------------------
function changeOrExpireAddress(pk) {
    if (isEntityReadOnlyYN === "Y") {
        return;
    }

    getRow(addressListGrid1, pk);
    var sourceRecordId = addressListGrid1.recordset("CSOURCERECORDID").value;
    var addressExpiredB = addressListGrid1.recordset("CADDRESSEXPIREDB").value;
    var mailOptionFlag = addressListGrid1.recordset("CADDRINMAILOPTION").value;
    var primaryAddressFlag = addressListGrid1.recordset("CPRIMARYADDRESSB").value;

    if (addressExpiredB === "Y") {
        alert(getMessage("ci.entity.addressesList.error.expiredAddressReadOnly"));
        return;
    }

    // Do not allow changes to an address used in the mail option.
    if (mailOptionFlag === "Y") {
        alert(getMessage("ci.entity.addressesList.error.addressUsedInMailOption"));
        return;
    }

    if (getObjectValue(addressfields.ENTITY_LOCK_FLAG) === "Y") {
        if (primaryAddressFlag === "Y" || primaryAddressFlag === "F") {
            handleAddressLocked();
            return;
        } else {
            if (getObjectValue(addressfields.CS_ALLOWADDLOCKEDPOL) === "N") {
                handleAddressLocked();
                return;
            }
        }
    }

    var path = "";
    if (primaryAddressFlag === "Y" || primaryAddressFlag === "F") {
        // If the address to be modified is primary address, open add new address window to add a new address to replace current address
        path = getCISPath() + "/ciAddressAdd.do?process=loadAddressDetail&sourceTableName=ENTITY";
        path += "&sourceRecordId=" + sourceRecordId;
        path += "&expiringAddressId=" + pk;
        openDivPopup("AddressDetail", path, true, true, 20, 10, 900, 600, "", "", "", true, "", "", true);
    } else {
        // If it is not primary address, open expire address popup window to expire the address directly
        path = getCISPath() + "/ciAddressExpire.do?process=loadAddressForExpire&addressId=" + pk;
        openDivPopup("Address Expire", path, true, true, 20, 10, 900, 600, "", "", "", true, "", "", true);
    }
}

function copyAddress() {
    var url = getCISPath() + "/ciAddressCopy.do?entityId=" + getObjectValue("pk") + "&addressId=" + addressListGrid1.recordset("ID").value;
    openDivPopup("", url, true, true, 20, 10, 900, 430, "", "", "", true, "", "", true);
}

function handleAddressLocked() {
    if (getObjectValue(addressfields.CS_SHOWLOCKEDPOL) === "Y") {
        if (confirm(getMessage("ci.entity.addressesList.error.lockedAddress"))) {
            var url = getCISPath() + "/policymgr/displayLockedPolicy.do?process=loadAllLockedPolicy&entityId=" + getObjectValue("pk");
            openDivPopup("", url, true, true, "", "", "", "400", "", "", "", true);
        }
    } else {
        alert(getMessage("ci.entity.addressesList.error.modifyNotAllowed"));
    }
}

function getGridId() {
    return 'addressListGrid';
}

//-----------------------------------------------------------------------------
// Set Status Of Chg Address Role Button
//-----------------------------------------------------------------------------
function setStatusOfChgAddressRoleButton(pk) {
    if (hasObject(addressfields.BTN_CI_ADDR_CHG_ROLE)) {
        OPEN_ADDRESS_ROLE_CHG_PARAMETERS.entityId = getObjectValue("pk");
        OPEN_ADDRESS_ROLE_CHG_PARAMETERS.addressId = pk;
        OPEN_ADDRESS_ROLE_CHG_PARAMETERS.isPrimaryAddrChange = "N";
        OPEN_ADDRESS_ROLE_CHG_PARAMETERS.isUsedForChange = "Y";
        //following parameters are not used here
        OPEN_ADDRESS_ROLE_CHG_PARAMETERS.callbackEvent = "";
        OPEN_ADDRESS_ROLE_CHG_PARAMETERS.addressDesc = "";
        //Send AJAX to get number of address roles Info
        var action = getFormActionAttribute();
        var url = getCISPath() + "/" + action +
            "?process=getNumOfAddrRoleInfo&"+"&currectTime=" + Date.parse(new Date())+"&" + getAddressRoleChgParametersUrl();
        new AJAXRequest("get", url, '', afterGetNumOfAddrRoleInfo, false);
    }
}

//-----------------------------------------------------------------------------
// Handle the processing after getting number of address roles Info
//-----------------------------------------------------------------------------
function afterGetNumOfAddrRoleInfo(ajax) {
    if (ajax.readyState === 4) {
        if (ajax.status === 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                if (oValueList[0]["RETURNVALUE"] > 0) {
                    enableDisableField(getObject(addressfields.BTN_CI_ADDR_CHG_ROLE), false);
                } else {
                    enableDisableField(getObject(addressfields.BTN_CI_ADDR_CHG_ROLE), true);
                }
            }
        }
    }
}

function openChgAddressRoleWin() {
    OPEN_ADDRESS_ROLE_CHG_PARAMETERS.entityId = getObjectValue("pk");
    OPEN_ADDRESS_ROLE_CHG_PARAMETERS.addressId = addressListGrid1.recordset("ID").value;
    OPEN_ADDRESS_ROLE_CHG_PARAMETERS.addressDesc = addressListGrid1.recordset("CADDRESSSINGLELINE").value;
    OPEN_ADDRESS_ROLE_CHG_PARAMETERS.isPrimaryAddrChange = "N";
    OPEN_ADDRESS_ROLE_CHG_PARAMETERS.isUsedForChange = "Y";
    OPEN_ADDRESS_ROLE_CHG_PARAMETERS.callbackEvent = "refreshPage";
    openAddressRoleChgPopup();
}

function changePrimaryAddress() {
    if (confirm(getMessage("ci.entity.addressesList.confirm.changeToPrimaryAddress"))) {
        if (addressListGrid1.recordset("CEFFECTIVETODATE").value !== "01/01/3000") {
            if (!confirm(getMessage("ci.entity.addressesList.confirm.primaryAddressToDate"))) {
                return;
            }
        }

        var effectiveFromDate = addressListGrid1.recordset("CEFFECTIVEFROMDATE").value;
        var todayDate = formatDate(new Date(), 'mm/dd/yyyy');
        if (isDate2OnOrAfterDate1(effectiveFromDate, todayDate) === "N") {
            alert(getMessage("ci.entity.addressDetail.error.transferRoleLater"));
        }

        var newPrimaryAddressId = addressListGrid1.recordset("ID").value;
        setInputFormField("newPrimaryAddressId", newPrimaryAddressId);
        setInputFormField("process", "changePrimaryAddress");
        addressListGrid_update();
    }
}

function validatePercent(grid, types, field) {
    var fld = 0;
    var total = 0;
    var rs;

    var isNull=false;
    var hasPercentValue = false;

    first(grid);
    for (i = 0; i < grid.recordset.recordCount; i++) {
        if (grid.recordset("CCHANGELINK").value === "Change" && types.indexOf(grid.recordset("CADDRESSTYPECODE").value) !== -1) {
            fld = grid.recordset('C' + field).value;
            if (fld == null || fld === '') {
                isNull = true;
                fld = '';
            } else {
                hasPercentValue = true;
            }

            if (fld !== '') {
                if (!checkPercent(fld)) {
                    selectRowById("addressListGrid", addressListGrid1.recordset("ID").value);
                    alert(fld + " is invalid");
                    return false;
                }
                rs = fld.split("%");
                if (rs[0] != null) {
                    total += parseFloat(rs[0])/100;
                }
            }
        }
        next(grid);
    }

    if (isNull && total !== 0) {
        var msgType = getObjectValue(addressfields.CI_ADDR_PCT_PRAC_MSG);
        if (msgType === "WARNING" || msgType === "ERROR") {
            alert(getMessage("ci.entity.addressesList.error.percentNotEntered"));
        }
        if (msgType === "ERROR") {
            first(addressListGrid1);
            selectFirstRowInGrid("addressListGrid");
            return false;
        }
    }

    if (hasPercentValue && total.toFixed(4) !== 1.0000) {
        alert(getMessage("ci.entity.addressesList.error.percentInvalid"));
        first(addressListGrid1);
        selectFirstRowInGrid("addressListGrid");
        return false;
    }

    return true;
}

//-----------------------------------------------------------------------------
// check if the percent is valid
//-----------------------------------------------------------------------------
function checkPercent(fldvalue) {
    if (fldvalue.indexOf('%') === -1) {
        fldvalue += '%';
    }
    var pattern = /^((\d{1,3}\.\d{1,3}\%)|(\d{1,3}\%))$/;
    return (pattern.test(fldvalue));
}

function addAddressCopy() {
    var path = getCISPath() + "/ciAddressAdd.do?process=loadAddressDetail&sourceTableName=ENTITY";
    path += "&sourceRecordId=" + getObjectValue("pk");
    path += "&copyAddressId=" + addressListGrid1.recordset("ID").value;
    openDivPopup("AddressDetail", path, true, true, 20, 10, 900, 600, "", "", "", true, "", "", true);
}

function CIAddressListForm_btnClick(asBtn) {
    switch (asBtn) {
        case 'SELECT':
            addressListGrid_updatenode("CSELECT_IND", -1);
            first(addressListGrid1);
            gotopage(addressListGrid, 'F');
            selectFirstRowInGrid("addressListGrid");
            break;
        case 'DESELECT':
            addressListGrid_updatenode("CSELECT_IND", 0);
            first(addressListGrid1);
            gotopage(addressListGrid, 'F');
            selectFirstRowInGrid("addressListGrid");
            break;
    }
}

function bulkModifyAddress() {
    // Check if row selected.
    var selectedKeys = getSelectedKeys(addressListGrid1);
    if (selectedKeys.length === 0) {
        alert(getMessage("ci.common.error.row.noSelect"));
        return;
    }

    // Check if any address is expired.
    var selectedKeysStr = "";
    for (var i = 0; i < selectedKeys.length; i++) {
        var addressExpiredB = addressListGrid1.documentElement.selectNodes("//ROW[@id='" + selectedKeys[i] + "']/CADDRESSEXPIREDB")[0].text;
        if (addressExpiredB === 'Y') {
            alert(getMessage("ci.entity.addressesList.error.expiredAddressReadOnly"));
            return;
        }

        // primary address cannot do bulk modify since the change of primary address will affect a lot
        // we cannot just simply update a primary address information, especially its effective to date
        var primaryAddressB = addressListGrid1.documentElement.selectNodes("//ROW[@id='" + selectedKeys[i] + "']/CPRIMARYADDRESSB")[0].text;
        if (primaryAddressB === 'Y' || primaryAddressB === 'F') {
            alert(getMessage("ci.entity.addressesList.error.primaryAddressNoBulkModify"));
            return;
        }

        if (selectedKeysStr !== "") {
            selectedKeysStr += ",";
        }
        selectedKeysStr += selectedKeys[i];
    }

    var path = getCISPath() + "/ciAddressAdd.do?process=loadAddressDetail&sourceTableName=ENTITY";
    path += "&sourceRecordId=" + getObjectValue("pk");
    path += "&bulkModifyAddressId=" + selectedKeysStr;
    openDivPopup("AddressDetail", path, true, true, 20, 10, 900, 600, "", "", "", true, "", "", true);
}

function handleOnMailOption() {
    var hasMailOption = false;
    first(addressListGrid1);
    for (var i = 0; i < addressListGrid1.recordset.recordCount; i++) {
        if (addressListGrid1.recordset("UPDATE_IND").value === "Y" && addressListGrid1.recordset("CADDRINMAILOPTION").value === "Y") {
            hasMailOption = true;
            selectRowById("addressListGrid", addressListGrid1.recordset("ID").value);
            break;
        }
        next(addressListGrid1);
    }

    if (hasMailOption && !confirm(getMessage("ci.entity.addressesList.confirm.beforeSaveMailOption"))) {
        return false;
    }
    return true;
}

function clearUSADependFieldsValue() {
    //Clear 
    if (hasObject('USAAddress_num1') && isFieldHidden("USAAddress_num1")) {
        setObjectValue("USAAddress_num1", "");
    }
}

// LOV for county was not populating entirely when multiple states are in the grid.
function populateCountyLOV() {
    first(addressListGrid1);
    while (!addressListGrid1.recordset.eof) {
        fireAjaxForSelectedRow("addressListGrid", addressListGrid1.recordset("id").value);
        next(addressListGrid1);
    }
    first(addressListGrid1);
}

function handleGetCustomPageOptions() {
    return dti.oasis.page.newCustomPageOptions().enableDependencyDropdownListColumn();
}

//Get address filter string
function handleGetFilter() {
    var filter = '';
    var filterValue = getRadioButtonValue(getObject("ListFilter"));
    var today = getToday();

    if (filterValue === 'ACTIVE') {
        filter = "( ";
        filter = filter + "(concat(substring(substring(concat(CEFFECTIVETODATE,'01/01/3000'),1,10),7,4),substring(substring(concat(CEFFECTIVETODATE,'01/01/3000'),1,10),1,2),substring(substring(concat(CEFFECTIVETODATE,'01/01/3000'),1,10),4,2)) > " + today ;
        filter = filter + " and CEFFECTIVEFROMDATE != CEFFECTIVETODATE )";
        filter = filter + " )";
    } else if (filterValue === 'EXPIRED') {
        filter = "( ";
        filter = filter + " concat(substring(substring(concat(CEFFECTIVETODATE,'01/01/3000'),1,10),7,4),substring(substring(concat(CEFFECTIVETODATE,'01/01/3000'),1,10),1,2),substring(substring(concat(CEFFECTIVETODATE,'01/01/3000'),1,10),4,2)) <= " + today;
        filter = filter + " or ";
        filter = filter + "  CEFFECTIVEFROMDATE = CEFFECTIVETODATE";
        filter = filter + " )";
    }

    return filter;
}

/**
 * check if has exists future primary address
 * @returns {boolean}
 */
function hasFuturePrimaryAddress() {
    var hasFuturePrimayreAddrss = false;
    var currentRowId = addressListGrid1.recordset("ID").value;

    first(addressListGrid1);
    while (!addressListGrid1.recordset.eof) {
        if ("F" === addressListGrid1.recordset("CPRIMARYADDRESSB").value) {
            hasFuturePrimayreAddrss = true;
            break;
        }
        next(addressListGrid1);
    }

    first(addressListGrid1);
    getRow(addressListGrid1, currentRowId);

    return hasFuturePrimayreAddrss;
}