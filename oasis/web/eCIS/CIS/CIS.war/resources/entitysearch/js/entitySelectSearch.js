//-----------------------------------------------------------------------------
// Functions to support entity select list page.
// Author: unknown
// Date:   unknown
// Modifications:
//-----------------------------------------------------------------------------
// 05/17/2007    kshen    Added codes to handle refresh when class-code changed.
// 06/25/2007    kshen    Moddify for function clearSearchCriteria()
// 11/06/2007    kenney   Change the handleOnChange()
// 09/09/2008    Jacky   Change the validateEntitySelectEventName() to add handleOnFindAccount()
//                                 function name
// 04/23/2009    Fred     Added field country code
// 04/28/2010    Leo      Issue 107033
// 07/08/2010    dzhang   Add "handleFindNonBaseCompanyInsuredClientDone()" to function validateEntitySelectEventName.
// 07/13/2010       shchen      Provide search on CIS relationships and entitytype for issue 106849.
// 01/19/2011       syang       105832 - Added openDisciplineDeclineListPage() to open discipline decline page.
// 07/26/2011       parker       123101 - The list in "County" dropdown will disappear when select value for "Classification" dropdown.
// 11/22/2012    Leo      Issue 138686.
// 10/27/2014    kxiang   Issue 158657 - Removed codes about Location2, as it's obsolete.
// 01/27/2015    ylu      Issue 165742: support phone# wild search in popup page
// 06/21/2016    Elvin    Issue 177406: add entity_externalDataId when checking input
// 10/12/2017    ylu      Issue 185677: handle the parent page is not existed scenario.
// 06/15/2018    jld      Issue 193183: Make sure that date fields do not submit mask for search.
// 08/13/2018    htwang   Issue 191837: Modify the fireEvent() to put the parent method in the callback function of closeWindow().
// 08/23/2018    jdingle  Issue 195413. Do not allow search on enter if search button is not available.
// 08/29/2018    dpang    Issue 109216: Add code missed during refactor: resize window and move to top left if no entity is searched out.
// 10/16/2018    dzou     correct the field id, because field id is case-sensitive on other browsers except IE
// 10/19/2018    dzou     grid replacement
// 10/31/2018    jdingle  Issue 195285. Update js for onUnload event.
// 11/29/2018    htwang   Issue 197302: Modify doesFieldExistOnParentWindow() to use getParentWindowObject() to
//                        check whether the field exists on the parent window.
// 12/4/2018     dpang    Issue 196632: Add isNavigatedAwayFromCallingPage() to check if the page is navigated away from the calling page.
// 12/06/2019    dzhang   Issue 196632: Change to set value to parent field.
//-----------------------------------------------------------------------------

var stateFldID = "searchCriteria_stateCode";
var entityClassFldID = "searchCriteria_entityClassCode";
var lastOrOrgNameFldID = "searchCriteria_lastOrOrgName";
var firstNameFldID = "searchCriteria_firstName";
var taxIDFldID = "searchCriteria_taxId";
var clientIDFldID = "searchCriteria_clientId";
var dateOfBirthFldID = "searchCriteria_dateOfBirth";
var roleTypeCodeFldID = "searchCriteria_roleTypeCode";
var roleExternalIDFldID = "searchCriteria_roleExternalId";
var cityFldID = "searchCriteria_city";
var ZIPCodeFldID = "searchCriteria_zipCode";
var licenseNoFldID = "searchCriteria_licenseNo";
var riskClassCodeFldID = "searchCriteria_riskClassCode";
var eMailAddress1FldID = "searchCriteria_emailAddress1";
var entityLegacyDataID = "searchCriteria_legacyDataId";
var entityExternalDataID = "searchCriteria_externalDataId";
var vendorCode = "searchCriteria_vendorCode";
var entitySearch_addlField = "searchCriteria_addlField";
var longNameFldId = "searchCriteria_veryLongName";
var countryCodeFldId = "searchCriteria_countryCode";
var zipCodeForeignFldId = "searchCriteria_zipCodeForeign";
var provinceFldId = "searchCriteria_province";
var otherProvinceFldId = "searchCriteria_otherProvince";
var phoneNumberTypeCodeFldId = "searchCriteria_phoneNumberTypeCode";
var areaCodeFldId = "searchCriteria_areaCode";
var phoneNumberFldId = "searchCriteria_phoneNumber";
var addressLine1FldId = "searchCriteria_addressLine1";
var propertyDescriptionFldId = "searchCriteria_propertyDescription";
var propertyIDFldId = "searchCriteria_propertyId";
var cmLobCodeFldId = "searchCriteria_cmLobCode";
var surveyVisitFromDateFldId = "searchCriteria_surveyVisitFromDate";
var surveyVisitToDateFldId = "searchCriteria_surveyVisitToDate";
var entityType = "searchCriteria_entityTypeCode";
var relationShip = "searchCriteria_relationshipCode";
var char1FldID = "searchCriteria_char1";
var char2FldID = "searchCriteria_char2";
var char3FldID = "searchCriteria_char3";
var char4FldID = "searchCriteria_char4";
var char5FldID = "searchCriteria_char5";
var riskStateFldID = "searchCriteria_riskState";
var riskCountyFldID = "searchCriteria_riskCounty";
var refNoFldID = "searchCriteria_referenceNumber";
var dbaNameFldID = "searchCriteria_dbaName";
var genderFldID = "searchCriteria_gender";
var prefixNameFldID = "searchCriteria_prefixName";
var suffixNameFldID = "searchCriteria_suffixName";
var veryImportantPersonBFldID = "searchCriteria_veryImportantPersonB";
var titleFldID = "searchCriteria_title";
var maritalStatusFldID = "searchCriteria_maritalStatus";
var minorBFldID = "searchCriteria_minorB";
var deceasedBFldID = "searchCriteria_deceasedB";
var dateOfDeathFldID = "searchCriteria_dateOfDeath";
var dateOfMaturityFldID = "searchCriteria_dateOfMaturity";
var num1FldID = "searchCriteria_num1";
var num2FldID = "searchCriteria_num2";
var num3FldID = "searchCriteria_num3";
var date1FldID = "searchCriteria_date1";
var date2FldID = "searchCriteria_date2";
var date3FldID = "searchCriteria_date3";
var sicCodeFldID = "searchCriteria_sicCode";
var lossFreeDateFldID = "searchCriteria_lossFreeDate";
var claimsFreeDateFldID = "searchCriteria_claimsFreeDate";
var insuredSinceDateFldID = "searchCriteria_insuredSinceDate";
var dynamicClaimEntrySourceFldID = "searchCriteria_dynamicClaimEntrySource";
var industryDescFldID = "searchCriteria_industryDesc";
var fakeAdaNumberFldID = "searchCriteria_fakeAdaNumber";
var componentFldID = "searchCriteria_component";
var entityStatusCodeFldID = "searchCriteria_entityStatusCode";
var adaNumberFldID = "searchCriteria_adaNumber";
var webAddress1FldID = "searchCriteria_webAddress1";
var webUserBFldID = "searchCriteria_webUserB";
var webUserIdFldID = "searchCriteria_webUserId";
var hicnFldID = "searchCriteria_hicn";
var legalNameFldID = "searchCriteria_legalName";
var profDesignationFldID = "searchCriteria_profDesignation";
var electronicDistrbBFldID = "searchCriteria_electronicDistrbB";
var legalNameEffectiveDateFldID = "searchCriteria_legalNameEffectiveDate";

var listroleUrl = '';
var listpolicyUrl = '';
var entPKFldName = '';
var entFullNameFldName;
var entLastNameFldName;
var entFirstNameFldName;
var entMiddleNameFldName;
var entOrgNameFldName;
var entClientIdFldName;
var entAccountNoFldName;
var entPolicyNoFldName;
var externalNumberFldName;
var entityRoleTypeCodeFldName;
var openerFieldName;
var eventName;
var fromFM;
var fromDocProcess;

function handleOnUnload() {
    if (getParentWindow().handleOnEntitySearchWindowUnLoad) {
        getParentWindow().handleOnEntitySearchWindowUnLoad();
    }
}

function btnClick(btnID) {
    switch (btnID.toUpperCase()) {
        case 'CANCEL':
            closeWindow("handleOnEntitySearchClose()");
            break;

        /* If the user has clicked on Search but has not entered or selected any criteria, don't do the search. */
        case 'SEARCH':
            search();
            break;

        case 'ADD':
            openAddEntityPopup();
            break;

        case 'SELECT':
            select();
            break;

        case  'LISTROLE':
            listRole();
            break;

        case 'LISTPOLICY':
            listPolicy();
            break;

        case 'SELECTADDI':
            selectAdditionalEntityForAddRisk();
            break;

        case 'ADDTOCRM':
            alert("Add entity to CRM.");
            break;
    }
}

//-----------------------------------------------------------------------------
// OnChange event handler
//-----------------------------------------------------------------------------
function handleOnChange(field) {
    if (field.name == countryCodeFldId) {
        setObjectValue(stateFldID, "");
        setObjectValue(cityFldID, "");
        setObjectValue(ZIPCodeFldID, "");
        setObjectValue(zipCodeForeignFldId, "");
        setObjectValue(provinceFldId, "");
        setObjectValue(otherProvinceFldId, "");
    }
    return;
}
//-----------------------------------------------------------------------------
// Clear search criteria.
//-----------------------------------------------------------------------------
function clearSearchCriteria() {
    /* Clear all the search criteria. */
    clearFormFields(document.forms[0], true);
    //fix issue 65133 Clear both the search criteria and the Search results.
    //modify by Kyle (kshen)
    setObjectValue("process", "initPage");
    document.forms[0].action = "ciEntitySelectSearch.do";
    showProcessingDivPopup();
    submitFirstForm();
}

//-----------------------------------------------------------------------------
// Validate search criteria.
//-----------------------------------------------------------------------------
function validSearchCriteria() {
    //Last/ORG name and Long Name is configured as required then apply this validation rule
    if (isRequiredField(lastOrOrgNameFldID) && isRequiredField(longNameFldId)) {
        if (!isStringValue(getObjectValue(lastOrOrgNameFldID)) &&
            !isStringValue(getObjectValue(longNameFldId))) {
            var orgNameLabel = getLabel(lastOrOrgNameFldID);
            var longNameLabel = getLabel(longNameFldId);
            if (!isStringValue(orgNameLabel)) {
                orgNameLabel = getMessage("ci.common.error.entitySearch.lastName");
            }
            if (!isStringValue(longNameLabel)) {
                longNameLabel = getMessage("ci.common.error.entitySearch.longName");
            }
            alert(getMessage("ci.common.error.value.mustEntered", [orgNameLabel, longNameLabel]));
            return false;
        }
    }
    /* The user must select at least one search criterion. */
    if
    (
        !isValue(getSingleObject(stateFldID)) &&
        !isValue(getSingleObject(entityClassFldID)) &&
        !isValue(getSingleObject(roleTypeCodeFldID)) &&
        !isValue(getSingleObject(riskClassCodeFldID)) &&
        !isValue(getSingleObject(lastOrOrgNameFldID)) &&
        !isValue(getSingleObject(firstNameFldID)) &&
        !isValue(getSingleObject(taxIDFldID)) &&
        !isValue(getSingleObject(clientIDFldID)) &&
        !isValue(getSingleObject(dateOfBirthFldID)) &&
        !isValue(getSingleObject(cityFldID)) &&
        !isValue(getSingleObject(ZIPCodeFldID)) &&
        !isValue(getSingleObject(roleExternalIDFldID)) &&
        !isValue(getSingleObject(licenseNoFldID)) &&
        !isValue(getSingleObject(eMailAddress1FldID)) &&
        !isValue(getSingleObject(entityLegacyDataID)) &&
        !isValue(getSingleObject(entityExternalDataID)) &&
        !isValue(getSingleObject(vendorCode)) &&
        !isValue(getSingleObject(entitySearch_addlField)) &&
        !isValue(getSingleObject(longNameFldId)) &&
        !isValue(getSingleObject(countryCodeFldId)) &&
        !isValue(getSingleObject(zipCodeForeignFldId)) &&
        !isValue(getSingleObject(provinceFldId)) &&
        !isValue(getSingleObject(phoneNumberTypeCodeFldId)) &&
        !isValue(getSingleObject(areaCodeFldId)) &&
        !isValue(getSingleObject(phoneNumberFldId)) &&
        !isValue(getSingleObject(addressLine1FldId)) &&
        !isValue(getSingleObject(propertyDescriptionFldId)) &&
        !isValue(getSingleObject(propertyIDFldId)) &&
        !isValue(getSingleObject(cmLobCodeFldId)) &&
        !isValue(getSingleObject(surveyVisitFromDateFldId)) &&
        !isValue(getSingleObject(surveyVisitToDateFldId))&&
        !isValue(getSingleObject(entityType)) &&
        !isValue(getSingleObject(relationShip)) &&
        !isValue(getObject(char1FldID)) &&
        !isValue(getObject(char2FldID)) &&
        !isValue(getObject(char3FldID)) &&
        !isValue(getObject(char4FldID)) &&
        !isValue(getObject(char5FldID)) &&
        !isValue(getObject(riskStateFldID)) &&
        !isValue(getObject(riskCountyFldID)) &&
        !isValue(getObject(refNoFldID)) &&
        !isValue(getObject(dbaNameFldID)) &&
        !isValue(getObject(genderFldID)) &&
        !isValue(getObject(prefixNameFldID)) &&
        !isValue(getObject(suffixNameFldID)) &&
        !isValue(getObject(veryImportantPersonBFldID)) &&
        !isValue(getObject(titleFldID)) &&
        !isValue(getObject(maritalStatusFldID)) &&
        !isValue(getObject(minorBFldID)) &&
        !isValue(getObject(deceasedBFldID)) &&
        !isValue(getObject(dateOfDeathFldID)) &&
        !isValue(getObject(dateOfMaturityFldID)) &&
        !isValue(getObject(num1FldID)) &&
        !isValue(getObject(num2FldID)) &&
        !isValue(getObject(num3FldID)) &&
        !isValue(getObject(date1FldID)) &&
        !isValue(getObject(date2FldID)) &&
        !isValue(getObject(date3FldID)) &&
        !isValue(getObject(sicCodeFldID)) &&
        !isValue(getObject(lossFreeDateFldID)) &&
        !isValue(getObject(claimsFreeDateFldID)) &&
        !isValue(getObject(insuredSinceDateFldID)) &&
        !isValue(getObject(dynamicClaimEntrySourceFldID)) &&
        !isValue(getObject(industryDescFldID)) &&
        !isValue(getObject(fakeAdaNumberFldID)) &&
        !isValue(getObject(componentFldID)) &&
        !isValue(getObject(entityStatusCodeFldID)) &&
        !isValue(getObject(adaNumberFldID)) &&
        !isValue(getObject(webAddress1FldID)) &&
        !isValue(getObject(webUserBFldID)) &&
        !isValue(getObject(webUserIdFldID)) &&
        !isValue(getObject(hicnFldID)) &&
        !isValue(getObject(legalNameFldID)) &&
        !isValue(getObject(profDesignationFldID)) &&
        !isValue(getObject(electronicDistrbBFldID)) &&
        !isValue(getObject(legalNameEffectiveDateFldID))
    ) {
        alert(getMessage("ci.entity.message.searchCriteria.enter"));
        return false;
    }

    if (isValue(getSingleObject(phoneNumberFldId))) {
        if (getObjectValue("CI_PHONE_PART_SRCH")=="Y") {
            //return isPhone(phoneNumberFldId);
            var phoneNum = getObjectValue(phoneNumberFldId);
            var reTestForNumOnly = /^[\d]{1,7}$/;
            var reTestForNumWithDash = /^[\d]{1,3}-[\d]{1,4}$/;
            if (!reTestForNumOnly.test(phoneNum)  &&
                !reTestForNumWithDash.test(phoneNum)) {
                alert(getMessage("ci.entity.message.phoneNumber.invalid"));
                return false;
            }
        }
    }

    if (isValue(getSingleObject(dateOfBirthFldID))) {
        if (getObjectValue(dateOfBirthFldID)==='mm/dd/yyyy') {
            setObjectValue(dateOfBirthFldID,'');
        }
    }

    if (isValue(getSingleObject(surveyVisitFromDateFldId))) {
        if (getObjectValue(surveyVisitFromDateFldId)==='mm/dd/yyyy') {
            setObjectValue(surveyVisitFromDateFldId,'');
        }
    }

    if (isValue(getSingleObject(surveyVisitToDateFldId))) {
        if (getObjectValue(surveyVisitToDateFldId)==='mm/dd/yyyy') {
            setObjectValue(surveyVisitToDateFldId,'');
        }
    }

    return true;
}

function searchByClientID(clientId)
{
    clearSearchCriteria();
    setObjectValue(clientIDFldID, clientId);
    btnClick('search');
}

//-----------------------------------------------------------------------------
// Validates that the event name for coverage type selection is OK.
//-----------------------------------------------------------------------------
function validateEntitySelectEventName(eventName) {

    var validEventName = [
        "handleFindClient()",
        "validateChildEntity()",
        "getWIPInquiryList()",
        "handleOnChange(document.forms[0].claimEntryLog_entityInsuredFk)",
        "handleOnChange(document.forms[0].claimEntryLog_entityClaimantFk)",
        "handleOnChange(document.forms[0].claimEntryLog_entityDefenseLawFirmFk)",
        "handleOnChange(document.forms[0].claimEntryLog_entityPlaintiffLawFirmFk)",
        "addClaimant",
        "changeClaimant",
        "newInsuredSelected()",
        "nonInsuredChanged()",
        "attorneyChanged()",
        "selectEntity",
        "addParticipant",
        "selectRehabCo",
        "selectRehabRep",
        "copyPolicy",
        "riskselected",
        "handleOnChange(document.forms[0].cmTransaction_entityPayeeFkSelected)",
        "handleOnFindInvoicer()",
        "captureEntity()",
        "addClaimsStaff",
        "handleOnSelectPolicyHolderForCreatePolicy()",
        "handleOnSelectRiskEntity()",
        "handleOnAddOccupant()",
        "handleOnSelectCoiEntity()",
        "handleOnSelectAffiliationEntity()",
        "handleOnSelectEntity()",
        "handleOnFindAccount()",
        "handleOnChangeNewAcctHolder()",
        "handleOnSelectPolicy()",
        "displaySpecialConditionMessages()",
        "handleOnFindClient()",
        "returnInsuredId",
        "handleFindNonBaseCompanyInsuredClientDone()",
        "claimantSelected",
        "defAttorneySelected",
        "plantiffAttorneySelected",
        "defenseLawFirmSelected",
        "entityFieldRepSelected",
        "updateEducationFields"
    ];


    if (eventName == null) {
        return false;
    }

    if (!isStringValue(eventName)) {
        return false;
    }

    var len = validEventName.length;
    for (i = 0; i < len; i++) {
        if (eventName == validEventName[i]) {
            return true;
        }
    }

    /* All other possible values are invalid */
    return false;
}

function handleOnLoad() {
    checkFieldPassedByOpenerWindow();

    var objIdForAddlSql = getObject("idForAddlSql");
    var objAgentFld = getObject(roleTypeCodeFldID);

    if(objIdForAddlSql && 'AGENT' == objIdForAddlSql.value) {
        objAgentFld.onfocus = function() {
            var index = objAgentFld.selectedIndex;
            objAgentFld.onchange = function() {
                objAgentFld.selectedIndex = index;
            };
        };
    }

    if (getTableForGrid("entityListGrid") && getTableProperty(getTableForGrid("entityListGrid"), "hasrows")) {
        window.resizeTo(990, 850);
    } else {
        window.resizeTo(820, 470);
        window.moveTo(10, 10);
    }

    if (getParentWindow() != null) {
        if (getParentWindow().handleOnEntitySearchWindowLoad) {
            getParentWindow().handleOnEntitySearchWindowLoad();
        }
    }
    setIsEntitySearchWindowActive(true);
    setDefaultValueForRole();
    if (typeof eventName !== 'undefined') {
        if (getObject('CI_ENT_SEL_LST_FORM_ADDL')) {
            hideShowField(getObject('CI_ENT_SEL_LST_FORM_ADDL'), (eventName != "handleOnSelectRiskEntity()"));
        }
    }
}


function setIsEntitySearchWindowActive(value) {
    if (getParentWindow() != null) {
        if(getParentWindow().isEntitySearchWindowActive) {
            getParentWindow().isEntitySearchWindowActive = value;
        }
    }
}

function setDefaultValueForRole() {
    var defaultRoleTypeCode = "defaultRoleTypeCode";

    // Default the Role.
    if (getObject(defaultRoleTypeCode) && !isEmpty(getObjectValue(defaultRoleTypeCode))) {
        getObject(roleTypeCodeFldID).value = getObjectValue(defaultRoleTypeCode);
    }
}

/**
 * Check CSELECT_IND checkbox field in current selected row and uncheck other rows.
 *
 * Don't check CSELECT_IND field under below cases:
 * 1. When first load page.
 * 2. When select row in commonReadyStateReady.
 *
 * @param rowid
 */
function entityListGrid_selectRow(rowid) {
    //Check table property "selectRowInCommonReadyStateReady" for old grid in IE8.
    if (isProcessFieldDeps && !getTableProperty(getTableForXMLData(entityListGrid1), "selectRowInCommonReadyStateReady")) {
        uncheckOtherCheckbox(entityListGrid1);
    }
}

//-----------------------------------------------------------------------------
// Opens the discipline decline list page.
//-----------------------------------------------------------------------------
function openDisciplineDeclineListPage(entityPk) {
    if (entityPk == 0) {
        alert(getMessage("cs.entity.information.error.notRecorded"));
        return;
    }

    var path = getCSPath() + "/disciplinedeclinemgr/maintainDisciplineDecline.do?" +
        "process=loadAllDisciplineDeclineEntity&forDivPopupB=Y&ddlEntityId=" + entityPk;

    var entityName = entityListGrid1.recordset("CCLIENTNAME").value;
    if (!isEmpty(entityName)) {
        path = path + "&ddlEntityName=" + encodeURIComponent(entityName);
    }
    openDivPopup("", path, true, true, "", "", "800", "730", "", "", "", true);
}

//-----------------------------------------------------------------------------
// check policy whether exist (Add by Jacky 12/10/2008)
//-----------------------------------------------------------------------------
function callbackCheckPolicy(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseText;
            hideProcessingImgIndicator();
            if (data == 'no policy') {
                listroleUrl = '';
                alert(getMessage("ci.entity.message.noPolicy.associated"));
            } else {
                openDivPopup("Entity Policy List", listpolicyUrl, true, true, "", "", "", "", "", "", "", true);
            }
        }
    }
}

function callbackCheckRole(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseText;
            if (data == 'no role') {
                listroleUrl = '';
                alert(getMessage("ci.entity.message.noRoles.associated"));
            } else {
                openDivPopup("Entity Role List", listroleUrl, true, true, "", "", "", "", "", "", "", true);
            }
            hideProcessingImgIndicator();
        }
    }
}

function search() {
    showProcessingDivPopup();
    if (!validSearchCriteria()) {
        closeProcessingDivPopup();

    } else {
        window.resizeTo(990, 850);
        setObjectValue("process", "search");
        submitFirstForm();
    }
}

function handleSearchOnEnter() {
    if (hasObject('CI_ENTITY_SELECT_SCH_SCH')) {
        if (getObject('CI_ENTITY_SELECT_SCH_SCH').disabled === false) {
            btnClick('search');
        }
    }
}

function openAddEntityPopup() {
    var path = "ciEntityAddChoicePop.do?lNm=" +encodeURIComponent(getObjectValue(lastOrOrgNameFldID))+
        "&fNm=" + encodeURIComponent(getObjectValue(firstNameFldID)) +
        "&taxId="+getObjectValue(taxIDFldID)+
        "&dob="+getObjectValue(dateOfBirthFldID)+
        "&cls="+getObjectValue(entityClassFldID)+
        "&city="+encodeURIComponent(getObjectValue(cityFldID))+
        "&st="+getObjectValue(stateFldID)+
        "&cnty="+getObjectValue('searchCriteria_countyCode')+
        "&zip=" + getObjectValue(ZIPCodeFldID) +
        "&subCls=" + getObjectValue("searchCriteria_entitySubClassCode") +
        "&subType=" + getObjectValue("searchCriteria_entitySubTypeCode") +
        "&countryCode=" + getObjectValue(countryCodeFldId) +
        "&emailAddress=" + encodeURIComponent(getObjectValue(eMailAddress1FldID)) +
        "&dbaName=" + encodeURIComponent(getObjectValue(dbaNameFldID)) ;
    var mainwin = window.open(path, 'CIEntityAddPop',
        'top=10,left=10, width=845,height=250,innerHeight=250,innerWidth=840,scrollbars');
    mainwin.focus();
}

function selectAdditionalEntityForAddRisk() {
    var dataArray = getSelectedKeys(entityListGrid1);
    if (!validateIfOneAndOnlyOneRowIsSelected(dataArray)) {
        return;
    }

    getRow(entityListGrid1, dataArray[0]);
    var entityPK = dataArray[0];
    if (!isEmpty(entPKFldName)) {
        //get newEntityId in parent page, and save selected Entity Id.

        var pkObj = getParentWindowObject(entPKFldName);
        if (pkObj) {
            var oldEntityPk = pkObj.value;
            //add select additional entity for add risk
            if (!isEmpty(oldEntityPk) && (oldEntityPk != "0")) {
                pkObj.value = oldEntityPk + ',' + entityPK;
            } else {
                pkObj.value = entityPK;
            }
        }
    }
    clearSearchCriteria();
}

function checkFieldPassedByOpenerWindow() {
    eventName = getObjectValue("eventName");
    fromFM = getObjectValue("fromFM");
    fromDocProcess = getObjectValue("fromDocProcess");

    var possibleOpenerFields = ["entityPKFieldName",
        "entityFullNameFieldName",
        "entityLastNameFieldName",
        "entityFirstNameFieldName",
        "entityMiddleNameFieldName",
        "entityOrgNameFieldName",
        "entityClientIdFieldName",
        "entityAccountNoFieldName",
        "idForPolicyNo",
        "entityRoleTypeCodeFldName",
        "externalNumberFldName"];

    var openerWindowField;
    var fieldName;

    for (var idx = 0; idx < possibleOpenerFields.length; idx++) {
        fieldName = possibleOpenerFields[idx];
        openerWindowField = getObjectValue(fieldName);

        if (!isEmpty(openerWindowField)) {
            openerFieldName = openerWindowField;

            switch (fieldName) {
                case "entityPKFieldName":
                    entPKFldName = openerFieldName;
                    break;
                case "entityFullNameFieldName":
                    entFullNameFldName = openerFieldName;
                    break;
                case "entityLastNameFieldName":
                    entLastNameFldName = openerFieldName;
                    break;
                case "entityFirstNameFieldName":
                    entFirstNameFldName = openerFieldName;
                    break;
                case "entityMiddleNameFieldName":
                    entMiddleNameFldName = openerFieldName;
                    break;
                case "entityOrgNameFieldName":
                    entOrgNameFldName = openerFieldName;
                    break;
                case "entityClientIdFieldName":
                    entClientIdFldName = openerFieldName;
                    break;
                case "entityAccountNoFieldName":
                    entAccountNoFldName = openerFieldName;
                    break;
                case "idForPolicyNo":
                    entPolicyNoFldName = openerFieldName;
                    break;
                case "entityRoleTypeCodeFldName":
                    entityRoleTypeCodeFldName = openerFieldName;
                    break;
                case "externalNumberFldName":
                    externalNumberFldName = openerFieldName;
                    break;
            }
        }
    }
}

function getParentWindowObject(fldName) {
    return getParentWindow().getObject(fldName);
}

/**
 * Check if the page is navigated away from the calling page:
 * 1. The opener window is closed
 * 2. The opener field doesn't exist on the opener window anymore
 */
function isNavigatedAwayFromCallingPage() {
    var parentWindow = getParentWindow();

    if (!parentWindow || null == parentWindow.getObject(openerFieldName)) {
        var messagePopupOpenerError = getMessage('message.popup.opener.error');
        alert(messagePopupOpenerError);

        if (!parentWindow) {
            window.close();
        } else {
            closeWindow();
        }
        return true;
    }

    return false;
}

function validateSelectRiskEntity(dataArray) {
    var passValidation = true;

    //select a entity for adding risk
    if (eventName == "handleOnSelectRiskEntity()") {
        var pkObj = getParentWindowObject(entPKFldName);

        if (dataArray.length == 0) {
            if (isEmpty(pkObj.value) || (pkObj.value == "0")) {
                alert(getMessage('ci.common.error.row.noSelect'));
            } else {
                getParentWindow().handleEvent(eventName);
            }
            passValidation = false;
        } else if (dataArray.length >= 2) {
            alert(getMessage('ci.common.error.onlyOneRow.noSelect'));
            passValidation = false;
        }
        //for others case exluding adding risk
    } else {
        passValidation = validateIfOneAndOnlyOneRowIsSelected((dataArray));
    }
    return passValidation;
}

function setEntityPkAndClientIdValue(dataArray) {
    getRow(entityListGrid1, dataArray[0]);
    var entityPK = dataArray[0];
    if (!isEmpty(entPKFldName)) {
        var pkObj = getParentWindowObject(entPKFldName);

        if (pkObj) {
            var oldEntityPk = pkObj.value;
            //add select additional entity for add risk

            if ((eventName == "handleOnSelectRiskEntity()") && !isEmpty(oldEntityPk) && (oldEntityPk != "0")) {
                setOpenerWindowFieldValue(entPKFldName, oldEntityPk + ',' + entityPK);
            } else {
                setOpenerWindowFieldValue(entPKFldName, entityPK);
            }
        }
    }

    var clientID = entityListGrid1.recordset("CCLIENTID").value;

    setOpenerWindowFieldValue(entClientIdFldName, clientID);
}

function setOpenerWindowFieldValue(fldName, value) {
    if (!isEmpty(fldName)) {
        getParentWindow().setObjectValue(fldName, value);
    }
}

function setValueForFullName() {
    var fullName = entityListGrid1.recordset("CCLIENTNAME").value;
    setOpenerWindowFieldValue(entFullNameFldName, fullName);
}

function setValueForPerson() {
    /* Dealing with a person. */
    var lastName = entityListGrid1.recordset("CLASTNAME").value;
    var firstName = entityListGrid1.recordset("CFIRSTNAME").value;
    var middleName = entityListGrid1.recordset("CMIDDLENAME").value;

    setOpenerWindowFieldValue(entLastNameFldName, lastName);
    setOpenerWindowFieldValue(entFirstNameFldName, firstName);

    if (!isEmpty(entMiddleNameFldName)) {
        var middleNameObj = getParentWindowObject(entMiddleNameFldName);
        if (middleNameObj) {
            if (middleName == '-') {
                setOpenerWindowFieldValue(entMiddleNameFldName, '');
            }
            else {
                setOpenerWindowFieldValue(entMiddleNameFldName, middleName);
            }
        }
    }
    if (entOrgNameFldName != entLastNameFldName) {
        setOpenerWindowFieldValue(entOrgNameFldName, "");
    }
}

function setValueForOrg() {
    var orgName = entityListGrid1.recordset("CORGNAME").value;

    setOpenerWindowFieldValue(entOrgNameFldName, orgName);
    if (entLastNameFldName != entOrgNameFldName) {
        setOpenerWindowFieldValue(entLastNameFldName, "");
    }

    setOpenerWindowFieldValue(entFirstNameFldName, "");
    setOpenerWindowFieldValue(entMiddleNameFldName, "");
}

function doesEntityTypeEqualToDefaultValue() {
    var entityType = entityListGrid1.recordset("CENTITYTYPE").value;
    var defaultEntityType = getObjectValue("defaultEntityType");

    if (entityType == 'P' && defaultEntityType == 'O') {
        alert(getMessage('ci.entity.message.organization.selectOne'));
        return false;
    } else if (entityType == 'O' && defaultEntityType == 'P') {
        alert(getMessage('ci.entity.message.person.selectOne'));
        return false;
    }
    return true;
}

function setValueForEntity() {
    var entityType = entityListGrid1.recordset("CENTITYTYPE").value;

    if (entityType == 'P') {
        setValueForPerson();
    } else if (entityType == 'O') {
        setValueForOrg();
    }
}

function fireEvent(entityPK) {
    var messagePopupOpenerError = getMessage('message.popup.opener.error');
    if (validateEntitySelectEventName(eventName)) {
        if (!isEmpty(eventName)) {
            try {

                var jsonEntity = getEntityJsonValue(entityListGrid1.documentElement, entityPK);
                entityListGrid1.recordset.movefirst();
                if (isIE8) {
                    getParentWindow().focus();
                }
                closeWindow(function () {
                    // Put the parent window method in the callback function,
                    // so Entity Select Search page can close itself when its parent window is being closed.
                    getParentWindow().handleEvent(eventName, jsonEntity);
                });
                return;
            }
            catch (ex) {
                alert(messagePopupOpenerError);
                closeWindow();
                return;
            }
        }
    }

    closeWindow();
}

function selectEntityForFM() {
    if (isNavigatedAwayFromCallingPage()) {
        return;
    }

    var dataArray = getSelectedKeys(entityListGrid1);
    if (!validateSelectRiskEntity(dataArray)) {
        return;
    }
    setEntityPkAndClientIdValue(dataArray);

    var entityPK = dataArray[0];
    var accountNoObj = getParentWindowObject(entAccountNoFldName);
    var accHolderObj = getParentWindowObject(entFullNameFldName);
    var policyNoObj = getParentWindowObject(entPolicyNoFldName);

    if ((undefined == policyNoObj || accHolderObj || accountNoObj)
        && (getObject('idForPolicyNo') == null || getObject('idForPolicyNo').value == '')) {
        var accountNo = getObjectValue("AccountNo");
        var idForAddlSql = getObject("idForAddlSql");

        if ((accountNo == null || '' == accountNo) && (accountNoObj || (accHolderObj && 'insuredFullName' != accHolderObj.name
                && 'claimantFullName' != accHolderObj.name))
            && (null == idForAddlSql || '' == idForAddlSql.value)) {
            alert(getMessage('ci.entity.message.account.noSelect'));
            return false;
        }

        if (!isEmpty(entAccountNoFldName) || !isEmpty(entFullNameFldName)) {
            if (accountNoObj || accHolderObj) {
                if (accHolderObj) {
                    accountNoObj = getParentWindowObject(getObjectValue("entityAccountNoFieldName"));
                }
                accountNoObj.value = accountNo;
            }

            var entAccountNoROSpanFieldName = isEmpty(entAccountNoFldName) ? "" : "window.opener.document.all." + entAccountNoFldName + "ROSPAN";
            var accountNoROSpanObj = eval(accHolderObj != null ? 'window.opener.document.forms[0].accountNoCriteria' : entAccountNoROSpanFieldName);

            if (undefined == accountNoROSpanObj && accountNoObj && (null == idForAddlSql || '' == idForAddlSql.value)
                && 'insuredFullName' != accHolderObj.name
                && 'claimantFullName' != accHolderObj.name) {
                // changed by Jacky 10-09-2009
                setOpenerWindowFieldValue(entAccountNoFldName, accountNo)
            }

            if (accountNoROSpanObj) {
                accountNoROSpanObj.innerText = accountNo;
            }
        }
    }

    policyNoObj = getParentWindowObject(entPolicyNoFldName);
    if (null != policyNoObj) {
        var policyNo = getObjectValue("PolicyNo");

        if (policyNoObj && (policyNo == null || '' == policyNo)) {
            alert(getMessage('ci.entity.message.policy.noSelect'));
            return false;
        }
        setOpenerWindowFieldValue(entPolicyNoFldName, policyNo);
    }

    setValueForFullName();
    if(!doesEntityTypeEqualToDefaultValue()) {
        return;
    }

    setValueForEntity();

    /* Check for existence of Event.  If exists, then fire it. */
    fireEvent(entityPK);
}

function commonSelectEntity() {
    if (isNavigatedAwayFromCallingPage()) {
        return;
    }

    var dataArray = getSelectedKeys(entityListGrid1);

    //select a entity for adding risk
    if(!validateSelectRiskEntity(dataArray)) {
        return;
    }

    setEntityPkAndClientIdValue(dataArray);

    var entityPK = dataArray[0];
    var accountNoObj = getParentWindowObject(entAccountNoFldName);
    if (accountNoObj) {
        var accountNo = getObjectValue("AccountNo");
        var entityAccountNoFieldName = getObjectValue("entityAccountNoFieldName");
        var roleBtnObj = getObject("CI_ENTITY_SEARCH_LISTROLE");

        if (null != roleBtnObj && "accountNo" == entityAccountNoFieldName && (accountNo == null || '' == accountNo)) {
            alert(getMessage('ci.entity.message.account.noSelect'));
            return false;
        }
        setOpenerWindowFieldValue(entAccountNoFldName, accountNo);
    }

    var policyNoObj = getParentWindowObject(entPolicyNoFldName);
    if (policyNoObj) {
        var policyNo = getObjectValue("PolicyNo");
        var entityPolicyNoFieldName =getObjectValue("entityPolicyNoFieldName");
        var policyBtnObj = getObject("CI_ENTITY_SEARCH_LISTPOLI");

        if (null != policyBtnObj && "policyNo" == entityPolicyNoFieldName && (policyNo == null || '' == policyNo)) {
            alert(getMessage('ci.entity.message.policy.noSelect'));
            return false;
        }
        setOpenerWindowFieldValue(entPolicyNoFldName, policyNo);
    }

    if ('true' == getObjectValue('fromDocProcess')) {
        var externalNumberObj = getParentWindowObject(externalNumberFldName);
        var entityRoleTypeCodeObj = getParentWindowObject(entityRoleTypeCodeFldName);
        if (null != externalNumberObj) {
            setOpenerWindowFieldValue(externalNumberFldName, getObjectValue("externalNumber"));
        }
        if (null != entityRoleTypeCodeObj) {
            setOpenerWindowFieldValue(entityRoleTypeCodeFldName, getObjectValue("entityRoleTypeCode"));
        }
    }

    setValueForFullName();

    if(!doesEntityTypeEqualToDefaultValue()) {
        return;
    }

    setValueForEntity();

    /* Check for existence of Event.  If exists, then fire it. */
    fireEvent(entityPK);
}

function select() {
    if ('true' == getObjectValue("fromFM")) {
        selectEntityForFM();
    } else {
        commonSelectEntity();
    }
}

function listRole() {
    var dataArray = getSelectedKeys(entityListGrid1);
    if (!validateIfOneAndOnlyOneRowIsSelected(dataArray)) {
        return;
    }

    getRow(entityListGrid1, dataArray[0]);
    var entityPK = dataArray[0];

    listroleUrl = getAppPath() + "/ciEntityListRole.do?process=loadEntityListRole&pk=" + entityPK
        + "&eventName=" + getObjectValue("eventName");
    if ("accountHolder" == getObjectValue('entityFullNameFieldName')) {
        listroleUrl += '&entityPKFieldName=accountHolderEntityId';
    }
    if (("policyHolderName" == getObjectValue('entityFullNameFieldName') ||
            "policyHolder" == getObjectValue('entityFullNameFieldName')) ||
        "policyHolderNameNew" == getObjectValue('entityFullNameFieldName')) {
        listroleUrl += '&entityPKFieldName=policyHolderEntityId';
    }

    // For issue 102270, added by Stephen 12-28-2009.
    listroleUrl += '&fromDocProcess=true';

    showProcessingImgIndicator();
    new AJAXRequest("get", listroleUrl, '', callbackCheckRole, false);
}

function listPolicy() {
    var dataArray = getSelectedKeys(entityListGrid1);
    if (!validateIfOneAndOnlyOneRowIsSelected(dataArray)) {
        return;
    }

    getRow(entityListGrid1, dataArray[0]);
    var entityPK = dataArray[0];
    var entityName = entityListGrid1.recordset("CCLIENTNAME").value;
    var entityType = entityListGrid1.recordset("CENTITYTYPE").value;
    var policyNoFldname = 'idForPolicyNo';

    showProcessingImgIndicator();
    listpolicyUrl = getAppPath() + "/ciSummary.do?pk=" + entityPK + "&entityType=" + entityType
        + "&entityName=" + encodeURIComponent(entityName) + "&entityId=" + entityPK + "&" + policyNoFldname + "="
        + getObjectValue(policyNoFldname) + "&getPolicyListFromEntityList=Y";
    new AJAXRequest("get", listpolicyUrl, '', callbackCheckPolicy, false);
}

function validateIfOneAndOnlyOneRowIsSelected(dataArray) {
    if (dataArray.length == 0) {
        alert(getMessage('ci.common.error.row.noSelect'));
        return false;
    }

    if (dataArray.length >= 2) {
        alert(getMessage('ci.common.error.onlyOneRow.noSelect'));
        return false;
    }

    return true;
}

function handleOnGridInitialization(gridId) {
    dti.oasis.grid.setProperty(gridId, "autoSelectFirstRow", false);
    return;
}
