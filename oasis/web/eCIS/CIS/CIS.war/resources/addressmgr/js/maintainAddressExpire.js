//-----------------------------------------------------------------------------
// Functions to support Address Expire page.
// Author: kshen
// Date:   02/02/2007
// Modifications:
//-----------------------------------------------------------------------------
// 02/07/2007   kshen  Added dummy function checkForm, handleOnChange. If there
//                     is no function checkForm, page expire address would raise
//                     a javascript error. Moved function testDateBefore, this
//                     function hasn't been used.
// 10/17/2008   kshen  Changed  to  open  add  address page instead of  add  usa
//                     address page/add foreign address page.
// 10/06/2010   wfu    111776: Replaced hardcode string with resource definition
// 09/28/2018   Elvin  Issue 195344: passing entered effectiveToDate to Add Address window if creating new address
//-----------------------------------------------------------------------------

function handleOnButtonClick(btnId) {
    if (btnId == 'CANCEL') {
        if (isPageDataChanged() && !confirm(getMessage("js.lose.changes.confirmation"))) {
            return;
        } else {
            closeWindow("refreshPage");
            return;
        }
    } else if (btnId == 'SAVE') {
        if (!validate(document.forms[0])) {
            return;
        }

        if (!validateBeforeSave()) {
            return;
        }

        if (confirm(getMessage("ci.entity.message.address.createNew", new Array("\n")))) {
            var path = "ciAddressAdd.do?process=loadAddressDetail&sourceTableName=ENTITY";
            path += "&sourceRecordId=" + getObjectValue("sourceRecordId");
            path += "&expiringAddressId=" + getObjectValue("addressId");
            path += "&effectiveToDate=" + getObjectValue("effectiveToDate");

            // open add address window
            setWindowLocation(path);
            return;
        } else {
            if  ("Y" == getObjectValue("CS_VALIDATE_ADDXREF")) {
                OPEN_ADDRESS_ROLE_CHG_PARAMETERS.entityId = getObjectValue("sourceRecordId");
                OPEN_ADDRESS_ROLE_CHG_PARAMETERS.addressId = getObjectValue("addressId");
                OPEN_ADDRESS_ROLE_CHG_PARAMETERS.addressDesc = "";
                OPEN_ADDRESS_ROLE_CHG_PARAMETERS.isPrimaryAddrChange = getObjectValue("primaryAddressB");
                OPEN_ADDRESS_ROLE_CHG_PARAMETERS.isUsedForChange = "Y";
                OPEN_ADDRESS_ROLE_CHG_PARAMETERS.callbackEvent = "refreshPage";

                var url = getCISPath() + "/ciAddressList.do?process=getNumOfAddrRoleInfo&" + getAddressRoleChgParametersUrl();
                new AJAXRequest("get", url, '', afterGetNumOfAddrRoleInfo, false);
            } else {
                expireAddress();
            }
        }
    }
}

//-----------------------------------------------------------------------------
// specific validation rules
//-----------------------------------------------------------------------------
function validateBeforeSave() {
    var effectiveFromDate = getObjectValue('effectiveFromDate');
    var effectiveToDate = getObjectValue('effectiveToDate');
    if (isDate2OnOrAfterDate1(effectiveFromDate, effectiveToDate) == 'N') {
        alert(getMessage("ci.address.error.wrongEffectiveDate", new Array(effectiveToDate, effectiveFromDate)));
        return false;
    }

    return true;
}

function afterGetNumOfAddrRoleInfo(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }

            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                if (oValueList[0]["RETURNVALUE"] > 0) {
                    alert(getMessage("ci.entity.message.address.cannotBeExpired"));
                } else {
                    expireAddress();
                }
            }
        }
    }
}

function expireAddress() {
    var url = "ciAddressExpire.do?process=expireAddress";
    url += "&addressId=" + getObjectValue("addressId");
    url += "&effectiveToDate=" + getObjectValue("effectiveToDate");

    new AJAXRequest("POST", url, '', afterExpireAddress, false);
}

function afterExpireAddress(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseText;
            if (data != "Y") {
                var xml = ajax.responseXML;
                if (!handleAjaxMessages(xml, null)) {
                    return;
                }
            } else {
                alert(getMessage("ci.entity.message.address.expiredSuccessfully"));
                closeWindow("refreshPage");
                return;
            }
        }
    }
}