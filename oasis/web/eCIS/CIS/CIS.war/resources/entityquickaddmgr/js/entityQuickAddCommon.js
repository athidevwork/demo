/**
 * Created by jdingle on 8/12/2016.
 Revision Date    Revised By  Description
 -----------------------------------------------------------------------------
  09/23/2016      dzhang      Issue 179052: add obr check when saving person or organization.
  09/26/2018      kshen       195835. CIS grid replacement.
  10/12/2018      jdingle     Issue 190972. placement for fields when country changed.
  11/16/2018      Elvin       Issue 195835: grid replacement
 -----------------------------------------------------------------------------
 */

var isChanged = false;
var phoneNumFldID       = "phoneNumber_phoneNumber";
var areaCodeFldID       = "phoneNumber_areaCode";
var firstNameFldID      = "entity_firstName";
var lastNameFldID       = "entity_lastName";
var orgNameFldID        = "entity_organizationName";
var addrTypeFldID       = "address_addressTypeCode";
var addrLine1FldID      = "address_addressLine1";
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
var suffixNameFldID     = "entity_suffixName";
var vendorVerifyFldID   = "CM_CHK_VENDOR_VERIFY";
var provinceHtml = "";
var provinceHtml2 = "";
var veryLongNameFldId = "entity_veryLongName";
var canadianPostCodeFormat = "^[a-zA-Z]\\d[a-zA-Z] \\d[a-zA-Z]\\d$";
var isSavingInProcess = false;
var eMailAddress1FldId = "entity_eMailAddress1";
var eMailAddress2FldId = "entity_eMailAddress2";
var eMailAddress3FldId = "entity_eMailAddress3";
var currentEducationSuffix = "";
var currentAddressSuffix = "";
var currentAddressPrefix = "";

function commonOnLoad() {
    if (afterSave() == true) {
        return;
    }
    // regular checkFields is done in handleOnLoad.
    currentAddressPrefix = "address2_";
    handleAddressFields();

    autoFind_address2_zipCode = false;
}

function handleOnChange(field) {
    isChanged = true;

    var msg = '';
    resetOKToCheckForEntDupsFlag(field);

    switch (field.name) {
        case veryLongNameFldId:
            setOrgNameFromVeryLongName(field);
            break;

        case SSNFldID:
        case TINFldID:
        case NPIFldID:
            validateEntityIdentifier(field);
            break;

        case 'address_zipCode':
        case 'address2_zipCode':
        case 'address_zipCodeForeign':
        case 'address2_zipCodeForeign':
        case 'address_zipPlusFour':
        case 'address2_zipPlusFour':
        case 'address_stateCode':
        case 'address2_stateCode':
        case 'address_city':
        case 'address2_city':
        case "address_countryCode":
        case "address2_countryCode":
            currentAddressPrefix = field.name.substring(0, field.name.indexOf('_') + 1);

            if (!addressFieldChanged(field)) {
                event.returnValue = false;
                return;
            }
            break;

        case "entityClass_entityClassCode":
            resetNetworkDiscount(field);
            break;

        case 'educationProfile_institutionStateCode':
            if (!isEmpty(field.value)) {
                setObjectValue("educationProfile_institutionCountryCode", "USA");
            }
            setObjectValue("educationProfile_institutionCity", "");
            break;

        case 'educationProfile2_institutionStateCode':
            if (!isEmpty(field.value)) {
                setObjectValue("educationProfile2_institutionCountryCode", "USA");
            }
            setObjectValue("educationProfile2_institutionCity", "");
            break;

        case 'educationProfile3_institutionStateCode':
            if (!isEmpty(field.value)) {
                setObjectValue("educationProfile3_institutionCountryCode", "USA");
            }
            setObjectValue("educationProfile3_institutionCity", "");
            break;

        case 'educationProfile_institutionCountryCode':
            setObjectValue("educationProfile_institutionStateCode", "");
            setObjectValue("educationProfile_institutionCity", "");
            break;

        case 'educationProfile2_institutionCountryCode':
            setObjectValue("educationProfile2_institutionStateCode", "");
            setObjectValue("educationProfile2_institutionCity", "");
            break;

        case 'educationProfile3_institutionCountryCode':
            setObjectValue("educationProfile3_institutionStateCode", "");
            setObjectValue("educationProfile3_institutionCity", "");
            break;

        case 'entityDenominator_denominatorAmt':
            if (!isPositiveInteger(field.value)) {
                msg = getMessage("ci.common.error.value.number", new Array(getLabel(field)));
            }
            break;

        default:
            break;
    }

    if (!isEmpty(msg)) {
        alert(msg);
        postChangeReselectField(field);
        return false;
    }
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
        || field.name == "address_city"
        || field.name == "address_stateCode"
        || field.name == "address2_city"
        || field.name == "address2_stateCode"
        || field.name == dobFldID) {
        if (getObjectValue("okToSkipEntityDups") == 'Y') {
            setObjectValue("okToSkipEntityDups", "N");
        }
    }
}

function find(findId) {
    switch (findId) {
        case "address_zipCode":
        case "address2_zipCode":
            currentAddressPrefix = field.name.substring(0, field.name.indexOf('_') + 1);

            zipFieldsFind(findId);
            break;
        case "educationProfile_institutionName":
            selectInstitutionName("");
            break;
        case "educationProfile2_institutionName":
            selectInstitutionName("2");
            break;
        case "educationProfile3_institutionName":
            selectInstitutionName("3");
            break;
        case "externalClaimsReportSummary_claimantname":
            //openEntitySelectWinFullName("claimantEntityFk", "claimantname");
            openEntitySelectWinEvtDfltEntCls('externalClaimsReportSummary_claimantEntityId', 'externalClaimsReportSummary_claimantName', '', '', '', '', 'CLAIMANT', 'N', "handleOnSelectFinder()");
            break;
        case "externalClaimsReportSummary_carrierName":
            //openEntitySelectWinFullName("carrierEntityFk", "carrierName");
            openEntitySelectWinEvtDfltEntCls('externalClaimsReportSummary_CARRIERENTITYFK', 'externalClaimsReportSummary_carrierName', '', '', '', '', 'CARRIER', 'N', 'handleOnSelectFinder()');
            break;
        default:
            break;
    }
}

function handleOnSelectFinder() {
   // placeholder if function needed
}

function selectInstitutionName(suffix) {
    if (!isStringValue(suffix)) {
        suffix = "";
    }
    currentEducationSuffix = suffix; // used in updateEducationFields
    var ciSchoolClass = "MEDSCHOOL";
    var ciInstitutionFlt = "N";
    var fromSc = getObjectValue("educationProfile" + suffix + "_institutionStateCode");
    var fromCc = getObjectValue("educationProfile" + suffix + "_institutionCountryCode");
    if (hasObject("ciSchoolClass")) {
        ciSchoolClass = getObjectValue("ciSchoolClass");
    }
    if (hasObject("ciInstitutionFlt")) {
        ciInstitutionFlt = getObjectValue("ciInstitutionFlt");
    }
    if (ciInstitutionFlt == "Y") {
        openEntitySelectWinEvtDfltEntClsState("educationProfile" + suffix + "_entityInstitutionId", "educationProfile" + suffix + "_institutionName", '', '', '', '', ciSchoolClass, 'Y', 'updateEducationFields', fromSc, fromCc, "educationProfile" + suffix + "_institutionCountryCode", "educationProfile" + suffix + "_institutionStateCode", "educationProfile" + suffix + "_institutionCity");
    } else {
        openEntitySelectWinEvtDfltEntCls("educationProfile" + suffix + "_entityInstitutionId", "educationProfile" + suffix + "_institutionName", '', '', '', '', ciSchoolClass, 'Y', 'updateEducationFields', "educationProfile" + suffix + "_institutionCountryCode", "educationProfile" + suffix + "_institutionStateCode", "educationProfile" + suffix + "_institutionCity");
    }
}

function handleEvent(event, jsonEntity) {
    // Called by fireEvent of entitySelectSearch.js
    if (event == "updateEducationFields") {
        var eduCity = jsonEntity.CCITY;

        var eduState = jsonEntity.CSTATECODE;
        if (eduState == '' || eduState == -1) {
            eduState = jsonEntity.CPROVINCE;
        }
        var eduCountry = jsonEntity.CCOUNTRYCODE;

        updateEducationFields(eduCity, eduState, eduCountry);
    }
}

function updateEducationFields(city, stateCode, countryCode) {
    var suffix = currentEducationSuffix;
    setObjectValue("educationProfile" + suffix + "_institutionStateCode", stateCode);
    if (stateCode != '' && stateCode != '-1' && (countryCode == '' || countryCode == '-1'))
        countryCode = "USA";
    setObjectValue("educationProfile" + suffix + "_institutionCountryCode", countryCode);
    setObjectValue("educationProfile" + suffix + "_institutionCity", city);

    var institutionName = getObjectValue("educationProfile" + suffix + "_institutionName");
    setObjectValue("educationProfile" + suffix + "_institutionName", institutionName + ", " + city + ", " + stateCode + ", " + countryCode);
}

function afterSave() {
    if (getObjectValue("saveAndClose") == "Y") {
        // User asked to Save and Close.
        // if there are no duplicates go to Entity Search.
        goToModule('search');
        return true;
    }

    if (getObjectValue("CI_ENTY_CONTINUE_ADD") == "Y") {
        clearFormFields(document.forms[0], true);
        setInputFormField("process", "init");
        submitFirstForm();
        return true;
    } else {
        return false;
    }
}

//-----------------------------------------------------------------------------
// validation rules that are shared between person and organization
//-----------------------------------------------------------------------------
function validateCommonFields() {
    /* validate license data */
    var olicensedDt = getObject("licenseProfile_dateLicensed");
    var oexpDt = getObject("licenseProfile_expirationDate");
    var licensedDtLb = getLabel(olicensedDt);
    var expDtLb = getLabel(oexpDt);
    /* validate date field */
    if (isDate2OnOrAfterDate1(olicensedDt.value, oexpDt.value) == 'N') {
        alert(getMessage("ci.common.error.licenseDate.before", new Array(licensedDtLb, expDtLb)));
        return false;
    }
    /* validate license data */
    if (isDate2OnOrAfterDate1(getObjectValue("entityDenominator_effectiveFromDate"), getObjectValue("entityDenominator_effectiveToDate")) == "N") {
        alert(getMessage("ci.detail.denominator.date.after", new Array(getLabel(getObject("entityDenominator_effectiveToDate")), getLabel(getObject("entityDenominator_effectiveFromDate")))));
        return false;
    }
    return true;
}

function getAddressFieldId(fieldName) {
    switch(fieldName) {
        case "ZIP_CODE":
            return currentAddressPrefix + "zipCode";
        case "CITY":
            return currentAddressPrefix + "city";
        case "STATE_CODE":
            return currentAddressPrefix + "stateCode";
        case "COUNTY_CODE":
            return currentAddressPrefix + "countyCode";
        case "ZIP_PLUS_FOUR":
            return currentAddressPrefix + "zipPlusFour";
        case "ZIP_CODE_FOREIGN":
            return currentAddressPrefix + "zipCodeForeign";
        case "USA_ADDRESS_B":
            return currentAddressPrefix + "usaAddressB";
        case "COUNTRY_CODE":
            return currentAddressPrefix + "countryCode";
        case "PROVINCE":
            return currentAddressPrefix + "province";
        case "OTHER_PROVINCE":
            return currentAddressPrefix + "otherProvince";
        case "ADDRESS_TYPE_CODE":
            return currentAddressPrefix + "addressTypeCode";
    }
}