//-----------------------------------------------------------------------------
// Functions to support Address Add page.
// Author: kshen
// Date:   unknown
// Modifications:
//-----------------------------------------------------------------------------
// 12/02/2008    kshen      Added codes to support system parameter "ZIP_CODE_ENABLE",
//                          "ZIP_OVERRIDE_ADDR", and "CS_SHOW_ZIPCD_LIST".
// 12/03/2008    kshen      Use reuseable methon "setZipCode", and "findZipCode" to
//                          process zipcode.
// 07/27/2009    Jacky      Issue 95993: check countyCode and zipPlusFour fields whether exist or not
//                          before processing their attributes
// 11/12/2009    hxk        Remove logic that was blanking out the postal code
//                          when the provinice was changed.
// 03/22/2010    Kenney     Remove the logic of phone number validation.
//                          Phone number format will be handled by framework
// 07/23/2010    Blake       For issue 109815
// 03/29/2011    hxk        Issue 119014
//                          Remove setting of zip and zip plus four to empty string.
// 07/04/2011    Michael    Issue 117347
// 05/17/2013    Elvin      Issue 144456: add DOB/decease today date validation
// 05/17/2013    Elvin      Issue 145943: add address effective from and to date validation
// 08/23/2013    kshen      Issue 142975.
// 09/25/2013    kshen      Issue 148102
// 10/29/2013    ldong      Issue 138932
// 01/23/2014    Elvin      Issue 150547: add entity_emailAddress2 and entity_emailAddress3
// 06/05/2014    bzhu       Issue 153402.
// 06/25/2014    ldong      Issue 155318
// 07/18/2014    jld        Issue 155318. Correction to validate call.
// 12/15/2014    Elvin      Issue 158114: add isOkToChangePages
// 03/09/2015    bzhu       Issue 160643. Make page title configurable.
// 04/15/2015    bzhu       Issue 159178. Remain province for other country.
// 04/08/2015    ylu        Issue 159820: add classification date validation against DOB/DOI date
// 02/12/2016    jld        Issue 167977. Reset county LOV if page reloaded for found duplicate.
// 04/06/2016    ylu        Issue 169990. accept freedom validation for foreign postal code
// 09/20/2016    dzhang     Issue 179052: add obr check in popBtnClick method when saving
//                                        enity.
// 05/07/2018    jld        Issue 193125: Add npi_no.
// 09/18/2018    ylu        Issue 195835: grid replacement.
//                          1). remove un-useful code. 2). use closeWindow() function.
// 09/20/2018    kshen      Issue 195835. Show processing when submitting form
// 10/01/2018    jld        Issue 191748: Move network discount validation to OBR.
// 11/16/2018    Elvin      Issue 195835: grid replacement
//-----------------------------------------------------------------------------
var isChanged = false;
var phoneNumFldID       = "phoneNumber_phoneNumber";
var areaCodeFldID       = "phoneNumber_areaCode";
var firstNameFldID      = "entity_firstName";
var lastNameFldID       = "entity_lastName";
var orgNameFldID        = "entity_organizationName";
var addrTypeFldID       = "address_addressTypeCode";
var addrLine1FldID      = "address_addressLine1";
var cityFldID           = "address_city";
var stateFldID          = "address_stateCode";
var primAddrBFldID      = "address_primaryAddressB";
var usaAddrBFldID       = "address_usaAddressB";
var countryCodeFldID    = "address_countryCode";
var addrEffFrDtFldID    = "address_effectiveFromDate";
var addrEffToDtFldID    = "address_effectiveToDate";
var SSNFldID            = "entity_socialSecurityNumber";
var TINFldID            = "entity_federalTaxID";
var NPIFldID            = "entity_npiNo";
var SSNVerBFldID        = "entity_ssnVerifiedB";
var TINVerBFldID        = "entity_federalTaxIDVerifiedB";
var dfltTaxIDFldID      = "entity_defaultTaxID";
var entTypeFldID        = "entity_entityType";
var dobFldID            = "entity_dateOfBirth";
var deceasedBFldID      = "entity_deceasedB";
var deceasedDateFldID   = "entity_dateOfDeath";
var deceasedDateLabelFldID = "entity_dateOfDeathFLDLABEL";
var classCodeFldID      = "entityClass_entityClassCode";
var subClassCodeFldID   = "entityClass_entitySubClassCode";
var subTypeCodeFldID    = "entityClass_entitySubTypeCode";
var classEffFrDtFldID   = "entityClass_effectiveFromDate";
var classEffToDtFldID   = "entityClass_effectiveToDate";
var suffixNameFldID = "entity_suffixName";
var vendorVerifyFldID   = "CM_CHK_VENDOR_VERIFY";
var zipcodeFldID = "address_zipCode";
var zipcodeolugfourFidID = "address_zipPlusFour";
var zipCodeForeignFldId = "address_zipCodeForeign";
var provinceFldId = "address_province";
var countyCodeFldID = "address_countyCode";
var provinceHtml = "";
var veryLongNameFldId = "entity_veryLongName";
var canadianPostCodeFormat = "^[a-zA-Z]\\d[a-zA-Z] \\d[a-zA-Z]\\d$";
var eMailAddress1FldId = "entity_eMailAddress1";
var eMailAddress2FldId = "entity_eMailAddress2";
var eMailAddress3FldId = "entity_eMailAddress3";
// these are used by quick add
var addrTypeFldID2       = "address2_addressTypeCode";
var addrLine1FldID2      = "address2_addressLine1";
var cityFldID2           = "address2_city";
var stateFldID2          = "address2_stateCode";
var primAddrBFldID2      = "address2_primaryAddressB";
var usaAddrBFldID2       = "address2_usaAddressB";
var countryCodeFldID2    = "address2_countryCode";
var addrEffFrDtFldID2    = "address2_effectiveFromDate";
var addrEffToDtFldID2    = "address2_effectiveToDate";
var zipcodeFldID2 = "address2_zipCode";
var zipcodeolugfourFidID2 = "address2_zipPlusFour";
var countyCodeFldID2 = "address2_countyCode";
var phoneNumFldID2       = "phoneNumber2_phoneNumber";
var areaCodeFldID2       = "phoneNumber2_areaCode";

function handleOnLoad() {
    if (getObject("isNewValue") && getObject("CI_ENTY_CONTINUE_ADD")) {
        var continueAdd = getObjectValue("CI_ENTY_CONTINUE_ADD");

        if (continueAdd.toUpperCase() == 'Y') {
            var newVal = getObjectValue("isNewValue");
            if (newVal == 'N') {
                var url = getAppPath() + "/ciSelectedField.do";
                openDivPopup("", url, true, true, null, null, 400, 200, "", 0, "", true);
            }
        } else {
            if (window.afterSave) {
                afterSave();
            }
        }
    }

    autoFind_address_zipCode = false;

    handleAddressFields();
}

function btnClick(btnID) {
    if (btnID == 'save') {
        if (!validate(document.forms[0], true)) {
            return;
        }

        if (!validateAllEntityAddFields()) {
            return;
        }

        hideAction("CM_ACS_SAVE");
        setObjectValue("process", "save");
        showProcessingImgIndicator();
        submitFirstForm();
    }
}

// button click on add entity popup page
function popBtnClick(btnID) {
    switch (btnID.toUpperCase()) {
        case 'CANCEL':
            if (isChanged) {
                if (!confirm(getMessage("js.lose.changes.confirmation"))) {
                    return;
                }
            }
            closeWindow(function () {
                var parentWindow = getParentWindow();
                if (parentWindow) {
                    parentWindow.focus();
                }
            });
            break;

        case 'SELECT':
            if (!validate(document.forms[0], true)) {
                return;
            }
            if (!validateAllEntityAddFields()) {
                return;
            }
            enableDisableField(getObject("CI_ENT_POP_ADD_SEL"), true);
            setObjectValue("process", "select");
            showProcessingImgIndicator();
            submitFirstForm();
            break;

        case 'GOTOCLIENT':
            if (!validate(document.forms[0], true)) {
                return;
            }
            if (!validateAllEntityAddFields()) {
                return;
            }
            enableDisableField(getObject("CI_ENT_POP_ADD_CANCEL"), true);
            setObjectValue("process", "goToClient");
            showProcessingImgIndicator();
            submitFirstForm();
            break;
    }
}

function goToCISEntityModify(pk, type) {
    var entityRootContext = generateURL();
    var reqParams = "?pk=" + pk + "&entityType=" + type + "&process=loadEntityData";

    switch (type.substr(0, 1)) {
        case 'P':
            setWindowLocation(entityRootContext + "/ciEntityPersonModify.do" + reqParams);
            break;
        case 'O':
            setWindowLocation(entityRootContext + "/ciEntityOrgModify.do" + reqParams);
            break;
        default:
            alert(getMessage("ci.entity.message.entityType.unknown"));
            break;
    }
}

/*
 * Generate CIS URL via current path
 */
function generateURL() {
    var cisRoot = getTopNavApplicationUrl("CIS");

    if (isEmpty(cisRoot)) {
        // retrieve the web context root for current application
        var appPath = getAppPath();
        // pick up the parent context root
        var parentContextRoot = appPath.replace(/\/[^\/]+$/, '');
        // infer the context url for eClaim
        cisRoot = parentContextRoot + "/" + "CIS";
    }
    return cisRoot;
}

function hideAction(action) {
    // Call changeActionItemDisplay in edits.js.
    changeActionItemDisplay(action, true);
}

function validateEntityIdentifier(field) {
    var msg = '';
    switch (field.name) {
        case SSNFldID:
            msg = validateSSN(field);
            break;
        case TINFldID:
            msg = validateTIN(field);
            break;
        case NPIFldID:
            msg = validateNPI(field);
            break;
    }

    if (!isEmpty(msg)) {
        alert(msg);
        field.focus();
        postChangeReselectField(field);
        return false;
    }

    resetOKToSkipTaxIDDupsFlag(field, getObject(entTypeFldID), getObject("okToSkipTaxIDDups"));
    return true;
}

function setOrgNameFromVeryLongName(veryLongNameFld) {
    var longName = veryLongNameFld.value;
    if (isStringValue(longName)) {
        if (hasObject(orgNameFldID)) {
            setObjectValue(orgNameFldID, longName.substring(0, 60));
        }
    }
}

function resetNetworkDiscount(classCodeFld) {
    if (classCodeFld.value != "NETWORK" && hasObject("entityClass_networkDiscount")) {
        setObjectValue("entityClass_networkDiscount", "");
    }
}

//-----------------------------------------------------------------------------
// OnChange event handler
//-----------------------------------------------------------------------------
function handleOnChange(field) {
    isChanged = true;

    if (field.name == getAddressFieldId("ZIP_CODE")
        || field.name == getAddressFieldId("ZIP_PLUS_FOUR")
        || field.name == getAddressFieldId("ZIP_CODE_FOREIGN")
        || field.name == getAddressFieldId("STATE_CODE")
        || field.name == getAddressFieldId("CITY")
        || field.name == getAddressFieldId("COUNTRY_CODE")) {
        if (!addressFieldChanged(field)) {
            event.returnValue = false;
        }
    }

    switch (field.name) {
        case veryLongNameFldId:
            setOrgNameFromVeryLongName(field);
            break;
        case SSNFldID:
        case TINFldID:
        case NPIFldID:
            validateEntityIdentifier(field);
            break;
        case classCodeFldID:
            resetNetworkDiscount(field);
            break;
    }
    resetOKToCheckForEntDupsFlag(field);
}

//-----------------------------------------------------------------------------
// specific validation rules
//-----------------------------------------------------------------------------
function validateAllEntityAddFields() {
    var msg = '';

    if (getObject(SSNFldID)) {
        msg += validateSSN(getObject(SSNFldID));
    }
    if (getObject(TINFldID)) {
        msg += validateTIN(getObject(TINFldID));
    }
    if (getObject(NPIFldID)) {
        msg += validateNPI(getObject(NPIFldID));
    }

    msg += validateEMail(getObject(eMailAddress1FldId));
    msg += validateEMail(getObject(eMailAddress2FldId));
    msg += validateEMail(getObject(eMailAddress3FldId));

    // area code should not be empty if phone number is supplied
    if (!isEmpty(getObjectValue(phoneNumFldID)) && isEmpty(getObjectValue(areaCodeFldID))) {
        msg += getMessage("ci.common.error.classCode.required", new Array(getLabel(areaCodeFldID))) + "\n";
    }
    if (!isEmpty(getObjectValue(phoneNumFldID2)) && isEmpty(getObjectValue(areaCodeFldID2))) {
        msg += getMessage("ci.common.error.classCode.required", new Array(getLabel(areaCodeFldID2))) + "\n";
    }

    // address line 1 should not be empty if phone number is supplied
    if (!isEmpty(getObjectValue(phoneNumFldID)) && isEmpty(getObjectValue(addrLine1FldID))) {
        msg += getMessage("ci.common.error.classCode.required", new Array(getLabel(addrLine1FldID))) + "\n";
    }
    if (!isEmpty(getObjectValue(phoneNumFldID2)) && isEmpty(getObjectValue(addrLine1FldID2))) {
        msg += getMessage("ci.common.error.classCode.required", new Array(getLabel(addrLine1FldID2))) + "\n";
    }

    msg += validateDefaultVerifiedTaxID(getObject(dfltTaxIDFldID), getObject(SSNFldID), getObject(SSNVerBFldID),
        getObject(TINFldID), getObject(TINVerBFldID), getObject(vendorVerifyFldID));
    msg += validateBirthDeceasedDate();
    msg += validateAddressDate();
    msg += validateClassificationDate();

    if (msg != '') {
        alert(msg);
        return false;
    }

    if (!validateCommonAddressFields()) {
        return false;
    }

    return true;
}

function validateBirthDeceasedDate() {
    var today = formatDate(new Date(), 'mm/dd/yyyy');
    var dateOfBirth = getObjectValue("entity_dateOfBirth");
    var dateOfDeath = getObjectValue("entity_dateOfDeath");

    var message = '';
    if (isDate2OnOrAfterDate1(dateOfBirth, today) == 'N') {
        message += getMessage("ci.entity.message.dateValue.beforeToday", [getLabel(getObject("entity_dateOfBirth")), dateOfBirth]) + "\n";
    }
    if (isDate2OnOrAfterDate1(dateOfDeath, today) == 'N') {
        message += getMessage("ci.entity.message.dateValue.beforeToday", [getLabel(getObject("entity_dateOfDeath")), dateOfDeath]) + "\n";
    }
    if (isDate2OnOrAfterDate1(dateOfBirth, dateOfDeath) == 'N') {
        message += getMessage("ci.entity.message.dateValue.after", [getLabel(getObject("entity_dateOfDeath")), dateOfDeath,
            getLabel(getObject("entity_dateOfBirth")), dateOfBirth]) + "\n";
    }
    return message;
}

function validateAddressDate() {
    var today = formatDate(new Date(), 'mm/dd/yyyy');
    var addressEffectiveFromDate = getObjectValue("address_effectiveFromDate");
    var addressEffectiveToDate = getObjectValue("address_effectiveToDate");

    var message = '';
    if (isDate2OnOrAfterDate1(addressEffectiveFromDate, today) == 'N') {
        message += getMessage("ci.entity.message.dateValue.beforeToday", [getLabel(getObject("address_effectiveFromDate")), addressEffectiveFromDate]) + "\n";
    }
    if (isDate2OnOrAfterDate1(today, addressEffectiveToDate) == 'N') {
        message += getMessage("ci.entity.message.dateValue.afterToday", [getLabel(getObject("address_effectiveToDate")), addressEffectiveToDate]) + "\n";
    }
    if (isDate2OnOrAfterDate1(addressEffectiveFromDate, addressEffectiveToDate) == 'N') {
        message += getMessage("ci.entity.message.dateValue.after", [getLabel(getObject("address_effectiveToDate")), addressEffectiveToDate,
            getLabel(getObject("address_effectiveFromDate")), addressEffectiveFromDate]) + "\n";
    }

    if (getObject("address2_addressLine1")) {
        var address2EffectiveFromDate = getObjectValue("address2_effectiveFromDate");
        var address2EffectiveToDate = getObjectValue("address2_effectiveToDate");

        if (isDate2OnOrAfterDate1(address2EffectiveFromDate, today) == 'N') {
            message += getMessage("ci.entity.message.dateValue.beforeToday", [getLabel(getObject("address2_effectiveFromDate")), address2EffectiveFromDate]) + "\n";
        }
        if (isDate2OnOrAfterDate1(today, address2EffectiveToDate) == 'N') {
            message += getMessage("ci.entity.message.dateValue.afterToday", [getLabel(getObject("address2_effectiveToDate")), address2EffectiveToDate]) + "\n";
        }
        if (isDate2OnOrAfterDate1(address2EffectiveFromDate, address2EffectiveToDate) == 'N') {
            message += getMessage("ci.entity.message.dateValue.after", [getLabel(getObject("address2_effectiveToDate")), address2EffectiveToDate,
                getLabel(getObject("address2_effectiveFromDate")), address2EffectiveFromDate]) + "\n";
        }
    }
    return message;
}

function validateClassificationDate() {
    var entityClassCode = getObjectValue("entityClass_entityClassCode");
    var classEffectiveFromDate = getObjectValue("entityClass_effectiveFromDate");
    var classEffectiveToDate = getObjectValue("entityClass_effectiveToDate");
    var dateOfBirth = getObjectValue("entity_dateOfBirth");

    var message = '';
    if (!isEmpty(entityClassCode)) {
        if (isDate2OnOrAfterDate1(dateOfBirth, classEffectiveFromDate) == 'N') {
            message += getMessage("ci.entity.message.dateValue.after", [getLabel(getObject("entityClass_effectiveFromDate")), classEffectiveFromDate,
                getLabel(getObject("entity_dateOfBirth")), dateOfBirth]) + "\n";
        }
        if (isDate2OnOrAfterDate1(dateOfBirth, classEffectiveToDate) == 'N') {
            message += getMessage("ci.entity.message.dateValue.after", [getLabel(getObject("entityClass_effectiveToDate")), classEffectiveToDate,
                getLabel(getObject("entity_dateOfBirth")), dateOfBirth]) + "\n";
        }
        if (isDate2OnOrAfterDate1(classEffectiveFromDate, classEffectiveToDate) == 'N') {
            message += getMessage("ci.entity.message.dateValue.after", [getLabel(getObject("entityClass_effectiveFromDate")), classEffectiveFromDate,
                getLabel(getObject("entityClass_effectiveToDate")), classEffectiveToDate]) + "\n";
        }
    }
    return message;
}

//-----------------------------------------------------------------------------
// Reset the OK to skip entity dups flag;  if the flag was Y and the user
// changed any of these fields, then set it to N, because now it's not OK to skip tax ID
// dups on a save.
//-----------------------------------------------------------------------------
function resetOKToCheckForEntDupsFlag(field) {
    if (field.name == SSNFldID
        || field.name == TINFldID
        || field.name == firstNameFldID
        || field.name == lastNameFldID
        || field.name == orgNameFldID
        || field.name == cityFldID
        || field.name == stateFldID
        || field.name == dobFldID) {
        if (getObjectValue('okToSkipEntityDups') == 'Y') {
            setObjectValue('okToSkipEntityDups', 'N');
        }
    }
}

//-----------------------------------------------------------------------------
// The entity add JSP needs to call this function if the process is
// "showDups".  When showing duplicates, the page should act as if data has been
// changed, because data changes have not been saved.
//-----------------------------------------------------------------------------
function setIsChangedFlag(booleanValue) {
    isChanged = booleanValue;
}

function find(findId) {
    zipFieldsFind(findId);
}

function clearFields(fieldDelimitedByCommaId) {
    var fieldArr = getObjectValue(fieldDelimitedByCommaId).split(",");

    var fieldId = "";
    for (var index = 0; index < fieldArr.length; index++) {
        fieldId = fieldArr[index];

        if (!isEmpty(fieldId) && hasObject(fieldId)) {
            clearObject(fieldId);
        }
    }
}

/**
 * This function will be called from Entity Field Select page
 * to reuse or clear fields when continue add entity.
 *
 * @param obj
 */
function dealWithAddedFields(obj) {
    for (var i = 0; i < obj.length; i++) {
        if (obj[i]) {
            switch (obj[i].value) {
                case 'address':
                    if (obj[i].checked) {
                        clearFields("CI_REUSE_ADDRESS_CLEAR");
                    } else {
                        clearFields("CI_ENTY_ADD_REUSE_FIELDS_ADDRESS");
                    }
                    break;
                case 'phone':
                    if (obj[i].checked) {
                        clearFields("CI_REUSE_PHONE_CLEAR");
                    } else {
                        clearFields("CI_ENTY_ADD_REUSE_FIELDS_PHONE");
                    }
                    break;
                case 'classification':
                    if (obj[i].checked) {
                        clearFields("CI_REUSE_CLASSIFICATION_CLEAR");
                    } else {
                        clearFields("CI_ENTY_ADD_REUSE_FIELDS_CLASSIFICATION");
                    }
                    break;
            }
        }
    }
}

function clearObject(objid) {
    var obj = getObject(objid);
    if (isArray(obj) && obj.length > 0) {
        if (obj.type) {
            if (obj.type == "select-one" && obj.selectedIndex != -1) {
                obj.selectedIndex = -1;
            } else if (obj.type == "select-multiple") {
                for (var i = 0; i < obj.length; i++) {
                    if (obj[i].selected) {
                        obj[i].selected = false;
                    }
                }
            }
        } else if (obj[0].type) {
            if (obj[0].type == "radio") {
                for (var i = 0; i < obj.length; i++) {
                    if (obj[i].checked) {

                    }
                }
            } else if (obj[0].type == "checkbox") {
                for (var i = 0; i < obj.length; i++) {
                    if (obj[i].checked) {
                        obj[i].checked = false;
                    }
                }
            } else {
                obj[0].value = '';
            }
        }
    } else {
        if (obj.type && obj.type == "checkbox") {
            obj.checked = false;
        } else {
            obj.value = '';
            var obj_disp_only = getObject(objid+'_DISP_ONLY');
            if(obj_disp_only){
                obj_disp_only.value='';
            }
        }
    }
}

/**
 * This function is called from Entity Field Select page.
 */
function dealwithCancel(){
    if (window.afterSave){
        afterSave();
    }
}

function getAddressFieldId(fieldName) {
    var prefix = "address_";

    switch (fieldName) {
        case "ZIP_CODE":
            return prefix + "zipCode";
        case "CITY":
            return prefix + addressfields.CITY;
        case "STATE_CODE":
            return prefix + addressfields.STATE_CODE;
        case "COUNTY_CODE":
            return prefix + addressfields.COUNTY_CODE;
        case "ZIP_PLUS_FOUR":
            return prefix + addressfields.ZIP_PLUS_FOUR;
        case "ZIP_CODE_FOREIGN":
            return prefix + addressfields.ZIP_CODE_FOREIGN;
        case "USA_ADDRESS_B":
            return prefix + addressfields.USA_ADDRESS_B;
        case "COUNTRY_CODE":
            return prefix + addressfields.COUNTRY_CODE;
        case "PROVINCE":
            return prefix + addressfields.PROVINCE;
        case "OTHER_PROVINCE":
            return prefix + addressfields.OTHER_PROVINCE;
        case "ADDRESS_TYPE_CODE":
            return prefix + addressfields.ADDRESS_TYPE_CODE;
    }
}