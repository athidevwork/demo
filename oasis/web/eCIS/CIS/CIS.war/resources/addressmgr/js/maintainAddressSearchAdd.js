//-----------------------------------------------------------------------------
//  Description: Javascript of address search add
//  Revision Date    Revised By  Description
//  06/10/2015       bzhu        Issue 163122: Move obsolete "<script for=" checkbox selection from JSP to JS.
//  09/30/2015       dpang       Issue 160562: Change the check of formPostOfficeAddressBNumber to suit more display types.
// 04/06/2016        ylu         Issue 169990. accept freedom validation for foreign postal code
//  12/28/2016       dzhang      Issue 178298: Change the check of formUsaAddressBNumber to suit more display types.
//  10/08/2018       Elvin       Issue 195835: grid replacement
//  11/09/2018       Elvin       Issue 195835: invoke address province/otherProvince logic
//  ---------------------------------------------------------------------------

function handleOnLoad() {
    // we have validation on city and state if user manually click on finder icon
    // so if user enters zip code directly and wants to auto-populate city and state
    // the auto-find function will pop up js error message, which is incorrect
    // so we need to disable zip code auto-find functionality here
    autoFind_zipcode = false;

    if ("Y" == getObjectValue(addressfields.READ_ONLY)) {
        enableDisableField(getObject('CI_ADDRS_SCH_ADD_GRID_ADD'), true);
        enableDisableField(getObject('CI_ADDRS_SCH_ADD_GRID_DEL'), true);
        enableDisableField(getObject('CI_ADDRS_SCH_ADD_FORM_SAV'), true);
        enableDisableField(getObject('CI_ADDRS_SCH_ADD_FORM_SEL'), true);
        enableDisableField(getObject('CI_ADDRS_SCH_ADD_FORM_FND'), true);
    }

    // if allowOtherClient is Y, show Find Client button
    hideShowField(getObject('CI_ADDRS_SCH_ADD_FORM_FND'), getObjectValue(addressfields.ALLOW_OTHER_CLIENT) != 'Y');
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

    if (field.name == addressfields.CLIENT_B) {
        // value == Y means allow add address to the pass in entity, if not, the address will have a dummy sourceRecordFk
        if (field.value == 'Y') {
            testgrid1.recordset("CSOURCERECORDID").value = getObjectValue(addressfields.ORIG_SOURCE_RECORD_ID);
            testgrid1.recordset("CSOURCETABLENAME").value = 'ENTITY';
        } else {
            testgrid1.recordset("CSOURCERECORDID").value = getObjectValue(addressfields.DUMMY_SOURCE_RECORD_ID);
            testgrid1.recordset("CSOURCETABLENAME").value = 'DUMMY';
        }
    } else {
        testgrid1.recordset("CADDRESSSINGLELINE").value = formatAddressSingleLine();
    }
}

function testgrid_selectRow(pk) {
    // this page also does not allow to update an existing address (except addressName field)
    // if read only is Y, everything is read only
    // if not, for an existing address, only addressName can be modified
    var updateIndicator = testgrid1.recordset("UPDATE_IND").value;
    if (updateIndicator != 'I') {
        setFieldReadonly(addressfields.USA_ADDRESS_B);
        setFieldReadonly(addressfields.ADDRESS_TYPE_CODE);
        setFieldReadonly(addressfields.ADDRESS_LINE_1);
        setFieldReadonly(addressfields.ADDRESS_LINE_2);
        setFieldReadonly(addressfields.ADDRESS_LINE_3);
        setFieldReadonly(addressfields.EFFECTIVE_FROM_DATE);
        setFieldReadonly(addressfields.EFFECTIVE_TO_DATE);
        setFieldReadonly(addressfields.CITY);
        setFieldReadonly(addressfields.STATE_CODE);
        setFieldReadonly(addressfields.COUNTY_CODE);
        setFieldReadonly(addressfields.ZIP_CODE);
        setFieldReadonly(addressfields.ZIP_PLUS_FOUR);
        setFieldReadonly(addressfields.COUNTRY_CODE);
        setFieldReadonly(addressfields.PROVINCE);
        setFieldReadonly(addressfields.OTHER_PROVINCE);
        setFieldReadonly(addressfields.ZIP_CODE_FOREIGN);

        // this page does not allow to add primary address
        // in the old setFormToEditState method, it still sets the primaryAddressB field to read only
        // so primaryAddressB field is always read only
    } else {
        // if inSourceTableName = ENTITY, then disable clientB field, which means address will be added under the pass in entity
        // if not, then we should enable clientB field
        if ("ENTITY" != getObjectValue(addressfields.ORIG_SOURCE_TABLE_NAME)) {
            setFieldEditable(addressfields.CLIENT_B);
        }
    }

    handleAddressFields();
}

function handleOnSubmit(action) {
    var proceed = false;
    switch (action) {
        case 'SAVE':
            first(testgrid1);
            while (!testgrid1.recordset.eof) {
                var updateIndicator = testgrid1.recordset("UPDATE_IND").value;
                if (updateIndicator == 'I' || updateIndicator == 'Y') {
                    // common validate form is handled in commonOnSubmit, here do special business validation
                    if (!validateBeforeSave()) {
                        return false;
                    }
                }
                next(testgrid1);
            }
            document.forms[0].process.value = "saveAddressSearchAdd";
            proceed = true;
            break;
        default:
            proceed = false;
    }
    return proceed;
}

function validateBeforeSave() {
    var effectiveFromDate = testgrid1.recordset("CEFFECTIVEFROMDATE").value;
    var effectiveToDate = testgrid1.recordset("CEFFECTIVETODATE").value;
    var todayDate = formatDate(new Date(), 'mm/dd/yyyy');

    // basic from-to date validation
    if (isDate2OnOrAfterDate1(effectiveFromDate, effectiveToDate) == 'N') {
        alert(getMessage("ci.address.error.wrongEffectiveDate", new Array(effectiveToDate, effectiveFromDate)));
        return false;
    }

    // since we cannot select a future address here, we should not allow add future address also
    if (isDate2OnOrAfterDate1(effectiveFromDate, todayDate) == 'N') {
        alert(getMessage("ci.address.searchAdd.error.futureAddressNotAllowed"));
        return false;
    }

    // effective to date against today
    if (isDate2OnOrAfterDate1(todayDate, effectiveToDate) == 'N') {
        alert(getMessage("ci.address.error.wrongEffectiveToDate"));
        return false;
    }
    return true;
}

function handleOnButtonClick(btnId) {
    switch (btnId) {
        case 'SELECT':
            var dataArray = getSelectedKeys(testgrid1);
            if (dataArray.length != 1) {
                alert(getMessage("ci.common.error.onlyOneRow.noSelect"));
                return;
            }

            getRow(testgrid1, dataArray[0]);
            var updateIndicator = testgrid1.recordset("UPDATE_IND").value;
            if (updateIndicator == 'I' || updateIndicator == 'Y') {
                alert(getMessage("ci.address.searchAdd.error.dataNotSaved"));
                return;
            }

            var effectiveFromDate = testgrid1.recordset("CEFFECTIVEFROMDATE").value;
            var effectiveToDate = testgrid1.recordset("CEFFECTIVETODATE").value;
            var todayDate = formatDate(new Date(), 'mm/dd/yyyy');
            // expired address/future address can not be selected here
            if (isDate2OnOrAfterDate1(todayDate, effectiveToDate) == "N") {
                alert(getMessage("ci.address.searchAdd.error.expiredAddressForbidden"));
                return;
            }
            if (isDate2OnOrAfterDate1(effectiveFromDate, todayDate) == "N") {
                alert(getMessage("ci.address.searchAdd.error.FutureAddressForbidden"));
                return;
            }

            if (isPageDataChanged() && !confirm(getMessage("js.lose.changes.confirmation"))) {
                return;
            }

            infoFromAddressSearchAdd[0] = testgrid1.recordset("ID").value;
            infoFromAddressSearchAdd[1] = testgrid1.recordset("CADDRESSTYPECODE").value;
            infoFromAddressSearchAdd[2] = testgrid1.recordset("CADDRESSNAME").value;
            infoFromAddressSearchAdd[3] = testgrid1.recordset("CADDRESSLINE1").value;
            infoFromAddressSearchAdd[4] = testgrid1.recordset("CADDRESSLINE2").value;
            infoFromAddressSearchAdd[5] = testgrid1.recordset("CADDRESSLINE3").value;
            infoFromAddressSearchAdd[6] = testgrid1.recordset("CCITY").value;
            infoFromAddressSearchAdd[7] = testgrid1.recordset("CSTATECODE").value;
            infoFromAddressSearchAdd[8] = testgrid1.recordset("CCOUNTYCODE").value;
            infoFromAddressSearchAdd[9] = testgrid1.recordset("CZIPCODE").value;
            infoFromAddressSearchAdd[10] = testgrid1.recordset("CZIPPLUSFOUR").value;
            infoFromAddressSearchAdd[11] = testgrid1.recordset("CPOSTOFFICEADDRESSB").value;
            infoFromAddressSearchAdd[12] = testgrid1.recordset("CUSAADDRESSB").value;
            infoFromAddressSearchAdd[13] = testgrid1.recordset("CPRIMARYADDRESSB").value;
            infoFromAddressSearchAdd[14] = testgrid1.recordset("CEFFECTIVEFROMDATE").value;
            infoFromAddressSearchAdd[15] = testgrid1.recordset("CEFFECTIVETODATE").value;
            infoFromAddressSearchAdd[16] = testgrid1.recordset("CPROVINCE").value;
            infoFromAddressSearchAdd[17] = testgrid1.recordset("CCOUNTRYCODE").value;
            infoFromAddressSearchAdd[18] = testgrid1.recordset("CADDRESSSINGLELINE").value;

            closeWindow(function () {
                if (getParentWindow() && getParentWindow().getInfoFromAddressSearchAdd) {
                    getParentWindow().getInfoFromAddressSearchAdd(infoFromAddressSearchAdd);
                }
            });
            break;
        case 'FINDCLIENT':
            openEntitySelectWinFullName(addressfields.ORIG_SOURCE_RECORD_ID, addressfields.ENTITY_NAME, "handleFindClient()");
            break;
        case 'CLOSE':
            if (isPageDataChanged() && !confirm(getMessage("js.lose.changes.confirmation"))) {
                return;
            } else {
                closeWindow();
            }
            break;
        default:
            break;
    }
}

function testgrid_setInitialValues() {
    var path = getCISPath() + "/ciAddressSearchAdd.do?process=getInitialValues";
    new AJAXRequest("get", path, '', afterGetInitialValues, false);

    // Issue 160588 - If we already have in_source_tablename of ENTITY, set source accordingly.
    if (getObjectValue(addressfields.ORIG_SOURCE_TABLE_NAME) == 'ENTITY') {
        testgrid1.recordset("CSOURCERECORDID").value = getObjectValue(addressfields.ORIG_SOURCE_RECORD_ID);
        testgrid1.recordset("CSOURCETABLENAME").value = getObjectValue(addressfields.ORIG_SOURCE_TABLE_NAME);
    } else {
        testgrid1.recordset("CSOURCERECORDID").value = getObjectValue(addressfields.DUMMY_SOURCE_RECORD_ID);
        testgrid1.recordset("CSOURCETABLENAME").value = 'DUMMY';
    }
    if (getObjectValue(addressfields.ORIG_ADDRESS_TYPE_CODE) != '') {
        setItem(testgrid1, "CADDRESSTYPECODE", getObjectValue(addressfields.ORIG_ADDRESS_TYPE_CODE));
    }
}

function afterGetInitialValues(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null))
                return;

            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                setFormFieldValuesByObject(oValueList[0]);
            }
        }
    }
}

function handleFindClient() {
    // we replaced the origSourceRecordId with new selected entity, reload page to show address list of new entity
    setObjectValue(addressfields.ORIG_SOURCE_TABLE_NAME, "ENTITY");
    setObjectValue(addressfields.ORIG_ADDRESS_ID, "");
    setObjectValue(addressfields.ORIG_ADDRESS_TYPE_CODE, "");
    setObjectValue("process", "loadAddressSearchAdd");
    submitFirstForm();
}

function formatAddressSingleLine() {
    var addressSingleLine = '';
    var addressName = getObjectValue(addressfields.ADDRESS_NAME);
    var addressLine1 = getObjectValue(addressfields.ADDRESS_LINE_1);
    var addressLine2 = getObjectValue(addressfields.ADDRESS_LINE_2);
    var addressLine3 = getObjectValue(addressfields.ADDRESS_LINE_3);
    var city = getObjectValue(addressfields.CITY);
    var countryCode = getObjectValue(addressfields.COUNTRY_CODE);

    if (!isEmpty(addressName)) {
        addressSingleLine += ", " + addressName;
    }
    if (!isEmpty(addressLine1)) {
        addressSingleLine += ", " + addressLine1;
    }
    if (!isEmpty(addressLine2)) {
        addressSingleLine += ", " + addressLine2;
    }
    if (!isEmpty(addressLine3)) {
        addressSingleLine += ", " + addressLine3;
    }
    if (!isEmpty(city)) {
        addressSingleLine += ", " + city;
    }

    if (countryCode == getObjectValue(addressfields.KEY_COUNTRY_CODE_USA)) {
        var stateCode = getObjectValue(addressfields.STATE_CODE);
        var zipcode = getObjectValue(addressfields.ZIP_CODE);
        var zipPlusFour = getObjectValue(addressfields.ZIP_PLUS_FOUR);

        if (!isEmpty(stateCode)) {
            addressSingleLine += ", " + stateCode;
        }
        if (!isEmpty(zipcode)) {
            addressSingleLine += " " + zipcode;
        }
        if (!isEmpty(zipPlusFour)) {
            addressSingleLine += " " + zipPlusFour;
        }
    } else {
        var province = '';
        if (isCountryCodeConfigured(countryCode)) {
            province = getObjectValue(addressfields.PROVINCE);
        } else {
            province = getObjectValue(addressfields.OTHER_PROVINCE);
        }
        var zipCodeForeign = getObjectValue(addressfields.ZIP_CODE_FOREIGN);
        var countryCodeIndex = getObject(addressfields.COUNTRY_CODE).selectedIndex;

        if (!isEmpty(province)) {
            addressSingleLine += ", " + province;
        }
        if (!isEmpty(zipCodeForeign)) {
            addressSingleLine += " " + zipCodeForeign;
        }
        if (countryCodeIndex != 0) {
            var countryCodeDisplayText = getObject(addressfields.COUNTRY_CODE).options[countryCodeIndex].text;
            addressSingleLine += " " + countryCodeDisplayText;
        }
    }

    if (addressSingleLine.startsWith(",")) {
        addressSingleLine = addressSingleLine.substr(2);
    }
    return addressSingleLine;
}

function find(findId) {
    zipFieldsFind(findId);
}