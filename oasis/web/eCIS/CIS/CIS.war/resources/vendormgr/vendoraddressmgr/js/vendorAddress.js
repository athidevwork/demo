//-----------------------------------------------------------------------------
// Functions to support Vendor Address page.
// Author: kshen
// Date:   unknown
// Modifications:
//-----------------------------------------------------------------------------
// 12/03/2008    kshen      Use reuseable methon "setZipCode", and "findZipCode" to 
//                          process zipcode.
// 01/15/2010    kshen      Added codes to show/hide usa/non-usa address fields
// 05/04/2010    Jacky      Issue #105600
// 07/13/2010    kshen      Set the USA Address Flag if the country is Changed.
// 08/01/2014    ylu        155367: for a given & exact zip code, not to trigger auto-find,
//                          which same as that of maintainAddressDetail.js & csZipLookup.js
// 08/21/2014    Elvin      Issue 155305: show different Province field between CAN and other countries
// 08/25/2014    Elvin      Issue 155305: invoke COUNTRY_CODE_CONFIG to show province field
// 11/27/2014    bzhu       Issue 159450
//                          1. clear address_zipPlusFour when switching address_usaAddressB to N.
//                          2. set address_countryCode to USA when switching address_usaAddressB to Y.
// 05/16/2016    ylu        Issue 169990: clear required property for province field when it become un-required
// 11/09/2018    Elvin      Issue 195835: invoke address province/otherProvince logic
//-----------------------------------------------------------------------------

function handleOnLoad() {
    // we have validation on city and state if user manually click on finder icon
    // so if user enters zip code directly and wants to auto-populate city and state
    // the auto-find function will pop up js error message, which is incorrect
    // so we need to disable zip code auto-find functionality here
    autoFind_zipcode = false;

    handleAddressFields();
}

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
}

function handleOnButtonClick(btnId) {
    if (btnId == 'REFRESH') {
        if (isPageDataChanged() && !confirm(getMessage("js.lose.changes.confirmation"))) {
            return;
        } else {
            setObjectValue("process", "loadVendorAddress");
            submitFirstForm();
        }
    }
}

function handleOnSubmit(action) {
    var proceed = false;
    switch (action) {
        case 'SAVE':
            if (!validate(document.forms[0], true)) {
                return;
            }

            if (!validateBeforeSave()) {
                return;
            }

            setObjectValue("process", "saveVendorAddress");
            proceed = true;
            break;
        default:
            proceed = false;
    }
    return proceed;
}

function validateBeforeSave() {
    if (!validateCommonAddressFields()) {
        return false;
    }
    return true;
}

function find(findId) {
    zipFieldsFind(findId);
}