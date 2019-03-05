//-----------------------------------------------------------------------------
// Functions to support Address detail page.
// Author: kshen
// Date:   unknown
// Modifications:
//-----------------------------------------------------------------------------
// 12/02/2008    kshen      Added codes to support system parameter "ZIP_CODE_ENABLE",
//                          "ZIP_OVERRIDE_ADDR", and "CS_SHOW_ZIPCD_LIST".
// 12/03/2008    kshen      Use reuseable methon "setZipCode", and "findZipCode" to
//                          process zipcode.
// 10/30/2009    hxk        Remove logic that was blanking out the postal code
//                          when the provinice was changed.
// 01/15/2010    kshen      Added codes to check zip code format when saving
//                          an address.
// 01/21/2010    Fred       Added function validatePostalCode(iss101585)
// 05/04/2010    Jacky      Issue #105600
// 05/13/2011    Blake      Modified for issue 120677
// 05/02/2013    kshen      Issue 141148.
// 06/21/2013    kshen      Issue 144776
// 05/16/2016    ylu        Issue 169990: clear required property for province field when it become un-required
// 09/18/2018    dzhang     Issue 195835: Grid replacement 1) Remove unused codes.
// 11/09/2018    Elvin      Issue 195835: invoke address province/otherProvince logic
//-----------------------------------------------------------------------------

var cityFldID = "city";
var stateFldID = "stateCode";
var zipcodeFldID = "zipcode";
var isAddrRoleCheckedDuringLoad = false;
var addrUpdaterInvoked = false;
var t_timer;
var timerCount = 0;

function handleOnLoad() {
    // we have validation on city and state if user manually click on finder icon
    // so if user enters zip code directly and wants to auto-populate city and state
    // the auto-find function will pop up js error message, which is incorrect
    // so we need to disable zip code auto-find functionality here
    autoFind_zipcode = false;

    if('Y' == getObjectValue("saveSucceed")) {
        closeWindow("refreshPage");
        return;
    }

    handleAddressFields();

    // do address role validation only when system configured to do so
    if ("Y" == getObjectValue('CS_VALIDATE_ADDXREF')) {
        if (isEmpty(getObjectValue('isTransRolesToNewAddr')) && !isEmpty(getObjectValue('expiringAddressId'))) {
            //Check if old address has entity roles related
            checkNumOfAddrRoleInfo();
            isAddrRoleCheckedDuringLoad = true;
        } else {
            if ("Y" == getObjectValue('primaryAddressB') && "Y" != getObjectValue('isTransRolesToNewAddr') && isEmpty(getObjectValue('expiringAddressId'))) {
                //Check if there are primary address roles
                checkNumOfPrimaryAddrRoleInfo();
            }
        }
    }
}

//-----------------------------------------------------------------------------
// Check Num of Address Roles
//-----------------------------------------------------------------------------
function checkNumOfAddrRoleInfo() {
    OPEN_ADDRESS_ROLE_CHG_PARAMETERS.entityId = getObjectValue('sourceRecordId');
    OPEN_ADDRESS_ROLE_CHG_PARAMETERS.addressId = getObjectValue('expiringAddressId');
    OPEN_ADDRESS_ROLE_CHG_PARAMETERS.isPrimaryAddrChange = "N";
    OPEN_ADDRESS_ROLE_CHG_PARAMETERS.isUsedForChange = "N";
    //following parameters are not used here
    OPEN_ADDRESS_ROLE_CHG_PARAMETERS.callbackEvent = "";
    OPEN_ADDRESS_ROLE_CHG_PARAMETERS.addressDesc = "";
    //Send AJAX to get number of address roles Info
    var url = getCISPath() + "/ciAddressList.do?process=getNumOfAddrRoleInfo&" + getAddressRoleChgParametersUrl();
    new AJAXRequest("get", url, '', afterCheckNumOfAddrRoleInfo, false);
}

//-----------------------------------------------------------------------------
// Check Num of Address Roles
//-----------------------------------------------------------------------------
function afterCheckNumOfAddrRoleInfo(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                if (oValueList[0]["RETURNVALUE"] > 0) {
                    //Open Warning Pop-up
                    OPEN_ADDRESS_ROLE_CHG_PARAMETERS.entityId = getObjectValue('sourceRecordId');
                    OPEN_ADDRESS_ROLE_CHG_PARAMETERS.addressId = getObjectValue('expiringAddressId');
                    OPEN_ADDRESS_ROLE_CHG_PARAMETERS.isPrimaryAddrChange = "N";
                    OPEN_ADDRESS_ROLE_CHG_PARAMETERS.isUsedForChange = "N";
                    //following parameters are not used here
                    OPEN_ADDRESS_ROLE_CHG_PARAMETERS.callbackEvent = "setIsTransRolesToNewAddress";
                    OPEN_ADDRESS_ROLE_CHG_PARAMETERS.addressDesc = "";
                    openAddressRoleChgWarningDivPopup();
                }
            }
        }
    }
}
//-----------------------------------------------------------------------------
// Check Num of Primary Address Roles
//-----------------------------------------------------------------------------
function checkNumOfPrimaryAddrRoleInfo() {
    var isFutureAddress = false;
    var todayDate = formatDate(new Date(), 'mm/dd/yyyy');
    var effectiveFromDate = getObjectValue('effectiveFromDate');

    if (isStringValue(effectiveFromDate)) {
        if (isDate2OnOrAfterDate1(effectiveFromDate, todayDate) == "N") {
            isFutureAddress = true;
        }
    }

    OPEN_ADDRESS_ROLE_CHG_PARAMETERS.entityId = getObjectValue('sourceRecordId');
    // If we are adding an future address,
    // the old primary address will be expired with the new effective from date.
    // So this is a special case.
    OPEN_ADDRESS_ROLE_CHG_PARAMETERS.isPrimaryAddrChange = isFutureAddress ? "N" : "Y";
    OPEN_ADDRESS_ROLE_CHG_PARAMETERS.isUsedForChange = "N";
    //following parameters are not used here
    OPEN_ADDRESS_ROLE_CHG_PARAMETERS.addressId = ""; // Get primary address pk in Server Side
    OPEN_ADDRESS_ROLE_CHG_PARAMETERS.callbackEvent = "";
    OPEN_ADDRESS_ROLE_CHG_PARAMETERS.addressDesc = "";
    //Send AJAX to get number of address roles Info
    var url = getCISPath() + "/ciAddressList.do?process=getNumOfPrimaryAddrRoleInfo&" + getAddressRoleChgParametersUrl();
    new AJAXRequest("get", url, '', afterCheckNumOfPrimaryAddrRoleInfo, false);
}

//-----------------------------------------------------------------------------
// Check Num of Primary Address Roles
//-----------------------------------------------------------------------------
function afterCheckNumOfPrimaryAddrRoleInfo(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                if (oValueList[0]["RETURNVALUE"] > 0) {
                    var isFutureAddress = false;
                    var todayDate = formatDate(new Date(), 'mm/dd/yyyy');
                    var effectiveFromDate = getObjectValue('effectiveFromDate');

                    if (isStringValue(effectiveFromDate)) {
                        if (isDate2OnOrAfterDate1(effectiveFromDate, todayDate) == "N") {
                            isFutureAddress = true;
                        }
                    }
                    //Open Warning Pop-up
                    OPEN_ADDRESS_ROLE_CHG_PARAMETERS.entityId = getObjectValue('sourceRecordId');
                    OPEN_ADDRESS_ROLE_CHG_PARAMETERS.addressId = oValueList[0]["PRIMARYADDRESSID"];
                    // If we are adding an future address,
                    // the old primary address will be expired with the new effective from date.
                    // So this is a special case.
                    OPEN_ADDRESS_ROLE_CHG_PARAMETERS.isPrimaryAddrChange = isFutureAddress ? "N": "Y";
                    OPEN_ADDRESS_ROLE_CHG_PARAMETERS.isUsedForChange = "N";
                    //following parameters are not used here
                    OPEN_ADDRESS_ROLE_CHG_PARAMETERS.callbackEvent = "setIsTransRolesToNewAddress";
                    OPEN_ADDRESS_ROLE_CHG_PARAMETERS.addressDesc = "";
                    openAddressRoleChgWarningDivPopup();
                }
            }
        }
    }
}

function setIsTransRolesToNewAddress() {
    setObjectValue('isTransRolesToNewAddr', SELECTED_ADDRESS_ROLE_CHG_INFO.isTransRolesToNewAddress);

    if ("N" == SELECTED_ADDRESS_ROLE_CHG_INFO.isTransRolesToNewAddress) {
        // Cancel role transfer when changing address
        if (isAddrRoleCheckedDuringLoad) {
            closeWindow();
        } else {
            // Cancel role transfer when adding a new primary address
            getObject('primaryAddressB').checked = false;
        }
    }
}

//-----------------------------------------------------------------------------
// OnChange event handler
//-----------------------------------------------------------------------------
function handleOnChange(field) {
    if (field.name == getAddressFieldId("ZIP_CODE")
        || field.name == getAddressFieldId("ZIP_PLUS_FOUR")
        || field.name == getAddressFieldId("ZIP_CODE_FOREIGN")
        || field.name == getAddressFieldId("STATE_CODE")
        || field.name == getAddressFieldId("CITY")
        || field.name == getAddressFieldId("COUNTRY_CODE")) {
        if (!addressFieldChanged(field)) {
            event.returnValue = false;
            return;
        }
    }

    if (field.name == 'primaryAddressB') {
        if ("Y" == field.value && "Y" == getObjectValue('CS_VALIDATE_ADDXREF') && "Y" != getObjectValue('isTransRolesToNewAddr') && isEmpty(getObjectValue('expiringAddressId'))) {
            //Check if there are primary address roles
            checkNumOfPrimaryAddrRoleInfo();
        } else if (isEmpty(getObjectValue('expiringAddressId'))) {
            setObjectValue('isTransRolesToNewAddr', "N");
        }

        if ("Y" == field.value) {
            setObjectValue("effectiveToDate", "01/01/3000");
        }
    } else if (field.name == "effectiveToDate") {
        if ("Y" == getObjectValue("primaryAddressB") && field.value != "01/01/3000") {
            alert(getMessage("ci.entity.addressDetail.error.primaryAddressToDate"));
            event.returnValue = false;
            return;
        }
    }
}

function handleOnButtonClick(btnId) {
    if (btnId == 'CANCEL') {
        if (isPageDataChanged() && !confirm(getMessage("js.lose.changes.confirmation"))) {
            return;
        } else {
            closeWindow("refreshPage");
        }
    } else if (btnId == 'SAVE') {
        if (addrUpdaterInvoked == true) {
            t_timer = setInterval("checkIfCityStateZipUpdated()", 1000);
            return;
        }

        if (!validate(document.forms[0], true)) {
            return;
        }

        if (!validateBeforeSave()) {
            return;
        }

        // only when we are doing Add Address, or Add Address Copy, we can change the primaryAddressB field
        // if we are doing bulk modify, no need to confirm primary address changing (primaryAddressB will not be changed in bulk modify)
        // if expiringAddressId is not empty which means we are updating an existing primary (future) address, primaryAddressB is read only
        if ("Y" == getObjectValue("primaryAddressB")) {
            if (isEmpty(getObjectValue('bulkModifyAddressId')) && isEmpty(getObjectValue('expiringAddressId'))) {
                if (!confirm(getMessage('ci.entity.addressDetail.confirm.primaryAddressChange'))) {
                    return false;
                }
            }

            //if current address is not an exists future primary address, check if the address list includes exists future primary address.
            var todayDate = formatDate(new Date(), 'mm/dd/yyyy');
            var currentFromDate = getObjectValue('effectiveFromDate');
            if (getObjectValue("isFuturePrimaryAddressB") != 'Y' && isDate2OnOrAfterDate1(currentFromDate, todayDate) == 'N') {
                var parentWindow = getParentWindow();
                if (parentWindow && parentWindow.hasFuturePrimaryAddress) {
                    if (parentWindow.hasFuturePrimaryAddress()) {
                        alert(getMessage("ci.entity.message.address.onlyOneFuturePrimaryAddressAllowed"));
                        return false;
                    }
                }

            }
        }

        setObjectValue("process", "saveAddressDetail");
        submitFirstForm();
    }
}

//-----------------------------------------------------------------------------
// specific validation rules
//-----------------------------------------------------------------------------
function validateBeforeSave() {
    if (!validateCommonAddressFields()) {
        return false;
    }

    var originalFromDate = getObjectValue('effectiveFromDateOriginal');
    var currentFromDate = getObjectValue('effectiveFromDate');
    var currentToDate = getObjectValue("effectiveToDate");
    var todayDate = formatDate(new Date(), 'mm/dd/yyyy');

    // basic from-to date validation
    if (isDate2OnOrAfterDate1(currentFromDate, currentToDate) == 'N') {
        alert(getMessage("ci.address.error.wrongEffectiveDate", new Array(currentToDate, currentFromDate)));
        return false;
    }

    // when changing current primary address, the new address's effective from date must be on or after the old one.
    if (!isEmpty(getObjectValue('expiringAddressId'))) {
        if (isDate2OnOrAfterDate1(originalFromDate, currentFromDate) == 'N') {
            alert(getMessage("ci.entity.message.address.effectiveDate", new Array(currentFromDate, originalFromDate)));
            return false;
        }
    }

    if ("Y" == getObjectValue('primaryAddressB') || "F" == getObjectValue('primaryAddressB')) {
        // check whether system parameter is configured to validate primary address effective from date must be on or after today
        if ("Y" == getObjectValue('ADDR_EFF_ATTER_TODAY')) {
            if (isDate2OnOrAfterDate1(todayDate, currentFromDate) == "N") {
                alert(getMessage('ci.entity.addressDetail.error.primaryAddressFromDate'));
                return false;
            }
        }

        // if this is a future primary address, alert a message to user about the roles auto-changing logic
        if (isDate2OnOrAfterDate1(currentFromDate, todayDate) == "N") {
            alert(getMessage("ci.entity.addressDetail.error.transferRoleLater"));
        }

        // primary address effective to date cannot be updated manually
        // only when we add future primary address, the previous effective to date can be changed automatically
        if (isStringValue(currentToDate)) {
            if (currentToDate != "01/01/3000") {
                alert(getMessage("ci.entity.addressDetail.error.primaryAddressToDate"));
                return false;
            }
        }
    }
    return true;
}

function find(findId) {
    zipFieldsFind(findId);
}

function checkIfCityStateZipUpdated() {
    // check that all 3 fields are populated. If so, cancel timer
    timerCount++;
    if (isValue(getObject(cityFldID)) && isValue(getObject(stateFldID)) && isValue(getObject(zipcodeFldID))) {
        addrUpdaterInvoked = false;
        timerCount = 0;
        clearInterval(t_timer);
        handleOnButtonClick("SAVE");
    }
    // if the fields are set in a few tries, then allow save to continue.
    if (timerCount >= 5) {
        addrUpdaterInvoked = false;
        timerCount = 0;
        clearInterval(t_timer);
        handleOnButtonClick("SAVE");
    }
}

function checkChangeStatusBeforeClose() {
    var ok = true;
    if (isPageDataChanged()) {
        if (!confirm(getMessage("js.lose.changes.confirmation", new Array(""))))
            ok = false;
    }
    return  ok;
}
