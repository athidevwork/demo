//-----------------------------------------------------------------------------
// Common functions to support address change
// Modifications:
//-----------------------------------------------------------------------------
// 11/09/2018    Elvin       Issue 195835: add handleAddressFields, province/otherProvince switch logic
// 12/10/2018    jdingle     Issue 195835: add handleAddressFields, province/otherProvince switch logic
//-----------------------------------------------------------------------------

function handleAddressFields() {
    var countryCode = getObjectValue(getAddressFieldId("COUNTRY_CODE"));
    if (countryCode == getObjectValue(addressfields.KEY_COUNTRY_CODE_USA)) {
        setFieldValue(getAddressFieldId("USA_ADDRESS_B"), 'Y');
    } else {
        setFieldValue(getAddressFieldId("USA_ADDRESS_B"), 'N');

        if (getObject(getAddressFieldId("PROVINCE")) && !isElementHidden(getObject(getAddressFieldId("PROVINCE")))) {
            switchProvinceField(countryCode);
        }
    }
}

function addressFieldChanged(field) {
    if (field.name == getAddressFieldId("ZIP_CODE")) {
        if (!validateZipOrPostalCode(field.name)) {
            return false;
        } else {
            (new addressUpdater(getAddressFieldId("CITY"), getAddressFieldId("STATE_CODE"), getAddressFieldId("COUNTY_CODE"), getAddressFieldId("ZIP_CODE"))).updateByZipCode();
        }
    } else if (field.name == getAddressFieldId("ZIP_PLUS_FOUR")) {
        if (!validateZipOrPostalCode(field.name)) {
            return false;
        }
    } else if (field.name == getAddressFieldId("ZIP_CODE_FOREIGN")) {
        if (!validateZipOrPostalCode(field.name)) {
            return false;
        }
    } else if (field.name == getAddressFieldId("STATE_CODE") || field.name == getAddressFieldId("CITY")) {
        if ('Y' == getObjectValue(getAddressFieldId("USA_ADDRESS_B"))) {
            if (field.name == getAddressFieldId("STATE_CODE")) {
                (new addressUpdater(getAddressFieldId("CITY"), getAddressFieldId("STATE_CODE"), getAddressFieldId("COUNTY_CODE"), getAddressFieldId("ZIP_CODE"))).updateByUSCityOrState();
            } else {
                (new addressUpdater(getAddressFieldId("CITY"), getAddressFieldId("STATE_CODE"), getAddressFieldId("COUNTY_CODE"), getAddressFieldId("ZIP_CODE"))).updateByUSCity();
            }
        }

        if (getObject(getAddressFieldId("ZIP_PLUS_FOUR"))) {
            setObjectValue(getAddressFieldId("ZIP_PLUS_FOUR"), '');
        }
    } else if (field.name == getAddressFieldId("COUNTRY_CODE")) {
        if (field.value == getObjectValue(addressfields.KEY_COUNTRY_CODE_USA)) {
            setFieldValue(getAddressFieldId("USA_ADDRESS_B"), 'Y');
        } else {
            setFieldValue(getAddressFieldId("USA_ADDRESS_B"), 'N');

            switchProvinceField(field.value);
        }
    }
    return true;
}

function validateCommonAddressFields() {
    if (!validateZipOrPostalCode(getAddressFieldId("ZIP_CODE"))
        || !validateZipOrPostalCode(getAddressFieldId("ZIP_PLUS_FOUR"))
        || !validateZipOrPostalCode(getAddressFieldId("ZIP_CODE_FOREIGN"))
        || !validateAddressType(getAddressFieldId("ADDRESS_TYPE_CODE"))) {
        return false;
    }
    return true;
}

function validateZipOrPostalCode(fieldId) {
    var fieldValue = getObjectValue(fieldId);
    var regExp;
    var continueValidate = false;
    if ('Y' == getObjectValue(getAddressFieldId("USA_ADDRESS_B")) || getObjectValue(getAddressFieldId("COUNTRY_CODE")) == getObjectValue(addressfields.KEY_COUNTRY_CODE_USA)) {
        if (fieldId == getAddressFieldId("ZIP_CODE")) {
            if (!isStringValue(fieldValue)) {
                return true;
            }
            regExp = new RegExp(/(^\d{5}$)|(^\d{5}-\d{4}$)/);
            continueValidate = true;
        } else if (fieldId == getAddressFieldId("ZIP_PLUS_FOUR")) {
            if (!isStringValue(fieldValue)) {
                return true;
            }
            regExp = new RegExp(/(^\d{4}$)/);
            continueValidate = true;
        }
    } else {
        if (fieldId == getAddressFieldId("ZIP_CODE_FOREIGN")) {
            if (!isStringValue(fieldValue)) {
                return true;
            }
            continueValidate = true;
        }

        if (getObjectValue(getAddressFieldId("COUNTRY_CODE")) == getObjectValue(addressfields.KEY_COUNTRY_CODE_CAN)) {
            if (!isStringValue(fieldValue)) {
                return true;
            }
            regExp = new RegExp("^[a-zA-Z]\\d[a-zA-Z] \\d[a-zA-Z]\\d$");
        } else {
            regExp = new RegExp("^[\\s\\S]{0,10}$");
        }
    }

    if (continueValidate && !regExp.test(fieldValue)) {
        alert(getMessage("ci.address.error.invalidZipOrPostalCode", new Array(getLabel(fieldId))));
        return false;
    }
    return true;
}

function validateAddressType(fieldId) {
    var fieldValue = getObjectValue(fieldId);
    if (!isStringValue(fieldValue)) {
        return true;
    }

    if ('N' == getObjectValue(getAddressFieldId("USA_ADDRESS_B")) || getObjectValue(getAddressFieldId("COUNTRY_CODE")) != getObjectValue(addressfields.KEY_COUNTRY_CODE_USA)) {
        if (fieldValue == 'PREMISE') {
            alert(getMessage("ci.address.error.invalidAddressType", [getLabel(getObject(fieldId)), fieldValue]));
            return false;
        }
    }
    return true;
}

function zipFieldsFind(findId) {
    if (findId == getAddressFieldId("ZIP_CODE")) {
        if (!isValue(getObject(getAddressFieldId("CITY"))) || !isValue(getObject(getAddressFieldId("STATE_CODE")))) {
            alert(getMessage("ci.address.error.bothValuesRequired", new Array(getLabel(getAddressFieldId("CITY")), getLabel(getAddressFieldId("STATE_CODE")))));
            return;
        } else {
            var path = getCSPath() + "/ziplookup.do?process=zippopup&address_city=" + getObjectValue(getAddressFieldId("CITY")) + "&address_stateCode=" + getObjectValue(getAddressFieldId("STATE_CODE"));
            var mainWin = window.open(path, 'ciZipLookup', 'width=630,height=500,innerHeight=630,innerWidth=500,scrollbars');
            mainWin.focus();
        }
    } else if (findId == getAddressFieldId("ZIP_CODE_FOREIGN")) {
        if (!isValue(getObject(getAddressFieldId("CITY"))) || !isValue(getObject(getAddressFieldId("PROVINCE")))) {
            alert(getMessage("ci.address.error.bothValuesRequired", new Array(getLabel(getAddressFieldId("CITY")), getLabel(getAddressFieldId("PROVINCE")))));
            return;
        } else {
            var path = getCSPath() + "/ziplookup.do?process=postalcodepopup&address_city=" + getObjectValue(getAddressFieldId("CITY")) + "&address_province=" + getObjectValue(getAddressFieldId("PROVINCE"));
            var mainWin = window.open(path, 'ciZipLookup', 'width=630,height=400,innerHeight=330,innerWidth=500,scrollbars=yes');
            mainWin.focus();
        }
    }
}

function switchProvinceField(countryCode) {
    if (hasObject(getAddressFieldId("PROVINCE")) && hasObject(getAddressFieldId("OTHER_PROVINCE"))) {
        // display province or otherProvince field base on configuration
        if (isCountryCodeConfigured(countryCode)) {
            hideShowFieldById(getAddressFieldId("PROVINCE"), false);
            hideShowFieldById(getAddressFieldId("OTHER_PROVINCE"), true);
        } else {
            hideShowFieldById(getAddressFieldId("PROVINCE"), true);
            hideShowFieldById(getAddressFieldId("OTHER_PROVINCE"), false);
        }
    }
}

function isCountryCodeConfigured(countryCode) {
    var countryCodeArr = getObjectValue("COUNTRY_CODE_CONFIG").split(",");
    for (var i = 0; i < countryCodeArr.length; i++) {
        if (countryCodeArr[i] == countryCode) {
            return true;
        }
    }
    return false;
}

// override this function if current page has different address field ids
function getAddressFieldId(fieldName) {
    switch(fieldName) {
        case "ZIP_CODE":
            return addressfields.ZIP_CODE;
        case "CITY":
            return addressfields.CITY;
        case "STATE_CODE":
            return addressfields.STATE_CODE;
        case "COUNTY_CODE":
            return addressfields.COUNTY_CODE;
        case "ZIP_PLUS_FOUR":
            return addressfields.ZIP_PLUS_FOUR;
        case "ZIP_CODE_FOREIGN":
            return addressfields.ZIP_CODE_FOREIGN;
        case "USA_ADDRESS_B":
            return addressfields.USA_ADDRESS_B;
        case "COUNTRY_CODE":
            return addressfields.COUNTRY_CODE;
        case "PROVINCE":
            return addressfields.PROVINCE;
        case "OTHER_PROVINCE":
            return addressfields.OTHER_PROVINCE;
        case "ADDRESS_TYPE_CODE":
            return addressfields.ADDRESS_TYPE_CODE;
    }
}